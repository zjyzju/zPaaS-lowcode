<!-- 编辑组合功能 -->
<template>
<el-form  v-if="comboFuncDefine != null && comboFuncDefine.id != null" :model="comboFuncDefine" label-width="120px" :rules="validateRules" ref="comboFuncDefineForm">
    <el-row :gutter="4">
       <el-col :span="24">
           <el-breadcrumb separator=">">
               <el-breadcrumb-item><span class="title">系统功能</span></el-breadcrumb-item>
               <el-breadcrumb-item>组合功能</el-breadcrumb-item>
               <el-breadcrumb-item ><span class="title1">{{comboFuncDefine.name}}</span></el-breadcrumb-item>
            </el-breadcrumb>
       </el-col>
    </el-row>
    <el-row :gutter="4"><el-col :span="24">&nbsp;</el-col></el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="标识" required prop="id">
                <el-input v-model="comboFuncDefine.id" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="名称" required prop="name">
                <el-input v-model="comboFuncDefine.name" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="8">
            <el-form-item label="组合功能模板" required prop="comboTemplateId">
                <el-select v-model="comboFuncDefine.comboTemplateId" class="m-2" placeholder="Select" size="small">
                    <el-option v-for="item in comboFuncTemplates" :key="item.id" :label="item.name" :value="item.id"/>
                </el-select>
            </el-form-item>
        </el-col>
        
    </el-row>
    <el-row :gutter="4">
        <el-col :span="8">
            <el-form-item label="归属业务域"  required prop="domainId">
                <el-input type="hidden" v-model="comboFuncDefine.domainId" size="small" />
                <el-input v-model="businessDomainName" readonly size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="16">
            <el-form-item label="描述">
                <el-input type="textarea" :rows="3" v-model="comboFuncDefine.description" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    
    <el-row :gutter="4">
        <el-col :span="20" class="toolbar-right">
            <el-link :href="comboFuncDefine.comboFuncTemplate.url+'?comboFuncId='+comboFuncDefine.id"  type="primary" target="_blank">预览</el-link>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="saveComboFuncDefine()">保存</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            <el-button type="primary" size="small" @click="deleteComboFuncDefine()">删除</el-button>
        </el-col>
        <el-col :span="4" class="toolbar-right">&nbsp;</el-col>
    </el-row>
    
    <!-- 组合功能标签页 -->
    <br/><br/><br/>
    <el-row :gutter="4" v-if="comboFuncDefine != null && comboFuncDefine.id != null">
        <el-col :span="1"></el-col>
        <el-col :span="23">
            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
            <div class="toolbar1">
                <span class="title">组合功能标签页</span>&nbsp;&nbsp;&nbsp;&nbsp;
                <el-link @click="showComboFuncTabCreatePage()"  type="primary" >新建</el-link>
            </div>
            </el-divider>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="comboFuncDefine != null && comboFuncDefine.id != null">
        <el-col :span="1"/>
        <el-col :span="23">
            <el-table :data="comboFuncDefine.comboFuncTabs" stripe style="width: 100%">
                <el-table-column  label="操作" width="100">
                    <template #default="scope">
                        <el-link @click="showComboFuncTabEditPage(scope.row.id)"  type="primary">编辑</el-link>&nbsp;
                        <el-link @click="deleteComboFuncTab(scope.row.id)"  type="primary">删除</el-link>&nbsp;
                    </template>
                </el-table-column>
                    <el-table-column prop="id" label="标签页标识" width="245" />
                    <el-table-column prop="name" label="标签页名称" width="200" />
                    <el-table-column prop="funcDefine.name" label="包含功能" width="380" >
                        <template #default="scope">
                            {{scope.row.funcDefine.id}}（{{scope.row.funcDefine.name}}）
                        </template>
                    </el-table-column>
                    <el-table-column prop="displayOrder" label="显示顺序"/>
            </el-table>
        </el-col>
    </el-row>
</el-form>

<!-- 编辑组合功能标签页信息 -->
<editComboFuncTab v-if="showComboFuncTabEdit==true && comboFuncTab != null" :showComboFuncTabEdit="showComboFuncTabEdit" :comboFuncTab="comboFuncTab" @updateComboFuncTabToList="updateComboFuncTabToList" @hideComboFuncTabEditPage="hideComboFuncTabEditPage"/>
<!-- 新建组合功能标签页信息 -->
<createComboFuncTab v-if="showComboFuncTabCreate==true && newComboFuncTab != null" :showComboFuncTabCreate="showComboFuncTabCreate" :newComboFuncTab="newComboFuncTab" @addComboFuncTabToList="addComboFuncTabToList" @hideComboFuncTabCreatePage="hideComboFuncTabCreatePage"/>
</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import { useRoute } from 'vue-router'

import editComboFuncTab from './workbench-comboFuncTabEdit.vue'
import createComboFuncTab from './workbench-comboFuncTabCreate.vue'

export default {
    props: [],
    
    emits: ['updateToList', 'deleteFromList'],
    
    setup (props, {attrs, emit, slots}) {
        const updateComboFuncDefineToList = (comboFuncDefine)=> {
            emit('updateToList', 'comboFuncDefine', comboFuncDefine);
        };
        
        const deleteComboFuncDefineFromList = (comboFuncDefine)=> {
            emit('deleteFromList', 'comboFuncDefine', comboFuncDefine);
        };
        
        return {
        	updateComboFuncDefineToList,
        	deleteComboFuncDefineFromList
        };
    },
    components: {
    	editComboFuncTab,
    	createComboFuncTab
    },
    data() {       
        const comboFuncDefine = ref(null);
        const router = useRoute();

    	const comboFuncTab = ref({});
    	const showComboFuncTabEdit = ref(false);
    	
    	const newComboFuncTab = ref({});
        const showComboFuncTabCreate = ref(false);
    	
    	const comboFuncTemplates = ref([]);

        const businessDomainName = ref(null);

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "comboTemplateId": [
                { required: true, message: '组合功能模板不能为空！', trigger: 'blur' }
            ]
        });
    	
    	return {
            comboFuncDefine,
            router,
        	comboFuncTab,
        	showComboFuncTabEdit,
        	
        	newComboFuncTab,
        	showComboFuncTabCreate,
        	
        	comboFuncTemplates,
            validateRules,
            businessDomainName
        }
    },
    watch: {        
        'router.query': function(val, old){
            if(val != null && val.objectId != null && (old == null || val.objectId != old.objectId)) {
                setTimeout(()=>{
                    this.loadComboFuncDefine();
                },100);
            }else if(val == null || val.objectId == null) {
                this.comboFuncDefine = null;
            }
        }
    },
    mounted() {
    	this.loadComboFuncTemplates();
        this.loadComboFuncDefine();
    },
    methods: {
        loadComboFuncDefine() {
            var objectId = this.router.query.objectId;
            if(objectId == null) {
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/fe/api/comboFuncDefine/loadAll/" + objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.comboFuncDefine = data.data;
                    var url = "/lcdp-proxy/lowcode/platform/be/api/businessDomain/byId/" + data.data.domainId;
                    axiosClient.get(url).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.businessDomainName = data.data.name;
                        }
                    });
                }
            });
        },
    	showComboFuncTabEditPage(id) {
    		var url = "/lcdp-proxy/lowcode/platform/fe/api/comboFuncTab/loadAll/" + id ;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.comboFuncTab = data.data;
                    this.showComboFuncTabEdit = true;
                }else {
                    ElMessage.error(`加载组合功能标签页信息失败`);
                }
            });
    	},
    	updateComboFuncTabToList(data) {
    		if(this.comboFuncDefine.comboFuncTabs != null && this.comboFuncDefine.comboFuncTabs.length > 0) {
                for(var index in this.comboFuncDefine.comboFuncTabs) {
                    if(this.comboFuncDefine.comboFuncTabs[index].id == data.id) {
                        this.comboFuncDefine.comboFuncTabs[index] = data;
                        break;
                    }
                }
            }
    		this.comboFuncDefine.comboFuncTabs.sort((a,b)=>{
                if(a.displayOrder > b.displayOrder) {
                    return 1;
                }else if(a.displayOrder < b.displayOrder) {
                    return -1;
                }else {
                    return 0;
                }
            });
    		this.comboFuncTab = null;
    		this.showComboFuncTabEdit = false;
    	},
    	hideComboFuncTabEditPage() {
    		this.comboFuncTab = null;
            this.showComboFuncTabEdit = false;
    	},
    	showComboFuncTabCreatePage() {
    		this.newComboFuncTab = {
    			"comboFuncId": this.comboFuncDefine.id,
    			"systemId": this.comboFuncDefine.systemId,
    			"tenantId": this.comboFuncDefine.tenantId,
    			"tabFuncId": "",
    			"funcDefine": {}
    		};
            this.showComboFuncTabCreate = true;
        },
        addComboFuncTabToList(data) {
            if(this.comboFuncDefine.comboFuncTabs == null) {
            	this.comboFuncDefine.comboFuncTabs = [];
            }
            this.comboFuncDefine.comboFuncTabs.push(data);
            this.comboFuncDefine.comboFuncTabs.sort((a,b)=>{
            	if(a.displayOrder > b.displayOrder) {
            		return 1;
            	}else if(a.displayOrder < b.displayOrder) {
            		return -1;
            	}else {
            		return 0;
            	}
            });
            this.newComboFuncTab = null;
            this.showComboFuncTabCreate = false;
        },
        hideComboFuncTabCreatePage() {
            this.newComboFuncTab = null;
            this.showComboFuncTabCreate = false;
        },
        deleteComboFuncTab(id) {
        	ElMessageBox.confirm(
                    '该组合功能标签页将被删除. 是否继续?',
                    '警告',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning',
                    }
                 ).then(() => {
                	 var url = "/lcdp-proxy/lowcode/platform/fe/api/comboFuncTab/delete/" + this.comboFuncDefine.systemId + "/" + id;
                     axiosClient.post(url).then((response) => {
                         var data = response.data;
                         if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                             for(var i in this.comboFuncDefine.comboFuncTabs) {
                                 if(this.comboFuncDefine.comboFuncTabs[i].id == id) {
                                     this.comboFuncDefine.comboFuncTabs.splice(i,1);
                                     break;
                                 }
                             }
                             ElMessage(`删除组合功能标签页信息成功!`);
                         }
                     }).catch(()=>{
                         ElMessage.error(`删除组合功能标签页信息失败!`);
                     });
                 }).catch(()=>{});
        },
    	saveComboFuncDefine() {
            var url = "/lcdp-proxy/lowcode/platform/fe/api/comboFuncDefine/save";
            this.$refs.comboFuncDefineForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.comboFuncDefine).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.updateComboFuncDefineToList(this.comboFuncDefine);
                            ElMessage(`更新组合页面对象资源信息成功`);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        deleteComboFuncDefine() {
            ElMessageBox.confirm(
                '该组合页面对象资源(' + this.comboFuncDefine.name + ')将被删除. 是否继续?',
                '警告',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning',
                }
             ).then(() => {
                 var url = "/lcdp-proxy/lowcode/platform/fe/api/comboFuncDefine/delete/" + this.comboFuncDefine.id;
                 axiosClient.post(url).then((response) => {
                     var data = response.data;
                     if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                         this.deleteComboFuncDefineFromList(this.comboFuncDefine);
                         
                         ElMessage(`删除组合页面对象资源(`+ this.comboFuncDefine.name +`)成功。`);
                     }
                 }).catch(()=>{
                     ElMessage.error(`删除组合页面对象资源(`+ this.comboFuncDefine.name +`)失败！`);
                 });
             }).catch(()=>{});
        },
        loadComboFuncTemplates() {
            var url = "/lcdp-proxy/lowcode/platform/fe/api/comboFuncTemplate/list" ;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.comboFuncTemplates = data.data;
                }
            });
        }
    }
}
</script>

<style scoped>
.layout-container-main .toolbar-right {
  display: inline-flex;
  align-items: center;
  justify-content: right; 
  height: 100%;
  right: 20px;
}

.toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
  height: 100%;
  right: 20px;
}
</style>