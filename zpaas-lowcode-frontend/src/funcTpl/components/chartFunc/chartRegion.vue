<!-- 功能区域-可视化图表 -->
<template>
    <el-row :gutter="4" v-if="chartCfgData.filterAttrs != null  && chartCfgData.filterAttrs.length > 0" v-for="(attrAssignsRow, key) in chartCfgData.filterAttrs">
        <el-col :span="attrAssign.colSpan" v-for="(attrAssign, index) in attrAssignsRow.attrAssigns">
            <el-form-item :label="attrAssign.displayName" :required="attrAssign.required == 'Y'" :prop="attrAssign.id" size="default">
                <reportFilterComponent :regionData="selectedFilterValues" :attrAssign="attrAssign"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="chartCfgData.tagAttrs != null && chartCfgData.tagAttrs.length > 0">
        <el-col :span="2"><el-text class="mx-1">筛选标签</el-text></el-col>
        <el-col :span="22">
            <el-row :gutter="4" v-if="chartCfgData.tagAttrs != null">
                <el-col :span="attrAssign.colSpan" v-for="(attrAssign, index) in chartCfgData.tagAttrs">
                    <reportFilterComponent :regionData="selectedTags" :attrAssign="attrAssign"/>
                </el-col>
            </el-row>
        </el-col>
    </el-row>   
    <el-row :gutter="4" v-if="(chartCfgData.filterAttrs != null && chartCfgData.filterAttrs.length > 0 && chartCfgData.filterAttrs[0].attrAssigns != null && chartCfgData.filterAttrs[0].attrAssigns.length > 0) || (chartCfgData.tagAttrs != null && chartCfgData.tagAttrs.length > 0)">
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
    <el-row :gutter="4" :id="'V' + funcRegion.id">
        <el-col :span="24">
            <div id="chartDiv" style="height: 500px; width: 98%;"></div>
        </el-col>
    </el-row>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref, markRaw } from 'vue';
import {processChartData, getSampleChartData} from '../../../designer/js/chart.js'
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
        reportFilterComponent
    },
    
    data() {   
        const selectedFilterValues = ref({});//用于存放选中的筛选器的值
        const selectedTags = ref({});//用于存放选中的标签

        const chartsInst = ref(null);//图表组件实例对象
        const chartOption = ref({});//图表组件的Option对象


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
        if(this.chartCfgData.chartComponents[0].keyDataAttrs != null) {
            for(var index in this.chartCfgData.chartComponents[0].keyDataAttrs) {//清空同时初始组件的各keyData属性
                if(this.chartCfgData[this.chartCfgData.chartComponents[0].keyDataAttrs[index].type]  == null) {
                    this.chartCfgData[this.chartCfgData.chartComponents[0].keyDataAttrs[index].type] = [];
                }
            }
        }
        setTimeout(()=>{
            if(this.chartCfgData.chartComponents[0].type != null && this.chartCfgData.chartComponents[0].type.length > 0) {
                // 获取DOM元素并且进行初始化
                if(this.chartsInst == null) {
                    this.chartsInst = markRaw(echarts.init(document.getElementById('chartDiv')));
                }
                this.chartOption = getSampleChartData([this.chartCfgData.chartComponents[0].type]);
                this.loadData();
            }
        }, 100);
    },
    methods: {
    	loadData() {
            var loadChartDataReq = {
                funcId: this.funcRegion.funcId,
                funcRegionId: this.funcRegion.id, 
                selectedFilterValues: this.selectedFilterValues, 
                selectedTags: this.selectedTags
            };
            var url = "/lcdp-proxy/lowcode/fe/api/chartFunc/loadData";
            //从后台加载数据
            axiosClient.post(url, loadChartDataReq).then((response) => {
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