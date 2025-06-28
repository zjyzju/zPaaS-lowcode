<template>
<div class="common-layout">
<el-container> 
    <el-main>
        <querySelectQueryRegion v-if="queryRegion != null" :queryCondition="queryCondition" :funcRegion="queryRegion" :resultRegion="resultRegion" :funcDefine="funcDefine" @operationExecuted="operationExecuted"/>
	    <querySelectResultRegion v-if="resultRegion != null" :funcRegion="resultRegion" :refresh="refresh" :funcDefine="funcDefine" :regionData="regionData['15']" :srcOperation="srcOperation['15']" @operationExecuted="operationExecuted"/>
	</el-main>
</el-container>
</div>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'
import querySelectQueryRegion from './region-querySelectQueryRegion.vue'
import querySelectResultRegion from './region-querySelectResultRegion.vue'
import {processLabelValue, funcDefinPreProcess} from '../../js/common.js'

export default {
	props: ['funcId', 'showSubFunc', 'params'],
	
    emits: ['objectSelected', 'hideSubFunc', 'clearSelect'],
    
    setup (props, {attrs, emit, slots}) {
        const hideSubFunc = () => {
            emit('hideSubFunc')
        };
        const objectSelected = (updateParam) => {
            emit('objectSelected', updateParam)
        };

        const clearSelect = (updateParam) => {
            emit('clearSelect', updateParam)
        };
        
        return {
        	hideSubFunc,
            objectSelected,
            clearSelect
        }
    },
	
    components: {
    	querySelectQueryRegion,
    	querySelectResultRegion,
    },
    
    data() {   
    	const funcDefine = ref({});
    	const queryRegion = ref(null);
    	const resultRegion = ref(null);
    	
    	const regionData = ref({});
    	const showRegion = ref({});
    	const srcOperation = ref({});
    	
    	const refresh = ref(1);
    	const funcParams = ref({});
    	const queryCondition = ref({});
    	
    	return {
        	funcDefine,
        	queryRegion,
        	resultRegion,
        	
        	queryCondition,
            funcParams,
        	
        	regionData,//{tplRegionId: data}
        	showRegion,//{tplRegionId: FuncRegion}
        	srcOperation,//{tplRegionId: FuncOperation}
        	refresh //数值类型，一直++
        	
        }
    },
    mounted() {
    	if(this.params != null) {
    		this.funcParams = JSON.parse(unescape(this.params));
            if(this.funcParams != null) {
                for(var key in this.funcParams) {
                    this.queryCondition[key] = this.funcParams[key];
                }
            }
    	}
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
                    this.queryRegion = this.funcDefine.regionMap["14"];//"1"是查询区模板标识，regionMap的Key使用的是区域模板标识
                    this.resultRegion = this.funcDefine.regionMap["15"];//"2"是结果区模板标识
                    this.regionData["15"] = [];//设置结果区的初始数据为空数组
                }else {
                	ElMessage.error(`加载功能定义信息失败`);
                }
                
            });
    	},
    	operationExecuted(srcOperation, data, srcData, srcRegion) {
    		if(srcOperation.operationType == 'C' || srcOperation.operationType == 'U' || (srcOperation.operationType == 'R' && srcRegion.tplRegion.id=='14')) {
    			this.regionData[this.funcDefine.targetRegionMap[srcOperation.funcRegionId].tplRegionId] = data;
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
    		}else if(srcOperation.operationType == 'S') {
                this.regionData[this.funcDefine.targetRegionMap[srcOperation.funcRegionId].tplRegionId] = null;
                this.showRegion[this.funcDefine.targetRegionMap[srcOperation.funcRegionId].tplRegionId] = false;
                this.objectSelected(data);
            }else if(srcOperation.operationType == 'R') {
                this.regionData[this.funcDefine.targetRegionMap[srcOperation.funcRegionId].tplRegionId] = null;
                this.showRegion[this.funcDefine.targetRegionMap[srcOperation.funcRegionId].tplRegionId] = false;
                this.clearSelect(data);
            }else if(srcOperation.operationType == 'N') {
                this.regionData[this.funcDefine.targetRegionMap[srcOperation.funcRegionId].tplRegionId] = data;
                this.showRegion[this.funcDefine.targetRegionMap[srcOperation.funcRegionId].tplRegionId] = false;
                this.hideSubFunc();
            }else if(srcOperation.operationType == 'Q'){//查询
                var targetRegion = this.funcDefine.targetRegionMap[srcOperation.targetRegionId];
                this.regionData[targetRegion.tplRegionId]["data"] = data.data;
                this.regionData[targetRegion.tplRegionId]["pageParam"] = data.pageParam;
                if(data.data != null) {
                    processLabelValue(targetRegion, this.regionData[targetRegion.tplRegionId]["data"], null, true, this.funcDefine);
                }
                this.showRegion[targetRegion.tplRegionId] = true;
                this.srcOperation[targetRegion.tplRegionId] = {
                    "srcOperation" : srcOperation,
                    "srcData" : srcData,
                    "srcRegion" : srcRegion
                }
            }else {//其他
                var targetRegion = this.funcDefine.targetRegionMap[srcOperation.targetRegionId];
                this.regionData[targetRegion.tplRegionId] = data
                if(data.data != null) {
                    processLabelValue(targetRegion, this.regionData[targetRegion.tplRegionId], null, true, this.funcDefine);
                }
                this.showRegion[targetRegion.tplRegionId] = true;
                this.srcOperation[targetRegion.tplRegionId] = {
                    "srcOperation" : srcOperation,
                    "srcData" : srcData,
                    "srcRegion" :srcRegion
                }
            }
    		
    	}
    }
 }
</script>