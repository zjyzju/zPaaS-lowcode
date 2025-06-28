<!--编辑/查看类属性展示组件-->
<template>
<div :style="'width:100%;display:' + (attrAssign.displayHiddenMode == '0' ? 'none' :'block')">
<!-- 主对象属性 -->
    <!-- 隐藏框 -->
    <el-input type="hidden" v-model="regionData[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'H' && subObject == null" size="small"/>
    <!-- 文本输入框 -->
    <el-input v-model="regionData[attrAssign.attribute.code]" v-else-if="attrAssign.displayType == 'I' && subObject == null" :readonly="attrAssign.readonly=='Y' || disableFlag == true" size="small" />
    <!-- 文本域 -->
    <el-input v-model="regionData[attrAssign.attribute.code]" type="textarea" :rows="attrAssign.rowSpan" :readonly="attrAssign.readonly=='Y' || disableFlag == true" v-else-if="attrAssign.displayType == 'A' && subObject == null" size="small"/>
    <!-- 日期时间选择框 -->
    <el-date-picker type="datetime" v-model="regionData[attrAssign.attribute.code]" v-else-if="attrAssign.displayType == 'T' && subObject == null" value-format="YYYY-MM-DD HH:mm:ss" :readonly="attrAssign.readonly=='Y' || disableFlag == true" size="small"/>
    <!-- 日期选择框 -->
    <el-date-picker type="date" v-model="regionData[attrAssign.attribute.code]" v-else-if="attrAssign.displayType == 'D' && subObject == null" value-format="YYYY-MM-DD" :readonly="attrAssign.readonly=='Y' || disableFlag == true" size="small"/>
    <!-- 下拉选择框 -->
    <el-select v-model="regionData[attrAssign.attribute.code]" :disabled="attrAssign.readonly=='Y' || disableFlag == true" clearable filterable v-else-if="attrAssign.displayType == 'S' && subObject == null" size="small">
        <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == '' || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == '' || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'V' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'A'))"
                v-for="item in funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].options">
            <el-option :key="item.value" :label="item.label" :value="item.value" v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == regionData[funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"/>
        </template>
        <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != '' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != ''"
                v-for="item in regionData[attrAssign.attribute.code+'_options']">
            <el-option :key="item.value" :label="item.label" :value="item.value" v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == regionData[funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"/>
        </template>
    </el-select>
    <!-- 级联选择框 -->
    <el-cascader v-model="regionData[attrAssign.attribute.code]" :options="funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].options" :disabled="attrAssign.readonly=='Y' || disableFlag == true" clearable v-else-if="attrAssign.displayType == 'E' && subObject == null && funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == '' || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == '' || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'V' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'A'))" size="small" />
    <el-cascader v-model="regionData[attrAssign.attribute.code]" :options="regionData[attrAssign.attribute.code+'_options']" :disabled="attrAssign.readonly=='Y' || disableFlag == true" clearable v-else-if="attrAssign.displayType == 'E' && subObject == null && funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != '' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != ''" size="small" />
    <!-- 树形下拉选择框 -->
    <treeSelect :regionData="regionData" :attrCode="attrAssign.attribute.code" :options="funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].options" :disabled="attrAssign.readonly=='Y' || disableFlag == true" clearable v-else-if="attrAssign.displayType == 'K' && subObject == null && funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == '' || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == '' || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'V' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'A'))" size="small"/>
    <treeSelect :regionData="regionData" :attrCode="attrAssign.attribute.code" :options="regionData[attrAssign.attribute.code+'_options']" :disabled="attrAssign.readonly=='Y' || disableFlag == true" clearable v-else-if="attrAssign.displayType == 'K' && subObject == null && funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != '' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != ''" size="small"/>
    
    <!-- 单选框 -->
    <el-radio-group v-model="regionData[attrAssign.attribute.code]" :disabled="attrAssign.readonly=='Y' || disableFlag == true" v-else-if="attrAssign.displayType == 'R' && subObject == null" class="ml-4">
        <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == '' || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == '' || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'V' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'A'))"
                v-for="item in funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].options">
            <el-radio v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == regionData[funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"
                     :value="item.value" size="large">{{item.label}}</el-radio>
        </template>
        <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != '' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != ''"
                v-for="item in regionData[attrAssign.attribute.code+'_options']">
            <el-radio v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == regionData[funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"
                     :value="item.value" size="large">{{item.label}}</el-radio>
        </template>
    </el-radio-group>
    <!-- 多选框 -->
    <el-checkbox-group v-model="regionData[attrAssign.attribute.code]" :disabled="attrAssign.readonly=='Y' || disableFlag == true" v-else-if="attrAssign.displayType == 'C' && subObject == null && Array.isArray(regionData[attrAssign.attribute.code])" class="ml-4">
        <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == '' || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == '' || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'V' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'A'))"
                v-for="item in funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].options">
            <el-checkbox v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == regionData[funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"
                     :value="item.value" size="large">{{item.label}}</el-checkbox>
        </template>
        <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != '' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != ''"
                v-for="item in regionData[attrAssign.attribute.code+'_options']">
            <el-checkbox v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == regionData[funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"
                     :value="item.value" size="large">{{item.label}}</el-checkbox>
        </template>
    </el-checkbox-group>
    <!-- 弹出选择框 -->
    <popupSelectComponent  v-else-if="attrAssign.displayType == 'P' && subObject == null" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :disableFlag ="attrAssign.readonly=='Y' || disableFlag == true" :subObject="subObject"/>
    <!-- 弹出查看框 -->
    <popupViewComponent  v-else-if="attrAssign.displayType == 'V' && subObject == null" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :disableFlag ="attrAssign.readonly=='Y' || disableFlag == true" :subObject="subObject"/>
    <!-- 密码框 -->
    <el-input type="password" v-model="regionData[attrAssign.attribute.code]" v-else-if="attrAssign.displayType == 'W' && subObject == null" size="small" :readonly="attrAssign.readonly=='Y' || disableFlag == true"/>
    <!-- 文件上传 -->
    <fileUploadComponent  v-else-if="attrAssign.displayType == 'F' && subObject == null" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :disableFlag ="attrAssign.readonly=='Y' || disableFlag == true"/>
    <!-- 文件下载 -->
    <fileDownloadComponent  v-else-if="attrAssign.displayType == 'G' && subObject == null" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :disableFlag ="attrAssign.readonly=='Y' || disableFlag == true"/>
    <!-- 文本标签-->
    <span v-else-if="attrAssign.displayType == 'L' && subObject == null">{{ regionData[attrAssign.attribute.code+'_label'] != null ? regionData[attrAssign.attribute.code+'_label'] : regionData[attrAssign.attribute.code] }}</span>

<!-- 一对一子对象属性 -->

    <!-- 隐藏框 -->
    <el-input type="hidden" v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]" v-if="attrAssign.displayType == 'H' && subObject != null" size="small"/>
    <!-- 文本输入框 -->
    <el-input v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]" v-else-if="attrAssign.displayType == 'I' && subObject != null" :readonly="attrAssign.readonly=='Y' || disableFlag == true" size="small"/>
    <!-- 文本域 -->
    <el-input v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]" type="textarea" :rows="attrAssign.rowSpan" :readonly="attrAssign.readonly=='Y' || disableFlag == true" v-else-if="attrAssign.displayType == 'A' && subObject != null" size="small"/>
    <!-- 日期时间选择框 -->
    <el-date-picker type="datetime" v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]" v-else-if="attrAssign.displayType == 'T' && subObject != null" value-format="YYYY-MM-DD HH:mm:ss" :readonly="attrAssign.readonly=='Y' || disableFlag == true" size="small"/>
    <!-- 日期选择框 -->
    <el-date-picker type="date" v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]" v-else-if="attrAssign.displayType == 'D' && subObject != null" value-format="YYYY-MM-DD" :readonly="attrAssign.readonly=='Y' || disableFlag == true" size="small"/>
    <!-- 下拉选择框 -->
    <el-select v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]" :disabled="attrAssign.readonly=='Y' || disableFlag == true" clearable v-else-if="attrAssign.displayType == 'S' && subObject != null" size="small">
        <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == '' || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == '' || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'V' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'A'))"
                v-for="item in funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].options">
            <el-option :key="item.value" :label="item.label" :value="item.value" v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == regionData[subObject.mainRelAttribute.code][funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"/>
        </template>
        <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != '' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != ''"
                v-for="item in regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_options']">
            <el-option :key="item.value" :label="item.label" :value="item.value" v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == regionData[subObject.mainRelAttribute.code][funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"/>
        </template>
    </el-select>
    <!-- 级联选择框 -->
    <el-cascader v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]" :options="funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].options" :disabled="attrAssign.readonly=='Y' || disableFlag == true" clearable v-else-if="attrAssign.displayType == 'E' && subObject != null && funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == '' || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == '' || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'V' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'A'))" size="small"/>
    <el-cascader v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]" :options="regionData[attrAssign.attribute.code+'_options']" :disabled="attrAssign.readonly=='Y' || disableFlag == true" clearable v-else-if="attrAssign.displayType == 'E' && subObject != null && funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != '' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != ''" size="small"/>
    <!-- 树形下拉选择框 -->
    <treeSelect :regionData="regionData[subObject.mainRelAttribute.code]" :attrCode="attrAssign.attribute.code" :options="funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].options" :disabled="attrAssign.readonly=='Y' || disableFlag == true" clearable v-else-if="attrAssign.displayType == 'K' && subObject != null && funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == '' || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == '' || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'V' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'A'))" size="small"/>
    <treeSelect :regionData="regionData[subObject.mainRelAttribute.code]" :attrCode="attrAssign.attribute.code" :options="regionData[attrAssign.attribute.code+'_options']" :disabled="attrAssign.readonly=='Y' || disableFlag == true" clearable v-else-if="attrAssign.displayType == 'K' && subObject != null && funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != '' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != ''" size="small"/>
    <!-- 单选框 -->
    <el-radio-group v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]" :disabled="attrAssign.readonly=='Y' || disableFlag == true" v-else-if="attrAssign.displayType == 'R' && subObject != null" class="ml-4">
        <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == '' || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == '' || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'V' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'A'))"
                v-for="item in funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].options">
            <el-radio v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == regionData[subObject.mainRelAttribute.code][funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"
                     :value="item.value" size="large">{{item.label}}</el-radio>
        </template>
        <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != '' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != ''"
                v-for="item in regionData[attrAssign.attribute.code+'_options']">
            <el-radio v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == regionData[subObject.mainRelAttribute.code][funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"
                     :value="item.value" size="large">{{item.label}}</el-radio>
        </template>
    </el-radio-group>
    <!-- 多选框 -->
    <el-checkbox-group v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]" :disabled="attrAssign.readonly=='Y' || disableFlag == true" v-else-if="attrAssign.displayType == 'C' && subObject != null && Array.isArray(regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code])" class="ml-4">
        <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == '' || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == '' || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'V' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].interactionType != 'A'))"
                v-for="item in funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].options">
            <el-checkbox v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == regionData[subObject.mainRelAttribute.code][funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"
                     :value="item.value" size="large">{{item.label}}</el-checkbox>
        </template>
        <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != '' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != ''"
                v-for="item in regionData[attrAssign.attribute.code+'_options']">
            <el-checkbox v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == regionData[subObject.mainRelAttribute.code][funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"
                     :value="item.value" size="large">{{item.label}}</el-checkbox>
        </template>
    </el-checkbox-group>
    <!-- 弹出选择框 -->
    <popupSelectComponent  v-else-if="attrAssign.displayType == 'P' && subObject != null" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :subObject="subObject" :disableFlag ="attrAssign.readonly=='Y' || disableFlag == true"/>
    <!-- 弹出查看框 -->
    <popupViewComponent  v-else-if="attrAssign.displayType == 'V' && subObject != null" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :subObject="subObject" :disableFlag ="attrAssign.readonly=='Y' || disableFlag == true"/>
    <!-- 密码框 -->
    <el-input type="password" v-model="regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code]" v-else-if="attrAssign.displayType == 'W' && subObject != null" size="small" :readonly="attrAssign.readonly=='Y' || disableFlag == true"/>
    <!-- 文件上传 -->
    <fileUploadComponent  v-else-if="attrAssign.displayType == 'F' && subObject != null" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData[subObject.mainRelAttribute.code]" :attrAssign="attrAssign" :disableFlag ="attrAssign.readonly=='Y' || disableFlag == true"/>
    <!-- 文件下载 -->
    <fileDownloadComponent  v-else-if="attrAssign.displayType == 'G' && subObject != null" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData[subObject.mainRelAttribute.code]" :attrAssign="attrAssign" :disableFlag ="attrAssign.readonly=='Y' || disableFlag == true"/>
    <!-- 文本标签-->
    <span v-else-if="attrAssign.displayType == 'L' && subObject != null">{{ regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_label'] != null ? regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code+'_label'] : regionData[subObject.mainRelAttribute.code][attrAssign.attribute.code] }}</span>
</div>
</template>
<script>
import popupSelectComponent from './popupSelectAttrComponent.vue'
import popupViewComponent from './popupViewAttrComponent.vue'
import fileUploadComponent from './fileUploadComponent.vue'
import fileDownloadComponent from './fileDownloadComponent.vue'
import treeSelect from './treeSelectComponent.vue'

export default {
    props: ['funcRegion','funcDefine','regionData', 'attrAssign', 'disableFlag', 'subObject'],
    
    components: {
    	popupSelectComponent,
    	fileUploadComponent,
    	fileDownloadComponent,
        popupViewComponent,
        treeSelect
    },
    mounted() {
        
        if(this.attrAssign.displayType == 'C' || this.attrAssign.displayType == 'E') {//多选框/级联选择框
            if(this.subObject == null) {//主对象或者从对象新增/编辑页的多选框
                if(this.regionData[this.attrAssign.attribute.code] == null || this.regionData[this.attrAssign.attribute.code] == "") {
                    this.regionData[this.attrAssign.attribute.code] = [];
                }else {//使用“$;”进行分拆
                    this.regionData[this.attrAssign.attribute.code] = this.regionData[this.attrAssign.attribute.code].split("$;");
                }
            }else {//从对象的多选框
                if(this.regionData[subObject.mainRelAttribute.code][this.attrAssign.attribute.code] == null || this.regionData[subObject.mainRelAttribute.code][this.attrAssign.attribute.code] == "") {
                    this.regionData[subObject.mainRelAttribute.code][this.attrAssign.attribute.code] = [];
                }else {//使用“$;”进行分拆
                    this.regionData[subObject.mainRelAttribute.code][this.attrAssign.attribute.code] = this.regionData[subObject.mainRelAttribute.code][this.attrAssign.attribute.code].split("$;");
                }
            }
        }
    }
} 
</script>