<!-- 功能区域编辑 -->
<template>
<el-drawer v-model="showFlag" direction="rtl" size="65%">
    <template #header>
        <span class="title">功能区域编辑</span>
    </template>
    <template #default>
        <div>
        <el-scrollbar>
<el-form v-if="funcRegion != null" :model="funcRegion" label-width="120px" :rules="validateRules" ref="funcRegionForm">
    
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="功能区域标识" required prop="id">
                <el-input v-model="funcRegion.id" readonly size="small"/>
                <el-input type=hidden v-model="funcRegion.funcId" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="功能区域名称" required prop="name">
                <el-input v-model="funcRegion.name" size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="区域模板" required prop="tplRegionId">
                <el-input type=hidden v-model="funcRegion.tplCompositeId" size="small"/>
                <el-input type=hidden v-model="funcRegion.tplRegionId" size="small"/>
                <el-input v-model="funcRegion.tplRegion.name" readonly size="small" />
            </el-form-item>
        </el-col>
    
        <el-col :span="12">
            <el-form-item label="区域配置信息" prop="regionCfg">
                <el-input v-model="funcRegion.regionCfg" type="textarea" rows="4" size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
</el-form>
            </el-scrollbar>
        </div>
    </template>
    <template #footer>
        <div style="flex: auto">
            <el-button @click="hideFuncRegionEditPage()">取消</el-button>
            <el-button type="primary" @click="saveFuncRegion()">保存</el-button>
        </div>
    </template>
</el-drawer>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'


export default {
    props: ['funcRegion','showFuncRegionEdit'],
    
    emits: ['updateFuncRegionToList','hideFuncRegionEditPage'],
    
    setup(props, {attrs, emit, slots}) {
        const updateFuncRegionToList = (funcRegion) => {
            emit('updateFuncRegionToList', funcRegion);
        };
        
        const hideFuncRegionEditPage = () => {
            emit('hideFuncRegionEditPage');
        };
        
        return {
        	updateFuncRegionToList,
        	hideFuncRegionEditPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showFuncRegionEdit;
            },
            set(value) {
                this.hideFuncRegionEditPage();
            }
        }
    },
    components: {
    	
    },
    
    data() {

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "tplRegionId": [
                { required: true, message: '区域模板不能为空！', trigger: 'blur' }
            ]
        });

        
    	
    	return {
            validateRules
        }
    },
    
    methods: {
    	saveFuncRegion() {
            var url = "/lcdp-proxy/lowcode/platform/fe/api/funcRegion/save";
            this.$refs.funcRegionForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.funcRegion).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.funcRegion.updateTime = data.data.updateTime;
                            this.updateFuncRegionToList(data.data);
                            ElMessage(`更新功能区域信息成功!`);
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