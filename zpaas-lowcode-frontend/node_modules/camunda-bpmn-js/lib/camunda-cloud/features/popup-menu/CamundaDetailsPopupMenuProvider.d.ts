export default class CamundaDetailsPopupMenuProvider {
  static $inject: string[];

  /**
   * @param popupMenu
   *
   * @implements PopupMenuProvider
   */
  constructor(popupMenu: PopupMenu);

  /**
   * Register create menu provider in the popup menu
   */
  register(): void;

  /**
   * Returns the create options as menu entries
   *
   * @param _element
   */
  getPopupMenuEntries(_element: Element): (existingEntries: PopupMenuEntries) => PopupMenuEntries;
}

type PopupMenu = import("diagram-js/lib/features/popup-menu/PopupMenu").default;
type Element = import("diagram-js/lib/model").Element;
type PopupMenuEntries = import("diagram-js/lib/features/popup-menu/PopupMenuProvider").PopupMenuEntries;
