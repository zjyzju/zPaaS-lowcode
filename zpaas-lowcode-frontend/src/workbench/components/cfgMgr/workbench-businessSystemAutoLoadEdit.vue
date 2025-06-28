<!-- 编辑系统信息 -->
<template>
<el-dialog v-model="showFlag" title="编辑系统自动加载信息">
    <template #header>
        <span class="title">编辑系统自动加载信息</span>
    </template>
    <el-form  :model="businessSystemAutoLoad" label-width="120px" :rules="validateRules" ref="businessSystemAutoLoadForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="业务系统标识" required prop="systemId">
                    <el-input v-model="businessSystemAutoLoad.systemId" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="业务系统名称">
                    <el-input v-model="businessSystemAutoLoad.systemName"  size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
        
        <el-row :gutter="4">
            <el-col :span="9">
                <el-form-item label="自动加载服务"  required prop="autoLoadService">
                    <el-input v-model="businessSystemAutoLoad.autoLoadService" type="hidden" size="small"/>
                    <el-input v-model="businessSystemAutoLoad.autoLoadServiceCode" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="3">
                <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectObjectId()" ><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearObjectId()" ><label>清空</label></el-link>                                                
                </el-form-item>
            
            </el-col>  
            <el-col :span="12">
                <el-form-item label="服务方法" required prop="autoLoadMethod">
                    <el-select v-model="businessSystemAutoLoad.autoLoadMethod" clearable class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in operationList" :key="item.id" :label="item.code" :value="item.id" />
                    </el-select>
                </el-form-item>
            </el-col>                             
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideBusinessSystemAutoLoadEditPage()">取消</el-button>
            <el-button type="primary" @click="saveBusinessSystemAutoLoad()">确定</el-button>
        </span>
    </template>
</el-dialog>
<!-- 选择调用服务对象信息 -->
<serviceObjectSelect v-if="showTargetServiceObjectSelect"  @managedObjectSelected="targetManagedObjectSelected" @hideServiceObjectSelectPage="hideTargetServiceObjectSelectPage" :showServiceObjectSelect="showTargetServiceObjectSelect"  :managedObjectsForSelect="targetManagedObjectsForSelect"/>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import serviceObjectSelect from '../domainModel/workbench-serviceObjectSelect.vue'


export default {
    props: ['businessSystemAutoLoad', 'showBusinessSystemAutoLoadEdit'],
    
    emits: [ 'hideBusinessSystemAutoLoadEditPage', 'updateBusinessSystemAutoLoadToInfo'],
    
    setup(props, {attrs, emit, slots}) {
        const hideBusinessSystemAutoLoadEditPage = () => {
            emit('hideBusinessSystemAutoLoadEditPage');
        };
        
        const updateBusinessSystemAutoLoadToInfo = (system) => {
        	emit('updateBusinessSystemAutoLoadToInfo', system);
        };
        
        return {
        	hideBusinessSystemAutoLoadEditPage,
        	updateBusinessSystemAutoLoadToInfo
        }
    },
    
    components: {
    	serviceObjectSelect
    },
    
    computed: {
        showFlag: {
            get() {
                return this.showBusinessSystemAutoLoadEdit
            },
            set(value) {
                this.hideBusinessSystemAutoLoadEditPage();
            }
        }
    },
    data() {
    	const targetManagedObjectsForSelect = ref(null);
        const showTargetServiceObjectSelect = ref(false);
        
        const operationList = ref([]);

        const validateRules = ref({
            "systemId": [
                { required: true, message: '业务系统标识不能为空！', trigger: 'blur' }
            ],
            "autoLoadService": [
                { required: true, message: '自动加载服务不能为空！', trigger: 'blur' }
            ],
            "autoLoadMethod": [
                { required: true, message: '服务方法不能为空！', trigger: 'blur' }
            ]
        });
    	
        return {
        	showTargetServiceObjectSelect,
            targetManagedObjectsForSelect,
            
            operationList,
            validateRules
        }
    },
    mounted() {
    	if(this.businessSystemAutoLoad.autoLoadService != null && this.businessSystemAutoLoad.autoLoadService != "") {
            var url = "/lcdp-proxy/lowcode/platform/be/api/serviceObject/byId/" + this.businessSystemAutoLoad.autoLoadService;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.businessSystemAutoLoad.autoLoadServiceCode = data.data.code;
                }              
            });
            var url = "/lcdp-proxy/lowcode/platform/be/api/operation/list/S/" + this.businessSystemAutoLoad.autoLoadService;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.operationList = data.data;
                }
            });
        }
    },
    
    methods: {
    	saveBusinessSystemAutoLoad() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/businessSystemAutoLoad/save";
            this.$refs.businessSystemAutoLoadForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url, this.businessSystemAutoLoad).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`修改系统自动加载信息成功`);
                            this.updateBusinessSystemAutoLoadToInfo(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        selectObjectId() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/serviceObject/list/" + this.businessSystemAutoLoad.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.targetManagedObjectsForSelect = data.data;
                    this.showTargetServiceObjectSelect = true;
                }
            });
        },
        clearObjectId() {
            this.businessSystemAutoLoad.autoLoadService = "";
            this.businessSystemAutoLoad.autoLoadServiceCode = "";
            this.businessSystemAutoLoad.autoLoadMethod = "";
            this.operationList = [];
        },
        targetManagedObjectSelected(obj) {
            this.businessSystemAutoLoad.autoLoadService = obj.id;
            this.businessSystemAutoLoad.autoLoadServiceCode = obj.code;
            this.businessSystemAutoLoad.autoLoadMethod = "";
            
            this.targetManagedObjectsForSelect = null;
            this.showTargetServiceObjectSelect = false;
            
            var url = "/lcdp-proxy/lowcode/platform/be/api/operation/list/S/" + this.businessSystemAutoLoad.autoLoadService;
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
