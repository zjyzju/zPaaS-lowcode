<!-- 功能区域设计器-表格容器 -->
<template>          
<div >
<el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
<el-row :gutter="4" v-if="customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.showTitleArea == 'Y'">
    <el-col :span="24">
        <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
        <div class="toolbar1">
            <span :id="customizedLayout.name" class="title">{{ customizedLayout.name }}</span>&nbsp;&nbsp;

            <span v-if="customizedLayout.titleButtonComponents != null" v-for="operation in customizedLayout.titleButtonComponents">
                <popupViewButtonComponent v-if="operation.operationType == 'V'" :operation="operation" :regionData="regionData" :funcRegion="customizedLayout.layoutRegions[0].funcRegion" :funcDefine="funcDefine" mainPage="Y"/>
                <el-link v-else :type="(operation.operationCfgJSON == null || operation.operationCfgJSON.displayColor == null) ? 'primary' : operation.operationCfgJSON.displayColor" @click="executeTitleOperation(operation)">{{operation.name}}</el-link>
                &nbsp;
            </span>
        </div>
        </el-divider>
    </el-col>
</el-row>
<el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
<el-row :gutter="4">
    <el-col :span="24">
		<el-table  v-if="customizedLayout.layoutRegions != null && customizedLayout.layoutRegions[0] != null && customizedLayout.layoutRegions[0].funcRegion.regionAttrAssigns != null && regionData != null" :data="(customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.tableDataSourceAttr != null && regionData[customizedLayout.layoutCfgJSON.tableDataSourceAttr] != null) ?regionData[customizedLayout.layoutCfgJSON.tableDataSourceAttr] : (regionData[customizedLayout.id] != null ? regionData[customizedLayout.id].data: null)" @current-change="handleCurrentChange" @selection-change="handleSelectionChange" :highlight-current-row="customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.selectMode == 'S'" stripe style="width: 100%">
		    <el-table-column type="selection" width="30" v-if="customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.selectMode == 'M'"/>
            <el-table-column  label="操作" width="180" v-if="customizedLayout.buttonComponents != null && customizedLayout.buttonComponents.length > 0">
		        <template #default="scope">
		            <span v-if="customizedLayout.buttonComponents != null" v-for="operation in customizedLayout.buttonComponents">
                        <popupViewButtonComponent v-if="operation.operationType == 'V'" :operation="operation" :regionData="scope.row" :funcRegion="customizedLayout.layoutRegions[0].funcRegion" :funcDefine="funcDefine" mainPage="Y"/>
                        <el-link v-else :type="(operation.operationCfgJSON == null || operation.operationCfgJSON.displayColor == null) ? 'primary' : operation.operationCfgJSON.displayColor" @click="executeOperation(operation, scope.row, scope.$index)">{{operation.name}}</el-link>
                        &nbsp;
                    </span>
		        </template>
		    </el-table-column>
		    <el-table-column v-if="customizedLayout.layoutRegions[0].funcRegion.regionAttrAssigns != null" v-for="(attrAssign, index) in customizedLayout.layoutRegions[0].funcRegion.regionAttrAssigns" 
		                :prop="attrAssign.attribute.code" :label="attrAssign.displayName" :width="attrAssign.displayWidth" :sortable="attrAssign.displayCfgJSON != null && attrAssign.displayCfgJSON.sortable">
		        <template #default="scope">
                    <popupViewComponent  v-if="attrAssign.displayType == 'V'" :funcRegion="customizedLayout.layoutRegions[0].funcRegion" :funcDefine="funcDefine" :regionData="scope.row" :attrAssign="attrAssign" :disableFlag="true" :subObject="null"/>
		            <tableColumnComponent v-else :row="scope.row" :attrAssign="attrAssign" :attrCode="attrAssign.attribute.code" :displayType="attrAssign.displayType" :attrOption="funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId]"/>
		        </template>
		    </el-table-column>
		</el-table>
    </el-col>
</el-row>
<el-row :gutter="4" v-if="customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.showPageArea == 'Y' && regionData != null && regionData[customizedLayout.id] != null && regionData[customizedLayout.id].pageParam != null">
    <el-col :span="24">
        <el-pagination @current-change="reQuery" small background :total="parseInt(regionData[customizedLayout.id].pageParam.total)" :page-size="regionData[customizedLayout.id].pageParam.pageRows" class="mt-4" />
    </el-col>
</el-row>
</div>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import {ElMessage} from 'element-plus'
import popupViewButtonComponent from '../common/popupViewButtonComponent.vue'
import tableColumnComponent from '../common/tableColumnComponent.vue'
import {executeOperationForCustomized} from '../../js/common.js'
import popupViewComponent from '../common/popupViewAttrComponent.vue'

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

    computed: {
        
    },
    components: {
    	popupViewButtonComponent,
        tableColumnComponent,
        popupViewComponent
    },
    
    data() {   
       
    	return {
            
        }
    },
    mounted() {
        console.log(this.customizedLayout);
    	if(this.customizedLayout.layoutRegions == null || this.customizedLayout.layoutRegions.length == 0 || this.customizedLayout.layoutRegions[0].funcRegion == null) {
            ElMessage.error("not found layoutRegion info!");
        }

        this.customizedLayout.buttonComponents=[];
        this.customizedLayout.titleButtonComponents=[];
       
        if(this.customizedLayout.layoutRegions[0].funcRegion.regionOperations != null && this.customizedLayout.layoutRegions[0].funcRegion.regionOperations.length > 0) {
            for(var index in this.customizedLayout.layoutRegions[0].funcRegion.regionOperations) {
                var regionOperation = this.customizedLayout.layoutRegions[0].funcRegion.regionOperations[index];
                var regionOperationJSON = null;
                if(regionOperation.operationCfg != null && regionOperation.operationCfg.trim().length > 0) {
                    regionOperationJSON = JSON.parse(regionOperation.operationCfg);
                }else {
                    regionOperationJSON = {};
                }
                if(regionOperationJSON != null && regionOperationJSON.displayPosition == "2") {//表头
                    this.customizedLayout.titleButtonComponents.push(regionOperation);
                }else {
                    this.customizedLayout.buttonComponents.push(regionOperation);
                }
                regionOperation.operationCfgJSON = regionOperationJSON;
            }
        }

        if(this.regionData[this.customizedLayout.id] == null) {
            this.regionData[this.customizedLayout.id] = {};
        }
        if(this.customizedLayout.layoutCfgJSON != null && this.customizedLayout.layoutCfgJSON.tableDataSourceObject != null && this.customizedLayout.layoutCfgJSON.tableDataSourceAttr != null) {
            if(this.regionData[this.customizedLayout.layoutCfgJSON.tableDataSourceAttr] == null) {
                for(var i in this.funcDefine.objectAssigns) {
                    if(this.funcDefine.objectAssigns[i].id == this.customizedLayout.layoutCfgJSON.tableDataSourceObject) {
                        var assignObject = this.funcDefine.objectAssigns[i];
                        for(var j in assignObject.attributes) {
                            if(assignObject.attributes[j].code == this.customizedLayout.layoutCfgJSON.tableDataSourceAttr) {
                                if(assignObject.attributes[j].isListAttr == 'Y') {
                                    this.regionData[this.customizedLayout.layoutCfgJSON.tableDataSourceAttr] = [];
                                }else {
                                    this.regionData[this.customizedLayout.layoutCfgJSON.tableDataSourceAttr] = {};
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
    },
    methods: {
        executeOperation(operation, rowData, index) {
            this.regionData[this.customizedLayout.id].rowIndex = index;
            executeOperationForCustomized(operation, this.funcDefine, this.customizedPage, rowData, this.hideDialogPage, this.showDialogPage, this.refreshTableData, this.objectUpdated, this.parentPage, this.srcOperation, this.updateTabelRow, this.regionData);
        },
        executeTitleOperation(operation) {
            executeOperationForCustomized(operation, this.funcDefine, this.customizedPage, this.regionData, this.hideDialogPage, this.showDialogPage, this.refreshTableData, this.objectUpdated, this.parentPage, this.srcOperation, null, null);
        },
        updateTabelRow(isDelete, updateObject) {
            if(this.customizedLayout.layoutCfgJSON != null && this.customizedLayout.layoutCfgJSON.tableDataSourceAttr != null && this.customizedLayout.layoutCfgJSON.tableDataSourceAttr.length > 0) {
                if(isDelete) {
                    this.regionData[this.customizedLayout.layoutCfgJSON.tableDataSourceAttr].splice(this.regionData[this.customizedLayout.id].rowIndex,1);
                }else {
                    if(updateObject != null) {
                        for(key in updateObject) {
                            this.regionData[this.customizedLayout.layoutCfgJSON.tableDataSourceAttr][this.regionData[this.customizedLayout.id].rowIndex][key] = updateObject[key];
                        }
                    }
                }
            }else {
                if(this.regionData[this.customizedLayout.id] != null) {//表格选中的数据
                    if(isDelete) {
                        this.regionData[this.customizedLayout.id].data.splice(this.regionData[this.customizedLayout.id].rowIndex,1);
                    }else {
                        if(updateObject != null) {
                            for(key in updateObject) {
                                this.regionData[this.customizedLayout.id].data[this.regionData[this.customizedLayout.id].rowIndex][key] = updateObject[key];
                            }
                        }
                    }
                }
            }
        },
        reQuery(currentPage) {
            if(this.regionData[this.customizedLayout.id].originQueryInfo == null) {
                return;
            }
        	const operation = this.regionData[this.customizedLayout.id].originQueryInfo.operation;
        	const req = this.regionData[this.customizedLayout.id].originQueryInfo.req;
        	var url = "/lcdp-proxy/lowcode/" + this.funcDefine.platformUrl + "/" + this.funcDefine.systemUrl + "/" + operation.exposedServiceMapping;
            if(this.regionData[this.customizedLayout.id].pageParam != null) {
                if(currentPage == null && this.regionData[this.customizedLayout.id].pageParam.pageNo == null) {
                    currentPage = 1;
                }
                if(currentPage != null) {
                    this.regionData[this.customizedLayout.id].pageParam.pageNo = currentPage;
                }
            	req.reqData["pageParam"] = this.regionData[this.customizedLayout.id].pageParam;
            }
            axiosClient.post(url, req).then((response) => {
                var result = response.data;
                if(result != null && result.status == '200') {
                	this.regionData[this.customizedLayout.id].data = result.data;
                }else {
                    ElMessage.error(`请求后端服务失败！状态码：` + result.status + `，错误信息：` + result.message );
                }
            }).catch((error)=>{
                ElMessage.error(`请求后端服务失败！` + error);
            });
        },
        handleCurrentChange(val) {
            if(this.customizedLayout.layoutCfgJSON == null || this.customizedLayout.layoutCfgJSON.selectMode != 'S') {
                return;
            }
            this.regionData[this.customizedLayout.id].selected = val;
        },
        handleSelectionChange(val) {
            if(this.customizedLayout.layoutCfgJSON == null || this.customizedLayout.layoutCfgJSON.selectMode != 'M') {
                return;
            }
            this.regionData[this.customizedLayout.id].selected = val;
        }
    }
}
</script>
<style scoped>
.toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
  height: 100%;
  right: 20px;
}
</style>