<!-- Aigc能力调用节点节点-节点配置信息-编辑页 -->

<template v-if="showNodeCfgEdit == 'AigcInvokeNode' && nodeCfgJson != null && nodeCfgJson.isListResult != null">
	<el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="Aigc资源">
                <el-select v-model="nodeCfgJson.aigcResourceId" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in aigcResources" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="模型名">
                <el-input v-model="nodeCfgJson.model" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="API类型">
                <el-select v-model="nodeCfgJson.invokeApi" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in apiTypesOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="流式调用">
                <el-select v-model="nodeCfgJson.isStream" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in yesOrNoOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="模型参数">
                <el-input v-model="nodeCfgJson.options" type="textarea" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="消息来源">
                <el-select v-model="nodeCfgJson.messageSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.messageSource=contextType;this.nodeCfgJson.messageKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="消息Key">
                <el-input v-model="nodeCfgJson.messageKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="消息属性">
                <el-input v-model="nodeCfgJson.messageAttr" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="历史消息来源">
                <el-select v-model="nodeCfgJson.historySource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.historySource=contextType;this.nodeCfgJson.historyKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="历史消息Key">
                <el-input v-model="nodeCfgJson.historyKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="历史消息属性">
                <el-input v-model="nodeCfgJson.historyAttr" size="small" />
            </el-form-item>
        </el-col>
    </el-row>

    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="用户名来源">
                <el-select v-model="nodeCfgJson.userNameSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.userNameSource=contextType;this.nodeCfgJson.userNameKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="用户名Key">
                <el-input v-model="nodeCfgJson.userNameKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="用户名属性">
                <el-input v-model="nodeCfgJson.userNameAttr" size="small" />
            </el-form-item>
        </el-col>
    </el-row>

    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="助手名来源">
                <el-select v-model="nodeCfgJson.assistantNameSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.assistantNameSource=contextType;this.nodeCfgJson.assistantNameKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="助手名Key">
                <el-input v-model="nodeCfgJson.assistantNameKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="助手名属性">
                <el-input v-model="nodeCfgJson.assistantNameAttr" size="small" />
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
    
    emits: ['updateNodeCfgJson', 'showAvailableDataSelectPage'],
        
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
    	const aigcResources = ref([]);
    	const apiTypesOptions = ref(null);
    	const yesOrNoOptions = ref(null);
    	const objectAttrSourceOptions = ref(null);
        
       const nodeResultTypeOptions = ref(null);
        
        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        
        const nodeResultCode = ref("");
        
        return {
        	aigcResources,
        	apiTypesOptions,
        	yesOrNoOptions,
        	nodeResultTypeOptions,
        	showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,
            
            objectAttrSourceOptions,
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
        'nodeCfgJson.nodeResultType': function(val, old){
            this.nodeCfgJson.nodeResultClass = "";
            this.nodeResultCode = "";
        }
    
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_SOURCE_TYPE_WITH_PRE','PUB_YES_OR_NO','AIGC_API_TYPE','OBJECT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectAttrSourceOptions = data.data['OBJECT_SOURCE_TYPE_WITH_PRE'];
                this.yesOrNoOptions = data.data['PUB_YES_OR_NO'];
                this.apiTypesOptions = data.data['AIGC_API_TYPE'];
                this.nodeResultTypeOptions = data.data['OBJECT_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	var url = "/lcdp-proxy/lowcode/platform/be/api/serverResource/list/" + this.systemId + "/A";
        axiosClient.get(url).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.aigcResources = data.data;
                if(this.aigcResources != null && this.aigcResources.length > 0 && (this.nodeCfgJson.aigcResourceId == null || this.nodeCfgJson.aigcResourceId == "") ) {
                    this.nodeCfgJson.aigcResourceId = this.aigcResources[0].id;
                }
            }
        });
        
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

