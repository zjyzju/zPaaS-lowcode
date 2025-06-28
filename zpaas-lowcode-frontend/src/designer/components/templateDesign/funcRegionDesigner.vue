<template> 
<el-dialog v-model="showFlag" v-if="showRegionDesignerPage ==true" title="功能区域设计器" width="98vw" top="2vh"  :close-on-press-escape="false" :close-on-click-modal="false" :show-close="false">
    <template #header>
        <span class="title">功能区域设计器--{{ this.funcRegion.name }}</span>
    </template>
    <template #default>
        <div class="common-layout">
            <el-container  style="height: 81vh">
                <el-aside width="180px">
                    <el-tabs v-model="leftActiveName">
                        <el-tab-pane label="可选属性" name="attrForSelectTab">
                            <undisplayObjectAttrs :objectAttrMapForSelect="objectAttrMapForSelect" @onChangeEnd="onChangeEnd"/>
                        </el-tab-pane>
                        <el-tab-pane label="其他操作" name="addOperationTab">
                            <el-collapse v-model="activeOtherOperation">
                                <el-collapse-item title="新增操作" name="addOperation">
                                    <operationAdd :funcRegion="funcRegion" :objectAssigns="objectAssigns" @addFuncRegionOperation="addFuncRegionOperation" :operationTypeOptions="operationTypeOptions"/>
                                </el-collapse-item>
                                <el-collapse-item title="复制属性" name="copyAttrAssigns">
                                    <el-link @click="showFuncRegionAttrAssignCopyPage()"  type="primary" >复制(从本功能)</el-link>&nbsp;&nbsp;
                                    <el-link @click="showFuncRegionAttrAssignCopyPageFromOther()"  type="primary" >复制(从其他功能)</el-link>
                                </el-collapse-item>
                                <el-collapse-item title="编辑绑定对象" name="editObjectAssign">
                                    <div v-for="objectAssign in objectAssigns">
                                        <el-link @click="showObjectAssignEditPage(objectAssign.id)" type="primary" >{{ objectAssign.assignObject.name }}</el-link>&nbsp;&nbsp;
                                    </div>
                                </el-collapse-item>
                            </el-collapse>
                        </el-tab-pane>
                    </el-tabs>
                </el-aside>
                <el-divider direction="vertical" border-style="dashed" style="height:100%;margin-left: 1px;margin-right: 1px; color: cornflowerblue;"></el-divider>
                <el-main>
                    <!-- 可视化编辑器 -->
                    <designer v-if="showRegionDesignerPage==true" :showRegionDesignerPage="showRegionDesignerPage" :funcRegion="funcRegion"  :mainObject="mainObject" :attrAssignInfo="attrAssignInfo" :subObjects="subObjects" :subObjectAttrList="subObjectAttrList" @onAttrSelected="onAttrSelected" @onChangeEnd="onChangeEnd" @deleteAttr="deleteAttr" @onOperationSelected="onOperationSelected" @deleteRegionOperation="deleteRegionOperation"/>
                </el-main>
                <el-divider direction="vertical" border-style="dashed" style="height:100%;margin-left: 1px;margin-right: 1px; color: cornflowerblue;"></el-divider>
                <el-aside width="180px">
                    <el-tabs v-model="rightActiveName">
                        <el-tab-pane label="属性编辑" name="editPanel">
                            <attrAssignEdit v-if="currentAttrAssign != null" :funcRegionAttrAssign="currentAttrAssign" :objectAssigns="objectAssigns" :assignObject="objectMap[currentAttrAssign.objectId]" :displayTypeOptions="displayTypeOptions"/>
                            <operationEdit v-if="currentRegionOperation != null" :funcRegion="funcRegion" :objectAssigns="objectAssigns" :funcRegionOperation="currentRegionOperation" :funcRegions="funcRegions" :operationTypeOptions="operationTypeOptions" :pageDisplayTypeOptions="pageDisplayTypeOptions"/>
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

<!-- 复制功能区域绑定属性信息 -->
<copyFuncRegionAttrAssign v-if="showFuncRegionAttrAssignCopy==true && funcRegions != null" :showFuncRegionAttrAssignCopy="showFuncRegionAttrAssignCopy" :funcRegions="funcRegions" :targetRegionId="funcRegion.id" @refreshFuncRegionAttrAssignToList="refreshFuncRegionAttrAssignToList" @hideFuncRegionAttrAssignCopyPage="hideFuncRegionAttrAssignCopyPage"/>

<!-- 复制功能区域绑定属性信息(从其他功能) -->
<copyFuncRegionAttrAssignFromOther v-if="showFuncRegionAttrAssignCopyFromOther==true" :showFuncRegionAttrAssignCopyFromOther="showFuncRegionAttrAssignCopyFromOther" :systemId="funcRegion.systemId" :targetFuncId="funcRegion.funcId" :targetRegionId="funcRegion.id" @refreshFuncRegionAttrAssignToList="refreshFuncRegionAttrAssignToList" @hideFuncRegionAttrAssignCopyPageFromOther="hideFuncRegionAttrAssignCopyPageFromOther"/>

<!-- 编辑对象绑定信息 -->
<editObjectAssign v-if="showObjectAssignEdit==true && objectAssign != null" :showObjectAssignEdit="showObjectAssignEdit" :objectAssign="objectAssign" :mainObjectAssign="mainObjectAssign" :objectAssigns="objectAssigns" @updateObjectAssignToList="updateObjectAssignToList" @hideObjectAssignEditPage="hideObjectAssignEditPage"/>

</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import {formatAttrDisplayInfo} from '../../../funcTpl/js/common.js'
import designer from './designer.vue'
import undisplayObjectAttrs from './funcRegionDesigner-undisplayObjectAttrs.vue'
import attrAssignEdit from './funcRegionDesigner-attrAssignEdit.vue'
import operationAdd from './funcRegionDesigner-operationAdd.vue'
import operationEdit from './funcRegionDesigner-operationEdit.vue'

import copyFuncRegionAttrAssign from '../../../workbench/components/funcMgr/workbench-funcRegionAttrAssignCopy.vue'
import copyFuncRegionAttrAssignFromOther from '../../../workbench/components/funcMgr/workbench-funcRegionAttrAssignCopyFromOther.vue'

import editObjectAssign from '../../../workbench/components/funcMgr/workbench-FuncObjectAssignEdit.vue'

export default {
    props: ['showRegionDesignerPage','funcRegion', 'objectMap', 'targetRegionMap', 'funcRegions', 'objectAssigns'],
    
    emits: ['hideRegionDesigner'],
    
    setup (props, {attrs, emit, slots}) {
        const hideRegionDesigner = () => {
            emit('hideRegionDesigner')
        };
        return {
        	hideRegionDesigner
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showRegionDesignerPage;
            },
            set(value) {
                this.hideRegionDesigner();
            }
        }
    },
    components: {
    	designer,
        undisplayObjectAttrs,
        attrAssignEdit,
        operationAdd,
        operationEdit,

        copyFuncRegionAttrAssign,
        copyFuncRegionAttrAssignFromOther,

        editObjectAssign
    },
    
    data() {   
        const attrAssignInfo = ref({});
        const subObjectAttrList = ref(null);
        const mainObject = ref(null);
        const subObjects = ref([]);
        const regionData = ref({});
        const objectAttrMapForSelect = ref({});
        const subAttrNeedFormat = ref(false);
        const currentAttrAssign = ref(null);
        const currentRegionOperation = ref(null);

        const leftActiveName = ref("attrForSelectTab");
        const rightActiveName = ref("editPanel");
        const activeOtherOperation = ref("addOperation");

        const showFuncRegionAttrAssignCopy = ref(false);   	
    	const showFuncRegionAttrAssignCopyFromOther = ref(false);

        const showObjectAssignEdit = ref(false);
    	const objectAssign = ref(null);   	
    	const mainObjectAssign = ref(null);

        const displayTypeOptions = ref(null);
        const operationTypeOptions = ref(null);
        const pageDisplayTypeOptions = ref(null);

    	return {
            attrAssignInfo,
            subObjectAttrList,
            mainObject,
            subObjects,
            regionData,
            objectAttrMapForSelect,
            subAttrNeedFormat,
            currentAttrAssign,
            currentRegionOperation,
            leftActiveName,
            rightActiveName,
            activeOtherOperation,

            showFuncRegionAttrAssignCopy,
        	showFuncRegionAttrAssignCopyFromOther,

            showObjectAssignEdit,
        	objectAssign,
        	mainObjectAssign,
            displayTypeOptions,
            operationTypeOptions,
            pageDisplayTypeOptions
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['DISPLAY_TYPE','OPERATION_TYPE', 'DISPLAY_PAGE_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.displayTypeOptions = data.data['DISPLAY_TYPE'];
                this.operationTypeOptions = data.data['OPERATION_TYPE'];
                this.pageDisplayTypeOptions = data.data['DISPLAY_PAGE_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	this.initAttrDisplayInfo(this.funcRegion);
    },
    methods: {
    	initAttrDisplayInfo() {
            this.subObjects = [];
            this.subAttrNeedFormat = false;
            this.mainObject = null;
            this.regionData = {};
            this.attrAssignList = null;
            this.subObjectAttrList = null;
            if(this.funcRegion != null && this.funcRegion.regionAttrAssigns != null) {
                if(this.objectAssigns != null) {
                    for(var index in this.objectAssigns) {
                        if(this.objectAssigns[index].assignType == "M") {
                            this.mainObject = this.objectAssigns[index];
                        }else {
                            this.subObjects.push(this.objectAssigns[index]);
                            if(this.objectAssigns[index].mainRelAttribute.isListAttr == 'N') {
                                if(this.regionData[this.objectAssigns[index].mainRelAttribute.code] == null) {
                                    this.regionData[this.objectAssigns[index].mainRelAttribute.code] = {};
                                }
                                this.subAttrNeedFormat = true;
                            }else {
                                if(this.regionData[this.objectAssigns[index].mainRelAttribute.code] == null) {
                                    this.regionData[this.objectAssigns[index].mainRelAttribute.code] = [];
                                }
                            }
                        }
                    }
                }
                if(this.funcRegion != null && this.funcRegion.regionAttrAssigns != null) {
                    var mainAttrAssigns = [];//主对象绑定属性列表
                    var subObjectAttrMap = {};//从对象绑定属性Map
                    //将绑定属性按主对象、从对象进行分类
                    for(var index =0; index< this.funcRegion.regionAttrAssigns.length; index++) {
                        var currentAttrAssign = this.funcRegion.regionAttrAssigns[index];
                        //非主对象的属性按从对象分表格展示
                        if(currentAttrAssign.objectAssignId != this.mainObject.id) {
                            var attrAssignList = subObjectAttrMap[currentAttrAssign.objectAssignId];
                            if(attrAssignList == null) {
                                attrAssignList = [];
                                subObjectAttrMap[currentAttrAssign.objectAssignId] = attrAssignList;
                            }
                            attrAssignList.push(currentAttrAssign);
                        }else {
                            mainAttrAssigns.push(currentAttrAssign);
                        }
                        
                    }
                    //处理未显示属性
                    this.processUndisplayAttr(mainAttrAssigns, subObjectAttrMap);
                    //格式化显示
                    this.formatDisplay(mainAttrAssigns, subObjectAttrMap);
                }
            }
        },
        reformatDisplayInfo() {
            var index = 1;
            //处理属性设计信息
            var mainAttrAssigns = [];
            var subObjectAttrMap = {};
            this.processAttrDesignInfo(mainAttrAssigns, subObjectAttrMap)

            this.formatDisplay(mainAttrAssigns, subObjectAttrMap);
        },
        processAttrDesignInfo(mainAttrAssigns, subObjectAttrMap) {
            var index = 1;
            //处理主对象
            for(var i in this.attrAssignInfo.attrAssigns) {
                if(this.attrAssignInfo.attrAssigns[i].rowIndex != null) {
                    for(var j in this.attrAssignInfo.attrAssigns[i].attrAssigns) {
                        this.attrAssignInfo.attrAssigns[i].attrAssigns[j].displayOrder = index++;
                        mainAttrAssigns.push(this.attrAssignInfo.attrAssigns[i].attrAssigns[j]);
                    }
                }else {
                    this.attrAssignInfo.attrAssigns[i].displayOrder = index++;
                    mainAttrAssigns.push(this.attrAssignInfo.attrAssigns[i]);
                }
            }
            //处理从对象
            if(this.subObjectAttrList != null && this.subObjectAttrList.length > 0) {
                for(var subObjectIndex in this.subObjectAttrList) {
                    var subObjectRow = this.subObjectAttrList[subObjectIndex];
                    var currAttrAssignList = null;
                    index += 20;
                    if(this.subObjects[subObjectIndex].mainRelAttribute.isListAttr == 'N' || this.funcRegion.tplRegion.regionType.startsWith('S')) {//一对一对象或者从对象编辑区
                        currAttrAssignList = [];
                        for(var i in subObjectRow.attrAssigns) {
                            for(var j in subObjectRow.attrAssigns[i].attrAssigns) {
                                subObjectRow.attrAssigns[i].attrAssigns[j].displayOrder = index++;
                                currAttrAssignList.push(subObjectRow.attrAssigns[i].attrAssigns[j]);
                            }
                        }
                    }else {//一对多对象
                        for(var i in subObjectRow.attrAssigns) {
                            subObjectRow.attrAssigns[i].displayOrder = index++;
                        }
                        currAttrAssignList = subObjectRow.attrAssigns;
                    }
                    subObjectAttrMap[subObjectRow.id] = currAttrAssignList;
                }
            }
        },
        processOperationDesignInfo(mainAttrAssigns, subObjectAttrMap) {
            var index = 1;
            //处理主对象
            for(var i in this.funcRegion.regionOperations) {
               this.funcRegion.regionOperations[i].displayOrder = index++;
            }
        },
        formatDisplay(mainAttrAssigns, subObjectAttrMap){
            //结果区时，按表格的方式展示主对象属性
            if(this.funcRegion.tplRegion.regionType == 'MR') {
                this.attrAssignInfo = {
                    id: this.mainObject.id,
                    attrAssigns: mainAttrAssigns
                };
                return;
            }
            //格式化展示主对象属性
            var attrAssignMap = {};
            if(mainAttrAssigns != null && mainAttrAssigns.length > 0) {
                attrAssignMap = formatAttrDisplayInfo(mainAttrAssigns);
            }

            //存在一对一的从对象或者从对象编辑区，需要进行格式化展示
            if(this.subAttrNeedFormat || this.funcRegion.tplRegion.regionType.startsWith('S')) {
                if(this.subObjects != null) {
                    for(var index in this.subObjects) {
                        if(this.subObjects[index].assignType == "S" ) {//只处理从对象
                            if(this.subObjects[index].mainRelAttribute.isListAttr == 'N' || this.funcRegion.tplRegion.regionType.startsWith('S')) {//一对一的从对象
                                var subAttrAssigns = subObjectAttrMap[this.subObjects[index].id];
                                if(subAttrAssigns == null) {
                                    continue;
                                }
                                var subAttrAssignMap = formatAttrDisplayInfo(subAttrAssigns);
                                subObjectAttrMap[this.subObjects[index].id] = subAttrAssignMap;
                            }
                        }
                    }
                }
            }
            //生成主对象属性列表（分行）
            var attrAssignList = [];
            for(var key in attrAssignMap) {
                if(attrAssignMap[key] != null && attrAssignMap[key].length != 0) {
                    var attrAssignRow = {
                        rowIndex: key,
                        attrAssigns: attrAssignMap[key]
                    };
                    attrAssignList.push(attrAssignRow);
                }
            }
            this.attrAssignInfo = {
                id: this.mainObject.id,
                attrAssigns: attrAssignList
            }
            //生成从对象属性列表（分对象，如果一对一的从对象，再分行）
            this.subObjectAttrList = [];
            for(var index in this.subObjects) {
                var currAttrAssignList = null;
                if(this.subObjects[index].mainRelAttribute.isListAttr == 'N' || this.funcRegion.tplRegion.regionType.startsWith('S')) {//一对一对象
                    var currAttrAssignMap = subObjectAttrMap[this.subObjects[index].id];
                    currAttrAssignList = [];
                    for(var key in currAttrAssignMap) {
                        if(currAttrAssignMap[key] != null && currAttrAssignMap[key].length != 0) {
                            var subAttrAssignRow = {
                                rowIndex: key,
                                attrAssigns: currAttrAssignMap[key]
                            };
                            currAttrAssignList.push(subAttrAssignRow);
                        }
                    }
                }else {//一对多对象
                    currAttrAssignList = subObjectAttrMap[this.subObjects[index].id];
                }
                var currSubAttrAssignRow = {
                    id: this.subObjects[index].id,
                    attrAssigns: currAttrAssignList
                };
                this.subObjectAttrList.push(currSubAttrAssignRow);
            }

            console.log(this.attrAssignInfo, this.subObjectAttrList);
        },
        processUndisplayAttr(mainAttrAssigns, subObjectAttrMap) {
            //处理未显示的属性
            var undisplayObjectAttrMap = {};
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
                for(var attrIndex in objectAssign.assignObject.attributes) {
                    var attribute = objectAssign.assignObject.attributes[attrIndex];
                    var isDisplay = false;
                    
                    if(objectAssign.assignType == 'M') {
                        for(var i in mainAttrAssigns) {
                            if(attribute.id == mainAttrAssigns[i].attributeId) {
                                isDisplay = true;
                                break;
                            }
                        }
                    }else {
                        var subObjectAttrs = subObjectAttrMap[objectAssign.id]
                        for(var i in subObjectAttrs) {
                            if(attribute.id == subObjectAttrs[i].attributeId) {
                                isDisplay = true;
                                break;
                            }
                        }
                    }
                    if(!isDisplay) {
                        var undispalyAttr = {
                            attribute: attribute,
                            attributeId: attribute.id,
                            displayType: 'I',
                            colSpan: 1,
                            rowSpan: 1,
                            displayName: attribute.name,
                            funcId: objectAssign.funcId,
                            funcRegionId: this.funcRegion.id,
                            objectAssignId: objectAssign.id,
                            objectId: objectAssign.objectId,
                            objectType: objectAssign.objectType,
                            systemId: objectAssign.systemId,
                            tenantId: objectAssign.tenantId
                        };
                        assignObject.undispalyAttrs.push(undispalyAttr);
                    }
                }
                
            }
            var undisplayObjectAttrs = [];
            for(var key in undisplayObjectAttrMap) {
                undisplayObjectAttrs.push(undisplayObjectAttrMap[key]);
            }
            this.objectAttrMapForSelect = {
                undisplayObjectAttrs: undisplayObjectAttrs
            }
        },
        onChangeEnd(event) {
            this.reformatDisplayInfo();
        },
        deleteAttr(srcObject, attr) {
            if(srcObject == null || attr == null) {
                return;
            }
            if(srcObject.attrAssigns.length <= 1 && (srcObject.attrAssigns[0].attrAssigns == null || srcObject.attrAssigns[0].attrAssigns.length <=1)) {
                ElMessage.error(`不能删除所有属性！`);
                return;
            }
            for(var i in srcObject.attrAssigns) {
                var attrAssignsRow = srcObject.attrAssigns[i];
                if(attrAssignsRow.rowIndex != null) {
                    for(var j in attrAssignsRow.attrAssigns) {
                        var attrAssign = attrAssignsRow.attrAssigns[j];
                        if(attrAssign == attr) {
                            attrAssignsRow.attrAssigns.splice(j, 1);
                            break;
                        }
                    }
                }else {
                    if(attrAssignsRow == attr) {
                        srcObject.attrAssigns.splice(i, 1);
                        break;
                    }
                }
            }
            if(attr.attributeId != null) {
                for(var index in this.objectAttrMapForSelect.undisplayObjectAttrs) {
                    var undisplayObject = this.objectAttrMapForSelect.undisplayObjectAttrs[index];
                    if(undisplayObject.id == srcObject.id) {
                        undisplayObject.undispalyAttrs.push(attr);
                        break;
                    }
                }
            }
            
            this.reformatDisplayInfo();
        },
        onAttrSelected(attrAssign) {
            if(attrAssign.attributeId == null) {
                return;
            }
            if(this.currentAttrAssign != null && this.currentAttrAssign.attributeId == attrAssign.attributeId) {
                return;
            }
            this.currentAttrAssign = attrAssign;
            this.currentRegionOperation = null;
        },
        onOperationSelected(regionOperation) {
            if(this.currentRegionOperation != null && this.currentRegionOperation == regionOperation) {
                return;
            }
            this.currentRegionOperation = regionOperation;
            this.currentAttrAssign = null;
        },
        addFuncRegionOperation(regionOperation) {
            if(this.funcRegion.regionOperations == null) {
                this.funcRegion.regionOperations = [];
            }
            this.funcRegion.regionOperations.push(regionOperation);
        },
        deleteRegionOperation(index) {
            ElMessageBox.confirm(
                    '该区域操作将被删除. 是否继续?',
                    '警告',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning',
                    }
                ).then(() => {
                    if(this.currentRegionOperation == this.funcRegion.regionOperations[index]) {
                        this.currentRegionOperation = null;
                    }
                    this.funcRegion.regionOperations.splice(index,1);
                });
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
                var mainAttrAssigns = [];
                var subObjectAttrMap = {};
                this.processAttrDesignInfo(mainAttrAssigns, subObjectAttrMap);
                this.processOperationDesignInfo();
                for(var key in subObjectAttrMap) {
                    if(subObjectAttrMap[key] != null && subObjectAttrMap[key].length > 0) {
                        mainAttrAssigns.push(...subObjectAttrMap[key]);
                    }
                }
                for(var i in mainAttrAssigns) {
                    if(mainAttrAssigns[i].attributeId == null) {
                        ElMessage.error(`请删除除所有的占位属性（名称为‘请删除’）！`);
                        return;
                    }
                }
                var designInfo = {
                    funcId: this.funcRegion.funcId,
                    funcRegionId: this.funcRegion.id,
                    attrAssigns: mainAttrAssigns,
                    regionOperations: this.funcRegion.regionOperations
                }
                console.log(designInfo);
                var url = "/lcdp-proxy/lowcode/platform/fe/api/funcRegion/saveDesignInfo";
                axiosClient.post(url, designInfo).then((response) => {
                    var data = response.data;
                    if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                        ElMessage(`保存成功。`);
                        this.hideRegionDesigner();
                    }
                }).catch((ex)=>{
                    ElMessage.error(`保存失败！` + ex);
                });
            }).catch((e)=>{
                ElMessage.error(`保存失败！` + e);
            });
        },
        showFuncRegionAttrAssignCopyPage() {
            this.showFuncRegionAttrAssignCopy = true;
        },
        hideFuncRegionAttrAssignCopyPage() {
            this.showFuncRegionAttrAssignCopy = false;
        },
        showFuncRegionAttrAssignCopyPageFromOther() {
            this.showFuncRegionAttrAssignCopyFromOther = true;
        },
        hideFuncRegionAttrAssignCopyPageFromOther() {
            this.showFuncRegionAttrAssignCopyFromOther = false;
        },
        refreshFuncRegionAttrAssignToList(data) {
            this.funcRegion.regionAttrAssigns = data;
            this.showFuncRegionAttrAssignCopy = false;
            this.showFuncRegionAttrAssignCopyFromOther = false;
            this.initAttrDisplayInfo();
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
                }else {
                    ElMessage.error(`加载对象绑定信息失败`);
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
                this.hideRegionDesigner();
            }).catch((e)=>{
                
            });
        }
    }
 }
</script>