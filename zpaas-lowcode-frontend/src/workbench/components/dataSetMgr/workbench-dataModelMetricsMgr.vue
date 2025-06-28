<!-- 指标管理页 -->
<template>
<el-dialog v-model="showFlag" title="指标管理" width="75vw">
    <template #header>
        <span class="title">指标管理</span>
    </template>
    <el-row :gutter="4">
        <el-col :span="24">
            &nbsp;&nbsp;&nbsp;<el-link type="primary" size="small" @click="showDataModelMetricsCreatePage()">新增指标</el-link>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-scrollbar height="450px">
                <el-table :data="dataModelMetrics" stripe style="width: 100%">
                    <el-table-column prop="id" label="标识" width="251" />
                    <el-table-column prop="code" label="编码" show-overflow-tooltip width="180" />
                    <el-table-column prop="name" label="名称" show-overflow-tooltip width="180" />
                    <el-table-column prop="metricType" label="指标类型" width="100" > 
                        <template #default="scope">
                            <span v-if="scope.row.metricType == 'D'">明细指标</span>
                            <span v-else-if="scope.row.metricType == 'S'">统计指标</span>
                            <span v-else>{{scope.row.metricType}}</span>
                        </template>
                    </el-table-column>
                    <el-table-column prop="metricFormula" label="指标公式" show-overflow-tooltip width="300" />
                    <el-table-column label="操作" show-overflow-tooltip width="80">
                        <template #default="scope">
                            <el-link type="primary"  size="small" @click="showDataModelMetricEditPage(scope.row.id)"><el-icon><Edit /></el-icon></el-link>&nbsp;
                            <el-link type="primary"  size="small" @click="deleteDataModelMetrics(scope.row)"><el-icon><Delete /></el-icon></el-link>&nbsp;
                        </template>
                    </el-table-column>
                </el-table>
            </el-scrollbar>
        </el-col>
    </el-row>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDataModelMetricsMgrPage()">取消</el-button>
        </span>
    </template>
</el-dialog>

<!-- 标签新增 -->
<dataModelMetricsCreate v-if="showDataModelMetricsCreate" @addNewDataModelMetricsToList="addNewDataModelMetricsToList" @hideDataModelMetricsCreatePage="hideDataModelMetricsCreatePage" :showDataModelMetricsCreate="showDataModelMetricsCreate" :newDataModelMetrics="newDataModelMetrics" :dataModel="dataModel"/>

<!-- 标签编辑 -->
<dataModelMetricEdit v-if="showDataModelMetricEdit" @updateDataModelMetricToList="updateDataModelMetricToList" @hideDataModelMetricEditPage="hideDataModelMetricEditPage" :showDataModelMetricEdit="showDataModelMetricEdit" :dataModelMetric="dataModelMetric" :dataModel="dataModel"/>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage, ElTable } from 'element-plus'

import dataModelMetricsCreate from './workbench-dataModelMetricCreate.vue'
import dataModelMetricEdit from './workbench-dataModelMetricEdit.vue'

export default {
    props: ['dataModelMetrics', 'dataModel', 'showDataModelMetricsMgr'],
    
    emits: ['hideDataModelMetricsMgrPage'],
    
    setup(props, {attrs, emit, slots}) {
        const hideDataModelMetricsMgrPage = () => {
            emit('hideDataModelMetricsMgrPage');
        };
        
        return {
        	hideDataModelMetricsMgrPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showDataModelMetricsMgr;
            },
            set(value) {
                this.hideDataModelMetricsMgrPage();
            }
        }
    },
    components: {
        dataModelMetricsCreate,
        dataModelMetricEdit
    },
    watch: {
        
    },
    data() {
        const showDataModelMetricsCreate = ref(false);
        const newDataModelMetrics = ref(null);

        const showDataModelMetricEdit = ref(false);
        const dataModelMetric = ref(null);
        
    	
        return {
    		showDataModelMetricsCreate,
            newDataModelMetrics,
            showDataModelMetricEdit,
            dataModelMetric
        }
    },
    
    methods: {
    	deleteDataModelMetrics(metrics) {
            ElMessageBox.confirm(
                '该指标(' + metrics.name + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModelMetrics/delete/" + metrics.id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         for(var i in this.dataModelMetrics) {
                            if(this.dataModelMetrics[i].id == metrics.id) {
                                this.dataModelMetrics.splice(i,1);
                                break;
                            }
                         }
                         ElMessage(`删除指标(`+ metrics.name +`)成功。`);
                     }
                 }).catch((ex)=>{
                     ElMessage.error(`删除指标(`+ metrics.name +`)失败！` +ex);
                 });
             }).catch((ex)=>{ElMessage.error(`删除指标(`+ metrics.name +`)失败！` + ex);});
        },
        showDataModelMetricsCreatePage() {
            this.newDataModelMetrics = {
                id: "",
                code: "",
                name: "",
                metricType: "D",
                metricFormula: "",
                dataModelId: this.dataModel.id,
                systemId: this.dataModel.systemId,
                tenantId: this.dataModel.tenantId
            };
            this.showDataModelMetricsCreate = true;
        },
        hideDataModelMetricsCreatePage() {
            this.newDataModelMetrics = null;
            this.showDataModelMetricsCreate = false;
        },
        addNewDataModelMetricsToList(metric) {
            if(this.dataModelMetrics == null) {
                this.dataModelMetrics = [];
            }
            this.dataModelMetrics.push(metric);
            this.hideDataModelMetricsCreatePage();
        },
        showDataModelMetricEditPage(metricId) {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModelMetrics/byId/" + metricId;
            axiosClient.post(url).then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataModelMetric = data.data;
                    this.showDataModelMetricEdit = true;
                }
            }).catch((ex)=>{
                ElMessage.error(`加载指标信息失败！` +ex);
            });
            
        },
        hideDataModelMetricEditPage() {
            this.dataModelMetric = null;
            this.showDataModelMetricEdit = false;
        },
        updateDataModelMetricToList(metric) {
            if(this.dataModelMetrics == null) {
                this.dataModelMetrics = [];
            }
            for(var i in this.dataModelMetrics) {
                if(this.dataModelMetrics[i].id == metric.id) {
                    this.dataModelMetrics[i] = metric;
                    break;
                }
            }
            this.hideDataModelMetricEditPage();
        }
    }
}

</script>