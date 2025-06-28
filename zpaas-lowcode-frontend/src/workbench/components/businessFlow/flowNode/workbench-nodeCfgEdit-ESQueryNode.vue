<!-- ES查询类节点-节点配置信息-编辑页 -->

<template v-if="showNodeCfgEdit == 'ESQueryNode' && nodeCfgJson != null && nodeCfgJson.isListResult != null">
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="ES服务器">
                <el-select v-model="nodeCfgJson.resourceId" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in esResources" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>
	<el-row :gutter="4">
        <el-col :span="16">
             <el-form-item label="SQL标识">
                <el-input v-model="nodeCfgJson.sqlId" readonly size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="selectSql()" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearSql()" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
    </el-row>
	<el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="SQL语句">
                <el-input v-model="nodeCfgJson.sqlSentence" type="textarea" :rows="4"  size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="16">
            <el-form-item label="是否分页">
                <el-checkbox v-model="nodeCfgJson.pageFlag" label="结果分页" />
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
    
    
<!-- 选择对象关系映射信息 -->
<sqlManagementSelect v-if="showSqlSelect"  @sqlSelected="sqlSelected" @hideSqlSelectPage="hideSqlSelectPage" :showSqlSelect="showSqlSelect"  :sqlsForSelect="sqlsForSelect"/>
<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideDomainObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>
<!-- 选择值传递对象信息 -->
<valueObjectSelect v-if="showValueObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideValueObjectSelectPage="hideValueObjectSelectPage" :showValueObjectSelect="showValueObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

import sqlManagementSelect from '../../dbMgr/workbench-sqlManagementSelect.vue'
import domainObjectSelect from '../../domainModel/workbench-domainObjectSelect.vue'
import valueObjectSelect from '../../domainModel/workbench-valueObjectSelect.vue'

export default {
    props: ['showNodeCfgEdit','nodeCfgJson','systemId'],
    
    emits: ['updateNodeCfgJson'],
        
    setup (props, {attrs, emit, slots}) {
        const updateNodeCfgJson = (json)=> {
            emit('updateNodeCfgJson', json);
        };
        
        return {
        	updateNodeCfgJson
        };
    },
    components: {
    	sqlManagementSelect,
    	domainObjectSelect,
    	valueObjectSelect
    },
    data() {            
    	const nodeResultTypeOptions = ref(null);
    	
        const showSqlSelect = ref(false);
        const sqlsForSelect = ref(null);
        const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        const esResources = ref([]);
        
        const nodeResultCode = ref("");
        
        return {
        	showSqlSelect,
        	sqlsForSelect,
        	nodeResultTypeOptions,
        	showDomainObjectSelect,
        	showValueObjectSelect,
        	managedObjectsForSelect,
        	esResources,
        	
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
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.nodeResultTypeOptions = data.data['OBJECT_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	var url = "/lcdp-proxy/lowcode/platform/be/api/serverResource/list/" + this.systemId + "/E";
        axiosClient.get(url).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.esResources = data.data;
                if(this.esResources != null && this.esResources.length > 0 && (this.nodeCfgJson.resourceId == null || this.nodeCfgJson.resourceId == "") ) {
                    this.nodeCfgJson.resourceId = this.esResources[0].id;
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
    	selectSql() {
    		if(this.nodeCfgJson == null) {
    			return;
    		}
    		var url = "/lcdp-proxy/lowcode/platform/be/api/sqlManagement/list/" + this.systemId + "/E/" + this.nodeCfgJson.resourceId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.sqlsForSelect = data.data;
                    this.showSqlSelect = true;
                }
            });
    	},
    	hideSqlSelectPage() {
    		this.showSqlSelect = false;
    		this.sqlsForSelect = null;
    	
    	},
    	sqlSelected(sql) {
    		console.log(sql);
    		this.nodeCfgJson.sqlId = sql.id;
            this.nodeCfgJson.sqlSentence = sql.sqlSentence;
            this.showSqlSelect = false;
            this.sqlsForSelect = null;
    	},
    	clearSql() {
    		this.nodeCfgJson.sqlId = "";
    		this.nodeCfgJson.sqlSentence = "";
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

