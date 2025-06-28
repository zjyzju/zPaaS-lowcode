<!-- ForEachNode-节点配置信息-编辑页 -->

<template v-if="showNodeCfgEdit == 'ForEachNode' && nodeCfgJson != null && nodeCfgJson.isListResult != null">
	<el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="循环对象来源">
                <el-select v-model="nodeCfgJson.arrayObjectSource" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in paramObjectSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
		    <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="showAvailableDataSelectPage(paramObjectSourceOptions, (contextType, contextKey)=>{this.nodeCfgJson.arrayObjectSource=contextType;this.nodeCfgJson.arrayObjectKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
            </el-form-item>
		</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="循环对象Key">
                <el-input v-model="nodeCfgJson.arrayObjectKey" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="循环对象属性">
                <el-input v-model="nodeCfgJson.arrayAttrPath" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="元素对象类型">
                <el-select v-model="nodeCfgJson.arrayElementObjectType" class="m-2" placeholder="Select" size="small" clearable>
                    <el-option v-for="item in nodeResultTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            &nbsp;&nbsp;<el-checkbox v-model="nodeCfgJson.arrayElementIsList" label="是否列表" />
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="元素对象">
                <el-input v-model="nodeCfgJson.arrayElementObjectClass" v-if="nodeCfgJson.arrayElementObjectType=='J'" size="small" />
                <el-input type="hidden" v-model="nodeCfgJson.arrayElementObjectClass" readonly v-if="nodeCfgJson.arrayElementObjectType!='J'" size="small" />
                <el-input v-model="arrayElementObjectCode" readonly v-if="nodeCfgJson.arrayElementObjectType!='J'" size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8" v-if="nodeCfgJson.arrayElementObjectType!='J'">
            <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="selectElementObjectClass()" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearElementObjectClass()" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="子业务流">
                <el-input v-model="nodeCfgJson.subBusinessFlowId" readonly size="small" />
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


<!-- 选择元素对象领域对象信息 -->
<domainObjectSelect v-if="showElementDomainObjectSelect"  @managedObjectSelected="managedElementObjectSelected" @hideDomainObjectSelectPage="hideElementDomainObjectSelectPage" :showDomainObjectSelect="showElementDomainObjectSelect"  :managedObjectsForSelect="managedElementObjectsForSelect"/>
<!-- 选择元素对象值传递对象信息 -->
<valueObjectSelect v-if="showElementValueObjectSelect"  @managedObjectSelected="managedElementObjectSelected" @hideValueObjectSelectPage="hideElementValueObjectSelectPage" :showValueObjectSelect="showElementValueObjectSelect"  :managedObjectsForSelect="managedElementObjectsForSelect"/>

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
    	const paramObjectSourceOptions = ref(null);
    	const nodeResultTypeOptions = ref(null);
        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        
        const showElementDomainObjectSelect = ref(false);
        const managedElementObjectsForSelect = ref(null);
        const showElementValueObjectSelect = ref(false);
        
        const arrayElementObjectCode = ref("");
        const nodeResultCode = ref("");
        
        return {
        	paramObjectSourceOptions,
        	nodeResultTypeOptions,
        	
        	showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,
            
            showElementDomainObjectSelect,
            showElementValueObjectSelect,
            managedElementObjectsForSelect,
            
            arrayElementObjectCode,
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
    	,
        'nodeCfgJson.arrayElementObjectType': function(val, old){
            this.nodeCfgJson.arrayElementObjectClass = "";
            this.arrayElementObjectCode = "";
        }
    
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_SOURCE_TYPE_ALL','OBJECT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.paramObjectSourceOptions = data.data['OBJECT_SOURCE_TYPE_ALL'];
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
    	
    	if((this.nodeCfgJson.arrayElementObjectType == "D" || this.nodeCfgJson.arrayElementObjectType == "V")  && this.nodeCfgJson.arrayElementObjectClass != null && this.nodeCfgJson.arrayElementObjectClass != "") {
            var queryCodeUrl = "/lcdp-proxy/lowcode/platform/be/api/managedObject/queryObjectCode/" + this.nodeCfgJson.arrayElementObjectType + "/" + this.nodeCfgJson.arrayElementObjectClass;
            axiosClient.get(queryCodeUrl).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.arrayElementObjectCode = data.data;
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
        selectElementObjectClass() {console.log("selectElementObjectClass");
            if("D" == this.nodeCfgJson.arrayElementObjectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedElementObjectsForSelect = data.data;
                        this.showElementDomainObjectSelect = true;
                    }
                });
            }else if("V" == this.nodeCfgJson.arrayElementObjectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedElementObjectsForSelect = data.data;
                        this.showElementValueObjectSelect = true;
                    }
                });
            }else {
                return;
            }
        },
        clearElementObjectClass(){
            this.nodeCfgJson.arrayElementObjectClass = "";
            this.arrayElementObjectCode = "";
        },
        managedElementObjectSelected(obj) {
            this.nodeCfgJson.arrayElementObjectClass = obj.id;
            this.arrayElementObjectCode = obj.code;
            this.managedElementObjectsForSelect = null;
            this.showElementDomainObjectSelect = false;
            this.showElementValueObjectSelect = false;
        },
        hideElementDomainObjectSelectPage(){
             this.managedElementObjectsForSelect = null;
             this.showElementDomainObjectSelect = false;
        },
        hideElementValueObjectSelectPage(){
            this.managedElementObjectsForSelect = null;
            this.showElementValueObjectSelect = false;
        }
    }
}
</script>

