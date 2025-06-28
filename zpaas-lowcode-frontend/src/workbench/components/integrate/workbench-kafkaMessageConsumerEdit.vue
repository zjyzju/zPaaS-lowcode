<!-- 编辑Kafka消息消费 -->
<template>
<el-form v-if="kafkaMessageConsumer != null" :model="kafkaMessageConsumer" label-width="120px" :rules="validateRules" ref="kafkaMessageConsumerForm">
    <el-row :gutter="4">
       <el-col :span="24">
           <el-breadcrumb separator=">">
               <el-breadcrumb-item><span class="title">异步消息处理</span></el-breadcrumb-item>
               <el-breadcrumb-item>Kafka消息消费</el-breadcrumb-item>
               <el-breadcrumb-item ><span class="title1">{{kafkaMessageConsumer.name}}</span></el-breadcrumb-item>
            </el-breadcrumb>
       </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="标识" required prop="id">
                <el-input v-model="kafkaMessageConsumer.id" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="名称" required prop="name">
                <el-input v-model="kafkaMessageConsumer.name" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="描述">
                <el-input v-model="kafkaMessageConsumer.description" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="Kafka消费端" required prop="consumerResourceId">
                <el-select v-model="kafkaMessageConsumer.consumerResourceId" class="m-2" clearable placeholder="Select" size="small">
                       <el-option
                              v-for="item in kafkaConsumerResources"
                              :key="item.id"
                              :label="item.name"
                              :value="item.id"
                        />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="16">              
            <el-form-item label="消息主题" required prop="messageTopics">
                <el-input v-model="kafkaMessageConsumer.messageTopics"  size="small"/>
            </el-form-item>
        </el-col>  
    </el-row>
    <el-row :gutter="4">
        <el-col :span="6">
            <el-form-item label="消费服务对象" required prop="serviceObjectId">
                <el-input type="hidden" v-model="kafkaMessageConsumer.serviceObjectId" readonly size="small" />
                <el-input v-model="serviceObjectCode" readonly  size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="2" >
            <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="selectServiceObject" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearServiceObject" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="消费服务方法" required prop="operationId">
                <el-select v-model="kafkaMessageConsumer.operationId" class="m-2" clearable placeholder="Select" size="small">
                       <el-option
                              v-for="item in operations"
                              :key="item.id"
                              :label="item.code"
                              :value="item.id"
                        />
                </el-select>
                </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="状态" required prop="status">
                <el-select v-model="kafkaMessageConsumer.status" class="m-2" placeholder="Select" disabled size="small">
                    <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="创建时间">
                <el-input v-model="kafkaMessageConsumer.createTime" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="更新时间">
                <el-input v-model="kafkaMessageConsumer.updateTime" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="">入参格式要求：topic: String, key: String, msg: String/JSONObject</el-form-item>
        </el-col>
    </el-row>
</el-form>
    
    <br>
    <el-row :gutter="4">
        <el-col :span="20" class="toolbar-right">
            <el-button type="primary" size="small" @click="saveKafkaMessageConsumer()">保存</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="deleteKafkaMessageConsumer()">删除</el-button>
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
        const updateKafkaMessageConsumerToList = (kafkaMessageConsumer)=> {
            emit('updateToList', 'kafkaMessageConsumer', kafkaMessageConsumer);
        };
        
        const deleteKafkaMessageConsumerFromList = (kafkaMessageConsumer)=> {
            emit('deleteFromList', 'kafkaMessageConsumer', kafkaMessageConsumer);
        };
        
        return {
        	updateKafkaMessageConsumerToList,
        	deleteKafkaMessageConsumerFromList
        };
    },
    components: {
    	serviceObjectSelect
    },
    data() {   
        const kafkaMessageConsumer = ref(null);
        const router = useRoute();
        
    	const statusOptions = ref(null);
    	
    	const managedObjectsForSelect = ref(null);
        const showServiceObjectSelect = ref(false);
        
        const serviceObjectCode = ref("");
        
        const operations = ref([]);
        const kafkaConsumerResources = ref([]);

        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "consumerResourceId": [
                { required: true, message: 'Kafka消息端不能为空！', trigger: 'blur' }
            ],
            "messageTopics": [
                { required: true, message: '消息主题不能为空！', trigger: 'blur' }
            ],
            "serviceObjectId": [
                { required: true, message: '消息服务对象不能为空！', trigger: 'blur' }
            ],
            "operationId": [
                { required: true, message: '消息服务方法不能为空！', trigger: 'blur' }
            ],
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "status": [
                { required: true, message: '状态不能为空！', trigger: 'blur' }
            ]
        });
    	
    	return {
            kafkaMessageConsumer,
            router,
        	statusOptions,
        	
        	showServiceObjectSelect,
            managedObjectsForSelect,
            
            serviceObjectCode,
            
            operations,
            kafkaConsumerResources,

            validateRules
        }
    },
    watch: {
    	'kafkaMessageConsumer.serviceObjectId': function(val, old){
            if(val == null || old == null) {
                return;
            }
            if(this.kafkaMessageConsumer.serviceObjectId != null && this.kafkaMessageConsumer.serviceObjectId.trim().length > 0) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/serviceObject/byId/" + this.kafkaMessageConsumer.serviceObjectId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.serviceObjectCode = data.data.code;
                    }
                });
                
                var url = "/lcdp-proxy/lowcode/platform/be/api/operation/listNoParam/S/" + this.kafkaMessageConsumer.serviceObjectId;
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
                    this.loadKafkaMessageConsumer();
                },100);
            }else if(val == null || val.objectId == null) {
                this.kafkaMessageConsumer = null;
            }
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_STATUS']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.statusOptions = data.data['PUB_STATUS'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	this.loadKafkaMessageConsumer();
    },
    methods: {
        loadKafkaMessageConsumer() {
            var objectId = this.router.query.objectId;
            var systemId = this.router.query.systemId;
            if(objectId == null) {
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/messageConsumer/queryMessageConsumer/" + systemId + "/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.kafkaMessageConsumer = data.data;
                    var url = "/lcdp-proxy/lowcode/platform/be/api/serverResource/list/" + systemId + "/C";
                    axiosClient.get(url).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.kafkaConsumerResources = data.data;
                            if(this.kafkaConsumerResources != null && this.kafkaConsumerResources.length > 0 && (this.kafkaMessageConsumer.consumerResourceId == null || this.kafkaMessageConsumer.consumerResourceId == "") ) {
                                this.kafkaMessageConsumer.consumerResourceId = this.kafkaConsumerResources[0].id;
                            }
                        }
                    });
                    
                    if(this.kafkaMessageConsumer.serviceObjectId != null && this.kafkaMessageConsumer.serviceObjectId.trim().length > 0) {
                        var url = "/lcdp-proxy/lowcode/platform/be/api/serviceObject/byId/" + this.kafkaMessageConsumer.serviceObjectId;
                        axiosClient.get(url).then((response) => {
                            var data = response.data; 
                            if(data != null && data.status == "200" && data.data != null) {
                                this.serviceObjectCode = data.data.code;
                            }
                        });
                        
                        var url = "/lcdp-proxy/lowcode/platform/be/api/operation/listNoParam/S/" + this.kafkaMessageConsumer.serviceObjectId;
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
    	saveKafkaMessageConsumer() {
        	var url = "/lcdp-proxy/lowcode/platform/be/api/messageConsumer/modify";
            this.$refs.kafkaMessageConsumerForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url, this.kafkaMessageConsumer).then((response) => {
                        var data = response.data;
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            this.updateKafkaMessageConsumerToList(data.data);
                            ElMessage(`修改Kafka消息消费服务(`+ this.kafkaMessageConsumer.name +`)成功。`);
                        }
                    }).catch((ex)=>{
                        ElMessage.error(`修改Kafka消息消费服务(`+ this.kafkaMessageConsumer.name +`)失败！` + ex);
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        deleteKafkaMessageConsumer() {
            ElMessageBox.confirm(
                '该Kafka消息消费服务(' + this.kafkaMessageConsumer.name + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/be/api/messageConsumer/delete/" + this.kafkaMessageConsumer.systemId + "/" + this.kafkaMessageConsumer.id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         this.deleteKafkaMessageConsumerFromList(this.kafkaMessageConsumer);
                         
                         ElMessage(`删除Kafka消息消费服务(`+ this.kafkaMessageConsumer.name +`)成功。`);
                     }
                 }).catch(()=>{
                     ElMessage.error(`删除Kafka消息消费服务(`+ this.kafkaMessageConsumer.name +`)失败！`);
                 });
             }).catch(()=>{});
        },
        selectServiceObject() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/serviceObject/list/" + this.kafkaMessageConsumer.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.managedObjectsForSelect = data.data;
                    this.showServiceObjectSelect = true;
                }
            });
        },
        clearServiceObject() {
            this.kafkaMessageConsumer.serviceObjectId = "";
            this.serviceObjectCode = "";
            this.kafkaMessageConsumer.operationId = "";
            this.operations = [];
        },
        managedObjectSelected(obj) {
            this.kafkaMessageConsumer.serviceObjectId = obj.id;
            this.serviceObjectCode = obj.code;
            this.managedObjectsForSelect = null;
            this.showServiceObjectSelect = false;
            this.kafkaMessageConsumer.operationId = "";
            
            var url = "/lcdp-proxy/lowcode/platform/be/api/operation/list/S/" + this.kafkaMessageConsumer.serviceObjectId;
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