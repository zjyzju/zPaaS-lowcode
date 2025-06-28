<template>
    <div v-if="mainPage != null">
        <customizedPageComponent :customizedPage="mainPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="null" :srcOperation="null" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPage" @refreshTableData="refreshTableData"/>
    </div>

    <el-dialog v-model="showDialog" v-if="showDialog && customizePageForShow != null && regionData[customizePageForShow.id] != null" :title="customizePageForShow.name"  width="85vw" top="4vh" append-to-body>
        <template #header>
            <span class="title">{{ customizePageForShow.name }}</span>
        </template>
        <customizedPageSub :funcDefine="funcDefine" :customizedPage="customizePageForShow" :regionData="regionData[customizePageForShow.id]" :parentPage="mainPage" :srcOperation="srcOperation"  @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPage" @refreshTableData="refreshTableData"/>
    </el-dialog>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref} from 'vue';
import { ElMessage } from 'element-plus'
import {isJsonObject, isJsonArray, funcDefinPreProcess, filterFuncRegions, processLabelValue, findCustomizedLayouts} from '../../js/common.js'

import customizedPageComponent from './customizedPage.vue'
import customizedPageSub from './customizedPage.vue'

export default {
    components: {
        customizedPageComponent,
        customizedPageSub
    },
    
    data() {   
        const funcDefine = ref(null);
        const funcId = ref(null);
        const funcParams = ref(null);
        const mainPage = ref(null);
        const regionData = ref({});
        const mainObject = ref(null);
        const subObjects = ref(null);
        const objectAssigns = ref(null);
        const showDialog = ref(false);
        const customizePageForShow = ref(null);
        const srcOperation = ref(null);
        
        return {
            funcId,
            funcParams,
            funcDefine,
            mainPage,
            regionData,
            mainObject,
            subObjects,
            objectAssigns,
            showDialog,
            customizePageForShow,
            srcOperation
        }
    },
    mounted() {
        var url = document.location;
        var searchString = url.search.substring(1);
        var params = searchString.split("&");
        
        params.forEach(item=>{
            var kv = item.split("=");
            if("funcId" == kv[0]) {
                this.funcId = kv[1];
                return;
            }else if("params" == kv[0] && kv[1] != null) {
                this.funcParams = JSON.parse(unescape(kv[1]));
                if(this.funcParams != null) {
                    for(var key in this.funcParams) {
                        this.regionData[key] = this.funcParams[key];
                    }
                }
            }
        })
        this.reloadFuncDefineAll();
    },
    methods: {
        reloadFuncDefineAll() {
            if(this.funcId == null) {
                return;
            }
            var url = "/lcdp-proxy/lowcode/fe/api/funcDefine/loadAll/" + this.funcId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                    this.funcDefine = data.data;
                    //进行数据处理，方便前台界面使用---start
                    funcDefinPreProcess(this.funcDefine);
                    //进行数据处理，方便前台界面使用---end
                    document.title = this.funcDefine.name;//设置功能页面的名称
                    if(this.funcDefine != null && this.funcDefine.customizedPages != null && this.funcDefine.customizedPages.length > 0) {
                        for(var i in this.funcDefine.customizedPages) {
                            if(this.funcDefine.customizedPages[i].isMainPage == 'Y') {
                                this.mainPage = this.funcDefine.customizedPages[i];
                                break;
                            }
                        }
                    }
                    if(this.mainPage == null) {
                        ElMessage.error("not found main page!")
                        return;
                    }
                    this.initAttrDisplayInfo();
                }
            });
        },
        initAttrDisplayInfo() {
           //初始化主对象、子对象、是否有子对象属性需要格式化展示以及regionData的属性初始化
            this.subObjects = [];
            this.mainObject = null;
            // this.subAttrNeedFormat = false;
            this.objectAssigns = this.funcDefine.objectAssigns;
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
                            // this.subAttrNeedFormat = true;
                        }else {
                            if(this.objectAssigns[index].mainRelAttribute != null && this.regionData[this.objectAssigns[index].mainRelAttribute.code] == null) {
                                this.regionData[this.objectAssigns[index].mainRelAttribute.code] = [];
                            }
                        }
                    }
                }
            }
        },
        showDialogPage(customizedPageId, data, srcOperation) {
            var customizedPage = null;
            for(var i = 0; i<this.funcDefine.customizedPages.length; i++) {
                if(this.funcDefine.customizedPages[i].id == customizedPageId) {
                    customizedPage = this.funcDefine.customizedPages[i];
                    break;
                }
            }
            if(customizedPage == null) {
                ElMessage.error("invalid customizedPageId!");
            }
            this.customizePageForShow = customizedPage;
            this.srcOperation = srcOperation;
            this.regionData[this.customizePageForShow.id] = data;
            if(data != null && srcOperation.operationType != 'P') {
                var customizedLayouts = findCustomizedLayouts(this.customizePageForShow.customizedLayouts, null);
                for(var i in customizedLayouts) {
                    var targetLayout = customizedLayouts[i];
                    if(targetLayout != null && (targetLayout.componentCode == 'MCL' || targetLayout.componentCode == 'NCL')  && targetLayout.layoutRegions != null && targetLayout.layoutRegions.length > 0 && targetLayout.layoutRegions[0].funcRegion != null) {
                        processLabelValue(targetLayout.layoutRegions[0].funcRegion, this.regionData[this.customizePageForShow.id], null, true, this.funcDefine);
                    }
                }
            }
            this.showDialog = true;
        },
        hideDialogPage() {
            this.showDialog = false;
            this.regionData[this.customizePageForShow.id] = null;
            this.customizePageForShow = null
            this.srcOperation = null;
        },
        objectUpdated(updateObject, operation) {
            //console.log(updateObject, operation);
            var refreshTargetPage = operation.operationCfgJSON.refreshTargetPage;
            var refreshTarget = operation.operationCfgJSON.refreshTarget;
            var updateMapping = operation.operationCfgJSON.updateMapping;
            var targetPage = null;
            for(var i in this.funcDefine.customizedPages) {
                if(this.funcDefine.customizedPages[i].id == refreshTargetPage) {
                    targetPage = this.funcDefine.customizedPages[i];
                    break;
                }
            }
            if(targetPage == null) {
                ElMessage.error('invalid refreshTargetPage!')
            }
            var targetRegionData = this.regionData;
            if(targetPage.isMainPage != 'Y') {
                targetRegionData = this.regionData[targetPage.id];
            }
            //过滤需要重置的区域列表
            var layouts = [];
            if(refreshTarget != null && refreshTarget.length > 0) {
                layouts = findCustomizedLayouts(targetPage.customizedLayouts, refreshTarget);
            } 
            var updateMappingJSON = null;
            if(updateMapping != null) {
                if(typeof(updateMapping) == 'string') {
                    if(updateMapping.trim().length > 0) {
                        updateMappingJSON = JSON.parse(updateMapping);
                    }
                }else {
                    updateMappingJSON = updateMapping;
                }
            }
            for(var i in layouts) {
                if(layouts[i].componentCode == 'MCL') {//表单
                    var funcRegion = layouts[i].layoutRegions[0].funcRegion;
                    var retainHistory = false;
                    if(operation.operationCfgJSON != null && operation.operationCfgJSON.retainHistory == "Y") {
                        retainHistory = true;
                    }
                    if(updateMappingJSON != null) {
                        for(var i in updateMappingJSON) {
                            var mapping = updateMappingJSON[i];
                            if(mapping.targetAttr != null && mapping.targetAttr.trim().length > 0) {
                                if(retainHistory && targetRegionData[mapping.targetAttr] == null) {
                                    targetRegionData[mapping.targetAttr] = "";
                                }
                                if(mapping.valueType == "F") {//固定值
                                    if(retainHistory) {
                                        targetRegionData[mapping.targetAttr] += mapping.value;
                                    }else {
                                        targetRegionData[mapping.targetAttr] = mapping.value;
                                    }
                                }else if(mapping.valueType == "A") {//属性值
                                    if(mapping.value == "/") {
                                        if(retainHistory) {
                                            targetRegionData[mapping.targetAttr] += updateObject
                                        }else {
                                            targetRegionData[mapping.targetAttr] = updateObject;
                                        }
                                    }else {
                                        if(retainHistory) {
                                            targetRegionData[mapping.targetAttr] += updateObject[mapping.value]
                                        }else {
                                            targetRegionData[mapping.targetAttr] = updateObject[mapping.value];
                                        }
                                    }
                                }
                            }else{
                                if(retainHistory) {
                                    if(targetRegionData[mapping.value] == null) {
                                        targetRegionData[mapping.value] = "";
                                    }
                                    targetRegionData[mapping.value] += updateObject[mapping.value]
                                }else {
                                    targetRegionData[mapping.value] = updateObject[mapping.value];
                                }
                            }
                        }
                    }else {
                        if(updateObject[attrAssign.attribute.code] != undefined) {
                            if(retainHistory) {
                                if(targetRegionData[attrAssign.attribute.code] == null) {
                                    targetRegionData[attrAssign.attribute.code] = "";
                                }
                                targetRegionData[attrAssign.attribute.code] += updateObject[attrAssign.attribute.code]
                            }else {
                                targetRegionData[attrAssign.attribute.code] = updateObject[attrAssign.attribute.code];
                            }
                        }
                    }
                    processLabelValue(funcRegion, targetRegionData, null, false, this.funcDefine);
                }else if(layouts[i].componentCode == 'NCL') {//表格
                    processLabelValue(layouts[i].layoutRegions[0].funcRegion, updateObject, null, false, this.funcDefine);
                    if(operation.operationType == 'P') {
                        if(layouts[i].layoutCfgJSON != null && layouts[i].layoutCfgJSON.tableDataSourceAttr != null && layouts[i].layoutCfgJSON.tableDataSourceAttr.length > 0) {
                            targetRegionData[layouts[i].layoutCfgJSON.tableDataSourceAttr].push(updateObject);
                        }else {
                            if(targetRegionData[layouts[i].id] != null) {//表格选中的数据
                                targetRegionData[layouts[i].id].data.push(updateObject);
                            }
                        }
                    }else  {
                        if(layouts[i].layoutCfgJSON != null && layouts[i].layoutCfgJSON.tableDataSourceAttr != null && layouts[i].layoutCfgJSON.tableDataSourceAttr.length > 0) {
                            targetRegionData[layouts[i].layoutCfgJSON.tableDataSourceAttr][this.regionData[layouts[i].id].rowIndex] = updateObject;
                        }else {
                            if(targetRegionData[layouts[i].id] != null) {//表格选中的数据
                                targetRegionData[layouts[i].id].data[this.regionData[layouts[i].id].rowIndex] = updateObject;
                            }
                        }
                    }
                }else if(layouts[i].componentCode == 'TCL') {//树形容器
                    if(layouts[i].layoutCfgJSON != null && layouts[i].layoutCfgJSON.treeDataSourceAttr != null && layouts[i].layoutCfgJSON.treeDataSourceAttr.length > 0) {
                        targetRegionData[layouts[i].layoutCfgJSON.treeDataSourceAttr] = updateObject;
                    }else {
                        if(targetRegionData[layouts[i].id] != null) {
                            targetRegionData[layouts[i].id].data = updateObject;
                        }
                    }
                }
            }
            
        },
        refreshTableData(currentPage, customizedLayoutId, clearCondition) {//customizedLayout是id
            if(this.regionData[customizedLayoutId].originQueryInfo == null) {
                return;
            }
            const operation = this.regionData[customizedLayoutId].originQueryInfo.operation;
            const req = this.regionData[customizedLayoutId].originQueryInfo.req;
            //如果需要清空请求参数
            if(clearCondition == true && req != null && req.reqData != null) {
                for(var key in req.reqData) {
                    if(key == "pageParam") {//分页信息，不清空
                        continue;
                    }
                    var param = req.reqData[key];
                    if(isJsonObject(param)) {//Json对象
                        for(var key1 in param) {
                            param[key1] = null;
                        }
                    }else if(isJsonArray(param)) {
                        var isJson = fasle;//是否json对象的数组
                        for(var i in param) {
                            if(typeof(param[i]) == 'object' && Object.prototype.toString.call(param[i]).toLowerCase() == '[object object]' && !param[i].length) {//Json对象
                                for(var key1 in param[i]) {
                                    param[i][key1] = null;
                                }
                                isJson = true;
                            }
                        }
                        if(!isJson) {
                            req.reqData[key] = null;
                        }
                    }else {
                        req.reqData[key] = null;
                    }
                }
                currentPage = 1;
            }
            var url = "/lcdp-proxy/lowcode/" + this.funcDefine.platformUrl + "/" + this.funcDefine.systemUrl + "/" + operation.exposedServiceMapping;
            if(this.regionData[customizedLayoutId].pageParam != null) {
                if(currentPage == null && this.regionData[customizedLayoutId].pageParam.pageNo == null) {
                    currentPage = 1;
                }
                if(currentPage != null) {
                    this.regionData[customizedLayoutId].pageParam.pageNo = currentPage;
                }
                req.reqData["pageParam"] = this.regionData[customizedLayoutId].pageParam;
                req.reqData["pageParam"].queryTotal = true;
            }
            axiosClient.post(url, req).then((response) => {
                var result = response.data;
                if(result != null && result.status == '200') {
                    this.regionData[customizedLayoutId].data = result.data;
                    this.regionData[customizedLayoutId].pageParam = result.pageParam;
                    if(result.data != null) {
                        var funcRegions = [];
                        var customizedLayoutIds = [];
                        customizedLayoutIds.push(customizedLayoutId)
                        for(var k in this.funcDefine.customizedPages) {
                            filterFuncRegions(this.funcDefine.customizedPages[k].customizedLayouts, funcRegions, customizedLayoutIds);
                        }
                        if(funcRegions.length > 0) {
                            processLabelValue(funcRegions[0], this.regionData[customizedLayoutId].data, null, true, this.funcDefine);
                        }
                    }
                    
                }else {
                    ElMessage.error(`请求后端服务失败！状态码：` + result.status + `，错误信息：` + result.message );
                }
            }).catch((error)=>{
                ElMessage.error(`请求后端服务失败！` + error);
            });
        }
    } 
}
</script>