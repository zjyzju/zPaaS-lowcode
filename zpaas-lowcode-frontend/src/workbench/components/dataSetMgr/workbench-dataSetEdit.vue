<!-- 编辑数据集对象 -->
<template>
<el-form  v-if="dataSet != null && dataSet.id != null" :model="dataSet" label-width="130px" :rules="validateRules" ref="dataSetForm">
    <el-row :gutter="4">
       <el-col :span="24">
           <el-breadcrumb separator=">">
               <el-breadcrumb-item><span class="title">BI数据对象</span></el-breadcrumb-item>
               <el-breadcrumb-item>数据集</el-breadcrumb-item>
               <el-breadcrumb-item ><span class="title1">{{dataSet.name}}</span></el-breadcrumb-item>
            </el-breadcrumb>
       </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="标识" required prop="id">
                <el-input v-model="dataSet.id" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="编码" required prop="code">
                <el-input v-model="dataSet.code"  size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="名称" required prop="name">
                <el-input v-model="dataSet.name"  size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="归属业务域"  required prop="domainId">
                <el-input type="hidden" v-model="dataSet.domainId" size="small" />
                <el-input v-model="businessDomainName" readonly size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="描述" prop="description">
                <el-input type="textarea" :rowspan="3" v-model="dataSet.description"  size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8"></el-col>
    </el-row>

    <el-row :gutter="4">
        <el-col :span="1"></el-col>
        <el-col :span="5">
            <span class="title" style="font-size: 14px; font-weight: 500;">数据模型：{{ currentDataModel == null ? "" : currentDataModel.name }}</span>
        </el-col>
        <el-col :span="3">
            <el-link type="primary" size="small" @click="showDataModelSelectPage()">选择数据模型</el-link>&nbsp;&nbsp;
        </el-col>
        <el-col :span="1"></el-col>
        <el-col :span="14">
            <span class="title" style="font-size: 14px; font-weight: 500;">数据集明细内容：</span>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            &nbsp;
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="1">

        </el-col>
        <el-col :span="8">
            <el-container  style="height: 50vh">
                <el-aside width="100%">
                    <el-table @selection-change="handleDataModelColumnSelectionChange" :data="dataModelSchemas" stripe style="width: 100%;">
                        <el-table-column type="selection" width="30" />
                        <el-table-column prop="code" label="编码" show-overflow-tooltip width="140"/>
                        <el-table-column prop="name" label="名称" show-overflow-tooltip width="140"/>
                        <el-table-column prop="type" label="类型" show-overflow-tooltip width="110">
                            <template #default="scope">
                                <el-select v-model="scope.row.type" class="m-2" placeholder="Select" size="small" clearable disabled>
                                    <template #label="{ label, value }">
                                        <el-tag :type="value == 'D' || value == 'T' ? 'primary' : (value == 'M' || value == 'Q' ? 'success' : (value == 'L' || value == 'F' ? 'warning' : (value == 'P' ? 'danger' : 'info')))" effect="dark" size="small" >{{ label }}</el-tag>
                                    </template>
                                    <el-option v-for="item in columnTypeOptions" :key="item.value" :label="item.label" :value="item.value" >
                                        <div class="flex items-center">
                                            <el-tag :type="item.value2" effect="dark" size="small" >{{ item.label }}</el-tag>
                                        </div>
                                    </el-option>
                                </el-select>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-aside>
            </el-container>
        </el-col>
        <el-col :span="1" style="display: inline-flex; align-items: center; vertical-align: middle;">
            <el-row :gutter="4">
                <el-col :span="24">
                    &nbsp;<el-link type="primary" size="small" @click="addToDataSet()">增加<el-icon><ArrowRight /></el-icon></el-link>
                    <br/><br/>
                    &nbsp;<el-link type="primary" size="small" @click="removeFromDataSet()">移除<el-icon><ArrowLeft /></el-icon></el-link>
                </el-col>
            </el-row>
            
        </el-col>
        <el-col :span="14">
            <el-row :gutter="4">
                <el-col :span="24">
                    <el-container  style="height: 30vh">
                        <el-aside width="100%">
                            <el-table @selection-change="handleDataSetDetailSelectionChange" v-if="dataSet != null" :data="dataSet.details" stripe style="width: 100%;">
                                <el-table-column type="selection" width="30" />
                                <el-table-column prop="content.code" label="编码" show-overflow-tooltip width="150"/>
                                <el-table-column prop="content.name" label="名称" show-overflow-tooltip width="150"/>
                                <el-table-column prop="detailContentAlias" label="别名" show-overflow-tooltip width="120">
                                    <template #default="scope">
                                        <el-input v-model="scope.row.detailContentAlias"  size="small"/>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="type" label="类型" show-overflow-tooltip width="110">
                                    <template #default="scope">
                                        <el-select v-model="scope.row.detailType" class="m-2" placeholder="Select" size="small" clearable disabled>
                                            <template #label="{ label, value }">
                                                <el-tag :type="value == 'D' || value == 'T' ? 'primary' : (value == 'M' || value == 'Q' ? 'success' : (value == 'L' || value == 'F' ? 'warning' : (value == 'P' ? 'danger' : 'info')))" effect="dark" size="small" >{{ label }}</el-tag>
                                            </template>
                                            <el-option v-for="item in columnTypeOptions" :key="item.value" :label="item.label" :value="item.value" >
                                                <div class="flex items-center">
                                                    <el-tag :type="item.value2" effect="dark" size="small" >{{ item.label }}</el-tag>
                                                </div>
                                            </el-option>
                                        </el-select>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="dataModel.name" label="归属模型" show-overflow-tooltip width="140"/>
                            </el-table>
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
                <el-col :span="24">
                    <span class="title" style="font-size: 14px; font-weight: 500;">数据集构成模型：</span>
                </el-col>
            </el-row>
            <el-row :gutter="4">
                <el-col :span="24">
                    <el-container  style="height: 20vh">
                        <el-aside width="100%">
                            <el-table v-if="dataSet != null" :data="dataSet.compositions" stripe style="width: 100%;">
                                <el-table-column prop="code" label="模型编码" show-overflow-tooltip width="160">
                                    <template #default="scope">
                                        {{ scope.row.dataModel.code }}
                                    </template>
                                </el-table-column>
                                <el-table-column prop="name" label="模型名称" show-overflow-tooltip width="160">
                                    <template #default="scope">
                                        {{ scope.row.dataModel.name }}
                                    </template>
                                </el-table-column>
                                <el-table-column label="设置JOIN条件" show-overflow-tooltip width="140">
                                    <template #default="scope">
                                        <el-link  type="primary" size="small" v-if="Object.keys(dataSet.compositions).length > 1" @click="showDataSetCompositionJoinSetPage(scope.row)">设置</el-link>
                                    </template>
                                </el-table-column>
                            </el-table>
                        </el-aside>
                    </el-container>
                </el-col>
            </el-row>
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
            <el-button type="primary" size="small" @click="saveDataSet()">保存</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="deleteDataSet()">删除</el-button>
        </el-col>
        <el-col :span="4" class="toolbar-right">&nbsp;</el-col>
    </el-row>
</el-form>
<!-- 数据模型选择 -->
<dataModelSelect v-if="showDataModelSelect" @dataModelSelected="dataModelSelected" @hideDataModelSelectPage="hideDataModelSelectPage" :showDataModelSelect="showDataModelSelect" :dataModelsForSelect="dataModelsForSelect"/>

<!-- 数据模型选择 -->
<dataSetCompositionJoinSet v-if="showDataSetCompositionJoinSet" @hideDataSetCompositionJoinSetPage="hideDataSetCompositionJoinSetPage" :showDataSetCompositionJoinSet="showDataSetCompositionJoinSet" :dataSetComposition="dataSetComposition" :dataSet="dataSet"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'
import draggable from 'vuedraggable'

import dataModelSelect from './workbench-dataModelSelect.vue'
import dataSetCompositionJoinSet from './workbench-dataSetCompositionJoinSet.vue'

export default {
    props: [],
    
    emits: ['updateToList', 'deleteFromList'],
    
    setup (props, {attrs, emit, slots}) {
        const updateDataSetToList = (dataSet)=> {
            emit('updateToList', 'dataSet', dataSet);
        };
        
        const deleteDataSetFromList = (dataSet)=> {
            emit('deleteFromList', 'dataSet', dataSet);
        };
        
        return {
        	updateDataSetToList,
        	deleteDataSetFromList
        };
    },
    components: {
        draggable,
        dataModelSelect,
        dataSetCompositionJoinSet
    },
    data() {            
        const columnTypeOptions = ref(null);

        const dataSet = ref(null);
        const router = useRoute();

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "code": [
                { required: true, message: '编码不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ]
        });

        const showDataModelSelect = ref(false);
        const dataModelsForSelect = ref(null);
        const currentDataModel = ref(null);

        const dataModelSchemas = ref(null);

        const dataModelColumnSelected = ref(null);
        const dataSetDetailSelected = ref(null);

        const showDataSetCompositionJoinSet = ref(false);
        const dataSetComposition = ref(null);

        const businessDomainName = ref(null);
        
        return {
            dataSet,
            router,

            showDataModelSelect,
            dataModelsForSelect,
            currentDataModel,

            dataModelSchemas,

            dataModelColumnSelected,
            dataSetDetailSelected,

            showDataSetCompositionJoinSet,
            dataSetComposition,

        	validateRules,
            columnTypeOptions,
            businessDomainName
        }
    },
    watch: {        
        'router.query': function(val, old){
            if(val != null && val.objectId != null && (old == null || val.objectId != old.objectId)) {
                setTimeout(()=>{
                    this.loadDataSet();
                },100);
            }else if(val == null || val.objectId == null) {
                this.dataSet = null;
            }
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValuesMore", ['BI_COLUMN_TYPE_DATASET']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.columnTypeOptions = data.data['BI_COLUMN_TYPE_DATASET'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        this.loadDataSet();
    },
    methods: {
        loadDataSet() {
            var objectId = this.router.query.objectId;
            if(objectId == null) {
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataSet/loadAll/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataSet = data.data;
                    if(this.dataSet != null && this.dataSet.details != null && this.dataSet.compositions != null) {
                        for(var i in this.dataSet.details) {
                            for(var j in this.dataSet.compositions) {
                                if(this.dataSet.details[i].content.dataModelId == this.dataSet.compositions[j].dataModelId) {
                                    this.dataSet.details[i].dataModel = this.dataSet.compositions[j].dataModel;
                                    break;
                                }
                            }
                        }
                    }
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
    	saveDataSet() {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataSet/save";
            this.$refs.dataSetForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.dataSet).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.dataSet = data.data;
                            if(this.dataSet != null && this.dataSet.details != null && this.dataSet.compositions != null) {
                                for(var i in this.dataSet.details) {
                                    for(var j in this.dataSet.compositions) {
                                        if(this.dataSet.details[i].content.dataModelId == this.dataSet.compositions[j].dataModelId) {
                                            this.dataSet.details[i].dataModel = this.dataSet.compositions[j].dataModel;
                                            break;
                                        }
                                    }
                                }
                            }
                            this.updateDataSetToList(this.dataSet);
                            ElMessage(`更新数据集信息成功!`);
                        }
                    }).catch(ex => {
                        ElMessage.error(`更新数据集信息失败: ` + ex);
                    }) ;
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        deleteDataSet() {
            ElMessageBox.confirm(
                '该数据集(' + this.dataSet.name + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/bi/api/dataSet/delete/" + this.dataSet.id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         this.deleteDataSetFromList(this.dataSet);
                         
                         ElMessage(`删除数据集(`+ this.dataSet.name +`)成功。`);
                     }
                 }).catch((ex)=>{
                     ElMessage.error(`删除数据集(`+ this.dataSet.name +`)失败！` +ex);
                 });
             }).catch((ex)=>{ElMessage.error(`删除数据集(`+ this.dataSet.name +`)失败！` + ex);});
        },
        showDataModelSelectPage() {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModel/list/" + this.dataSet.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataModelsForSelect = data.data;
                    this.showDataModelSelect = true;
                }
            }).catch(ex=>{
                ElMessage.error(`加载数据模型列表失败！` + ex);
            });
        },
        dataModelSelected(obj) {
            if(obj != null && obj.id != null) {
                var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModel/loadAll/" + obj.id;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.currentDataModel = data.data;
                        this.hideDataModelSelectPage();
                        if(this.currentDataModel != null) {
                            this.dataModelSchemas = [];
                            for(var i in this.currentDataModel.columns) {
                                var found = false;
                                for(var j in this.dataSet.details) {
                                    if(this.dataSet.details[j].detailType == this.currentDataModel.columns[i].columnType &&
                                        this.dataSet.details[j].detailContentId == this.currentDataModel.columns[i].id) {
                                        found = true;
                                        break;
                                    }
                                }
                                if(found) {
                                    continue;
                                }
                                this.dataModelSchemas.push({
                                    id: this.currentDataModel.columns[i].id,
                                    code: this.currentDataModel.columns[i].code,
                                    name: this.currentDataModel.columns[i].name,
                                    type: this.currentDataModel.columns[i].columnType
                                });
                            }

                            for(var i in this.currentDataModel.metrics) {
                                var found = false;
                                for(var j in this.dataSet.details) {
                                    if(this.dataSet.details[j].detailType == 'Q' &&
                                        this.dataSet.details[j].detailContentId == this.currentDataModel.metrics[i].id) {
                                        found = true;
                                        break;
                                    }
                                }
                                if(found) {
                                    continue;
                                }
                                this.dataModelSchemas.push({
                                    id: this.currentDataModel.metrics[i].id,
                                    code: this.currentDataModel.metrics[i].code,
                                    name: this.currentDataModel.metrics[i].name,
                                    type: 'Q'
                                });
                            }

                            for(var i in this.currentDataModel.tags) {
                                var found = false;
                                for(var j in this.dataSet.details) {
                                    if(this.dataSet.details[j].detailType == 'L' &&
                                        this.dataSet.details[j].detailContentId == this.currentDataModel.tags[i].id) {
                                        found = true;
                                        break;
                                    }
                                }
                                if(found) {
                                    continue;
                                }
                                this.dataModelSchemas.push({
                                    id: this.currentDataModel.tags[i].id,
                                    code: this.currentDataModel.tags[i].code,
                                    name: this.currentDataModel.tags[i].name,
                                    type: 'L'
                                });
                            }

                            for(var i in this.currentDataModel.filters) {
                                var found = false;
                                for(var j in this.dataSet.details) {
                                    if(this.dataSet.details[j].detailType == 'F' &&
                                        this.dataSet.details[j].detailContentId == this.currentDataModel.filters[i].id) {
                                        found = true;
                                        break;
                                    }
                                }
                                if(found) {
                                    continue;
                                }
                                this.dataModelSchemas.push({
                                    id: this.currentDataModel.filters[i].id,
                                    code: this.currentDataModel.filters[i].code,
                                    name: this.currentDataModel.filters[i].name,
                                    type: 'F'
                                });
                            }
                        }
                    }
                }).catch(ex=>{
                    ElMessage.error(`加载数据模型全量信息失败！` + ex);
                });
            }
        },
        hideDataModelSelectPage() {
            this.dataModelsForSelect = null;
            this.showDataModelSelect = false;
        },
        showDataSetCompositionJoinSetPage(composition) {
            this.dataSetComposition = composition;
            this.showDataSetCompositionJoinSet = true;
        },
        hideDataSetCompositionJoinSetPage() {
            this.dataSetComposition = null;
            this.showDataSetCompositionJoinSet = false;
        },
        handleDataModelColumnSelectionChange(columns) {
            this.dataModelColumnSelected = columns;
        },
        handleDataSetDetailSelectionChange(columns) {
            this.dataSetDetailSelected = columns;
        },
        addToDataSet() {
            if(this.dataModelColumnSelected == null || this.dataModelColumnSelected.length == 0) {
                return;
            }
            if(this.dataSet.details == null) {
                this.dataSet.details = [];
            }
            if(this.dataSet.compositions == null) {
                this.dataSet.compositions = [];
            }
            for(var i in this.dataModelColumnSelected) {
                this.dataSet.details.push({
                    id: '',
                    detailType:  this.dataModelColumnSelected[i].type,
                    detailContentId: this.dataModelColumnSelected[i].id,
                    detailContentAlias: '',
                    tenantId: this.dataSet.tenantId,
                    systemId: this.dataSet.systemId,
                    content: this.dataModelColumnSelected[i],
                    dataModel: {
                        id: this.currentDataModel.id,
                        code: this.currentDataModel.code,
                        name: this.currentDataModel.name
                    }
                })
                for(var j in this.dataModelSchemas) {
                    if(this.dataModelSchemas[j].id == this.dataModelColumnSelected[i].id) {
                        this.dataModelSchemas.splice(j, 1);
                    }
                }
                var found = false;
                for(var j in this.dataSet.compositions) {
                    if(this.dataSet.compositions[j].dataModelId == this.currentDataModel.id) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    this.dataSet.compositions.push({
                        dataModelId: this.currentDataModel.id,
                        tenantId: this.dataSet.tenantId,
                        systemId: this.dataSet.systemId,
                        dataModel: {
                            id: this.currentDataModel.id,
                            code: this.currentDataModel.code,
                            name: this.currentDataModel.name
                        }
                    });
                }
            }
        },
        removeFromDataSet() {
            if(this.dataSetDetailSelected == null || this.dataSetDetailSelected.length == 0) {
                return;
            }
            if(this.dataModelSchemas == null) {
                this.dataModelSchemas = [];
            }
            for(var i in this.dataSetDetailSelected) {
                if(this.currentDataModel != null && this.currentDataModel.id != null && this.currentDataModel.id == this.dataSetDetailSelected[i].dataModel.id) {
                    this.dataModelSchemas.push({
                        id: this.dataSetDetailSelected[i].detailContentId,
                        code: this.dataSetDetailSelected[i].content.code,
                        name: this.dataSetDetailSelected[i].content.name,
                        type:  this.dataSetDetailSelected[i].detailType,
                    });
                }
                
                for(var j in this.dataSet.details) {
                    if(this.dataSet.details[j].detailContentId == this.dataSetDetailSelected[i].detailContentId && 
                            this.dataSet.details[j].detailType == this.dataSetDetailSelected[i].detailType) {
                        this.dataSet.details.splice(j, 1);
                    }
                }
            }
            
            for(var index in this.dataSet.compositions) {
                var found =false;
                for(var j in this.dataSet.details) {
                    if(this.dataSet.details[j].dataModel.id == this.dataSet.compositions[index].dataModelId) {
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    this.dataSet.compositions.splice(index, 1);
                }
            }
            if(this.dataSet.compositions.length == 1) {
                this.dataSet.compositions[0].compositionJoins = [];
            }
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