<template> 
    <el-form  :model="funcRegionAttrAssign" label-width="70px" :rules="validateRules" ref="funcRegionAttrAssignForm">
        <el-row :gutter="4" v-if="assignObject != null">
            <el-col :span="24">
                <el-form-item label="属性" required prop="attributeId">
                    <el-select v-model="funcRegionAttrAssign.attributeId" class="m-2" disabled placeholder="Select" size="small" >
                           <el-option v-for="item in assignObject.biDataSet.details" :key="item.id" :label="item.content.name" :value="item.id" />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="名称" required prop="displayName">
                    <el-input  v-model="funcRegionAttrAssign.displayName"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="类型" required prop="displayType">
                      <el-select v-model="funcRegionAttrAssign.displayType" disabled class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in displayTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="funcRegionAttrAssign.displayType == 'M'">
            <el-col :span="12">
                <el-form-item label="行数">
                    <el-input  v-model="funcRegionAttrAssign.rowSpan"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="列数">
                    <el-input  v-model="funcRegionAttrAssign.colSpan"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="funcRegionAttrAssign.displayType == 'O'">
            <el-col :span="24">
                <el-form-item label="是否维度" prop="isDimension">
                    <el-select v-model="funcRegionAttrAssign.displayCfgJSON.isDimension" class="m-2" clearable placeholder="Select" size="small" >
                        <el-option v-for="item in yesOrNoOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="funcRegionAttrAssign.displayType == 'Q' || funcRegionAttrAssign.displayType == 'O'">
            <el-col :span="24">
                <el-form-item label="显示格式" prop="displayFormat">
                    <el-select v-model="funcRegionAttrAssign.displayCfgJSON.displayFormat" class="m-2" clearable placeholder="Select" size="small" >
                           <el-option v-for="item in displayFormatOptions" :key="item.value" :label="item.label" :value="item.value" />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="funcRegionAttrAssign.displayCfgJSON.displayFormat == 'P' || funcRegionAttrAssign.displayCfgJSON.displayFormat == 'N' || funcRegionAttrAssign.displayCfgJSON.displayFormat == 'D'">
            <el-col :span="24">
                <el-form-item label="格式配置" prop="formatCfg">
                    <el-input  v-model="funcRegionAttrAssign.displayCfgJSON.formatCfg"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="funcRegionAttrAssign.displayType == 'S'">
            <el-col :span="24">
                <el-form-item label="获取方式" prop="acquireType">
                    <el-select v-model="funcRegionAttrAssign.displayCfgJSON.acquireType" class="m-2" clearable placeholder="Select" size="small" >
                        <el-option v-for="item in acquireTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="funcRegionAttrAssign.displayType == 'O'">
            <el-col :span="24">
                <el-form-item label="字典配置" prop="acquireType">
                    <el-select v-model="funcRegionAttrAssign.displayCfgJSON.acquireType" class="m-2" clearable placeholder="Select" size="small" >
                        <el-option v-for="item in acquireTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="funcRegionAttrAssign.displayCfgJSON.acquireType == 'M'">
            <el-col :span="24">
                <el-form-item label="调用服务">
                    <el-input type="hidden" v-model="funcRegionAttrAssign.displayCfgJSON.exposedServiceId"  size="small"/>
                    <el-input type="hidden" v-model="funcRegionAttrAssign.displayCfgJSON.serviceObjectId"  size="small"/>
                    <el-input type="hidden" v-model="funcRegionAttrAssign.displayCfgJSON.serviceOperationId"  size="small"/>
                    <el-input  v-model="funcRegionAttrAssign.displayCfgJSON.operationHttpMapping" readonly size="small"/>
                </el-form-item>                                                
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="funcRegionAttrAssign.displayCfgJSON.acquireType == 'M'">
            <el-col :span="12"></el-col>
            <el-col :span="12">
                <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectExposedService()" size="small"><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearExposedService()" size="small" ><label>清空</label></el-link>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="funcRegionAttrAssign.displayCfgJSON.acquireType == 'F'">
            <el-col :span="24">
                <el-form-item label="数据配置">
                    <el-input type="textarea" rows="3" v-model="funcRegionAttrAssign.displayCfgJSON.jsonData" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="样式">
                    <el-input type="textarea" rows="5" v-model="funcRegionAttrAssign.displayCfg" readonly size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>

     <!-- 选择系统对外服务方法信息 -->
     <exposedServiceSelect v-if="showExposedServiceSelect"  @exposedServiceSelected="exposedServiceSelected" @hideExposedServiceSelectPage="hideExposedServiceSelectPage" :showExposedServiceSelect="showExposedServiceSelect"  :exposedServicesForSelect="exposedServicesForSelect"/>

</template>
<script>
import { ref } from 'vue';
import axiosClient from '/src/js/utils/axios.js';

import exposedServiceSelect from '../../../workbench/components/exposeMgr/workbench-exposedServiceSelect.vue';

export default {
    props: ['funcRegionAttrAssign','objectAssigns', 'assignObject', 'reportCfgData'],
    
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
        const yesOrNoOptions = ref(null);

        const acquireTypeOptions = ref(null);

        const displayTypeOptions = ref(null);

        const displayFormatOptions = ref(null);

        const validateRules = ref({
            "objectId": [
                { required: true, message: '归属对象不能为空！', trigger: 'blur' }
            ],
            "attributeId": [
                { required: true, message: '绑定属性不能为空！', trigger: 'blur' }
            ],
            "displayName": [
                { required: true, message: '显示名称不能为空！', trigger: 'blur' }
            ],
            "displayType": [
                { required: true, message: '显示类型不能为空！', trigger: 'blur' }
            ],
        });

        const showExposedServiceSelect = ref(false);
        const exposedServicesForSelect = ref(null);
        
        return {
            showExposedServiceSelect,
        	exposedServicesForSelect,

            yesOrNoOptions,
            acquireTypeOptions,
            displayTypeOptions,
            displayFormatOptions,
            validateRules
        }
    },
    watch: {
        'funcRegionAttrAssign.displayCfgJSON': {
            handler: function(value, old) {
                if(value == null) {
                    this.funcRegionAttrAssign.displayCfgJSON = {};
                }
                this.funcRegionAttrAssign.displayCfg = JSON.stringify(this.funcRegionAttrAssign.displayCfgJSON);
            },
            deep: true
        },
        'funcRegionAttrAssign.displayCfgJSON.acquireType': {
            handler: function(value, old) {
                if(value == 'F') {
                    this.funcRegionAttrAssign.displayCfgJSON.exposedServiceId = "";
                    this.funcRegionAttrAssign.displayCfgJSON.serviceObjectId = "";
                    this.funcRegionAttrAssign.displayCfgJSON.serviceOperationId = "";
                    this.funcRegionAttrAssign.displayCfgJSON.operationHttpMapping = "";
                }else if(value == 'M') {
                    this.funcRegionAttrAssign.displayCfgJSON.jsonData = null;
                }
            }
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_YES_OR_NO','BI_DISPLAY_TYPE','BI_DATA_DISPLAY_FORMAT_TYPE','BI_DATA_ACQUIRE_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.yesOrNoOptions = data.data['PUB_YES_OR_NO'];
                this.displayTypeOptions = data.data['BI_DISPLAY_TYPE'];
                this.displayFormatOptions = data.data['BI_DATA_DISPLAY_FORMAT_TYPE'];
                this.acquireTypeOptions = data.data['BI_DATA_ACQUIRE_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
        selectExposedService() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/exposedService/list/" + this.funcRegionAttrAssign.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.exposedServicesForSelect = data.data;
                    this.showExposedServiceSelect = true;
                } 
            });
        },
        clearExposedService(){
        	this.funcRegionAttrAssign.displayCfgJSON.exposedServiceId = "";
            this.funcRegionAttrAssign.displayCfgJSON.serviceObjectId = "";
            this.funcRegionAttrAssign.displayCfgJSON.serviceOperationId = "";
            this.funcRegionAttrAssign.displayCfgJSON.operationHttpMapping = "";
        },
        exposedServiceSelected(obj) {
            this.funcRegionAttrAssign.displayCfgJSON.exposedServiceId = obj.id;
            this.funcRegionAttrAssign.displayCfgJSON.operationHttpMapping = obj.httpMapping;
            this.funcRegionAttrAssign.displayCfgJSON.serviceObjectId = obj.serviceId;
            this.funcRegionAttrAssign.displayCfgJSON.serviceOperationId = obj.operationId;
            this.exposedServicesForSelect = null;
            this.showExposedServiceSelect = false;
        },
        hideExposedServiceSelectPage(){
             this.exposedServicesForSelect = null;
             this.showExposedServiceSelect = false;
        },
    }
 }
</script>