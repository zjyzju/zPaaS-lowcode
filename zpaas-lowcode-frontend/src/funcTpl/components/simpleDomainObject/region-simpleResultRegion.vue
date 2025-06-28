<template>
<div>
<br/>
<el-row :gutter="4">
    <el-col :span="24">
        <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
        <div class="toolbar1">
            <span class="title">查询列表</span>&nbsp;&nbsp;
            <span v-if="funcRegion.regionOperations != null" v-for="operation in funcRegion.regionOperations">
                <el-link v-if="operation.operationType == 'P' || operation.operationType == 'E'" type="primary"  size="small" @click="executeOperation(operation, null)">{{operation.name}}</el-link>&nbsp;
            </span>
        </div>
        </el-divider>
    </el-col>
</el-row>
<el-row :gutter="4">
    <el-col :span="24">
		<el-table  v-if="funcRegion.regionAttrAssigns != null && regionData != null" :data="regionData.data" stripe style="width: 100%">
		    <el-table-column  label="操作" width="180" v-if="funcRegion.regionOperations != null && funcRegion.regionOperations.length > 0">
		        <template #default="scope">
		            <span v-if="funcRegion.regionOperations != null" v-for="operation in funcRegion.regionOperations">
                        <template v-if="operation.operationType != 'P' && operation.operationType != 'E' && operation.operationType != 'V'">
                            <el-link type="primary"  size="small" @click="executeOperation(operation, scope.row)">{{operation.name}}</el-link>&nbsp;
                        </template>
                        <template v-if="operation.operationType == 'V'">
                            <popupViewComponent :operation="operation" :regionData="scope.row" :funcRegion="funcRegion" :funcDefine="funcDefine" mainPage="Y"/>&nbsp;
                        </template>
                    </span>
		        </template>
		    </el-table-column>
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
</div>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import {generateReqObject, validateReqData} from '../../js/common.js'
import popupViewComponent from '../common/popupViewButtonComponent.vue'
import tableColumnComponent from '../common/tableColumnComponent.vue'

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
        tableColumnComponent
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
        
        
        return {
            mainObject,
            subObjects
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
    	executeOperation(operation, data) {
            if(operation.operationType == "B") {//根据主键查询
            	var url = "/lcdp-proxy/lowcode/" + this.funcDefine.platformUrl + "/" + this.funcDefine.systemUrl + "/" + operation.exposedServiceMapping;
                var req = {};
                if(operation.operation.inParams != null && operation.operation.inParams.length > 0) {
                    req = generateReqObject(operation.operation.inParams, data, operation, this.funcRegion, this.funcDefine, false);
                }
              //参数校验
                if(operation.operation.validateRuleGroup != null && operation.operation.validateRuleGroup.validateRules != null) {
                    if(!validateReqData(operation.operation.validateRuleGroup, req.reqData)) {
                        return;
                    }
                }
                axiosClient.post(url, req).then((response) => {
                    var data = response.data;
                    if(data != null && data.status == '200') {
                        this.operationExecuted(operation, data.data, null);
                    }else {
                        ElMessage.error(`请求后端服务失败！状态码：` + data.status + `，错误信息：` + data.message );
                    }
                }).catch(()=>{
                    ElMessage.error(`请求后端服务失败！`);
                });
            }else if(operation.operationType == "D") {//删除
                var url = "/lcdp-proxy/lowcode/" + this.funcDefine.platformUrl + "/" + this.funcDefine.systemUrl + "/" + operation.exposedServiceMapping;
                var req = {};
                if(operation.operation.inParams != null && operation.operation.inParams.length > 0) {
                	req = generateReqObject(operation.operation.inParams, data, operation, this.funcRegion, this.funcDefine, false);
                }
              //参数校验
                if(operation.operation.validateRuleGroup != null && operation.operation.validateRuleGroup.validateRules != null) {
                    if(!validateReqData(operation.operation.validateRuleGroup, req.reqData)) {
                        return;
                    }
                }
                ElMessageBox.confirm(
                        '该对象(' + data + ')将被删除. 是否继续?',
                        '警告',
                        {
                          confirmButtonText: '确定',
                          cancelButtonText: '取消',
                          type: 'warning',
                        }
                      ).then(() => {
                          axiosClient.post(url,req).then((response) => {
                        	  var result = response.data; 
                              if(result.data > 0 && result.status == '200') {
                                  ElMessage(`删除对象(`+data+`)成功。`);
                                  if(this.regionData.pageParam != null) {
                                    this.reQuery(this.regionData.pageParam.pageNo);
                                  }else {
                                    this.reQuery();
                                  }
                                  
                              }else {
                            	  ElMessage.error(`请求后端服务失败！状态码：` + result.status + `，错误信息：` + result.message );
                              }
                          }).catch((error)=>{
                        	  ElMessage.error(`请求后端服务失败！状态码：` + error.status + `，错误信息：` + error.message );
                          });
                      }).catch(()=>{
                      });
                
                
            }else if(operation.operationType == "P") {//预新增
                this.operationExecuted(operation, {}, null);
            }else if(operation.operationType == "E") {
                if(this.srcOperation == null || this.srcOperation.srcData == null) {
                    ElMessage(`请先执行查询操作！`);
                    return;
                }
                var data = this.srcOperation.srcData;
                var url = "/lcdp-proxy/lowcode/" + this.funcDefine.platformFileDownloadUrl + "/" + this.funcDefine.systemUrl + "/" + operation.exposedServiceMapping;
                var req = {};
                if(operation.operation.inParams != null && operation.operation.inParams.length > 0) {
                    req = generateReqObject(operation.operation.inParams, data, this.srcOperation.srcOperation, this.srcOperation.srcRegion, this.funcDefine, false);
                }
              //参数校验
                if(operation.operation.validateRuleGroup != null && operation.operation.validateRuleGroup.validateRules != null) {
                    if(!validateReqData(operation.operation.validateRuleGroup, req.reqData)) {
                        return;
                    }
                }
                axiosClient.post(url, req, {"responseType":"blob"}).then((response) => {
                    if(response != null && response.status == "200") {
                        const blob = new Blob([response.data], { "type": response.headers["content-type"]});
                        const disposition = decodeURI(response.headers['content-disposition'])
                        // 从响应头中获取文件名称
                        const fileName = disposition.substring(disposition.indexOf('filename=') + 10, disposition.length-1)
                        const a = document.createElement("a"); 
                        const url = window.URL.createObjectURL(blob); // 创建媒体流 url
                        a.download = fileName;
                        a.href = url;
                        a.style.display = "none";
                        document.body.appendChild(a);
                        a.click();
                        a.parentNode.removeChild(a);
                        window.URL.revokeObjectURL(url); // 删除创建的媒体流 url 对象
                    }else {
                        ElMessage.error(`请求后端服务失败！status:` + response.status);
                    }
                    
                }).catch((err)=>{
                    ElMessage.error(`请求后端服务失败！` + err);
                });
            }else {
                var url = "/lcdp-proxy/lowcode/" + this.funcDefine.platformUrl + "/" + this.funcDefine.systemUrl + "/" + operation.exposedServiceMapping;
                var req = {};
                if(operation.operation.inParams != null && operation.operation.inParams.length > 0) {
                    req = generateReqObject(operation.operation.inParams, data, operation, this.funcRegion, this.funcDefine, false);
                }
              //参数校验
                if(operation.operation.validateRuleGroup != null && operation.operation.validateRuleGroup.validateRules != null) {
                    if(!validateReqData(operation.operation.validateRuleGroup, req.reqData)) {
                        return;
                    }
                }
                axiosClient.post(url, req).then((response) => {
                    var data = response.data;
                    if(data != null && data.status == '200') {
                        this.operationExecuted(operation, data.data, null);
                    }else {
                        ElMessage.error(`请求后端服务失败！状态码：` + data.status + `，错误信息：` + data.message );
                    }
                }).catch(()=>{
                    ElMessage.error(`请求后端服务失败！`);
                });
            }
        },
        reQuery(currentPage) {
        	if(currentPage == null) {
        		currentPage = 1;
        	}
        	const operation = this.srcOperation.srcOperation;
        	const data = this.srcOperation.srcData;
        	const srcRegion = this.srcOperation.srcRegion;
            var url = "/lcdp-proxy/lowcode/" + this.funcDefine.platformUrl + "/" + this.funcDefine.systemUrl + "/" + operation.exposedServiceMapping;
            var req = {};
            if(operation.operation.inParams != null && operation.operation.inParams.length > 0) {
            	req = generateReqObject(operation.operation.inParams, data, operation, srcRegion, this.funcDefine, true);
            }else {
            	req.reqData = {};
            }
            if(this.regionData.pageParam != null  && (this.funcRegion.regionCfg == null || JSON.parse(this.funcRegion.regionCfg).pageSupport != false)) {
            	this.regionData.pageParam.pageNo = currentPage;
                req.reqData["pageParam"] = this.regionData.pageParam;
            }
            
            axiosClient.post(url, req).then((response) => {
                var result = response.data;
                if(result != null && result.status == '200') {
                	this.operationExecuted(operation, result, data, srcRegion);
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