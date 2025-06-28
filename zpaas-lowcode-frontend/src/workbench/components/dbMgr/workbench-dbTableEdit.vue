<!-- 编辑数据库表 -->
<template>
<el-form  v-if="dbTable != null && dbTable.id != null" :model="dbTable" label-width="120px" :rules="validateRules" ref="dbTableForm">   
    <el-row :gutter="4">
       <el-col :span="24">
           <el-breadcrumb separator=">">
               <el-breadcrumb-item><span class="title">数据库对象</span></el-breadcrumb-item>
               <el-breadcrumb-item>数据库表</el-breadcrumb-item>
               <el-breadcrumb-item ><span class="title1">{{dbTable.name}}</span></el-breadcrumb-item>
            </el-breadcrumb>
       </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="标识" required prop="id">
                <el-input v-model="dbTable.id" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="名称" required prop="name">
                <el-input v-model="dbTable.name" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="描述" required prop="description">
                <el-input v-model="dbTable.description" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="归属数据库" required prop="schemaId">
                <el-input type="hidden" v-model="dbTable.schemaId" size="small"/>
                <el-input v-model="dbTable.dbSchema.description" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="创建时间">
                <el-input type="hidden" v-model="dbTable.status" size="small"/>
                <el-input v-model="dbTable.createTime" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            &nbsp;
        </el-col>
    </el-row> 
    <el-row :gutter="4">
        <el-col :span="20" class="toolbar-right">
            <el-button type="primary" size="small" @click="loadObjectRelationMappingsbyTableId(dbTable.id)">查看ORM信息</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="saveDbTable()">保存</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="deleteDbTable()">删除</el-button>
        </el-col>
        <el-col :span="4" class="toolbar-right">&nbsp;</el-col>
    </el-row>
    
    <!-- 数据库表字段信息 -->
    <br/>
    <el-row :gutter="4" v-if="dbTable != null && dbTable.id != null">
        <el-col :span="1"></el-col>
        <el-col :span="23">
            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 6px">
            <div class="toolbar1">
                <span class="title">库表字段</span>&nbsp;&nbsp;&nbsp;&nbsp;
                <el-link @click="showDbColumnCreatePage()"  type="primary" >新建</el-link>
            </div>
            </el-divider>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="dbTable != null && dbTable.id != null">
        <el-col :span="1"/>
        <el-col :span="23">
            <el-table :data="dbTable.dbColumns" stripe style="width: 100%">
                <el-table-column  label="操作" width="110">
                    <template #default="scope">
                        <el-link @click="showDbColumnEditPage(scope.row.id)"  type="primary">编辑</el-link>&nbsp;
                        <el-link  type="primary"  @click="deleteDbColumn(scope.row.id, scope.row.name)">删除</el-link>
                    </template>
                </el-table-column>
                    <el-table-column prop="name" label="名称" width="180" />
                    <el-table-column prop="description" label="描述" width="180" />
                    <el-table-column prop="type" label="字段类型" width="120" />
                    <el-table-column prop="length" label="字段长度" width="100" />
                    <el-table-column prop="isPrimaryKey" label="是否主键" width="80" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.isPrimaryKey == 'Y'">是</span>
                              <span v-if="scope.row.isPrimaryKey == 'N'">否</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="isNull" label="是否可空" width="80" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.isNull == 'Y'">是</span>
                              <span v-if="scope.row.isNull == 'N'">否</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="keyGenerateMethod" label="主键生成方法" width="120"/>
                    <el-table-column prop="displayOrder" label="显示顺序"/>
            </el-table>
        </el-col>
    </el-row>
</el-form>

<!-- 新建数据库表字段 -->
<createDbColumn v-if="showDbColumnCreate==true && newDbColumn != null" @addNewDbColumnToList="addNewDbColumnToList" @hideDbColumnCreatePage="hideDbColumnCreatePage"  :showDbColumnCreate="showDbColumnCreate" :newDbColumn="newDbColumn"/>

<!-- 修改数据库表字段 -->
<editDbColumn v-if="showDbColumnEdit==true && dbColumn != null" @updateDbColumnToList="updateDbColumnToList" @hideDbColumnEditPage="hideDbColumnEditPage"  :showDbColumnEdit="showDbColumnEdit" :dbColumn="dbColumn"/>

<!-- 编辑对象关系映射信息 -->
<editObjectRelationMapping v-if="showObjectRelationMappingEdit==true && objectRelationMappings != null"  @hideEditObjectRelationMappingPage="hideEditObjectRelationMappingPage"  :showObjectRelationMappingEdit="showObjectRelationMappingEdit"  :objectRelationMappings="objectRelationMappings" :orType="orType" :orId="orId" :tanentId="dbTable.tenantId" :systemId="dbTable.systemId"/>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'

import createDbColumn from './workbench-dbColumnCreate.vue'
import editDbColumn from './workbench-dbColumnEdit.vue'
import editObjectRelationMapping from '../domainModel/workbench-objectRelationMappingEdit.vue'

export default {
    props: [],
    
    emits: ['updateToList', 'deleteFromList'],
    
    setup(props, {attrs, emit, slots}) {
        const updateDbTableToList = (dbTable) => {
            emit('updateToList', 'dbTable', dbTable);
        };
        
        const deleteDbTableFromList = (dbTable) => {
        	emit('deleteFromList', 'dbTable', dbTable);
        };
        
        return {
        	updateDbTableToList,
        	deleteDbTableFromList
        }
    },
    
    components: {
    	createDbColumn,
        editDbColumn,
        editObjectRelationMapping
    },
    
    data() {
        const dbTable = ref(null);
        const router = useRoute();

    	const showDbColumnCreate = ref(false);
        const newDbColumn = ref({});
        
        const showDbColumnEdit = ref(false);
        const dbColumn = ref({});
        
        const showObjectRelationMappingEdit = ref(false);
        const objectRelationMappings = ref([]);

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "description": [
                { required: true, message: '描述不能为空！', trigger: 'blur' }
            ],
            "schemaId": [
                { required: true, message: '归属数据库不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
            dbTable,
            router,
        	showDbColumnCreate,
            newDbColumn,
            showDbColumnEdit,
            dbColumn,
            showObjectRelationMappingEdit,
            objectRelationMappings,
            validateRules
        }
    },
    watch: {        
        'router.query': function(val, old){
            if(val != null && val.objectId != null && (old == null || val.objectId != old.objectId)) {
                setTimeout(()=>{
                    this.loadDbTable();
                },100);
            }else if(val == null || val.objectId == null) {
                this.dbTable = null;
            }
        }
    },
    mounted() {
        this.loadDbTable();
    },
    methods: {
        loadDbTable() {
            var objectId = this.router.query.objectId;
            if(objectId == null) {
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/dbTable/byId/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dbTable = data.data;
                }
            });
        },
    	loadObjectRelationMappingsbyTableId(tableId) {
            var url = "/lcdp-proxy/lowcode/platform/be/api/objectRelationMapping/listByTableId/" + tableId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.objectRelationMappings = data.data;
                    this.orType = "R";
                    this.orId = tableId;
                    this.showObjectRelationMappingEdit = true;
                }
            });
        },
        hideEditObjectRelationMappingPage() {
            this.showObjectRelationMappingEdit = false;
        },
        saveDbTable() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/dbTable/save";
            this.$refs.dbTableForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url, this.dbTable).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                            this.updateDbTableToList(this.dbTable)
                            ElMessage(`更新数据库表对象成功，共更新(`+data.data+`)条属性`);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        deleteDbTable() {
            ElMessageBox.confirm(
                '该数据库表(' + this.dbTable.description + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/be/api/dbTable/delete/" + this.dbTable.id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         ElMessage(`删除数据库表(`+ this.dbTable.description +`)成功。`);
                         this.deleteDbTableFromList(this.dbTable)
                     }
                 }).catch(()=>{
                     ElMessage.error(`删除数据库表(`+ this.dbTable.description +`)失败！`);
                 });
             }).catch(()=>{});
        },
        showDbColumnCreatePage() {
            this.newDbColumn = {
                id:"",
                name:"",
                description:"",
                type:"",
                length:"",
                isPrimaryKey:"",
                isNull:"",
                keyGenerateMethod:"",
                displayOrder:"",
                tableId: this.dbTable.id,
                createTime : "",
                updateTime : "",
                systemId: this.dbTable.systemId,
                tenantId: this.dbTable.tenantId
            };
            this.showDbColumnCreate = true;
        },
        hideDbColumnCreatePage() {
            this.showDbColumnCreate = false;
        },
        addNewDbColumnToList(data) {
            if(this.dbTable.dbColumns == null) {
            	this.dbTable.dbColumns = [];
            }
            this.dbTable.dbColumns.push(data);
            this.dbTable.dbColumns.sort((a,b)=> {
                if(a.displayOrder > b.displayOrder) {
                    return 1;
                }else if(a.displayOrder == b.displayOrder) {
                    return 0;
                }else {
                    return -1;
                }
            });
         
            this.showDbColumnCreate = false;
        },
        showDbColumnEditPage(columnId) {
            var url = "/lcdp-proxy/lowcode/platform/be/api/dbColumn/byId/" + columnId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dbColumn = data.data;
                    this.showDbColumnEdit = true;
                }
            });
        },
        hideDbColumnEditPage() {
            this.showDbColumnEdit = false;
        },
        updateDbColumnToList(data) {
            if(this.dbTable.dbColumns == null) {
            	this.dbTable.dbColumns = [];
            }
            for(var i in this.dbTable.dbColumns) {
                if(this.dbTable.dbColumns[i].id == data.id) {
                	this.dbTable.dbColumns.splice(i,1)
                    break;
                }
            }
            this.dbTable.dbColumns.push(data);
            this.dbTable.dbColumns.sort((a,b)=> {
                if(a.displayOrder > b.displayOrder) {
                    return 1;
                }else if(a.displayOrder == b.displayOrder) {
                    return 0;
                }else {
                    return -1;
                }
            });
         
            this.showDbColumnEdit = false;
        },
        deleteDbColumn(dbColumnId, dbColumnCode) {
            ElMessageBox.confirm(
                    '该字段(' + dbColumnCode + ')将被删除. 是否继续?',
                    '警告',
                    {
                      confirmButtonText: '确定',
                      cancelButtonText: '取消',
                      type: 'warning',
                    }
                  ).then(() => {
                      var url = "/lcdp-proxy/lowcode/platform/be/api/dbColumn/delete/" + dbColumnId;
                      axiosClient.post(url).then((response) => {
                          var data = response.data; 
                          if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                              if(this.dbTable.dbColumns != null && this.dbTable.dbColumns.length > 0) {
                                  for(var i in  this.dbTable.dbColumns) {
                                      if( this.dbTable.dbColumns[i].id == dbColumnId) {
                                    	  this.dbTable.dbColumns.splice(i,1);
                                          break;
                                      }
                                  }
                                  
                              }
                              ElMessage(`删除字段(`+dbColumnCode+`)成功。`);
                          }
                      }).catch(()=>{
                          ElMessage.error(`删除字段(`+dbColumnCode+`)失败！`);
                      });
                  }).catch(()=>{
                  });
            
        },
        
    }
}

</script>

<style scoped>
.layout-container-main .toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
  height: 100%;
  right: 20px;
}

.layout-container-main .toolbar-right {
  display: inline-flex;
  align-items: center;
  justify-content: right; 
  height: 100%;
  right: 20px;
}
</style>
