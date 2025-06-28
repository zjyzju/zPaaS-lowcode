export default class Viewer extends BaseViewer<null> {
  /**
   * @param options
   */
  constructor(options?: BaseViewerOptions);
}

type BaseViewerOptions = import("bpmn-js/lib/BaseViewer").BaseViewerOptions;
import BaseViewer from '../base/Viewer';
