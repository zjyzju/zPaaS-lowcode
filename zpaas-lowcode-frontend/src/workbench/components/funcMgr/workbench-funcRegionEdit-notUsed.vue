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
    
    <!-- 绑定属性信息 -->
    <br/>
    <el-row :gutter="4" v-if="funcRegion != null">
        <el-col :span="1"></el-col>
        <el-col :span="23">
            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 6px">
            <div class="toolbar1">
                <span class="title">绑定属性</span>&nbsp;&nbsp;&nbsp;&nbsp;
                <el-link @click="showFuncRegionAttrAssignCreatePage()"  type="primary" >设置</el-link>&nbsp;&nbsp;
                <el-link @click="showFuncRegionAttrAssignCopyPage()"  type="primary" >复制(从本功能)</el-link>&nbsp;&nbsp;
                <el-link @click="showFuncRegionAttrAssignCopyPageFromOther()"  type="primary" >复制(从其他功能)</el-link>
            </div>
            </el-divider>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="funcRegion != null">
        <el-col :span="1"/>
        <el-col :span="23">
            <el-table :data="funcRegion.regionAttrAssigns" stripe style="width: 100%">
                <el-table-column  label="可选值" width="90">
                    <template #default="scope">
                        <el-link @click="showFuncRegionAttrAssignEditPage(scope.row.id)"  type="primary">编辑</el-link>&nbsp;
                        <el-link  type="primary"  @click="deleteFuncRegionAttrAssign(scope.row.id)">删除</el-link>
                    </template>
                </el-table-column>
                    <el-table-column prop="objectId" label="归属对象" width="160" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span>{{this.objectMap[scope.row.objectId].assignObject.code}}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="attributeId" label="属性" width="160" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.attribute != null">{{scope.row.attribute.code}}</span>
                            </div>
                        </template>
                    </el-table-column>
                     <el-table-column prop="displayName" label="显示名称" width="120" />
                    <el-table-column prop="displayType" label="显示类型" width="80" >
                         <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span v-if="scope.row.displayType == 'I'">输入框</span>
                                <span v-if="scope.row.displayType == 'S'">选择框</span>
                                <span v-if="scope.row.displayType == 'R'">单选框</span>
                                <span v-if="scope.row.displayType == 'C'">多选框</span>
                                <span v-if="scope.row.displayType == 'A'">文本域</span>
                                <span v-if="scope.row.displayType == 'H'">隐藏框</span>
                                <span v-if="scope.row.displayType == 'D'">日期选择框</span>
                                <span v-if="scope.row.displayType == 'T'">日期时间选择框</span>
                                <span v-if="scope.row.displayType == 'L'">文本标签</span>
                                <span v-if="scope.row.displayType == 'P'">弹出选择框</span>
                                <span v-if="scope.row.displayType == 'V'">弹出查看框</span>
                                <span v-if="scope.row.displayType == 'W'">密码框</span>
                                <span v-if="scope.row.displayType == 'F'">文件上传</span>
                                <span v-if="scope.row.displayType == 'G'">文件下载</span>
                                <span v-if="scope.row.displayType == 'E'">级联选择</span>
                                <span v-if="scope.row.displayType == 'K'">树形下拉选择</span>
                            </div>
                        </template>
                    </el-table-column>
                   <el-table-column prop="displayOrder" label="显示顺序" width="80" />
                    <el-table-column prop="readonly" label="只读" width="60" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.readonly == 'Y'">是</span>
                              <span v-if="scope.row.readonly == 'N'">否</span>
                              <span v-if="scope.row.readonly == null || scope.row.readonly == ''">否</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="colSpan" label="占用列数" width="80" />
                    <el-table-column prop="displayWidth" label="显示宽度" width="80" />
                    <el-table-column prop="rowSpan" label="占用行数"/>
                    
                </el-table>
        </el-col>
    </el-row>
    
    
    <!-- 绑定操作信息 -->
    <br/>
    <el-row :gutter="4" v-if="funcRegion != null">
        <el-col :span="1"></el-col>
        <el-col :span="23">
            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 6px">
            <div class="toolbar1">
                <span class="title">绑定操作</span>&nbsp;&nbsp;&nbsp;&nbsp;
                <el-link @click="showFuncRegionOperationCreatePage()"  type="primary" >新建</el-link>
            </div>
            </el-divider>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="funcRegion != null">
        <el-col :span="1"/>
        <el-col :span="23">
            <el-table :data="funcRegion.regionOperations" stripe style="width: 100%">
                <el-table-column  label="操作" width="100">
                    <template #default="scope">
                        <el-link @click="showFuncRegionOperationEditPage(scope.row.id)"  type="primary">编辑</el-link>&nbsp;
                        <el-link  type="primary" @click="deleteFuncRegionOperation(scope.row.id)">删除</el-link>
                    </template>
                </el-table-column>
                    <el-table-column prop="name" label="操作名称"  width="80"/>
                    <el-table-column prop="operationType" label="类型" width="80" >
                         <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.operationType == 'C'">新增</span>
                              <span v-if="scope.row.operationType == 'D'">删除</span>
                              <span v-if="scope.row.operationType == 'U'">修改</span>
                              <span v-if="scope.row.operationType == 'Q'">查询列表</span>
                              <span v-if="scope.row.operationType == 'N'">取消</span>
                              <span v-if="scope.row.operationType == 'P'">预新增</span>
                              <span v-if="scope.row.operationType == 'B'">主键查询</span>
                              <span v-if="scope.row.operationType == 'E'">导出</span>
                              <span v-if="scope.row.operationType == 'S'">选择</span>
                              <span v-if="scope.row.operationType == 'V'">查看（弹出）</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="objectAssignId" label="归属绑定对象" width="150" >
                         <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <template v-for="item in objectAssigns">
                                    <span v-if="scope.row.objectAssignId == item.id">{{item.assignObject.code}}</span>
                                </template>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="exposedService.httpMapping" label="服务URL" width="220"  />
                    <el-table-column prop="serviceOperationId" label="服务方法" width="270" >
                         <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.serviceObject != null && scope.row.operation != null">{{scope.row.serviceObject.code}}.{{scope.row.operation.code}}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="targetRegionId" label="目标区域"  width="200" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="this.targetRegionMap[scope.row.targetRegionId] != null">{{this.targetRegionMap[scope.row.targetRegionId].name}}</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="displayType" label="显示类型" width="80" >
                         <template #default="scope">
                             <div style="display: flex; align-items: center">
                                <span v-if="scope.row.displayType == 'P'">弹出页面</span>
                                <span v-if="scope.row.displayType == 'S'">原页面</span>
                             </div>
                         </template>
                    </el-table-column>
                    <el-table-column prop="isUserDefined" label="自定义" width="70" >
                         <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.isUserDefined == 'Y'">是</span>
                              <span v-if="scope.row.isUserDefined == 'N'">否</span>
                              <span v-if="scope.row.isUserDefined == null || scope.row.isUserDefined == ''">否</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="displayOrder" label="显示顺序" />
                    
                </el-table>
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

<!-- 编辑功能区域绑定属性信息 -->
<editFuncRegionAttrAssign v-if="showFuncRegionAttrAssignEdit==true && funcRegionAttrAssign != null" :showFuncRegionAttrAssignEdit="showFuncRegionAttrAssignEdit" :funcRegionAttrAssign="funcRegionAttrAssign" :objectMap="objectMap" :objectAssigns="objectAssigns" @updateFuncRegionAttrAssignToList="updateFuncRegionAttrAssignToList" @hideFuncRegionAttrAssignEditPage="hideFuncRegionAttrAssignEditPage"/>

<!-- 新增功能区域绑定属性信息 -->
<createFuncRegionAttrAssign v-if="showFuncRegionAttrAssignCreate==true && newFuncRegionAttrAssign != null" :showFuncRegionAttrAssignCreate="showFuncRegionAttrAssignCreate" :newFuncRegionAttrAssign="newFuncRegionAttrAssign" :objectMap="objectMap" :objectAssigns="objectAssigns" @addFuncRegionAttrAssignToList="addFuncRegionAttrAssignToList" @hideFuncRegionAttrAssignCreatePage="hideFuncRegionAttrAssignCreatePage"/>

<!-- 复制功能区域绑定属性信息 -->
<copyFuncRegionAttrAssign v-if="showFuncRegionAttrAssignCopy==true && funcRegions != null" :showFuncRegionAttrAssignCopy="showFuncRegionAttrAssignCopy" :funcRegions="funcRegions" :targetRegionId="funcRegion.id" @refreshFuncRegionAttrAssignToList="refreshFuncRegionAttrAssignToList" @hideFuncRegionAttrAssignCopyPage="hideFuncRegionAttrAssignCopyPage"/>

<!-- 复制功能区域绑定属性信息(从其他功能) -->
<copyFuncRegionAttrAssignFromOther v-if="showFuncRegionAttrAssignCopyFromOther==true" :showFuncRegionAttrAssignCopyFromOther="showFuncRegionAttrAssignCopyFromOther" :systemId="funcRegion.systemId" :targetFuncId="funcRegion.funcId" :targetRegionId="funcRegion.id" @refreshFuncRegionAttrAssignToList="refreshFuncRegionAttrAssignToList" @hideFuncRegionAttrAssignCopyPageFromOther="hideFuncRegionAttrAssignCopyPageFromOther"/>

<!-- 编辑功能区域绑定操作信息 -->
<editFuncRegionOperation v-if="showFuncRegionOperationEdit==true && funcRegionOperation != null" :showFuncRegionOperationEdit="showFuncRegionOperationEdit" :funcRegionOperation="funcRegionOperation" :funcRegions="funcRegions" :objectAssigns="objectAssigns" @updateFuncRegionOperationToList="updateFuncRegionOperationToList" @hideFuncRegionOperationEditPage="hideFuncRegionOperationEditPage"/>

<!-- 新增功能区域绑定操作信息 -->
<createFuncRegionOperation v-if="showFuncRegionOperationCreate==true && newFuncRegionOperation != null" :showFuncRegionOperationCreate="showFuncRegionOperationCreate" :newFuncRegionOperation="newFuncRegionOperation" :funcRegions="funcRegions" :funcRegion="funcRegion" :objectAssigns="objectAssigns" @addFuncRegionOperationToList="addFuncRegionOperationToList" @hideFuncRegionOperationCreatePage="hideFuncRegionOperationCreatePage"/>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'

import editFuncRegionAttrAssign from './workbench-funcRegionAttrAssignEdit.vue'
import createFuncRegionAttrAssign from './workbench-funcRegionAttrAssignCreate.vue'
import editFuncRegionOperation from './workbench-funcRegionOperationEdit.vue'
import createFuncRegionOperation from './workbench-funcRegionOperationCreate.vue'
import copyFuncRegionAttrAssign from './workbench-funcRegionAttrAssignCopy.vue'
import copyFuncRegionAttrAssignFromOther from './workbench-funcRegionAttrAssignCopyFromOther.vue'

export default {
    props: ['funcRegion','showFuncRegionEdit', 'objectMap', 'targetRegionMap', 'funcRegions', 'objectAssigns'],
    
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
    	editFuncRegionAttrAssign,
    	createFuncRegionAttrAssign,
    	editFuncRegionOperation,
    	createFuncRegionOperation,
    	copyFuncRegionAttrAssign,
    	copyFuncRegionAttrAssignFromOther
    },
    
    data() {
    	const showFuncRegionAttrAssignEdit = ref(false);
    	const funcRegionAttrAssign = ref(null);
    	
    	const showFuncRegionAttrAssignCreate = ref(false);
    	const newFuncRegionAttrAssign = ref(null);
    	
    	const showFuncRegionOperationEdit = ref(false);
    	const funcRegionOperation = ref(null);
    	
    	const showFuncRegionOperationCreate = ref(false);
    	const newFuncRegionOperation = ref(null);
    	
    	const showFuncRegionAttrAssignCopy = ref(false);
    	
    	const showFuncRegionAttrAssignCopyFromOther = ref(false);

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
        	showFuncRegionAttrAssignEdit,
        	funcRegionAttrAssign,
        	showFuncRegionAttrAssignCreate,
        	newFuncRegionAttrAssign,
        	showFuncRegionOperationEdit,
        	funcRegionOperation,
        	showFuncRegionOperationCreate,
        	newFuncRegionOperation,
        	showFuncRegionAttrAssignCopy,
        	showFuncRegionAttrAssignCopyFromOther,
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
        showFuncRegionAttrAssignEditPage(attrAssignId) {
        	var url = "/lcdp-proxy/lowcode/platform/fe/api/funcRegionAttrAssign/byId/" + attrAssignId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                    this.funcRegionAttrAssign = data.data;
                    this.showFuncRegionAttrAssignEdit = true;
                }
            });
        },
        deleteFuncRegionAttrAssign(attrAssignId) {
        	ElMessageBox.confirm(
                    '该功能区域绑定属性将被删除. 是否继续?',
                    '警告',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning',
                    }
                 ).then(() => {
                	 var url = "/lcdp-proxy/lowcode/platform/fe/api/funcRegionAttrAssign/delete/" + attrAssignId;
                     axiosClient.post(url).then((response) => {
                         var data = response.data;
                         if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                             for(var i in this.funcRegion.regionAttrAssigns) {
                                 if(this.funcRegion.regionAttrAssigns[i].id == attrAssignId) {
                                     this.funcRegion.regionAttrAssigns.splice(i,1);
                                     break;
                                 }
                             }
                             ElMessage(`删除功能区域绑定属性信息成功!`);
                         }
                     }).catch(()=>{
                         ElMessage.error(`删除功能区域绑定属性信息失败!`);
                     });
                 }).catch(()=>{});
        	
        },
        hideFuncRegionAttrAssignEditPage() {
        	this.funcRegionAttrAssign = null;
            this.showFuncRegionAttrAssignEdit = false;
        },
        updateFuncRegionAttrAssignToList(data) {
        	if(this.funcRegion.regionAttrAssigns != null) {
        		for(var i in this.funcRegion.regionAttrAssigns) {
                	if(this.funcRegion.regionAttrAssigns[i].id == data.id) {
                		this.funcRegion.regionAttrAssigns[i] = data;
                		break;
                	}
                }
                console.log(this.objectMap);
                this.funcRegion.regionAttrAssigns.sort((a,b)=>{
        			return this.sortRegionAttrAssign(a, b);
                });
            }
        	this.funcRegionAttrAssign = null;
            this.showFuncRegionAttrAssignEdit = false;
        },
        showFuncRegionAttrAssignCreatePage() {
        	this.newFuncRegionAttrAssign = {
        		id : "",
        		funcId: this.funcRegion.funcId,
        		funcRegionId: this.funcRegion.id,
        		objectAssignId: "",
        		objectType: "",
        		objectId: "",
        		readonly: "N",
        		displayType: "I",
        		systemId: this.funcRegion.systemId,
        		tenantId: this.funcRegion.tenantId
        	}
        	this.showFuncRegionAttrAssignCreate = true;
        },
        hideFuncRegionAttrAssignCreatePage() {
            this.newFuncRegionAttrAssign = null;
            this.showFuncRegionAttrAssignCreate = false;
        },
        addFuncRegionAttrAssignToList(data) {
            if(this.funcRegion.regionAttrAssigns == null) {
            	this.funcRegion.regionAttrAssigns = [];
            }
            this.funcRegion.regionAttrAssigns.push(data);
            this.funcRegion.regionAttrAssigns.sort((a,b)=>{
            	return this.sortRegionAttrAssign(a, b);
            });
            this.newFuncRegionAttrAssign = null;
            this.showFuncRegionAttrAssignCreate = false;
        },
        sortRegionAttrAssign(a, b) {
            // if(a.assignType == "M" && b.assignType != "M") {
        	// 	return -1;
        	// }else if(a.assignType != "M" && b.assignType == "M") {
            //     return 1;
            // }
            
            if((a.objectAssignId == null && b.objectAssignId == null) || a.objectAssignId == b.objectAssignId) {
                if(a.displayOrder == null && b.displayOrder == null) {
                    return 0;
                }else if(a.displayOrder == null) {
                    return -1;
                }else if(b.displayOrder == null) {
                    return 1;
                }else {
                    return a.displayOrder-b.displayOrder;
                }
            }else {
                if(a.objectAssignId == null) {
                    return -1;
                }else if(b.objectAssignId == null) {
                    return 1
                }else {
                    if(this.objectMap[a.objectId] == null || this.objectMap[a.objectId].displayOrder == null) {
                        return -1;
                    }else if(this.objectMap[b.objectId] == null || this.objectMap[b.objectId].displayOrder == null) {
                        return 1;
                    }else {
                        return this.objectMap[a.objectId].displayOrder-this.objectMap[b.objectId].displayOrder;
                    }
                }
            }
        },
        showFuncRegionAttrAssignCopyPage() {
            this.showFuncRegionAttrAssignCopy = true;
        },
        hideFuncRegionAttrAssignCopyPage() {
            this.showFuncRegionAttrAssignCopy = false;
        },
        showFuncRegionAttrAssignCopyPageFromOther() {
            this.showFuncRegionAttrAssignCopyFromOther = true;
        },
        hideFuncRegionAttrAssignCopyPageFromOther() {
            this.showFuncRegionAttrAssignCopyFromOther = false;
        },
        refreshFuncRegionAttrAssignToList(data) {
            this.funcRegion.regionAttrAssigns = data;
            this.showFuncRegionAttrAssignCopy = false;
            this.showFuncRegionAttrAssignCopyFromOther = false;
        },
        showFuncRegionOperationEditPage(id) {
        	var url = "/lcdp-proxy/lowcode/platform/fe/api/funcRegionOperation/byId/" + id;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                    this.funcRegionOperation = data.data;
                    this.showFuncRegionOperationEdit = true;
                }
            });
        },
        deleteFuncRegionOperation(id) {
            ElMessageBox.confirm(
                    '该功能区域绑定操作将被删除. 是否继续?',
                    '警告',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning',
                    }
                 ).then(() => {
                     var url = "/lcdp-proxy/lowcode/platform/fe/api/funcRegionOperation/delete/" + id;
                     axiosClient.post(url).then((response) => {
                         var data = response.data;
                         if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                             for(var i in this.funcRegion.regionOperations) {
                                 if(this.funcRegion.regionOperations[i].id == id) {
                                     this.funcRegion.regionOperations.splice(i,1);
                                     break;
                                 }
                             }
                             ElMessage(`删除功能区域绑定操作信息成功!`);
                         }
                     }).catch(()=>{
                         ElMessage.error(`删除功能区域绑定操作信息失败!`);
                     });
                 }).catch(()=>{});
            
        },
        hideFuncRegionOperationEditPage() {
            this.funcRegionOperation = null;
            this.showFuncRegionOperationEdit = false;
        },
        updateFuncRegionOperationToList(data) {
            if(this.funcRegion.regionOperations != null) {
                for(var i in this.funcRegion.regionOperations) {
                    if(this.funcRegion.regionOperations[i].id == data.id) {
                        this.funcRegion.regionOperations[i] = data;
                        break;
                    }
                }
            }
            this.funcRegionOperation = null;
            this.showFuncRegionOperationEdit = false;
        },
        showFuncRegionOperationCreatePage(){
        	this.newFuncRegionOperation = {
                    id : "",
                    funcId: this.funcRegion.funcId,
                    funcRegionId: this.funcRegion.id,
                    isUserDefined: "N",
                    displayType: "S",
                    exposedService: {},
                    serviceObject: {},
                    operation: {},
                    systemId: this.funcRegion.systemId,
                    tenantId: this.funcRegion.tenantId
            }
            this.showFuncRegionOperationCreate = true;
        },
        hideFuncRegionOperationCreatePage() {
            this.newFuncRegionOperation = null;
            this.showFuncRegionOperationCreate = false;
        },
        addFuncRegionOperationToList(data) {
            if(this.funcRegion.regionOperations == null) {
                this.funcRegion.regionOperations = [];
            }
            this.funcRegion.regionOperations.push(data);
            this.newFuncRegionOperation = null;
            this.showFuncRegionOperationCreate = false;
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