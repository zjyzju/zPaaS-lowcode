<!-- 新建BI模型对象 -->
<template>
<el-dialog v-model="showFlag" title="新建BI模型对象" width="60vw">
    <template #header>
        <span class="title">新建BI模型对象</span>
    </template>
    <el-form  :model="newDataModel" label-width="130px" :rules="validateRules" ref="newDataModelForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="编码" required prop="code">
                    <el-input v-model="newDataModel.code"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="newDataModel.name"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="归属业务域" required prop="domainId">
                    <el-select v-model="newDataModel.domainId" v-if="businessDomains"  class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in businessDomains"
                                  :key="item.businessDomain.id"
                                  :label="item.businessDomain.name"
                                  :value="item.businessDomain.id"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="类型" required prop="type">
                    <el-select v-model="newDataModel.type" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in dataModelTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="数据源类型" required prop="dataSourceType">
                    <el-select v-model="newDataModel.dataSourceType" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in dataSourceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="归属数据源" required prop="dataSourceId">
                    <el-select v-model="newDataModel.dataSourceId" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in dataSources" :key="item.id" :label="item.name" :value="item.id" />
                    </el-select>
                </el-form-item>
            </el-col>
            
        </el-row>
        <el-row :gutter="4">
            <el-col :span="9" v-if="newDataModel.type != 'S'">
                <el-form-item label="物理来源对象" required prop="physicalSrcObject">
                    <el-input v-model="newDataModel.physicalSrcObject" :readonly="true"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="3" v-if="newDataModel.type != 'S'">
                <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectPhysicalObject()" ><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearPhysicalObject()" ><label>清空</label></el-link>                                                
                </el-form-item>
            </el-col>
            <el-col :span="12" v-if="newDataModel.type == 'S'">
                <el-form-item label="物理来源对象" required prop="physicalSrcObject">
                    <el-input type="textarea" :rowspan="6" v-model="newDataModel.physicalSrcObject"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="物理对象说明" prop="srcObjectDesc">
                    <el-input v-model="newDataModel.srcObjectDesc"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="描述" prop="description">
                    <el-input type="textarea" rowspan="6" v-model="newDataModel.description"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDataModelCreatePage()">取消</el-button>
            <el-button type="primary" @click="dataModelCreateBack()">上一步</el-button>
            <el-button type="primary" @click="createDataModel()">确定</el-button>
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
    props: ['newDataModel','showDataModelCreate', 'businessDomains'],
    
    emits: ['addNewDataModelToList', 'hideDataModelCreatePage', 'dataModelCreateBack'],
    
    setup (props, {attrs, emit, slots}) {
        const addNewDataModelToList = (newDataModel)=> {
            emit('addNewDataModelToList', newDataModel);
        };
        
        const hideDataModelCreatePage = () => {
            emit('hideDataModelCreatePage');	
        };
        
        const dataModelCreateBack = () => {
            emit('dataModelCreateBack'); 
        };
        
        return {
        	addNewDataModelToList,
        	hideDataModelCreatePage,
        	dataModelCreateBack
        };
    },
    components: {
        mdaTableSelect
    },
    computed: {
        showFlag: {
            get() {
                return this.showDataModelCreate;
            },
            set(value) {
                this.hideDataModelCreatePage();
            }
        }
    },
    data() {            
        const dataSourceTypeOptions = ref(null);

        const dataModelTypeOptions = ref(null);

        const showMdaTableSelect = ref(false);
        const mdaTablesForSelect = ref(null);
        
        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "code": [
                { required: true, message: '编码不能为空！', trigger: 'blur' }
            ],
            "type": [
                { required: true, message: '类型不能为空！', trigger: 'blur' }
            ],
            "dataSourceType": [
                { required: true, message: '数据源类型不能为空！', trigger: 'blur' }
            ],
            "dataSourceId": [
                { required: true, message: '归属数据源不能为空！', trigger: 'blur' }
            ],
            "physicalSrcObject": [
                { required: true, message: '物理来源对象不能为空！', trigger: 'blur' }
            ],
            "domainId": [
                { required: true, message: '归属业务域不能为空！', trigger: 'blur' }
            ]
        });
        const dataSources = ref(null);
        
        return {
        	dataSourceTypeOptions,
            dataModelTypeOptions,
            validateRules,
            showMdaTableSelect,
            mdaTablesForSelect,
            dataSources
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['BI_DATASOURCE_TYPE','BI_DATA_MODEL_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.dataSourceTypeOptions = data.data['BI_DATASOURCE_TYPE'];
                this.dataModelTypeOptions = data.data['BI_DATA_MODEL_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    watch: {        
        'newDataModel.dataSourceType': function(val, old){
        	this.newDataModel.dataSourceId = "";
            this.newDataModel.physicalSrcObject = "";
            if(val == "R") {//关系数据库
                var url = "/lcdp-proxy/lowcode/platform/be/api/dbSchema/list/" + this.newDataModel.systemId ;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.dataSources = data.data;
                    }
                });
            }else {
                this.dataSources = null;
            }
        },
        'newDataModel.dataSourceId': function(val, old){
        	this.newDataModel.physicalSrcObject = "";
        },
        'newDataModel.type': function(val, old){
        	this.newDataModel.physicalSrcObject = "";
        }
    },
    methods: {
    	createDataModel() {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataModel/add";
            this.$refs.newDataModelForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.newDataModel).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`新增数据模型对象成功`);
                            this.addNewDataModelToList(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        selectPhysicalObject() {
            if(this.newDataModel.dataSourceId == null || this.newDataModel.dataSourceId.trim().length == 0) {
                ElMessage(`请先选择归属数据源！`);
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/mda/queryTableList/" + this.newDataModel.systemId + "/" + this.newDataModel.dataSourceId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.mdaTablesForSelect = data.data;
                    this.showMdaTableSelect = true;
                }
            });
        },
        clearPhysicalObject() {
            this.newDataModel.physicalSrcObject = "";
        },
        mdaTableSelected(obj) {
            this.newDataModel.physicalSrcObject = obj.tableName;
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