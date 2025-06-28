<!--查看（弹出）类按钮-->
<template>
    <el-button :type="(operation.operationCfgJSON == null || operation.operationCfgJSON.displayColor == null) ? 'primary' : operation.operationCfgJSON.displayColor" v-if="operation.operationType == 'V' && (operation.objectAssignId == null || operation.objectAssignId == '' || mainPage != 'Y')"  :size="(operation.operationCfgJSON == null || operation.operationCfgJSON.displaySize == null) ? 'small' : operation.operationCfgJSON.displaySize" @click="executeOperation(operation)" :round="operation.operationCfgJSON != null && operation.operationCfgJSON.displayStyle == 'round'" :plain="operation.operationCfgJSON != null && operation.operationCfgJSON.displayStyle == 'plain'" :circle="operation.operationCfgJSON != null && operation.operationCfgJSON.displayStyle == 'circle'" :link="operation.operationCfgJSON != null && operation.operationCfgJSON.displayStyle == 'link'">{{operation.name}}</el-button>
    <el-link :type="(operation.operationCfgJSON == null || operation.operationCfgJSON.displayColor == null) ? 'primary' : operation.operationCfgJSON.displayColor" v-if="operation.operationType == 'V' && operation.objectAssignId != null && operation.objectAssignId != '' && mainPage == 'Y'"  :size="(operation.operationCfgJSON == null || operation.operationCfgJSON.displaySize == null) ? 'small' : operation.operationCfgJSON.displaySize" @click="executeOperation(operation)">{{operation.name}}</el-link>
	<el-dialog v-model="showDynamicComponent" v-if="showDynamicComponent == true" :title="subFuncName" width="1000px" append-to-body>
        <component :is="dynamicComponent" v-if="popupSubFuncId != 'viewProcessComponent'" :funcDefine="subFuncDefine" :params="params"  :data="subData" @hideViewPage="hideViewPage" @objectUpdated="objectUpdated"/>
        <component :is="dynamicComponent" v-if="popupSubFuncId == 'viewProcessComponent'" :params="params" @hideViewPage="hideViewPage"/>
    </el-dialog>
	
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref, defineAsyncComponent, shallowRef } from 'vue';
import { ElMessage } from 'element-plus'
import {generateReqObject, funcDefinPreProcess, getDynamicComponet} from '../../js/common.js'


export default {
    props: ['operation','regionData','funcDefine', 'funcRegion', 'mainPage'],
    
    emits: [],
    
    setup(props, {attrs, emit, slots}) {
    	return {
        	
        }
    }, 
	
    components: {
    	 
    },
    
    data() {      
        const dynamicComponent = ref(null);
        const showDynamicComponent = ref(false);
        const popupSubFuncId = ref(null);
        const params = ref(null);
        const subData = ref([]);
        const subFuncDefine = ref(null);
        const subFuncName = ref('');
        const currentOperationCfg = ref(null);
        
        return {
            dynamicComponent,
            showDynamicComponent,
            popupSubFuncId,
            params,
            subData,
            subFuncDefine,
            subFuncName,
            currentOperationCfg
        }
    },
    computed: {
    	
    },
    watch: {        
        
    },
    mounted() {
        
    },
    
    methods: {
    	executeOperation(operation) {
            console.log(this.regionData);
            if(operation.operationType == 'V') {//查看（弹出）操作
                if(operation.operationCfg != null && operation.operationCfg.trim().length != 0) {
                	this.currentOperationCfg = JSON.parse(operation.operationCfg);
                    var paramsJSON = this.processParamCfg(this.currentOperationCfg, this.regionData);
                    console.log(paramsJSON, this.currentOperationCfg);
                    var viewType = paramsJSON.viewType;
                    if(this.currentOperationCfg.funcType == "P") {//viewProcessComponent，选项选择页
                    	var hasParam = false;
                        for(var key in paramsJSON) {
                        	if(paramsJSON[key] != null && paramsJSON[key].trim().length > 0) {
                        		hasParam = true;
                        	}
                        }
                    	if(!hasParam) {
                            ElMessage.error(`流程实例标识不能为空！`);
                            return;
                        }
                        this.popupSubFuncId = "viewProcessComponent";
                        this.dynamicComponent = shallowRef(defineAsyncComponent(getDynamicComponet("/src/funcTpl/components/common/viewProcessComponent.vue")));
                        this.params = paramsJSON;
                        this.subFuncName = "查看流程信息";
                        this.showDynamicComponent = true;
                    }else {//子功能/子页面
                        if(this.currentOperationCfg.subFuncParentAttr != null && this.currentOperationCfg.subFuncParentAttr.trim().length > 0 
                        		&& this.currentOperationCfg.subFuncMap != null) {//如果配置多个子功能，根据子功能上级属性字段进行区分
                        	this.popupSubFuncId = this.currentOperationCfg.subFuncMap[paramsJSON[this.currentOperationCfg.subFuncParentAttr]];
                        }else {
                        	this.popupSubFuncId = this.currentOperationCfg.subFuncId;
                        }
                        if(this.popupSubFuncId == null || this.popupSubFuncId.trim().length == 0 ) {
                            ElMessage(`未配置弹出查询子功能！`);
                            return;
                        }
                        
                        if(operation.exposedServiceMapping != null && operation.exposedServiceMapping.trim().length != 0) {
                            var url = "/lcdp-proxy/lowcode/" + this.funcDefine.platformUrl + "/" + this.funcDefine.systemUrl + "/" + operation.exposedServiceMapping;
                            var req = {};
                            if(operation.operation.inParams != null && operation.operation.inParams.length > 0) {
                                req = generateReqObject(operation.operation.inParams, this.regionData, operation, this.funcRegion, this.funcDefine, false);
                            }
                            axiosClient.post(url, req).then((response) => {//调用后台服务获取数据
                                
                                this.subData = response.data.data;
                                for(var key in paramsJSON) {
                                    if(key != "viewType" ) {
                                        this.subData[key] = paramsJSON[key];
                                    }
                                }
                                if(this.subData != null) {
                                    axiosClient.get("/lcdp-proxy/lowcode/fe/api/funcDefine/loadAll/" + this.popupSubFuncId).then((response) => {//加载子功能配置信息
                                        var data = response.data; 
                                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                                            this.subFuncDefine = data.data; 
                                            //进行数据处理，方便前台界面使用---start
                                            funcDefinPreProcess(this.subFuncDefine);
                                            //进行数据处理，方便前台界面使用---end
                                            this.dynamicComponent = shallowRef(defineAsyncComponent(getDynamicComponet(this.subFuncDefine.funcTemplate.url)));
                                            this.params = paramsJSON;
                                            this.subFuncName = this.subFuncDefine.name;
                                            this.showDynamicComponent = true;
                                        }else {
                                            ElMessage.error(`加载功能定义信息失败`);
                                        }
                                    });
                                }else {
                                    ElMessage.error(`请求后端服务失败！状态码：` + this.subData.status + `，错误信息：` + this.subData.message );
                                }
                            }).catch((e)=>{
                                ElMessage.error(`请求后端服务失败！` + e);
                            });
                        }else {
                            //新增时，不需要调用后端服务
                            if(viewType != 'C' && (operation.exposedServiceMapping == null || operation.exposedServiceMapping.trim().length == 0)) {
                                ElMessage(`未绑定后端服务！`);
                                return;
                            }
                            if(viewType == 'C') {
                                this.subData = [];
                                for(var key in paramsJSON) {
                                    if(key != "viewType" ) {
                                        this.subData[key] = paramsJSON[key];
                                    }
                                }
                                axiosClient.get("/lcdp-proxy/lowcode/fe/api/funcDefine/loadAll/" + this.popupSubFuncId).then((response) => {//加载子功能配置信息
                                    var data = response.data; 
                                    if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                                        this.subFuncDefine = data.data; 
                                        //进行数据处理，方便前台界面使用---start
                                        funcDefinPreProcess(this.subFuncDefine);
                                        //进行数据处理，方便前台界面使用---end
                                        this.dynamicComponent = shallowRef(defineAsyncComponent(getDynamicComponet(this.subFuncDefine.funcTemplate.url)));
                                        this.params = paramsJSON;
                                        this.subFuncName = this.subFuncDefine.name;
                                        this.showDynamicComponent = true;
                                    }else {
                                        ElMessage.error(`加载功能定义信息失败`);
                                    }
                                });
                            }
                        }
                    }
                }
            }else  {
                ElMessage.error(`错误的操作类型！`);
            }
        },
        hideViewPage() {
        	this.showDynamicComponent = false;
            this.dynamicComponent = null;
            this.subFuncDefine = null;
            this.subData = null;
            this.currentOperationCfg = null;
        },
        objectUpdated(newObject) {
        	var paramsJSON = this.processParamCfg(this.currentOperationCfg, this.regionData);
        	var returnParams = null;
        	if(this.currentOperationCfg.subFuncParentAttr != null && this.currentOperationCfg.subFuncParentAttr.trim().length > 0 && 
        			this.currentOperationCfg.returnParamsMap != null) {//如果配置多个子功能，根据子功能上级属性字段进行区分
        		
                if(typeof(this.currentOperationCfg.returnParams) == 'string') {
                    var map = JSON.parse(this.currentOperationCfg.returnParamsMap);
                    returnParams = map[paramsJSON[this.currentOperationCfg.subFuncParentAttr]];
                }else {
                    returnParams = this.currentOperationCfg.returnParamsMap[paramsJSON[this.currentOperationCfg.subFuncParentAttr]];
                }
            }else {
                if(typeof(this.currentOperationCfg.returnParams) == 'string') {
                    returnParams = JSON.parse(this.currentOperationCfg.returnParams);
                }else {
                    returnParams = this.currentOperationCfg.returnParams;
                }
            }
        	if(returnParams != null || returnParams.length > 0) {
        		//根据返回参数配置，更新regionData中的值
        		for(var i in returnParams) {
                    if(returnParams[i].valueType == "F") {//固定值
                        this.regionData[returnParams[i].targetAttr] = returnParams[i].value;
                    }else if(returnParams[i].valueType == "A") {//属性值
                        if(returnParams[i].targetAttr == null || returnParams[i].targetAttr.trim().length == 0) {
                        	this.regionData[returnParams[i].value] = newObject[returnParams[i].value];
                        }else {
                        	this.regionData[returnParams[i].targetAttr] = newObject[returnParams[i].value];
                        }
                    }else {
                        ElMessage.error(`非法的valueType: ` + returnParams[i].valueType);
                    }
                }
        	}
        	this.hideViewPage();
        },
        processParamCfg(currentCfg, regionData) {//根据参数配置解析参数值
        	var paramsJSON = {};
            if(currentCfg.params != null && currentCfg.params.length > 0) {
                var paramsCfgJson = null;
                if(typeof(currentCfg.params) == 'string') {
                    paramsCfgJson = JSON.parse(currentCfg.params);
                }else {
                    paramsCfgJson = currentCfg.params;
                }
                
                for(var i in paramsCfgJson) {
                    if(paramsCfgJson[i].valueType == "F") {//固定值
                        paramsJSON[paramsCfgJson[i].targetAttr] = paramsCfgJson[i].value;
                    }else if(paramsCfgJson[i].valueType == "A") {//属性值
                        if(paramsCfgJson[i].targetAttr == null || paramsCfgJson[i].targetAttr.trim().length == 0) {
                            paramsJSON[paramsCfgJson[i].value] = regionData[paramsCfgJson[i].value];
                        }else {
                            paramsJSON[paramsCfgJson[i].targetAttr] = regionData[paramsCfgJson[i].value];
                        }
                    }else {
                        ElMessage.error(`非法的valueType: ` + paramsCfgJson[i].valueType);
                        return null;
                    }
                }
            }
            return paramsJSON;
        }
    }
}
</script>