<!-- SpringElNode-节点配置信息-编辑页 -->

<template v-if="showNodeCfgEdit == 'SpringElNode' && nodeCfgJson != null && nodeCfgJson.isListResult != null">
	<el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="SpEL表达式">
                <el-input v-model="nodeCfgJson.springExpr" type="textarea" rows="5" placeholder="(${1}+${2})*(${3}-${4}/${5})" size="small" />
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
                    <el-divider border-style="dashed" style="margin-top: 1px;margin-bottom: 10px" >设置取值</el-divider>
                </el-col>
            </el-row>
		    <el-row :gutter="4">
		        <el-col :span="16">
		            <el-form-item label="值对象来源" label-width='100px'>
		                <el-select v-model="valueObjectSource" class="m-2" placeholder="Select" size="small">
		                    <el-option v-for="item in objectSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
		                </el-select>
		            </el-form-item>
		        </el-col>
                <el-col :span="3">
                    <el-form-item label="" label-width="10px">
                        <el-link  type="primary" @click="showAvailableDataSelectPage(objectSourceOptions, (contextType, contextKey)=>{this.valueObjectSource=contextType;this.valueObjectKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
                    </el-form-item>
                </el-col>
		        <el-col :span="5">
		            <el-checkbox v-model="valueObjectIsList" label="是否列表" />
		        </el-col>
		    </el-row>
		    <el-row :gutter="4">
		        <el-col :span="16">
		            <el-form-item label="值对象 Key" label-width='100px'>
		                <el-input v-model="valueObjectKey" size="small" />
		            </el-form-item>
		        </el-col>
		        <el-col :span="8">
		            <el-form-item label="属性" label-width='45px'>
		                <el-input v-model="valueAttrPath" size="small" />
		            </el-form-item>
		        </el-col>
		    </el-row>
		   <el-row :gutter="4">
		        <el-col :span="16">
		            <el-form-item label="取值方式" label-width='100px'>
		                <el-select v-model="valueType" clearable class="m-2" placeholder="Select" size="small">
		                    <el-option v-for="item in valueTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
		                </el-select>
		            </el-form-item>
		        </el-col>
		        <el-col :span="8"></el-col>
		    </el-row>
            <el-row :gutter="4">
		        <el-col :span="16">
		            <el-form-item label="上下文Key" label-width='100px'>
		                <el-input v-model="contextKey" size="small" />
		            </el-form-item>
		        </el-col>
		        <el-col :span="8">
		            <div style="margin-bottom: 5px;margin-top: 1px;margin-left: 5px;">
                        <el-button type="default" size="small" @click="addValue()">添加</el-button>
                    </div>
		        </el-col>
		    </el-row>
		    
		    <el-row :gutter="4">
		        <el-col :span="24">
		        <el-scrollbar  height="200px">
		            <el-table ref="valuesTable" :data="nodeCfgJson.values" stripe style="width: 100%">
		                <el-table-column fixed="left" label="删除" width="60">
		                    <template  #default="scope">
		                        <el-button link type="primary" size="small" @click.prevent="deleteValue(scope.$index)" >X</el-button>
		                    </template>
					    </el-table-column>
		                <el-table-column prop="valueObjectSource" label="来源" width="95" >
                            <template #default="scope">
                                <span v-if="scope.row['valueObjectSource'] == 'P'">过程数据</span>
                                <span v-else-if="scope.row['valueObjectSource'] == 'I'">输入参数</span>
                                <span v-else-if="scope.row['valueObjectSource'] == 'N'">预处理对象</span>
                                <span v-else-if="scope.row['valueObjectSource'] == 'D'">领域对象</span>
                                <span v-else-if="scope.row['valueObjectSource'] == 'O'">属主对象</span>
                                <span v-else-if="scope.row['valueObjectSource'] == 'F'">固定值</span>
                                <span v-else>{{ scope.row['valueObjectSource'] }}</span>
                            </template>
                        </el-table-column>
		                <el-table-column prop="valueObjectIsList" label="列表" width="55" >
                            <template #default="scope">
                                <span v-if="scope.row['valueObjectIsList'] == true">是</span>
                                <span v-else-if="scope.row['valueObjectIsList'] == false">否</span>
                                <span v-else>{{ scope.row['valueObjectIsList'] }}</span>
                            </template>
                        </el-table-column>
		                <el-table-column prop="valueObjectKey" label="Key" width="160" />
		                <el-table-column prop="valueAttrPath" label="属性" width="140" />
		                <el-table-column prop="valueType" label="取值类型" width="110" >
                            <template #default="scope">
                                <span v-if="scope.row['valueType'] == 'O'">原值</span>
                                <span v-else-if="scope.row['valueType'] == 'C'">数组的大小</span>
                                <span v-else-if="scope.row['valueType'] == 'L'">字符串长度</span>
                                <span v-else>{{ scope.row['valueType'] }}</span>
                            </template>
                        </el-table-column>
                        <el-table-column prop="contextKey" label="上下文Key" width="200" />
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
    	const valueTypeOptions = ref(null);
        
       const nodeResultTypeOptions = ref(null);
        
        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        
        const valueType = ref('O');
        const valueObjectSource = ref('I');
        const valueObjectIsList = ref(false);
        const valueObjectKey = ref(null);
        const valueAttrPath = ref(null);
        const contextKey = ref(null)
        
        const nodeResultCode = ref("");
        
        return {
        	objectSourceOptions,
        	nodeResultTypeOptions,
        	showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,
            
            valueTypeOptions,
            
            valueType,
            valueObjectSource,
            valueObjectIsList,
            valueObjectKey,
            valueAttrPath,
            contextKey,
            
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
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_SOURCE_TYPE_ALL','SPRING_EL_VALUE_TYPE','OBJECT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectSourceOptions = data.data['OBJECT_SOURCE_TYPE_ALL'];
                this.valueTypeOptions = data.data['SPRING_EL_VALUE_TYPE'];
                this.nodeResultTypeOptions = data.data['OBJECT_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	if(this.nodeCfgJson.values == null) {
    		this.nodeCfgJson.values = [];
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
        addValue() {
            var value = {
                   valueType : this.valueType,
                   valueObjectSource : this.valueObjectSource,
                   valueObjectIsList : this.valueObjectIsList,
                   valueObjectKey : this.valueObjectKey,
                   valueAttrPath : this.valueAttrPath,
                   contextKey: this.contextKey
            };
            this.nodeCfgJson.values.push(value);
        },
        deleteValue(row) {
            this.nodeCfgJson.values.splice(row,1);
        }
    }
}
</script>

