<!-- Kafka消息消费服务新建 -->
<template>
<el-dialog v-model="showFlag" title="Kafka消息消费服务新建">
    <template #header>
        <span class="title">Kafka消息消费服务新建</span>
    </template>
    <el-form  :model="kafkaMessageConsumer" label-width="120px" :rules="validateRules" ref="kafkaMessageConsumerForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="kafkaMessageConsumer.name"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="描述">
                    <el-input v-model="kafkaMessageConsumer.description"  size="small"/>
                </el-form-item>
            </el-col>  
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
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
            <el-col :span="12">              
                <el-form-item label="消息主题" required prop="messageTopics">
                    <el-input v-model="kafkaMessageConsumer.messageTopics"  size="small"/>
                </el-form-item>
            </el-col>  
        </el-row>
		<el-row :gutter="4">
	        <el-col :span="9">
	            <el-form-item label="消费服务对象" required prop="serviceObjectId">
	                <el-input type="hidden" v-model="kafkaMessageConsumer.serviceObjectId" readonly size="small" />
	                <el-input v-model="serviceObjectCode" readonly  size="small" />
	            </el-form-item>
	        </el-col>
	        <el-col :span="3" >
	            <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectServiceObject" ><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearServiceObject" ><label>清空</label></el-link>                                                
                </el-form-item>
	        </el-col>
	        <el-col :span="12">
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
	    </el-row>
	    <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="">入参格式要求：topic: String, key: String, msg: String/JSONObject</el-form-item>
            </el-col>
        </el-row>
	</el-form>
    
	<template #footer>
		<span class="dialog-footer"> 
		    <el-button @click="hideKafkaMessageConsumerCreatePage">取消</el-button> 
		    <el-button type="primary" @click="kafkaMessageConsumerCreateBack">上一步</el-button>
			<el-button type="primary" @click="createKafkaMessageConsumer">确定</el-button>
		</span>
	</template>
</el-dialog>

<!-- 选择调用服务对象信息 -->
<serviceObjectSelect v-if="showServiceObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideServiceObjectSelectPage="hideServiceObjectSelectPage" :showServiceObjectSelect="showServiceObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'

import serviceObjectSelect from '../domainModel/workbench-serviceObjectSelect.vue'

export default {
    props: ['kafkaMessageConsumer','showKafkaMessageConsumerCreate', 'businessSystem'],
    
    emits: ['kafkaMessageConsumerCreateBack', 'hideKafkaMessageConsumerCreatePage', 'addKafkaMessageConsumerToList'],
    
    setup (props, {attrs, emit, slots}) {
        const addKafkaMessageConsumerToList = (kafkaMessageConsumer)=> {
            emit('addKafkaMessageConsumerToList', kafkaMessageConsumer);
        };
        
        const hideKafkaMessageConsumerCreatePage = () => {
            emit('hideKafkaMessageConsumerCreatePage');	
        };
        
        const kafkaMessageConsumerCreateBack = () => {
            emit('kafkaMessageConsumerCreateBack'); 
        };
        
        return {
        	kafkaMessageConsumerCreateBack,
        	hideKafkaMessageConsumerCreatePage,
        	addKafkaMessageConsumerToList
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showKafkaMessageConsumerCreate;
            },
            set(value) {
                this.hideKafkaMessageConsumerCreatePage();
            }
        }
    },
    components: {
    	serviceObjectSelect
    },
    data() {            
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
            ]
        });
        
        return {
        	showServiceObjectSelect,
            managedObjectsForSelect,
            
            serviceObjectCode,
            
            operations,
            kafkaConsumerResources,
            validateRules
        }
    },
    mounted() {
    	var url = "/lcdp-proxy/lowcode/platform/be/api/serverResource/list/" + this.kafkaMessageConsumer.systemId + "/C";
        axiosClient.get(url).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.kafkaConsumerResources = data.data;
                if(this.kafkaConsumerResources != null && this.kafkaConsumerResources.length > 0 && (this.kafkaMessageConsumer.consumerResourceId == null || this.kafkaMessageConsumer.consumerResourceId == "") ) {
                    this.kafkaMessageConsumer.consumerResourceId = this.kafkaConsumerResources[0].id;
                }
            }
        });
    },
    methods: {
    	
    	createKafkaMessageConsumer() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/messageConsumer/save";
            this.$refs.kafkaMessageConsumerForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.kafkaMessageConsumer).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            ElMessage(`创建Kafka消息消费服务成功！`);
                            this.addKafkaMessageConsumerToList(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
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
            
            var url = "/lcdp-proxy/lowcode/platform/be/api/operation/listNoParam/S/" + this.kafkaMessageConsumer.serviceObjectId;
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