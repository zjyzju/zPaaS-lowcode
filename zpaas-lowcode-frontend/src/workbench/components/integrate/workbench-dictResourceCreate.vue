<!-- 新建字典资源 -->
<template>
<el-dialog v-model="showFlag" title="新建字典资源" width="80vw">
    <template #header>
        <span class="title">新建字典资源</span>
    </template>
    <el-form  :model="newDictResource" label-width="130px" :rules="validateRules" ref="newDictResourceForm">
        <el-row :gutter="4">
            <el-col :span="8">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="newDictResource.name"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="类型" required prop="type">
                    <el-select v-model="newDictResource.type" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in resourceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="8">
                <el-form-item label="归属数据库" required prop="dbSchemaId">
                    <el-select v-model="newDictResource.dbSchemaId" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in dbSchemas" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="6">
                <el-form-item label="字典表" required prop="dictTable">
                    <el-input v-model="newDictResource.dictTable" :readonly="true"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="2">
                <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectDictTable(true)" ><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearDictTable(true)" ><label>清空</label></el-link>                                                
                </el-form-item>
            </el-col>
            <el-col :span="6">
                <el-form-item label="字典从表" required prop="subDictTable" v-if="newDictResource.type == 'M'">
                    <el-input v-model="newDictResource.subDictTable" :readonly="true"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="2">
                <el-form-item label="" label-width="10px" v-if="newDictResource.type == 'M'">
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
                    <el-icon><histogram /></el-icon><span>字典配置</span>&nbsp;&nbsp;&nbsp;&nbsp;
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
                    <el-select v-model="newDictResource.dictIdCol" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="字典编码列" required prop="dictCodeCol">
                    <el-select v-model="newDictResource.dictCodeCol" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="字典名称列" required prop="dictNameCol">
                    <el-select v-model="newDictResource.dictNameCol" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="8">
                <el-form-item label="字典状态列">
                    <el-select v-model="newDictResource.dictStatusCol" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="有效状态值">
                    <el-input v-model="newDictResource.effStatusValue"  size="small"/>
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
                    <el-icon><histogram /></el-icon><span>字典值配置</span>&nbsp;&nbsp;&nbsp;&nbsp;
                </div>
                </el-divider>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">&nbsp;</el-col>
        </el-row>
        <el-row :gutter="4"  v-if="newDictResource.type == 'M'">
            <el-col :span="8">
                <el-form-item label="关联字典标识列">
                    <el-select v-model="newDictResource.subTableDictCodeCol" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="从表状态列">
                    <el-select v-model="newDictResource.subTableStatusCol" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="从表有效状态值">
                    <el-input v-model="newDictResource.effSubTableStatusValue"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>  
        <el-row :gutter="4">
            <el-col :span="8">
                <el-form-item label="字典值列" required prop="dictValueCol">
                    <el-select v-model="newDictResource.dictValueCol" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" v-if="newDictResource.type == 'S'"/>
                        <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" v-if="newDictResource.type == 'M'"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="字典值名称列" required prop="dictValueLabelCol">
                    <el-select v-model="newDictResource.dictValueLabelCol" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" v-if="newDictResource.type == 'S'" />
                        <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" v-if="newDictResource.type == 'M'"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="上级字典列">
                    <el-select v-model="newDictResource.parentDictCodeCol" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" v-if="newDictResource.type == 'S'" />
                        <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" v-if="newDictResource.type == 'M'"/>
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="8">
                <el-form-item label="上级字典值列">
                    <el-select v-model="newDictResource.parentDictValueCol" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" v-if="newDictResource.type == 'S'" />
                        <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" v-if="newDictResource.type == 'M'"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="字典值2列">
                    <el-select v-model="newDictResource.dictValue2Col" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" v-if="newDictResource.type == 'S'" />
                        <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" v-if="newDictResource.type == 'M'"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="8">
                <el-form-item label="字典值3列">
                    <el-select v-model="newDictResource.dictValue3Col" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in columns" :key="item.id" :label="item.name" :value="item.id" v-if="newDictResource.type == 'S'" />
                        <el-option v-for="item in subColumns" :key="item.id" :label="item.name" :value="item.id" v-if="newDictResource.type == 'M'"/>
                    </el-select>
                </el-form-item>
            </el-col>
            
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDictResourceCreatePage()">取消</el-button>
            <el-button type="primary" @click="dictResourceCreateBack()">上一步</el-button>
            <el-button type="primary" @click="createDictResource()">确定</el-button>
        </span>
    </template>
</el-dialog>

<!-- 选择数据库表信息 -->
<mdaTableSelect v-if="showMdaTableSelect"  @mdaTableSelected="mdaTableSelected" @hideMdaTableSelectPage="hideMdaTableSelectPage" :showMdaTableSelect="showMdaTableSelect"  :mdaTablesForSelect="mdaTablesForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'
import mdaTableSelect from '../assist/workbench-mdaTableSelect.vue'


export default {
    props: ['newDictResource','showDictResourceCreate', 'dbSchemas'],
    
    emits: ['addNewDictResourceToList', 'hideDictResourceCreatePage', 'dictResourceCreateBack'],
    
    setup (props, {attrs, emit, slots}) {
        const addNewDictResourceToList = (newDictResource)=> {
            emit('addNewDictResourceToList', newDictResource);
        };
        
        const hideDictResourceCreatePage = () => {
            emit('hideDictResourceCreatePage');	
        };
        
        const dictResourceCreateBack = () => {
            emit('dictResourceCreateBack'); 
        };
        
        return {
        	addNewDictResourceToList,
        	hideDictResourceCreatePage,
        	dictResourceCreateBack
        };
    },
    components: {
        mdaTableSelect
    },
    computed: {
        showFlag: {
            get() {
                return this.showDictResourceCreate;
            },
            set(value) {
                this.hideDictResourceCreatePage();
            }
        }
    },
    data() {            
        const resourceTypeOptions = ref(null);

        const showMdaTableSelect = ref(false);
        const mdaTablesForSelect = ref(null);
        const columns = ref(null);
        const subColumns = ref(null);

        const selectMainFlag = ref(true);

        const validateRules = ref({
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
        	resourceTypeOptions,
            validateRules,
            showMdaTableSelect,
            mdaTablesForSelect,
            selectMainFlag,
            columns,
            subColumns
        }
    },
    watch: {        
        'newDictResource.dbSchemaId': function(val, old){
        	this.newDictResource.dictTable = "";
            this.newDictResource.subDictTable = "";
            this.columns = [];
            this.subColumns = [];

            this.newDictResource.dictCodeCol = "";
            this.newDictResource.dictNameCol = "";
            this.newDictResource.dictStatusCol = "";
            this.newDictResource.effStatusValue = "";

            this.newDictResource.subTableDictCodeCol = "";
            this.newDictResource.subTableStatusCol = "";
            this.newDictResource.dictValueCol = "";
            this.newDictResource.dictValueLabelCol = "";
            this.newDictResource.parentDictCodeCol = "";
            this.newDictResource.parentDictValueCol = "";
            this.newDictResource.dictValue2Col = "";
            this.newDictResource.dictValue3Col = "";
        },
        'newDictResource.type': function(val, old){
        	this.newDictResource.dictTable = "";
            this.newDictResource.subDictTable = "";
            this.columns = [];
            this.subColumns = [];

            this.newDictResource.dictCodeCol = "";
            this.newDictResource.dictNameCol = "";
            this.newDictResource.dictStatusCol = "";
            this.newDictResource.effStatusValue = "";

            this.newDictResource.subTableDictCodeCol = "";
            this.newDictResource.subTableStatusCol = "";
            this.newDictResource.dictValueCol = "";
            this.newDictResource.dictValueLabelCol = "";
            this.newDictResource.parentDictCodeCol = "";
            this.newDictResource.parentDictValueCol = "";
            this.newDictResource.dictValue2Col = "";
            this.newDictResource.dictValue3Col = "";
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
    },
    methods: {
    	createDictResource() {
            if(this.newDictResource.type == 'M' && (this.newDictResource.subTableDictCodeCol == null || this.newDictResource.subTableDictCodeCol.trim().length == 0)) {
                ElMessage(`主从表模式时，从表字典编码列不能为空！`);
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/dictResource/add";
            this.$refs.newDictResourceForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.newDictResource).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`新增字典资源成功`);
                            this.addNewDictResourceToList(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        selectDictTable(flag) {
            if(this.newDictResource.dbSchemaId == null || this.newDictResource.dbSchemaId.trim().length == 0) {
                ElMessage(`请先选择归属数据库！`);
                return;
            }
            this.selectMainFlag = flag;
        	var url = "/lcdp-proxy/lowcode/platform/be/api/mda/queryTableList/" + this.newDictResource.systemId + "/" + this.newDictResource.dbSchemaId;
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
                this.newDictResource.dictTable = "";
                this.newDictResource.subDictTable = "";
                this.columns = [];

                this.newDictResource.dictCodeCol = "";
                this.newDictResource.dictNameCol = "";
                this.newDictResource.dictStatusCol = "";
                this.newDictResource.effStatusValue = "";

                this.newDictResource.subTableDictCodeCol = "";
                this.newDictResource.subTableStatusCol = "";
                this.newDictResource.dictValueCol = "";
                this.newDictResource.dictValueLabelCol = "";
                this.newDictResource.parentDictCodeCol = "";
                this.newDictResource.parentDictValueCol = "";
                this.newDictResource.dictValue2Col = "";
                this.newDictResource.dictValue3Col = "";
            }else {
                this.newDictResource.subDictTable = "";
                this.subColumns = [];
                
                this.newDictResource.subTableDictCodeCol = "";
                this.newDictResource.subTableStatusCol = "";
                this.newDictResource.dictValueCol = "";
                this.newDictResource.dictValueLabelCol = "";
                this.newDictResource.parentDictCodeCol = "";
                this.newDictResource.parentDictValueCol = "";
                this.newDictResource.dictValue2Col = "";
                this.newDictResource.dictValue3Col = "";
            }
            
        },
        mdaTableSelected(obj) {
            if(this.selectMainFlag) {
                this.newDictResource.dictTable = obj.tableName;
                this.columns = [];
            }else {
                this.newDictResource.subDictTable = obj.tableName;
                this.subColumns = [];
            }
        	
            
            var url = "/lcdp-proxy/lowcode/platform/be/api/mda/queryColumnList/" + this.newDictResource.systemId + "/" + this.newDictResource.dbSchemaId + "/" + obj.tableName;
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