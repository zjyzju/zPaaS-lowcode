<!-- 新建数据库表 -->
<template>
<el-dialog v-model="showFlag" title="新建数据库表">
    <template #header>
        <span class="title">新建数据库表</span>
    </template>
    <el-form  :model="newDbTable" label-width="120px" :rules="validateRules" ref="newDbTableForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="newDbTable.name"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="描述" required prop="description">
                    <el-input v-model="newDbTable.description"  size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
    </el-form>
    <el-form  :model="newDbTable" label-width="120px">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="归属数据库" required prop="schemaId">
                    <el-select v-model="newDbTable.schemaId" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in dbSchemas" :key="item.id" :label="item.name" :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label=" ">
                    &nbsp;
                </el-form-item>
            </el-col>            
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDbTableCreatePage()">取消</el-button>
            <el-button type="primary" @click="manageDbTableCreateBack()">上一步</el-button>
            <el-button type="primary" @click="createDbTable()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['newDbTable', 'showDbTableCreate', 'dbSchemas'],
    
    emits: [ 'hideDbTableCreatePage', 'addNewDbTableToList', 'dbTableCreateBack'],
    
    setup(props, {attrs, emit, slots}) {
        const hideDbTableCreatePage = () => {
            emit('hideDbTableCreatePage');
        };
        
        const updateDbTableToList = (dbTable) => {
            emit('addNewDbTableToList', dbTable);
        };
        
        const manageDbTableCreateBack = () => {
        	emit('dbTableCreateBack');
        };
        
        return {
        	hideDbTableCreatePage,
            updateDbTableToList,
            manageDbTableCreateBack
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showDbTableCreate;
            },
            set(value) {
                this.hideDbTableCreatePage();
            }
        }
    },
    data() {
        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "description": [
                { required: true, message: '描述不能为空！', trigger: 'blur' }
            ],
            "schemaId": [
                { required: true, message: '归属数据库不能为空！', trigger: 'blur' }
            ]
        });

        return {
            validateRules
        }
    },
    
    methods: {
    	createDbTable() {
            if(this.newDbTable.schemaId == null || this.newDbTable.schemaId.trim().length == 0) {
                ElMessage.error(`归属数据库不能为空！`);
                return;
            }
    		var url = "/lcdp-proxy/lowcode/platform/be/api/dbTable/add";
            this.$refs.newDbTableForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url, this.newDbTable).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`新增数据库表成功`);
                            this.updateDbTableToList(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
    }
}

</script>
