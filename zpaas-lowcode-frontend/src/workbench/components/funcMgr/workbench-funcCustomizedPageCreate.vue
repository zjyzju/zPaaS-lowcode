<!-- 自定义页面信息新建 -->
<template>
<el-drawer v-model="showFlag" direction="rtl" size="65%">
    <template #header>
        <span class="title">自定义页面新建</span>
    </template>
    <template #default>
        <div>
        <el-scrollbar>
<el-form v-if="newCustomizedPage != null" :model="newCustomizedPage" label-width="120px" :rules="validateRules" ref="newCustomizedPageForm">
    
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="页面名称" required prop="name">
                <el-input v-model="newCustomizedPage.name" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="是否主页面" required prop="isMainPage">
                <el-select v-model="newCustomizedPage.isMainPage" disabled class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in yesNoOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="页面配置信息" prop="pageCfg">
                <el-input v-model="newCustomizedPage.pageCfg" type="textarea" rows="4" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="12">&nbsp;</el-col>
    </el-row>
</el-form>
            </el-scrollbar>
        </div>
    </template>
    <template #footer>
        <div style="flex: auto">
            <el-button @click="hideCustomizedPageCreate()">取消</el-button>
            <el-button type="primary" @click="saveCustomizedPage()">保存</el-button>
        </div>
    </template>
</el-drawer>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'


export default {
    props: ['newCustomizedPage','showCustomizedPageCreate'],
    
    emits: ['updateNewCustomizedPageToList','hideCustomizedPageCreate'],
    
    setup(props, {attrs, emit, slots}) {
        const updateNewCustomizedPageToList = (newCustomizedPage) => {
            emit('updateNewCustomizedPageToList', newCustomizedPage);
        };
        
        const hideCustomizedPageCreate = () => {
            emit('hideCustomizedPageCreate');
        };
        
        return {
        	updateNewCustomizedPageToList,
        	hideCustomizedPageCreate
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showCustomizedPageCreate;
            },
            set(value) {
                this.hideCustomizedPageCreate();
            }
        }
    },
    components: {
    	
    },
    
    data() {
        const yesNoOptions = ref(null);

        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "isMainPage": [
                { required: true, message: '区域模板不能为空！', trigger: 'blur' }
            ]
        });

        
    	
    	return {
            validateRules,
            yesNoOptions
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_YES_OR_NO']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.yesNoOptions = data.data['PUB_YES_OR_NO'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	saveCustomizedPage() {
            var url = "/lcdp-proxy/lowcode/platform/fe/api/customizedPage/create";
            this.$refs.newCustomizedPageForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.newCustomizedPage).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.updateNewCustomizedPageToList(data.data);
                            ElMessage(`新增自定义页面信息成功!`);
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
<style scoped>
.layout-container-main .toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
  height: 100%;
  right: 20px;
}

.layout-container-main .toolbar-right {
  display: inline-flex;
  align-items: center;
  justify-content: right; 
  height: 100%;
  right: 20px;
}
</style>