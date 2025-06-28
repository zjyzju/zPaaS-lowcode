<!-- 可视化图表容器 -->
<template>
    <el-row :gutter="4" v-if="customizedLayout.filterAttrs != null  && customizedLayout.filterAttrs.length > 0" v-for="(attrAssignsRow, key) in customizedLayout.filterAttrs">
        <el-col :span="attrAssign.colSpan" v-for="(attrAssign, index) in attrAssignsRow.attrAssigns">
            <el-form-item :label="attrAssign.displayName" :required="attrAssign.required == 'Y'" :prop="attrAssign.id" size="default">
                <reportFilterComponent :regionData="selectedFilterValues" :attrAssign="attrAssign"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="customizedLayout.tagAttrs != null && customizedLayout.tagAttrs.length > 0">
        <el-col :span="2"><el-text class="mx-1">筛选标签</el-text></el-col>
        <el-col :span="22">
            <el-row :gutter="4" v-if="customizedLayout.tagAttrs != null">
                <el-col :span="attrAssign.colSpan" v-for="(attrAssign, index) in customizedLayout.tagAttrs">
                    <reportFilterComponent :regionData="selectedTags" :attrAssign="attrAssign"/>
                </el-col>
            </el-row>
        </el-col>
    </el-row>   
    <el-row :gutter="4" v-if="(customizedLayout.filterAttrs != null && customizedLayout.filterAttrs.length > 0 && customizedLayout.filterAttrs[0].attrAssigns != null && customizedLayout.filterAttrs[0].attrAssigns.length > 0) || (customizedLayout.tagAttrs != null && customizedLayout.tagAttrs.length > 0)">
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

    <el-row :gutter="4" :id="'V' + customizedLayout.id">
        <el-col :span="24">
            <div id="chartDiv" style="width: 98%;"></div>
        </el-col>
    </el-row>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import {formatFormDisplay} from '../../js/common.js'
import {processChartData, getSampleChartData} from '../../../designer/js/chart.js'
import { ref, markRaw } from 'vue';
import {ElMessage } from 'element-plus'
import * as echarts from 'echarts'
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
        reportFilterComponent
    },
    watch: {
        
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
                this.initContainer();
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        
    },
    methods: {
        initContainer() {
            if(this.customizedLayout.layoutCfg == null || this.customizedLayout.layoutCfg.trim().length == 0) {
                this.customizedLayout.layoutCfgJSON = {'chartHeight':300};
            }else {
                this.customizedLayout.layoutCfgJSON = JSON.parse(this.customizedLayout.layoutCfg);
                if(this.customizedLayout.layoutCfgJSON.chartHeight == null) {
                    this.customizedLayout.layoutCfgJSON.chartHeight = 300;
                }
            }
            
            if(this.customizedLayout.layoutCfgJSON != null) {
                if(this.customizedLayout.layoutCfgJSON.chartComponent != null) {
                    for(var i in this.chartData.chartComponents) {
                        if(this.customizedLayout.layoutCfgJSON.chartComponent == this.chartData.chartComponents[i].type) {
                            var chartComponent = this.cloneChartComponent(this.chartData.chartComponents[i]);
                            this.customizedLayout.chartComponents = [];
                            this.customizedLayout.chartComponents.push(chartComponent);//设置图表组件
                            if(this.customizedLayout.chartComponents[0].keyDataAttrs != null) {
                                for(var index in this.customizedLayout.chartComponents[0].keyDataAttrs) {//清空同时初始组件的各keyData属性
                                    this.customizedLayout[this.customizedLayout.chartComponents[0].keyDataAttrs[index].type] = [];
                                }
                            }
                            break;
                        }
                    }
                }
            }
            if(this.customizedLayout.layoutRegions != null && this.customizedLayout.layoutRegions.length > 0 && this.customizedLayout.layoutRegions[0].funcRegion != null && this.customizedLayout.layoutRegions[0].funcRegion.regionAttrAssigns != null && this.customizedLayout.layoutRegions[0].funcRegion.regionAttrAssigns.length > 0) {
                var filterAttrs = [];
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
                            if(attr.displayType == 'S') {
                                attrCode = 'data_apply_column-' + index;
                            } 
                            attr.dataSetDetail = {content:{code: attrCode,name: attr.displayName},detailType:'C'};
                        }
                        if(attr.displayCfgJSON.keyDataType != null && this.customizedLayout[attr.displayCfgJSON.keyDataType] != null) {
                            this.customizedLayout[attr.displayCfgJSON.keyDataType].push(attr);
                        }
                    }
                }
                this.customizedLayout.filterAttrs = formatFormDisplay(filterAttrs);
                this.customizedLayout.tagAttrs = tagAttrs;
            }

            if(this.customizedLayout.filterAttrs == null || this.customizedLayout.filterAttrs.length == 0) {
                this.customizedLayout.filterAttrs = [];
                this.customizedLayout.filterAttrs.push({rowIndex:0, attrAssigns:[]});
            }
            if(this.customizedLayout.tagAttrs == null || this.customizedLayout.tagAttrs.length == 0) {
                this.customizedLayout.tagAttrs = [];
            }
            
            setTimeout(()=>{
                if(this.customizedLayout.chartComponents[0].type != null && this.customizedLayout.chartComponents[0].type.length > 0) {
                    // 获取DOM元素并且进行初始化
                    if(this.chartsInst == null) {
                        this.chartsInst = markRaw(echarts.init(document.getElementById('chartDiv')));
                    }
                    this.chartOption = getSampleChartData(this.customizedLayout.chartComponents[0].type);
                    
                    this.chartsInst.clear();
                    // 创建图标
                    this.chartsInst.setOption(this.chartOption);
                    this.chartsInst.resize({height:((this.customizedLayout.layoutCfgJSON != null && this.customizedLayout.layoutCfgJSON.chartHeight != null) ? parseInt(this.customizedLayout.layoutCfgJSON.chartHeight) : 300)});
                    this.loadData();
                }
            }, 100);
        },
    	loadData() {
            var loadChartDataReq = {
                funcId: this.customizedLayout.funcId,
                funcRegionId: this.customizedLayout.layoutRegions[0].funcRegion.id, 
                selectedFilterValues: this.selectedFilterValues, 
                selectedTags: this.selectedTags
            };
            var url = "/lcdp-proxy/lowcode/fe/api/chartFunc/loadData";
            //从后台加载数据
            axiosClient.post(url, loadChartDataReq).then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) {
                    var datasLoaded = data.data;
                    processChartData(this.customizedLayout.chartComponents[0].keyDataAttrs, this.customizedLayout, datasLoaded.dataSetDatas, datasLoaded.dataApplyColumnDatas, this.chartOption, this.customizedLayout.chartComponents[0].type);
                    this.chartsInst.clear();
                    this.chartsInst.setOption(this.chartOption);//更新图表展示
                }else {
                    ElMessage.error(`加载数据集数据失败！`);
                }
            }).catch((ex)=>{
                ElMessage.error(`加载数据集数据失败！` + ex);
            });
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