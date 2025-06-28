<!-- 功能区域设计器-表单设计 -->
<template>
    <draggable v-model="attrAssignInfo.attrAssigns" tag="div" item-key="rowIndex" class="dragArea" >
        <template #item="{element : attrAssignRow}">
                <draggable v-model="attrAssignRow.attrAssigns" @end="onChangeEnd" :group="assignObject.id" tag="div" item-key="attributeId" class="el-row" style="margin-left: -2px; margin-right: -2px;" >
                    <template #item="{element : attrAssign, index}">
                        <el-col :span="8*attrAssign.colSpan" class="floatDeleteContainer"  @click="onAttrSelected(attrAssign)">
                            <el-form-item :label="attrAssign.displayName" :required="attrAssign.required == 'Y'" :prop="attrAssign.attribute.code">
                                <displayComponent :funcRegion="funcRegion" :funcDefine="{}" :regionData="{}" :attrAssign="attrAssign" :subObject="null"/>
                            </el-form-item>
                            <el-link class="floatDelete" size="small" @click="deleteAttr(attrAssignInfo, attrAssign)"><el-icon><Delete /></el-icon></el-link>
                        </el-col>
                    </template>
                </draggable>
        </template>
    </draggable>
</template>

<script>
import draggable from 'vuedraggable'
import displayComponent from '../common/displayComponent.vue'

export default {
    props: ['attrAssignInfo','funcRegion', 'assignObject'],
    
    emits: ['deleteAttr', 'onChangeEnd', 'onAttrSelected'],
    
    setup (props, {attrs, emit, slots}) {
        const onChangeEnd = (event) => {
            emit('onChangeEnd', event);
        };

        const onAttrSelected = (attrAssign) => {
            emit('onAttrSelected', attrAssign);
        };

        const deleteAttr = (srcObject, attr) => {
            emit('deleteAttr', srcObject, attr);
        };

        return {
        	onChangeEnd,
            onAttrSelected,
            deleteAttr
        }
    },
    computed: {
        
    },
    components: {
    	draggable,
        displayComponent
    },
    
    data() {   
        

    	return {
            
        }
    },
    mounted() {
    	if(this.attrAssignInfo == null || this.attrAssignInfo.attrAssigns == null || this.attrAssignInfo.attrAssigns.length == 0) {
            this.attrAssignInfo.attrAssigns = [
                {
                    rowIndex: 0,
                    attrAssigns:[
                        {
                            attribute: {code:''},
                            displayName: '请删除',
                            displayType: 'I',
                            colSpan: '1'
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
    margin-left: 95.5%; 
    margin-top: -42px;
    z-index: 99;
}
</style>