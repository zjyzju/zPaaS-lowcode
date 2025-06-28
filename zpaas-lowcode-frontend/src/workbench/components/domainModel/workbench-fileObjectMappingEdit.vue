<!-- 文件对象映射信息编辑页 -->
 <template>
<el-drawer v-model="showFlag" direction="rtl" size="60%">
    <template #header>
        <span class="title">文件对象映射信息编辑</span>
    </template>
    <template #default>
        <div>
        <el-scrollbar>
            <div>
                <el-link  type="primary" @click="showFileObjectMappingCreatePage()">新建文件对象映射信息</el-link>
            </div>
            <el-form v-if="showFileObjectMappingEdit && fileObjectMappings" v-for="fileObjectMapping in fileObjectMappings" :model="fileObjectMapping" label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
		                <el-divider content-position="left"  >
		                    <div class="toolbar1">
		                        <span class="title">文件对象映射：{{fileObjectMapping.name}}&nbsp;&nbsp;&nbsp;&nbsp;
                                <el-link  type="primary" @click="showFileObjectMappingModifyPage(fileObjectMapping.id)">编辑</el-link></span>&nbsp;&nbsp;&nbsp;&nbsp;
		                    </div>
		                </el-divider>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="标识">
                            <el-input v-model="fileObjectMapping.id"  size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="名称">
                            <el-input v-model="fileObjectMapping.name"  size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="对象类型">
                            <el-select v-model="fileObjectMapping.objectType" class="m-2" placeholder="Select"  size="small" disabled>
			                    <el-option v-for="item in objectTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
			                </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="对象">
                            <el-input v-model="fileObjectMapping.objectCode" readonly size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="文件类型">
                            <el-select v-model="fileObjectMapping.fileType" class="m-2" placeholder="Select"  size="small">
                                <el-option v-for="item in fileTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="文件说明">
                            <el-input v-model="fileObjectMapping.fileDesc"  size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="映射方向">
                            <el-select v-model="fileObjectMapping.mappingDirect" class="m-2" placeholder="Select"  size="small">
			                    <el-option v-for="item in mappingDirectOptions" :key="item.value" :label="item.label" :value="item.value" />
			                </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12"></el-col>
                        
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="创建时间">
                            <el-input v-model="fileObjectMapping.createTime"  size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="更新时间">
                            <el-input v-model="fileObjectMapping.updateTime"  size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <br/>
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 12px">
                        <div class="toolbar1">
                            <el-icon><histogram /></el-icon><span>属性列映射</span>&nbsp;&nbsp;&nbsp;&nbsp;
                            
                        </div>
                        </el-divider>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="fileObjectMapping.attributeColumnMappings">
                    <el-col :span="1"/>
                    <el-col :span="23">
                        <el-table :data="fileObjectMapping.attributeColumnMappings" stripe style="width: 100%">
                            <el-table-column prop="attributeCode" label="属性" width="150" />
                            <el-table-column prop="columnIndex" label="列序号" width="80" />
                            <el-table-column prop="headerName" label="列头" width="150"/>
                            <el-table-column prop="needMerge" label="是否合并" width="100" >
                                <template #default="scope">
                                    <div style="display: flex; align-items: center" >
                                        <span v-if="scope.row.needMerge == 'Y'">是</span>
                                        <span v-if="scope.row.needMerge == 'N'">否</span>
                                    </div>
                                </template>
                            </el-table-column>
                            <el-table-column prop="isSum" label="是否累加" width="100" >
                                <template #default="scope">
                                    <div style="display: flex; align-items: center" >
                                        <span v-if="scope.row.isSum == 'Y'">是</span>
                                        <span v-if="scope.row.isSum == 'N'">否</span>
                                    </div>
                                </template>
                            </el-table-column>
                            <el-table-column prop="sumByColumnIndex" label="累加维度列" width="120" />
                            <el-table-column prop="sumColumnIndex" label="累加列" width="80" />
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
        </div>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="cancelEditFileObjectMapping()">取消</el-button>
      </div>
    </template>
</el-drawer>

<!-- 新建对象关系映射信息 -->
<createFileObjectMapping v-if="showFileObjectMappingCreate==true && newFileObjectMapping != null"  @hideFileObjectMappingCreatePage="hideFileObjectMappingCreatePage" @addNewFileObjectMappingToList="addNewFileObjectMappingToList" :showFileObjectMappingCreate="showFileObjectMappingCreate"  :newFileObjectMapping="newFileObjectMapping"/>
<!-- 修改对象关系映射信息 -->
<modifyFileObjectMapping v-if="showFileObjectMappingModify==true && modifyFileObjectMapping != null"  @hideFileObjectMappingModifyPage="hideFileObjectMappingModifyPage" @updateFileObjectMappingToList="updateFileObjectMappingToList" :showFileObjectMappingModify="showFileObjectMappingModify"  :modifyFileObjectMapping="modifyFileObjectMapping"/>

</template>


<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

import createFileObjectMapping from './workbench-fileObjectMappingCreate.vue'
import modifyFileObjectMapping from './workbench-fileObjectMappingModify.vue'

export default {
	props: ['fileObjectMappings','showFileObjectMappingEdit', 'objectType', 'objectId', 'tenantId', 'systemId'],
	
	emits: ['hideEditFileObjectMappingPage'],
	
	components: {
		createFileObjectMapping,
		modifyFileObjectMapping
	},
	
	setup (props, {attrs, emit, slots}) {
		const cancelEditFileObjectMapping = ()=> {
	    	emit('hideEditFileObjectMappingPage');
	    };
	    
	    return {
	    	cancelEditFileObjectMapping
	    };
	},
	computed: {
        showFlag: {
            get() {
                return this.showFileObjectMappingEdit;
            },
            set(value) {
                this.cancelEditFileObjectMapping();
            }
        }
    },
    data() {    		
		const showFileObjectMappingCreate = ref(false);
        const newFileObjectMapping = ref({});
        
        const showFileObjectMappingModify = ref(false);
        const modifyFileObjectMapping = ref({});

        const mappingDirectOptions = ref(null);

        const fileTypeOptions = ref(null);
        
        const objectTypeOptions = ref(null);
        
        return {
            showFileObjectMappingCreate,
            newFileObjectMapping,
            
            showFileObjectMappingModify,
            modifyFileObjectMapping,
            
            objectTypeOptions,
            mappingDirectOptions,
            fileTypeOptions
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_TYPE_HAS_ATTRIBUTE','FILE_OBJECT_MAPPING_DIRECTION','FILE_OBJECT_MAPPING_FILE_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectTypeOptions = data.data['OBJECT_TYPE_HAS_ATTRIBUTE'];
                this.mappingDirectOptions = data.data['FILE_OBJECT_MAPPING_DIRECTION'];
                this.fileTypeOptions = data.data['FILE_OBJECT_MAPPING_FILE_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	showFileObjectMappingCreatePage() {
            this.newFileObjectMapping = {
                id : "",
                name : "",
                objectType : this.objectType,
                objectId : this.objectId,
                mappingDirect : "",
                fileDesc : "",
                fileType: "",
                status : "",
                tenantId : this.tenantId,
                systemId : this.systemId,
                createTime : "",
                updateTime : "",
                attributeColumnMappings : [],
                object : {}
            }
            
            this.showFileObjectMappingCreate = true;
        },
        
        addNewFileObjectMappingToList(newFileObjectMapping) {
            if(this.fileObjectMappings == null) {
                this.fileObjectMappings = [];
            }
            this.fileObjectMappings.push(newFileObjectMapping);
            this.showFileObjectMappingCreate = false;
        },
        hideFileObjectMappingCreatePage() {
            this.showFileObjectMappingCreate = false;
        },
        showFileObjectMappingModifyPage(id) {
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