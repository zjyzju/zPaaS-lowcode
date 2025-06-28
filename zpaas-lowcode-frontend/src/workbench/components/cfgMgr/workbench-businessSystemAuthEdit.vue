<!-- 编辑系统认证信息 -->
<template>
<el-dialog v-model="showFlag" title="编辑系统认证信息" width="800px">
    <template #header>
        <span class="title">编辑系统认证信息</span>
    </template>
    <el-form  :model="businessSystemAuth" label-width="120px" :rules="validateRules" ref="businessSystemAuthForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="标识">
                    <el-input v-model="businessSystemAuth.id" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="归属业务系统" required prop="systemId">
                    <el-input v-model="businessSystemAuth.systemId" type="hidden"  size="small"/>
                    <el-input v-model="businessSystemAuth.systemName" readonly  size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="认证方式" required prop="authType">
                    <el-select v-model="businessSystemAuth.authType" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in authTypeOptions"
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
                <el-form-item label="用户信息Key">
                    <el-input v-model="businessSystemAuth.userInfoKey"  size="small"/>
                </el-form-item>
            </el-col>  
            <el-col :span="12">              
                <el-form-item label="用户标识属性">
                    <el-input v-model="businessSystemAuth.userIdAttr"  size="small"/>
                </el-form-item>
            </el-col>                     
        </el-row>
        <el-row :gutter="4">
            <el-col :span="9">
                <el-form-item label="用户信息服务">
                    <el-input v-model="businessSystemAuth.userInfoService" type="hidden" size="small"/>
                    <el-input v-model="businessSystemAuth.userInfoServiceCode" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="3">
	            <el-form-item label="" label-width="10px">
	                <el-link  type="primary" @click="selectObjectId()" ><label>选择</label></el-link>&nbsp;
	                <el-link  type="primary" @click="clearObjectId()" ><label>清空</label></el-link>                                                
	            </el-form-item>
	        
            </el-col>  
            <el-col :span="12">
                <el-form-item label="用户信息方法">
                    <el-select v-model="businessSystemAuth.userInfoMethod" clearable class="m-2" placeholder="Select" size="small">
	                    <el-option v-for="item in operationList" :key="item.id" :label="item.code" :value="item.id" />
	                </el-select>
                </el-form-item>
            </el-col>                             
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="认证配置信息">
                    <el-input type="textarea" :rows="4" v-model="businessSystemAuth.authConfig"  size="small"/>
                </el-form-item>
            </el-col>      
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="忽略URL">
                    <el-input type="textarea" :rows="4" v-model="businessSystemAuth.ignoreUrls"  size="small"/>
                </el-form-item>
            </el-col>      
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideBusinessSystemAuthEditPage()">取消</el-button>
            <el-button type="primary" @click="saveBusinessSystemAuth()">确定</el-button>
        </span>
    </template>
</el-dialog>

<!-- 选择调用服务对象信息 -->
<serviceObjectSelect v-if="showTargetServiceObjectSelect"  @managedObjectSelected="targetManagedObjectSelected" @hideServiceObjectSelectPage="hideTargetServiceObjectSelectPage" :showServiceObjectSelect="showTargetServiceObjectSelect"  :managedObjectsForSelect="targetManagedObjectsForSelect"/>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

import serviceObjectSelect from '../domainModel/workbench-serviceObjectSelect.vue'

export default {
    props: ['businessSystemAuth', 'showBusinessSystemAuthEdit'],
    
    emits: [ 'hideBusinessSystemAuthEditPage', 'updateBusinessSystemAuthToInfo'],
    
    setup(props, {attrs, emit, slots}) {
        const hideBusinessSystemAuthEditPage = () => {
            emit('hideBusinessSystemAuthEditPage');
        };
        
        const updateBusinessSystemAuthToInfo = (system) => {
        	emit('updateBusinessSystemAuthToInfo', system);
        };
        
        return {
        	hideBusinessSystemAuthEditPage,
        	updateBusinessSystemAuthToInfo
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showBusinessSystemAuthEdit;
            },
            set(value) {
                this.hideBusinessSystemAuthEditPage();
            }
        }
    },
    components: {
        serviceObjectSelect
    },
    data() {
    	const authTypeOptions = ref(null);
    	
    	const targetManagedObjectsForSelect = ref(null);
        const showTargetServiceObjectSelect = ref(false);
        
        const operationList = ref([]);

        const validateRules = ref({
            "systemId": [
                { required: true, message: '归属业务系统不能为空！', trigger: 'blur' }
            ],
            "authType": [
                { required: true, message: '认证方式不能为空！', trigger: 'blur' }
            ]
        });
    	
        return {
        	authTypeOptions,
        	showTargetServiceObjectSelect,
            targetManagedObjectsForSelect,
            
            operationList,
            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['BUSINESS_SYSTEM_AUTH_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.authTypeOptions = data.data['BUSINESS_SYSTEM_AUTH_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	if(this.businessSystemAuth.userInfoService != null && this.businessSystemAuth.userInfoService != "") {
    		var url = "/lcdp-proxy/lowcode/platform/be/api/serviceObject/byId/" + this.businessSystemAuth.userInfoService;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.businessSystemAuth.userInfoServiceCode = data.data.code;
                }              
            });
            var url = "/lcdp-proxy/lowcode/platform/be/api/operation/list/S/" + this.businessSystemAuth.userInfoService;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.operationList = data.data;
                }
            });
    	}
    },
    methods: {
    	saveBusinessSystemAuth() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/businessSystemAuth/save";
            this.$refs.businessSystemAuthForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url, this.businessSystemAuth).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            ElMessage(`修改业务系统认证信息成功`);
                            this.updateBusinessSystemAuthToInfo(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        selectObjectId() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/serviceObject/list/" + this.businessSystemAuth.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.targetManagedObjectsForSelect = data.data;
                    this.showTargetServiceObjectSelect = true;
                }
            });
        },
        clearObjectId() {
            this.businessSystemAuth.userInfoService = "";
            this.businessSystemAuth.userInfoServiceCode = "";
            this.businessSystemAuth.userInfoMethod = "";
            this.operationList = [];
        },
        targetManagedObjectSelected(obj) {
        	this.businessSystemAuth.userInfoService = obj.id;
        	this.businessSystemAuth.userInfoServiceCode = obj.code;
        	this.businessSystemAuth.userInfoMethod = "";
            
            this.targetManagedObjectsForSelect = null;
            this.showTargetServiceObjectSelect = false;
            
            var url = "/lcdp-proxy/lowcode/platform/be/api/operation/list/S/" + this.businessSystemAuth.userInfoService;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.operationList = data.data;
                }
            });
            
        },
        hideTargetDomainObjectSelectPage(){
             this.targetManagedObjectsForSelect = null;
             this.showTargetDomainObjectSelect = false;
        },
        hideTargetServiceObjectSelectPage(){
            this.targetManagedObjectsForSelect = null;
            this.showTargetServiceObjectSelect = false;
        }
    }
}

</script>
