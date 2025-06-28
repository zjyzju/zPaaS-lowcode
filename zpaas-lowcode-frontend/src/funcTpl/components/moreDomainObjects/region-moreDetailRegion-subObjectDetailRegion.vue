<template>
<div>
<el-form  v-if="funcRegion != null" :model="funcRegion" label-width="120px">
    <el-row :gutter="4" v-if="this.attrAssignMap != null" v-for="(attrAssigns, key) in this.attrAssignMap">
        <el-col :span="8*attrAssign.colSpan" v-for="(attrAssign, index) in attrAssigns">
            <el-form-item :label="attrAssign.displayName">
                <attrDisplayComponent :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :subObject="null" :disableFlag="true"/>
            </el-form-item>
        </el-col>
    </el-row>

    <el-row :gutter="4">
        <el-col :span="8">
            &nbsp;
        </el-col>
        <el-col :span="15" style="justify-content: right; display: inline-flex;">
            <span v-if="funcRegion.regionOperations != null" v-for="operation in funcRegion.regionOperations">
                <template  v-if="operation.objectAssignId == null || operation.objectAssignId == '' || operation.objectAssignId == this.subObjectAssign.id">
                    <el-button type="primary" v-if="operation.operationType != 'N' && operation.operationType != 'V'"  size="small" @click="executeOperation(operation)">{{operation.name}}</el-button>
                    <el-button size="small" v-if="operation.operationType == 'N'" @click="executeOperation(operation)">{{operation.name}}</el-button>
                    <popupViewComponent v-if="operation.operationType == 'V'" :operation="operation" :regionData="regionData" :funcRegion="funcRegion" :funcDefine="funcDefine" mainPage="N"/>
                    &nbsp;&nbsp;
                </template>
            </span>
        </el-col>
        <el-col :span="1">&nbsp;</el-col>
    </el-row>
</el-form>
</div>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import attrDisplayComponent from '../common/attrDisplayComponent.vue'
import {formatAttrDisplayInfo, generateReqObject, validateReqData} from '../../js/common.js'
import popupViewComponent from '../common/popupViewButtonComponent.vue'

export default {
    props: ['funcRegion','funcDefine','regionData', 'subObjectAssign'],
    
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
    	attrDisplayComponent,
    	popupViewComponent
    },
    
    data() {      
        const attrAssignMap = ref(null);
        
        return {
            attrAssignMap
        }
    },
    mounted() {
    	if(this.funcRegion != null && this.funcRegion.regionAttrAssigns != null) {
            //格式属性的显示位置以及换行等
            this.attrAssignMap = formatAttrDisplayInfo(this.funcRegion.regionAttrAssigns, this.subObjectAssign);
            
            for(var index =0; index< this.funcRegion.regionAttrAssigns.length; index++) {
                //进行联动控制的判断
                if(this.funcDefine.objectMap[this.funcRegion.regionAttrAssigns[index].objectId].attrOptionMap == null) {
                    continue;
                }
                var attrOption = this.funcDefine.objectMap[this.funcRegion.regionAttrAssigns[index].objectId].attrOptionMap[this.funcRegion.regionAttrAssigns[index].attributeId];//当前绑定属性的配置信息
                if(attrOption == null) {
                    continue;
                }
                if((attrOption.interactionType != null && attrOption.interactionType != "N") || attrOption.parentAttributeId != null) {
                    this.interactionFlag = true;//当前区域存在联动控制时，设置为true
                }
            }
        }
    },
    
    methods: {
    	executeOperation(operation) {
    		if(operation.operationType == 'N') {//取消操作
    			this.operationExecuted(operation, null, this.subObjectAssign);
    		}else {
    			ElMessageBox.confirm('是否继续?','警告',
                        {
                          confirmButtonText: '确定',
                          cancelButtonText: '取消',
                          type: 'warning',
                        }
                ).then(() => {
	    			if(operation.exposedServiceMapping != null && operation.exposedServiceMapping.trim().length != 0) {
	    				var url = "/lcdp-proxy/lowcode/" + this.funcDefine.platformUrl + "/" + this.funcDefine.systemUrl + "/" + operation.exposedServiceMapping;
	                    var req = {};
	                    if(operation.operation.inParams != null && operation.operation.inParams.length > 0) {
	                    	req = generateReqObject(operation.operation.inParams, this.regionData, operation, this.funcRegion, this.funcDefine, false);
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
	                            ElMessage(`修改成功！`);
	                            this.operationExecuted(operation, data.data, this.subObjectAssign);
	                        }else {
	                            ElMessage.error(`请求后端服务失败！状态码：` + data.status + `，错误信息：` + data.message );
	                        }
	                    }).catch(()=>{
	                        ElMessage.error(`请求后端服务失败！`);
	                    });
	    			}else {
	    				this.operationExecuted(operation, this.regionData, this.subObjectAssign);
	    			}
                });
    		}
    	},
    	generateReqObject(inParam, regionData) {
    		var paramObj = {};
            if(inParam.paramType != 'J') {
                inParam.paramClassObject.attributes.forEach(attribute=>{
                	if(regionData[attribute.code] != null) {
                		paramObj[attribute.code] = regionData[attribute.code];
                	}else {
                		paramObj[attribute.code] = '';
                	}
                	
                });
            }else {
                paramObj = data
            }
            var reqData = {};
            reqData[inParam.code] = paramObj;
            var reqObject = {
                "reqData": reqData
            }
            return reqObject;
    	}
    	
    }
    
 }
</script>
<style scoped>
    /* 输入框或下拉选框禁用时：加粗显示提示语 */
    :deep(.el-input.is-disabled .el-input__inner){
        color: black ;
    }
</style>