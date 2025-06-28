<!-- 编辑系统功能 -->
<template>
<el-form  v-if="funcDefine != null && funcDefine.id != null" :model="funcDefine" label-width="120px" :rules="validateRules" ref="funcDefineForm">
    <el-row :gutter="4">
       <el-col :span="24">
           <el-breadcrumb separator=">">
               <el-breadcrumb-item><span class="title">系统功能</span></el-breadcrumb-item>
               <el-breadcrumb-item>系统功能</el-breadcrumb-item>
               <el-breadcrumb-item ><span class="title1">{{funcDefine.name}}</span></el-breadcrumb-item>
            </el-breadcrumb>
       </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="标识" required prop="id">
                <el-input v-model="funcDefine.id" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="名称" required prop="name">
                <el-input v-model="funcDefine.name" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="描述">
                <el-input v-model="funcDefine.description" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="归属业务域"  required prop="domainId">
                <el-input type="hidden" v-model="funcDefine.domainId" size="small" />
                <el-input v-model="businessDomainName" readonly size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="功能模板" required prop="templateId">
                <el-input v-model="funcDefine.funcTemplate.name" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="16">
            &nbsp;
        </el-col>
    </el-row>
    
    <el-row :gutter="4">
        <el-col :span="20" class="toolbar-right">
            <el-link :href="funcDefine.funcTemplate.url+'?funcId='+funcDefine.id" v-if="funcDefine.funcTemplate.isMainFunc == 'Y' " type="primary" target="_blank">预览</el-link>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="saveFuncDefine()">保存</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="deleteFuncDefine()">删除</el-button>
        </el-col>
        <el-col :span="4" class="toolbar-right">&nbsp;</el-col>
    </el-row>
    
    <!-- 绑定对象信息 -->
    <br/>
    <el-row :gutter="4" v-if="funcDefine != null && funcDefine.id != null">
        <el-col :span="1"></el-col>
        <el-col :span="23">
            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
            <div class="toolbar1">
                <span class="title">绑定对象</span>&nbsp;&nbsp;&nbsp;&nbsp;
                <el-link @click="showObjectAssignCreatePage()"  type="primary" >新建</el-link>
            </div>
            </el-divider>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="funcDefine != null && funcDefine.id != null">
        <el-col :span="1"/>
        <el-col :span="23">
            <el-table :data="funcDefine.objectAssigns" stripe style="width: 100%">
                <el-table-column  label="操作" width="110">
                    <template #default="scope">
                        <el-link @click="showObjectAssignEditPage(scope.row.id)"  type="primary">编辑</el-link>&nbsp;
                        <el-link  type="primary"  @click="deleteObjectAssign(scope.row.id)">删除</el-link>
                    </template>
                </el-table-column>
                    <el-table-column prop="id" label="对象绑定标识" width="280" />
                    <el-table-column prop="objectType" label="绑定对象类型" width="110" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.objectType == 'D'">领域对象</span>
                              <span v-if="scope.row.objectType == 'V'">值传递对象</span>
                              <span v-if="scope.row.objectType == 'B'">BI数据集</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="objectId" label="绑定对象" width="180" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.assignObject != null && scope.row.objectType != 'B'">{{scope.row.assignObject.code}}</span>
                              <span v-else-if="scope.row.biDataSet != null && scope.row.objectType == 'B'">{{scope.row.biDataSet.code}}</span>
                              <span v-else>{{scope.row.objectId}}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="assignType" label="绑定类型" width="90" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.assignType == 'M'">主对象</span>
                              <span v-if="scope.row.assignType == 'S'">从对象</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="keyAttrId" label="关键属性"  width="180">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.keyAttribute != null">{{scope.row.keyAttribute.code}}</span>
                              <span v-if="scope.row.keyAttribute == null">{{scope.row.keyAttrId}}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="relAttrId" label="关联属性"  width="180">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.relAttribute != null">{{scope.row.relAttribute.code}}</span>
                              <span v-if="scope.row.relAttribute == null">{{scope.row.relAttrId}}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="mainRelAttrId" label="主对象关联属性"   width="180">
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.mainRelAttribute != null">{{scope.row.mainRelAttribute.code}}</span>
                              <span v-if="scope.row.mainRelAttribute == null">{{scope.row.mainRelAttrId}}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="displayOrder" label="显示顺序"/>
            </el-table>
        </el-col>
    </el-row>
    
    <!-- 功能区域信息 -->
    <br/><br/><br/>
    <el-row :gutter="4" v-if="funcDefine != null && funcDefine.id != null && funcDefine.funcTemplate.templateType != 'C'">
        <el-col :span="1"></el-col>
        <el-col :span="23">
            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
            <div class="toolbar1">
                <span class="title">功能区域</span>&nbsp;&nbsp;&nbsp;&nbsp;
            </div>
            </el-divider>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="funcDefine != null && funcDefine.id != null && funcDefine.funcTemplate.templateType != 'C'">
        <el-col :span="1"/>
        <el-col :span="23">
            <el-table :data="funcDefine.funcRegions" stripe style="width: 100%">
                <el-table-column  label="操作" width="135">
                    <template #default="scope">
                        <el-link @click="showFuncRegionEditPage(scope.row.id, false)"  type="primary">编辑</el-link>&nbsp;
                        <el-link @click="showFuncRegionEditPage(scope.row.id, true)"  type="primary">可视化设计</el-link>
                    </template>
                </el-table-column>
                    <el-table-column prop="id" label="区域标识" width="280" />
                    <el-table-column prop="name" label="区域名称" width="180" />
                    <el-table-column prop="tplRegionId" label="区域模板" width="220" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.tplRegion != null">{{scope.row.tplRegionId}}({{scope.row.tplRegion.name}})</span>
                              <span v-if="scope.row.tplRegion == null">{{scope.row.tplRegionId}}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="创建时间" width="190" />
                    <el-table-column prop="updateTime" label="更新时间" width="190"  />
            </el-table>
        </el-col>
    </el-row>
    <!-- 功能自定义页面信息 -->
    <el-row :gutter="4" v-if="funcDefine != null && funcDefine.id != null && funcDefine.funcTemplate.templateType == 'C'">
        <el-col :span="1"></el-col>
        <el-col :span="23">
            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
            <div class="toolbar1">
                <span class="title">功能自定义页面</span>&nbsp;&nbsp;&nbsp;&nbsp;
                <el-link @click="showCustomizedPageCreatePage()"  type="primary" >新建从页面</el-link>
            </div>
            </el-divider>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="funcDefine != null && funcDefine.id != null && funcDefine.funcTemplate.templateType == 'C'">
        <el-col :span="1"/>
        <el-col :span="23">
            <el-table :data="funcDefine.customizedPages" stripe style="width: 100%">
                <el-table-column  label="操作" width="130">
                    <template #default="scope">
                        <el-link @click="showFuncRegionEditPage(scope.row.id, false)"  type="primary">编辑</el-link>&nbsp;
                        <el-link @click="showFuncRegionEditPage(scope.row.id, true)"  type="primary">可视化设计</el-link>
                    </template>
                </el-table-column>
                    <el-table-column prop="id" label="页面标识" width="251" />
                    <el-table-column prop="name" label="页面名称" width="180" />
                    <el-table-column prop="isMainPage" label="是否主页面" width="220" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.isMainPage == 'Y'">是</span>
                              <span v-else-if="scope.row.isMainPage == 'N'">否</span>
                              <span v-else>{{ scope.row.isMainPage }}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="createTime" label="创建时间" width="180" />
                    <el-table-column prop="updateTime" label="更新时间" width="180"  />
            </el-table>
        </el-col>
    </el-row>
</el-form>

<!-- 新建对象绑定信息 -->
<createObjectAssign v-if="showObjectAssignCreate==true && newObjectAssign != null" :showObjectAssignCreate="showObjectAssignCreate" :newObjectAssign="newObjectAssign" :mainObjectAssign="mainObjectAssign" :templateType="funcDefine.funcTemplate.templateType" :isCreate="true" @updateNewObjectAssignToList="updateNewObjectAssignToList" @hideObjectAssignCreatePage="hideObjectAssignCreatePage"/>

<!-- 编辑对象绑定信息 -->
<editObjectAssign v-if="showObjectAssignEdit==true && objectAssign != null" :showObjectAssignEdit="showObjectAssignEdit" :objectAssign="objectAssign" :mainObjectAssign="mainObjectAssign" :objectAssigns="funcDefine.objectAssigns" @updateObjectAssignToList="updateObjectAssignToList" @hideObjectAssignEditPage="hideObjectAssignEditPage"/>

<!-- 编辑功能区域信息 -->
<editFuncRegion v-if="showFuncRegionEdit==true && funcRegion != null" :showFuncRegionEdit="showFuncRegionEdit" :funcRegion="funcRegion"  @updateFuncRegionToList="updateFuncRegionToList" @hideFuncRegionEditPage="hideFuncRegionEditPage"/>

<!-- 编辑自定义页面信息 -->
<editFuncCustomizedPage v-if="showCustomizedPageEdit==true && customizedPage != null" :showCustomizedPageEdit="showCustomizedPageEdit" :customizedPage="customizedPage" @updateCustomizedPageToList="updateCustomizedPageToList" @hideCustomizedPageEditPage="hideCustomizedPageEditPage"/>
<!-- 编辑自定义页面信息 -->
<createFuncCustomizedPage v-if="showCustomizedPageCreate==true && newCustomizedPage != null" :showCustomizedPageCreate="showCustomizedPageCreate" :newCustomizedPage="newCustomizedPage" @updateNewCustomizedPageToList="updateNewCustomizedPageToList" @hideCustomizedPageCreate="hideCustomizedPageCreate"/>

<!-- 可视化编辑器 -->
<regionDesigner v-if="showRegionDesignerPage==true" :showRegionDesignerPage="showRegionDesignerPage" :funcRegion="funcRegion"  :objectMap="funcDefine.objectMap" :objectAssigns="funcDefine.objectAssigns" :targetRegionMap="funcDefine.targetRegionMap" :funcRegions="funcDefine.funcRegions" @hideRegionDesigner="hideRegionDesigner"/>
<!-- 可视化自定义编辑器 -->
<customizedDesigner v-if="showCustomizedDesignerPage==true" :showCustomizedDesignerPage="showCustomizedDesignerPage" :customizedPage="customizedPage" :customizedPages="funcDefine.customizedPages" :objectAssigns="funcDefine.objectAssigns" :objectMap="funcDefine.objectMap" @hideCustomizedDesigner="hideCustomizedDesigner" @reloadFuncRegionEditPage="reloadFuncRegionEditPage"/>
<!-- 报表可视化编辑器 -->
<reportRegionDesigner v-if="showReportRegionDesignerPage==true" :showReportRegionDesignerPage="showReportRegionDesignerPage" :funcRegion="funcRegion"  :objectMap="funcDefine.objectMap" :objectAssigns="funcDefine.objectAssigns" :targetRegionMap="funcDefine.targetRegionMap" :funcRegions="funcDefine.funcRegions" @hideReportRegionDesigner="hideReportRegionDesigner"/>
<!-- 可视化图表可视化编辑器 -->
<chartRegionDesigner v-if="showChartRegionDesignerPage==true" :showChartRegionDesignerPage="showChartRegionDesignerPage" :funcRegion="funcRegion"  :objectMap="funcDefine.objectMap" :objectAssigns="funcDefine.objectAssigns" :targetRegionMap="funcDefine.targetRegionMap" :funcRegions="funcDefine.funcRegions" @hideChartRegionDesigner="hideChartRegionDesigner"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref, defineAsyncComponent } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'

const editObjectAssign = defineAsyncComponent(() => import('./workbench-FuncObjectAssignEdit.vue'));
const createObjectAssign = defineAsyncComponent(() => import('./workbench-FuncObjectAssignCreate.vue'));
const editFuncRegion = defineAsyncComponent(() => import('./workbench-funcRegionEdit.vue'));
const regionDesigner = defineAsyncComponent(() => import('../../../designer/components/templateDesign/funcRegionDesigner.vue'));
const customizedDesigner = defineAsyncComponent(() => import('../../../designer/components/customizedDesign/funcCustomizedPageDesigner.vue'));
const editFuncCustomizedPage = defineAsyncComponent(() => import('./workbench-funcCustomizedPageEdit.vue'));
const createFuncCustomizedPage = defineAsyncComponent(() => import('./workbench-funcCustomizedPageCreate.vue'));
const reportRegionDesigner = defineAsyncComponent(() => import('../../../designer/components/reportDesign/reportRegionDesigner.vue'));
const chartRegionDesigner = defineAsyncComponent(() => import('../../../designer/components/chartDesign/chartRegionDesigner.vue'));

export default {
    props: [],
    
    emits: ['updateToList', 'deleteFromList'],
    
    setup (props, {attrs, emit, slots}) {
        const updateFuncDefineToList = (funcDefine)=> {
            emit('updateToList', 'funcDefine', funcDefine);
        };
        
        const deleteFuncDefineFromList = (funcDefine)=> {
            emit('deleteFromList', 'funcDefine', funcDefine);
        };
        
        return {
        	updateFuncDefineToList,
        	deleteFuncDefineFromList
        };
    },
    components: {
    	editObjectAssign,
    	createObjectAssign,
    	editFuncRegion,
        regionDesigner,
        customizedDesigner,
        editFuncCustomizedPage,
        createFuncCustomizedPage,
        reportRegionDesigner,
        chartRegionDesigner
    },
    data() {       
        const funcDefine = ref(null);
        const router = useRoute();

    	const showObjectAssignEdit = ref(false);
    	const objectAssign = ref(null);
    	
    	const mainObjectAssign = ref(null);
    	
    	const showObjectAssignCreate = ref(false);
        const newObjectAssign = ref(null);
        
        const showFuncRegionEdit = ref(false);
        const funcRegion = ref(null);
        const customizedPage = ref(null);
        const showCustomizedPageEdit = ref(false);
        const newCustomizedPage = ref(null);
        const showCustomizedPageCreate = ref(false);

        const businessDomainName = ref(null);

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "templateId": [
                { required: true, message: '功能模板不能为空！', trigger: 'blur' }
            ]
        });

        const showRegionDesignerPage = ref(false);
        const showReportRegionDesignerPage = ref(false);
        const showChartRegionDesignerPage = ref(false);
        const showCustomizedDesignerPage = ref(false);
    	
        return {
            funcDefine,
            router,
        	showObjectAssignEdit,
        	objectAssign,
        	mainObjectAssign,
        	showObjectAssignCreate,
        	newObjectAssign,
        	showFuncRegionEdit,
        	funcRegion,
            customizedPage,
            showCustomizedPageEdit,
            validateRules,
            showRegionDesignerPage,
            showCustomizedDesignerPage,
            newCustomizedPage,
            showCustomizedPageCreate,
            showReportRegionDesignerPage,
            showChartRegionDesignerPage,
            businessDomainName
        }
    },
    watch: {        
        'router.query': function(val, old){
            if(val != null && val.objectId != null && (old == null || val.objectId != old.objectId)) {
                setTimeout(()=>{
                    this.loadFuncDefine();
                },100);
            }else if(val == null || val.objectId == null) {
                this.funcDefine = null;
            }
        }
    },
    mounted() {
        this.loadFuncDefine();
    },
    methods: {
        loadFuncDefine() {
            var objectId = this.router.query.objectId;
            if(objectId == null) {
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/fe/api/funcDefine/loadAll/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    data = data.data;
                    //进行数据处理，方便前台界面使用---start
                    var attributeMap = {};//attributeId=>Attriubte
                    if(data.objectAssigns != null) {
                        var objectMap = {};//设置对象Map，方便页面使用，objectId=》FuncObjectAssign
                        for(var i in data.objectAssigns) {
                            var row = data.objectAssigns[i];
                            objectMap[row.objectId] = row;
                            if(row.attributes != null) {
                                for(var j in row.attributes) {
                                    attributeMap[row.attributes[j].id] = row.attributes[j];
                                }
                            }
                        }
                        data.objectMap = objectMap;
                    }
                    if(data.funcRegions != null) {
                        var regionMap = {};//tplReginId-》FuncRegion，方便前台界面使用
                        var targetRegionMap = {};//funcReginId-》FuncRegion，方便前台界面使用，用于获取目标区域
                        for(var i in data.funcRegions) {
                            var row = data.funcRegions[i];
                            regionMap[row.tplRegionId] = row;
                            targetRegionMap[row.id] = row;
                            //补充绑定对象的属性信息
                            if(row.regionAttrAssigns != null) {
                                for(var j in row.regionAttrAssigns) {
                                    row.regionAttrAssigns[j].attribute = attributeMap[row.regionAttrAssigns[j].attributeId];
                                }
                            }
                        }
                        data.regionMap = regionMap;
                        data.targetRegionMap = targetRegionMap;
                        data.funcRegions.sort((a,b)=>{return a.name > b.name});
                    }
                    //进行数据处理，方便前台界面使用---end
                    this.funcDefine = data;
                    var url = "/lcdp-proxy/lowcode/platform/be/api/businessDomain/byId/" + data.domainId;
                    axiosClient.get(url).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.businessDomainName = data.data.name;
                        }
                    });
                }
            });
        },
        reloadFuncRegionEditPage(funcRegionId) {
            this.hideCustomizedDesigner();
            this.showFuncRegionEditPage(funcRegionId, true);
        },
    	showFuncRegionEditPage(id, isDesigner) {
            if(this.funcDefine.funcTemplate.templateType == 'C') {//自定义功能
                if(isDesigner) {
                    //重新加载功能信息，因为功能页面的信息可能不完整
                    var url = "/lcdp-proxy/lowcode/platform/fe/api/funcDefine/loadAll/" + this.funcDefine.id ;
                    axiosClient.post(url).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            data = data.data;
                            //进行数据处理，方便前台界面使用---start
                            var attributeMap = {};//attributeId=>Attriubte
                            if(data.objectAssigns != null) {
                                var objectMap = {};//设置对象Map，方便页面使用，objectId=》FuncObjectAssign
                                for(var i in data.objectAssigns) {
                                    var row = data.objectAssigns[i];
                                    objectMap[row.objectId] = row;
                                    if(row.attributes != null) {
                                        for(var j in row.attributes) {
                                            attributeMap[row.attributes[j].id] = row.attributes[j];
                                        }
                                    }
                                }
                                data.objectMap = objectMap;
                            }
                            if(data.funcRegions != null) {
                                var regionMap = {};//tplReginId-》FuncRegion，方便前台界面使用
                                var targetRegionMap = {};//funcReginId-》FuncRegion，方便前台界面使用，用于获取目标区域
                                for(var i in data.funcRegions) {
                                    var row = data.funcRegions[i];
                                    regionMap[row.tplRegionId] = row;
                                    targetRegionMap[row.id] = row;
                                    //补充绑定对象的属性信息
                                    if(row.regionAttrAssigns != null) {
                                        for(var j in row.regionAttrAssigns) {
                                            row.regionAttrAssigns[j].attribute = attributeMap[row.regionAttrAssigns[j].attributeId];
                                        }
                                    }
                                }
                                data.regionMap = regionMap;
                                data.targetRegionMap = targetRegionMap;
                                data.funcRegions.sort((a,b)=>{return a.name > b.name});
                            }
                            //进行数据处理，方便前台界面使用---end
                            this.funcDefine = data;
                            if(this.funcDefine != null && this.funcDefine.customizedPages != null) {
                                for(var j in this.funcDefine.customizedPages) {
                                    if(this.funcDefine.customizedPages[j].id == id) {
                                        this.customizedPage = this.funcDefine.customizedPages[j];
                                        this.showCustomizedDesignerPage = true;
                                        this.showRegionDesignerPage = false;
                                        this.showFuncRegionEdit = false;
                                        this.showCustomizedPageEdit = false;
                                        break;
                                    }
                                }
                            }
                        }
                    });
                    
                }else {
                    if(this.funcDefine != null && this.funcDefine.customizedPages != null) {
                        for(var j in this.funcDefine.customizedPages) {
                            if(this.funcDefine.customizedPages[j].id == id) {
                                this.customizedPage = this.funcDefine.customizedPages[j];
                                this.showCustomizedDesignerPage = false;
                                this.showRegionDesignerPage = false;
                                this.showFuncRegionEdit = false;
                                this.showCustomizedPageEdit = true;
                                break;
                            }
                        }
                    }
                    
                }
                
            }else {//功能模板
                var url = "/lcdp-proxy/lowcode/platform/fe/api/funcRegion/loadAll/" + id ;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.funcRegion = data.data;
                        if(isDesigner) {
                            if(this.funcDefine.funcTemplate.templateType == 'R') {
                                this.showRegionDesignerPage = false;
                                this.showChartRegionDesignerPage = false;
                                this.showReportRegionDesignerPage = true;
                            }else if(this.funcDefine.funcTemplate.templateType == 'V') {
                                this.showRegionDesignerPage = false;
                                this.showChartRegionDesignerPage = true;
                                this.showReportRegionDesignerPage = false;
                            }else {
                                this.showRegionDesignerPage = true;
                                this.showReportRegionDesignerPage = false;
                                this.showChartRegionDesignerPage = false;
                            }
                            
                            this.showCustomizedDesignerPage = false;
                            this.showFuncRegionEdit = false;
                            this.showCustomizedPageEdit = false;
                        }else {
                            this.showFuncRegionEdit = true;
                            this.showRegionDesignerPage = false;
                            this.showReportRegionDesignerPage = false;
                            this.showChartRegionDesignerPage = false;
                            this.showCustomizedDesignerPage = false;
                            this.showCustomizedPageEdit = false;
                        }
                    }
                });
            }

    		
    	},
    	updateFuncRegionToList(data) {
    		if(this.funcDefine.funcRegions != null && this.funcDefine.funcRegions.length > 0) {
                for(var index in this.funcDefine.funcRegions) {
                    if(this.funcDefine.funcRegions[index].id == data.id) {
                        this.funcDefine.funcRegions[index].updateTime = data.updateTime;
                        this.funcDefine.funcRegions[index].name = data.name;
                        this.funcDefine.funcRegions.sort((a,b)=>{return a.name > b.name});
                        break;
                    }
                }
            }
    		this.funcRegion = null;
            this.showFuncRegionEdit = false;
    	},
    	hideFuncRegionEditPage() {
    		this.funcRegion = null;
            this.showFuncRegionEdit = false;
    	},
        updateCustomizedPageToList(data) {
    		if(this.funcDefine.customizedPages != null && this.funcDefine.customizedPages.length > 0) {
                for(var index in this.funcDefine.customizedPages) {
                    if(this.funcDefine.customizedPages[index].id == data.id) {
                        this.funcDefine.customizedPages[index].updateTime = data.updateTime;
                        this.funcDefine.customizedPages[index].name = data.name;
                        this.funcDefine.customizedPages[index].isMainPage = data.isMainPage;
                        this.funcDefine.customizedPages.sort((a,b)=>{
                            const dateA = new Date(a.createTime);
                            const dateB = new Date(b.createTime);
                            if(dateA == dateB) {
                                return 0;
                            }else if(dateA < dateB) {
                                return -1;
                            }else {
                                return 1;
                            }
                        });
                        break;
                    }
                }
            }
    		this.funcRegion = null;
            this.showFuncRegionEdit = false;
    	},
        hideCustomizedPageEditPage() {
    		this.customizedPage = null;
            this.showCustomizedPageEdit = false;
    	},
        showCustomizedPageCreatePage() {
    		this.newCustomizedPage =  {
    			id : null,
    			funcId : this.funcDefine.id,
    			name : null,
    			isMainPage : "N",
    			systemId : this.funcDefine.systemId,
                tenantId : this.funcDefine.tenantId
    		};
    		this.showCustomizedPageCreate = true;
    	},
        updateNewCustomizedPageToList(data) {
            if(this.funcDefine.customizedPages == null || this.funcDefine.customizedPages.length == 0) {
            	this.funcDefine.customizedPages = [];
            }
            this.funcDefine.customizedPages.push(data);
            this.funcDefine.customizedPages.sort((a,b)=>{
                const dateA = new Date(a.createTime);
                const dateB = new Date(b.createTime);
                if(dateA == dateB) {
                    return 0;
                }else if(dateA < dateB) {
                    return -1;
                }else {
                    return 1;
                }
            });
            this.newCustomizedPage = null;
            this.showFuncCustomizedPageCreate = false;
        },
        hideCustomizedPageCreate() {
            this.newCustomizedPage = null;
            this.showCustomizedPageCreate = false;
        },
    	showObjectAssignEditPage(id) {
    		var url = "/lcdp-proxy/lowcode/platform/fe/api/funcObjectAssign/loadAll/" + id ;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                	this.objectAssign = data.data;
                    if(this.objectAssign != null && this.objectAssign.attrOptionMap != null) {
                        for(var key in this.objectAssign.attrOptionMap) {
                            if(this.objectAssign.attrOptionMap[key] != null && this.objectAssign.attrOptionMap[key].options == null) {
                                this.objectAssign.attrOptionMap[key].options = [];
                            }
                        }
                    }
                            
                	if(this.funcDefine.objectAssigns != null && this.funcDefine.objectAssigns.length > 0) {
                        for(var index in this.funcDefine.objectAssigns) {
                            if(this.funcDefine.objectAssigns[index].assignType == 'M') {
                            	this.mainObjectAssign = this.funcDefine.objectAssigns[index];
                            	break;
                            }
                        }
                	}else {
                        this.mainObjectAssign = null;
                    }
                    this.showObjectAssignEdit = true;
                }
            });
    		
    	},
    	updateObjectAssignToList(data) {
            if(this.funcDefine.objectAssigns != null && this.funcDefine.objectAssigns.length > 0) {
            	for(var index in this.funcDefine.objectAssigns) {
            		if(this.funcDefine.objectAssigns[index].id == data.id) {
            			this.funcDefine.objectAssigns[index] = data;
                        break;
            		}
            	}
            	this.funcDefine.objectAssigns.sort((a,b)=>{
            		return a.displayOrder-b.displayOrder;
            	});
            }
            this.loadFuncDefine();
            this.objectAssign = null;
            this.showObjectAssignEdit = false;
        },
    	hideObjectAssignEditPage() {
    		this.objectAssign = null;
            this.showObjectAssignEdit = false;
    	},
    	showObjectAssignCreatePage() {
            var objectType = "D";
            if(this.funcDefine.funcTemplate.templateType == 'R' || this.funcDefine.funcTemplate.templateType == 'V') {
                objectType = "B";
                if(this.funcDefine.objectAssigns != null && this.funcDefine.objectAssigns.length > 0) {
                    ElMessage(`统计报表/可视化图表功能模板只允许绑定一个数据集！`);
                    return;
                }
            }
    		this.newObjectAssign =  {
    			id : null,
    			funcId : this.funcDefine.id,
    			objectType : objectType,
    			objectId : "",
    			assignType : 'S',
    			relAttrId : null,
    			assignObject : {},
    			systemId : this.funcDefine.systemId,
                tenantId : this.funcDefine.tenantId
    		};
    		if(this.funcDefine.objectAssigns != null && this.funcDefine.objectAssigns.length > 0) {
                for(var index in this.funcDefine.objectAssigns) {
                    if(this.funcDefine.objectAssigns[index].assignType == 'M') {
                        this.mainObjectAssign = this.funcDefine.objectAssigns[index];
                        break;
                    }
                }
            }else {
            	this.mainObjectAssign = null;
            }
            this.showObjectAssignCreate = true;
    	},
        updateNewObjectAssignToList(data) {
            if(this.funcDefine.objectAssigns == null || this.funcDefine.objectAssigns.length == 0) {
            	this.funcDefine.objectAssigns = [];
            }
            this.funcDefine.objectAssigns.push(data);
            this.funcDefine.objectAssigns.sort((a,b)=>{
                return a.displayOrder-b.displayOrder;
            });
            this.loadFuncDefine();
            this.newObjectAssign = null;
            this.showObjectAssignCreate = false;
        },
        hideObjectAssignCreatePage() {
            this.newObjectAssign = null;
            this.showObjectAssignCreate = false;
        },
        deleteObjectAssign(id) {
        	ElMessageBox.confirm(
                    '该绑定对象将被删除. 是否继续?',
                    '警告',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning',
                    }
                 ).then(() => {
                     var url = "/lcdp-proxy/lowcode/platform/fe/api/funcObjectAssign/delete/" + id;
                     axiosClient.post(url).then((response) => {
                         var data = response.data;
                         if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                             if(this.funcDefine.objectAssigns != null && this.funcDefine.objectAssigns.length > 0) {
                            	 for(var i in this.funcDefine.objectAssigns) {
                            		 if(this.funcDefine.objectAssigns[i].id == id) {
                            			 this.funcDefine.objectAssigns.splice(i,1);
                            			 break;
                            		 }
                            	 }
                             }
                             ElMessage(`删除绑定对象成功。`);
                         }
                     }).catch(()=>{
                         ElMessage.error(`删除绑定对象失败！`);
                     });
                 }).catch(()=>{});
        },
    	saveFuncDefine() {
            var url = "/lcdp-proxy/lowcode/platform/fe/api/funcDefine/save";
            this.$refs.funcDefineForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.funcDefine).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.updateFuncDefineToList(this.funcDefine);
                            ElMessage(`更新系统功能信息成功`);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        deleteFuncDefine() {
            ElMessageBox.confirm(
                '该系统功能(' + this.funcDefine.name + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/fe/api/funcDefine/delete/" + this.funcDefine.id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         this.deleteFuncDefineFromList(this.funcDefine);
                         
                         ElMessage(`删除系统功能(`+ this.funcDefine.name +`)成功。`);
                     }
                 }).catch(()=>{
                     ElMessage.error(`删除系统功能(`+ this.funcDefine.name +`)失败！`);
                 });
             }).catch(()=>{});
        },
        hideRegionDesigner(){
            this.showRegionDesignerPage = false;
        },
        hideReportRegionDesigner(){
            this.showReportRegionDesignerPage = false;
        },
        hideChartRegionDesigner(){
            this.showChartRegionDesignerPage = false;
        },
        hideCustomizedDesigner() {
            this.showCustomizedDesignerPage = false;
        }
    }
}
</script>

<style scoped>
.toolbar-right {
  display: inline-flex;
  align-items: center;
  justify-content: right; 
  height: 100%;
  right: 20px;
}
.toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
  height: 100%;
  right: 20px;
}
</style>