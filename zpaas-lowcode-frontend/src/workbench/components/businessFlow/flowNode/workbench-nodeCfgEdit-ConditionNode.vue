<!-- ConditionNode-节点配置信息-编辑页 -->

<template v-if="showNodeCfgEdit == 'ConditionNode' && nodeCfgJson != null && nodeCfgJson.isListResult != null">
	<el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="条件表达式">
                <el-input v-model="nodeCfgJson.conditionExpr" placeholder="（${1} and ${2}）or (${3} and ${4} and ${5})" size="small" />
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
        <el-col :span="1">
            <el-divider direction="vertical" border-style="dashed" style="height:100%;margin-top: 1px;margin-bottom: 1px" ></el-divider>
        </el-col>
        <el-col :span="22">
            <el-row :gutter="4">
                <el-col :span="24">
                    <el-divider border-style="dashed" style="margin-top: 1px;margin-bottom: 10px" >设置条件</el-divider>
                </el-col>
            </el-row>
		    <el-row :gutter="4">
		        <el-col :span="14">
		            <el-form-item label="条件对象来源" label-width='100px'>
		                <el-select v-model="conditionObjectSource" class="m-2" placeholder="Select" size="small">
		                    <el-option v-for="item in objectSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
		                </el-select>
		            </el-form-item>
		        </el-col>
                <el-col :span="3">
                    <el-form-item label="" label-width="10px">
                        <el-link  type="primary" @click="showAvailableDataSelectPage(objectSourceOptions, (contextType, contextKey)=>{this.conditionObjectSource=contextType;this.conditionObjectKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
                    </el-form-item>
                </el-col>
		        <el-col :span="7">
		            &nbsp;&nbsp;<el-checkbox v-model="conditionObjectIsList" label="是否列表" />
		        </el-col>
		    </el-row>
		    <el-row :gutter="4">
		        <el-col :span="14">
		            <el-form-item label="条件对象 Key" label-width='100px'>
		                <el-input v-model="conditionObjectKey" size="small" />
		            </el-form-item>
		        </el-col>
		        <el-col :span="10">
		            <el-form-item label="属性" label-width='45px'>
		                <el-input v-model="conditionAttrPath" size="small" />
		            </el-form-item>
		        </el-col>
		    </el-row>
		   <el-row :gutter="4">
		        <el-col :span="14">
		            <el-form-item label="比 较 操 作 符" label-width='100px'>
		                <el-select v-model="conditionOperator" clearable class="m-2" placeholder="Select" size="small">
		                    <el-option v-for="item in conditionOperatorOptions" :key="item.value" :label="item.label" :value="item.value" />
		                </el-select>
		            </el-form-item>
		        </el-col>
		        <el-col :span="10">
		            <el-form-item label="类型" label-width='45px'>
		                <el-select v-model="compareType" clearable class="m-2" placeholder="Select" size="small">
		                    <el-option v-for="item in compareTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
		                </el-select>
		            </el-form-item>
		        </el-col>
		    </el-row>
		    <el-row :gutter="4">
		        <el-col :span="14">
		            <el-form-item label="值 对 象 来 源" label-width='100px'>
		                <el-select v-model="valueObjectSource" clearable class="m-2" placeholder="Select" size="small">
		                    <el-option v-for="item in objectSourceOptions" :key="item.value" :label="item.label" :value="item.value" />
		                </el-select>
		            </el-form-item>
		        </el-col>
                <el-col :span="3">
                    <el-form-item label="" label-width="10px">
                        <el-link  type="primary" @click="showAvailableDataSelectPage(objectSourceOptions, (contextType, contextKey)=>{this.valueObjectSource=contextType;this.valueObjectKey=contextKey;})" ><label>选择</label></el-link>&nbsp;
                    </el-form-item>
                </el-col>
		        <el-col :span="7">
		            &nbsp;&nbsp;<el-checkbox v-model="valueObjectIsList" label="是否列表" />
		        </el-col>
		    </el-row>
		    <el-row :gutter="4">
		        <el-col :span="14">
		            <el-form-item label="值 对 象  Key" label-width='100px'>
		                <el-input v-model="valueObjectKey" size="small" />
		            </el-form-item>
		        </el-col>
		        <el-col :span="10">
		            <el-form-item label="属性"  label-width='45px'>
		                <el-input v-model="valueAttrPath"  size="small" />
		            </el-form-item>
		        </el-col>
		    </el-row>
		     <el-row :gutter="4">
                <el-col :span="20">
                    &nbsp;
                </el-col>
                <el-col :span="4">                  
                    <div style="margin-bottom: 5px;margin-top: 1px;margin-left: 5px;">
                        <el-button type="default" size="small" @click="addCondition()">添加</el-button>
                    </div>
                </el-col>
            </el-row>
		    <el-row :gutter="4">
		        <el-col :span="24">
		        <el-scrollbar  height="200px">
		            <el-table ref="conditionsTable" :data="nodeCfgJson.conditions" stripe style="width: 100%">
		                <el-table-column fixed="left" label="删除" width="60">
		                    <template  #default="scope">
		                        <el-button link type="primary" size="small" @click.prevent="deleteCondition(scope.$index)" >X</el-button>
		                    </template>
					        
					    </el-table-column>
		                <el-table-column  label="条件对象">
		                    <el-table-column prop="conditionObjectSource" label="来源" width="95" >
                                <template #default="scope">
                                    <span v-if="scope.row['conditionObjectSource'] == 'P'">过程数据</span>
                                    <span v-else-if="scope.row['conditionObjectSource'] == 'I'">输入参数</span>
                                    <span v-else-if="scope.row['conditionObjectSource'] == 'N'">预处理对象</span>
                                    <span v-else-if="scope.row['conditionObjectSource'] == 'D'">领域对象</span>
                                    <span v-else-if="scope.row['conditionObjectSource'] == 'O'">属主对象</span>
                                    <span v-else-if="scope.row['conditionObjectSource'] == 'F'">固定值</span>
                                    <span v-else>{{ scope.row['conditionObjectSource'] }}</span>
                                </template>
                            </el-table-column>
		                    <el-table-column prop="conditionObjectIsList" label="列表" width="55" >
                                <template #default="scope">
                                    <span v-if="scope.row['conditionObjectIsList'] == true">是</span>
                                    <span v-else-if="scope.row['conditionObjectIsList'] == false">否</span>
                                    <span v-else>{{ scope.row['conditionObjectIsList'] }}</span>
                                </template>
                            </el-table-column>
		                    <el-table-column prop="conditionObjectKey" label="Key" width="160" />
		                    <el-table-column prop="conditionAttrPath" label="属性" width="140" />
		                </el-table-column>
		                
		                <el-table-column  label="比较类型">
		                    <el-table-column prop="conditionOperator" label="比较" width="80" >
                                <template #default="scope">
                                    <span v-if="scope.row['conditionOperator'] == 'EQ'">等于</span>
                                    <span v-else-if="scope.row['conditionOperator'] == 'NE'">不等于</span>
                                    <span v-else-if="scope.row['conditionOperator'] == 'GT'">大于</span>
                                    <span v-else-if="scope.row['conditionOperator'] == 'GE'">大于等于</span>
                                    <span v-else-if="scope.row['conditionOperator'] == 'LT'">小于</span>
                                    <span v-else-if="scope.row['conditionOperator'] == 'LE'">小于等于</span>
                                    <span v-else>{{ scope.row['conditionOperator'] }}</span>
                                </template>
                            </el-table-column>
		                    <el-table-column prop="compareType" label="类型" width="110" >
                                <template #default="scope">
                                    <span v-if="scope.row['compareType'] == 'S'">字符串比较</span>
                                    <span v-else-if="scope.row['compareType'] == 'B'">boolean比较</span>
                                    <span v-else-if="scope.row['compareType'] == 'D'">日期比较</span>
                                    <span v-else-if="scope.row['compareType'] == 'N'">数值比较</span>
                                    <span v-else-if="scope.row['compareType'] == 'C'">数组大小比较</span>
                                    <span v-else-if="scope.row['compareType'] == 'L'">长度比较</span>
                                    <span v-else-if="scope.row['compareType'] == 'H'">是否包含</span>
                                    <span v-else-if="scope.row['compareType'] == 'O'">是否对象</span>
                                    <span v-else-if="scope.row['compareType'] == 'A'">是否数组</span>
                                    <span v-else>{{ scope.row['compareType'] }}</span>
                                </template>
                            </el-table-column>
		                </el-table-column>
		                
		                <el-table-column  label="值对象">
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
		                </el-table-column>
		                
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
    	const conditionOperatorOptions = ref(null);
        
        const nodeResultTypeOptions = ref(null);
        const compareTypeOptions = ref(null);
        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        
        const conditionObjectSource = ref('I');
        const conditionObjectIsList = ref(false);
        const conditionObjectKey = ref('');
        const conditionAttrPath = ref('');
        const conditionOperator = ref('EQ');
        const compareType = ref('S');
        const valueObjectSource = ref('I');
        const valueObjectIsList = ref(false);
        const valueObjectKey = ref('');
        const valueAttrPath = ref('');
        
        const nodeResultCode = ref("");
        
        return {
        	objectSourceOptions,
        	nodeResultTypeOptions,
        	showDomainObjectSelect,
            showValueObjectSelect,
            managedObjectsForSelect,
            
            compareTypeOptions,
            conditionOperatorOptions,
            
            conditionObjectSource,
            conditionObjectIsList,
            conditionObjectKey,
            conditionAttrPath,
            conditionOperator,
            compareType,
            valueObjectSource,
            valueObjectIsList,
            valueObjectKey,
            valueAttrPath,
            
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
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_SOURCE_TYPE_ALL','PUB_COMPARE_TYPE','OBJECT_TYPE','COMPARE_TYPE_BUSINESS']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectSourceOptions = data.data['OBJECT_SOURCE_TYPE_ALL'];
                this.conditionOperatorOptions = data.data['PUB_COMPARE_TYPE'];
                this.nodeResultTypeOptions = data.data['OBJECT_TYPE'];
                this.compareTypeOptions = data.data['COMPARE_TYPE_BUSINESS'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	if(this.nodeCfgJson.conditions == null) {
    		this.nodeCfgJson.conditions = [];
    	}
    	if((this.nodeCfgJson.nodeResultType == "D" || this.nodeCfgJson.nodeResultType == "V")  && this.nodeCfgJson.nodeResultClass != null && this.nodeCfgJson.nodeResultClass != "") {
            var queryCodeUrl = "/lcdp-proxy/lowcode/platform/be/api/managedObject/queryObjectCode/" + this.nodeCfgJson.nodeResultType + "/" + this.nodeCfgJson.nodeResultClass;
            axiosClient.get(queryCodeUrl).then((response) => {
                var data = response.data; 
                this.nodeResultCode = data;
            });
        }
    },     
    methods: {
    	selectNodeResultClass() {
            if("D" == this.nodeCfgJson.nodeResultType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    console.log(data);
                    this.managedObjectsForSelect = data;
                    this.showDomainObjectSelect = true;
                });
            }else if("V" == this.nodeCfgJson.nodeResultType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    console.log(data);
                    this.managedObjectsForSelect = data;
                    this.showValueObjectSelect = true;
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
        addCondition() {
            var condition = {
                   conditionObjectSource : this.conditionObjectSource,
                   conditionObjectIsList : this.conditionObjectIsList,
                   conditionObjectKey : this.conditionObjectKey,
                   conditionAttrPath : this.conditionAttrPath,
                   conditionOperator : this.conditionOperator,
                   compareType : this.compareType,
                   valueObjectSource : this.valueObjectSource,
                   valueObjectIsList : this.valueObjectIsList,
                   valueObjectKey : this.valueObjectKey,
                   valueAttrPath : this.valueAttrPath
            };
            this.nodeCfgJson.conditions.push(condition);
        },
        deleteCondition(row) {
            this.nodeCfgJson.conditions.splice(row,1);
        }
    }
}
</script>

