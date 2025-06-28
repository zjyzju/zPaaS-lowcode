<!-- 属性编辑页 -->
<template>
<el-dialog v-model="showFlag">
    <template #header>
        <span class="title">属性编辑</span>
    </template>
    <template #default>
        <div>
            <el-form v-if="showAttributeEdit && currentAttribute" :model="currentAttribute" label-width="120px" :rules="validateRules" ref="currentAttributeForm">
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="标识" required prop="id">
                            <el-input v-model="currentAttribute.id" readonly  size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="编码" required prop="code">
                            <el-input v-model="currentAttribute.code"  size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="名称" required prop="name">
                            <el-input v-model="currentAttribute.name"  size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="是否列表" required prop="isListAttr">
                            <el-select v-model="currentAttribute.isListAttr" class="m-2" placeholder="Select" size="small">
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
                
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="属性类型" required prop="attrType">
                            <el-select v-model="currentAttribute.attrType" class="m-2" placeholder="Select" size="small">
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
                        <el-form-item label="属性类" required prop="attrClass">
                            <el-input type="hidden" v-model="currentAttribute.attrClass" v-if="currentAttribute.attrType!='J'"  size="small"/>
                            <el-input v-model="currentAttribute.attrClassObject.code" :readonly="true"  v-if="currentAttribute.attrType!='J'" size="small"/>
                            
                            <el-input v-model="currentAttribute.attrClass" v-if="currentAttribute.attrType=='J'"  size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="3" v-if="currentAttribute.attrType!='J'">
                        <el-form-item label="" label-width="10px">
                            <el-link  type="primary" @click="selectAttrClass()" ><label>选择</label></el-link>&nbsp;
                            <el-link  type="primary" @click="clearAttrClass()" ><label>清空</label></el-link>                                                
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="hideAttributeEditPage">取消</el-button>
        <el-button type="primary" @click="saveAttribute">保存</el-button>
      </div>
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
    props: ['currentAttribute','showAttributeEdit'],
    
    emits: ['hideAttributeEditPage', 'updateAttributeToList'],
    
    setup (props, {attrs, emit, slots}) {
        const hideAttributeEditPage = ()=> {
            emit('hideAttributeEditPage');
        };
        
        const updateAttributeToList = (newAttr) => {
            emit('updateAttributeToList', newAttr);
        };
        
        return {
            hideAttributeEditPage,
            updateAttributeToList
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showAttributeEdit;
            },
            set(value) {
                this.hideAttributeEditPage();
            }
        }
    },
    components: {
        domainObjectSelect,
        valueObjectSelect 
    },
    watch: {        
        'currentAttribute.attrType': function(val, old){
            this.currentAttribute.attrClass = "";
            this.currentAttribute.attrClassObject = {};
        }
    
    },
    data() {            
        const attrTypeOptions = ref(null);
        
        const yesOrNoOptions = ref(null);
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
        
        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        
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
    	saveAttribute() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/attribute/save";
            this.$refs.currentAttributeForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.currentAttribute).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                            ElMessage(`更新属性信息成功，共更新(`+data.data+`)条属性`);
                            this.updateAttributeToList(this.currentAttribute);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        selectAttrClass() {
            if("D" == this.currentAttribute.attrType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.currentAttribute.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedObjectsForSelect = data.data;
                        this.showDomainObjectSelect = true;
                    }
                });
            }else if("V" == this.currentAttribute.attrType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/list/" + this.currentAttribute.systemId;
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
            this.currentAttribute.attrClass = "";
            this.currentAttribute.attrClassObject = {};
        },
        managedObjectSelected(obj) {
            this.currentAttribute.attrClass = obj.id;
            this.currentAttribute.attrClassObject = obj;
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