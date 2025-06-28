<!-- 编辑功能区域操作 -->
<template>
<el-dialog v-model="showFlag" v-if="showFuncRegionOperationEdit ==true && funcRegionOperation != null" title="编辑功能区域绑定操作">
    <template #header>
        <span class="title">编辑功能区域绑定操作</span>
    </template>
    <el-form  :model="funcRegionOperation" label-width="120px" :rules="validateRules" ref="funcRegionOperationForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="标识" required prop="id">
                    <el-input v-model="funcRegionOperation.id" readonly size="small"/>
                    <el-input type="hidden" v-model="funcRegionOperation.funcId"  size="small"/>
                    <el-input type="hidden" v-model="funcRegionOperation.funcRegionId" size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="操作名称" required prop="name">
                    <el-input  v-model="funcRegionOperation.name" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="自定义操作">
                    <el-radio-group v-model="funcRegionOperation.isUserDefined" disabled class="ml-4">
                        <el-radio value="Y" size="large">是</el-radio>
                        <el-radio value="N" size="large">否</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="操作模板标识">
                    <el-input  v-model="funcRegionOperation.tplOperationId" clearable readonly size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="操作类型" required prop="operationType">
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
            <el-col :span="12">
                <el-form-item label="显示类型">
                      <el-select v-model="funcRegionOperation.displayType" class="m-2" placeholder="Select" size="small">
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
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="操作配置信息">
                    <el-input  v-model="funcRegionOperation.operationCfg" type="textarea" rows="6" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="16">
                <el-form-item label="服务URL">
                    <el-input type="hidden" v-model="funcRegionOperation.exposedServiceId"  size="small"/>
                    <el-input type="hidden" v-model="funcRegionOperation.serviceObjectId"  size="small"/>
                    <el-input type="hidden" v-model="funcRegionOperation.serviceOperationId"  size="small"/>
                    <el-input  v-model="funcRegionOperation.exposedService.httpMapping" v-if="funcRegionOperation.exposedService != null" readonly size="small"/>
                     <el-input  v-model="funcRegionOperation.exposedServiceId" v-if="funcRegionOperation.exposedService == null" readonly size="small"/>
                </el-form-item>                                                
            </el-col>
            <el-col :span="8">
                <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectExposedService()" ><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearExposedService()" ><label>清空</label></el-link>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="服务对象">
                    <el-input  v-model="funcRegionOperation.serviceObject.code" v-if="funcRegionOperation.serviceObject != null" readonly size="small"/>
                    <el-input  v-model="funcRegionOperation.serviceObjectId" v-if="funcRegionOperation.serviceObject == null" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="服务方法">
                    <el-input  v-model="funcRegionOperation.operation.code" v-if="funcRegionOperation.operation != null" readonly size="small"/>
                    <el-input  v-model="funcRegionOperation.serviceOperationId" v-if="funcRegionOperation.operation == null" readonly size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="归属绑定对象">
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
            <el-col :span="12">
                <el-form-item label="目标区域">
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
            <el-col :span="12">
                <el-form-item label="显示顺序" required prop="displayOrder">
                    <el-input  v-model="funcRegionOperation.displayOrder"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                &nbsp;
            </el-col>
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideFuncRegionOperationEditPage">取消</el-button>
            <el-button type="primary" @click="saveFuncRegionOperation()">确定</el-button>
        </span>
    </template>
</el-dialog>

<!-- 选择系统对外服务方法信息 -->
<exposedServiceSelect v-if="showExposedServiceSelect"  @exposedServiceSelected="exposedServiceSelected" @hideExposedServiceSelectPage="hideExposedServiceSelectPage" :showExposedServiceSelect="showExposedServiceSelect"  :exposedServicesForSelect="exposedServicesForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';

import exposedServiceSelect from '../exposeMgr/workbench-exposedServiceSelect.vue';


export default {
	props: ['showFuncRegionOperationEdit','funcRegionOperation', 'funcRegions','objectAssigns'],
    
    emits: ['updateFuncRegionOperationToList', 'hideFuncRegionOperationEditPage'],
    
    setup (props, {attrs, emit, slots}) {
        const hideFuncRegionOperationEditPage = () => {
            emit('hideFuncRegionOperationEditPage')
        };
        const updateFuncRegionOperationToList = (updateParam) => {
        	emit('updateFuncRegionOperationToList', updateParam)
        };
        
        return {
        	updateFuncRegionOperationToList,
        	hideFuncRegionOperationEditPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showFuncRegionOperationEdit;
            },
            set(value) {
                this.hideFuncRegionOperationEditPage();
            }
        }
    },
    components: {
    	exposedServiceSelect
    },
    watch: {        
        
    },
    data() {
        const operationTypeOptions = ref(null);
        
        const displayTypeOptions = ref(null);
        
        const showExposedServiceSelect = ref(false);
        const exposedServicesForSelect = ref(null);

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '操作名称不能为空！', trigger: 'blur' }
            ],
            "operationType": [
                { required: true, message: '操作类型不能为空！', trigger: 'blur' }
            ],
            "displayOrder": [
                { required: true, message: '顺序不能为空！', trigger: 'blur' },
                { pattern: /^[0-9]*$/, message: '顺序必须是整数类型！', trigger: 'blur' }
            ]
        });
        
        return {
        	operationTypeOptions,
        	displayTypeOptions,
        	showExposedServiceSelect,
        	exposedServicesForSelect,
            validateRules
        }
    },
    mounted(){
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OPERATION_TYPE','DISPLAY_PAGE_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.operationTypeOptions = data.data['OPERATION_TYPE'];
                this.displayTypeOptions = data.data['DISPLAY_PAGE_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	saveFuncRegionOperation() {
            var url = "/lcdp-proxy/lowcode/platform/fe/api/funcRegionOperation/save";
            this.$refs.funcRegionOperationForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.funcRegionOperation).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            ElMessage(`保存功能区域绑定操作信息成功!`);
                            this.updateFuncRegionOperationToList(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
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