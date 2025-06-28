<!-- 功能区域设计器-统计报表设计 -->
<template>
    <el-row :gutter="4">
        <el-col :span="24">
            &nbsp;
        </el-col>
    </el-row>
    <el-row :gutter="4" class="dragArea" :id="funcRegion.id+ '-filter'">
        <el-col :span="24">
            <draggable v-model="reportCfgData.filterAttrs" tag="div" item-key="rowIndex">
                <template #item="{element : attrAssignRow}">
                    <draggable v-model="attrAssignRow.attrAssigns" @change="onChange" group="reportFilterContainer" tag="div" item-key="attributeId" class="el-row" style="margin-left: -2px; margin-right: -2px; min-height: 1em" >
                        <template #item="{element : attrAssign, index}">
                            <el-col :span="attrAssign.colSpan==null?0:parseInt(attrAssign.colSpan)" class="floatDeleteContainer"  @click="onAttrSelected(attrAssign)">
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
    <el-row :gutter="4">
        <el-col :span="24">
            &nbsp;
        </el-col>
    </el-row>    
    <el-row :gutter="4" class="dragArea" :id="funcRegion.id+ '-tag'">
        <el-col :span="2">筛选标签</el-col>
        <el-col :span="22">
            <draggable v-model="reportCfgData.tagAttrs" group="reportTagContainer" tag="div" item-key="attributeId" class="el-row" style="margin-left: -2px; margin-right: -2px; min-height: 1em" >
                <template #item="{element : attrAssign, index}">
                    <el-col :span="attrAssign.colSpan==null?0:parseInt(attrAssign.colSpan)" class="floatDeleteContainer"  @click="onAttrSelected(attrAssign)">
                        <reportFilterComponent :regionData="selectedTags" :attrAssign="attrAssign"/>
                        <el-link class="floatDelete" size="small" @click="deleteTagAttr(attrAssign)"><el-icon><Delete /></el-icon></el-link>
                    </el-col>
                </template>
            </draggable>
        </el-col>
    </el-row>   
    <el-row :gutter="4">
        <el-col :span="24">
            &nbsp;
        </el-col>
    </el-row>  
    <el-row :gutter="4">
        <el-col :span="20">
            &nbsp;
        </el-col>
        <el-col :span="4">
            <el-button  @click="loadData()" type="primary" size="small">提取数据</el-button>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            &nbsp;
        </el-col>
    </el-row>    
    <el-row :gutter="4" class="dragArea" :id="'R' + funcRegion.id">
        <el-col :span="24">
            <el-table v-if="reportCfgData != null && reportCfgData.reportAttrs != null && reportCfgData.reportAttrs.length > 0" :data="reportData" :show-summary="reportData != null && funcRegion.regionCfgJSON != null && funcRegion.regionCfgJSON.showSumRow == 'Y'" :summary-method="getSummaries" stripe style="width: 100%" highlight-current-row show-overflow-tooltip :span-method="spanTable">
                <loopTableColumnComponent v-for="attrAssign in reportCfgData.reportAttrs" :attrAssign="attrAssign" :parentAttrAssigns="reportCfgData.reportAttrs" :funcRegion="funcRegion" @onAttrSelected="onAttrSelected" @deleteAttr="deleteAttr" @addSiblingAttr="addSiblingAttr" @addSubAttr="addSubAttr"/>
	        </el-table>
        </el-col>
    </el-row>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import {formatFormDisplay, getAllAttrAssigns, reportCellValueFormat} from '../../js/common.js'
import { ref } from 'vue';
import Sortable from 'sortablejs'
import draggable from 'vuedraggable'
import {ElMessage } from 'element-plus'
import loopTableColumnComponent from '../common/loopTableColumnComponent.vue'
import reportFilterComponent from '../common/reportFilterComponent.vue'

export default {
    props: ['reportCfgData','funcRegion', 'assignObject'],
    
    emits: ['onChangeEnd', 'onAttrSelected'],
    
    setup (props, {attrs, emit, slots}) {
        const onChangeEnd = (event) => {
            emit('onChangeEnd', event);
        };

        const onAttrSelected = (attrAssign) => {
            emit('onAttrSelected', attrAssign);
        };

        return {
        	onChangeEnd,
            onAttrSelected
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
    mounted() {
        if(this.funcRegion.regionCfg == null || this.funcRegion.regionCfg.trim().length == 0) {
            this.funcRegion.regionCfgJSON = {};
        }else {
            this.funcRegion.regionCfgJSON = JSON.parse(this.funcRegion.regionCfg);
        }
        if(this.reportCfgData.reportAttrs.length == 0) {
            var attr = this.createAttr('请修改');
            this.reportCfgData.reportAttrs.push(attr);
        }
        if(this.reportCfgData.filterAttrs.length == 0) {
            this.reportCfgData.filterAttrs.push({rowIndex:0, attrAssigns:[]});
        }
        setTimeout(()=>{
            this.draggableEnable();
        }, 100);
    },
    methods: {
    	loadData() {
            var leafAttrs = [];
            this.findLeafAttrs(this.reportCfgData.reportAttrs, leafAttrs);
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
            const wrapperTr = document.querySelectorAll('#R' + this.funcRegion.id + ' .el-table__header-wrapper thead > tr');
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
                            const rootAttrs = JSON.parse(JSON.stringify(this.reportCfgData.reportAttrs));//深度复制表格列对象，防止数据变更过程中触发表格的重新渲染
                            this.deleteNullAttr(rootAttrs);//删除draggalbe组件产生的null元素
                            console.log(rootAttrs, event.oldIndex, event.newIndex, srcLevel, toLevel);
                            this.moveItem(rootAttrs, event.oldIndex, event.newIndex, srcLevel, toLevel);//对源数据进行变更操作并重新渲染生成表格
                        }, 100);
                        
                    },
                    onAdd: event => {
                        var toArea = event.to.children[0].className;
                        if(toArea.match(/column/g)==null && event.to.children[1] != null) {//特殊处理第一个子元素是源对象的情况
                            toArea = event.to.children[1].className;
                        } 
                        var toLevel = toArea.match(/column/g).length;//获取目标位置所在的层次
                        const rootAttrs = JSON.parse(JSON.stringify(this.reportCfgData.reportAttrs));//深度复制表格列对象，防止数据变更过程中触发表格的重新渲染
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
            console.log(rootAttrs, toLocation);
            if(toLocation != null) {
                toLocation.parentAttrs.splice(toLocation.position, 0, newItem);
                this.reportCfgData.reportAttrs = null;
                this.$nextTick(()=>{
                    this.reportCfgData.reportAttrs = rootAttrs;
                    setTimeout(()=>{
                        this.draggableEnable();
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
                this.reportCfgData.reportAttrs = null;
                setTimeout(()=>{
                    this.reportCfgData.reportAttrs = rootAttrs;
                    setTimeout(()=>{
                        this.draggableEnable();
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
            const rootAttrs = JSON.parse(JSON.stringify(this.reportCfgData.reportAttrs))
            this.reportCfgData.reportAttrs = null;
            this.$nextTick(()=>{
                this.reportCfgData.reportAttrs = rootAttrs;
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
            if(this.reportCfgData.reportAttrs == srcAttrAssigns && srcAttrAssigns.length <= 1 && (srcAttrAssigns[0].attrAssigns == null || srcAttrAssigns[0].attrAssigns.length <=1)) {
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
        },
        deleteFilterAttr(attr) {
            if(attr == null || this.reportCfgData.filterAttrs == null) {
                return;
            }
            for(var i in this.reportCfgData.filterAttrs) {
                var attrAssignsRow = this.reportCfgData.filterAttrs[i];
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
        },
        deleteTagAttr(attr) {
            if(attr == null || this.reportCfgData.tagAttrs == null) {
                return;
            }
            for(var i in this.reportCfgData.tagAttrs) {
                if(this.reportCfgData.tagAttrs[i] == attr) {
                    this.reportCfgData.tagAttrs.splice(i, 1);
                    break;
                }
            }
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
        onChange(event) {
            this.formatDisplay();
        },
        formatDisplay(){
            var allFilterAttrs = getAllAttrAssigns(this.reportCfgData.filterAttrs);
            this.reportCfgData.filterAttrs = formatFormDisplay(allFilterAttrs);
        },
        spanTable(cell) {
            var countObj = {
                index: -1
            };
            var column = this.findLeafAttr(this.reportCfgData.reportAttrs, cell.columnIndex, countObj);
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
                funcId: this.funcRegion.funcId,
                funcRegionId: this.funcRegion.id,
                objectAssignId: this.assignObject.id,
                objectId: this.assignObject.objectId,
                objectType: this.assignObject.objectType,
                systemId: this.funcRegion.systemId,
                tenantId: this.funcRegion.tenantId
            };
        },
        getSummaries(param) {
            const sums = [];
            var leafAttrs = [];
            this.findLeafAttrs(this.reportCfgData.reportAttrs, leafAttrs);
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
        }
    }
    
 }
</script>
<style scoped>
.dragArea {
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
</style>