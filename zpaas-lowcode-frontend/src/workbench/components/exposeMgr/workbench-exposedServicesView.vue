<!-- 暴露服务列表查看页 -->
<template>
<el-dialog v-model="showFlag" title="暴露服务列表查看" width="1050px">
    <template #header>
        <span class="title">暴露服务列表查看</span>
    </template>
    <div>
        <el-form-item label="过滤对象">
            <el-input v-model="filterString" size="small"/>
        </el-form-item>
    </div>
    <div>
        <el-scrollbar height="400px">
            <el-form v-if="showExposedServiceView && exposedServicesForView" label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="managedObjectSelectTable" :data="filteredObjects" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
                                <el-table-column prop="httpMapping" label="测试" width="60" >
                                    <template #default="scope">
                                        <el-link type="primary" @click="testService(scope.row.id)">测试</el-link>
                                    </template>
                                </el-table-column>
                                <el-table-column prop="httpMapping" label="服务URL" width="350" />
                                <el-table-column prop="serviceObject.code" label="对应服务" width="220" />
                                <el-table-column prop="operation.code" label="对应服务方法" width="260"  />
                                <el-table-column prop="httpMethod" label="HttpMethod" width="100" />
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
    </div>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideExposedServiceViewPage()">取消</el-button>
            <el-button type="primary" @click="viewExposedService()">查看</el-button>
        </span>
    </template>
</el-dialog>

<!-- 查看暴露服务详细信息 -->
<exposedServiceDetail v-if="showExposedServiceDetail==true && exposedOperation != null" @hideExposedServiceDetailPage="hideExposedServiceDetailPage" :showExposedServiceDetail="showExposedServiceDetail"  :exposedOperation="exposedOperation"/>

<!-- 暴露服务测试 -->
<exposedServiceTest v-if="showExposedServiceTest==true && exposedOperation != null" @hideExposedServiceTestPage="hideExposedServiceTestPage" :showExposedServiceTest="showExposedServiceTest"  :exposedOperation="exposedOperation"/>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

import exposedServiceDetail from './workbench-exposedServiceDetail.vue'
import exposedServiceTest from './workbench-exposedServiceTest.vue'

export default {
    props: ['showExposedServiceView', 'exposedServicesForView'],
    
    emits: [ 'hideExposedServiceViewPage'],
    
    setup(props, {attrs, emit, slots}) {
        const hideExposedServiceViewPage = () => {
            emit('hideExposedServiceViewPage');
        };
        
        return {
        	hideExposedServiceViewPage
        }
    },
    components: {
    	exposedServiceDetail,
        exposedServiceTest
    },
    computed: {
        showFlag: {
            get() {
                return this.showExposedServiceView;
            },
            set(value) {
                this.hideExposedServiceViewPage();
            }
        }
    },
    data() {
    	const currentRow = ref();
    	const filteredObjects = ref([]);
        const filterString = ref("");
        
        const exposedOperation = ref({});
        const showExposedServiceDetail = ref(false);
        const showExposedServiceTest = ref(false);
        
        return {
            currentRow,
            filteredObjects,
            filterString,
            
            exposedOperation,
            showExposedServiceDetail,
            showExposedServiceTest
        }
    },
    mounted() {
        this.filteredObjects = this.exposedServicesForView;
    },
    watch: {
        'filterString' (newVal, oldVal){
            this.filterObject();
        }
    },
    methods: {
    	handleCurrentChange(val) {
    		this.currentRow = val;
    	},
    	viewExposedService() {
    		if(this.currentRow == null || this.currentRow.id == null) {
    			ElMessage.error(`请先选择一条服务！`);
    			return;
    		}
    		var exposedService = this.currentRow;
            if(exposedService != null) {
            	console.log(exposedService);
                this.exposedOperation = {
                        id : exposedService.id,
                        serviceId : exposedService.serviceId,
                        serviceCode : exposedService.serviceObject.code,
                        operationId : exposedService.operationId,
                        operationCode : exposedService.operation.code,
                        httpMethod : exposedService.httpMethod,
                        httpMapping : exposedService.httpMapping,
                        tanentId : exposedService.tenantId,
                        systemId : exposedService.systemId,
                        domainId : exposedService.domainId,
                        status : exposedService.status,
                        createTime : exposedService.createTime,
                        updateTime : exposedService.updateTime
                    }
                var url = "/lcdp-proxy/lowcode/platform/be/api/parameter/load/" + exposedService.operationId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.exposedOperation.parameters = data.data;
                        this.showExposedServiceDetail = true;
                    }
                });
            }else {
                ElMessage.error(`获取服务方法发布信息失败`);
            } 
    	},
    	hideExposedServiceDetailPage() {
            this.showExposedServiceDetail = false;
        },
        filterObject() {
            if(this.filterString == null || this.filterString.trim().length == 0) {
                this.filteredObjects = this.exposedServicesForView;
            }else {
                if(this.exposedServicesForView != null && this.exposedServicesForView.length > 0) {
                    var tempObjects = [];
                    this.exposedServicesForView.forEach((row)=>{
                        if(row.serviceObject.code.indexOf(this.filterString) >= 0 || row.operation.code.indexOf(this.filterString) >= 0 || row.httpMapping.indexOf(this.filterString) >= 0) {
                            tempObjects.push(row);
                        } 
                    });
                    this.filteredObjects = tempObjects;
                }
            }
        },
        hideExposedServiceTestPage() {
            this.showExposedServiceTest = false;
        },
        testService(exposedServiceId) {
            var exposedService = null;
            for(var i in this.exposedServicesForView) {
                if(this.exposedServicesForView[i].id == exposedServiceId) {
                    exposedService = this.exposedServicesForView[i];
                    break;
                }
            }
            if(exposedService != null) {
            	this.exposedOperation = {
                        id : exposedService.id,
                        serviceId : exposedService.serviceId,
                        serviceCode : exposedService.serviceObject.code,
                        operationId : exposedService.operationId,
                        operationCode : exposedService.operation.code,
                        httpMethod : exposedService.httpMethod,
                        httpMapping : exposedService.httpMapping,
                        tanentId : exposedService.tenantId,
                        systemId : exposedService.systemId,
                        domainId : exposedService.domainId,
                        status : exposedService.status,
                        createTime : exposedService.createTime,
                        updateTime : exposedService.updateTime
                    }
                var url = "/lcdp-proxy/lowcode/platform/be/api/parameter/load/" + exposedService.operationId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.exposedOperation.parameters = data.data;
                        this.showExposedServiceTest = true;
                    }
                });
            }else {
                ElMessage.error(`获取服务方法发布信息失败`);
            } 
        }
    }   
}

</script>