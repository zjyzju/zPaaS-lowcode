<!-- Sql选择页 -->
<template>
<el-dialog v-model="showFlag" title="Sql选择" width="800px">
    <template #header>
        <span class="title">Sql选择</span>
    </template>
    <div>
        <el-scrollbar height="350px">
            <el-form v-if="showSqlSelect && sqlsForSelect" label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="sqlSelectTable" :data="sqlsForSelect" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
                                <el-table-column prop="id" label="标识" width="251" />
                                <el-table-column prop="code" label="编码" width="220" />
                                <el-table-column prop="dbSchema.name" label="对应数据库" width="140" />
                                <el-table-column prop="sqlSentence" label="sql语句" width="440" />
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
    </div>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideSqlSelectPage()">取消</el-button>
            <el-button type="primary" @click="selectSql()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['showSqlSelect', 'sqlsForSelect'],
    
    emits: [ 'hideSqlSelectPage', 'sqlSelected'],
    
    setup(props, {attrs, emit, slots}) {
        const hideSqlSelectPage = () => {
            emit('hideSqlSelectPage');
        };
        
        const sqlSelected = (sql) => {
            emit('sqlSelected', sql);
        };
        return {
        	hideSqlSelectPage,
        	sqlSelected
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showSqlSelect;
            },
            set(value) {
                this.hideSqlSelectPage();
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
    	selectSql() {
    		if(this.currentRow == null || this.currentRow.id == null) {
    			ElMessage.error(`请先选择一条Sql语句！`);
    			return;
    		}
    		this.sqlSelected(this.currentRow);
        }
    }
}

</script>