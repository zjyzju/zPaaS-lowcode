<!-- 编辑系统信息 -->
<template>
<el-dialog v-model="showFlag" title="编辑系统信息" append-to-body>
    <template #header>
        <span class="title">编辑系统信息</span>
    </template>
    <el-form  :model="businessSystem" label-width="120px" :rules="validateRules" ref="businessSystemForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="标识" required prop="id">
                    <el-input v-model="businessSystem.id" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="businessSystem.name"  size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="描述">
                    <el-input v-model="businessSystem.description"  size="small"/>
                </el-form-item>
            </el-col>  
            <el-col :span="12">              
                <el-form-item label="请求上下文" required prop="servletContext">
                    <el-input v-model="businessSystem.servletContext"  size="small"/>
                </el-form-item>
            </el-col>
                     
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="系统图标">
                    <el-select v-model="businessSystem.systemIcon" class="m-2" clearable placeholder="Select" size="small">
                        <el-option v-for="item in systemIconOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="版本">
                    <el-input v-model="businessSystem.version"  size="small"/>
                </el-form-item>
            </el-col>  
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="创建时间">
                    <el-input v-model="businessSystem.createTime" readonly size="small"/>
                </el-form-item>
            </el-col> 
            <el-col :span="12">

            </el-col>
        </el-row>
        
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideBusinessSystemEditPage()">取消</el-button>
            <el-button type="primary" @click="saveBusinessSystem()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'


export default {
    props: ['businessSystem', 'showBusinessSystemEdit'],
    
    emits: [ 'hideBusinessSystemEditPage', 'updateBusinessSystemToInfo'],
    
    setup(props, {attrs, emit, slots}) {
        const hideBusinessSystemEditPage = () => {
            emit('hideBusinessSystemEditPage');
        };
        
        const updateBusinessSystemToInfo = (system) => {
        	emit('updateBusinessSystemToInfo', system);
        };
        
        return {
        	hideBusinessSystemEditPage,
        	updateBusinessSystemToInfo
        }
    },
    
    components: {
    	
    },
    
    computed: {
        showFlag: {
            get() {
                return this.showBusinessSystemEdit
            },
            set(value) {
                this.hideBusinessSystemEditPage();
            }
        }
    },
    data() {
    	const systemIconOptions = ref(null);

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "servletContext": [
                { required: true, message: '请求上下文不能为空！', trigger: 'blur' }
            ],
            "tenantId": [
                { required: true, message: '归属租户不能为空！', trigger: 'blur' }
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
    	saveBusinessSystem() {
            if(this.businessSystem.servletContext != null && this.businessSystem.servletContext.startsWith("/")) {
                ElMessage.error(`请求上下文请不要以“/”开头！`);
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/businessSystem/save";
            this.$refs.businessSystemForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url, this.businessSystem).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data > 0) {
                            ElMessage(`修改业务系统信息成功`);
                            this.updateBusinessSystemToInfo(this.businessSystem);
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
