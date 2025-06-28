/**
 * Get extension elements of element. Optionally filter by type.
 *
 * @param  element
 * @param  type
 *
 * @return
 */
export function getExtensionElementsList(element: Element | ModdleElement, type?: string): ModdleElement[];

/**
 * Remove one or more extension elements. Remove bpmn:ExtensionElements
 * afterwards if it's empty.
 *
 * @param element
 * @param businessObject
 * @param extensionElementsToRemove
 * @param commandStack
 */
export function removeExtensionElements(element: Element, businessObject: ModdleElement, extensionElementsToRemove: ModdleElement | ModdleElement[], commandStack: CommandStack): void;

type CommandStack = import("diagram-js/lib/command/CommandStack").default;
type Element = import("bpmn-js/lib/model/Types").Element;
type ModdleElement = import("bpmn-js/lib/model/Types").ModdleElement;
