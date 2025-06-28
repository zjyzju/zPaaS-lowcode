<!-- 新建方法 -->
<template>
<el-dialog v-model="showFlag" title="新建方法">
    <template #header>
        <span class="title">新建方法</span>
    </template>
    <el-form  :model="newOperation" label-width="120px" :rules="validateRules" ref="newOperationForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="编码" required prop="code">
                    <el-input v-model="newOperation.code"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="newOperation.name"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
		    <el-col :span="12">
		        <el-form-item label="业务流类型" required prop="flowType">
		            <el-select v-model="newOperation.flowType" class="m-2" placeholder="Select" clearable size="small">
		                <el-option
		                  v-for="item in flowTypeOptions"
		                  :key="item.value"
		                  :label="item.label"
		                  :value="item.value"
		                />
		             </el-select>
		        </el-form-item>
		    </el-col>
		    <el-col :span="12">
		        <el-form-item label="对应数据库">
		            <el-select v-model="newOperation.dbSchemaId" class="m-2" placeholder="Select" clearable size="small">
		                <el-option
		                  v-for="item in dbSchemas"
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
                <el-form-item label="是否加入事务" required prop="transactionRequired">
                    <el-select v-model="newOperation.transactionRequired" class="m-2" placeholder="Select" clearable size="small">
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
                
            </el-col>
        </el-row>
        
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideOperationCreatePage()">取消</el-button>
            <el-button type="primary" @click="createOperation()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'



export default {
    props: ['newOperation','showOperationCreate'],
    
    emits: ['addNewOperationToList', 'hideOperationCreatePage'],
    
    setup (props, {attrs, emit, slots}) {
        const addNewOperationToList = (newOperation)=> {
            emit('addNewOperationToList', newOperation);
        };
        
        const hideOperationCreatePage = () => {
            emit('hideOperationCreatePage');	
        };
        
        return {
        	addNewOperationToList,
        	hideOperationCreatePage
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showOperationCreate;
            },
            set(value) {
                this.hideOperationCreatePage();
            }
        }
    },
    data() {            
        const flowTypeOptions = ref(null);
        
        const yesOrNoOptions = ref(null);

        const validateRules = ref({
            "code": [
                { required: true, message: '编码不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "transactionRequired": [
                { required: true, message: '是否加入事务不能为空！', trigger: 'blur' }
            ],
            "flowType": [
                { required: true, message: '业务流类型不能为空！', trigger: 'blur' }
            ]
        });

        const dbSchemas = ref(null);
        
        return {
            flowTypeOptions,
            yesOrNoOptions,
            validateRules,
            dbSchemas
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_YES_OR_NO','BUSINESS_FLOW_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.yesOrNoOptions = data.data['PUB_YES_OR_NO'];
                this.flowTypeOptions = data.data['BUSINESS_FLOW_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        var url = "/lcdp-proxy/lowcode/platform/be/api/dbSchema/list/" + this.newOperation.systemId;
        axiosClient.get(url).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.dbSchemas = data.data;
            }
        });
    },
    methods: {
    	createOperation() {
            this.$refs.newOperationForm.validate((valid, fields)=> {
                if(valid) {
                    var url = "/lcdp-proxy/lowcode/platform/be/api/operation/addWithFlow";
                    axiosClient.post(url,this.newOperation).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`新增方法成功`);
                            this.addNewOperationToList(data.data);
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