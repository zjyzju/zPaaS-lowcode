<!-- 功能区域设计器-标题区 -->
<template>
    <el-row :gutter="4">
         <el-col :span="24">
             <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
             <div class="toolbar1"  style="justify-content: right; display: flex; flex-wrap: nowrap;">
                 <span class="title" v-if="funcRegion.tplRegion.regionType != 'MR'">{{assignObject.assignObject.name}}信息</span>
                 <span class="title" v-if="funcRegion.tplRegion.regionType == 'MR'">查询列表</span>
                 &nbsp;&nbsp;
                 <draggable tag="span" v-if="funcRegion.regionOperations != null" item-key="id" v-model="funcRegion.regionOperations" style="justify-content: right; display: flex; flex-wrap: nowrap;">
                    <template #item="{element : operation, index}">
                        <span class="floatDeleteContainer" v-if="operation.operationType == 'P' && (operation.objectAssignId == assignObject.id || (funcRegion.tplRegion.regionType == 'MR' && (operation.objectAssignId == null || operation.objectAssignId =='')))"  @click="onOperationSelected(operation)">
                            <el-link type="primary"  size="small" @click="">{{operation.name}}</el-link>
                            &nbsp;
                            <el-link class="floatDelete" size="small" @click="deleteRegionOperation(index)"><el-icon><Delete /></el-icon></el-link>
                        </span>
                    </template>
                </draggable>
             </div>
             </el-divider>
         </el-col>
     </el-row>
     <el-row :gutter="4">
         <el-col :span="24">
             &nbsp;
         </el-col>
     </el-row>
</template>
<script>
import draggable from 'vuedraggable'

export default {
    props: ['funcRegion', 'assignObject'],
    
    emits: ['deleteRegionOperation', 'onOperationSelected'],
    
    setup (props, {attrs, emit, slots}) {
        const deleteRegionOperation = (index) => {
            emit('deleteRegionOperation', index);
        };

        const onOperationSelected = (regionOperation) => {
            emit('onOperationSelected', regionOperation);
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
    margin-top: -11px;
    z-index: 99;
}
.toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
  height: 100%;
  right: 20px;
}
</style>