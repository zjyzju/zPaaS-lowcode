<!-- 本地方法调用节点-节点配置信息-编辑页 -->

<template v-if="showNodeCfgEdit == 'LocalInvokeNode' && nodeCfgJson != null && nodeCfgJson.isListResult != null">
	<el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="调用对象类型">
                <el-select v-model="nodeCfgJson.objectType" class="m-2" placeholder="Select" size="small" clearable>
                    <el-option v-for="item in managedObjectTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="调用对象">
                <el-input type="hidden" v-model="nodeCfgJson.objectId" readonly size="small" />
                <el-input v-model="objectCode" readonly size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="selectObjectId()" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearObjectId()" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="调用对象Key">
                <el-input v-model="nodeCfgJson.instanceKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="调用方法">
                <el-select v-model="nodeCfgJson.operationId" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in operationList" :key="item.id" :label="item.code" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            &nbsp;&nbsp;<el-checkbox v-model="nodeCfgJson.asyncInvoke" label="异步调用" />
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="参数实例来源">
                <el-select v-model="nodeCfgJson.inParamInstanceSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in paramObjectSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(paramObjectSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.inParamInstanceSource=contextType;this.nodeCfgJson.inParamInstanceKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="参数实例Key">
                <el-input v-model="nodeCfgJson.inParamInstanceKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="执行结果类型">
                <el-select v-model="nodeCfgJson.nodeResultType" class="m-2" placeholder="Select" size="small" clearable>
                    <el-option v-for="item in nodeResultTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            &nbsp;&nbsp;<el-checkbox v-model="nodeCfgJson.isListResult" label="是否列表" />
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="执行结果对象">
                <el-input v-model="nodeCfgJson.nodeResultClass" v-if="nodeCfgJson.nodeResultType=='J'" size="small" />
                <el-input type="hidden" v-model="nodeCfgJson.nodeResultClass" readonly v-if="nodeCfgJson.nodeResultType!='J'" size="small" />
                <el-input v-model="nodeResultCode" readonly v-if="nodeCfgJson.nodeResultType!='J'" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8" v-if="nodeCfgJson.nodeResultType!='J'">
            <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="selectNodeResultClass()" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearNodeResultClass()" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
    </el-row>
    
    
<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideDomainObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>
<!-- 选择值传递对象信息 -->
<valueObjectSelect v-if="showValueObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideValueObjectSelectPage="hideValueObjectSelectPage" :showValueObjectSelect="showValueObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>


<!-- 选择调用领域对象信息 -->
<domainObjectSelect v-if="showTargetDomainObjectSelect"  @managedObjectSelected="targetManagedObjectSelected" @hideDomainObjectSelectPage="hideTargetDomainObjectSelectPage" :showDomainObjectSelect="showTargetDomainObjectSelect"  :managedObjectsForSelect="targetManagedObjectsForSelect"/>
<!-- 选择调用服务对象信息 -->
<serviceObjectSelect v-if="showTargetServiceObjectSelect"  @managedObjectSelected="targetManagedObjectSelected" @hideServiceObjectSelectPage="hideTargetServiceObjectSelectPage" :showServiceObjectSelect="showTargetServiceObjectSelect"  :managedObjectsForSelect="targetManagedObjectsForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';

import domainObjectSelect from '../../domainModel/workbench-domainObjectSelect.vue'
import serviceObjectSelect from '../../domainModel/workbench-serviceObjectSelect.vue'
import valueObjectSelect from '../../domainModel/workbench-valueObjectSelect.vue'

export default {
    props: ['showNodeCfgEdit','nodeCfgJson','systemId'],
    
    emits: ['updateNodeCfgJson','showAvailableDataSelectPage'],
        
    setup (props, {attrs, emit, slots}) {
        const updateNodeCfgJson = (json)=> {
            emit('updateNodeCfgJson', json);
        };

        const showAvailableDataSelectPage = (contextTypes, dataSelectedHandler)=> {
            emit('showAvailableDataSelectPage', contextTypes, dataSelectedHandler);
        };
        
        return {
        	updateNodeCfgJson,
            showAvailableDataSelectPage
        };
    },
    components: {
    	domainObjectSelect,
        valueObjectSelect,
        serviceObjectSelect
    },
    data() {            
    	const paramObjectSourceOptions = ref(null);
    	const nodeResultTypeOptions = ref(null);
        const managedObjectTypeOptions = ref(null);
        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        
        const showTargetDomainObjectSelect = ref(false);
        const targetManagedObjectsForSelect = ref(null);
        const showTargetServiceObjectSelect = ref(false);
        
        const operationList = ref([]);
        
        const objectCode = ref("");
        const nodeResultCode = ref("");
        
        return {
        	paramObjectSourceOptions,
        	nodeResultTypeOptions,
        	managedObjectTypeOptions,
        	
        	showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,
            
            showTargetDomainObjectSelect,
            showTargetServiceObjectSelect,
            targetManagedObjectsForSelect,
            
            operationList,
            
            objectCode,
            nodeResultCode
        }
    },
    watch: {    	
    	'nodeCfgJson': {
    		handler: function(val, old){
    			this.updateNodeCfgJson(val);
    		},
    		deep:true // 深度监听的参数
    	},
        'nodeCfgJson.objectType': function(val, old){
        	this.nodeCfgJson.objectId = "";
        	this.objectCode = "";
            this.nodeCfgJson.instanceKey = "";
            this.nodeCfgJson.operationId = "";
            this.nodeCfgJson.objectSubType = "";
            this.operationList = [];
        },
        'nodeCfgJson.nodeResultType': function(val, old){
            this.nodeCfgJson.nodeResultClass = "";
            this.nodeResultCode = "";
        }
    
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_SOURCE_TYPE_THREE','OBJECT_TYPE_HAS_OPERATION','OBJECT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.paramObjectSourceOptions = data.data['OBJECT_SOURCE_TYPE_THREE'];
                this.managedObjectTypeOptions = data.data['OBJECT_TYPE_HAS_OPERATION'];
                this.nodeResultTypeOptions = data.data['OBJECT_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	if(this.nodeCfgJson.objectType != null && this.nodeCfgJson.objectId != null && this.nodeCfgJson.objectId != "") {
    		var url = "/lcdp-proxy/lowcode/platform/be/api/operation/list/" + this.nodeCfgJson.objectType + "/" + this.nodeCfgJson.objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) { 
                    this.operationList = data.data;
                }
            });
            
            var queryCodeUrl = "/lcdp-proxy/lowcode/platform/be/api/managedObject/queryObjectCode/" + this.nodeCfgJson.objectType + "/" + this.nodeCfgJson.objectId;
            axiosClient.get(queryCodeUrl).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.objectCode = data.data;
                }
            });
    	}
    	
    	if((this.nodeCfgJson.nodeResultType == "D" || this.nodeCfgJson.nodeResultType == "V")  && this.nodeCfgJson.nodeResultClass != null && this.nodeCfgJson.nodeResultClass != "") {
            var queryCodeUrl = "/lcdp-proxy/lowcode/platform/be/api/managedObject/queryObjectCode/" + this.nodeCfgJson.nodeResultType + "/" + this.nodeCfgJson.nodeResultClass;
            axiosClient.get(queryCodeUrl).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.nodeResultCode = data.data;
                }
            });
        }
    	
    },     
    methods: {
    	selectObjectId() {
    		if("D" == this.nodeCfgJson.objectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.targetManagedObjectsForSelect = data.data;
                        this.showTargetDomainObjectSelect = true;
                    }
                });
            }else if("S" == this.nodeCfgJson.objectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/serviceObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.targetManagedObjectsForSelect = data.data;
                        this.showTargetServiceObjectSelect = true;
                    }
                });
            }else {
                return;
            }
    	},
    	clearObjectId() {
    		this.nodeCfgJson.objectId = "";
    		this.objectCode = "";
    		this.nodeCfgJson.instanceKey = "";
    		this.nodeCfgJson.operationId = "";
    		this.nodeCfgJson.objectSubType = "";
    		this.operationList = [];
    	},
    	targetManagedObjectSelected(obj) {
            this.nodeCfgJson.objectId = obj.id;
            this.objectCode = obj.code;
            this.nodeCfgJson.instanceKey = "";
            this.nodeCfgJson.operationId = "";
            if(obj.objectType != null) {
            	this.nodeCfgJson.objectSubType = obj.objectType;
            }else {
            	this.nodeCfgJson.objectSubType = obj.serviceType;
            }
            
            this.targetManagedObjectsForSelect = null;
            this.showTargetDomainObjectSelect = false;
            this.showTargetServiceObjectSelect = false;
            
            var url = "/lcdp-proxy/lowcode/platform/be/api/operation/list/" + this.nodeCfgJson.objectType + "/" + this.nodeCfgJson.objectId;
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
        },
    	selectNodeResultClass() {
            if("D" == this.nodeCfgJson.nodeResultType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedObjectsForSelect = data.data;
                        this.showDomainObjectSelect = true;
                    }
                });
            }else if("V" == this.nodeCfgJson.nodeResultType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedObjectsForSelect = data.data;
                        this.showValueObjectSelect = true;
                    }
                });
            }else {
                return;
            }
        },
        clearNodeResultClass(){
            this.nodeCfgJson.nodeResultClass = "";
            this.nodeResultCode = "";
        },
        managedObjectSelected(obj) {
            this.nodeCfgJson.nodeResultClass = obj.id;
            this.nodeResultCode = obj.code;
            this.managedObjectsForSelect = null;
            this.showDomainObjectSelect = false;
            this.showValueObjectSelect = false;
        },
        hideDomainObjectSelectPage(){
             this.managedObjectsForSelect = null;
             this.showDomainObjectSelect = false;
        },
        hideValueObjectSelectPage(){
            this.managedObjectsForSelect = null;
            this.showValueObjectSelect = false;
        }
    }
}
</script>

