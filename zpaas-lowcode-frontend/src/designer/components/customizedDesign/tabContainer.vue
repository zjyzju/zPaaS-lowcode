<!-- 功能区域设计器-标签页容器 -->
<template>
<div class="dragArea">
    <el-row :gutter="4">
        <el-col :span="24">
            <span class="floatDeleteTabContainer">
                <el-icon @click="onCustomizedLayoutSelected(tabComponent)"><Management /></el-icon>
                <el-link class="floatDeleteTab" size="small" @click="deleteContainer(this.componentIndex)"><el-icon><Delete /></el-icon></el-link>
            </span>
        </el-col>
    </el-row>
    <el-row :gutter="4" >
        <el-col :span="24">
            <el-tabs v-model="activeTab" style="width: 100%;" editable @edit="handleTabeEdit">
                <el-tab-pane v-for="(tab,tabIndex) in tabComponent.tabs" :label="tab.tabName" :name="tab.tabCode" style="height: fit-content; min-height: 200px;">
                    <draggable v-model="tabComponent.tabSubComponents[tabIndex]" @change="onChange" group="containerComponent" tag="div" item-key="id" style="min-height: 200px;">
                        <template #item="{element : containerComponent, index}">
                            <formContainer v-if="containerComponent.componentCode == 'MCL'" :formComponent="containerComponent" :componentIndex="tabIndex+':'+index" @deleteContainer="deleteTabContainer" @onAttrSelected="onAttrSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
                            <reportContainer v-else-if="containerComponent.componentCode == 'RCL'" :formComponent="containerComponent" :componentIndex="tabIndex+':'+index" @deleteContainer="deleteTabContainer" @onAttrSelected="onAttrSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
                            <chartContainer v-else-if="containerComponent.componentCode == 'CCL'" :formComponent="containerComponent" :componentIndex="tabIndex+':'+index" @deleteContainer="deleteTabContainer" @onAttrSelected="onAttrSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
                            <tableContainer v-else-if="containerComponent.componentCode == 'NCL'" :tableComponent="containerComponent" :componentIndex="tabIndex+':'+index" @deleteContainer="deleteTabContainer" @onAttrSelected="onAttrSelected" @onOperationSelected="onOperationSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
                            <buttonContainer v-else-if="containerComponent.componentCode == 'BCL'" :buttonComponent="containerComponent" :componentIndex="tabIndex+':'+index" @deleteContainer="deleteTabContainer" @onOperationSelected="onOperationSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea_B"/>
                            <tabContainer v-else-if="containerComponent.componentCode == 'OLC'" :tabComponent="containerComponent" :componentIndex="tabIndex+':'+index" @deleteContainer="deleteTabContainer" @onAttrSelected="onAttrSelected" @onOperationSelected="onOperationSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
                            <verticalLayoutContainer v-else-if="containerComponent.componentCode == 'QLC'" :layoutComponent="containerComponent" :componentIndex="tabIndex+':'+index" @deleteContainer="deleteTabContainer" @onAttrSelected="onAttrSelected" @onOperationSelected="onOperationSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" class="subArea"/>
                            <dividerComponent v-else-if="containerComponent.componentCode == 'DOC'" :dividerComponent="containerComponent" :componentIndex="tabIndex+':'+index" @deleteContainer="deleteTabContainer" @onCustomizedLayoutSelected="onCustomizedLayoutSelected"/>
                            <brComponent v-else-if="containerComponent.componentCode == 'LOC'" :brComponent="containerComponent" :componentIndex="tabIndex+':'+index" @deleteContainer="deleteTabContainer" @onCustomizedLayoutSelected="onCustomizedLayoutSelected"/>
                            <treeContainer v-else-if="containerComponent.componentCode == 'TCL'" :treeComponent="containerComponent" :componentIndex="tabIndex+':'+index" @deleteContainer="deleteTabContainer" @onCustomizedLayoutSelected="onCustomizedLayoutSelected" @onOperationSelected="onOperationSelected" class="subArea"/>
                            <anchorComponent v-else-if="containerComponent.componentCode == 'AOC'" :anchorComponent="containerComponent" :componentIndex="tabIndex+':'+index" @deleteContainer="deleteTabContainer" @onCustomizedLayoutSelected="onCustomizedLayoutSelected"/>
                        </template>
                    </draggable>
                </el-tab-pane>
            </el-tabs>
        </el-col>
    </el-row>
</div>
</template>

<script>
import draggable from 'vuedraggable'
import {ref, defineAsyncComponent } from 'vue';

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
    props: ['componentIndex', 'tabComponent'],
    
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
            onOperationSelected,
            onCustomizedLayoutSelected
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
        var activeTab = ref(null);

    	return {
            activeTab
        }
    },
    watch: {
        'tabComponent.layoutCfgJSON': {
            handler: function(value, old) {
                this.tabComponent.layoutCfg = JSON.stringify(this.tabComponent.layoutCfgJSON);
                this.tabComponent.tabs = this.tabComponent.layoutCfgJSON.tabs;
            },
            deep: true
        }
        
    },
    mounted() {
        if(this.tabComponent.subCustomizedLayouts == null) {
            this.tabComponent.subCustomizedLayouts = [];
        }
        var layoutCfgJSON = null;
        if(this.tabComponent.layoutCfg == null || this.tabComponent.layoutCfg.trim().length == 0) {
            layoutCfgJSON = {};
        }else {
            layoutCfgJSON = JSON.parse(this.tabComponent.layoutCfg);
        }
        if(layoutCfgJSON.tabs == null || layoutCfgJSON.tabs.length == 0) {
            layoutCfgJSON.tabs = [];
            layoutCfgJSON.tabs[0] = {
                tabCode: 'tab1',
                tabName: '标签页1'
            };
        }
        this.tabComponent.layoutCfgJSON = layoutCfgJSON;
        this.tabComponent.layoutCfg = JSON.stringify(layoutCfgJSON);
        this.tabComponent.tabs = layoutCfgJSON.tabs;

        this.activeTab = layoutCfgJSON.tabs[0].tabCode;
        this.tabComponent.tabSubComponents = [];
        
        for(var index in layoutCfgJSON.tabs) {
            if(this.tabComponent.tabSubComponents[index] == null) {
                this.tabComponent.tabSubComponents[index] = [];
            }
        }

        for(var i in this.tabComponent.subCustomizedLayouts) {
            var subCustomizedLayout = this.tabComponent.subCustomizedLayouts[i];
            var subLayoutCfgJSON = null;
            if(subCustomizedLayout.layoutCfg == null || subCustomizedLayout.layoutCfg.trim().length == 0) {
                subLayoutCfgJSON = {};
            }else {
                subLayoutCfgJSON = JSON.parse(subCustomizedLayout.layoutCfg);
            }
            if(subLayoutCfgJSON.tabIndex == null || subLayoutCfgJSON.tabIndex.trim().length == 0) {
                subLayoutCfgJSON.tabIndex = 0;
            }
            subCustomizedLayout.layoutCfg = JSON.stringify(subLayoutCfgJSON);
            if(layoutCfgJSON.tabs[subLayoutCfgJSON.tabIndex] != null) {
                this.tabComponent.tabSubComponents[subLayoutCfgJSON.tabIndex].push(subCustomizedLayout);
            }
        }
    },
    methods: {
        onChange(event) {
            var layouts = [];
            var displayOrder = 1;
            for(var i in this.tabComponent.tabSubComponents) {
                for(var j in this.tabComponent.tabSubComponents[i]) {
                    var subCustomizedLayout = this.tabComponent.tabSubComponents[i][j];
                    layouts.push(subCustomizedLayout);
                    var subLayoutCfgJSON = null;
                    if(subCustomizedLayout.layoutCfg == null || subCustomizedLayout.layoutCfg.trim().length == 0) {
                        subLayoutCfgJSON = {};
                    }else {
                        subLayoutCfgJSON = JSON.parse(subCustomizedLayout.layoutCfg);
                    }
                    subLayoutCfgJSON.tabIndex = i;
                    subCustomizedLayout.layoutCfg = JSON.stringify(subLayoutCfgJSON);
                    subCustomizedLayout.displayOrder = displayOrder++;
                }
            }

            this.tabComponent.subCustomizedLayouts = layouts;
        },
        deleteTabContainer(componentIndex) {
            if(componentIndex == null) {
                return;
            }
            var tabAndIndex = componentIndex.split(":");
            this.tabComponent.tabSubComponents[tabAndIndex[0]].splice(parseInt(tabAndIndex[1]),1);
        },
        handleTabeEdit(tab, action) {
            if(action == "add") {
                var maxIndex = 0;
                for(var i in this.tabComponent.layoutCfgJSON.tabs) {
                    var index = parseInt(this.tabComponent.layoutCfgJSON.tabs[i].tabCode.substr(3));
                    if(maxIndex < index) {
                        maxIndex = index;
                    }
                }
                maxIndex++;
                this.tabComponent.layoutCfgJSON.tabs.push({
                    tabCode: 'tab'+maxIndex,
                    tabName: '标签页'+maxIndex
                });
                this.tabComponent.tabSubComponents[this.tabComponent.layoutCfgJSON.tabs.length-1] = [];
            }else if(action == "remove") {
                for(var i in this.tabComponent.layoutCfgJSON.tabs) {
                    if(this.tabComponent.layoutCfgJSON.tabs[i].tabCode == tab) {
                        this.tabComponent.layoutCfgJSON.tabs.splice(i, 1);
                        this.tabComponent.tabSubComponents.splice(i, 1);
                        this.onChange(null);
                        if(this.activeTab == tab) {
                            if(this.tabComponent.layoutCfgJSON.tabs.length > 0) {
                                this.activeTab = this.tabComponent.layoutCfgJSON.tabs[0].tabCode;
                            }else {
                                this.activeTab = "";
                            }
                            
                        }
                        break;
                    }
                }
            }
        }
    }    
}
</script>
<style scoped>
.dragArea {
    outline: 1px dashed;
    width:99%;
    outline-color: darkgray;
    min-height: 200px;
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
</style>