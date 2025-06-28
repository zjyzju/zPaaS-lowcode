<!-- 取消发布服务方法 -->
<template>
<el-dialog v-model="showFlag" title="取消发布服务方法">
    <template #header>
        <span class="title">取消发布服务方法</span>
    </template>
    <el-form  :model="exposedOperation" label-width="120px" :rules="validateRules" ref="exposedOperationForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="发布标识" required prop="id">
                    <el-input v-model="exposedOperation.id" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="发布状态">
                    <el-select v-model="exposedOperation.status" class="m-2" disabled placeholder="Select" size="small">
                           <el-option
                                  v-for="item in statusOptions"
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
                <el-form-item label="服务编码">
                    <el-input v-model="exposedOperation.serviceCode" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="方法编码">
                    <el-input v-model="exposedOperation.operationCode" readonly size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="HttpMethod">
                    <el-select v-model="exposedOperation.httpMethod" class="m-2" disabled placeholder="Select" size="small">
                           <el-option
                                  v-for="item in httpMethodOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="请求URL">
                    <el-input v-model="exposedOperation.httpMapping" readonly size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="创建时间">
                    <el-input v-model="exposedOperation.createTime" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="更新时间">
                    <el-input v-model="exposedOperation.updateTime" readonly size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideCancelExposeOperationPage()">取消</el-button>
            <el-button type="primary" @click="cancelExposeOperation()">确定</el-button>
        </span>
    </template>
</el-dialog>
</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'

export default {
    props: ['exposedOperation', 'showCancelExposeOperation'],
    
    emits: ['updateCancelExposeOperationToList', 'hideCancelExposeOperationPage'],
    
    setup(props, {attrs, emit, slots}) {
        const updateCancelExposeOperationToList = (exposedOp) => {
            emit('updateCancelExposeOperationToList', exposedOp);
        };
        
        const hideCancelExposeOperationPage = () => {
            emit('hideCancelExposeOperationPage');
        }
        
        return {
        	updateCancelExposeOperationToList,
        	hideCancelExposeOperationPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showCancelExposeOperation;
            },
            set(value) {
                this.hideCancelExposeOperationPage();
            }
        }
    },
    data() {
        const httpMethodOptions = ref(null);
        
        const statusOptions = ref(null);

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
            httpMethodOptions,
            statusOptions,
            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_HTTP_METHOD','PUB_STATUS']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.httpMethodOptions = data.data['PUB_HTTP_METHOD'];
                this.statusOptions = data.data['PUB_STATUS'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },  
    methods: {
    	cancelExposeOperation() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/operation/cancelExpose";
            this.$refs.exposedOperationForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url, this.exposedOperation).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                            ElMessage(`取消发布服务方法成功`);
                            this.updateCancelExposeOperationToList(this.exposedOperation);
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