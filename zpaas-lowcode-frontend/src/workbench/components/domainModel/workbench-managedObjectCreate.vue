<!-- 新建管理对象2 -->
<template>
<el-dialog v-model="showFlag" title="新建管理对象">
    <template #header>
        <span class="title">新建管理对象</span>
    </template>
    <el-form  :model="newManagedObject" label-width="120px" :rules="validateRules" ref="ruleFormRef" >
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="编码" required prop="code">
                    <el-input v-model="newManagedObject.code"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="newManagedObject.name"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="对象类型" required prop="objectType">
                      <el-select v-model="newManagedObject.objectType" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in objectTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="归属业务域" required prop="businessDomainId">
                    <el-select v-model="newManagedObject.businessDomainId" v-if="businessDomains"  class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in businessDomains"
                                  :key="item.businessDomain.id"
                                  :label="item.businessDomain.name"
                                  :value="item.businessDomain.id"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="描述">
                    <el-input v-model="newManagedObject.description"  size="small"/>
                </el-form-item>
            </el-col>
           
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideManagedObjectCreatePage()">取消</el-button>
            <el-button type="primary" @click="managedObjectCreateBack()">上一步</el-button>
            <el-button type="primary" @click="createManageObject()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import {reactive, ref } from 'vue';
import { ElMessageBox, ElMessage} from 'element-plus'

export default {
    props: ['newManagedObject', 'showManagedObjectCreate', 'businessDomains'],
    
    emits: [ 'hideManagedObjectCreatePage', 'addNewManagedObjectToList', 'managedObjectCreateBack'],
    
    setup(props, {attrs, emit, slots}) {
        const hideManagedObjectCreatePage = () => {
            emit('hideManagedObjectCreatePage');
        };
        
        const addNewManagedObjectToList = (system) => {
            emit('addNewManagedObjectToList', system);
        };
        
        const managedObjectCreateBack = () => {
            emit('managedObjectCreateBack');
        };
        
        return {
        	hideManagedObjectCreatePage,
        	addNewManagedObjectToList,
        	managedObjectCreateBack
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showManagedObjectCreate;
            },
            set(value) {
                this.hideManagedObjectCreatePage();
            }
        }
    },
    data() {
    	const objectTypes = [
    	    {
    	        value: 'S',
    	        label: '服务对象',
    	    },
    	    {
    	        value: 'D',
    	        label: '领域对象',
    	    },
    	    {
    	        value: 'V',
    	        label: '值传递对象',
    	    }
    	];

    	const objectTypeOptions = ref(objectTypes);

        const validateRules = ref({
            "code": [
                { required: true, message: '编码不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "objectType": [
                { required: true, message: '对象类型不能为空！', trigger: 'blur' }
            ],
            "businessDomainId": [
                { required: true, message: '归属业务域不能为空！', trigger: 'blur' }
            ]
        });

        return {
        	objectTypes,
        	objectTypeOptions,
            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValuesByCond", [{"dictCode":"BUSINESS_SYSTEM_SUB_TYPE", "parentDictCode":"BUSINESS_SYSTEM_TYPE", "parentDictValue":"D"}]).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectTypes = data.data['BUSINESS_SYSTEM_SUB_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },   
    methods: {
    	createManageObject() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/managedObject/add";
            this.$refs.ruleFormRef.validate((valid, fields) => {
                if (valid) {
                    axiosClient.post(url, this.newManagedObject).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.managedObject.id != null) {
                            ElMessage(`新增管理对象成功`);
                            this.addNewManagedObjectToList(data.data);
                        }
                    });
                } else {
                    console.log('参数校验失败!', fields)
                }
            });
        }
    }
}

</script>