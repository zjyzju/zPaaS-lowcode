<!-- 数据映射规则选择页 -->
<template>
<el-dialog v-model="showFlag" title="数据映射规则选择" width="800px">
    <template #header>
        <span class="title">数据映射规则选择</span>
    </template>
    <div>
        <el-scrollbar  height="350px">
            <el-form v-if="showDataMappingSelect && dataMappingsForSelect"  label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="dataMappingSelectTable" :data="dataMappingsForSelect" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
                                <el-table-column prop="id" label="标识" width="251" />
                                <el-table-column prop="name" label="名称" width="200" />
                                <el-table-column prop="fromObjectType" label="源对象" width="190" >
                                    <template #default="scope">
	                                    <div style="display: flex; align-items: center">
	                                      <span v-if="scope.row.fromObjectType == 'D'">领域对象({{scope.row.fromObject.code}})</span>
	                                      <span v-if="scope.row.fromObjectType == 'V'">值传递对象({{scope.row.fromObject.code}})</span>
	                                    </div>
	                                </template>
                                </el-table-column>
                                <el-table-column prop="toObjectType" label="目标对象类型" width="190" >
                                    <template #default="scope">
                                        <div style="display: flex; align-items: center">
                                          <span v-if="scope.row.toObjectType == 'D'">领域对象({{scope.row.toObject.code}})</span>
                                          <span v-if="scope.row.toObjectType == 'V'">值传递对象({{scope.row.toObject.code}})</span>
                                        </div>
                                    </template>
                                </el-table-column>
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
    </div>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDataMappingSelectPage()">取消</el-button>
            <el-button type="primary" @click="selectDataMapping()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['showDataMappingSelect', 'dataMappingsForSelect', 'dataMappingSelectType'],
    
    emits: [ 'hideDataMappingSelectPage', 'dataMappingSelected'],
    
    setup(props, {attrs, emit, slots}) {
        const hideDataMappingSelectPage = () => {
            emit('hideDataMappingSelectPage');
        };
        
        const dataMappingSelected = (mappings, type) => {
            emit('dataMappingSelected', mappings, type);
        };
        return {
        	hideDataMappingSelectPage,
        	dataMappingSelected
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showDataMappingSelect;
            },
            set(value) {
                this.hideDataMappingSelectPage();
            }
        }
    },
    data() {
    	
    	
    	const currentRow = ref()
    	
        return {
    		currentRow
        }
    },
    
    methods: {
    	handleCurrentChange(val) {
    		this.currentRow = val;
    	},
    	selectDataMapping() {
    		if(this.currentRow == null || this.currentRow.id == null) {
    			ElMessage.error(`请先选择一条数据映射规则！`);
    			return;
    		}
    		if(this.dataMappingSelectType == 'D' && this.currentRow.toObjectType != 'D') {
                ElMessage.error(`目标对象类型不是领域对象！`);
                return;
            }
    		this.dataMappingSelected(this.currentRow, this.dataMappingSelectType);
        }
    }
}

</script>