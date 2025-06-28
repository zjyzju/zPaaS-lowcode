<!-- 新建数据库 -->
<template>
<el-dialog v-model="showFlag" title="新建数据库">
    <template #header>
        <span class="title">新建数据库</span>
    </template>
    <el-form  :model="newDbSchema" label-width="120px" :rules="validateRules" ref="newDbSchemaForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="newDbSchema.name"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="描述" required prop="description">
                    <el-input v-model="newDbSchema.description"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            
            <el-col :span="12">
                <el-form-item label="类型" required prop="dbType">
                    <el-select v-model="newDbSchema.dbType" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in dbTypeOptions"
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
                <el-form-item label="用户名" required prop="username">
                    <el-input v-model="newDbSchema.username"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="密码">
                    <el-input type="password" v-model="newDbSchema.password"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="访问串" required prop="url">
                    <el-input v-model="newDbSchema.url"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="驱动类">
                    <el-input v-model="newDbSchema.driverClass"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="数据源配置">
                    <el-input type="textarea" :rows="4" v-model="newDbSchema.dataSourceCfg"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>      
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDbSchemaCreatePage()">取消</el-button>
            <el-button type="primary" @click="dbSchemaCreateBack()">上一步</el-button>
            <el-button type="primary" @click="createDbSchema()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'



export default {
    props: ['newDbSchema','showDbSchemaCreate'],
    
    emits: ['addNewDbSchemaToList', 'hideDbSchemaCreatePage', 'dbSchemaCreateBack'],
    
    setup (props, {attrs, emit, slots}) {
        const addNewDbSchemaToList = (newDbSchema)=> {
            emit('addNewDbSchemaToList', newDbSchema);
        };
        
        const hideDbSchemaCreatePage = () => {
            emit('hideDbSchemaCreatePage');	
        };
        
        const dbSchemaCreateBack = () => {
            emit('dbSchemaCreateBack'); 
        };
        
        return {
        	addNewDbSchemaToList,
        	hideDbSchemaCreatePage,
        	dbSchemaCreateBack
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showDbSchemaCreate;
            },
            set(value) {
                this.hideDbSchemaCreatePage();
            }
        }
    },
    data() {            
        const dbTypeOptions = ref(null);
        

        const validateRules = ref({
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
        	dbTypeOptions,
            validateRules
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
    },
    methods: {
    	createDbSchema() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/dbSchema/add";
            this.$refs.newDbSchemaForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.newDbSchema).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`新增数据库成功`);
                            this.addNewDbSchemaToList(data.data);
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