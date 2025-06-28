<!-- 新建安全密钥资源 -->
<template>
<el-dialog v-model="showFlag" title="新建安全密钥资源">
    <template #header>
        <span class="title">新建安全密钥资源</span>
    </template>
    <el-form  :model="newSecurityKeyResource" label-width="120px" :rules="validateRules" ref="newSecurityKeyResourceForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="newSecurityKeyResource.name"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="类型" required prop="type">
                    <el-select v-model="newSecurityKeyResource.type" class="m-2" placeholder="Select" size="small">
                           <el-option v-for="item in securityKeyTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="加密算法"  prop="cipherAlgorithm">
                    <el-select v-model="newSecurityKeyResource.cipherAlgorithm"  v-if="newSecurityKeyResource.type == 'P'" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in asymmetricAlgorithmOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                    <el-select v-model="newSecurityKeyResource.cipherAlgorithm"  v-else class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in symmetricAlgorithmOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                &nbsp;
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="newSecurityKeyResource.type != 'P'">
            <el-col :span="24">
                <el-form-item label="密钥" prop="securityKey">
                    <el-input v-model="newSecurityKeyResource.securityKey" type="password"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="newSecurityKeyResource.type == 'P'">
            <el-col :span="12">
                <el-form-item label="私钥">
                    <el-input type="file" id="privateKeyFile" v-model="newSecurityKeyResource.privateKeyFile" accept="application/x-pkcs12" @change="privateKeyFileChanged()" clearable size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="私钥密码">
                    <el-input type="password" v-model="newSecurityKeyResource.privateKeyPwd"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="newSecurityKeyResource.type == 'P'">
            <el-col :span="12">
                <el-form-item label="公钥">
                    <el-input type="file" id="publicKeyFile" v-model="newSecurityKeyResource.publicKeyFile" accept="application/x-x509-ca-cert" @change="publicKeyFileChanged()" clearable size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                &nbsp;
            </el-col>
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideSecurityKeyResourceCreatePage()">取消</el-button>
            <el-button type="primary" @click="securityKeyResourceCreateBack()">上一步</el-button>
            <el-button type="primary" @click="createSecurityKeyResource()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'



export default {
    props: ['newSecurityKeyResource','showSecurityKeyResourceCreate'],
    
    emits: ['addNewSecurityKeyResourceToList', 'hideSecurityKeyResourceCreatePage', 'securityKeyResourceCreateBack'],
    
    setup (props, {attrs, emit, slots}) {
        const addNewSecurityKeyResourceToList = (newSecurityKeyResource)=> {
            emit('addNewSecurityKeyResourceToList', newSecurityKeyResource);
        };
        
        const hideSecurityKeyResourceCreatePage = () => {
            emit('hideSecurityKeyResourceCreatePage');	
        };
        
        const securityKeyResourceCreateBack = () => {
            emit('securityKeyResourceCreateBack'); 
        };
        
        return {
        	addNewSecurityKeyResourceToList,
        	hideSecurityKeyResourceCreatePage,
        	securityKeyResourceCreateBack
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showSecurityKeyResourceCreate;
            },
            set(value) {
                this.hideSecurityKeyResourceCreatePage();
            }
        }
    },
    data() {            
        const securityKeyTypeOptions = ref(null);

        const symmetricAlgorithmOptions = ref(null);

        const asymmetricAlgorithmOptions = ref(null);

        const privateKeyFile = ref(null);
        const publicKeyFile = ref(null);

        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "type": [
                { required: true, message: '类型不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
        	securityKeyTypeOptions,
            symmetricAlgorithmOptions,
            asymmetricAlgorithmOptions,
            privateKeyFile,
            publicKeyFile,
            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['SECURITY_KEY_TYPE','SYMMETRIC_ALGORITHM','ASYMMETRIC_ALGORITHM']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.securityKeyTypeOptions = data.data['SECURITY_KEY_TYPE'];
                this.symmetricAlgorithmOptions = data.data['SYMMETRIC_ALGORITHM'];
                this.asymmetricAlgorithmOptions = data.data['ASYMMETRIC_ALGORITHM'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	createSecurityKeyResource() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/securityKeyResource/add";
            this.$refs.newSecurityKeyResourceForm.validate((valid, fields)=> {
                if(valid) {
                    var formData = new FormData();
                    if(this.newSecurityKeyResource.privateKeyFile != null && this.privateKeyFile != null) {
                        formData.append('privateKeyFile', this.privateKeyFile);
                    }
                    if(this.newSecurityKeyResource.publicKeyFile != null && this.publicKeyFile != null) {
                        formData.append('publicKeyFile', this.publicKeyFile);
                    }
                    var securityKeyResource = {
                        id : null,
                        name : this.newSecurityKeyResource.name,
                        type : this.newSecurityKeyResource.type,
                        securityKey : this.newSecurityKeyResource.securityKey,
                        cipherAlgorithm : this.newSecurityKeyResource.cipherAlgorithm,
                        privateKeyFile : null,
                        privateKeyFileName : null,
                        publicKeyFile : null,
                        publicKeyFileName : null,
                        privateKeyPwd : this.newSecurityKeyResource.privateKeyPwd,
                        tenantId : this.newSecurityKeyResource.tenantId,
                        systemId : this.newSecurityKeyResource.systemId
                    };
                    if(this.newSecurityKeyResource.privateKeyFile != null && this.privateKeyFile != null) {
                        securityKeyResource.privateKeyFileName = this.privateKeyFile.name;
                    }
                    if(this.newSecurityKeyResource.publicKeyFile != null && this.publicKeyFile != null) {
                        securityKeyResource.publicKeyFileName = this.publicKeyFile.name;
                    }
                    formData.append('securityKeyResource', JSON.stringify(securityKeyResource));
                    axiosClient.post(url,formData, {headers:{'Content-Type':'multipart/form-data'}}).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`新增安全密钥资源成功`);
                            this.addNewSecurityKeyResourceToList(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        privateKeyFileChanged() {
            var files = document.querySelector('#privateKeyFile').files;
            if(files != null && files.length > 0) {
                this.privateKeyFile = files[0];
            }else {
                this.privateKeyFile = null;
            }
        },
        publicKeyFileChanged() {
            var files = document.querySelector('#publicKeyFile').files;
            if(files != null && files.length > 0) {
                this.publicKeyFile = files[0];
            }else {
                this.publicKeyFile = null;
            }
        }
    }
}
</script>