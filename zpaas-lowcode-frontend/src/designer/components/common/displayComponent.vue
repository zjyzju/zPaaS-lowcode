<!--编辑/查看类属性展示组件-->
<template>
<!-- 主对象属性 -->
    <!-- 隐藏框 -->
    <el-input type="hidden" v-model="regionData[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'H' && subObject == null" size="small"/>
    <!-- 文本输入框 -->
    <el-input v-model="regionData[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'I' && subObject == null" :readonly="attrAssign.readonly=='Y' || disableFlag == true" size="small"/>
    <!-- 文本域 -->
    <el-input v-model="regionData[attrAssign.attribute.code]" type="textarea" :rows="attrAssign.rowSpan" :readonly="attrAssign.readonly=='Y' || disableFlag == true" v-if="attrAssign.displayType == 'A' && subObject == null" size="small"/>
    <!-- 日期时间选择框 -->
    <el-date-picker type="datetime" v-model="regionData[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'T' && subObject == null" value-format="YYYY-MM-DD HH:mm:ss" :readonly="attrAssign.readonly=='Y' || disableFlag == true" size="small"/>
    <!-- 日期选择框 -->
    <el-date-picker type="date" v-model="regionData[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'D' && subObject == null" value-format="YYYY-MM-DD" :readonly="attrAssign.readonly=='Y' || disableFlag == true" size="small"/>
    <!-- 下拉选择框 -->
    <el-select v-model="regionData[attrAssign.attribute.code]" :disabled="attrAssign.readonly=='Y' || disableFlag == true" clearable v-if="attrAssign.displayType == 'S' && subObject == null" size="small">
        <el-option key="option1" label="选项1" value="option1"/>
        <el-option key="option2" label="选项2" value="option2"/>
    </el-select>
    <!-- 级联选择框 -->
    <el-cascader v-model="regionData[attrAssign.attribute.code]" :options="cascaderOptions" :disabled="attrAssign.readonly=='Y' || disableFlag == true" clearable v-if="attrAssign.displayType == 'E' && subObject == null" size="small"/>
    <!-- 树形下拉选择框 -->
    <treeSelect :regionData="regionData" :attrCode="attrAssign.attribute.code" :options="cascaderOptions" :disabled="attrAssign.readonly=='Y' || disableFlag == true" v-if="attrAssign.displayType == 'K' && subObject == null" size="small"/>
    <!-- 单选框 -->
    <el-radio-group v-model="regionData[attrAssign.attribute.code]" :disabled="attrAssign.readonly=='Y' || disableFlag == true" v-if="attrAssign.displayType == 'R' && subObject == null" class="ml-4">
        <el-radio value="radio1" size="small">单选项1</el-radio>
        <el-radio value="radio2" size="small">单选项2</el-radio>
    </el-radio-group>
    <!-- 多选框 -->
    <el-checkbox-group v-model="regionData[attrAssign.attribute.code]" :disabled="attrAssign.readonly=='Y' || disableFlag == true" v-if="attrAssign.displayType == 'C' && subObject == null" class="ml-4">
        <el-checkbox value="checkbox1" size="small">多选项1</el-checkbox>
        <el-checkbox value="checkbox2" size="small">多选项2</el-checkbox>
    </el-checkbox-group>
    <!-- 弹出选择框 -->
    <popupSelectComponent  v-if="attrAssign.displayType == 'P' && subObject == null" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :disableFlag="attrAssign.readonly=='Y' || disableFlag == true" :subObject="subObject"/>
    <!-- 弹出选择框 -->
    <popupViewComponent  v-if="attrAssign.displayType == 'V' && subObject == null" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :disableFlag="attrAssign.readonly=='Y' || disableFlag == true" :subObject="subObject"/>
    <!-- 密码框 -->
    <el-input type="password" v-model="regionData[attrAssign.attribute.code]" v-if="attrAssign.displayType == 'W' && subObject == null" size="small" :readonly="attrAssign.readonly=='Y' || disableFlag == true"/>
    <!-- 文件上传 -->
    <fileUploadComponent  v-if="attrAssign.displayType == 'F' && subObject == null" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :disableFlag="attrAssign.readonly=='Y' || disableFlag == true"/>
    <!-- 文件下载 -->
    <fileDownloadComponent  v-if="attrAssign.displayType == 'G' && subObject == null" :funcRegion="funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :disableFlag="attrAssign.readonly=='Y' || disableFlag == true"/>   

</template>
<script>
import popupSelectComponent from '../../../funcTpl/components/common/popupSelectAttrComponent.vue'
import popupViewComponent from '../../../funcTpl/components/common/popupViewAttrComponent.vue'
import fileUploadComponent from '../../../funcTpl/components/common/fileUploadComponent.vue'
import fileDownloadComponent from '../../../funcTpl/components/common/fileDownloadComponent.vue'
import treeSelect from '../../../funcTpl/components/common/treeSelectComponent.vue'


export default {
    props: ['funcRegion','funcDefine','regionData', 'attrAssign', 'disableFlag', 'subObject'],
    
    components: {
    	popupSelectComponent,
    	fileUploadComponent,
    	fileDownloadComponent,
        popupViewComponent,
        treeSelect
    },
    data() {
        const cascaderOptions = [
            {
                value: 'option1',
                label: 'option1', 
                children: [
                    {
                        value: 'option11',
                        label: 'option11',
                        children: [
                            {
                                value: 'option111',
                                label: 'option111',
                            },
                            {
                                value: 'option112',
                                label: 'option112',
                            }
                        ],
                    },
                    {
                        value: 'option12',
                        label: 'option12',
                        children: [
                            {
                                value: 'option121',
                                label: 'option121',
                            },
                            {
                                value: 'option122',
                                label: 'option122',
                            }
                        ],
                    }
                ],
            },
            {
                value: 'option2',
                label: 'option2',
                children: [
                    {
                        value: 'option21',
                        label: 'option21',
                    },
                    {
                        value: 'option22',
                        label: 'option22',
                    },
                    {
                        value: 'option23',
                        label: 'option23',
                    }
                ],
            }
        ];
        
        return {
            cascaderOptions
        }
    },
    mounted() {
        
    }
}
</script>