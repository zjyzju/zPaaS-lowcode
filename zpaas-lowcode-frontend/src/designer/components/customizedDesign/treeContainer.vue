<!-- 功能区域设计器-树形容器 -->
<template>          
<div class="dragArea"  :id="treeComponent.name">
    <el-row :gutter="4">
        <el-col :span="8">
            <span class="floatDeleteTableContainer">
                <el-icon @click="onCustomizedLayoutSelected(treeComponent)"><List /></el-icon>
                <el-link class="floatDeleteTable" size="small" @click="deleteContainer(this.componentIndex)"><el-icon><Delete /></el-icon></el-link>
            </span>
        </el-col>
        <el-col :span="16">
            <el-link type="primary" @click="setInitOperation" v-if="treeComponent.layoutCfgJSON != null && treeComponent.layoutCfgJSON.setInitOperation == 'Y'">设置初始化操作</el-link>&nbsp;
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="23">
            <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
            <el-row :gutter="4" v-if="treeComponent.layoutCfgJSON != null && treeComponent.layoutCfgJSON.showFilterArea == 'Y'">
                <el-col :span="24">
                    <el-input v-model="filterText" style="width: 98%"  class="m-2" size="small" placeholder="Filter keyword" />
                </el-col>
            </el-row>
            <el-row :gutter="4">
                <el-col :span="24">
                    <el-scrollbar :height="(treeComponent.layoutCfgJSON != null && treeComponent.layoutCfgJSON.treeDisplayHeight != null && parseInt(treeComponent.layoutCfgJSON.treeDisplayHeight) != null) ? parseInt(treeComponent.layoutCfgJSON.treeDisplayHeight) + 'px' : '100%'" style="overflow-x: hidden; width: 100%;">
                        <el-tree ref="treeRef" style="max-width: 100%" class="filter-tree" :data="data" :props="defaultProps" default-expand-all :filter-node-method="filterNode" @current-change="currentChange" highlight-current/>
                    </el-scrollbar>
                </el-col>
            </el-row>
        </el-col>
        <el-col :span="1">
            <el-divider border-style="solid" direction="vertical" style="height:100%;margin-left: 1px;margin-right: 1px;align-self: center;"/>
        </el-col>
    </el-row>
</div>
</template>
<script>
import { ref} from 'vue';

export default {
    props: ['componentIndex', 'treeComponent'],
    
    emits: ['deleteContainer', 'onCustomizedLayoutSelected', 'onOperationSelected'],
    
    setup (props, {attrs, emit, slots}) {
       const onCustomizedLayoutSelected = (customizedLayout) => {
            emit('onCustomizedLayoutSelected', customizedLayout);
        };

        const deleteContainer = (index) => {
            emit('deleteContainer', index);
        };

        const onOperationSelected = (operation) => {
            emit('onOperationSelected', operation);
        };

        return {
        	deleteContainer,
            onCustomizedLayoutSelected,
            onOperationSelected
        }
    },
    computed: {
        
    },
    components: {
    	
    },
    
    data() {   
        const data  = ref([
            {
                id: 1,
                label: 'Level one 1',
                children: [
                {
                    id: 4,
                    label: 'Level two 1-1',
                    children: [
                    {
                        id: 9,
                        label: 'Level three 1-1-1',
                    },
                    {
                        id: 10,
                        label: 'Level three 1-1-2',
                    },
                    ],
                },
                ],
            },
            {
                id: 2,
                label: 'Level one 2',
                children: [
                {
                    id: 5,
                    label: 'Level two 2-1',
                },
                {
                    id: 6,
                    label: 'Level two 2-2',
                },
                ],
            },
            {
                id: 3,
                label: 'Level one 3',
                children: [
                {
                    id: 7,
                    label: 'Level two 3-1',
                },
                {
                    id: 8,
                    label: 'Level two 3-2',
                },
                ],
            }
        ]);

        const defaultProps = ref({
            children: 'children',
            label: 'label',
        });

        const filterText = ref(null);
       
    	return {
            data,
            defaultProps,
            filterText
        }
    },
    watch: {
        'filterText': function(value, old) {
            this.$refs.treeRef.filter(value);
        }
    },
    mounted() {

        if(this.treeComponent.layoutRegions == null) {
            this.treeComponent.layoutRegions=[];
        }
        if(this.treeComponent.layoutRegions.length == 0) {
            this.treeComponent.layoutRegions.push({
                id: null,
                funcId: this.treeComponent.funcId,
                pageId: this.treeComponent.pageId,
                layoutId: this.treeComponent.id,
                regionId: null,
                systemId: this.treeComponent.systemId,
                tenantId: this.treeComponent.tenantId,
                funcRegion: {
                    id: null,
                    funcId: this.treeComponent.funcId,
                    systemId: this.treeComponent.systemId,
                    tenantId: this.treeComponent.tenantId,
                    regionAttrAssigns: [],
                    regionOperations: []
                }
            });
        }
        this.treeComponent.initOperation=null;
        this.treeComponent.currentChangeOperation=null;
       
        if(this.treeComponent.layoutRegions[0].funcRegion.regionOperations.length > 0) {
            for(var index in this.treeComponent.layoutRegions[0].funcRegion.regionOperations) {
                var regionOperation = this.treeComponent.layoutRegions[0].funcRegion.regionOperations[index];
                var regionOperationJSON = null;
                if(regionOperation.operationCfg != null && regionOperation.operationCfg.trim().length > 0) {
                    regionOperationJSON = JSON.parse(regionOperation.operationCfg);
                }else {
                    regionOperationJSON = {};
                }
                regionOperation.operationCfgJSON = regionOperationJSON;
                if(regionOperationJSON != null && regionOperationJSON.treeOperationType == "2") {//初始化操作
                    this.treeComponent.initOperation = regionOperation;
                }else {//树节点点击响应操作
                    this.treeComponent.currentChangeOperation = regionOperation;
                }

            }
        }

        if(this.treeComponent.currentChangeOperation == null) {
            var regionOperation = {};
            var regionOperationJSON = null;
            if(regionOperation.operationCfg != null && regionOperation.operationCfg.trim().length > 0) {
                regionOperationJSON = JSON.parse(regionOperation.operationCfg);
            }else {
                regionOperationJSON = {};
            }
            regionOperation.name = "点击操作";
            regionOperation.operationType = "Q";
            regionOperationJSON.treeOperationType = "1";
            regionOperation.funcId=this.treeComponent.funcId;
            regionOperation.systemId=this.treeComponent.systemId;
            regionOperation.tenantId=this.treeComponent.tenantId;
            regionOperation.operationCfgJSON = regionOperationJSON;
            regionOperation.operationCfg = JSON.stringify(regionOperationJSON);
            this.treeComponent.currentChangeOperation = regionOperation;
            this.treeComponent.layoutRegions[0].funcRegion.regionOperations.push(this.treeComponent.currentChangeOperation);
        }
        if(this.treeComponent.initOperation == null) {
            var regionOperation = {};
            var regionOperationJSON = null;
            if(regionOperation.operationCfg != null && regionOperation.operationCfg.trim().length > 0) {
                regionOperationJSON = JSON.parse(regionOperation.operationCfg);
            }else {
                regionOperationJSON = {};
            }
            regionOperation.name = "初始化操作";
            regionOperation.operationType = "Q";
            regionOperationJSON.treeOperationType = "2";
            regionOperation.funcId=this.treeComponent.funcId;
            regionOperation.systemId=this.treeComponent.systemId;
            regionOperation.tenantId=this.treeComponent.tenantId;
            regionOperation.operationCfgJSON = regionOperationJSON;
            regionOperation.operationCfg = JSON.stringify(regionOperationJSON);
            this.treeComponent.initOperation = regionOperation;
            this.treeComponent.layoutRegions[0].funcRegion.regionOperations.push(this.treeComponent.initOperation);
        }

        var layoutCfgJSON = null;
        if(this.treeComponent.layoutCfg == null || this.treeComponent.layoutCfg.trim().length == 0) {
            layoutCfgJSON = {};
        }else {
            layoutCfgJSON = JSON.parse(this.treeComponent.layoutCfg);
        }
        if(layoutCfgJSON.showFilterArea == null || layoutCfgJSON.showFilterArea.trim().length == 0) {
            layoutCfgJSON.showTitleArea = 'N';
        }
        this.treeComponent.layoutCfgJSON = layoutCfgJSON;
        this.treeComponent.layoutCfg = JSON.stringify(layoutCfgJSON);
    },
    methods: {
        filterNode(value, data) {
            if(value == null || value.trim().length == 0) {
                return true;
            }
            return data.label.includes(value);
        },
        currentChange(value, data) {
            console.log(value, data);
            this.onOperationSelected(this.treeComponent.currentChangeOperation);
        },
        setInitOperation() {
            this.onOperationSelected(this.treeComponent.initOperation);
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