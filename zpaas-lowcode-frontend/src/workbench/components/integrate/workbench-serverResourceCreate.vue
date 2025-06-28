<!-- 新建技术服务 -->
<template>
<el-dialog v-model="showFlag" title="新建技术服务">
    <template #header>
        <span class="title">新建技术服务</span>
    </template>
    <el-form  :model="newServerResource" label-width="120px" :rules="validateRules" ref="newServerResourceForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="newServerResource.name"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="描述">
                    <el-input v-model="newServerResource.description"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            
            <el-col :span="12">
                <el-form-item label="类型" required prop="resourceType">
                    <el-select v-model="newServerResource.resourceType" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in resourceTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                &nbsp;
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="用户名">
                    <el-input v-model="newServerResource.username"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="密码">
                    <el-input type="password" v-model="newServerResource.password"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="访问串">
                    <el-input v-model="newServerResource.url"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="服务器地址">
                    <el-input v-model="newServerResource.host"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="24">
                <el-form-item label="端口">
                    <el-input v-model="newServerResource.port"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="服务资源配置">
                    <el-input type="textarea" :rows="4" v-model="newServerResource.serverCfg"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>      
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideServerResourceCreatePage()">取消</el-button>
            <el-button type="primary" @click="serverResourceCreateBack()">上一步</el-button>
            <el-button type="primary" @click="createServerResource()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'



export default {
    props: ['newServerResource','showServerResourceCreate'],
    
    emits: ['addNewServerResourceToList', 'hideServerResourceCreatePage', 'serverResourceCreateBack'],
    
    setup (props, {attrs, emit, slots}) {
        const addNewServerResourceToList = (newServerResource)=> {
            emit('addNewServerResourceToList', newServerResource);
        };
        
        const hideServerResourceCreatePage = () => {
            emit('hideServerResourceCreatePage');	
        };
        
        const serverResourceCreateBack = () => {
            emit('serverResourceCreateBack'); 
        };
        
        return {
        	addNewServerResourceToList,
        	hideServerResourceCreatePage,
        	serverResourceCreateBack
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showServerResourceCreate;
            },
            set(value) {
                this.hideServerResourceCreatePage();
            }
        }
    },
    data() {            
        const resourceTypeOptions = ref(null);

        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "resourceType": [
                { required: true, message: '类型不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
        	resourceTypeOptions,
            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['SERVER_RESOURCE_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.resourceTypeOptions = data.data['SERVER_RESOURCE_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	createServerResource() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/serverResource/add";
            this.$refs.newServerResourceForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.newServerResource).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`新增技术服务成功`);
                            this.addNewServerResourceToList(data.data);
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