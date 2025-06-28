<template>
<div class="common-layout">
<el-container> 
    <el-main>
	    <el-row :gutter="4">
	        <el-col :span="24">
	           <SimpleQueryRegion v-if="queryRegion != null" :queryCondition="queryCondition" :funcRegion="queryRegion"  :resultRegion="resultRegion" :funcDefine="funcDefine" @operationExecuted="operationExecuted"/>
		    </el-col>
		</el-row>
		<el-row :gutter="4">
	        <el-col :span="24">
			    <SimpleResultRegion v-if="resultRegion != null" :funcRegion="resultRegion" :refresh="refresh" :funcDefine="funcDefine" :regionData="regionData['2']" :srcOperation="srcOperation['2']" @operationExecuted="operationExecuted"/>
			</el-col>
		</el-row>
	</el-main>
</el-container>
</div>
<el-dialog v-model="showRegion['3']" v-if="regionData['3'] != null" :title="editRegion.name"  width="80vw" top="4vh"  append-to-body>
    <template #header>
        <span class="title">{{ editRegion.name }}</span>
    </template>
    <SimpleEditRegion v-if="editRegion != null" :funcRegion="editRegion" :funcDefine="funcDefine" :regionData="regionData['3']" @operationExecuted="operationExecuted"/>
</el-dialog>

<el-dialog v-model="showRegion['4']" v-if="regionData['4'] != null" :title="detailRegion.name"  width="80vw" top="4vh" append-to-body>
    <template #header>
        <span class="title">{{ detailRegion.name }}</span>
    </template>
    <SimpleDetailRegion v-if="detailRegion != null" :funcRegion="detailRegion" :funcDefine="funcDefine" :regionData="regionData['4']" @operationExecuted="operationExecuted"/>
</el-dialog>

<el-dialog v-model="showRegion['5']" v-if="regionData['5'] != null" :title="createRegion.name"  width="80vw" top="4vh" append-to-body>
    <template #header>
        <span class="title">{{ createRegion.name }}</span>
    </template>
    <SimpleCreateRegion v-if="createRegion != null" :funcRegion="createRegion" :funcDefine="funcDefine" :regionData="regionData['5']" @operationExecuted="operationExecuted"/>
</el-dialog>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import SimpleQueryRegion from './region-simpleQueryRegion.vue'
import SimpleResultRegion from './region-simpleResultRegion.vue'
import SimpleEditRegion from './region-simpleEditRegion.vue'
import SimpleDetailRegion from './region-simpleDetailRegion.vue'
import SimpleCreateRegion from './region-simpleCreateRegion.vue'
import {processLabelValue, funcDefinPreProcess} from '../../js/common.js'

export default {
    components: {
    	SimpleQueryRegion,
    	SimpleResultRegion,
    	SimpleEditRegion,
    	SimpleDetailRegion,
    	SimpleCreateRegion
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
    	
    	const refresh = ref(1);
    	const funcParams = ref({});
    	const queryCondition = ref({});

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
                    this.queryRegion = this.funcDefine.regionMap["1"];//"1"是查询区模板标识，regionMap的Key使用的是区域模板标识
                    this.resultRegion = this.funcDefine.regionMap["2"];//"2"是结果区模板标识
                    this.regionData["2"] = {"data":[]};//设置结果区的初始数据为空数组
                    this.editRegion = this.funcDefine.regionMap["3"];//"3"是编辑区模板标识
                    this.detailRegion = this.funcDefine.regionMap["4"];//"4"是明细区模板标识
                    this.createRegion = this.funcDefine.regionMap["5"];//"5"是新增区模板标识
                }
                
            });
    	},
    	operationExecuted(srcOperation, data, srcData, srcRegion) {
    		if(srcOperation.operationType == 'C' || srcOperation.operationType == 'U'  || srcOperation.operationType == 'R') {//新增保存/修改保存/重置
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
            }else if(srcOperation.operationType == 'P' || srcOperation.operationType == 'B') {//预新增/根据主键查询
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