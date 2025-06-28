<!-- 功能区域设计器-按钮容器 -->
<template>          
<div >
    <el-row :gutter="4">
        <el-col :span="1"></el-col>
        <el-col :span="22">
            <div class="toolbar1"  style="justify-content: right; display: flex; flex-wrap: nowrap;">
                <span v-if="customizedLayout.layoutRegions[0].funcRegion.regionOperations != null" v-for="operation in customizedLayout.layoutRegions[0].funcRegion.regionOperations">
                    <popupViewButtonComponent v-if="operation.operationType == 'V'" :operation="operation" :regionData="regionData" :funcRegion="customizedLayout.layoutRegions[0].funcRegion" :funcDefine="funcDefine" mainPage="Y"/>
                    <el-button v-else  @click="executeOperation(operation)" :type="(operation.operationCfgJSON == null || operation.operationCfgJSON.displayColor == null) ? 'primary' : operation.operationCfgJSON.displayColor"  :size="(operation.operationCfgJSON == null || operation.operationCfgJSON.displaySize == null) ? 'small' : operation.operationCfgJSON.displaySize" :round="operation.operationCfgJSON != null && operation.operationCfgJSON.displayStyle == 'round'" :plain="operation.operationCfgJSON != null && operation.operationCfgJSON.displayStyle == 'plain'" :circle="operation.operationCfgJSON != null && operation.operationCfgJSON.displayStyle == 'circle'" :link="operation.operationCfgJSON != null && operation.operationCfgJSON.displayStyle == 'link'">{{operation.name}}</el-button>
                    &nbsp;
                </span>
            </div>
        </el-col>
        <el-col :span="1"></el-col>
     </el-row>
</div>
</template>
<script>
import {ElMessage} from 'element-plus'
import popupViewButtonComponent from '../common/popupViewButtonComponent.vue'
import {executeOperationForCustomized} from '../../js/common.js'

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
    	popupViewButtonComponent
    },
    
    data() {   
       
    	return {
            
        }
    },
    mounted() {
    	if(this.customizedLayout.layoutRegions == null || this.customizedLayout.layoutRegions.length == 0 || this.customizedLayout.layoutRegions[0].funcRegion == null) {
            ElMessage.error("not found layoutRegion info!");
        }

        if(this.customizedLayout.layoutRegions[0].funcRegion.regionOperations.length > 0) {
            for(var i in this.customizedLayout.layoutRegions[0].funcRegion.regionOperations) {
                var operation = this.customizedLayout.layoutRegions[0].funcRegion.regionOperations[i];
                if(operation.operationCfg == null || operation.operationCfg.trim().length == 0) {
                    operation.operationCfgJSON = {};
                }else {
                    operation.operationCfgJSON = JSON.parse(operation.operationCfg);
                }
            }
        }
    },
    methods: {
        executeOperation(operation) {
            var isMainCheckRule = false;
            
            if(operation.operationCfg != null && operation.operationCfg.trim().length > 0) {
                var operationCfgJson = JSON.parse(operation.operationCfg);
                if(operationCfgJson.mainCheckRule != null && (operationCfgJson.mainCheckRule == "true" || operationCfgJson.mainCheckRule)) {
                    isMainCheckRule = true;
                }
            }
            if(isMainCheckRule && this.customizedPageForm != null) {
                this.customizedPageForm.validate((valid, fields)=> {
                    console.log(valid, fields);
                    if(valid) {
                        executeOperationForCustomized(operation, this.funcDefine, this.customizedPage, this.regionData, this.hideDialogPage, this.showDialogPage, this.refreshTableData, this.objectUpdated, this.parentPage, this.srcOperation, null, null);
                    }else {
                        console.log("参数校验失败！", fields);
                    }
                });
            }else {
                executeOperationForCustomized(operation, this.funcDefine, this.customizedPage, this.regionData, this.hideDialogPage, this.showDialogPage, this.refreshTableData, this.objectUpdated, this.parentPage, this.srcOperation, null, null);
            }
           
        }
    }
    
 }
</script>
<style scoped>

</style>