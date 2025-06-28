<!-- 领域对象方法一键暴露 -->
<template>
<el-dialog v-model="showFlag" title="领域对象方法一键暴露">
    <template #header>
        <span class="title">领域对象方法一键暴露</span>
    </template>
    <el-form  :model="domainOperationExpose" label-width="120px" :rules="validateRules" ref="domainOperationExposeForm">
		<el-row :gutter="4">
	        <el-col :span="9">
	            <el-form-item label="领域对象" required prop="domainObjectId">
	                <el-input type="hidden" v-model="domainOperationExpose.domainObjectId" readonly size="small" />
	                <el-input v-model="domainObjectCode" readonly  size="small" />
	            </el-form-item>
	        </el-col>
	        <el-col :span="3" >
	            <el-form-item label="" label-width="10px">
	                <el-link  type="primary" @click="selectDomainObject" ><label>选择</label></el-link>&nbsp;
	                <el-link  type="primary" @click="clearDomainObject" ><label>清空</label></el-link>                                                
	            </el-form-item>
	        </el-col>
	        <el-col :span="12">
                <el-form-item label="领域对象方法" required prop="operationId">
                    <el-select v-model="domainOperationExpose.operationId" class="m-2" clearable placeholder="Select" size="small">
                           <el-option
                                  v-for="item in operations"
                                  :key="item.id"
                                  :label="item.code"
                                  :value="item.id"
                            />
                    </el-select>
                </el-form-item>
            </el-col>
	    </el-row>
	    
	    <el-row :gutter="4">
            <el-col :span="9">
                <el-form-item label="目标服务对象" required prop="serviceObjectId">
                    <el-input type="hidden" v-model="domainOperationExpose.serviceObjectId" readonly size="small" />
                    <el-input v-model="serviceObjectCode" readonly  size="small" />
                </el-form-item>
            </el-col>
            <el-col :span="3" >
                <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectServiceObject" ><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearServiceObject" ><label>清空</label></el-link>                                                
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="HttpMethod" required prop="httpMethod">
                    <el-select v-model="domainOperationExpose.httpMethod" class="m-2" clearable placeholder="Select" size="small">
                           <el-option
                                  v-for="item in httpMethodOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        
        <el-row :gutter="4">
            <el-col :span="24">              
                <el-form-item label="请求URL" required prop="httpMapping">
                    <el-input v-model="domainOperationExpose.httpMapping"  size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
    </el-form>
    
	<template #footer>
		<span class="dialog-footer"> 
		    <el-button @click="hideDomainOperationExposePage">取消</el-button> 
		    <el-button type="primary" @click="domainOperationExposeBack">上一步</el-button>
			<el-button type="primary" @click="exposeDomainOperation">确定</el-button>
		</span>
	</template>
</el-dialog>

<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideDomainObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>

<!-- 选择调用服务对象信息 -->
<serviceObjectSelect v-if="showTargetServiceObjectSelect"  @managedObjectSelected="targetManagedObjectSelected" @hideServiceObjectSelectPage="hideTargetServiceObjectSelectPage" :showServiceObjectSelect="showTargetServiceObjectSelect"  :managedObjectsForSelect="targetManagedObjectsForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

import domainObjectSelect from '../domainModel/workbench-domainObjectSelect.vue'
import serviceObjectSelect from '../domainModel/workbench-serviceObjectSelect.vue'

export default {
    props: ['domainOperationExpose','showDomainOperationExpose'],
    
    emits: ['domainOperationExposeBack', 'hideDomainOperationExposePage', 'finishDomainOperationExpose'],
    
    setup (props, {attrs, emit, slots}) {
        const finishDomainOperationExpose = ()=> {
            emit('finishDomainOperationExpose');
        };
        
        const hideDomainOperationExposePage = () => {
            emit('hideDomainOperationExposePage');	
        };
        
        const domainOperationExposeBack = () => {
            emit('domainOperationExposeBack'); 
        };
        
        return {
        	domainOperationExposeBack,
        	hideDomainOperationExposePage,
        	finishDomainOperationExpose
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showDomainOperationExpose;
            },
            set(value) {
                this.hideDomainOperationExposePage();
            }
        }
    },
    components: {
    	domainObjectSelect,
    	serviceObjectSelect
    },
    data() {            
    	const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        
        const targetManagedObjectsForSelect = ref(null);
        const showTargetServiceObjectSelect = ref(false);
        
        const domainObjectCode = ref("");
        const serviceObjectCode = ref("");
        
        const operations = ref([]);
        
        const httpMethodOptions = ref(null);

        const validateRules = ref({
            "domainObjectId": [
                { required: true, message: '领域对象不能为空！', trigger: 'blur' }
            ],
            "operationId": [
                { required: true, message: '领域对象方法不能为空！', trigger: 'blur' }
            ],
            "serviceObjectId": [
                { required: true, message: '目标服务对象不能为空！', trigger: 'blur' }
            ],
            "httpMethod": [
                { required: true, message: 'HttpMethod不能为空！', trigger: 'blur' }
            ],
            "httpMapping": [
                { required: true, message: '请求URL不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
        	showDomainObjectSelect,
            managedObjectsForSelect,
            
            showTargetServiceObjectSelect,
            targetManagedObjectsForSelect,
            
            domainObjectCode,
            serviceObjectCode,
            
            operations,
            httpMethodOptions,
            validateRules
        }
    },
    watch: {
    	
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
    	exposeDomainOperation() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/exposedService/exposeDomainOperation";
            this.$refs.domainOperationExposeForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.domainOperationExpose).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            ElMessage(`一键暴露领域对象方法成功！`);
                            this.finishDomainOperationExpose();
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
    	selectDomainObject() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.domainOperationExpose.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.managedObjectsForSelect = data.data;
                    this.showDomainObjectSelect = true;
                }
            });
            
        },
        clearDomainObject(){
            this.domainOperationExpose.domainObjectId = "";
            this.domainObjectCode = "";
            this.domainOperationExpose.operationId = "";
            this.operations = [];
        },
        managedObjectSelected(obj) {
            this.domainOperationExpose.domainObjectId = obj.id;
            this.domainObjectCode = obj.code;
            this.domainOperationExpose.operationId = "";
            this.managedObjectsForSelect = null;
            this.showDomainObjectSelect = false;
            
            var url = "/lcdp-proxy/lowcode/platform/be/api/operation/list/D/" + this.domainOperationExpose.domainObjectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.operations = data.data;
                }
            });
        },
        hideDomainObjectSelectPage(){
             this.managedObjectsForSelect = null;
             this.showDomainObjectSelect = false;
        },
        selectServiceObject() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/serviceObject/list/" + this.domainOperationExpose.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.targetManagedObjectsForSelect = data.data;
                    this.showTargetServiceObjectSelect = true;
                }
            });
        },
        clearServiceObject() {
            this.domainOperationExpose.serviceObjectId = "";
            this.serviceObjectCode = "";
        },
        targetManagedObjectSelected(obj) {
            this.domainOperationExpose.serviceObjectId = obj.id;
            this.serviceObjectCode = obj.code;
            this.targetManagedObjectsForSelect = null;
            this.showTargetServiceObjectSelect = false;
        },
        hideTargetServiceObjectSelectPage(){
            this.targetManagedObjectsForSelect = null;
            this.showTargetServiceObjectSelect = false;
        }
    }
}
</script>