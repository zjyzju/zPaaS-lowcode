// CustomPalette.js
export default class CustomPalette {
    constructor(bpmnFactory, create, elementFactory, palette, translate) {
        this.bpmnFactory = bpmnFactory;
        this.create = create;
        this.elementFactory = elementFactory;
        this.translate = translate;
        
        palette.registerProvider(this);
    }
    // 这个函数就是绘制palette的核心
    getPaletteEntries(element) {
        return {
            'create.ORM存取节点': {
                group: 'model', // 分组名
                className: 'icon-custom orm-repository-task', // 样式类名
                title: this.translate('创建一个类型为ORM存取节点的任务节点'),
                action: { // 操作
                    dragstart: this.createTask(), // 开始拖拽时调用的事件
                    click: this.createTask() // 点击时调用的事件
                }
            }
        }
    }
    
    createTask() {
        const that = this;
        return function(event) {
            const businessObject = that.bpmnFactory.create('bpmn:Task',{name:'ORM存取节点'});
            const shape = that.elementFactory.createShape({
                type: 'bpmn:Task',
                businessObject
            });
            console.log(shape) // 只在拖动或者点击时触发
            that.create.start(event, shape);
        }
    }
    
}

CustomPalette.$inject = [
    'bpmnFactory',
    'create',
    'elementFactory',
    'palette',
    'translate'
]