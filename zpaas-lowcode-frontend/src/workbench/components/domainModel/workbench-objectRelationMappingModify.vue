<!-- 修改ORM -->
<template>
<el-dialog v-model="showFlag" title="修改对象关系映射" width="800px">
    <template #header>
        <span class="title">修改对象关系映射</span>
    </template>
    <el-form  :model="modifyObjectRelationMapping" label-width="120px" :rules="validateRules" ref="modifyObjectRelationMappingForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="标识" required prop="id">
                    <el-input v-model="modifyObjectRelationMapping.id" readonly  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="modifyObjectRelationMapping.name"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="orType == 'O'">
            <el-col :span="12">
                <el-form-item label="领域对象" required prop="domainObjectId">
                    <el-input v-model="modifyObjectRelationMapping.domainObjectId" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="9">
                <el-form-item label="数据库表" required prop="tableId">
                    <el-input v-model="modifyObjectRelationMapping.tableId" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="3">
                <el-form-item label="" label-width="10px">
	                <el-link type="primary" @click="selectDbTable()" ><label>选择</label></el-link>&nbsp;
	                <el-link type="primary" @click="clearDbTable()" ><label>清空</label></el-link> 
                </el-form-item>
            </el-col>
        </el-row>
       
        <el-row :gutter="4"  v-if="orType == 'R'">
            <el-col :span="9">
                <el-form-item label="领域对象" required prop="domainObjectId">
                    <el-input v-model="modifyObjectRelationMapping.domainObjectId" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="3">
                <el-form-item label="" label-width="10px">
                    <el-link type="primary" @click="selectDomainObject()" ><label>选择</label></el-link>&nbsp;
                    <el-link type="primary" @click="clearDomainObject()" ><label>清空</label></el-link> 
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="数据库表" required prop="tableId">
                    <el-input v-model="modifyObjectRelationMapping.tableId" readonly size="small"/>
                </el-form-item>
            </el-col>
            
        </el-row>
        
         <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="领域对象编码">
                    <el-input v-model="modifyObjectRelationMapping.relDomainObject.code" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="数据库表名称">
                    <el-input v-model="modifyObjectRelationMapping.relDbTable.name" readonly size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        
        
        <el-row :gutter="4">
                    <el-col :span="24">
                        <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 12px">
                        <div class="toolbar1">
                            <span class="title">属性字段映射</span>&nbsp;&nbsp;&nbsp;&nbsp;
                        </div>
                        </el-divider>
                    </el-col>
                </el-row>
        <el-row :gutter="4">
		    <el-col :span="2"></el-col>
		    <el-col :span="20">
		        <el-row :gutter="4">
		            <el-col :span="12">
		                <el-form-item label="领域对象属性">
		                    <el-select v-model="domainObjectId1" class="m-2" placeholder="Select" size="small">
		                        <el-option v-for="item in modifyObjectRelationMapping.relDomainObject.attributes" :key="item.id" :label="item.code" :value="item.id" />
		                    </el-select>
		                </el-form-item>
		            </el-col>
		            <el-col :span="12">
		                <el-form-item label="数据库表字段">
		                    <el-select v-model="columnId1" class="m-2" placeholder="Select" size="small">
		                        <el-option v-for="item in modifyObjectRelationMapping.relDbTable.dbColumns" :key="item.id" :label="item.name" :value="item.id" />
		                    </el-select>
		                </el-form-item>
		            </el-col>
		        </el-row>
		    </el-col>  
		    <el-col :span="2"><el-link  type="primary" @click="addColumnMapping()">添加</el-link></el-col>                 
		</el-row>
		<el-row :gutter="4" v-if="modifyObjectRelationMapping.columnMappings">
		    <el-col :span="1"/>
		    <el-col :span="23">
		    <el-scrollbar height="200px">
		        <el-table :data="modifyObjectRelationMapping.columnMappings" stripe style="width: 100%">
		            <el-table-column  label="操作" width="60">
		                <template #default="scope">
		                    <el-link  type="primary" @click="deleteColumnMapping(scope.$index)">删除</el-link>
		                </template>
		            </el-table-column>
		            <el-table-column prop="relAttribute.code" label="关联属性编码" width="180" />
		            <el-table-column prop="relAttribute.name" label="关联属性名称" width="180" />
		            <el-table-column prop="relDbColumn.name" label="关联字段名称" width="180" />
		            <el-table-column prop="relDbColumn.description" label="关联字段描述"/>
		        </el-table>
		    </el-scrollbar>
		    </el-col>
		</el-row>
    </el-form>

    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideObjectRelationMappingModifyPage()">取消</el-button>
            <el-button type="primary" @click="saveObjectRelationMapping()">确定</el-button>
        </span>
    </template>
</el-dialog>

<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideDomainObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>

<!-- 选择数据库表信息 -->
<dbTableSelect v-if="showDbTableSelect"  @dbTableSelected="dbTableSelected" @hideDbTableSelectPage="hideDbTableSelectPage" :showDbTableSelect="showDbTableSelect"  :dbTablesForSelect="dbTablesForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

import domainObjectSelect from './workbench-domainObjectSelect.vue'
import dbTableSelect from '../dbMgr/workbench-dbTableSelect.vue'

export default {
    props: ['modifyObjectRelationMapping','showObjectRelationMappingModify','orType'],
    
    emits: ['updateObjectRelationMappingToList', 'hideObjectRelationMappingModifyPage'],
    
    setup (props, {attrs, emit, slots}) {
        const updateObjectRelationMappingToList = (objectRelationMapping)=> {
            emit('updateObjectRelationMappingToList', objectRelationMapping);
        };
        
        const hideObjectRelationMappingModifyPage = () => {
            emit('hideObjectRelationMappingModifyPage');	
        };
        
        return {
        	updateObjectRelationMappingToList,
        	hideObjectRelationMappingModifyPage
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showObjectRelationMappingModify;
            },
            set(value) {
                this.hideObjectRelationMappingModifyPage();
            }
        }
    },
    components: {
        domainObjectSelect,
        dbTableSelect
    },
    data() {            
    	const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showDbTableSelect = ref(false);
        const dbTablesForSelect = ref(null);
        const domainObjectId1 = ref(null);
        const columnId1 = ref(null);

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "domainObjectId": [
                { required: true, message: '领域对象不能为空！', trigger: 'blur' }
            ],
            "tableId": [
                { required: true, message: '数据库表不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
        	showDomainObjectSelect,
            managedObjectsForSelect,
            showDbTableSelect,
            dbTablesForSelect,
            domainObjectId1,
            columnId1,
            validateRules
        }
    },
    methods: {
    	saveObjectRelationMapping() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/objectRelationMapping/updateWithColumnMapping";
            this.$refs.modifyObjectRelationMappingForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.modifyObjectRelationMapping).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`修改对象关系映射成功`);
                            this.updateObjectRelationMappingToList(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        selectDomainObject() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.modifyObjectRelationMapping.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.managedObjectsForSelect = data.data;
                    this.showDomainObjectSelect = true;
                }
            });
        },
        clearDomainObject(){
            this.modifyObjectRelationMapping.domainObjectId = "";
            this.modifyObjectRelationMapping.columnMappings = [];
            this.modifyObjectRelationMapping.relDomainObject = {};
        },
        managedObjectSelected(obj) {
            this.modifyObjectRelationMapping.domainObjectId = obj.id;
            var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/byId/" + obj.id;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.modifyObjectRelationMapping.relDomainObject = data.data;
                    this.managedObjectsForSelect = null;
                    this.showDomainObjectSelect = false;
                }
            });
        },
        hideDomainObjectSelectPage(){
             this.managedObjectsForSelect = null;
             this.showDomainObjectSelect = false;
        },
        selectDbTable() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/dbTable/list/" + this.modifyObjectRelationMapping.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dbTablesForSelect = data.data;
                    this.showDbTableSelect = true;
                }
            });
        },
        clearDbTable(){
            this.modifyObjectRelationMapping.tableId = "";
            this.modifyObjectRelationMapping.columnMappings = [];
            this.modifyObjectRelationMapping.relDbTable = {};
        },
        dbTableSelected(obj) {
            this.modifyObjectRelationMapping.tableId = obj.id;
            var url = "/lcdp-proxy/lowcode/platform/be/api/dbTable/byId/" + obj.id;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.modifyObjectRelationMapping.relDbTable = data.data;
                    this.dbTablesForSelect = null;
                    this.showDbTableSelect = false;
                }
            });
            
        },
        hideDbTableSelectPage(){
             this.dbTablesForSelect = null;
             this.showDbTableSelect = false;
        },
        addColumnMapping() {
        	if(this.domainObjectId1 == null || this.domainObjectId1 == "" || this.columnId1 == null || this.columnId1 == "") {
        		ElMessage.error(`请先选择领域对象属性和数据库字段！`);
        		return;
        	}
        	var newColumnMapping = {
        			id: "",
        			attributeId: this.domainObjectId1,
        			columnId: this.columnId1,
        			objectRelationMappingId: this.modifyObjectRelationMapping.id,
        			tenantId : this.modifyObjectRelationMapping.tenantId,
                    systemId : this.modifyObjectRelationMapping.systemId,
                    createTime : "",
                    updateTime : ""
        	}
        	this.modifyObjectRelationMapping.relDomainObject.attributes.forEach((attr)=>{
        		if(attr.id == newColumnMapping.attributeId) {
        			newColumnMapping.relAttribute = attr;
        			return;
        		}
        	});
        	this.modifyObjectRelationMapping.relDbTable.dbColumns.forEach((col)=>{
                if(col.id == newColumnMapping.columnId) {
                    newColumnMapping.relDbColumn = col;
                    return;
                }
            });
        	if(this.modifyObjectRelationMapping.columnMappings == null) {
        		this.modifyObjectRelationMapping.columnMappings = [];
        	}
        	this.modifyObjectRelationMapping.columnMappings.push(newColumnMapping);
        },
        deleteColumnMapping(row) {
        	this.modifyObjectRelationMapping.columnMappings.splice(row,1);
        }
    }
}
</script>