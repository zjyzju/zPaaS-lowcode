<!-- 系统功能选择页 -->
<template>
<el-dialog v-model="showFlag" v-if="showFuncDefineSelect ==true && funcDefinesForSelect != null" title="系统功能选择" width="800px">
    <template #header>
        <span class="title">系统功能选择</span>
    </template>
    <div>
        <el-form-item label="过滤对象">
            <el-input v-model="filterString" size="small"/>
        </el-form-item>
    </div>
    <div>
        <el-scrollbar height="350px">
            <el-form v-if="showFuncDefineSelect && funcDefinesForSelect" label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="funcDefineSelectTable" :data="filteredObjects" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
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
            <el-button @click="hideFuncDefineSelectPage()">取消</el-button>
            <el-button type="primary" @click="selectFuncDefine()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['showFuncDefineSelect', 'funcDefinesForSelect'],
    
    emits: [ 'hideFuncDefineSelectPage', 'funcDefineSelected'],
    
    setup(props, {attrs, emit, slots}) {
        const hideFuncDefineSelectPage = () => {
            emit('hideFuncDefineSelectPage');
        };
        
        const funcDefineSelected = (obj) => {
            emit('funcDefineSelected', obj);
        };
        return {
        	hideFuncDefineSelectPage,
        	funcDefineSelected
        }
    },  
    computed: {
        showFlag: {
            get() {
                return this.showFuncDefineSelect;
            },
            set(value) {
                this.hideFuncDefineSelectPage();
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
    	this.filteredObjects = this.funcDefinesForSelect;
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
    	selectFuncDefine() {
    		if(this.currentRow == null || this.currentRow.id == null) {
    			ElMessage.error(`请先选择一个系统功能！`);
    			return;
    		}
    		this.funcDefineSelected(this.currentRow);
        },
        filterObject() {
            if(this.filterString == null || this.filterString.trim().length == 0) {
                this.filteredObjects = this.funcDefinesForSelect;
            }else {
                if(this.funcDefinesForSelect != null && this.funcDefinesForSelect.length > 0) {
                    var tempObjects = [];
                    this.funcDefinesForSelect.forEach((row)=>{
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