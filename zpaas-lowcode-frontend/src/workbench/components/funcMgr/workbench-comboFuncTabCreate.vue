<!-- 组合功能标签页新建 -->
<template>
<el-drawer v-model="showFlag" direction="rtl" size="68%">
    <template #header>
        <span class="title">组合功能标签页新建</span>
    </template>
    <template #default>
        <div>
        <el-scrollbar>
<el-form v-if="newComboFuncTab != null" :model="newComboFuncTab" label-width="120px" :rules="validateRules" ref="newComboFuncTabForm">
    
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="标签页名称" required prop="name"> 
                <el-input v-model="newComboFuncTab.name" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="12">
            
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="9">
            <el-form-item label="包含功能" required prop="tabFuncId">
                <el-input type=hidden v-model="newComboFuncTab.tabFuncId" size="small"/>
                <el-input v-model="newComboFuncTab.funcDefine.name" readonly size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="3">
            <el-form-item label="" label-width="10px">
                <el-link  type="primary" @click="selectFuncDefine()" ><label>选择</label></el-link>&nbsp;
                <el-link  type="primary" @click="clearFuncDefine()" ><label>清空</label></el-link>                                                
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="显示顺序" required prop="displayOrder">
                <el-input v-model="newComboFuncTab.displayOrder" size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="18">
            <el-form-item label="标签页参数">
                <el-input type="textarea" v-model="newComboFuncTab.tabParams" :rows="3" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="6">
            &nbsp;
        </el-col>
    </el-row>
</el-form>
            </el-scrollbar>
        </div>
    </template>
    <template #footer>
        <div style="flex: auto">
            <el-button @click="hideComboFuncTabCreatePage()">取消</el-button>
            <el-button type="primary" @click="addComboFuncTab()">保存</el-button>
        </div>
    </template>
</el-drawer>

<!-- 选择页面功能信息 -->
<funcDefineSelect v-if="showFuncDefineSelect == true"  @funcDefineSelected="funcDefineSelected" @hideFuncDefineSelectPage="hideFuncDefineSelectPage" :showFuncDefineSelect="showFuncDefineSelect"  :funcDefinesForSelect="funcDefinesForSelect"/>
</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

import funcDefineSelect from './workbench-funcDefineSelect.vue'

export default {
    props: ['newComboFuncTab','showComboFuncTabCreate'],
    
    emits: ['addComboFuncTabToList','hideComboFuncTabCreatePage'],
    
    setup(props, {attrs, emit, slots}) {
        const addComboFuncTabToList = (newComboFuncTab) => {
            emit('addComboFuncTabToList', newComboFuncTab);
        };
        
        const hideComboFuncTabCreatePage = () => {
            emit('hideComboFuncTabCreatePage');
        };
        
        return {
        	addComboFuncTabToList,
        	hideComboFuncTabCreatePage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showComboFuncTabCreate;
            },
            set(value) {
                this.hideComboFuncTabCreatePage();
            }
        }
    },
    components: {
    	funcDefineSelect
    },
    
    data() {
    	const showFuncDefineSelect = ref(false);
        const funcDefinesForSelect = ref(null);

        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "tabFuncId": [
                { required: true, message: '包含功能不能为空！', trigger: 'blur' }
            ],
            "displayOrder": [
                { required: true, message: '顺序不能为空！', trigger: 'blur' },
                { pattern: /^[0-9]*$/, message: '顺序必须是整数类型！', trigger: 'blur' }
            ]
        });
        
    	return {
    		showFuncDefineSelect,
    		funcDefinesForSelect,
            validateRules
        }
    },
    
    methods: {
    	addComboFuncTab() {
            var url = "/lcdp-proxy/lowcode/platform/fe/api/comboFuncTab/add";
            this.$refs.newComboFuncTabForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.newComboFuncTab).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.newComboFuncTab.updateTime = data.data.updateTime;
                            this.addComboFuncTabToList(data.data);
                            ElMessage(`新增组合功能标签页信息成功!`);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        selectFuncDefine() {
        	var url = "/lcdp-proxy/lowcode/platform/fe/api/funcDefine/list/" + this.newComboFuncTab.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.funcDefinesForSelect = data.data;
                    this.showFuncDefineSelect = true;
                }
            });
        },
        clearFuncDefine(){
            this.newComboFuncTab.tabFuncId = "";
            this.newComboFuncTab.funcDefine = {};
        },
        funcDefineSelected(obj) {
            this.newComboFuncTab.tabFuncId = obj.id;
            this.newComboFuncTab.funcDefine = obj;
            this.funcDefinesForSelect = null;
            this.showFuncDefineSelect = false;
        },
        hideFuncDefineSelectPage(){
             this.funcDefinesForSelect = null;
             this.showFuncDefineSelect = false;
        }
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