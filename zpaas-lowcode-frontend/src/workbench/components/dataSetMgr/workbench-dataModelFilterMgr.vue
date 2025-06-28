<!-- 筛选器管理页 -->
<template>
<el-dialog v-model="showFlag" title="筛选器管理" width="60vw">
    <template #header>
        <span class="title">筛选器管理</span>
    </template>
    <el-row :gutter="4">
        <el-col :span="24">
            &nbsp;&nbsp;&nbsp;<el-link type="primary" size="small" @click="showDataModelFilterCreatePage()">新增筛选器</el-link>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-scrollbar height="450px">
                <el-table :data="dataModelFilters" stripe style="width: 100%">
                        <el-table-column prop="id" label="标识" width="251" />
                        <el-table-column prop="code" label="编码" show-overflow-tooltip width="180" />
                        <el-table-column prop="name" label="名称" show-overflow-tooltip width="180" />
                        <el-table-column prop="description" label="描述" show-overflow-tooltip width="180" />
                        <el-table-column label="操作" show-overflow-tooltip width="80">
                            <template #default="scope">
                                <el-link type="primary"  size="small" @click="showDataModelFilterEditPage(scope.row.id)"><el-icon><Edit /></el-icon></el-link>&nbsp;
                                <el-link type="primary"  size="small" @click="deleteDataModelFilter(scope.row)"><el-icon><Delete /></el-icon></el-link>&nbsp;
                            </template>
                        </el-table-column>
                </el-table>
            </el-scrollbar>
        </el-col>
    </el-row>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDataModelFilterMgrPage()">取消</el-button>
        </span>
    </template>
</el-dialog>

<!-- 筛选器新增 -->
<dataModelFilterCreate v-if="showDataModelFilterCreate" @addNewDataModelFilterToList="addNewDataModelFilterToList" @hideDataModelFilterCreatePage="hideDataModelFilterCreatePage" :showDataModelFilterCreate="showDataModelFilterCreate" :newDataModelFilter="newDataModelFilter" :dataModel="dataModel"/>

<!-- 筛选器编辑 -->
<dataModelFilterEdit v-if="showDataModelFilterEdit" @updateDataModelFilterToList="updateDataModelFilterToList" @hideDataModelFilterEditPage="hideDataModelFilterEditPage" :showDataModelFilterEdit="showDataModelFilterEdit" :dataModelFilter="dataModelFilter" :dataModel="dataModel"/>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'

import dataModelFilterCreate from './workbench-dataModelFilterCreate.vue'
import dataModelFilterEdit from './workbench-dataModelFilterEdit.vue'

export default {
    props: ['dataModelFilters', 'dataModel', 'showDataModelFilterMgr'],
    
    emits: ['hideDataModelFilterMgrPage'],
    
    setup(props, {attrs, emit, slots}) {
        const hideDataModelFilterMgrPage = () => {
            emit('hideDataModelFilterMgrPage');
        };
        
        return {
        	hideDataModelFilterMgrPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showDataModelFilterMgr;
            },
            set(value) {
                this.hideDataModelFilterMgrPage();
            }
        }
    },
    components: {
        dataModelFilterCreate,
        dataModelFilterEdit
    },
    watch: {
        
    },
    data() {
        const showDataModelFilterCreate = ref(false);
        const newDataModelFilter = ref(null);

        const showDataModelFilterEdit = ref(false);
        const dataModelFilter = ref(null);
        
    	
        return {
    		showDataModelFilterCreate,
            newDataModelFilter,
            showDataModelFilterEdit,
            dataModelFilter
        }
    },
    
    methods: {
    	deleteDataModelFilter(metrics) {
            ElMessageBox.confirm(
                '该筛选器(' + metrics.name + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
            ).then(() => {
                var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModelFilter/delete/" + metrics.id;
                axiosClient.post(url).then((response) => {
                    var data = response.data;
                    if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                        for(var i in this.dataModelFilters) {
                           if(this.dataModelFilters[i].id == metrics.id) {
                               this.dataModelFilters.splice(i,1);
                               break;
                           }
                        }
                        ElMessage(`删除筛选器(`+ metrics.name +`)成功。`);
                    }
                }).catch((ex)=>{
                    ElMessage.error(`删除筛选器(`+ metrics.name +`)失败！` +ex);
                });
            }).catch((ex)=>{ElMessage.error(`删除筛选器(`+ metrics.name +`)失败！` + ex);});
        },
        showDataModelFilterCreatePage() {
            this.newDataModelFilter = {
                id: "",
                code: "",
                name: "",
                metricType: "D",
                metricFormula: "",
                dataModelId: this.dataModel.id,
                systemId: this.dataModel.systemId,
                tenantId: this.dataModel.tenantId
            };
            this.showDataModelFilterCreate = true;
        },
        hideDataModelFilterCreatePage() {
            this.newDataModelFilter = null;
            this.showDataModelFilterCreate = false;
        },
        addNewDataModelFilterToList(metric) {
            if(this.dataModelFilters == null) {
                this.dataModelFilters = [];
            }
            this.dataModelFilters.push(metric);
            this.hideDataModelFilterCreatePage();
        },
        showDataModelFilterEditPage(metricId) {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModelFilter/byId/" + metricId;
            axiosClient.post(url).then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataModelFilter = data.data;
                    this.showDataModelFilterEdit = true;
                }
            }).catch((ex)=>{
                ElMessage.error(`加载筛选器信息失败！` +ex);
            });
            
        },
        hideDataModelFilterEditPage() {
            this.dataModelFilter = null;
            this.showDataModelFilterEdit = false;
        },
        updateDataModelFilterToList(metric) {
            if(this.dataModelFilters == null) {
                this.dataModelFilters = [];
            }
            for(var i in this.dataModelFilters) {
                if(this.dataModelFilters[i].id == metric.id) {
                    this.dataModelFilters[i] = metric;
                    break;
                }
            }
            this.hideDataModelFilterEditPage();
        }
    }
}

</script>