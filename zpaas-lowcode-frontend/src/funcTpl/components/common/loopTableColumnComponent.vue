<!-- 递归table-column组件 -->
<template>          
    <el-table-column v-if="attrAssign != null" :prop="(attrAssign.dataSetDetail.detailContentAlias != null && attrAssign.dataSetDetail.detailContentAlias.length > 0) ? attrAssign.dataSetDetail.detailContentAlias : attrAssign.dataSetDetail.content.code" :label="attrAssign.displayName" :width="attrAssign.displayWidth" :sortable="attrAssign.displayCfgJSON != null && attrAssign.displayCfgJSON.sortable" :id="attrAssign.displayName" :formatter="displayFormat">
        <loopTableColumnComponent v-if="attrAssign.attrAssigns != null" v-for="subAttrAssign in attrAssign.attrAssigns" :attrAssign="subAttrAssign" :parentAttrAssigns="attrAssign.attrAssigns" :funcRegion="funcRegion"/>
    </el-table-column> 
</template>
<script>
import { defineAsyncComponent } from 'vue';
import {reportCellValueFormat} from '../../js/common.js'

const loopTableColumnComponent = defineAsyncComponent(() => import('./loopTableColumnComponent.vue'));

export default {
    props: ['attrAssign','parentAttrAssigns', 'funcRegion'],
    
    emits: [],
    
    setup (props, {attrs, emit, slots}) {
        
        return {
        	
        }
    },
    computed: {
        
    },
    components: {
    	loopTableColumnComponent
    },
    
    data() {   
       
    	return {
            
        }
    },
    mounted() {
    	
    },
    methods: {
    	displayFormat(row, col, cellValue, index) {
            return reportCellValueFormat(this.attrAssign, cellValue);
        }
    }
}
</script>