<!-- ObjectSetterNode-节点配置信息-编辑页 -->

<template v-if="showNodeCfgEdit == 'ObjectSetterNode' && nodeCfgJson != null && nodeCfgJson.isListResult != null">
	<el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="目标对象来源">
                <el-select v-model="nodeCfgJson.toObjectInstanceSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in toObjectSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="3">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(toObjectSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.toObjectInstanceSource=contextType;this.nodeCfgJson.toObjectInstanceKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
        <el-col :span="5">
            <el-checkbox v-model="nodeCfgJson.isListType" label="是否列表" />
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="目标对象Key">
                <el-input v-model="nodeCfgJson.toObjectInstanceKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="目标对象属性">
                <el-input v-model="nodeCfgJson.toObjectInstanceAttr" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="目标对象类型">
                <el-select v-model="nodeCfgJson.toObjectType" class="m-2" placeholder="Select" size="small" clearable>
                    <el-option v-for="item in nodeResultTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="目标对象">
                <el-input v-model="nodeCfgJson.toObjectClass" v-if="nodeCfgJson.toObjectType=='J'" size="small" />
                <el-input type="hidden" v-model="nodeCfgJson.toObjectClass" readonly v-if="nodeCfgJson.toObjectType!='J'" size="small" />
                <el-input v-model="toObjectCode" readonly v-if="nodeCfgJson.toObjectType!='J'" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="" label-width="10px" v-if="nodeCfgJson.toObjectType!='J'">
                <el-link  type="primary" @click="selectToObjectClass()" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearToObjectClass()" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="1">
            <el-divider direction="vertical" border-style="dashed" style="height:100%;margin-top: 1px;margin-bottom: 1px" ></el-divider>
        </el-col>
        <el-col :span="22">
            <el-row :gutter="4">
                <el-col :span="24">
                    <el-divider border-style="dashed" style="margin-top: 1px;margin-bottom: 10px" >设置规则</el-divider>
                </el-col>
            </el-row>
		    <el-row :gutter="4">
		        <el-col :span="16">
		            <el-form-item label="源对象类型" label-width='100px'>
		                <el-select v-model="fromObjectType" clearable class="m-2" placeholder="Select" size="small">
		                    <el-option v-for="item in objectSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
		                </el-select>
		            </el-form-item>
		        </el-col>
                <el-col :span="3">
                    <el-form-item label="" label-width="10px">
                        <el-link  type="primary" @click="showAvailableDataSelectPage(objectSourceOptions, (contextType, contextKey)=>{this.fromObjectType=contextType;this.fromObjectKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
                    </el-form-item>
                </el-col>
		        <el-col :span="5">
		            <el-checkbox v-model="fromObjectIsList" label="是否列表" />
		        </el-col>
		    </el-row>
		    <el-row :gutter="4">
		        <el-col :span="16">
		            <el-form-item label="源对象Key" label-width='100px'>
		                <el-input v-model="fromObjectKey" size="small" />
		            </el-form-item>
		        </el-col>
		        <el-col :span="8"></el-col>
		    </el-row>
		    <el-row :gutter="4">
                <el-col :span="24">
                    <el-form-item label="属性映射规则" label-width='100px'>
                        <el-input v-model="attrMappings" type="textarea" :rows="4" size="small" />
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="4">
                <el-col :span="16"></el-col>
                <el-col :span="8">
                    <div style="margin-bottom: 5px;margin-top: 1px;margin-left: 5px;">
                        <el-button type="default" size="small" @click="addRule()">添加</el-button>
                    </div>
                </el-col>
            </el-row>
		   <el-row :gutter="4">
		        <el-col :span="24">
		        <el-scrollbar  height="200px">
		            <el-table ref="paramRulesTable" :data="nodeCfgJson.paramsRule" stripe style="width: 100%">
		                <el-table-column fixed="left" label="删除" width="60">
		                    <template  #default="scope">
		                        <el-button link type="primary" size="small" @click.prevent="deleteRule(scope.$index)" >X</el-button>
		                    </template>
					        
					    </el-table-column>
		                <el-table-column prop="fromObjectType" label="来源" width="95" >
		                    <template  #default="scope">
		                        <div style="display: flex; align-items: center">
		                            <span v-if="scope.row.fromObjectType == 'I'">输入参数</span>
		                            <span v-else-if="scope.row.fromObjectType == 'P'">过程数据</span>
		                            <span v-else-if="scope.row.fromObjectType == 'D'">领域对象</span>
		                            <span v-else-if="scope.row.fromObjectType == 'F'">固定值</span>
                                    <span v-else>{{ scope.row.fromObjectType }}</span>
		                        </div>
		                    </template>
		                </el-table-column>
		                <el-table-column prop="isListType" label="是否列表" width="80" >
		                    <template  #default="scope">
		                        <div style="display: flex; align-items: center">
		                            <span v-if="scope.row.isListType == true">是</span>
		                            <span v-else-if="scope.row.isListType != true">否</span>
                                    <span v-else>{{ scope.row.isListType }}</span>
		                        </div>
		                    </template>
		                </el-table-column>
		                <el-table-column prop="fromObjectKey" label="Key" width="160" />
                        <el-table-column prop="attrMappings" label="属性映射规则" width="600" />
		            </el-table>
		        </el-scrollbar>
		        </el-col>
		    </el-row>
            <el-row :gutter="4">
                <el-col :span="24">
                    <el-divider border-style="dashed" style="margin-top: 1px;margin-bottom: 1px" ></el-divider>
                </el-col>
            </el-row>
        </el-col>
        <el-col :span="1">
            <el-divider direction="vertical" border-style="dashed" style="height:100%;margin-top: 1px;margin-bottom: 1px" ></el-divider>
        </el-col>
    </el-row>
    
    <el-row :gutter="4">
        <el-col :span="2">
            &nbsp;
        </el-col>
        <el-col :span="22">
            &nbsp;
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
<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showToDomainObjectSelect"  @managedObjectSelected="managedToObjectSelected" @hideDomainObjectSelectPage="hideToDomainObjectSelectPage" :showDomainObjectSelect="showToDomainObjectSelect"  :managedObjectsForSelect="managedToObjectsForSelect"/>
<!-- 选择值传递对象信息 -->
<valueObjectSelect v-if="showToValueObjectSelect"  @managedObjectSelected="managedToObjectSelected" @hideValueObjectSelectPage="hideToValueObjectSelectPage" :showValueObjectSelect="showToValueObjectSelect"  :managedObjectsForSelect="managedToObjectsForSelect"/>

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
    	const objectSourceOptions = ref(null);
    	const toObjectSourceOptions = ref(null);
        
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
        
        const toObjectCode = ref("");
        const nodeResultCode = ref("");
        
        return {
        	objectSourceOptions,
        	nodeResultTypeOptions,
        	
        	showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,
            
            showToDomainObjectSelect,
            showToValueObjectSelect,
            managedToObjectsForSelect,
            
            toObjectSourceOptions,
            
            fromObjectType,
            fromObjectIsList,
            fromObjectKey,
            attrMappings,
            
            toObjectCode,
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
        'nodeCfgJson.toObjectType': function(val, old){
        	this.nodeCfgJson.toObjectClass = "";
        	this.toObjectCode = "";
        }
    
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_SOURCE_TYPE','OBJECT_SOURCE_TYPE_ALL','OBJECT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectSourceOptions = data.data['OBJECT_SOURCE_TYPE'];
                this.toObjectSourceOptions = data.data['OBJECT_SOURCE_TYPE_ALL'];
                this.nodeResultTypeOptions = data.data['OBJECT_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	if(this.nodeCfgJson.paramsRule == null) {
    		this.nodeCfgJson.paramsRule = [];
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
        
        if((this.nodeCfgJson.toObjectType == "D" || this.nodeCfgJson.toObjectType == "V")  && this.nodeCfgJson.toObjectClass != null && this.nodeCfgJson.toObjectClass != "") {
            var queryCodeUrl = "/lcdp-proxy/lowcode/platform/be/api/managedObject/queryObjectCode/" + this.nodeCfgJson.toObjectType + "/" + this.nodeCfgJson.toObjectClass;
            axiosClient.get(queryCodeUrl).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.toObjectCode = data.data;
                }
            });
        }
    },     
    methods: {
    	selectToObjectClass() {
            if("D" == this.nodeCfgJson.toObjectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedToObjectsForSelect = data.data;
                        this.showToDomainObjectSelect = true;
                    }
                });
            }else if("V" == this.nodeCfgJson.toObjectType) {
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
            this.nodeCfgJson.toObjectClass = "";
            this.toObjectCode = "";
        },
        managedToObjectSelected(obj) {
            this.nodeCfgJson.toObjectClass = obj.id;
            this.toObjectCode = obj.code;
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
        addRule() {
            var param = {
            		fromObjectType : this.fromObjectType,
            		fromObjectIsList : this.fromObjectIsList,
            		fromObjectKey : this.fromObjectKey,
            		attrMappings : this.attrMappings
            };
            this.nodeCfgJson.paramsRule.push(param);
        },
        deleteRule(row) {
            this.nodeCfgJson.paramsRule.splice(row,1);
        }
    }
}
</script>

