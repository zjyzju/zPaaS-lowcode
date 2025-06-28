<!-- 数据映射规则查询选择页 -->
<template>
<el-dialog v-model="showFlag" title="数据映射规则选择" width="800px">
    <template #header>
        <span class="title">数据映射规则选择</span>
    </template>
    <div>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="对象类型">
                      <el-select v-model="objectType" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in objectTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="9">
                <el-form-item label="数据转换对象">
                    <el-input type="hidden" v-model="objectId"   size="small"/>
                    <el-input v-model="objectCode" :readonly="true"  size="small"/>
                    
                </el-form-item>
            </el-col>
            <el-col :span="3">
                <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectObject()" ><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearObject()" ><label>清空</label></el-link>                                                
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="20" class="toolbar-right">
                <el-button type="primary" size="small" @click="queryDataMapping()">查询</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            </el-col>
            <el-col :span="4" class="toolbar-right">&nbsp;</el-col>
        </el-row>

        <!-- 数据映射列表 -->
        <br/>
        <el-row :gutter="4">
            <el-col :span="1"></el-col>
            <el-col :span="23">
                <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
                <div class="toolbar1">
                    <el-icon><histogram /></el-icon><span>数据映射列表</span>&nbsp;&nbsp;&nbsp;&nbsp;
                </div>
                </el-divider>
            </el-col>
        </el-row>

        <el-scrollbar  height="350px">
            <el-form  label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="dataMappingSelectTable" :data="dataMappings" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
                                <el-table-column prop="id" label="标识" width="251" />
                                <el-table-column prop="name" label="名称" width="200" />
                                <el-table-column prop="fromObjectType" label="源对象" width="190" >
                                    <template #default="scope">
	                                    <div style="display: flex; align-items: center">
	                                      <span v-if="scope.row.fromObjectType == 'D'">领域对象({{scope.row.fromObject.code}})</span>
	                                      <span v-if="scope.row.fromObjectType == 'V'">值传递对象({{scope.row.fromObject.code}})</span>
	                                    </div>
	                                </template>
                                </el-table-column>
                                <el-table-column prop="toObjectType" label="目标对象类型" width="190" >
                                    <template #default="scope">
                                        <div style="display: flex; align-items: center">
                                          <span v-if="scope.row.toObjectType == 'D'">领域对象({{scope.row.toObject.code}})</span>
                                          <span v-if="scope.row.toObjectType == 'V'">值传递对象({{scope.row.toObject.code}})</span>
                                        </div>
                                    </template>
                                </el-table-column>
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
    </div>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDataMappingSelectPage()">取消</el-button>
            <el-button type="primary" @click="selectDataMapping()">确定</el-button>
        </span>
    </template>
</el-dialog>
<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideDomainObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedDObjectsForSelect"/>
<!-- 选择值传递对象信息 -->
<valueObjectSelect v-if="showValueObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideValueObjectSelectPage="hideValueObjectSelectPage" :showValueObjectSelect="showValueObjectSelect"  :managedObjectsForSelect="managedRObjectsForSelect"/>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'
import domainObjectSelect from './workbench-domainObjectSelect.vue';
import valueObjectSelect from './workbench-valueObjectSelect.vue';

export default {
    props: ['showDataMappingSelect', 'systemId'],
    
    emits: [ 'hideDataMappingSelectPage', 'dataMappingSelected'],
    
    setup(props, {attrs, emit, slots}) {
        const hideDataMappingSelectPage = () => {
            emit('hideDataMappingSelectPage');
        };
        
        const dataMappingSelected = (mappings, type) => {
            emit('dataMappingSelected', mappings, type);
        };
        return {
        	hideDataMappingSelectPage,
        	dataMappingSelected
        }
    },
    components: {
        domainObjectSelect,
        valueObjectSelect
    },
    computed: {
        showFlag: {
            get() {
                return this.showDataMappingSelect;
            },
            set(value) {
                this.hideDataMappingSelectPage();
            }
        }
    },
    data() {
    	const objectTypeOptions = ref(null);
        const objectType = ref("D");
        const objectId = ref(null);
        const objectCode = ref(null);
        const managedDObjectsForSelect = ref([]);
        const showDomainObjectSelect = ref(false);
        const showValueObjectSelect = ref(false);
        const managedRObjectsForSelect = ref([]);
    	
    	const currentRow = ref();

        const dataMappings = ref(null);
    	
        return {
    		currentRow,
            objectTypeOptions,
            objectType,
            objectId,
            objectCode,
            managedDObjectsForSelect,
            managedRObjectsForSelect,
            showDomainObjectSelect,
            showValueObjectSelect,
            dataMappings
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_TYPE_HAS_ATTRIBUTE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectTypeOptions = data.data['OBJECT_TYPE_HAS_ATTRIBUTE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	handleCurrentChange(val) {
    		this.currentRow = val;
    	},
    	selectDataMapping() {
    		if(this.currentRow == null || this.currentRow.id == null) {
    			ElMessage.error(`请先选择一条数据映射规则！`);
    			return;
    		}
    		if(this.dataMappingSelectType == 'D' && this.currentRow.toObjectType != 'D') {
                ElMessage.error(`目标对象类型不是领域对象！`);
                return;
            }
    		this.dataMappingSelected(this.currentRow, this.dataMappingSelectType);
        },
        selectObject() {
            if("D" == this.objectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedDObjectsForSelect = data.data;
                        this.showDomainObjectSelect = true;
                    }
                });
            }else if("V" == this.objectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedRObjectsForSelect = data.data;
                        this.showValueObjectSelect = true;
                    }
                });
            }else {
                return;
            }
        },
        clearObject(){
            this.objectId = "";
            this.objectCode = "";
        },
        managedObjectSelected(obj) {
            this.objectId = obj.id;
            this.objectCode = obj.code;
            this.managedDObjectsForSelect = null;
            this.managedRObjectsForSelect = null;
            this.showDomainObjectSelect = false;
            this.showValueObjectSelect = false;
        },
        hideDomainObjectSelectPage(){
             this.managedDObjectsForSelect = null;
             this.showDomainObjectSelect = false;
        },
        hideValueObjectSelectPage(){
            this.managedRObjectsForSelect = null;
            this.showValueObjectSelect = false;
        },
        queryDataMapping() {
            if(this.objectType ==null || this.objectType.trim().length == 0 || this.objectId == null || this.objectId.trim().length == 0) {
                ElMessage.error(`请先选择对象类型和数据转换对象！`);
    			return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/dataMapping/listByObject/" + this.objectType + "/" + this.objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dataMappings = data.data;
                }
            });
        }
    }
}

</script>