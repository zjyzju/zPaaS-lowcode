<!-- 编辑定时任务 -->
<template>
<el-form v-if="cronJob != null" :model="cronJob" label-width="120px" :rules="validateRules" ref="cronJobForm">
    <el-row :gutter="4">
       <el-col :span="24">
           <el-breadcrumb separator=">">
               <el-breadcrumb-item><span class="title">定时任务</span></el-breadcrumb-item>
               <el-breadcrumb-item>定时任务</el-breadcrumb-item>
               <el-breadcrumb-item ><span class="title1">{{cronJob.name}}</span></el-breadcrumb-item>
            </el-breadcrumb>
       </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="标识" required prop="id">
                <el-input v-model="cronJob.id" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="编码" required prop="code">
                <el-input v-model="cronJob.code"  size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="名称" required prop="name">
                <el-input v-model="cronJob.name"  size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="任务类型" required prop="jobType">
                <el-select v-model="cronJob.jobType" class="m-2" clearable placeholder="Select" size="small">
                       <el-option v-for="item in jobTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">              
            <el-form-item label="定时设置" required prop="jobSchedule" v-if="cronJob.jobType == 'Q'">
                <el-input v-model="cronJob.jobSchedule"  size="small"/>
            </el-form-item>
        </el-col>  
        <el-col :span="8">              
            
        </el-col>  
    </el-row>
	<el-row :gutter="4">
	    <el-col :span="6">
	        <el-form-item label="任务服务对象" required prop="jobHandlerService">
	            <el-input type="hidden" v-model="cronJob.jobHandlerService" readonly size="small" />
	            <el-input v-model="jobHandlerServiceCode" readonly  size="small" />
	        </el-form-item>
	    </el-col>
	    <el-col :span="2" >
	        <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="selectServiceObject" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearServiceObject" ><label>清空</label></el-link>                                                
            </el-form-item>
	    </el-col>
	    <el-col :span="8">
            <el-form-item label="任务服务方法" required prop="jobHandlerMethod">
                <el-select v-model="cronJob.jobHandlerMethod" class="m-2" clearable placeholder="Select" size="small">
                       <el-option v-for="item in operations" :key="item.id" :label="item.code" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">              
            
        </el-col>  
	</el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="任务配置信息" prop="jobCfg">
                <el-input type="textarea" rows="3" v-model="cronJob.jobCfg"  size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="描述" prop="description">
                <el-input type="textarea" rows="3" v-model="cronJob.description"  size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
	<el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="">入参格式要求：jobContext: JSONObject</el-form-item>
        </el-col>
    </el-row>
</el-form>
    
    <br>
    <el-row :gutter="4">
        <el-col :span="20" class="toolbar-right">
            <el-button type="primary" size="small" @click="saveCronJob()">保存</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="deleteCronJob()">删除</el-button>
        </el-col>
        <el-col :span="4" class="toolbar-right">&nbsp;</el-col>
    </el-row>
<!-- 选择调用服务对象信息 -->
<serviceObjectSelect v-if="showServiceObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideServiceObjectSelectPage="hideServiceObjectSelectPage" :showServiceObjectSelect="showServiceObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'

import serviceObjectSelect from '../domainModel/workbench-serviceObjectSelect.vue'

export default {
    props: [],
    
    emits: ['updateToList', 'deleteFromList'],
    
    setup (props, {attrs, emit, slots}) {
        const updateCronJobToList = (cronJob)=> {
            emit('updateToList', 'cronJob', cronJob);
        };
        
        const deleteCronJobFromList = (cronJob)=> {
            emit('deleteFromList', 'cronJob', cronJob);
        };
        
        return {
        	updateCronJobToList,
        	deleteCronJobFromList
        };
    },
    components: {
    	serviceObjectSelect
    },
    data() {   
        const cronJob = ref(null);
        const router = useRoute();
        
    	const jobTypeOptions = ref(null);
    	
    	const managedObjectsForSelect = ref(null);
        const showServiceObjectSelect = ref(false);
        
        const jobHandlerServiceCode = ref("");
        
        const operations = ref([]);
        
        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "code": [
                { required: true, message: '编码不能为空！', trigger: 'blur' }
            ],
            "jobType": [
                { required: true, message: '任务类型不能为空！', trigger: 'blur' }
            ],
            "jobHandlerService": [
                { required: true, message: '任务执行器服务对象不能为空！', trigger: 'blur' }
            ],
            "jobHandlerMethod": [
                { required: true, message: '任务执行器服务方法不能为空！', trigger: 'blur' }
            ]
        });
    	
    	return {
            cronJob,
            router,
        	jobTypeOptions,
        	
        	showServiceObjectSelect,
            managedObjectsForSelect,
            
            jobHandlerServiceCode,
            
            operations,
            
            validateRules
        }
    },
    watch: {
    	'cronJob.jobHandlerService': function(val, old){
            if(val == null || old == null) {
                return;
            }
            if(this.cronJob.jobHandlerService != null && this.cronJob.jobHandlerService.trim().length > 0) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/serviceObject/byId/" + this.cronJob.jobHandlerService;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.jobHandlerServiceCode = data.data.code;
                    }
                });
                
                var url = "/lcdp-proxy/lowcode/platform/be/api/operation/listNoParam/S/" + this.cronJob.jobHandlerService;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.operations = data.data;
                    }
                });
            }
        },
        'router.query': function(val, old){
            if(val != null && val.objectId != null && (old == null || val.objectId != old.objectId)) {
                setTimeout(()=>{
                    this.loadCronJob();
                },100);
            }else if(val == null || val.objectId == null) {
                this.cronJob = null;
            }
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['CRON_JOB_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.jobTypeOptions = data.data['CRON_JOB_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	this.loadCronJob();
    },
    methods: {
        loadCronJob() {
            var objectId = this.router.query.objectId;
            var systemId = this.router.query.systemId;
            if(objectId == null) {
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/cronJob/byId/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.cronJob = data.data;
                    
                    if(this.cronJob.jobHandlerService != null && this.cronJob.jobHandlerService.trim().length > 0) {
                        var url = "/lcdp-proxy/lowcode/platform/be/api/serviceObject/byId/" + this.cronJob.jobHandlerService;
                        axiosClient.get(url).then((response) => {
                            var data = response.data; 
                            if(data != null && data.status == "200" && data.data != null) {
                                this.jobHandlerServiceCode = data.data.code;
                            }
                        });
                        
                        var url = "/lcdp-proxy/lowcode/platform/be/api/operation/listNoParam/S/" + this.cronJob.jobHandlerService;
                        axiosClient.get(url).then((response) => {
                            var data = response.data; 
                            if(data != null && data.status == "200" && data.data != null) {
                                this.operations = data.data;
                            }
                        });
                    }
                }
            });
        },
    	saveCronJob() {
        	var url = "/lcdp-proxy/lowcode/platform/be/api/cronJob/save";
            this.$refs.cronJobForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url, this.cronJob).then((response) => {
                        var data = response.data;
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            this.updateCronJobToList(data.data);
                            ElMessage(`修改定时任务服务(`+ this.cronJob.name +`)成功。`);
                        }
                    }).catch((ex)=>{
                        ElMessage.error(`修改定时任务服务(`+ this.cronJob.name +`)失败！` + ex);
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        deleteCronJob() {
            ElMessageBox.confirm(
                '该定时任务服务(' + this.cronJob.name + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/be/api/cronJob/delete/" + this.cronJob.id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         this.deleteCronJobFromList(this.cronJob);
                         
                         ElMessage(`删除定时任务服务(`+ this.cronJob.name +`)成功。`);
                     }
                 }).catch(()=>{
                     ElMessage.error(`删除定时任务服务(`+ this.cronJob.name +`)失败！`);
                 });
             }).catch(()=>{});
        },
        selectServiceObject() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/serviceObject/list/" + this.cronJob.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.managedObjectsForSelect = data.data;
                    this.showServiceObjectSelect = true;
                }
            });
        },
        clearServiceObject() {
            this.cronJob.serviceObjectId = "";
            this.serviceObjectCode = "";
            this.cronJob.operationId = "";
            this.operations = [];
        },
        managedObjectSelected(obj) {
            this.cronJob.jobHandlerService = obj.id;
            this.jobHandlerServiceCode = obj.code;
            this.managedObjectsForSelect = null;
            this.showServiceObjectSelect = false;
            this.cronJob.jobHandlerMethod = "";
            
            var url = "/lcdp-proxy/lowcode/platform/be/api/operation/list/S/" + this.cronJob.jobHandlerService;
            axiosClient.get(url).then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) { 
                    this.operations = data.data;
                }
            });
        },
        hideServiceObjectSelectPage(){
            this.managedObjectsForSelect = null;
            this.showServiceObjectSelect = false;
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
/* 输入框或下拉选框禁用时：加粗显示提示语 */
:deep(.el-input.is-disabled .el-input__inner){
    color: black ;
}
</style>