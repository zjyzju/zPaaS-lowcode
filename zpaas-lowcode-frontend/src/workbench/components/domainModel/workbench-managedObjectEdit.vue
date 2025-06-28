<!-- 管理对象编辑 -->
<template>
<el-form v-if="managedObject != null && managedObject.managedObject != null" :model="managedObject" ref="managedObjectForm" :rules="validateRules" label-width="120px">
    <el-row :gutter="4">
	   <el-col :span="24">
	       <el-breadcrumb separator=">">
	           <el-breadcrumb-item v-if="businessDomainName != null"><span class="title">{{businessDomainName}}</span></el-breadcrumb-item>
	           <el-breadcrumb-item v-else>&nbsp;&nbsp;{{managedObject.managedObject.domainId}}</el-breadcrumb-item>

               <el-breadcrumb-item>业务对象</el-breadcrumb-item>
	           
	           <el-breadcrumb-item v-if="objectTypeName != null">{{objectTypeName}}</el-breadcrumb-item>
	           <el-breadcrumb-item v-else>{{managedObject.objectType}}</el-breadcrumb-item>
	           
	           <el-breadcrumb-item><span class="title1">{{managedObject.managedObject.code}}-{{managedObject.managedObject.name}}</span></el-breadcrumb-item>
	        </el-breadcrumb>
	   </el-col>
	</el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="标识" required prop="managedObject.id">
                <el-input v-model="managedObject.managedObject.id" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="编码" required prop="managedObject.code">
                <el-input v-model="managedObject.managedObject.code" size="small"/>
                <el-input type="hidden" v-model="managedObject.managedObject.status" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="名称"  required prop="managedObject.name">
                <el-input v-model="managedObject.managedObject.name" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="对象类型"  required prop="objectType">
                <el-input type="hidden" v-model="managedObject.objectType" size="small" />
                <el-input v-model="objectTypeName" readonly size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="归属业务域"  required prop="managedObject.domainId">
                <el-input type="hidden" v-model="managedObject.managedObject.domainId" size="small" />
                <el-input v-model="businessDomainName" readonly size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="描述">
                <el-input v-model="managedObject.managedObject.description" size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="20" class="toolbar-right">
            <el-button v-if="managedObject.objectType == 'D' || managedObject.objectType == 'V'" type="primary" size="small" @click="loadFileObjectMappings(managedObject.objectType, managedObject.managedObject.id)">查看文件对象映射信息</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button v-if="managedObject.objectType == 'D' || managedObject.objectType == 'V'" type="primary" size="small" @click="showRuleGroupView(managedObject.objectType, managedObject.managedObject.id)">查看校验规则组信息</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button v-if="managedObject.objectType == 'D' || managedObject.objectType == 'V'" type="primary" size="small" @click="loadDataMappings(managedObject.objectType, managedObject.managedObject.id)">查看数据映射信息</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button v-if="managedObject.objectType == 'D'" type="primary" size="small" @click="loadObjectRelationMappingsbyDomainObjectId(managedObject.managedObject.id)">查看ORM信息</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="saveManagedObject()">保存</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="deleteManagedObject(managedObject.objectType, managedObject.managedObject.id, managedObject.managedObject.code)">删除</el-button>
        </el-col>
        <el-col :span="4" class="toolbar-right">&nbsp;</el-col>
    </el-row>
    
    <!-- 管理对象属性信息 -->
    <br/>
    <el-row :gutter="4" v-if="managedObject.objectType == 'D' || managedObject.objectType == 'V'">
        <el-col :span="1"></el-col>
        <el-col :span="23">
            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 6px">
            <div class="toolbar1">
                <span class="title">对象属性</span>&nbsp;&nbsp;&nbsp;&nbsp;
                <el-link @click="showAttributeCreatePage()"  type="primary" >新建</el-link>
            </div>
            </el-divider>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="managedObject.objectType == 'D' || managedObject.objectType == 'V'">
        <el-col :span="1"/>
        <el-col :span="23">
            <el-table :data="managedObject.attributes" stripe style="width: 100%">
                <el-table-column  label="操作" width="100">
                    <template #default="scope">
                        <el-link @click="showAttributeEditPage(scope.row.id)"  type="primary">编辑</el-link>&nbsp;
                        <el-link  type="primary"  @click="deleteAttribute(scope.row.id, scope.row.code)">删除</el-link>
                    </template>
                </el-table-column>
                    <el-table-column prop="code" label="编码" width="230" />
                    <el-table-column prop="name" label="名称" width="230" />
                    <el-table-column prop="isListAttr" label="是否列表" width="80" >
                         <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.isListAttr == 'Y'">是</span>
                              <span v-if="scope.row.isListAttr == 'N'">否</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="attrType" label="属性类型" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span v-if="scope.row.attrType == 'J'">Java原生类型({{scope.row.attrClass}})</span>
                                <span v-if="scope.row.attrType == 'D'">领域对象({{scope.row.attrClassObject.code}})</span>
                                <span v-if="scope.row.attrType == 'V'">值传递对象({{scope.row.attrClassObject.code}})</span>
                            </div>
                        </template>
                    </el-table-column>
                    
                </el-table>
        </el-col>
    </el-row>
    
    <br/><br/>
    <!-- 管理对象方法信息 -->
    <el-row :gutter="4" v-if="managedObject.objectType == 'D' || managedObject.objectType == 'S'">
        <el-col :span="1"></el-col>
        <el-col :span="23">
            <el-divider content-position="left" style="margin-top: 1px;margin-bottom: 6px">
            <div class="toolbar1">
                <span class="title">对象方法</span>&nbsp;&nbsp;&nbsp;&nbsp;
                <el-link  type="primary" @click="showOperationCreatePage()">新建</el-link>
            </div>
            </el-divider>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="managedObject.objectType == 'D' || managedObject.objectType == 'S'">
        <el-col :span="1"/>
        <el-col :span="23">
            <el-table :data="managedObject.operations" stripe style="width: 100%">
                <el-table-column  label="编辑" width="220" >
                    <template #default="scope">
                        <el-link  type="primary"  @click="editOperation(scope.row.id)">编辑</el-link>&nbsp;
                        <el-link  type="primary"  @click="deleteOperation(scope.row.id, scope.row.code)">删除</el-link>&nbsp;
                        <el-link  type="primary"  @click="editBusinessFlow(scope.row.id, scope.row.businessFlowId)">逻辑编排</el-link>&nbsp;
                        
                        <el-link  type="primary" v-if="managedObject.objectType=='S' && managedObject.exposedServiceMap!=null && managedObject.exposedServiceMap[scope.row.id] != null"  @click="showCancelExposeOperationPage(scope.row.id, scope.row.code)">取消发布</el-link>
                        <el-link  type="primary" v-else-if="managedObject.objectType=='S'"  @click="showExposeOperationPage(scope.row.id, scope.row.code)">发布</el-link>
                    </template>
                </el-table-column>
                <el-table-column prop="code" label="编码" width="230" />
                <el-table-column prop="name" label="名称" width="230" />                           
                <el-table-column prop="accessPrivilege" label="访问权限" width="80">
                     <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.accessPrivilege == '1'">公开</span>
                              <span v-if="scope.row.accessPrivilege == '0'">私有</span>
                            </div>
                      </template>
                </el-table-column>    
                <el-table-column prop="businessFlowId" label="业务流标识" />                        
            </el-table>
        </el-col>
    </el-row>
</el-form>

<!-- 编辑方法 -->
<editOperation v-if="showOperationEdit==true && operationInfo != null" @hideEditOperationPage="hideEditOperationPage" @updateOperationToList="updateOperationToList" :operationInfo="operationInfo" :showOperationEdit="showOperationEdit"/>

<!-- 新建方法 -->
<createOperation v-if="showOperationCreate==true && newOperation != null" @addNewOperationToList="addNewOperationToList" @hideOperationCreatePage="hideOperationCreatePage" :newOperation="newOperation" :showOperationCreate="showOperationCreate"/>

<!-- 编辑属性 -->
<editAttribute v-if="showAttributeEdit==true && currentAttribute != null" @hideAttributeEditPage="hideAttributeEditPage" @updateAttributeToList="updateAttributeToList" :currentAttribute="currentAttribute" :showAttributeEdit="showAttributeEdit"/>

<!-- 新建属性 -->
<createAttribute v-if="showAttributeCreate==true && newAttribute != null" @hideAttributeCreatePage="hideAttributeCreatePage" @addNewAttributeToList="addNewAttributeToList" :newAttribute="newAttribute" :showAttributeCreate="showAttributeCreate"/>

<!-- 编辑业务流 -->
<editBusinessFlow v-if="showBusinessFlowEdit==true && businessFlowInfo != null" @hideBusinessFlowEditPage="hideBusinessFlowEditPage" :businessFlowInfo="businessFlowInfo" :showBusinessFlowEdit="showBusinessFlowEdit"/>

<!-- 发布服务方法 -->
<exposeOperation v-if="showExposeOperation==true && exposedOperation != null" @hideExposeOperationPage="hideExposeOperationPage" @updateExposedOperationToList="updateExposedOperationToList" :showExposeOperation="showExposeOperation"  :exposedOperation="exposedOperation"/>

<!-- 取消发布服务方法 -->
<cancelExposeOperation v-if="showCancelExposeOperation==true && exposedOperation != null" @hideCancelExposeOperationPage="hideCancelExposeOperationPage" @updateCancelExposeOperationToList="updateCancelExposeOperationToList" :showCancelExposeOperation="showCancelExposeOperation"  :exposedOperation="exposedOperation"/>

<!-- 编辑对象关系映射信息 -->
<editObjectRelationMapping v-if="showObjectRelationMappingEdit==true && objectRelationMappings != null"  @hideEditObjectRelationMappingPage="hideEditObjectRelationMappingPage"  :showObjectRelationMappingEdit="showObjectRelationMappingEdit"  :objectRelationMappings="objectRelationMappings" :orType="orType" :orId="orId" :tenantId="managedObject.managedObject.tenantId" :systemId="managedObject.managedObject.systemId"/>

<!-- 编辑数据映射信息 -->
<editDataMapping v-if="showDataMappingEdit==true && dataMappings != null"  @hideEditDataMappingPage="hideEditDataMappingPage"  :showDataMappingEdit="showDataMappingEdit"  :dataMappings="dataMappings" :objectType="objectTypeOfDataMapping" :objectId="objectIdOfDataMapping" :tenantId="managedObject.managedObject.tenantId" :systemId="managedObject.managedObject.systemId"/>

<!-- 查看校验规则组信息 -->
<ruleGroupList v-if="showRuleGroupSelect"  @hideRuleGroupSelectPage="hideRuleGroupSelectPage" :showRuleGroupSelect="showRuleGroupSelect"  :ruleGroupsForSelect="ruleGroupsForSelect" :systemId="managedObject.managedObject.systemId" :objectType="managedObject.objectType" :objectId="managedObject.managedObject.id" :tenantId="managedObject.managedObject.tenantId"/>

<!-- 编辑数据映射信息 -->
<fileObjectMappingEdit v-if="showFileObjectMappingEdit==true && fileObjectMappings != null"  @hideEditFileObjectMappingPage="hideEditFileObjectMappingPage"  :showFileObjectMappingEdit="showFileObjectMappingEdit"  :fileObjectMappings="fileObjectMappings" :objectType="objectTypeOfDataMapping" :objectId="objectIdOfDataMapping" :tenantId="managedObject.managedObject.tenantId" :systemId="managedObject.managedObject.systemId"/>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref, defineAsyncComponent } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'

// import editOperation from './workbench-operationEdit.vue'
// import createOperation from './workbench-operationCreate.vue'
// import createAttribute from './workbench-attributeCreate.vue'
// import editAttribute from './workbench-attributeEdit.vue'
// import editBusinessFlow from '../businessFlow/workbench-businessFlowEdit.vue'
// import exposeOperation from '../exposeMgr/workbench-operationExpose.vue'
// import cancelExposeOperation from '../exposeMgr/workbench-operationCancelExpose.vue'
// import editObjectRelationMapping from './workbench-objectRelationMappingEdit.vue'
// import editDataMapping from './workbench-dataMappingEdit.vue'
// import ruleGroupList from './workbench-ruleGroupList.vue'
// import fileObjectMappingEdit from './workbench-fileObjectMappingEdit.vue'

const editOperation = defineAsyncComponent(() => import('./workbench-operationEdit.vue'));
const createOperation = defineAsyncComponent(() => import('./workbench-operationCreate.vue'));
const createAttribute = defineAsyncComponent(() => import('./workbench-attributeCreate.vue'));
const editAttribute = defineAsyncComponent(() => import('./workbench-attributeEdit.vue'));
const editBusinessFlow = defineAsyncComponent(() => import('../businessFlow/workbench-businessFlowEdit.vue'));
const exposeOperation = defineAsyncComponent(() => import('../exposeMgr/workbench-operationExpose.vue'));
const cancelExposeOperation = defineAsyncComponent(() => import('../exposeMgr/workbench-operationCancelExpose.vue'));
const editObjectRelationMapping = defineAsyncComponent(() => import('./workbench-objectRelationMappingEdit.vue'));
const editDataMapping = defineAsyncComponent(() => import('./workbench-dataMappingEdit.vue'));
const ruleGroupList = defineAsyncComponent(() => import('./workbench-ruleGroupList.vue'));
const fileObjectMappingEdit = defineAsyncComponent(() => import('./workbench-fileObjectMappingEdit.vue'));

export default {
    props: [],
    
    emits: ['deleteFromList','updateToList'],
    
    setup(props, {attrs, emit, slots}) {
        const deleteManagedObjectFromList = (system) => {
            emit('deleteFromList', 'managedObject', system);
        };

        const updateManagedObjectToList = (system) => {
            emit('updateToList', 'managedObject', system);
        };
        
        return {
        	deleteManagedObjectFromList,
            updateManagedObjectToList
        }
    },
    
    components: {
    	editOperation,
        createOperation,
        createAttribute,
        editAttribute,
        editBusinessFlow,
        exposeOperation,
        cancelExposeOperation,
        editObjectRelationMapping,
        editDataMapping,
        ruleGroupList,
        fileObjectMappingEdit
    },
    
    data() {
        const router = useRoute();
        const managedObject = ref(null);
        const objectTypeName = ref(null);
        const businessDomainName = ref(null);

    	const operationInfo = ref({});
        const showOperationEdit = ref(false);
        
        const showOperationCreate = ref(false);
        const newOperation = ref({});
        
        const showAttributeCreate = ref(false);
        const newAttribute = ref({});
        
        const showAttributeEdit = ref(false);
        const currentAttribute = ref({});
        
        const businessFlowInfo = ref({});
        const showBusinessFlowEdit = ref(false);
        
        const exposedOperation = ref({});
        const showExposeOperation = ref(false);
        const showCancelExposeOperation = ref(false);
        
        const showObjectRelationMappingEdit = ref(false);
        const objectRelationMappings = ref([]);
       
        const showDataMappingEdit = ref(false);
        const dataMappings = ref([]);
        const objectTypeOfDataMapping = ref("");
        const objectIdOfDataMapping = ref("");

        const showFileObjectMappingEdit = ref(false);
        const fileObjectMappings = ref([]);
        
        const orType = ref("");
        const orId = ref("");
    	
        const showRuleGroupSelect = ref(false);
        const ruleGroupsForSelect = ref(null);

        const validateRules = ref({
            "managedObject.id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "managedObject.code": [
                { required: true, message: '编码不能为空！', trigger: 'blur' }
            ],
            "managedObject.name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "objectType": [
                { required: true, message: '对象类型不能为空！', trigger: 'blur' }
            ],
            "managedObject.domainId": [
                { required: true, message: '归属业务域不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
            router,
            managedObject,
            objectTypeName,
            businessDomainName,

        	operationInfo,
            showOperationEdit,           
            showOperationCreate,
            newOperation,
            showAttributeCreate,
            newAttribute,
            showAttributeEdit,
            currentAttribute,
            showBusinessFlowEdit,
            businessFlowInfo,
            showExposeOperation,
            exposedOperation,
            showCancelExposeOperation,
            
            showObjectRelationMappingEdit,
            objectRelationMappings,
            showDataMappingEdit,
            dataMappings,
            orType,
            orId,
            objectTypeOfDataMapping,
            objectIdOfDataMapping,
            
            showRuleGroupSelect,
            ruleGroupsForSelect,

            showFileObjectMappingEdit,
            fileObjectMappings,

            validateRules
        }
    },
    watch: {
        'router.query': function(val, old){
            if(val != null && val.objectId != null && (old == null || val.objectId != old.objectId)) {
                setTimeout(()=>{
                    this.loadManagedObject(val);
                },100);
            }else if(val == null || val.objectId == null) {
                this.managedObject = null;
            }
        }
    },
    mounted() {
        this.loadManagedObject(this.router.query);
    },
    methods: {
        loadManagedObject(param) {
            this.objectTypeName = param.objectTypeName;
            var type = param.objectType;
            var objectId = param.objectId;
            if(objectId == null) {
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/managedObject/loadInfo/" + type + "/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    if(data.data.attributes != null) {
                        data.data.attributes.sort((a,b)=>{return a.code > b.code});
                    }
                    if(data.data.operations != null) {
                        data.data.operations.sort((a,b)=>{return a.code > b.code});   
                    }

                    var url = "/lcdp-proxy/lowcode/platform/be/api/businessDomain/byId/" + data.data.managedObject.domainId;
                    axiosClient.get(url).then((response) => {
                        var data1 = response.data; 
                        if(data1 != null && data1.status == "200" && data1.data != null) {
                            this.businessDomainName = data1.data.name;
                        }
                    });
                    
                    this.managedObject = data.data;
                }
            });
        },
    	loadDataMappings(objectType, objectId) {
            var url = "/lcdp-proxy/lowcode/platform/be/api/dataMapping/listByObject/" + objectType + "/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataMappings = data.data;
                    this.objectTypeOfDataMapping = objectType;
                    this.objectIdOfDataMapping = objectId;
                    this.showDataMappingEdit = true;
                }
            });
        },
        hideEditDataMappingPage() {
            this.showDataMappingEdit = false;
        },
        loadFileObjectMappings(objectType, objectId) {
            var url = "/lcdp-proxy/lowcode/platform/be/api/fileObjectMapping/listByObject/" + objectType + "/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.fileObjectMappings = data.data;
                    this.objectTypeOfDataMapping = objectType;
                    this.objectIdOfDataMapping = objectId;
                    this.showFileObjectMappingEdit = true;
                }
            });
        },
        hideEditFileObjectMappingPage() {
            this.showFileObjectMappingEdit = false;
        },
        loadObjectRelationMappingsbyDomainObjectId(domainObjectId) {
            var url = "/lcdp-proxy/lowcode/platform/be/api/objectRelationMapping/listByDomainObjectId/" + domainObjectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.objectRelationMappings = data.data;
                    this.orType = "O";
                    this.orId = domainObjectId;
                    this.showObjectRelationMappingEdit = true;
                }
            });
        },
        hideEditObjectRelationMappingPage() {
            this.showObjectRelationMappingEdit = false;
        },
        saveManagedObject() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/managedObject/save";
            
            var objectSubType = "";
            var objectContainerType = "";
            if(this.managedObject.objectType == "S") {
                objectSubType = this.managedObject.managedObject.serviceType;
            }else if(this.managedObject.objectType == "D") {
                objectSubType = this.managedObject.managedObject.objectType;
                objectContainerType = this.managedObject.managedObject.objectContainerType;
            }else if(this.managedObject.objectType == "V") {
                objectContainerType = this.managedObject.managedObject.objectContainerType;
            }
            var updateManagedObject = {
                    id : this.managedObject.managedObject.id,
                    code : this.managedObject.managedObject.code,
                    name : this.managedObject.managedObject.name,
                    objectType : this.managedObject.objectType,
                    objectSubType : objectSubType,
                    businessDomainId : this.managedObject.managedObject.domainId,
                    objectContainerType : objectContainerType,
                    description : this.managedObject.managedObject.description,
                    systemId : this.managedObject.managedObject.systemId,
                    tenantId : this.managedObject.managedObject.tenantId,
                    status : this.managedObject.managedObject.status,
                    createTime : this.managedObject.managedObject.createTime,
                    updateTime : this.managedObject.managedObject.updateTime
                };
            
            this.$refs.managedObjectForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,updateManagedObject).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                            ElMessage(`更新对象信息成功，共更新(`+data.data+`)条属性`);
                            this.updateManagedObjectToList(this.managedObject);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
        },
        deleteManagedObject(objectType, objectId, objectCode) {
            ElMessageBox.confirm(
                '该对象(' + objectCode + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/be/api/managedObject/delete/" + objectType + "/" + objectId;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         ElMessage(`删除对象(`+ objectCode +`)成功。`);
                         this.deleteManagedObjectFromList(this.managedObject);
                     }
                 }).catch(()=>{
                     ElMessage.error(`删除对象(`+ objectCode +`)失败！`);
                 });
             }).catch(()=>{});
        },
        showAttributeCreatePage() {
            this.newAttribute = {
                id:"",
                code:"",
                name:"",
                attrType:"J",
                attrClass:"",
                isListAttr:"N",
                objectType: this.managedObject.objectType,
                objectId: this.managedObject.managedObject.id,
                systemId: this.managedObject.managedObject.systemId,
                tenantId: this.managedObject.managedObject.tenantId,
                attrClassObject: {}
            };
            this.showAttributeCreate = true;
        },
        addNewAttributeToList(newAttr) {
            var attrList = this.managedObject.attributes;
            if(attrList == null) {
                attrList = [];
                this.managedObject.attributes = attrList;
            }
            attrList.push(newAttr);
            this.managedObject.attributes.sort((a,b)=>{return a.code > b.code});
            this.showAttributeCreate = false;
        },
        hideAttributeCreatePage() {
            this.showAttributeCreate = false;
        },
        showAttributeEditPage(attrId) {
             this.managedObject.attributes.forEach((item)=> {
                if(item.id == attrId) {
                    this.currentAttribute = {
                            id:item.id,
                            code:item.code,
                            name:item.name,
                            attrType:item.attrType,
                            attrClass:item.attrClass,
                            isListAttr:item.isListAttr,
                            objectType: item.objectType,
                            objectId: item.objectId,
                            systemId: item.systemId,
                            tenantId: item.tenantId,
                            attrClassObject: item.attrClassObject
                        };
                    this.showAttributeEdit = true;
                    return;
                }
            });       
        },
        hideAttributeEditPage() {
            this.showAttributeEdit = false;
        },
        updateAttributeToList(updateAttr) {
            this.managedObject.attributes.forEach((item)=> {
                if(item.id == updateAttr.id) {
                   item.code = updateAttr.code;
                   item.name = updateAttr.name;
                   item.attrType = updateAttr.attrType;
                   item.attrClass = updateAttr.attrClass;
                   item.isListAttr = updateAttr.isListAttr;
                   item.objectType = updateAttr.objectType;
                   item.objectId = updateAttr.objectId;
                   item.systemId = updateAttr.systemId;
                   item.tenantId = updateAttr.tenantId;
                   item.attrClassObject = updateAttr.attrClassObject;
                   this.showAttributeEdit = false;
                   this.managedObject.attributes.sort((a,b)=>{return a.code > b.code});
                   return;
                }
            });
            
        },
        deleteAttribute(attrId, attrCode) {
            ElMessageBox.confirm(
                    '该属性(' + attrCode + ')将被删除. 是否继续?',
                    '警告',
                    {
                      confirmButtonText: '确定',
                      cancelButtonText: '取消',
                      type: 'warning',
                    }
                  ).then(() => {
                      var url = "/lcdp-proxy/lowcode/platform/be/api/attribute/delete/" + attrId;
                      axiosClient.post(url).then((response) => {
                          var data = response.data; 
                          if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                              if(this.managedObject.attributes != null && this.managedObject.attributes.length > 0) {
                                  for(var i in  this.managedObject.attributes) {
                                      if( this.managedObject.attributes[i].id == attrId) {
                                          this.managedObject.attributes.splice(i,1);
                                          break;
                                      }
                                  }
                                  
                              }
                              ElMessage(`删除方法(`+attrCode+`)成功。`);
                          }
                      }).catch(()=>{
                          ElMessage.error(`删除方法(`+attrCode+`)失败！`);
                      });
                  }).catch(()=>{
                  });
            
        },
        editOperation(operationId) {
            var url = "/lcdp-proxy/lowcode/platform/be/api/operation/loadInfo/" + operationId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    data.data.operation.outParams = [];
                    data.data.operation.outParams.push(data.data.operation.outParam);
                    this.operationInfo = data.data;
                    this.showOperationEdit = true;
                }
            });
        },
        deleteOperation(operationId, operationCode) {
            ElMessageBox.confirm(
                    '该方法(' + operationCode + ')将被删除. 是否继续?',
                    '警告',
                    {
                      confirmButtonText: '确定',
                      cancelButtonText: '取消',
                      type: 'warning',
                    }
                  ).then(() => {
                      var url = "/lcdp-proxy/lowcode/platform/be/api/operation/delete/" + operationId;
                      axiosClient.post(url).then((response) => {
                          var data = response.data; 
                          if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                              if(this.managedObject.operations != null && this.managedObject.operations.length > 0) {
                                  for(var i in  this.managedObject.operations) {
                                      if( this.managedObject.operations[i].id == operationId) {
                                          this.managedObject.operations.splice(i,1);
                                          break;
                                      }
                                  }
                                  
                              }
                              ElMessage(`删除方法(`+operationCode+`)成功。`);
                          }
                      }).catch(()=>{
                          ElMessage.error(`删除方法(`+operationCode+`)失败！`);
                      });
                  }).catch(()=>{
                  });
            
        },
        hideEditOperationPage() {
            this.showOperationEdit = false
        },
        showOperationCreatePage() {
            this.newOperation = {
                id:"",
                code:"",
                name:"",
                accessPrivilege:"1",
                businessFlowId:"",
                transactionRequired:"N",
                flowType:"C",
                dbSchemaId:"",
                displayOrder: null,
                objectType: this.managedObject.objectType,
                objectId: this.managedObject.managedObject.id,
                systemId: this.managedObject.managedObject.systemId,
                tenantId: this.managedObject.managedObject.tenantId
            };
            this.showOperationCreate = true;
        },
        addNewOperationToList(newOperation) {
            var operationList = this.managedObject.operations;
            if(operationList == null) {
                operationList = [];
                this.managedObject.operations = operationList;
            }
            operationList.push(newOperation);
            this.managedObject.operations.sort((a,b)=>{return a.code > b.code});
            this.showOperationCreate = false;
        },
        updateOperationToList(updateOperation) {
            this.managedObject.operations.forEach((item)=> {
                if(item.id == updateOperation.id) {
                   item.code = updateOperation.code;
                   item.name = updateOperation.name;
                   item.businessFlowId = updateOperation.businessFlowId;
                   item.accessPrivilege = updateOperation.accessPrivilege;
                   item.status = updateOperation.status;
                   item.objectType = updateOperation.objectType;
                   item.objectId = updateOperation.objectId;
                   item.systemId = updateOperation.systemId;
                   item.tenantId = updateOperation.tenantId;
                   this.showOperationEdit = false;
                   this.managedObject.operations.sort((a,b)=>{return a.code > b.code});
                   return;
                }
            });
            
        },
        hideOperationCreatePage() {
            this.showOperationCreate = false;
        },
        hideBusinessFlowEditPage() {
            this.showBusinessFlowEdit = false
        },       
        editBusinessFlow(operationId, businessFlowId) {
            var url = "/lcdp-proxy/lowcode/platform/be/api/businessFlow/loadInfo/" + businessFlowId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null && data.data != "") {
                    this.businessFlowInfo = data.data;
                }else {
                    this.businessFlowInfo = {
                            businessFlow : {
                                id : businessFlowId,
                                name: "",
                                transactionRequired: "N",
                                dbSchemaId: "",
                                flowType: "C",
                                parentFlowId: "",
                                systemId: this.managedObject.managedObject.systemId,
                                tenantId: this.managedObject.managedObject.tenantId
                            },
                            businessFlowNodes : []
                    };
                }
                this.businessFlowInfo.ownerAttributes = this.managedObject.attributes;
                
                var loadOperationUrl = "/lcdp-proxy/lowcode/platform/be/api/operation/loadInfo/" + operationId;
                axiosClient.get(loadOperationUrl).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.businessFlowInfo.operationParams = data.data.parameters;
                        this.businessFlowInfo.operation = data.data.operation;
                        this.showBusinessFlowEdit = true;
                    }
                });
            });
        },
        showExposeOperationPage(operationId, operationCode) {
            this.exposedOperation = {
                id : "",
                serviceId : this.managedObject.managedObject.id,
                serviceCode : this.managedObject.managedObject.code,
                operationId : operationId,
                operationCode : operationCode,
                httpMethod : "POST",
                httpMapping : "",
                tenantId : this.managedObject.managedObject.tenantId,
                systemId : this.managedObject.managedObject.systemId,
                domainId : this.managedObject.managedObject.domainId,
                status : "",
                createTime : "",
                updateTime : ""
            }
            this.showExposeOperation = true;
        },
        updateExposedOperationToList(exposedOperation) {
            if(this.managedObject.exposedServiceMap == null) {
                this.managedObject.exposedServiceMap = {};
            }
            this.managedObject.exposedServiceMap[exposedOperation.operationId] = exposedOperation;
            this.showExposeOperation = false;
        },
        hideExposeOperationPage() {
            this.showExposeOperation = false;
        },
        showCancelExposeOperationPage(operationId, operationCode) {
            var exposedService = this.managedObject.exposedServiceMap[operationId];
            if(exposedService != null) {
                this.exposedOperation = {
                        id : exposedService.id,
                        serviceId : exposedService.serviceId,
                        serviceCode : this.managedObject.managedObject.code,
                        operationId : exposedService.operationId,
                        operationCode : operationCode,
                        httpMethod : exposedService.httpMethod,
                        httpMapping : exposedService.httpMapping,
                        tenantId : exposedService.tenantId,
                        systemId : exposedService.systemId,
                        domainId : exposedService.domainId,
                        status : exposedService.status,
                        createTime : exposedService.createTime,
                        updateTime : exposedService.updateTime
                    }
                this.showCancelExposeOperation = true;
            }else {
                ElMessage.error(`获取服务方法发布信息失败`);
            }       
        },
        updateCancelExposeOperationToList(exposed) {
            this.managedObject.exposedServiceMap[exposed.operationId] = null;
            this.showCancelExposeOperation = false; 
        },
        hideCancelExposeOperationPage() {
            this.showCancelExposeOperation = false;
        },
        showRuleGroupView(objectType, objectId) {
            if(objectType != "D" && objectType != "V") {
                ElMessage(`只有领域对象和值传递对象可以设置校验规则组！`);
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/validateRuleGroup/listByObject/" + this.managedObject.managedObject.systemId + "/" + objectType + "/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.ruleGroupsForSelect = data.data;
                    this.showRuleGroupSelect = true;
                }
            });
        },
        hideRuleGroupSelectPage(){
             this.ruleGroupsForSelect = null;
             this.showRuleGroupSelect = false;
        },
    }
}

</script>
<style scoped>
.layout-container-main .toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
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

</style>