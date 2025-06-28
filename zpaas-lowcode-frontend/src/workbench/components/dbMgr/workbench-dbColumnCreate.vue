<!-- 新建表字段 -->

<template>
<el-dialog v-model="showFlag" title="新建表字段">
    <template #header>
        <span class="title">新建表字段</span>
    </template>
    <el-form  :model="newDbColumn" label-width="120px" :rules="validateRules" ref="newDbColumnForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="newDbColumn.name"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="描述" required prop="description">
                    <el-input v-model="newDbColumn.description"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="类型" required prop="type">
                      <el-select v-model="newDbColumn.type" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in columnTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="长度">
                    <el-input v-model="newDbColumn.length"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="是否主键">
                      <el-select v-model="newDbColumn.isPrimaryKey" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in yesOrNoOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="主键生成方法">
                      <el-select v-model="newDbColumn.keyGenerateMethod" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in keyGenerateMethods"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
         <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="是否可空">
                      <el-select v-model="newDbColumn.isNull" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in yesOrNoOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="显示顺序" required prop="displayOrder">
                    <el-input v-model="newDbColumn.displayOrder"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDbColumnCreatePage">取消</el-button>
            <el-button type="primary" @click="createDbColumn()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'


export default {
    props: ['newDbColumn','showDbColumnCreate'],
    
    emits: ['hideDbColumnCreatePage', 'addNewDbColumnToList'],
    
    setup (props, {attrs, emit, slots}) {
        const hideDbColumnCreatePage = ()=> {
            emit('hideDbColumnCreatePage');
        };
        
        const addNewDbColumnToList = (newAttr) => {
        	emit('addNewDbColumnToList', newAttr);
        };
        
        return {
        	hideDbColumnCreatePage,
        	addNewDbColumnToList
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showDbColumnCreate;
            },
            set(value) {
                this.hideDbColumnCreatePage();
            }
        }
    },
    data() {            
    	const keyGenerateMethods = ref(null);
        
    	const yesOrNoOptions = ref(null);
    	
    	const columnTypeOptions = ref(null);

        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "description": [
                { required: true, message: '描述不能为空！', trigger: 'blur' }
            ],
            "type": [
                { required: true, message: '类型不能为空！', trigger: 'blur' }
            ],
            "displayOrder": [
                { required: true, message: '顺序不能为空！', trigger: 'blur' },
                { pattern: /^[0-9]*$/, message: '顺序必须是整数类型！', trigger: 'blur' }
            ]
        });
        
        return {
        	keyGenerateMethods,
        	yesOrNoOptions,
        	columnTypeOptions,
            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_YES_OR_NO','KEY_GENERATE_METHOD','DB_COLUMN_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.yesOrNoOptions = data.data['PUB_YES_OR_NO'];
                this.keyGenerateMethods = data.data['KEY_GENERATE_METHOD'];
                this.columnTypeOptions = data.data['DB_COLUMN_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	createDbColumn() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/dbColumn/add";
            this.$refs.newDbColumnForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.newDbColumn).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`新增数据库表字段成功`);
                            this.addNewDbColumnToList(data.data);
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