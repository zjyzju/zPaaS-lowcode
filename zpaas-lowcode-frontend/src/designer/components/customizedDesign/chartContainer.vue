<!-- 功能区域设计器-可视化图表容器 -->
<template>
<div class="dragArea" :id="formComponent.name">
    <el-row :gutter="4">
        <el-col :span="24">
            <span class="floatDeleteFormContainer">
                <el-icon @click="onCustomizedLayoutSelected(formComponent)"><TrendCharts /></el-icon>
                <el-link class="floatDeleteForm" size="small" @click="deleteContainer(this.componentIndex)"><el-icon><Delete /></el-icon></el-link>
            </span>
        </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4" class="dragAreaSub" :id="formComponent.id+ '-filter'" v-if="this.formComponent.layoutCfgJSON != null && this.formComponent.layoutCfgJSON.showFilterArea == 'Y'">
        <el-col :span="24">
            <draggable v-model="formComponent.filterAttrs" tag="div" item-key="rowIndex">
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
    <el-row :gutter="4" v-if="this.formComponent.layoutCfgJSON != null && this.formComponent.layoutCfgJSON.showFilterArea == 'Y'">
        <el-col :span="24">
            &nbsp;
        </el-col>
    </el-row>    
    <el-row :gutter="4" class="dragAreaSub" :id="formComponent.id+ '-tag'" v-if="this.formComponent.layoutCfgJSON != null && this.formComponent.layoutCfgJSON.showFilterArea == 'Y'">
        <el-col :span="4">筛选标签</el-col>
        <el-col :span="20">
            <draggable v-model="formComponent.tagAttrs" group="reportTagContainer" tag="div" item-key="attributeId" class="el-row" style="margin-left: -2px; margin-right: -2px; min-height: 1em" >
                <template #item="{element : attrAssign, index}">
                    <el-col :span="attrAssign.colSpan==null?0:parseInt(attrAssign.colSpan)" class="floatDeleteContainer"  @click="onAttrSelected(attrAssign)">
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
    <el-row :gutter="4" class="dragAreaSub" :id="'V' + formComponent.id">
        <el-col :span="24">
            <draggable  v-model="formComponent.chartComponents" group="reportContainer" tag="div" item-key="attributeId" class="el-row" style="margin-left: -2px; margin-right: -2px; min-height: 2em;  min-width: 100px;" >
                <template #item="{element : chartComponent, index}">
                    <el-col v-if="chartComponent.type != null" :span="24" class="floatDeleteContainer">
                        <div id="chartDiv"></div>
                        <el-link class="floatDelete" size="small" @click="deleteChartComponent()"><el-icon><Delete /></el-icon></el-link>
                    </el-col>
                    <el-col v-else :span="24" class="floatDeleteContainer">
                        <div id="emptyDiv">{{ chartComponent.name }}</div>
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
    <el-row :gutter="4" class="dragAreaSub" >
        <el-col :span="(keyData.width==null || keyData.width==1) ? 6 : 6*keyData.width" v-for="keyData in formComponent.chartComponents[0].keyDataAttrs" v-if="formComponent.chartComponents != null && formComponent.chartComponents[0].keyDataAttrs != null">
            <el-col :span="8">{{ keyData['name'] }}</el-col>
            <el-col :span="16">
                <draggable v-model="formComponent[keyData['type']]" :id="keyData['type']" group="reportContainer" @add="onKeyDataChanged" tag="div" item-key="attributeId" style="margin-left: -2px; margin-right: -2px; min-height: 1em; width: 100%;" >
                    <template #item="{element : attrAssign, index}">
                        <el-col :span="(keyData.width==null || keyData.width==1) ? 24 : 6" class="floatDeleteContainer"  @click="onAttrSelected(attrAssign)">
                            <el-tag :key="attrAssign.dataSetDetail.content.code" style='margin:5px;' type="primary" effect="dark">
                                {{attrAssign.displayName}}
                            </el-tag>
                            <el-link class="floatDelete" size="small" @click="deleteAttr(keyData['type'], attrAssign)"><el-icon><Delete /></el-icon></el-link>
                        </el-col>
                    </template>
                </draggable>
            </el-col>
        </el-col>
    </el-row>
</div>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import {formatFormDisplay, getAllAttrAssigns} from '../../js/common.js'
import {processChartData, getSampleChartData} from '../../js/chart.js'
import { ref, markRaw } from 'vue';
import Sortable from 'sortablejs'
import draggable from 'vuedraggable'
import {ElMessage } from 'element-plus'
import * as echarts from 'echarts'
import reportFilterComponent from '../common/reportFilterComponent.vue'

export default {
    props: ['formComponent','componentIndex'],
    
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
        draggable
    },
    watch: {
        'formComponent.layoutCfgJSON.chartHeight': function(val, old){
    		if(this.chartsInst != null) {
                var height = (this.formComponent.layoutCfgJSON != null && this.formComponent.layoutCfgJSON.chartHeight != null && !isNaN(parseInt(this.formComponent.layoutCfgJSON.chartHeight))) ? parseInt(this.formComponent.layoutCfgJSON.chartHeight) : 300;
                this.chartsInst.resize({'height':height});
            }
        }
    },
    data() {   
        const selectedFilterValues = ref({});//用于存放选中的筛选器的值
        const selectedTags = ref({});//用于存放选中的标签

        const chartsInst = ref(null);//图表组件实例对象
        const chartOption = ref(null);//图表组件的Option对象
        
        const chartData = ref({});

    	return {
            selectedFilterValues,
            selectedTags,
            chartsInst,
            chartOption,
            chartData
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/fe/api/dict/getChartDesignComponents").then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.chartData.chartComponents = data.data;
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        if(this.formComponent.layoutCfg == null || this.formComponent.layoutCfg.trim().length == 0) {
            this.formComponent.layoutCfgJSON = {'chartHeight':300};
        }else {
            this.formComponent.layoutCfgJSON = JSON.parse(this.formComponent.layoutCfg);
            if(this.formComponent.layoutCfgJSON.chartHeight == null) {
                this.formComponent.layoutCfgJSON.chartHeight = 300;
            }
        }
        
        if(this.formComponent.layoutCfgJSON != null) {
            if(this.formComponent.layoutCfgJSON.chartComponent != null) {
                for(var i in this.chartData.chartComponents) {
                    if(this.formComponent.layoutCfgJSON.chartComponent == this.chartData.chartComponents[i].type) {
                        var chartComponent = this.cloneChartComponent(this.chartData.chartComponents[i]);
                        this.formComponent.chartComponents = [];
                        this.formComponent.chartComponents.push(chartComponent);//设置图表组件
                        if(this.formComponent.chartComponents[0].keyDataAttrs != null) {
                            for(var index in this.formComponent.chartComponents[0].keyDataAttrs) {//清空同时初始组件的各keyData属性
                                this.formComponent[this.formComponent.chartComponents[0].keyDataAttrs[index].type] = [];
                            }
                        }
                        break;
                    }
                }
            }
        }
        if(this.formComponent.layoutRegions != null && this.formComponent.layoutRegions.length > 0 && this.formComponent.layoutRegions[0].funcRegion != null && this.formComponent.layoutRegions[0].funcRegion.regionAttrAssigns != null && this.formComponent.layoutRegions[0].funcRegion.regionAttrAssigns.length > 0) {
            var filterAttrs = [];
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
                        if(attr.displayType == 'S') {
                            attrCode = 'data_apply_column-' + index;
                        } 
                        attr.dataSetDetail = {content:{code: attrCode,name: attr.displayName},detailType:'C'};
                    }
                    if(attr.displayCfgJSON.keyDataType != null && this.formComponent[attr.displayCfgJSON.keyDataType] != null) {
                        this.formComponent[attr.displayCfgJSON.keyDataType].push(attr);
                    }
                }
            }
            this.formComponent.filterAttrs = formatFormDisplay(filterAttrs);
            this.formComponent.tagAttrs = tagAttrs;
        }

        if(this.formComponent.filterAttrs == null || this.formComponent.filterAttrs.length == 0) {
            this.formComponent.filterAttrs = [];
            this.formComponent.filterAttrs.push({rowIndex:0, attrAssigns:[]});
        }
        if(this.formComponent.tagAttrs == null || this.formComponent.tagAttrs.length == 0) {
            this.formComponent.tagAttrs = [];
        }
        if(this.formComponent.chartComponents == null || this.formComponent.chartComponents.length == 0) {
            this.formComponent.chartComponents = [{
                name: "拖动增加图表组件",
                type: null
            }];
        }
        
        if(this.formComponent.layoutRegions == null) {
            this.formComponent.layoutRegions = [];
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
            if(this.formComponent.chartComponents[0].type != null && this.formComponent.chartComponents[0].type.length > 0) {
                // 获取DOM元素并且进行初始化
                if(this.chartsInst == null) {
                    this.chartsInst = markRaw(echarts.init(document.getElementById('chartDiv')));
                }
                this.chartOption = getSampleChartData(this.formComponent.chartComponents[0].type);
                
                this.chartsInst.clear();
                // 创建图标
                this.chartsInst.setOption(this.chartOption);
                this.chartsInst.resize({height:((this.formComponent.layoutCfgJSON != null && this.formComponent.layoutCfgJSON.chartHeight != null) ? parseInt(this.formComponent.layoutCfgJSON.chartHeight) : 300)});
            }
        }, 100);
    },
    methods: {
    	loadData() {
            //循环获取所有的属性
            var chartAttrs = [];
            if(this.formComponent.chartComponents[0].keyDataAttrs != null) {
                for(var index in this.formComponent.chartComponents[0].keyDataAttrs) {
                    var keyData = this.formComponent.chartComponents[0].keyDataAttrs[index];
                    if(this.formComponent[keyData['type']] != null) {
                        for(var i in this.formComponent[keyData['type']]) {
                            chartAttrs.push(this.formComponent[keyData['type']][i]);
                        }
                    }
                }
            }
            var dataApplyColumns = [];
            if(chartAttrs != null && chartAttrs.length > 0) {//属性不为空时
                var dataSetDetails = [];//数据集属性
                var displayCfgs = [];//数据集属性的配置信息
                var notDataSetDetailColumns = [];//非数据集属性，如数据提供列
                for(var i in chartAttrs) {
                    if(chartAttrs[i].dataSetDetail != null && chartAttrs[i].dataSetDetail.id != null) {//数据集属性
                        dataSetDetails.push(chartAttrs[i].dataSetDetail);
                        displayCfgs.push(chartAttrs[i].displayCfgJSON);
                        if(chartAttrs[i].displayCfgJSON.acquireType != null) {//配置了数据字典获取的属性
                            var dataApplyColumn = {
                                code: chartAttrs[i].dataSetDetail.content.code,
                                displayCfg: chartAttrs[i].displayCfgJSON
                            }
                            dataApplyColumns.push(dataApplyColumn);
                        }
                    }else {//非数据集属性，如数据提供列
                        notDataSetDetailColumns.push(chartAttrs[i]);
                    }
                }
                //加载数据集数据的请求对象
                var loadChartDataReq = null;
                if(dataSetDetails != null && dataSetDetails.length > 0) {
                    loadChartDataReq = {
                        dataSetDetails: dataSetDetails, 
                        displayCfgs: displayCfgs,
                        selectedFilterValues: this.selectedFilterValues, 
                        selectedTags: this.selectedTags
                    };
                }
                //加载数据的请求对象
                var loadDataReq = {
                    systemId: this.formComponent.systemId,
                    tenantId: this.formComponent.tenantId,
                    loadReportDataReq: loadChartDataReq, //数据集数据加载请求
                    dataApplyColumns: null //数据提供列的列表
                };
                if(notDataSetDetailColumns.length > 0) {//如果有配置非数据集属性
                    for(var columnIndex in notDataSetDetailColumns) {
                        var column = notDataSetDetailColumns[columnIndex];
                        var dataApplyColumn = {
                            code: column.dataSetDetail.content.code,
                            displayCfg: column.displayCfgJSON
                        }
                        dataApplyColumns.push(dataApplyColumn);//加入到请求数据中
                    }
                }
                loadDataReq.dataApplyColumns = dataApplyColumns;
                var url = "/lcdp-proxy/lowcode/platform/bi/api/dataSet/loadDataWithDataApplyColumn";
                //从后台加载数据
                axiosClient.post(url, loadDataReq).then((response) => {
                    var data = response.data;
                    if(data != null && data.status == "200" && data.data != null) {
                        var datasLoaded = data.data;
                        processChartData(this.formComponent.chartComponents[0].keyDataAttrs, this.formComponent, datasLoaded.dataSetDatas, datasLoaded.dataApplyColumnDatas, this.chartOption, this.formComponent.chartComponents[0].type);
                        this.chartsInst.clear();
                        this.chartsInst.setOption(this.chartOption);//更新图表展示
                    }
                }).catch((ex)=>{
                    ElMessage.error(`加载数据集数据失败！` + ex);
                });
            }
        },
        draggableEnable() {
            const wrapperTr = document.querySelectorAll('#V' + this.formComponent.id + ' .el-row > div');
            for(var index = 0; index < wrapperTr.length; index++) {
                Sortable.create(wrapperTr[index], {
                    animation: 180,
                    delay: 0,
                    group: 'chartComponentContainer',
                    onEnd: event => {
                        this.syncAllAttrs();
                    },
                    onAdd: event => {
                        var chartComponent = event.item._underlying_vm_;
                        if(this.formComponent.chartComponents.length == 1) {
                            this.formComponent.chartComponents.splice(0, 1);
                            if(this.chartsInst != null) {
                                this.chartsInst.dispose();
                                this.chartsInst = null;
                            }
                        }
                        this.formComponent.chartComponents.push(chartComponent);
                        if(this.formComponent.chartComponents[0].keyDataAttrs != null) {
                            for(var index in this.formComponent.chartComponents[0].keyDataAttrs) {
                                this.formComponent[this.formComponent.chartComponents[0].keyDataAttrs[index].type] = [];
                            }
                        }
                        this.syncAllAttrs();
                        setTimeout(()=>{
                            this.draggableEnable();
                            // 获取DOM元素并且进行初始化
                            if(this.chartsInst == null) {
                                this.chartsInst = markRaw(echarts.init(document.getElementById('chartDiv')));
                            }
                            this.chartOption = getSampleChartData(this.formComponent.chartComponents[0].type);
                            this.chartsInst.clear();
                            // 创建图标
                            this.chartsInst.setOption(this.chartOption);
                            this.chartsInst.resize({height:((this.formComponent.layoutCfgJSON != null && this.formComponent.layoutCfgJSON.chartHeight != null) ? parseInt(this.formComponent.layoutCfgJSON.chartHeight) : 300)});
                        }, 100);
                    }
                });
            }
		},
        deleteChartComponent() {
            if(this.formComponent.chartComponents.length == 1) {
                this.formComponent.chartComponents.splice(0, 1);
                if(this.chartsInst != null) {
                    this.chartsInst.dispose();
                    this.chartsInst = null;
                }
            }
            this.formComponent.chartComponents.push({
                name: "拖动增加图表组件",
                type: null
            });
            
            setTimeout(()=>{
                this.draggableEnable();
                this.syncAllAttrs();
            }, 100);
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
        deleteAttr(dataType, attr) {
            if(attr == null || this.formComponent[dataType] == null) {
                return;
            }
            for(var i in this.formComponent[dataType]) {
                if(this.formComponent[dataType][i] == attr) {
                    this.formComponent[dataType].splice(i, 1);
                    break;
                }
            }
            this.syncAllAttrs();
        },
        onKeyDataChanged(event) {
            if(event.to.id != 'series') {
                var newIndex = null;
                for(var i in this.formComponent[event.to.id]) {
                    if(this.formComponent[event.to.id][i].attributeId == event.item._underlying_vm_.attributeId) {
                        newIndex = i;
                        break;
                    }
                }
                
                for(var i=this.formComponent[event.to.id].length; i>=0; i--) {
                    if(i != newIndex) {
                        this.formComponent[event.to.id].splice(i,1);
                    }
                }
            }
            this.syncAllAttrs();
        },
        onTagAttrChange(event) {
            this.syncAllAttrs();
        },
        onFilterAttrChange(event) {
            this.formatDisplay();
            this.syncAllAttrs();
        },
        formatDisplay(){
            var allFilterAttrs = getAllAttrAssigns(this.formComponent.filterAttrs);
            this.formComponent.filterAttrs = formatFormDisplay(allFilterAttrs);
        },
        syncAllAttrs() {
            console.log(this.formComponent);

            if(this.formComponent.chartComponents != null && this.formComponent.chartComponents.length > 0) {
                this.formComponent.layoutCfgJSON.chartComponent = this.formComponent.chartComponents[0].type;
            }else {
                this.formComponent.layoutCfgJSON.chartComponent = null;
            }
            this.formComponent.layoutCfg = JSON.stringify(this.formComponent.layoutCfgJSON);

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

            index += 100;
            //循环获取所有的图表属性
            if(this.formComponent.chartComponents[0].keyDataAttrs != null) {
                for(var index in this.formComponent.chartComponents[0].keyDataAttrs) {
                    var keyData = this.formComponent.chartComponents[0].keyDataAttrs[index];
                    if(this.formComponent[keyData['type']] != null) {
                        for(var i in this.formComponent[keyData['type']]) {
                            var chartAttr = this.formComponent[keyData['type']][i];
                            chartAttr.displayOrder = index++;
                            chartAttr.displayCfgJSON.subRegionType = "C";
                            chartAttr.displayCfgJSON.keyDataType = keyData['type']; //图表关键数据类型
                            chartAttr.displayCfg = JSON.stringify(chartAttr.displayCfgJSON);
                            regionAttrAssigns.push(chartAttr);
                        }
                    }
                }
            }

            this.formComponent.layoutRegions[0].funcRegion.regionAttrAssigns = regionAttrAssigns;
        },
        cloneChartComponent(originComponent) {
            return {
                name: originComponent.name,
                type: originComponent.type,
                keyDataAttrs: originComponent.keyDataAttrs
            };
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