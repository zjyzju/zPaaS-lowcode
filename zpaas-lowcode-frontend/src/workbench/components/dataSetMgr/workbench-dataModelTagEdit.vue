<!-- 标签编辑 -->

<template>
<el-dialog v-model="showFlag" title="标签编辑" width="60vw">
    <template #header>
        <span class="title">标签编辑</span>
    </template>
    <el-form  :model="dataModelTag" label-width="120px" :rules="validateRules" ref="dataModelTagForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="标识" required prop="id">
                    <el-input v-model="dataModelTag.id" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="编码" required prop="code">
                    <el-input v-model="dataModelTag.code"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="dataModelTag.name"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="描述"  prop="description">
                    <el-input type="textarea" :rows="4" v-model="dataModelTag.description"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>   
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="标签过滤器" required prop="filterFormula">
                    <el-input type="textarea" :rows="4" v-model="dataModelTag.filterFormula"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="1"></el-col>
            <el-col :span="11">
                公式示例：<br/>
                &nbsp;&nbsp;&nbsp;&nbsp;type in ('B','C') and sub_type in ('B01', 'C03')<br/>
                &nbsp;&nbsp;&nbsp;&nbsp;type = 'B' and sub_type = 'B01'
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
        <el-row :gutter="4" v-if="dataModel != null">
            <el-col :span="1">
                &nbsp;
            </el-col>
            <el-col :span="23">
                <el-container  style="height: 35vh">
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
            <el-button @click="hideDataModelTagEditPage">取消</el-button>
            <el-button type="primary" @click="saveDataModelTag()">确定</el-button>
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
    props: ['dataModelTag','showDataModelTagEdit', 'dataModel'],
    
    emits: ['hideDataModelTagEditPage', 'updateDataModelTagToList'],
    
    setup (props, {attrs, emit, slots}) {
        const hideDataModelTagEditPage = ()=> {
            emit('hideDataModelTagEditPage');
        };
        
        const updateDataModelTagToList = (newAttr) => {
        	emit('updateDataModelTagToList', newAttr);
        };
        
        return {
        	hideDataModelTagEditPage,
        	updateDataModelTagToList
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showDataModelTagEdit;
            },
            set(value) {
                this.hideDataModelTagEditPage();
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
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "code": [
                { required: true, message: '编码不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "filterFormula": [
                { required: true, message: '标签过滤器不能为空！', trigger: 'blur' }
            ]
        });

        const columnTypeOptions = ref(null);

        const dataModelColumns = ref([]);
        return {
        	dataModelColumns,
            columnTypeOptions,

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
    	saveDataModelTag() {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModelTag/save";
            this.$refs.dataModelTagForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.dataModelTag).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                        ElMessage(`保存标签成功`);
                        this.updateDataModelTagToList(data.data);
                    }
                });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        }
    }
    
}
</script>