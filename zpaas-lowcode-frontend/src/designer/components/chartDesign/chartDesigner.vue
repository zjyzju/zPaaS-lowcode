<!-- 功能区域设计器-可视化图表设计 -->
<template>
    <el-row :gutter="4">
        <el-col :span="24">
            &nbsp;
        </el-col>
    </el-row>
    <el-row :gutter="4" class="dragArea" :id="funcRegion.id+ '-filter'">
        <el-col :span="24">
            <draggable v-model="chartCfgData.filterAttrs" tag="div" item-key="rowIndex">
                <template #item="{element : attrAssignRow}">
                    <draggable v-model="attrAssignRow.attrAssigns" @change="onChange" group="chartFilterContainer" tag="div" item-key="attributeId" class="el-row" style="margin-left: -2px; margin-right: -2px; min-height: 1em" >
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
            <draggable v-model="chartCfgData.tagAttrs" group="chartTagContainer" tag="div" item-key="attributeId" class="el-row" style="margin-left: -2px; margin-right: -2px; min-height: 1em" >
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
    <el-row :gutter="4" class="dragArea" :id="'V' + funcRegion.id">
        <el-col :span="24">
            <draggable  v-model="chartCfgData.chartComponents" group="chartComponentContainer" tag="div" item-key="attributeId" class="el-row" style="margin-left: -2px; margin-right: -2px; min-height: 2em;  min-width: 100px;" >
                <template #item="{element : chartComponent, index}">
                    <el-col v-if="chartComponent.type != null" :span="24" class="floatDeleteContainer">
                        <div id="chartDiv" style=" height: 350px"></div>
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
    <el-row :gutter="4" class="dragArea" >
        <el-col :span="(keyData.width==null || keyData.width==1) ? 6 : 6*keyData.width" v-for="keyData in chartCfgData.chartComponents[0].keyDataAttrs" v-if="chartCfgData.chartComponents != null && chartCfgData.chartComponents[0].keyDataAttrs != null">
            <el-col :span="8">{{ keyData['name'] }}</el-col>
            <el-col :span="16">
                <draggable v-model="chartCfgData[keyData['type']]" :id="keyData['type']" group="chartDataContainer" @add="onKeyDataChanged" tag="div" item-key="attributeId" style="margin-left: -2px; margin-right: -2px; min-height: 1em; width: 100%;" >
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
    props: ['chartCfgData','funcRegion', 'assignObject'],
    
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
        draggable
    },
    
    data() {   
        const selectedFilterValues = ref({});//用于存放选中的筛选器的值
        const selectedTags = ref({});//用于存放选中的标签

        const chartsInst = ref(null);//图表组件实例对象
        const chartOption = ref(null);//图表组件的Option对象
        

    	return {
            selectedFilterValues,
            selectedTags,
            chartsInst,
            chartOption
        }
    },
    mounted() {
        if(this.funcRegion.regionCfg == null || this.funcRegion.regionCfg.trim().length == 0) {
            this.funcRegion.regionCfgJSON = {};
        }else {
            this.funcRegion.regionCfgJSON = JSON.parse(this.funcRegion.regionCfg);
        }
        if(this.chartCfgData.filterAttrs.length == 0) {
            this.chartCfgData.filterAttrs.push({rowIndex:0, attrAssigns:[]});
        }
        if(this.chartCfgData.chartComponents == null || this.chartCfgData.chartComponents.length == 0) {
            this.chartCfgData.chartComponents = [{
                name: "拖动增加图表组件",
                type: null
            }];
        }
        if(this.chartCfgData.chartComponents[0].keyDataAttrs != null) {
            for(var index in this.chartCfgData.chartComponents[0].keyDataAttrs) {//清空同时初始组件的各keyData属性
                if(this.chartCfgData[this.chartCfgData.chartComponents[0].keyDataAttrs[index].type]  == null) {
                    this.chartCfgData[this.chartCfgData.chartComponents[0].keyDataAttrs[index].type] = [];
                }
            }
        }
        setTimeout(()=>{
            this.draggableEnable();
            if(this.chartCfgData.chartComponents[0].type != null && this.chartCfgData.chartComponents[0].type.length > 0) {
                // 获取DOM元素并且进行初始化
                if(this.chartsInst == null) {
                    this.chartsInst = markRaw(echarts.init(document.getElementById('chartDiv')));
                }
                this.chartOption = getSampleChartData(this.chartCfgData.chartComponents[0].type);
                
                this.chartsInst.clear();
                // 创建图标
                this.chartsInst.setOption(this.chartOption);
            }
            
        }, 100);
    },
    methods: {
    	loadData() {
            //循环获取所有的属性
            var chartAttrs = [];
            if(this.chartCfgData.chartComponents[0].keyDataAttrs != null) {
                for(var index in this.chartCfgData.chartComponents[0].keyDataAttrs) {
                    var keyData = this.chartCfgData.chartComponents[0].keyDataAttrs[index];
                    if(this.chartCfgData[keyData['type']] != null) {
                        for(var i in this.chartCfgData[keyData['type']]) {
                            chartAttrs.push(this.chartCfgData[keyData['type']][i]);
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
                    systemId: this.funcRegion.systemId,
                    tenantId: this.funcRegion.tenantId,
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
                        processChartData(this.chartCfgData.chartComponents[0].keyDataAttrs, this.chartCfgData, datasLoaded.dataSetDatas, datasLoaded.dataApplyColumnDatas, this.chartOption, this.chartCfgData.chartComponents[0].type);
                        this.chartsInst.clear();
                        this.chartsInst.setOption(this.chartOption);//更新图表展示
                    }else {
                        ElMessage.error(`加载数据集数据失败！`);
                    }
                }).catch((ex)=>{
                    ElMessage.error(`加载数据集数据失败！` + ex);
                });
            }
        },
        draggableEnable() {
            const wrapperTr = document.querySelectorAll('#V' + this.funcRegion.id + ' .el-row > div');
            for(var index = 0; index < wrapperTr.length; index++) {
                Sortable.create(wrapperTr[index], {
                    animation: 180,
                    delay: 0,
                    group: 'chartComponentContainer',
                    onEnd: event => {
                        
                    },
                    onAdd: event => {
                        var chartComponent = event.item._underlying_vm_;
                        if(this.chartCfgData.chartComponents.length == 1) {
                            this.chartCfgData.chartComponents.splice(0, 1);
                            if(this.chartsInst != null) {
                                this.chartsInst.dispose();
                                this.chartsInst = null;
                            }
                        }
                        this.chartCfgData.chartComponents.push(chartComponent);
                        if(this.chartCfgData.chartComponents[0].keyDataAttrs != null) {
                            for(var index in this.chartCfgData.chartComponents[0].keyDataAttrs) {
                                this.chartCfgData[this.chartCfgData.chartComponents[0].keyDataAttrs[index].type] = [];
                            }
                        }
                        
                        setTimeout(()=>{
                            this.draggableEnable();
                            // 获取DOM元素并且进行初始化
                            if(this.chartsInst == null) {
                                this.chartsInst = markRaw(echarts.init(document.getElementById('chartDiv')));
                            }
                            this.chartOption = getSampleChartData(this.chartCfgData.chartComponents[0].type);
                            this.chartsInst.clear();
                            // 创建图标
                            this.chartsInst.setOption(this.chartOption);
                        }, 100);
                    }
                });
            }
		},
        deleteChartComponent() {
            if(this.chartCfgData.chartComponents.length == 1) {
                this.chartCfgData.chartComponents.splice(0, 1);
                if(this.chartsInst != null) {
                    this.chartsInst.dispose();
                    this.chartsInst = null;
                }
            }
            this.chartCfgData.chartComponents.push({
                name: "拖动增加图表组件",
                type: null
            });
            
            setTimeout(()=>{
                this.draggableEnable();
            }, 100);
        },
        deleteFilterAttr(attr) {
            if(attr == null || this.chartCfgData.filterAttrs == null) {
                return;
            }
            for(var i in this.chartCfgData.filterAttrs) {
                var attrAssignsRow = this.chartCfgData.filterAttrs[i];
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
            if(attr == null || this.chartCfgData.tagAttrs == null) {
                return;
            }
            for(var i in this.chartCfgData.tagAttrs) {
                if(this.chartCfgData.tagAttrs[i] == attr) {
                    this.chartCfgData.tagAttrs.splice(i, 1);
                    break;
                }
            }
        },
        deleteAttr(dataType, attr) {
            if(attr == null || this.chartCfgData[dataType] == null) {
                return;
            }
            for(var i in this.chartCfgData[dataType]) {
                if(this.chartCfgData[dataType][i] == attr) {
                    this.chartCfgData[dataType].splice(i, 1);
                    break;
                }
            }
        },
        onKeyDataChanged(event) {
            if(event.to.id != 'series') {
                var newIndex = null;
                for(var i in this.chartCfgData[event.to.id]) {
                    if(this.chartCfgData[event.to.id][i].attributeId == event.item._underlying_vm_.attributeId) {
                        newIndex = i;
                        break;
                    }
                }
                
                for(var i=this.chartCfgData[event.to.id].length; i>=0; i--) {
                    if(i != newIndex) {
                        this.chartCfgData[event.to.id].splice(i,1);
                    }
                }
            }
        },
        onChange(event) {
            this.formatDisplay();
        },
        formatDisplay(){
            var allFilterAttrs = getAllAttrAssigns(this.chartCfgData.filterAttrs);
            this.chartCfgData.filterAttrs = formatFormDisplay(allFilterAttrs);
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