<!-- 领域对象选择页 -->
<template>
<el-dialog v-model="showFlag" v-if="showDomainObjectSelect ==true && managedObjectsForSelect != null" title="领域对象选择" width="800px">
    <template #header>
        <span class="title">领域对象选择</span>
    </template>
    <div>
        <el-form-item label="过滤对象">
            <el-input v-model="filterString" size="small"/>
        </el-form-item>
    </div>
    <div>
        <el-scrollbar height="350px">
            <el-form v-if="showDomainObjectSelect && managedObjectsForSelect" label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="managedObjectSelectTable" :data="filteredObjects" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
                                <el-table-column prop="id" label="标识" width="251" />
                                <el-table-column prop="code" label="编码" width="220" />
                                <el-table-column prop="name" label="名称" width="140" />
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
    </div>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDomainObjectSelectPage()">取消</el-button>
            <el-button type="primary" @click="selectDomainObject()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['showDomainObjectSelect', 'managedObjectsForSelect'],
    
    emits: [ 'hideDomainObjectSelectPage', 'managedObjectSelected'],
    
    setup(props, {attrs, emit, slots}) {
        const hideDomainObjectSelectPage = () => {
            emit('hideDomainObjectSelectPage');
        };
        
        const managedObjectSelected = (obj) => {
            emit('managedObjectSelected', obj);
        };
        return {
        	hideDomainObjectSelectPage,
        	managedObjectSelected
        }
    },  
    computed: {
        showFlag: {
            get() {
                return this.showDomainObjectSelect;
            },
            set(value) {
                this.hideDomainObjectSelectPage();
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
    	this.filteredObjects = this.managedObjectsForSelect;
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
    	selectDomainObject() {
    		if(this.currentRow == null || this.currentRow.id == null) {
    			ElMessage.error(`请先选择一条领域对象！`);
    			return;
    		}
    		this.managedObjectSelected(this.currentRow);
        },
        filterObject() {
            if(this.filterString == null || this.filterString.trim().length == 0) {
                this.filteredObjects = this.managedObjectsForSelect;
            }else {
                if(this.managedObjectsForSelect != null && this.managedObjectsForSelect.length > 0) {
                    var tempObjects = [];
                    this.managedObjectsForSelect.forEach((row)=>{
                        if(row.code.indexOf(this.filterString) >= 0) {
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