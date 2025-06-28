<!-- 数据模型选择页 -->
<template>
<el-dialog v-model="showFlag" v-if="showDataModelSelect ==true && dataModelsForSelect != null" title="数据模型选择" width="850px">
    <template #header>
        <span class="title">数据模型选择</span>
    </template>
    <div>
        <el-form-item label="过滤对象">
            <el-input v-model="filterString" size="small"/>
        </el-form-item>
    </div>
    <div>
        <el-scrollbar height="350px">
            <el-form v-if="showDataModelSelect && dataModelsForSelect" label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="dataModelSelectTable" :data="filteredObjects" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
                                <el-table-column prop="id" label="标识" width="251" />
                                <el-table-column prop="code" label="编码" show-overflow-tooltip width="180" />
                                <el-table-column prop="name" label="名称" show-overflow-tooltip width="180" />
                                <el-table-column prop="description" show-overflow-tooltip width="200" label="描述" />
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
    </div>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDataModelSelectPage()">取消</el-button>
            <el-button type="primary" @click="selectDataModel()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['showDataModelSelect', 'dataModelsForSelect'],
    
    emits: [ 'hideDataModelSelectPage', 'dataModelSelected'],
    
    setup(props, {attrs, emit, slots}) {
        const hideDataModelSelectPage = () => {
            emit('hideDataModelSelectPage');
        };
        
        const dataModelSelected = (obj) => {
            emit('dataModelSelected', obj);
        };
        return {
        	hideDataModelSelectPage,
        	dataModelSelected
        }
    },  
    computed: {
        showFlag: {
            get() {
                return this.showDataModelSelect;
            },
            set(value) {
                this.hideDataModelSelectPage();
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
    	this.filteredObjects = this.dataModelsForSelect;
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
    	selectDataModel() {
    		if(this.currentRow == null || this.currentRow.id == null) {
    			ElMessage.error(`请先选择一个数据模型！`);
    			return;
    		}
    		this.dataModelSelected(this.currentRow);
        },
        filterObject() {
            if(this.filterString == null || this.filterString.trim().length == 0) {
                this.filteredObjects = this.dataModelsForSelect;
            }else {
                if(this.dataModelsForSelect != null && this.dataModelsForSelect.length > 0) {
                    var tempObjects = [];
                    this.dataModelsForSelect.forEach((row)=>{
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