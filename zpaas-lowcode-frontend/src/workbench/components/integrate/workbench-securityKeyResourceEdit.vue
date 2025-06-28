<!-- 编辑安全密钥资源 -->
<template>
<el-form  v-if="securityKeyResource != null && securityKeyResource.id != null" :model="securityKeyResource" label-width="130px" :rules="validateRules" ref="securityKeyResourceForm">
    <el-row :gutter="4">
       <el-col :span="24">
           <el-breadcrumb separator=">">
               <el-breadcrumb-item><span class="title">安全密钥资源</span></el-breadcrumb-item>
               <el-breadcrumb-item>安全密钥资源</el-breadcrumb-item>
               <el-breadcrumb-item ><span class="title1">{{securityKeyResource.name}}</span></el-breadcrumb-item>
            </el-breadcrumb>
       </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="标识" required prop="id">
                <el-input v-model="securityKeyResource.id" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="名称" required prop="name">
                <el-input v-model="securityKeyResource.name"  size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="类型" required prop="type">
                <el-select v-model="securityKeyResource.type" class="m-2" placeholder="Select" disabled size="small">
                    <el-option v-for="item in securityKeyTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="加密算法"  prop="cipherAlgorithm">
                <el-select v-model="securityKeyResource.cipherAlgorithm" v-if="securityKeyResource.type == 'P'" class="m-2" placeholder="Select" size="small">
                       <el-option v-for="item in asymmetricAlgorithmOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
                <el-select v-model="securityKeyResource.cipherAlgorithm" v-else class="m-2" placeholder="Select" size="small">
                       <el-option v-for="item in symmetricAlgorithmOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="16">
            <el-form-item label="密钥" prop="securityKey" v-if="securityKeyResource.type != 'P'">
                <el-input v-model="securityKeyResource.securityKey" type="password"  size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="securityKeyResource.type == 'P'">
        <el-col :span="8">
            <el-form-item label="私钥">
                <el-input v-model="securityKeyResource.privateKeyFileName" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="更新私钥">
                <el-input type="file" id="privateKeyFile" v-model="securityKeyResource.privateKeyFile" accept="application/x-pkcs12" @change="privateKeyFileChanged()" clearable size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="私钥密码">
                <el-input type="password" v-model="securityKeyResource.privateKeyPwd" size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="securityKeyResource.type == 'P'">
        <el-col :span="8">
            <el-form-item label="公钥">
                <el-input v-model="securityKeyResource.publicKeyFileName" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="更新公钥">
                <el-input type="file" id="publicKeyFile" v-model="securityKeyResource.publicKeyFile" accept="application/x-x509-ca-cert" @change="publicKeyFileChanged()" clearable size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            &nbsp;
        </el-col>
    </el-row>

    <el-row :gutter="4">
        <el-col :span="20" class="toolbar-right">
            <el-button type="primary" size="small" @click="saveSecurityKeyResource()">保存</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="deleteSecurityKeyResource()">删除</el-button>
        </el-col>
        <el-col :span="4" class="toolbar-right">&nbsp;</el-col>
    </el-row>
</el-form>
</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'


export default {
    props: [],
    
    emits: ['updateToList', 'deleteFromList'],
    
    setup (props, {attrs, emit, slots}) {
        const updateSecurityKeyResourceToList = (securityKeyResource)=> {
            emit('updateToList', 'securityKeyResource', securityKeyResource);
        };
        
        const deleteSecurityKeyResourceFromList = (securityKeyResource)=> {
            emit('deleteFromList', 'securityKeyResource', securityKeyResource);
        };
        
        return {
        	updateSecurityKeyResourceToList,
        	deleteSecurityKeyResourceFromList
        };
    },
    components: {
        
    },
    data() {            
        const securityKeyTypeOptions = ref(null);

        const symmetricAlgorithmOptions = ref(null);

        const asymmetricAlgorithmOptions = ref(null);

        const securityKeyResource = ref(null);
        const router = useRoute();

        const privateKeyFile = ref(null);
        const publicKeyFile = ref(null);

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "type": [
                { required: true, message: '类型不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
            securityKeyResource,
            router,
            privateKeyFile,
            publicKeyFile,

        	securityKeyTypeOptions,
            symmetricAlgorithmOptions,
            asymmetricAlgorithmOptions,
            validateRules,
        }
    },
    watch: {        
        'router.query': function(val, old){
            if(val != null && val.objectId != null && (old == null || val.objectId != old.objectId)) {
                setTimeout(()=>{
                    this.loadSecurityKeyResource();
                },100);
            }else if(val == null || val.objectId == null) {
                this.securityKeyResource = null;
            }
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

        this.loadSecurityKeyResource();
    },
    methods: {
        loadSecurityKeyResource() {
            var objectId = this.router.query.objectId;
            if(objectId == null) {
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/securityKeyResource/byId/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.securityKeyResource = data.data;
                }
            });
        },
    	saveSecurityKeyResource() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/securityKeyResource/save";
            this.$refs.securityKeyResourceForm.validate((valid, fields)=> {
                if(valid) {
                    var formData = new FormData();
                    if(this.securityKeyResource.privateKeyFile != null && this.privateKeyFile != null) {
                        formData.append('privateKeyFile', this.privateKeyFile);
                    }
                    if(this.securityKeyResource.publicKeyFile != null && this.publicKeyFile != null) {
                        formData.append('publicKeyFile', this.publicKeyFile);
                    }
                    var securityKeyResource = {
                        id : this.securityKeyResource.id,
                        name : this.securityKeyResource.name,
                        type : this.securityKeyResource.type,
                        securityKey : this.securityKeyResource.securityKey,
                        cipherAlgorithm : this.securityKeyResource.cipherAlgorithm,
                        privateKeyFile : null,
                        privateKeyFileName : this.securityKeyResource.privateKeyFileName,
                        publicKeyFile : null,
                        publicKeyFileName : this.securityKeyResource.publicKeyFileName,
                        privateKeyPwd : this.securityKeyResource.privateKeyPwd,
                        tenantId : this.securityKeyResource.tenantId,
                        systemId : this.securityKeyResource.systemId,
                        createTime : this.securityKeyResource.createTime,
                        updateTime : this.securityKeyResource.updateTime
                    };
                    if(this.securityKeyResource.privateKeyFile != null && this.privateKeyFile != null) {
                        securityKeyResource.privateKeyFileName = this.privateKeyFile.name;
                    }
                    if(this.securityKeyResource.publicKeyFile != null && this.publicKeyFile != null) {
                        securityKeyResource.publicKeyFileName = this.publicKeyFile.name;
                    }
                    formData.append('securityKeyResource', JSON.stringify(securityKeyResource));
                    axiosClient.post(url,formData, {headers:{'Content-Type':'multipart/form-data'}}).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.securityKeyResource = data.data;
                            this.updateSecurityKeyResourceToList(this.securityKeyResource);
                            ElMessage(`更新安全密钥资源信息成功`);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        deleteSecurityKeyResource() {
            ElMessageBox.confirm(
                '该安全密钥资源(' + this.securityKeyResource.name + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/be/api/securityKeyResource/delete/" + this.securityKeyResource.id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         this.deleteSecurityKeyResourceFromList(this.securityKeyResource);
                         
                         ElMessage(`删除安全密钥资源(`+ this.securityKeyResource.name +`)成功。`);
                     }else {
                         ElMessage.error(`删除安全密钥资源(`+ this.securityKeyResource.name +`)失败！`);
                     }
                 }).catch(()=>{
                     ElMessage.error(`删除安全密钥资源(`+ this.securityKeyResource.name +`)失败！`);
                 });
             }).catch(()=>{});
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

<style scoped>
.layout-container-main .toolbar-right {
  display: inline-flex;
  align-items: center;
  justify-content: right; 
  height: 100%;
  right: 20px;
}
.toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
  height: 100%;
  right: 20px;
}
</style>