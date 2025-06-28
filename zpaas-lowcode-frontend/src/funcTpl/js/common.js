/**
 * 功能模板公共函数
 */
import axiosClient from '/src/js/utils/axios.js';
import {ElMessage, ElMessageBox} from 'element-plus';
import { ref } from 'vue';

const modules = import.meta.glob(['/src/funcTpl/components/common/viewProcessComponent.vue', '/src/funcTpl/components/common/optionSelectComponent.vue','/src/funcTpl/components/customizedFunc/customizedPage.vue','/src/funcTpl/components/querySelectSubFunc/querySelectSubFunc.vue', '/src/funcTpl/components/simpleObjectSubFunc/simpleObjectSubFunc.vue', '/src/funcTpl/components/moreObjectSubFunc/moreObjectSubFunc.vue']);

/**
 * 返回动态加载的组件
 * @param  url 
 * @returns 
 */
export function getDynamicComponet(url) {
    return modules[url];
}

/**
 * 联动处理,当发生属性值变更时触发
 * newRegionData 新数据
 * oldRegionData 原数据
 * funcDefine 功能配置信息
 * funcRegion 区域配置信息
 * subObjectAssign 从对象配置信息
 * regionData 区域数据信息
 */
export function interactionProcess(newRegionData, oldRegionData, funcDefine, funcRegions, subObjectAssign, regionData, formatRegionAttrDisplay) {
    //当新旧数据对象为空时，不进行处理
    if (newRegionData == null || oldRegionData == null) {
        return;
    }
    //当功能以及区域配置信息为空时，不进行处理
    if (funcDefine == null || funcRegions == null || regionData == null) {
        return;
    }

    var regions = null;
    if(isJsonObject(funcRegions)) {
        var regions = [];
        regions.push(funcRegions);
    }else if(isJsonArray(funcRegions)) {
        regions = funcRegions;
    }
    //获取发生值变化的属性
    var srcAttrAssign = null;
    var srcAttrValue = null;
    var needReformatDisplay = false;//是否需要重新布局页面
    for (var key in newRegionData) {//循环比对新旧数据对象中的属性，找到发生变化的属性为止
        //暂不处理列表类型属性联动的情况，一对多类型的从对象属性
        if (Array.isArray(newRegionData[key]) || Array.isArray(oldRegionData[key])) {
            continue;
        }
        //属性是对象的情况，一对一类型的从对象属性
        if (newRegionData[key] instanceof Object || oldRegionData[key] instanceof Object) {
            for (var subKey in newRegionData[key]) {
                //从对象属性发生变化
                if (newRegionData[key] != null && oldRegionData[key] != null && newRegionData[key][subKey] != oldRegionData[key][subKey]) {//该属性的值发生了变化
                    srcAttrValue = newRegionData[key][subKey];//获取新值
                    for(var i in regions) {
                        var funcRegion = regions[i];
                        for (var index in funcRegion.regionAttrAssigns) {//循环绑定属性列表，找到值发生变化属性对应的绑定属性信息
                            var rowAttrAssign = funcRegion.regionAttrAssigns[index];
                            if (subObjectAssign != null && rowAttrAssign.objectAssignId != subObjectAssign.id) {//subObjectAssign不为空时，非当前编辑从对象，跳过
                                continue;
                            }
                            if (subKey == rowAttrAssign.attribute.code && funcDefine.objectMap[rowAttrAssign.objectId].mainRelAttribute != null && key == funcDefine.objectMap[rowAttrAssign.objectId].mainRelAttribute.code) {//发生变化属性对应的绑定属性信息
                                srcAttrAssign = rowAttrAssign;//找到对应的区域绑定属性
                                if(onSrcAttrValueChanged(srcAttrAssign, srcAttrValue, funcDefine, regions, subObjectAssign, regionData, oldRegionData)) {
                                    needReformatDisplay = true;
                                }
                                break;//找到对应的属性信息后，跳出循环
                            }
                        }
                    }
                }
            }
        }else if (newRegionData[key] != oldRegionData[key]) {//一级属性的情况，如主对象属性 //该属性的值发生了变化，一次触发只会有一个属性发生变化
            srcAttrValue = newRegionData[key];//获取新值
            for(var i in regions) {
                var funcRegion = regions[i];
                for (var index in funcRegion.regionAttrAssigns) {//循环绑定属性列表，找到值发生变化属性对应的绑定属性信息
                    var rowAttrAssign = funcRegion.regionAttrAssigns[index];
                    if (subObjectAssign != null && rowAttrAssign.objectAssignId != subObjectAssign.id) {//subObjectAssign不为空时，表示非当前编辑从对象，跳过
                        continue;
                    }
                    if (key == rowAttrAssign.attribute.code) {//发生变化属性对应的绑定属性信息
                        srcAttrAssign = rowAttrAssign;//找到对应的区域绑定属性
                        if(onSrcAttrValueChanged(srcAttrAssign, srcAttrValue, funcDefine, regions, subObjectAssign, regionData, oldRegionData)) {//找到对应的属性信息后
                            needReformatDisplay = true;
                        }
                        break;
                    }
                }
            }
        }
    }

    if(needReformatDisplay && formatRegionAttrDisplay != null) {//需要重新布局页面
        formatRegionAttrDisplay();
    }
}

//响应属性值的变化
function onSrcAttrValueChanged(srcAttrAssign, srcAttrValue, funcDefine, regions, subObjectAssign, regionData, oldRegionData) {
    var needReformatDisplay = false;//是否需要重新布局页面
    //找到与源属性存在联动关系的属性进行处理
    for(var i in regions) {
        var funcRegion = regions[i];
        for (var index in funcRegion.regionAttrAssigns) {//循环绑定属性列表，找到值发生变化属性对应的绑定属性信息
            const regionAttrAssign = funcRegion.regionAttrAssigns[index];
            if (subObjectAssign != null && regionAttrAssign.objectAssignId != subObjectAssign.id) {//subObjectAssign不为空时，表示非当前编辑从对象，跳过
                continue;
            }
            //找到对应的绑定对象
            const assignObject_attr = funcDefine.objectMap[regionAttrAssign.objectId];
            //获取对应属性选项配置信息
            var attrOption = assignObject_attr.attrOptionMap[regionAttrAssign.attributeId];//当前绑定属性的配置信息
            //当前属性配置了联动处理且上级属性是源属性
            if (attrOption != null && (attrOption.interactionType != null && attrOption.interactionType != "N")
                && attrOption.parentAttributeId == srcAttrAssign.attributeId && attrOption.parentObjectAssignId == srcAttrAssign.objectAssignId) {
                if(attrOption.interactionType == "V" || attrOption.interactionType == "A") {//属性值联动
                    //主对象属性或者在从对象编辑页面
                    if (assignObject_attr.assignType == 'M' || subObjectAssign != null || regionData[assignObject_attr.mainRelAttribute.code] == null) {
                        if(regionData[regionAttrAssign.attribute.code] == oldRegionData[regionAttrAssign.attribute.code]) {//如果联动属性的值已经发生过变化，则不进行变更
                            if(regionData[regionAttrAssign.attribute.code] == null) {
                                regionData[regionAttrAssign.attribute.code] = "";//将当前属性的值清空
                            }else {
                                regionData[regionAttrAssign.attribute.code] = null;//将当前属性的值清空
                            }
                            if (regionAttrAssign.displayType == "P" || regionAttrAssign.displayType == "V") {//弹出选择类型
                                regionData[regionAttrAssign.attribute.code + "_label"] = "";//将当前属性的值名称清空
                            }
                            if(regionData[regionAttrAssign.attribute.code + "_options"] != undefined) {
                                regionData[regionAttrAssign.attribute.code + "_options"] = null;
                            }
                        }
                    } else {//从对象属性
                        if (Array.isArray(regionData[assignObject_attr.mainRelAttribute.code])) {//一对多类型的从对象暂不支持
                            continue;
                        }
                        if(regionData[assignObject_attr.mainRelAttribute.code][regionAttrAssign.attribute.code] == oldRegionData[assignObject_attr.mainRelAttribute.code][regionAttrAssign.attribute.code]) {//如果联动属性的值已经发生过变化，则不进行变更
                            if(regionData[assignObject_attr.mainRelAttribute.code][regionAttrAssign.attribute.code] == null) {
                                regionData[assignObject_attr.mainRelAttribute.code][regionAttrAssign.attribute.code] = "";//将当前属性的值清空
                            }else {
                                regionData[assignObject_attr.mainRelAttribute.code][regionAttrAssign.attribute.code] = null;//将当前属性的值清空
                            }
                            if (regionAttrAssign.displayType == "P" || regionAttrAssign.displayType == "V") {//弹出选择类型
                                regionData[assignObject_attr.mainRelAttribute.code][regionAttrAssign.attribute.code + "_label"] = "";//将当前属性的值名称清空
                            }
                            if(regionData[assignObject_attr.mainRelAttribute.code][regionAttrAssign.attribute.code + "_options"] != undefined) {
                                regionData[assignObject_attr.mainRelAttribute.code][regionAttrAssign.attribute.code + "_options"] = null;
                            }
                        }
                    }
                    if(attrOption.optionCfgType == 'S' && (attrOption.popupCfg == null || attrOption.popupCfg.trim().length == 0)) {//SQL方式,更新下拉列表选项 // && srcAttrValue != null && srcAttrValue.trim().length > 0
                        var params = null;
                        if (assignObject_attr.assignType == 'M' || subObjectAssign != null || regionData[assignObject_attr.mainRelAttribute.code] == null) {
                            params = processLabelValueParams(attrOption, srcAttrValue, regionData, null);
                        }else {
                            params = processLabelValueParams(attrOption, srcAttrValue, regionData, assignObject_attr.mainRelAttribute.code);
                        }
                        if(params == null) {
                            params = {};
                        }else {
                            if(params.parentAttrValue == null || params.parentAttrValue.length == 0) {
                                params.parentAttrValue = srcAttrValue;
                            } 
                        }
                        axiosClient.post("/lcdp-proxy/lowcode/fe/api/funcObjectAttrOptions/getOptionsWithParent/" + attrOption.id + "/" + params.parentAttrValue, params.params).then((response) => {
                            var data1 = response.data;
                            if (data1 != null && data1.status == "200" && data1.data != null) {
                                if (assignObject_attr.assignType == 'M' || subObjectAssign != null || regionData[assignObject_attr.mainRelAttribute.code] == null) {
                                    regionData[regionAttrAssign.attribute.code + "_options"] = data1.data;//将当前属性的下拉列表
                                } else {//从对象属性
                                    if (!Array.isArray(regionData[assignObject_attr.mainRelAttribute.code])) {//一对多类型的从对象暂不支持
                                        regionData[assignObject_attr.mainRelAttribute.code][regionAttrAssign.attribute.code + "_options"] = data1.data;//将当前属性的下拉列表
                                    }
                                }
                            }
                        });
                    }
                }
                
                //显示隐藏控制
                if(attrOption.interactionType == "D" || attrOption.interactionType == "A") {
                    if(attrOption.displayHideCfg == null || attrOption.displayHideCfg.trim().length == 0) {//未配置显示隐藏信息
                        continue;
                    }
                    attrOption.displayHideCfgJSON = JSON.parse(attrOption.displayHideCfg);
                    if(attrOption.displayHideCfgJSON == null) {
                        continue;
                    }

                    var mode = attrOption.displayHideCfgJSON[srcAttrValue];//获取显示隐藏模式
                    if(mode == null) {
                        mode = '1';//未配置，则默认为显示
                    }

                    if(regionAttrAssign.displayHiddenMode  == '0' && mode != '0') {//从隐藏到显示
                        needReformatDisplay = true;
                    }else if(regionAttrAssign.displayHiddenMode != '0' && mode == '0') {//从显示到隐藏
                        needReformatDisplay = true;
                    }

                    regionAttrAssign.displayHiddenMode =  mode;
                }
            }
        }
    }
    return needReformatDisplay;
}

/**
 * 功能定义数据预处理
 * funcDefine：功能定义信息
 */
export function funcDefinPreProcess(funcDefine) {
    var attributeMap = {};//attributeId=>Attriubte
    var dataSetDetailMap = {};//detailId=>DataSetDetail,用于数据集对象
    if (funcDefine.objectAssigns != null) {
        var objectMap = {};//设置对象Map，方便页面使用，objectId=》FuncObjectAssign
        for (var i in funcDefine.objectAssigns) {
            var row = funcDefine.objectAssigns[i];
            objectMap[row.objectId] = row;
            if (row.attributes != null) {
                for (var j in row.attributes) {
                    attributeMap[row.attributes[j].id] = row.attributes[j];
                }
            }
            if (row.dataSetDetails != null) {
                for (var j in row.dataSetDetails) {
                    dataSetDetailMap[row.dataSetDetails[j].id] = row.dataSetDetails[j];
                }
            }
            if(row.attrOptionMap == null) {
                row.attrOptionMap = {};
            }
        }
        funcDefine.objectMap = objectMap;
    }
    if (funcDefine.funcRegions != null && funcDefine.funcRegions.length > 0) {
        var regionMap = {};//tplReginId-》FuncRegion，方便前台界面使用
        var targetRegionMap = {};//funcReginId-》FuncRegion，方便前台界面使用，用于获取目标区域
        for (var i in funcDefine.funcRegions) {
            var row = funcDefine.funcRegions[i];
            regionMap[row.tplRegionId] = row;
            targetRegionMap[row.id] = row;
            //补充绑定对象的属性信息
            if (row.regionAttrAssigns != null) {
                for (var j in row.regionAttrAssigns) {
                    row.regionAttrAssigns[j].attribute = attributeMap[row.regionAttrAssigns[j].attributeId];
                    row.regionAttrAssigns[j].dataSetDetail = dataSetDetailMap[row.regionAttrAssigns[j].attributeId];
                    //将displayCfg：String转换成displayCfgJSON
                    if(row.regionAttrAssigns[j].displayCfg != null && row.regionAttrAssigns[j].displayCfg.trim().length > 0) {
                        row.regionAttrAssigns[j].displayCfgJSON = JSON.parse(row.regionAttrAssigns[j].displayCfg);
                    }
                }
            }
        }
        funcDefine.regionMap = regionMap;
        funcDefine.targetRegionMap = targetRegionMap;
    }
    if(funcDefine.customizedPages != null && funcDefine.customizedPages.length > 0) {
        for(var i in funcDefine.customizedPages) {
            var customizedPage = funcDefine.customizedPages[i];
            funcDefinPreProcessCustomizedLayout(customizedPage.customizedLayouts, attributeMap, dataSetDetailMap);
        }
    }
}

/**
 * 自定义功能布局数据预处理
 * @param {*} customizedLayouts 
 * @param {*} attributeMap 
 * @returns 
 */
function funcDefinPreProcessCustomizedLayout(customizedLayouts, attributeMap, dataSetDetailMap) {
    if(customizedLayouts == null || customizedLayouts.length == 0) {
        return;
    }
    for(var i in customizedLayouts) {
        var customizedLayout = customizedLayouts[i];
        if(customizedLayout.layoutRegions !=  null) {
            for(var j in customizedLayout.layoutRegions) {
                var layoutRegion = customizedLayout.layoutRegions[j];
                if(layoutRegion.funcRegion != null) {
                    if (layoutRegion.funcRegion.regionAttrAssigns != null) {
                        for (var k in layoutRegion.funcRegion.regionAttrAssigns) {
                            var regionAttrAssign = layoutRegion.funcRegion.regionAttrAssigns[k];
                            regionAttrAssign.attribute = attributeMap[regionAttrAssign.attributeId];
                            regionAttrAssign.dataSetDetail = dataSetDetailMap[regionAttrAssign.attributeId];
                            //将displayCfg：String转换成displayCfgJSON
                            if(regionAttrAssign.displayCfg != null && regionAttrAssign.displayCfg.trim().length > 0) {
                                regionAttrAssign.displayCfgJSON = JSON.parse(regionAttrAssign.displayCfg);
                            }else {
                                regionAttrAssign.displayCfgJSON = {};
                            }
                        }
                    }

                    if (layoutRegion.funcRegion.regionOperations != null) {
                        for (var k in layoutRegion.funcRegion.regionOperations) {
                            var regionOperation = layoutRegion.funcRegion.regionOperations[k];
                            //将displayCfg：String转换成displayCfgJSON
                            if(regionOperation.operationCfg != null && regionOperation.operationCfg.trim().length > 0) {
                                regionOperation.operationCfgJSON = JSON.parse(regionOperation.operationCfg);
                            }else {
                                regionOperation.operationCfgJSON = {};
                            }
                        }
                    }
                }
            }
        }

        if(customizedLayout.layoutCfg == null || customizedLayout.layoutCfg.trim().length == 0) {
            customizedLayout.layoutCfgJSON = {};
        }else {
            customizedLayout.layoutCfgJSON = JSON.parse(customizedLayout.layoutCfg);
        }

        if(customizedLayout.subCustomizedLayouts != null) {
            funcDefinPreProcessCustomizedLayout(customizedLayout.subCustomizedLayouts, attributeMap, dataSetDetailMap);
        }
    }
}

/**
 * 格式化属性的显示位置以及换行等
 * attrAssigns 绑定属性列表
 * subObjectAssign 从绑定对象，可为空，用于判断是否当前对象的属性
 */
export function formatAttrDisplayInfo(attrAssigns, subObjectAssign) {
    var row = 0;
    var attrMap = {};
    var columnCountOfRow = 0;
    attrMap[0] = [];
    for (var index = 0; index < attrAssigns.length; index++) {
        if (subObjectAssign != null && attrAssigns[index].objectAssignId != subObjectAssign.id) {//非当前编辑从对象，跳过
            continue;
        }
        //规范化一些为空的属性
        if (attrAssigns[index].colSpan == null || attrAssigns[index].colSpan == 0 || attrAssigns[index].colSpan == '0') {
            if (attrAssigns[index].displayType == 'H') {
                attrAssigns[index].colSpan = 0;
            } else {
                attrAssigns[index].colSpan = 1;
            }
        } else {
            attrAssigns[index].colSpan = parseInt(attrAssigns[index].colSpan);
        }
        
        if (attrAssigns[index].displayType == 'H') {
            attrAssigns[index].colSpan = 0;
        } else if (attrAssigns[index].colSpan > 3) {//当某个属性的colspan超过3时，独占一行
            attrAssigns[index].colSpan = 3;//colspan最长支持3
        }
        
        if (attrAssigns[index].rowSpan == null || attrAssigns[index].rowSpan == 0  || attrAssigns[index].rowSpan == '0') {
            attrAssigns[index].rowSpan = 1;
        }else {
            attrAssigns[index].rowSpan = parseInt(attrAssigns[index].rowSpan);
        }

        if(attrAssigns[index].displayHiddenMode != null) {//存在显示隐藏的控制
            if(attrAssigns[index].originColSpan == null) {
                if(attrAssigns[index].colSpan > 0) {
                    attrAssigns[index].originColSpan = attrAssigns[index].colSpan;
                }else {
                    attrAssigns[index].originColSpan = 0;
                }
            }

            if(attrAssigns[index].displayHiddenMode == '0') {//当前要隐藏
                attrAssigns[index].colSpan = 0;//隐藏
            }else {//当前要显示
                if(attrAssigns[index].colSpan == 0) {//恢复显示
                    attrAssigns[index].colSpan = attrAssigns[index].originColSpan;
                }
            }
        }

        //当前行放不下的情况，需要先进行换行操作
        if (columnCountOfRow != 0 && (columnCountOfRow + attrAssigns[index].colSpan) > 3) {
            attrAssigns[index - 1].colSpan += (3 - columnCountOfRow)//使前一个属性的colspan占满剩下的空间
            columnCountOfRow = 3;//进行换行操作
            if (columnCountOfRow != 0 && columnCountOfRow % 3 == 0) {
                columnCountOfRow = 0;
                row++;
                attrMap[row] = [];
            }
        }
        //处理当前的属性
        attrMap[row].push(attrAssigns[index]);
        columnCountOfRow = columnCountOfRow + attrAssigns[index].colSpan;
        //进行分行判断
        if (columnCountOfRow != 0 && columnCountOfRow % 3 == 0) {
            columnCountOfRow = 0;
            row++;
            attrMap[row] = [];
        }
    }
    return attrMap;
}

/**
 * 拼装参数列表
 * attrOptions 属性选项配置信息
 * parentAttrValue 上级属性值
 * targetRegionData 区域数据
 * 返回参数列表
 */
function processLabelValueParams(attrOptions, parentAttrValue, targetRegionData, mainRelAttribute) {
    //尝试获取其他参数
    var result = {};//返回对象
    var params = [];
    var currentCfg = null;
    var defaultCfg = null;
    if (attrOptions.optionCfg != null && attrOptions.optionCfg.trim().length != 0) {
        
        var optionCfgs = eval(attrOptions.optionCfg);
        //根据上级属性值获取对应的配置
        for (var i in optionCfgs) {
            var parent = null;//默认使用上级属性值
            //如果有配置catagoryAttr属性，则使用catagoryAttr的配置来获取parent的值
            if(optionCfgs[i].catagoryAttr != null && optionCfgs[i].catagoryAttr.valueType != null && optionCfgs[i].catagoryAttr.valueType.length > 0) {
                if (optionCfgs[i].catagoryAttr.valueType == "F") {//固定值
                    parent = optionCfgs[i].catagoryAttr.value;
                } else if (optionCfgs[i].catagoryAttr.valueType == "A") {//属性值
                    if(mainRelAttribute != null && mainRelAttribute.length > 0) {//当mainRelAttribute不为空时，优先从子对象中获取值
                        if(targetRegionData[mainRelAttribute] != null) {
                            parent = targetRegionData[mainRelAttribute][optionCfgs[i].catagoryAttr.value];
                        }
                    }
                    if(parent == null || parent.trim().length == 0) {//子对象取不到时，尝试从主对象获取
                        parent = targetRegionData[optionCfgs[i].catagoryAttr.value];
                    }
                } else if (optionCfgs[i].catagoryAttr.valueType == "P") {//上级属性
                    parent = parentAttrValue;
                } else {
                    ElMessage.error(`非法的valueType: ` + optionCfgs[i].catagoryAttr.valueType);
                    return;
                }
            }
            if(parent == null || parent.trim().length == 0) {//如果获取不到，则使用上级属性值
                parent = parentAttrValue;
            }
            if (optionCfgs[i].parent == parent) {//找到对应的配置
                currentCfg = optionCfgs[i];
                result.parentAttrValue = parent;
                break;
            } else if (optionCfgs[i].parent == null || optionCfgs[i].parent.trim().length == 0) {//默认配置
                defaultCfg = optionCfgs[i];
            }
        }
        if (currentCfg == null) {
            currentCfg = defaultCfg;//未找到对应配置时，使用默认配置
        }
        if (currentCfg == null) {
            ElMessage.error(`未找到对应的配置optionCfg`);
            return;
        }
        //拼装额外的参数
        if (currentCfg.params != null && currentCfg.params.length > 0) {
            for (var i in currentCfg.params) {
                if (currentCfg.params[i].valueType == "F") {//固定值
                    params.push(currentCfg.params[i].value);
                } else if (currentCfg.params[i].valueType == "A") {//属性值
                    var attrValue = null;
                    if(mainRelAttribute != null && mainRelAttribute.length > 0) {//当mainRelAttribute不为空时，优先从子对象中获取值
                        if(targetRegionData[mainRelAttribute] != null) {
                            attrValue = targetRegionData[mainRelAttribute][currentCfg.params[i].value];
                        }
                    }
                    if(attrValue == null) {//子对象取不到时，尝试从主对象获取
                        attrValue = targetRegionData[currentCfg.params[i].value];
                    }
                    params.push(attrValue);
                } else if (currentCfg.params[i].valueType == "P") {//上级属性
                    params.push(parentAttrValue);
                } else {
                    ElMessage.error(`非法的valueType: ` + currentCfg.params[i].valueType);
                    return;
                }
            }
        }
    }
    result.params = params;
    return result;
}

/**
 * 处理value到label的翻译
 * targetRegion 进行翻译处理的目标区域配置信息
 * targetRegionData 进行翻译处理的目标区域的数据信息
 * subObjectAssign 从绑定对象，可为空，用于判断是否当前对象的属性
 * mainPage 是否在主页面，用于判断是否是主页面中的从对象
 * funcDefine 功能定义信息，全量配置信息
 */
export function processLabelValue(targetRegion, targetRegionData, subObjectAssign, mainPage, funcDefine) {
    //处理value到label的翻译
    var processed = {};//保存已经处理过的属性key（一对多类型的从对象）
    for (var index in targetRegion.regionAttrAssigns) {
        const currentAttrAssign = targetRegion.regionAttrAssigns[index];
        if (subObjectAssign != null && currentAttrAssign.objectAssignId != subObjectAssign.id) {//非当前绑定对象的属性
            continue;
        }
        const objectAssign = funcDefine.objectMap[currentAttrAssign.objectId];//获取对应绑定对象
        var attrValue = null;//获取当前属性值
        if(Array.isArray(targetRegionData)) {//查询结果区的情况
            for(var j in targetRegionData) {//循环处理每一条记录
                //递归调用
                processLabelValue(targetRegion, targetRegionData[j], objectAssign, false, funcDefine);
            }
            break;
        }else if (mainPage && objectAssign.assignType == "S") {//从对象
            if(targetRegionData[objectAssign.mainRelAttribute.code] != null) {//存在从对象属性时
                if(Array.isArray(targetRegionData[objectAssign.mainRelAttribute.code])) {//从对象属性是列表的情况，一对多从对象
                    if(processed[objectAssign.mainRelAttribute.code] == null) {//判断该对象是否已经处理过
                        processed[objectAssign.mainRelAttribute.code] = objectAssign.mainRelAttribute.code;//设置已处理标志
                        for(var j in targetRegionData[objectAssign.mainRelAttribute.code]) {//循环处理每一条记录
                            //递归调用
                            processLabelValue(targetRegion, targetRegionData[objectAssign.mainRelAttribute.code][j], objectAssign, false, funcDefine);
                        }
                    }
                    continue;//一对多的从对象，跳过处理
                }else {
                    attrValue = targetRegionData[objectAssign.mainRelAttribute.code][currentAttrAssign.attribute.code];//获取当前属性值
                }
            }else {//存在从对象属性时,尝试从非从对象属性值中获取（如编辑从对象信息页面），自定义功能无法区分是否是从对象编辑页面
                attrValue = targetRegionData[currentAttrAssign.attribute.code];//获取当前属性值
            }            
        } else {//主对象
            attrValue = targetRegionData[currentAttrAssign.attribute.code];//获取当前属性值
        }
        if ((attrValue == null || attrValue == "") && (currentAttrAssign.displayType == "P" ||  currentAttrAssign.displayType == "V")) {
            continue;//当前值为空且需要翻译（P/V）时不需要处理
        }
        var attrOptions = objectAssign.attrOptionMap[currentAttrAssign.attributeId];//获取属性选项信息
        if (attrOptions != null) {//如果存在属性选项信息
            //弹出选择框或者存在属性联动
            if (currentAttrAssign.displayType == "P" ||  currentAttrAssign.displayType == "V" ||
                    (attrOptions.interactionType != null && attrOptions.interactionType.trim().length != 0 && attrOptions.interactionType != 'N')) {
                //尝试获取上级属性值
                var parentAttrValue = null;
                if (attrOptions.parentAttributeId != null && attrOptions.parentAttributeId != "") {
                    for (var i in targetRegion.regionAttrAssigns) {//循环绑定属性列表，找到上级属性对应的绑定属性信息
                        var rowAttrAssign = targetRegion.regionAttrAssigns[i];
                        if (rowAttrAssign.objectAssignId != objectAssign.id) {//非当前绑定对象的属性
                            continue;
                        }
                        if (attrOptions.parentAttributeId == rowAttrAssign.attributeId
                            && attrOptions.parentObjectAssignId == rowAttrAssign.objectAssignId) {//判断是否是当前属性的上级属性
                            if (mainPage && objectAssign.assignType == "S") {//从对象
                                if(targetRegionData[objectAssign.mainRelAttribute.code] != null) {
                                    if(Array.isArray(targetRegionData[objectAssign.mainRelAttribute.code])) {
                
                                    }else {
                                        parentAttrValue = targetRegionData[objectAssign.mainRelAttribute.code][rowAttrAssign.attribute.code];//获取上级属性的值
                                    }
                                }else {
                                    parentAttrValue = targetRegionData[rowAttrAssign.attribute.code];//获取上级属性的值
                                }
                                
                            } else {//主对象
                                parentAttrValue = targetRegionData[rowAttrAssign.attribute.code];//获取上级属性的值
                            }
                        }
                    }
                }
                
                //尝试获取其他参数
                var params = null;
                if (objectAssign.assignType == 'M' || subObjectAssign != null) {
                    params = processLabelValueParams(attrOptions, parentAttrValue, targetRegionData, null);
                }else {
                    params = processLabelValueParams(attrOptions, parentAttrValue, targetRegionData, objectAssign.mainRelAttribute.code);
                }
                if(params == null) {
                    params = {};
                }else {
                    if(params.parentAttrValue == null || params.parentAttrValue.length == 0) {
                        params.parentAttrValue = parentAttrValue;
                    } 
                }
                if (attrOptions.parentAttributeId != null && attrOptions.parentAttributeId != ""  && (attrOptions.interactionType == "V" || attrOptions.interactionType == "A")) {//存在上级属性值
                    if (currentAttrAssign.displayType == "P" || currentAttrAssign.displayType == "V") {//弹出选择框时的处理
                        axiosClient.post("/lcdp-proxy/lowcode/fe/api/funcObjectAttrOptions/getLabelWithParent/" + attrOptions.id + "/" + attrValue + "/" + params.parentAttrValue, params.params).then((response) => {
                            var data1 = response.data;
                            if (data1 != null && data1.status == "200" && data1.data != null) {
                                if (mainPage && objectAssign.assignType == "S") {//从对象
                                    if(targetRegionData[objectAssign.mainRelAttribute.code] != null) {
                                        if(Array.isArray(targetRegionData[objectAssign.mainRelAttribute.code])) {
                    
                                        }else {
                                            targetRegionData[objectAssign.mainRelAttribute.code][currentAttrAssign.attribute.code + "_label"] = data1.data;//获取当前属性值
                                        }
                                    }else {
                                        targetRegionData[currentAttrAssign.attribute.code + "_label"] = data1.data;//获取当前属性值
                                    }
                                    
                                } else {//主对象
                                    targetRegionData[currentAttrAssign.attribute.code + "_label"] = data1.data;//获取当前属性值
                                }
                            }
                        });
                    }else {
                        
                        if(attrOptions.optionCfgType == 'S') {//SQL方式
                            const attrValue_T = attrValue;
                            axiosClient.post("/lcdp-proxy/lowcode/fe/api/funcObjectAttrOptions/getOptionsWithParent/" + attrOptions.id + "/" + params.parentAttrValue, params.params).then((response) => {
                                var data1 = response.data;
                                if (data1 != null && data1.status == "200" && data1.data != null) {
                                    if (mainPage && objectAssign.assignType == "S") {//从对象
                                        if(targetRegionData[objectAssign.mainRelAttribute.code] != null) {
                                            if(Array.isArray(targetRegionData[objectAssign.mainRelAttribute.code])) {
                        
                                            }else {
                                                targetRegionData[objectAssign.mainRelAttribute.code][currentAttrAssign.attribute.code + "_options"] = data1.data;//获取当前属性值
                                            }
                                        }else {
                                            targetRegionData[currentAttrAssign.attribute.code + "_options"] = data1.data;//获取当前属性值
                                        }
                                        
                                    } else {//主对象
                                        targetRegionData[currentAttrAssign.attribute.code + "_options"] = data1.data;//获取当前属性值
                                    }
                                    for(var j in data1.data) {
                                        if(attrValue_T == data1.data[j].value) {
                                            if (mainPage && objectAssign.assignType == "S") {//从对象
                                                if(targetRegionData[objectAssign.mainRelAttribute.code] != null) {
                                                    if(Array.isArray(targetRegionData[objectAssign.mainRelAttribute.code])) {
                                
                                                    }else {
                                                        targetRegionData[objectAssign.mainRelAttribute.code][currentAttrAssign.attribute.code + "_label"] = data1.data[j].label;//获取当前属性值
                                                    }
                                                }else {
                                                    targetRegionData[currentAttrAssign.attribute.code + "_label"] = data1.data[j].label;//获取当前属性值
                                                }
                                                
                                            } else {//主对象
                                                targetRegionData[currentAttrAssign.attribute.code + "_label"] = data1.data[j].label;//获取当前属性值
                                            }
                                            break;
                                        }
                                    }
                                }
                            });
                        }
                    }
                    
                } else {//目前非弹出选择框且无上级属性值时，不需要处理，只需要处理弹出选择框的情况
                    if (currentAttrAssign.displayType == "P" || currentAttrAssign.displayType == "V") {//弹出选择框时的处理
                        axiosClient.post("/lcdp-proxy/lowcode/fe/api/funcObjectAttrOptions/getLabel/" + attrOptions.id + "/" + attrValue, params.params).then((response) => {
                            var data1 = response.data;
                            if (data1 != null && data1.status == "200" && data1.data != null) {
                                targetRegionData[currentAttrAssign.attribute.code + "_label"] = data1.data;
                                if (data1 != null) {
                                    if (mainPage && objectAssign.assignType == "S" && subObjectAssign != null) {//从对象
                                        if(targetRegionData[objectAssign.mainRelAttribute.code] != null) {
                                            if(Array.isArray(targetRegionData[objectAssign.mainRelAttribute.code])) {
                    
                                            }else {
                                                targetRegionData[objectAssign.mainRelAttribute.code][currentAttrAssign.attribute.code + "_label"] = data1.data;//获取当前属性值
                                            }
                                        }else {
                                            targetRegionData[currentAttrAssign.attribute.code + "_label"] = data1.data;//获取当前属性值
                                        }
                                        
                                    } else {//主对象
                                        targetRegionData[currentAttrAssign.attribute.code + "_label"] = data1.data;//获取当前属性值
                                    }
                                }
                            }
                        });
                    }
                }

                //显示隐藏控制
                if(parentAttrValue != null && (attrOptions.interactionType == "D" || attrOptions.interactionType == "A")) {
                    if(attrOptions.displayHideCfg == null || attrOptions.displayHideCfg.trim().length == 0) {//未配置显示隐藏信息
                        continue;
                    }
                    attrOptions.displayHideCfgJSON = JSON.parse(attrOptions.displayHideCfg);
                    if(attrOptions.displayHideCfgJSON == null) {
                        continue;
                    }

                    var mode = attrOptions.displayHideCfgJSON[parentAttrValue];//获取显示隐藏模式
                    if(mode == null) {
                        mode = '1';//未配置，则默认为显示
                    }

                    // if(currentAttrAssign.displayHiddenMode  == '0' && mode != '0') {//从隐藏到显示
                    //     needReformatDisplay = true;
                    // }else if(currentAttrAssign.displayHiddenMode != '0' && mode == '0') {//从显示到隐藏
                    //     needReformatDisplay = true;
                    // }

                    currentAttrAssign.displayHiddenMode =  mode;
                }
            }
        }
    }
}

/**
 * 自定义功能执行操作功能封装
 * @param {*} operation 当前操作配置便利店
 * @param {*} funcDefine  
 * @param {*} customizedPage 
 * @param {*} regionData 当前数据，如果表格行操作的话，是当前行的数据
 * @param {*} hideDialogPage 用于关闭弹窗的方法
 * @param {*} showDialogPage 用于打开弹窗的方法
 * @param {*} refreshTableData 用于刷新表格数据的方法
 * @param {*} objectUpdated 用于更新父页面数据的方法
 * @param {*} parentPage 父页面
 * @param {*} srcOperation 父页面打开该弹窗的操作
 * @param {*} updateTabelRow 用于父页面表格行数据的方法
 * @param {*} regionDataAll 表格行操作时，使用传递完全数据
 */
export function executeOperationForCustomized(operation, funcDefine, customizedPage, regionData, hideDialogPage, showDialogPage, refreshTableData, objectUpdated, parentPage, srcOperation, updateTabelRow, regionDataAll) {
    console.log(operation, regionData);
    showTipInfoForCustomized(operation, 'PRE');//显示事前提示信息
    if(operation.operationCfgJSON != null && operation.operationCfgJSON.alertMethodPre == 'C') {//事前确认
        var msg = (operation.operationCfgJSON.alertMsgPre == null || operation.operationCfgJSON.alertMsgPre.trim().length == 0) ? '是否继续?':operation.operationCfgJSON.alertMsgPre;
        msg = processAlertMsg(msg, regionData);
        ElMessageBox.confirm(msg,'警告',
                {
                  confirmButtonText: '确定',
                  cancelButtonText: '取消',
                  type: (operation.operationCfgJSON.alertTypePre == null || operation.operationCfgJSON.alertTypePre.trim().length == 0) ? 'info' : operation.operationCfgJSON.alertTypePre,
                }
        ).then(() => {
            onExecuteOperationForCustomized(operation, funcDefine, customizedPage, regionData, hideDialogPage, showDialogPage, refreshTableData, objectUpdated, parentPage, srcOperation, updateTabelRow, regionDataAll);
            showTipInfoForCustomized(operation, 'POST');//显示事后提示信息
        });
    }else {
        onExecuteOperationForCustomized(operation, funcDefine, customizedPage, regionData, hideDialogPage, showDialogPage, refreshTableData, objectUpdated, parentPage, srcOperation, updateTabelRow, regionDataAll);
        showTipInfoForCustomized(operation, 'POST');//显示事后提示信息
    }
}

/**
 * 处理提示信息中，占位信息的替换
 * @param {*} msg 
 * @param {*} regionData 
 * @returns 
 */
function processAlertMsg(msg, regionData) {
    if(msg == null || msg.length == 0) {
        return msg;
    }
    var reg = /\$\{(.+?)\}/g;
    var regResult = msg.match(reg);
    for(var i in regResult) {
        var attrCodes = regResult[i].substring(2,regResult[i].length-1).split('.');
        var attrValue = null;
        for(var j in attrCodes) {
            if(attrValue == null) {
                attrValue = regionData[attrCodes[j]];
                if(attrValue == null) {
                    break;
                }
            }else {
                if(isJsonObject(attrValue)) {
                    attrValue = attrValue[attrCodes[j]];
                    if(attrValue == null) {
                        break;
                    }
                }else {
                    attrValue = null;
                    break;
                }
            }
        }
        msg = msg.replace(regResult[i], attrValue);
    }
    return msg;
}

/**
 * 判断是否是JSON对象
 * @param {*} obj 
 * @returns 
 */
export function isJsonObject(obj) {
    if(typeof(obj) == 'object' && Object.prototype.toString.call(obj).toLowerCase() == '[object object]' && !obj.length) {
        return true;
    }else {
        return false;
    }
}

/**
 * 判断是否是JSONArray对象
 * @param {*} obj 
 * @returns 
 */
export function isJsonArray(obj) {
    if(typeof(obj) == 'object' && Object.prototype.toString.call(obj).toLowerCase() == '[object array]' && obj.length > 0) {
        return true;
    }else {
        return false;
    }
}



/**
 * 显示提醒信息
 * @param {*} operation 
 * @param {*} phase 
 */
function showTipInfoForCustomized(operation, phase) {
    if(operation.operationCfgJSON != null && operation.operationCfgJSON.alertMethodPre == 'T' && phase == 'PRE') {//事前提示
        if(operation.operationCfgJSON.alertTypePre == 'error') {
            ElMessage.error(operation.operationCfgJSON.alertMsgPre);
        }else if(operation.operationCfgJSON.alertTypePre == 'warning') {
            ElMessage.warning(operation.operationCfgJSON.alertMsgPre);
        }else if(operation.operationCfgJSON.alertTypePre == 'success') {
            ElMessage.success(operation.operationCfgJSON.alertMsgPre);
        }else {
            ElMessage.info(operation.operationCfgJSON.alertMsgPre);
        }
    }else if(operation.operationCfgJSON != null && operation.operationCfgJSON.alertMethodPost == 'T' && phase == 'POST') {//事后提示
        if(operation.operationCfgJSON.alertTypePost == 'error') {
            ElMessage.error(operation.operationCfgJSON.alertMsgPost);
        }else if(operation.operationCfgJSON.alertTypePost == 'warning') {
            ElMessage.warning(operation.operationCfgJSON.alertMsgPost);
        }else if(operation.operationCfgJSON.alertTypePost == 'success') {
            ElMessage.success(operation.operationCfgJSON.alertMsgPost);
        }else {
            ElMessage.info(operation.operationCfgJSON.alertMsgPost);
        }
    }
}

function onExecuteOperationPostAction(customizedPage, operation, hideDialogPage, refreshTableData, objectUpdated, selectedData, parentPage, srcOperation, regionData, result, funcDefine, req) {
    if(operation.displayType == 'P' && operation.targetRegionId != null && operation.targetRegionId != customizedPage.id && parentPage != null && srcOperation != null){//有打开新的页面的情况下，暂不执行事后动作
        return;
    }
    //刷新表格数据
    if(operation.operationCfgJSON.postAction == 'R' && operation.operationCfgJSON.refreshTarget != null && operation.operationCfgJSON.refreshTarget.length > 0) {
        if(operation.operationType == 'Q') {
            var targetLayouts = findCustomizedLayouts(customizedPage.customizedLayouts, operation.operationCfgJSON.refreshTarget);
            for(var i in targetLayouts) {
                if(targetLayouts[i].componentCode == 'NCL') {//表格容器
                    regionData[targetLayouts[i].id].data = selectedData;
                    if(regionData[targetLayouts[i].id].data != null) {
                        processLabelValue(targetLayouts[i].layoutRegions[0].funcRegion, regionData[targetLayouts[i].id].data, null, true, funcDefine);
                    }
                    if(result.pageParam != null) {
                        regionData[targetLayouts[i].id].pageParam = result.pageParam;
                        regionData[targetLayouts[i].id].originQueryInfo = {
                            operation: operation,
                            req: req
                        };
                    }
                }
            }
        }else {
            if(refreshTableData != null && operation.operationType != 'S') {//选择类型时，不触发刷新操作
                for(var i in operation.operationCfgJSON.refreshTarget) {
                    refreshTableData(null, operation.operationCfgJSON.refreshTarget[i], operation.operationType=='R');
                }
            }
        }
    }else if(operation.operationCfgJSON.postAction == 'U' && operation.operationCfgJSON.refreshTarget != null && operation.operationCfgJSON.refreshTarget.length > 0) {
        if(objectUpdated != null && operation.operationType != 'S') {//选择类型时，不触发刷新操作
            objectUpdated(selectedData, operation);
        }
    }

    //不是主页面且未配置不关闭弹窗
    if(customizedPage.isMainPage != "Y" && operation.operationCfgJSON.closePopWindow == 'Y' && hideDialogPage != null) {
        hideDialogPage();
    }

    //执行上级页面打开弹窗按钮的事后操作
    if(parentPage != null && srcOperation != null && operation.operationCfgJSON.closePopWindow == 'Y' && hideDialogPage != null) {
        onExecuteOperationPostAction(parentPage, srcOperation, hideDialogPage, refreshTableData, objectUpdated, selectedData, null, null, regionData, result, funcDefine);
    }
}

/**
 * 判断json对象是否为空
 * @param {*} json 
 * @returns 
 */
export function isJsonEmpty(json) {
    if(json == null ) {
        return true;
    }
    var isEmpty = true;
    for(var key in json) {
        isEmpty = false;
        break;
    }
    return isEmpty;
}


function onExecuteOperationForCustomized(operation, funcDefine, customizedPage, regionData, hideDialogPage, showDialogPage, refreshTableData, objectUpdated, parentPage, srcOperation, updateTabelRow, regionDataAll) {
    if(operation.operationType == 'N') {//N: 取消
        hideDialogPage();
    }else if(operation.operationType == 'R') {//R: 重置
        //过滤需要重置的区域列表
        var layouts = [];
        if(operation.operationCfgJSON != null && operation.operationCfgJSON.srcData != null && operation.operationCfgJSON.srcData.length > 0) {
            filterCustomizedLayouts(customizedPage.customizedLayouts, layouts, operation.operationCfgJSON.srcData);
        } 
        for(var i in layouts) {
            if(layouts[i].componentCode == 'TCL') {
                var selectedValueKey = 'selectedId_tree';
                if(layouts[i].layoutCfgJSON != null && layouts[i].layoutCfgJSON.selectedValueKey != null && layouts[i].layoutCfgJSON.selectedValueKey.length > 0) {
                    selectedValueKey = layouts[i].layoutCfgJSON.selectedValueKey;
                }
                regionData[selectedValueKey] = null;//清空表单属性值
            }else {
                var funcRegion = layouts[i].layoutRegions[0].funcRegion;
                if(funcRegion != null && funcRegion.regionAttrAssigns != null && funcRegion.regionAttrAssigns.length > 0) {
                    for (var index = 0; index < funcRegion.regionAttrAssigns.length; index++) {
                        var attrAssign = funcRegion.regionAttrAssigns[index];
                        regionData[attrAssign.attribute.code] = null;//清空表单属性值
                        if(regionData[attrAssign.attribute.code+"_label"] != undefined) {
                            regionData[attrAssign.attribute.code+"_label"] = null;//清空表单属性翻译属性
                        }
                    }
                }
            }
        }
        
        onExecuteOperationPostAction(customizedPage, operation, hideDialogPage, refreshTableData, null, null, parentPage, srcOperation, regionData, null, funcDefine,null);
    }else if(operation.operationType == 'P' || operation.operationType == 'F') {//P：预新增；F:前端编辑
        
        //有配置弹出页面的情况
        if(operation.displayType == 'P' && operation.targetRegionId != null && operation.targetRegionId != customizedPage.id){//打开新的页面
            var paramsJSON = {};
            if(operation.operationCfgJSON.params != null && operation.operationCfgJSON.params.length > 0) {
                paramsJSON = processParamCfg(operation.operationCfgJSON.params, regionData);
            }else if(operation.operationType == 'F') {
                for(var key in regionData) {
                    paramsJSON[key] = regionData[key];
                }
            }
            showDialogPage(operation.targetRegionId, paramsJSON, operation);
        }
        onExecuteOperationPostAction(customizedPage, operation, hideDialogPage, refreshTableData, objectUpdated, regionData, parentPage, srcOperation, null, null, funcDefine, null);
    }else {
        var selectedData = null;
        var originReqData = null;
        if(operation.operationType == 'S' || operation.operationType == 'E') {//S：选择（选中一或多条数据并返回）；E：导出
            //获取对应列表的选中数据
            if(operation.operationCfgJSON != null && operation.operationCfgJSON.srcData != null && operation.operationCfgJSON.srcData.length > 0) {
                var srcLayouts = findCustomizedLayouts(customizedPage.customizedLayouts, operation.operationCfgJSON.srcData);
                if(srcLayouts != null && srcLayouts.length > 0) {
                    var formData = {};
                    for(var i in srcLayouts) {
                        if(srcLayouts[i].componentCode == 'MCL') {//表单
                            if(srcLayouts[i].layoutRegions[0].funcRegion.regionAttrAssigns != null) {
                                for(var j in srcLayouts[i].layoutRegions[0].funcRegion.regionAttrAssigns) {
                                    var attrAssign = srcLayouts[i].layoutRegions[0].funcRegion.regionAttrAssigns[j];
                                    formData[attrAssign.attribute.code] = regionData[attrAssign.attribute.code];
                                }
                            }
                        }else if(srcLayouts[i].componentCode == 'NCL') {//表格
                            //非选择类表格的情况
                            if(srcLayouts[i].layoutCfgJSON != null && srcLayouts[i].layoutCfgJSON.tableDataSourceAttr != null && srcLayouts[i].layoutCfgJSON.tableDataSourceAttr.length > 0) {
                                formData[srcLayouts[i].layoutCfgJSON.tableDataSourceAttr] = regionData[srcLayouts[i].layoutCfgJSON.tableDataSourceAttr];
                            }else {
                                if(regionData[operation.operationCfgJSON.srcData[0]] != null) {//表格选中的数据
                                    selectedData = regionData[operation.operationCfgJSON.srcData[0]].selected;
                                }
                            }
                        }
                    }
                    if(selectedData == null && !isJsonEmpty(formData)) {
                        selectedData = formData;
                    }
                }
                
                //导出且未选择数据，使用表格的查询条件
                if(operation.operationType == 'E' && selectedData == null && regionData[operation.operationCfgJSON.srcData[0]].originQueryInfo != null) {
                    originReqData = regionData[operation.operationCfgJSON.srcData[0]].originQueryInfo.req.reqData;
                }
            }else{//没有选择源对象时，默认使用整个regionData
                if(operation.operationType == 'S') {
                    selectedData = regionData;
                }
            } 
            if(selectedData == null && operation.operationType == 'S') {
                ElMessage.error(`请先选择数据！`);
                return;
            }
            if(selectedData == null && operation.operationType == 'E') {
                if (operation.operationCfgJSON.params != null && operation.operationCfgJSON.params.length > 0) {
                    var paramsJSON = null;
                    if(typeof(operation.operationCfgJSON.params) == 'string') {
                        paramsJSON = JSON.parse(operation.operationCfgJSON.params);
                    }else {
                        paramsJSON = operation.operationCfgJSON.params;
                    }
                    for(var k in paramsJSON) {
                        if(paramsJSON[k].valueType == 'S') {
                            ElMessage.error(`请先选择数据！`);
                            return;
                        }
                    }
                }
            }
        }
        //C：新增；D：删除；U：修改；Q：查询列表; B：主键查询；S：选择（选中一或多条数据并返回）；E：导出；V：查看（弹出，对应子功能）；M：流式调用 需要调用后端服务且配置了后端服务
        if(operation.exposedServiceMapping != null && operation.exposedServiceMapping.trim().length > 0){
            var url = "/lcdp-proxy/lowcode/" + (operation.operationType == 'E' ? funcDefine.platformFileDownloadUrl : (operation.operationType == 'M' ?funcDefine.platformStreamUrl : funcDefine.platformUrl)) + "/" + funcDefine.systemUrl + "/" + operation.exposedServiceMapping;
            var req = {};
            if(operation.operationType == 'E' && selectedData == null && originReqData != null) {//导出且未选中数据时，使用表格原来的查询条件
                var reqData = {};
                for(var key in originReqData) {
                    if(key != 'pageParam') {
                        reqData[key] = originReqData[key];
                    }
                }
                req['reqData'] = reqData;
            }else {
                if(operation.operation.inParams != null && operation.operation.inParams.length > 0) {
                    req = generateReqObjectCustomizedPage(operation.operation.inParams, regionData, operation, customizedPage, funcDefine, operation.operationCfgJSON.supportPages!='N', selectedData);
                }
            }
            
            //参数校验
            if(operation.operation.validateRuleGroup != null && operation.operation.validateRuleGroup.validateRules != null) {
                if(!validateReqData(operation.operation.validateRuleGroup, req.reqData)) {
                    return;
                }
            }
            if(operation.operationType == 'E') {
                axiosClient.post(url, req, {"responseType":"blob"}).then((response) => {
                    if(response != null && response.status == "200") {
                        const blob = new Blob([response.data], { "type": response.headers["content-type"]});
                        const disposition = decodeURI(response.headers['content-disposition'])
                        // 从响应头中获取文件名称
                        const fileName = disposition.substring(disposition.indexOf('filename=') + 10, disposition.length-1)
                        const a = document.createElement("a"); 
                        const url = window.URL.createObjectURL(blob); // 创建媒体流 url
                        a.download = fileName;
                        a.href = url;
                        a.style.display = "none";
                        document.body.appendChild(a);
                        a.click();
                        a.parentNode.removeChild(a);
                        window.URL.revokeObjectURL(url); // 删除创建的媒体流 url 对象
                    }else {
                        ElMessage.error(`请求后端服务失败！status:` + response.status);
                    }
                    
                }).catch((err)=>{
                    ElMessage.error(`请求后端服务失败！` + err);
                });
            }else if(operation.operationType == 'M') {//流式调用
                url = url + '?' + encodeURI('params=' + JSON.stringify(req));
                const eventSource = new EventSource(url);
                // var returnMessage = "";
                // if(operation.operationCfgJSON != null && operation.operationCfgJSON.addToTargetAttr != null && operation.operationCfgJSON.addToTargetAttr.trim().length > 0) {
                //     returnMessage = "\nUser: " + req.reqData[operation.operationCfgJSON.addToTargetAttr] + "\n";
                // } 
                // returnMessage += "Assistant: ";
                // objectUpdated({"streamResponse": returnMessage}, operation);
                eventSource.onmessage = (event) => {
                    objectUpdated({"streamResponse": event.data}, operation);
                };
                eventSource.onerror = (event) => {
                    eventSource.close();
                };
                eventSource.addEventListener("close", function (e) {
                    eventSource.close();
                });
            }else {
                axiosClient.post(url, req).then((response) => {
                    var result = response.data;
                    if(result != null && result.status == '200') {
                        var resultData = result.data;
                        
                        if(operation.operationType == 'D' && updateTabelRow != null) {//D: 删除, 前端删除
                            if(!isNaN(parseInt(resultData)) && isFinite(resultData))  {
                                if(result.data > 0) {
                                    updateTabelRow(true, null);
                                    ElMessage(`删除对象成功。`);
                                }
                            }else {
                                updateTabelRow(false, resultData);
                                ElMessage(`删除对象成功。`);
                            }
                            
                        }else if(operation.operationType == 'C' || operation.operationType == 'U') {//C：新增；U：修改；
                            if(!isNaN(parseInt(resultData)) && isFinite(resultData))  {
                                
                            }else {
                                selectedData = resultData;
                            }
                        }else if(operation.operationType == 'S') {//选择操作时，更新源数据
                            if(!isNaN(parseInt(resultData)) && isFinite(resultData))  {
                                
                            }else {
                                var updateMapping = operation.operationCfgJSON.updateMapping;
                                var updateMappingJSON = {};
                                if(updateMapping != null) {
                                    if(typeof(updateMapping) == 'string') {
                                        if(updateMapping.trim().length > 0) {
                                            updateMappingJSON = JSON.parse(updateMapping);
                                        }
                                    }else {
                                        updateMappingJSON = updateMapping;
                                    }
                                }
                                for(var i in updateMappingJSON) {
                                    var mapping = updateMappingJSON[i];
                                    if(mapping.valueType == "F") {//固定值
                                        selectedData[mapping.targetAttr] = mapping.value;
                                    }else if(mapping.valueType == "A") {//属性值
                                        if(mapping.value == "/") {
                                            selectedData[mapping.targetAttr] = resultData;
                                        }else {
                                            selectedData[mapping.targetAttr] = resultData[mapping.value];
                                        }
                                    }
                                }
                            }
                        }else {
                            selectedData = resultData;
                        }
    
                        //有配置弹出页面的情况
                        if(operation.displayType == 'P' && operation.targetRegionId != null && operation.targetRegionId != customizedPage.id){//打开新的页面
                            var paramsJSON = {};
                            if(operation.operationCfgJSON.params != null && operation.operationCfgJSON.params.length > 0) {
                                if(regionDataAll != null) {
                                    paramsJSON = processParamCfg(operation.operationCfgJSON.params, regionDataAll);
                                }
                                var paramsJSON1 = processParamCfg(operation.operationCfgJSON.params, regionData);
                                for(key in paramsJSON1) {
                                    if(paramsJSON[key] == null || paramsJSON1[key] != null) {
                                        paramsJSON[key] = paramsJSON1[key];
                                    }
                                }
                            }
                            if(resultData == null) {
                                resultData = {};
                            }
                            for(var key in paramsJSON) {
                                if(resultData[key] == undefined) {
                                    resultData[key] = paramsJSON[key];
                                }
                            }
                            showDialogPage(operation.targetRegionId, resultData, operation);
                        }

                        
    
                        onExecuteOperationPostAction(customizedPage, operation, hideDialogPage, refreshTableData, objectUpdated, selectedData, parentPage, srcOperation, regionData, result, funcDefine, req);
                    }else {
                        ElMessage.error(`请求后端服务失败！状态码：` + result.status + `，错误信息：` + result.message );
                    }
                }).catch((error)=>{
                    ElMessage.error(`请求后端服务失败！` + error);
                });
            }
            
        }else {//不需要调用后端服务的情况
            if(operation.operationType == 'D' && updateTabelRow != null) {//D: 删除, 前端删除
                updateTabelRow(true, null);
            }

            //有配置弹出页面的情况
            if(operation.displayType == 'P' && operation.targetRegionId != null && operation.targetRegionId != customizedPage.id){//打开新的页面
                var paramsJSON = {};
                if(operation.operationCfgJSON.params != null && operation.operationCfgJSON.params.length > 0) {
                    if(regionDataAll != null) {
                        paramsJSON = processParamCfg(operation.operationCfgJSON.params, regionDataAll);
                    }
                    var paramsJSON1 = processParamCfg(operation.operationCfgJSON.params, regionData);
                    for(key in paramsJSON1) {
                        if(paramsJSON[key] == null || paramsJSON1[key] != null) {
                            paramsJSON[key] = paramsJSON1[key];
                        }
                    }
                }
                showDialogPage(operation.targetRegionId, paramsJSON, operation);
            }

            onExecuteOperationPostAction(customizedPage, operation, hideDialogPage, refreshTableData, objectUpdated, selectedData, parentPage, srcOperation, regionData, null, funcDefine, null);
        }
    }
}

/**
 * 处理自定义的传入参数
 * @param {*} params 
 * @param {*} regionData 
 * @returns 
 */
export function processParamCfg(params, regionData) {//根据参数配置解析参数值
    var paramsJSON = {};
    if(params != null && params.length > 0) {
        var paramsCfgJson = null;
        if(typeof(params) == 'string') {
            paramsCfgJson = JSON.parse(params);
        }else {
            paramsCfgJson = params;
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

export function onExecuteOperation(regionOperation, regionData, funcRegion, funcDefine, supportPages, operationExecuted, subObjectAssign, funcRegionForm) {
    var isMainCheckRule = false;
    if(regionOperation.operationCfg != null && regionOperation.operationCfg.trim().length > 0) {
        var operationCfgJson = JSON.parse(regionOperation.operationCfg);
        if(operationCfgJson.mainCheckRule != null && (operationCfgJson.mainCheckRule == "true" || operationCfgJson.mainCheckRule)) {
            isMainCheckRule = true;
        }
    }
    if(isMainCheckRule) {
        funcRegionForm.validate((valid, fields)=> {
            if(valid) {
                invokeService(regionOperation, regionData, funcRegion, funcDefine, supportPages, operationExecuted, subObjectAssign);
            }else {
                console.log("参数校验失败！", fields);
            }
        });
    }else {
        invokeService(regionOperation, regionData, funcRegion, funcDefine, supportPages, operationExecuted, subObjectAssign);
    }


}

function invokeService(regionOperation, regionData, funcRegion, funcDefine, supportPages, operationExecuted, subObjectAssign) {
    if(regionOperation.exposedServiceMapping != null && regionOperation.exposedServiceMapping.trim().length != 0) {
        var req = {};
        var url = "/lcdp-proxy/lowcode/" + funcDefine.platformUrl + "/" + funcDefine.systemUrl + "/" + regionOperation.exposedServiceMapping;
        if(regionOperation.operation.inParams != null && regionOperation.operation.inParams.length > 0) {
            req = generateReqObject(regionOperation.operation.inParams, regionData, regionOperation, funcRegion, funcDefine, supportPages);
        }
        //参数校验
        if(regionOperation.operation.validateRuleGroup != null && regionOperation.operation.validateRuleGroup.validateRules != null) {
            if(!validateReqData(regionOperation.operation.validateRuleGroup, req.reqData)) {
                return;
            }
        }
        axiosClient.post(url, req).then((response) => {
            var data = response.data;
            if(data != null && data.status == '200') {
                if(funcRegion != null && funcRegion.tplRegionId == '9') {//主编辑-从新增
                    if(subObjectAssign != null && subObjectAssign.keyAttribute != null && subObjectAssign.keyAttribute.code != null) {
                        regionData[subObjectAssign.keyAttribute.code] = data.data;
                    }
                    operationExecuted(regionOperation, regionData, subObjectAssign);
                }else {
                    ElMessage(`更新成功！`);
                    operationExecuted(regionOperation, data.data, subObjectAssign);
                }
            }else {
                ElMessage.error(`请求后端服务失败！状态码：` + data.status + `，错误信息：` + data.message );
            }
        }).catch(()=>{
            ElMessage.error(`请求后端服务失败！`);
        });
    }else {
        operationExecuted(regionOperation, regionData, subObjectAssign);
    }  
}

/**
 * 处理预校验规则的生成
 * @param regionOperations 区域操作信息
 * @param objectAssign 绑定对象信息，不为空时表示处理的是该绑定对象关联的操作
 * @returns 预校验规则
 */
export function processPreValidateRules(regionOperations, objectAssign) {
    if(regionOperations == null || regionOperations.length == 0) {
        return null;
    }
    for(var i in regionOperations) {
        if(regionOperations[i].operationCfg != null && regionOperations[i].operationCfg.trim().length > 0) {
            //需要过滤不属于当前从对象的操作
            if(objectAssign != null && regionOperations[i].objectAssignId != null && regionOperations[i].objectAssignId.trim().length > 0 && regionOperations[i].objectAssignId != objectAssign.id) {
                continue;
            }
            var operationCfgJson = JSON.parse(regionOperations[i].operationCfg);
            //只有当mainCheckRule有配置且为true的时候才处理预校验规则的生成
            if(operationCfgJson.mainCheckRule != null && (operationCfgJson.mainCheckRule == "true" || operationCfgJson.mainCheckRule)) {
                if(regionOperations[i].operation != null && regionOperations[i].operation.validateRuleGroup != null) {
                    return generatePreValidateRules(regionOperations[i].operation.validateRuleGroup, null, null, false);
                }
            }
        }
    }
    return null;
}

/**
 * 生成预校验规则组
 * validateRuleGroup: 校验规则组
 */
function generatePreValidateRules(validateRuleGroup, parentAttrCode, preValidateRules, isProcessingSub) {
    //当校验规则未配置时，默认返回为null
    if(validateRuleGroup == null || validateRuleGroup.validateRules == null || validateRuleGroup.validateRules.length == 0) {
        return null;
    }
    if(preValidateRules == null) {
        preValidateRules = {};
    }
    for(var i in validateRuleGroup.validateRules) {
        var validateRule = validateRuleGroup.validateRules[i];
        if(validateRule.attributeCode == null || validateRule.attributeCode.trim().length == 0) {
            ElMessage.error(`校验规则配置有误：未配置校验属性！`);
            return null;
        }

        var preValidateRuleArray = null;
        if(validateRule.subAttributeCode != null && validateRule.subAttributeCode.trim().length > 0) {//对象类型的参数，只有第一层会存在
            preValidateRuleArray = preValidateRules[validateRule.subAttributeCode];
            if(preValidateRuleArray == null) {
                preValidateRuleArray = [];
                preValidateRules[validateRule.subAttributeCode] = preValidateRuleArray;
            }
        }else {//非对象类型
            var preValidateRuleKey = validateRule.attributeCode;
            if(parentAttrCode != null && validateRule.attributeCode.trim().length > 0) {//处理预校验规则Key的问题
                preValidateRuleKey = parentAttrCode + "." + validateRule.attributeCode;
            }
            preValidateRuleArray = preValidateRules[preValidateRuleKey];
            if(preValidateRuleArray == null) {
                preValidateRuleArray = [];
                preValidateRules[preValidateRuleKey] = preValidateRuleArray;
            }
        }
        
        preValidateRuleArray.push(generatePreValidateRule(validateRule));
    }

    //递归处理子校验规则组
    if(validateRuleGroup.subRuleGroupMap != null) {
        for(var key in validateRuleGroup.subRuleGroupMap) {
            console.log(key, validateRuleGroup.subRuleGroupMap[key]);
            var dotIndex = key.indexOf(".");
            if(dotIndex < 0) {
                if(isProcessingSub) {
                    generatePreValidateRules(validateRuleGroup.subRuleGroupMap[key], key, preValidateRules, true);
                }else {
                    generatePreValidateRules(validateRuleGroup.subRuleGroupMap[key], parentAttrCode, preValidateRules, true);
                }
            }else {
                var subParentAttrCode = null;
                if(parentAttrCode != null && validateRule.attributeCode.trim().length > 0) {
                    subParentAttrCode = parentAttrCode + "." + key.substring(dotIndex+1);
                }else {
                    subParentAttrCode = key.substring(dotIndex+1);
                }
                generatePreValidateRules(validateRuleGroup.subRuleGroupMap[key], subParentAttrCode, preValidateRules, true);
            }
        }
    }
    return preValidateRules;
}

/**
 * 生成预校验规则
 */
function generatePreValidateRule(validateRule) {
    var errorCode = null;
    var message = null;
    var ruleValue1Json = null;
    if (validateRule.ruleValue1 != null) {
        ruleValue1Json = JSON.parse(validateRule.ruleValue1);
        errorCode = ruleValue1Json["errorCode"];
        message = ruleValue1Json["message"];
    }
    if (message == null || message.trim().length == 0) {
        message = "属性（" + validateRule.attributeCode +
            ((validateRule.subAttributeCode != null && validateRule.subAttributeCode.trim().length > 0) ? ("." + validateRule.subAttributeCode) : "") +
            "）未通过校验器（" + validateRule.ruleTypeName + "）的校验！"
    }
    var preValidateRule = {};
    preValidateRule.message = message;
    preValidateRule.trigger = "blur";
    if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.EmptyValidator") {//空值校验
        emptyValidatorGen(ruleValue1Json, preValidateRule);
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.OptionValidator") {//可选值校验
        optionValidatorGen(ruleValue1Json, preValidateRule);
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.RangeValidator") {//范围校验
        rangeValidatorGen(ruleValue1Json, preValidateRule);
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.LengthValidator") {//长度校验
        lengthValidatorGen(ruleValue1Json, preValidateRule);
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.PhoneValidator") {//手机号码校验
       phoneValidatorGen(ruleValue1Json, preValidateRule);
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.CertCodeValidator") {//身份证号码校验
        certCodeValidatorGen(ruleValue1Json, preValidateRule);
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.EmailValidator") {//邮件地址格式校验
        emailValidatorGen(ruleValue1Json, preValidateRule);
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.RegExprValidator") {//正则表达式校验
        regExprValidatorGen(ruleValue1Json, preValidateRule);
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.TypeValidator") {//类型校验
        typeValidatorGen(ruleValue1Json, preValidateRule);
    }
    return preValidateRule;
}

/**
 * 生成类型校验预校验规则
 */
function typeValidatorGen(ruleValue1Json, preValidateRule) {
    var attrType = null;
    if (ruleValue1Json != null) {
        attrType = ruleValue1Json["attrType"];
    }
    
    //当类型配置为空时，默认返回
    if (attrType == null || attrType.trim().length == 0) {
        return ;
    }
    if(attrType == "Date") {
        preValidateRule.type = "date";
    }else if(attrType == "Long" || attrType == "Integer" || attrType == "Byte"){
        preValidateRule.type = "integer";
    }else if(attrType == "Float" || attrType == "Double"){
        preValidateRule.type = "float";
    }else if(attrType == "Boolean") {
        preValidateRule.type = "boolean";
    }else if(attrType == "String") {
        preValidateRule.type = "string";
    }else if(attrType == "Character") {
        preValidateRule.type = "string";
    }
}

/**
 * 生成正则表达式校验预校验规则
 */
function regExprValidatorGen(ruleValue1Json, preValidateRule) {
    var regExpr = "";
    if (ruleValue1Json != null) {
        regExpr = ruleValue1Json["regExpr"];
    }
    
    if (regExpr != null && regExpr.trim().length > 0) {
        preValidateRule.pattern = regExpr;
    }
}

/**
 * 生成邮件地址格式校验预校验规则
 */
function emailValidatorGen(ruleValue1Json, preValidateRule) {
    preValidateRule.type = "email";
}

/**
 * 生成身份证号码校验预校验规则
 */
function certCodeValidatorGen(ruleValue1Json, preValidateRule) {
    preValidateRule.pattern = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
}

/**
 * 生成手机号码校验预校验南昌
 */
function phoneValidatorGen(ruleValue1Json, preValidateRule) {
    preValidateRule.pattern = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(16[5,6])|(17[0-8])|(18[0-9])|(19[1、5、8、9]))\\d{8}$";   
}

/**
 * 生成长度校验的预校验规则
 * { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' }
 */
function lengthValidatorGen(ruleValue1Json, preValidateRule) {
    var minLength = null;
    var maxLength = null;
    if (ruleValue1Json != null) {
        minLength = ruleValue1Json["minLength"];
        maxLength = ruleValue1Json["maxLength"];
    }
    
    if(minLength != null && minLength.trim().length != 0) {
        preValidateRule.min = minLength;
    }
    if(maxLength != null && maxLength.trim().length != 0) {
        preValidateRule.max = maxLength;
    }
}

/**
 * 生成范围值校验的预校验规则
 * { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' }
 */
function rangeValidatorGen(ruleValue1Json, preValidateRule) {
    var startValue = null;
    var endValue = null;
    if (ruleValue1Json != null) {
        startValue = ruleValue1Json["startValue"];
        endValue = ruleValue1Json["endValue"];
    }
    
    if(startValue != null && startValue.trim().length != 0) {
        preValidateRule.min = startValue;
    }
    if(endValue != null && endValue.trim().length != 0) {
        preValidateRule.max = endValue;
    }
}

/**
 * 生成可选值校验预校验规则
 * { type: 'enum', enum: ['admin', 'user', 'guest'] }
 */
function optionValidatorGen(ruleValue1Json, preValidateRule) {
    var options = [];
    if (ruleValue1Json != null && ruleValue1Json["optionValues"] != null) {
        options = ruleValue1Json["optionValues"];
    }
    //未配置可选项时，默认返回true
    if(options == null || options.lengt == 0) {
        return ;
    }
    
    preValidateRule.type = "enum";
    preValidateRule.enum = options;
    preValidateRule.trigger = "change";
}

/**
 * 生成空值校验的预校验规则
 * { required: true, message: '编码不能为空！', trigger: 'blur' }
 */
function emptyValidatorGen(ruleValue1Json, preValidateRule) {
    var unNull = false;
    var unEmpty = false;
    if (ruleValue1Json != null) {
        unNull = ruleValue1Json["unNull"];
        unEmpty = ruleValue1Json["unEmpty"];
    }

    if (unNull || unEmpty) {
        preValidateRule.required = true;
    } else {
        preValidateRule.required = false;
    }
}

/**
 * 对请求参数进行合法性校验
 * validateRuleGroup: 校验规则组
 * reqData: 请求参数
 */
export function validateReqData(validateRuleGroup, reqData) {
    //当校验规则未配置时，默认返回为true
    if(validateRuleGroup == null || validateRuleGroup.validateRules == null || validateRuleGroup.validateRules.length == 0) {
        return true;
    }
    
    if(reqData == null) {
        ElMessage.error(`请求参数不能为空！`);
        return false;
    }

    var result = true;
    var messages = [];
    result = validateData(validateRuleGroup, reqData, messages);

    if(!result) {
        var info = "";
        for(var i in messages) {
            info += "（" + i + "）" + messages[i] + "<br/>";
        }
        ElMessage.error({dangerouslyUseHTMLString: true, message: `请求参数未通过参数校验，错误信息如下：<br/>` + info});
        return false;
    }
    
    return true;
 
}

/**
 * 数据合法性校验
 * @param {*} validateRuleGroup 
 * @param {*} data 
 * @param {*} messages 
 * @returns 
 */
function validateData(validateRuleGroup, data, messages) {
    var result = true;
    
    for(var i in validateRuleGroup.validateRules) {
        var validateRule = validateRuleGroup.validateRules[i];
        if(validateRule.attributeCode == null || validateRule.attributeCode.trim().length == 0) {
            ElMessage.error(`校验规则配置有误：未配置校验属性！`);
            return false;
        }
        var ruleResult = null;//{"passed":false/true, "message":""}
        var attrValue = data[validateRule.attributeCode];
        if(validateRule.subAttributeCode != null && validateRule.subAttributeCode.trim().length > 0) {//对象类型的参数
            if(Array.isArray(attrValue)) {//数组的情况
                for(var i in attrValue) {
                    ruleResult = validate(validateRule, attrValue[i][validateRule.subAttributeCode], data);
                    if(!ruleResult.passed) {//校验未通过
                        result = false;
                        messages.push(ruleResult.message);
                    }
                }
            }else {
                ruleResult = validate(validateRule, attrValue[validateRule.subAttributeCode], data);
                if (!ruleResult.passed) {//校验未通过
                    result = false;
                    messages.push(ruleResult.message);
                }
            }
        }else {//非对象类型参数
            ruleResult = validate(validateRule, attrValue, data);
            if (!ruleResult.passed) {//校验未通过
                result = false;
                messages.push(ruleResult.message);
            }
        }
    }

    //递归处理子校验规则组
    if(validateRuleGroup.subRuleGroupMap != null) {
        for(var key in validateRuleGroup.subRuleGroupMap) {
            console.log(key, validateRuleGroup.subRuleGroupMap[key]);
            var dotIndex = key.indexOf(".");
            if(dotIndex < 0) {
                if(data[key] != null && !validateData(validateRuleGroup.subRuleGroupMap[key], data[key], messages)) {
                    result = false;
                }
            }else {
                if(data[key.substring(0,dotIndex)] != null && !validateData(validateRuleGroup.subRuleGroupMap[key], data[key.substring(0,dotIndex)][key.substring(dotIndex+1)], messages)) {
                    result = false;
                }
            }
        }
    }

    return result;
}

/**
 * 属性值校验实现方法
 * 返回值：{"passed":false/true, "message":""}
 */
function validate(validateRule, attrValue, reqData) {
    var passed = true;
    var errorCode = null;
    var message = null;
    var ruleValue1Json = null;
    if (validateRule.ruleValue1 != null) {
        ruleValue1Json = JSON.parse(validateRule.ruleValue1);
        errorCode = ruleValue1Json["errorCode"];
        message = ruleValue1Json["message"];
    }
    if (message == null || message.trim().length == 0) {
        message = "属性（" + validateRule.attributeCode +
            ((validateRule.subAttributeCode != null && validateRule.subAttributeCode.trim().length > 0) ? ("." + validateRule.subAttributeCode) : "") +
            "）未通过校验器（" + validateRule.ruleTypeName + "）的校验！"
    }
    if (errorCode != null && errorCode.trim().length != 0) {
        message = "错误码: " + errorCode + " , " + message;
    }
    if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.EmptyValidator") {//空值校验
        passed = emptyValidator(ruleValue1Json, attrValue);
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.OptionValidator") {//可选值校验
        passed = optionValidator(ruleValue1Json, attrValue);
        message += " 可选值为（" + ruleValue1Json["optionValues"] + "）";
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.RangeValidator") {//范围校验
        passed = rangeValidator(ruleValue1Json, attrValue);
        message += " 范围值为（" + ruleValue1Json["startValue"] + "," + ruleValue1Json["endValue"] + "）";
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.LengthValidator") {//长度校验
        passed = lengthValidator(ruleValue1Json, attrValue);
        message += " 长度范围为（" + ruleValue1Json["minLength"] + "," + ruleValue1Json["maxLength"] + "）";
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.PhoneValidator") {//手机号码校验
        passed = phoneValidator(ruleValue1Json, attrValue);
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.CertCodeValidator") {//身份证号码校验
        passed = certCodeValidator(ruleValue1Json, attrValue);
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.EmailValidator") {//邮件地址格式校验
        passed = emailValidator(ruleValue1Json, attrValue);
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.RegExprValidator") {//正则表达式校验
        passed = regExprValidator(ruleValue1Json, attrValue);
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.TypeValidator") {//类型校验
        passed = typeValidator(ruleValue1Json, attrValue);
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.IpAddrValidator") {//ip地址校验
        passed = ipAddrValidator(ruleValue1Json, attrValue);
    } else if (validateRule.ruleTypeClass == "cn.zpaas.lowcode.be.engine.ability.validator.UrlValidator") {//url校验
        passed = urlValidator(ruleValue1Json, attrValue);
    }
    
    var validateResult = {};
    validateResult["passed"] = passed;
    validateResult["message"] = message;
    
    return validateResult;
}

/**
 * 类型校验
 */
function typeValidator(ruleValue1Json, attrValue) {
    var attrType = null;
    if (ruleValue1Json != null) {
        attrType = ruleValue1Json["attrType"];
    }
    
    //当类型配置为空时，默认返回true
    if (attrType == null || attrType.trim().length == 0) {
        return true;
    }
    //当属性值为空时，默认为校验不通过
    if(attrValue == null) {
        return false;
    }
    
    if(attrType == "Date") {
        if((isNaN(attrValue) && !isNaN(Date.parse(attrValue)))) {
            return true;    
        }
    }else if(attrType == "Long" || attrType == "Integer" || attrType == "Byte"){
        if(!isNaN(parseInt(attrValue)) && isFinite(attrValue)) {
            return true;  
        }
    }else if(attrType == "Float" || attrType == "Double"){
        if(!isNaN(parseFloat(attrValue)) && isFinite(attrValue)) {
            return true;  
        }
    }else if(attrType == "Boolean") {
        if(attrValue.toUpperCase() == "TRUE" || attrValue.toUpperCase() == "FALSE") {
            return true;    
        }
    }else if(attrType == "String") {
        if(typeof (attrValue) === "string") {
            return true;    
        }
    }else if(attrType == "Character") {
        if(typeof (attrValue) === "string" && attrValue.length == 1) {
            return true;    
        }
    }
    
    return false;
}

/**
 * 正则表达式校验
 */
function regExprValidator(ruleValue1Json, attrValue) {
    var regExpr = "";
    if (ruleValue1Json != null) {
        regExpr = ruleValue1Json["regExpr"];
    }
    
    //当未配置正则表达式时，默认返回true
    if (regExpr == null || regExpr.trim().length == 0) {
        return true;
    }
    //当属性值为空时，默认为校验不通过
    if(attrValue == null) {
        return false;
    }
    if(new RegExp(regExpr).test(attrValue)) {
        return true;
    }
    return false;
}

/**
 * 邮件地址格式校验
 */
function emailValidator(ruleValue1Json, attrValue) {
    //当属性值为空时，默认为校验不通过
    if(attrValue == null) {
        return false;
    }
    var email_reg = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
    if (attrValue.match(email_reg)) {
        return true;
    }

    return false;
}

/**
 * 身份证号码校验
 */
function certCodeValidator(ruleValue1Json, attrValue) {
    //当属性值为空时，默认为校验不通过
    if(attrValue == null) {
        return false;
    }
    var certCode_reg = "(^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}$)";
    if (attrValue.match(certCode_reg)) {
        if (attrValue.length == 18) {
            var charArray = Array.from(attrValue);
            //前十七位加权因子
            var idCardWi = [7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2];
            //这是除以11后，可能产生的11位余数对应的验证码
            var idCardY = ["1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"];
            var sum = 0;
            for (var i = 0; i < idCardWi.length; i++) {
                var current = parseInt(charArray[i]);
                var count = current * idCardWi[i];
                sum += count;
            }
            var idCardLast = charArray[17];
            var idCardMod = sum % 11;
            if (idCardY[idCardMod].toUpperCase() == idCardLast.toUpperCase()) {
                return true;
            }

        }
    }
    return false;
}

/**
 * 手机号码校验
 */
function phoneValidator(ruleValue1Json, attrValue) {
    //当属性值为空时，默认为校验不通过
    if(attrValue == null) {
        return false;
    }
    var phone_reg = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(16[5,6])|(17[0-8])|(18[0-9])|(19[1、5、8、9]))\\d{8}$";
    if(attrValue.match(phone_reg)) {
        return true;
    }
    
    return false;
}

/**
 * url地址格式校验
 */
function urlValidator(ruleValue1Json, attrValue) {
    //当属性值为空时，默认为校验通过
    if(attrValue == null || ruleValue1Json == null) {
        return true;
    }
    var urlType = ruleValue1Json['urlType'];
	var isMultiUrl = ruleValue1Json['isMultiUrl'];
	var splitFlag = ruleValue1Json['splitFlag'];
	
	// 默认为IPV4
	if(urlType == null || urlType.trim().length == 0) {
		ipType = '1';
	}
	//分隔符默认为逗号
	if (splitFlag == null || splitFlag.trim().length == 0) {
		splitFlag = ',';
	}
    var isPass = true;

	//使用正则表达式进行校验
	if(ipType == '1') {//url
		isPass = isValidUrl(attrValue, isMultiUrl, splitFlag);
	}else if(ipType == '2') {//DOMAIN_NAME
		isPass = isValidDomainName(attrValue, isMultiUrl, splitFlag);
	}

    return isPass;
}

const DOMAIN_NAME_PATTERN = "^((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6}$";
const URL_PATTERN = "^(((http|ftp|https)://)?)((((?!-)[A-Za-z0-9-]{1,63}(?<!-)\\.)+[A-Za-z]{2,6})|((1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d))|(\\[([a-f0-9]{1,4}:){7}[a-f0-9]{1,4}\\])|(\\[([a-f0-9]{1,4}:){1,6}(:|(:[a-f0-9]{1,4}){1,6})\\]))(:[0-9]{1,4})?(/[a-zA-Z0-9&%./-~-]*)?$";
	
function isValidUrl(attrValue, isMultiUrl, splitFlag) {
    var isPass = true;
    if(isMultiUrl) {//多Url校验
        var urls = attrValue.split(splitFlag);
        for(var i in urls) {
            if(urls[i] != null && urls[i].trim().length > 0) {
                isPass = URL_PATTERN.match(urls[i]);
                if(!isPass) {//只要有一个多url校验地址不合法，中断循环
                    break;
                }
            }
        }
    }else {//单url校验
        isPass = URL_PATTERN.match(attrValue);
    }
    
    return isPass;
}

function isValidDomainName(attrValue, isMultiUrl, splitFlag) {
    var isPass = true;
    if(isMultiUrl) {//多DomainName校验
        var urls = attrValue.split(splitFlag);
        for(var i in urls) {
            if(urls[i] != null && urls[i].trim().length > 0) {
                isPass = DOMAIN_NAME_PATTERN.match(urls[i]);
                if(!isPass) {//只要有一个多DomainName校验地址不合法，中断循环
                    break;
                }
            }
        }
    }else {//单DomainName校验
        isPass = DOMAIN_NAME_PATTERN.match(attrValue);
    }
    
    return isPass;
}

/**
 * ip地址格式校验
 */
function ipAddrValidator(ruleValue1Json, attrValue) {
    //当属性值为空时，默认为校验通过
    if(attrValue == null || ruleValue1Json == null) {
        return true;
    }
    var ipType = ruleValue1Json['ipType'];
	var isCidr = ruleValue1Json['isCidr'];
	var splitFlag = ruleValue1Json['splitFlag'];
	var isMultiIp = ruleValue1Json['isMultiIp'];
	
	// 默认为IPV4
	if(ipType == null || ipType.trim().length == 0) {
		ipType = '1';
	}
	//分隔符默认为逗号
	if (splitFlag == null || splitFlag.trim().length == 0) {
		splitFlag = ',';
	}
    var isPass = true;

	//使用正则表达式进行校验
	if(ipType == '1') {//ipv4
		isPass = isValidIpv4(attrValue, isCidr, isMultiIp, splitFlag);
	}else if(ipType == '2') {//ipv6
		isPass = isValidIpv6(attrValue, isCidr, isMultiIp, splitFlag);
	}else if(ipType == '3') {//ipv4/ipv6
		isPass = isValidIpv4(attrValue, isCidr, isMultiIp, splitFlag) || isValidIpv6(attrValue, isCidr, isMultiIp, splitFlag);
	}

    return isPass;
}

const IPV4_PATTERN = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)$";
const IPV6_PATTERN = "^([a-f0-9]{1,4}:){7}[a-f0-9]{1,4}$";
const IPV6_PATTERN1 = "^(([a-f0-9]{1,4}:){1,6}(:|(:[a-f0-9]{1,4}){1,6}))$";
const IPV4_CIDR_PATTERN = "^(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|[1-9])\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)\\.(1\\d{2}|2[0-4]\\d|25[0-5]|[1-9]\\d|\\d)/(1[0-9]|2[0-9]|3[0-2]|\\d)$";
const IPV6_CIDR_PATTERN = "^([a-f0-9]{1,4}:){7}[a-f0-9]{1,4}/(1[0-1][0-9]|12[0-8]|[1-9][0-9]|[1-9]|\\d)$";
const IPV6_CIDR_PATTERN1 = "^(([a-f0-9]{1,4}:){1,6}(:|(:[a-f0-9]{1,4}){1,6}))/(1[0-1][0-9]|12[0-8]|[1-9][0-9]|[1-9]|\\d)$";

/**
 * ipv4地址校验
 * @param {*} attrValue 
 * @param {*} isCidr 
 * @param {*} isMultiIp 
 * @param {*} splitFlag 
 * @returns 
 */
function isValidIpv4(attrValue, isCidr, isMultiIp, splitFlag) {
    var isPass = true;
    if(isCidr) {//CIDR
        if(isMultiIp) {//多CIDR校验
            var ipAddrs = attrValue.split(splitFlag);
            for(var i in ipAddrs) {
                if(ipAddrs[i] != null && ipAddrs[i].trim().length > 0) {
                    isPass = IPV4_CIDR_PATTERN.match(ipAddrs[i]);
                    if(!isPass) {//只要有一个多CIDR校验地址不合法，中断循环
                        break;
                    }
                }
            }
        }else {//单多CIDR校验校验
            isPass = IPV4_CIDR_PATTERN.match(attrValue);
        }
    }else {//ip地址校验
        if(isMultiIp) {//多ip校验
            for(var i in ipAddrs) {
                if(ipAddrs[i] != null && ipAddrs[i].trim().length > 0) {
                    isPass = IPV4_PATTERN.match(ipAddrs[i]);
                    if(!isPass) {//只要有一个ip地址不合法，中断循环
                        break;
                    }
                }
            }
        }else {//单ip校验
            isPass = IPV4_PATTERN.match(attrValue);
        }
    }
    return isPass;
}

/**
 * ipv6地址校验
 * @param {*} attrValue 
 * @param {*} isCidr 
 * @param {*} isMultiIp 
 * @param {*} splitFlag 
 * @returns 
 */
function isValidIpv6(attrValue, isCidr, isMultiIp, splitFlag) {
    var isPass = true;
    if(isCidr) {//CIDR
        if(isMultiIp) {//多CIDR校验
            var ipAddrs = attrValue.split(splitFlag);
            for(var i in ipAddrs) {
                if(ipAddrs[i] != null && ipAddrs[i].trim().length > 0) {
                    isPass = IPV6_CIDR_PATTERN.match(ipAddrs[i]) || IPV6_CIDR_PATTERN1.match(ipAddrs[i]);
                    if(!isPass) {//只要有一个多CIDR校验地址不合法，中断循环
                        break;
                    }
                }
            }
        }else {//单多CIDR校验校验
            isPass = IPV6_CIDR_PATTERN.match(attrValue) || IPV6_CIDR_PATTERN1.match(attrValue);
        }
    }else {//ip地址校验
        if(isMultiIp) {//多ip校验
            for(var i in ipAddrs) {
                if(ipAddrs[i] != null && ipAddrs[i].trim().length > 0) {
                    isPass = IPV6_PATTERN.match(ipAddrs[i]) || IPV6_PATTERN1.match(ipAddrs[i]);
                    if(!isPass) {//只要有一个ip地址不合法，中断循环
                        break;
                    }
                }
            }
        }else {//单ip校验
            isPass = IPV6_PATTERN.match(attrValue) || IPV6_PATTERN1.match(attrValue);
        }
    }
    return isPass;
}

/**
 * 长度校验
 */
function lengthValidator(ruleValue1Json, attrValue) {
    var minLength = null;
    var maxLength = null;
    if (ruleValue1Json != null) {
        minLength = ruleValue1Json["minLength"];
        maxLength = ruleValue1Json["maxLength"];
    }
    
    //当最大和最小长度都为空时，默认返回true
    if ((minLength == null || minLength.trim().length == 0) && (maxLength == null || maxLength.trim().length == 0)) {
        return true;
    }
    //当属性值为空时，默认为校验不通过
    if(attrValue == null) {
        return false;
    }
    
    var length = attrValue.length;
    if(minLength != null && minLength.trim().length != 0) {
        if(length < parseInt(minLength)) {
            return false;
        }
    }
    if(maxLength != null && maxLength.trim().length != 0) {
        if(length > parseInt(maxLength)) {
            return false;
        }
    }
    
    return true;
}

/**
 * 生成范围值校验预校验规则
 */
function rangeValidator(ruleValue1Json, attrValue) {
    var startValue = null;
    var endValue = null;
    if (ruleValue1Json != null) {
        startValue = ruleValue1Json["startValue"];
        endValue = ruleValue1Json["endValue"];
    }
    
    //当起始和终止值都为空时，默认返回true
    if ((startValue == null || startValue.trim().length == 0) && (endValue == null || endValue.trim().length == 0)) {
        return true;
    }
    //当属性值为空时，默认为校验不通过
    if(attrValue == null) {
        return false;
    }
    
    if(!isNaN(parseFloat(attrValue)) && isFinite(attrValue)) {
        var numberValue = parseFloat(attrValue);
        if(startValue != null && startValue.trim().length != 0) {
            if(numberValue < parseFloat(startValue)) {
                return false;
            }
        }
        if(endValue != null && endValue.trim().length != 0) {
            if(numberValue > parseFloat(endValue)) {
                return false;
            }
        }
    }else if(isNaN(attrValue) && !isNaN(Date.parse(attrValue))) {
        var dateValue = Date.parse(attrValue);
        if(dateValue != null) {
            if(startValue != null && startValue.trim().length != 0) {
                if(dateValue < Date.parse(startValue)) {
                    return false;
                }
            }
            if(endValue != null && endValue.trim().length != 0) {
                if(dateValue > Date.parse(endValue)) {
                    return false;
                }
            }
        }
    }else {
        if (startValue != null && startValue.trim().length != 0) {
            if (attrValue.localeCompare(startValue)<0) {
                return false;
            }
        }
        if (endValue != null && endValue.trim().length != 0) {
            if (attrValue.localeCompare(endValue)>0) {
                return false;
            }
        }
    }
    return true;
}

/**
 * 可选值校验
 */
function optionValidator(ruleValue1Json, attrValue) {
    var passed = false;
    var options = [];
    if (ruleValue1Json != null && ruleValue1Json["optionValues"] != null) {
        options = ruleValue1Json["optionValues"];
    }
    //未配置可选项时，默认返回true
    if(options == null || options.lengt == 0) {
        return true;
    }
    
    for(var i in options) {
        if(options[i] == attrValue) {
            passed = true;
            break;
        }
    }
    
    return passed;
}

/**
 * 空值校验
 */
function emptyValidator(ruleValue1Json, attrValue) {
    var passed = true;
    var unNull = false;
    var unEmpty = false;
    if (ruleValue1Json != null) {
        unNull = ruleValue1Json["unNull"];
        unEmpty = ruleValue1Json["unEmpty"];
    }

    if (unNull && unEmpty) {
        if (attrValue == null || (typeof (attrValue) === "string" && attrValue.trim().length == 0)) {
            passed = false;
        }
    } else if (unNull) {
        if (attrValue == null) {
            passed = false;
        }
    } else if (unEmpty) {
        if (attrValue != null && attrValue.trim().length == 0) {
            passed = false;
        }
    }
    return passed;
}

/**
 * 处理请求对象的生成
 * inParams 请求参数定义信息列表
 * data 当前区域数据信息
 * customizedPage 自定义信息
 * funcDefine 功能定义信息
 * supportPages 是否需要分页
 */
export function generateReqObjectCustomizedPage(inParams, data, operation, customizedPage, funcDefine, supportPages, selectedData) {
    const reqData = {};
    
    var funcRegions = [];
    if(operation.operationCfgJSON != null && operation.operationCfgJSON.srcData != null && operation.operationCfgJSON.srcData.length > 0) {
        filterFuncRegions(customizedPage.customizedLayouts, funcRegions, operation.operationCfgJSON.srcData);
    }   

    if(funcRegions.length > 0) {
        for(var i in funcRegions) {
            generateReqObjectImpl(inParams, data, operation, funcRegions[i], funcDefine, reqData, selectedData);
        }
    }else {
        generateReqObjectImpl(inParams, data, operation, null, funcDefine, reqData, selectedData);
    }
    

    if(supportPages && operation.operationType == 'Q') {//需要支持分页
        reqData["pageParam"] = {
            "pageNo":1,
            "pageRows": (operation.operationCfgJSON != null && parseInt(operation.operationCfgJSON.pageRows) > 0) ? parseInt(operation.operationCfgJSON.pageRows) : 10,
            "sortName":"",
            "sortOrder":"",
            "total":0,
            "queryTotal":true
        }
    }
    
    var reqObject = {
        "reqData": reqData
    }
    return reqObject;

}

/**
 * 过滤出customizedLayoutIds包含的布局对应的funcRegion
 * @param {*} customizedLayouts 
 * @param {*} funcRegons 
 * @param {*} customizedLayoutIds 
 * @returns 
 */
export function filterFuncRegions(customizedLayouts, funcRegions, customizedLayoutIds, layoutType) {
    if(customizedLayouts == null || customizedLayouts.length == 0) {
        return;
    }
    for(var i in customizedLayouts) {
        var customizedLayout = customizedLayouts[i];
        if((customizedLayoutIds == null || customizedLayoutIds.includes(customizedLayout.id)) && (layoutType == null || customizedLayout.componentCode == layoutType)) {
            if(customizedLayout.layoutRegions != null && customizedLayout.layoutRegions.length > 0) {
                funcRegions.push(customizedLayout.layoutRegions[0].funcRegion);
            }
        }

        if(customizedLayout.subCustomizedLayouts != null) {
            filterFuncRegions(customizedLayout.subCustomizedLayouts, funcRegions, customizedLayoutIds, layoutType);
        }
    }
}

/**
 * 过滤出customizedLayoutIds包含的布局
 * @param {*} customizedLayouts 
 * @param {*} customizedLayoutIds 
 * @returns 
 */
export function findCustomizedLayouts(customizedLayouts, customizedLayoutIds) {
    var layouts = [];
    
    filterCustomizedLayouts(customizedLayouts, layouts, customizedLayoutIds)
    
    return layouts;
}

/**
 * 过滤出customizedLayoutIds包含的布局
 * @param {*} customizedLayouts 
 * @param {*} layouts 
 * @param {*} customizedLayoutIds 
 * @returns 
 */
function filterCustomizedLayouts(customizedLayouts, layouts, customizedLayoutIds) {
    if(customizedLayouts == null || customizedLayouts.length == 0) {
        return ;
    }
    for(var i in customizedLayouts) {
        var customizedLayout = customizedLayouts[i];
        if(customizedLayoutIds == null || customizedLayoutIds.includes(customizedLayout.id)) {
            layouts.push(customizedLayout);
        }
        
        if(customizedLayout.subCustomizedLayouts != null) {
            filterCustomizedLayouts(customizedLayout.subCustomizedLayouts, layouts, customizedLayoutIds);
        }
    }
}

/**
 * 处理请求对象的生成
 * inParams 请求参数定义信息列表
 * data 当前区域数据信息
 * funcRegion 区域定义信息
 * funcDefine 功能定义信息
 * supportPages 是否需要分页
 */
export function generateReqObject(inParams, data, operation, funcRegion, funcDefine, supportPages) {
    const reqData = {};

    generateReqObjectImpl(inParams, data, operation, funcRegion, funcDefine, reqData);

    if(supportPages) {//需要支持分页
        reqData["pageParam"] = {
            "pageNo":1,
            "pageRows":10,
            "sortName":"",
            "sortOrder":"",
            "total":0,
            "queryTotal":true
        }
    }
    
    var reqObject = {
        "reqData": reqData
    }
    return reqObject;
}


/**
 * 处理请求对象的生成
 * inParams 请求参数定义信息列表
 * data 当前区域数据信息
 * funcRegion 区域定义信息
 * funcDefine 功能定义信息
 * supportPages 是否需要分页
 */
function generateReqObjectImpl(inParams, data, operation, funcRegion, funcDefine, reqData, selectedData) {
    if(inParams != null && inParams.length > 0) {
        inParams.forEach(inParam => {//循环处理所有参数
            if(operation.operationCfg != null && operation.operationCfg.trim().length > 0) {//有配置操作配置信息
                var operationCfg = JSON.parse(operation.operationCfg);//转换成JSON对象
                var paramsJSON = null;
                if (operationCfg.params != null && operationCfg.params.length > 0) {
                    if(typeof(operationCfg.params) == 'string') {
                        paramsJSON = JSON.parse(operationCfg.params);
                    }else {
                        paramsJSON = operationCfg.params;
                    }
                    var configured = false;
                    for (var index in paramsJSON) {
                        var param = paramsJSON[index];
                        if (param.targetAttr != null && inParam.code == param.targetAttr) {//包含当前请求参数的配置，使用配置来生成请求参数
                            configured = true;
                            if (param.valueType == "F") {//固定值
                                reqData[param.targetAttr] = param.value;
                            } else if (param.valueType == "A") {//属性值
                                reqData[param.targetAttr] = processParamNotObject(param.value, 'J', data, funcRegion, funcDefine);
                            } else if (param.valueType == "S") {//选中的数据
                                reqData[param.targetAttr] = selectedData;
                            } else {
                                ElMessage.error(`非法的valueType: ` + param.valueType);
                            }
                            break;
                        }
                    }
                    if(configured) {//有对应的配置信息，不需要再执行后续的处理逻辑，直接返回
                        return;
                    }
                }
            }
            //执行默认的参数拼装规则
            var paramObj = processParam(inParam, data, funcRegion, funcDefine, operation);
            if(paramObj == undefined) {
                paramObj = null;
            }
            if(reqData[inParam.code] == null) {
                reqData[inParam.code] = paramObj;
            }else if(inParam.paramType == 'J') {
                if(paramObj != null && paramObj.trim().length > 0) {
                    reqData[inParam.code] = paramObj;
                }
            }else {
                if(paramObj != null) {
                    for(var key in paramObj) {
                        if(paramObj[key] != null) {
                            reqData[inParam.code][key] = paramObj[key];
                        }
                    }
                }
            }
        });
    }
}


/**
 * 处理一个请求参数的拼装，根据入参的code取对应的值，如果入参是个对象则根据对象的属性
 * inParam 请求参数定义信息
 * data 当前区域数据信息
 * funcRegion 区域定义信息
 * funcDefine 功能定义信息
 */
function processParam(inParam, data, funcRegion, funcDefine, operation) {
    if(inParam.paramType != 'J') {//对象类型
       return processParamObject(inParam, data, funcRegion, funcDefine, operation);
    }else {//原始Java类型
        return processParamNotObject(inParam.code, inParam.paramType, data, funcRegion, funcDefine);
    }
}

/**
 * 处理对象类型请求参数的拼装，根据入参根据对象的属性code取对应的值
 * inParam 请求参数定义信息
 * data 当前区域数据信息
 * funcRegion 区域定义信息
 * funcDefine 功能定义信息
 */
function processParamObject(inParam, data, funcRegion, funcDefine, operation) {
    var paramObj = {};
    
    inParam.paramClassObject.attributes.forEach(attribute => {//循环所有属性
        if(operation.operationCfg != null && operation.operationCfg.trim().length > 0) {//有配置操作配置信息
            var operationCfg = JSON.parse(operation.operationCfg);//转换成JSON对象
            var paramsJSON = null;
            if (operationCfg.params != null && operationCfg.params.length > 0) {
                if(typeof(operationCfg.params) == 'string') {
                    paramsJSON = JSON.parse(operationCfg.params);
                }else {
                    paramsJSON = operationCfg.params;
                }
                var configured = false;
                for (var index in paramsJSON) {
                    var param = paramsJSON[index];
                    if (param.targetAttr != null && attribute.code == param.targetAttr) {//包含当前请求参数的配置，使用配置来生成请求参数
                        configured = true;
                        var attrValue = null;
                        if (param.valueType == "F") {//固定值
                            attrValue = param.value;
                        } else if (param.valueType == "A") {//属性值
                            attrValue = processParamNotObject(param.value, 'J', data, funcRegion, funcDefine);
                        } else if (param.valueType == "S") {//选中的数据
                            attrValue = selectedData;
                        } else {
                            ElMessage.error(`非法的valueType: ` + param.valueType);
                        }
                        if(attrValue == undefined) {
                            attrValue = null;
                        }
                        paramObj[attribute.code] = attrValue;
                        break;
                    }
                }
                if(configured) {//有对应的配置信息，不需要再执行后续的处理逻辑，直接返回
                    return;
                }
            }
        }
        if (data[attribute.code] != null) {//当前区域存在该属性的数据
            var attrValue = data[attribute.code];
            if(attribute.attrType == 'J') {//非对象类型
                if (Array.isArray(attrValue) && attribute.isListAttr != 'Y') {//合理多值的情况，使用“$;”进行拼接
                    attrValue = concatMultiValue(attrValue);
                } else {
                    if(funcRegion != null && funcRegion.regionAttrAssigns != null && funcRegion.regionAttrAssigns.length > 0) {
                        for (var index = 0; index < funcRegion.regionAttrAssigns.length; index++) {//处理范围查询的情况
                            var attrAssign = funcRegion.regionAttrAssigns[index];
                            if (attrAssign.attributeId == attribute.id) {
                                if (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null &&
                                    funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].queryType == 'R') {//判断是否是范围查询
                                    attrValue = attrValue + "$;" + data[attribute.code + '_end'];//拼装起始和终止值
                                }
                                break;
                            }
                        }
                    }
                }
                paramObj[attribute.code] = attrValue;
            }else {//对象类型
                var subObj = funcDefine.objectMap[attribute.attrClass];
                if (attribute.isListAttr == 'Y') {
                    paramObj[attribute.code] = [];
                    if (subObj != null && data[attribute.code].length > 0) {
                        for (var j in data[attribute.code]) {
                            var row = {};
                            if (subObj.attributes != null && subObj.attributes.length > 0) {
                                for (var i in subObj.attributes) {
                                    if (data[attribute.code][j][subObj.attributes[i].code] != null) {
                                        row[subObj.attributes[i].code] = data[attribute.code][j][subObj.attributes[i].code];
                                    } else {
                                        row[subObj.attributes[i].code] = '';
                                    }
                                }
                            }
                            paramObj[attribute.code].push(row);
                        }
                    }
                } else {
                    paramObj[attribute.code] = {};
                    if (subObj != null && subObj.attributes != null && subObj.attributes.length > 0) {
                        for (var i in subObj.attributes) {
                            if (data[attribute.code][subObj.attributes[i].code] != null) {
                                paramObj[attribute.code][subObj.attributes[i].code] = data[attribute.code][subObj.attributes[i].code];
                            } else {
                                paramObj[attribute.code][subObj.attributes[i].code] = '';
                            }
                        }
                    }
                }
            }
            
        } else {//当前区域不存在该属性的数据
            if (attribute.attrType == 'J') {//非对象类型
                if (attribute.isListAttr == 'Y') {//将该属性值置空
                    paramObj[attribute.code] = [];
                } else {
                    paramObj[attribute.code] = '';
                }
            } else {
                if (attribute.isListAttr == 'Y') {//将该属性值置空
                    paramObj[attribute.code] = [];
                } else {
                    paramObj[attribute.code] = {};
                }
            }
            
        }
    });
    return paramObj;
}

/**
 * 处理对象类型请求参数的拼装，根据入参根据对象的属性code取对应的值
 * inParamCode 请求参数编码
 * inParamType 请求参数类型
 * data 当前区域数据信息
 * funcRegion 区域定义信息
 * funcDefine 功能定义信息
 */
function processParamNotObject(inParamCode, inParamType, data, funcRegion, funcDefine) {
    var attrValue = data[inParamCode];
    if(inParamType == 'J') {//多值处理
        if (attrValue != null && Array.isArray(attrValue)) {//合理多值的情况，使用“$;”进行拼接
            attrValue = concatMultiValue(attrValue);
        } else {
            if(funcRegion != null && funcRegion.regionAttrAssigns != null && funcRegion.regionAttrAssigns.length > 0) {
                for (var index = 0; index < funcRegion.regionAttrAssigns.length; index++) {//处理范围查询的情况
                    var attrAssign = funcRegion.regionAttrAssigns[index];
                    if (attrAssign.attribute.code == inParamCode) {//处理范围查询的情况
                        if (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null &&
                            funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].queryType == 'R') {//判断是否是范围查询
                            attrValue = attrValue + "$;" + data[attribute.code + '_end'];
                        }
                        break;
                    }
                }
            }
            
        }
    }
    
    return attrValue;
}

/**
 * 拼接字符串，使用“$;"
 * attrValues 要拼接的属性值列表
 */
function concatMultiValue(attrValues) {
    var attrValue = "";
    if(attrValues.length > 0) {
        for(var i in attrValues) {
            if(i == attrValues.length-1) {
                attrValue = attrValue + attrValues[i];
            }else {
                attrValue = attrValue + attrValues[i] + "$;";
            }
        }
    }
    return attrValue;
}

/**
 * 根据value获取value，支持列表和树形结构
 * @param {*} value 
 * @param {*} options 
 * @returns 
 */
function getLabel(value, options) {
    for(var index in options) {
        if(options[index].value == value) {
            return options[index].label;
        }else {
            if(options[index].children != null && options[index].children.length > 0) {
                var label = getLabel(value, options[index].children);
                if(label != null) {
                    return label;
                }
            }
        }
    }
    return null;
}

/**
 * 根据value获取value，支持列表和树形结构
 * @param {*} value 
 * @param {*} options 
 * @returns 
 */
function getLabels(values, options) {
    var labels = "";
    for(var index in values) {
        var label = getLabel(values[index], options);
        if(label == null) {
            label = values[index];
        }
        if(labels.length == 0) {
            labels = label;
        }else {
            labels += "/" + label;
        }
    }
    return labels;
}

/**
 * 根据value获取value，支持列表和树形结构
 * @param {*} value 
 * @param {*} options 
 * @returns 
 */
export function getLableByValue(value, options) {
    var label = null;
    if(Array.isArray(value)) {
        label = getLabels(value,options);
    }else {
        label = getLabel(value,options);
    }
    if(label == null) {
        return value;
    }else {
        return label;
    }
}

/**
 * 格式化统计报表的展示
 * @param {*} attrAssigns 属性列表
 * @returns [{attrAssigns:[{attrAssigns:[]}]}]
 */
export function formatReportDisplay(attrAssigns){
    var attrAssignList = [];
    if(attrAssigns != null && attrAssigns.length > 0) {
        //格式化表单展示
        var attrAssignMap = {};
        for(var i in attrAssigns) {
            var attr = attrAssigns[i];
            attrAssignMap[attr.id] = attr;
        }
        for(var i in attrAssigns) {
            var attr = attrAssigns[i];
            var parentAssignId = attr.displayCfgJSON.parentAssignId;
            if(parentAssignId != null && parentAssignId != "" && attrAssignMap[parentAssignId] != null) {
                if(attrAssignMap[parentAssignId].attrAssigns == null) {
                    attrAssignMap[parentAssignId].attrAssigns = [];
                }
                attrAssignMap[parentAssignId].attrAssigns.push(attr);
            }
        }
        for(var key in attrAssignMap) {
            if(attrAssignMap[key].displayCfgJSON.parentAssignId == null || attrAssignMap[key].displayCfgJSON.parentAssignId == "") {
                attrAssignList.push(attrAssignMap[key]);
            }
        }
    }
    
    return attrAssignList;
}

/**
 * 格式化表单的展示
 * @param {*} attrAssigns 属性列表
 * @returns {rowIndex: 0, attrAssigns:['attr']}
 */
export function formatFormDisplay(attrAssigns){
    //生成表单属性列表（分行）
    var attrAssignList = [];
    if(attrAssigns.length == 0) {
        attrAssignList.push({rowIndex: 0, attrAssigns:[]});
    }else {
        //格式化表单展示
        var attrAssignMap = {};
        attrAssignMap = formatFormAttrDisplayInfo(attrAssigns);
        for(var key in attrAssignMap) {
            if(attrAssignMap[key] != null && attrAssignMap[key].length != 0) {
                var attrAssignRow = {
                    rowIndex: key,
                    attrAssigns: attrAssignMap[key]
                };
                attrAssignList.push(attrAssignRow);
            }
        }
    }
    
    return attrAssignList;
}

/**
 * 构建属性列表
 * @param {*} formatAttrAssigns {rowIndex: 0, attrAssigns:['attr']}
 * @returns 返回属性的列表
 */
export function getAllAttrAssigns(formatAttrAssigns) {
    var allFilterAttrs = [];
    var index = 1;
    //处理绑定属性
    for(var i in formatAttrAssigns) {
        if(formatAttrAssigns[i].rowIndex != null) {
            for(var j in formatAttrAssigns[i].attrAssigns) {
                formatAttrAssigns[i].attrAssigns[j].displayOrder = index++;
                allFilterAttrs.push(formatAttrAssigns[i].attrAssigns[j]);
            }
        }else {
            formatAttrAssigns[i].displayOrder = index++;
            allFilterAttrs.push(formatAttrAssigns[i]);
        }
    }
    return allFilterAttrs;
}

/**
 * 表单中属性的格式化显示
 * @param {*} attrAssigns 
 * @returns {"rowIndex": [{attr}]},如{0:[{attr1},{attr2}]}
 */
function formatFormAttrDisplayInfo(attrAssigns) {
    var row = 0;
    var attrMap = {};
    var columnCountOfRow = 0;
    attrMap[0] = [];
    for (var index = 0; index < attrAssigns.length; index++) {
        //规范化一些为空的属性
        if (attrAssigns[index].colSpan == null || attrAssigns[index].colSpan == 0 || attrAssigns[index].colSpan == '0') {
            if (attrAssigns[index].displayType == 'H') {
                attrAssigns[index].colSpan = 0;
            } else {
                attrAssigns[index].colSpan = 8;
            }
        } else {
            attrAssigns[index].colSpan = parseInt(attrAssigns[index].colSpan);
        }
        
        if (attrAssigns[index].displayType == 'H') {
            attrAssigns[index].colSpan = 0;
        } else if (attrAssigns[index].colSpan > 24) {//当某个属性的colspan超过24时，独占一行
            attrAssigns[index].colSpan = 24;//colspan最长支持24
        }
        
        if (attrAssigns[index].rowSpan == null || attrAssigns[index].rowSpan == 0  || attrAssigns[index].rowSpan == '0') {
            attrAssigns[index].rowSpan = 1;
        }else {
            attrAssigns[index].rowSpan = parseInt(attrAssigns[index].rowSpan);
        }

        //当前行放不下的情况，需要先进行换行操作
        if (columnCountOfRow != 0 && (columnCountOfRow + attrAssigns[index].colSpan) > 24) {
            attrAssigns[index - 1].colSpan += (24 - columnCountOfRow)//使前一个属性的colspan占满剩下的空间
            columnCountOfRow = 24;//进行换行操作
            if (columnCountOfRow != 0 && columnCountOfRow % 24 == 0) {
                columnCountOfRow = 0;
                row++;
                attrMap[row] = [];
            }
        }
        //处理当前的属性
        attrMap[row].push(attrAssigns[index]);
        columnCountOfRow = columnCountOfRow + attrAssigns[index].colSpan;
        //进行分行判断
        if (columnCountOfRow != 0 && columnCountOfRow % 24 == 0) {
            columnCountOfRow = 0;
            row++;
            attrMap[row] = [];
        }
    }
    return attrMap;
}

/**
 * 服务单元格数据格式化
 * @param {*} attrAssign 
 * @param {*} cellValue 
 * @returns 
 */
export function reportCellValueFormat(attrAssign, cellValue) {
    if(attrAssign.displayCfgJSON.displayFormat == 'P') {
        var retainSize = 0;
        if(attrAssign.displayCfgJSON.formatCfg != null && attrAssign.displayCfgJSON.formatCfg.trim().length > 0) {
            retainSize = parseInt(attrAssign.displayCfgJSON.formatCfg);
        }
        return Math.round(cellValue * 100 * Math.pow(10, retainSize))/Math.pow(10, retainSize) + '%';
    }else if(attrAssign.displayCfgJSON.displayFormat == 'N') {
        var retainSize = 0;
        if(attrAssign.displayCfgJSON.formatCfg != null && attrAssign.displayCfgJSON.formatCfg.trim().length > 0) {
            retainSize = parseInt(attrAssign.displayCfgJSON.formatCfg);
        }
        return Math.round(cellValue * Math.pow(10, retainSize))/Math.pow(10, retainSize);
    }else {
        return cellValue;
    }
    
}