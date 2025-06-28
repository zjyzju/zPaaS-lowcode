<!-- 功能区域设计器-表单容器 -->
<template>
    <el-row :gutter="4" v-if="customizedLayout.attrAssigns != null" v-for="(attrAssignsRow, key) in customizedLayout.attrAssigns">
        <el-col :span="attrAssign.colSpan==null?0:parseInt(attrAssign.colSpan)" v-for="(attrAssign, index) in attrAssignsRow.attrAssigns">
            <el-form-item :label="attrAssign.displayName" :required="attrAssign.required == 'Y'" :prop="attrAssign.attribute.code" :label-width="(customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.labelWidth != null) ? customizedLayout.layoutCfgJSON.labelWidth+'px' : '100px'" style="width: 98%;">
                <attrDisplayComponent :funcRegion="customizedLayout.layoutRegions[0].funcRegion" :funcDefine="funcDefine" :regionData="regionData" :attrAssign="attrAssign" :subObject="(customizedLayout.layoutCfgJSON != null && customizedLayout.layoutCfgJSON.formDataSourceAttr != null && regionData[customizedLayout.layoutCfgJSON.formDataSourceAttr] != null)? funcDefine.objectMap[attrAssign.objectId] : null"/>
            </el-form-item>
        </el-col>
    </el-row>
</template>

<script>
import {ElMessage } from 'element-plus'
import { ref } from 'vue';
import attrDisplayComponent from '../common/attrDisplayComponent.vue'

export default {
    props: [ 'customizedLayout', 'customizedPage', 'funcDefine', 'regionData', 'parentPage', 'srcOperation'],
    
    emits: [],
    
    setup (props, {attrs, emit, slots}) {
        // const onAttrSelected = (attrAssign) => {
        //     emit('onAttrSelected', attrAssign);
        // };

        

        return {
        	//deleteContainer
        }
    },
    components: {
    	attrDisplayComponent
    },
    data() {   
        const validateRules = ref(null);

    	return {
            validateRules
        }
    },
    mounted() {
        console.log(this.customizedLayout);

        if(this.customizedLayout.layoutCfgJSON != null && this.customizedLayout.layoutCfgJSON.formDataSourceObject != null && this.customizedLayout.layoutCfgJSON.formDataSourceAttr != null) {
            if(this.regionData[this.customizedLayout.layoutCfgJSON.formDataSourceAttr] == null) {
                for(var i in this.funcDefine.objectAssigns) {
                    if(this.funcDefine.objectAssigns[i].id == this.customizedLayout.layoutCfgJSON.formDataSourceObject) {
                        var assignObject = this.funcDefine.objectAssigns[i];
                        for(var j in assignObject.attributes) {
                            if(assignObject.attributes[j].code == this.customizedLayout.layoutCfgJSON.formDataSourceAttr) {
                                if(assignObject.attributes[j].isListAttr == 'Y') {
                                    this.regionData[this.customizedLayout.layoutCfgJSON.formDataSourceAttr] = [];
                                }else {
                                    this.regionData[this.customizedLayout.layoutCfgJSON.formDataSourceAttr] = {};
                                }
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }

        //处理预校验规则
        // if(this.customizedLayout.layoutRegions[0].funcRegion != null && this.customizedLayout.layoutRegions[0].funcRegion.regionOperations != null) {
        //     this.validateRules = processPreValidateRules(this.customizedLayout.layoutRegions[0].funcRegion.regionOperations, null);
        // }

        this.formatDisplay();
    },
    methods: {
        formatDisplay(){
            if(this.customizedLayout.layoutRegions == null || this.customizedLayout.layoutRegions.length == 0 || this.customizedLayout.layoutRegions[0].funcRegion == null) {
                ElMessage.error("not found layoutRegion info!");
            }
            
            //格式化表单展示
            var attrAssignMap = {};
            attrAssignMap = this.formatAttrDisplayInfo(this.customizedLayout.layoutRegions[0].funcRegion.regionAttrAssigns);
            

            //生成表单属性列表（分行）
            var attrAssignList = [];
            if(this.customizedLayout.layoutRegions[0].funcRegion.regionAttrAssigns.length > 0) {
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
            
            this.customizedLayout.attrAssigns = attrAssignList;
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

</style>