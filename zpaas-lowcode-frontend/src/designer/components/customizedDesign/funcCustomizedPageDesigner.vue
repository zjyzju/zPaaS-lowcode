<template> 
<el-dialog v-model="showFlag" v-if="showCustomizedDesignerPage ==true" title="自定义功能区域设计器" width="98vw" top="2vh"  :close-on-press-escape="false" :close-on-click-modal="false" :show-close="false">
    <template #header>
        <span class="title">自定义功能区域设计器--{{ this.customizedPage.name }}</span>
    </template>
    <template #default>
        <div class="common-layout">
            <el-container  style="height: 82vh">
                <el-aside width="200px">
                    <el-tabs v-model="leftActiveName">
                        <el-tab-pane label="属性/组件" name="attrForSelectTab">
                            <undisplayObjectAttrsAndComponents :objectAttrMapForSelect="objectAttrMapForSelect" :customizedPage="customizedPage" :customizedDesignComponents="customizedDesignComponents"/>
                        </el-tab-pane>
                        <el-tab-pane label="其他操作" name="otherTab">
                            <el-collapse v-model="activeOtherOperation">
                                <el-collapse-item title="编辑绑定对象" name="editObjectAssign">
                                    <div v-for="objectAssign in objectAssigns">
                                        <el-link @click="showObjectAssignEditPage(objectAssign.id)" type="primary" >{{ objectAssign.assignObject != null ? objectAssign.assignObject.name : objectAssign.biDataSet.name}}</el-link>&nbsp;&nbsp;
                                    </div>
                                </el-collapse-item>
                                <el-collapse-item title="从其他页面复制" name="copyAttrAssigns">
                                    <el-link @click="showFuncCustomizedPageCopyPage()"  type="primary" >复制(从其他页面)</el-link>
                                </el-collapse-item>
                            </el-collapse>
                        </el-tab-pane>
                    </el-tabs>
                </el-aside>
                <el-divider direction="vertical" border-style="dashed" style="height:100%;margin-left: 1px;margin-right: 1px; color: cornflowerblue;"></el-divider>
                <el-main>
                    <!-- 可视化编辑器 -->
                    <customizedDesigner v-if="showCustomizedDesignerPage==true" :rootComponent="rootComponent" @onAttrSelected="onAttrSelected" @onOperationSelected="onOperationSelected" @onCustomizedLayoutSelected="onCustomizedLayoutSelected"/>
                </el-main>
                <el-divider direction="vertical" border-style="dashed" style="height:100%;margin-left: 1px;margin-right: 1px; color: cornflowerblue;"></el-divider>
                <el-aside width="220px">
                    <el-tabs v-model="rightActiveName">
                        <el-tab-pane label="属性编辑" name="editPanel">
                            <attrAssignEdit v-if="currentAttrAssign != null" :attrAssign="currentAttrAssign" :objectAssigns="objectAssigns" :assignObject="objectMap[currentAttrAssign.objectId]"/>
                            <reportAttrAssignEdit v-else-if="currentReportAttrAssign != null && attrFormComponent != null" :attrAssign="currentReportAttrAssign" :objectAssigns="objectAssigns" :assignObject="objectMap[currentReportAttrAssign.objectId]" :formComponent="attrFormComponent"/>
                            <reportAttrAssignEdit v-else-if="currentReportAttrAssign != null" :attrAssign="currentReportAttrAssign" :objectAssigns="objectAssigns" :assignObject="objectMap[currentReportAttrAssign.objectId]"/>
                            <operationEdit v-else-if="currentRegionOperation != null" :customizedPage="customizedPage" :objectAssigns="objectAssigns" :funcRegionOperation="currentRegionOperation" :customizedPages="customizedPages"/>
                            <customizedLayoutEdit v-else-if="currentCustomizedLayout != null" :customizedLayout="currentCustomizedLayout" :objectAssigns="objectAssigns"/>
                        </el-tab-pane>
                    </el-tabs>
                </el-aside>
            </el-container>
        </div>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="closeRegionDesigner()">取消</el-button>
        <el-button type="primary" @click="saveDesignInfo()">保存</el-button>
      </div>
    </template>
</el-dialog>

<!-- 编辑对象绑定信息 -->
<editObjectAssign v-if="showObjectAssignEdit==true && objectAssign != null" :showObjectAssignEdit="showObjectAssignEdit" :objectAssign="objectAssign" :mainObjectAssign="mainObjectAssign" :objectAssigns="objectAssigns" @updateObjectAssignToList="updateObjectAssignToList" @hideObjectAssignEditPage="hideObjectAssignEditPage"/>

<!-- 复制页面(从其他功能) -->
<funcCustomizedPageCopyFromOther v-if="showFuncCustomizedPageCopy==true" :showFuncCustomizedPageCopy="showFuncCustomizedPageCopy" :systemId="customizedPage.systemId" :targetPageId="customizedPage.id" :targetFuncId="customizedPage.funcId" @reloadCustomizedPage="reloadCustomizedPage" @hideFuncCustomizedPageCopyPage="hideFuncCustomizedPageCopyPage"/>

</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import customizedDesigner from './customizedDesigner.vue'
import undisplayObjectAttrsAndComponents from './undisplayObjectAttrsAndComponents.vue'
import attrAssignEdit from './attrAssignEdit.vue'
import reportAttrAssignEdit from './attrAssignEdit-report.vue'
import chartAttrAssignEdit from './attrAssignEdit-chart.vue'
import operationEdit from './operationEdit.vue'
import customizedLayoutEdit from './customizedLayoutEdit.vue'

import editObjectAssign from '../../../workbench/components/funcMgr/workbench-FuncObjectAssignEdit.vue'
import funcCustomizedPageCopyFromOther from './funcCustomizedPageCopyFromOther.vue'

export default {
    props: ['showCustomizedDesignerPage', 'objectAssigns','customizedPage', 'customizedPages', 'objectMap'],
    
    emits: ['hideCustomizedDesigner', 'reloadFuncRegionEditPage'],
    
    setup (props, {attrs, emit, slots}) {
        const hideCustomizedDesigner = () => {
            emit('hideCustomizedDesigner')
        };

        const reloadFuncRegionEditPage = (funcRegionId) => {
            emit('reloadFuncRegionEditPage', funcRegionId)
        };

        return {
        	hideCustomizedDesigner,
            reloadFuncRegionEditPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showCustomizedDesignerPage;
            },
            set(value) {
                this.hideCustomizedDesigner();
            }
        }
    },
    components: {
    	customizedDesigner,
        undisplayObjectAttrsAndComponents,
        attrAssignEdit,
        reportAttrAssignEdit,
        chartAttrAssignEdit,
        operationEdit,
        customizedLayoutEdit,

        editObjectAssign,
        funcCustomizedPageCopyFromOther
    },
    
    data() {   
        const currentAttrAssign = ref(null);
        const currentReportAttrAssign = ref(null);
        const currentRegionOperation = ref(null);
        const currentCustomizedLayout = ref(null);
        const attrFormComponent = ref(null);

        const leftActiveName = ref("attrForSelectTab");
        const rightActiveName = ref("editPanel");
        const activeOtherOperation = ref("addOperation");

        const showFuncRegionAttrAssignCopy = ref(false);   	
    	const showFuncRegionAttrAssignCopyFromOther = ref(false);

        const showObjectAssignEdit = ref(false);
    	const objectAssign = ref(null);   	
    	const mainObjectAssign = ref(null);

        const mainObject = ref(null);
        const subObjects = ref(null);
        const subAttrNeedFormat = ref(false);
        const regionData = ref(null);
        const objectAttrMapForSelect = ref(null);
        const rootComponent = ref({});

        const showFuncCustomizedPageCopy = ref(false);

        const customizedDesignComponents = ref({});
        const dataSetColumnType = ref(null);

    	return {
            currentAttrAssign,
            currentReportAttrAssign,
            currentRegionOperation,
            currentCustomizedLayout,
            attrFormComponent,

            leftActiveName,
            rightActiveName,
            activeOtherOperation,

            showFuncRegionAttrAssignCopy,
        	showFuncRegionAttrAssignCopyFromOther,

            showObjectAssignEdit,
        	objectAssign,
        	mainObjectAssign,

            mainObject,
            subObjects,
            subAttrNeedFormat,
            regionData,
            objectAttrMapForSelect,
            rootComponent,

            showFuncCustomizedPageCopy,
            customizedDesignComponents,
            dataSetColumnType
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/fe/api/dict/getCustomizedDesignComponents").then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.customizedDesignComponents.components = data.data;
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValuesMore", ['BI_COLUMN_TYPE_DATASET']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.dataSetColumnType = data.data['BI_COLUMN_TYPE_DATASET'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        this.initAttrDisplayInfo();
    },
    methods: {
    	initAttrDisplayInfo() {
            //加载并初始化containerComponents信息
            this.rootComponent.containerComponents = this.customizedPage.customizedLayouts;
            if(this.rootComponent.containerComponents == null) {
                this.rootComponent.containerComponents = [];
            }
            //初始化主对象、子对象、是否有子对象属性需要格式化展示以及regionData的属性初始化
            this.subObjects = [];
            this.mainObject = null;
            this.subAttrNeedFormat = false;
            this.regionData = {};
            if(this.objectAssigns != null) {
                for(var index in this.objectAssigns) {
                    if(this.objectAssigns[index].assignType == "M") {
                        this.mainObject = this.objectAssigns[index];
                    }else {
                        this.subObjects.push(this.objectAssigns[index]);
                        if(this.objectAssigns[index].mainRelAttribute != null && this.objectAssigns[index].mainRelAttribute.isListAttr == 'N') {
                            if(this.regionData[this.objectAssigns[index].mainRelAttribute.code] == null) {
                                this.regionData[this.objectAssigns[index].mainRelAttribute.code] = {};
                            }
                            this.subAttrNeedFormat = true;
                        }else {
                            if(this.objectAssigns[index].mainRelAttribute != null && this.regionData[this.objectAssigns[index].mainRelAttribute.code] == null) {
                                this.regionData[this.objectAssigns[index].mainRelAttribute.code] = [];
                            }
                        }
                    }
                }
            }
            //处理未显示属性
            this.processUndisplayAttr();
        },
        processUndisplayAttr() {//处理未显示的属性
            var undisplayObjectAttrMap = {};
            
            var hasDataSet = false;
            for(var index in this.objectAssigns) {
                var objectAssign = this.objectAssigns[index];
                var assignObject = undisplayObjectAttrMap[objectAssign.id];
                if(assignObject == null) {
                    assignObject = {
                        id: objectAssign.id,
                        undispalyAttrs: [],
                        objectAssign: objectAssign
                    };
                    undisplayObjectAttrMap[objectAssign.id] = assignObject;
                }
                if(objectAssign.assignObject != null && objectAssign.assignObject.attributes != null) {
                    for(var attrIndex in objectAssign.assignObject.attributes) {
                        var attribute = objectAssign.assignObject.attributes[attrIndex];
                        var isDisplay = false;//this.isDisplayed(attribute, this.rootComponent.containerComponents);
                        
                        if(!isDisplay) {
                            var undispalyAttr = {
                                attribute: attribute,
                                attributeId: attribute.id,
                                displayType: 'I',
                                colSpan: 8,
                                rowSpan: 1,
                                displayWidth: 120,
                                displayName: attribute.name,
                                funcId: objectAssign.funcId,
                                funcRegionId: null,
                                objectAssignId: objectAssign.id,
                                objectId: objectAssign.objectId,
                                objectType: objectAssign.objectType,
                                systemId: objectAssign.systemId,
                                tenantId: objectAssign.tenantId
                            };
                            assignObject.undispalyAttrs.push(undispalyAttr);
                        }
                    }
                }else if(objectAssign.biDataSet != null && objectAssign.biDataSet.details != null) {
                    hasDataSet = true;
                    assignObject.filterAttrs = [];
                    assignObject.tagAttrs = [];
                    for(var attrIndex in objectAssign.biDataSet.details) {
                        var detail = objectAssign.biDataSet.details[attrIndex];
                        var isDisplay = false;
                        
                        if(!isDisplay) {
                            var undispalyAttr = {
                                dataSetDetail: detail,
                                attributeId: detail.id,
                                displayType: 'O',
                                colSpan: 8,
                                rowSpan: 1,
                                displayWidth: 150,
                                displayName: detail.content.name,
                                funcId: objectAssign.funcId,
                                funcRegionId: objectAssign.funcRegionId,
                                objectAssignId: objectAssign.id,
                                objectId: objectAssign.objectId,
                                objectType: objectAssign.objectType,
                                systemId: objectAssign.systemId,
                                tenantId: objectAssign.tenantId
                            };
                            if(detail.detailType == 'F') {
                                assignObject.filterAttrs.push(undispalyAttr);
                            }else if(detail.detailType == 'L') {
                                assignObject.tagAttrs.push(undispalyAttr);
                            }else {
                                assignObject.undispalyAttrs.push(undispalyAttr);
                            }
                        }
                    }
                }
            }

            if(hasDataSet) {
                var commonObject = {
                    id: '$0011001&',
                    undispalyAttrs: [
                        {
                            dataSetDetail: {content:{code:'data_apply_column',name:'数据提供列'},detailType:'C'},
                            attributeId: 'N/A',
                            displayType: 'S',
                            colSpan: 8,
                            rowSpan: 1,
                            displayWidth: 150,
                            displayName: "数据提供列",
                            funcId: this.customizedPage.funcId,
                            funcRegionId: '',
                            objectAssignId: '',
                            objectId: '',
                            objectType: '',
                            systemId: this.customizedPage.systemId,
                            tenantId: this.customizedPage.tenantId
                        },
                        {
                            dataSetDetail: {content:{code:'common_sum_column',name:'累加列'},detailType:'C'},
                            attributeId: 'N/A',
                            displayType: 'Q',
                            colSpan: 8,
                            rowSpan: 1,
                            displayWidth: 150,
                            displayName: "累加列",
                            funcId: this.customizedPage.funcId,
                            funcRegionId: '',
                            objectAssignId: '',
                            objectId: '',
                            objectType: '',
                            systemId: this.customizedPage.systemId,
                            tenantId: this.customizedPage.tenantId
                        }
                    ],
                    objectAssign: {
                        biDataSet: {
                            name: '公共属性'
                        },
                        id: '$0011001&'
                    }
                }
                undisplayObjectAttrMap[commonObject.id] = commonObject;
            }
            
            var undisplayObjectAttrs = [];
            for(var key in undisplayObjectAttrMap) {
                undisplayObjectAttrs.push(undisplayObjectAttrMap[key]);
            }
            this.objectAttrMapForSelect = {
                undisplayObjectAttrs: undisplayObjectAttrs
            }
        },
        isDisplayed(attribute, components) {//判断某属性是否已经展示
            if(components == null || components.length == 0) {
                return false;
            }
            for(var index in components) {
                var component = components[index];
                if(component.type == 'CC') {//容器类容器控件
                    if(this.isDisplayed(attribute, component.subComponents)) {
                        return true;
                    }
                }else if(component.type == 'CL') {//非容器类容器控件
                    if(component.subComponents != null && component.subComponents.length > 0) {
                        for(var subIndex in component.subComponents) {
                            var subComponent = component.subComponents[subIndex];
                            if(subComponent.attributeId == attribute.id) {
                                return true;
                            }
                        }
                    }
                }
            }
            return false;
        },
        onAttrSelected(attrAssign, attrFormComponent) {
            if(this.currentAttrAssign != null && attrAssign.attributeId != null && this.currentAttrAssign.attributeId == attrAssign.attributeId) {
                return;
            }
            if(attrAssign.dataSetDetail != null) {
                this.currentAttrAssign = null;
                this.currentReportAttrAssign = attrAssign;
                this.currentRegionOperation = null;
                this.currentCustomizedLayout = null;
                this.attrFormComponent = attrFormComponent;
            }else {
                this.currentAttrAssign = attrAssign;
                this.currentReportAttrAssign = null;
                this.currentRegionOperation = null;
                this.currentCustomizedLayout = null;
                this.attrFormComponent = null;
            }
        },
        onOperationSelected(regionOperation) {
            if(this.currentRegionOperation != null && this.currentRegionOperation == regionOperation) {
                return;
            }
            if(regionOperation.operationCfg == null || regionOperation.operationCfg.trim().length == 0) {
                regionOperation.operationCfgJSON = {};
            }else {
                regionOperation.operationCfgJSON = JSON.parse(regionOperation.operationCfg);
            }
            this.currentRegionOperation = regionOperation;
            this.currentAttrAssign = null;
            this.currentReportAttrAssign = null;
            this.currentCustomizedLayout = null;
        },
        onCustomizedLayoutSelected(customizedLayout) {
            if(this.currentCustomizedLayout != null && this.currentCustomizedLayout == customizedLayout) {
                return;
            }
            if(customizedLayout.layoutCfg == null || customizedLayout.layoutCfg.trim().length == 0) {
                customizedLayout.layoutCfgJSON = {};
            }else {
                customizedLayout.layoutCfgJSON = JSON.parse(customizedLayout.layoutCfg);
            }
            this.currentCustomizedLayout = customizedLayout;
            this.currentAttrAssign = null;
            this.currentReportAttrAssign = null;
            this.currentRegionOperation = null;
        },
        saveDesignInfo() {
            ElMessageBox.confirm(
                '是否保存设计信息?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
            ).then(() => {
                var url = "/lcdp-proxy/lowcode/platform/fe/api/customizedPage/savePageLayouts/" + this.customizedPage.id;
                axiosClient.post(url, this.rootComponent.containerComponents).then((response) => {
                    var data = response.data;
                    if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                        ElMessage(`保存成功。`);
                        this.hideCustomizedDesigner();
                    }
                }).catch((ex)=>{
                    ElMessage.error(`保存失败！` + ex);
                });
            }).catch((e)=>{
                ElMessage.error(`保存失败！` + e);
            });
        },
        showObjectAssignEditPage(id) {
    		var url = "/lcdp-proxy/lowcode/platform/fe/api/funcObjectAssign/loadAll/" + id ;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null && data.data != null) {
                	this.objectAssign = data.data;
                    if(this.objectAssign != null && this.objectAssign.attrOptionMap != null) {
                        for(var key in this.objectAssign.attrOptionMap) {
                            if(this.objectAssign.attrOptionMap[key] != null && this.objectAssign.attrOptionMap[key].options == null) {
                                this.objectAssign.attrOptionMap[key].options = [];
                            }
                        }
                    }
                            
                	if(this.objectAssigns != null && this.objectAssigns.length > 0) {
                        for(var index in this.objectAssigns) {
                            if(this.objectAssigns[index].assignType == 'M') {
                            	this.mainObjectAssign = this.objectAssigns[index];
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
            if(this.objectAssigns != null && this.objectAssigns.length > 0) {
            	for(var index in this.objectAssigns) {
            		if(this.objectAssigns[index].id == data.id) {
            			this.objectAssigns[index] = data;
            			break;
            		}
            	}
            	this.objectAssigns.sort((a,b)=>{
            		return a.displayOrder-b.displayOrder;
            	});
            }
            this.objectAssign = null;
            this.showObjectAssignEdit = false;
        },
    	hideObjectAssignEditPage() {
    		this.objectAssign = null;
            this.showObjectAssignEdit = false;
    	},
        closeRegionDesigner() {
            ElMessageBox.confirm(
                '未保存的设计信息将会丢失，请确认是否关闭设计页面?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
            ).then(() => {
                this.hideCustomizedDesigner();
            }).catch((e)=>{
                
            });
        },
        showFuncCustomizedPageCopyPage() {
            this.showFuncCustomizedPageCopy = true;
        },
        hideFuncCustomizedPageCopyPage() {
            this.showFuncCustomizedPageCopy = false;
        },
        reloadCustomizedPage() {
            this.reloadFuncRegionEditPage(this.customizedPage.id);
        }
    }
 }
</script>