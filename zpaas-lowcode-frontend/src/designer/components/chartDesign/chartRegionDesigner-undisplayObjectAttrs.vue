<template> 
    <el-collapse>
        <div v-for="assignObject in objectAttrMapForSelect.undisplayObjectAttrs" class="dragArea" v-if="detailTypeMap != null">
            <el-collapse-item :title="assignObject.objectAssign.biDataSet.name" :name="assignObject.id">
                <draggable v-model="assignObject.undispalyAttrs" :group="{name: 'chartDataContainer', pull: 'clone', put: false }" :clone="cloneComponent" :sort="false" tag="div" item-key="id" class="el-row" style="margin-left: -2px; margin-right: -2px;" >
                    <template #item="{element : attrAssign, index}">
                        <el-tag :key="attrAssign.dataSetDetail.content.code" style='margin:5px;' :type="detailTypeMap[attrAssign.dataSetDetail.detailType].value2" effect="dark">
                            {{detailTypeMap[attrAssign.dataSetDetail.detailType].label + ": " +attrAssign.dataSetDetail.content.name + ": " + attrAssign.dataSetDetail.content.code}}
                        </el-tag>
                    </template>
                </draggable>
            </el-collapse-item>
            <el-collapse-item v-if="assignObject.filterAttrs != null" :title="assignObject.objectAssign.biDataSet.name + '-筛选'" :name="assignObject.id + '-filter'">
                <draggable v-model="assignObject.filterAttrs" :group="{name: 'chartFilterContainer', pull: 'clone', put: false }" :clone="cloneFilterComponent" :sort="false" tag="div" item-key="id" class="el-row" style="margin-left: -2px; margin-right: -2px;" >
                    <template #item="{element : attrAssign, index}">
                        <el-tag :key="attrAssign.dataSetDetail.content.code" style='margin:5px;' :type="detailTypeMap[attrAssign.dataSetDetail.detailType].value2" effect="dark">
                            {{detailTypeMap[attrAssign.dataSetDetail.detailType].label + ": " +attrAssign.dataSetDetail.content.name + ": " + attrAssign.dataSetDetail.content.code}}
                        </el-tag>
                    </template>
                </draggable>
            </el-collapse-item>
            <el-collapse-item v-if="assignObject.tagAttrs != null" :title="assignObject.objectAssign.biDataSet.name + '-标签'" :name="assignObject.id + '-tag'">
                <draggable v-model="assignObject.tagAttrs" :group="{name: 'chartTagContainer', pull: 'clone', put: false }" :clone="cloneFilterComponent" :sort="false" tag="div" item-key="id" class="el-row" style="margin-left: -2px; margin-right: -2px;" >
                    <template #item="{element : attrAssign, index}">
                        <el-tag :key="attrAssign.dataSetDetail.content.code" style='margin:5px;' :type="detailTypeMap[attrAssign.dataSetDetail.detailType].value2" effect="dark">
                            {{detailTypeMap[attrAssign.dataSetDetail.detailType].label + ": " +attrAssign.dataSetDetail.content.name + ": " + attrAssign.dataSetDetail.content.code}}
                        </el-tag>
                    </template>
                </draggable>
            </el-collapse-item>
        </div>
        <el-collapse-item  title="可视化图表组件" name="可视化图表组件">
            <draggable v-model="chartData.chartComponents" :group="{name: 'chartComponentContainer', pull: 'clone', put: false }" :clone="cloneChartComponent" :sort="false" tag="div" item-key="id" class="el-row" style="margin-left: -2px; margin-right: -2px;" >
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
        </el-collapse-item>
    </el-collapse>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import draggable from 'vuedraggable'

export default {
    props: ['objectAttrMapForSelect', 'chartData'],
    
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
            var dataSetDetail = JSON.parse(JSON.stringify(originComponent.dataSetDetail));
            var attributeId = originComponent.attributeId;
            if(originComponent.displayType == 'S') {//数据提供列
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
        cloneChartComponent(originComponent) {
            return {
                name: originComponent.name,
                type: originComponent.type,
                keyDataAttrs: originComponent.keyDataAttrs
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