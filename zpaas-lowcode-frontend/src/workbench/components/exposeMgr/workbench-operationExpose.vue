<!-- 发布服务方法 -->
<template>
<el-dialog v-model="showFlag" title="发布服务方法">
    <template #header>
        <span class="title">发布服务方法</span>
    </template>
    <el-form  :model="exposedOperation" label-width="120px" :rules="validateRules" ref="exposedOperationForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="服务编码" required prop="serviceCode">
                    <el-input v-model="exposedOperation.serviceCode"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="方法编码" required prop="operationCode">
                    <el-input v-model="exposedOperation.operationCode"  size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="HttpMethod"  required prop="httpMethod">
                    <el-select v-model="exposedOperation.httpMethod" class="m-2" placeholder="Select" size="small">
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
                <el-form-item label="请求URL" required prop="httpMapping">
                    <el-input v-model="exposedOperation.httpMapping"  size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideExposeOperationPage()">取消</el-button>
            <el-button type="primary" @click="exposeOperation()">确定</el-button>
        </span>
    </template>
</el-dialog>
</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'

export default {
	props: ['exposedOperation', 'showExposeOperation'],
	
	emits: ['updateExposedOperationToList', 'hideExposeOperationPage'],
	
	setup(props, {attrs, emit, slots}) {
		const updateExposedOperationToList = (exposedOp) => {
			emit('updateExposedOperationToList', exposedOp);
		};
		
		const hideExposeOperationPage = () => {
			emit('hideExposeOperationPage');
		}
		
		return {
			updateExposedOperationToList,
			hideExposeOperationPage
		}
	},
	computed: {
        showFlag: {
            get() {
                return this.showExposeOperation;
            },
            set(value) {
                this.hideExposeOperationPage();
            }
        }
    },
	data() {
		const httpMethodOptions = ref(null);

        const validateRules = ref({
            "serviceCode": [
                { required: true, message: '服务编码不能为空！', trigger: 'blur' }
            ],
            "operationCode": [
                { required: true, message: '方法编码不能为空！', trigger: 'blur' }
            ],
            "httpMethod": [
                { required: true, message: 'HttpMethod不能为空！', trigger: 'blur' }
            ],
            "httpMapping": [
                { required: true, message: '请求URL不能为空！', trigger: 'blur' }
            ]
        });
		
		return {
			httpMethodOptions,
            validateRules
		}
	},
	mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_HTTP_METHOD']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.httpMethodOptions = data.data['PUB_HTTP_METHOD'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },  
	methods: {
		exposeOperation() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/operation/expose";
            this.$refs.exposedOperationForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.exposedOperation).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            ElMessage(`发布服务方法成功`);
                            this.updateExposedOperationToList(data.data);
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

