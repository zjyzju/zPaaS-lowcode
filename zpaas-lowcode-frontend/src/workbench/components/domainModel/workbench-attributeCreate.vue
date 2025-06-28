<!-- 新建属性 -->

<template>
<el-dialog v-model="showFlag" title="新建属性">
    <template #header>
        <span class="title">新建属性</span>
    </template>
    <el-form  :model="newAttribute" label-width="120px" :rules="validateRules" ref="newAttributeForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="编码" required prop="code">
                    <el-input v-model="newAttribute.code"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="newAttribute.name"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="属性类型"  required prop="attrType">
                      <el-select v-model="newAttribute.attrType" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in attrTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="9">
                <el-form-item label="属性类"  required prop="attrClass">
                    <el-input type="hidden" v-model="newAttribute.attrClass" v-if="newAttribute.attrType!='J'"  size="small"/>
                    <el-input v-model="newAttribute.attrClassObject.code" :readonly="true"  v-if="newAttribute.attrType!='J'" size="small"/>
                    
                    <el-input v-model="newAttribute.attrClass" v-if="newAttribute.attrType=='J'"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="3" v-if="newAttribute.attrType!='J'">
                <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectAttrClass()" ><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearAttrClass()" ><label>清空</label></el-link>                                                
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="是否列表"  required prop="isListAttr">
                      <el-select v-model="newAttribute.isListAttr" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in yesOrNoOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideAttributeCreatePage">取消</el-button>
            <el-button type="primary" @click="createAttribute()">确定</el-button>
        </span>
    </template>
</el-dialog>
<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideDomainObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>
<!-- 选择值传递对象信息 -->
<valueObjectSelect v-if="showValueObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideValueObjectSelectPage="hideValueObjectSelectPage" :showValueObjectSelect="showValueObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

import domainObjectSelect from './workbench-domainObjectSelect.vue'
import valueObjectSelect from './workbench-valueObjectSelect.vue'

export default {
    props: ['newAttribute','showAttributeCreate'],
    
    emits: ['hideAttributeCreatePage', 'addNewAttributeToList'],
    
    setup (props, {attrs, emit, slots}) {
        const hideAttributeCreatePage = ()=> {
            emit('hideAttributeCreatePage');
        };
        
        const addNewAttributeToList = (newAttr) => {
        	emit('addNewAttributeToList', newAttr);
        };
        
        return {
        	hideAttributeCreatePage,
        	addNewAttributeToList
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showAttributeCreate;
            },
            set(value) {
                this.hideAttributeCreatePage();
            }
        }
    },
    components: {
        domainObjectSelect,
        valueObjectSelect 
    },
    watch: {        
        'newAttribute.attrType': function(val, old){
            this.newAttribute.attrClass = "";
            this.newAttribute.attrClassObject = {};
        }
    
    },
    data() {            
    	const attrTypeOptions = ref(null);
        
    	const yesOrNoOptions = ref(null);
    	
    	const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        
        const validateRules = ref({
            "code": [
                { required: true, message: '编码不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "attrType": [
                { required: true, message: '属性类型不能为空！', trigger: 'blur' }
            ],
            "attrClass": [
                { required: true, message: '属性类不能为空！', trigger: 'blur' }
            ],
            "isListAttr": [
                { required: true, message: '是否列表不能为空！', trigger: 'blur' }
            ]
        });
        return {
        	attrTypeOptions,
        	yesOrNoOptions,
        	showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,

            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_TYPE','PUB_YES_OR_NO']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.attrTypeOptions = data.data['OBJECT_TYPE'];
                this.yesOrNoOptions = data.data['PUB_YES_OR_NO'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	createAttribute() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/attribute/add";
            console.log(this.newAttribute);
            this.$refs.newAttributeForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.newAttribute).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                        ElMessage(`新增属性成功`);
                        this.addNewAttributeToList(data.data);
                    }
                });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        selectAttrClass() {
            if("D" == this.newAttribute.attrType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.newAttribute.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedObjectsForSelect = data.data;
                        this.showDomainObjectSelect = true;
                    }
                });
            }else if("V" == this.newAttribute.attrType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/list/" + this.newAttribute.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedObjectsForSelect = data.data;
                        this.showValueObjectSelect = true;
                    }
                });
            }else {
                return;
            }
        },
        clearAttrClass(){
            this.newAttribute.attrClass = "";
            this.newAttribute.attrClassObject = {};
        },
        managedObjectSelected(obj) {
            this.newAttribute.attrClass = obj.id;
            this.newAttribute.attrClassObject = obj;
            this.managedObjectsForSelect = null;
            this.showDomainObjectSelect = false;
            this.showValueObjectSelect = false;
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