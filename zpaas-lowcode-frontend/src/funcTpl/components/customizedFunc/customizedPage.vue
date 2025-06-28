<template>
    <el-form  :id="customizedPage.name"  :model="regionData" style="width: 98%;" :rules="validateRules" ref="customizedPageForm">
        <div v-if="customizedPage != null" v-for="customizedLayout in customizedPage.customizedLayouts" style="width:98%;margin-left: 10px;">
            <formContainer v-if="customizedLayout.componentCode == 'MCL'" :customizedLayout="customizedLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation"/>
            <reportContainer v-if="customizedLayout.componentCode == 'RCL'" :customizedLayout="customizedLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation"/>
            <chartContainer v-if="customizedLayout.componentCode == 'CCL'" :customizedLayout="customizedLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation"/>
            <tableContainer v-else-if="customizedLayout.componentCode == 'NCL'" :customizedLayout="customizedLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData"  :parentPage="parentPage" :srcOperation="srcOperation" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPageSub" @refreshTableData="refreshTableData"/>
            <verticalLayoutContainer v-else-if="customizedLayout.componentCode == 'QLC'" :customizedLayout="customizedLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation" :customizedPageForm="this.$refs.customizedPageForm" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPageSub" @refreshTableData="refreshTableData"/>
            <buttonContainer v-else-if="customizedLayout.componentCode == 'BCL'" :customizedLayout="customizedLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation" :customizedPageForm="this.$refs.customizedPageForm" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPageSub" @refreshTableData="refreshTableData"/>
            <tabContainer v-else-if="customizedLayout.componentCode == 'OLC'" :customizedLayout="customizedLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData" :parentPage="parentPage" :srcOperation="srcOperation" :customizedPageForm="this.$refs.customizedPageForm" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPageSub" @refreshTableData="refreshTableData"/>
            <dividerComponent v-else-if="customizedLayout.componentCode == 'DOC'" :customizedLayout="customizedLayout"/>
            <brComponent v-else-if="customizedLayout.componentCode == 'LOC'" :customizedLayout="customizedLayout"/>
            <treeContainer v-else-if="customizedLayout.componentCode == 'TCL'" :customizedLayout="customizedLayout" :customizedPage="customizedPage" :funcDefine="funcDefine" :regionData="regionData"  :parentPage="parentPage" :srcOperation="srcOperation" @hideDialogPage="hideDialogPage" @objectUpdated="objectUpdated" @showDialogPage="showDialogPageSub" @refreshTableData="refreshTableData"/>
            <anchorComponent v-else-if="customizedLayout.componentCode == 'AOC'" :customizedLayout="customizedLayout"/>
        </div>
    </el-form>
    <el-dialog v-model="showDialogSub" v-if="showDialogSub && customizePageForShowSub != null && regionData[customizePageForShowSub.id] != null" :title="customizePageForShowSub.name"  width="70vw" top="4vh" append-to-body>
        <template #header>
            <span class="title">{{ customizePageForShowSub.name }}</span>
        </template>
        <customizedPageSub :funcDefine="funcDefine" :customizedPage="customizePageForShowSub" :regionData="regionData[customizePageForShowSub.id]" :parentPage="customizedPage" :srcOperation="srcOperationSub"  @hideDialogPage="hideDialogPageSub" @objectUpdated="objectUpdatedSub" @showDialogPage="showDialogPageSub" @refreshTableData="refreshTableData"/>
    </el-dialog>
</template>
<script>
import { ref, defineAsyncComponent} from 'vue';
import { ElMessage } from 'element-plus'
import {isJsonEmpty, processPreValidateRules, filterFuncRegions, interactionProcess, processLabelValue, findCustomizedLayouts} from '../../js/common.js'

import formContainer from './formContainer.vue'
import tableContainer from './tableContainer.vue'
import buttonContainer from './buttonContainer.vue'
import tabContainer from './tabContainer.vue'
import verticalLayoutContainer from './verticalLayoutContainer.vue'
import dividerComponent from './dividerComponent.vue'
import brComponent from './brComponent.vue'
import treeContainer from './treeContainer.vue'
import anchorComponent from './anchorComponent.vue'
import reportContainer from './reportContainer.vue'
import chartContainer from './chartContainer.vue'

const customizedPageSub = defineAsyncComponent(() => import('./customizedPage.vue'));

export default {
    props: ['funcDefine', 'customizedPage', 'regionData', 'parentPage', 'srcOperation'],

    emits: ['hideDialogPage', 'objectUpdated', 'showDialogPage', 'refreshTableData'],
    
    setup(props, {attrs, emit, slots}) {
        const hideDialogPage = ()=> {
            emit('hideDialogPage');
        }
        const objectUpdated = (updateObject, operation) => {
            emit('objectUpdated', updateObject, operation);
        }
        const showDialogPage = (customizedPageId, data, srcOperation) => {
            emit('showDialogPage', customizedPageId, data, srcOperation);
        }
        const refreshTableData = (currentPage, customizedPageId, clearCondition) => {
            emit('refreshTableData', currentPage, customizedPageId, clearCondition);
        }
        return {
            hideDialogPage,
            objectUpdated,
            showDialogPage,
            refreshTableData
        }
    }, 
    components: {
        formContainer,
        tableContainer,
        buttonContainer,
        tabContainer,
        verticalLayoutContainer,
        dividerComponent,
        brComponent,
        customizedPageSub,
        treeContainer,
        anchorComponent,
        reportContainer,
        chartContainer
    },   
    data() {   
        const showDialogSub = ref(false);
        const customizePageForShowSub = ref(null);
        const srcOperationSub = ref(null);
        const formRegions = ref([]);
        const interactionFlag = ref(false);
        const validateRules = ref([]);
        
        return {
            showDialogSub,
            customizePageForShowSub,
            srcOperationSub,
            formRegions,
            interactionFlag,
            validateRules
        }
    },
    computed: {
        regionData1: function() {
            return JSON.parse(JSON.stringify(this.regionData));
        }
    },
    watch: {        
        'regionData1': {
            handler: function(newRegionData, oldRegionData){
            	if(!this.interactionFlag) {
                    return;//当前不存在联动控制时，不进行联动处理
                }
                interactionProcess(newRegionData, oldRegionData, this.funcDefine, this.formRegions, null, this.regionData);//联动处理
            },
            deep:true // 深度监听的参数
        }
    },
    mounted() {
        filterFuncRegions(this.customizedPage.customizedLayouts, this.formRegions, null, 'M');

        if(this.formRegions.length > 0) {
            for(var i in this.formRegions) {

                //处理预校验规则
                if(this.formRegions[i] != null && this.formRegions[i].regionOperations != null) {
                    var rules = processPreValidateRules(this.formRegions[i].regionOperations, null);
                    console.log(rules);
                    if(rules != null && rules.length > 0) {
                        this.validateRules = rules;
                    }
                }

                var attrAssigns = this.formRegions[i].regionAttrAssigns;
                for(var index in attrAssigns) {
                    //进行联动控制的判断
                    var currentAttrObject = this.funcDefine.objectMap[attrAssigns[index].objectId];
                    if(currentAttrObject.attrOptionMap == null) {
                        continue;
                    }
                    var attrOption = currentAttrObject.attrOptionMap[attrAssigns[index].attributeId];//当前绑定属性的配置信息
                    if(attrOption == null) {
                        continue;
                    }
                    if((attrOption.interactionType != null && attrOption.interactionType != "N") || attrOption.parentAttributeId != null) {
                        this.interactionFlag = true;//当前区域存在联动控制时，设置为true
                        break;
                    }
                }
                if(this.interactionFlag) {
                    break;
                }             
            }
        }

        var buttonRegions = [];
        filterFuncRegions(this.customizedPage.customizedLayouts, buttonRegions, null, 'B');
        if(buttonRegions != null && buttonRegions.length > 0) {
            for(var i in buttonRegions) {
                //处理预校验规则
                if(buttonRegions[i] != null && buttonRegions[i].regionOperations != null) {
                    var rules = processPreValidateRules(buttonRegions[i].regionOperations, null);
                    if(!isJsonEmpty(rules)) {
                        this.validateRules = rules;
                    }
                }
            }
        }
    },
    methods: {
        showDialogPageSub(customizedPageId, data, srcOperation) {
            if(this.parentPage == null) {
                this.showDialogPage(customizedPageId, data, srcOperation);
                return;
            }
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
            this.customizePageForShowSub = customizedPage;
            this.srcOperationSub = srcOperation;
            this.regionData[this.customizePageForShowSub.id] = data;
            if(data != null) {
                var customizedLayouts = findCustomizedLayouts(this.customizePageForShowSub.customizedLayouts, null);
                for(var i in customizedLayouts) {
                    var targetLayout = customizedLayouts[i];
                    if(targetLayout != null && (targetLayout.componentCode == 'MCL' || targetLayout.componentCode == 'NCL') && targetLayout.layoutRegions != null && targetLayout.layoutRegions.length > 0 && targetLayout.layoutRegions[0].funcRegion != null) {
                        processLabelValue(targetLayout.layoutRegions[0].funcRegion, this.regionData[this.customizePageForShowSub.id], null, false, this.funcDefine);
                    }
                }
            }
            this.showDialogSub = true;
        },
        hideDialogPageSub() {
            this.showDialogSub = false;
            this.regionData[this.customizePageForShowSub.id] = null;
            this.customizePageForShowSub = null
            this.srcOperationSub = null;
        },
        objectUpdatedSub(updateObject, operation) {
            console.log(updateObject, operation);
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
            //console.log(targetPage, this.regionData);
            var targetRegionData = this.regionData;
            //过滤需要更新的布局
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
                    if(funcRegion != null && funcRegion.regionAttrAssigns != null && funcRegion.regionAttrAssigns.length > 0) {
                        for (var index = 0; index < funcRegion.regionAttrAssigns.length; index++) {
                            var attrAssign = funcRegion.regionAttrAssigns[index];
                            if(updateMappingJSON != null) {
                                for(var i in updateMappingJSON) {
                                    var mapping = updateMappingJSON[i];
                                    if(attrAssign.attribute.code == mapping.targetAttr) {
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
                                        break;
                                    }else if((mapping.targetAttr == null || mapping.targetAttr.trim().length == 0) && mapping.valueType == "A" && attrAssign.attribute.code == mapping.value) {
                                        if(retainHistory) {
                                            if(targetRegionData[mapping.value] == null) {
                                                targetRegionData[mapping.value] = "";
                                            }
                                            targetRegionData[mapping.value] += updateObject[mapping.value]
                                        }else {
                                            targetRegionData[mapping.value] = updateObject[mapping.value];
                                        }
                                        break;
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
                        }
                        processLabelValue(funcRegion, targetRegionData, null, false, this.funcDefine);
                    }
                }else if(layouts[i].componentCode == 'NCL') {//表格
                    processLabelValue(layouts[i].layoutRegions[0].funcRegion, updateObject, null, false, this.funcDefine);
                    if(operation.operationType == 'P') {
                        if(layouts[i].layoutCfgJSON != null && layouts[i].layoutCfgJSON.tableDataSourceAttr != null && layouts[i].layoutCfgJSON.tableDataSourceAttr.length > 0) {
                            setTimeout(() => targetRegionData[layouts[i].layoutCfgJSON.tableDataSourceAttr].push(updateObject), 400);
                        }else {
                            if(targetRegionData[layouts[i].id] != null) {//表格选中的数据
                                setTimeout(() => targetRegionData[layouts[i].id].data.push(updateObject), 400);
                            }
                        }
                    }else  {
                        if(layouts[i].layoutCfgJSON != null && layouts[i].layoutCfgJSON.tableDataSourceAttr != null && layouts[i].layoutCfgJSON.tableDataSourceAttr.length > 0) {
                            setTimeout(() => targetRegionData[layouts[i].layoutCfgJSON.tableDataSourceAttr][this.regionData[layouts[i].id].rowIndex] = updateObject, 200);
                        }else {
                            if(targetRegionData[layouts[i].id] != null) {//表格选中的数据
                                setTimeout(() => targetRegionData[layouts[i].id].data[this.regionData[layouts[i].id].rowIndex] = updateObject,400);
                            }
                        }
                    }
                }
            }
        }
    } 
}
</script>