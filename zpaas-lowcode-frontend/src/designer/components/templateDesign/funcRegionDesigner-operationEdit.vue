<template> 
     <el-form  :model="funcRegionOperation" label-width="60px" :rules="validateRules" ref="funcRegionOperationForm">
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="名称" required prop="name">
                    <el-input  v-model="funcRegionOperation.name" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="类型" required prop="operationType">
                      <el-select v-model="funcRegionOperation.operationType" clearable class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in operationTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="URL">
                    <el-input type="hidden" v-model="funcRegionOperation.exposedServiceId"  size="small"/>
                    <el-input type="hidden" v-model="funcRegionOperation.serviceObjectId"  size="small"/>
                    <el-input type="hidden" v-model="funcRegionOperation.serviceOperationId"  size="small"/>
                    <el-input  v-model="funcRegionOperation.exposedService.httpMapping" v-if="funcRegionOperation.exposedService != null" readonly size="small"/>
                    <el-input  v-model="funcRegionOperation.exposedServiceId" v-if="funcRegionOperation.exposedService == null" readonly size="small"/>
                </el-form-item>                                                
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12"></el-col>
            <el-col :span="12">
                <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectExposedService()" size="small"><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearExposedService()" size="small" ><label>清空</label></el-link>
                </el-form-item>
            </el-col>
        </el-row>
       <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="归属">
                    <el-select v-model="funcRegionOperation.objectAssignId" class="m-2" clearable placeholder="Select" size="small">
                           <el-option
                                  v-for="item in objectAssigns"
                                  :key="item.id"
                                  :label="item.assignObject.code"
                                  :value="item.id"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="目标">
                    <el-select v-model="funcRegionOperation.targetRegionId" class="m-2" clearable placeholder="Select" size="small">
                           <el-option
                                  v-for="item in funcRegions"
                                  :key="item.id"
                                  :label="item.name"
                                  :value="item.id"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="配置">
                    <el-input  v-model="funcRegionOperation.operationCfg" type="textarea" rows="6" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>

    <!-- 选择系统对外服务方法信息 -->
    <exposedServiceSelect v-if="showExposedServiceSelect"  @exposedServiceSelected="exposedServiceSelected" @hideExposedServiceSelectPage="hideExposedServiceSelectPage" :showExposedServiceSelect="showExposedServiceSelect"  :exposedServicesForSelect="exposedServicesForSelect"/>

</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';

import exposedServiceSelect from '../../../workbench/components/exposeMgr/workbench-exposedServiceSelect.vue';

export default {
    props: ['funcRegion','objectAssigns','funcRegionOperation', 'funcRegions', 'operationTypeOptions', 'pagepageDisplayTypeOptions'],
    
    emits: [],
    
    setup (props, {attrs, emit, slots}) {
        

        return {
            
        }
    },
    computed: {
        
    },
    components: {
        exposedServiceSelect
    },
    
    data() {   

        const showExposedServiceSelect = ref(false);
        const exposedServicesForSelect = ref(null);

        const validateRules = ref({
            "name": [
                { required: true, message: '操作名称不能为空！', trigger: 'blur' }
            ],
            "operationType": [
                { required: true, message: '操作类型不能为空！', trigger: 'blur' }
            ],
        });

        return {
            showExposedServiceSelect,
        	exposedServicesForSelect,
            validateRules
        }
    },
    mounted() {
        
    },
    methods: {
        selectExposedService() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/exposedService/list/" + this.funcRegionOperation.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                    this.exposedServicesForSelect = data.data;
                    this.showExposedServiceSelect = true;
                }
            });
        },
        clearExposedService(){
        	this.funcRegionOperation.exposedServiceId = "";
            this.funcRegionOperation.exposedService = {};
            this.funcRegionOperation.serviceObject = {};
            this.funcRegionOperation.serviceObjectId = "";
            this.funcRegionOperation.serviceOperationId = "";
            this.funcRegionOperation.operation = {};
        },
        exposedServiceSelected(obj) {
            this.funcRegionOperation.exposedServiceId = obj.id;
            this.funcRegionOperation.exposedService = obj;
            this.funcRegionOperation.serviceObject = obj.serviceObject;
            this.funcRegionOperation.serviceObjectId = obj.serviceId;
            this.funcRegionOperation.serviceOperationId = obj.operationId;
            this.funcRegionOperation.operation = obj.operation;
            this.exposedServicesForSelect = null;
            this.showExposedServiceSelect = false;
        },
        hideExposedServiceSelectPage(){
             this.exposedServicesForSelect = null;
             this.showExposedServiceSelect = false;
        }
    }
 }
</script>