export default class Modeler extends BpmnModeler<null> {
  /**
   * @param options
   */
  constructor(options?: BaseViewerOptions);

  /**
   * @param options
   *
   * @returns
   */
  getModules(options?: {
      disableAdjustOrigin?: boolean;
      disableGrid?: boolean;
  }): ModuleDeclaration[];
}

type BaseViewerOptions = import("bpmn-js/lib/BaseViewer").BaseViewerOptions;
type ModuleDeclaration = import("didi").ModuleDeclaration;
import BpmnModeler from 'bpmn-js/lib/Modeler';
