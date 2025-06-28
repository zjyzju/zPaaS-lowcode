<template>
<el-scrollbar height="70vh" style="overflow-x: hidden; width: 100%;">
<el-form  v-if="funcRegion != null" :model="funcRegion" label-width="120px" style="width: 98%;">
    <el-row :gutter="4" v-if="this.attrAssignMap != null" v-for="(attrAssigns, key) in this.attrAssignMap">
        <el-col :span="8*attrAssign.colSpan" v-for="(attrAssign, index) in attrAssigns">
            <el-form-item :label="attrAssign.displayName">
                <attrDisplayComponent :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :subObject="null" :disableFlag="true"/>
            </el-form-item>
        </el-col>
    </el-row>
    
    <el-row :gutter="4" v-if="subObjects != null && subObjects.length > 0" v-for="subObject in subObjects">
        <el-col :span="24">
            <el-row :gutter="4">
                <el-col :span="24">
                    &nbsp;
                </el-col>
            </el-row>
        </el-col>
        <el-col :span="24">
            <el-row :gutter="4">
                <el-col :span="24">
                    <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
                    <div class="toolbar1">
                        <span class="title">{{subObject.assignObjectName}}信息</span>&nbsp;&nbsp;
                        <span v-if="funcRegion.regionOperations != null" v-for="operation in funcRegion.regionOperations">
                            <el-link v-if="operation.operationType == 'P' && operation.objectAssignId == subObject.id" type="primary"  size="small" @click="executeSubOperation(operation, null)">{{operation.name}}</el-link>&nbsp;
                        </span>
                    </div>
                    </el-divider>
                </el-col>
            </el-row>
            <el-row :gutter="4">
                <el-col :span="24">
                    &nbsp;
                </el-col>
            </el-row>
            <!-- 一对多，列表形式 -->
            <el-row :gutter="4" v-if="subObject.mainRelAttribute.isListAttr=='Y'">
                <el-col :span="24">
                    <el-table  v-if="subObjectAttrMap != null && subObjectAttrMap[subObject.id] != null" 
                           :data="regionData[subObject.mainRelAttribute.code]" stripe style="width: 100%;" scrollbar-always-on >
                        <el-table-column  label="操作" width="100" v-if="funcRegion.regionOperations != null && funcRegion.regionOperations.length > 0">
                            <template #default="scope">
                                <span v-if="funcRegion.regionOperations != null" v-for="operation in funcRegion.regionOperations">
                                    <template v-if="operation.operationType != 'P' && operation.operationType != 'V' && operation.objectAssignId == subObject.id">
                                        <el-link type="primary"  size="small" @click="executeSubOperation(operation, scope.row)">{{operation.name}}</el-link>&nbsp;
                                    </template>
                                    <template v-if="operation.operationType == 'V' && operation.objectAssignId == subObject.id">
                                        <popupViewComponent :operation="operation" :regionData="scope.row" :funcRegion="funcRegion" :funcDefine="funcDefine" mainPage="Y"/>&nbsp;
                                    </template>
                                </span>
                            </template>
                        </el-table-column> 
                        <el-table-column v-for="(attrAssign, index) in subObjectAttrMap[subObject.id]" 
                                    :prop="attrAssign.attribute.code" :label="attrAssign.displayName" :width="attrAssign.displayWidth">
                            <template #default="scope">
                                <popupViewComponent  v-if="attrAssign.displayType == 'V'" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="scope.row" :attrAssign="attrAssign" :disableFlag="true" :subObject="subObject"/>
		                        <tableColumnComponent :row="scope.row" :attrAssign="attrAssign" :attrCode="attrAssign.attribute.code" :displayType="attrAssign.displayType" :attrOption="funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId]"/>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-col>
                <el-col :span="24">
                    &nbsp;
                </el-col>
            </el-row>  
            
            <!-- 一对一，form形式 -->
            <el-row :gutter="4" v-if="subObject.mainRelAttribute.isListAttr=='N' && subObjectAttrMap != null && subObjectAttrMap[subObject.id] != null" v-for="(attrAssigns, key) in subObjectAttrMap[subObject.id]">
                <el-col :span="8*attrAssign.colSpan" v-for="(attrAssign, index) in attrAssigns">
                    <el-form-item :label="attrAssign.displayName">
                        <attrDisplayComponent :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :subObject="subObject" :disableFlag="true"/>
                    </el-form-item>
                </el-col>
            </el-row>             
        </el-col>
    </el-row>
</el-form>
</el-scrollbar>
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
                <template v-if="operation.objectAssignId == null || operation.objectAssignId == '' || operation.objectAssignId == this.mainObject.id">
                    <el-button type="primary" v-if="operation.operationType != 'N' && operation.operationType != 'V'"  size="small" @click="executeOperation(operation)">{{operation.name}}</el-button>
                    <el-button size="small" v-if="operation.operationType == 'N'" @click="executeOperation(operation)">{{operation.name}}</el-button>
                    <popupViewComponent v-if="operation.operationType == 'V'" :operation="operation" :regionData="regionData" :funcRegion="funcRegion" :funcDefine="funcDefine" mainPage="Y"/>
                    &nbsp;&nbsp;
                </template>
            </span>
        </el-col>
        <el-col :span="1">&nbsp;</el-col>
    </el-row>

<el-dialog v-model="showSubRegion['13']" v-if="subRegionData['13'] != null" :title="subDetailRegion.name" width="900px">
    <template #header>
        <span class="title">{{ subDetailRegion.name }}</span>
    </template>
    <subObjectDetailRegion v-if="subDetailRegion != null" :funcRegion="subDetailRegion" :funcDefine="funcDefine" :regionData="subRegionData['13']" :subObjectAssign="subObjectAssign" @operationExecuted="subOperationExecuted"/>
</el-dialog>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import subObjectDetailRegion from './region-moreDetailRegion-subObjectDetailRegion.vue'
import attrDisplayComponent from '../common/attrDisplayComponent.vue'
import {formatAttrDisplayInfo, processLabelValue, generateReqObject, validateReqData} from '../../js/common.js'
import popupViewComponent from '../common/popupViewButtonComponent.vue'
import tableColumnComponent from '../common/tableColumnComponent.vue'

export default {
    props: ['funcRegion','funcDefine','regionData'],
    
    emits: ['operationExecuted'],
    
    setup(props, {attrs, emit, slots}) {
    	const operationExecuted = (operation, data, srcData) => {
            emit('operationExecuted', operation, data, srcData);
        };
        return {
        	operationExecuted
        }
    }, 
	
    components: {
    	subObjectDetailRegion,
    	attrDisplayComponent,
    	popupViewComponent,
        tableColumnComponent
    },
    data() {      
        const mainObject = ref({});
        const subObjects = ref([]);
        const attrAssignMap = ref(null);
        const subObjectAttrMap = ref({});
        
        const subRegionData = ref({});
        const showSubRegion = ref({});
        
        const subDetailRegion = ref(null);
        const subObjectAssign = ref(null);
        
        return {
        	 mainObject,//主对象
             subObjects,//从对象列表
             attrAssignMap,//存放主对象属性的格式化展示数据，row-》FuncRegionAttrAssign
             subObjectAttrMap,//从对象属性的格式化展示数据，一对多的情况assignId-》attrAssignList；一对一的情况assignId-》Map（row-》FuncRegionAttrAssign）
             
             subRegionData,
             showSubRegion,
             
             subDetailRegion,
             subObjectAssign
        }
    },
    
    mounted() {
    	var subAttrNeedFormat = false; //是否存在一对一的从对象，需要进行格式化展示
    	if(this.funcDefine != null && this.funcDefine.objectAssigns != null) {
            for(var index in this.funcDefine.objectAssigns) {
                if(this.funcDefine.objectAssigns[index].assignType == "M") {
                    this.mainObject = this.funcDefine.objectAssigns[index];
                }else {
                	this.subObjects.push(this.funcDefine.objectAssigns[index]);
                    if(this.funcDefine.objectAssigns[index].mainRelAttribute.isListAttr == 'N') {
                        if(this.regionData[this.funcDefine.objectAssigns[index].mainRelAttribute.code] == null) {
                            this.regionData[this.funcDefine.objectAssigns[index].mainRelAttribute.code] = {};
                        }
                        subAttrNeedFormat = true;
                    }else {
                        if(this.regionData[this.funcDefine.objectAssigns[index].mainRelAttribute.code] == null) {
                            this.regionData[this.funcDefine.objectAssigns[index].mainRelAttribute.code] = [];
                        }
                    }
                }
            }
            this.subDetailRegion = this.funcDefine.regionMap["13"];//从对象明细区域
        }
        
        
        var mainAttrAssigns = [];
        
        if(this.funcRegion != null && this.funcRegion.regionAttrAssigns != null) {
            for(var index =0; index< this.funcRegion.regionAttrAssigns.length; index++) {
                //非主对象的属性按从对象分表格展示
                if(this.funcRegion.regionAttrAssigns[index].objectAssignId != this.mainObject.id) {
                    var attrAssignList = this.subObjectAttrMap[this.funcRegion.regionAttrAssigns[index].objectAssignId];
                    if(attrAssignList == null) {
                        attrAssignList = [];
                        this.subObjectAttrMap[this.funcRegion.regionAttrAssigns[index].objectAssignId] = attrAssignList;
                    }
                    attrAssignList.push(this.funcRegion.regionAttrAssigns[index]);
                }else {
                    mainAttrAssigns.push(this.funcRegion.regionAttrAssigns[index]);
                }
            }
            //格式化展示主对象属性
            if(mainAttrAssigns != null && mainAttrAssigns.length > 0) {
                this.attrAssignMap = formatAttrDisplayInfo(mainAttrAssigns);
            }
            //存在一对一的从对象，需要进行格式化展示
            if(subAttrNeedFormat) {
                if(this.funcDefine != null && this.funcDefine.objectMap != null) {
                    for(var key in this.funcDefine.objectMap) {
                        if(this.funcDefine.objectMap[key].assignType == "S" && this.funcDefine.objectMap[key].mainRelAttribute.isListAttr == 'N') {
                            var subAttrAssigns = this.subObjectAttrMap[this.funcDefine.objectMap[key].id];
                            if(subAttrAssigns == null) {
                                continue;
                            }
                            var subAttrAssignMap = formatAttrDisplayInfo(subAttrAssigns);
                            this.subObjectAttrMap[this.funcDefine.objectMap[key].id] = subAttrAssignMap;
                        }
                    }
                }
            }
        }
    },
    
    methods: {
    	executeOperation(operation) {
            console.log(operation, this.regionData);
            if(operation.operationType == 'N') {//取消操作
                this.operationExecuted(operation, null);
            }else {
            	ElMessageBox.confirm('是否继续?','警告',
                        {
                          confirmButtonText: '确定',
                          cancelButtonText: '取消',
                          type: 'warning',
                        }
                ).then(() => {
	            	var url = "/lcdp-proxy/lowcode/" + this.funcDefine.platformUrl + "/" + this.funcDefine.systemUrl + "/" + operation.exposedServiceMapping;
	                console.log("url:" + url);
	                var req = {};
	                if(operation.operation.inParams != null && operation.operation.inParams.length > 0) {
	                    req = generateReqObject(operation.operation.inParams, this.regionData, operation, this.funcRegion, this.funcDefine, false);
	                }
	                console.log("req:", req);
	              //参数校验
                    if(operation.operation.validateRuleGroup != null && operation.operation.validateRuleGroup.validateRules != null) {
                        if(!validateReqData(operation.operation.validateRuleGroup, req.reqData)) {
                            return;
                        }
                    }
	                axiosClient.post(url, req).then((response) => {
	                    var data = response.data;
	                    if(data != null && data.status == '200') {
	                        ElMessage(`操作成功！`);
	                        this.operationExecuted(operation, data.data, null, null);
	                    }else {
	                        ElMessage.error(`请求后端服务失败！状态码：` + data.status + `，错误信息：` + data.message );
	                    }
	                }).catch(()=>{
	                    ElMessage.error(`请求后端服务失败！`);
	                });
                });
            }
        },
        executeSubOperation(operation, data) {
            this.subObjectAssign = null;
            for(var i in this.funcDefine.objectAssigns) {
                if(this.funcDefine.objectAssigns[i].id == operation.objectAssignId) {
                    this.subObjectAssign = this.funcDefine.objectAssigns[i];
                    break;
                }
            }
            
            if(operation.operationType == 'B') {//从对象的修改操作（根据主键加载信息）
                var url = "/lcdp-proxy/lowcode/" + this.funcDefine.platformUrl + "/" + this.funcDefine.systemUrl + "/" + operation.exposedServiceMapping;
                var req = {};
                if(operation.operation.inParams != null && operation.operation.inParams.length > 0) {
                	req = generateReqObject(operation.operation.inParams, data, operation, this.funcRegion, this.funcDefine, false);
                }
                axiosClient.post(url, req).then((response) => {
                    var data = response.data;
                    if(data != null && data.status == '200') {
                    	var targetRegion = this.funcDefine.targetRegionMap[operation.targetRegionId];
                        this.subRegionData[targetRegion.tplRegionId] = data.data;
                        var targetRegionData = this.subRegionData[targetRegion.tplRegionId];
                        //处理label/value的翻译
                        processLabelValue(targetRegion, targetRegionData, this.subObjectAssign, false, this.funcDefine);
                        this.showSubRegion[this.funcDefine.targetRegionMap[operation.targetRegionId].tplRegionId] = true;
                    }else {
                        ElMessage.error(`请求后端服务失败！状态码：` + data.status + `，错误信息：` + data.message );
                    }
                }).catch((ex)=>{
                    ElMessage.error(`请求后端服务失败！` + ex);
                });
                
            }
        },
        subOperationExecuted(srcOperation, data, objectAssign) {
            this.showSubRegion = {};
            this.subRegionData = {};
            this.subObjectAssign = null;
        }
    }    
}
</script>
<style scoped>
    /* 输入框或下拉选框禁用时：加粗显示提示语 */
    :deep(.el-input.is-disabled .el-input__inner){
        color: black ;
    }

.toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
  height: 100%;
  right: 20px;
}
</style>