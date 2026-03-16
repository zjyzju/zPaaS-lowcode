import { parser, trackVariables } from '@bpmn-io/lezer-feel';
import {
  ContextTracker,
} from '@lezer/lr';

import { has, isNil } from 'min-dash';

import { EntriesContext } from './VariableContext';
import { getExtensionElementsList } from '../../base/util/ExtensionElementsUtil';
import { is } from 'bpmn-js/lib/util/ModelUtil';
import { getParents } from '../../base/util/elementsUtil';
import { mergeList } from '../../base/util/listUtil';

export function parseVariables(variables) {

  const variablesToResolve = [];

  // Step 1 - Parse all variables and populate all that don't have references
  // to other variables
  variables.forEach(variable => {
    variable.origin.forEach(origin => {
      const expressionDetails = getExpressionDetails(variable, origin);

      if (!expressionDetails) {
        return;
      }

      const { expression, unresolved } = expressionDetails;

      variablesToResolve.push({ variable, expression, unresolved });
    });
  });

  // Step 2 - Order all Variables and resolve them
  return resolveReferences(variablesToResolve, variables);
}

function resolveReferences(variablesToResolve, allVariables) {
  const sortedVariables = [];

  // Step 2.1 - Try to order Variables that rely on each other
  variablesToResolve.forEach((details) => {
    const { variable, unresolved } = details;
    const insertBefore = sortedVariables.findIndex(({ unresolved: u }) => {
      return u.includes(variable.name);
    });

    // Insert directly before the first variable that depends on this one
    if (insertBefore > -1) {
      sortedVariables.splice(insertBefore, 0, details);
      return;
    }

    // Insert directly after the last variable that this one depends on
    // this ensures that later downstream variables are behind this one
    const insertAfter = sortedVariables.findLastIndex(({ variable: v }) => {
      return unresolved.includes(v.name);
    });

    if (insertAfter > -1) {
      sortedVariables.splice(insertAfter + 1, 0, details);
      return;
    }

    sortedVariables.push(details);
  });

  const variablesWithoutMappings = allVariables.filter(v =>
    !variablesToResolve.find(({ variable: unresolved }) => {
      return v === unresolved;
    })
  );

  const rootContext = {
    name: 'OuterContext',
    entries: toOptimizedFormat(variablesWithoutMappings)
  };

  const newVariables = [];

  // Step 2.2 - parse in order, building up the context with resolved variable values
  // This will resolve all variables that don't have circular dependencies on each other
  sortedVariables.forEach(({ variable, expression, unresolved }) => {
    const scopedContext = filterForScope(rootContext, variable);
    const resultContext = getResultContext(expression, scopedContext);

    let computedResult = resultContext.computedValue();

    // Wrap primitive values in an EntriesContext
    if (!(computedResult instanceof EntriesContext)) {
      computedResult = EntriesContext.of(computedResult);
    }

    // If the result is null but a referenced variable was absent from scope, the null
    // means "unknown" (e.g. provided by a job worker at run-time), not "proven Null".
    // Clear atomicValue so getType() returns 'Any' instead of 'Null'.
    // Only consider top-level variable names; dotted paths (e.g. "foo.bar") are property
    // accesses, not standalone variable references that would indicate an out-of-scope variable.
    const unresolvedRoots = unresolved.filter(name => !name.includes('.'));

    if (computedResult.value.atomicValue === null &&
        unresolvedRoots.some(name => !has(scopedContext.entries, name))) {
      computedResult = EntriesContext.of(undefined);

      // Preserve the FEEL expression for user interface
      computedResult.value.info = expression;
    }

    // Ensure we don't copy the scope from the mapped variable
    computedResult.scope = variable.scope;

    // If the same variable name was already resolved (e.g. via input mapping AND a task output),
    // merge the types so downstream expressions see the full union (e.g. 'Null|Number').
    const existing = rootContext.entries[variable.name];

    if (existing) {
      const existingType = getType(existing.value);
      const newType = getType(computedResult.value);
      const mergedType = mergeList(existingType, newType, '|', true);
      if (mergedType) {
        computedResult.value.type = mergedType;
      }
    }

    rootContext.entries[variable.name] = computedResult;

    newVariables.push({
      newVariable: toUnifiedFormat({
        [variable.name]: computedResult
      })[0],
      oldVariable: variable
    });
  });

  // Ensure meta-data (scope, origin) is kept from original variable
  const enrichedVariables = newVariables.map(({ newVariable, oldVariable }) => {
    if (oldVariable) {
      return {
        ...newVariable,
        ...oldVariable
      };
    }
    return newVariable;
  });

  return enrichedVariables;
}


// helpers //////////////////////

/**
 * Parses the expression with the given variables and return the result context.
 *
 * Without a leading `=` the expression is interpreted as a string literal.
 *
 * @param {string} expression
 * @param {Variables} variables
 * @returns {EntriesContext}
 */
export function getResultContext(expression, variables = {}) {

  const contextTracker = trackVariables(variables, EntriesContext);

  // expression is a static value
  if (!expression.startsWith('=')) {

    // empty expressions are invalid FEEL, mapped to `null`
    // TODO(nikku): completely unsafe stuff, don't do, remove ASAP
    return contextTracker.start.literal(expression || null);
  }

  // remove leading `=`
  expression = expression.substring(1);


  // This is a hack to get the latest variables from the context tracker
  // lezer does not automatically annotate the parse tree with the context
  let latestVariables = contextTracker.start;

  const customContextTracker = new ContextTracker({
    start: contextTracker.start,
    reduce(...args) {
      const result = contextTracker.reduce(...args);
      latestVariables = result;
      return result;
    }
  });

  const contextualParser = parser.configure({
    contextTracker: customContextTracker,
    dialect: 'camunda',
    strict: true
  });

  try {
    contextualParser.parse(expression);
  } catch (error) {

    // bail out in case of an error
    return latestVariables;
  }

  return latestVariables;
}

/**
 * Given a Variable and a specific origin, return the mapping expression and all
 * unresolved variables used in that expression. Returns undefined if no mapping
 * exists for the given origin.
 *
 * @param {ProcessVariable} variable
 * @param {djs.model.Base} origin
 * @returns {{ expression: string, unresolved: Array<string> }}}
 */
function getExpressionDetails(variable, origin) {

  // if variable scope is !== origin (global), prioritize IoExpression over ScriptExpression
  // if variable scope is === origin (local), prioritize ScriptExpression over IoExpression
  const expression = variable.scope !== origin
    ? getIoExpression(variable, origin) ?? getScriptExpression(variable, origin)
    : getScriptExpression(variable, origin) ?? getIoExpression(variable, origin);

  if (isNil(expression)) {
    return;
  }

  const result = getResultContext(expression);

  const unresolved = findUnresolvedVariables(result);

  return { expression, unresolved };
}

/**
 * Given a Variable and a specific origin, return the expression.
 *
 * Returns `undefined` if no mapping exists for the given origin.
 *
 * @param {ProcessVariable} variable
 * @param {djs.model.Base} origin
 *
 * @returns {string|undefined}
 */
function getIoExpression(variable, origin) {
  const ioMapping = getExtensionElementsList(origin, 'zeebe:IoMapping')[0];

  if (!ioMapping) {
    return;
  }

  let mappings;
  if (origin === variable.scope) {
    mappings = ioMapping.inputParameters;
  } else {
    mappings = ioMapping.outputParameters;
  }

  if (!mappings) {
    return;
  }

  const mapping = mappings.slice().reverse().find(
    mapping => mapping.target === variable.name
  );

  // treat non-existing source as empty, to yield <null>
  return mapping && (mapping.source || '');
}

/**
 * Given a Variable and a specific origin, return the mapping expression for script
 * task result variable. Returns undefined if no mapping exists for the given origin.
 *
 * @param {ProcessVariable} variable
 * @param {djs.model.Base} origin
 * @returns {string}
 */
function getScriptExpression(variable, origin) {
  const script = getExtensionElementsList(origin, 'zeebe:Script')[0];

  if (!script) {
    return;
  }

  if (script.resultVariable === variable.name) {

    // treat non-existing expression as empty, to yield <null>
    return script.expression || '';
  }
}

/**
 * Traverses the parseTree and returns all `VariableName` nodes with no value
 *
 * @param {Object} node
 * @returns {Array<string>}
 */
function findUnresolvedVariables(node) {
  const results = [];

  if (node.name === 'PathExpression') {
    const [ object ] = node.children;
    results.push(...findUnresolvedVariables(object));
    return results;
  }

  results.push(...(node.children.flatMap(findUnresolvedVariables)));

  if (node.name === 'VariableName' && !node.value) {
    results.push(node.raw);
  }

  return results;
}


/**
 * Transforms the entries of a variable from an array to an object.
 * This allows faster lookup times during parsing.
 *
 * [ { name, entries: [] } ]
 * to
 * {name: { name, entries: {} }}
 */
function toOptimizedFormat(variables) {

  if (!variables) {
    return;
  }

  const result = {};

  variables.forEach(variable => {
    result[variable.name] = { ...variable };
    result[variable.name].entries = toOptimizedFormat(variable.entries);
  });

  return result;
}

/**
 * Transforms EntriesContext to the format required by the feel-editor
 */
export function toUnifiedFormat(variables) {
  if (!variables) {
    return;
  }

  const result = [];

  for (const key in variables) {
    let variable = variables[key];

    if (variable instanceof EntriesContext) {
      variable = variable.value;
    }

    if (!variable) {
      result.push({
        name: key,
        type: 'Null',
        info: 'null'
      });
      continue;
    }

    result.push({
      ...annotate(variable),
      entries: toUnifiedFormat(variable.entries),
      name: key,
      scope: variable.scope
    });
  }

  return result;
}


function annotate(variable) {
  return {
    ...variable,
    type: getType(variable),
    info: getInfo(variable)
  };

}

function getType(variable) {

  if (!variable) {
    return '';
  }

  if (variable.type) {
    return variable.type;
  }

  if (variable.entries && Object.keys(variable.entries).length) {
    return 'Context';
  }

  if (variable.atomicValue === null) {
    return 'Null';
  }

  if (!isNil(variable.atomicValue)) {
    return capitalize(typeof variable.atomicValue);
  }

  return 'Any';
}

function getInfo(variable) {
  if (!variable) {
    return '';
  }

  if (variable.info) {
    return variable.info;
  }

  if (!isNil(variable.atomicValue)) {
    return '' + variable.atomicValue;
  }

  return '';
}

function capitalize(string) {
  return string.charAt(0).toUpperCase() + string.slice(1);
}

function filterForScope(context, variable) {
  const scopedResults = {
    entries: {}
  };

  const validScopes = variable.origin.flatMap(bo => {
    return [ bo, ...getParents(bo) ];
  });

  for (const key in context.entries) {
    const entry = context.entries[key];

    if (validScopes.find(scope => scope.id === entry.scope.id)) {
      scopedResults.entries[key] = entry;
    }
  }

  return scopedResults;
}

/**
 * Remove input/output element name after current definition
 */
export function getElementNamesToRemove(moddleElement, inputOutput) {
  const namesToFilter = [];

  // Input: remove all inputs defined after the current input definition
  if (is(moddleElement, 'zeebe:Input')) {
    const allInputs = inputOutput.inputParameters;

    const inputsToFilter =
        allInputs
          .slice(allInputs.indexOf(moddleElement))
          .map(o => o.target);

    namesToFilter.push(...inputsToFilter);
  }

  const allOutputs = inputOutput.outputParameters;

  // Output: remove all outputs defined after the current output definition
  if (is(moddleElement, 'zeebe:Output')) {

    // Get all output mappings defined after the current element, including own name
    const outputsToFilter = allOutputs
      .slice(allOutputs.indexOf(moddleElement))
      .map(o => o.target);

    namesToFilter.push(...outputsToFilter);
  }

  // Input or general property: remove all outputs
  else if (allOutputs) {

    // Input or execution-related element, remove all outputs
    const outputsToFilter = allOutputs
      .map(o => o.target);

    namesToFilter.push(...outputsToFilter);
  }

  return namesToFilter;
}
