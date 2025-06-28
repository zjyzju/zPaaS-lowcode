<!-- 功能区域设计器-表格容器 -->
<template>          
<div class="dragArea">
    <el-row :gutter="4">
        <el-col :span="24">
            <span class="floatDeleteTableContainer">
                <el-icon @click="onCustomizedLayoutSelected(tableComponent)"><Expand /></el-icon>
                <el-link class="floatDeleteTable" size="small" @click="deleteContainer(this.componentIndex)"><el-icon><Delete /></el-icon></el-link>
            </span>
        </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4" v-if="tableComponent.layoutCfgJSON != null && tableComponent.layoutCfgJSON.showTitleArea == 'Y'">
        <el-col :span="24">
            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
            <div  class="toolbar1">
                <span :id="tableComponent.name" class="title">{{ tableComponent.name }}</span>
                
                &nbsp;&nbsp;
                <draggable tag="span" group="buttonContainer" @change="onChange" item-key="id" v-model="tableComponent.titleButtonComponents" style="justify-content: right; display: flex; flex-wrap: nowrap; min-height: 15px; min-width: 15px;">
                   <template #item="{element : operation, index}">
                       <span class="floatDeleteContainer"  @click="onOperationSelected(operation)">
                           <template v-if="operation.operationType == 'V'">
                               <popupViewButtonComponent :operation="operation" :regionData="{}" :funcRegion="{}" :funcDefine="{}" mainPage="Y"/>&nbsp;
                               <el-link class="floatDelete" size="small" @click="deleteTitleOperation(index)"><el-icon><Delete /></el-icon></el-link>
                           </template>
                           <template v-else>
                               <el-link :type="(operation.operationCfgJSON == null || operation.operationCfgJSON.displayColor == null) ? 'primary' : operation.operationCfgJSON.displayColor" @click="">{{operation.name}}</el-link>&nbsp;
                               <el-link class="floatDelete" size="small" @click="deleteTitleOperation(index)"><el-icon><Delete /></el-icon></el-link>
                           </template>
                       </span>
                   </template>
               </draggable>
            </div>
            </el-divider>
        </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="1" style="z-index: 99;"  v-if="tableComponent.layoutCfgJSON != null && tableComponent.layoutCfgJSON.selectMode == 'M'">
            <table>
                <tr>
                    <th colspan="1" style="text-align: left; padding: 12px 0;">
                        <el-checkbox v-model="checked1" label="" size="small" />
                    </th>
                </tr>
                <tr>
                    <td colspan="1" style="display: flex; flex-wrap: nowrap;z-index: 99; min-height: 15px; min-width: 15px;">
                        <el-checkbox v-model="checked2" label="" size="small" />
                    </td>
                </tr>
            </table>
        </el-col>
        <el-col :span="4" style="z-index: 99;"  v-if="tableComponent.layoutCfgJSON != null && tableComponent.layoutCfgJSON.showOperationCol == 'Y'">
            <table>
                <tr>
                    <th colspan="1" style="text-align: left; padding: 12px 0;">操作</th>
                </tr>
                <tr>
                    <draggable v-model="tableComponent.buttonComponents"  @change="onChange" group="buttonContainer" tag="td" item-key="id"   style="display: flex; flex-wrap: nowrap;z-index: 99; min-height: 15px; min-width: 15px;">
                        <template #item="{element : operation, index}">
                            <span class="floatDeleteContainer"  @click="onOperationSelected(operation)">
                                <template v-if="operation.operationType == 'V'">
                                    <popupViewButtonComponent :operation="operation" :regionData="{}" :funcRegion="{}" :funcDefine="{}" mainPage="Y"/>&nbsp;
                                    <el-link class="floatDelete" size="small" @click="deleteRegionOperation(index)"><el-icon><Delete /></el-icon></el-link>
                                </template>
                                <template v-else>
                                    <el-link :type="(operation.operationCfgJSON == null || operation.operationCfgJSON.displayColor == null) ? 'primary' : operation.operationCfgJSON.displayColor" @click="">{{operation.name}}</el-link>&nbsp;
                                    <el-link class="floatDelete" size="small" @click="deleteRegionOperation(index)"><el-icon><Delete /></el-icon></el-link>
                                </template>
                            </span>
                        </template>
                    </draggable>
                </tr>
            </table>
        </el-col>
        <el-col :span="19 + 4*(tableComponent.layoutCfgJSON.showOperationCol == 'Y'? 0 : 1) + (tableComponent.layoutCfgJSON.selectMode == 'M'? 0 : 1)" v-if="tableComponent.layoutCfgJSON != null">
            <div  style="width: 100%; overflow-x: auto;">
                <table  v-if="tableComponent.layoutRegions != null && tableComponent.layoutRegions[0] != null && tableComponent.layoutRegions[0].funcRegion.regionAttrAssigns != null" :width="tableWidth">
                    <draggable v-model="tableComponent.layoutRegions[0].funcRegion.regionAttrAssigns" @change="onAttrChange" group="formContainer" tag="tr" item-key="id" style="height: 15px;">
                        <template #item="{element : attrAssign, index}">
                            <th :width="attrAssign.displayWidth"  class="floatDeleteContainer1" @click="onAttrSelected(attrAssign)" style="text-align: left; padding: 12px 0;">
                                {{ attrAssign.displayName }}&nbsp;&nbsp;&nbsp;&nbsp;
                                <el-link class="floatDelete1" size="small" @click="deleteAttr(attrAssign)"><el-icon><Delete /></el-icon></el-link>
                            </th>
                        </template>
                    </draggable>
                    <tr>
                        <td :colspan="tableComponent.layoutRegions[0].funcRegion.regionAttrAssigns.length">
                            &nbsp;
                        </td>
                    </tr>
                </table>
            </div>
        </el-col>
    </el-row> 
    <br/>
    <el-row :gutter="4"  v-if="tableComponent.layoutCfgJSON != null && tableComponent.layoutCfgJSON.showPageArea == 'Y'">
        <el-col :span="24">
            <el-pagination  small background :total="20" :page-size="10" class="mt-4" />
        </el-col>
    </el-row> 
</div>
</template>
<script>
import { ref} from 'vue';
import { ElMessageBox} from 'element-plus'
import draggable from 'vuedraggable'
import popupViewButtonComponent from '../../../funcTpl/components/common/popupViewButtonComponent.vue'

export default {
    props: ['componentIndex', 'tableComponent'],
    
    emits: ['onAttrSelected', 'deleteContainer', 'onOperationSelected', 'onCustomizedLayoutSelected'],
    
    setup (props, {attrs, emit, slots}) {
        const onAttrSelected = (attrAssign) => {
            emit('onAttrSelected', attrAssign);
        };

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
            onAttrSelected,
            onOperationSelected,
            onCustomizedLayoutSelected
        }
    },
    computed: {
        tableWidth: {
            get() {
                var width = 0;
                for(var i in this.tableComponent.layoutRegions[0].funcRegion.regionAttrAssigns) {
                    if(this.tableComponent.layoutRegions[0].funcRegion.regionAttrAssigns[i].displayWidth == null) {
                        width += 100;
                    }else {
                        width += parseInt(this.tableComponent.layoutRegions[0].funcRegion.regionAttrAssigns[i].displayWidth);
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
        const checked1 = ref(null);
        const checked2 = ref(null);
       
    	return {
            checked1,
            checked2
        }
    },
    mounted() {
    	if(this.tableComponent.layoutRegions == null) {
            this.tableComponent.layoutRegions=[];
        }
        if(this.tableComponent.layoutRegions.length == 0) {
            this.tableComponent.layoutRegions.push({
                id: null,
                funcId: this.tableComponent.funcId,
                pageId: this.tableComponent.pageId,
                layoutId: this.tableComponent.id,
                regionId: null,
                systemId: this.tableComponent.systemId,
                tenantId: this.tableComponent.tenantId,
                funcRegion: {
                    id: null,
                    funcId: this.tableComponent.funcId,
                    systemId: this.tableComponent.systemId,
                    tenantId: this.tableComponent.tenantId,
                    regionAttrAssigns: [],
                    regionOperations: []
                }
            });
        }
        this.tableComponent.buttonComponents=[];
        this.tableComponent.titleButtonComponents=[];
       
        if(this.tableComponent.layoutRegions[0].funcRegion.regionOperations.length > 0) {
            for(var index in this.tableComponent.layoutRegions[0].funcRegion.regionOperations) {
                var regionOperation = this.tableComponent.layoutRegions[0].funcRegion.regionOperations[index];
                var regionOperationJSON = null;
                if(regionOperation.operationCfg != null && regionOperation.operationCfg.trim().length > 0) {
                    regionOperationJSON = JSON.parse(regionOperation.operationCfg);
                }else {
                    regionOperationJSON = {};
                }
                regionOperation.operationCfgJSON = regionOperationJSON;
                if(regionOperationJSON != null && regionOperationJSON.displayPosition == "2") {//表头
                    this.tableComponent.titleButtonComponents.push(regionOperation);
                }else {
                    this.tableComponent.buttonComponents.push(regionOperation);
                }

            }
        }

        var layoutCfgJSON = null;
        if(this.tableComponent.layoutCfg == null || this.tableComponent.layoutCfg.trim().length == 0) {
            layoutCfgJSON = {};
        }else {
            layoutCfgJSON = JSON.parse(this.tableComponent.layoutCfg);
        }
        if(layoutCfgJSON.showTitleArea == null || layoutCfgJSON.showTitleArea.trim().length == 0) {
            layoutCfgJSON.showTitleArea = 'Y';
        }
        if(layoutCfgJSON.showPageArea == null || layoutCfgJSON.showPageArea.trim().length == 0) {
            layoutCfgJSON.showPageArea = 'Y';
        }
        if(layoutCfgJSON.showOperationCol == null || layoutCfgJSON.showOperationCol.trim().length == 0) {
            layoutCfgJSON.showOperationCol = 'Y';
        }
        this.tableComponent.layoutCfgJSON = layoutCfgJSON;
        this.tableComponent.layoutCfg = JSON.stringify(layoutCfgJSON);
    },
    methods: {
        onChange(event) {
            var buttons = [];
            var displayOrder = 1
            for(var index in this.tableComponent.titleButtonComponents) {
                var regionOperationJSON = null;
                if(this.tableComponent.titleButtonComponents[index].operationCfg == null || this.tableComponent.titleButtonComponents[index].operationCfg.trim().length == 0) {
                    regionOperationJSON = {};
                }else {
                    regionOperationJSON = JSON.parse(this.tableComponent.titleButtonComponents[index].operationCfg);
                }
                regionOperationJSON.displayPosition = "2";
                this.tableComponent.titleButtonComponents[index].operationCfg = JSON.stringify(regionOperationJSON);
                this.tableComponent.titleButtonComponents[index].displayOrder = displayOrder++;
                buttons.push(this.tableComponent.titleButtonComponents[index]);
            }

            for(var index in this.tableComponent.buttonComponents) {
                var regionOperationJSON = null;
                if(this.tableComponent.buttonComponents[index].operationCfg == null || this.tableComponent.buttonComponents[index].operationCfg.trim().length == 0) {
                    regionOperationJSON = {};
                }else {
                    regionOperationJSON = JSON.parse(this.tableComponent.buttonComponents[index].operationCfg);
                }
                regionOperationJSON.displayPosition = "1";
                this.tableComponent.buttonComponents[index].operationCfg = JSON.stringify(regionOperationJSON);
                this.tableComponent.buttonComponents[index].displayOrder = displayOrder++;
                buttons.push(this.tableComponent.buttonComponents[index]);
            }
            
            this.tableComponent.layoutRegions[0].funcRegion.regionOperations = buttons;
        },
        onAttrChange(event) {
            for(var i in this.tableComponent.layoutRegions[0].funcRegion.regionAttrAssigns) {
                this.tableComponent.layoutRegions[0].funcRegion.regionAttrAssigns[i].displayOrder = (i+1);
            }
        },
    	deleteAttr(attr) {
            console.log(attr);
            for(var i in this.tableComponent.layoutRegions[0].funcRegion.regionAttrAssigns) {
                var attrAssignsRow = this.tableComponent.layoutRegions[0].funcRegion.regionAttrAssigns[i];
                if(attrAssignsRow == attr) {
                    this.tableComponent.layoutRegions[0].funcRegion.regionAttrAssigns.splice(i, 1);
                    this.onAttrChange(null);
                    break;
                }
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
                    this.tableComponent.buttonComponents.splice(index,1);
                    this.onChange(null);
                });
        },
        deleteTitleOperation(index) {
            ElMessageBox.confirm(
                    '该按钮将被删除. 是否继续?',
                    '警告',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning',
                    }
                ).then(() => {
                    this.tableComponent.titleButtonComponents.splice(index,1);
                    this.onChange(null);
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
    min-height: 5em;
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
.floatDeleteTable{
    display: none;
    float: right;
    margin-right: 98%; 
    margin-top: -33px;
    z-index: 99;
}

.floatDeleteTableContainer{
    &:hover .floatDeleteTable{
        display: block;  
    }
}

.toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
  height: 100%;
  right: 20px;
}
</style>