<template>
<div>
<br/>
	<el-row :gutter="4">
	    <el-col :span="24">
	        <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
	        <div class="toolbar1">
	            <span class="title">查询列表</span>&nbsp;&nbsp;
	        </div>
	        </el-divider>
	    </el-col>
	</el-row>
	<el-row :gutter="4">
	    <el-col :span="24">
	        <el-table  v-if="funcRegion.regionAttrAssigns != null && regionData != null" :data="regionData.data" stripe style="width: 100%" @current-change="handleCurrentChange"  highlight-current-row>
	            <el-table-column v-if="funcRegion.regionAttrAssigns != null" v-for="(attrAssign, index) in funcRegion.regionAttrAssigns" 
	                        :prop="attrAssign.attribute.code" :label="attrAssign.displayName" :width="attrAssign.displayWidth" :sortable="attrAssign.displayCfgJSON != null && attrAssign.displayCfgJSON.sortable">
                    <template #default="scope">
                        <popupViewComponent  v-if="attrAssign.displayType == 'V'" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="scope.row" :attrAssign="attrAssign" :disableFlag="true" :subObject="null"/>
		                <tableColumnComponent v-else :row="scope.row" :attrAssign="attrAssign" :attrCode="attrAssign.attribute.code" :displayType="attrAssign.displayType" :attrOption="funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId]"/>
                    </template>
	            </el-table-column>
	        </el-table>
	    </el-col>
	</el-row>
	<el-row :gutter="4">
	    <el-col :span="24">
	        <el-pagination v-if="regionData != null && regionData.pageParam != null && (funcRegion.regionCfg == null || funcRegion.regionCfg.trim().length == 0 || JSON.parse(funcRegion.regionCfg).pageSupport != false)" @current-change="reQuery" small background :total="parseInt(regionData.pageParam.total)" :page-size="regionData.pageParam.pageRows" class="mt-4" />
	    </el-col>
	</el-row>
	<el-row :gutter="4">
	    <el-col :span="24">
	        &nbsp;
	    </el-col>
	</el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            &nbsp;
        </el-col>
        <el-col :span="15" style="justify-content: right; display: inline-flex;">
            <span v-if="funcRegion.regionOperations != null" v-for="operation in funcRegion.regionOperations">
                <el-button type="primary" v-if="operation.operationType != 'N' && operation.operationType != 'V' && operation.operationType != 'R'"  size="small" @click="executeOperation(operation)">{{operation.name}}</el-button>
                <el-button size="small" v-if="operation.operationType == 'N' || operation.operationType == 'R'" @click="executeOperation(operation)">{{operation.name}}</el-button>
                <popupViewComponent v-if="operation.operationType == 'V'" :operation="operation" :regionData="regionData" :funcRegion="funcRegion" :funcDefine="funcDefine" mainPage="Y"/>
                &nbsp;&nbsp;
            </span>
        </el-col>
        <el-col :span="1">&nbsp;</el-col>
    </el-row>
</div>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'
import {generateReqObject} from '../../js/common.js'
import tableColumnComponent from '../common/tableColumnComponent.vue'
import popupViewComponent from '../common/popupViewButtonComponent.vue'

export default {
    props: ['funcRegion','funcDefine', 'regionData', 'srcOperation', 'refresh'],
    
    emits: ['operationExecuted'],
    
    setup(props, {attrs, emit, slots}) {
        const operationExecuted = (operation, data, srcData, srcRegion) => {
            emit('operationExecuted', operation, data, srcData, srcRegion);
        };
        return {
            operationExecuted
        }
    },  
    
    components: {
    	popupViewComponent,
        tableColumnComponent,
        popupViewComponent
    },
    
    watch: {
        'refresh': function(val, old){
            if((val == null || old == null) || val == old || this.srcOperation == null) {
                return;
            }
            if(this.refresh > 0) {
                if(this.regionData.pageParam == null ) {
                    this.reQuery(1);
                }else {
                    this.reQuery(this.regionData.pageParam.pageNo);
                }
            }
        }
    },
    
    data() {      
        const mainObject = ref({});
        const subObjects = ref([]);
        const currentRow = ref();
        
        return {
            mainObject,
            subObjects,
            currentRow
        }
    },
    mounted() {
        
        if(this.funcDefine != null && this.funcDefine.objectMap != null) {
            for(var key in this.funcDefine.objectMap) {
                if(this.funcDefine.objectMap[key].assignType == "M") {
                    this.mainObject = this.funcDefine.objectMap[key];
                }else {
                    this.subObjects.push(this.funcDefine.objectMap[key]);
                }
            }
        }
        
        
    },
    
    methods: {
    	handleCurrentChange(val) {
            this.currentRow = val;
        },
        executeOperation(operation, data) {
            if(operation.operationType == "S") {
            	if(this.currentRow == null) {
                    ElMessage.error(`请先选择一条记录！`);
                    return;
                }
            	this.operationExecuted(operation, this.currentRow, null, this.funcRegion);
            }else {
                this.operationExecuted(operation, null, null, this.funcRegion);
            }
            
        },
        reQuery(currentPage) {
            if(currentPage == null) {
                currentPage = 1;
            }
            var operation = this.srcOperation.srcOperation;
            var data = this.srcOperation.srcData;
            var url = "/lcdp-proxy/lowcode/" + this.funcDefine.platformUrl + "/" + this.funcDefine.systemUrl + "/" + operation.exposedServiceMapping;
            var req = {};
            if(operation.operation.inParams != null && operation.operation.inParams.length > 0) {
            	req = generateReqObject(operation.operation.inParams, data, operation, this.funcRegion, this.funcDefine, false);
            }else {
                req.reqData = {};
            }
            if(this.regionData.pageParam != null && (this.funcRegion.regionCfg == null || JSON.parse(this.funcRegion.regionCfg).pageSupport != false)) {
                this.regionData.pageParam.pageNo = currentPage;
                req.reqData["pageParam"] = this.regionData.pageParam;
            }
            
            axiosClient.post(url, req).then((response) => {
                var result = response.data;
                console.log(result);
                if(result != null && result.status == '200') {
                    this.operationExecuted(operation, result, data);
                }else {
                    ElMessage.error(`请求后端服务失败！状态码：` + result.status + `，错误信息：` + result.message );
                }
            }).catch((error)=>{
                ElMessage.error(`请求后端服务失败！` + error);
            });
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