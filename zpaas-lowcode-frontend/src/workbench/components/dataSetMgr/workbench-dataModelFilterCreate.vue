<!-- 新建筛选器 -->

<template>
<el-dialog v-model="showFlag" title="新建筛选器" width="65vw" top="20px">
    <template #header>
        <span class="title">新建筛选器</span>
    </template>
    <el-form  :model="newDataModelFilter" label-width="120px" :rules="validateRules" ref="newDataModelFilterForm">
        <el-row :gutter="4">
            <el-col :span="8">
                <el-form-item label="编码" required prop="code">
                    <el-input v-model="newDataModelFilter.code"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="newDataModelFilter.name"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="描述" prop="description">
                    <el-input v-model="newDataModelFilter.description"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
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
                                    <el-table :data="newDataModelFilter.filterValues" stripe style="width: 100%">
                                        <el-table-column prop="code" label="筛选值编码" show-overflow-tooltip width="160"/>
                                        <el-table-column prop="name" label="筛选值名称" show-overflow-tooltip width="180"/>
                                        <el-table-column prop="filterFormula" label="筛选公式" show-overflow-tooltip width="180"/>
                                        <el-table-column prop="description" label="筛选值描述" show-overflow-tooltip width="260"/>
                                        <el-table-column label="操作" show-overflow-tooltip width="80">
                                            <template #default="scope">
                                                <el-link type="primary"  size="small" @click="deleteFilterValue(scope.$index)"><el-icon><Delete /></el-icon></el-link>&nbsp;
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
            <el-button @click="hideDataModelFilterCreatePage">取消</el-button>
            <el-button type="primary" @click="createDataModelFilter()">确定</el-button>
        </span>
    </template>
</el-dialog>
</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'
import draggable from 'vuedraggable'


export default {
    props: ['newDataModelFilter','showDataModelFilterCreate', 'dataModel'],
    
    emits: ['hideDataModelFilterCreatePage', 'addNewDataModelFilterToList'],
    
    setup (props, {attrs, emit, slots}) {
        const hideDataModelFilterCreatePage = ()=> {
            emit('hideDataModelFilterCreatePage');
        };
        
        const addNewDataModelFilterToList = (newAttr) => {
        	emit('addNewDataModelFilterToList', newAttr);
        };
        
        return {
        	hideDataModelFilterCreatePage,
        	addNewDataModelFilterToList
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showDataModelFilterCreate;
            },
            set(value) {
                this.hideDataModelFilterCreatePage();
            }
        }
    },
    components: {
        draggable
    },
    watch: {        
       
    
    },
    data() {            
    	const validateRules = ref({
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

        return {
        	dataModelColumns,
            columnTypeOptions,

            filterValueCode,
            filterValueName,
            filterValueDescription,
            filterFormula,

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
    	createDataModelFilter() {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModelFilter/add";
            this.$refs.newDataModelFilterForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.newDataModelFilter).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                        ElMessage(`新增筛选器成功`);
                        this.addNewDataModelFilterToList(data.data);
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
            if(this.newDataModelFilter.filterValues == null) {
                this.newDataModelFilter.filterValues = [];
            }
            this.newDataModelFilter.filterValues.push({
                id: '',
                code: this.filterValueCode,
                name: this.filterValueName,
                description: this.filterValueDescription,
                filterFormula: this.filterFormula,
                systemId: this.newDataModelFilter.systemId,
                tenantId: this.newDataModelFilter.tenantId
            });
        },
        deleteFilterValue(index) {
            this.newDataModelFilter.filterValues.splice(index,1);
        }
    }
    
}
</script>