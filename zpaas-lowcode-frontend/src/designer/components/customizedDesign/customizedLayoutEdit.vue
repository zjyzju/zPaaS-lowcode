<template> 
    <el-form  :model="customizedLayout" label-width="80px" :rules="validateRules" ref=" customizedLayoutForm">
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="名称" required prop="name">
                    <el-input  v-model=" customizedLayout.name"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="组件"  required prop="componentCode">
                    <el-select v-model=" customizedLayout.componentCode" class="m-2" placeholder="Select" size="small" disabled>
                        <el-option v-for="item in layoutComponents" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        
        <!-- 分隔线组件配置信息----begion -->
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'DOC'">
            <el-col :span="24">
                <el-form-item label="方向">
                    <el-select v-model="customizedLayout.layoutCfgJSON.direction" clearable class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in directionOptions" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'DOC' && customizedLayout.layoutCfgJSON.direction == 'vertical'">
            <el-col :span="24">
                <el-form-item label="垂直高度">
                    <el-input  v-model="customizedLayout.layoutCfgJSON.verticalHeight"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'DOC'">
            <el-col :span="24">
                <el-form-item label="内容位置">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.contentPosition" clearable class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in positionOptions" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <!-- 分隔线组件配置信息----end -->
        <!-- 标签页组件配置信息----begion -->
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'OLC'" v-for="tab in customizedLayout.layoutCfgJSON.tabs">
            <el-col :span="24">
                <el-form-item :label="tab.tabCode">
                    <el-input  v-model="tab.tabName"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <!-- 标签页组件配置信息----end -->
        <!-- 垂直布局组件配置信息----begion -->
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'QLC'">
            <el-col :span="24">
                <el-form-item label="分区数">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.splitNumber" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in splitNumberOptions" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'QLC'" v-for="(subAreaSpan, index) in customizedLayout.layoutCfgJSON.subAreaSpans">
            <el-col :span="24">
                <el-form-item :label="index + '区宽'">
                    <el-input  v-model="customizedLayout.layoutCfgJSON.subAreaSpans[index]"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <!-- 垂直布局组件配置信息----end -->
        <!-- 表格容器组件配置信息----begion -->
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'NCL'">
            <el-col :span="24">
                <el-form-item label="数据源">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.tableDataSource" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in tableDataSourceOptions" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'NCL' && customizedLayout.layoutCfgJSON.tableDataSource == 'O'">
            <el-col :span="24">
                <el-form-item label="源对象">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.tableDataSourceObject" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in objectAssigns" :key="item.id" :label="item.assignObject.code" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'NCL' && customizedLayout.layoutCfgJSON.tableDataSource == 'O'">
            <el-col :span="24">
                <el-form-item label="源属性">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.tableDataSourceAttr" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in objectAssignAttrs" :key="item.code" :label="item.code" :value="item.code"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'NCL'">
            <el-col :span="24">
                <el-form-item label="标题区域">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.showTitleArea" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in yesOrNoOptions" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'NCL'">
            <el-col :span="24">
                <el-form-item label="操作列">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.showOperationCol" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in yesOrNoOptions" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'NCL'">
            <el-col :span="24">
                <el-form-item label="分页区域">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.showPageArea" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in yesOrNoOptions" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'NCL' && customizedLayout.layoutCfgJSON.showPageArea == 'Y'">
            <el-col :span="24">
                <el-form-item label="每页条数">
                    <el-input v-model="customizedLayout.layoutCfgJSON.pageSize" class="m-2" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'NCL'">
            <el-col :span="24">
                <el-form-item label="选择模式">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.selectMode" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in selectModeOptions" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <!-- 表格容器组件配置信息----end -->
        <!-- 表单容器组件配置信息----begion -->
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'MCL'">
            <el-col :span="24">
                <el-form-item label="数据源">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.formDataSource" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in tableDataSourceOptions" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'MCL' && customizedLayout.layoutCfgJSON.formDataSource == 'O'">
            <el-col :span="24">
                <el-form-item label="源对象">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.formDataSourceObject" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in objectAssigns" :key="item.id" :label="item.assignObject.code" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'MCL' && customizedLayout.layoutCfgJSON.formDataSource == 'O'">
            <el-col :span="24">
                <el-form-item label="源属性">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.formDataSourceAttr" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in objectAssignAttrs" :key="item.code" :label="item.code" :value="item.code"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'MCL'">
            <el-col :span="24">
                <el-form-item label="标签宽度">
                    <el-input v-model="customizedLayout.layoutCfgJSON.labelWidth" class="m-2" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <!-- 表单容器组件配置信息----end -->

        <!-- 树形容器组件配置信息----begion -->
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'TCL'">
            <el-col :span="24">
                <el-form-item label="初始化">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.setInitOperation" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in yesOrNoOptions" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'TCL'">
            <el-col :span="24">
                <el-form-item label="数据源">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.treeDataSource" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in tableDataSourceOptions" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'TCL' && customizedLayout.layoutCfgJSON.treeDataSource == 'O'">
            <el-col :span="24">
                <el-form-item label="源对象">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.treeDataSourceObject" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in objectAssigns" :key="item.id" :label="item.assignObject.code" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'TCL' && customizedLayout.layoutCfgJSON.treeDataSource == 'O'">
            <el-col :span="24">
                <el-form-item label="源属性">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.treeDataSourceAttr" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in objectAssignAttrs" :key="item.code" :label="item.code" :value="item.code"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'TCL'">
            <el-col :span="24">
                <el-form-item label="显示过滤">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.showFilterArea" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in yesOrNoOptions" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'TCL'">
            <el-col :span="24">
                <el-form-item label="显示高度">
                    <el-input v-model="customizedLayout.layoutCfgJSON.treeDisplayHeight" class="m-2" size="small" clearable/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'TCL'">
            <el-col :span="24">
                <el-form-item label="选中值Key">
                    <el-input v-model="customizedLayout.layoutCfgJSON.selectedValueKey" class="m-2" size="small" clearable/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'TCL'">
            <el-col :span="24">
                <el-form-item label="value列" required>
                    <el-input v-model="customizedLayout.layoutCfgJSON.valueAttr" class="m-2" size="small" clearable/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'TCL'">
            <el-col :span="24">
                <el-form-item label="label列">
                    <el-input v-model="customizedLayout.layoutCfgJSON.labelAttr" class="m-2" size="small" clearable/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'TCL'">
            <el-col :span="24">
                <el-form-item label="children列">
                    <el-input v-model="customizedLayout.layoutCfgJSON.childrenAttr" class="m-2" size="small" clearable/>
                </el-form-item>
            </el-col>
        </el-row>
        <!-- 树形容器组件配置信息----end -->

        <!-- 锚点组件配置信息----begion -->
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'AOC'">
            <el-col :span="24">
                <el-form-item label="锚点方向">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.anchorDirection" clearable class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in directionOptions" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'AOC'">
            <el-col :span="24">
                <el-form-item label="锚点数">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.anchorNumber" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in anchorNumberOptions" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'AOC'" v-for="(subAreaSpan, index) in customizedLayout.layoutCfgJSON.anchorNames">
            <el-col :span="24">
                <el-form-item :label="'锚点名' + index">
                    <el-input  v-model="customizedLayout.layoutCfgJSON.anchorNames[index]"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'AOC'" v-for="(subAreaSpan, index) in customizedLayout.layoutCfgJSON.anchorValues">
            <el-col :span="24">
                <el-form-item :label="'锚点值' + index">
                    <el-input  v-model="customizedLayout.layoutCfgJSON.anchorValues[index]"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <!-- 锚点布局组件配置信息----end -->
        <!-- 报表/图表组件配置信息----begin -->
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'RCL' || customizedLayout.componentCode == 'CCL'">
            <el-col :span="24">
                <el-form-item label="显示筛选">
                    <el-select v-model=" customizedLayout.layoutCfgJSON.showFilterArea" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in yesOrNoOptions" :key="item.value" :label="item.label" :value="item.value"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="customizedLayout.componentCode == 'CCL'">
            <el-col :span="24">
                <el-form-item label="图表高度">
                    <el-input  v-model="customizedLayout.layoutCfgJSON.chartHeight"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <!-- 报表/图表组件配置信息----end -->
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="配置">
                    <el-input type="textarea" rows="5" v-model="customizedLayout.layoutCfg" readonly size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';

export default {
    props: ['customizedLayout', 'objectAssigns'],
    
    emits: [],
    
    setup (props, {attrs, emit, slots}) {
        
        return {
            
        }
    },
    computed: {
        
    },
    components: {
        
    },
    
    data() {   
        const layoutComponents = ref(null);

        const positionOptions = ref(null);

        const directionOptions = ref(null);

        const splitNumberOptions = ref(null);

        const anchorNumberOptions = ref(null);

        const yesOrNoOptions = ref(null);

        const selectModeOptions = ref(null);

        const tableDataSourceOptions = ref(null);

        const objectAssignAttrs = ref(null);

        const validateRules = ref({
            "name": [
                { required: true, message: '布局名称不能为空！', trigger: 'blur' }
            ],
            "componentCode": [
                { required: true, message: '组件类型不能为空！', trigger: 'blur' }
            ]
        });

        
        return {
            validateRules,
            layoutComponents,
            positionOptions,
            splitNumberOptions,
            yesOrNoOptions,
            selectModeOptions,
            tableDataSourceOptions,
            objectAssignAttrs,
            directionOptions,
            anchorNumberOptions
        }
    },
    watch: {
        'customizedLayout.layoutCfgJSON': {
            handler: function(value, old) {
                if(value == null){
                    return;
                }
                this.customizedLayout.layoutCfg = JSON.stringify(this.customizedLayout.layoutCfgJSON);
            },
            deep: true
        },
        'customizedLayout.layoutCfgJSON.tableDataSourceObject': {
            handler: function(value, old) {
                if(value == null){
                    this.objectAssignAttrs = null;
                    return;
                }
                for(var i in this.objectAssigns) {
                    if(this.objectAssigns[i].id == value) {
                        this.objectAssignAttrs = this.objectAssigns[i].attributes;
                        break;
                    }
                }               
            }
        },
        'customizedLayout.layoutCfgJSON.formDataSourceObject': {
            handler: function(value, old) {
                if(value == null){
                    this.objectAssignAttrs = null;
                    return;
                }
                for(var i in this.objectAssigns) {
                    if(this.objectAssigns[i].id == value) {
                        this.objectAssignAttrs = this.objectAssigns[i].attributes;
                        break;
                    }
                }               
            }
        },
        'customizedLayout.layoutCfgJSON.treeDataSourceObject': {
            handler: function(value, old) {
                if(value == null){
                    this.objectAssignAttrs = null;
                    return;
                }
                for(var i in this.objectAssigns) {
                    if(this.objectAssigns[i].id == value) {
                        this.objectAssignAttrs = this.objectAssigns[i].attributes;
                        break;
                    }
                }               
            }
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['LAYOUT_COMPONENT','DISPLAY_POSITION_TYPE','DISPLAY_DIRECTION_TYPE','ANCHOR_NUMBER','PUB_YES_OR_NO','SUPPORT_SELECT_MODE','TABLE_DATA_SOURCE_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.layoutComponents = data.data['LAYOUT_COMPONENT'];
                this.positionOptions = data.data['DISPLAY_POSITION_TYPE'];
                this.directionOptions = data.data['DISPLAY_DIRECTION_TYPE'];
                this.splitNumberOptions = data.data['SPLIT_NUMBER'];
                this.anchorNumberOptions = data.data['ANCHOR_NUMBER'];
                this.yesOrNoOptions = data.data['PUB_YES_OR_NO'];
                this.selectModeOptions = data.data['SUPPORT_SELECT_MODE'];
                this.tableDataSourceOptions = data.data['TABLE_DATA_SOURCE_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        if(this.objectAssigns != null && this.customizedLayout.layoutCfgJSON != null && this.customizedLayout.layoutCfgJSON.tableDataSourceObject != null) {
            for(var i in this.objectAssigns) {
                if(this.objectAssigns[i].id == this.customizedLayout.layoutCfgJSON.tableDataSourceObject) {
                    this.objectAssignAttrs = this.objectAssigns[i].attributes;
                    break;
                }
            } 
        }
        if(this.objectAssigns != null && this.customizedLayout.layoutCfgJSON != null && this.customizedLayout.layoutCfgJSON.formDataSourceObject != null) {
            for(var i in this.objectAssigns) {
                if(this.objectAssigns[i].id == this.customizedLayout.layoutCfgJSON.formDataSourceObject) {
                    this.objectAssignAttrs = this.objectAssigns[i].attributes;
                    break;
                }
            } 
        }
    },
    methods: {
    }
 }
</script>