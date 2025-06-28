<!-- ValidateNode-节点配置信息-编辑页 -->

<template v-if="showNodeCfgEdit == 'ValidateNode' && nodeCfgJson != null && nodeCfgJson.isListResult != null">
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
            <el-form-item label="目标对象类型">
                <el-select v-model="nodeCfgJson.targetObjectType" class="m-2" placeholder="Select" size="small" clearable>
                    <el-option v-for="item in toObjectTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="目标对象">
                <el-input type="hidden" v-model="nodeCfgJson.targetObjectClass" readonly  size="small" />
                <el-input v-model="targetObjectCode" readonly  size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="selectToObjectClass()" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearToObjectClass()" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="校验规则">
                <el-input type="hidden" v-model="nodeCfgJson.ruleGroupId" readonly  size="small" />
                <el-input v-model="ruleGroupName" readonly  size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="selectRuleGroup()" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearRuleGroup()" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
    </el-row>
    
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="是否中断执行">
                <el-checkbox v-model="nodeCfgJson.interruptBusinessFlow" label="中断执行" />
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
<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showToDomainObjectSelect"  @managedObjectSelected="managedToObjectSelected" @hideDomainObjectSelectPage="hideToDomainObjectSelectPage" :showDomainObjectSelect="showToDomainObjectSelect"  :managedObjectsForSelect="managedToObjectsForSelect"/>
<!-- 选择值传递对象信息 -->
<valueObjectSelect v-if="showToValueObjectSelect"  @managedObjectSelected="managedToObjectSelected" @hideValueObjectSelectPage="hideToValueObjectSelectPage" :showValueObjectSelect="showToValueObjectSelect"  :managedObjectsForSelect="managedToObjectsForSelect"/>
<!-- 选择校验规则组信息 -->
<ruleGroupSelect v-if="showRuleGroupSelect"  @ruleGroupSelected="ruleGroupSelected" @hideRuleGroupSelectPage="hideRuleGroupSelectPage" :showRuleGroupSelect="showRuleGroupSelect"  :ruleGroupsForSelect="ruleGroupsForSelect" :systemId="systemId" :objectType="nodeCfgJson.targetObjectType" :objectId="nodeCfgJson.targetObjectClass" :tenantId="tenantId"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

import domainObjectSelect from '../../domainModel/workbench-domainObjectSelect.vue'
import valueObjectSelect from '../../domainModel/workbench-valueObjectSelect.vue'
import ruleGroupSelect from '../../domainModel/workbench-ruleGroupSelect.vue'

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
        ruleGroupSelect
    },
    data() {            
    	const objectSourceOptions = ref(null);
    	const toObjectSourceOptions = ref(null);
        
       const toObjectTypeOptions = ref(null);
       const nodeResultTypeOptions = ref(null);
        
        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        
        const showToDomainObjectSelect = ref(false);
        const managedToObjectsForSelect = ref(null);
        const showToValueObjectSelect = ref(false);
        
        const fromObjectType = ref('');
        const fromObjectIsList = ref(false);
        const fromObjectKey = ref('');
        const attrMappings = ref('[{"fromAttrPath":"","toObjectAttr":""}]');
        
        const showRuleGroupSelect = ref(false);
        const ruleGroupsForSelect = ref(null);
        
        const targetObjectCode = ref("");
        const ruleGroupName = ref("");
        const nodeResultCode = ref("");
        
        return {
        	objectSourceOptions,
        	nodeResultTypeOptions,
        	
        	showRuleGroupSelect,
        	ruleGroupsForSelect,
        	
        	showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,
            
            showToDomainObjectSelect,
            showToValueObjectSelect,
            managedToObjectsForSelect,
            
            toObjectSourceOptions,
            toObjectTypeOptions,
            
            fromObjectType,
            fromObjectIsList,
            fromObjectKey,
            attrMappings,
            
            targetObjectCode,
            ruleGroupName,
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
        },
        'nodeCfgJson.targetObjectType': function(val, old){
            this.nodeCfgJson.targetObjectClass = "";
            this.targetObjectCode = "";
            this.nodeCfgJson.ruleGroupId = "";
            this.ruleGroupName = "";
        }
    
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_SOURCE_TYPE_WITH_PRE','OBJECT_SOURCE_TYPE_DP','OBJECT_TYPE','OBJECT_TYPE_HAS_ATTRIBUTE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectSourceOptions = data.data['OBJECT_SOURCE_TYPE_WITH_PRE'];
                this.toObjectSourceOptions = data.data['OBJECT_SOURCE_TYPE_DP'];
                this.nodeResultTypeOptions = data.data['OBJECT_TYPE'];
                this.toObjectTypeOptions = data.data['OBJECT_TYPE_HAS_ATTRIBUTE'];
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
        
        if((this.nodeCfgJson.targetObjectType == "D" || this.nodeCfgJson.targetObjectType == "V")  && this.nodeCfgJson.targetObjectClass != null && this.nodeCfgJson.targetObjectClass != "") {
            var queryCodeUrl = "/lcdp-proxy/lowcode/platform/be/api/managedObject/queryObjectCode/" + this.nodeCfgJson.targetObjectType + "/" + this.nodeCfgJson.targetObjectClass;
            axiosClient.get(queryCodeUrl).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.targetObjectCode = data.data;
                }
            });
        }
        
        if(this.nodeCfgJson.ruleGroupId != null && this.nodeCfgJson.ruleGroupId != "") {
            var queryCodeUrl = "/lcdp-proxy/lowcode/platform/be/api/validateRuleGroup/queryName/" + this.nodeCfgJson.ruleGroupId;
            axiosClient.get(queryCodeUrl).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.ruleGroupName = data.data;
                }
            });
        }
    },     
    methods: {
    	selectRuleGroup() {
    		if(this.nodeCfgJson.targetObjectClass == null || this.nodeCfgJson.targetObjectClass == "") {
    			ElMessage(`请先选择目标对象！`);
    			return;
    		}
            var url = "/lcdp-proxy/lowcode/platform/be/api/validateRuleGroup/listByObject/" + this.systemId + "/" + this.nodeCfgJson.targetObjectType + "/" + this.nodeCfgJson.targetObjectClass;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.ruleGroupsForSelect = data.data;
                    this.showRuleGroupSelect = true;
                }
            });
        },
        clearRuleGroup(){
            this.nodeCfgJson.ruleGroupId = "";
            this.ruleGroupName = "";
        },
        ruleGroupSelected(obj) {
            this.nodeCfgJson.ruleGroupId = obj.id;
            this.ruleGroupName = obj.name;
            this.ruleGroupsForSelect = null;
            this.showRuleGroupSelect = false;
        },
        hideRuleGroupSelectPage(){
             this.ruleGroupsForSelect = null;
             this.showRuleGroupSelect = false;
        },
        selectToObjectClass() {
            if("D" == this.nodeCfgJson.targetObjectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedToObjectsForSelect = data.data;
                        this.showToDomainObjectSelect = true;
                    }
                });
            }else if("V" == this.nodeCfgJson.targetObjectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedToObjectsForSelect = data.data;
                        this.showToValueObjectSelect = true;
                    }
                });
            }else {
                return;
            }
        },
        clearToObjectClass(){
            this.nodeCfgJson.targetObjectClass = "";
            this.targetObjectCode = "";
            this.clearRuleGroup();
        },
        managedToObjectSelected(obj) {
            this.nodeCfgJson.targetObjectClass = obj.id;
            this.targetObjectCode = obj.name;
            this.managedToObjectsForSelect = null;
            this.showToDomainObjectSelect = false;
            this.showToValueObjectSelect = false;
        },
        hideToDomainObjectSelectPage(){
             this.managedToObjectsForSelect = null;
             this.showToDomainObjectSelect = false;
        },
        hideToValueObjectSelectPage(){
            this.managedToObjectsForSelect = null;
            this.showToValueObjectSelect = false;
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

