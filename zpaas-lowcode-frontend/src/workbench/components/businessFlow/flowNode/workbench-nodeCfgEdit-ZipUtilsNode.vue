<!-- Zip能力节点-节点配置信息-编辑页 -->

<template v-if="showNodeCfgEdit == 'ZipUtilsNode' && nodeCfgJson != null && nodeCfgJson.isListResult != null">
	<el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="Zip操作">
                <el-select v-model="nodeCfgJson.zipOperation" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in zipOperationOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="文件名">
                <el-input v-model="nodeCfgJson.fileName" size="small" />
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
	                <el-divider border-style="dashed" style="margin-top: 1px;margin-bottom: 10px" >源文件列表</el-divider>
	            </el-col>
	        </el-row>
            <el-row :gutter="4">
                <el-col :span="16">
                    <el-form-item label="源文件来源">
                        <el-select v-model="srcFileSource" clearable class="m-2" placeholder="Select" size="small">
                            <el-option v-for="item in objectAttrSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
                        </el-select>
                    </el-form-item>
                </el-col>
                <el-col :span="3">
                    <el-form-item label="" label-width="10px">
                        <el-link  type="primary" @click="showAvailableDataSelectPage(objectAttrSourceOptions, (contextType, contextKey)=>{this.srcFileSource=contextType;this.srcFileKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
                    </el-form-item>
                </el-col>
                <el-col :span="5">
                    <div style="margin-bottom: 1px;margin-top: 4px;margin-left: 5px;">
                        <el-button type="default" size="small" @click="addSrcFile()">添加</el-button>
                    </div>
                </el-col>
            </el-row>
            <el-row :gutter="4">
                <el-col :span="12">
                    <el-form-item label="源文件Key">
                        <el-input v-model="srcFileKey" size="small" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="源文件属性">
                        <el-input v-model="srcFileAttr" size="small" />
                    </el-form-item>
                </el-col>
            </el-row>
	        
	        <el-row :gutter="4">
	            <el-col :span="24">
	            <el-scrollbar  height="200px">
	                <el-table ref="srcFilesTable" :data="nodeCfgJson.srcFiles" stripe style="width: 100%">
	                    <el-table-column fixed="left" label="删除" width="60">
	                        <template  #default="scope">
	                            <el-button link type="primary" size="small" @click.prevent="deleteSrcFile(scope.$index)" >X</el-button>
	                        </template>
	                    </el-table-column>
                        <el-table-column prop="srcFileSource" label="来源" width="95" >
		                    <template  #default="scope">
		                        <div style="display: flex; align-items: center">
		                            <span v-if="scope.row.srcFileSource == 'I'">输入参数</span>
		                            <span v-else-if="scope.row.srcFileSource == 'P'">过程数据</span>
		                            <span v-else-if="scope.row.srcFileSource == 'D'">领域对象</span>
		                            <span v-else-if="scope.row.srcFileSource == 'F'">固定值</span>
                                    <span v-else-if="scope.row.srcFileSource == 'N'">预处理参数</span>
                                    <span v-else>{{ scope.row.srcFileSource }}</span>
		                        </div>
		                    </template>
		                </el-table-column>
		                <el-table-column prop="srcFileKey" label="Key" width="100" />
		                <el-table-column prop="srcFileAttr" label="属性" width="100" />
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
    	const zipOperationOptions = ref(null);
    	
    	const objectAttrSourceOptions = ref(null);
        
       const nodeResultTypeOptions = ref(null);
        
        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);

        const srcFileSource = ref(null);
        const srcFileKey = ref(null);
        const srcFileAttr = ref(null);
        
        const nodeResultCode = ref("");
        
        return {
        	zipOperationOptions,
        	nodeResultTypeOptions,

        	showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,

            srcFileSource,
            srcFileKey,
            srcFileAttr,
            
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
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_SOURCE_TYPE_WITH_PRE','ZIP_OPERATION_TYPE','OBJECT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectAttrSourceOptions = data.data['OBJECT_SOURCE_TYPE_WITH_PRE'];
                this.zipOperationOptions = data.data['ZIP_OPERATION_TYPE'];
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
       addSrcFile() {
           var srcFile = {
               srcFileSource : this.srcFileSource,
               srcFileKey : this.srcFileKey,
               srcFileAttr : this.srcFileAttr
           };
           this.nodeCfgJson.srcFiles.push(srcFile);
       },
       deleteSrcFile(row) {
           this.nodeCfgJson.srcFiles.splice(row,1);
       }
    }
}
</script>

