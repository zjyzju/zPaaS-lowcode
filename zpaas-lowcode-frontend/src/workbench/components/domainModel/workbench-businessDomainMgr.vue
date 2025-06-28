<!-- 业务域管理 -->
<template>
<el-dialog v-model="showFlag" title="业务域列表" width="800px">
    <template #header>
        <span class="title">业务域列表</span>
    </template>
    <div>
        <el-form v-if="showBusinessDomainMgr" label-width="120px">
             <el-row :gutter="4">
                <el-col :span="24">
                    <el-table ref="businessDomainsTable" :data="businessDomains" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
                            <el-table-column prop="httpMapping" label="编辑" width="120" >
                                <template #default="scope">
                                    <el-link type="primary" @click="showBusinessDomainEditPage(scope.row.id)">编辑</el-link>&nbsp;
                                    <el-link type="primary" @click="deleteBusinessDomain(scope.row.id, scope.row.name)">删除</el-link>
                                </template>
                            </el-table-column>
                            <el-table-column prop="id" label="标识" width="260" />
                            <el-table-column prop="name" label="名称" width="220" />
                            <el-table-column prop="description" label="描述" width="360"  />
                    </el-table>
                </el-col>
            </el-row>
            
        </el-form>
    </div>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideBusinessDomainMgrPage()">取消</el-button>
        </span>
    </template>
</el-dialog>

<!-- 编辑业务域 -->
<editBusinessDomain v-if="showBusinessDomainEdit==true && businessDomain != null" @updateBusinessDomainToList="updateBusinessDomainToList" @hideBusinessDomainEditPage="hideBusinessDomainEditPage"  :showBusinessDomainEdit="showBusinessDomainEdit"  :businessDomain="businessDomain"/>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref, defineAsyncComponent } from 'vue';
import { ElMessageBox, ElMessage, ElTable } from 'element-plus'

const editBusinessDomain = defineAsyncComponent(() => import('./workbench-businessDomainEdit.vue'));


export default {
    props: ['showBusinessDomainMgr', 'systemId'],
    
    emits: [ 'hideBusinessDomainMgrPage'],
    
    setup(props, {attrs, emit, slots}) {
        const hideBusinessDomainMgrPage = () => {
            emit('hideBusinessDomainMgrPage');
        };
        
        return {
        	hideBusinessDomainMgrPage
        }
    },
    components: {
    	editBusinessDomain
    },
    computed: {
        showFlag: {
            get() {
                return this.showBusinessDomainMgr;
            },
            set(value) {
                this.hideBusinessDomainMgrPage();
            }
        }
    },
    data() {
    	const currentRow = ref();
        const businessDomains = ref(null);
    	
        const showBusinessDomainEdit = ref(false);
        const businessDomain = ref(null);
        
        return {
            currentRow,
            businessDomains,
            
            showBusinessDomainEdit,
            businessDomain
        }
    },
    mounted() {
        this.loadBusinessDomains();
    },
    watch: {
        
    },
    methods: {
        loadBusinessDomains() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/businessDomain/list/" + this.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.businessDomains = data;
                }
            });
        },
    	handleCurrentChange(val) {
    		this.currentRow = val;
    	},
    	showBusinessDomainEditPage(id) {
    		var url = "/lcdp-proxy/lowcode/platform/be/api/businessDomain/byId/" + id;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                    this.businessDomain = data;
                    this.showBusinessDomainEdit = true;
                }
            });
    	},
    	hideBusinessDomainEditPage() {
            this.showBusinessDomainEdit = false;
            this.businessDomain = null;
        }, 
        updateBusinessDomainToList() {
            this.showBusinessDomainEdit = false;
            this.businessDomain = null;
            this.loadBusinessDomains();
        },
        deleteBusinessDomain(id, code) {
            ElMessageBox.confirm(
                '该业务域(' + code + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/be/api/businessDomain/delete/" + id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         ElMessage(`删除业务域(`+ code +`)成功。`);
                         this.loadBusinessDomains();
                     }
                 }).catch(()=>{
                     ElMessage.error(`删除业务域(`+ code +`)失败！`);
                 });
             }).catch((ex)=>{console.log(ex)});
        }
    }   
}

</script>