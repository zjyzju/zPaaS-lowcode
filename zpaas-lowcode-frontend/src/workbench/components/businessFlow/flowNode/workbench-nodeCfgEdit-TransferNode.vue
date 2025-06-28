<!-- 参数转换节点-节点配置信息-编辑页 -->

<template v-if="showNodeCfgEdit == 'TransferNode' && nodeCfgJson != null && nodeCfgJson.isListResult != null">
	<el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="源对象来源">
                <el-select v-model="nodeCfgJson.fromObjectInstanceSource" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="3">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.fromObjectInstanceSource=contextType;this.nodeCfgJson.fromObjectInstanceKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
        <el-col :span="5">
            <el-checkbox v-model="nodeCfgJson.isListType" label="是否列表" />
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="源对象Key">
                <el-input v-model="nodeCfgJson.fromObjectInstanceKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="源对象属性">
                <el-input v-model="nodeCfgJson.fromObjectInstanceAttr" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="源对象类型">
                <el-select v-model="nodeCfgJson.fromObjectType" class="m-2" placeholder="Select" size="small" clearable>
                    <el-option v-for="item in managedObjectTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="源对象">
                <el-input v-model="nodeCfgJson.fromObjectClass" v-if="nodeCfgJson.fromObjectType=='J'" size="small" />
                <el-input type="hidden" v-model="nodeCfgJson.fromObjectClass" readonly v-if="nodeCfgJson.fromObjectType!='J'" size="small" />
                <el-input v-model="fromObjectCode" readonly v-if="nodeCfgJson.fromObjectType!='J'" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="selectFromObjectClass()" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearFromObjectClass()" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="目标对象来源">
                <el-select v-model="nodeCfgJson.toObjectInstanceSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in toObjectSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(toObjectSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.toObjectInstanceSource=contextType;this.nodeCfgJson.toObjectInstanceKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="目标对象Key">
                <el-input v-model="nodeCfgJson.toObjectInstanceKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
    <el-row :gutter="4" >
	    <el-col :span="16">
	        <el-form-item label="转换规则">
	            <el-input type= "hidden" v-model="nodeCfgJson.dataMappingId" readonly size="small" /> 
	            <el-input v-model="dataMappingName" readonly size="small" />                                                         
	        </el-form-item>
	    </el-col>
	    <el-col :span="8">
	        <el-form-item label="" label-width="10px">
	            <el-link  type="primary" @click="selectDataMapping('I')" ><label>设置</label></el-link>&nbsp;
	            <el-link  type="primary" @click="clearDataMapping('I')" ><label>清空</label></el-link>                                                
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
    
    
<!-- 选择数据映射信息 -->
<dataMappingSelect v-if="showDataMappingSelect" :dataMappingSelectType="dataMappingSelectType"  @dataMappingSelected="dataMappingSelected" @hideDataMappingSelectPage="hideDataMappingSelectPage" :showDataMappingSelect="showDataMappingSelect"  :dataMappingsForSelect="dataMappingsForSelect"></dataMappingSelect>
<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideDomainObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>
<!-- 选择值传递对象信息 -->
<valueObjectSelect v-if="showValueObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideValueObjectSelectPage="hideValueObjectSelectPage" :showValueObjectSelect="showValueObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

import dataMappingSelect from '../../domainModel/workbench-dataMappingSelect.vue'
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
    	dataMappingSelect,
    	domainObjectSelect,
        valueObjectSelect
    },
    data() {            
    	const objectSourceOptions = ref(null);
    	const toObjectSourceOptions = ref(null);
        
       const nodeResultTypeOptions = ref(null);
        const managedObjectTypeOptions = ref(null);
        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        
        const showDataMappingSelect = ref(false);
        const dataMappingsForSelect = ref({});
        const dataMappingSelectType = ref(null);
        
        const targetObjectType = ref("");
        
        const fromObjectCode = ref("");
        const nodeResultCode = ref("");
        const dataMappingName = ref("");
        
        return {
        	objectSourceOptions,
        	nodeResultTypeOptions,
        	showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,
            showDataMappingSelect,
            dataMappingsForSelect,
            dataMappingSelectType,
            
            managedObjectTypeOptions,
            toObjectSourceOptions,
            targetObjectType,
            
            fromObjectCode,
            nodeResultCode,
            dataMappingName
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
        },
        'nodeCfgJson.fromObjectType': function(val, old){
            this.nodeCfgJson.fromObjectClass = "";
            this.fromObjectCode = "";
            this.clearDataMapping();
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_SOURCE_TYPE_WITH_PRE','OBJECT_SOURCE_TYPE_DP','OBJECT_TYPE','OBJECT_TYPE_HAS_ATTRIBUTE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectSourceOptions = data.data['OBJECT_SOURCE_TYPE_WITH_PRE'];
                this.toObjectSourceOptions = data.data['OBJECT_SOURCE_TYPE_DP'];
                this.nodeResultTypeOptions = data.data['OBJECT_TYPE'];
                this.managedObjectTypeOptions = data.data['OBJECT_TYPE_HAS_ATTRIBUTE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
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
        
        if((this.nodeCfgJson.fromObjectType == "D" || this.nodeCfgJson.fromObjectType == "V")  && this.nodeCfgJson.fromObjectClass != null && this.nodeCfgJson.fromObjectClass != "") {
            var queryCodeUrl = "/lcdp-proxy/lowcode/platform/be/api/managedObject/queryObjectCode/" + this.nodeCfgJson.fromObjectType + "/" + this.nodeCfgJson.fromObjectClass;
            axiosClient.get(queryCodeUrl).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.fromObjectCode = data.data;
                }
            });
        }
        
        if(this.nodeCfgJson.dataMappingId != null && this.nodeCfgJson.dataMappingId != "") {
            var queryCodeUrl = "/lcdp-proxy/lowcode/platform/be/api/dataMapping/queryName/" + this.nodeCfgJson.dataMappingId;
            axiosClient.get(queryCodeUrl).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataMappingName = data.data;
                }
            });
        }
    },     
    methods: {
    	clearDataMapping(type){
    		this.nodeCfgJson.dataMappingId = "";
    		this.dataMappingName = "";
    		this.nodeCfgJson.toObjectType = "";
            this.nodeCfgJson.toObjectClass = "";
            //this.nodeCfgJson.fromObjectType = "";
            //this.nodeCfgJson.fromObjectClass = "";
        },
        selectDataMapping(type) {
            if(this.nodeCfgJson.fromObjectClass == null || this.nodeCfgJson.fromObjectClass == "") {
            	ElMessage.error(`请先选择源对象类型！`);
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/dataMapping/listByFromObject/" + this.nodeCfgJson.fromObjectType + "/" + this.nodeCfgJson.fromObjectClass;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataMappingsForSelect = data.data;
                    this.dataMappingSelectType = type;
                    this.showDataMappingSelect = true;
                }
            });
        },
        hideDataMappingSelectPage() {
            this.showDataMappingSelect = false;
            this.dataMappingsForSelect = null;
            this.dataMappingSelectType = null;
        },
        dataMappingSelected(mappings, type) {
        	this.nodeCfgJson.dataMappingId = mappings.id;
        	this.dataMappingName = mappings.name;
        	//this.nodeCfgJson.toObjectType = mappings.toObjectType;
        	//this.nodeCfgJson.toObjectClass = mappings.toObjectId;
        	this.nodeCfgJson.fromObjectType = mappings.fromObjectType;
            this.nodeCfgJson.fromObjectClass = mappings.fromObjectId;
            
            
            this.showDataMappingSelect = false;
            this.dataMappingsForSelect = null;
            this.dataMappingSelectType = null;
        },
    	selectNodeResultClass() {
        	this.targetObjectType = "V";
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
            	this.targetObjectType = "";
                return;
            }
        },
        clearNodeResultClass(){
            this.nodeCfgJson.nodeResultClass = "";
            this.nodeResultCode = "";
            this.clearDataMapping();
        },
        managedObjectSelected(obj) {
            if(this.targetObjectType == "V") {
                this.nodeCfgJson.nodeResultClass = obj.id;
                this.nodeResultCode = obj.code;
            }else if(this.targetObjectType == "F") {
                this.nodeCfgJson.fromObjectClass = obj.id;
                this.fromObjectCode = obj.code;
                this.clearDataMapping();
            }
            
            this.managedObjectsForSelect = null;
            this.showDomainObjectSelect = false;
            this.showValueObjectSelect = false;
        },
        selectFromObjectClass() {
        	this.targetObjectType = "F";
            if("D" == this.nodeCfgJson.fromObjectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedObjectsForSelect = data.data;
                        this.showDomainObjectSelect = true;
                    }
                });
            }else if("V" == this.nodeCfgJson.fromObjectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedObjectsForSelect = data.data;
                        this.showValueObjectSelect = true;
                    }   
                });
            }else {
            	this.targetObjectType = "";
                return;
            }
        },
        clearFromObjectClass(){
            this.nodeCfgJson.fromObjectClass = "";
            this.fromObjectCode = "";
            this.clearDataMapping();
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

