<!-- 组合功能标签页编辑 -->
<template>
<el-drawer v-model="showFlag" direction="rtl" size="68%">
    <template #header>
        <span class="title">组合功能标签页编辑</span>
    </template>
    <template #default>
        <div>
        <el-scrollbar>
<el-form v-if="comboFuncTab != null" :model="comboFuncTab" label-width="120px" :rules="validateRules" ref="comboFuncTabForm">
    
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="标签页标识" required prop="id">
                <el-input v-model="comboFuncTab.id" readonly size="small"/>
                <el-input type=hidden v-model="comboFuncTab.comboFuncId" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="标签页名称" required prop="name">
                <el-input v-model="comboFuncTab.name" size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="9">
            <el-form-item label="包含功能" required prop="tabFuncId">
                <el-input type=hidden v-model="comboFuncTab.tabFuncId" size="small"/>
                <el-input v-model="comboFuncTab.funcDefine.name" readonly size="small" />
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
                <el-input v-model="comboFuncTab.displayOrder" size="small"/>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="18">
            <el-form-item label="标签页参数">
                <el-input type="textarea" v-model="comboFuncTab.tabParams" :rows="3" size="small"/>
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
            <el-button @click="hideComboFuncTabEditPage()">取消</el-button>
            <el-button type="primary" @click="saveComboFuncTab()">保存</el-button>
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
    props: ['comboFuncTab','showComboFuncTabEdit'],
    
    emits: ['updateComboFuncTabToList','hideComboFuncTabEditPage'],
    
    setup(props, {attrs, emit, slots}) {
        const updateComboFuncTabToList = (comboFuncTab) => {
            emit('updateComboFuncTabToList', comboFuncTab);
        };
        
        const hideComboFuncTabEditPage = () => {
            emit('hideComboFuncTabEditPage');
        };
        
        return {
        	updateComboFuncTabToList,
        	hideComboFuncTabEditPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showComboFuncTabEdit;
            },
            set(value) {
                this.hideComboFuncTabEditPage();
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
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
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
    	saveComboFuncTab() {
            var url = "/lcdp-proxy/lowcode/platform/fe/api/comboFuncTab/save";
            this.$refs.comboFuncTabForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.comboFuncTab).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.comboFuncTab.updateTime = data.data.updateTime;
                            this.updateComboFuncTabToList(data.data);
                            ElMessage(`更新组合功能标签页信息成功!`);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        selectFuncDefine() {
        	var url = "/lcdp-proxy/lowcode/platform/fe/api/funcDefine/list/" + this.comboFuncTab.systemId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.funcDefinesForSelect = data.data;
                    this.showFuncDefineSelect = true;
                }
            });
        },
        clearFuncDefine(){
            this.comboFuncTab.tabFuncId = "";
            this.comboFuncTab.funcDefine = {};
        },
        funcDefineSelected(obj) {
            this.comboFuncTab.tabFuncId = obj.id;
            this.comboFuncTab.funcDefine = obj;
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