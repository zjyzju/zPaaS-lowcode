<!-- 功能区域设计器-垂直布局控件 -->
<template>
<div class="dragArea">
    <el-row :gutter="4">
        <el-col :span="24">
            <span class="floatDeleteTabContainer">
                <el-icon @click="onCustomizedLayoutSelected(layoutComponent)"><Menu/></el-icon>
                <el-link class="floatDeleteTab" size="small" @click="deleteContainer(this.componentIndex)"><el-icon><Delete /></el-icon></el-link>
            </span>
        </el-col>
    </el-row>
    <el-row :gutter="4" >
        <el-col :span="parseInt(layoutComponent.layoutCfgJSON.subAreaSpans[i])" class="dragArea" v-for="(subAreaComponentArray, i) in layoutComponent.subAreaComponents" v-if="layoutComponent.layoutCfgJSON != null">
            <draggable v-model="layoutComponent.subAreaComponents[i]" @change="onChange" group="containerComponent" tag="div" item-key="rowIndex" style="min-height: 100px;">
                <template #item="{element : containerComponent, index}">
                    <formContainer v-if="containerComponent.componentCode == 'MCL'" :formComponent="containerComponent" :componentIndex="i + ':' + index" @deleteContainer="deleteSubContainer"  @onAttrSelected="onAttrSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
                    <reportContainer v-else-if="containerComponent.componentCode == 'RCL'" :formComponent="containerComponent" :componentIndex="i + ':' + index" @deleteContainer="deleteSubContainer"  @onAttrSelected="onAttrSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
                    <chartContainer v-else-if="containerComponent.componentCode == 'CCL'" :formComponent="containerComponent" :componentIndex="i + ':' + index" @deleteContainer="deleteSubContainer"  @onAttrSelected="onAttrSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
                    <tableContainer v-else-if="containerComponent.componentCode == 'NCL'" :tableComponent="containerComponent" :componentIndex="i + ':' + index" @deleteContainer="deleteSubContainer"  @onAttrSelected="onAttrSelected" @onOperationSelected="onOperationSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
                    <buttonContainer v-else-if="containerComponent.componentCode == 'BCL'" :buttonComponent="containerComponent" :componentIndex="i + ':' + index" @deleteContainer="deleteSubContainer" @onOperationSelected="onOperationSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea_B"/>
                    <tabContainer v-else-if="containerComponent.componentCode == 'OLC'" :tabComponent="containerComponent" :componentIndex="i + ':' + index" @deleteContainer="deleteSubContainer"  @onAttrSelected="onAttrSelected" @onOperationSelected="onOperationSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
                    <verticalLayoutContainer v-else-if="containerComponent.code == 'QLC'" :layoutComponent="containerComponent" :componentIndex="i + ':' + index" @deleteContainer="deleteSubContainer"  @onAttrSelected="onAttrSelected" @onOperationSelected="onOperationSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
                    <dividerComponent v-else-if="containerComponent.componentCode == 'DOC'" :dividerComponent="containerComponent" :componentIndex="i + ':' + index" @deleteContainer="deleteSubContainer" @onCustomizedLayoutSelected="onCustomizedLayoutSelected"/>
                    <brComponent v-else-if="containerComponent.componentCode == 'LOC'" :brComponent="containerComponent" :componentIndex="i + ':' + index" @deleteContainer="deleteSubContainer" @onCustomizedLayoutSelected="onCustomizedLayoutSelected"/>
                    <treeContainer v-else-if="containerComponent.componentCode == 'TCL'" :treeComponent="containerComponent" :componentIndex="i + ':' + index" @deleteContainer="deleteSubContainer" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" @onOperationSelected="onOperationSelected" class="subArea"/>
                    <anchorComponent v-else-if="containerComponent.componentCode == 'AOC'" :anchorComponent="containerComponent" :componentIndex="i + ':' + index" @deleteContainer="deleteSubContainer" @onCustomizedLayoutSelected="onCustomizedLayoutSelected"/>
                </template>
            </draggable>
        </el-col>
    </el-row>
</div>
</template>

<script>
import draggable from 'vuedraggable'
import {defineAsyncComponent } from 'vue';

import formContainer from './formContainer.vue'
import reportContainer from './reportContainer.vue'
import chartContainer from './chartContainer.vue'
// const formContainer = defineAsyncComponent(() => import('./formContainer.vue'));
import tableContainer from './tableContainer.vue'
// const tableContainer = defineAsyncComponent(() => import('./tableContainer.vue'));
import buttonContainer from './buttonContainer.vue'
// const buttonContainer = defineAsyncComponent(() => import('./buttonContainer.vue'));
// import tabContainer from './tabContainer.vue'
const tabContainer = defineAsyncComponent(() => import('./tabContainer.vue'));
// import verticalLayoutContainer from './verticalLayoutContainer.vue'
const verticalLayoutContainer = defineAsyncComponent(() => import('./verticalLayoutContainer.vue'));

import dividerComponent from './dividerComponent.vue'
import brComponent from './brComponent.vue'
import treeContainer from './treeContainer.vue'
import anchorComponent from './anchorComponent.vue'

export default {
    props: ['componentIndex', 'layoutComponent'],
    
    emits: ['onAttrSelected', 'deleteContainer', 'onOperationSelected', 'onCustomizedLayoutSelected'],
    
    setup (props, {attrs, emit, slots}) {
        const onAttrSelected = (attrAssign, formComponent) => {
            emit('onAttrSelected', attrAssign, formComponent);
        };

        const onOperationSelected = (operation) => {
            emit('onOperationSelected', operation);
        };

        const onCustomizedLayoutSelected = (customizedLayout) => {
            emit('onCustomizedLayoutSelected', customizedLayout);
        };

        const deleteContainer = (componentIndex) => {
            emit('deleteContainer', componentIndex);
        };

        return {
        	onAttrSelected,
            deleteContainer,
            onCustomizedLayoutSelected,
            onOperationSelected
        }
    },
    computed: {
        
    },
    components: {
        formContainer,
        reportContainer,
        chartContainer,
        tableContainer,
        buttonContainer,
        tabContainer,
        verticalLayoutContainer,
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
    watch: {
        'layoutComponent.layoutCfgJSON.splitNumber': function(value, old) {
            if(old == null) {
                return;
            }
            var subAreaSpans = [];
            var splitNumber = parseInt(value);
            var originNumber = parseInt(old);
            for(var i=0; i<splitNumber; i++) {
                subAreaSpans[i] = 24/splitNumber;
            }
            this.layoutComponent.layoutCfgJSON.subAreaSpans = subAreaSpans;
            this.layoutComponent.layoutCfg = JSON.stringify(this.layoutComponent.layoutCfgJSON);
            if(splitNumber > originNumber) {
                for(var i=originNumber; i < splitNumber; i++) {
                    this.layoutComponent.subAreaComponents[i] = [];
                }
            }else {
                this.layoutComponent.subAreaComponents.splice(splitNumber, (originNumber-splitNumber));
                this.onChange(null);
            }
        }
    },
    mounted() {
        var layoutCfgJSON = null;
        if(this.layoutComponent.layoutCfg == null || this.layoutComponent.layoutCfg.trim().length == 0) {
            layoutCfgJSON = {};
        }else {
            layoutCfgJSON = JSON.parse(this.layoutComponent.layoutCfg);
        }
        if(layoutCfgJSON.splitNumber == null || layoutCfgJSON.splitNumber == 0) {
            layoutCfgJSON.splitNumber = 2;
            layoutCfgJSON.subAreaSpans = [];
            layoutCfgJSON.subAreaSpans[0] = 8;
            layoutCfgJSON.subAreaSpans[1] = 16;
        }
        this.layoutComponent.layoutCfgJSON = layoutCfgJSON;
        this.layoutComponent.layoutCfg = JSON.stringify(layoutCfgJSON);

        this.layoutComponent.subAreaComponents = [];
        for(var i=0; i<this.layoutComponent.layoutCfgJSON.splitNumber; i++) {
            this.layoutComponent.subAreaComponents[i] = [];
        }
        
        if(this.layoutComponent.subCustomizedLayouts == null) {
            this.layoutComponent.subCustomizedLayouts = [];
        }

        for(var i in this.layoutComponent.subCustomizedLayouts) {
            var subLayoutCfgJSON = null;
            if(this.layoutComponent.subCustomizedLayouts[i].layoutCfg == null || this.layoutComponent.subCustomizedLayouts[i].layoutCfg.trim().length == 0) {
                subLayoutCfgJSON = {};
            }else {
                subLayoutCfgJSON = JSON.parse(this.layoutComponent.subCustomizedLayouts[i].layoutCfg);
            }
            if(subLayoutCfgJSON.index == null) {
                subLayoutCfgJSON.index = 0;
            }
            this.layoutComponent.subAreaComponents[subLayoutCfgJSON.index].push(this.layoutComponent.subCustomizedLayouts[i]);
            
            this.layoutComponent.subCustomizedLayouts[i].layoutCfg = JSON.stringify(subLayoutCfgJSON);
        }
    },
    methods: {
        onChange(event) {
            var customizedLayouts = [];
            var displayOrder = 1;
            for(var i in this.layoutComponent.subAreaComponents) {
                for(var j in this.layoutComponent.subAreaComponents[i]) {
                    var subAreaComponent = this.layoutComponent.subAreaComponents[i][j];
                    var layoutCfgJSON = null;
                    if(subAreaComponent.layoutCfg == null || subAreaComponent.layoutCfg.trim().length == 0) {
                        layoutCfgJSON = {};
                    }else {
                        layoutCfgJSON = JSON.parse(subAreaComponent.layoutCfg);
                    }
                    layoutCfgJSON.index = i;
                    subAreaComponent.layoutCfg = JSON.stringify(layoutCfgJSON);
                    subAreaComponent.displayOrder = displayOrder++;
                    customizedLayouts.push(subAreaComponent);
                }
            }
            this.layoutComponent.subCustomizedLayouts = customizedLayouts;
        },
        deleteSubContainer(componentIndex) {
            if(componentIndex == null) {
                return;
            }
            var index = componentIndex.split(":");
            this.layoutComponent.subAreaComponents[index[0]].splice(index[1],1);
            this.onChange(null);
        }
    }
    
 }
</script>
<style scoped>
.dragArea {
    outline: 1px dashed;
    width:99%;
    outline-color: darkgray;
    min-height: 6em;
    display: block;
    margin: 0 auto;
    margin-top: 0.5ch;
    margin-bottom: 0.5ch;
    padding-top: 0.5ch;
    padding-bottom: 0.5ch;
    padding-left: 0.5ch;
}
.floatDeleteTab{
    display: none;
    float: right;
    margin-right: 98%; 
    margin-top: -33px;
    z-index: 99;
}

.floatDeleteTabContainer{
    &:hover .floatDeleteTab{
        display: block;  
    }
}
.subArea {
    outline: 1px dashed;
    width:99%;
    outline-color: darkgray;
    min-height: 6em;
    display: block;
    margin: 0 auto;
    margin-top: 0.5ch;
    margin-bottom: 0.5ch;
    padding-top: 0.5ch;
    padding-bottom: 0.5ch;
    padding-left: 0.5ch;
}

.subArea_B {
    outline: 1px dashed;
    width:99%;
    outline-color: darkgray;
    min-height: 4em;
    display: block;
    margin: 0 auto;
    margin-top: 0.5ch;
    margin-bottom: 0.5ch;
    padding-top: 0.5ch;
    padding-bottom: 0.5ch;
    padding-left: 0.5ch;
}
</style>