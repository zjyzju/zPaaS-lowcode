<!-- 对象绑定新建 -->

<template>
<el-dialog v-model="showFlag" title="对象绑定">
    <template #header>
        <span class="title">对象绑定</span>
    </template>
    <el-form  :model="newObjectAssign" label-width="120px" :rules="validateRules" ref="newObjectAssignForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="对象类型" required prop="objectType">
                      <el-select v-model="newObjectAssign.objectType" :disabled="newObjectAssign.objectType=='B'" class="m-2" placeholder="Select" size="small">
                           <el-option v-if="templateType=='V' || templateType=='R'" v-for="item in objectTypeOptionsBI" :key="item.value" :label="item.label" :value="item.value" />
                           <el-option v-if="templateType=='C'" v-for="item in objectTypeOptionsAll" :key="item.value" :label="item.label" :value="item.value" />
                           <el-option v-if="templateType!='C' && templateType!='V' && templateType!='R'" v-for="item in objectTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="9">
                <el-form-item label="绑定对象" required prop="objectId">
                    <el-input type="hidden" v-model="newObjectAssign.objectId" size="small"/>
                    <el-input v-model="newObjectAssign.assignObject.code" :readonly="true"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="3">
                <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectObjectClass()" ><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearObjectClass()" ><label>清空</label></el-link>                                                
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
	            <el-form-item label="绑定类型" required prop="assignType">
	                <el-select v-model="newObjectAssign.assignType" class="m-2" placeholder="Select" size="small">
	                    <el-option v-for="item in assignTypeOptions" :key="item.value" :value="item.value" :label="item.label" />
	                </el-select>
	            </el-form-item>
	        </el-col>
            <el-col :span="12">
	            <el-form-item label="关键属性" required prop="keyAttrId" v-if="newObjectAssign.objectType != 'B'">
	                <el-select v-model="newObjectAssign.keyAttrId" clearable class="m-2" placeholder="Select" size="small">
	                    <el-option
	                            v-for="item in newObjectAssign.attributes"
	                            :key="item.id"
	                            :label="item.code"
	                            :value="item.id"
	                     />
	                </el-select>
	            </el-form-item>
	        </el-col>
        </el-row>
        <el-row :gutter="4" v-if="newObjectAssign.objectType != 'B'">
            <el-col :span="12">
                <el-form-item label="关联属性">
                    <el-select v-model="newObjectAssign.relAttrId" clearable class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in newObjectAssign.attributes" :key="item.id" :label="item.code"  :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
	            <el-form-item label="主对象关联属性">
	                <el-select v-model="newObjectAssign.mainRelAttrId" clearable class="m-2" placeholder="Select" size="small">
	                    <el-option v-if="mainObjectAssign != null"
	                            v-for="item in mainObjectAssign.attributes"
	                            :key="item.id"
	                            :label="item.code"
	                            :value="item.id"
	                     />
	                </el-select>
	            </el-form-item>
	        </el-col>
        </el-row>
        <el-row :gutter="4">
	        <el-col :span="12">
	            <el-form-item label="显示顺序" required prop="displayOrder">
	                <el-input v-model="newObjectAssign.displayOrder" size="small" />
	            </el-form-item>
	        </el-col>
	        <el-col :span="12">
	            &nbsp;
	        </el-col>
	    </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideObjectAssignCreatePage">取消</el-button>
            <el-button type="primary" @click="createObjectAssign()">确定</el-button>
        </span>
    </template>
</el-dialog>
<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideDomainObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>
<!-- 选择值传递对象信息 -->
<valueObjectSelect v-if="showValueObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideValueObjectSelectPage="hideValueObjectSelectPage" :showValueObjectSelect="showValueObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>
<!-- 数据集选择 -->
<dataSetSelect v-if="showDataSetSelect" @dataSetSelected="dataSetSelected" @hideDataSetSelectPage="hideDataSetSelectPage" :showDataSetSelect="showDataSetSelect" :dataSetsForSelect="dataSetsForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

import domainObjectSelect from '../domainModel/workbench-domainObjectSelect.vue'
import valueObjectSelect from '../domainModel/workbench-valueObjectSelect.vue'
import dataSetSelect from '../dataSetMgr/workbench-dataSetSelect.vue'

export default {
    props: ['newObjectAssign','showObjectAssignCreate', 'isCreate', 'mainObjectAssign', 'templateType'],
    
    emits: ['hideObjectAssignCreatePage', 'updateNewObjectAssignToList'],
    
    setup (props, {attrs, emit, slots}) {
        const hideObjectAssignCreatePage = ()=> {
            emit('hideObjectAssignCreatePage');
        };
        
        const updateNewObjectAssignToList = (newAttr) => {
        	emit('updateNewObjectAssignToList', newAttr);
        };
        
        return {
        	hideObjectAssignCreatePage,
        	updateNewObjectAssignToList
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showObjectAssignCreate;
            },
            set(value) {
                this.hideObjectAssignCreatePage();
            }
        }
    },
    components: {
        domainObjectSelect,
        valueObjectSelect,
        dataSetSelect
    },
    watch: {        
        'newObjectAssign.objectType': function(val, old){
            this.newObjectAssign.objectId = "";
            this.newObjectAssign.assignObject = {};
        }
    
    },
    data() {            
    	const objectTypeOptions = ref(null);

        const objectTypeOptionsAll = ref(null);

        const objectTypeOptionsBI = ref(null);
    	
    	const assignTypeOptions = ref(null);
        
    	const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);

        const showDataSetSelect = ref(false);
        const dataSetsForSelect = ref(null);

        const validateRules = ref({
            "objectType": [
                { required: true, message: '对象类型不能为空！', trigger: 'blur' }
            ],
            "objectId": [
                { required: true, message: '绑定对象不能为空！', trigger: 'blur' }
            ],
            "assignType": [
                { required: true, message: '绑定类型不能为空！', trigger: 'blur' }
            ],
            "keyAttrId": [
                { required: true, message: '关键属性不能为空！', trigger: 'blur' }
            ],
            "displayOrder": [
                { required: true, message: '顺序不能为空！', trigger: 'blur' },
                { pattern: /^[0-9]*$/, message: '顺序必须是整数类型！', trigger: 'blur' }
            ]
        });
        
        return {
        	objectTypeOptions,
            objectTypeOptionsBI,
            objectTypeOptionsAll,
        	showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,
            showDataSetSelect,
            dataSetsForSelect,
            assignTypeOptions,
            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_TYPE_HAS_ATTRIBUTE','OBJECT_TYPE_BUSINESS','OBJECT_TYPE_BI','ASSIGN_OBJECT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectTypeOptions = data.data['OBJECT_TYPE_HAS_ATTRIBUTE'];
                this.objectTypeOptionsAll = data.data['OBJECT_TYPE_BUSINESS'];
                this.objectTypeOptionsBI = data.data['OBJECT_TYPE_BI'];
                this.assignTypeOptions = data.data['ASSIGN_OBJECT_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	createObjectAssign() {
            this.$refs.newObjectAssignForm.validate((valid, fields)=> {
                if(valid) {
                    if(this.isCreate) {
                        var url = "/lcdp-proxy/lowcode/platform/fe/api/funcObjectAssign/add";
                        console.log(this.newObjectAssign);
                        axiosClient.post(url,this.newObjectAssign).then((response) => {
                            var data = response.data; 
                            if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                                ElMessage(`新增绑定对象成功`);
                                if(this.newObjectAssign.attributes != null && this.newObjectAssign.attributes.length > 0) {
                                    for(var i in this.newObjectAssign.attributes) {
                                        if(this.newObjectAssign.attributes[i].id == this.newObjectAssign.relAttrId) {
                                            this.newObjectAssign.relAttribute = this.newObjectAssign.attributes[i];
                                            data.data.relAttribute = this.newObjectAssign.attributes[i];
                                            break;
                                        }
                                    }
                                }
                                this.updateNewObjectAssignToList(data.data);
                            }
                        });
                    }else {
                        if(this.newObjectAssign.attributes != null) {
                            for(var i in this.newObjectAssign.attributes) {
                                if(this.newObjectAssign.attributes[i].id == this.newObjectAssign.relAttrId) {
                                    this.newObjectAssign.relAttribute = this.newObjectAssign.attributes[i];
                                    break;
                                }
                            }
                        }
                        this.updateNewObjectAssignToList(this.newObjectAssign);
                    }
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
    		
        },
        selectObjectClass() {
            if("D" == this.newObjectAssign.objectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.newObjectAssign.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedObjectsForSelect = data.data;
                        this.showDomainObjectSelect = true;
                    }
                });
            }else if("V" == this.newObjectAssign.objectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/list/" + this.newObjectAssign.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedObjectsForSelect = data.data;
                        this.showValueObjectSelect = true;
                    }
                });
            }else if("B" == this.newObjectAssign.objectType) {
                var url = "/lcdp-proxy/lowcode/platform/bi/api/dataSet/list/" + this.newObjectAssign.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.dataSetsForSelect = data.data;
                        this.showDataSetSelect = true;
                    }
                });
            }else {
                return;
            }
        },
        clearObjectClass(){
            this.newObjectAssign.objectId = "";
            this.newObjectAssign.assignObject = {};
            this.newObjectAssign.attributes = null;
            this.newObjectAssign.relAttrId = "";
        },
        managedObjectSelected(obj) {
            this.newObjectAssign.objectId = obj.id;
            this.newObjectAssign.assignObject = obj;
            this.managedObjectsForSelect = null;
            this.showDomainObjectSelect = false;
            this.showValueObjectSelect = false;
            
            var url = "/lcdp-proxy/lowcode/platform/be/api/attribute/list/" + this.newObjectAssign.objectType + "/" + this.newObjectAssign.objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                	this.newObjectAssign.attributes = data.data;
                }
            });
        },
        dataSetSelected(obj) {
            this.newObjectAssign.objectId = obj.id;
            this.newObjectAssign.assignObject = obj;
            this.dataSetsForSelect = null;
            this.showDataSetSelect = false;
            
            this.newObjectAssign.keyAttrId = "";
            this.newObjectAssign.relAttrId = "";
        },
        hideDataSetSelectPage(){
             this.managedObjectsForSelect = null;
             this.showDataSetSelect = false;
        },
        hideDomainObjectSelectPage(){
             this.managedObjectsForSelect = null;
             this.showDomainObjectSelect = false;
        },
        hideValueObjectSelectPage(){
            this.managedObjectsForSelect = null;
            this.showValueObjectSelect = false;
       }
    }
    
}
</script>