<!-- 数据库表选择页 -->
<template>
<el-dialog v-model="showFlag" v-if="showMdaTableSelect ==true && mdaTablesForSelect != null" title="数据库表选择" width="800px">
    <template #header>
        <span class="title">数据库表选择</span>
    </template>
    <div>
        <el-form-item label="过滤对象">
            <el-input v-model="filterString" size="small"/>
        </el-form-item>
    </div>
    <div>
        <el-scrollbar height="350px">
            <el-form v-if="showMdaTableSelect && mdaTablesForSelect" label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="mdaTableSelectTable" :data="filteredObjects" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
                                <el-table-column prop="tableName" label="名称" width="251" />
                                <el-table-column prop="tableRemarks" label="描述" />
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
    </div>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideMdaTableSelectPage()">取消</el-button>
            <el-button type="primary" @click="selectMdaTable()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import { ref } from 'vue';
import { ElMessage, ElTable } from 'element-plus'

export default {
    props: ['showMdaTableSelect', 'mdaTablesForSelect'],
    
    emits: [ 'hideMdaTableSelectPage', 'mdaTableSelected'],
    
    setup(props, {attrs, emit, slots}) {
        const hideMdaTableSelectPage = () => {
            emit('hideMdaTableSelectPage');
        };
        
        const mdaTableSelected = (obj) => {
            emit('mdaTableSelected', obj);
        };
        return {
        	hideMdaTableSelectPage,
        	mdaTableSelected
        }
    },  
    computed: {
        showFlag: {
            get() {
                return this.showMdaTableSelect;
            },
            set(value) {
                this.hideMdaTableSelectPage();
            }
        }
    },
    data() {
    	   	
    	const currentRow = ref();
    	const filteredObjects = ref([]);
        const filterString = ref("");
        return {
    		currentRow,
    		filteredObjects,
    		filterString
        }
    },
    mounted() {
    	this.filteredObjects = this.mdaTablesForSelect;
    },
    watch: {
        'filterString' (newVal, oldVal){
            this.filterObject();
        }
    },
    methods: {
    	handleCurrentChange(val) {
    		this.currentRow = val;
    	},
    	selectMdaTable() {
    		if(this.currentRow == null || this.currentRow.tableName == null) {
    			ElMessage.error(`请先选择一条数据库表！`);
    			return;
    		}
    		this.mdaTableSelected(this.currentRow);
        },
        filterObject() {
            if(this.filterString == null || this.filterString.trim().length == 0) {
                this.filteredObjects = this.mdaTablesForSelect;
            }else {
                if(this.mdaTablesForSelect != null && this.mdaTablesForSelect.length > 0) {
                    var tempObjects = [];
                    this.mdaTablesForSelect.forEach((row)=>{
                        if(row.tableName.indexOf(this.filterString) >= 0) {
                        	tempObjects.push(row);
                        } 
                    });
                    this.filteredObjects = tempObjects;
                }
            }
        }
    }
}

</script>