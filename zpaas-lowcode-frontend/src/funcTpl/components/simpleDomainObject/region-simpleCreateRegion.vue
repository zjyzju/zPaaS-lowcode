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
</el-form>
</el-scrollbar>
    <el-row :gutter="4">
        <el-col :span="8">
            &nbsp;
        </el-col>
        <el-col :span="15" style="justify-content: right; display: inline-flex;">
            <span v-if="funcRegion.regionOperations != null" v-for="operation in funcRegion.regionOperations">
                <el-button type="primary" v-if="operation.operationType != 'N' && operation.operationType != 'V'"  size="small" @click="executeOperation(operation)">{{operation.name}}</el-button>
                <el-button size="small" v-if="operation.operationType == 'N'" @click="executeOperation(operation)">{{operation.name}}</el-button>
                <popupViewComponent v-if="operation.operationType == 'V'" :operation="operation" :regionData="regionData" :funcRegion="funcRegion" :funcDefine="funcDefine" mainPage="Y"/>
                &nbsp;&nbsp;
            </span>
        </el-col>
        <el-col :span="1">&nbsp;</el-col>
    </el-row>

</template>
<script>
import { ref } from 'vue';
import { ElMessageBox } from 'element-plus'
import attrDisplayComponent from '../common/attrDisplayComponent.vue'
import {interactionProcess, formatAttrDisplayInfo, onExecuteOperation, processPreValidateRules} from '../../js/common.js'
import popupViewComponent from '../common/popupViewButtonComponent.vue'

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
    	attrDisplayComponent,
    	popupViewComponent
    },
    
    data() {      
        const mainObject = ref({});
        const subObjects = ref([]);
        const attrAssignMap = ref(null);
        const interactionFlag = ref(null);
        const validateRules = ref(null);
        
        return {
            mainObject,
            subObjects,
            attrAssignMap,
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
        if(this.funcDefine != null && this.funcDefine.objectMap != null) {
            for(var key in this.funcDefine.objectMap) {
                if(this.funcDefine.objectMap[key].assignType == "M") {
                    this.mainObject = this.funcDefine.objectMap[key];
                }else {
                    this.subObjects.push(this.funcDefine.objectMap[key]);
                }
            }
        }
        
        if(this.funcRegion != null && this.funcRegion.regionAttrAssigns != null) {
        	//格式属性的显示位置以及换行等
            this.attrAssignMap = formatAttrDisplayInfo(this.funcRegion.regionAttrAssigns);
            
            for(var index =0; index< this.funcRegion.regionAttrAssigns.length; index++) {
            	//进行联动控制的判断
            	if(this.funcDefine.objectMap[this.funcRegion.regionAttrAssigns[index].objectId].attrOptionMap == null) {
                    continue
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

        //处理预校验规则
        if(this.funcRegion != null && this.funcRegion.regionOperations != null) {
            this.validateRules = processPreValidateRules(this.funcRegion.regionOperations, null);
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
                    console.log(this.regionData);
	    			onExecuteOperation(operation, this.regionData, this.funcRegion, this.funcDefine, false, this.operationExecuted, null, this.$refs.funcRegionForm);
                });
    		}
    		
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