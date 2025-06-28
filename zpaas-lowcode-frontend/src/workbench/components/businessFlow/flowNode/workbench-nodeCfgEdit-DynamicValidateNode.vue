<!-- DynamicValidateNode-节点配置信息-编辑页 -->

<template v-if="showNodeCfgEdit == 'DynamicValidateNode' && nodeCfgJson != null && nodeCfgJson.isListResult != null">
	<el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="目标对象来源">
                <el-select v-model="nodeCfgJson.targetObjectInstanceSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="3">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.targetObjectInstanceSource=contextType;this.nodeCfgJson.targetObjectInstanceKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
        <el-col :span="5">
            <el-checkbox v-model="nodeCfgJson.isListType" label="是否列表" />
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="目标对象Key">
                <el-input v-model="nodeCfgJson.targetObjectInstanceKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="目标对象属性">
                <el-input v-model="nodeCfgJson.targetObjectInstanceAttr" size="small" />
            </el-form-item>
        </el-col>
    </el-row>

    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="动态映射">
                <el-input type="hidden" v-model="nodeCfgJson.dynamicMappingId" size="small" />
                <el-input v-model="dynamicMappingName" readonly size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8" >
            <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="selectDynamicMapping()" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearDynamicMapping()" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="关键值来源">
                <el-select v-model="nodeCfgJson.keyValueSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.keyValueSource=contextType;this.nodeCfgJson.keyValueKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="关键值Key">
                <el-input v-model="nodeCfgJson.keyValueKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="关键值属性">
                <el-input v-model="nodeCfgJson.keyValueAttr" size="small" />
            </el-form-item>
        </el-col>
    </el-row>

    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="是否中断执行">
                <el-checkbox v-model="nodeCfgJson.interruptBusinessFlow" label="中断执行" />
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
<!-- 选择动态映射信息 -->
<dynamicMappingQuerySelect v-if="showDynamicMappingQuerySelect" :queryDynamicMappingCondition="queryDynamicMappingCondition" :showDynamicMappingSelect="showDynamicMappingQuerySelect" @hideDynamicMappingSelectPage="hideDynamicMappingSelectPage" @dynamicMappingSelected="dynamicMappingSelected" :systemId="systemId"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';

import domainObjectSelect from '../../domainModel/workbench-domainObjectSelect.vue'
import valueObjectSelect from '../../domainModel/workbench-valueObjectSelect.vue'
import dynamicMappingQuerySelect from '../../dynamicMapping/workbench-dynamicMappingQuerySelect.vue';

export default {
    props: ['showNodeCfgEdit','nodeCfgJson','systemId','tenantId'],
    
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
        dynamicMappingQuerySelect
    },
    data() {            
    	const objectSourceOptions = ref(null);
    	
       const nodeResultTypeOptions = ref(null);

       
        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);

        const showDynamicMappingQuerySelect = ref(false);
        const queryDynamicMappingCondition = ref({"mappingType":"V"});
        
        const dynamicMappingName = ref("");
        const nodeResultCode = ref("");
        
        return {
        	objectSourceOptions,
        	nodeResultTypeOptions,
        	
        	showDynamicMappingQuerySelect,
        	queryDynamicMappingCondition,
        	
        	showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,

            dynamicMappingName,
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
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_SOURCE_TYPE','OBJECT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectSourceOptions = data.data['OBJECT_SOURCE_TYPE'];
                this.nodeResultTypeOptions = data.data['OBJECT_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	if(this.nodeCfgJson.paramRules == null) {
    		this.nodeCfgJson.paramRules = [];
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
        
        if(this.nodeCfgJson.dynamicMappingId != null && this.nodeCfgJson.dynamicMappingId.trim().length > 0) {
            var queryCodeUrl = "/lcdp-proxy/lowcode/platform/be/api/dynamicMapping/queryName/" + this.nodeCfgJson.dynamicMappingId;
            axiosClient.get(queryCodeUrl).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dynamicMappingName = data.data;
                    if(this.dynamicMappingName == null || this.dynamicMappingName.trim().length == 0) {
                        this.dynamicMappingName = this.nodeCfgJson.dynamicMappingId;
                    }
                }
            });
        }
    },     
    methods: {
        selectDynamicMapping() {
            this.showDynamicMappingQuerySelect = true;
        },
        hideDynamicMappingSelectPage() {
            this.showDynamicMappingQuerySelect = false;
        },
        dynamicMappingSelected(mappings) {
        	this.nodeCfgJson.dynamicMappingId = mappings.id;
        	this.dynamicMappingName = mappings.name;
        	
            this.showDynamicMappingQuerySelect = false;
        },
        clearDynamicMapping(){
            this.nodeCfgJson.dynamicMappingId = "";
            this.dynamicMappingName = "";
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
            this.nodeResultCode = obj.name;
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

