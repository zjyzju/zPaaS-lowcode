<!-- 编辑技术服务 -->
<template>
<el-form  v-if="serverResource != null && serverResource.id != null" :model="serverResource" label-width="120px" :rules="validateRules" ref="serverResourceForm">
    <el-row :gutter="4">
       <el-col :span="24">
           <el-breadcrumb separator=">">
               <el-breadcrumb-item><span class="title">技术服务</span></el-breadcrumb-item>
               <el-breadcrumb-item>技术服务</el-breadcrumb-item>
               <el-breadcrumb-item ><span class="title1">{{serverResource.name}}</span></el-breadcrumb-item>
            </el-breadcrumb>
       </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="标识" required prop="id">
                <el-input v-model="serverResource.id" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="名称" required prop="name">
                <el-input v-model="serverResource.name" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="描述">
                <el-input v-model="serverResource.description" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="类型" required prop="resourceType">
                <el-select v-model="serverResource.resourceType" class="m-2" placeholder="Select" size="small">
                       <el-option
                              v-for="item in resourceTypeOptions"
                              :key="item.value"
                              :label="item.label"
                              :value="item.value"
                        />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="用户名">
                <el-input v-model="serverResource.username" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="密码">
                <el-input type="password" v-model="serverResource.password" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="访问串">
                <el-input v-model="serverResource.url"  size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="服务资源地址">
                <el-input v-model="serverResource.host"  size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="端口">
                <el-input v-model="serverResource.port"  size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="创建时间">
                <el-input v-model="serverResource.createTime" readonly size="small"/>
                <el-input type="hidden" v-model="serverResource.status" size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="服务资源配置">
                <el-input type="textarea" :rows="4" v-model="serverResource.serverCfg"  size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="20" class="toolbar-right">
            <el-button type="primary" size="small" @click="saveServerResource()">保存</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="deleteServerResource()">删除</el-button>
        </el-col>
        <el-col :span="4" class="toolbar-right">&nbsp;</el-col>
    </el-row>
</el-form>
</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'


export default {
    props: [],
    
    emits: ['updateToList', 'deleteFromList'],
    
    setup (props, {attrs, emit, slots}) {
        const updateServerResourceToList = (serverResource)=> {
            emit('updateToList', 'serverResource', serverResource);
        };
        
        const deleteServerResourceFromList = (serverResource)=> {
            emit('deleteFromList', 'serverResource', serverResource);
        };
        
        return {
        	updateServerResourceToList,
        	deleteServerResourceFromList
        };
    },
    
    data() {    
        const serverResource = ref(null);
        const router = useRoute();

        const resourceTypeOptions = ref(null);

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "resourceType": [
                { required: true, message: '类型不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
            serverResource,
            router,
        	resourceTypeOptions,
            validateRules
        }
    },
    watch: {        
        'router.query': function(val, old){
            if(val != null && val.objectId != null && (old == null || val.objectId != old.objectId)) {
                setTimeout(()=>{
                    this.loadServerResource();
                },100);
            }else if(val == null || val.objectId == null) {
                this.serverResource = null;
            }
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

        this.loadServerResource();
    },
    methods: {
        loadServerResource() {
            var objectId = this.router.query.objectId;
            if(objectId == null) {
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/serverResource/byId/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.serverResource = data.data;
                }
            });
        },
    	saveServerResource() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/serverResource/save";
            this.$refs.serverResourceForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.serverResource).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                            this.updateServerResourceToList(this.serverResource);
                            ElMessage(`更新技术服务信息成功，共更新(`+data.data+`)条属性`);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        deleteServerResource() {
            ElMessageBox.confirm(
                '该技术服务资源(' + this.serverResource.name + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/be/api/serverResource/delete/" + this.serverResource.id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         this.deleteServerResourceFromList(this.serverResource);
                         
                         ElMessage(`删除技术服务资源(`+ this.serverResource.name +`)成功。`);
                     }
                 }).catch(()=>{
                     ElMessage.error(`删除技术服务资源(`+ this.serverResource.name +`)失败！`);
                 });
             }).catch(()=>{});
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
</style>