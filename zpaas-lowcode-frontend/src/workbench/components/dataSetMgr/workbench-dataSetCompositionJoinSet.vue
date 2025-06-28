<!-- JOIN条件设置 -->

<template>
<el-dialog v-model="showFlag" title="JOIN条件设置" width="70vw">
    <template #header>
        <span class="title">JOIN条件设置</span>
    </template>
    <el-form  :model="dataSetComposition" label-width="120px" :rules="validateRules" ref="dataSetCompositionJoinForm">
        <el-row :gutter="4">
            <el-col :span="8">
                <el-form-item label="主模型编码" prop="code">
                    <el-input v-model="dataSetComposition.dataModel.code" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="主模型名称" prop="name">
                    <el-input v-model="dataSetComposition.dataModel.name" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                &nbsp;
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="1">
                &nbsp;
            </el-col>
            <el-col :span="22">
                <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 12px">
                    <span class="title" style="font-size: 14px; font-weight: 500;">JOIN条件：</span>
                </el-divider>
            </el-col>
            <el-col :span="1">
                &nbsp;
            </el-col>
        </el-row> 
        <el-row :gutter="4">
            <el-col :span="1">
                &nbsp;
            </el-col>
            <el-col :span="22">
                <el-row :gutter="4">
                    <el-col :span="8">
                        <el-form-item label="主模型字段"  prop="mainColId">
                            <el-select v-model="mainColId" class="m-2" placeholder="Select" size="small">
                                <el-option v-for="item in mainDataModelColumns" :key="item.detailContentId" :label="item.content.name" :value="item.detailContentId" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="条件类型"  prop="condType">
                            <el-select v-model="condType" class="m-2" placeholder="Select" size="small" clearable>
                                <el-option v-for="item in condTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="JOIN类型"  prop="joinType">
                            <el-select v-model="joinType" class="m-2" placeholder="Select" size="small" clearable>
                                <el-option v-for="item in joinTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="8">
                        <el-form-item label="关联模型"  prop="joinRelDataModelId">
                            <el-select v-model="joinRelDataModelId" class="m-2" clearable placeholder="Select" size="small">
                                <el-option v-for="item in dataSet.compositions" :key="item.dataModelId" :label="item.dataModel.name" :value="item.dataModelId" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="关联字段类型"  prop="relColType">
                            <el-select v-model="relColType" class="m-2" placeholder="Select" size="small">
                                <el-option v-for="item in relColTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item label="关联字段"  prop="relColId">
                            <el-input v-if="relColType == 'F'" v-model="relColId"  size="small"/>
                            <el-select v-else v-model="relColId" class="m-2" placeholder="Select" size="small">
                                <el-option v-for="item in relDataModelColumns" :key="item.detailContentId" :label="item.content.name" :value="item.detailContentId" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>   
                <el-row :gutter="4">
                    <el-col :span="21">
                        &nbsp;
                    </el-col>
                    <el-col :span="3">
                        <el-link type="primary" size="small" @click="addJoinCondition()">增加</el-link>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="1">
                        &nbsp;
                    </el-col>
                    <el-col :span="23">
                        <el-container  style="height: 30vh">
                            <el-aside width="100%">
                                <el-row :gutter="4">
                                    <el-table :data="dataSetComposition.compositionJoins" stripe style="width: 100%">
                                        <el-table-column prop="mainColCode" label="主模型字段" show-overflow-tooltip width="180"/>
                                        <el-table-column prop="condType" label="条件类型" show-overflow-tooltip width="90">
                                            <template #default="scope">
                                                <span v-if="scope.row['condType'] == 'EQ'">等于</span>
                                                <span v-else-if="scope.row['condType'] == 'NE'">不等于</span>
                                                <span v-else-if="scope.row['condType'] == 'GT'">大于</span>
                                                <span v-else-if="scope.row['condType'] == 'GE'">大于等于</span>
                                                <span v-else-if="scope.row['condType'] == 'LT'">小于</span>
                                                <span v-else-if="scope.row['condType'] == 'LE'">小于等于</span>
                                                <span v-else>{{ scope.row['condType'] }}</span>
                                            </template>
                                        </el-table-column>
                                        <el-table-column prop="joinType" label="JOIN类型" show-overflow-tooltip width="90">
                                            <template #default="scope">
                                                <span v-if="scope.row['joinType'] == 'L'">左连接</span>
                                                <span v-else-if="scope.row['joinType'] == 'R'">右连接</span>
                                                <span v-else-if="scope.row['joinType'] == 'I'">内连接</span>
                                                <span v-else>{{ scope.row['joinType'] }}</span>
                                            </template>
                                        </el-table-column>
                                        <el-table-column prop="joinRelDataModelName" label="关联模型" show-overflow-tooltip width="180"/>
                                        <el-table-column prop="relColType" label="关联字段类型" show-overflow-tooltip width="110">
                                            <template #default="scope">
                                                <span v-if="scope.row['relColType'] == 'C'">字段</span>
                                                <span v-else-if="scope.row['relColType'] == 'F'">固定值</span>
                                                <span v-else>{{ scope.row['relColType'] }}</span>
                                            </template>
                                        </el-table-column>
                                        <el-table-column prop="relColCode" label="关联字段" show-overflow-tooltip width="180">
                                            <template #default="scope">
                                                <span v-if="scope.row['relColCode'] != null">{{scope.row['relColCode']}}</span>
                                                <span v-else>{{ scope.row['relColId'] }}</span>
                                            </template>
                                        </el-table-column>
                                        
                                        <el-table-column label="操作" show-overflow-tooltip width="80">
                                            <template #default="scope">
                                                <el-link type="primary"  size="small" @click="deleteJoinCondition(scope.row, scope.$index)"><el-icon><Delete /></el-icon></el-link>&nbsp;
                                            </template>
                                        </el-table-column>
                                    </el-table>
                                </el-row>
                            </el-aside>
                        </el-container>
                    </el-col>
                </el-row>
            </el-col>
            <el-col :span="1">
                &nbsp;
            </el-col>
        </el-row> 
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button type="primary" @click="hideDataSetCompositionJoinSetPage()">关闭</el-button>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
        </span>
    </template>
</el-dialog>
</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import draggable from 'vuedraggable'


export default {
    props: ['showDataSetCompositionJoinSet', 'dataSet', 'dataSetComposition'],
    
    emits: ['hideDataSetCompositionJoinSetPage'],
    
    setup (props, {attrs, emit, slots}) {
        const hideDataSetCompositionJoinSetPage = ()=> {
            emit('hideDataSetCompositionJoinSetPage');
        };
        
        return {
        	hideDataSetCompositionJoinSetPage
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showDataSetCompositionJoinSet;
            },
            set(value) {
                this.hideDataSetCompositionJoinSetPage();
            }
        }
    },
    components: {
        draggable
    },
    watch: {        
       'joinRelDataModelId': function(val, old) {
            if(val == null) {
                this.relDataModelColumns = [];
                return;
            }
            if(this.dataSet.details != null) {
                this.relDataModelColumns = [];
                for(var i in this.dataSet.details) {
                    if(this.dataSet.details[i].dataModel.id == val &&
                            this.dataSet.details[i].detailType != 'L' && this.dataSet.details[i].detailType != 'Q' && this.dataSet.details[i].detailType != 'F') {
                        this.relDataModelColumns.push(this.dataSet.details[i]);
                    }
                }
            }
        }
    
    },
    data() {            
    	const validateRules = ref({
            
        });

        const relColTypeOptions = ref(null);

        const joinTypeOptions = ref(null);

        const condTypeOptions = ref(null);

        const mainDataModelColumns = ref([]);
        const relDataModelColumns = ref(null);

        const joinRelDataModelId= ref(null);
        const mainColId = ref(null);
        const condType = ref('EQ');
        const joinType = ref('I');
        const relColType = ref('C');
        const relColId = ref(null);

        return {
        	mainDataModelColumns,
            relDataModelColumns,
            relColTypeOptions,
            condTypeOptions,
            joinTypeOptions,

            joinRelDataModelId,
            mainColId,
            condType,
            joinType,
            relColType,
            relColId,

            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_COMPARE_TYPE','BI_REL_COLUMN_TYPE','BI_JOIN_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.condTypeOptions = data.data['PUB_COMPARE_TYPE'];
                this.relColTypeOptions = data.data['BI_REL_COLUMN_TYPE'];
                this.joinTypeOptions = data.data['BI_JOIN_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        if(this.dataSet.details != null) {
            for(var i in this.dataSet.details) {
                if(this.dataSet.details[i].dataModel.id == this.dataSetComposition.dataModelId &&
                        this.dataSet.details[i].detailType != 'L' && this.dataSet.details[i].detailType != 'Q' && this.dataSet.details[i].detailType != 'F') {
                    this.mainDataModelColumns.push(this.dataSet.details[i]);
                }
            }
        }
        if(this.dataSetComposition.compositionJoins != null) {
            for(var index in this.dataSetComposition.compositionJoins) {
                if(this.dataSetComposition.compositionJoins[index].joinRelDataModelName == null) {
                    for(var i in this.dataSet.compositions) {
                        if(this.dataSet.compositions[i].dataModelId == this.dataSetComposition.compositionJoins[index].joinRelDataModelId) {
                            this.dataSetComposition.compositionJoins[index].joinRelDataModelName = this.dataSet.compositions[i].dataModel.name;
                            break;
                        }
                    }
                }

                if(this.dataSetComposition.compositionJoins[index].mainColCode == null) {
                    for(var i in this.dataSet.details) {
                        if(this.dataSet.details[i].detailContentId == this.dataSetComposition.compositionJoins[index].mainColId &&
                                this.dataSet.details[i].detailType != 'L' && this.dataSet.details[i].detailType != 'Q' && this.dataSet.details[i].detailType != 'F') {
                            this.dataSetComposition.compositionJoins[index].mainColCode = this.dataSet.details[i].content.code;
                        }
                    }
                }

                if(this.dataSetComposition.compositionJoins[index].relColCode == null) {
                    for(var i in this.dataSet.details) {
                        if(this.dataSet.details[i].detailContentId == this.dataSetComposition.compositionJoins[index].relColId &&
                                this.dataSet.details[i].detailType != 'L' && this.dataSet.details[i].detailType != 'Q' && this.dataSet.details[i].detailType != 'F') {
                            this.dataSetComposition.compositionJoins[index].relColCode = this.dataSet.details[i].content.code;
                        }
                    }
                }
            }
        }
    },
    methods: {
    	addJoinCondition() {
            if(this.mainColId == null || this.mainColId.trim().length == 0) {
                ElMessage.error(`主模型字段不能为空！`);
                return;
            }
            if(this.relColId == null || this.relColId.trim().length == 0) {
                ElMessage.error(`关联字段不能为空！`);
                return;
            }
            if(this.condType == null || this.condType.trim().length == 0) {
                ElMessage.error(`条件类型不能为空！`);
                return;
            }
            if(this.relColType == null || this.relColType.trim().length == 0) {
                ElMessage.error(`字段类型不能为空！`);
                return;
            }
            if(this.dataSetComposition.compositionJoins == null) {
                this.dataSetComposition.compositionJoins = [];
            }
            var joinRelDataModelName = this.joinRelDataModelId;
            for(var i in this.dataSet.compositions) {
                if(this.dataSet.compositions[i].dataModelId == this.joinRelDataModelId) {
                    joinRelDataModelName = this.dataSet.compositions[i].dataModel.name;
                    break;
                }
            }
            var mainColCode = this.mainColId;
            for(var i in this.mainDataModelColumns) {
                if(this.mainDataModelColumns[i].detailContentId == this.mainColId) {
                    mainColCode = this.mainDataModelColumns[i].content.code;
                    break;
                }
            }
            var relColCode = this.relColId;
            if(this.relColType == 'C') {
                for(var i in this.relDataModelColumns) {
                    if(this.relDataModelColumns[i].detailContentId == this.relColId) {
                        relColCode = this.relDataModelColumns[i].content.code;
                        break;
                    }
                }
            }
            var joinCondition = {
                id: '',
                joinMainCompoId: '',
                mainColId: this.mainColId,
                mainColCode: mainColCode,
                condType: this.condType,
                joinType: this.joinType,
                joinRelDataModelId: this.joinRelDataModelId,
                joinRelDataModelName: joinRelDataModelName,
                relColType: this.relColType,
                relColId: this.relColId,
                relColCode: relColCode,
                systemId: this.dataSet.systemId,
                tenantId: this.dataSet.tenantId
            };

            this.dataSetComposition.compositionJoins.push(joinCondition);
        },
        deleteJoinCondition(joinCondition, index) {
            ElMessageBox.confirm(
                '该JOIN条件将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                this.dataSetComposition.compositionJoins.splice(index, 1);
             }).catch((ex)=>{ElMessage.error(`删除JOIN条件失败！` + ex);});
        }
    }
    
}
</script>