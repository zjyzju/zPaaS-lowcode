<template>
<el-scrollbar height="70vh" style="overflow-x: hidden; width: 100%;">
<el-form  v-if="funcRegion != null" :model="regionData" label-width="120px" style="width: 98%;" :rules="validateRules" ref="funcRegionForm">
    <el-row :gutter="4" v-if="this.attrAssignMap != null" v-for="(attrAssigns, key) in this.attrAssignMap">
        <el-col :span="8*attrAssign.colSpan" v-for="(attrAssign, index) in attrAssigns">
            <el-form-item :label="attrAssign.displayName" :required="attrAssign.required == 'Y'" :prop="attrAssign.attribute.code">
                <attrDisplayComponent :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :subObject="null"/>
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
			                <el-link v-if="operation.operationType == 'P' && operation.objectAssignId == subObject.id" type="primary"  size="small" @click="executeSubOperation(operation, regionData)">{{operation.name}}</el-link>&nbsp;
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
					       :data="regionData[subObject.mainRelAttribute.code]" stripe style="width: 100%;"  scrollbar-always-on>
					    <el-table-column  label="操作" width="150" v-if="funcRegion.regionOperations != null && funcRegion.regionOperations.length > 0">
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
		            <el-form-item :label="attrAssign.displayName" :prop="subObject.mainRelAttribute.code + '.' + attrAssign.attribute.code">
		                <attrDisplayComponent :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :subObject="subObject"/>
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

<el-dialog v-model="showSubRegion['10']" v-if="subRegionData['10'] != null" :title="subCreateRegion.name" width="900px">
    <template #header>
        <span class="title">{{ subCreateRegion.name }}</span>
    </template>
    <subObjectCreateRegion v-if="subCreateRegion != null" :funcRegion="subCreateRegion" :funcDefine="funcDefine" :regionData="subRegionData['10']" :subObjectAssign="subObjectAssign" @operationExecuted="subOperationExecuted"/>
</el-dialog>
<el-dialog v-model="showSubRegion['11']" v-if="subRegionData['11'] != null" :title="subEditRegion.name" width="900px">
    <template #header>
        <span class="title">{{ subEditRegion.name }}</span>
    </template>
    <subObjectEditRegion v-if="subEditRegion != null" :funcRegion="subEditRegion" :funcDefine="funcDefine" :regionData="subRegionData['11']" :subObjectAssign="subObjectAssign" @operationExecuted="subOperationExecuted"/>
</el-dialog>
<el-dialog v-model="showSubRegion['12']" v-if="subRegionData['12'] != null" :title="subDetailRegion.name" width="900px">
    <template #header>
        <span class="title">{{ subDetailRegion.name }}</span>
    </template>
    <subObjectDetailRegion v-if="subDetailRegion != null" :funcRegion="subDetailRegion" :funcDefine="funcDefine" :regionData="subRegionData['12']" :subObjectAssign="subObjectAssign" @operationExecuted="subOperationExecuted"/>
</el-dialog>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import {interactionProcess, formatAttrDisplayInfo, processLabelValue, generateReqObject, onExecuteOperation, processPreValidateRules, processParamCfg} from '../../js/common.js'
import subObjectCreateRegion from './region-moreEditRegion-subObjectCreateRegion.vue'
import subObjectEditRegion from './region-moreEditRegion-subObjectEditRegion.vue'
import subObjectDetailRegion from './region-moreEditRegion-subObjectDetailRegion.vue'
import attrDisplayComponent from '../common/attrDisplayComponent.vue'
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
    	subObjectCreateRegion,
    	subObjectEditRegion,
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
        
        const subCreateRegion = ref(null);
        const subEditRegion = ref(null);
        const subDetailRegion = ref(null);
        const subObjectAssign = ref(null);
        
        const interactionFlag = ref(false);

        const validateRules = ref(null);
        
        return {
            mainObject,//主对象
            subObjects,//从对象列表
            attrAssignMap,//存放主对象属性的格式化展示数据，row-》FuncRegionAttrAssign
            subObjectAttrMap,//从对象属性的格式化展示数据，一对多的情况assignId-》attrAssignList；一对一的情况assignId-》Map（row-》FuncRegionAttrAssign）
            
            subRegionData,
            showSubRegion,
            
            subCreateRegion,
            subEditRegion,
            subDetailRegion,
            subObjectAssign,
            
            interactionFlag,
            validateRules
        }
    },
    computed: {
        regionData1: function() {
            return JSON.parse(JSON.stringify(this.regionData));
        }
    },
    watch: {        
        'regionData1': {
            handler: function(newRegionData, oldRegionData){
            	if(!this.interactionFlag) {
                    return;//当前不存在联动控制时，不进行联动处理
                }
                interactionProcess(newRegionData, oldRegionData, this.funcDefine, this.funcRegion, null, this.regionData);//联动处理
            },
            deep:true // 深度监听的参数
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
            this.subCreateRegion = this.funcDefine.regionMap["10"];//从对象新增区域
            this.subEditRegion = this.funcDefine.regionMap["11"];//从对象修改区域
            this.subDetailRegion = this.funcDefine.regionMap["12"];//从对象明细区域
        }
        
        var mainAttrAssigns = [];
        
        if(this.funcRegion != null && this.funcRegion.regionAttrAssigns != null) {
        	var currentAttrAssign = null;
            for(var index =0; index< this.funcRegion.regionAttrAssigns.length; index++) {
            	currentAttrAssign = this.funcRegion.regionAttrAssigns[index];
            	//非主对象的属性按从对象分表格展示
            	if(currentAttrAssign.objectAssignId != this.mainObject.id) {
            		var attrAssignList = this.subObjectAttrMap[currentAttrAssign.objectAssignId];
            		if(attrAssignList == null) {
            			attrAssignList = [];
            			this.subObjectAttrMap[currentAttrAssign.objectAssignId] = attrAssignList;
            		}
            		attrAssignList.push(currentAttrAssign);
            	}else {
            		mainAttrAssigns.push(currentAttrAssign);
            	}
            	
            	//进行联动控制的判断
            	var currentAttrObject = this.funcDefine.objectMap[currentAttrAssign.objectId];
            	if(currentAttrObject.attrOptionMap == null) {
            		continue;
            	}
                var attrOption = currentAttrObject.attrOptionMap[currentAttrAssign.attributeId];//当前绑定属性的配置信息
                if(attrOption == null) {
                    continue;
                }
                if((attrOption.interactionType != null && attrOption.interactionType != "N") || attrOption.parentAttributeId != null) {
                    this.interactionFlag = true;//当前区域存在联动控制时，设置为true
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

        //处理预校验规则
        if(this.funcRegion != null && this.funcRegion.regionOperations != null) {
            this.validateRules = processPreValidateRules(this.funcRegion.regionOperations, this.mainObject);
        }
    },
    
    methods: {
    	executeOperation(operation) {
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
	    			onExecuteOperation(operation, this.regionData, this.funcRegion, this.funcDefine, false, this.operationExecuted, null, this.$refs.funcRegionForm);
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
    		
    		if(operation.operationType == 'P') {//从对象的预新增操作
                if(operation.exposedServiceMapping != null && operation.exposedServiceMapping.trim().length > 0) {
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

                            //处理页面间的参数传递
                            var paramsJSON = {};
                            if(operation.operationCfg != null && operation.operationCfg.length > 0) {
                                var operationCfgJSON = JSON.parse(operation.operationCfg);
                                if(operationCfgJSON.params != null) {
                                    paramsJSON = processParamCfg(operationCfgJSON.params, this.regionData);
                                }
                                if(this.subRegionData[targetRegion.tplRegionId] == null) {
                                    this.subRegionData[targetRegion.tplRegionId] = {};
                                }
                                for(var key in paramsJSON) {
                                    if(this.subRegionData[targetRegion.tplRegionId][key] == undefined) {
                                        this.subRegionData[targetRegion.tplRegionId][key] = paramsJSON[key];
                                    }
                                }
                            }

                            var targetRegionData = this.subRegionData[targetRegion.tplRegionId];
                            //处理label/value的翻译
                            processLabelValue(targetRegion, targetRegionData, this.subObjectAssign, false, this.funcDefine);
                            this.showSubRegion[targetRegion.tplRegionId] = true;
                        }else {
                            ElMessage.error(`请求后端服务失败！状态码：` + data.status + `，错误信息：` + data.message );
                        }
                    }).catch((error)=>{
                        ElMessage.error(`请求后端服务失败！` + error);
                    });
                }else {
                    var newSubObjectInfo = {};
                    var tplRegionId = this.funcDefine.targetRegionMap[operation.targetRegionId].tplRegionId;
                    newSubObjectInfo[this.subObjectAssign.relAttribute.code] = this.regionData[this.mainObject.keyAttribute.code];    
                    this.subRegionData[tplRegionId] = newSubObjectInfo;
                    //处理页面间的参数传递
                    var paramsJSON = {};
                    if(operation.operationCfg != null && operation.operationCfg.length > 0) {
                        var operationCfgJSON = JSON.parse(operation.operationCfg);
                        if(operationCfgJSON.params != null) {
                            paramsJSON = processParamCfg(operationCfgJSON.params, this.regionData);
                        }
                        if(this.subRegionData[tplRegionId] == null) {
                            this.subRegionData[tplRegionId] = {};
                        }
                        for(var key in paramsJSON) {
                            if(this.subRegionData[tplRegionId][key] == undefined) {
                                this.subRegionData[tplRegionId][key] = paramsJSON[key];
                            }
                        }
                    }
                    this.showSubRegion[tplRegionId] = true;
                }
            }else if(operation.operationType == 'B') {//从对象的修改操作（根据主键加载信息）
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

                        //处理页面间的参数传递
                        var paramsJSON = {};
                        if(operation.operationCfg != null && operation.operationCfg.length > 0) {
                            var operationCfgJSON = JSON.parse(operation.operationCfg);
                            if(operationCfgJSON.params != null) {
                                paramsJSON = processParamCfg(operationCfgJSON.params, this.regionData);
                            }
                            if(this.subRegionData[targetRegion.tplRegionId] == null) {
                                this.subRegionData[targetRegion.tplRegionId] = {};
                            }
                            for(var key in paramsJSON) {
                                if(this.subRegionData[targetRegion.tplRegionId][key] == undefined) {
                                    this.subRegionData[targetRegion.tplRegionId][key] = paramsJSON[key];
                                }
                            }
                        }
                        

                        var targetRegionData = this.subRegionData[targetRegion.tplRegionId];
                        //处理label/value的翻译
                        processLabelValue(targetRegion, targetRegionData, this.subObjectAssign, false, this.funcDefine);
                        
                    	this.showSubRegion[targetRegion.tplRegionId] = true;
                        
                    }else {
                        ElMessage.error(`请求后端服务失败！状态码：` + data.status + `，错误信息：` + data.message );
                    }
                }).catch((error)=>{
                    ElMessage.error(`请求后端服务失败！` + error);
                });
            	
            }else if(operation.operationType == 'D') {//从对象的删除操作
            	var url = "/lcdp-proxy/lowcode/" + this.funcDefine.platformUrl + "/" + this.funcDefine.systemUrl + "/" + operation.exposedServiceMapping;
                var req = {};
                var param = {};
                
                if(operation.operation.inParams != null && operation.operation.inParams.length > 0) {
                	req = generateReqObject(operation.operation.inParams, data, operation, this.funcRegion, this.funcDefine, false);
                }
                
                ElMessageBox.confirm(
                        '该对象将被删除. 是否继续?',
                        '警告',
                        {
                          confirmButtonText: '确定',
                          cancelButtonText: '取消',
                          type: 'warning',
                        }
                      ).then(() => {
                          axiosClient.post(url,req).then((response) => {
                              var result = response.data; 
                              console.log(result);
                              if(result.status == '200' && result.data != null) {
                                  if(!isNaN(parseInt(result.data)) && isFinite(result.data))  {
                                      if(result.data > 0) {
                                        if(this.regionData[this.subObjectAssign.mainRelAttribute.code] != null) {
                                            for(var i in this.regionData[this.subObjectAssign.mainRelAttribute.code]) {
                                            
                                                if(this.regionData[this.subObjectAssign.mainRelAttribute.code][i][this.subObjectAssign.keyAttribute.code] == data[this.subObjectAssign.keyAttribute.code]) {
                                                    this.regionData[this.subObjectAssign.mainRelAttribute.code].splice(i,1);
                                                    ElMessage(`删除对象成功。`);
                                                    break;
                                                }
                                            }
                                        }
                                      }
                                  }else {
                                        if(this.regionData[this.subObjectAssign.mainRelAttribute.code] != null) {
                                            for(var i in this.regionData[this.subObjectAssign.mainRelAttribute.code]) {
                                                if(this.regionData[this.subObjectAssign.mainRelAttribute.code][i][this.subObjectAssign.keyAttribute.code] == result.data[this.subObjectAssign.keyAttribute.code]) {
                                                    this.regionData[this.subObjectAssign.mainRelAttribute.code][i] = result.data;
                                                    ElMessage(`删除对象成功。`);
                                                    break;
                                                }
                                            }
                                        }
                                  }
                                  
                              }else {
                                  ElMessage.error(`请求后端服务失败！状态码：` + result.status + `，错误信息：` + result.message );
                              }
                          }).catch((error)=>{
                              ElMessage.error(`请求后端服务失败！状态码：` + error.status + `，错误信息：` + error.message );
                          });
                      }).catch(()=>{
                      });
                
            	this.showSubRegion = {};
                this.subRegionData = {};
            }
    	},
    	subOperationExecuted(srcOperation, data, objectAssign) {
            if(srcOperation.operationType == 'C') {
            	//处理label/value的翻译
            	if(this.regionData[objectAssign.mainRelAttribute.code] == null) {
                    this.regionData[objectAssign.mainRelAttribute.code] = [];
                }
            	processLabelValue(this.funcRegion, data, this.subObjectAssign, false, this.funcDefine);
            	setTimeout(() => this.regionData[objectAssign.mainRelAttribute.code].push(data), 400);
            } if(srcOperation.operationType == 'U') {
            	if(this.regionData[objectAssign.mainRelAttribute.code] != null) {
                    for(var i in this.regionData[objectAssign.mainRelAttribute.code]) {
                    	if(this.regionData[objectAssign.mainRelAttribute.code][i][objectAssign.keyAttribute.code] == data[objectAssign.keyAttribute.code]) {
                    		//处理label/value的翻译
                            processLabelValue(this.funcRegion, data, this.subObjectAssign, false, this.funcDefine);
                    		setTimeout(() => this.regionData[objectAssign.mainRelAttribute.code][i] = data, 400);
                    		break;
                    	}
                    }
                }
            }else if(srcOperation.operationType == 'N') {
            	
            }
            
            this.showSubRegion = {};
            this.subRegionData = {};
            this.subObjectAssign = null;
        },
    	popupValueSelected(attrCode, value, label) {
            this.regionData[attrCode] = value;
            this.regionData[attrCode+'_label'] = label;
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