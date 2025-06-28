<!--弹出选择类显示组件-->
<template>
    <el-input type="hidden" v-model="regionData[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'P' && subObject == null" size="small"/>
    <el-input v-model="regionData[attrAssign.attribute.code+'_label']" v-if="attrAssign.displayType == 'P' && subObject == null && regionData[attrAssign.attribute.code+'_label'] != null && regionData[attrAssign.attribute.code+'_label'] != ''"  readonly size="small" width="50%">
        <template #append>
            <el-button  size="small" @click="popupSelect(attrAssign)"  :disabled="attrAssign.readonly=='Y' || disableFlag"><el-icon><search /></el-icon></el-button>
        </template>
    </el-input>
    <el-input v-model="regionData[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'P' && subObject == null && (regionData[attrAssign.attribute.code+'_label'] == null || regionData[attrAssign.attribute.code+'_label'] == '')"  readonly size="small" width="50%">
        <template #append>
            <el-button  size="small" @click="popupSelect(attrAssign)"  :disabled="attrAssign.readonly=='Y' || disableFlag"><el-icon><search /></el-icon></el-button>
        </template>
    </el-input>
    
    <el-input type="hidden" v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]" v-if="attrAssign.displayType == 'P' && subObject != null" size="small"/>
    <el-input v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_label']" v-if="attrAssign.displayType == 'P' && subObject != null && regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_label'] != null && regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_label'] != ''" readonly size="small" width="50%">
        <template #append>
            <el-button  size="small" @click="popupSelect(attrAssign)"  :disabled="attrAssign.readonly=='Y' || disableFlag"><el-icon><search /></el-icon></el-button>
        </template>
    </el-input>
	<el-input v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]" v-if="attrAssign.displayType == 'P' && subObject != null && (regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_label'] == null || regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_label'] == '')" readonly size="small" width="50%" >
        <template #append>
            <el-button  size="small" @click="popupSelect(attrAssign)"  :disabled="attrAssign.readonly=='Y' || disableFlag"><el-icon><search /></el-icon></el-button>
        </template>
    </el-input>
	
	<div>
	    <el-dialog v-model="showDynamicComponent" v-if="showDynamicComponent == true" title="弹出选择" width="900px" append-to-body>
            <component :is="dynamicComponent" v-if="popupSubFuncId == 'optionSelectComponent'" :params="params" @hideOptionSelect="hidePopupSelectSubFunc" @objectSelected="objectSelected"/>
            <component :is="dynamicComponent" v-else-if="popupSubFuncId == 'customizedPageComponent'" :funcDefine="funcDefine" :customizedPage="customizePageForShow" :regionData="regionData[customizePageForShow.id]" :parentPage="null" :srcOperation="null"  @hideDialogPage="hidePopupSelectSubFunc" @objectUpdated="objectSelected" @showDialogPage="null" @refreshTableData="null"/>
            <component :is="dynamicComponent" v-else :funcId="popupSubFuncId" :params="params" @hideSubFunc="hidePopupSelectSubFunc" @objectSelected="objectSelected" @clearSelect="clearSelect"/>
        </el-dialog>
	</div>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref, defineAsyncComponent, shallowRef } from 'vue';
import { ElMessage } from 'element-plus'
import {getDynamicComponet} from '../../js/common.js'


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
        //弹出选择-start
        const dynamicComponent = ref(null);
        const showDynamicComponent = ref(false);
        const popupSubFuncId = ref(null);
        const params = ref(null);
        const currentPopupAttr = ref(null);
        const customizePageForShow = ref(null);
        //弹出选择-end

        return {
            //弹出选择-start
            dynamicComponent,
            showDynamicComponent,
            popupSubFuncId,
            params,
            currentPopupAttr,
            customizePageForShow,
            //弹出选择-end
        }
    },
    computed: {
    	
    },
    watch: {        
        
    },
    mounted() {
        //console.log(this.attrAssign, this.disableFlag);
    	if(this.subObject == null) {
    		if(this.regionData[this.attrAssign.attribute.code+'_label'] == null) {
    			this.regionData[this.attrAssign.attribute.code+'_label'] = this.regionData[this.attrAssign.attribute.code];
            }
    	}else {
    		if(this.regionData[this.subObject.mainRelAttribute.code][this.attrAssign.attribute.code+'_label'] == null) {
    			this.regionData[this.subObject.mainRelAttribute.code][this.attrAssign.attribute.code+'_label'] = this.regionData[this.subObject.mainRelAttribute.code][this.attrAssign.attribute.code];
            }
    	}
        
    },
    
    methods: {
    	popupSelect(attrAssign) {
            this.currentPopupAttr = attrAssign;//设置当前属性
            var currentCfg = this.getCurrentPopupCfg();//获取当前对应的配置信息
            if(currentCfg == null) {
                return;
            }
            
            var paramsJSON = {};
            if(currentCfg.params != null && currentCfg.params.length > 0) {
                for(var i in currentCfg.params) {
                	if(currentCfg.params[i].valueType == "F") {//固定值
                        paramsJSON[currentCfg.params[i].targetAttr] = currentCfg.params[i].value;
                    }else if(currentCfg.params[i].valueType == "A") {//属性值
                        paramsJSON[currentCfg.params[i].targetAttr] = this.regionData[currentCfg.params[i].value];
                    }else {
                        ElMessage.error(`非法的valueType: ` + currentCfg.params[i].valueType);
                        return;
                    }
                    if(currentCfg.funcType == "O") {//当功能类型为选项选择页，只取第一条params配置
                    	if(currentCfg.params[i].labelAttr == null || currentCfg.params[i].labelAttr == '' 
                    			|| currentCfg.params[i].labelAttr == null || currentCfg.params[i].labelAttr == '') {
                    		ElMessage.error(`当功能类型为选项选择页时，必须指定值和标签列对应的属性！`);
                            return;
                        }
                        
                        paramsJSON.valueAttr = currentCfg.params[i].valueAttr;
                        paramsJSON.labelAttr = currentCfg.params[i].labelAttr;
                        paramsJSON.codeAttr = currentCfg.params[i].codeAttr;
                    	break;
                    }
                }
            }
            if(currentCfg.funcType == "O") {//当功能类型为选项选择页，只取第一条params配置
            	if(paramsJSON.options == null || !Array.isArray(paramsJSON.options)) {
            		ElMessage.error(`当功能类型为选项选择页时，必须指定选项列表！`);
                    return;
            	}
            }
            if(currentCfg.funcType == "O") {//optionSelectComponent，选项选择页
            	this.popupSubFuncId = "optionSelectComponent";
            	this.dynamicComponent = shallowRef(defineAsyncComponent(getDynamicComponet("/src/funcTpl/components/common/optionSelectComponent.vue")));
                this.params = paramsJSON;
                this.showDynamicComponent = true;
            }else if(currentCfg.funcType == "C") {//自定义页面
                if(currentCfg.subFuncId == null || currentCfg.subFuncId == "") {
                    ElMessage(`未配置弹出自定义页面！`);
                    return;
                }
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
                this.regionData[this.customizePageForShow.id] = paramsJSON;
                this.dynamicComponent = shallowRef(defineAsyncComponent(getDynamicComponet("/src/funcTpl/components/customizedFunc/customizedPage.vue")));
                this.showDynamicComponent = true;
            }else{//子功能
            	if(currentCfg.subFuncId == null || currentCfg.subFuncId == "") {
                    ElMessage(`未配置弹出查询子功能！`);
                    return;
                }
                this.popupSubFuncId = currentCfg.subFuncId;
            	axiosClient.get("/lcdp-proxy/lowcode/fe/api/funcDefine/loadFuncDefineAndTpl/" + this.popupSubFuncId).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                        var subFunc = data.data; 
                        this.dynamicComponent = shallowRef(defineAsyncComponent(getDynamicComponet(subFunc.funcTemplate.url)));
                        this.params = escape(JSON.stringify(paramsJSON));
                        this.showDynamicComponent = true;
                    }else {
                        ElMessage.error(`加载功能定义信息失败`);
                    }
                });
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
        hidePopupSelectSubFunc() {
        	this.showDynamicComponent = false;
        	this.dynamicComponent = null;
        	this.currentPopupAttr = null;
        },
        clearSelect() {
            if(this.subObject == null) {//主对象
        		this.regionData[this.attrAssign.attribute.code] = null;
                this.regionData[this.attrAssign.attribute.code+'_label'] = null;
            }else {//从对象
            	this.regionData[this.subObject.mainRelAttribute.code][this.attrAssign.attribute.code] = null;
                this.regionData[this.subObject.mainRelAttribute.code][this.attrAssign.attribute.code+'_label'] = null;
            }
            this.showDynamicComponent = false;
            this.dynamicComponent = null;
            this.currentPopupAttr = null;
        },
        popupValueSelected(attrCode, value, label, optionReturns) {
            console.log(attrCode, value, label, optionReturns, this.subObject);
            if(this.subObject == null) {//主对象
        		this.regionData[attrCode] = value;
                this.regionData[attrCode+'_label'] = label;
                if(optionReturns != null) {
                    for(var key in optionReturns) {
                        this.regionData[key] = optionReturns[key];
                    }
                }
            }else {//从对象
            	this.regionData[this.subObject.mainRelAttribute.code][attrCode] = value;
                this.regionData[this.subObject.mainRelAttribute.code][attrCode+'_label'] = label;
                if(optionReturns != null) {
                    for(var key in optionReturns) {
                        this.regionData[this.subObject.mainRelAttribute.code][key] = optionReturns[key];
                    }
                }
            }
        },
        objectSelected(data) {
        	if(data != null) {//如果返回对象不为空
        		var currentCfg = this.getCurrentPopupCfg();//获取当前对应的配置信息
                
        		//获取并设置返回对象的id和name信息
        		if(currentCfg.returnParams != null && currentCfg.returnParams.labelAttr != null && currentCfg.returnParams.valueAttr != null) {
                    var optionReturns = [];//额外返回信息
                    //处理额外返回的信息
                    if(currentCfg.optionReturnParams != null && currentCfg.optionReturnParams.length > 0) {
                        for(var i in currentCfg.optionReturnParams) {
                            var optionParam = currentCfg.optionReturnParams[i];
                            if(optionParam.valueType == 'F') {//固定值
                                optionReturns[optionParam.targetAttr] = optionParam.value;
                            }else if(optionParam.valueType == 'A') {//属性值
                                optionReturns[optionParam.targetAttr] = data[optionParam.value];
                            }else {
                                //非法的配置，不作处理
                            }
                            
                        }
                    }
        			this.popupValueSelected(this.currentPopupAttr.attribute.code, data[currentCfg.returnParams.valueAttr], data[currentCfg.returnParams.labelAttr], optionReturns);
                }
        	}
        	
        	this.showDynamicComponent = false;
            this.dynamicComponent = null;
            this.currentPopupAttr = null;
        }
        
    }
 }
</script>