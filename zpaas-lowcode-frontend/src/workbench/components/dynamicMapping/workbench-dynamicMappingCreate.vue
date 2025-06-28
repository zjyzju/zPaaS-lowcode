<!-- 新建动态映射 -->
<template>
<el-dialog v-model="showFlag" title="新建动态映射">
    <template #header>
        <span class="title">新建动态映射</span>
    </template>
    <el-form  :model="newDynamicMapping" label-width="120px" :rules="validateRules" ref="newDynamicMappingForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="newDynamicMapping.name"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="映射类型" required prop="mappingType">
                    <el-select v-model="newDynamicMapping.mappingType" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in mappingTypeOptions"
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
                <el-form-item label="业务类型">
                    <el-input v-model="newDynamicMapping.businessType"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="子业务类型">
                    <el-input v-model="newDynamicMapping.subBusinessType"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="关键属性">
                    <el-input v-model="newDynamicMapping.keyAttribute"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="描述">
                    <el-input v-model="newDynamicMapping.description"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDynamicMappingCreatePage()">取消</el-button>
            <el-button type="primary" @click="dynamicMappingCreateBack()">上一步</el-button>
            <el-button type="primary" @click="createDynamicMapping()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'



export default {
    props: ['newDynamicMapping','showDynamicMappingCreate'],
    
    emits: ['addNewDynamicMappingToList', 'hideDynamicMappingCreatePage', 'dynamicMappingCreateBack'],
    
    setup (props, {attrs, emit, slots}) {
        const addNewDynamicMappingToList = (newDynamicMapping)=> {
            emit('addNewDynamicMappingToList', newDynamicMapping);
        };
        
        const hideDynamicMappingCreatePage = () => {
            emit('hideDynamicMappingCreatePage');	
        };
        
        const dynamicMappingCreateBack = () => {
            emit('dynamicMappingCreateBack'); 
        };
        
        return {
        	addNewDynamicMappingToList,
        	hideDynamicMappingCreatePage,
        	dynamicMappingCreateBack
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showDynamicMappingCreate;
            },
            set(value) {
                this.hideDynamicMappingCreatePage();
            }
        }
    },
    data() {            
        const mappingTypeOptions = ref(null);

        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "mappingType": [
                { required: true, message: '映射类型不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
        	mappingTypeOptions,
            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['DYNAMIC_MAPPING_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.mappingTypeOptions = data.data['DYNAMIC_MAPPING_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	createDynamicMapping() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/dynamicMapping/add";
            this.$refs.newDynamicMappingForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.newDynamicMapping).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`新增动态映射成功`);
                            this.addNewDynamicMappingToList(data.data);
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