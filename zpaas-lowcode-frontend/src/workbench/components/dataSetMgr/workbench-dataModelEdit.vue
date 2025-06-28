<!-- 编辑BI模型对象 -->
<template>
<el-form  v-if="dataModel != null && dataModel.id != null" :model="dataModel" label-width="130px" :rules="validateRules" ref="dataModelForm">
    <el-row :gutter="4">
       <el-col :span="24">
           <el-breadcrumb separator=">">
               <el-breadcrumb-item><span class="title">BI数据对象</span></el-breadcrumb-item>
               <el-breadcrumb-item>BI模型对象</el-breadcrumb-item>
               <el-breadcrumb-item ><span class="title1">{{dataModel.name}}</span></el-breadcrumb-item>
            </el-breadcrumb>
       </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="标识" required prop="id">
                <el-input v-model="dataModel.id" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="编码" required prop="code">
                <el-input v-model="dataModel.code"  size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="名称" required prop="name">
                <el-input v-model="dataModel.name"  size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="类型" required prop="type">
                <el-select v-model="dataModel.type" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in dataModelTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="归属业务域"  required prop="domainId">
                <el-input type="hidden" v-model="dataModel.domainId" size="small" />
                <el-input v-model="businessDomainName" readonly size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="数据源类型" required prop="dataSourceType">
                <el-select v-model="dataModel.dataSourceType" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in dataSourceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="归属数据源" required prop="dataSourceId">
                <el-select v-model="dataModel.dataSourceId" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in dataSources" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="6" v-if="dataModel.type != 'S'">
            <el-form-item label="物理来源对象" required prop="physicalSrcObject">
                <el-input v-model="dataModel.physicalSrcObject" :readonly="true"  size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="2" v-if="dataModel.type != 'S'">
            <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="selectPhysicalObject()" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearPhysicalObject()" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
        <el-col :span="8" v-if="dataModel.type == 'S'">
            <el-form-item label="物理来源对象" required prop="physicalSrcObject">
                <el-input type="textarea" :rowspan="3" v-model="dataModel.physicalSrcObject"  size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="物理对象说明" prop="srcObjectDesc">
                <el-input v-model="dataModel.srcObjectDesc"  size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="16">
            <el-form-item label="描述" prop="description">
                <el-input type="textarea" :rowspan="3" v-model="dataModel.description"  size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="1"></el-col>
        <el-col :span="23">
            <el-link type="primary" size="small" @click="showDataModelMetricsMgrPage()">指标管理</el-link>&nbsp;&nbsp;
            <el-link type="primary" size="small" @click="showDataModelTagMgrPage()">标签管理</el-link>&nbsp;&nbsp;
            <el-link type="primary" size="small" @click="showDataModelFilterMgrPage()">筛选器管理</el-link>&nbsp;&nbsp;
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">&nbsp;</el-col>
    </el-row>

    <el-row :gutter="4">
        <el-col :span="1">
            &nbsp;
        </el-col>
        <el-col :span="5">
            <span class="title" style="font-size: 14px; font-weight: 500;">模型结构：</span>
        </el-col>
        <el-col :span="6">
            <el-link type="primary" size="small" @click="loadDataAndSchema()">加载数据及原始结构</el-link>
        </el-col>
        <el-col :span="12">
            <span class="title" style="font-size: 14px; font-weight: 500;">模型数据：</span>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="dataModel != null">
        <el-col :span="1">

        </el-col>
        <el-col :span="23">
            <el-container  style="height: 40vh">
                <el-aside width="50%">
                    <el-row :gutter="4">
                        <el-table  v-if="dataModel.columns != null" :data="dataModel.columns" stripe style="width: 100%">
                            <el-table-column prop="name" label="字段名称" show-overflow-tooltip width="180">
                                <template #default="scope">
                                    <el-input v-model="scope.row.name"  size="small"/>
                                </template>
                            </el-table-column>
                            <el-table-column prop="code" label="字段编码" show-overflow-tooltip width="160"/>
                            <el-table-column prop="valueType" label="值类型" show-overflow-tooltip width="100"/>
                            <el-table-column prop="columnType" label="类型设置" show-overflow-tooltip width="120">
                                <template #default="scope">
                                    <el-select v-model="scope.row.columnType" class="m-2" placeholder="Select" size="small" clearable>
                                        <template #label="{ label, value }">
                                            <el-tag :type="value == 'D' ? 'primary' : (value == 'M' ? 'success' : (value == 'T' ? 'warning' : (value == 'P' ? 'danger' : 'info')))" effect="dark" size="small" >{{ label }}</el-tag>
                                        </template>
                                        <el-option v-for="item in columnTypeOptions" :key="item.value" :label="item.label" :value="item.value" >
                                            <div class="flex items-center">
                                                <el-tag :type="item.value2" effect="dark" size="small" >{{ item.label }}</el-tag>
                                            </div>
                                        </el-option>
                                    </el-select>
                                </template>
                            </el-table-column>
                            <el-table-column prop="translateType" label="翻译类型" show-overflow-tooltip width="120">
                                <template #default="scope">
                                    <el-select v-model="scope.row.translateType" class="m-2" placeholder="Select" size="small" clearable>
                                        <el-option v-for="item in translateTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                                    </el-select>
                                </template>
                            </el-table-column>
                            <el-table-column prop="optionType" label="翻译选项类型" show-overflow-tooltip width="120">
                                <template #default="scope">
                                    <el-select v-model="scope.row.optionType" class="m-2" placeholder="Select" size="small" clearable>
                                        <el-option v-for="item in optionTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                                    </el-select>
                                </template>
                            </el-table-column>
                            <el-table-column prop="optionCfg" label="翻译选项配置" show-overflow-tooltip width="300">
                                <template #default="scope">
                                    <el-input v-model="scope.row.optionCfg"  size="small"/>
                                </template>
                            </el-table-column>
                        </el-table>
                    </el-row>
                </el-aside>
                <el-aside width="50%">
                    <el-row :gutter="4">
                        <el-col :span="1">
                            &nbsp;
                        </el-col>
                        <el-col :span="22">
                            <el-table  v-if="datas != null && dataModelSchemas != null" :data="datas" stripe style="width: 100%">
                                <el-table-column v-for="(column, index) in dataModelSchemas" :prop="column.columnName" :label="column.columnName" show-overflow-tooltip width="180">
                                    <template #default="scope">
                                        {{ scope.row[column.columnName] }}
                                    </template>
                                </el-table-column>
                            </el-table>
                        </el-col>
                        <el-col :span="1">
                            &nbsp;
                        </el-col>
                    </el-row>
                </el-aside>
            </el-container>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            &nbsp;
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12" class="toolbar-right">
            &nbsp;
        </el-col>
        <el-col :span="8" class="toolbar-right">
            <el-button type="primary" size="small" @click="saveDataModel()">保存</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="deleteDataModel()">删除</el-button>
        </el-col>
        <el-col :span="4" class="toolbar-right">&nbsp;</el-col>
    </el-row>
</el-form>

<!-- 选择数据库表信息 -->
<mdaTableSelect v-if="showMdaTableSelect"  @mdaTableSelected="mdaTableSelected" @hideMdaTableSelectPage="hideMdaTableSelectPage" :showMdaTableSelect="showMdaTableSelect"  :mdaTablesForSelect="mdaTablesForSelect"/>

<!-- 标签管理 -->
<dataModelMetricsMgr v-if="showDataModelMetricsMgr" @hideDataModelMetricsMgrPage="hideDataModelMetricsMgrPage" :showDataModelMetricsMgr="showDataModelMetricsMgr" :dataModel="dataModel" :dataModelMetrics="dataModelMetrics"/>

<!-- 标签管理 -->
<dataModelTagMgr v-if="showDataModelTagMgr" @hideDataModelTagMgrPage="hideDataModelTagMgrPage" :showDataModelTagMgr="showDataModelTagMgr" :dataModel="dataModel" :dataModelTags="dataModelTags"/>

<!-- 标签管理 -->
<dataModelFilterMgr v-if="showDataModelFilterMgr" @hideDataModelFilterMgrPage="hideDataModelFilterMgrPage" :showDataModelFilterMgr="showDataModelFilterMgr" :dataModel="dataModel" :dataModelFilters="dataModelFilters"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import draggable from 'vuedraggable'

import mdaTableSelect from '../assist/workbench-mdaTableSelect.vue'
import dataModelMetricsMgr from './workbench-dataModelMetricsMgr.vue'
import dataModelTagMgr from './workbench-dataModelTagMgr.vue'
import dataModelFilterMgr from './workbench-dataModelFilterMgr.vue'


export default {
    props: [],
    
    emits: ['updateToList', 'deleteFromList'],
    
    setup (props, {attrs, emit, slots}) {
        const updateDataModelToList = (dataModel)=> {
            emit('updateToList', 'dataModel', dataModel);
        };
        
        const deleteDataModelFromList = (dataModel)=> {
            emit('deleteFromList', 'dataModel', dataModel);
        };
        
        return {
        	updateDataModelToList,
        	deleteDataModelFromList
        };
    },
    components: {
        mdaTableSelect,
        draggable,
        dataModelMetricsMgr,
        dataModelTagMgr,
        dataModelFilterMgr
    },
    data() {            
        const dataSourceTypeOptions = ref(null);

        const dataModelTypeOptions = ref(null);

        const columnTypeOptions = ref(null);

        const translateTypeOptions = ref(null);

        const optionTypeOptions = ref(null);

        const dataModel = ref(null);
        const router = useRoute();

        const showMdaTableSelect = ref(false);
        const mdaTablesForSelect = ref(null);
        const dataSources = ref(null);

        const dataModelSchemas = ref(null);
        const datas = ref(null);

        const dataModelMetrics = ref(null);
        const showDataModelMetricsMgr = ref(false);

        const dataModelTags = ref(null);
        const showDataModelTagMgr = ref(false);

        const dataModelFilters = ref(null);
        const showDataModelFilterMgr = ref(false);

        const businessDomainName = ref(null);
        
        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "code": [
                { required: true, message: '编码不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "type": [
                { required: true, message: '类型不能为空！', trigger: 'blur' }
            ],
            "dataSourceType": [
                { required: true, message: '数据源不能为空！', trigger: 'blur' }
            ],
            "dataSourceId": [
                { required: true, message: '归属数据源不能为空！', trigger: 'blur' }
            ],
            "physicalSrcObject": [
                { required: true, message: '物理来源对象不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
            dataModel,
            router,

        	dataSourceTypeOptions,
            dataModelTypeOptions,
            validateRules,
            showMdaTableSelect,
            mdaTablesForSelect,
            dataSources,
            businessDomainName,

            dataModelSchemas,
            datas,
            columnTypeOptions,

            dataModelMetrics,
            showDataModelMetricsMgr,

            dataModelTags,
            showDataModelTagMgr,

            dataModelFilters,
            showDataModelFilterMgr,

            translateTypeOptions,
            optionTypeOptions
        }
    },
    watch: {        
        'router.query': function(val, old){
            if(val != null && val.objectId != null && (old == null || val.objectId != old.objectId)) {
                setTimeout(()=>{
                    this.loadDataModel();
                },100);
            }else if(val == null || val.objectId == null) {
                this.dataModel = null;
            }
        },
        'dataModel.dataSourceType': function(val, old){
            if(old == null) {
                return;
            }
        	this.dataModel.dataSourceId = "";
            this.dataModel.physicalSrcObject = "";
            this.listDataSource();
        },
        'dataModel.dataSourceId': function(val, old){
            if(old == null) {
                return;
            }
        	this.dataModel.physicalSrcObject = "";
        },
        'dataModel.type': function(val, old){
            if(old == null) {
                return;
            }
        	this.dataModel.physicalSrcObject = "";
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['BI_DATASOURCE_TYPE','BI_DATA_MODEL_TYPE','BI_TRANSLATE_TYPE','BI_TRANSLATE_OPTION_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.dataSourceTypeOptions = data.data['BI_DATASOURCE_TYPE'];
                this.dataModelTypeOptions = data.data['BI_DATA_MODEL_TYPE'];
                this.translateTypeOptions = data.data['BI_TRANSLATE_TYPE'];
                this.optionTypeOptions = data.data['BI_TRANSLATE_OPTION_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValuesMore", ['BI_COLUMN_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.columnTypeOptions = data.data['BI_COLUMN_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        this.loadDataModel();
    },
    methods: {
        loadDataModel() {
            var objectId = this.router.query.objectId;
            if(objectId == null) {
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModel/loadWithColumn/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataModel = data.data;
                    if(this.dataModel.metrics == null) {
                        this.dataModel.metrics = [];
                    }
                    if(this.dataModel.tags == null) {
                        this.dataModel.tags = [];
                    }
                    if(this.dataModel.filters == null) {
                        this.dataModel.filters = [];
                    }
                    this.listDataSource();
                    var url = "/lcdp-proxy/lowcode/platform/be/api/businessDomain/byId/" + data.data.domainId;
                    axiosClient.get(url).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.businessDomainName = data.data.name;
                        }
                    });
                }
            });
        },
    	saveDataModel() {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModel/save";
            this.$refs.dataModelForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.dataModel).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.dataModel = data.data;
                            this.updateDataModelToList(this.dataModel);
                            ElMessage(`更新BI模型对象信息成功!`);
                        }
                    }).catch(ex => {
                        ElMessage.error(`更新BI模型对象信息失败: ` + ex);
                    }) ;
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        listDataSource() {
            if(this.dataModel.dataSourceType == "R") {//关系数据库
                var url1 = "/lcdp-proxy/lowcode/platform/be/api/dbSchema/list/" + this.dataModel.systemId ;
                axiosClient.get(url1).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.dataSources = data;
                    }
                });
            }else {
                this.dataSources = null;
            }
        },
        deleteDataModel() {
            ElMessageBox.confirm(
                '该数据模型(' + this.dataModel.name + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModel/delete/" + this.dataModel.id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         this.deleteDataModelFromList(this.dataModel);
                         
                         ElMessage(`删除数据模型(`+ this.dataModel.name +`)成功。`);
                     }
                 }).catch((ex)=>{
                     ElMessage.error(`删除数据模型(`+ this.dataModel.name +`)失败！` +ex);
                 });
             }).catch((ex)=>{ElMessage.error(`删除数据模型(`+ this.dataModel.name +`)失败！` + ex);});
        },
        selectPhysicalObject() {
            if(this.dataModel.dataSourceId == null || this.dataModel.dataSourceId.trim().length == 0) {
                ElMessage(`请先选择归属数据源！`);
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/mda/queryTableList/" + this.dataModel.systemId + "/" + this.dataModel.dataSourceId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.mdaTablesForSelect = data.data;
                    this.showMdaTableSelect = true;
                }
            });
        },
        clearPhysicalObject() {
            this.dataModel.physicalSrcObject = "";
        },
        mdaTableSelected(obj) {
            this.dataModel.physicalSrcObject = obj.tableName;
            this.mdaTablesForSelect = null;
            this.showMdaTableSelect = false;
        },
        hideMdaTableSelectPage(){
             this.mdaTablesForSelect = null;
             this.showMdaTableSelect = false;
        },
        loadDataAndSchema() {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModel/loadDataAndSchema";
            axiosClient.post(url, this.dataModel).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataModelSchemas = data.data.dataModelSchemas;
                    this.datas = data.data.datas;
                    if(this.dataModel.columns == null) {
                        this.dataModel.columns = [];
                    }
                    if(this.dataModelSchemas != null && this.dataModelSchemas.length > 0) {
                        var found = false;
                        for(var i in this.dataModelSchemas) {
                            found = false;
                            for(var j in this.dataModel.columns) {
                                if(this.dataModelSchemas[i].columnName == this.dataModel.columns[j].code) {
                                    found = true;
                                    break;
                                }
                            }
                            if(!found) {
                                this.dataModel.columns.push({
                                    id: '',
                                    code: this.dataModelSchemas[i].columnName,
                                    name: this.dataModelSchemas[i].remarks,
                                    columnType: '',
                                    valueType: this.dataModelSchemas[i].typeName,
                                    translateType: '',
                                    optionType: '',
                                    optionCfg: '',
                                    tenantId: this.dataModel.tenantId,
                                    systemId: this.dataModel.systemId
                                });
                               
                            }
                        }
                    }
                    
                }
            });
        },
        showDataModelMetricsMgrPage() {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModelMetrics/listByDataModel/" + this.dataModel.id;
            axiosClient.post(url).then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataModelMetrics = data.data;
                    this.showDataModelMetricsMgr = true;
                }
            }).catch((ex)=>{
                ElMessage.error(`加载指标列表失败！` +ex);
            });
        },
        hideDataModelMetricsMgrPage() {
            this.dataModelMetrics = null;
            this.showDataModelMetricsMgr = false;
        },
        showDataModelTagMgrPage() {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModelTag/listByDataModel/" + this.dataModel.id;
            axiosClient.post(url).then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataModelTags = data.data;
                    this.showDataModelTagMgr = true;
                }
            }).catch((ex)=>{
                ElMessage.error(`加载标签列表失败！` +ex);
            });
        },
        hideDataModelTagMgrPage() {
            this.dataModelTags = null;
            this.showDataModelTagMgr = false;
        },
        showDataModelFilterMgrPage() {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModelFilter/listByDataModel/" + this.dataModel.id;
            axiosClient.post(url).then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataModelFilters = data.data;
                    this.showDataModelFilterMgr = true;
                }
            }).catch((ex)=>{
                ElMessage.error(`加载筛选器列表失败！` +ex);
            });
        },
        hideDataModelFilterMgrPage() {
            this.dataModelFilters = null;
            this.showDataModelFilterMgr = false;
        }
    }
}
</script>

<style scoped>
.layout-container-main .toolbar-right {
  display: inline-flex;
  align-items: center;
  justify-content: right; 
  height: 100%;
  right: 20px;
}
.toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
  height: 100%;
  right: 20px;
}
</style>