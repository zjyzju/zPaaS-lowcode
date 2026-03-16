# @bpmn-io/svg-to-image

[![CI](https://github.com/bpmn-io/svg-to-image/actions/workflows/CI.yml/badge.svg)](https://github.com/bpmn-io/svg-to-image/actions/workflows/CI.yml)

Converts an SVG to an image with decent quality.

## Usage

```javascript
import { svgToImage } from 'svg-to-image';

const svg = '<svg>...</svg>';

// Generate PNG
const result = await svgToImage(svg, {
    imageType: 'png',
    outputFormat: 'blob'
});
```

## Build and Run

```
# install dependencies
npm install

# run project, executing all tasks
npm run all
```

## How it works

This package uses [canvg](https://github.com/canvg/canvg) to render SVG elements onto a canvas and then exports them as image data URLs or [image blobs](https://developer.mozilla.org/en-US/docs/Web/API/Blob).

## License

MIT
