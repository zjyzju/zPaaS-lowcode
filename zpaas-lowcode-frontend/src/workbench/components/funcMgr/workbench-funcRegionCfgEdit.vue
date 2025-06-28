<!-- 功能区域配置信息编辑 -->
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
            <el-form-item label="区域模板" prop="tplRegionId">
                <el-input type=hidden v-model="funcRegion.tplCompositeId" size="small"/>
                <el-input type=hidden v-model="funcRegion.tplRegionId" size="small"/>
                <el-input v-model="funcRegion.tplRegion.name" readonly size="small" />
            </el-form-item>
        </el-col>
    
        <el-col :span="12">
            <el-form-item label="区域配置信息" prop="regionCfg">
                <el-input v-model="funcRegion.regionCfg" type="textarea" readonly rows="4" size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="funcRegion.regionCfgJSON != null & funcRegion.tplRegion.regionType == 'RM'">
        <el-col :span="12">
            <el-form-item label="显示合并行" required prop="id">
                <el-select v-model="funcRegion.regionCfgJSON.showSumRow" class="m-2" clearable placeholder="Select" size="small" >
                    <el-option v-for="item in yesOrNoOptions" :key="item.value" :label="item.label" :value="item.value" />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="12">
            
        </el-col>
    </el-row>
</el-form>
            </el-scrollbar>
        </div>
    </template>
    <template #footer>
        <div style="flex: auto">
            <el-button @click="hideRegionCfgEditPage()">取消</el-button>
            <el-button type="primary" @click="saveRegionCfg()">保存</el-button>
        </div>
    </template>
</el-drawer>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'


export default {
    props: ['funcRegion','showRegionCfgEdit'],
    
    emits: ['hideRegionCfgEditPage'],
    
    setup(props, {attrs, emit, slots}) {
        const hideRegionCfgEditPage = () => {
            emit('hideRegionCfgEditPage');
        };
        
        return {
        	hideRegionCfgEditPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showRegionCfgEdit;
            },
            set(value) {
                this.hideRegionCfgEditPage();
            }
        }
    },
    components: {
    	
    },
    
    data() {
        const yesOrNoOptions = ref(null);

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ]
        });

        return {
            yesOrNoOptions,
            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_YES_OR_NO']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.yesOrNoOptions = data.data['PUB_YES_OR_NO'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        if(this.funcRegion.regionCfg == null || this.funcRegion.regionCfg.trim().length == 0) {
            this.funcRegion.regionCfgJSON = {};
        }else {
            this.funcRegion.regionCfgJSON = JSON.parse(this.funcRegion.regionCfg);
        }
    },
    methods: {
    	saveRegionCfg() {
            var url = "/lcdp-proxy/lowcode/platform/fe/api/funcRegion/saveRegionCfg";
            this.$refs.funcRegionForm.validate((valid, fields)=> {
                if(valid) {
                    this.funcRegion.regionCfg = JSON.stringify(this.funcRegion.regionCfgJSON);
                    axiosClient.post(url,this.funcRegion).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.funcRegion.updateTime = data.data.updateTime;
                            ElMessage(`更新功能区域配置信息成功!`);
                            this.hideRegionCfgEditPage();
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