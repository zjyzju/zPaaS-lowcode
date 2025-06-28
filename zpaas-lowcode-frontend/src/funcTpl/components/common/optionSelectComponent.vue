<!--通用可选项选择子功能-->
<template>
    <el-scrollbar height="400px">
        <el-row :gutter="4">
            <el-col :span="24">
                <el-table :data="params.options" stripe style="width: 100%" @current-change="handleCurrentChange"  highlight-current-row>
                    <el-table-column :prop="params.valueAttr" label="值" :width="260" >
                        <template #default="scope" >
                            <div style="display: flex; align-items: center">
                                 <span>{{scope.row[this.params.valueAttr]}}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column v-if="params.codeAttr != null && params.codeAttr != ''" :prop="params.codeAttr" label="编码" >
                        <template #default="scope" >
                            <div style="display: flex; align-items: center">
                                <span>{{scope.row[this.params.codeAttr]}}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column :prop="params.labelAttr" label="名称" >
                        <template #default="scope" >
                            <div style="display: flex; align-items: center">
                                <span>{{scope.row[this.params.labelAttr]}}</span>
                            </div>
                        </template>
                    </el-table-column>
                </el-table>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                &nbsp;
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="20">
                &nbsp;
            </el-col>
            <el-col :span="4">
                <el-button type="primary"  size="small" @click="selectObject()">选择</el-button>&nbsp;&nbsp;
                <el-button size="small" @click="hideOptionSelect()">取消</el-button>
            </el-col>
        </el-row>
    </el-scrollbar>
</template>
<script>
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'


export default {
    props: ['params'],
    
    emits: ['objectSelected', 'hideOptionSelect'],
    
    setup (props, {attrs, emit, slots}) {
        const hideOptionSelect = () => {
            emit('hideOptionSelect')
        };
        const objectSelected = (updateParam) => {
            emit('objectSelected', updateParam)
        };
        
        return {
        	hideOptionSelect,
            objectSelected
        }
    },
    
    components: {
        
    },
    
    data() {   
    	const currentRow = ref();
    	
        return {
        	currentRow
        }
    },
    mounted() {
       
    },
    methods: {
    	handleCurrentChange(val) {
            this.currentRow = val;
        },
        selectObject() {
        	if(this.currentRow == null) {
                ElMessage.error(`请先选择一条记录！`);
                return;
            }
            this.objectSelected(this.currentRow);
        }
    }
}
</script>