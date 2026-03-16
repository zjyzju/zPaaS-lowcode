export const FormHandlerModule: {
  'resources.formHandler.create': (string | typeof CreateMenuProvider)[];
  'resources.formHandler.append': (string | typeof AppendMenuProvider)[];
  'resources.formHandler.replace': (string | typeof ReplaceMenuProvider)[];
};
import { CreateMenuProvider } from './CreateMenuProvider';
import { AppendMenuProvider } from './AppendMenuProvider';
import { ReplaceMenuProvider } from './ReplaceMenuProvider';
