<!--查看（弹出）类属性-->
<template>
    <el-input type="hidden" v-model="regionData[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'V' && subObject == null" size="small"/>
    <el-input v-model="regionData[attrAssign.attribute.code+'_label']" v-if="attrAssign.displayType == 'V' && subObject == null && regionData[attrAssign.attribute.code+'_label'] != null && regionData[attrAssign.attribute.code+'_label'] != '' && disableFlag == false" readonly size="small" width="50%">
        <template #append>
            <el-button  size="small" @click="popupView(attrAssign)"><el-icon><search /></el-icon></el-button>
        </template>
    </el-input>
    <el-input v-model="regionData[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'V' && subObject == null && (regionData[attrAssign.attribute.code+'_label'] == null || regionData[attrAssign.attribute.code+'_label'] == '') && disableFlag == false" readonly size="small" width="50%">
        <template #append>
            <el-button  size="small" @click="popupView(attrAssign)"><el-icon><search /></el-icon></el-button>
        </template>
    </el-input>
    <el-link v-if="attrAssign.displayType == 'V' && subObject == null && disableFlag == true" type="primary" @click="popupView(attrAssign)">{{(regionData[attrAssign.attribute.code+'_label'] != null && regionData[attrAssign.attribute.code+'_label'] != '') ? regionData[attrAssign.attribute.code+'_label']:regionData[attrAssign.attribute.code]}}</el-link>
    
    <el-input type="hidden" v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]" v-if="attrAssign.displayType == 'V' && subObject != null" size="small"/>
    <el-input v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_label']" v-if="attrAssign.displayType == 'V' && subObject != null && regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_label'] != null && regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_label'] != '' && disableFlag == false" readonly size="small" width="50%">
        <template #append>
            <el-button  size="small" @click="popupView(attrAssign)"><el-icon><search /></el-icon></el-button>
        </template>
    </el-input>
	<el-input v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]" v-if="attrAssign.displayType == 'V' && subObject != null && (regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_label'] == null || regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_label'] == '') && disableFlag == false" readonly size="small" width="50%">
        <template #append>
            <el-button  size="small" @click="popupView(attrAssign)"><el-icon><search /></el-icon></el-button>
        </template>
    </el-input>
    <el-link v-if="attrAssign.displayType == 'V' && subObject != null && disableFlag == true" type="primary" @click="popupView(attrAssign)">{{(regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_label'] != null && regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_label'] != '') ? regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_label']:regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]}}</el-link>
	
    <el-dialog v-model="showDynamicComponent" v-if="showDynamicComponent == true" top="5vh" :title="subFuncName" width="1000px" append-to-body>
        <component :is="dynamicComponent" v-if="popupSubFuncId == 'viewProcessComponent'" :params="params" @hideViewPage="hideViewPage"/>
        <component :is="dynamicComponent" v-else-if="popupSubFuncId == 'customizedPageComponent'" :funcDefine="funcDefine" :customizedPage="customizePageForShow" :regionData="regionData[customizePageForShow.id]" :parentPage="null" :srcOperation="null"  @hideDialogPage="hideViewPage" @objectUpdated="objectUpdated" @showDialogPage="null" @refreshTableData="null"/>
        <component :is="dynamicComponent" v-else :funcDefine="subFuncDefine" :params="params" :data="subData" @hideViewPage="hideViewPage" @objectUpdated="objectUpdated"/>
    </el-dialog>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref, defineAsyncComponent, shallowRef } from 'vue';
import { ElMessage } from 'element-plus'
import {processLabelValue, generateReqObject, funcDefinPreProcess, getDynamicComponet} from '../../js/common.js'


export default {
    props: ['funcRegion','funcDefine','regionData', 'attrAssign', 'disableFlag', 'subObject'],
    
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
        const customizePageForShow = ref(null);
        const popupSubFuncId = ref(null);
        const params = ref(null);
        const subData = ref([]);
        const subFuncDefine = ref(null);
        const subFuncName = ref('');
        const operation = ref({});
        
        return {
            dynamicComponent,
            customizePageForShow,
            showDynamicComponent,
            popupSubFuncId,
            params,
            subData,
            subFuncDefine,
            subFuncName,
            operation
        }
    },
    computed: {
    	
    },
    watch: {        
        
    },
    mounted() {
        var displayCfg = this.attrAssign.displayCfgJSON;
        if(displayCfg != null && displayCfg.popupView != null 
                        && displayCfg.popupView.operationId != null && displayCfg.popupView.operationId.trim().length != 0) {
            axiosClient.get("/lcdp-proxy/lowcode/fe/api/operation/loadAll/" + displayCfg.popupView.operationId).then((response) => {//加载方法配置信息
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) {
                    this.operation = data.data; 
                }
            });
        }
    },
    
    methods: {
        popupView(attrAssign) {
            console.log(attrAssign);
            if(this.funcDefine.objectMap == null) {
                return;
            }
            this.currentPopupAttr = attrAssign;//设置当前属性
            var currentCfg = this.getCurrentPopupCfg();//获取当前对应的配置信息
           
            if(currentCfg == null) {
                return;
            }
            
            var paramsJSON = this.processParamCfg(currentCfg, this.regionData);
            
            if(currentCfg.funcType == "P") {//viewProcessComponent，选项选择页
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
                if(currentCfg.subFuncParentAttr != null && currentCfg.subFuncParentAttr.trim().length > 0 
                		&& currentCfg.subFuncMap != null) {//如果配置多个子功能，根据子功能上级属性字段进行区分
                	this.popupSubFuncId = currentCfg.subFuncMap[paramsJSON[currentCfg.subFuncParentAttr]];
                }else {
                	this.popupSubFuncId = currentCfg.subFuncId;
                }
                if(this.popupSubFuncId == null || this.popupSubFuncId.trim().length == 0 ) {
                    ElMessage(`未配置弹出查看子功能！`);
                    return;
                }
                
                if(attrAssign.displayCfgJSON != null && attrAssign.displayCfgJSON.popupView != null 
                        && attrAssign.displayCfgJSON.popupView.operationId != null && attrAssign.displayCfgJSON.popupView.operationId.trim().length != 0
                        && attrAssign.displayCfgJSON.popupView.httpMapping != null && attrAssign.displayCfgJSON.popupView.httpMapping.trim().length != 0) {

                    var url = "/lcdp-proxy/lowcode/" + this.funcDefine.platformUrl + "/" + this.funcDefine.systemUrl + "/" + attrAssign.displayCfgJSON.popupView.httpMapping;
                    var req = {};
                    if(this.operation.inParams != null && this.operation.inParams.length > 0) {
                        req = generateReqObject(this.operation.inParams, this.regionData, this.operation, this.funcRegion, this.funcDefine, false);
                    }
                    axiosClient.post(url, req).then((response) => {//调用后台服务获取数据
                        console.log(response.data);
                        this.subData = response.data.data;
                        if(this.subData != null) {
                            if(currentCfg.funcType == "C") {//自定义页面
                                this.popupSubFuncId = "customizedPageComponent";
                                var customizedPage = null;
                                for(var i = 0; i<this.funcDefine.customizedPages.length; i++) {
                                    if(this.funcDefine.customizedPages[i].id == currentCfg.subFuncId) {
                                        customizedPage = this.funcDefine.customizedPages[i];
                                        break;
                                    }
                                }
                                if(customizedPage == null) {
                                    ElMessage.error("invalid customizedPageId!");
                                }
                                this.customizePageForShow = customizedPage;
                                this.regionData[this.customizePageForShow.id] = this.subData;
                                if(this.subData != null) {
                                    for(var i in this.customizePageForShow.customizedLayouts) {
                                        var targetLayout = this.customizePageForShow.customizedLayouts[i];
                                        if(targetLayout != null && targetLayout.layoutRegions != null && targetLayout.layoutRegions.length > 0 && targetLayout.layoutRegions[0].funcRegion != null) {
                                            processLabelValue(targetLayout.layoutRegions[0].funcRegion, this.regionData[this.customizePageForShow.id], null, true, this.funcDefine);
                                        }
                                    }
                                }
                                this.dynamicComponent = shallowRef(defineAsyncComponent(getDynamicComponet("/src/funcTpl/components/customizedFunc/customizedPage.vue")));
                                this.showDynamicComponent = true;
                            }else {
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
                        }else {
                            ElMessage.error(`请求后端服务失败！状态码：` + response.data.status + `，错误信息：` + response.data.message );
                        }
                    }).catch((e)=>{
                        ElMessage.error(`请求后端服务失败！` + e);
                    });
                }else {
                    this.subData = [];
                    for(var key in paramsJSON) {
                        if(key != "viewType" ) {
                            this.subData[key] = paramsJSON[key];
                        }
                    }
                    if(currentCfg.funcType == "C") {//自定义页面
                        this.popupSubFuncId = "customizedPageComponent";
                        var customizedPage = null;
                        for(var i = 0; i<this.funcDefine.customizedPages.length; i++) {
                            if(this.funcDefine.customizedPages[i].id == currentCfg.subFuncId) {
                                customizedPage = this.funcDefine.customizedPages[i];
                                break;
                            }
                        }
                        if(customizedPage == null) {
                            ElMessage.error("invalid customizedPageId!");
                        }
                        this.customizePageForShow = customizedPage;
                        this.regionData[this.customizePageForShow.id] = this.subData;
                        this.dynamicComponent = shallowRef(defineAsyncComponent(getDynamicComponet("/src/funcTpl/components/customizedFunc/customizedPage.vue")));
                        this.showDynamicComponent = true;
                    }else {
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
        },
        getCurrentPopupCfg() {//获取当前弹出查询配置信息
        	var currentObjectAssign = this.funcDefine.objectMap[this.currentPopupAttr.objectId];//当前绑定对象信息
        	if(currentObjectAssign.attrOptionMap == null 
                    || currentObjectAssign.attrOptionMap[this.currentPopupAttr.attributeId] == null
                    || currentObjectAssign.attrOptionMap[this.currentPopupAttr.attributeId].popupCfg == null
                    || currentObjectAssign.attrOptionMap[this.currentPopupAttr.attributeId].popupCfg.trim().length == 0) {
                ElMessage(`未配置弹出查询信息！`);
                return;
            }
            var attrOptions = currentObjectAssign.attrOptionMap[this.currentPopupAttr.attributeId];//当前绑定属性的配置信息
            var popupCfgJSON = JSON.parse(attrOptions.popupCfg);//弹出查询信息
            var currentCfg = null;//定义当前配置属性
            if(attrOptions.interactionType == null || attrOptions.interactionType == "") {//为空时，无联动
                currentCfg =popupCfgJSON["default"];//无联动时使用default属性的配置
            }else if((attrOptions.interactionType == "V" || attrOptions.interactionType == "A") && this.funcRegion.regionAttrAssigns != null) {//属性值联动，需要根据上级属性的值获取对应的配置
                for(var index in this.funcRegion.regionAttrAssigns) {
                    if(this.funcRegion.regionAttrAssigns[index].attributeId == attrOptions.parentAttributeId) {//获取上级属性的绑定属性信息
                    	if(this.subObject == null) {//主对象
                    		if(this.regionData[this.funcRegion.regionAttrAssigns[index].attribute.code] == null ||
                                    this.regionData[this.funcRegion.regionAttrAssigns[index].attribute.code] == '') {
                                ElMessage(`请先选择关联属性值（` + this.funcRegion.regionAttrAssigns[index].attribute.name + `）!`);
                                return;
                            }
                            currentCfg =popupCfgJSON[this.regionData[this.funcRegion.regionAttrAssigns[index].attribute.code]];//根据上级属性的值获取对应的配置
                    	}else {//从对象
                    		if(this.regionData[this.subObject.mainRelAttribute.code][this.funcRegion.regionAttrAssigns[index].attribute.code] == null ||
                                    this.regionData[this.subObject.mainRelAttribute.code][this.funcRegion.regionAttrAssigns[index].attribute.code] == '') {
                                ElMessage(`请先选择关联属性值（` + this.funcRegion.regionAttrAssigns[index].attribute.name + `）!`);
                                return;
                            }
                            currentCfg =popupCfgJSON[this.regionData[this.subObject.mainRelAttribute.code][this.funcRegion.regionAttrAssigns[index].attribute.code]];//根据上级属性的值获取对应的配置
                    	}
                    	
                        break;
                    }
                }
            }
            if(currentCfg == null) {
            	currentCfg =popupCfgJSON["default"];//找不到对应配置时，使用default属性的配置
            }
            return currentCfg;
        },
        hideViewPage() {
        	this.showDynamicComponent = false;
            this.dynamicComponent = null;
            this.subFuncDefine = null;
            this.subData = null;
        },
        objectUpdated(newObject) {
            var currentCfg = this.getCurrentPopupCfg();//获取当前对应的配置信息
        	var paramsJSON = this.processParamCfg(currentCfg, this.regionData);
        	var returnParams = null;
            
        	if(currentCfg.subFuncParentAttr != null && currentCfg.subFuncParentAttr.trim().length > 0 && 
        			currentCfg.returnParamsMap != null) {//如果配置多个子功能，根据子功能上级属性字段进行区分
        		returnParams = currentCfg.returnParamsMap[paramsJSON[currentCfg.subFuncParentAttr]];
            }else {
            	returnParams = currentCfg.returnParams;
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
                for(var i in currentCfg.params) {
                    if(currentCfg.params[i].valueType == "F") {//固定值
                        paramsJSON[currentCfg.params[i].targetAttr] = currentCfg.params[i].value;
                    }else if(currentCfg.params[i].valueType == "A") {//属性值
                        if(currentCfg.params[i].targetAttr == null || currentCfg.params[i].targetAttr.trim().length == 0) {
                            paramsJSON[currentCfg.params[i].value] = regionData[currentCfg.params[i].value];
                        }else {
                            paramsJSON[currentCfg.params[i].targetAttr] = regionData[currentCfg.params[i].value];
                        }
                    }else {
                        ElMessage.error(`非法的valueType: ` + currentCfg.params[i].valueType);
                        return null;
                    }
                }
            }
            return paramsJSON;
        }
    }
 }
</script>