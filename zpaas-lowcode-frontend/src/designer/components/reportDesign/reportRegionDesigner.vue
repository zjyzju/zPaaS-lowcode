<template> 
<el-dialog v-model="showFlag" v-if="showReportRegionDesignerPage ==true" title="统计报表设计器" width="98vw" top="2vh"  :close-on-press-escape="false" :close-on-click-modal="false" :show-close="false">
    <template #header>
        <span class="title">统计报表设计器--{{ this.funcRegion.name }}</span>
    </template>
    <template #default>
        <div class="common-layout">
            <el-container  style="height: 81vh">
                <el-aside width="200px">
                    <el-tabs v-model="leftActiveName">
                        <el-tab-pane label="可选属性" name="attrForSelectTab">
                            <undisplayObjectAttrs :objectAttrMapForSelect="objectAttrMapForSelect" @onChangeEnd="onChangeEnd"/>
                        </el-tab-pane>
                        <el-tab-pane label="其他操作" name="otherTab">
                            <el-collapse v-model="activeOtherOperation">
                                <el-collapse-item title="编辑区域配置信息" name="editRegionCfg">
                                    <el-link @click="showRegionCfgEditPage()" type="primary" >{{ funcRegion.name }}</el-link>&nbsp;&nbsp;
                                </el-collapse-item>
                            </el-collapse>
                        </el-tab-pane>
                    </el-tabs>
                </el-aside>
                <el-divider direction="vertical" border-style="dashed" style="height:100%;margin-left: 1px;margin-right: 1px; color: cornflowerblue;"></el-divider>
                <el-main>
                    <!-- 可视化编辑器 -->
                    <reportDesigner  v-if="funcRegion.tplRegion.regionType == 'RM' && assignObject != null" :reportCfgData="reportCfgData" :funcRegion="funcRegion" :assignObject="assignObject" @onChangeEnd="onChangeEnd" @onAttrSelected="onAttrSelected"/>
                </el-main>
                <el-divider direction="vertical" border-style="dashed" style="height:100%;margin-left: 1px;margin-right: 1px; color: cornflowerblue;"></el-divider>
                <el-aside width="200px">
                    <el-tabs v-model="rightActiveName">
                        <el-tab-pane label="属性编辑" name="editPanel">
                            <attrAssignEdit v-if="currentAttrAssign != null" :funcRegionAttrAssign="currentAttrAssign" :objectAssigns="objectAssigns" :assignObject="objectMap[currentAttrAssign.objectId]" :reportCfgData="reportCfgData"/>
                        </el-tab-pane>
                    </el-tabs>
                </el-aside>
            </el-container>
        </div>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="closeReportDesigner()">取消</el-button>
        <el-button type="primary" @click="saveDesignInfo()">保存</el-button>
      </div>
    </template>
</el-dialog>

<!-- 编辑区域配置信息 -->
<regionCfgEdit v-if="showRegionCfgEdit==true && funcRegion != null" :showRegionCfgEdit="showRegionCfgEdit" :funcRegion="funcRegion" @hideRegionCfgEditPage="hideRegionCfgEditPage"/>

</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import {formatFormDisplay, getAllAttrAssigns, formatReportDisplay} from '../../js/common.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import undisplayObjectAttrs from './reportRegionDesigner-undisplayObjectAttrs.vue'
import reportDesigner from './reportDesigner.vue'
import attrAssignEdit from './reportRegionDesigner-attrAssignEdit.vue'
import regionCfgEdit from '../../../workbench/components/funcMgr/workbench-FuncRegionCfgEdit.vue'

export default {
    props: ['showReportRegionDesignerPage','funcRegion', 'objectMap', 'targetRegionMap', 'funcRegions', 'objectAssigns'],
    
    emits: ['hideReportRegionDesigner'],
    
    setup (props, {attrs, emit, slots}) {
        const hideReportRegionDesigner = () => {
            emit('hideReportRegionDesigner')
        };
        return {
        	hideReportRegionDesigner
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showReportRegionDesignerPage;
            },
            set(value) {
                this.hideReportRegionDesigner();
            }
        }
    },
    components: {
    	undisplayObjectAttrs,
        reportDesigner,
        attrAssignEdit,
        regionCfgEdit
    },
    
    data() {   
        const objectAttrMapForSelect = ref({});
        const currentAttrAssign = ref(null);
        const assignObject = ref(null);
        
        const leftActiveName = ref("attrForSelectTab");
        const rightActiveName = ref("editPanel");
        const activeOtherOperation = ref("editRegionCfg");

        const showRegionCfgEdit = ref(false);
        
        const reportCfgData = ref({
            reportAttrs: [],
            filterAttrs: [],
            tagAttrs: []
        });

        
    	return {
            objectAttrMapForSelect,
            currentAttrAssign,
            assignObject,
            leftActiveName,
            rightActiveName,
            activeOtherOperation,

            showRegionCfgEdit,
            
            reportCfgData
        }
    },
    mounted() {
        this.initAttrDisplayInfo();
    },
    methods: {
    	initAttrDisplayInfo() {
            if(this.objectAssigns != null && this.objectAssigns.length > 0) {
                this.assignObject = this.objectAssigns[0];
            }
            if(this.funcRegion != null && this.funcRegion.regionAttrAssigns != null) {
                if(this.funcRegion != null && this.funcRegion.regionAttrAssigns != null) {
                    var filterAttrs = [];
                    var reportAttrs = [];
                    var tagAttrs = [];
                    for(var index =0; index< this.funcRegion.regionAttrAssigns.length; index++) {
                        var attr = this.funcRegion.regionAttrAssigns[index];
                        //非主对象的属性按从对象分表格展示
                        if(attr.displayCfg == null || attr.displayCfg == '') {
                            attr.displayCfg = "{}";
                        }
                        attr.displayCfgJSON = JSON.parse(attr.displayCfg);
                        if(attr.displayCfgJSON.subRegionType == 'T') {
                            tagAttrs.push(attr);
                        }else if(attr.displayCfgJSON.subRegionType == 'F') {
                            filterAttrs.push(attr);
                        }else {
                            if(attr.attributeId.startsWith('N/A')) {
                                var attrCode = 'N/A-' + index;
                                if(attr.displayType == 'Q') {
                                    attrCode = 'common_sum_column-' + index;
                                } 
                                attr.dataSetDetail = {content:{code: attrCode,name: attr.displayName},detailType:'C'};
                            }
                            reportAttrs.push(attr);
                        }
                    }
                    this.reportCfgData.filterAttrs = formatFormDisplay(filterAttrs);
                    this.reportCfgData.reportAttrs = formatReportDisplay(reportAttrs);
                    this.reportCfgData.tagAttrs = tagAttrs;

                    //处理未显示属性
                    this.processUndisplayAttr();
                }
            }
        },
        processUndisplayAttr() {//处理未显示的属性
            var undisplayObjectAttrMap = {};
            var commonObject = {
                id: '$0011001&',
                undispalyAttrs: [
                    {
                        dataSetDetail: {content:{code:'common_sum_column',name:'累加列'},detailType:'C'},
                        attributeId: 'N/A',
                        displayType: 'Q',
                        colSpan: 8,
                        rowSpan: 1,
                        displayWidth: 150,
                        displayName: "累加列",
                        funcId: this.funcRegion.funcId,
                        funcRegionId: this.funcRegion.id,
                        objectAssignId: this.assignObject.id,
                        objectId: this.assignObject.objectId,
                        objectType: this.assignObject.objectType,
                        systemId: this.funcRegion.systemId,
                        tenantId: this.funcRegion.tenantId
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
            for(var index in this.objectAssigns) {
                var objectAssign = this.objectAssigns[index];
                var assignObject = undisplayObjectAttrMap[objectAssign.id];
                if(assignObject == null) {
                    assignObject = {
                        id: objectAssign.id,
                        undispalyAttrs: [],
                        filterAttrs: [],
                        tagAttrs: [],
                        objectAssign: objectAssign
                    };
                    undisplayObjectAttrMap[objectAssign.id] = assignObject;
                }
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
            var undisplayObjectAttrs = [];
            for(var key in undisplayObjectAttrMap) {
                undisplayObjectAttrs.push(undisplayObjectAttrMap[key]);
            }
            this.objectAttrMapForSelect = {
                undisplayObjectAttrs: undisplayObjectAttrs
            }
        },
        onChangeEnd(event) {
            //this.reformatDisplayInfo();
        },
        onAttrSelected(attrAssign) {
            if(this.currentAttrAssign != null && attrAssign.attributeId != null && this.currentAttrAssign.attributeId == attrAssign.attributeId) {
                return;
            }
            this.currentAttrAssign = attrAssign;
            this.currentRegionOperation = null;
        },
        syncReportAttrs(rootAttrs) {
            if(rootAttrs == null || rootAttrs.length == 0) {
                return null;
            }
            for(var i in rootAttrs) {
                var attr = rootAttrs[i];
                if(attr.displayCfgJSON != null) {
                    attr.displayCfg = JSON.stringify(attr.displayCfgJSON);
                }
                if(attr.attrAssigns != null && attr.attrAssigns.length > 0) {
                    this.syncReportAttrs(attr.attrAssigns);
                }
            }
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
                const filterAttrs = getAllAttrAssigns(this.reportCfgData.filterAttrs);
                this.syncReportAttrs(this.reportCfgData.reportAttrs);
                var designInfo = {
                    funcId: this.funcRegion.funcId,
                    funcRegionId: this.funcRegion.id,
                    reportAttrs: this.reportCfgData.reportAttrs,
                    tagAttrs: this.reportCfgData.tagAttrs,
                    filterAttrs: filterAttrs
                }
                console.log(designInfo);
                var url = "/lcdp-proxy/lowcode/platform/fe/api/funcRegion/saveReportDesignInfo";
                axiosClient.post(url, designInfo).then((response) => {
                    var data = response.data;
                    if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                        ElMessage(`保存成功。`);
                        this.hideReportRegionDesigner();
                    }
                }).catch((ex)=>{
                    ElMessage.error(`保存失败！` + ex);
                });
            }).catch((e)=>{
                ElMessage.error(`保存失败！` + e);
            });
        },
        closeReportDesigner() {
            ElMessageBox.confirm(
                '未保存的设计信息将会丢失，请确认是否关闭设计页面?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
            ).then(() => {
                this.hideReportRegionDesigner();
            }).catch((e)=>{
                
            });
        },
        showRegionCfgEditPage() {
            this.showRegionCfgEdit = true;
        },
        hideRegionCfgEditPage() {
            this.showRegionCfgEdit = false;
        }
    }
 }
</script>