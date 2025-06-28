<!-- 树形容器 -->
<template>          
<div :id="customizedLayout.name">
   <el-row :gutter="4">
        <el-col :span="23">
            <el-row :gutter="4" v-if="customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.showFilterArea == 'Y'">
                <el-col :span="24">
                    <el-input v-model="filterText" style="width: 98%" class="m-2" size="small" placeholder="Filter keyword" />
                </el-col>
            </el-row>
            <el-row :gutter="4">
                <el-col :span="24">
                    <el-scrollbar :height="(customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.treeDisplayHeight != null && parseInt(customizedLayout.layoutCfgJSON.treeDisplayHeight) != null) ? parseInt(customizedLayout.layoutCfgJSON.treeDisplayHeight) + 'px' : '100%'" style="overflow-x: hidden; width: 100%;">
                        <el-tree ref="treeRef" style="max-width: 100%" class="filter-tree" :data="(customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.treeDataSourceAttr != null && regionData[customizedLayout.layoutCfgJSON.treeDataSourceAttr] != null) ?regionData[customizedLayout.layoutCfgJSON.treeDataSourceAttr] : (regionData[customizedLayout.id] != null ? regionData[customizedLayout.id].data: null)" :props="defaultProps" default-expand-all :filter-node-method="filterNode" @current-change="currentChange" highlight-current/>
                    </el-scrollbar>
                </el-col>
            </el-row>
        </el-col>
        <el-col :span="1">
            <el-divider border-style="solid" direction="vertical" style="height:100%;margin-left: 1px;margin-right: 1px;align-self: center;"/>
        </el-col>
    </el-row>
</div>
</template>
<script>
import { ref} from 'vue';
import {executeOperationForCustomized} from '../../js/common.js'

export default {
    props: ['customizedLayout', 'customizedPage', 'funcDefine', 'regionData', 'parentPage', 'srcOperation'],
    
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
    components: {
    	
    },
    
    data() {   
        const defaultProps = ref({
            children: 'children',
            label: 'label',
        });

        const filterText = ref(null);

        const selectedValueKey = ref('selectedId_tree');
       
    	return {
            defaultProps,
            filterText,
            selectedValueKey
        }
    },
    watch: {
        'filterText': function(value, old) {
            this.$refs.treeRef.filter(value);
        }
    },
    mounted() {
        if(this.regionData[this.customizedLayout.id] == null) {
            this.regionData[this.customizedLayout.id] = {};
        }

        this.customizedLayout.initOperation=null;
        this.customizedLayout.currentChangeOperation=null;
       
        if(this.customizedLayout.layoutRegions[0].funcRegion.regionOperations.length > 0) {
            for(var index in this.customizedLayout.layoutRegions[0].funcRegion.regionOperations) {
                var regionOperation = this.customizedLayout.layoutRegions[0].funcRegion.regionOperations[index];
                var regionOperationJSON = null;
                if(regionOperation.operationCfg != null && regionOperation.operationCfg.trim().length > 0) {
                    regionOperationJSON = JSON.parse(regionOperation.operationCfg);
                }else {
                    regionOperationJSON = {};
                }
                regionOperation.operationCfgJSON = regionOperationJSON;
                if(regionOperationJSON != null && regionOperationJSON.treeOperationType == "2") {//初始化操作
                    this.customizedLayout.initOperation = regionOperation;
                }else {//树节点点击响应操作
                    this.customizedLayout.currentChangeOperation = regionOperation;
                }

            }
        }

        var layoutCfgJSON = null;
        if(this.customizedLayout.layoutCfg == null || this.customizedLayout.layoutCfg.trim().length == 0) {
            layoutCfgJSON = {};
        }else {
            layoutCfgJSON = JSON.parse(this.customizedLayout.layoutCfg);
        }
        if(layoutCfgJSON.showFilterArea == null || layoutCfgJSON.showFilterArea.trim().length == 0) {
            layoutCfgJSON.showTitleArea = 'N';
        }
        this.customizedLayout.layoutCfgJSON = layoutCfgJSON;
        this.customizedLayout.layoutCfg = JSON.stringify(layoutCfgJSON);

        if(this.customizedLayout.layoutCfgJSON.labelAttr != null && this.customizedLayout.layoutCfgJSON.labelAttr.trim().length > 0) {
            this.defaultProps.label = this.customizedLayout.layoutCfgJSON.labelAttr;
        }
        if(this.customizedLayout.layoutCfgJSON.childrenAttr != null && this.customizedLayout.layoutCfgJSON.childrenAttr.trim().length > 0) {
            this.defaultProps.children = this.customizedLayout.layoutCfgJSON.childrenAttr;
        }

        if(this.customizedLayout.layoutCfgJSON != null && this.customizedLayout.layoutCfgJSON.selectedValueKey != null && this.customizedLayout.layoutCfgJSON.selectedValueKey.length > 0) {
            this.selectedValueKey = this.customizedLayout.layoutCfgJSON.selectedValueKey;
        }

        if(this.customizedLayout.initOperation != null && this.customizedLayout.initOperation.serviceOperationId != null) {//执行初始化操作
            executeOperationForCustomized(this.customizedLayout.initOperation, this.funcDefine, this.customizedPage, this.regionData, this.hideDialogPage, this.showDialogPage, this.refreshTableData, this.objectUpdated, this.parentPage, this.srcOperation, null, null);
        }
    },
    methods: {
        filterNode(value, data) {
            if(value == null || value.trim().length == 0) {
                return true;
            }
            return data[this.customizedLayout.layoutCfgJSON.labelAttr != null ? this.customizedLayout.layoutCfgJSON.labelAttr : 'label'].includes(value);
        },
        currentChange(value, data) {
            if(value == null) {
                this.regionData[this.selectedValueKey] = null;
            }else {
                if(this.customizedLayout.layoutCfgJSON != null && this.customizedLayout.layoutCfgJSON.valueAttr != null && this.customizedLayout.layoutCfgJSON.valueAttr.length > 0) {
                    this.regionData[this.selectedValueKey] = value[this.customizedLayout.layoutCfgJSON.valueAttr];
                }else {
                    this.regionData[this.selectedValueKey] = value['value'];
                }
            }
            if(this.customizedLayout.currentChangeOperation != null) {//执行初始化操作
                executeOperationForCustomized(this.customizedLayout.currentChangeOperation, this.funcDefine, this.customizedPage, this.regionData, this.hideDialogPage, this.showDialogPage, this.refreshTableData, this.objectUpdated, this.parentPage, this.srcOperation, null, null);
            }
        }
    }
    
 }
</script>
<style scoped>

</style>