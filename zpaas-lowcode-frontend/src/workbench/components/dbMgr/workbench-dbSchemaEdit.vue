<!-- 新建数据库 -->
<template>
<el-form  v-if="dbSchema != null && dbSchema.id != null" :model="dbSchema" label-width="120px" :rules="validateRules" ref="dbSchemaForm">   
    <el-row :gutter="4">
       <el-col :span="24">
           <el-breadcrumb separator=">">
               <el-breadcrumb-item><span class="title">数据库对象</span></el-breadcrumb-item>
               <el-breadcrumb-item>数据库</el-breadcrumb-item>
               <el-breadcrumb-item ><span class="title1">{{dbSchema.name}}</span></el-breadcrumb-item>
            </el-breadcrumb>
       </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="标识" required prop="id">
                <el-input v-model="dbSchema.id" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="名称" required prop="name">
                <el-input v-model="dbSchema.name" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="描述" required prop="description">
                <el-input v-model="dbSchema.description" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="类型" required prop="dbType">
                <el-select v-model="dbSchema.dbType" class="m-2" placeholder="Select" size="small">
                       <el-option
                              v-for="item in dbTypeOptions"
                              :key="item.value"
                              :label="item.label"
                              :value="item.value"
                        />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="用户名" required prop="username">
                <el-input v-model="dbSchema.username" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="密码">
                <el-input type="password" v-model="dbSchema.password" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="访问串" required prop="url">
                <el-input v-model="dbSchema.url"  size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="驱动类">
                <el-input v-model="dbSchema.driverClass"  size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-form-item label="数据源配置">
                <el-input type="textarea" :rows="4" v-model="dbSchema.dataSourceCfg"  size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="创建时间">
                <el-input v-model="dbSchema.createTime" size="small"/>
                <el-input type="hidden" v-model="dbSchema.status" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            &nbsp;
        </el-col>
        <el-col :span="8">
            &nbsp;
        </el-col>
    </el-row> 
    <el-row :gutter="4">
        <el-col :span="20" class="toolbar-right">
            <el-button type="primary" size="small" @click="saveDbSchema()">保存</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="deleteDbSchema()">删除</el-button>
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
        const updateDbSchemaToList = (dbSchema)=> {
            emit('updateToList', 'dbSchema', dbSchema);
        };
        
        const deleteDbSchemaFromList = (dbSchema)=> {
            emit('deleteFromList', 'dbSchema', dbSchema);
        };
        
        return {
        	updateDbSchemaToList,
        	deleteDbSchemaFromList
        };
    },
    
    data() { 
        const dbSchema = ref(null);
        const router = useRoute();

        const dbTypeOptions = ref(null);

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
            "dbType": [
                { required: true, message: '类型不能为空！', trigger: 'blur' }
            ],
            "username": [
                { required: true, message: '用户名不能为空！', trigger: 'blur' }
            ],
            "url": [
                { required: true, message: '访问串不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
            dbSchema,
            router,
        	dbTypeOptions,
            validateRules
        }
    },
    watch: {        
        'router.query': function(val, old){
            if(val != null && val.objectId != null && (old == null || val.objectId != old.objectId)) {
                setTimeout(()=>{
                    this.loadDbSchema();
                },100);
            }else if(val == null || val.objectId == null) {
                this.dbSchema = null;
            }
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['DATABASE_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.dbTypeOptions = data.data['DATABASE_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        this.loadDbSchema();
    },
    methods: {
        loadDbSchema() {
            var objectId = this.router.query.objectId;
            if(objectId == null) {
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/dbSchema/byId/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.dbSchema = data.data;
                }
            });
        },
    	saveDbSchema() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/dbSchema/save";
            this.$refs.dbSchemaForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.dbSchema).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                        this.updateDbSchemaToList(this.dbSchema);
                            ElMessage(`更新数据库对象成功，共更新(`+data.data+`)条属性`);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        deleteDbSchema() {
            ElMessageBox.confirm(
                '该数据库(' + this.dbSchema.description + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/be/api/dbSchema/delete/" + this.dbSchema.id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         ElMessage(`删除数据库(`+ this.dbSchema.description +`)成功。`);
                         this.deleteDbSchemaFromList(this.dbSchema);
                     }
                 }).catch(()=>{
                     ElMessage.error(`删除数据库(`+ this.dbSchema.description +`)失败！`);
                 });
             }).catch(()=>{});
        },
    }
}
</script>
<style>
.layout-container-main .toolbar-right {
  display: inline-flex;
  align-items: center;
  justify-content: right; 
  height: 100%;
  right: 20px;
}
</style>