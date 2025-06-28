<!-- 对象关系映射规则选择页 -->
<template>
<el-dialog v-model="showFlag" title="对象关系映射规则选择" width="800px">
    <template #header>
        <span class="title">对象关系映射规则选择</span>
    </template>
    <div>
        <el-form-item label="过滤对象">
            <el-input v-model="filterString" size="small"/>
        </el-form-item>
    </div>
    <div>
        <el-scrollbar height="350px">
            <el-form v-if="showObjectRelationMappingSelect && objectRelationMappingsForSelect" label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="objectRelationMappingSelectTable" :data="filteredObjects" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
                                <el-table-column prop="id" label="标识" width="251" />
                                <el-table-column prop="name" label="名称" width="220" />
                                <el-table-column prop="relDomainObject.code" label="领域对象" width="140" />
                                <el-table-column prop="relDbTable.name" label="关联数据库"/>
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
    </div>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideObjectRelationMappingSelectPage()">取消</el-button>
            <el-button type="primary" @click="selectObjectRelationMapping()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['showObjectRelationMappingSelect', 'objectRelationMappingsForSelect'],
    
    emits: [ 'hideObjectRelationMappingSelectPage', 'objectRelationMappingSelected'],
    
    setup(props, {attrs, emit, slots}) {
        const hideObjectRelationMappingSelectPage = () => {
            emit('hideObjectRelationMappingSelectPage');
        };
        
        const objectRelationMappingSelected = (mappings) => {
            emit('objectRelationMappingSelected', mappings);
        };
        return {
        	hideObjectRelationMappingSelectPage,
        	objectRelationMappingSelected
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showObjectRelationMappingSelect;
            },
            set(value) {
                this.hideObjectRelationMappingSelectPage();
            }
        }
    },
    mounted() {
        this.filteredObjects = this.objectRelationMappingsForSelect;
    },
    watch: {
        'filterString' (newVal, oldVal){
            this.filterObject();
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
    
    methods: {
    	handleCurrentChange(val) {
    		this.currentRow = val;
    	},
    	selectObjectRelationMapping() {
    		if(this.currentRow == null || this.currentRow.id == null) {
    			ElMessage.error(`请先选择一条对象关系映射规则！`);
    			return;
    		}
    		this.objectRelationMappingSelected(this.currentRow);
        },
        filterObject() {
            if(this.filterString == null || this.filterString.trim().length == 0) {
                this.filteredObjects = this.objectRelationMappingsForSelect;
            }else {
                if(this.objectRelationMappingsForSelect != null && this.objectRelationMappingsForSelect.length > 0) {
                    var tempObjects = [];
                    this.objectRelationMappingsForSelect.forEach((row)=>{
                        if(row.relDomainObject.code.indexOf(this.filterString) >= 0) {
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