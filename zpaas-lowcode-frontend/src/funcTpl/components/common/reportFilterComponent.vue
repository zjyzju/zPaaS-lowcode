<!--BI筛选器属性展示组件-->
<template>
    <!-- BI筛选器多选框 -->
    <el-checkbox-group v-model="regionData[attrAssign.dataSetDetail.detailContentId]" :disabled="attrAssign.readonly=='Y' || disableFlag == true" v-if="attrAssign.displayType == 'M'" class="ml-4">
        <el-checkbox v-for="filterValue in attrAssign.dataSetDetail.content.filterValues" :value="filterValue.id">{{ filterValue.name }}</el-checkbox>
    </el-checkbox-group>

    <!-- BI标签选择按钮 -->
    <el-checkbox-group v-model="regionData[attrAssign.dataSetDetail.detailContentId]" :disabled="attrAssign.readonly=='Y' || disableFlag == true" v-if="attrAssign.displayType == 'N'" size="small">
        <el-checkbox-button :key="attrAssign.dataSetDetail.detailContentId" :value="attrAssign.dataSetDetail.detailContentId" size="small">{{ attrAssign.displayName }}</el-checkbox-button>
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