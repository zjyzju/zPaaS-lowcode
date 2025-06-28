<!-- 动态映射规则查询选择页 -->
<template>
<el-dialog v-model="showFlag" title="动态映射规则选择" width="800px">
    <template #header>
        <span class="title">动态映射规则选择</span>
    </template>
    <div>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="动态映射类型">
                    <el-select v-model="queryDynamicMappingCondition.mappingType" class="m-2" disabled placeholder="Select" size="small">
                        <el-option
                                v-for="item in mappingTypeOptions"
                                :key="item.value"
                                :label="item.label"
                                :value="item.value"
                            />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="关键属性">
                    <el-input v-model="queryDynamicMappingCondition.keyAttribute"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="业务类型">
                    <el-input v-model="queryDynamicMappingCondition.businessType" size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="子业务类型">
                    <el-input v-model="queryDynamicMappingCondition.subBusinessType" size="small" />
                </el-form-item>
            </el-col>
        </el-row>
        
        <el-row :gutter="4">
            <el-col :span="20" class="toolbar-right">
            </el-col>
            <el-col :span="4" class="toolbar-right"><el-button type="primary" size="small" @click="queryDynamicMapping()">查询</el-button></el-col>
        </el-row>

        <!-- 动态映射列表 -->
        <br/>
        <el-row :gutter="4">
            <el-col :span="1"></el-col>
            <el-col :span="23">
                <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
                <div class="toolbar1">
                    <el-icon><histogram /></el-icon><span>动态映射列表</span>&nbsp;&nbsp;&nbsp;&nbsp;
                </div>
                </el-divider>
            </el-col>
        </el-row>

        <el-scrollbar  height="350px">
            <el-form  label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="dynamicMappingSelectTable" :data="dynamicMappings" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
                                <el-table-column prop="id" label="标识" width="251" />
                                <el-table-column prop="name" label="名称" width="200" />
                                <el-table-column prop="mappingType" label="映射类型" width="120" >
                                    <template #default="scope">
	                                    <div style="display: flex; align-items: center">
	                                      <span v-if="scope.row.mappingType == 'V'">校验规则组</span>
	                                      <span v-if="scope.row.mappingType == 'D'">数据映射</span>
                                          <span v-if="scope.row.mappingType == 'O'">方法</span>
	                                      <span v-if="scope.row.mappingType == 'F'">文件对象映射</span>
	                                    </div>
	                                </template>
                                </el-table-column>
                                <el-table-column prop="businessType" label="业务类型" width="120" />
                                <el-table-column prop="subBusinessType" label="子业务类型" width="120" />
                                <el-table-column prop="keyAttribute" label="关键属性" width="120" />
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
    </div>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDynamicMappingSelectPage()">取消</el-button>
            <el-button type="primary" @click="selectDynamicMapping()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['showDynamicMappingSelect', 'systemId', 'queryDynamicMappingCondition'],
    
    emits: [ 'hideDynamicMappingSelectPage', 'dynamicMappingSelected'],
    
    setup(props, {attrs, emit, slots}) {
        const hideDynamicMappingSelectPage = () => {
            emit('hideDynamicMappingSelectPage');
        };
        
        const dynamicMappingSelected = (mappings, type) => {
            emit('dynamicMappingSelected', mappings, type);
        };
        return {
        	hideDynamicMappingSelectPage,
        	dynamicMappingSelected
        }
    },
    components: {
       
    },
    computed: {
        showFlag: {
            get() {
                return this.showDynamicMappingSelect;
            },
            set(value) {
                this.hideDynamicMappingSelectPage();
            }
        }
    },
    data() {
    	const mappingTypeOptions = ref(null);
        
    	const currentRow = ref();

        const dynamicMappings = ref(null);
    	
        return {
    		currentRow,
            mappingTypeOptions,
            dynamicMappings
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['DYNAMIC_MAPPING_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.mappingTypeOptions = data.data['DYNAMIC_MAPPING_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	handleCurrentChange(val) {
    		this.currentRow = val;
    	},
    	selectDynamicMapping() {
    		if(this.currentRow == null || this.currentRow.id == null) {
    			ElMessage.error(`请先选择一条动态映射规则！`);
    			return;
    		}
    		
    		this.dynamicMappingSelected(this.currentRow);
        },
        queryDynamicMapping() {
            this.queryDynamicMappingCondition.systemId = this.systemId;
            var url = "/lcdp-proxy/lowcode/platform/be/api/dynamicMapping/queryByCondition";
            axiosClient.post(url, this.queryDynamicMappingCondition).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dynamicMappings = data.data;
                }
                
            });
        }
    }
}

</script>