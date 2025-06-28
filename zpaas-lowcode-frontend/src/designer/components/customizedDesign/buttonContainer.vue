<!-- 功能区域设计器-按钮容器 -->
<template>          
<div class="dragArea">
    <el-row :gutter="4">
        <el-col :span="24">
            <span class="floatDeleteButtonContainer">
                <el-icon @click="onCustomizedLayoutSelected(buttonComponent)"><CircleCheckFilled /></el-icon>
                <el-link class="floatDeleteButton" size="small" @click="deleteContainer(this.componentIndex)"><el-icon><Delete /></el-icon></el-link>
            </span>
        </el-col>
    </el-row>
    
    <el-row :gutter="4">
        <el-col :span="1"></el-col>
        <el-col :span="22">
            <div class="toolbar1"  style="justify-content: right; display: flex; flex-wrap: nowrap;">
                <draggable tag="span" group="buttonContainer" item-key="id" v-model="buttonComponent.layoutRegions[0].funcRegion.regionOperations" @change="onChange" style="justify-content: right; display: flex; flex-wrap: nowrap; min-height: 15px; width: 100%;" v-if="buttonComponent.layoutRegions != null && buttonComponent.layoutRegions[0] != null">
                    <template #item="{element : operation, index}">
                        <span class="floatDeleteContainer"  @click="onOperationSelected(operation)">
                            <template v-if="operation.operationType == 'V'">
                                <popupViewButtonComponent :operation="operation" :regionData="{}" :funcRegion="{}" :funcDefine="{}" mainPage="Y"/>&nbsp;
                                <el-link class="floatDelete" size="small" @click="deleteRegionOperation(index)"><el-icon><Delete /></el-icon></el-link>
                            </template>
                            <template v-else>
                                <el-button :type="(operation.operationCfgJSON == null || operation.operationCfgJSON.displayColor == null) ? 'primary' : operation.operationCfgJSON.displayColor"  :size="(operation.operationCfgJSON == null || operation.operationCfgJSON.displaySize == null) ? 'small' : operation.operationCfgJSON.displaySize" @click="" :round="operation.operationCfgJSON != null && operation.operationCfgJSON.displayStyle == 'round'" :plain="operation.operationCfgJSON != null && operation.operationCfgJSON.displayStyle == 'plain'" :circle="operation.operationCfgJSON != null && operation.operationCfgJSON.displayStyle == 'circle'" :link="operation.operationCfgJSON != null && operation.operationCfgJSON.displayStyle == 'link'">{{operation.name}}</el-button>&nbsp;
                                <el-link class="floatDelete" size="small" @click="deleteRegionOperation(index)"><el-icon><Delete /></el-icon></el-link>
                            </template>
                        </span>
                    </template>
                </draggable>
            </div>
        </el-col>
        <el-col :span="1"></el-col>
     </el-row>
</div>
</template>
<script>
import {ElMessageBox} from 'element-plus'
import draggable from 'vuedraggable'
import popupViewButtonComponent from '../../../funcTpl/components/common/popupViewButtonComponent.vue'

export default {
    props: ['componentIndex', 'buttonComponent'],
    
    emits: ['deleteContainer', 'onOperationSelected', 'onCustomizedLayoutSelected'],
    
    setup (props, {attrs, emit, slots}) {
        const onOperationSelected = (operation) => {
            emit('onOperationSelected', operation);
        };

        const onCustomizedLayoutSelected = (customizedLayout) => {
            emit('onCustomizedLayoutSelected', customizedLayout);
        };

        const deleteContainer = (index) => {
            emit('deleteContainer', index);
        };

        return {
        	deleteContainer,
            onOperationSelected,
            onCustomizedLayoutSelected
        }
    },
    computed: {
        
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
    	if(this.buttonComponent.layoutRegions == null) {
            this.buttonComponent.layoutRegions=[];
        }
        if(this.buttonComponent.layoutRegions.length == 0) {
            this.buttonComponent.layoutRegions.push({
                id: null,
                funcId: this.buttonComponent.funcId,
                pageId: this.buttonComponent.pageId,
                layoutId: this.buttonComponent.id,
                regionId: null,
                systemId: this.buttonComponent.systemId,
                tenantId: this.buttonComponent.tenantId,
                funcRegion: {
                    id: null,
                    funcId: this.buttonComponent.funcId,
                    systemId: this.buttonComponent.systemId,
                    tenantId: this.buttonComponent.tenantId,
                    regionOperations: []
                }
            });
        }

        for(var i in this.buttonComponent.layoutRegions[0].funcRegion.regionOperations) {
            var operation = this.buttonComponent.layoutRegions[0].funcRegion.regionOperations[i];
            if(operation.operationCfg == null || operation.operationCfg.trim().length == 0) {
                operation.operationCfgJSON = {};
            }else {
                operation.operationCfgJSON = JSON.parse(operation.operationCfg);
            }
        }
    },
    methods: {
        onChange(event) {
            for(var i in this.buttonComponent.layoutRegions[0].funcRegion.regionOperations) {
                this.buttonComponent.layoutRegions[0].funcRegion.regionOperations[i].displayOrder = (i+1);
            }
        },
    	deleteRegionOperation(index) {
            ElMessageBox.confirm(
                    '该按钮将被删除. 是否继续?',
                    '警告',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning',
                    }
                ).then(() => {
                    this.buttonComponent.layoutRegions[0].funcRegion.regionOperations.splice(index,1);
                });
        }
    }
    
 }
</script>
<style scoped>
.dragArea {
    outline: 1px dashed;
    width:99%;
    outline-color: darkgray;
    min-height: 3em;
    display: block;
    margin: 0 auto;
    margin-top: 0.5ch;
    margin-bottom: 0.5ch;
    padding-top: 0.5ch;
    padding-bottom: 0.5ch;
    padding-left: 0.5ch;
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
.floatDeleteButton{
    display: none;
    float: right;
    margin-right: 98%; 
    margin-top: -33px;
    z-index: 99;
}

.floatDeleteButtonContainer{
    &:hover .floatDeleteButton{
        display: block;  
    }
}
</style>