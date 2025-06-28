<!-- 数据集选择页 -->
<template>
<el-dialog v-model="showFlag" v-if="showDataSetSelect ==true && dataSetsForSelect != null" title="数据集选择" width="850px">
    <template #header>
        <span class="title">数据集选择</span>
    </template>
    <div>
        <el-form-item label="过滤对象">
            <el-input v-model="filterString" size="small"/>
        </el-form-item>
    </div>
    <div>
        <el-scrollbar height="350px">
            <el-form v-if="showDataSetSelect && dataSetsForSelect" label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="dataSetSelectTable" :data="filteredObjects" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
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
            <el-button @click="hideDataSetSelectPage()">取消</el-button>
            <el-button type="primary" @click="selectDataSet()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['showDataSetSelect', 'dataSetsForSelect'],
    
    emits: [ 'hideDataSetSelectPage', 'dataSetSelected'],
    
    setup(props, {attrs, emit, slots}) {
        const hideDataSetSelectPage = () => {
            emit('hideDataSetSelectPage');
        };
        
        const dataSetSelected = (obj) => {
            emit('dataSetSelected', obj);
        };
        return {
        	hideDataSetSelectPage,
        	dataSetSelected
        }
    },  
    computed: {
        showFlag: {
            get() {
                return this.showDataSetSelect;
            },
            set(value) {
                this.hideDataSetSelectPage();
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
    	this.filteredObjects = this.dataSetsForSelect;
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
    	selectDataSet() {
    		if(this.currentRow == null || this.currentRow.id == null) {
    			ElMessage.error(`请先选择一个数据集！`);
    			return;
    		}
    		this.dataSetSelected(this.currentRow);
        },
        filterObject() {
            if(this.filterString == null || this.filterString.trim().length == 0) {
                this.filteredObjects = this.dataSetsForSelect;
            }else {
                if(this.dataSetsForSelect != null && this.dataSetsForSelect.length > 0) {
                    var tempObjects = [];
                    this.dataSetsForSelect.forEach((row)=>{
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