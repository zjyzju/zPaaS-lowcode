import CustomPalette from './CustomPalette'
import CustomContextPadProvider from './CustomContextPadProvider'
import CustomReplaceMenuProvider from './CustomReplaceMenuProvider'

export default {
    __init__: ['paletteProvider'],
    paletteProvider: ['type', CustomPalette],
    contextPadProvider: ['type', CustomContextPadProvider],
    replaceMenuProvider: ['type', CustomReplaceMenuProvider]
}