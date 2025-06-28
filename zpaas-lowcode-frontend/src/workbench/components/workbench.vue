<template>
<div class="common-layout">
<el-container class="layout-container-main" style="height: 500px"> 
    <!-- header begin -->
    <el-header style="text-align: right; font-size: 12px" height="52px">
	    <div class="toolbar2" style="width:100%">
	        <el-row :gutter="4">
	            <el-col :span="5" style="width:260px;"  class="toolbar1">
	                <el-icon size="35px"><grid /></el-icon>
	                <label style="font-size:22px">zPaaS低代码开发平台</label>       
	            </el-col>
	            <el-col :span="15" style="width:900px">
	                <div class="toolbar" style="width:100%">
	                    <label style="font-size:22px;" v-if="businessSystemInfo.businessSystem">{{businessSystemInfo.businessSystem.description}}</label>
	                </div>
	            </el-col>
	            <el-col :span="4" style="width:200px">
	                <div class="toolbar" style="width:100%">
	                    <el-dropdown>
	                        <el-icon style="margin-right: 8px; margin-top: 1px"><setting color="#ffffff"/></el-icon>
	                        <template #dropdown>
	                            <el-dropdown-menu>
	                                <el-dropdown-item @click="showBusinessSystemEditPage()">编辑系统信息</el-dropdown-item>
	                                <el-dropdown-item @click="showBusinessSystemAutoLoadEditPage()">编辑系统自动加载信息</el-dropdown-item>
	                                <el-dropdown-item @click="showBusinessSystemAuthEditPage()">编辑系统认证信息</el-dropdown-item>
	                                <el-dropdown-item @click="viewExposedServices()">查看暴露服务</el-dropdown-item>
                                    <el-dropdown-item @click="showBusinessDomainMgrPage()">管理业务域</el-dropdown-item>
	                                <el-dropdown-item @click="closeWorkbench()">关闭工作台</el-dropdown-item>
	                                <el-dropdown-item @click="reloadSystemCache()">刷新缓存</el-dropdown-item>
	                                <el-dropdown-item @click="changePassword()">修改密码</el-dropdown-item>
                                    <el-dropdown-item @click="exit()">退出登录</el-dropdown-item>
	                            </el-dropdown-menu>
	                        </template>
	                    </el-dropdown>
	                    <span>{{this.loginInfo.loginUser.realName}}，您好！</span>
	                </div>   
	            </el-col>
	        </el-row>   
	    </div>   
	</el-header>
	<!-- header end -->
	<el-divider style="margin-top: 1px;margin-bottom: 1px"></el-divider>
    <el-container >
        <!-- left side begin -->
        <el-aside :width="asideWidth" ref="workbenchAside">
		    <workbenchNavigate :businessSystemInfo="businessSystemInfo" @handleClick="handleClick" @collapseAside="collapseAside" @reloadBusinessSystem='reloadBusinessSystem' @manageObjectCreateStep1='manageObjectCreateStep1'/>
		</el-aside>
        <!-- left side end -->
        <el-divider direction="vertical" border-style="solid" style="height:100%;margin-left: 1px;margin-right: 1px"></el-divider>
        
	    <el-main>
	        <el-scrollbar height="90vh" >
		        <br/>
	            <el-row :gutter="4">
                   <el-col :span="24"> 
                        <router-view v-if="showObjectEdit && editObjectKey != null"   @deleteFromList="deleteFromList" @updateToList="updateToList"/>
                    </el-col>
                </el-row>
            </el-scrollbar>
	   </el-main>
    </el-container>
</el-container>
</div>
<!-- 主页面结束 -->


<!-- 新建管理对象1 -->
<el-dialog v-model="showManagedObjectCreate1" title="新建管理对象">
    <template #header>
        <span class="title">新建管理对象</span>
    </template>
    <el-scrollbar  height="420px" v-if="showManagedObjectCreate1">
        <el-tree
            ref="managedObjectTypeSelect"
		    :data="managedObjectTypes"
		    show-checkbox
		    node-key="value"
		    default-expand-all
		    :props="defaultProps"
		    :default-checked-keys="currentObjectType"
		    @check-change="selectTreeNode"
		  />
    
    </el-scrollbar>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="showManagedObjectCreate1 = false">取消</el-button>
            <el-button type="primary" @click="manageObjectCreateNext()">下一步</el-button>
        </span>
    </template>
</el-dialog>

<!-- 根据数据库表生成 -->
<modelDrivenGenerate v-if="showModelDrivenGenerate==true && modelDrivenRequest != null" @hideModelDrivenGeneratePage="hideModelDrivenGeneratePage" @manageModelDrivenGenerateBack="manageModelDrivenGenerateBack" :modelDrivenRequest="modelDrivenRequest" :showModelDrivenGenerate="showModelDrivenGenerate" :dbSchemas="businessSystemInfo.dbSchemas" :businessDomains="businessSystemInfo.businessDomains"/>

<!-- 编辑业务系统 -->
<editBusinessSystem v-if="showBusinessSystemEdit==true && businessSystem != null" @updateBusinessSystemToInfo="updateBusinessSystemToInfo" @hideBusinessSystemEditPage="hideBusinessSystemEditPage"  :showBusinessSystemEdit="showBusinessSystemEdit"  :businessSystem="businessSystem"/>

<!-- 编辑业务系统认证信息 -->
<editBusinessSystemAuth v-if="showBusinessSystemAuthEdit==true && businessSystemAuth != null" @updateBusinessSystemAuthToInfo="updateBusinessSystemAuthToInfo" @hideBusinessSystemAuthEditPage="hideBusinessSystemAuthEditPage"  :showBusinessSystemAuthEdit="showBusinessSystemAuthEdit"  :businessSystemAuth="businessSystemAuth"/>

<!-- 编辑业务系统自动加载信息 -->
<editBusinessSystemAutoLoad v-if="showBusinessSystemAutoLoadEdit==true && businessSystemAutoLoad != null" @updateBusinessSystemAutoLoadToInfo="updateBusinessSystemAutoLoadToInfo" @hideBusinessSystemAutoLoadEditPage="hideBusinessSystemAutoLoadEditPage"  :showBusinessSystemAutoLoadEdit="showBusinessSystemAutoLoadEdit"  :businessSystemAutoLoad="businessSystemAutoLoad"/>

<!-- 管理业务域 -->
<mgrBusinessDomain v-if="showBusinessDomainMgr==true" @hideBusinessDomainMgrPage="hideBusinessDomainMgrPage" :showBusinessDomainMgr="showBusinessDomainMgr"  :systemId="businessSystemInfo.businessSystem.id"/>


<!-- 新建业务域 -->
<createBusinessDomain v-if="showBusinessDomainCreate==true && businessDomain != null" @manageBusinessDomainCreateBack="manageBusinessDomainCreateBack" @updateBusinessDomainToList="updateBusinessDomainToList" @hideBusinessDomainCreatePage="hideBusinessDomainCreatePage"  :showBusinessDomainCreate="showBusinessDomainCreate"  :businessDomain="businessDomain"/>

<!-- 新建管理对象 -->
<createManagedObject v-if="showManagedObjectCreate==true && newManagedObject != null" :businessDomains="businessSystemInfo.businessDomains" @managedObjectCreateBack="managedObjectCreateBack" @addNewManagedObjectToList="addNewManagedObjectToList" @hideManagedObjectCreatePage="hideManagedObjectCreatePage"  :showManagedObjectCreate="showManagedObjectCreate"  :newManagedObject="newManagedObject"/>

<!-- 新建数据库对象 -->
<createDbSchema v-if="showDbSchemaCreate==true && newDbSchema != null" @dbSchemaCreateBack="dbSchemaCreateBack" @addNewDbSchemaToList="addNewDbSchemaToList" @hideDbSchemaCreatePage="hideDbSchemaCreatePage"  :showDbSchemaCreate="showDbSchemaCreate"  :newDbSchema="newDbSchema"/>

<!-- 新建数据库表对象 -->
<createDbTable v-if="showDbTableCreate==true && newDbTable != null" @dbTableCreateBack="dbTableCreateBack" @addNewDbTableToList="addNewDbTableToList" @hideDbTableCreatePage="hideDbTableCreatePage"  :showDbTableCreate="showDbTableCreate" :dbSchemas="businessSystemInfo.dbSchemas" :newDbTable="newDbTable"/>

<!-- 新建技术服务资源对象 -->
<createServerResource v-if="showServerResourceCreate==true && newServerResource != null" @serverResourceCreateBack="serverResourceCreateBack" @addNewServerResourceToList="addNewServerResourceToList" @hideServerResourceCreatePage="hideServerResourceCreatePage"  :showServerResourceCreate="showServerResourceCreate"  :newServerResource="newServerResource"/>

<!-- 新建页面对象资源 -->
<createFuncDefine v-if="showFuncDefineCreate==true && newFuncDefine != null" @funcDefineCreateBack="funcDefineCreateBack" @addNewFuncDefineToList="addNewFuncDefineToList" @hideFuncDefineCreatePage="hideFuncDefineCreatePage"  :showFuncDefineCreate="showFuncDefineCreate"  :newFuncDefine="newFuncDefine" :businessDomains="businessSystemInfo.businessDomains" :dbSchemas="businessSystemInfo.dbSchemas"/>

<!-- 新建组合页面对象资源 -->
<createComboFuncDefine v-if="showComboFuncDefineCreate==true && newComboFuncDefine != null" @comboFuncDefineCreateBack="comboFuncDefineCreateBack" @addNewComboFuncDefineToList="addNewComboFuncDefineToList" @hideComboFuncDefineCreatePage="hideComboFuncDefineCreatePage"  :showComboFuncDefineCreate="showComboFuncDefineCreate"  :newComboFuncDefine="newComboFuncDefine" :businessDomains="businessSystemInfo.businessDomains"/>

<!-- 新建工作流流程资源 -->
<createWorkflowProcessResource v-if="showWorkflowProcessResourceCreate==true && newWorkflowProcessResource != null" @workflowProcessResourceCreateBack="workflowProcessResourceCreateBack" @addNewWorkflowProcessResourceToList="addNewWorkflowProcessResourceToList" @hideWorkflowProcessResourceCreatePage="hideWorkflowProcessResourceCreatePage"  :showWorkflowProcessResourceCreate="showWorkflowProcessResourceCreate"  :newWorkflowProcessResource="newWorkflowProcessResource"/>

<!-- 修改密码 -->
<passwordChange v-if="showPasswordChangePage==true && passwordChangeVo != null" @hidePasswordChangePage="hidePasswordChangePage" :passwordChange="passwordChangeVo" :showPasswordChangePage="showPasswordChangePage"/>

<!-- 查看暴露服务列表信息 -->
<exposedServiceView v-if="showExposedServiceView" @hideExposedServiceViewPage="hideExposedServiceViewPage" :showExposedServiceView="showExposedServiceView"  :exposedServicesForView="exposedServicesForView"/>

<!-- 领域对象方法一键暴露 -->
<domainOperationExposeCom v-if="showDomainOperationExpose==true && domainOperationExpose != null" @domainOperationExposeBack="domainOperationExposeBack" @finishDomainOperationExpose="finishDomainOperationExpose" @hideDomainOperationExposePage="hideDomainOperationExposePage"  :showDomainOperationExpose="showDomainOperationExpose"  :domainOperationExpose="domainOperationExpose"/>

<!-- Kafka消息消费服务新建 -->
<kafkaMessageConsumerCreate v-if="showKafkaMessageConsumerCreate==true && newKafkaMessageConsumer != null" @kafkaMessageConsumerCreateBack="kafkaMessageConsumerCreateBack" @addKafkaMessageConsumerToList="addKafkaMessageConsumerToList" @hideKafkaMessageConsumerCreatePage="hideKafkaMessageConsumerCreatePage"  :showKafkaMessageConsumerCreate="showKafkaMessageConsumerCreate"  :kafkaMessageConsumer="newKafkaMessageConsumer"/>

<!-- 动态映射新建 -->
<dynamicMappingCreate v-if="showDynamicMappingCreate==true && newDynamicMapping != null" @dynamicMappingCreateBack="dynamicMappingCreateBack" @addNewDynamicMappingToList="addNewDynamicMappingToList" @hideDynamicMappingCreatePage="hideDynamicMappingCreatePage"  :showDynamicMappingCreate="showDynamicMappingCreate"  :newDynamicMapping="newDynamicMapping"/>

<!-- 新建字典资源对象 -->
<createDictResource v-if="showDictResourceCreate==true && newDictResource != null" @dictResourceCreateBack="dictResourceCreateBack" @addNewDictResourceToList="addNewDictResourceToList" @hideDictResourceCreatePage="hideDictResourceCreatePage"  :showDictResourceCreate="showDictResourceCreate"  :newDictResource="newDictResource" :dbSchemas="businessSystemInfo.dbSchemas" />

<!-- 新建安全密钥资源对象 -->
<createSecurityKeyResource v-if="showSecurityKeyResourceCreate==true && newSecurityKeyResource != null" @securityKeyResourceCreateBack="securityKeyResourceCreateBack" @addNewSecurityKeyResourceToList="addNewSecurityKeyResourceToList" @hideSecurityKeyResourceCreatePage="hideSecurityKeyResourceCreatePage"  :showSecurityKeyResourceCreate="showSecurityKeyResourceCreate"  :newSecurityKeyResource="newSecurityKeyResource" />

<!-- 新建数据模型对象 -->
<createDataModel v-if="showDataModelCreate==true && newDataModel != null" @dataModelCreateBack="dataModelCreateBack" @addNewDataModelToList="addNewDataModelToList" @hideDataModelCreatePage="hideDataModelCreatePage"  :showDataModelCreate="showDataModelCreate"  :newDataModel="newDataModel" :businessDomains="businessSystemInfo.businessDomains"/>

<!-- 新建数据集对象 -->
<createDataSet v-if="showDataSetCreate==true && newDataSet != null" @dataSetCreateBack="dataSetCreateBack" @addNewDataSetToList="addNewDataSetToList" @hideDataSetCreatePage="hideDataSetCreatePage"  :showDataSetCreate="showDataSetCreate"  :newDataSet="newDataSet" :businessDomains="businessSystemInfo.businessDomains"/>

<!-- 定时任务新建 -->
<cronJobCreate v-if="showCronJobCreate==true && newCronJob != null" @cronJobCreateBack="cronJobCreateBack" @addCronJobToList="addCronJobToList" @hideCronJobCreatePage="hideCronJobCreatePage"  :showCronJobCreate="showCronJobCreate"  :cronJob="newCronJob"/>

</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref, defineAsyncComponent } from 'vue';
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'

import workbenchNavigate from './workbench-navigate.vue'

//import createDbSchema from './dbMgr/workbench-dbSchemaCreate.vue'
const createDbSchema = defineAsyncComponent(() => import('./dbMgr/workbench-dbSchemaCreate.vue'));
//import createDbTable from './dbMgr/workbench-dbTableCreate.vue'
const createDbTable = defineAsyncComponent(() => import('./dbMgr/workbench-dbTableCreate.vue'));
//import editBusinessSystem from './domainModel/workbench-businessSystemEdit.vue'
const editBusinessSystem = defineAsyncComponent(() => import('./domainModel/workbench-businessSystemEdit.vue'));
const mgrBusinessDomain = defineAsyncComponent(() => import('./domainModel/workbench-businessDomainMgr.vue'));
//import createBusinessDomain from './domainModel/workbench-businessDomainCreate.vue'
const createBusinessDomain = defineAsyncComponent(() => import('./domainModel/workbench-businessDomainCreate.vue'));
//import createManagedObject from './domainModel/workbench-managedObjectCreate.vue'
const createManagedObject = defineAsyncComponent(() => import('./domainModel/workbench-managedObjectCreate.vue'));
// import createFuncDefine from './funcMgr/workbench-funcDefineCreate.vue'
const createFuncDefine = defineAsyncComponent(() => import('./funcMgr/workbench-funcDefineCreate.vue'));
// import createComboFuncDefine from './funcMgr/workbench-comboFuncDefineCreate.vue'
const createComboFuncDefine = defineAsyncComponent(() => import('./funcMgr/workbench-comboFuncDefineCreate.vue'));
// import editBusinessSystemAuth from './cfgMgr/workbench-businessSystemAuthEdit.vue'
const editBusinessSystemAuth = defineAsyncComponent(() => import('./cfgMgr/workbench-businessSystemAuthEdit.vue'));
// import editBusinessSystemAutoLoad from './cfgMgr/workbench-businessSystemAutoLoadEdit.vue'
const editBusinessSystemAutoLoad = defineAsyncComponent(() => import('./cfgMgr/workbench-businessSystemAutoLoadEdit.vue'));
// import modelDrivenGenerate from './assist/workbench-modelDrivenGenerate.vue'
const modelDrivenGenerate = defineAsyncComponent(() => import('./assist/workbench-modelDrivenGenerate.vue'));
// import domainOperationExposeCom from './assist/workbench-domainOperationExpose.vue';
const domainOperationExposeCom = defineAsyncComponent(() => import('./assist/workbench-domainOperationExpose.vue'));
// import createServerResource from './integrate/workbench-serverResourceCreate.vue';
const createServerResource = defineAsyncComponent(() => import('./integrate/workbench-serverResourceCreate.vue'));
// import createDictResource from './integrate/workbench-dictResourceCreate.vue';
const createDictResource = defineAsyncComponent(() => import('./integrate/workbench-dictResourceCreate.vue'));
const createSecurityKeyResource = defineAsyncComponent(() => import('./integrate/workbench-securityKeyResourceCreate.vue'));
// import createWorkflowProcessResource from './integrate/workbench-workflowProcessResourceCreate.vue';
const createWorkflowProcessResource = defineAsyncComponent(() => import('./integrate/workbench-workflowProcessResourceCreate.vue'));
// import kafkaMessageConsumerCreate from './integrate/workbench-kafkaMessageConsumerCreate.vue';
const kafkaMessageConsumerCreate = defineAsyncComponent(() => import('./integrate/workbench-kafkaMessageConsumerCreate.vue'));
// import dynamicMappingCreate from './dynamicMapping/workbench-dynamicMappingCreate.vue';
const dynamicMappingCreate = defineAsyncComponent(() => import('./dynamicMapping/workbench-dynamicMappingCreate.vue'));
// import exposedServiceView from './exposeMgr/workbench-exposedServicesView.vue';
const exposedServiceView = defineAsyncComponent(() => import('./exposeMgr/workbench-exposedServicesView.vue'));
// import passwordChange from '/src/systemFramework/components/passwordChange.vue';
const passwordChange = defineAsyncComponent(() => import('/src/systemFramework/components/passwordChange.vue'));

const createDataModel = defineAsyncComponent(() => import('./dataSetMgr/workbench-dataModelCreate.vue'));

const createDataSet = defineAsyncComponent(() => import('./dataSetMgr/workbench-dataSetCreate.vue'));

const cronJobCreate = defineAsyncComponent(() => import('./integrate/workbench-cronJobCreate.vue'));

var url = document.location;
var searchString = url.search.substring(1);
var params = searchString.split("&");
var businessSystemId = "";
params.forEach(item=>{
    var kv = item.split("=");
    if("businessSystemId" == kv[0]) {
        businessSystemId = kv[1];
        return;
    }
})

const businessSystemInfo = ref({businessSystem:{description:""},businessDomains:[]});
const tableData = ref([]);

const showManagedObjectCreate1 = ref(false);

const currentObjectType = ref([]);


export default {
    components: {
    	workbenchNavigate,
    	
    	editBusinessSystem,
    	editBusinessSystemAutoLoad,
        createBusinessDomain,
        mgrBusinessDomain,
        createManagedObject,
        modelDrivenGenerate,
        createDbSchema,
        createDbTable,
        createServerResource,
        createFuncDefine,
        createComboFuncDefine,
        createWorkflowProcessResource,
        
        editBusinessSystemAuth,
        
        passwordChange,
        exposedServiceView,
        domainOperationExposeCom,
        kafkaMessageConsumerCreate,
        dynamicMappingCreate,
        createDictResource,
        createSecurityKeyResource,
        createDataModel,
        createDataSet,
        cronJobCreate
    },
    
    data() {  
        const router = useRouter();

    	const managedObjectTypes = ref(null);
    	
    	const defaultProps = {
    		    children: 'children',
    		    label: 'label'
    		};
    	
    	
    	const showBusinessSystemEdit = ref(false);
    	const businessSystem = ref({});
    	
    	const showBusinessSystemAuthEdit = ref(false);
        const businessSystemAuth = ref({});
        
        const showBusinessSystemAutoLoadEdit = ref(false);
        const businessSystemAutoLoad = ref({});
    	
    	const showBusinessDomainCreate = ref(false);
    	const businessDomain = ref({});

        const showBusinessDomainMgr = ref(false);
    	
    	const showManagedObjectCreate = ref(false);
    	const newManagedObject = ref({});
    	
    	const showModelDrivenGenerate = ref(false);
    	const modelDrivenRequest = ref({});
    	
    	const showDbSchemaCreate = ref(false);
        const newDbSchema = ref({});
        
        const showServerResourceCreate = ref(false);
        const newServerResource = ref({});
        
        const showDbTableCreate = ref(false);
        const newDbTable = ref({});
        
        const showFuncDefineCreate = ref(false);
        const newFuncDefine = ref({});
        
        const showComboFuncDefineCreate = ref(false);
        const newComboFuncDefine = ref({});

        const showDataModelCreate = ref(false);
        const newDataModel = ref({});

        const showDataSetCreate = ref(false);
        const newDataSet = ref({});
        
        const showWorkflowProcessResourceCreate = ref(false);
        const newWorkflowProcessResource = ref({});
        
        const loginInfo = ref({"loginUser":{},"menuRights":[]});
        const showPasswordChangePage = ref(false);
        const passwordChangeVo = ref(null);
        
        const showExposedServiceView = ref(false);
        const exposedServicesForView = ref(null);
        
        const showDomainOperationExpose= ref(false);
        const domainOperationExpose = ref(null);
        
        const showKafkaMessageConsumerCreate= ref(false);
        const newKafkaMessageConsumer = ref({});

        const showDynamicMappingCreate = ref(false);
        const newDynamicMapping = ref({});

        const showDictResourceCreate = ref(false);
        const newDictResource = ref({});

        const showSecurityKeyResourceCreate = ref(false);
        const newSecurityKeyResource = ref({});

        const showCronJobCreate = ref(false);
        const newCronJob = ref({});
        
        const asideWidth = ref("251px");

        const editObjectKey = ref(null);
        const editObject = ref(null);
        const showObjectEdit = ref(false);

        return {
            router,
        	businessSystem,
            showBusinessSystemEdit,
            businessSystemAuth,
            showBusinessSystemAuthEdit,
            businessSystemAutoLoad,
            showBusinessSystemAutoLoadEdit,
            showBusinessDomainCreate,
            businessDomain,
            showBusinessDomainMgr,
            showManagedObjectCreate,
            newManagedObject,
            showDbSchemaCreate,
            newDbSchema,
            showServerResourceCreate,
            newServerResource,
            showDbTableCreate,
            newDbTable,
            showFuncDefineCreate,
            newFuncDefine,
            showComboFuncDefineCreate,
            newComboFuncDefine,
            showDataModelCreate,
            newDataModel,
            showDataSetCreate,
            newDataSet,
            showWorkflowProcessResourceCreate,
            newWorkflowProcessResource,
            showDynamicMappingCreate,
            newDynamicMapping,
            showDictResourceCreate,
            newDictResource,
            showSecurityKeyResourceCreate,
            newSecurityKeyResource,
            showCronJobCreate,
            newCronJob,
            
            businessSystemInfo,
        	tableData,
        	showManagedObjectCreate1,
        	
        	
        	defaultProps,
        	currentObjectType,
        	managedObjectTypes,
        	
        	showModelDrivenGenerate,
        	modelDrivenRequest,
        	
        	loginInfo,
            passwordChangeVo,
            showPasswordChangePage,
            
            showExposedServiceView,
            exposedServicesForView,
            
            showDomainOperationExpose,
            domainOperationExpose,
            
            showKafkaMessageConsumerCreate,
            newKafkaMessageConsumer,
            
            asideWidth,

            editObjectKey,
            editObject,
            showObjectEdit
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getManagedObjecTypes").then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null && data.data.length != null) {
                this.managedObjectTypes = data.data;
                this.initWorkbench();
                this.showObjectEdit = false;
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });       
    },
    methods: {
    	collapseAside(flag) {
    		if(flag) {
    			this.asideWidth = "79px";
    		}else {
    			this.asideWidth = "235px";
    		}
    	},
    	selectTreeNode(data, checked, tree) {
    	    if (checked) {
    	        this.currentNodeData = data;
    	        this.$refs.managedObjectTypeSelect.setCheckedNodes([data]);
    	    }
    	},
    	reloadBusinessSystem() {
    		axiosClient.get("/lcdp-proxy/lowcode/platform/be/api/businessSystem/loadInfo/" + businessSystemId).then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null && data.data.businessSystem != null) {
                    businessSystemInfo.value = data.data;
                    //console.log(businessSystemInfo.value);
                    this.reloadFeInfo();
                    this.reloadBiInfo();
                }
            }).catch(ex=> {
                ElMessage.error(`加载be数据失败！` + ex);
            });
    	},
        reloadFeInfo() {
            axiosClient.get("/lcdp-proxy/lowcode/platform/fe/api/businessSystem/loadFeInfo/" + businessSystemId).then((response) => {
                var data1 = response.data; 
                if(data1 != null && data1.status == "200" && data1.data != null) {
                    var data = data1.data;
                    if(data != null && data.funcDefines != null && data.funcDefines.length > 0 && businessSystemInfo.value.businessDomains != null && businessSystemInfo.value.businessDomains.length > 0) {
                        var domain2funcDefineMap = {};
                        for(var i in data.funcDefines) {
                            var funcDefine = data.funcDefines[i];
                            if(funcDefine.domainId != null && funcDefine.domainId.trim().length > 0) {
                                if(domain2funcDefineMap[funcDefine.domainId] == null) {
                                    domain2funcDefineMap[funcDefine.domainId] = [];
                                }
                                domain2funcDefineMap[funcDefine.domainId].push(funcDefine);
                            }
                        }
                        for(var j in businessSystemInfo.value.businessDomains) {
                            var businessDomain = businessSystemInfo.value.businessDomains[j];
                            if(domain2funcDefineMap[businessDomain.businessDomain.id] != null) {
                                businessDomain.funcDefines = domain2funcDefineMap[businessDomain.businessDomain.id];
                            }
                        }
                    }

                    if(data != null && data.subFuncDefines != null && data.subFuncDefines.length > 0 && businessSystemInfo.value.businessDomains != null && businessSystemInfo.value.businessDomains.length > 0) {
                        var domain2funcDefineMap = {};
                        for(var i in data.subFuncDefines) {
                            var funcDefine = data.subFuncDefines[i];
                            if(funcDefine.domainId != null && funcDefine.domainId.trim().length > 0) {
                                if(domain2funcDefineMap[funcDefine.domainId] == null) {
                                    domain2funcDefineMap[funcDefine.domainId] = [];
                                }
                                domain2funcDefineMap[funcDefine.domainId].push(funcDefine);
                            }
                        }
                        for(var j in businessSystemInfo.value.businessDomains) {
                            var businessDomain = businessSystemInfo.value.businessDomains[j];
                            if(domain2funcDefineMap[businessDomain.businessDomain.id] != null) {
                                businessDomain.subFuncDefines = domain2funcDefineMap[businessDomain.businessDomain.id];
                            }
                        }
                    }

                    if(data != null && data.comboFuncDefines != null && data.comboFuncDefines.length > 0 && businessSystemInfo.value.businessDomains != null && businessSystemInfo.value.businessDomains.length > 0) {
                        var domain2funcDefineMap = {};
                        for(var i in data.comboFuncDefines) {
                            var funcDefine = data.comboFuncDefines[i];
                            if(funcDefine.domainId != null && funcDefine.domainId.trim().length > 0) {
                                if(domain2funcDefineMap[funcDefine.domainId] == null) {
                                    domain2funcDefineMap[funcDefine.domainId] = [];
                                }
                                domain2funcDefineMap[funcDefine.domainId].push(funcDefine);
                            }
                        }
                        for(var j in businessSystemInfo.value.businessDomains) {
                            var businessDomain = businessSystemInfo.value.businessDomains[j];
                            if(domain2funcDefineMap[businessDomain.businessDomain.id] != null) {
                                businessDomain.comboFuncDefines = domain2funcDefineMap[businessDomain.businessDomain.id];
                            }
                        }
                    }
                }
            }).catch(ex=> {
                ElMessage.error(`加载fe数据失败！` + ex);
            });
        },
        reloadBiInfo() {
            axiosClient.get("/lcdp-proxy/lowcode/platform/bi/api/businessSystem/loadBiInfo/" + businessSystemId).then((response) => {
                var data = response.data; 
                var data1 = response.data; 
                if(data1 != null && data1.status == "200" && data1.data != null) {
                    var data = data1.data;
                    if(data != null && data.dataModels != null && data.dataModels.length > 0 && businessSystemInfo.value.businessDomains != null && businessSystemInfo.value.businessDomains.length > 0) {
                        var domain2dataModelMap = {};
                        for(var i in data.dataModels) {
                            var dataModel = data.dataModels[i];
                            if(dataModel.domainId != null && dataModel.domainId.trim().length > 0) {
                                if(domain2dataModelMap[dataModel.domainId] == null) {
                                    domain2dataModelMap[dataModel.domainId] = [];
                                }
                                domain2dataModelMap[dataModel.domainId].push(dataModel);
                            }
                        }
                        for(var j in businessSystemInfo.value.businessDomains) {
                            var businessDomain = businessSystemInfo.value.businessDomains[j];
                            if(domain2dataModelMap[businessDomain.businessDomain.id] != null) {
                                businessDomain.dataModels = domain2dataModelMap[businessDomain.businessDomain.id];
                            }
                        }
                    }

                    if(data != null && data.dataSets != null && data.dataSets.length > 0 && businessSystemInfo.value.businessDomains != null && businessSystemInfo.value.businessDomains.length > 0) {
                        var domain2dataSetMap = {};
                        for(var i in data.dataSets) {
                            var dataSet = data.dataSets[i];
                            if(dataSet.domainId != null && dataSet.domainId.trim().length > 0) {
                                if(domain2dataSetMap[dataSet.domainId] == null) {
                                    domain2dataSetMap[dataSet.domainId] = [];
                                }
                                domain2dataSetMap[dataSet.domainId].push(dataSet);
                            }
                        }
                        for(var j in businessSystemInfo.value.businessDomains) {
                            var businessDomain = businessSystemInfo.value.businessDomains[j];
                            if(domain2dataSetMap[businessDomain.businessDomain.id] != null) {
                                businessDomain.dataSets = domain2dataSetMap[businessDomain.businessDomain.id];
                            }
                        }
                    }
                }
            }).catch(ex=> {
                ElMessage.error(`加载bi数据失败！` + ex);
            });
        },
    	initWorkbench() {
            axiosClient.post("/lcdp-proxy/lowcode/service/sm/loadUserInfo", {}).then((response) => {
                var data = response.data; 
                if(data != null && data.data != null && data.data.loginUser != null) {
                    this.loginInfo = data.data;
                    this.reloadBusinessSystem();
                }else {
                    window.location = "/login.html";
                }
            }).catch((e) => {
                console.log(e);
                window.location = "/login.html";
            });
        },
        exit() {
            axiosClient.post("/lcdp-proxy/lowcode/service/sm/logout", {}).then((response) => {
                window.location = "/login.html";
            }).catch((e) => {
                console.log(e);
                window.location = "/login.html";
            });
        },
        hidePasswordChangePage() {
            this.passwordChangeVo = null;
            this.showPasswordChangePage = false;
            this.rawPassword = null;
        },
        changePassword() {
            this.passwordChangeVo = {};
            this.passwordChangeVo.userId = this.loginInfo.loginUser.id;
            this.passwordChangeVo.userName = this.loginInfo.loginUser.userName;
            this.showPasswordChangePage = true;
        },
    	handleClick(type, subType, objectId){
            this.showObjectEdit = false;
            currentObjectType.value = [type + ":" + subType];

            if(type == "O" && subType == "D") {//数据库对象
                this.editObjectKey = "dbSchema";
                this.router.replace({name: 'editDbSchema', query: {"objectId": objectId}});
            }else if(type == "O" && subType == "T") {//数据库表对象
                this.editObjectKey = "dbTable";
                this.router.replace({name: 'editDbTable', query: {"objectId": objectId}});
            } else if(type == "O" && subType == "R") {//技术服务资源对象
                this.editObjectKey = "serverResource";
                this.router.replace({name: 'editServerResource', query: {"objectId": objectId}});
            }  else if(type == "O" && subType == "Y") {//动态映射
                this.editObjectKey = "dynamicMapping";
                this.router.replace({name: 'editDynamicMapping', query: {"objectId": objectId}});
            } else if(type == "F" && subType == "F") {//页面对象
                this.editObjectKey = "funcDefine";
                this.router.replace({name: 'editFuncDefine', query: {"objectId": objectId}});
            } else if(type == "F" && subType == "S") {//页面对象
                this.editObjectKey = "funcDefine";
                this.router.replace({name: 'editFuncDefine', query: {"objectId": objectId}});
            }else if(type == "W" && subType == "P") {//流程资源
                this.editObjectKey = "workflowProcessResource";
                this.router.replace({name: 'editWorkflowProcessResource', query: {"objectId": objectId}});
            }else if(type == "F" && subType == "C") {//组合页面对象
                this.editObjectKey = "comboFuncDefine";
                this.router.replace({name: 'editComboFuncDefine', query: {"objectId": objectId}});
            }else if(type == "B" && subType == "M") {//数据模型对象
                this.editObjectKey = "dataModel";
                this.router.replace({name: 'editDataModel', query: {"objectId": objectId}});
            } else if(type == "B" && subType == "S") {//数据集对象
                this.editObjectKey = "dataSet";
                this.router.replace({name: 'editDataSet', query: {"objectId": objectId}});
            } else if(type == "M" && subType == "K") {//Kafka消息消费服务
                this.editObjectKey = "kafkaMessageConsumer";
                this.router.replace({name: 'editKafkaMessageConsumer', query: {"objectId": objectId, "systemId":businessSystemInfo.value.businessSystem.id}});
            } else if(type == "O" && subType == "J") {//定时任务
                this.editObjectKey = "cronJob";
                this.router.replace({name: 'editCronJob', query: {"objectId": objectId, "systemId":businessSystemInfo.value.businessSystem.id}});
            }  else if(type == "O" && subType == "P") {//字典资源对象
                this.editObjectKey = "dictResource";
                this.router.replace({name: 'editDictResource', query: {"objectId": objectId}});
            }  else if(type == "O" && subType == "S") {//安全密钥资源对象
                this.editObjectKey = "securityKeyResource";
                this.router.replace({name: 'editSecurityKeyResource', query: {"objectId": objectId}});
            }  else {//业务对象
                var subTypeName = null;
                this.managedObjectTypes.forEach((objectType)=>{
                    if(type == objectType.value) {
                        objectType.children.forEach((subObjectType)=>{
                            if(subObjectType.value == ("D:"+subType)) {
                                subTypeName = subObjectType.label;
                                return;
                            }
                        });
                        return;
                    }
                });
                
                this.editObjectKey = "managedObject";
                this.router.replace({
                    name: 'editManagedObject', 
                    query: {
                        "objectType": subType, 
                        "objectId": objectId, 
                        "objectTypeName": subTypeName
                    }
                });

            	
            }
            this.showObjectEdit = true;	
        },
        
        manageObjectCreateStep1() {
        	showManagedObjectCreate1.value=true;
            if(this.$refs.managedObjectTypeSelect != null) {
            	this.$refs.managedObjectTypeSelect.setCheckedKeys([currentObjectType.value]);
            }
        },
        manageObjectCreateNext() {
        	var types = this.$refs.managedObjectTypeSelect.getCheckedKeys(true);
        	if(types == null || types.length == 0) {
        		ElMessage.error(`必须选择一个要创建的对象类型（叶子节点）`);
        		return;
        	}
        	if(types.length > 1) {
        		ElMessage.error(`只能选择一个要创建的对象类型（叶子节点）`);
                return;
        	}
        	
        	var businessDomainId = "";
            if(this.editObject != null && this.editObject.managedObject != null) {
                businessDomainId = this.editObject.managedObject.domainId;
            }
        	
        	var a = types[0].split(":");
        	if(a[0] == "O") {
        		if(a[1] == "B") {//创建业务域
        			this.businessDomain = {
        				id : "",
        				name : "",
        				description : "",
        				status : "",
        				createTime : "",
        				updateTime : "",
        				systemId: businessSystemInfo.value.businessSystem.id,
                        tenantId: businessSystemInfo.value.businessSystem.tenantId
        			}
        			showManagedObjectCreate1.value = false;
                    this.showBusinessDomainCreate = true;
        		}else if(a[1] == "M") {//根据数据库表生成
        			var dbSchemaId = "";
        			if(businessSystemInfo.value.dbSchemas != null && businessSystemInfo.value.dbSchemas.length > 0) {
        				dbSchemaId = businessSystemInfo.value.dbSchemas[0].id;
        			}
        			if(businessDomainId == "" && businessSystemInfo.value.businessDomains != null && businessSystemInfo.value.businessDomains.length > 0) {
        				businessDomainId = businessSystemInfo.value.businessDomains[0].businessDomain.id;
        			}
        			
                    this.modelDrivenRequest = {
                            generateDataMapping : true,
                            generateValueObject : true,
                            tableArray : [],
                            ignorePrefixes : [],
                            ignorePrefixString : "",
                            dbSchemaId : dbSchemaId,
                            systemId: businessSystemInfo.value.businessSystem.id,
                            businessDomainId: businessDomainId
                    }
                    showManagedObjectCreate1.value = false;
                    this.showModelDrivenGenerate = true;
                }else if(a[1] == "D") {//新建数据库对象
                	 this.newDbSchema = {
                        id : "",
                        name : "",
                        description : "",
                        dbType : "M",
                        username : "",
                        password : "",
                        url : "",
                        driverClass : "",
                        dataSourceCfg : "",
                        status : "",
                        createTime : "",
                        updateTime : "",
                        systemId: businessSystemInfo.value.businessSystem.id,
                        tenantId: businessSystemInfo.value.businessSystem.tenantId
                    }
                	showManagedObjectCreate1.value = false;
                	this.showDbSchemaCreate = true;
                }else if(a[1] == "Y") {//新建动态映射
                	 this.newDynamicMapping = {
                        id : "",
                        name : "",
                        description : "",
                        mappingType : "",
                        businessType : "",
                        subBusinessType : "",
                        keyAttribute : "",
                        status : "",
                        createTime : "",
                        updateTime : "",
                        systemId: businessSystemInfo.value.businessSystem.id,
                        tenantId: businessSystemInfo.value.businessSystem.tenantId
                    }
                	showManagedObjectCreate1.value = false;
                	this.showDynamicMappingCreate = true;
                }else if(a[1] == "T") {//新建数据库表对象
                	if(businessSystemInfo.value.dbSchemas == null || businessSystemInfo.value.dbSchemas.length == 0) {
                		ElMessage.error(`请先创建一个数据库`);
                        return;
                    }
                    this.newDbTable = {
                        id : "",
                        name : "",
                        description : "",
                        schemaId : "",
                        status : "",
                        createTime : "",
                        updateTime : "",
                        systemId: businessSystemInfo.value.businessSystem.id,
                        tenantId: businessSystemInfo.value.businessSystem.tenantId
                    }
                    showManagedObjectCreate1.value = false;
                    this.showDbTableCreate = true;
                }else if(a[1] == "E") {//领域对象方法一键暴露
                    this.domainOperationExpose = {
                        domainObjectId : "",
                        operationId : "",
                        serviceObjectId : "",
                        httpMapping : "",
                        httpMethod : "POST",
                        systemId: businessSystemInfo.value.businessSystem.id,
                        tenantId: businessSystemInfo.value.businessSystem.tenantId
                    }
                    showManagedObjectCreate1.value = false;
                    this.showDomainOperationExpose = true;
                }else if(a[1] == "R") {//新建技术服务资源
                    this.newServerResource = {
                            id : "",
                            name : "",
                            description : "",
                            resourceType : "E",
                            username : "",
                            password : "",
                            url : "",
                            host : "",
                            port : "",
                            serverCfg : "",
                            status : "",
                            createTime : "",
                            updateTime : "",
                            systemId: businessSystemInfo.value.businessSystem.id,
                            tenantId: businessSystemInfo.value.businessSystem.tenantId
                        }
                        showManagedObjectCreate1.value = false;
                        this.showServerResourceCreate = true;
                }else if(a[1] == "P") {//新建字典资源
                    this.newDictResource = {
                            id : "",
                            name : "",
                            type : "S",
                            dbSchemaId : "",
                            dictTable : "",
                            subDictTable : "",
                            
                            status : "",
                            createTime : "",
                            updateTime : "",
                            systemId: businessSystemInfo.value.businessSystem.id,
                            tenantId: businessSystemInfo.value.businessSystem.tenantId
                        }
                        showManagedObjectCreate1.value = false;
                        this.showDictResourceCreate = true;
                }else if(a[1] == "S") {//新建安全密钥资源
                    this.newSecurityKeyResource = {
                            id : "",
                            name : "",
                            type : "K",
                            securityKey : "",
                            cipherAlgorithm : "",
                            privateKeyFile : "",
                            publicKeyFile : "",
                            privateKeyPwd : "",
                            
                            createTime : "",
                            updateTime : "",
                            systemId: businessSystemInfo.value.businessSystem.id,
                            tenantId: businessSystemInfo.value.businessSystem.tenantId
                        }
                        showManagedObjectCreate1.value = false;
                        this.showSecurityKeyResourceCreate = true;
                }else if(a[1] == "J") {//定时任务
                    this.newCronJob = {
                            id : "",
                            name : "",
                            code : "",
                            jobType : "Q",
                            jobSchedule : "",
                            jobHandlerService : "",
                            jobHandlerMethod : "",
                            description : "",
                            createTime : "",
                            updateTime : "",
                            systemId: businessSystemInfo.value.businessSystem.id,
                            tenantId: businessSystemInfo.value.businessSystem.tenantId
                        }
                        showManagedObjectCreate1.value = false;
                        this.showCronJobCreate = true;
                }
            }else if(a[0] == "W") {
                if(a[1] == "P") {//新建流程资源
                    this.newWorkflowProcessResource = {
                            id : "",
                            name : "",
                            description : "",
                            processDefId : "",
                            status : "",
                            createTime : "",
                            updateTime : "",
                            systemId: businessSystemInfo.value.businessSystem.id,
                            tenantId: businessSystemInfo.value.businessSystem.tenantId,
                            bpmnXml: ""
                    }
                    showManagedObjectCreate1.value = false;
                    this.showWorkflowProcessResourceCreate = true;
                }
            }else if(a[0] == "M") {
                if(a[1] == "K") {//新建Kafka消息消费服务
                    this.newKafkaMessageConsumer = {
                            id : "",
                            name : "",
                            description : "",
                            consumerResourceId : "",
                            messageTopics : "",
                            serviceObjectId : "",
                            operationId : "",
                            status : "",
                            createTime : "",
                            updateTime : "",
                            systemId: businessSystemInfo.value.businessSystem.id,
                            tenantId: businessSystemInfo.value.businessSystem.tenantId,
                    }
                    showManagedObjectCreate1.value = false;
                    this.showKafkaMessageConsumerCreate = true;
                }
            }else if(a[0] == "F") {
            	if(a[1] == "F") {//新建页面对象资源
                    this.newFuncDefine = {
                            id : "",
                            name : "",
                            description : "",
                            templateId : "",
                            status : "",
                            createTime : "",
                            updateTime : "",
                            systemId: businessSystemInfo.value.businessSystem.id,
                            tenantId: businessSystemInfo.value.businessSystem.tenantId,
                            funcRegion: [],
                            objectAssigns:[]
                    }
                    showManagedObjectCreate1.value = false;
                    this.showFuncDefineCreate = true;
                }else if(a[1] == "C") {//新建组合页面对象资源
                    this.newComboFuncDefine = {
                            id : "",
                            name : "",
                            description : "",
                            comboTemplateId : "",
                            status : "",
                            createTime : "",
                            updateTime : "",
                            systemId: businessSystemInfo.value.businessSystem.id,
                            tenantId: businessSystemInfo.value.businessSystem.tenantId,
                            comboFuncTabs: [],
                    }
                    showManagedObjectCreate1.value = false;
                    this.showComboFuncDefineCreate = true;
                }
            }else if(a[0] == "B") {
            	if(a[1] == "M") {//新建数据模型对象资源
                    this.newDataModel = {
                            id : "",
                            code : "",
                            name : "",
                            description : "",
                            dataSourceType : "",
                            dataSourceId : "",
                            physicalSrcObject : "",
                            srcObjectDesc : "",
                            status : "",
                            createTime : "",
                            updateTime : "",
                            systemId: businessSystemInfo.value.businessSystem.id,
                            tenantId: businessSystemInfo.value.businessSystem.tenantId,
                    }
                    showManagedObjectCreate1.value = false;
                    this.showDataModelCreate = true;
                }else if(a[1] == "S") {//新建数据集对象资源
                    this.newDataSet = {
                            id : "",
                            code : "",
                            name : "",
                            description : "",
                            status : "",
                            createTime : "",
                            updateTime : "",
                            systemId: businessSystemInfo.value.businessSystem.id,
                            tenantId: businessSystemInfo.value.businessSystem.tenantId,
                    }
                    showManagedObjectCreate1.value = false;
                    this.showDataSetCreate = true;
                }
            }else {//创建管理对象     		
                this.newManagedObject = {
                        id:"",
                        code:"",
                        name:"",
                        objectType:a[1],
                        businessDomainId: businessDomainId,
                        description: "",
                        systemId: businessSystemInfo.value.businessSystem.id,
                        tenantId: businessSystemInfo.value.businessSystem.tenantId
                    };
                showManagedObjectCreate1.value = false;
                this.showManagedObjectCreate = true;
        	}
        },

        updateToList(type, data) {
            if(type == "managedObject") {
                this.updateManagedObjectToList(data);
            }else if(type == "dbSchema") {
                this.updateDbSchemaToList(data);
            }else if(type == "dbTable") {
                this.updateDbTableToList(data);
            }else if(type == "serverResource") {
                this.updateServerResourceToList(data);
            }else if(type == "dictResource") {
                this.updateDictResourceToList(data);
            }else if(type == "securityKeyResource") {
                this.updateSecurityKeyResourceToList(data);
            }else if(type == "dynamicMapping") {
                this.updateDynamicMappingToList(data);
            }else if(type == "workflowProcessResource") {
                this.updateWorkflowProcessResourceToList(data);
            }else if(type == "kafkaMessageConsumer") {
                this.updateKafkaMessageConsumerToList(data);
            }else if(type == "funcDefine") {
                this.updateFuncDefineToList(data);
            }else if(type == "comboFuncDefine") {
                this.updateComboFuncDefineToList(data);
            }else if(type == "dataSet") {
                this.updateDataSetToList(data);
            }else if(type == "dataModel") {
                this.updateDataModelToList(data);
            }else if(type == "cronJob") {
                this.updateCronJobToList(data);
            }else {
                console.log("error objectType");
            }
        },
        deleteFromList(type, data) {
            if(type == "managedObject") {
                this.deleteManagedObjectFromList(data);
            }else if(type == "dbSchema") {
                this.deleteDbSchemaFromList(data);
            }else if(type == "dbTable") {
                this.deleteDbTableFromList(data);
            }else if(type == "serverResource") {
                this.deleteServerResourceFromList(data);
            }else if(type == "dictResource") {
                this.deleteDictResourceFromList(data);
            }else if(type == "securityKeyResource") {
                this.deleteSecurityKeyResourceFromList(data);
            }else if(type == "dynamicMapping") {
                this.deleteDynamicMappingFromList(data);
            }else if(type == "workflowProcessResource") {
                this.deleteWorkflowProcessResourceFromList(data);
            }else if(type == "kafkaMessageConsumer") {
                this.deleteKafkaMessageConsumerFromList(data);
            }else if(type == "funcDefine") {
                this.deleteFuncDefineFromList(data);
            }else if(type == "comboFuncDefine") {
                this.deleteComboFuncDefineFromList(data);
            }else if(type == "dataSet") {
                this.deleteDataSetFromList(data);
            }else if(type == "dataModel") {
                this.deleteDataModelFromList(data);
            }else if(type == "cronJob") {
                this.deleteCronJobFromList(data);
            }else {
                console.log("error objectType", type);
            }
        },

        
        managedObjectCreateBack() {
        	showManagedObjectCreate1.value = true;
            this.showManagedObjectCreate = false;
        },
        hideManagedObjectCreatePage() {
        	this.showManagedObjectCreate = false;
        },
        updateManagedObjectToList(data) {
        	var businessDomainForUpdateManagedObject = null;
        	console.log(data);
            for(var i in businessSystemInfo.value.businessDomains) {
                if(businessSystemInfo.value.businessDomains[i].businessDomain.id == data.managedObject.domainId) {
                    businessDomainForUpdateManagedObject = businessSystemInfo.value.businessDomains[i];
                    break;
                }
            }
            if(businessDomainForUpdateManagedObject == null) {
                return;
            }
            
            if(data.objectType == "S") {
                var soList = businessDomainForUpdateManagedObject.serviceObjects;
                
                if(soList != null && soList.length > 0) {
                    for(var j in soList) {
                        if(soList[j].id == data.managedObject.id) {
                            soList[j] = data.managedObject;
                            break;
                        }
                    }
                }
            }else if(data.objectType == "D") {
                var doList = businessDomainForUpdateManagedObject.domainObjects;
                if(doList != null && doList.length > 0) {
                    for(var j in doList) {
                        if(doList[j].id == data.managedObject.id) {
                            doList[j] = data.managedObject;
                            break;
                        }
                    }
                }
            }else if(data.objectType == "V") {
                var voList = businessDomainForUpdateManagedObject.valueObjects;
                if(voList != null && voList.length > 0) {
                    for(var j in voList) {
                        if(voList[j].id == data.managedObject.id) {
                            voList[j] = data.managedObject;
                            break;
                        }
                    }
                }
            }
        },
        deleteManagedObjectFromList(data) {
        	var businessDomainForDeleteManagedObject = null;
        	console.log(data);
            for(var i in businessSystemInfo.value.businessDomains) {
                if(businessSystemInfo.value.businessDomains[i].businessDomain.id == data.managedObject.domainId) {
                    businessDomainForDeleteManagedObject = businessSystemInfo.value.businessDomains[i];
                    break;
                }
            }
            if(businessDomainForDeleteManagedObject == null) {
                return;
            }
            
            if(data.objectType == "S") {
                var soList = businessDomainForDeleteManagedObject.serviceObjects;
                
                if(soList != null && soList.length > 0) {
                    for(var j in soList) {
                        if(soList[j].id == data.managedObject.id) {
                            soList.splice(j,1);
                            break;
                        }
                    }
                }
            }else if(data.objectType == "D") {
                var doList = businessDomainForDeleteManagedObject.domainObjects;
                if(doList != null && doList.length > 0) {
                    for(var j in doList) {
                        if(doList[j].id == data.managedObject.id) {
                            doList.splice(j,1);
                            break;
                        }
                    }
                }
            }else if(data.objectType == "V") {
                var voList = businessDomainForDeleteManagedObject.valueObjects;
                if(voList != null && voList.length > 0) {
                    for(var j in voList) {
                        if(voList[j].id == data.managedObject.id) {
                            voList.splice(j,1);
                            break;
                        }
                    }
                }
            }
            
            this.editObject = null;
        	this.editObjectKey = null;
            this.showObjectEdit = false;
            this.router.replace({name: 'blank'});
        },
        addNewManagedObjectToList(data) {
        	var businessDomains = businessSystemInfo.value.businessDomains;
            businessDomains.forEach((item)=> {
                if(item.businessDomain.id == data.managedObject.domainId) {
                    if(data.objectType == "S") {
                        if(item.soMap == null) {
                            item.soMap = {};
                        }
                        var serviceArray = item.serviceObjects;
                        if(serviceArray == null) {
                            serviceArray = [];
                            item.serviceObjects = serviceArray;
                        }
                        serviceArray.push(data.managedObject);
                        serviceArray.sort((a,b)=> {
                        	
                        	if(a.code > b.code) {
                                return 1;
                            }else if(a.code == b.code) {
                                return 0;
                            }else {
                                return -1;
                            }
                        });
                    }else if(data.objectType == "D") {
                        var domainArray = item.domainObjects;
                        if(domainArray == null) {
                            domainArray = [];
                            item.domainObjects = domainArray;
                        }
                        domainArray.push(data.managedObject);
                        domainArray.sort((a,b)=> {
                            if(a.code > b.code) {
                                return 1;
                            }else if(a.code == b.code) {
                                return 0;
                            }else {
                                return -1;
                            }
                        });
                    }else if(data.objectType == "V") {
                        if(item.valueObjects == null) {
                            item.valueObjects = [];
                        }
                        item.valueObjects.push(data.managedObject);
                        
                        item.valueObjects.sort((a,b)=> {
                            if(a.code > b.code) {
                                return 1;
                            }else if(a.code == b.code) {
                                return 0;
                            }else {
                                return -1;
                            }
                        });
                    }
                }
            });
            this.editObject = data;
            this.editObjectKey = "managedObject";
            this.managedObjectTypes.forEach((objectType)=>{
                if('D' == objectType.value) {
                    objectType.children.forEach((subObjectType)=>{
                    	if(subObjectType.value == ("D:"+this.editObject.objectType)) {
                            this.editObject.objectTypeName = subObjectType.label;
                            return;
                        }
                    });
                    return;
                }
            });
            businessSystemInfo.value.businessDomains.forEach((row)=> {
                if(row.businessDomain.id==data.managedObject.domainId) {
                    this.editObject.businessDomainName = row.businessDomain.name;
                    return;
                }
            });
            currentObjectType.value = ["D:"+this.editObject.objectType];
            this.showObjectEdit = true;
            this.router.replace({
                    name: 'editManagedObject', 
                    query: {
                        "objectType": this.editObject.objectType, 
                        "objectId": this.editObject.managedObject.id, 
                        "objectTypeName":  this.editObject.objectTypeName,
                    }});
            this.showManagedObjectCreate = false;
        },

        dbSchemaCreateBack() {
            showManagedObjectCreate1.value = true;
            this.showDbSchemaCreate = false;
        },
        hideDbSchemaCreatePage() {
            this.showDbSchemaCreate = false;
        },
        addNewDbSchemaToList(data) {
        	if(businessSystemInfo.value.dbSchemas == null) {
        		businessSystemInfo.value.dbSchemas = [];
        	}
        	businessSystemInfo.value.dbSchemas.push(data);
        	businessSystemInfo.value.dbSchemas.sort((a,b)=>{return a.name > b.name});
        	this.showDbSchemaCreate = false;
            this.editObject = data;
        	this.editObjectKey = "dbSchema";
            currentObjectType.value = ["O:D"];
            this.showObjectEdit = true;
            this.router.replace({name: 'editDbSchema', query: {"objectId": this.editObject.id}});
        },
        updateDbSchemaToList(data) {
        	 businessSystemInfo.value.dbSchemas.forEach((row)=> {
                 if(row.id == data.id) {
                     row.name = data.name;
                     row.description = data.description;
                     row.dbType = data.dbType;
                     row.username = data.username;
                     row.password = data.password;
                     row.url = data.url;
                     row.driverClass = data.driverClass;
                     row.dataSourceCfg = data.dataSourceCfg;
                     row.status = data.status;
                     businessSystemInfo.value.dbSchemas.sort((a,b)=>{return a.name > b.name});
                     return;
                 }
             });
        },
        deleteDbSchemaFromList(data) {
        	for(var i in businessSystemInfo.value.dbSchemas) {
                if(businessSystemInfo.value.dbSchemas[i].id == data.id) {
                    businessSystemInfo.value.dbSchemas.splice(i,1);
                    break;
                }
            }
        	this.editObject = null;
        	this.editObjectKey = null;
            this.showObjectEdit = false;
            this.router.replace({name: 'blank'});
        },

        cronJobCreateBack() {
            showManagedObjectCreate1.value = true;
            this.showCronJobCreate = false;
        },
        hideCronJobCreatePage() {
            this.showCronJobCreate = false;
        },
        addCronJobToList(data) {
        	if(businessSystemInfo.value.cronJobs == null) {
        		businessSystemInfo.value.cronJobs = [];
        	}
        	businessSystemInfo.value.cronJobs.push(data);
        	businessSystemInfo.value.cronJobs.sort((a,b)=>{return a.name > b.name});
        	this.showCronJobCreate = false;
            this.editObject = data;
        	this.editObjectKey = "cronJob";
            currentObjectType.value = ["O:J"];
            this.showObjectEdit = true;
            this.router.replace({name: 'editCronJob', query: {"objectId": this.editObject.id}});
        },
        updateCronJobToList(data) {
        	 businessSystemInfo.value.cronJobs.forEach((row)=> {
                 if(row.id == data.id) {
                     row.name = data.name;
                     row.code = data.code;
                     row.description = data.description;
                     row.jobType = data.jobType;
                     row.jobHandlerMethod = data.jobHandlerMethod;
                     row.jobHandlerService = data.jobHandlerService;
                     row.jobSchedule = data.jobSchedule;
                     businessSystemInfo.value.cronJobs.sort((a,b)=>{return a.name > b.name});
                     return;
                 }
             });
        },
        deleteCronJobFromList(data) {
        	for(var i in businessSystemInfo.value.cronJobs) {
                if(businessSystemInfo.value.cronJobs[i].id == data.id) {
                    businessSystemInfo.value.cronJobs.splice(i,1);
                    break;
                }
            }
        	this.editObject = null;
        	this.editObjectKey = null;
            this.showObjectEdit = false;
            this.router.replace({name: 'blank'});
        },

        serverResourceCreateBack() {
            showManagedObjectCreate1.value = true;
            this.showServerResourceCreate = false;
        },
        hideServerResourceCreatePage() {
            this.showServerResourceCreate = false;
        },
        addNewServerResourceToList(data) {
            if(businessSystemInfo.value.serverResources == null) {
                businessSystemInfo.value.serverResources = [];
            }
            businessSystemInfo.value.serverResources.push(data);
            businessSystemInfo.value.serverResources.sort((a,b)=>{return a.name > b.name});
            this.showServerResourceCreate = false;
            this.editObject = data;
        	this.editObjectKey = "serverResource";
            currentObjectType.value = ["O:R"];
            this.showObjectEdit = true;
            this.router.replace({name: 'editServerResource', query: {"objectId": this.editObject.id}});
        },
        deleteServerResourceFromList(data) {
        	for(var i in businessSystemInfo.value.serverResources) {
                if(businessSystemInfo.value.serverResources[i].id == data.id) {
                	businessSystemInfo.value.serverResources.splice(i,1);
                    this.editObject = null;
        	        this.editObjectKey = null;
                    this.showObjectEdit = false;
                    this.router.replace({name: 'blank'});
                    break;
                }
            }
        },
        updateServerResourceToList(data) {
        	businessSystemInfo.value.serverResources.forEach((row)=> {
                if(row.id == data.id) {
                    row.name = data.name;
                    row.description = data.description;
                    row.resourceType = data.resourceType;
                    row.username = data.username;
                    row.password = data.password;
                    row.url = data.url;
                    row.host = data.host;
                    row.port = data.port;
                    row.serverCfg = data.serverCfg;
                    row.status = data.status;
                    businessSystemInfo.value.serverResources.sort((a,b)=>{return a.name > b.name});
                    return;
                }
            });
        },
        dictResourceCreateBack() {
            showManagedObjectCreate1.value = true;
            this.showDictResourceCreate = false;
        },
        hideDictResourceCreatePage() {
            this.showDictResourceCreate = false;
        },
        addNewDictResourceToList(data) {
            if(businessSystemInfo.value.dictResources == null) {
                businessSystemInfo.value.dictResources = [];
            }
            businessSystemInfo.value.dictResources.push(data);
            businessSystemInfo.value.dictResources.sort((a,b)=>{return a.name > b.name});
            this.showDictResourceCreate = false;
            this.editObject = data;
        	this.editObjectKey = "dictResource";
            currentObjectType.value = ["O:P"];
            this.showObjectEdit = true;
            this.router.replace({name: 'editDictResource', query: {"objectId": this.editObject.id}});
        },
        deleteDictResourceFromList(data) {
        	for(var i in businessSystemInfo.value.dictResources) {
                if(businessSystemInfo.value.dictResources[i].id == data.id) {
                	businessSystemInfo.value.dictResources.splice(i,1);
                    this.editObject = null;
        	        this.editObjectKey = null;
                    this.showObjectEdit = false;
                    this.router.replace({name: 'blank'});
                    break;
                }
            }
        },
        updateDictResourceToList(data) {
        	businessSystemInfo.value.dictResources.forEach((row)=> {
                if(row.id == data.id) {
                    row.name = data.name;
                    row.type = data.type;
                    row.dbSchemaId = data.dbSchemaId;
                    row.dictTable = data.dictTable;
                    row.subDictTable = data.subDictTable;
                    
                    row.dictCodeCol = data.dictCodeCol;
                    row.dictNameCol = data.dictNameCol;
                    row.dictStatusCol = data.dictStatusCol;
                    row.effStatusValue = data.effStatusValue;

                    row.subTableDictCodeCol = data.subTableDictCodeCol;
                    row.subTableStatusCol = data.subTableStatusCol;
                    row.dictValueCol = data.dictValueCol;
                    row.dictValueLabelCol = data.dictValueLabelCol;
                    row.parentDictCodeCol = data.parentDictCodeCol;
                    row.parentDictValueCol = data.parentDictValueCol;
                    row.dictValue2Col = data.dictValue2Col;
                    row.dictValue3Col = data.dictValue3Col;
                    row.status = data.status;
                    businessSystemInfo.value.dictResources.sort((a,b)=>{return a.name > b.name});
                    return;
                }
            });
        },
        securityKeyResourceCreateBack() {
            showManagedObjectCreate1.value = true;
            this.showSecurityKeyResourceCreate = false;
        },
        hideSecurityKeyResourceCreatePage() {
            this.showSecurityKeyResourceCreate = false;
        },
        addNewSecurityKeyResourceToList(data) {
            if(businessSystemInfo.value.securityKeyResources == null) {
                businessSystemInfo.value.securityKeyResources = [];
            }
            businessSystemInfo.value.securityKeyResources.push(data);
            businessSystemInfo.value.securityKeyResources.sort((a,b)=>{return a.name > b.name});
            this.showSecurityKeyResourceCreate = false;
            this.editObject = data;
        	this.editObjectKey = "securityKeyResource";
            currentObjectType.value = ["O:S"];
            this.showObjectEdit = true;
            this.router.replace({name: 'editSecurityKeyResource', query: {"objectId": this.editObject.id}});
        },
        deleteSecurityKeyResourceFromList(data) {
        	for(var i in businessSystemInfo.value.securityKeyResources) {
                if(businessSystemInfo.value.securityKeyResources[i].id == data.id) {
                	businessSystemInfo.value.securityKeyResources.splice(i,1);
                    this.editObject = null;
        	        this.editObjectKey = null;
                    this.showObjectEdit = false;
                    this.router.replace({name: 'blank'});
                    break;
                }
            }
        },
        updateSecurityKeyResourceToList(data) {
        	businessSystemInfo.value.securityKeyResources.forEach((row)=> {
                if(row.id == data.id) {
                    row.name = data.name;
                    row.type = data.type;
                    row.securityKey = data.securityKey;
                    row.cipherAlgorithm = data.cipherAlgorithm;
                    row.privateKeyFile = data.privateKeyFile;
                    row.publicKeyFile = data.publicKeyFile;
                    row.privateKeyFileName = data.privateKeyFileName;
                    row.publicKeyFileName = data.publicKeyFileName;
                    row.privateKeyPwd = data.privateKeyPwd;
                    businessSystemInfo.value.dictResources.sort((a,b)=>{return a.name > b.name});
                    return;
                }
            });
        },
        dynamicMappingCreateBack() {
            showManagedObjectCreate1.value = true;
            this.showDynamicMappingCreate = false;
        },
        hideDynamicMappingCreatePage() {
            this.showDynamicMappingCreate = false;
        },
        addNewDynamicMappingToList(data) {
            if(businessSystemInfo.value.dynamicMappings == null) {
                businessSystemInfo.value.dynamicMappings = [];
            }
            businessSystemInfo.value.dynamicMappings.push(data);
            businessSystemInfo.value.dynamicMappings.sort((a,b)=>{return a.name > b.name});
            this.showDynamicMappingCreate = false;
            this.editObject = data;
        	this.editObjectKey = "dynamicMapping";
            currentObjectType.value = ["O:Y"];
            this.showObjectEdit = true;
            this.router.replace({name: 'editDynamicMapping', query: {"objectId": this.editObject.id}});
        },
        deleteDynamicMappingFromList(data) {
        	for(var i in businessSystemInfo.value.dynamicMappings) {
                if(businessSystemInfo.value.dynamicMappings[i].id == data.id) {
                	businessSystemInfo.value.dynamicMappings.splice(i,1);
                    this.editObject = null;
        	        this.editObjectKey = null;
                    this.showObjectEdit = false;
                    this.router.replace({name: 'blank'});
                    break;
                }
            }
        },
        updateDynamicMappingToList(data) {
        	businessSystemInfo.value.dynamicMappings.forEach((row)=> {
                if(row.id == data.id) {
                    row.name = data.name;
                    row.description = data.description;
                    row.mappingType = data.mappingType;
                    row.businessType = data.businessType;
                    row.subBusinessType = data.subBusinessType;
                    row.keyAttribute = data.keyAttribute;
                    row.status = data.status;
                    businessSystemInfo.value.dynamicMappings.sort((a,b)=>{return a.name > b.name});
                    return;
                }
            });
        },
        workflowProcessResourceCreateBack() {
            showManagedObjectCreate1.value = true;
            this.showWorkflowProcessResourceCreate = false;
        },
        hideWorkflowProcessResourceCreatePage() {
            this.showWorkflowProcessResourceCreate = false;
        },
        addNewWorkflowProcessResourceToList(data) {
            if(businessSystemInfo.value.workflowProcessResources == null) {
                businessSystemInfo.value.workflowProcessResources = [];
            }
            businessSystemInfo.value.workflowProcessResources.push(data);
            businessSystemInfo.value.workflowProcessResources.sort((a,b)=>{return a.name > b.name});
            currentObjectType.value = ["W:P"];
            this.editObject = data;
            this.editObjectKey = "workflowProcessResource";
            this.showObjectEdit = true;
            this.router.replace({name: 'editWorkflowProcessResource', query: {"objectId": this.editObject.id}});
            this.showWorkflowProcessResourceCreate = false;
        },
        updateWorkflowProcessResourceToList(data) {
            businessSystemInfo.value.workflowProcessResources.forEach((row)=> {
                if(row.id == data.id) {
                    row.name = data.name;
                    row.description = data.description;
                    businessSystemInfo.value.workflowProcessResources.sort((a,b)=>{return a.name > b.name});
                    return;
                }
            });
        },
        deleteWorkflowProcessResourceFromList(data) {
            for(var i in businessSystemInfo.value.workflowProcessResources) {
                if(businessSystemInfo.value.workflowProcessResources[i].id == data.id) {
                    businessSystemInfo.value.workflowProcessResources.splice(i,1);
                    this.editObject = null;
        	        this.editObjectKey = null;
                    this.showObjectEdit = false;
                    this.router.replace({name: 'blank'});
                    break;
                }
            }
        },
        kafkaMessageConsumerCreateBack() {
            showManagedObjectCreate1.value = true;
            this.showKafkaMessageConsumerCreate = false;
        },
        hideKafkaMessageConsumerCreatePage() {
            this.showKafkaMessageConsumerCreate = false;
        },
        addKafkaMessageConsumerToList(data) {
            if(businessSystemInfo.value.messageConsumers == null) {
                businessSystemInfo.value.messageConsumers = [];
            }
            businessSystemInfo.value.messageConsumers.push(data);
            businessSystemInfo.value.messageConsumers.sort((a,b)=>{return a.name > b.name});
            currentObjectType.value = ["M:K"];
            this.editObject = data;
            this.editObjectKey = "kafkaMessageConsumer";
            this.showObjectEdit = true;
            this.router.replace({name: 'editKafkaMessageConsumer', query: {"objectId": this.editObject.id, "systemId":businessSystemInfo.value.businessSystem.id}});
            this.showKafkaMessageConsumerCreate = false;
        },
        updateKafkaMessageConsumerToList(data) {
            businessSystemInfo.value.messageConsumers.forEach((row)=> {
                if(row.id == data.id) {
                    row.name = data.name;
                    row.description = data.description;
                    businessSystemInfo.value.messageConsumers.sort((a,b)=>{return a.name > b.name});
                    return;
                }
            });
        },
        deleteKafkaMessageConsumerFromList(data) {
            for(var i in businessSystemInfo.value.messageConsumers) {
                if(businessSystemInfo.value.messageConsumers[i].id == data.id) {
                    businessSystemInfo.value.messageConsumers.splice(i,1);
                    this.editObject = null;
        	        this.editObjectKey = null;
                    this.showObjectEdit = false;
                    this.router.replace({name: 'blank'});
                    break;
                }
            }
        },
        funcDefineCreateBack() {
            showManagedObjectCreate1.value = true;
            this.showFuncDefineCreate = false;
        },
        hideFuncDefineCreatePage() {
            this.showFuncDefineCreate = false;
        },
        addNewFuncDefineToList(data) {
            var businessDomainForUpdateManagedObject = null;
        	for(var i in businessSystemInfo.value.businessDomains) {
                if(businessSystemInfo.value.businessDomains[i].businessDomain.id == data.domainId) {
                    businessDomainForUpdateManagedObject = businessSystemInfo.value.businessDomains[i];
                    break;
                }
            }
            if(businessDomainForUpdateManagedObject == null) {
                return;
            }
            if(data.funcTemplate != null && 'N' == data.funcTemplate.isMainFunc) {
                if(businessDomainForUpdateManagedObject.subFuncDefines == null) {
                    businessDomainForUpdateManagedObject.subFuncDefines = [];
                }
                businessDomainForUpdateManagedObject.subFuncDefines.push(data);
                businessDomainForUpdateManagedObject.subFuncDefines.sort((a,b)=>{return a.name > b.name});
            }else {
                if(businessDomainForUpdateManagedObject.funcDefines == null) {
                    businessDomainForUpdateManagedObject.funcDefines = [];
                }
                businessDomainForUpdateManagedObject.funcDefines.push(data);
                businessDomainForUpdateManagedObject.funcDefines.sort((a,b)=>{return a.name > b.name});
            }
            
            this.editObject = data;
            currentObjectType.value = ["F:F"];
            this.editObjectKey = "funcDefine";
            this.showObjectEdit = true;
            this.router.replace({name: 'editFuncDefine', query: {"objectId": this.editObject.id}});
            this.showFuncDefineCreate = false;
        },
        updateFuncDefineToList(data) {
            var businessDomainForUpdateManagedObject = null;
        	for(var i in businessSystemInfo.value.businessDomains) {
                if(businessSystemInfo.value.businessDomains[i].businessDomain.id == data.domainId) {
                    businessDomainForUpdateManagedObject = businessSystemInfo.value.businessDomains[i];
                    break;
                }
            }
            if(businessDomainForUpdateManagedObject == null) {
                return;
            }
            if(data.funcTemplate != null && 'N' == data.funcTemplate.isMainFunc) {
                businessDomainForUpdateManagedObject.subFuncDefines.forEach((row)=> {
                    if(row.id == data.id) {
                        row.name = data.name;
                        row.description = data.description;
                        businessDomainForUpdateManagedObject.subFuncDefines.sort((a,b)=>{return a.name > b.name});
                        return;
                    }
                });
            }else {
                businessDomainForUpdateManagedObject.funcDefines.forEach((row)=> {
                    if(row.id == data.id) {
                        row.name = data.name;
                        row.description = data.description;
                        businessDomainForUpdateManagedObject.funcDefines.sort((a,b)=>{return a.name > b.name});
                        return;
                    }
                });
            }
        	
        },
        deleteFuncDefineFromList(data) {
            var businessDomainForUpdateManagedObject = null;
        	for(var i in businessSystemInfo.value.businessDomains) {
                if(businessSystemInfo.value.businessDomains[i].businessDomain.id == data.domainId) {
                    businessDomainForUpdateManagedObject = businessSystemInfo.value.businessDomains[i];
                    break;
                }
            }
            if(businessDomainForUpdateManagedObject == null) {
                return;
            }
            if(data.funcTemplate != null && 'N' == data.funcTemplate.isMainFunc) {
                for(var i in businessDomainForUpdateManagedObject.subFuncDefines) {
                    if(businessDomainForUpdateManagedObject.subFuncDefines[i].id == data.id) {
                        businessDomainForUpdateManagedObject.subFuncDefines.splice(i,1);
                        this.editObject = null;
                        this.editObjectKey = null;
                        this.showObjectEdit = false;
                        this.router.replace({name: 'blank'});
                        break;
                    }
                }
            }else {
                for(var i in businessDomainForUpdateManagedObject.funcDefines) {
                    if(businessDomainForUpdateManagedObject.funcDefines[i].id == data.id) {
                        businessDomainForUpdateManagedObject.funcDefines.splice(i,1);
                        this.editObject = null;
                        this.editObjectKey = null;
                        this.showObjectEdit = false;
                        this.router.replace({name: 'blank'});
                        break;
                    }
                }
            }
        	
        },
        comboFuncDefineCreateBack() {
            showManagedObjectCreate1.value = true;
            this.showComboFuncDefineCreate = false;
        },
        hideComboFuncDefineCreatePage() {
            this.showComboFuncDefineCreate = false;
        },
        addNewComboFuncDefineToList(data) {
            var businessDomainForUpdateManagedObject = null;
        	for(var i in businessSystemInfo.value.businessDomains) {
                if(businessSystemInfo.value.businessDomains[i].businessDomain.id == data.domainId) {
                    businessDomainForUpdateManagedObject = businessSystemInfo.value.businessDomains[i];
                    break;
                }
            }
            if(businessDomainForUpdateManagedObject == null) {
                return;
            }
            if(businessDomainForUpdateManagedObject.comboFuncDefines == null) {
                businessDomainForUpdateManagedObject.comboFuncDefines = [];
            }
            businessDomainForUpdateManagedObject.comboFuncDefines.push(data);
            businessDomainForUpdateManagedObject.comboFuncDefines.sort((a,b)=>{return a.name > b.name});
            currentObjectType.value = ["F:C"];
            this.editObject = data;
            this.editObjectKey = "comboFuncDefine";
            this.showObjectEdit = true;
            this.router.replace({name: 'editComboFuncDefine', query: {"objectId": this.editObject.id}});
            this.showComboFuncDefineCreate = false;
            console.log(this.editObject);
        },
        updateComboFuncDefineToList(data) {
            var businessDomainForUpdateManagedObject = null;
        	for(var i in businessSystemInfo.value.businessDomains) {
                if(businessSystemInfo.value.businessDomains[i].businessDomain.id == data.domainId) {
                    businessDomainForUpdateManagedObject = businessSystemInfo.value.businessDomains[i];
                    break;
                }
            }
            if(businessDomainForUpdateManagedObject == null) {
                return;
            }
            businessDomainForUpdateManagedObject.comboFuncDefines.forEach((row)=> {
                if(row.id == data.id) {
                    row.name = data.name;
                    row.description = data.description;
                    businessDomainForUpdateManagedObject.comboFuncDefines.sort((a,b)=>{return a.name > b.name});
                    return;
                }
            });
        },
        deleteComboFuncDefineFromList(data) {
            var businessDomainForUpdateManagedObject = null;
        	for(var i in businessSystemInfo.value.businessDomains) {
                if(businessSystemInfo.value.businessDomains[i].businessDomain.id == data.domainId) {
                    businessDomainForUpdateManagedObject = businessSystemInfo.value.businessDomains[i];
                    break;
                }
            }
            if(businessDomainForUpdateManagedObject == null) {
                return;
            }
            for(var i in businessDomainForUpdateManagedObject.comboFuncDefines) {
                if(businessDomainForUpdateManagedObject.comboFuncDefines[i].id == data.id) {
                    businessDomainForUpdateManagedObject.comboFuncDefines.splice(i,1);
                    this.editObject = null;
        	        this.editObjectKey = null;
                    this.showObjectEdit = false;
                    this.router.replace({name: 'blank'});
                    break;
                }
            }
        },
        dataModelCreateBack() {
            showManagedObjectCreate1.value = true;
            this.showDataModelCreate = false;
        },
        hideDataModelCreatePage() {
            this.showDataModelCreate = false;
        },
        addNewDataModelToList(data) {
            var businessDomainForUpdateManagedObject = null;
        	for(var i in businessSystemInfo.value.businessDomains) {
                if(businessSystemInfo.value.businessDomains[i].businessDomain.id == data.domainId) {
                    businessDomainForUpdateManagedObject = businessSystemInfo.value.businessDomains[i];
                    break;
                }
            }
            if(businessDomainForUpdateManagedObject == null) {
                return;
            }
            if(businessDomainForUpdateManagedObject.dataModels == null) {
                businessDomainForUpdateManagedObject.dataModels = [];
            }
            businessDomainForUpdateManagedObject.dataModels.push(data);
            businessDomainForUpdateManagedObject.dataModels.sort((a,b)=>{return a.name > b.name});
            
            this.editObject = data;
            currentObjectType.value = ["B:M"];
            this.editObjectKey = "dataModel";
            this.showObjectEdit = true;
            this.router.replace({name: 'editDataModel', query: {"objectId": this.editObject.id}});
            this.showDataModelCreate = false;
        },
        updateDataModelToList(data) {
            var businessDomainForUpdateManagedObject = null;
        	for(var i in businessSystemInfo.value.businessDomains) {
                if(businessSystemInfo.value.businessDomains[i].businessDomain.id == data.domainId) {
                    businessDomainForUpdateManagedObject = businessSystemInfo.value.businessDomains[i];
                    break;
                }
            }
            if(businessDomainForUpdateManagedObject == null) {
                return;
            }
            businessDomainForUpdateManagedObject.dataModels.forEach((row)=> {
                if(row.id == data.id) {
                    row.name = data.name;
                    row.description = data.description;
                    businessDomainForUpdateManagedObject.dataModels.sort((a,b)=>{return a.name > b.name});
                    return;
                }
            });
        },
        deleteDataModelFromList(data) {
            var businessDomainForUpdateManagedObject = null;
        	for(var i in businessSystemInfo.value.businessDomains) {
                if(businessSystemInfo.value.businessDomains[i].businessDomain.id == data.domainId) {
                    businessDomainForUpdateManagedObject = businessSystemInfo.value.businessDomains[i];
                    break;
                }
            }
            if(businessDomainForUpdateManagedObject == null) {
                return;
            }
            for(var i in businessDomainForUpdateManagedObject.dataModels) {
                if(businessDomainForUpdateManagedObject.dataModels[i].id == data.id) {
                    businessDomainForUpdateManagedObject.dataModels.splice(i,1);
                    this.editObject = null;
                    this.editObjectKey = null;
                    this.showObjectEdit = false;
                    this.router.replace({name: 'blank'});
                    break;
                }
            }
        },
        dataSetCreateBack() {
            showManagedObjectCreate1.value = true;
            this.showDataSetCreate = false;
        },
        hideDataSetCreatePage() {
            this.showDataSetCreate = false;
        },
        addNewDataSetToList(data) {
            var businessDomainForUpdateManagedObject = null;
        	for(var i in businessSystemInfo.value.businessDomains) {
                if(businessSystemInfo.value.businessDomains[i].businessDomain.id == data.domainId) {
                    businessDomainForUpdateManagedObject = businessSystemInfo.value.businessDomains[i];
                    break;
                }
            }
            if(businessDomainForUpdateManagedObject == null) {
                return;
            }
            if(businessDomainForUpdateManagedObject.dataSets == null) {
                businessDomainForUpdateManagedObject.dataSets = [];
            }
            businessDomainForUpdateManagedObject.dataSets.push(data);
            businessDomainForUpdateManagedObject.dataSets.sort((a,b)=>{return a.name > b.name});
            
            this.editObject = data;
            currentObjectType.value = ["B:S"];
            this.editObjectKey = "dataSet";
            this.showObjectEdit = true;
            this.router.replace({name: 'editDataSet', query: {"objectId": this.editObject.id}});
            this.showDataSetCreate = false;
        },
        updateDataSetToList(data) {
            var businessDomainForUpdateManagedObject = null;
        	for(var i in businessSystemInfo.value.businessDomains) {
                if(businessSystemInfo.value.businessDomains[i].businessDomain.id == data.domainId) {
                    businessDomainForUpdateManagedObject = businessSystemInfo.value.businessDomains[i];
                    break;
                }
            }
            if(businessDomainForUpdateManagedObject == null) {
                return;
            }
            businessDomainForUpdateManagedObject.dataSets.forEach((row)=> {
                if(row.id == data.id) {
                    row.name = data.name;
                    row.description = data.description;
                    businessDomainForUpdateManagedObject.dataSets.sort((a,b)=>{return a.name > b.name});
                    return;
                }
            });
        },
        deleteDataSetFromList(data) {
            var businessDomainForUpdateManagedObject = null;
        	for(var i in businessSystemInfo.value.businessDomains) {
                if(businessSystemInfo.value.businessDomains[i].businessDomain.id == data.domainId) {
                    businessDomainForUpdateManagedObject = businessSystemInfo.value.businessDomains[i];
                    break;
                }
            }
            if(businessDomainForUpdateManagedObject == null) {
                return;
            }
            for(var i in businessDomainForUpdateManagedObject.dataSets) {
                if(businessDomainForUpdateManagedObject.dataSets[i].id == data.id) {
                    businessDomainForUpdateManagedObject.dataSets.splice(i,1);
                    this.editObject = null;
                    this.editObjectKey = null;
                    this.showObjectEdit = false;
                    this.router.replace({name: 'blank'});
                    break;
                }
            }
        },
        dbTableCreateBack() {
            showManagedObjectCreate1.value = true;
            this.showDbTableCreate = false;
        },
        hideDbTableCreatePage() {
            this.showDbTableCreate = false;
        },
        addNewDbTableToList(data) {
            if(businessSystemInfo.value.dbSchemas == null) {
                businessSystemInfo.value.dbSchemas = [];
            }
            for(var i in businessSystemInfo.value.dbSchemas) {
            	if(businessSystemInfo.value.dbSchemas[i].id == data.schemaId) {
            		if(businessSystemInfo.value.dbSchemas[i].dbTables == null) {
            			businessSystemInfo.value.dbSchemas[i].dbTables = [];
            		}
            		businessSystemInfo.value.dbSchemas[i].dbTables.push(data);
                    this.editObject = data;
            		this.editObject.dbColumns = [];
            		this.editObject.dbSchema = businessSystemInfo.value.dbSchemas[i];
            		currentObjectType.value = ["O:T"];
                    this.editObjectKey = "dbTable";
                    businessSystemInfo.value.dbSchemas[i].dbTables.sort((a,b)=>{return a.name > b.name});
                    this.showObjectEdit = true;
                    this.router.replace({name: 'editDbTable', query: {"objectId": this.editObject.id}});
            		break;
            	}
            }
            this.showDbTableCreate = false;
        },
        updateDbTableToList(data){
        	businessSystemInfo.value.dbSchemas.forEach((db)=> {
                if(db.id == data.schemaId) {
                    db.dbTables.forEach((row)=> {
                        if(row.id == data.id) {
                            row.name = data.name;
                            row.description = data.description;
                            row.schemaId = data.schemaId;
                            row.status = data.status;
                            db.dbTables.sort((a,b)=>{return a.name > b.name});
                        }
                        return;
                    });                          
                    return;
                }
            });
        },
        deleteDbTableFromList(data) {
        	for(var i in businessSystemInfo.value.dbSchemas) {
                if(businessSystemInfo.value.dbSchemas[i].id == data.schemaId) {
                    for(var j in businessSystemInfo.value.dbSchemas[i].dbTables) {
                        if(businessSystemInfo.value.dbSchemas[i].dbTables[j].id == data.id) {
                            businessSystemInfo.value.dbSchemas[i].dbTables.splice(j,1);
                            break;
                        }
                    }
                    break;
                }
            }
        	this.editObject = null;
        	this.editObjectKey = null;
            this.showObjectEdit = false;
            this.router.replace({name: 'blank'});
        },



        showBusinessSystemEditPage() {
            this.businessSystem = {
                id : businessSystemInfo.value.businessSystem.id,
                name : businessSystemInfo.value.businessSystem.name,
                servletContext : businessSystemInfo.value.businessSystem.servletContext,
                description : businessSystemInfo.value.businessSystem.description,
                version : businessSystemInfo.value.businessSystem.version,
                status : businessSystemInfo.value.businessSystem.status,
                tenantId : businessSystemInfo.value.businessSystem.tenantId,
                createTime : businessSystemInfo.value.businessSystem.createTime,
                updateTime : businessSystemInfo.value.businessSystem.updateTime
            }    
            this.showBusinessSystemEdit = true;
        },
        hideBusinessSystemEditPage() {
            this.showBusinessSystemEdit = false;
        },
        updateBusinessSystemToInfo(system) {
            businessSystemInfo.value.businessSystem = system;
            this.showBusinessSystemEdit = false;
        },
        showBusinessSystemAuthEditPage() {
        	if(businessSystemInfo.value.businessSystem.businessSystemAuth != null) {
        		this.businessSystemAuth = {
                    id : businessSystemInfo.value.businessSystem.businessSystemAuth.id,
                    authType : businessSystemInfo.value.businessSystem.businessSystemAuth.authType,
                    userInfoKey : businessSystemInfo.value.businessSystem.businessSystemAuth.userInfoKey,
                    userIdAttr : businessSystemInfo.value.businessSystem.businessSystemAuth.userIdAttr,
                    userInfoService : businessSystemInfo.value.businessSystem.businessSystemAuth.userInfoService,
                    userInfoMethod : businessSystemInfo.value.businessSystem.businessSystemAuth.userInfoMethod,
                    authConfig : businessSystemInfo.value.businessSystem.businessSystemAuth.authConfig,
                    ignoreUrls : businessSystemInfo.value.businessSystem.businessSystemAuth.ignoreUrls,
                    systemId : businessSystemInfo.value.businessSystem.businessSystemAuth.systemId,
                    systemName : businessSystemInfo.value.businessSystem.name,
                    tenantId : businessSystemInfo.value.businessSystem.businessSystemAuth.tenantId,
                    createTime : businessSystemInfo.value.businessSystem.businessSystemAuth.createTime,
                    updateTime : businessSystemInfo.value.businessSystem.businessSystemAuth.updateTime
                } 
        	}else {
        		this.businessSystemAuth = {
        			systemId : businessSystemInfo.value.businessSystem.id,
                    systemName : businessSystemInfo.value.businessSystem.name,
                    tenantId : businessSystemInfo.value.businessSystem.tenantId,
        		};
        	}
               
            this.showBusinessSystemAuthEdit = true;
        },
        hideBusinessSystemAuthEditPage() {
            this.showBusinessSystemAuthEdit = false;
        },
        updateBusinessSystemAuthToInfo(auth) {
        	businessSystemInfo.value.businessSystem.businessSystemAuth = auth;
            this.showBusinessSystemAuthEdit = false;
        },
        showBusinessSystemAutoLoadEditPage() {
            if(businessSystemInfo.value.businessSystem.businessSystemAutoLoad != null) {
                this.businessSystemAutoLoad = {
                    id : businessSystemInfo.value.businessSystem.businessSystemAutoLoad.id,
                    autoLoadService : businessSystemInfo.value.businessSystem.businessSystemAutoLoad.autoLoadService,
                    autoLoadMethod : businessSystemInfo.value.businessSystem.businessSystemAutoLoad.autoLoadMethod,
                    systemId : businessSystemInfo.value.businessSystem.businessSystemAutoLoad.systemId,
                    systemName : businessSystemInfo.value.businessSystem.name,
                    tenantId : businessSystemInfo.value.businessSystem.businessSystemAutoLoad.tenantId,
                    createTime : businessSystemInfo.value.businessSystem.businessSystemAutoLoad.createTime,
                    updateTime : businessSystemInfo.value.businessSystem.businessSystemAutoLoad.updateTime
                } 
            }else {
                this.businessSystemAutoLoad = {
                    systemId : businessSystemInfo.value.businessSystem.id,
                    systemName : businessSystemInfo.value.businessSystem.name,
                    tenantId : businessSystemInfo.value.businessSystem.tenantId,
                };
            }
               
            this.showBusinessSystemAutoLoadEdit = true;
        },
        hideBusinessSystemAutoLoadEditPage() {
            this.showBusinessSystemAutoLoadEdit = false;
        },
        updateBusinessSystemAutoLoadToInfo(autoLoad) {
            businessSystemInfo.value.businessSystem.businessSystemAutoLoad = autoLoad;
            this.showBusinessSystemAutoLoadEdit = false;
        },
        hideModelDrivenGeneratePage() {
            this.showModelDrivenGenerate = false;
        },
        manageModelDrivenGenerateBack() {
            showManagedObjectCreate1.value = true;
            this.showModelDrivenGenerate = false;
        },

        domainOperationExposeBack() {
            showManagedObjectCreate1.value = true;
            this.showDomainOperationExpose = false;
        },
        hideDomainOperationExposePage() {
            this.showDomainOperationExpose = false;
        },
        finishDomainOperationExpose(data) {
            this.domainOperationExpose = null;
            this.showDomainOperationExpose = false;
        },
        
    
        manageBusinessDomainCreateBack() {
            showManagedObjectCreate1.value = true;
            this.showBusinessDomainCreate = false;
        },
        
        
        hideBusinessDomainCreatePage() {
        	this.showBusinessDomainCreate = false;
        },
        updateBusinessDomainToList(bd) {
        	var businessDomains = businessSystemInfo.value.businessDomains;
            
            businessDomains.push({
                businessDomain: bd
            });
            businessDomains.sort((a,b)=> {
                if(a.name > b.name) {
                    return 1;
                }else if(a.name == b.name) {
                    return 0;
                }else {
                    return -1;
                }
            });
            this.showBusinessDomainCreate = false;
        },
        reloadSystemCache() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/cache/clusterReload/" + businessSystemInfo.value.businessSystem.id;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null && data.data == true) {
                    var feUrl = "/lcdp-proxy/lowcode/platform/fe/api/cache/clusterReload/" + businessSystemInfo.value.businessSystem.id;
                    axiosClient.get(feUrl).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data == true) {
                            var biUrl = "/lcdp-proxy/lowcode/platform/bi/api/cache/clusterReload/" + businessSystemInfo.value.businessSystem.id;
                            axiosClient.get(biUrl).then((response) => {
                                var data = response.data; 
                                if(data != null && data.status == "200" && data.data != null && data.data == true) {
                                    ElMessage(`缓存刷新请求发送成功`);
                                }else {
                                    ElMessage.error(`BI领域缓存刷新失败`);
                                }
                            });
                        }else {
                            ElMessage.error(`前端领域缓存刷新失败`);
                        }
                    });
                }else {
                    ElMessage.error(`服务端领域缓存刷新失败`);
                }
            });
        }, 
        closeWorkbench(){
        	window.close();
        },
        viewExposedServices() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/exposedService/list/" + businessSystemInfo.value.businessSystem.id;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.exposedServicesForView = data.data;
                    this.showExposedServiceView = true;
                }
            });
         },
         hideExposedServiceViewPage(){
              this.exposedServicesForView = null;
              this.showExposedServiceView = false;
         },
         showBusinessDomainMgrPage() {
            this.showBusinessDomainMgr = true;
         },
         hideBusinessDomainMgrPage(){
              this.showBusinessDomainMgr = false;
         }
    }
    
 }
</script>

<style scoped>
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}


.layout-container-main .el-header {
  position: relative;
  background-image: linear-gradient(to right, rgba(97, 116, 209, 1), rgba(90, 156, 248, 1));
  background-size: cover; 
  color:#ffffff
}
.layout-container-main .el-aside {
  color: var(--el-text-color-primary);
  background: #ffffff;
}
.layout-container-main .el-menu {
  border-right: none;
  background: #ffffff;
}
.layout-container-main .el-menu .el-menu-item {
  height:38px;
  line-height:38px;
}
.layout-container-main .el-main {
  padding: 0;
}

.layout-container-main .toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
  height: 100%;
  right: 20px;
}

.layout-container-main .toolbar {
  display: inline-flex;
  align-items: center;
  justify-content: center; 
  height: 100%;
  right: 20px;
}

.layout-container-main .toolbar-right {
  display: inline-flex;
  align-items: center;
  justify-content: right; 
  height: 100%;
  right: 20px;
}

.layout-container-main .toolbar2 {
  display: inline-flex;
  align-items: center;
  justify-content: center; 
  height: 100%;
  right: 20px;
}
</style>