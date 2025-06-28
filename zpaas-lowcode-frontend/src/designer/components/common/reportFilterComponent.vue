<!--BI筛选器属性展示组件-->
<template>
    <!-- BI筛选器多选框 -->
    <el-checkbox-group v-model="regionData[attrAssign.dataSetDetail.content.code]" :disabled="attrAssign.readonly=='Y' || disableFlag == true" v-if="attrAssign.displayType == 'M'" class="ml-4">
        <el-checkbox v-for="filterValue in attrAssign.dataSetDetail.content.filterValues" :value="filterValue.id" :label="filterValue.name" size="small"/>
    </el-checkbox-group>

    <!-- BI标签选择按钮 -->
    <el-checkbox-group v-model="regionData[attrAssign.dataSetDetail.content.code]" :disabled="attrAssign.readonly=='Y' || disableFlag == true" v-else-if="attrAssign.displayType == 'N'" size="small">
        <el-checkbox-button :key="attrAssign.dataSetDetail.content.id" :value="attrAssign.dataSetDetail.content.id" :label="attrAssign.dataSetDetail.content.name" size="small"/>
    </el-checkbox-group>

    <!-- 其他标签形式按钮 -->
    <el-checkbox-group v-model="regionData[attrAssign.dataSetDetail.content.code]" :disabled="attrAssign.readonly=='Y' || disableFlag == true" v-else size="small">
        <el-checkbox-button :key="attrAssign.dataSetDetail.content.id" :value="attrAssign.dataSetDetail.content.id" :label="attrAssign.dataSetDetail.content.name" size="small"/>
    </el-checkbox-group>
</template>
<script>

export default {
    props: ['regionData', 'attrAssign', 'disableFlag'],
    
    components: {
    	
    },
    data() {
        
        return {
 
        }
    },
    mounted() {
        
    }
}
</script>