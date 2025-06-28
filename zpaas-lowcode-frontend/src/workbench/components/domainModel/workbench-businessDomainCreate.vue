<!-- 新建业务域 -->
<template>
<el-dialog v-model="showFlag" title="新建业务域">
    <template #header>
        <span class="title">新建业务域</span>
    </template>
    <el-form  :model="businessDomain" label-width="120px" :rules="validateRules" ref="businessDomainForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="businessDomain.name"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="描述" required prop="description">
                    <el-input v-model="businessDomain.description"  size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideBusinessDomainCreatePage()">取消</el-button>
            <el-button type="primary" @click="manageBusinessDomainCreateBack()">上一步</el-button>
            <el-button type="primary" @click="createBusinessDomain()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'

export default {
    props: ['businessDomain', 'showBusinessDomainCreate'],
    
    emits: [ 'hideBusinessDomainCreatePage', 'updateBusinessDomainToList', 'manageBusinessDomainCreateBack'],
    
    setup(props, {attrs, emit, slots}) {
        const hideBusinessDomainCreatePage = () => {
            emit('hideBusinessDomainCreatePage');
        };
        
        const updateBusinessDomainToList = (system) => {
            emit('updateBusinessDomainToList', system);
        };
        
        const manageBusinessDomainCreateBack = () => {
        	emit('manageBusinessDomainCreateBack');
        };
        
        return {
        	hideBusinessDomainCreatePage,
            updateBusinessDomainToList,
            manageBusinessDomainCreateBack
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showBusinessDomainCreate;
            },
            set(value) {
                this.hideBusinessDomainCreatePage();
            }
        }
    },
    data() {
        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "description": [
                { required: true, message: '描述不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
            validateRules
        }
    },
    
    methods: {
    	createBusinessDomain() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/businessDomain/add";
            this.$refs.businessDomainForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url, this.businessDomain).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`新增业务域成功`);
                            this.updateBusinessDomainToList(data);
                        }
                    }).catch(ex=> {
                        ElMessage.error(`调用后端服务失败！` + ex);
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
    }
}

</script>
