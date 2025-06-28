<template> 
    <el-collapse>
        <draggable v-model="objectAttrMapForSelect.undisplayObjectAttrs" tag="div" item-key="id" class="dragArea" >
            <template #item="{element : assignObject}">
                <el-collapse-item :title="assignObject.objectAssign.assignObject.name" :name="assignObject.id">
                    <draggable v-model="assignObject.undispalyAttrs" :group="assignObject.id" tag="div" item-key="id" class="el-row" style="margin-left: -2px; margin-right: -2px;" >
                        <template #item="{element : attrAssign, index}">
                            <el-tag :key="attrAssign.attribute.code" style='margin:5px;' type="success" effect="dark">
                                {{attrAssign.attribute.name + ": " + attrAssign.attribute.code}}
                            </el-tag>
                        </template>
                    </draggable>
                </el-collapse-item>
            </template>
        </draggable>
    </el-collapse>
</template>
<script>
import draggable from 'vuedraggable'

export default {
    props: ['objectAttrMapForSelect'],
    
    emits: [],
    
    setup (props, {attrs, emit, slots}) {
        
        return {
            
        }
    },
    computed: {
        
    },
    components: {
        draggable
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