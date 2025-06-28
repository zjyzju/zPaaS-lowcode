<!--查询属性展示组件-->
<template>
    <el-form-item :label="attrAssign.displayName" label-width="120px">
       <template #label>
           <!-- 模糊查询标志 -->
           <span style="color: red;" v-if="funcDefine.objectMap[attrAssign.objectId].attrOptionMap != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].queryType == 'F'">%</span>
           {{attrAssign.displayName}}
           
       </template>
       <!-- 隐藏框 -->
       <el-input type="hidden" v-model="queryCondition[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'H'" size="small"/>
       <!-- 文本域 -->
       <el-input v-model="queryCondition[attrAssign.attribute.code]" type="textarea" :rows="attrAssign.rowSpan" :readonly="attrAssign.readonly=='Y'" v-if="attrAssign.displayType == 'A'" size="small"/>
       <!-- 下拉选择 -->
       <el-select v-model="queryCondition[attrAssign.attribute.code]" :disabled="attrAssign.readonly=='Y'" clearable filterable v-if="attrAssign.displayType == 'S'" size="small">
           <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == '' || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == '')"
                   v-for="item in funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].options">
               <el-option :key="item.value" :label="item.label" :value="item.value" v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == queryCondition[funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"/>
           </template>
           <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != null  && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != '' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != ''"
                v-for="item in queryCondition[attrAssign.attribute.code+'_options']">
               <el-option :key="item.value" :label="item.label" :value="item.value" v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == queryCondition[funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"/>
           </template>
       </el-select>
       <!-- 单选框 -->
       <el-radio-group v-model="queryCondition[attrAssign.attribute.code]" :disabled="attrAssign.readonly=='Y'" v-if="attrAssign.displayType == 'R'" class="ml-4">
           <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == '' || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == '')"
                   v-for="item in funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].options">
               <el-radio v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == queryCondition[funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"
                        :value="item.value" size="large">{{item.label}}</el-radio>
           </template>
           <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != null  && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != '' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != ''"
                v-for="item in queryCondition[attrAssign.attribute.code+'_options']">
               <el-radio v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == queryCondition[funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"
                        :value="item.value" size="large">{{item.label}}</el-radio>
           </template>
       </el-radio-group>
       <!-- 多选框 -->
       <el-checkbox-group v-model="queryCondition[attrAssign.attribute.code]" :disabled="attrAssign.readonly=='Y'" v-if="attrAssign.displayType == 'C'" class="ml-4">
           <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId == '' || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId == '')"
                   v-for="item in funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].options">
               <el-checkbox v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == queryCondition[funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"
                        :value="item.value" size="large">{{item.label}}</el-checkbox>
           </template>
           <template  v-if="funcDefine.objectMap[attrAssign.objectId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != null  && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentObjectAssignId != '' && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributeId != ''"
                v-for="item in queryCondition[attrAssign.attribute.code+'_options']">
               <el-checkbox v-if="item.parent == null || (funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath.trim().length > 0  && item.parent == queryCondition[funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].parentAttributePath])"
                        :value="item.value" size="large">{{item.label}}</el-checkbox>
           </template>
       </el-checkbox-group>
       <!-- 非范围条件 -->
       <template v-if="funcDefine.objectMap[attrAssign.objectId].attrOptionMap == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] == null || funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].queryType != 'R'">
           <!-- 输入框 -->
           <el-input v-model="queryCondition[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'I'" :readonly="attrAssign.readonly=='Y'" size="small"/>
           <!-- 日期时间选择框 -->
           <el-date-picker type="datetime" v-model="queryCondition[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'T'" :readonly="attrAssign.readonly=='Y'" size="small" value-format="YYYY-MM-DD HH:mm:ss"/>
           <!-- 日期选择框 -->
           <el-date-picker type="date" v-model="queryCondition[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'D'" :readonly="attrAssign.readonly=='Y'" size="small" value-format="YYYY-MM-DD"/>
       </template>
       <!-- 范围条件 -->
       <template v-if="funcDefine.objectMap[attrAssign.objectId].attrOptionMap != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId] != null && funcDefine.objectMap[attrAssign.objectId].attrOptionMap[attrAssign.attributeId].queryType == 'R'">
           <!-- 输入框开始 -->
           <el-input style="width:150px" v-model="queryCondition[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'I'" :readonly="attrAssign.readonly=='Y'" size="small"/>
           <!-- 日期时间选择框开始 -->
           <el-date-picker style="width:150px" type="datetime" v-model="queryCondition[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'T'" :readonly="attrAssign.readonly=='Y'" size="small" value-format="YYYY-MM-DD HH:mm:ss"/>
           <!-- 日期选择框开始 -->
           <el-date-picker style="width:150px" type="date" v-model="queryCondition[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'D'" :readonly="attrAssign.readonly=='Y'" size="small" value-format="YYYY-MM-DD"/>
           &nbsp;-&nbsp;
           <!-- 输入框结束 -->
           <el-input style="width:150px" v-model="queryCondition[attrAssign.attribute.code+'_end']" v-if="attrAssign.displayType == 'I'" :readonly="attrAssign.readonly=='Y'" size="small"/>
           <!-- 日期时间选择框结束 -->
           <el-date-picker style="width:150px" type="datetime" v-model="queryCondition[attrAssign.attribute.code+'_end']" v-if="attrAssign.displayType == 'T'" :readonly="attrAssign.readonly=='Y'" size="small" value-format="YYYY-MM-DD HH:mm:ss"/>
           <!-- 日期选择框结束 -->
           <el-date-picker style="width:150px" type="date" v-model="queryCondition[attrAssign.attribute.code+'_end']" v-if="attrAssign.displayType == 'D'" :readonly="attrAssign.readonly=='Y'" size="small" value-format="YYYY-MM-DD"/>
       </template>
       <!-- 弹出选择框 -->
       <popupSelectComponent  v-if="attrAssign.displayType == 'P' && subObject == null" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="queryCondition" :attrAssign="attrAssign" :disableFlag="disableFlag" :subObject="subObject"/>
       <!-- 弹出选择框 -->
       <popupViewComponent  v-if="attrAssign.displayType == 'V' && subObject == null" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="queryCondition" :attrAssign="attrAssign" :disableFlag="disableFlag" :subObject="subObject"/>
       <!-- 密码框 -->
       <el-input type="password" v-model="queryCondition[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'W'" size="small"/>
       
   </el-form-item>
</template>
<script>
import popupSelectComponent from './popupSelectAttrComponent.vue'
import popupViewComponent from './popupViewAttrComponent.vue'


export default {
    props: ['funcRegion','funcDefine','queryCondition', 'attrAssign', 'disableFlag', 'subObject'],
    
    components: {
    	popupSelectComponent,
        popupViewComponent
    }
}
</script>