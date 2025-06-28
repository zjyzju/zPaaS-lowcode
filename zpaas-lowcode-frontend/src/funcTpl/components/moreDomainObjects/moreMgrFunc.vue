<template>
<div class="common-layout">
<el-container> 
    <el-main>
        <el-row :gutter="4">
            <el-col :span="24">
                <moreQueryRegion v-if="queryRegion != null" :queryCondition="queryCondition" :funcRegion="queryRegion" :resultRegion="resultRegion" :funcDefine="funcDefine" @operationExecuted="operationExecuted"/>
	        </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
			    <moreResultRegion v-if="resultRegion != null" :funcRegion="resultRegion" :refresh="refresh" :funcDefine="funcDefine" :regionData="regionData['19']" :srcOperation="srcOperation['19']" @operationExecuted="operationExecuted"/>
			</el-col>
        </el-row>
	</el-main>
</el-container>
</div>
<el-dialog v-model="showRegion['8']" v-if="regionData['8'] != null" :title="editRegion.name"  width="80vw" top="4vh" append-to-body>
	<template #header>
        <span class="title">{{ editRegion.name }}</span>
    </template>
    <moreEditRegion v-if="editRegion != null" :funcRegion="editRegion" :funcDefine="funcDefine" :regionData="regionData['8']" @operationExecuted="operationExecuted"/>
</el-dialog>

<el-dialog v-model="showRegion['7']" v-if="regionData['7'] != null" :title="detailRegion.name"  width="80vw" top="4vh" append-to-body>
	<template #header>
        <span class="title">{{ detailRegion.name }}</span>
    </template>
    <moreDetailRegion v-if="detailRegion != null" :funcRegion="detailRegion" :funcDefine="funcDefine" :regionData="regionData['7']" @operationExecuted="operationExecuted"/>
</el-dialog>

<el-dialog v-model="showRegion['6']" v-if="regionData['6'] != null" :title="createRegion.name"  width="80vw" top="4vh" append-to-body>
	<template #header>
        <span class="title">{{ createRegion.name }}</span>
    </template>
	<moreCreateRegion v-if="createRegion != null" :funcRegion="createRegion" :funcDefine="funcDefine" :regionData="regionData['6']" @operationExecuted="operationExecuted"/>
</el-dialog>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import moreQueryRegion from './region-moreQueryRegion.vue'
import moreResultRegion from './region-moreResultRegion.vue'
import moreEditRegion from './region-moreEditRegion.vue'
import moreDetailRegion from './region-moreDetailRegion.vue'
import moreCreateRegion from './region-moreCreateRegion.vue'
import {processLabelValue, funcDefinPreProcess} from '../../js/common.js'

export default {
    components: {
    	moreQueryRegion,
    	moreResultRegion,
    	moreEditRegion,
    	moreDetailRegion,
    	moreCreateRegion
    },
    
    data() {   
    	const funcDefine = ref({});
    	const funcId = ref(null);
    	const queryRegion = ref(null);
    	const resultRegion = ref(null);
    	const editRegion = ref(null);
    	const detailRegion = ref(null);
    	const createRegion = ref(null);
    	
    	const regionData = ref({});
    	const showRegion = ref({});
    	const srcOperation = ref({});
    	const queryCondition = ref({});
    	
    	const refresh = ref(1);
    	const funcParams = ref({});
    	
    	return {
        	funcId,
        	funcDefine,
        	queryRegion,
        	resultRegion,
        	editRegion,
        	detailRegion,
        	createRegion,
        	
        	queryCondition,
        	funcParams,
        	
        	regionData,//{tplRegionId: data}
        	showRegion,//{tplRegionId: FuncRegion}
        	srcOperation,//{tplRegionId: FuncOperation}
        	refresh //数值类型，一直++
        	
        }
    },
    mounted() {
    	//获取URL参数
    	var url = document.location;
    	var searchString = url.search.substring(1);
    	var params = searchString.split("&");
    	
    	params.forEach(item=>{
    	    var kv = item.split("=");
    	    if("funcId" == kv[0]) {
    	        this.funcId = kv[1];
    	    }else if("params" == kv[0] && kv[1] != null) {
    	    	this.funcParams = JSON.parse(unescape(kv[1]));
    	    	if(this.funcParams != null) {
    	    		console.log(this.funcParams);
    	    		for(var key in this.funcParams) {
    	    			this.queryCondition[key] = this.funcParams[key];
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
                    this.queryRegion = this.funcDefine.regionMap["18"];//"18"是查询区模板标识，regionMap的Key使用的是区域模板标识
                    this.resultRegion = this.funcDefine.regionMap["19"];//"19"是结果区模板标识
                    this.regionData["19"] = {"data":[]};//设置结果区的初始数据为空数组
                    this.editRegion = this.funcDefine.regionMap["8"];//"8"是编辑区模板标识
                    this.detailRegion = this.funcDefine.regionMap["7"];//"7"是明细区模板标识
                    this.createRegion = this.funcDefine.regionMap["6"];//"6"是新增区模板标识
                }
            });
    	},
    	operationExecuted(srcOperation, data, srcData, srcRegion) {
			if(srcOperation.operationType == 'C' || srcOperation.operationType == 'U' || srcOperation.operationType == 'R') {//新增保存/修改保存/重置
    			this.regionData[this.funcDefine.targetRegionMap[srcOperation.funcRegionId].tplRegionId] = null;
                this.showRegion[this.funcDefine.targetRegionMap[srcOperation.funcRegionId].tplRegionId] = false;
				if(srcOperation.operationType == 'R' && srcOperation.targetRegionId != null) {
                    var targetRegion = this.funcDefine.targetRegionMap[srcOperation.targetRegionId];
                    if(this.srcOperation[targetRegion.tplRegionId] != null && this.srcOperation[targetRegion.tplRegionId].srcData != null) {
                        for(var i in srcRegion.regionAttrAssigns) {
                            var attrAssign = srcRegion.regionAttrAssigns[i];
                            if(attrAssign.readonly != 'Y') {
                                this.srcOperation[targetRegion.tplRegionId].srcData[attrAssign.attribute.code] = null;
                            }
                        }
                    }                    
                }
                this.refresh ++;
    		}else if(srcOperation.operationType == 'P' || srcOperation.operationType == 'B') {//预新增
            	var targetRegion = this.funcDefine.targetRegionMap[srcOperation.targetRegionId];
            	this.regionData[targetRegion.tplRegionId] = data
                if(data != null) {
                    processLabelValue(targetRegion, this.regionData[targetRegion.tplRegionId], null, true, this.funcDefine);
                }
                this.showRegion[targetRegion.tplRegionId] = true;
            }else if(srcOperation.operationType == 'Q'){//查询
            	var targetRegion = this.funcDefine.targetRegionMap[srcOperation.targetRegionId];
            	this.regionData[targetRegion.tplRegionId] = data;
    			if(data != null && data.data != null) {
    				processLabelValue(targetRegion, this.regionData[targetRegion.tplRegionId]["data"], null, true, this.funcDefine);
    			}
    			this.showRegion[targetRegion.tplRegionId] = true;
    			this.srcOperation[targetRegion.tplRegionId] = {
    				"srcOperation" : srcOperation,
    				"srcData" : srcData,
    				"srcRegion" : srcRegion
    			}
    		}else if(srcOperation.operationType == 'N' || srcOperation.targetRegionId == null || srcOperation.targetRegionId.trim().length == 0) {//取消等
                this.regionData[this.funcDefine.targetRegionMap[srcOperation.funcRegionId].tplRegionId] = data
                this.showRegion[this.funcDefine.targetRegionMap[srcOperation.funcRegionId].tplRegionId] = false;
            }else {//其他
                var targetRegion = this.funcDefine.targetRegionMap[srcOperation.targetRegionId];
                this.regionData[targetRegion.tplRegionId] = data
                processLabelValue(targetRegion, this.regionData[targetRegion.tplRegionId], null, true, this.funcDefine);
                this.showRegion[targetRegion.tplRegionId] = true;
            }
    		
    	}
    }
    
 }
</script>