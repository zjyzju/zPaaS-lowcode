<!-- 功能区域设计器-统计报表容器 -->
<template>
<div class="dragArea" :id="formComponent.name">
    <el-row :gutter="4">
        <el-col :span="24">
            <span class="floatDeleteFormContainer">
                <el-icon @click="onCustomizedLayoutSelected(formComponent)"><OfficeBuilding /></el-icon>
                <el-link class="floatDeleteForm" size="small" @click="deleteContainer(this.componentIndex)"><el-icon><Delete /></el-icon></el-link>
            </span>
        </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4" class="dragAreaSub" :id="formComponent.id+ '-filter'" v-if="this.formComponent.layoutCfgJSON != null && this.formComponent.layoutCfgJSON.showFilterArea == 'Y'">
        <el-col :span="24">
            <draggable v-model="formComponent.filterAttrs" tag="div" item-key="rowIndex">
                <template #item="{element : attrAssignRow}">
                    <draggable v-model="attrAssignRow.attrAssigns" @change="onFilterAttrChange" group="reportFilterContainer" tag="div" item-key="attributeId" class="el-row" style="margin-left: -2px; margin-right: -2px; min-height: 1em" >
                        <template #item="{element : attrAssign, index}">
                            <el-col :span="attrAssign.colSpan==null?0:parseInt(attrAssign.colSpan)" class="floatDeleteContainer"  @click="onAttrSelectedReport(attrAssign)">
                                <el-form-item :label="attrAssign.displayName" :required="attrAssign.required == 'Y'" :prop="attrAssign.dataSetDetail.code" size="default">
                                    <reportFilterComponent :regionData="selectedFilterValues" :attrAssign="attrAssign"/>
                                </el-form-item>
                                <el-link class="floatDelete" size="small" @click="deleteFilterAttr(attrAssign)"><el-icon><Delete /></el-icon></el-link>
                            </el-col>
                        </template>
                    </draggable>
                </template>
            </draggable>
        </el-col>
    </el-row>    
    <el-row :gutter="4" v-if="this.formComponent.layoutCfgJSON != null && this.formComponent.layoutCfgJSON.showFilterArea == 'Y'">
        <el-col :span="24">
            &nbsp;
        </el-col>
    </el-row>    
    <el-row :gutter="4" class="dragAreaSub" :id="formComponent.id+ '-tag'" v-if="this.formComponent.layoutCfgJSON != null && this.formComponent.layoutCfgJSON.showFilterArea == 'Y'">
        <el-col :span="4">筛选标签</el-col>
        <el-col :span="20">
            <draggable v-model="formComponent.tagAttrs" @change="onTagAttrChange" group="reportTagContainer" tag="div" item-key="attributeId" class="el-row" style="margin-left: -2px; margin-right: -2px; min-height: 1em" >
                <template #item="{element : attrAssign, index}">
                    <el-col :span="attrAssign.colSpan==null?0:parseInt(attrAssign.colSpan)" class="floatDeleteContainer"  @click="onAttrSelectedReport(attrAssign)">
                        <reportFilterComponent :regionData="selectedTags" :attrAssign="attrAssign"/>
                        <el-link class="floatDelete" size="small" @click="deleteTagAttr(attrAssign)"><el-icon><Delete /></el-icon></el-link>
                    </el-col>
                </template>
            </draggable>
        </el-col>
    </el-row>   
    <el-row :gutter="4" v-if="this.formComponent.layoutCfgJSON != null && this.formComponent.layoutCfgJSON.showFilterArea == 'Y'">
        <el-col :span="24">
            &nbsp;
        </el-col>
    </el-row>  
    <el-row :gutter="4">
        <el-col :span="16">
            &nbsp;
        </el-col>
        <el-col :span="8">
            <el-button  @click="loadData()" type="primary" size="small">提取数据</el-button>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            &nbsp;
        </el-col>
    </el-row>    
    <el-row :gutter="4" class="dragAreaSub" :id="'R' + formComponent.id">
        <el-col :span="24">
            <el-table v-if="formComponent != null && formComponent.reportAttrs != null && formComponent.reportAttrs.length > 0" :data="reportData" :show-summary="reportData != null && formComponent.layoutCfgJSON != null && formComponent.layoutCfgJSON.showSumRow == 'Y'" :summary-method="getSummaries" stripe style="width: 100%" highlight-current-row show-overflow-tooltip :span-method="spanTable">
                <loopTableColumnComponent v-for="attrAssign in formComponent.reportAttrs" :attrAssign="attrAssign" :parentAttrAssigns="formComponent.reportAttrs" :funcRegion="formComponent" @onAttrSelected="onAttrSelectedReport" @deleteAttr="deleteAttr" @addSiblingAttr="addSiblingAttr" @addSubAttr="addSubAttr"/>
	        </el-table>
        </el-col>
    </el-row>
</div>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import {formatFormDisplay, getAllAttrAssigns, reportCellValueFormat, formatReportDisplay} from '../../js/common.js'
import { ref } from 'vue';
import Sortable from 'sortablejs'
import draggable from 'vuedraggable'
import {ElMessage } from 'element-plus'
import loopTableColumnComponent from '../common/loopTableColumnComponent.vue'
import reportFilterComponent from '../common/reportFilterComponent.vue'

export default {
    props: ['componentIndex', 'formComponent'],
    
    emits: ['onAttrSelected', 'deleteContainer', 'onCustomizedLayoutSelected'],
    
    setup (props, {attrs, emit, slots}) {
        const onAttrSelected = (attrAssign, formComponent) => {
            emit('onAttrSelected', attrAssign, formComponent);
        };

        const onCustomizedLayoutSelected = (customizedLayout) => {
            emit('onCustomizedLayoutSelected', customizedLayout);
        };

        const deleteContainer = (componentIndex) => {
            emit('deleteContainer', componentIndex);
        };

        return {
        	onAttrSelected,
            onCustomizedLayoutSelected,
            deleteContainer
        }
    },
    computed: {
        
    },
    components: {
        reportFilterComponent,
        draggable,
    	loopTableColumnComponent
    },
    
    data() {   
        const reportData = ref(null);
        const selectedFilterValues = ref({});
        const selectedTags = ref({});
        const newAttrCount = ref(500);

    	return {
            reportData,
            selectedFilterValues,
            selectedTags,
            newAttrCount
        }
    },
    watch: {
        
    },
    mounted() {
        this.init();
    },
    methods: {
        init() {
            console.log(this.formComponent);
            if(this.formComponent.layoutCfg == null || this.formComponent.layoutCfg.trim().length == 0) {
                this.formComponent.layoutCfgJSON = {};
            }else {
                this.formComponent.layoutCfgJSON = JSON.parse(this.formComponent.layoutCfg);
            }
            if(this.formComponent.layoutRegions != null && this.formComponent.layoutRegions.length > 0 && this.formComponent.layoutRegions[0].funcRegion != null && this.formComponent.layoutRegions[0].funcRegion.regionAttrAssigns != null && this.formComponent.layoutRegions[0].funcRegion.regionAttrAssigns.length > 0) {
                var filterAttrs = [];
                var reportAttrs = [];
                var tagAttrs = [];
                for(var index =0; index< this.formComponent.layoutRegions[0].funcRegion.regionAttrAssigns.length; index++) {
                    var attr = this.formComponent.layoutRegions[0].funcRegion.regionAttrAssigns[index];
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
                this.formComponent.filterAttrs = formatFormDisplay(filterAttrs);
                this.formComponent.reportAttrs = formatReportDisplay(reportAttrs);
                this.formComponent.tagAttrs = tagAttrs;

            }
            if(this.formComponent.reportAttrs == null || this.formComponent.reportAttrs.length == 0) {
                this.formComponent.reportAttrs = [];
                var attr = this.createAttr('请修改');
                this.formComponent.reportAttrs.push(attr);
            }
            if(this.formComponent.filterAttrs == null || this.formComponent.filterAttrs.length == 0) {
                this.formComponent.filterAttrs = [];
                this.formComponent.filterAttrs.push({rowIndex:0, attrAssigns:[]});
            }
            if(this.formComponent.tagAttrs == null || this.formComponent.tagAttrs.length == 0) {
                this.formComponent.tagAttrs = [];
            }
            if(this.formComponent.layoutRegions == null) {
                this.formComponent.layoutRegions=[];
            }
            if(this.formComponent.layoutRegions.length == 0) {
                this.formComponent.layoutRegions.push({
                    id: null,
                    funcId: this.formComponent.funcId,
                    pageId: this.formComponent.pageId,
                    layoutId: this.formComponent.id,
                    regionId: null,
                    systemId: this.formComponent.systemId,
                    tenantId: this.formComponent.tenantId,
                    funcRegion: {
                        id: null,
                        funcId: this.formComponent.funcId,
                        systemId: this.formComponent.systemId,
                        tenantId: this.formComponent.tenantId,
                        regionAttrAssigns: []
                    }
                });
            }
            setTimeout(()=>{
                this.draggableEnable();
                this.syncAllAttrs();
            }, 100);
        },
    	loadData() {
            var leafAttrs = [];
            this.findLeafAttrs(this.formComponent.reportAttrs, leafAttrs);
            if(leafAttrs != null && leafAttrs.length > 0) {
                var dataSetDetails = [];
                var notDataSetDetailColumns = [];
                var displayCfgs = [];
                for(var i in leafAttrs) {
                    if(leafAttrs[i].dataSetDetail != null && leafAttrs[i].dataSetDetail.id != null) {
                        dataSetDetails.push(leafAttrs[i].dataSetDetail);
                        displayCfgs.push(leafAttrs[i].displayCfgJSON);
                    }else {
                        notDataSetDetailColumns.push(i);
                    }
                }
                var loadReportDataReq = {
                    dataSetDetails: dataSetDetails, 
                    displayCfgs: displayCfgs,
                    selectedFilterValues: this.selectedFilterValues, 
                    selectedTags: this.selectedTags
                };
                var url = "/lcdp-proxy/lowcode/platform/bi/api/dataSet/loadData";
                axiosClient.post(url, loadReportDataReq).then((response) => {
                    var data = response.data;
                    if(data != null && data.status == "200" && data.data != null) {
                        this.reportData = data.data;
                    }
                }).catch((ex)=>{
                    ElMessage.error(`加载数据集数据失败！` + ex);
                });
            }
        },
        draggableEnable() {
            const wrapperTr = document.querySelectorAll('#R' + this.formComponent.id + ' .el-table__header-wrapper thead > tr');
            for(var index = 0; index < wrapperTr.length; index++) {
                Sortable.create(wrapperTr[index], {
                    animation: 180,
                    delay: 0,
                    group: 'reportContainer',
                    onEnd: event => {
                        var srcLevel = event.item.className.match(/column/g).length;//获取源对象所在的层次
                        var toArea = event.to.children[0].className;
                        if(toArea == event.item.className && event.to.children[1] != null) {//特殊处理第一个子元素是源对象的情况
                            toArea = event.to.children[1].className;
                        }
                        var toLevel = toArea.match(/column/g).length;//获取目标位置所在的层次
                        setTimeout(()=>{
                            const rootAttrs = JSON.parse(JSON.stringify(this.formComponent.reportAttrs));//深度复制表格列对象，防止数据变更过程中触发表格的重新渲染
                            this.deleteNullAttr(rootAttrs);//删除draggalbe组件产生的null元素
                            this.moveItem(rootAttrs, event.oldIndex, event.newIndex, srcLevel, toLevel);//对源数据进行变更操作并重新渲染生成表格
                        }, 100);
                        
                    },
                    onAdd: event => {
                        var toArea = event.to.children[0].className;
                        if(toArea.match(/column/g)==null && event.to.children[1] != null) {//特殊处理第一个子元素是源对象的情况
                            toArea = event.to.children[1].className;
                        } 
                        var toLevel = toArea.match(/column/g).length;//获取目标位置所在的层次
                        const rootAttrs = JSON.parse(JSON.stringify(this.formComponent.reportAttrs));//深度复制表格列对象，防止数据变更过程中触发表格的重新渲染
                        this.addItem(rootAttrs, event.newIndex, toLevel, event.item._underlying_vm_);
                    }
                });
            }
		},
        addItem(rootAttrs, newIndex, toLevel, newItem) {
            var index = {
                level: 1,
                count: -1
            };
            var toLocation = this.findAttr(rootAttrs, index, toLevel, newIndex);
            if(toLocation == null) {//往最右侧添加时，会出现找不到目标位置的情况
                index = {
                    level: 1,
                    count: -1
                };
                toLocation = this.findAttr(rootAttrs, index, toLevel, newIndex-1);
                if(toLocation != null) {
                    toLocation.position ++;
                }
            }
            //console.log(rootAttrs, toLocation);
            if(toLocation != null) {
                toLocation.parentAttrs.splice(toLocation.position, 0, newItem);
                this.formComponent.reportAttrs = null;
                this.$nextTick(()=>{
                    this.formComponent.reportAttrs = rootAttrs;
                    setTimeout(()=>{
                        this.draggableEnable();
                        this.syncAllAttrs();
                    }, 100);
                });
            }
        },
        moveItem(rootAttrs, oldIndex, newIndex, srcLevel, toLevel) {
            var index = {
                level: 1,
                count: -1
            };
            var srcLocation = this.findAttr(rootAttrs, index, srcLevel, oldIndex);
            index = {
                level: 1,
                count: -1
            };
            var toLocation = this.findAttr(rootAttrs, index, toLevel, newIndex);
            console.log(srcLocation, toLocation);
            if(toLocation == null) {//往最右侧添加时，会出现找不到目标位置的情况
                index = {
                    level: 1,
                    count: -1
                };
                toLocation = this.findAttr(rootAttrs, index, toLevel, newIndex-1);
                if(toLocation != null) {
                    toLocation.position ++;
                }
            }
            // if(srcLevel == toLevel && oldIndex < newIndex) {
            //     toLocation.position ++;
            // }
            console.log(srcLocation, toLocation);
            if(srcLocation != null && toLocation != null) {
                var srcAttr = srcLocation.parentAttrs[srcLocation.position];
                srcLocation.parentAttrs.splice(srcLocation.position, 1);
                toLocation.parentAttrs.splice(toLocation.position, 0, srcAttr);
                this.formComponent.reportAttrs = null;
                setTimeout(()=>{
                    this.formComponent.reportAttrs = rootAttrs;
                    setTimeout(()=>{
                        this.draggableEnable();
                        this.syncAllAttrs();
                    }, 100);
                }, 50);
            }
        },
        findAttr(rootAttrs, index, targetLevel, targetCount) {
            if(rootAttrs == null || rootAttrs.length == 0) {
                return null;
            }
            if(index.level == targetLevel) {
                for(var i in rootAttrs) {
                    index.count++;
                    if(index.count == targetCount) {
                        return {
                            parentAttrs: rootAttrs,
                            position: i
                        };
                    }
                }
            }else if(index.level < targetLevel) {
                for(var i in rootAttrs) {
                    index.level++;
                    console.log(rootAttrs);
                    var result = this.findAttr(rootAttrs[i].attrAssigns, index, targetLevel, targetCount);
                    if(result != null) {
                        return result;
                    }
                    index.level--;
                }
                return null;
            } 
        },
        deleteNullAttr(rootAttrs) {
            if(rootAttrs == null || rootAttrs.length == 0) {
                return ;
            }
            for(var i = rootAttrs.length-1; i>=0; i--) {
                if(rootAttrs[i] == null) {
                    rootAttrs.splice(i,1);
                }else {
                    this.deleteNullAttr(rootAttrs[i].attrAssigns);
                }
            }
        },
        refreshReportTable() {
            const rootAttrs = JSON.parse(JSON.stringify(this.formComponent.reportAttrs))
            this.formComponent.reportAttrs = null;
            this.$nextTick(()=>{
                this.formComponent.reportAttrs = rootAttrs;
                setTimeout(()=>{
                    this.draggableEnable();
                }, 100);
            });
        },
        addSiblingAttr(rootAttrs, attrAssign) {
            if(rootAttrs == null || attrAssign == null) {
                return;
            }
            for(var i in rootAttrs) {
                if(rootAttrs[i] == attrAssign) {
                    const newAttr = this.createAttr(attrAssign.displayName + '-复制');
                    rootAttrs.splice(i+1, 0, newAttr);
                    break;
                }
            }
            this.refreshReportTable();
        },
        addSubAttr(attrAssign) {
            if(attrAssign == null) {
                return;
            }
            if(attrAssign.attrAssigns == null) {
                attrAssign.attrAssigns = [];
            }
            const newAttr = this.createAttr(attrAssign.displayName + '-子列');
            attrAssign.attrAssigns.push(newAttr);
            this.refreshReportTable();
        },
        deleteAttr(srcAttrAssigns, attr) {
            if(srcAttrAssigns == null || attr == null) {
                return;
            }
            if(this.formComponent.reportAttrs == srcAttrAssigns && srcAttrAssigns.length <= 1 && (srcAttrAssigns[0].attrAssigns == null || srcAttrAssigns[0].attrAssigns.length <=1)) {
                ElMessage.error(`不能删除所有属性！`);
                return;
            }
            for(var i in srcAttrAssigns) {
                var attrAssignsRow = srcAttrAssigns[i];
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
                        srcAttrAssigns.splice(i, 1);
                        break;
                    }
                }
            }
            this.syncAllAttrs();
        },
        deleteFilterAttr(attr) {
            if(attr == null || this.formComponent.filterAttrs == null) {
                return;
            }
            for(var i in this.formComponent.filterAttrs) {
                var attrAssignsRow = this.formComponent.filterAttrs[i];
                if(attrAssignsRow.attrAssigns != null) {
                    for(var j in attrAssignsRow.attrAssigns) {
                        var attrAssign = attrAssignsRow.attrAssigns[j];
                        if(attrAssign == attr) {
                            attrAssignsRow.attrAssigns.splice(j, 1);
                            break;
                        }
                    }
                }
            }
            this.formatDisplay();
            this.syncAllAttrs();
        },
        deleteTagAttr(attr) {
            if(attr == null || this.formComponent.tagAttrs == null) {
                return;
            }
            for(var i in this.formComponent.tagAttrs) {
                if(this.formComponent.tagAttrs[i] == attr) {
                    this.formComponent.tagAttrs.splice(i, 1);
                    break;
                }
            }
            this.syncAllAttrs();
        },
        findLeafAttrs(rootAttrs, leafAttrs) {
            if(rootAttrs == null || rootAttrs.length == 0 || leafAttrs == null) {
                return null;
            }
            for(var i in rootAttrs) {
                var attr = rootAttrs[i];
                if(attr.attrAssigns == null || attr.attrAssigns.length == 0) {
                    leafAttrs.push(attr);
                }else {
                    this.findLeafAttrs(attr.attrAssigns, leafAttrs);
                }
            }
        },
        findLeafAttr(rootAttrs, columnIndex, countObj) {
            if(rootAttrs == null || rootAttrs.length == 0 || columnIndex == null) {
                return null;
            }
            for(var i in rootAttrs) {
                var attr = rootAttrs[i];
                if(attr.attrAssigns == null || attr.attrAssigns.length == 0) {
                    countObj.index++;
                    if(countObj.index == columnIndex) {
                        return attr;
                    }
                }else {
                    var result = this.findLeafAttr(attr.attrAssigns, columnIndex, countObj);
                    if(result != null) {
                        return result;
                    }
                }
            }
        },
        syncAllAttrs() {
            var index = 1;
            var regionAttrAssigns = [];

            var allFilterAttrs = getAllAttrAssigns(this.formComponent.filterAttrs);
            for(var i in allFilterAttrs) {
                var filterAttr = allFilterAttrs[i];
                filterAttr.displayOrder = index++;
                filterAttr.displayCfgJSON.subRegionType = "F";
                filterAttr.displayCfg = JSON.stringify(filterAttr.displayCfgJSON);
                regionAttrAssigns.push(filterAttr);
            }
            index += 100;
            for(var i in this.formComponent.tagAttrs) {
                var tagAttr = this.formComponent.tagAttrs[i];
                tagAttr.displayOrder = index++;
                tagAttr.displayCfgJSON.subRegionType = "T";
                tagAttr.displayCfg = JSON.stringify(tagAttr.displayCfgJSON);
                regionAttrAssigns.push(tagAttr);
            }

            var indexObj = {"index": index + 100};
            this.syncReportAttrs(this.formComponent.reportAttrs, indexObj, regionAttrAssigns, null);

            this.formComponent.layoutRegions[0].funcRegion.regionAttrAssigns = regionAttrAssigns;
        },
        syncReportAttrs(reportAttrs, indexObj, regionAttrAssigns, parentAssignId) {
            for(var i in reportAttrs) {
                var reportAttr = reportAttrs[i];
                reportAttr.displayOrder = indexObj.index++;
                reportAttr.displayCfgJSON.subRegionType = "R";
                reportAttr.displayCfgJSON.parentAssignId = parentAssignId;
                reportAttr.displayCfg = JSON.stringify(reportAttr.displayCfgJSON);
                regionAttrAssigns.push(reportAttr);
                if(reportAttr.attrAssigns != null && reportAttr.attrAssigns.length > 0) {
                    this.syncReportAttrs(reportAttr.attrAssigns, indexObj, regionAttrAssigns, reportAttr.displayOrder);//使用displayOrder临时记录上下级关系，到后端更新成id。
                }
            }
        },
        onFilterAttrChange(event) {
            this.formatDisplay();
            this.syncAllAttrs();
        },
        onTagAttrChange(event) {
            this.syncAllAttrs();
        },
        formatDisplay(){
            var allFilterAttrs = getAllAttrAssigns(this.formComponent.filterAttrs);
            this.formComponent.filterAttrs = formatFormDisplay(allFilterAttrs);
        },
        spanTable(cell) {
            var countObj = {
                index: -1
            };
            var column = this.findLeafAttr(this.formComponent.reportAttrs, cell.columnIndex, countObj);
            if(column.displayCfgJSON.isMerge == 'Y') {
                var span = 0;
                for(var i in this.reportData) {
                    if(this.reportData[i][cell.column.property] == cell.row[cell.column.property]) {
                        if(i < cell.rowIndex) {
                            break;
                        }else {
                            span++;
                        }
                    }
                    
                }
                return {
                    rowspan: span,
                    colspan: 1
                }
            }else if(column.displayType == 'Q' && column.displayCfgJSON.sumCol != null && column.displayCfgJSON.sumCol != "") {
                var sumByColumn = column.displayCfgJSON.sumByCol;
                var sumColumn = column.displayCfgJSON.sumCol;
                if(sumByColumn != null && sumByColumn.indexOf('::') > 0) {
                    sumByColumn = sumByColumn.split('::')[1];
                }
                if(sumColumn != null && sumColumn.indexOf('::') > 0) {
                    sumColumn = sumColumn.split('::')[1];
                }
                var span = 0;
                var sum = 0;
                for(var i in this.reportData) {
                    if(sumByColumn != null && sumByColumn != "") {
                        if(this.reportData[i][sumByColumn] == cell.row[sumByColumn]) {
                            if(i < cell.rowIndex) {
                                break;
                            }else {
                                span++;
                                sum += parseFloat(this.reportData[i][sumColumn]);
                            }
                        }
                    }else {
                        if(i == cell.rowIndex) {
                            span++;
                            sum += parseFloat(this.reportData[i][sumColumn]);
                            break;
                        }
                    }
                }
                cell.row[cell.column.property] = sum;
                return {
                    rowspan: span,
                    colspan: 1
                }
            }
        },
        createAttr(attrName) {
            var newAttrCode = 'N/A-1-' + (new Date()).getTime();
            return {
                dataSetDetail: {content:{code: newAttrCode, name: attrName},detailType:'C'},
                attributeId: newAttrCode,
                displayType: 'O',
                colSpan: 8,
                rowSpan: 1,
                displayWidth: 150,
                displayName: attrName,
                displayCfgJSON: {},
                displayCfg: "{}",
                funcId: this.formComponent.funcId,
                formComponentId: this.formComponent.id,
                objectAssignId: '',
                objectId: '',
                objectType: '',
                systemId: this.formComponent.systemId,
                tenantId: this.formComponent.tenantId
            };
        },
        getSummaries(param) {
            const sums = [];
            var leafAttrs = [];
            this.findLeafAttrs(this.formComponent.reportAttrs, leafAttrs);
            for(var i in param.columns) {
                if(i == 0) {
                    sums[i] = '合计';
                }else {
                    var sum = 0;
                    for(var j in param.data) {
                        var value = param.data[j][param.columns[i].property];
                        if(value == null || value == "") {
                            continue;
                        }else if(Number.isNaN(value) || isNaN(value)) {
                            sum = "";
                            break;
                        }else {
                            sum += parseFloat(value);
                        }
                    }
                    sum = reportCellValueFormat(leafAttrs[i], sum);
                    sums[i] = sum;
                }
            }
            return sums;
        },
        onAttrSelectedReport(attrAssign) {
            this.onAttrSelected(attrAssign, this.formComponent);
        }
    }
    
 }
</script>
<style scoped>
.dragArea {
    outline: 1px dashed;
    width:99%;
    outline-color: darkgray;
    min-height: 5em;
    display: block;
    margin: 0 auto;
    margin-top: 0.5ch;
    margin-bottom: 0.5ch;
    padding-top: 0.5ch;
    padding-bottom: 0.5ch;
    padding-left: 0.5ch;
}

.dragAreaSub {
    outline: 1px dashed;
    width:100%;
    outline-color: darkgray;
}

.floatDeleteContainer{
    &:hover .floatDelete{
        display: block;  
    }
}
.floatDelete{
    display: none;
    float: left;
    margin-left: 95.5%; 
    margin-top: -42px;
    z-index: 99;
}
.floatDeleteForm{
    display: none;
    float: right;
    margin-right: 98%; 
    margin-top: -33px;
    z-index: 99;
}

.floatDeleteFormContainer{
    &:hover .floatDeleteForm{
        display: block;  
    }
}
</style>