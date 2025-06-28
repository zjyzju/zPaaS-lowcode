<!-- 对外服务选择页 -->
<template>
<el-dialog v-model="showFlag" title="对外服务选择" width="800px">
    <template #header>
        <span class="title">对外服务选择</span>
    </template>
    <div>
        <el-form-item label="过滤对象">
            <el-input v-model="filterString" size="small"/>
        </el-form-item>
    </div>
    <div>
        <el-scrollbar height="350px">
            <el-form v-if="showExposedServiceSelect && exposedServicesForSelect" label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="managedObjectSelectTable" :data="filteredObjects" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
                                <el-table-column prop="httpMapping" label="服务URL" width="220" />
                                <el-table-column prop="serviceObject.code" label="对应服务" width="200" />
                                <el-table-column prop="operation.code" label="对应服务方法" width="240"  />
                                <el-table-column prop="httpMethod" label="HttpMethod" />
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
    </div>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideExposedServiceSelectPage()">取消</el-button>
            <el-button type="primary" @click="selectExposedService()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['showExposedServiceSelect', 'exposedServicesForSelect'],
    
    emits: [ 'hideExposedServiceSelectPage', 'exposedServiceSelected'],
    
    setup(props, {attrs, emit, slots}) {
        const hideExposedServiceSelectPage = () => {
            emit('hideExposedServiceSelectPage');
        };
        
        const exposedServiceSelected = (obj) => {
            emit('exposedServiceSelected', obj);
        };
        return {
        	hideExposedServiceSelectPage,
        	exposedServiceSelected
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showExposedServiceSelect;
            },
            set(value) {
                this.hideExposedServiceSelectPage();
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
        this.filteredObjects = this.exposedServicesForSelect;
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
    	selectExposedService() {
    		if(this.currentRow == null || this.currentRow.id == null) {
    			ElMessage.error(`请先选择一条服务对象！`);
    			return;
    		}
    		this.exposedServiceSelected(this.currentRow);
        },
        filterObject() {
            if(this.filterString == null || this.filterString.trim().length == 0) {
                this.filteredObjects = this.exposedServicesForSelect;
            }else {
                if(this.exposedServicesForSelect != null && this.exposedServicesForSelect.length > 0) {
                    var tempObjects = [];
                    this.exposedServicesForSelect.forEach((row)=>{
                        if(row.serviceObject.code.indexOf(this.filterString) >= 0 || row.operation.code.indexOf(this.filterString) >= 0) {
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