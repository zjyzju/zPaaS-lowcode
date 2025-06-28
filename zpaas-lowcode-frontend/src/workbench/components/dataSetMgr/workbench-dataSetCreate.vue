<!-- 新建数据集对象 -->
<template>
<el-dialog v-model="showFlag" title="新建数据集对象" width="60vw">
    <template #header>
        <span class="title">新建数据集对象</span>
    </template>
    <el-form  :model="newDataSet" label-width="130px" :rules="validateRules" ref="newDataSetForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="编码" required prop="code">
                    <el-input v-model="newDataSet.code"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="newDataSet.name"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="归属业务域" required prop="domainId">
                    <el-select v-model="newDataSet.domainId" v-if="businessDomains"  class="m-2" placeholder="Select" size="small">
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
                <el-form-item label="描述" prop="description">
                    <el-input type="textarea" rowspan="6" v-model="newDataSet.description"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDataSetCreatePage()">取消</el-button>
            <el-button type="primary" @click="dataSetCreateBack()">上一步</el-button>
            <el-button type="primary" @click="createDataSet()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import mdaTableSelect from '../assist/workbench-mdaTableSelect.vue'


export default {
    props: ['newDataSet','showDataSetCreate', 'businessDomains'],
    
    emits: ['addNewDataSetToList', 'hideDataSetCreatePage', 'dataSetCreateBack'],
    
    setup (props, {attrs, emit, slots}) {
        const addNewDataSetToList = (newDataSet)=> {
            emit('addNewDataSetToList', newDataSet);
        };
        
        const hideDataSetCreatePage = () => {
            emit('hideDataSetCreatePage');	
        };
        
        const dataSetCreateBack = () => {
            emit('dataSetCreateBack'); 
        };
        
        return {
        	addNewDataSetToList,
        	hideDataSetCreatePage,
        	dataSetCreateBack
        };
    },
    components: {
        
    },
    computed: {
        showFlag: {
            get() {
                return this.showDataSetCreate;
            },
            set(value) {
                this.hideDataSetCreatePage();
            }
        }
    },
    data() {            
        
        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "code": [
                { required: true, message: '编码不能为空！', trigger: 'blur' }
            ],
            "domainId": [
                { required: true, message: '归属业务域不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
        	validateRules
        }
    },
    watch: {        
        
    },
    methods: {
    	createDataSet() {
            var url = "/lcdp-proxy/lowcode/platform/bi/api/dataSet/add";
            this.$refs.newDataSetForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.newDataSet).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`新增数据集对象成功`);
                            this.addNewDataSetToList(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
        }
    }
}
</script>