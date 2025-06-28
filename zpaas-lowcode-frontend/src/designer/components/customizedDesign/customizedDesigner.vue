<!-- 自定义功能区域设计器 -->
<template>
    <draggable v-model="rootComponent.containerComponents" @change="onChange" group="containerComponent" tag="div" item-key="rowIndex" >
        <template #item="{element : containerComponent, index}">
            <formContainer v-if="containerComponent.componentCode == 'MCL'" :formComponent="containerComponent" :componentIndex="index" @deleteContainer="deleteContainer" @onAttrSelected="onAttrSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
            <tableContainer v-else-if="containerComponent.componentCode == 'NCL'" :tableComponent="containerComponent" :componentIndex="index" @deleteContainer="deleteContainer" @onAttrSelected="onAttrSelected" @onOperationSelected="onOperationSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
            <buttonContainer v-else-if="containerComponent.componentCode == 'BCL'" :buttonComponent="containerComponent" :componentIndex="index" @deleteContainer="deleteContainer" @onOperationSelected="onOperationSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea_B"/>
            <tabContainer v-else-if="containerComponent.componentCode == 'OLC'" :tabComponent="containerComponent" :componentIndex="index" @deleteContainer="deleteContainer" @onAttrSelected="onAttrSelected" @onOperationSelected="onOperationSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
            <verticalLayoutContainer v-else-if="containerComponent.componentCode == 'QLC'" :layoutComponent="containerComponent" :componentIndex="index" @deleteContainer="deleteContainer" @onAttrSelected="onAttrSelected" @onOperationSelected="onOperationSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
            <reportContainer v-else-if="containerComponent.componentCode == 'RCL'" :formComponent="containerComponent" :componentIndex="index" @deleteContainer="deleteContainer" @onAttrSelected="onAttrSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
            <chartContainer v-else-if="containerComponent.componentCode == 'CCL'" :formComponent="containerComponent" :componentIndex="index" @deleteContainer="deleteContainer" @onAttrSelected="onAttrSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
            <dividerComponent v-else-if="containerComponent.componentCode == 'DOC'" :dividerComponent="containerComponent" :componentIndex="index" @deleteContainer="deleteContainer" @onCustomizedLayoutSelected="onCustomizedLayoutSelected"/>
            <brComponent v-else-if="containerComponent.componentCode == 'LOC'" :brComponent="containerComponent" :componentIndex="index" @deleteContainer="deleteContainer" @onCustomizedLayoutSelected="onCustomizedLayoutSelected"/>
            <treeContainer v-else-if="containerComponent.componentCode == 'TCL'" :treeComponent="containerComponent" :componentIndex="index" @deleteContainer="deleteContainer" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" @onOperationSelected="onOperationSelected" class="subArea"/>
            <anchorComponent v-else-if="containerComponent.componentCode == 'AOC'" :anchorComponent="containerComponent" :componentIndex="index" @deleteContainer="deleteContainer" @onCustomizedLayoutSelected="onCustomizedLayoutSelected"/>
        </template>
    </draggable>
 </template>
<script>
import draggable from 'vuedraggable' 
import {ElMessageBox} from 'element-plus'

import formContainer from './formContainer.vue'
import tableContainer from './tableContainer.vue'
import buttonContainer from './buttonContainer.vue'
import tabContainer from './tabContainer.vue'
import verticalLayoutContainer from './verticalLayoutContainer.vue'
import reportContainer from './reportContainer.vue'
import chartContainer from './chartContainer.vue'
import dividerComponent from './dividerComponent.vue'
import brComponent from './brComponent.vue'
import treeContainer from './treeContainer.vue'
import anchorComponent from './anchorComponent.vue'

export default {
    props: ['rootComponent'],
    
    emits: ['onAttrSelected','onOperationSelected','onCustomizedLayoutSelected'],
    
    setup (props, {attrs, emit, slots}) {
        const onAttrSelected = (attrAssign, formComponent) => {
            emit('onAttrSelected', attrAssign, formComponent);
        };

        const onOperationSelected = (regionOperation) => {
            emit('onOperationSelected', regionOperation);
        };

        const onCustomizedLayoutSelected = (customizedLayout) => {
            emit('onCustomizedLayoutSelected', customizedLayout);
        };

        return {
        	onAttrSelected,
            onOperationSelected,
            onCustomizedLayoutSelected
        }
    },
    computed: {
        
    },
    components: {
    	formContainer,
        tableContainer,
        buttonContainer,
        tabContainer,
        verticalLayoutContainer,
        reportContainer,
        chartContainer,
        dividerComponent,
        draggable,
        brComponent,
        treeContainer,
        anchorComponent
    },
    
    data() {   
        
    	return {
            
        }
    },
    mounted() {
    	if(this.rootComponent.containerComponents == null) {
            this.rootComponent.containerComponents = [];
        }
    },
    methods: {
        onChange(event) {
            var displayOrder = 1;
            for(var i in this.rootComponent.containerComponents) {
                this.rootComponent.containerComponents[i].displayOrder = displayOrder++;
            }
            console.log(this.rootComponent);
            var componentsStr = JSON.stringify(this.rootComponent.containerComponents);
            this.rootComponent.containerComponents = null;
            setTimeout(()=>{
                this.rootComponent.containerComponents = JSON.parse(componentsStr);
            }, 50);
        },
    	deleteContainer(componentIndex) {
            if(componentIndex == null || (componentIndex < 0 && componentIndex >= this.rootComponent.containerComponents.length)) {
                return;
            }
            ElMessageBox.confirm(
                    '该组件将被删除. 是否继续?',
                    '警告',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning',
                    }
                ).then(() => {
                    this.rootComponent.containerComponents.splice(componentIndex,1);
                });
        }
    }
 }
</script>
<style scoped>
.dragArea {
  outline: 1px dashed;
  width:100%;
  outline-color: darkgray;
  min-height: 20em;
  padding-top: 0.5ch;
  padding-bottom: 0.5ch;
}

.subArea {
  outline: 1px dashed;
  width:99%;
  outline-color: darkgray;
  min-height: 6em;
  display: block;
  margin: 0 auto;
  margin-top: 0.5ch;
  padding-bottom: 0.5ch;
}

.subArea_B {
  outline: 1px dashed;
  width:99%;
  outline-color: darkgray;
  min-height: 4em;
  display: block;
  margin: 0 auto;
  margin-top: 0.5ch;
  padding-bottom: 0.5ch;
}

.floatDeleteContainer{
    &:hover .floatDelete{
        display: block;  
    }
}
.floatDelete{
    display: none;
    float: left;
    margin-left: 95.5%; 
    margin-top: -42px;
    z-index: 99;
}
</style>