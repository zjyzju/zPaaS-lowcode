<!-- 统计报表容器 -->
<template>
    <el-row :gutter="4" v-if="customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.showFilterArea == 'Y' && customizedLayout.filterAttrs != null  && customizedLayout.filterAttrs.length > 0" v-for="(attrAssignsRow, key) in customizedLayout.filterAttrs">
        <el-col :span="attrAssign.colSpan" v-for="(attrAssign, index) in attrAssignsRow.attrAssigns">
            <el-form-item :label="attrAssign.displayName" :required="attrAssign.required == 'Y'" :prop="attrAssign.id" size="default">
                <reportFilterComponent :regionData="selectedFilterValues" :attrAssign="attrAssign"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.showFilterArea == 'Y' && customizedLayout.tagAttrs != null && customizedLayout.tagAttrs.length > 0">
        <el-col :span="2"><el-text class="mx-1">筛选标签</el-text></el-col>
        <el-col :span="22">
            <el-row :gutter="4" v-if="customizedLayout.tagAttrs != null">
                <el-col :span="attrAssign.colSpan" v-for="(attrAssign, index) in reportCfgData.tagAttrs">
                    <reportFilterComponent :regionData="selectedTags" :attrAssign="attrAssign"/>
                </el-col>
            </el-row>
        </el-col>
    </el-row>   
    <el-row :gutter="4" v-if="customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.showFilterArea == 'Y' && ((customizedLayout.filterAttrs != null && customizedLayout.filterAttrs.length > 0 && customizedLayout.filterAttrs[0].attrAssigns != null && customizedLayout.filterAttrs[0].attrAssigns.length > 0) || (customizedLayout.tagAttrs != null && customizedLayout.tagAttrs.length > 0))">
        <el-col :span="18">
            &nbsp;
        </el-col>
        <el-col :span="6">
            <el-button  @click="loadData()" type="primary" size="small">提取数据</el-button>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            &nbsp;
        </el-col>
    </el-row>  
    <el-row :gutter="4">
        <el-col :span="24">
            <el-table v-if="customizedLayout != null && customizedLayout.reportAttrs != null && customizedLayout.reportAttrs.length > 0" :data="reportData" :show-summary="reportData != null && customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.showSumRow == 'Y'" :summary-method="getSummaries" stripe style="width: 100%" highlight-current-row show-overflow-tooltip :span-method="spanTable">
                <loopTableColumnComponent v-for="attrAssign in customizedLayout.reportAttrs" :attrAssign="attrAssign" :parentAttrAssigns="customizedLayout.reportAttrs" :funcRegion="this.customizedLayout.layoutRegions[0].funcRegion"/>
	        </el-table>
        </el-col>
    </el-row>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import {formatFormDisplay, reportCellValueFormat, formatReportDisplay} from '../../js/common.js'
import { ref } from 'vue';
import {ElMessage } from 'element-plus'
import loopTableColumnComponent from '../common/loopTableColumnComponent.vue'
import reportFilterComponent from '../common/reportFilterComponent.vue'

export default {
    props: [ 'customizedLayout', 'customizedPage', 'funcDefine', 'regionData', 'parentPage', 'srcOperation'],
    
    emits: [],
    
    setup (props, {attrs, emit, slots}) {
        
        return {
        	
        }
    },
    computed: {
        
    },
    components: {
        reportFilterComponent,
        loopTableColumnComponent
    },
    
    data() {   
        const reportData = ref(null);
        const selectedFilterValues = ref({});
        const selectedTags = ref({});
        
    	return {
            reportData,
            selectedFilterValues,
            selectedTags
        }
    },
    watch: {
        
    },
    mounted() {
        this.init();
    },
    methods: {
        init() {
            if(this.customizedLayout.layoutCfg == null || this.customizedLayout.layoutCfg.trim().length == 0) {
                this.customizedLayout.layoutCfgJSON = {};
            }else {
                this.customizedLayout.layoutCfgJSON = JSON.parse(this.customizedLayout.layoutCfg);
            }
            if(this.customizedLayout.layoutRegions != null && this.customizedLayout.layoutRegions.length > 0 && this.customizedLayout.layoutRegions[0].funcRegion != null && this.customizedLayout.layoutRegions[0].funcRegion.regionAttrAssigns != null && this.customizedLayout.layoutRegions[0].funcRegion.regionAttrAssigns.length > 0) {
                var filterAttrs = [];
                var reportAttrs = [];
                var tagAttrs = [];
                for(var index =0; index< this.customizedLayout.layoutRegions[0].funcRegion.regionAttrAssigns.length; index++) {
                    var attr = this.customizedLayout.layoutRegions[0].funcRegion.regionAttrAssigns[index];
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
                this.customizedLayout.filterAttrs = formatFormDisplay(filterAttrs);
                this.customizedLayout.reportAttrs = formatReportDisplay(reportAttrs);
                this.customizedLayout.tagAttrs = tagAttrs;
            }
            this.loadData();
        },
    	loadData() {
            var leafAttrs = [];
            this.findLeafAttrs(this.customizedLayout.reportAttrs, leafAttrs);
            if(leafAttrs != null && leafAttrs.length > 0) {
                var dataSetDetails = [];
                var notDataSetDetailColumns = [];
                for(var i in leafAttrs) {
                    if(leafAttrs[i].dataSetDetail != null && leafAttrs[i].dataSetDetail.id != null) {
                        dataSetDetails.push(leafAttrs[i].dataSetDetail);
                    }else {
                        notDataSetDetailColumns.push(i);
                    }
                }
                var loadReportDataReq = {
                    funcId: this.customizedLayout.layoutRegions[0].funcRegion.funcId,
                    funcRegionId: this.customizedLayout.layoutRegions[0].funcRegion.id, 
                    selectedFilterValues: this.selectedFilterValues, 
                    selectedTags: this.selectedTags
                };
                var url = "/lcdp-proxy/lowcode/fe/api/reportFunc/loadData";
                axiosClient.post(url, loadReportDataReq).then((response) => {
                    var data = response.data;
                    if(data != null && data.status == "200" && data.data != null) {
                        this.reportData = data.data;
                    }else {
                        ElMessage.error(`加载数据集数据失败！`);
                    }
                }).catch((ex)=>{
                    ElMessage.error(`加载数据集数据失败！` + ex);
                });
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
        spanTable(cell) {
            var countObj = {
                index: -1
            };
            var column = this.findLeafAttr(this.customizedLayout.reportAttrs, cell.columnIndex, countObj);
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
        getSummaries(param) {
            const sums = [];
            var leafAttrs = [];
            this.findLeafAttrs(this.customizedLayout.reportAttrs, leafAttrs);
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