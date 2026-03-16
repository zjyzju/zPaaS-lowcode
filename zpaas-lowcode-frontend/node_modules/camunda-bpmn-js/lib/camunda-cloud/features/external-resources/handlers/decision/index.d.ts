export const DecisionHandlerModule: {
  'resources.decisionHandler.create': (string | typeof CreateMenuProvider)[];
  'resources.decisionHandler.append': (string | typeof AppendMenuProvider)[];
  'resources.decisionHandler.replace': (string | typeof ReplaceMenuProvider)[];
};
import { CreateMenuProvider } from './CreateMenuProvider';
import { AppendMenuProvider } from './AppendMenuProvider';
import { ReplaceMenuProvider } from './ReplaceMenuProvider';
