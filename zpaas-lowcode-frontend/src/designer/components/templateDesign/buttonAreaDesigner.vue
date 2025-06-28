<!-- 功能区域设计器-按钮区 -->
<template>
    <el-row :gutter="4">
        <el-col :span="8">
            &nbsp;
        </el-col>
        <el-col :span="15" style="justify-content: right; display: flex; flex-wrap: nowrap;" class="dragArea" >
            <draggable tag="span" v-if="funcRegion.regionOperations != null" item-key="id" v-model="funcRegion.regionOperations" style="justify-content: right; display: flex; flex-wrap: nowrap;">
                <template #item="{element : operation, index}">
                    <div class="floatDeleteContainer" v-if="operation.objectAssignId == assignObject.id || (operation.objectAssignId == null && (funcRegion.tplRegion.regionType.startsWith('M') || assignObject.assignType == 'M'))" @click="onOperationSelected(operation)">
                        <el-button type="primary" v-if="operation.operationType != 'N' && operation.operationType != 'V' && operation.operationType != 'R'"  size="small">{{operation.name}}</el-button>
                        <el-button size="small" v-if="operation.operationType == 'N' || operation.operationType == 'R'">{{operation.name}}</el-button>
                        <popupViewButtonComponent v-if="operation.operationType == 'V'" :operation="operation" :regionData="{}" :funcRegion="funcRegion" :funcDefine="{}" :mainPage="true"/>
                        &nbsp;&nbsp;
                        <el-link class="floatDelete" size="small" @click="deleteRegionOperation(index)"><el-icon><Delete /></el-icon></el-link>
                    </div>
                </template>
            </draggable>
        </el-col>
        <el-col :span="1">&nbsp;</el-col>
    </el-row>
</template>
<script>
import draggable from 'vuedraggable'
import popupViewButtonComponent from '../../../funcTpl/components/common/popupViewButtonComponent.vue'

export default {
    props: ['funcRegion', 'assignObject'],
    
    emits: ['deleteRegionOperation',  'onOperationSelected'],
    
    setup (props, {attrs, emit, slots}) {
        const onOperationSelected = (regionOperation) => {
            emit('onOperationSelected', regionOperation);
        };

        const deleteRegionOperation = (index) => {
            emit('deleteRegionOperation', index);
        };

        return {
        	onOperationSelected,
            deleteRegionOperation
        }
    },
    computed: {
        
    },
    components: {
    	draggable,
        popupViewButtonComponent,
    },
    
    data() {   
        
    	return {
            
        }
    },
    mounted() {
    	
    },
    methods: {
    	
    }
    
 }
</script>
<style scoped>
.dragArea {
  outline: 1px dashed;
  width:100%;
  outline-color: darkgray;
}

.floatDeleteContainer{
    &:hover .floatDelete{
        display: block;  
    }
}
.floatDelete{
    display: none;
    float: left;
    margin-right: -15px; 
    margin-top: -15px;
    z-index: 999;
}
</style>