<!-- 新建功能区域操作 -->
<template>
<el-dialog v-model="showFlag" v-if="showFuncRegionOperationCreate ==true && newFuncRegionOperation != null" title="新建功能区域绑定操作">
    <template #header>
        <span class="title">新建功能区域绑定操作</span>
    </template>
    <el-form  :model="newFuncRegionOperation" label-width="120px" :rules="validateRules" ref="newFuncRegionOperationForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="标识">
                    <el-input v-model="newFuncRegionOperation.id" readonly size="small"/>
                    <el-input type="hidden" v-model="newFuncRegionOperation.funcId"  size="small"/>
                    <el-input type="hidden" v-model="newFuncRegionOperation.funcRegionId" size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="操作名称" required prop="name">
                    <el-input  v-model="newFuncRegionOperation.name" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="自定义操作">
                    <el-radio-group v-model="newFuncRegionOperation.isUserDefined" class="ml-4">
                        <el-radio value="Y" size="large">是</el-radio>
                        <el-radio value="N" size="large">否</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="操作模板">
                    <el-select v-model="newFuncRegionOperation.tplOperationId" clearable class="m-2" placeholder="Select" size="small">
                        <el-option
                                  v-for="item in tplOperations" v-if="tplOperations != null"
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
                <el-form-item label="操作类型" required prop="operationType">
                      <el-select v-model="newFuncRegionOperation.operationType" clearable class="m-2" placeholder="Select" size="small">
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
                      <el-select v-model="newFuncRegionOperation.displayType" class="m-2" placeholder="Select" size="small">
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
                    <el-input  v-model="newFuncRegionOperation.operationCfg" type="textarea" rows="6" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="16">
                <el-form-item label="服务URL">
                    <el-input type="hidden" v-model="newFuncRegionOperation.exposedServiceId"  size="small"/>
                    <el-input type="hidden" v-model="newFuncRegionOperation.serviceObjectId"  size="small"/>
                    <el-input type="hidden" v-model="newFuncRegionOperation.serviceOperationId"  size="small"/>
                    <el-input  v-model="newFuncRegionOperation.exposedService.httpMapping" readonly size="small"/>
                    
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
                    <el-input  v-model="newFuncRegionOperation.serviceObject.code" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="服务方法">
                    <el-input  v-model="newFuncRegionOperation.operation.code" readonly size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="归属绑定对象">
                    <el-select v-model="newFuncRegionOperation.objectAssignId" class="m-2" clearable placeholder="Select" size="small">
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
                    <el-select v-model="newFuncRegionOperation.targetRegionId" class="m-2" clearable placeholder="Select" size="small">
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
                    <el-input  v-model="newFuncRegionOperation.displayOrder"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                &nbsp;
            </el-col>
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideFuncRegionOperationCreatePage">取消</el-button>
            <el-button type="primary" @click="addFuncRegionOperation()">确定</el-button>
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
	props: ['showFuncRegionOperationCreate','newFuncRegionOperation', 'funcRegions', 'funcRegion','objectAssigns'],
    
    emits: ['addFuncRegionOperationToList', 'hideFuncRegionOperationCreatePage'],
    
    setup (props, {attrs, emit, slots}) {
        const hideFuncRegionOperationCreatePage = () => {
            emit('hideFuncRegionOperationCreatePage')
        };
        const addFuncRegionOperationToList = (updateParam) => {
        	emit('addFuncRegionOperationToList', updateParam)
        };
        
        return {
        	addFuncRegionOperationToList,
        	hideFuncRegionOperationCreatePage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showFuncRegionOperationCreate;
            },
            set(value) {
                this.hideFuncRegionOperationCreatePage();
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
        
        const tplOperations = ref([]);

        const validateRules = ref({
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
        	
        	tplOperations,
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

    	var url = "/lcdp-proxy/lowcode/platform/fe/api/funcTemplateRegionOperation/loadByTplRegion/" + this.funcRegion.tplRegionId;
        axiosClient.get(url).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.tplOperations = data.data;
            }
        });
    },
    methods: {
    	addFuncRegionOperation() {
            var url = "/lcdp-proxy/lowcode/platform/fe/api/funcRegionOperation/add";
            this.$refs.newFuncRegionOperationForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.newFuncRegionOperation).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            ElMessage(`新增功能区域绑定操作信息成功!`);
                            this.addFuncRegionOperationToList(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        selectExposedService() {
           var url = "/lcdp-proxy/lowcode/platform/be/api/exposedService/list/" + this.newFuncRegionOperation.systemId;
           axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.exposedServicesForSelect = data.data;
                    this.showExposedServiceSelect = true;
                }
           });
        },
        clearExposedService(){
        	this.newFuncRegionOperation.exposedServiceId = "";
            this.newFuncRegionOperation.exposedService = {};
            this.newFuncRegionOperation.serviceObject = {};
            this.newFuncRegionOperation.serviceObjectId = "";
            this.newFuncRegionOperation.serviceOperationId = "";
            this.newFuncRegionOperation.operation = {};
        },
        exposedServiceSelected(obj) {
            this.newFuncRegionOperation.exposedServiceId = obj.id;
            this.newFuncRegionOperation.exposedService = obj;
            this.newFuncRegionOperation.serviceObject = obj.serviceObject;
            this.newFuncRegionOperation.serviceObjectId = obj.serviceId;
            this.newFuncRegionOperation.serviceOperationId = obj.operationId;
            this.newFuncRegionOperation.operation = obj.operation;
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