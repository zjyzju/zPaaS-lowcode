<!-- 新建动态映射详情 -->

<template>
<el-dialog v-model="showFlag" title="新建动态映射详情">
    <template #header>
        <span class="title">新建动态映射详情</span>
    </template>
    <el-form  :model="newDynamicMappingDetail" label-width="120px" :rules="validateRules" ref="newDynamicMappingDetailForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="关键属性值" required prop="keyAttrValue">
                    <el-input v-model="newDynamicMappingDetail.keyAttrValue"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="映射描述" required prop="description">
                    <el-input v-model="newDynamicMappingDetail.description"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="映射类型">
                    <el-select v-model="dynamicMapping.mappingType" class="m-2" disabled placeholder="Select" size="small">
                        <el-option
                                v-for="item in mappingTypeOptions"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="映射对象标识" required prop="mappingObjectId">
                    <el-input type="hidden" v-model="newDynamicMappingDetail.mappingObjectId"  size="small"/>
                    <el-input v-model="mappingObjectLabel" :readonly="true" size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="3">
                <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectMappingObject()" ><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearMappingObject()" ><label>清空</label></el-link>                                                
                </el-form-item>
            </el-col>
            <el-col :span="9"></el-col>
        </el-row>
        
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDynamicMappingDetailCreatePage">取消</el-button>
            <el-button type="primary" @click="createDynamicMappingDetail()">确定</el-button>
        </span>
    </template>
</el-dialog>
<!-- 选择数据映射信息 -->
<dataMappingQuerySelect v-if="showDataMappingQuerySelect"  :showDataMappingSelect="showDataMappingQuerySelect" @hideDataMappingSelectPage="hideDataMappingSelectPage" @dataMappingSelected="dataMappingSelected" :systemId="newDynamicMappingDetail.systemId"/>
<!-- 选择校验规则组信息 -->
<ruleGroupQuerySelect v-if="showRuleGroupSelect"  :showRuleGroupSelect="showRuleGroupSelect" @hideRuleGroupSelectPage="hideRuleGroupSelectPage" @ruleGroupSelected="ruleGroupSelected" :systemId="newDynamicMappingDetail.systemId"/>
<!-- 选择方法信息 -->
<operationSelect v-if="showOperationSelect"  :showOperationSelect="showOperationSelect" @hideOperationSelectPage="hideOperationSelectPage" @operationSelected="operationSelected" :systemId="newDynamicMappingDetail.systemId"/>
<!-- 选择文件对象映射信息 -->
<fileObjectMappingQuerySelect v-if="showFileObjectMappingQuerySelect"  :showFileObjectMappingSelect="showFileObjectMappingQuerySelect" @hideFileObjectMappingSelectPage="hideFileObjectMappingSelectPage" @fileObjectMappingSelected="fileObjectMappingSelected" :systemId="newDynamicMappingDetail.systemId"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

import dataMappingQuerySelect from '../domainModel/workbench-dataMappingQuerySelect.vue';
import ruleGroupQuerySelect from '../domainModel/workbench-ruleGroupQuerySelect.vue';
import operationSelect from '../domainModel/workbench-operationSelect.vue';
import fileObjectMappingQuerySelect from '../domainModel/workbench-fileObjectMappingQuerySelect.vue';

export default {
    props: ['newDynamicMappingDetail','showDynamicMappingDetailCreate', 'dynamicMapping'],
    
    emits: ['hideDynamicMappingDetailCreatePage', 'addNewDynamicMappingDetailToList'],
    
    setup (props, {attrs, emit, slots}) {
        const hideDynamicMappingDetailCreatePage = ()=> {
            emit('hideDynamicMappingDetailCreatePage');
        };
        
        const addNewDynamicMappingDetailToList = (newAttr) => {
        	emit('addNewDynamicMappingDetailToList', newAttr);
        };
        
        return {
        	hideDynamicMappingDetailCreatePage,
        	addNewDynamicMappingDetailToList
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showDynamicMappingDetailCreate;
            },
            set(value) {
                this.hideDynamicMappingDetailCreatePage();
            }
        }
    },
    components: {
        dataMappingQuerySelect,
        ruleGroupQuerySelect,
        operationSelect,
        fileObjectMappingQuerySelect
    },
    watch: {        
       
    },
    data() {            
    	const mappingTypeOptions = ref(null);
    	
    	const mappingObjectLabel = ref(null);

        const showDataMappingQuerySelect = ref(false);
        const showRuleGroupSelect = ref(false);
        const showOperationSelect = ref(false);
        const showFileObjectMappingQuerySelect = ref(false);

        const validateRules = ref({
            "keyAttrValue": [
                { required: true, message: '关键属性值不能为空！', trigger: 'blur' }
            ],
            "description": [
                { required: true, message: '描述不能为空！', trigger: 'blur' }
            ],
            "mappingObjectId": [
                { required: true, message: '映射对象不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
        	mappingTypeOptions,
        	mappingObjectLabel,

            showDataMappingQuerySelect,
            showRuleGroupSelect,
            showOperationSelect,
            showFileObjectMappingQuerySelect,
            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['DYNAMIC_MAPPING_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.mappingTypeOptions = data.data['DYNAMIC_MAPPING_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	createDynamicMappingDetail() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/dynamicMappingDetail/add";
            this.$refs.newDynamicMappingDetailForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.newDynamicMappingDetail).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`新增动态映射详情成功`);
                            this.addNewDynamicMappingDetailToList(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        clearMappingObject(){
    		this.newDynamicMappingDetail.mappingObjectId = "";
    		this.mappingObjectLabel = "";
    	},
        selectMappingObject(type) {
            if(this.dynamicMapping.mappingType == "D") {
                this.showDataMappingQuerySelect = true;
            }else if(this.dynamicMapping.mappingType == "V") {
                this.showRuleGroupSelect = true;
            }else if(this.dynamicMapping.mappingType == "O") {
                this.showOperationSelect = true;
            }else if(this.dynamicMapping.mappingType == "F") {
                this.showFileObjectMappingQuerySelect = true;
            }else {

            }
        },
        hideDataMappingSelectPage() {
            this.showDataMappingQuerySelect = false;
        },
        dataMappingSelected(mappings) {
        	this.newDynamicMappingDetail.mappingObjectId = mappings.id;
        	this.mappingObjectLabel = mappings.name;
        	
            this.showDataMappingQuerySelect = false;
        },
        hideRuleGroupSelectPage() {
            this.showRuleGroupSelect = false;
        },
        ruleGroupSelected(ruleGroup) {
        	this.newDynamicMappingDetail.mappingObjectId = ruleGroup.id;
        	this.mappingObjectLabel = ruleGroup.name;
        	
            this.showRuleGroupSelect = false;
        },
        hideOperationSelectPage() {
            this.showOperationSelect = false;
        },
        operationSelected(operation) {
        	this.newDynamicMappingDetail.mappingObjectId = operation.id;
        	this.mappingObjectLabel = operation.code;
        	
            this.showOperationSelect = false;
        },
        hideFileObjectMappingSelectPage() {
            this.showFileObjectMappingQuerySelect = false;
        },
        fileObjectMappingSelected(mappings) {
        	this.newDynamicMappingDetail.mappingObjectId = mappings.id;
        	this.mappingObjectLabel = mappings.name;
        	
            this.showFileObjectMappingQuerySelect = false;
        }
    }
    
}
</script>