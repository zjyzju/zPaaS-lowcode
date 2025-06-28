<!-- 功能区域设计器-表格设计 -->
<template>          
    <el-row :gutter="4" class="dragArea">
        <el-col :span="4" style="z-index: 99;">
            <table>
                <tr>
                    <th colspan="1" style="text-align: left; padding: 12px 0;">操作</th>
                </tr>
                <tr>
                    <draggable v-model="funcRegion.regionOperations" :group="'table_button_' + attrAssignInfo.id" tag="td" item-key="id" v-if="funcRegion.regionOperations != null"  style="display: flex; flex-wrap: nowrap;z-index: 99;">
                        <template #item="{element : operation, index}">
                            <span v-if="operation.objectAssignId == assignObject.id || (funcRegion.tplRegion.regionType == 'MR' && (operation.objectAssignId == null || operation.objectAssignId ==''))" class="floatDeleteContainer"  @click="onOperationSelected(operation)">
                                <template v-if="operation.operationType != 'P' && operation.operationType != 'V'">
                                    <el-link type="primary"  size="small" @click="">{{operation.name}}</el-link>&nbsp;
                                    <el-link class="floatDelete" size="small" @click="deleteRegionOperation(index)"><el-icon><Delete /></el-icon></el-link>
                                </template>
                                <template v-if="operation.operationType == 'V'">
                                    <popupViewButtonComponent :operation="operation" :regionData="{}" :funcRegion="funcRegion" :funcDefine="{}" mainPage="Y"/>&nbsp;
                                    <el-link class="floatDelete" size="small" @click="deleteRegionOperation(index)"><el-icon><Delete /></el-icon></el-link>
                                </template>
                            </span>
                        </template>
                    </draggable>
                </tr>
            </table>
        </el-col>
        <el-col :span="20">
            <div  style="width: 100%; overflow-x: auto;">
                <table  v-if="attrAssignInfo.attrAssigns != null" :width="tableWidth">
                    <draggable v-model="attrAssignInfo.attrAssigns" :group="attrAssignInfo.id" tag="tr" item-key="id" >
                        <template #item="{element : attrAssign, index}">
                            <th :width="attrAssign.displayWidth"  class="floatDeleteContainer1" @click="onAttrSelected(attrAssign)" style="text-align: left; padding: 12px 0;">
                                {{ attrAssign.displayName }}&nbsp;&nbsp;&nbsp;&nbsp;
                                <el-link class="floatDelete1" size="small" @click="deleteAttr(attrAssignInfo, attrAssign)"><el-icon><Delete /></el-icon></el-link>
                            </th>
                        </template>
                    </draggable>
                    <tr>
                        <td :colspan="attrAssignInfo.attrAssigns.length">
                            &nbsp;
                        </td>
                    </tr>
                </table>
            </div>
        </el-col>
    </el-row> 
    <br/>
    <el-row :gutter="4" v-if="funcRegion.tplRegion.regionType == 'MR'">
        <el-col :span="24">
            <el-pagination  small background :total="20" :page-size="10" class="mt-4" />
        </el-col>
    </el-row> 
</template>
<script>
import draggable from 'vuedraggable'
import popupViewButtonComponent from '../../../funcTpl/components/common/popupViewButtonComponent.vue'

export default {
    props: ['attrAssignInfo','funcRegion', 'assignObject'],
    
    emits: ['deleteAttr', 'onChangeEnd', 'onAttrSelected', 'onOperationSelected', 'deleteRegionOperation'],
    
    setup (props, {attrs, emit, slots}) {
        const onChangeEnd = (event) => {
            emit('onChangeEnd', event);
        };

        const onAttrSelected = (attrAssign) => {
            emit('onAttrSelected', attrAssign);
        };

        const onOperationSelected = (regionOperation) => {
            emit('onOperationSelected', regionOperation);
        };

        const deleteAttr = (srcObject, attr) => {
            emit('deleteAttr', srcObject, attr);
        };

        const deleteRegionOperation = (index) => {
            emit('deleteRegionOperation', index);
        };

        return {
        	onChangeEnd,
            onAttrSelected,
            onOperationSelected,
            deleteAttr,
            deleteRegionOperation
        }
    },
    computed: {
        tableWidth: {
            get() {
                var width = 0;
                for(var i in this.attrAssignInfo.attrAssigns) {
                    if(this.attrAssignInfo.attrAssigns[i].displayWidth == null) {
                        width += 100;
                    }else {
                        width += parseInt(this.attrAssignInfo.attrAssigns[i].displayWidth);
                    }
                }
                return width;
            }
        }
    },
    components: {
    	draggable,
        popupViewButtonComponent
    },
    
    data() {   
       
    	return {
            
        }
    },
    mounted() {
    	if(this.attrAssignInfo == null || this.attrAssignInfo.attrAssigns == null || this.attrAssignInfo.attrAssigns.length == 0) {
            this.attrAssignInfo = [
                    {
                        id: this.assignObject.id,
                        attrAssigns:[
                            {
                                attribute: {code:''},
                                displayName: '请删除',
                                displayType: 'I'
                            }
                        ]
                    }
                ]
            
        }
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
    z-index: 999;
}
.floatDeleteContainer1{
    &:hover .floatDelete1{
        display: block;  
    }
}
.floatDelete1{
    display: none;
    float:left;
    margin-right: -14px; 
    margin-top: -10px;
    z-index: 999;
}
</style>