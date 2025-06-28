<!-- 文件对象映射规则查询选择页 -->
<template>
<el-dialog v-model="showFlag" title="文件对象映射规则选择" width="800px">
    <template #header>
        <span class="title">文件对象映射规则选择</span>
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
                <el-form-item label="对象">
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
                <el-button type="primary" size="small" @click="queryFileObjectMapping()">查询</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            </el-col>
            <el-col :span="4" class="toolbar-right">&nbsp;</el-col>
        </el-row>

        <!-- 文件对象映射列表 -->
        <br/>
        <el-row :gutter="4">
            <el-col :span="1"></el-col>
            <el-col :span="23">
                <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
                <div class="toolbar1">
                    <el-icon><histogram /></el-icon><span>文件对象映射列表</span>&nbsp;&nbsp;&nbsp;&nbsp;
                </div>
                </el-divider>
            </el-col>
        </el-row>

        <el-scrollbar  height="350px">
            <el-form  label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="fileObjectMappingSelectTable" :data="fileObjectMappings" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
                            <el-table-column  label="操作" width="60">
                                <template #default="scope">
                                    <el-link  type="primary" @click="editFileObjectMapping(scope.row.id)">编辑</el-link>
                                </template>
                            </el-table-column>
                            <el-table-column prop="id" label="标识" width="251" />
                            <el-table-column prop="name" label="名称" width="200" />
                            <el-table-column prop="objectType" label="对象类型" width="100" >
                                <template #default="scope">
	                                <div style="display: flex; align-items: center">
	                                  <span v-if="scope.row.objectType == 'D'">领域对象</span>
	                                  <span v-if="scope.row.objectType == 'V'">值传递对象</span>
	                                </div>
	                            </template>
                            </el-table-column>
                            <el-table-column prop="objectCode" label="对象" width="120" />
                            <el-table-column prop="mappingDirect" label="映射方向" width="100" >
                                <template #default="scope">
                                    <div style="display: flex; align-items: center">
                                      <span v-if="scope.row.mappingDirect == 'F'">文件到对象</span>
                                      <span v-if="scope.row.mappingDirect == 'O'">对象到文件</span>
                                    </div>
                                </template>
                            </el-table-column>
                            <el-table-column prop="fileDesc" label="文件说明" width="150" />
                            <el-table-column prop="fileType" label="文件类型" width="100" >
                                <template #default="scope">
                                    <div style="display: flex; align-items: center">
                                      <span v-if="scope.row.fileType == 'E'">Excel文件</span>
                                      <span v-if="scope.row.fileType == 'T'">Text文件</span>
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
            <el-button @click="hideFileObjectMappingSelectPage()">取消</el-button>
            <el-button type="primary" @click="selectFileObjectMapping()">确定</el-button>
        </span>
    </template>
</el-dialog>
<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideDomainObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedDObjectsForSelect"/>
<!-- 选择值传递对象信息 -->
<valueObjectSelect v-if="showValueObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideValueObjectSelectPage="hideValueObjectSelectPage" :showValueObjectSelect="showValueObjectSelect"  :managedObjectsForSelect="managedRObjectsForSelect"/>
<!-- 修改对象关系映射信息 -->
<modifyFileObjectMapping v-if="showFileObjectMappingModify==true && modifyFileObjectMapping != null"  @hideFileObjectMappingModifyPage="hideFileObjectMappingModifyPage" @updateFileObjectMappingToList="updateFileObjectMappingToList" :showFileObjectMappingModify="showFileObjectMappingModify"  :modifyFileObjectMapping="modifyFileObjectMapping"/>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'
import domainObjectSelect from './workbench-domainObjectSelect.vue';
import valueObjectSelect from './workbench-valueObjectSelect.vue';
import modifyFileObjectMapping from './workbench-fileObjectMappingModify.vue'

export default {
    props: ['showFileObjectMappingSelect', 'systemId'],
    
    emits: [ 'hideFileObjectMappingSelectPage', 'fileObjectMappingSelected'],
    
    setup(props, {attrs, emit, slots}) {
        const hideFileObjectMappingSelectPage = () => {
            emit('hideFileObjectMappingSelectPage');
        };
        
        const fileObjectMappingSelected = (mappings, type) => {
            emit('fileObjectMappingSelected', mappings, type);
        };
        return {
        	hideFileObjectMappingSelectPage,
        	fileObjectMappingSelected
        }
    },
    components: {
        domainObjectSelect,
        valueObjectSelect,
        modifyFileObjectMapping
    },
    computed: {
        showFlag: {
            get() {
                return this.showFileObjectMappingSelect;
            },
            set(value) {
                this.hideFileObjectMappingSelectPage();
            }
        }
    },
    data() {
    	const objectTypeOptions = [
            {
                value: 'D',
                label: '领域对象'
            },
            {
                value: 'V',
                label: '值传递对象'
            }
        ];
        const objectType = ref("D");
        const objectId = ref(null);
        const objectCode = ref(null);
        const managedDObjectsForSelect = ref([]);
        const showDomainObjectSelect = ref(false);
        const showValueObjectSelect = ref(false);
        const managedRObjectsForSelect = ref([]);
    	
    	const currentRow = ref();

        const fileObjectMappings = ref(null);

        const showFileObjectMappingModify = ref(false);
        const modifyFileObjectMapping = ref({});
    	
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
            showFileObjectMappingModify,
            modifyFileObjectMapping,
            fileObjectMappings
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
    	selectFileObjectMapping() {
    		if(this.currentRow == null || this.currentRow.id == null) {
    			ElMessage.error(`请先选择一条文件对象映射规则！`);
    			return;
    		}
    		
    		this.fileObjectMappingSelected(this.currentRow);
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
        queryFileObjectMapping() {
            if(this.objectType ==null || this.objectType.trim().length == 0 || this.objectId == null || this.objectId.trim().length == 0) {
                ElMessage.error(`请先选择对象类型和对象！`);
    			return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/fileObjectMapping/listByObject/" + this.objectType + "/" + this.objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.fileObjectMappings = data.data;
                }
            });
        },
       editFileObjectMapping(id) {
            if(this.fileObjectMappings != null && this.fileObjectMappings.length > 0) {
                this.fileObjectMappings.forEach((fom)=>{
                    if(fom.id == id) {
                        this.modifyFileObjectMapping = fom;
                        return;
                    }
                });
            }
            if(this.modifyFileObjectMapping == null) {
                return;
            }
            this.modifyFileObjectMapping.object = {};
            this.showFileObjectMappingModify = true;
        },
        updateFileObjectMappingToList(fileObjectMapping) {
            if(this.fileObjectMappings == null) {
                this.fileObjectMappings = [];
            }
            for(var i in this.fileObjectMappings){
                if(this.fileObjectMappings[i].id == fileObjectMapping.id) {
                    this.fileObjectMappings.splice(i,1);
                }
            }
            this.fileObjectMappings.push(fileObjectMapping);
            this.showFileObjectMappingModify = false;
        },
        hideFileObjectMappingModifyPage() {
            this.showFileObjectMappingModify = false;
        }
    }
}

</script>