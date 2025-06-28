<!-- ORM数据库存取节点-节点配置信息-编辑页 -->

<template v-if="showNodeCfgEdit == 'ORMRepositoryNode' && nodeCfgJson != null && nodeCfgJson.isListResult != null">
	<el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="入库对象来源">
                <el-select v-model="nodeCfgJson.domainObjectSource" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="3">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.domainObjectSource=contextType;this.nodeCfgJson.domainObjectKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
        <el-col :span="5">
            <el-checkbox v-model="nodeCfgJson.isListType" label="是否列表" />
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="入库对象Key">
                <el-input v-model="nodeCfgJson.domainObjectKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="ORM映射规则">
                <el-input type="hidden" v-model="nodeCfgJson.ormId" readonly size="small" />
                <el-input v-model="ormName" readonly size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="selectObjectRelationMapping()" ><label>设置</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearObjectRelationMapping()" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="入库操作类型">
                <el-select v-model="nodeCfgJson.repositoryOperation" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in repositoryOperationOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
         <el-col :span="8" v-if="nodeCfgJson.repositoryOperation == 'QC'">
            &nbsp;&nbsp;<el-checkbox v-model="nodeCfgJson.pageFlag" label="是否分页" />
        </el-col>
    </el-row>
    <el-row :gutter="4"  v-if="nodeCfgJson.repositoryOperation == 'QC'">
        <el-col :span="24">
            <el-form-item label="查询选项">
                <el-input v-model="nodeCfgJson.queryOptions" type="textarea" placeholder='{"fuzzyQuery":"attr1,attr2", "rangeQuery":"attr3", "multiValueQuery":"attr4,attr5,attr6", "attr7":">", "attr8":"<="}' :rows="4" size="small" />
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
    
    
<!-- 选择对象关系映射信息 -->
<objectRelationMappingSelect v-if="showObjectRelationMappingSelect"  @objectRelationMappingSelected="objectRelationMappingSelected" @hideObjectRelationMappingSelectPage="hideObjectRelationMappingSelectPage" :showObjectRelationMappingSelect="showObjectRelationMappingSelect"  :objectRelationMappingsForSelect="objectRelationMappingsForSelect"></objectRelationMappingSelect>
<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideDomainObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>
<!-- 选择值传递对象信息 -->
<valueObjectSelect v-if="showValueObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideValueObjectSelectPage="hideValueObjectSelectPage" :showValueObjectSelect="showValueObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

import objectRelationMappingSelect from '../../domainModel/workbench-objectRelationMappingSelect.vue'
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
    	objectRelationMappingSelect,
    	domainObjectSelect,
        valueObjectSelect
    },
    data() {            
    	const objectSourceOptions = ref(null);
        
        const repositoryOperationOptions = ref(null);
        
        const nodeResultTypeOptions = ref(null);
        const showObjectRelationMappingSelect = ref(false);
        const objectRelationMappingsForSelect = ref(null);
        
        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        
        const nodeResultCode = ref("");
        const ormName = ref("");
        
        return {
        	objectSourceOptions,
        	repositoryOperationOptions,
        	nodeResultTypeOptions,
        	showObjectRelationMappingSelect,
        	objectRelationMappingsForSelect,
        	showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,
            
            nodeResultCode,
            ormName
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
        'nodeCfgJson.repositoryOperation': function(val, old){
            this.nodeCfgJson.pageFlag = false;
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_SOURCE_TYPE_ALL','DATABASE_OPERATION_TYPE','OBJECT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectSourceOptions = data.data['OBJECT_SOURCE_TYPE_ALL'];
                this.repositoryOperationOptions = data.data['DATABASE_OPERATION_TYPE'];
                this.nodeResultTypeOptions = data.data['OBJECT_TYPE'];
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
    	
    	if(this.nodeCfgJson.ormId != null && this.nodeCfgJson.ormId != "") {
            var queryCodeUrl = "/lcdp-proxy/lowcode/platform/be/api/objectRelationMapping/queryName/" + this.nodeCfgJson.ormId;
            axiosClient.get(queryCodeUrl).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.ormName = data.data;
                }
            });
        }
    },     
    methods: {
    	selectObjectRelationMapping() {
    		if(this.nodeCfgJson == null) {
    			return;
    		}
    		var url = "/lcdp-proxy/lowcode/platform/be/api/objectRelationMapping/list/" + this.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.objectRelationMappingsForSelect = data.data;
                    this.showObjectRelationMappingSelect = true;
                }
            });
    	},
    	hideObjectRelationMappingSelectPage() {
    		this.showObjectRelationMappingSelect = false;
    		this.objectRelationMappingsForSelect = null;
    	
    	},
    	objectRelationMappingSelected(mapping) {
    		this.nodeCfgJson.ormId = mapping.id;
    		this.ormName = mapping.name
            this.nodeCfgJson.domainObjectId = mapping.domainObjectId;
            this.showObjectRelationMappingSelect = false;
            this.objectRelationMappingsForSelect = null;
    	},
    	clearObjectRelationMapping() {
    		this.nodeCfgJson.ormId = "";
    		this.nodeCfgJson.domainObjectId = "";
    		this.ormName = "";
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

