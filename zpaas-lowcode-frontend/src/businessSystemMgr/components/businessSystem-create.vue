<!-- 创建业务系统 -->
<template>
<el-dialog v-model="showFlag" title="新建业务系统" append-to-body>
    <template #header>
        <span class="title">新建业务系统</span>
    </template>
    <el-form  :model="businessSystemNew" label-width="120px" :rules="validateRules" ref="businessSystemNewForm">
        <el-row :gutter="4">
            <el-col :span="12">              
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="businessSystemNew.name"  size="small"/>
                </el-form-item>
            </el-col>  
            <el-col :span="12">
                <el-form-item label="描述">
                    <el-input v-model="businessSystemNew.description"  size="small"/>
                </el-form-item>
            </el-col>          
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">              
                <el-form-item label="请求上下文" required prop="servletContext">
                    <el-input v-model="businessSystemNew.servletContext"  size="small"/>
                </el-form-item>
            </el-col>  
            <el-col :span="12">
                <el-form-item label="系统图标">
                    <el-select v-model="businessSystemNew.systemIcon" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in systemIconOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>           
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="版本">
                    <el-input v-model="businessSystemNew.version"  size="small"/>
                </el-form-item>
            </el-col>  
            <el-col :span="12">
                
            </el-col> 
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideBusinessSystemCreatePage()">取消</el-button>
            <el-button type="primary" @click="createBusinessSystem()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'

export default {
    props: ['businessSystemNew', 'showBusinessSystemCreate'],
    
    emits: [ 'hideBusinessSystemCreatePage', 'addBusinessSystemToList'],
    
    setup(props, {attrs, emit, slots}) {
        const hideBusinessSystemCreatePage = () => {
            emit('hideBusinessSystemCreatePage');
        };
        
        const addBusinessSystemToList = (system) => {
        	emit('addBusinessSystemToList', system);
        };
        
        return {
        	hideBusinessSystemCreatePage,
        	addBusinessSystemToList
        }
    },
    computed: {
        showFlag: {
	        get() {
	            return this.showBusinessSystemCreate
	        },
	        set(value) {
	            this.hideBusinessSystemCreatePage();
	        }
        }
    },
    data() {
    	const systemIconOptions = ref(null);
        
        // [
        //     {value: '1', label: '管理类系统'},
        //     {value: '2', label: '统计分析类系统'},
        //     {value: '3', label: '数据可视化类系统'},
        //     {value: '4', label: '电商类系统'},
        //     {value: '5', label: '其他类系统'}
        // ];

        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "servletContext": [
                { required: true, message: '请求上下文不能为空！', trigger: 'blur' }
            ]
        });
    	
    	return {
        	validateRules,
            systemIconOptions
        }
    },   
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['BUSINESS_SYSTEM_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.systemIconOptions = data.data['BUSINESS_SYSTEM_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },   
    methods: {
    	createBusinessSystem() {
            if(this.businessSystemNew.servletContext != null && this.businessSystemNew.servletContext.startsWith("/")) {
                ElMessage.error(`请求上下文请不要以“/”开头！`);
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/businessSystem/add";
            this.$refs.businessSystemNewForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url, this.businessSystemNew).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`新建业务系统信息成功,请重新登录以更新权限信息！`);
                            this.addBusinessSystemToList(data.data);
                        }
                    }).catch(ex => {
                        ElMessage.error(`新建业务系统信息失败!\n` + ex);
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        }
    }
}

</script>
