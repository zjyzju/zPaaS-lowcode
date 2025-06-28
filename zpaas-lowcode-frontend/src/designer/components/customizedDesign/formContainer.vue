<!-- 功能区域设计器-表单容器 -->
<template>
<div class="dragArea" :id="formComponent.name">
    <el-form :label-width="(formComponent.layoutCfgJSON != null && formComponent.layoutCfgJSON.labelWidth != null) ? formComponent.layoutCfgJSON.labelWidth+'px' : '100px'">
    <draggable v-model="formComponent.attrAssigns" tag="div" item-key="rowIndex">
        <template #header>
            <span class="floatDeleteFormContainer">
                <el-icon @click="onCustomizedLayoutSelected(formComponent)"><grid /></el-icon>
                <el-link class="floatDeleteForm" size="small" @click="deleteContainer(this.componentIndex)"><el-icon><Delete /></el-icon></el-link>
            </span>
        </template>
        <template #item="{element : attrAssignRow}">
            <draggable v-model="attrAssignRow.attrAssigns" @change="onChange" group="formContainer" tag="div" item-key="attributeId" class="el-row" style="margin-left: -2px; margin-right: -2px; min-height: 1em" >
                <template #item="{element : attrAssign, index}">
                    <el-col :span="attrAssign.colSpan==null?0:parseInt(attrAssign.colSpan)" class="floatDeleteContainer"  @click="onAttrSelected(attrAssign)">
                        <el-form-item :label="attrAssign.displayName" :required="attrAssign.required == 'Y'" :prop="attrAssign.attribute.code">
                            <displayComponent :funcRegion="{}" :funcDefine="{}" :regionData="{}" :attrAssign="attrAssign" :subObject="null"/>
                        </el-form-item>
                        <el-link class="floatDelete" size="small" @click="deleteAttr(attrAssign)"><el-icon><Delete /></el-icon></el-link>
                    </el-col>
                </template>
            </draggable>
        </template>
    </draggable>
    </el-form>
</div>
</template>

<script>
import draggable from 'vuedraggable'
import displayComponent from '../common/displayComponent.vue'

export default {
    props: ['componentIndex', 'formComponent'],
    
    emits: ['onAttrSelected', 'deleteContainer', 'onCustomizedLayoutSelected'],
    
    setup (props, {attrs, emit, slots}) {
        const onAttrSelected = (attrAssign) => {
            emit('onAttrSelected', attrAssign);
        };

        const onCustomizedLayoutSelected = (customizedLayout) => {
            emit('onCustomizedLayoutSelected', customizedLayout);
        };

        const deleteContainer = (componentIndex) => {
            emit('deleteContainer', componentIndex);
        };

        return {
        	onAttrSelected,
            onCustomizedLayoutSelected,
            deleteContainer
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
        if(this.formComponent.layoutCfg != null && this.formComponent.layoutCfg.trim().length > 0 && this.formComponent.layoutCfgJSON == null) {
            this.formComponent.layoutCfgJSON = JSON.parse(this.formComponent.layoutCfg);
        }

        this.formatDisplay();
    },
    methods: {
        onChange(event) {
            this.reformatDisplayInfo();
        },
        reformatDisplayInfo() {
            var subComponents = [];
            this.reconstructSubComponents(subComponents);
            this.formComponent.layoutRegions[0].funcRegion.regionAttrAssigns = subComponents;

            this.formatDisplay();
        },
        reconstructSubComponents(subComponents) {//重建subComponents列表
            var index = 1;
            //处理绑定属性
            for(var i in this.formComponent.attrAssigns) {
                if(this.formComponent.attrAssigns[i].rowIndex != null) {
                    for(var j in this.formComponent.attrAssigns[i].attrAssigns) {
                        this.formComponent.attrAssigns[i].attrAssigns[j].displayOrder = index++;
                        subComponents.push(this.formComponent.attrAssigns[i].attrAssigns[j]);
                    }
                }else {
                    this.formComponent.attrAssigns[i].displayOrder = index++;
                    subComponents.push(this.formComponent.attrAssigns[i]);
                }
            }
        },
        formatDisplay(){
            if(this.formComponent.layoutRegions == null) {
                this.formComponent.layoutRegions=[];
            }
            if(this.formComponent.layoutRegions.length == 0) {
                this.formComponent.layoutRegions.push({
                    id: null,
                    funcId: this.formComponent.funcId,
                    pageId: this.formComponent.pageId,
                    layoutId: this.formComponent.id,
                    regionId: null,
                    systemId: this.formComponent.systemId,
                    tenantId: this.formComponent.tenantId,
                    funcRegion: {
                        id: null,
                        funcId: this.formComponent.funcId,
                        systemId: this.formComponent.systemId,
                        tenantId: this.formComponent.tenantId,
                        regionAttrAssigns: []
                    }
                });
            }
            
            //格式化表单展示
            var attrAssignMap = {};
            attrAssignMap = this.formatAttrDisplayInfo(this.formComponent.layoutRegions[0].funcRegion.regionAttrAssigns);
            

            //生成表单属性列表（分行）
            var attrAssignList = [];
            if(this.formComponent.layoutRegions[0].funcRegion.regionAttrAssigns.length == 0) {
                attrAssignList.push({rowIndex: 0, attrAssigns:[]});
            }else {
                for(var key in attrAssignMap) {
                    if(attrAssignMap[key] != null && attrAssignMap[key].length != 0) {
                        var attrAssignRow = {
                            rowIndex: key,
                            attrAssigns: attrAssignMap[key]
                        };
                        attrAssignList.push(attrAssignRow);
                    }
                }
            }
            
            this.formComponent.attrAssigns = attrAssignList;
        },
        deleteAttr(attr) {
            for(var i in this.formComponent.attrAssigns) {
                var attrAssignsRow = this.formComponent.attrAssigns[i];
                if(attrAssignsRow.rowIndex != null) {
                    for(var j in attrAssignsRow.attrAssigns) {
                        var attrAssign = attrAssignsRow.attrAssigns[j];
                        if(attrAssign == attr) {
                            attrAssignsRow.attrAssigns.splice(j, 1);
                            break;
                        }
                    }
                }else {
                    if(attrAssignsRow == attr) {
                        this.formComponent.attrAssigns.splice(i, 1);
                        break;
                    }
                }
            }

            this.reformatDisplayInfo();
        },
        formatAttrDisplayInfo(attrAssigns) {
            var row = 0;
            var attrMap = {};
            var columnCountOfRow = 0;
            attrMap[0] = [];
            for (var index = 0; index < attrAssigns.length; index++) {
                //规范化一些为空的属性
                if (attrAssigns[index].colSpan == null || attrAssigns[index].colSpan == 0 || attrAssigns[index].colSpan == '0') {
                    if (attrAssigns[index].displayType == 'H') {
                        attrAssigns[index].colSpan = 0;
                    } else {
                        attrAssigns[index].colSpan = 8;
                    }
                } else {
                    attrAssigns[index].colSpan = parseInt(attrAssigns[index].colSpan);
                }
                
                if (attrAssigns[index].displayType == 'H') {
                    attrAssigns[index].colSpan = 0;
                } else if (attrAssigns[index].colSpan > 24) {//当某个属性的colspan超过24时，独占一行
                    attrAssigns[index].colSpan = 24;//colspan最长支持24
                }
                
                if (attrAssigns[index].rowSpan == null || attrAssigns[index].rowSpan == 0  || attrAssigns[index].rowSpan == '0') {
                    attrAssigns[index].rowSpan = 1;
                }else {
                    attrAssigns[index].rowSpan = parseInt(attrAssigns[index].rowSpan);
                }

                //当前行放不下的情况，需要先进行换行操作
                if (columnCountOfRow != 0 && (columnCountOfRow + attrAssigns[index].colSpan) > 24) {
                    attrAssigns[index - 1].colSpan += (24 - columnCountOfRow)//使前一个属性的colspan占满剩下的空间
                    columnCountOfRow = 24;//进行换行操作
                    if (columnCountOfRow != 0 && columnCountOfRow % 24 == 0) {
                        columnCountOfRow = 0;
                        row++;
                        attrMap[row] = [];
                    }
                }
                //处理当前的属性
                attrMap[row].push(attrAssigns[index]);
                columnCountOfRow = columnCountOfRow + attrAssigns[index].colSpan;
                //进行分行判断
                if (columnCountOfRow != 0 && columnCountOfRow % 24 == 0) {
                    columnCountOfRow = 0;
                    row++;
                    attrMap[row] = [];
                }
            }
            return attrMap;
        }
    }
    
 }
</script>
<style scoped>
.dragArea {
    outline: 1px dashed;
    width:99%;
    outline-color: darkgray;
    min-height: 6em;
    display: block;
    margin: 0 auto;
    margin-top: 0.5ch;
    margin-bottom: 0.5ch;
    padding-top: 0.5ch;
    padding-bottom: 0.5ch;
    padding-left: 0.5ch;
}
.formContainer {
  width:99%;
  min-height: 3em;
  display: block;
  margin: 0 auto;
  margin-top: 0.5ch;
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
.floatDeleteForm{
    display: none;
    float: right;
    margin-right: 98%; 
    margin-top: -33px;
    z-index: 99;
}

.floatDeleteFormContainer{
    &:hover .floatDeleteForm{
        display: block;  
    }
}
</style>