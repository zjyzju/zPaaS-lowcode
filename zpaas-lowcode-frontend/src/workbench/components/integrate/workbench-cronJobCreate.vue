<!-- 定时任务新建 -->
<template>
<el-dialog v-model="showFlag" title="定时任务新建">
    <template #header>
        <span class="title">定时任务新建</span>
    </template>
    <el-form  :model="cronJob" label-width="120px" :rules="validateRules" ref="cronJobForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="编码" required prop="code">
                    <el-input v-model="cronJob.code"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="cronJob.name"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="任务类型" required prop="jobType">
                    <el-select v-model="cronJob.jobType" class="m-2" clearable placeholder="Select" size="small">
                           <el-option v-for="item in jobTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="定时设置" required prop="jobSchedule" v-if="cronJob.jobType == 'Q'">
                    <el-input v-model="cronJob.jobSchedule"  size="small"/>
                </el-form-item>
            </el-col>  
        </el-row>
		<el-row :gutter="4">
	        <el-col :span="9">
	            <el-form-item label="任务服务对象" required prop="jobHandlerService">
	                <el-input type="hidden" v-model="cronJob.jobHandlerService" readonly size="small" />
	                <el-input v-model="jobHandlerServiceCode" readonly  size="small" />
	            </el-form-item>
	        </el-col>
	        <el-col :span="3" >
	            <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectServiceObject" ><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearServiceObject" ><label>清空</label></el-link>                                                
                </el-form-item>
	        </el-col>
	        <el-col :span="12">
                <el-form-item label="任务服务方法" required prop="jobHandlerMethod">
                    <el-select v-model="cronJob.jobHandlerMethod" class="m-2" clearable placeholder="Select" size="small">
                           <el-option v-for="item in operations" :key="item.id" :label="item.code" :value="item.id" />
                    </el-select>
                </el-form-item>
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
    
	<template #footer>
		<span class="dialog-footer"> 
		    <el-button @click="hideCronJobCreatePage">取消</el-button> 
		    <el-button type="primary" @click="cronJobCreateBack">上一步</el-button>
			<el-button type="primary" @click="createCronJob">确定</el-button>
		</span>
	</template>
</el-dialog>

<!-- 选择调用服务对象信息 -->
<serviceObjectSelect v-if="showServiceObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideServiceObjectSelectPage="hideServiceObjectSelectPage" :showServiceObjectSelect="showServiceObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

import serviceObjectSelect from '../domainModel/workbench-serviceObjectSelect.vue'

export default {
    props: ['cronJob','showCronJobCreate', 'businessSystem'],
    
    emits: ['cronJobCreateBack', 'hideCronJobCreatePage', 'addCronJobToList'],
    
    setup (props, {attrs, emit, slots}) {
        const addCronJobToList = (cronJob)=> {
            emit('addCronJobToList', cronJob);
        };
        
        const hideCronJobCreatePage = () => {
            emit('hideCronJobCreatePage');	
        };
        
        const cronJobCreateBack = () => {
            emit('cronJobCreateBack'); 
        };
        
        return {
        	cronJobCreateBack,
        	hideCronJobCreatePage,
        	addCronJobToList
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showCronJobCreate;
            },
            set(value) {
                this.hideCronJobCreatePage();
            }
        }
    },
    components: {
    	serviceObjectSelect
    },
    data() {            
    	const managedObjectsForSelect = ref(null);
        const showServiceObjectSelect = ref(false);
        
        const jobHandlerServiceCode = ref("");
        
        const operations = ref([]);

        const jobTypeOptions = ref(null);
        
        const validateRules = ref({
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
        	showServiceObjectSelect,
            managedObjectsForSelect,
            
            jobHandlerServiceCode,
            
            operations,
            jobTypeOptions,
            validateRules
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
    },
    methods: {	
    	createCronJob() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/cronJob/add";
            this.$refs.cronJobForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.cronJob).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            ElMessage(`创建定时任务成功！`);
                            this.addCronJobToList(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
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
            
            var url = "/lcdp-proxy/lowcode/platform/be/api/operation/listNoParam/S/" + this.cronJob.jobHandlerService;
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