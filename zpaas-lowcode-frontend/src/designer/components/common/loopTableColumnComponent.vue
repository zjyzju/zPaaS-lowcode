<!-- 递归table-column组件 -->
<template>          
    <el-table-column v-if="attrAssign != null" :prop="(attrAssign.dataSetDetail.detailContentAlias != null && attrAssign.dataSetDetail.detailContentAlias.length > 0) ? attrAssign.dataSetDetail.detailContentAlias : attrAssign.dataSetDetail.content.code" :label="attrAssign.displayName" :width="attrAssign.displayWidth" :sortable="attrAssign.displayCfgJSON != null && attrAssign.displayCfgJSON.sortable" :id="attrAssign.displayName" :formatter="displayFormat">
        <template #header="scope">
            <span class="floatDeleteContainer" @click="onAttrSelected(attrAssign)">
                {{ attrAssign.displayName }}&nbsp;
                <span class="floatDelete">
                    <el-link  size="small" @click="deleteAttr(parentAttrAssigns, attrAssign)"><el-icon><Delete /></el-icon></el-link>
                    &nbsp;
                    <el-link size="small" @click="addSiblingAttr(parentAttrAssigns, attrAssign)"><el-icon><ArrowRight /></el-icon></el-link>
                    &nbsp;
                    <el-link size="small" @click="addSubAttr(attrAssign)"><el-icon><ArrowDown /></el-icon></el-link>
                </span>
            </span>
        </template>
        <loopTableColumnComponent v-if="attrAssign.attrAssigns != null" v-for="subAttrAssign in attrAssign.attrAssigns" :attrAssign="subAttrAssign" :parentAttrAssigns="attrAssign.attrAssigns" :funcRegion="funcRegion" @onAttrSelected="onAttrSelected" @deleteAttr="deleteAttr" @addSiblingAttr="addSiblingAttr" @addSubAttr="addSubAttr"/>
    </el-table-column> 
</template>
<script>
import { defineAsyncComponent } from 'vue';
import {reportCellValueFormat} from '../../js/common.js'

const loopTableColumnComponent = defineAsyncComponent(() => import('./loopTableColumnComponent.vue'));

export default {
    props: ['attrAssign','parentAttrAssigns', 'funcRegion'],
    
    emits: ['deleteAttr', 'onAttrSelected', 'addSiblingAttr', 'addSubAttr'],
    
    setup (props, {attrs, emit, slots}) {
        const onAttrSelected = (attrAssign) => {
            emit('onAttrSelected', attrAssign);
        };

        const deleteAttr = (srcObject, attr) => {
            emit('deleteAttr', srcObject, attr);
        };

        const addSiblingAttr = (srcObject, attr) => {
            emit('addSiblingAttr', srcObject, attr);
        };

        const addSubAttr = (attr) => {
            emit('addSubAttr', attr);
        };

        return {
        	onAttrSelected,
            deleteAttr,
            addSiblingAttr,
            addSubAttr
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
<style scoped>
.floatDeleteContainer{
    &:hover .floatDelete{
        display: block;  
    }
   
}
.floatDelete{
    display: none;
    float:left;
    margin-right: -70px; 
    margin-top: -7px;
    z-index: 999;
}

</style>