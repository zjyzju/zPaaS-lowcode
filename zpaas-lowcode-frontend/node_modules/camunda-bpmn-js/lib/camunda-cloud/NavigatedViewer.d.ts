export default class NavigatedViewer extends BaseViewer<null> {
  /**
   * @param options
   */
  constructor(options?: BaseViewerOptions);
}

type BaseViewerOptions = import("bpmn-js/lib/BaseViewer").BaseViewerOptions;
import BaseViewer from '../base/NavigatedViewer';
