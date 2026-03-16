export const ProcessHandlerModule: {
  'resources.processHandler.create': (string | typeof CreateMenuProvider)[];
  'resources.processHandler.append': (string | typeof AppendMenuProvider)[];
  'resources.processHandler.replace': (string | typeof ReplaceMenuProvider)[];
};
import { CreateMenuProvider } from './CreateMenuProvider';
import { AppendMenuProvider } from './AppendMenuProvider';
import { ReplaceMenuProvider } from './ReplaceMenuProvider';
