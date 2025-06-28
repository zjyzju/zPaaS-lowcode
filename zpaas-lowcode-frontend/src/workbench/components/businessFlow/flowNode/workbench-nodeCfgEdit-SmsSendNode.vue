<!-- 短信发送节点-节点配置信息-编辑页 -->

<template v-if="showNodeCfgEdit == 'SmsSendNode' && nodeCfgJson != null && nodeCfgJson.isListResult != null">
	<el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="短信网关">
                <el-select v-model="nodeCfgJson.smsGatewayId" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in smsGatewayResources" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
	<el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="收信人来源">
                <el-select v-model="nodeCfgJson.receiverSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.receiverSource=contextType;this.nodeCfgJson.receiverKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="收信人Key">
                <el-input v-model="nodeCfgJson.receiverKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="收信人属性">
                <el-input v-model="nodeCfgJson.receiverAttr" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
   <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="短信内容来源">
                <el-select v-model="nodeCfgJson.contentSource" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.contentSource=contextType;this.nodeCfgJson.contentKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="短信内容Key">
                <el-input type="textarea" v-model="nodeCfgJson.contentKey" rows="6" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="短信内容属性">
                <el-input v-model="nodeCfgJson.contentAttr" size="small" />
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
	                <el-divider border-style="dashed" style="margin-top: 1px;margin-bottom: 10px" >设置短信内容参数</el-divider>
	            </el-col>
	        </el-row>
	        <el-row :gutter="4">
	            <el-col :span="16">
	                <el-form-item label="参数序号" label-width='100px'>
	                    <el-input v-model="paramId" size="small" />
	                </el-form-item>
	            </el-col>
	            <el-col :span="8">
                    <div style="margin-bottom: 1px;margin-top: 4px;margin-left: 5px;">
                        <el-button type="default" size="small" @click="addParam()">添加</el-button>
                    </div>
                </el-col>
	        </el-row>
	        <el-row :gutter="4">
                <el-col :span="16">
                    <el-form-item label="参数值来源">
		                <el-select v-model="paramSource" clearable class="m-2" placeholder="Select" size="small">
		                    <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
		                </el-select>
		            </el-form-item>
                </el-col>
                <el-col :span="8">
                    <el-form-item label="" label-width="10px">
                        <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.paramSource=contextType;this.paramKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
                    </el-form-item>
                </el-col>
            </el-row>
            <el-row :gutter="4">
                <el-col :span="12">
                    <el-form-item label="参数值Key" label-width='100px'>
                        <el-input v-model="paramKey" size="small" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="参数值属性" label-width='100px'>
                        <el-input v-model="paramAttr" size="small" />
                    </el-form-item>
                </el-col>
            </el-row>
            
	        <el-row :gutter="4">
	            <el-col :span="24">
	            <el-scrollbar  height="200px">
	                <el-table ref="contentParamsTable" :data="nodeCfgJson.contentParams" stripe style="width: 100%">
	                    <el-table-column fixed="left" label="删除" width="60">
	                        <template  #default="scope">
	                            <el-button link type="primary" size="small" @click.prevent="deleteParam(scope.$index)" >X</el-button>
	                        </template>
	                        
	                    </el-table-column>
	                    <el-table-column prop="paramId" label="参数序号" width="80" />
	                    <el-table-column prop="paramSource" label="参数值来源" width="150" >
		                    <template  #default="scope">
		                        <div style="display: flex; align-items: center">
		                            <span v-if="scope.row.paramSource == 'I'">输入参数</span>
		                            <span v-if="scope.row.paramSource == 'P'">过程数据</span>
		                            <span v-if="scope.row.paramSource == 'D'">领域对象</span>
		                            <span v-if="scope.row.paramSource == 'F'">固定值</span>
                                    <span v-if="scope.row.paramSource == 'N'">预处理参数</span>
		                        </div>
		                    </template>
		                </el-table-column>
	                    <el-table-column prop="paramKey" label="参数值Key" width="150" />
	                    <el-table-column prop="paramAttr" label="参数值属性" width="150" />
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
	   <el-col :span="24">
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

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

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
    	const objectAttrSourceOptions = ref(null);
        
       const nodeResultTypeOptions = ref(null);
       
        
        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        
        const paramId = ref(null);
        const paramSource = ref(null);
        const paramKey = ref(null);
        const paramAttr = ref(null);
        
        const nodeResultCode = ref("");
        
        const smsGatewayResources = ref([]);
        
        return {
        	objectAttrSourceOptions,
        	nodeResultTypeOptions,
        	showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,
            
            paramId,
            paramSource,
            paramKey,
            paramAttr,
            
             nodeResultCode,
            smsGatewayResources
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
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_SOURCE_TYPE_WITH_PRE','PUB_YES_OR_NO','OBJECT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectAttrSourceOptions = data.data['OBJECT_SOURCE_TYPE_WITH_PRE'];
                this.nodeResultTypeOptions = data.data['OBJECT_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	var url = "/lcdp-proxy/lowcode/platform/be/api/serverResource/list/" + this.systemId + "/G";
        axiosClient.get(url).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.smsGatewayResources = data.data;
                if(this.smsGatewayResources != null && this.smsGatewayResources.length > 0 && (this.nodeCfgJson.smsGatewayId == null || this.nodeCfgJson.smsGatewayId == "") ) {
                    this.nodeCfgJson.smsGatewayId = this.smsGatewayResources[0].id;
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
       },
       addParam() {
           var param = {
        	    paramId : this.paramId,
        		paramSource : this.paramSource,
                paramKey : this.paramKey,
                paramAttr : this.paramAttr,
               
           };
           this.nodeCfgJson.contentParams.push(param);
       },
       deleteParam(row) {
           this.nodeCfgJson.contentParams.splice(row,1);
       }
    }
}
</script>

