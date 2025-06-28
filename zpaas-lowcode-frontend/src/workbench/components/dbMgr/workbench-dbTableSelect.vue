<!-- 数据库表选择页 -->
<template>
<el-dialog v-model="showFlag" v-if="showDbTableSelect ==true && dbTablesForSelect != null" title="数据库表选择" width="800px">
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
            <el-form v-if="showDbTableSelect && dbTablesForSelect" label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="dbTableSelectTable" :data="filteredObjects" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
                                <el-table-column prop="id" label="标识" width="251" />
                                <el-table-column prop="name" label="名称" width="251" />
                                <el-table-column prop="description" label="描述" />
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
    </div>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDbTableSelectPage()">取消</el-button>
            <el-button type="primary" @click="selectDbTable()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['showDbTableSelect', 'dbTablesForSelect'],
    
    emits: [ 'hideDbTableSelectPage', 'dbTableSelected'],
    
    setup(props, {attrs, emit, slots}) {
        const hideDbTableSelectPage = () => {
            emit('hideDbTableSelectPage');
        };
        
        const dbTableSelected = (obj) => {
            emit('dbTableSelected', obj);
        };
        return {
        	hideDbTableSelectPage,
        	dbTableSelected
        }
    },  
    computed: {
        showFlag: {
            get() {
                return this.showDbTableSelect;
            },
            set(value) {
                this.hideDbTableSelectPage();
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
    	this.filteredObjects = this.dbTablesForSelect;
    },
    watch: {
        'filterString': function(newVal, oldVal){
            this.filterObject();
        }
    },
    methods: {
    	handleCurrentChange(val) {
    		this.currentRow = val;
    	},
    	selectDbTable() {
    		if(this.currentRow == null || this.currentRow.id == null) {
    			ElMessage.error(`请先选择一条数据库表！`);
    			return;
    		}
    		this.dbTableSelected(this.currentRow);
        },
        filterObject() {
            if(this.filterString == null || this.filterString.trim().length == 0) {
                this.filteredObjects = this.dbTablesForSelect;
            }else {
                if(this.dbTablesForSelect != null && this.dbTablesForSelect.length > 0) {
                    var tempObjects = [];
                    this.dbTablesForSelect.forEach((row)=>{
                        if(row.name.indexOf(this.filterString) >= 0) {
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