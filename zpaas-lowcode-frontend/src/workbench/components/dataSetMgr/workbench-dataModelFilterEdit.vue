<!-- 筛选器编辑 -->

<template>
<el-dialog v-model="showFlag" title="筛选器编辑" width="65vw" top="20px">
    <template #header>
        <span class="title">筛选器编辑</span>
    </template>
    <el-form  :model="dataModelFilter" label-width="120px" :rules="validateRules" ref="dataModelFilterForm">
        <el-row :gutter="4">
            <el-col :span="8">
                <el-form-item label="标识" required prop="id">
                    <el-input v-model="dataModelFilter.id" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="编码" required prop="code">
                    <el-input v-model="dataModelFilter.code"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="dataModelFilter.name" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="8">
                <el-form-item label="描述"  prop="description">
                    <el-input v-model="dataModelFilter.description"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="16">
                &nbsp;
            </el-col>
        </el-row> 
        <el-row :gutter="4">
            <el-col :span="1">
                &nbsp;
            </el-col>
            <el-col :span="22">
                <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 12px">
                    <span class="title" style="font-size: 14px; font-weight: 500;">筛选值管理：</span>
                </el-divider>
            </el-col>
            <el-col :span="1">
                &nbsp;
            </el-col>
        </el-row> 
        <el-row :gutter="4">
            <el-col :span="1">
                &nbsp;
            </el-col>
            <el-col :span="22">
                <el-row :gutter="4">
                    <el-col :span="8">
                        <el-form-item label="筛选值编码"  prop="filterValueCode">
                            <el-input v-model="filterValueCode"  size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="筛选值名称"  prop="filterValueName">
                            <el-input v-model="filterValueName"  size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="筛选值描述" prop="filterValueDescription">
                            <el-input v-model="filterValueDescription"  size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="筛选公式" prop="filterFormula">
                            <el-input type="textarea" :rows="3" v-model="filterFormula"  size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="1"></el-col>
                    <el-col :span="8">
                        公式示例：<br/>
                        &nbsp;&nbsp;&nbsp;&nbsp;type in ('B','C') and sub_type in ('B01', 'C03')<br/>
                        &nbsp;&nbsp;&nbsp;&nbsp;type = 'B' and sub_type = 'B01'
                    </el-col>
                    <el-col :span="3" style="vertical-align: bottom;">
                        <el-link type="primary" size="small" @click="addFilterValue()">增加</el-link>&nbsp;&nbsp;
                    </el-col>
                </el-row>   
                <el-row :gutter="4">
                    <el-col :span="1">
                        &nbsp;
                    </el-col>
                    <el-col :span="23">
                        <el-container  style="height: 30vh">
                            <el-aside width="100%">
                                <el-row :gutter="4">
                                    <el-table :data="dataModelFilter.filterValues" stripe style="width: 100%">
                                        <el-table-column prop="id" label="筛选值标识" show-overflow-tooltip width="251"/>
                                        <el-table-column prop="code" label="筛选值编码" show-overflow-tooltip width="160"/>
                                        <el-table-column prop="name" label="筛选值名称" show-overflow-tooltip width="180"/>
                                        <el-table-column prop="filterFormula" label="筛选公式" show-overflow-tooltip width="180"/>
                                        <el-table-column label="操作" show-overflow-tooltip width="80">
                                            <template #default="scope">
                                                <el-link type="primary"  size="small" @click="showDataModelFilterValueEditPage(scope.row.id)"><el-icon><Edit /></el-icon></el-link>&nbsp;
                                                <el-link type="primary"  size="small" @click="deleteFilterValue(scope.row, scope.$index)"><el-icon><Delete /></el-icon></el-link>&nbsp;
                                            </template>
                                        </el-table-column>
                                    </el-table>
                                </el-row>
                            </el-aside>
                        </el-container>
                    </el-col>
                </el-row>
            </el-col>
            <el-col :span="1">
                &nbsp;
            </el-col>
        </el-row> 
        
        <el-row :gutter="4">
            <el-col :span="1">
                &nbsp;
            </el-col>
            <el-col :span="7">
                <span class="title" style="font-size: 14px; font-weight: 500;">模型结构：</span>
            </el-col>
            <el-col :span="16">
                &nbsp;
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="1">
                &nbsp;
            </el-col>
            <el-col :span="23">
                <el-container  style="height: 20vh">
                    <el-aside width="100%">
                        <el-row :gutter="4">
                            <el-table  v-if="dataModelColumns != null" :data="dataModelColumns" stripe style="width: 100%">
                                <el-table-column prop="code" label="字段编码" show-overflow-tooltip width="160"/>
                                <el-table-column prop="name" label="字段名称" show-overflow-tooltip width="180"/>
                                <el-table-column prop="valueType" label="值类型" show-overflow-tooltip width="120"/>
                                <el-table-column prop="columnType" label="类型设置" show-overflow-tooltip width="150">
                                    <template #default="scope">
                                        <el-select v-model="scope.row.columnType" class="m-2" placeholder="Select" size="small" clearable disabled>
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
                            </el-table>
                        </el-row>
                    </el-aside>
                </el-container>
            </el-col>
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDataModelFilterEditPage">取消</el-button>
            <el-button type="primary" @click="saveDataModelFilter()">确定</el-button>
        </span>
    </template>
</el-dialog>

<!-- 筛选值编辑 -->
<dataModelFilterValueEdit v-if="showDataModelFilterValueEdit" @updateDataModelFilterValueToList="updateDataModelFilterValueToList" @hideDataModelFilterValueEditPage="hideDataModelFilterValueEditPage" :showDataModelFilterValueEdit="showDataModelFilterValueEdit" :dataModelFilterValue="dataModelFilterValue" :dataModel="dataModel"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import draggable from 'vuedraggable'

import dataModelFilterValueEdit from './workbench-dataModelFilterValueEdit.vue'

export default {
    props: ['dataModelFilter','showDataModelFilterEdit', 'dataModel'],
    
    emits: ['hideDataModelFilterEditPage', 'updateDataModelFilterToList'],
    
    setup (props, {attrs, emit, slots}) {
        const hideDataModelFilterEditPage = ()=> {
            emit('hideDataModelFilterEditPage');
        };
        
        const updateDataModelFilterToList = (newAttr) => {
        	emit('updateDataModelFilterToList', newAttr);
        };
        
        return {
        	hideDataModelFilterEditPage,
        	updateDataModelFilterToList
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showDataModelFilterEdit;
            },
            set(value) {
                this.hideDataModelFilterEditPage();
            }
        }
    },
    components: {
        draggable,
        dataModelFilterValueEdit
    },
    watch: {        
       
    
    },
    data() {            
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

        const columnTypeOptions = ref(null);

        const dataModelColumns = ref([]);

        const filterValueCode = ref(null);
        const filterValueName = ref(null);
        const filterValueDescription = ref(null);
        const filterFormula = ref(null);

        const showDataModelFilterValueEdit = ref(false);
        const dataModelFilterValue = ref(null);

        return {
        	dataModelColumns,
            columnTypeOptions,

            filterValueCode,
            filterValueName,
            filterValueDescription,
            filterFormula,

            showDataModelFilterValueEdit,
            dataModelFilterValue,

            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValuesMore", ['BI_COLUMN_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.columnTypeOptions = data.data['BI_COLUMN_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        if(this.dataModel.columns != null) {
            for(var i in this.dataModel.columns) {
                if(this.dataModel.columns[i].id != null && this.dataModel.columns[i].id.trim().length > 0 && this.dataModel.columns[i].columnType != null) {
                    this.dataModelColumns.push(this.dataModel.columns[i]);
                }
            }
        }
    },
    methods: {
    	saveDataModelFilter() {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModelFilter/save";
            this.$refs.dataModelFilterForm.validate((valid, fields)=> {
                if(valid) {
                    var updateDataModelFilter = {
                        id: this.dataModelFilter.id,
                        code: this.dataModelFilter.code,
                        name: this.dataModelFilter.name,
                        description: this.dataModelFilter.description,
                        dataModelId: this.dataModelFilter.dataModelId,
                        status: this.dataModelFilter.status,
                        systemId: this.dataModelFilter.systemId,
                        tenantId: this.dataModelFilter.tenantId,
                        createTime: this.dataModelFilter.createTime,
                        updateTime: this.dataModelFilter.updateTime
                    }
                    axiosClient.post(url,updateDataModelFilter).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                        ElMessage(`保存筛选器成功`);
                        this.updateDataModelFilterToList(data.data);
                    }
                });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
        }, 
        addFilterValue() {
            if(this.filterValueCode == null || this.filterValueCode.trim().length == 0) {
                ElMessage.error(`筛选值编码不能为空！`);
                return;
            }
            if(this.filterValueName == null || this.filterValueName.trim().length == 0) {
                ElMessage.error(`筛选值名称不能为空！`);
                return;
            }
            if(this.filterFormula == null || this.filterFormula.trim().length == 0) {
                ElMessage.error(`筛选公式不能为空！`);
                return;
            }
            if(this.dataModelFilter.filterValues == null) {
                this.dataModelFilter.filterValues = [];
            }
            var filterValue = {
                id: '',
                code: this.filterValueCode,
                name: this.filterValueName,
                filterId: this.dataModelFilter.id,
                description: this.filterValueDescription,
                filterFormula: this.filterFormula,
                systemId: this.dataModelFilter.systemId,
                tenantId: this.dataModelFilter.tenantId
            };

            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModelFilterValue/add";
            axiosClient.post(url, filterValue).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                    ElMessage(`新增筛选值成功`);
                    this.dataModelFilter.filterValues.push(data.data);
                }
            });
        },
        deleteFilterValue(filterValue, index) {
            ElMessageBox.confirm(
                '该筛选值(' + filterValue.name + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
            ).then(() => {
                var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModelFilterValue/delete/" + filterValue.id;
                axiosClient.post(url).then((response) => {
                    var data = response.data;
                    if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                       this.dataModelFilter.filterValues.splice(index, 1);
                        ElMessage(`删除筛选器(`+ filterValue.name +`)成功。`);
                    }
                }).catch((ex)=>{
                    ElMessage.error(`删除筛选器(`+ metfilterValuerics.name +`)失败！` +ex);
                });
            }).catch((ex)=>{ElMessage.error(`删除筛选器(`+ filterValue.name +`)失败！` + ex);});
        },
        showDataModelFilterValueEditPage(filterValueId) {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModelFilterValue/byId/" + filterValueId;
            axiosClient.post(url).then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataModelFilterValue = data.data;
                    this.showDataModelFilterValueEdit = true;
                }else {
                    ElMessage.error(`加载筛选值信息失败！`);
                }
            }).catch((ex)=>{
                ElMessage.error(`加载筛选值信息失败！` +ex);
            });
            
        },
        hideDataModelFilterValueEditPage() {
            this.dataModelFilterValue = null;
            this.showDataModelFilterValueEdit = false;
        },
        updateDataModelFilterValueToList(filterValue) {
            if(this.dataModelFilter.filterValues == null) {
                this.dataModelFilter.filterValues = [];
            }
            for(var i in this.dataModelFilter.filterValues) {
                if(this.dataModelFilter.filterValues[i].id == filterValue.id) {
                    this.dataModelFilter.filterValues[i] = filterValue;
                    break;
                }
            }
            this.hideDataModelFilterValueEditPage();
        }
    }
    
}
</script>