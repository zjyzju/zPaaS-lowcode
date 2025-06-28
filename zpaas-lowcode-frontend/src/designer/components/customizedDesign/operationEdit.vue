<template> 
     <el-form  :model="funcRegionOperation" label-width="80px" :rules="validateRules" ref="funcRegionOperationForm">
        <el-collapse v-model="activeNames" @change="handleChange">
            <el-collapse-item title="基本配置" name="baseCfg">
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-form-item label="操作名称" required prop="name">
                            <el-input  v-model="funcRegionOperation.name" size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-form-item label="操作类型" required prop="operationType">
                            <el-select v-model="funcRegionOperation.operationType" clearable class="m-2" placeholder="Select" size="small">
                                <el-option v-for="item in operationTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-form-item label="调用服务">
                            <el-input type="hidden" v-model="funcRegionOperation.exposedServiceId"  size="small"/>
                            <el-input type="hidden" v-model="funcRegionOperation.serviceObjectId"  size="small"/>
                            <el-input type="hidden" v-model="funcRegionOperation.serviceOperationId"  size="small"/>
                            <el-input  v-model="funcRegionOperation.exposedService.httpMapping" v-if="funcRegionOperation.exposedService != null" readonly size="small"/>
                            <el-input  v-model="funcRegionOperation.exposedServiceId" v-if="funcRegionOperation.exposedService == null" readonly size="small"/>
                        </el-form-item>                                                
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="16"></el-col>
                    <el-col :span="8">
                        <el-form-item label="" label-width="10px">
                            <el-link  type="primary" @click="selectExposedService()" size="small"><label>选择</label></el-link>&nbsp;
                            <el-link  type="primary" @click="clearExposedService()" size="small" ><label>清空</label></el-link>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null && funcRegionOperation.operationType == 'Q'">
                    <el-col :span="24">
                        <el-form-item label="是否分页">
                            <el-select v-model="funcRegionOperation.operationCfgJSON.supportPages" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in yesNoOptions" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null && funcRegionOperation.operationCfgJSON.supportPages == 'Y'">
                    <el-col :span="24">
                        <el-form-item label="每页条数">
                            <el-input  v-model="funcRegionOperation.operationCfgJSON.pageRows" size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-collapse-item>
            <el-collapse-item title="事前交互" name="preCfg">
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null">
                    <el-col :span="24">
                        <el-form-item label="主校验">
                            <el-select v-model="funcRegionOperation.operationCfgJSON.mainCheckRule" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in trueFalseOptions" :key="item.value" :label="item.label" :value="item.value"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null">
                    <el-col :span="24">
                        <el-form-item label="事前提醒">
                            <el-select v-model="funcRegionOperation.operationCfgJSON.alertMethodPre" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in alertMethodOptions" :key="item.value" :label="item.label" :value="item.value"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null && (funcRegionOperation.operationCfgJSON.alertMethodPre == 'T' || funcRegionOperation.operationCfgJSON.alertMethodPre == 'C')">
                    <el-col :span="24">
                        <el-form-item label="类型-事前">
                            <el-select v-model="funcRegionOperation.operationCfgJSON.alertTypePre" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in alertTypeOptions" :key="item.value" :label="item.label" :value="item.value"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4"v-if="funcRegionOperation.operationCfgJSON != null && (funcRegionOperation.operationCfgJSON.alertMethodPre == 'T' || funcRegionOperation.operationCfgJSON.alertMethodPre == 'C')">
                    <el-col :span="24">
                        <el-form-item label="信息-事前">
                            <el-input  v-model="funcRegionOperation.operationCfgJSON.alertMsgPre" size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-form-item label="源数据" v-if="funcRegionOperation.operationCfgJSON != null">
                            <el-tree-select size="small" v-model="funcRegionOperation.operationCfgJSON.srcData" :filter-node-method="filterNode" :data="customizedPage.customizedLayouts" v-if="customizedPage.customizedLayouts != null" ref="srcDataTree" node-key="id" :props="{label:'name', children:'subCustomizedLayouts'}" multiple default-expand-all style="width: 240px"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-form-item label="显示类型">
                            <el-select v-model="funcRegionOperation.displayType" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in displayTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.displayType == 'P'">
                    <el-col :span="24">
                        <el-form-item label="目标页面">
                            <el-select v-model="funcRegionOperation.targetRegionId" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in customizedPages" :key="item.id" :label="item.name" :value="item.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null && funcRegionOperation.operationType != 'V'">
                    <el-col :span="24">
                        <el-form-item label="传入参数">
                            <el-input type="textarea" :rows="3"  v-model="funcRegionOperation.operationCfgJSON.params" placeholder='[{"valueType":"F/A","value":"","targetAttr":""}]' size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null && funcRegionOperation.operationType != 'V'">
                    <el-col :span="24">
                        <el-form-item label="加入回填">
                            <el-input  v-model="funcRegionOperation.operationCfgJSON.addToTargetAttr" placeholder='填一个回填到目标的属性' size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-collapse-item>
            <el-collapse-item title="事后交互" name="postCfg">
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null">
                    <el-col :span="24">
                        <el-form-item label="事后提醒">
                            <el-select v-model="funcRegionOperation.operationCfgJSON.alertMethodPost" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in alertMethodPostOptions" :key="item.value" :label="item.label" :value="item.value"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null && (funcRegionOperation.operationCfgJSON.alertMethodPost == 'T' || funcRegionOperation.operationCfgJSON.alertMethodPost == 'C')">
                    <el-col :span="24">
                        <el-form-item label="类型-事后">
                            <el-select v-model="funcRegionOperation.operationCfgJSON.alertTypePost" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in alertTypeOptions" :key="item.value" :label="item.label" :value="item.value"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4"v-if="funcRegionOperation.operationCfgJSON != null && (funcRegionOperation.operationCfgJSON.alertMethodPost == 'T' || funcRegionOperation.operationCfgJSON.alertMethodPost == 'C')">
                    <el-col :span="24">
                        <el-form-item label="信息-事后">
                            <el-input  v-model="funcRegionOperation.operationCfgJSON.alertMsgPost" size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null">
                    <el-col :span="24">
                        <el-form-item label="关闭弹窗">
                            <el-select v-model="funcRegionOperation.operationCfgJSON.closePopWindow" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in yesNoOptions" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null">
                    <el-col :span="24">
                        <el-form-item label="事后操作">
                            <el-select v-model="funcRegionOperation.operationCfgJSON.postAction" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in postActionOptions" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null && (funcRegionOperation.operationCfgJSON.postAction == 'R' || funcRegionOperation.operationCfgJSON.postAction == 'U')">
                    <el-col :span="24">
                        <el-form-item label="目标页面">
                            <el-select v-model="funcRegionOperation.operationCfgJSON.refreshTargetPage" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in customizedPages" :key="item.id" :label="item.name" :value="item.id" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null && (funcRegionOperation.operationCfgJSON.postAction == 'R' || funcRegionOperation.operationCfgJSON.postAction == 'U') && funcRegionOperation.operationCfgJSON.refreshTargetPage != null">
                    <el-col :span="24">
                        <el-form-item label="更新目标">
                            <el-tree-select size="small" v-model="funcRegionOperation.operationCfgJSON.refreshTarget" :filter-node-method="filterNode" :data="refreshTargetPage.customizedLayouts" v-if="refreshTargetPage != null && refreshTargetPage.customizedLayouts != null" ref="refreshTargetTableTree" node-key="id" :props="{label:'name', children:'subCustomizedLayouts'}" multiple default-expand-all style="width: 240px"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null && funcRegionOperation.operationCfgJSON.postAction == 'U'">
                    <el-col :span="24">
                        <el-form-item label="回填映射">
                            <el-input type="textarea" :rows="3"  v-model="funcRegionOperation.operationCfgJSON.updateMapping" placeholder='[{"valueType":"F/A","value":"","targetAttr":""}]' size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null && funcRegionOperation.operationCfgJSON.postAction == 'U'">
                    <el-col :span="24">
                        <el-form-item label="保留历史">
                            <el-select v-model="funcRegionOperation.operationCfgJSON.retainHistory" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in yesNoOptions" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-collapse-item>
            <el-collapse-item title="样式配置" name="styleCfg">
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null">
                    <el-col :span="24">
                        <el-form-item label="按钮样式">
                            <el-select v-model="funcRegionOperation.operationCfgJSON.displayStyle" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in displayStyleOptions" :key="item.value" :label="item.label" :value="item.value"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null">
                    <el-col :span="24">
                        <el-form-item label="按钮色系">
                            <el-select v-model="funcRegionOperation.operationCfgJSON.displayColor" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in displayColorOptions" :key="item.value" :label="item.label" :value="item.value"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="funcRegionOperation.operationCfgJSON != null">
                    <el-col :span="24">
                        <el-form-item label="按钮大小">
                            <el-select v-model="funcRegionOperation.operationCfgJSON.displaySize" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in displaySizeOptions" :key="item.value" :label="item.label" :value="item.value"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-collapse-item>
            <el-collapse-item title="弹出配置" name="popupCfg" v-if="funcRegionOperation.operationCfgJSON != null && funcRegionOperation.operationType == 'V'">
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-form-item label="功能类型">
                            <el-select v-model="funcRegionOperation.operationCfgJSON.funcType" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in funcTypeOptions" :key="item.value" :label="item.label" :value="item.value"/>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="24" v-if="funcRegionOperation.operationCfgJSON.funcType == 'S'">
                        <el-form-item label="子功能">
                            <el-input  v-model="funcRegionOperation.operationCfgJSON.subFuncId" size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-form-item label="传入参数">
                            <el-input type="textarea" :rows="3"  v-model="funcRegionOperation.operationCfgJSON.params" placeholder='[{"valueType":"F/A","value":"","targetAttr":""}]' size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-form-item label="返回值">
                            <el-input type="textarea" :rows="3"  v-model="funcRegionOperation.operationCfgJSON.returnParams" placeholder='[{"valueType":"F/A","value":"","targetAttr":""}]' size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-form-item label="上级属性">
                            <el-input  v-model="funcRegionOperation.operationCfgJSON.subFuncParentAttr" size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-form-item label="子功能组">
                            <el-input type="textarea" :rows="3"  v-model="funcRegionOperation.operationCfgJSON.subFuncMap" placeholder='{"{parentValue}"":"{subFuncId}"}' size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-form-item label="返回值组">
                            <el-input type="textarea" :rows="3"  v-model="funcRegionOperation.operationCfgJSON.returnParamsMap" placeholder='{"{parentValue}"":[{"valueType":"F/A","value":"","targetAttr":""}]}' size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-collapse-item>
        </el-collapse>
        
        <!-- <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="操作配置">
                    <el-input  v-model="funcRegionOperation.operationCfg" type="textarea" rows="6" readonly size="small"/>
                </el-form-item>
            </el-col>
        </el-row> -->
    </el-form>

    <!-- 选择系统对外服务方法信息 -->
    <exposedServiceSelect v-if="showExposedServiceSelect"  @exposedServiceSelected="exposedServiceSelected" @hideExposedServiceSelectPage="hideExposedServiceSelectPage" :showExposedServiceSelect="showExposedServiceSelect"  :exposedServicesForSelect="exposedServicesForSelect"/>

</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';

import exposedServiceSelect from '../../../workbench/components/exposeMgr/workbench-exposedServiceSelect.vue';

export default {
    props: ['customizedPage','objectAssigns','funcRegionOperation', 'customizedPages'],
    
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
        const operationTypeOptions = ref(null);

        const displayTypeOptions = ref(null);

        const yesNoOptions = ref(null);

        const trueFalseOptions = ref(null);

        const displayStyleOptions = ref(null);

        const displayColorOptions = ref(null);

        const displaySizeOptions = ref(null);

        const funcTypeOptions = ref(null);

        const alertMethodOptions = ref(null);

        const alertMethodPostOptions = ref(null);

        const alertTypeOptions = ref(null);
        
        const postActionOptions = ref(null);
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

        const activeNames = ['baseCfg'];
        const refreshTargetPage = ref(null);

        return {
            operationTypeOptions,
        	displayTypeOptions,
        	showExposedServiceSelect,
        	exposedServicesForSelect,
            validateRules,
            displayStyleOptions,
            displayColorOptions,
            displaySizeOptions,
            yesNoOptions,
            funcTypeOptions,
            alertTypeOptions,
            alertMethodOptions,
            alertMethodPostOptions,
            trueFalseOptions,
            
            activeNames,
            refreshTargetPage,
            postActionOptions
        }
    },
    watch: {
        'funcRegionOperation.operationCfgJSON': {
            handler: function(value, old) {
                if(value == null) {
                    this.funcRegionOperation.operationCfgJSON = {};
                }
                this.funcRegionOperation.operationCfg = JSON.stringify(this.funcRegionOperation.operationCfgJSON);
            },
            deep: true
        },
        'funcRegionOperation.operationCfgJSON.refreshTargetPage': function(value, old) {
            if(value == null) {
                this.refreshTargetPage = null;
            }else {
                for(var i in this.customizedPages) {
                    if(this.customizedPages[i].id == value) {
                        this.refreshTargetPage = this.customizedPages[i];
                        break;
                    }
                }
            }
        }
        
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['POST_ACTION_TYPE','ALERT_LEVEL_TYPE','POST_ALERT_METHOD_TYPE','ALERT_METHOD_TYPE','SUB_FUNC_TYPE','OPERATION_TYPE_CUSTOMIZED','DISPLAY_PAGE_TYPE','PUB_YES_OR_NO','PUB_TRUE_OR_FALSE','BUTTON_DISPLAY_STYLE','DISPLAY_COLOR_TYPE','DISPLAY_SIZE_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.operationTypeOptions = data.data['OPERATION_TYPE_CUSTOMIZED'];
                this.displayTypeOptions = data.data['DISPLAY_PAGE_TYPE'];
                this.yesNoOptions = data.data['PUB_YES_OR_NO'];
                this.trueFalseOptions = data.data['PUB_TRUE_OR_FALSE'];
                this.displayStyleOptions = data.data['BUTTON_DISPLAY_STYLE'];
                this.displayColorOptions = data.data['DISPLAY_COLOR_TYPE'];
                this.displaySizeOptions = data.data['DISPLAY_SIZE_TYPE'];
                this.funcTypeOptions = data.data['SUB_FUNC_TYPE'];
                this.alertMethodOptions = data.data['ALERT_METHOD_TYPE'];
                this.alertMethodPostOptions = data.data['POST_ALERT_METHOD_TYPE'];
                this.alertTypeOptions = data.data['ALERT_LEVEL_TYPE'];
                this.postActionOptions = data.data['POST_ACTION_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        if(this.funcRegionOperation.operationCfgJSON == null) {
            if(this.funcRegionOperation.operationCfg == null || this.funcRegionOperation.operationCfg.trim().length == 0) {
                this.funcRegionOperation.operationCfgJSON = {};
            }else {
                this.funcRegionOperation.operationCfgJSON = JSON.parse(this.funcRegionOperation.operationCfg);
            }
        }

        if(this.funcRegionOperation.operationCfgJSON.refreshTargetPage != null) {
            for(var i in this.customizedPages) {
                if(this.customizedPages[i].id == this.funcRegionOperation.operationCfgJSON.refreshTargetPage) {
                    this.refreshTargetPage = this.customizedPages[i];
                    break;
                }
            }
        }
        this.$nextTick(() => {
            if(this.customizedPage.customizedLayouts != null && this.customizedPage.customizedLayouts.length > 0) {
                this.$refs.srcDataTree.filter("");
                if(this.$refs.targetDataTree != null) {
                    this.$refs.targetDataTree.filter("");
                }
            }
        });
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
        },
        filterNode(value, data) {
            if(data != null && (data.componentCode =='MCL' || data.componentCode == 'NCL' || data.componentCode == 'TCL')) {
                return true;
            }else {
                return false;
            }
        },
        handleChange(val) {
            console.log(val);
        }
    }
 }
</script>