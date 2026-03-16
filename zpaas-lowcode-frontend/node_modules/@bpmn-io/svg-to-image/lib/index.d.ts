/**
 * Turn an SVG into an image, returning the image data URL.
 *
 * @param svg
 *
 * @returns
 */
export function svgToImage(svg: string): Promise<string>;
/**
 * Turn an SVG into an image, returning the image data URL.
 *
 * @param svg
 * @param options
 *
 * @returns
 */
export function svgToImage(svg: string, options: {
    imageType?: "png" | "jpeg";
    outputFormat: "dataUrl";
}): Promise<string>;
/**
 * Turn an SVG into an image, returning the image {@link Blob}.
 *
 * @param svg
 * @param options
 *
 * @returns
 */
export function svgToImage(svg: string, options: {
    imageType?: "png" | "jpeg";
    outputFormat: "blob";
}): Promise<Blob>;
