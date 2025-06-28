<!-- 加解密能力工具节点-节点配置信息-编辑页 -->

<template v-if="showNodeCfgEdit == 'CipherUtilsNode' && nodeCfgJson != null && nodeCfgJson.isListResult != null">
	<el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="算法类型">
                <el-select v-model="nodeCfgJson.cipherAlgorithmType" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in cipherAlgorithmTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="算法">
                <el-select v-model="nodeCfgJson.cipherAlgorithm" class="m-2" placeholder="Select" size="small">
                    <div v-for="item in cipherAlgorithmOptions">
                        <el-option  v-if="item.value2 == nodeCfgJson.cipherAlgorithmType" :key="item.value" :label="item.label" :value="item.value" />
                    </div>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="操作类型">
                <el-select v-model="nodeCfgJson.operationType" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in operateTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="安全密钥">
                <el-select v-model="nodeCfgJson.securityKeyResourceId" class="m-2" placeholder="Select" size="small">
                    <div v-for="item in securityKeyResources">
                        <el-option  v-if="item.cipherAlgorithm == nodeCfgJson.cipherAlgorithmType" :key="item.id" :label="item.name" :value="item.id" />
                    </div>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
    
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="源文本来源">
                <el-select v-model="nodeCfgJson.srcTextSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in srcTextSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(srcTextSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.srcTextSource=contextType;this.nodeCfgJson.srcTextKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="源文本Key">
                <el-input v-model="nodeCfgJson.srcTextKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="源文本属性">
                <el-input v-model="nodeCfgJson.srcTextAttr" size="small" />
            </el-form-item>
        </el-col>
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

 </template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';

import domainObjectSelect from '../../domainModel/workbench-domainObjectSelect.vue'
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
        valueObjectSelect
    },
    data() {            
    	const cipherAlgorithmTypeOptions = ref(null);
        const cipherAlgorithmOptions = ref(null);
    	const operateTypeOptions = ref(null);
    	const srcTextSourceOptions = ref(null);
        
       const nodeResultTypeOptions = ref(null);

        const securityKeyResources = ref([]);

        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        
        const nodeResultCode = ref("");
        
        return {
        	cipherAlgorithmTypeOptions,
            cipherAlgorithmOptions,
        	operateTypeOptions,
        	nodeResultTypeOptions,

            showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,
        	
            srcTextSourceOptions,
            securityKeyResources,
            
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
        'nodeCfgJson.cipherAlgorithmType': {
    		handler: function(val, old){
    			this.nodeCfgJson.cipherAlgorithm = null;
                this.nodeCfgJson.securityKeyResourceId = null;
    		}
    	}
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['CIPHER_ALGORITHM_TYPE','CIPHER_OPERATE_TYPE','OBJECT_TYPE','OBJECT_SOURCE_TYPE_WITH_PRE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.cipherAlgorithmTypeOptions = data.data['OBJECT_SOURCE_TYPE_WITH_PRE'];
                this.operateTypeOptions = data.data['CIPHER_OPERATE_TYPE'];
                this.nodeResultTypeOptions = data.data['OBJECT_TYPE'];
                this.srcTextSourceOptions = data.data['OBJECT_SOURCE_TYPE_WITH_PRE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValuesMore", ['CIPHER_ALGORITHM_PADDING']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.cipherAlgorithmOptions = data.data['CIPHER_ALGORITHM_PADDING'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	this.loadSecurityKeyResources();
    },     
    methods: {
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
        },
        loadSecurityKeyResources() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/securityKeyResource/list/" + this.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.securityKeyResources = data.data;
                }
            });
        }
    }
}
</script>

