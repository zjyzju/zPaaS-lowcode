<!-- 新建组合功能资源 -->
<template>
<el-dialog v-model="showFlag" title="新建组合功能" width="800px">
    <template #header>
        <span class="title">新建组合功能</span>
    </template>
    <el-form  :model="newComboFuncDefine" label-width="120px" :rules="validateRules" ref="newComboFuncDefineForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="newComboFuncDefine.name"  size="small"/>
                </el-form-item>
            </el-col>
             <el-col :span="12">
                <el-form-item label="组合功能模板" required prop="comboTemplateId">
                    <el-select v-model="newComboFuncDefine.comboTemplateId" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in comboFuncTemplates"
                                  :key="item.id"
                                  :label="item.name"
                                  :value="item.id"
                            />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="归属业务域" required prop="domainId">
                    <el-select v-model="newComboFuncDefine.domainId" v-if="businessDomains"  class="m-2" placeholder="Select" size="small">
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
                <el-form-item label="描述">
                    <el-input type="textarea" :rows="3" v-model="newComboFuncDefine.description"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        
        
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideComboFuncDefineCreatePage()">取消</el-button>
            <el-button type="primary" @click="comboFuncDefineCreateBack">上一步</el-button>
            <el-button type="primary" @click="createComboFuncDefine()">确定</el-button>
        </span>
    </template>
</el-dialog>


</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'



export default {
    props: ['newComboFuncDefine','showComboFuncDefineCreate', 'businessDomains'],
    
    emits: ['addNewComboFuncDefineToList', 'hideComboFuncDefineCreatePage', 'comboFuncDefineCreateBack'],
    
    setup (props, {attrs, emit, slots}) {
        const addNewComboFuncDefineToList = (newComboFuncDefine)=> {
            emit('addNewComboFuncDefineToList', newComboFuncDefine);
        };
        
        const hideComboFuncDefineCreatePage = () => {
            emit('hideComboFuncDefineCreatePage');	
        };
        
        const comboFuncDefineCreateBack = () => {
            emit('comboFuncDefineCreateBack'); 
        };
        
        return {
        	addNewComboFuncDefineToList,
        	hideComboFuncDefineCreatePage,
        	comboFuncDefineCreateBack
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showComboFuncDefineCreate;
            },
            set(value) {
                this.hideComboFuncDefineCreatePage();
            }
        }
    },
    components: {
    	
    },
    data() {            
        const comboFuncTemplates = ref([]);

        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "comboTemplateId": [
                { required: true, message: '组合功能模板不能为空！', trigger: 'blur' }
            ],
            "domainId": [
                { required: true, message: '归属业务域不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
        	comboFuncTemplates,
        	validateRules
        }
    },
    watch: {
    	
    },
    mounted() {
    	this.loadComboFuncTemplates();
    },
    methods: {
    	createComboFuncDefine() {
    		var url = "/lcdp-proxy/lowcode/platform/fe/api/comboFuncDefine/add";
            this.$refs.newComboFuncDefineForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.newComboFuncDefine).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`新增组合功能成功！`);
                            this.addNewComboFuncDefineToList(data.data);
                        }
                    }).catch((error)=> {
                        ElMessage.error(`新增组合功能失败:` + error);
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        loadComboFuncTemplates() {
            var url = "/lcdp-proxy/lowcode/platform/fe/api/comboFuncTemplate/list" ;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.comboFuncTemplates = data.data;
                }else {
                    ElMessage.error(`加载组合功能模板列表信息失败`);
                }
            });
        }
    }
}
</script>