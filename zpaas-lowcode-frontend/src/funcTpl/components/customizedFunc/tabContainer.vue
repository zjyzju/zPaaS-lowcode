<!-- 功能区域设计器-标签页容器 -->
<template>
<div v-if="customizedLayout != null && customizedLayout.tabs != null">
    <el-row :gutter="4" >
        <el-col :span="24">
            <el-tabs v-model="activeTab" style="width: 100%;">
                <el-tab-pane v-for="(tab,tabIndex) in customizedLayout.tabs" :label="tab.tabName" :name="tab.tabCode" style="height: fit-content; min-height: 200px;">
                    <div v-if="customizedLayout.tabSubComponents[tabIndex] != null" v-for="subLayout in customizedLayout.tabSubComponents[tabIndex]">
                        <formContainer v-if="subLayout.componentCode == 'MCL'" :customizedLayout="subLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation"/>
                        <reportContainer v-if="subLayout.componentCode == 'RCL'" :customizedLayout="subLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation"/>
                        <chartContainer v-if="subLayout.componentCode == 'CCL'" :customizedLayout="subLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation"/>
                        <tableContainer v-else-if="subLayout.componentCode == 'NCL'" :customizedLayout="subLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPage" @refreshTableData="refreshTableData"/>
                        <verticalLayoutContainer v-else-if="subLayout.componentCode == 'QLC'" :customizedLayout="subLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation" :customizedPageForm="customizedPageForm" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPage" @refreshTableData="refreshTableData"/>
                        <buttonContainer v-else-if="subLayout.componentCode == 'BCL'" :customizedLayout="subLayout" :funcDefine="funcDefine" :customizedPage="customizedPage" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation" :customizedPageForm="customizedPageForm" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPage" @refreshTableData="refreshTableData"/>
                        <tabContainer v-else-if="subLayout.componentCode == 'OLC'" :customizedLayout="subLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation" :customizedPageForm="customizedPageForm" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPage" @refreshTableData="refreshTableData"/>
                        <dividerComponent v-else-if="subLayout.componentCode == 'DOC'" :customizedLayout="subLayout"/> 
                        <brComponent v-else-if="customizedLayout.componentCode == 'LOC'" :customizedLayout="customizedLayout"/>
                        <treeContainer v-else-if="subLayout.componentCode == 'TCL'" :customizedLayout="subLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPage" @refreshTableData="refreshTableData"/>
                        <anchorComponent v-else-if="customizedLayout.componentCode == 'AOC'" :customizedLayout="customizedLayout"/>
                    </div>
                </el-tab-pane>
            </el-tabs>
        </el-col>
    </el-row>
</div>
</template>

<script>
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
    props: ['customizedLayout', 'customizedPage', 'funcDefine', 'regionData', 'parentPage', 'srcOperation', 'customizedPageForm'],
    
    emits: ['hideDialogPage', 'objectUpdated', 'showDialogPage', 'refreshTableData'],
        
    setup(props, {attrs, emit, slots}) {
        const hideDialogPage = ()=> {
            emit('hideDialogPage');
        }

        const objectUpdated = (updateObject, operation) => {
            emit('objectUpdated', updateObject, operation);
        }

        const showDialogPage = (customizedPageId, data, srcOperation) => {
            emit('showDialogPage', customizedPageId, data, srcOperation);
        }

        const refreshTableData = (currentPage, customizedPageId, clearCondition) => {
            emit('refreshTableData', currentPage, customizedPageId, clearCondition);
        }

        return {
            hideDialogPage,
            objectUpdated,
            showDialogPage,
            refreshTableData
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
        dividerComponent,
        brComponent,
        treeContainer,
        anchorComponent,
        reportContainer,
        chartContainer
    },
    
    data() {   
        var activeTab = ref(null);

    	return {
            activeTab
        }
    },
    watch: {
        
    },
    mounted() {
        this.customizedLayout.tabs = this.customizedLayout.layoutCfgJSON.tabs;

        this.activeTab = this.customizedLayout.layoutCfgJSON.tabs[0].tabCode;
        this.customizedLayout.tabSubComponents = [];
        
        for(var index in this.customizedLayout.layoutCfgJSON.tabs) {
            if(this.customizedLayout.tabSubComponents[index] == null) {
                this.customizedLayout.tabSubComponents[index] = [];
            }
        }

        for(var i in this.customizedLayout.subCustomizedLayouts) {
            var subLayoutCfgJSON = this.customizedLayout.subCustomizedLayouts[i].layoutCfgJSON;
            if(subLayoutCfgJSON.tabIndex == null || subLayoutCfgJSON.tabIndex.trim().length == 0) {
                subLayoutCfgJSON.tabIndex = 0;
            }
            if(this.customizedLayout.layoutCfgJSON.tabs[subLayoutCfgJSON.tabIndex] != null) {
                this.customizedLayout.tabSubComponents[subLayoutCfgJSON.tabIndex].push(this.customizedLayout.subCustomizedLayouts[i]);
            }
        }
    },
    methods: {
        
    }    
}
</script>
<style scoped>

</style>