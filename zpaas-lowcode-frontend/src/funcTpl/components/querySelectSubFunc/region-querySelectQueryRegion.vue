<template>
<div>
<el-form  v-if="funcRegion.regionAttrAssigns != null" :model="funcRegion" label-width="120px">
    <el-row :gutter="4" v-if="this.attrAssignMap != null" v-for="(attrAssigns, key) in this.attrAssignMap">
        <el-col :span="8*attrAssign.colSpan" v-for="(attrAssign, index) in attrAssigns">
            <queryDisplayComponent :funcRegion="funcRegion" :funcDefine="funcDefine" :queryCondition="queryCondition" :attrAssign="attrAssign" :subObject="null"/>
        </el-col>
    </el-row>

    <el-row :gutter="4">
        <el-col :span="8">
            &nbsp;
        </el-col>
        <el-col :span="14" style="justify-content: right; display: inline-flex;">
            <span v-if="funcRegion.regionOperations != null" v-for="operation in funcRegion.regionOperations">
                <el-button type="primary" v-if="operation.operationType != 'N' && operation.operationType != 'V' && operation.operationType != 'R'"  size="small" @click="executeOperation(operation,this.queryCondition)">{{operation.name}}</el-button>
                <el-button size="small" v-if="operation.operationType == 'N' || operation.operationType == 'R'" @click="executeOperation(operation,this.queryCondition)">{{operation.name}}</el-button>
                <popupViewComponent v-if="operation.operationType == 'V'" :operation="operation" :regionData="this.queryCondition" :funcRegion="funcRegion" :funcDefine="funcDefine" mainPage="Y"/>
                &nbsp;&nbsp;
            </span>
        </el-col>
         <el-col :span="2">&nbsp;</el-col>
    </el-row>
</el-form>
</div>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'
import {interactionProcess, formatAttrDisplayInfo, generateReqObject} from '../../js/common.js'
import queryDisplayComponent from '../common/queryDisplayComponent.vue'
import popupViewComponent from '../common/popupViewButtonComponent.vue'

export default {
    props: ['funcRegion','funcDefine', 'queryCondition', 'resultRegion'],
    
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
    	queryDisplayComponent,
    	popupViewComponent
    },
    computed: {
        regionData1: function() {
            return JSON.parse(JSON.stringify(this.queryCondition));
        }
    },
    watch: {        
        'regionData1': {
            handler: function(newRegionData, oldRegionData){
                if(!this.interactionFlag) {
                    return;//当前不存在联动控制时，不进行联动处理
                }
                
                interactionProcess(newRegionData, oldRegionData, this.funcDefine, this.funcRegion, null, this.queryCondition);//联动处理
            },
            deep:true // 深度监听的参数
        }
    },
    
    data() {      
        const attrAssignMap = ref(null);
        const queryOperation = ref(null);
        const interactionFlag = ref(null);
        
        return {
            attrAssignMap,
            queryOperation,
            interactionFlag
        }
    },
    mounted() {
        
        if(this.funcRegion != null && this.funcRegion.regionAttrAssigns != null) {
        	//格式属性的显示位置以及换行等
            this.attrAssignMap = formatAttrDisplayInfo(this.funcRegion.regionAttrAssigns);
        	
            for(var index =0; index< this.funcRegion.regionAttrAssigns.length; index++) {
                //进行联动控制的判断
                var attrOption = this.funcDefine.objectMap[this.funcRegion.regionAttrAssigns[index].objectId].attrOptionMap[this.funcRegion.regionAttrAssigns[index].attributeId];//当前绑定属性的配置信息
                if(attrOption == null) {
                    continue;
                }
                if((attrOption.interactionType != null && attrOption.interactionType != "N") || attrOption.parentAttributeId != null) {
                    this.interactionFlag = true;//当前区域存在联动控制时，设置为true
                }
                
                //处理上级关联属性
                if(this.funcDefine.objectMap[this.funcRegion.regionAttrAssigns[index].objectId].attrOptionMap != null) {
                    var attrOption = this.funcDefine.objectMap[this.funcRegion.regionAttrAssigns[index].objectId].attrOptionMap[this.funcRegion.regionAttrAssigns[index].attributeId];
                    if(attrOption != null && attrOption.parentObjectAssignId != null && attrOption.parentObjectAssignId.trim().length > 0 &&
                            attrOption.parentAttributeId != null && attrOption.parentAttributeId.trim().length > 0 ) {
                        for(var i in this.funcDefine.objectAssigns) {
                            var objectAssign = this.funcDefine.objectAssigns[i];
                            if(objectAssign.id == attrOption.parentObjectAssignId) {
                                attrOption.parentAttributePath = null;
                                for(var j in objectAssign.attributes) {
                                    if(objectAssign.attributes[j].id == attrOption.parentAttributeId) {
                                        attrOption.parentAttributePath = objectAssign.attributes[j].code;
                                        break;
                                    }
                                }
                                
                                if("S" == objectAssign.assignType) {
                                    attrOption.parentAttributePath = objectAssign.mainRelAttribute.code + "." + attrOption.parentAttributePath;
                                }
                                break;
                            }
                        }
                    }
                }            }
            for(var operationIndex in this.funcRegion.regionOperations) {
                if(this.funcRegion.regionOperations[operationIndex].isUserDefined == 'N' && this.funcRegion.regionOperations[operationIndex].operationType == 'Q') {
                    this.queryOperation = this.funcRegion.regionOperations[operationIndex];
                    break;
                }
            }
        }
    },
    methods: {
        executeOperation(operation, data) {
            if(operation.operationType == 'R') {//重置
                for(var i in this.funcRegion.regionAttrAssigns) {
                    var attrAssign = this.funcRegion.regionAttrAssigns[i];
                    if(attrAssign.readonly != 'Y') {
                        data[attrAssign.attribute.code] = null;
                        if(data[attrAssign.attribute.code+"_label"] != undefined) {
                            data[attrAssign.attribute.code+"_label"] = null;
                        }
                    }
                }
                this.operationExecuted(operation, null, null, this.funcRegion);
            }else {
                if(operation.exposedServiceMapping != null && operation.exposedServiceMapping.trim().length > 0) {
                    var url = "/lcdp-proxy/lowcode/" + this.funcDefine.platformUrl + "/" + this.funcDefine.systemUrl + "/" + operation.exposedServiceMapping;
                    var req = {};
                    if(operation.operation.inParams != null && operation.operation.inParams.length > 0) {
                        req = generateReqObject(operation.operation.inParams, data, operation, this.funcRegion, this.funcDefine, (this.resultRegion.regionCfg == null || this.resultRegion.regionCfg.trim().length == 0 || JSON.parse(this.resultRegion.regionCfg).pageSupport != false));
                    }
                //参数校验
                    if(operation.operation.validateRuleGroup != null && operation.operation.validateRuleGroup.validateRules != null) {
                        if(!validateReqData(operation.operation.validateRuleGroup, req.reqData)) {
                            return;
                        }
                    }
                    axiosClient.post(url, req).then((response) => {
                        var result = response.data;
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
    }    
}
</script>