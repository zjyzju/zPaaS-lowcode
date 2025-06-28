<!-- 编辑文件对象映射信息 -->
<template>
<el-dialog v-model="showFlag" title="编辑文件对象映射信息" width="800px">
    <template #header>
        <span class="title">编辑文件对象映射信息</span>
    </template>
    <el-form  :model="modifyFileObjectMapping" label-width="120px" :rules="validateRules" ref="modifyFileObjectMappingForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="标识" required prop="id">
                    <el-input v-model="modifyFileObjectMapping.id" readonly  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="modifyFileObjectMapping.name"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="对象类型" required prop="objectType">
                    <el-select v-model="modifyFileObjectMapping.objectType" class="m-2" placeholder="Select" size="small" disabled>
                        <el-option v-for="item in objectTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="对象" required prop="objectId">
                    <el-input v-model="modifyFileObjectMapping.objectId" type="hidden" readonly size="small"/>
                    <el-input v-model="modifyFileObjectMapping.object.code" readonly size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="映射方向" required prop="mappingDirect">
                    <el-select v-model="modifyFileObjectMapping.mappingDirect" class="m-2" placeholder="Select" size="small" clearable>
                        <el-option v-for="item in mappingDirectOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="文件说明">
                    <el-input v-model="modifyFileObjectMapping.fileDesc" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="文件类型" required prop="fileType">
                    <el-select v-model="modifyFileObjectMapping.fileType" class="m-2" placeholder="Select" size="small" clearable>
                        <el-option v-for="item in fileTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 12px">
                <div class="toolbar1">
                    <span class="title">属性列映射</span>&nbsp;&nbsp;&nbsp;&nbsp;
                </div>
                </el-divider>
            </el-col>
        </el-row>
        <el-row :gutter="4">
		    <el-col :span="2"></el-col>
		    <el-col :span="20">
		        <el-row :gutter="4">
		            <el-col :span="12">
		                <el-form-item label="对象属性">
		                    <el-select v-model="attributeId" class="m-2" placeholder="Select" size="small" clearable>
		                        <el-option v-for="item in modifyFileObjectMapping.object.attributes" :key="item.id" :label="item.code" :value="item.id" />
		                    </el-select>
		                </el-form-item>
		            </el-col>
		            <el-col :span="12">
		                <el-form-item label="列序号">
                            <el-input v-model="columnIndex" size="small"/>
                        </el-form-item>
		            </el-col>
		        </el-row>
		    </el-col>  
		    <el-col :span="2"></el-col>                 
		</el-row>
        <el-row :gutter="4">
		    <el-col :span="2"></el-col>
		    <el-col :span="20">
		        <el-row :gutter="4">
                    <el-col :span="12">
		                <el-form-item label="列名">
                            <el-input v-model="headerName" class="m-2" size="small"/>
                        </el-form-item>
		            </el-col>
		            <el-col :span="12">
		                <el-form-item label="是否合并">
		                    <el-select v-model="needMerge" class="m-2" placeholder="Select" size="small" clearable>
		                        <el-option v-for="item in yesOrNoOptions" :key="item.value" :label="item.label" :value="item.value" />
		                    </el-select>
		                </el-form-item>
                    </el-col>
		        </el-row>
		    </el-col>  
		    <el-col :span="2"></el-col>                 
		</el-row>
        <el-row :gutter="4">
		    <el-col :span="2"></el-col>
		    <el-col :span="20">
		        <el-row :gutter="4">
                    <el-col :span="12">
		                <el-form-item label="是否累加">
		                    <el-select v-model="isSum" class="m-2" placeholder="Select" size="small" clearable>
		                        <el-option v-for="item in yesOrNoOptions" :key="item.value" :label="item.label" :value="item.value" />
		                    </el-select>
		                </el-form-item>
                    </el-col>
                    <el-col :span="12">
		                <el-form-item label="累加维度列">
                            <el-input v-model="sumByColumnIndex" class="m-2" size="small"/>
                        </el-form-item>
		            </el-col>
		        </el-row>
		    </el-col>  
		    <el-col :span="2"></el-col>                 
		</el-row>
        <el-row :gutter="4">
		    <el-col :span="2"></el-col>
		    <el-col :span="20">
		        <el-row :gutter="4">
                    <el-col :span="12">
		                <el-form-item label="累加列">
                            <el-input v-model="sumColumnIndex" class="m-2" size="small"/>
                        </el-form-item>
		            </el-col>
		            <el-col :span="12">
		                
                    </el-col>
		        </el-row>
		    </el-col>  
		    <el-col :span="2"><el-link  type="primary" @click="addAttributeColumnMapping()">添加</el-link></el-col>                 
		</el-row>


		<el-row :gutter="4" v-if="modifyFileObjectMapping.attributeColumnMappings">
		    <el-col :span="1"/>
		    <el-col :span="23">
		    <el-scrollbar height="200px">
		        <el-table :data="modifyFileObjectMapping.attributeColumnMappings" stripe style="width: 100%">
		            <el-table-column  label="操作" width="60">
		                <template #default="scope">
		                    <el-link  type="primary" @click="deleteAttributeColumnMapping(scope.$index)">删除</el-link>
		                </template>
		            </el-table-column>
		            <el-table-column prop="attributeCode" label="属性编码" width="150" />
		            <el-table-column prop="columnIndex" label="列序号" width="80" />
		            <el-table-column prop="headerName" label="列名" width="150" />
                    <el-table-column prop="needMerge" label="是否合并" width="100" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span v-if="scope.row.needMerge == 'Y'">是</span>
                                <span v-if="scope.row.needMerge == 'N'">否</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="isSum" label="是否累加" width="100" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span v-if="scope.row.isSum == 'Y'">是</span>
                                <span v-if="scope.row.isSum == 'N'">否</span>
                            </div>
                        </template>
                    </el-table-column>
		            <el-table-column prop="sumByColumnIndex" label="累加维度列" width="120" />
                    <el-table-column prop="sumColumnIndex" label="累加列" width="80"/>
		        </el-table>
		    </el-scrollbar>
		    </el-col>
		</el-row>
    </el-form>

    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideFileObjectMappingModifyPage()">取消</el-button>
            <el-button type="primary" @click="saveFileObjectMapping()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'


export default {
    props: ['modifyFileObjectMapping','showFileObjectMappingModify'],
    
    emits: ['updateFileObjectMappingToList', 'hideFileObjectMappingModifyPage'],
    
    setup (props, {attrs, emit, slots}) {
        const updateFileObjectMappingToList = (modifyFileObjectMapping)=> {
            emit('updateFileObjectMappingToList', modifyFileObjectMapping);
        };
        
        const hideFileObjectMappingModifyPage = () => {
            emit('hideFileObjectMappingModifyPage');	
        };
        
        return {
        	updateFileObjectMappingToList,
        	hideFileObjectMappingModifyPage
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showFileObjectMappingModify;
            },
            set(value) {
                this.hideFileObjectMappingModifyPage();
            }
        }
    },
    components: {
       
    },
    data() {            
    	const attributeId = ref(null);
        const columnIndex = ref(null);
        const headerName = ref(null);
        const needMerge = ref(null);
        const isSum = ref(null);
        const sumByColumnIndex = ref(null);
        const sumColumnIndex = ref(null);
        
        const objectTypeOptions = ref(null);

        const mappingDirectOptions = ref(null);

        const fileTypeOptions = ref(null);

        const yesOrNoOptions = ref(null);

        const statusOptions = ref(null);

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "objectType": [
                { required: true, message: '对象类型不能为空！', trigger: 'blur' }
            ],
            "objectId": [
                { required: true, message: '对象标识不能为空！', trigger: 'blur' }
            ],
            "mappingDirect": [
                { required: true, message: '映射方向不能为空！', trigger: 'blur' }
            ],
            "fileType": [
                { required: true, message: '文件类型不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
        	attributeId,
            columnIndex,
            headerName,
            needMerge,
            isSum,
            sumByColumnIndex,
            sumColumnIndex,
            objectTypeOptions,
            mappingDirectOptions,
            fileTypeOptions,
            yesOrNoOptions,
            statusOptions,
            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_TYPE_HAS_ATTRIBUTE','PUB_YES_OR_NO','FILE_OBJECT_MAPPING_DIRECTION','FILE_OBJECT_MAPPING_FILE_TYPE','PUB_STATUS']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectTypeOptions = data.data['OBJECT_TYPE_HAS_ATTRIBUTE'];
                this.yesOrNoOptions = data.data['PUB_YES_OR_NO'];
                this.mappingDirectOptions = data.data['FILE_OBJECT_MAPPING_DIRECTION'];
                this.fileTypeOptions = data.data['FILE_OBJECT_MAPPING_FILE_TYPE'];
                this.statusOptions = data.data['PUB_STATUS'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	var url;
    	if(this.modifyFileObjectMapping.objectType == "D") {
    		url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/byId/" + this.modifyFileObjectMapping.objectId ;
    	}else {
    		url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/byId/" + this.modifyFileObjectMapping.objectId ;
    	}
    	
        axiosClient.post(url).then((response) => {
            var data = response.data;
            if(data != null && data.status == "200" && data.data != null) {
        	    this.modifyFileObjectMapping.object = data.data;           
            }     
        });
    },
    methods: {
    	saveFileObjectMapping() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/fileObjectMapping/saveWithAttributeColumnMapping";
            this.$refs.modifyFileObjectMappingForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.modifyFileObjectMapping).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`修改文件对象映射成功`);
                            data.data.objectCode = this.modifyFileObjectMapping.object.code;
                            this.updateFileObjectMappingToList(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        addAttributeColumnMapping() {
        	if(this.attributeId == null || this.attributeId == "" || this.columnIndex == null || this.columnIndex == "") {
        		ElMessage.error(`请先选择对象属性和列序号！`);
        		return;
        	}
        	var newAttributeColumnMapping = {
        			id: "",
        			attributeId: this.attributeId,
        			columnIndex: this.columnIndex,
        			headerName: this.headerName,
                    needMerge: this.needMerge,
                    isSum: this.isSum,
                    sumByColumnIndex: this.sumByColumnIndex,
                    sumColumnIndex: this.sumColumnIndex,
        			tenantId : this.modifyFileObjectMapping.tenantId,
                    systemId : this.modifyFileObjectMapping.systemId,
                    createTime : "",
                    updateTime : ""
        	}
        	this.modifyFileObjectMapping.object.attributes.forEach((attr)=>{
        		if(attr.id == newAttributeColumnMapping.attributeId) {
        			newAttributeColumnMapping.attributeCode = attr.code;
        			return;
        		}
        	});
        	
        	if(this.modifyFileObjectMapping.attributeColumnMappings == null) {
        		this.modifyFileObjectMapping.attributeColumnMappings = [];
        	}
        	this.modifyFileObjectMapping.attributeColumnMappings.push(newAttributeColumnMapping);
        },
        deleteAttributeColumnMapping(row) {
        	this.modifyFileObjectMapping.attributeColumnMappings.splice(row,1);
        }
    }
}
</script>