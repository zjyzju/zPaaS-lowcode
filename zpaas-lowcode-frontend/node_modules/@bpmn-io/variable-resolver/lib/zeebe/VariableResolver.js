import { getProcessVariables, getScope } from '@bpmn-io/extract-process-variables/zeebe';
import { BaseVariableResolver } from '../base/VariableResolver';
import {
  parseVariables,
  getElementNamesToRemove
} from './util/feelUtility';
import {
  getBusinessObject,
  is
} from 'bpmn-js/lib/util/ModelUtil';

import { getInputOutput } from '../base/util/ExtensionElementsUtil';


const HIGH_PRIORITY = 2000;

const HIGHER_PRIORITY = 2250;

/**
 * The Camunda 8 Implementation for the VariableResolver.
 */
export default class ZeebeVariableResolver extends BaseVariableResolver {
  constructor(eventBus, bpmnjs) {
    super(eventBus, bpmnjs);
    this._baseExtractor = getProcessVariables;
    this._getScope = getScope;

    eventBus.on('variableResolver.parseVariables', HIGHER_PRIORITY, this._resolveVariables);
    eventBus.on('variableResolver.parseVariables', HIGH_PRIORITY, this._expandVariables);
  }

  async getVariablesForElement(element, moddleElement) {
    const variables = await super.getVariablesForElement(element);

    const bo = getBusinessObject(element);

    if (!moddleElement) {
      return variables;
    }

    const inputOutput = getInputOutput(bo);

    if (!inputOutput) {
      return variables;
    }

    const namesToFilter = getElementNamesToRemove(moddleElement, inputOutput);

    return variables.filter(v => {

      // Keep all variables that are also defined in other elements
      if (v.origin.length > 1 || v.origin[0] !== bo) {
        return true;
      }

      // Keep all variables from external data providers in outputs
      if (
        is(moddleElement, 'zeebe:Output') &&
        v.provider.find(extractor => extractor !== this._baseExtractor)
      ) {
        return true;
      }

      // Filter all pre-defined variables
      return !namesToFilter.includes(v.name);
    });
  }

  /**
   * Expand hierarchical name variables.
   *
   * @param {Event} e
   * @param {Object} context
   * @param {Array<ProcessVariable>} context.variables
   */
  _expandVariables(e, context) {
    const rawVariables = context.variables;

    const mappedVariables = {};

    for (const key in rawVariables) {
      mappedVariables[key] = expandHierarchicalNames(rawVariables[key]);
    }

    context.variables = mappedVariables;
  }

  /**
   * Parsed the variables and resolves the variable schema to kept the
   * variable schema throughout the process.
   *
   * @param {Event} e
   * @param {Object} context
   * @param {Array<ProcessVariable>} context.variables
   */
  _resolveVariables(e, context) {
    const rawVariables = context.variables;

    const mappedVariables = {};

    for (const key in rawVariables) {
      const variables = rawVariables[key];
      const newVariables = parseVariables(variables);

      mappedVariables[key] = [ ...variables, ...newVariables ];
    }

    context.variables = mappedVariables;
  }
}

/**
 * @param {Array<ProcessVariable>} variables
 *
 * @return {Array<ProcessVariable>}
 */
function expandHierarchicalNames(variables) {

  return variables.map(variable => {

    const {
      name,
      scope,
      provider,
      origin
    } = variable;

    const nameParts = name.split('.');

    if (nameParts.length === 1) {
      return variable;
    }

    const chain = [ {
      ...variable,
      name: nameParts[nameParts.length - 1]
    } ];

    for (let idx = nameParts.length - 1; idx > 0; idx--) {
      const namePart = nameParts[idx - 1];
      const lastVariable = chain[0];

      chain.unshift({
        name: namePart,
        type: 'Context',
        scope,
        provider,
        origin,
        entries: [
          lastVariable
        ]
      });
    }

    return chain[0];
  });
}