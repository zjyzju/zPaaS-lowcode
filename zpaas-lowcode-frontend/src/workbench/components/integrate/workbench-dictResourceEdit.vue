<!-- 编辑字典资源 -->
<template>
<el-form  v-if="dictResource != null && dictResource.id != null" :model="dictResource" label-width="130px" :rules="validateRules" ref="dictResourceForm">
    <el-row :gutter="4">
       <el-col :span="24">
           <el-breadcrumb separator=">">
               <el-breadcrumb-item><span class="title">字典资源</span></el-breadcrumb-item>
               <el-breadcrumb-item>字典资源</el-breadcrumb-item>
               <el-breadcrumb-item ><span class="title1">{{dictResource.name}}</span></el-breadcrumb-item>
            </el-breadcrumb>
       </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="标识" required prop="id">
                <el-input v-model="dictResource.id" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="名称" required prop="name">
                <el-input v-model="dictResource.name"  size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="类型" required prop="type">
                <el-select v-model="dictResource.type" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in resourceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="归属数据库" required prop="dbSchemaId">
                <el-select v-model="dictResource.dbSchemaId" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in dbSchemas" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="6">
            <el-form-item label="字典表" required prop="dictTable">
                <el-input v-model="dictResource.dictTable" :readonly="true"  size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="2">
            <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="selectDictTable(true)" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearDictTable(true)" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
        <el-col :span="6">
            <el-form-item label="字典从表" required prop="subDictTable" v-if="dictResource.type == 'M'">
                <el-input v-model="dictResource.subDictTable" :readonly="true"  size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="2">
            <el-form-item label="" label-width="10px" v-if="dictResource.type == 'M'">
                <el-link  type="primary" @click="selectDictTable(false)" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearDictTable(false)" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
    </el-row>

    <el-row :gutter="4">
        <el-col :span="24">&nbsp;</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 12px">
            <div class="toolbar1">
                <span class="title">字典配置</span>&nbsp;&nbsp;&nbsp;&nbsp;
            </div>
            </el-divider>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">&nbsp;</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="字典主键列" required prop="dictIdCol">
                <el-select v-model="dictResource.dictIdCol" class="m-2" clearable placeholder="Select" size="small">
                    <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="字典编码列" required prop="dictCodeCol">
                <el-select v-model="dictResource.dictCodeCol" class="m-2" clearable placeholder="Select" size="small">
                    <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="字典名称列" required prop="dictNameCol">
                <el-select v-model="dictResource.dictNameCol" class="m-2" clearable placeholder="Select" size="small">
                    <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="字典状态列">
                <el-select v-model="dictResource.dictStatusCol" class="m-2" clearable placeholder="Select" size="small">
                    <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="有效状态值">
                <el-input v-model="dictResource.effStatusValue"  size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            
        </el-col>
    </el-row>

    <el-row :gutter="4">
        <el-col :span="24">&nbsp;</el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 12px">
            <div class="toolbar1">
                <span class="title">字典值配置</span>&nbsp;&nbsp;&nbsp;&nbsp;
            </div>
            </el-divider>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">&nbsp;</el-col>
    </el-row>
    <el-row :gutter="4"  v-if="dictResource.type == 'M'">
        <el-col :span="8">
            <el-form-item label="从表字典编码列">
                <el-select v-model="dictResource.subTableDictCodeCol" class="m-2" clearable placeholder="Select" size="small">
                    <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="从表状态列">
                <el-select v-model="dictResource.subTableStatusCol" class="m-2" clearable placeholder="Select" size="small">
                    <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="从表有效状态值">
                <el-input v-model="dictResource.effSubTableStatusValue"  size="small"/>
            </el-form-item>
        </el-col>
    </el-row>  
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="字典值列" required prop="dictValueCol">
                <el-select v-model="dictResource.dictValueCol" class="m-2" clearable placeholder="Select" size="small">
                    <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" v-if="dictResource.type == 'S'"/>
                    <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" v-if="dictResource.type == 'M'"/>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="字典值名称列" required prop="dictValueLabelCol">
                <el-select v-model="dictResource.dictValueLabelCol" class="m-2" clearable placeholder="Select" size="small">
                    <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" v-if="dictResource.type == 'S'" />
                    <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" v-if="dictResource.type == 'M'"/>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="上级字典列">
                <el-select v-model="dictResource.parentDictCodeCol" class="m-2" clearable placeholder="Select" size="small">
                    <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" v-if="dictResource.type == 'S'" />
                    <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" v-if="dictResource.type == 'M'"/>
                </el-select>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="上级字典值列">
                <el-select v-model="dictResource.parentDictValueCol" class="m-2" clearable placeholder="Select" size="small">
                    <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" v-if="dictResource.type == 'S'" />
                    <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" v-if="dictResource.type == 'M'"/>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="字典值2列">
                <el-select v-model="dictResource.dictValue2Col" class="m-2" clearable placeholder="Select" size="small">
                    <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" v-if="dictResource.type == 'S'" />
                    <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" v-if="dictResource.type == 'M'"/>
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="字典值3列">
                <el-select v-model="dictResource.dictValue3Col" class="m-2" clearable placeholder="Select" size="small">
                    <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" v-if="dictResource.type == 'S'" />
                    <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" v-if="dictResource.type == 'M'"/>
                </el-select>
            </el-form-item>
        </el-col>
        
    </el-row>
    <el-row :gutter="4">
        <el-col :span="20" class="toolbar-right">
            <el-button type="primary" size="small" @click="saveDictResource()">保存</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="deleteDictResource()">删除</el-button>
        </el-col>
        <el-col :span="4" class="toolbar-right">&nbsp;</el-col>
    </el-row>
</el-form>

<!-- 选择数据库表信息 -->
<mdaTableSelect v-if="showMdaTableSelect"  @mdaTableSelected="mdaTableSelected" @hideMdaTableSelectPage="hideMdaTableSelectPage" :showMdaTableSelect="showMdaTableSelect"  :mdaTablesForSelect="mdaTablesForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'

import mdaTableSelect from '../assist/workbench-mdaTableSelect.vue'


export default {
    props: [],
    
    emits: ['updateToList', 'deleteFromList'],
    
    setup (props, {attrs, emit, slots}) {
        const updateDictResourceToList = (dictResource)=> {
            emit('updateToList', 'dictResource', dictResource);
        };
        
        const deleteDictResourceFromList = (dictResource)=> {
            emit('deleteFromList', 'dictResource', dictResource);
        };
        
        return {
        	updateDictResourceToList,
        	deleteDictResourceFromList
        };
    },
    components: {
        mdaTableSelect
    },
    data() {            
        const resourceTypeOptions = ref(null);

        const dictResource = ref(null);
        const router = useRoute();

        const showMdaTableSelect = ref(false);
        const mdaTablesForSelect = ref(null);
        const columns = ref([]);
        const subColumns = ref([]);

        const selectMainFlag = ref(true);

        const dbSchemas = ref(null);

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "type": [
                { required: true, message: '类型不能为空！', trigger: 'blur' }
            ],
            "dbSchemaId": [
                { required: true, message: '归属数据库不能为空！', trigger: 'blur' }
            ],
            "dictTable": [
                { required: true, message: '字典表不能为空！', trigger: 'blur' }
            ],
            "dictIdCol": [
                { required: true, message: '字典主键列不能为空！', trigger: 'blur' }
            ],
            "dictCodeCol": [
                { required: true, message: '字典编码列不能为空！', trigger: 'blur' }
            ],
            "dictNameCol": [
                { required: true, message: '字典名称列不能为空！', trigger: 'blur' }
            ],
            "dictValueCol": [
                { required: true, message: '字典值列不能为空！', trigger: 'blur' }
            ],
            "dictValueLabelCol": [
                { required: true, message: '字典值名称列不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
            dictResource,
            router,

        	resourceTypeOptions,
            validateRules,
            showMdaTableSelect,
            mdaTablesForSelect,
            selectMainFlag,
            columns,
            subColumns,
            dbSchemas
        }
    },
    watch: {        
        'dictResource': function(newObj, oldObj){
            if(oldObj == null || newObj == null || newObj.id != oldObj.id) {
                return;
            }
            if(newObj.dbSchemaId != oldObj.dbSchemaId || newObj.type != oldObj.type) {
                this.dictResource.dictTable = "";
                this.dictResource.subDictTable = "";
                this.columns = [];
                this.subColumns = [];

                this.dictResource.dictCodeCol = "";
                this.dictResource.dictNameCol = "";
                this.dictResource.dictStatusCol = "";
                this.dictResource.effStatusValue = "";

                this.dictResource.subTableDictCodeCol = "";
                this.dictResource.subTableStatusCol = "";
                this.dictResource.dictValueCol = "";
                this.dictResource.dictValueLabelCol = "";
                this.dictResource.parentDictCodeCol = "";
                this.dictResource.parentDictValueCol = "";
                this.dictResource.dictValue2Col = "";
                this.dictResource.dictValue3Col = "";
            }
        },
        'router.query': function(val, old){
            if(val != null && val.objectId != null && (old == null || val.objectId != old.objectId)) {
                setTimeout(()=>{
                    this.loadDictResource();
                },100);
            }else if(val == null || val.objectId == null) {
                this.dictResource = null;
            }
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['DICT_RESOURCE_TABLE_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.resourceTypeOptions = data.data['DICT_RESOURCE_TABLE_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        this.loadDictResource();
    },
    methods: {
        loadDictResource() {
            var objectId = this.router.query.objectId;
            if(objectId == null) {
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/dictResource/byId/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dictResource = data.data;
                    if(this.dictResource.dictTable != null && this.dictResource.dictTable.trim().length > 0) {
                        this.queryColumnList(this.dictResource.dictTable, true);
                    }
                    if(this.dictResource.subDictTable != null && this.dictResource.subDictTable.trim().length > 0) {
                        this.queryColumnList(this.dictResource.subDictTable, false);
                    }
                    var url = "/lcdp-proxy/lowcode/platform/be/api/dbSchema/list/" + this.dictResource.systemId;
                    axiosClient.get(url).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.dbSchemas = data.data;
                        }
                    });
                }
            });
        },
        queryColumnList(tableName, flag) {
            var url = "/lcdp-proxy/lowcode/platform/be/api/mda/queryColumnList/" + this.dictResource.systemId + "/" + this.dictResource.dbSchemaId + "/" + tableName;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    for(var i in data.data) {
                        var attribute = {
                        	id : data.data[i].columnName,
                            name : data.data[i].columnName
                        }
                        if(flag) {
                            this.columns.push(attribute);
                        }else {
                            this.subColumns.push(attribute);
                        }
                    }
                }
            });
        },
    	saveDictResource() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/dictResource/save";
            this.$refs.dictResourceForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.dictResource).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                            this.updateDictResourceToList(this.dictResource);
                            ElMessage(`更新字典资源信息成功，共更新(`+data.data+`)条属性`);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        deleteDictResource() {
            ElMessageBox.confirm(
                '该字典资源(' + this.dictResource.name + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/be/api/dictResource/delete/" + this.dictResource.id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         this.deleteDictResourceFromList(this.dictResource);
                         
                         ElMessage(`删除字典资源(`+ this.dictResource.name +`)成功。`);
                     }
                 }).catch(()=>{
                     ElMessage.error(`删除字典资源(`+ this.dictResource.name +`)失败！`);
                 });
             }).catch(()=>{});
        },
        selectDictTable(flag) {
            if(this.dictResource.dbSchemaId == null || this.dictResource.dbSchemaId.trim().length == 0) {
                ElMessage(`请先选择归属数据库！`);
                return;
            }
            this.selectMainFlag = flag;
        	var url = "/lcdp-proxy/lowcode/platform/be/api/mda/queryTableList/" + this.dictResource.systemId + "/" + this.dictResource.dbSchemaId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.mdaTablesForSelect = data.data;
                    this.showMdaTableSelect = true;
                }
            });
        },
        clearDictTable(flag) {
            if(flag) {
                this.dictResource.dictTable = "";
                this.dictResource.subDictTable = "";
                this.columns = [];

                this.dictResource.dictCodeCol = "";
                this.dictResource.dictNameCol = "";
                this.dictResource.dictStatusCol = "";
                this.dictResource.effStatusValue = "";

                this.dictResource.subTableDictCodeCol = "";
                this.dictResource.subTableStatusCol = "";
                this.dictResource.dictValueCol = "";
                this.dictResource.dictValueLabelCol = "";
                this.dictResource.parentDictCodeCol = "";
                this.dictResource.parentDictValueCol = "";
                this.dictResource.dictValue2Col = "";
                this.dictResource.dictValue3Col = "";
            }else {
                this.dictResource.subDictTable = "";
                this.subColumns = [];
                
                this.dictResource.subTableDictCodeCol = "";
                this.dictResource.subTableStatusCol = "";
                this.dictResource.dictValueCol = "";
                this.dictResource.dictValueLabelCol = "";
                this.dictResource.parentDictCodeCol = "";
                this.dictResource.parentDictValueCol = "";
                this.dictResource.dictValue2Col = "";
                this.dictResource.dictValue3Col = "";
            }
            
        },
        mdaTableSelected(obj) {
            if(this.selectMainFlag) {
                this.dictResource.dictTable = obj.tableName;
                this.columns = [];
            }else {
                this.dictResource.subDictTable = obj.tableName;
                this.subColumns = [];
            }
        	
            
            var url = "/lcdp-proxy/lowcode/platform/be/api/mda/queryColumnList/" + this.dictResource.systemId + "/" + this.dictResource.dbSchemaId + "/" + obj.tableName;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    for(var i in data.data) {
                        var attribute = {
                        	id : data.data[i].columnName,
                            name : data.data[i].columnName
                        }
                        if(this.selectMainFlag) {
                            this.columns.push(attribute);
                        }else {
                            this.subColumns.push(attribute);
                        }
                    }
                }
            });
            
            this.mdaTablesForSelect = null;
            this.showMdaTableSelect = false;
        },
        hideMdaTableSelectPage(){
             this.mdaTablesForSelect = null;
             this.showMdaTableSelect = false;
        }
    }
}
</script>

<style scoped>
.layout-container-main .toolbar-right {
  display: inline-flex;
  align-items: center;
  justify-content: right; 
  height: 100%;
  right: 20px;
}
.toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
  height: 100%;
  right: 20px;
}
</style>