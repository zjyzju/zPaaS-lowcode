<template> 
    <el-collapse v-if="detailTypeMap != null">
        <draggable v-model="objectAttrMapForSelect.undisplayObjectAttrs" tag="div" item-key="id" class="dragArea" >
            <template #item="{element : assignObject}">
                <el-collapse-item v-if="assignObject.objectAssign.assignObject != null" :title="assignObject.objectAssign.assignObject.name" :name="assignObject.id">
                    <draggable v-model="assignObject.undispalyAttrs" :group="{name: 'formContainer', pull: 'clone', put: false }" :clone="cloneComponent" :sort="false" tag="div" item-key="index" class="el-row" style="margin-left: -2px; margin-right: -2px;" >
                        <template #item="{element : attrAssign, index}">
                            <el-tag v-if="attrAssign.attribute != null" :key="attrAssign.attribute.code" style='margin:5px;' type="success" effect="dark">
                                {{attrAssign.attribute.name + ": " + attrAssign.attribute.code}}
                            </el-tag>
                        </template>
                    </draggable>
                </el-collapse-item>
                <el-collapse-item v-else-if="assignObject.objectAssign.biDataSet != null" :title="assignObject.objectAssign.biDataSet.name" :name="assignObject.id">
                    <draggable v-model="assignObject.undispalyAttrs" :group="{name: 'reportContainer', pull: 'clone', put: false }" :clone="cloneReportComponent" :sort="false" tag="div" item-key="index" class="el-row" style="margin-left: -2px; margin-right: -2px;" >
                        <template #item="{element : attrAssign, index}">
                            <el-tag :key="attrAssign.dataSetDetail.content.code" style='margin:5px;' :type="detailTypeMap[attrAssign.dataSetDetail.detailType].value2" effect="dark">
                                {{detailTypeMap[attrAssign.dataSetDetail.detailType].label + ": " +attrAssign.dataSetDetail.content.name + ": " + attrAssign.dataSetDetail.content.code}}
                            </el-tag>
                        </template>
                    </draggable>
                </el-collapse-item>
            </template>
        </draggable>

        <draggable v-model="objectAttrMapForSelect.undisplayObjectAttrs" tag="div" item-key="id" class="dragArea" >
            <template #item="{element : assignObject}">
                <el-collapse-item v-if="assignObject.filterAttrs != null" :title="assignObject.objectAssign.biDataSet.name + '-筛选'" :name="assignObject.id + '-filter'">
                    <draggable v-model="assignObject.filterAttrs" :group="{name: 'reportFilterContainer', pull: 'clone', put: false }" :clone="cloneFilterComponent" :sort="false" tag="div" item-key="id" class="el-row" style="margin-left: -2px; margin-right: -2px;" >
                        <template #item="{element : attrAssign, index}">
                            <el-tag :key="attrAssign.dataSetDetail.content.code" style='margin:5px;' :type="detailTypeMap[attrAssign.dataSetDetail.detailType].value2" effect="dark">
                                {{detailTypeMap[attrAssign.dataSetDetail.detailType].label + ": " +attrAssign.dataSetDetail.content.name + ": " + attrAssign.dataSetDetail.content.code}}
                            </el-tag>
                        </template>
                    </draggable>
                </el-collapse-item>
            </template>
        </draggable>
        <draggable v-model="objectAttrMapForSelect.undisplayObjectAttrs" tag="div" item-key="id" class="dragArea" >
            <template #item="{element : assignObject}">
                <el-collapse-item v-if="assignObject.tagAttrs != null" :title="assignObject.objectAssign.biDataSet.name + '-标签'" :name="assignObject.id + '-tag'">
                    <draggable v-model="assignObject.tagAttrs" :group="{name: 'reportTagContainer', pull: 'clone', put: false }" :clone="cloneFilterComponent" :sort="false" tag="div" item-key="id" class="el-row" style="margin-left: -2px; margin-right: -2px;" >
                        <template #item="{element : attrAssign, index}">
                            <el-tag :key="attrAssign.dataSetDetail.content.code" style='margin:5px;' :type="detailTypeMap[attrAssign.dataSetDetail.detailType].value2" effect="dark">
                                {{detailTypeMap[attrAssign.dataSetDetail.detailType].label + ": " +attrAssign.dataSetDetail.content.name + ": " + attrAssign.dataSetDetail.content.code}}
                            </el-tag>
                        </template>
                    </draggable>
                </el-collapse-item>
            </template>
        </draggable>
    </el-collapse>

    <el-collapse>
        <draggable v-model="customizedDesignComponents.components" tag="div" item-key="code" class="dragArea" >
            <template #item="{element : componentGroup}">
                <el-collapse-item :title="componentGroup.name" :name="componentGroup.code">
                    <draggable v-if="componentGroup.type == 'T'" v-model="componentGroup.subComponents" :group="{name: 'chartComponentContainer', pull: 'clone', put: false }" :clone="cloneChartComponent" :sort="false" tag="div" item-key="id" class="el-row" style="margin-left: -2px; margin-right: -2px;" >
                        <template #item="{element : chartComponent, index}">
                            <a href="#" style="padding: 10px 0 10px 20px; color: #6e7079; font-size: 14px; display: inline-block; vertical-align: middle;" onclick="return false;">
                                <span style="content:''; width: 20px; display: inline-block; border-radius: 50%; vertical-align: middle;">
                                    <svg width="20px" height="20px" viewBox="0 0 175 138" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink">
                                        <title>{{ chartComponent.name }}</title> 
                                        <g id="Page-1" stroke="none" stroke-width="1" fill="none" fill-rule="evenodd"> 
                                            <g id="line" fill="#5067A2" fill-rule="nonzero"> 
                                                <path v-for="path in chartComponent.paths" :d="path" id="Path"></path> 
                                            </g> 
                                        </g> 
                                    </svg>
                                </span>
                                <span style="display: inline-block; position: relative; vertical-align: middle; margin-left: 10px;">
                                    {{ chartComponent.name }}
                                </span>
                            </a>
                        </template>
                    </draggable>
                    <draggable v-else v-model="componentGroup.subComponents" :group="{ name: (componentGroup.type == 'C' || componentGroup.type == 'L' || componentGroup.type == 'O') ?'containerComponent':(componentGroup.type == 'B' ? 'buttonContainer': 'formContainer'), pull: 'clone', put: false }" :clone="cloneComponent" :sort="false" tag="div" item-key="index" class="el-row" style="margin-left: -2px; margin-right: -2px;" >
                        <template #item="{element : component, index}">
                            <el-tag  :key="component.code" style='margin:5px;' type="success" effect="dark">
                                <span v-if="componentGroup.type=='C' || componentGroup.type == 'L'" style="justify-content:center; display: flex; flex-wrap: nowrap;">
                                    <el-icon v-if="component.code == 'M'"><grid /></el-icon>
                                    <el-icon v-else-if="component.code == 'N'"><Expand /></el-icon>
                                    <el-icon v-else-if="component.code == 'B'"><CircleCheckFilled /></el-icon>
                                    <el-icon v-else-if="component.code == 'T'"><List /></el-icon>
                                    <el-icon v-else-if="component.code == 'R'"><OfficeBuilding /></el-icon>
                                    <el-icon v-else-if="component.code == 'C'"><TrendCharts /></el-icon>
                                    <el-icon v-else-if="component.code == 'O'"><Management /></el-icon>
                                    <el-icon v-else-if="component.code == 'Q'"><Menu /></el-icon>
                                    {{component.name}}
                                </span>
                                <span v-else>
                                    {{component.name}}
                                </span>
                            </el-tag>
                        </template>
                    </draggable>
                </el-collapse-item>
            </template>
        </draggable>
    </el-collapse>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import draggable from 'vuedraggable'

export default {
    props: ['objectAttrMapForSelect', 'customizedPage', 'customizedDesignComponents'],
    
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
        const detailTypeMap = ref(null);
        
        return {
            detailTypeMap
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValuesMore", ['BI_COLUMN_TYPE_DATASET']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                var dataSetColumnType = data.data['BI_COLUMN_TYPE_DATASET'];
                var map = {};
                for(var i in dataSetColumnType) {
                    map[dataSetColumnType[i].value] = dataSetColumnType[i];
                }
                this.detailTypeMap = map;
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
        cloneComponent(originComponent) {
            if(originComponent.attributeId != null) {
                return {
                    attribute: originComponent.attribute,
                    attributeId: originComponent.attributeId,
                    displayType: originComponent.displayType,
                    colSpan: originComponent.colSpan,
                    rowSpan: originComponent.rowSpan,
                    displayWidth: originComponent.displayWidth,
                    displayName: originComponent.displayName,
                    funcId: originComponent.funcId,
                    funcRegionId: originComponent.funcRegionId,
                    objectAssignId: originComponent.objectAssignId,
                    objectId: originComponent.objectId,
                    objectType: originComponent.objectType,
                    systemId: originComponent.systemId,
                    tenantId: originComponent.tenantId
                };
            }else if(originComponent.type.startsWith('C')) {//容器类控件
                return {
                    id: null,
                    name: originComponent.name,
                    componentCode: originComponent.code,
                    componentType: originComponent.type,
                    funcId: this.customizedPage.funcId,
                    pageId: this.customizedPage.id,
                    parentLayoutId: null,
                    displayOrder: 0,
                    layoutCfg: null,
                    systemId: this.customizedPage.systemId,
                    tenantId: this.customizedPage.tenantId,
                    createTime: null,
                    updateTime: null
                };
            }else if(originComponent.type.startsWith('F')) {//表单控件
                return {
                    displayType: originComponent.code, 
                    displayName: originComponent.name, 
                    attribute: {code:''}, 
                    colSpan: 8,
                    systemId: this.customizedPage.systemId,
                    tenantId: this.customizedPage.tenantId,
                };
            }else if(originComponent.type.startsWith('B')) {//按钮控件
                return {
                    operationType: originComponent.code, 
                    name: originComponent.name,
                    systemId: this.customizedPage.systemId,
                    tenantId: this.customizedPage.tenantId,
                    operationCfg: null,
                    operationCfgJSON: {}
                };
            }else if(originComponent.type.startsWith('L')) {//布局控件
                return {
                    id: null,
                    name: originComponent.name,
                    componentCode: originComponent.code,
                    componentType: originComponent.type,
                    funcId: this.customizedPage.funcId,
                    pageId: this.customizedPage.id,
                    parentLayoutId: null,
                    displayOrder: 0,
                    layoutCfg: null,
                    systemId: this.customizedPage.systemId,
                    tenantId: this.customizedPage.tenantId,
                    createTime: null,
                    updateTime: null
                };
            }else if(originComponent.type.startsWith('O')) {//其他控件
                return {
                    id: null,
                    name: originComponent.name,
                    componentCode: originComponent.code,
                    componentType: originComponent.type,
                    funcId: this.customizedPage.funcId,
                    pageId: this.customizedPage.id,
                    parentLayoutId: null,
                    displayOrder: 0,
                    layoutCfg: null,
                    systemId: this.customizedPage.systemId,
                    tenantId: this.customizedPage.tenantId,
                    createTime: null,
                    updateTime: null
                };
            }
        },
        cloneChartComponent(originComponent) {
            return {
                name: originComponent.name,
                type: originComponent.type,
                keyDataAttrs: originComponent.keyDataAttrs
            };
        },
        cloneReportComponent(originComponent) {
            var dataSetDetail = JSON.parse(JSON.stringify(originComponent.dataSetDetail));
            var attributeId = originComponent.attributeId;
            if(originComponent.displayType == 'Q') {//累加列
                var count = new Date().getTime();
                dataSetDetail.content.code += '-2-' + count;
                attributeId += '-2-' + count;
            }
            return {
                dataSetDetail: dataSetDetail,
                attributeId: attributeId,
                displayType: originComponent.displayType,
                colSpan: originComponent.colSpan,
                rowSpan: originComponent.rowSpan,
                displayWidth: originComponent.displayWidth,
                displayName: originComponent.displayName,
                displayCfgJSON: {},
                displayCfg: "{}",
                funcId: originComponent.funcId,
                funcRegionId: originComponent.funcRegionId,
                objectAssignId: originComponent.objectAssignId,
                objectId: originComponent.objectId,
                objectType: originComponent.objectType,
                systemId: originComponent.systemId,
                tenantId: originComponent.tenantId
            };
        },
        cloneFilterComponent(originComponent) {
            const clone = {
                dataSetDetail: originComponent.dataSetDetail,
                attributeId: originComponent.attributeId,
                displayType: originComponent.displayType,
                colSpan: originComponent.colSpan,
                rowSpan: originComponent.rowSpan,
                displayWidth: originComponent.displayWidth,
                displayName: originComponent.displayName,
                displayCfgJSON: {},
                displayCfg: "{}",
                funcId: originComponent.funcId,
                funcRegionId: originComponent.funcRegionId,
                objectAssignId: originComponent.objectAssignId,
                objectId: originComponent.objectId,
                objectType: originComponent.objectType,
                systemId: originComponent.systemId,
                tenantId: originComponent.tenantId
            };
            if(clone.dataSetDetail.detailType == 'F') {
                clone.displayType = "M";
            }else if(clone.dataSetDetail.detailType == 'L') {
                clone.displayType = "N";
                clone.colSpan = 3;
            }
            return clone;
        }
    }
 }
</script>