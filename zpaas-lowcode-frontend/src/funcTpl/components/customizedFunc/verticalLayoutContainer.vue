<!-- 功能区域设计器-垂直布局控件 -->
<template>
<div v-if="customizedLayout != null && customizedLayout.layoutCfgJSON != null">
    <el-row :gutter="4">
        <el-col :span="parseInt(customizedLayout.layoutCfgJSON.subAreaSpans[i])" class="dragArea" v-for="(subAreaComponentArray, i) in customizedLayout.subAreaComponents" v-if="customizedLayout.layoutCfgJSON != null">
            <div v-if="customizedLayout.subAreaComponents[i] != null" v-for="subLayout in customizedLayout.subAreaComponents[i]">
                <formContainer v-if="subLayout.componentCode == 'MCL'" :customizedLayout="subLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation"/>
                <reportContainer v-if="subLayout.componentCode == 'RCL'" :customizedLayout="subLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation"/>
                <chartContainer v-if="subLayout.componentCode == 'CCL'" :customizedLayout="subLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation"/>
                <tableContainer v-else-if="subLayout.componentCode == 'NCL'" :customizedLayout="subLayout":customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPage" @refreshTableData="refreshTableData"/>
                <verticalLayoutContainer v-else-if="subLayout.componentCode == 'QLC'" :customizedLayout="subLayout":customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation" :customizedPageForm="customizedPageForm" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPage" @refreshTableData="refreshTableData"/>
                <buttonContainer v-else-if="subLayout.componentCode == 'BCL'" :customizedLayout="subLayout":customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation" :customizedPageForm="customizedPageForm" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPage" @refreshTableData="refreshTableData"/>
                <tabContainer v-else-if="subLayout.componentCode == 'OLC'" :customizedLayout="subLayout":customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation" :customizedPageForm="customizedPageForm" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPage" @refreshTableData="refreshTableData"/>
                <dividerComponent v-else-if="subLayout.componentCode == 'DOC'" :customizedLayout="subLayout"/>
                <brComponent v-else-if="customizedLayout.componentCode == 'LOC'" :customizedLayout="customizedLayout"/>
                <treeContainer v-else-if="subLayout.componentCode == 'TCL'" :customizedLayout="subLayout":customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPage" @refreshTableData="refreshTableData"/>
                <anchorComponent v-else-if="customizedLayout.componentCode == 'AOC'" :customizedLayout="customizedLayout"/>
            </div>
        </el-col>
    </el-row>
</div>
</template>

<script>
import {defineAsyncComponent } from 'vue';

import formContainer from './formContainer.vue'
// const formContainer = defineAsyncComponent(() => import('./formContainer.vue'));
import reportContainer from './reportContainer.vue'
import chartContainer from './chartContainer.vue'
import tableContainer from './tableContainer.vue'
// const tableContainer = defineAsyncComponent(() => import('./tableContainer.vue'));
import buttonContainer from './buttonContainer.vue'
// const buttonContainer = defineAsyncComponent(() => import('./buttonContainer.vue'));
import tabContainer from './tabContainer.vue'
// const tabContainer = defineAsyncComponent(() => import('./tabContainer.vue'));
// import verticalLayoutContainer from './verticalLayoutContainer.vue'
const verticalLayoutContainer = defineAsyncComponent(() => import('./verticalLayoutContainer.vue'));

import treeContainer from './treeContainer.vue'

import dividerComponent from './dividerComponent.vue'
import brComponent from './brComponent.vue'
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
        
    	return {
            
        }
    },
    watch: {
        
    },
    mounted() {
        this.customizedLayout.subAreaComponents = [];
        for(var i=0; i<this.customizedLayout.layoutCfgJSON.splitNumber; i++) {
            this.customizedLayout.subAreaComponents[i] = [];
        }
        
        if(this.customizedLayout.subCustomizedLayouts == null) {
            this.customizedLayout.subCustomizedLayouts = [];
        }

        for(var i in this.customizedLayout.subCustomizedLayouts) {
            var subLayoutCfgJSON = this.customizedLayout.subCustomizedLayouts[i].layoutCfgJSON;
            this.customizedLayout.subAreaComponents[subLayoutCfgJSON.index].push(this.customizedLayout.subCustomizedLayouts[i]);
        }
    },
    methods: {
        
    }
    
 }
</script>
<style scoped>

</style>