<!-- 标签管理页 -->
<template>
<el-dialog v-model="showFlag" title="标签管理" width="75vw">
    <template #header>
        <span class="title">标签管理</span>
    </template>
    <el-row :gutter="4">
        <el-col :span="24">
            &nbsp;&nbsp;&nbsp;<el-link type="primary" size="small" @click="showDataModelTagCreatePage()">新增标签</el-link>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-scrollbar height="450px">
                <el-table :data="dataModelTags" stripe style="width: 100%">
                    <el-table-column prop="id" label="标识" width="251" />
                    <el-table-column prop="code" label="编码" show-overflow-tooltip width="180" />
                    <el-table-column prop="name" label="名称" show-overflow-tooltip width="180" />
                    <el-table-column prop="filterFormula" label="标签过滤器" show-overflow-tooltip width="300" />
                    <el-table-column label="操作" show-overflow-tooltip width="80">
                        <template #default="scope">
                            <el-link type="primary"  size="small" @click="showDataModelTagEditPage(scope.row.id)"><el-icon><Edit /></el-icon></el-link>&nbsp;
                            <el-link type="primary"  size="small" @click="deleteDataModelTag(scope.row)"><el-icon><Delete /></el-icon></el-link>&nbsp;
                        </template>
                    </el-table-column>
                </el-table>
            </el-scrollbar>
        </el-col>
    </el-row>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDataModelTagMgrPage()">取消</el-button>
        </span>
    </template>
</el-dialog>

<!-- 标签新增 -->
<dataModelTagCreate v-if="showDataModelTagCreate" @addNewDataModelTagToList="addNewDataModelTagToList" @hideDataModelTagCreatePage="hideDataModelTagCreatePage" :showDataModelTagCreate="showDataModelTagCreate" :newDataModelTag="newDataModelTag" :dataModel="dataModel"/>

<!-- 标签编辑 -->
<dataModelTagEdit v-if="showDataModelTagEdit" @updateDataModelTagToList="updateDataModelTagToList" @hideDataModelTagEditPage="hideDataModelTagEditPage" :showDataModelTagEdit="showDataModelTagEdit" :dataModelTag="dataModelTag" :dataModel="dataModel"/>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'

import dataModelTagCreate from './workbench-dataModelTagCreate.vue'
import dataModelTagEdit from './workbench-dataModelTagEdit.vue'

export default {
    props: ['dataModelTags', 'dataModel', 'showDataModelTagMgr'],
    
    emits: ['hideDataModelTagMgrPage'],
    
    setup(props, {attrs, emit, slots}) {
        const hideDataModelTagMgrPage = () => {
            emit('hideDataModelTagMgrPage');
        };
        
        return {
        	hideDataModelTagMgrPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showDataModelTagMgr;
            },
            set(value) {
                this.hideDataModelTagMgrPage();
            }
        }
    },
    components: {
        dataModelTagCreate,
        dataModelTagEdit
    },
    watch: {
        
    },
    data() {
        const showDataModelTagCreate = ref(false);
        const newDataModelTag = ref(null);

        const showDataModelTagEdit = ref(false);
        const dataModelTag = ref(null);
        
    	
        return {
    		showDataModelTagCreate,
            newDataModelTag,
            showDataModelTagEdit,
            dataModelTag
        }
    },
    
    methods: {
    	deleteDataModelTag(metrics) {
            ElMessageBox.confirm(
                '该标签(' + metrics.name + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModelTag/delete/" + metrics.id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         for(var i in this.dataModelTags) {
                            if(this.dataModelTags[i].id == metrics.id) {
                                this.dataModelTags.splice(i,1);
                                break;
                            }
                         }
                         ElMessage(`删除标签(`+ metrics.name +`)成功。`);
                     }
                 }).catch((ex)=>{
                     ElMessage.error(`删除标签(`+ metrics.name +`)失败！` +ex);
                 });
             }).catch((ex)=>{ElMessage.error(`删除标签(`+ metrics.name +`)失败！` + ex);});
        },
        showDataModelTagCreatePage() {
            this.newDataModelTag = {
                id: "",
                code: "",
                name: "",
                metricType: "D",
                metricFormula: "",
                dataModelId: this.dataModel.id,
                systemId: this.dataModel.systemId,
                tenantId: this.dataModel.tenantId
            };
            this.showDataModelTagCreate = true;
        },
        hideDataModelTagCreatePage() {
            this.newDataModelTag = null;
            this.showDataModelTagCreate = false;
        },
        addNewDataModelTagToList(metric) {
            if(this.dataModelTags == null) {
                this.dataModelTags = [];
            }
            this.dataModelTags.push(metric);
            this.hideDataModelTagCreatePage();
        },
        showDataModelTagEditPage(metricId) {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModelTag/byId/" + metricId;
            axiosClient.post(url).then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataModelTag = data.data;
                    this.showDataModelTagEdit = true;
                }else {
                    ElMessage.error(`加载标签信息失败！`);
                }
            }).catch((ex)=>{
                ElMessage.error(`加载标签信息失败！` +ex);
            });
            
        },
        hideDataModelTagEditPage() {
            this.dataModelTag = null;
            this.showDataModelTagEdit = false;
        },
        updateDataModelTagToList(metric) {
            if(this.dataModelTags == null) {
                this.dataModelTags = [];
            }
            for(var i in this.dataModelTags) {
                if(this.dataModelTags[i].id == metric.id) {
                    this.dataModelTags[i] = metric;
                    break;
                }
            }
            this.hideDataModelTagEditPage();
        }
    }
}

</script>