<template>
<el-container> 
    <el-main>
        <el-row :gutter="4">
            <el-col :span="24">
                <chartRegion  v-if="funcRegion != null && funcRegion.tplRegion.regionType == 'VM' && assignObject != null" :chartCfgData="chartCfgData" :funcRegion="funcRegion" :assignObject="assignObject"/>
            </el-col>
        </el-row>
    </el-main>
</el-container>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import chartRegion from './chartRegion.vue'
import {funcDefinPreProcess, formatFormDisplay} from '../../js/common.js'
import {getChartComponents} from '../../../designer/js/chart.js'

export default {
    components: {
    	chartRegion
    },
    
    data() {   
    	const funcDefine = ref({});
    	const funcId = ref(null);
    	const funcRegion = ref(null);
    	
    	const regionData = ref({});
    	const showRegion = ref({});
    	
    	const funcParams = ref({});

		const assignObject = ref(null);
		const chartCfgData = ref({
            filterAttrs: [],
            tagAttrs: []
        });

        const chartComponents = ref(null);
    	
    	return {
        	funcId,
        	funcDefine,
        	funcRegion,
        	funcParams,

            regionData,//{tplRegionId: data}
        	showRegion,//{tplRegionId: FuncRegion}
        
			assignObject,
			chartCfgData,
            chartComponents
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/fe/api/dict/getChartDesignComponents").then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.chartComponents = data.data;
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

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
                        
                    }
                }
            }
    	})
        this.reloadFuncDefineAll();
    },
    methods: {
    	reloadFuncDefineAll() {
    		axiosClient.get("/lcdp-proxy/lowcode/fe/api/funcDefine/loadAll/" + this.funcId).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                	this.funcDefine = data.data;
                    //进行数据处理，方便前台界面使用---start
                    funcDefinPreProcess(this.funcDefine);
                    //进行数据处理，方便前台界面使用---end
                    document.title = this.funcDefine.name;//设置功能页面的名称
                    this.funcRegion = this.funcDefine.regionMap["82"];//"82"是图表区模板标识，regionMap的Key使用的是区域模板标识
					this.initAttrDisplayInfo();
                }
            });
    	},
		initAttrDisplayInfo() {
            if(this.funcDefine.objectAssigns != null && this.funcDefine.objectAssigns.length > 0) {
                this.assignObject = this.funcDefine.objectAssigns[0];
            }
            if(this.funcRegion != null && this.funcRegion.regionCfg != null) {
                var regionCfgJSON = JSON.parse(this.funcRegion.regionCfg);
                if(regionCfgJSON.chartComponent != null) {
                    for(var i in this.chartComponents) {
                        if(regionCfgJSON.chartComponent == this.chartComponents[i].type) {
                            var chartComponent = this.cloneChartComponent(this.chartComponents[i]);
                            this.chartCfgData.chartComponents = [];
                            this.chartCfgData.chartComponents.push(chartComponent);//设置图表组件
                            if(this.chartCfgData.chartComponents[0].keyDataAttrs != null) {
                                for(var index in this.chartCfgData.chartComponents[0].keyDataAttrs) {//清空同时初始组件的各keyData属性
                                    this.chartCfgData[this.chartCfgData.chartComponents[0].keyDataAttrs[index].type] = [];
                                }
                            }
                            break;
                        }
                    }
                }
            }
            if(this.funcRegion != null && this.funcRegion.regionAttrAssigns != null) {
                if(this.funcRegion != null && this.funcRegion.regionAttrAssigns != null) {
                    var filterAttrs = [];
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
                                if(attr.displayType == 'S') {
                                    attrCode = 'data_apply_column-' + index;
                                } 
                                attr.dataSetDetail = {content:{code: attrCode,name: attr.displayName},detailType:'C'};
                            }
                            if(attr.displayCfgJSON.keyDataType != null && this.chartCfgData[attr.displayCfgJSON.keyDataType] != null) {
                                this.chartCfgData[attr.displayCfgJSON.keyDataType].push(attr);
                            }
                        }
                    }
                    
                    this.chartCfgData.filterAttrs = formatFormDisplay(filterAttrs);
                    this.chartCfgData.tagAttrs = tagAttrs;
                }
            }
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