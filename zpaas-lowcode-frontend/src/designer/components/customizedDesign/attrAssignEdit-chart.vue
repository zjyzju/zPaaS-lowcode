<template> 
    <el-form  :model="formComponent" label-width="70px" :rules="validateRules" ref="formComponentForm">
        <el-row :gutter="4" v-if="assignObject != null">
            <el-col :span="24">
                <el-form-item label="属性" required prop="attributeId">
                    <el-select v-model="formComponent.attributeId" class="m-2" disabled placeholder="Select" size="small" >
                           <el-option v-for="item in assignObject.biDataSet.details" :key="item.id" :label="item.content.name" :value="item.id" />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="名称" required prop="displayName">
                    <el-input  v-model="formComponent.displayName"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="类型" required prop="displayType">
                      <el-select v-model="formComponent.displayType" disabled class="m-2" placeholder="Select" size="small">
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
        <el-row :gutter="4" v-if="formComponent.displayType == 'M'">
            <el-col :span="12">
                <el-form-item label="行数">
                    <el-input  v-model="formComponent.rowSpan"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="列数">
                    <el-input  v-model="formComponent.colSpan"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="formComponent.displayType == 'O'">
            <el-col :span="24">
                <el-form-item label="是否维度" prop="isDimension">
                    <el-select v-model="formComponent.displayCfgJSON.isDimension" class="m-2" clearable placeholder="Select" size="small" >
                        <el-option v-for="item in yesOrNoOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="formComponent.displayType == 'Q' || formComponent.displayType == 'O'">
            <el-col :span="24">
                <el-form-item label="显示格式" prop="displayFormat">
                    <el-select v-model="formComponent.displayCfgJSON.displayFormat" class="m-2" clearable placeholder="Select" size="small" >
                           <el-option v-for="item in displayFormatOptions" :key="item.value" :label="item.label" :value="item.value" />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="formComponent.displayCfgJSON.displayFormat == 'P' || formComponent.displayCfgJSON.displayFormat == 'N' || formComponent.displayCfgJSON.displayFormat == 'D'">
            <el-col :span="24">
                <el-form-item label="格式配置" prop="formatCfg">
                    <el-input  v-model="formComponent.displayCfgJSON.formatCfg"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="formComponent.displayType == 'S'">
            <el-col :span="24">
                <el-form-item label="获取方式" prop="acquireType">
                    <el-select v-model="formComponent.displayCfgJSON.acquireType" class="m-2" clearable placeholder="Select" size="small" >
                        <el-option v-for="item in acquireTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="formComponent.displayType == 'O'">
            <el-col :span="24">
                <el-form-item label="字典配置" prop="acquireType">
                    <el-select v-model="formComponent.displayCfgJSON.acquireType" class="m-2" clearable placeholder="Select" size="small" >
                        <el-option v-for="item in acquireTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="formComponent.displayCfgJSON.acquireType == 'M'">
            <el-col :span="24">
                <el-form-item label="调用服务">
                    <el-input type="hidden" v-model="formComponent.displayCfgJSON.exposedServiceId"  size="small"/>
                    <el-input type="hidden" v-model="formComponent.displayCfgJSON.serviceObjectId"  size="small"/>
                    <el-input type="hidden" v-model="formComponent.displayCfgJSON.serviceOperationId"  size="small"/>
                    <el-input  v-model="formComponent.displayCfgJSON.operationHttpMapping" readonly size="small"/>
                </el-form-item>                                                
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="formComponent.displayCfgJSON.acquireType == 'M'">
            <el-col :span="12"></el-col>
            <el-col :span="12">
                <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectExposedService()" size="small"><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearExposedService()" size="small" ><label>清空</label></el-link>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="formComponent.displayCfgJSON.acquireType == 'F'">
            <el-col :span="24">
                <el-form-item label="数据配置">
                    <el-input type="textarea" rows="3" v-model="formComponent.displayCfgJSON.jsonData" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="样式">
                    <el-input type="textarea" rows="5" v-model="formComponent.displayCfg" readonly size="small"/>
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
    props: ['formComponent','objectAssigns', 'assignObject'],
    
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
        'formComponent.displayCfgJSON': {
            handler: function(value, old) {
                if(value == null) {
                    this.formComponent.displayCfgJSON = {};
                }
                this.formComponent.displayCfg = JSON.stringify(this.formComponent.displayCfgJSON);
            },
            deep: true
        },
        'formComponent.displayCfgJSON.acquireType': {
            handler: function(value, old) {
                if(value == 'F') {
                    this.formComponent.displayCfgJSON.exposedServiceId = "";
                    this.formComponent.displayCfgJSON.serviceObjectId = "";
                    this.formComponent.displayCfgJSON.serviceOperationId = "";
                    this.formComponent.displayCfgJSON.operationHttpMapping = "";
                }else if(value == 'M') {
                    this.formComponent.displayCfgJSON.jsonData = null;
                }
            }
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_YES_OR_NO','BI_DISPLAY_TYPE','BI_DATA_ACQUIRE_TYPE','BI_DATA_DISPLAY_FORMAT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.yesOrNoOptions = data.data['PUB_YES_OR_NO'];
                this.displayTypeOptions = data.data['BI_DISPLAY_TYPE'];
                this.acquireTypeOptions = data.data['BI_DATA_ACQUIRE_TYPE'];
                this.displayFormatOptions = data.data['BI_DATA_DISPLAY_FORMAT_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
        selectExposedService() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/exposedService/list/" + this.formComponent.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.exposedServicesForSelect = data.data;
                    this.showExposedServiceSelect = true;
                }
            });
        },
        clearExposedService(){
        	this.formComponent.displayCfgJSON.exposedServiceId = "";
            this.formComponent.displayCfgJSON.serviceObjectId = "";
            this.formComponent.displayCfgJSON.serviceOperationId = "";
            this.formComponent.displayCfgJSON.operationHttpMapping = "";
        },
        exposedServiceSelected(obj) {
            this.formComponent.displayCfgJSON.exposedServiceId = obj.id;
            this.formComponent.displayCfgJSON.operationHttpMapping = obj.httpMapping;
            this.formComponent.displayCfgJSON.serviceObjectId = obj.serviceId;
            this.formComponent.displayCfgJSON.serviceOperationId = obj.operationId;
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