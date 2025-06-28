<template>
<el-container> 
    <el-main>
        <el-row :gutter="4">
            <el-col :span="24">
                <reportRegion  v-if="reportRegion != null && reportRegion.tplRegion.regionType == 'RM' && assignObject != null" :reportCfgData="reportCfgData" :funcRegion="reportRegion" :assignObject="assignObject"/>
            </el-col>
        </el-row>
    </el-main>
</el-container>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'
import reportRegion from './reportRegion.vue'
import {funcDefinPreProcess, formatFormDisplay, formatReportDisplay} from '../../js/common.js'

export default {
    components: {
    	reportRegion
    },
    
    data() {   
    	const funcDefine = ref({});
    	const funcId = ref(null);
    	const reportRegion = ref(null);
    	
    	const regionData = ref({});
    	const showRegion = ref({});
    	
    	const funcParams = ref({});

		const assignObject = ref(null);
		const reportCfgData = ref({
            reportAttrs: [],
            filterAttrs: [],
            tagAttrs: []
        });
    	
    	return {
        	funcId,
        	funcDefine,
        	reportRegion,
        	funcParams,

            regionData,//{tplRegionId: data}
        	showRegion,//{tplRegionId: FuncRegion}
        
			assignObject,
			reportCfgData
        }
    },
    mounted() {
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
                    this.reportRegion = this.funcDefine.regionMap["81"];//"81"是报表区模板标识，regionMap的Key使用的是区域模板标识
					this.initAttrDisplayInfo();
                }else {
                	ElMessage.error(`加载功能定义信息失败`);
                }
                
            });
    	},
		initAttrDisplayInfo() {
            if(this.funcDefine.objectAssigns != null && this.funcDefine.objectAssigns.length > 0) {
                this.assignObject = this.funcDefine.objectAssigns[0];
            }
            if(this.reportRegion != null && this.reportRegion.regionAttrAssigns != null) {
                if(this.reportRegion != null && this.reportRegion.regionAttrAssigns != null) {
                    var filterAttrs = [];
                    var reportAttrs = [];
                    var tagAttrs = [];
                    for(var index =0; index< this.reportRegion.regionAttrAssigns.length; index++) {
                        var attr = this.reportRegion.regionAttrAssigns[index];
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
                }
            }
        }
    }
    
 }
</script>