<!-- 绑定对象编辑 -->
<template>
<el-dialog v-model="showFlag" width="70vw" top="8vh">
    <template #header>
        <span class="title">绑定对象编辑</span>
    </template>
    <template #default>
        <div>
        <el-scrollbar height="65vh">
<el-form v-if="objectAssign != null" :model="objectAssign" label-width="120px" :rules="validateRules" ref="objectAssignForm">
    
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="对象绑定标识" required prop="id">
                <el-input v-model="objectAssign.id" readonly size="small"/>
                <el-input type=hidden v-model="objectAssign.funcId" size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="绑定对象类型" required prop="objectType">
                <el-input type=hidden v-model="objectAssign.objectType" size="small"/>
                <div style="display: flex; align-items: center">
                    <span v-if="objectAssign.objectType == 'D'">领域对象</span>
                    <span v-else-if="objectAssign.objectType == 'V'">值传递对象</span>
                    <span v-else-if="objectAssign.objectType == 'B'">BI数据集</span>
                    <span v-else>{{objectAssign.objectType}}</span>
                </div>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="绑定对象" required prop="objectId">
                <el-input type=hidden v-model="objectAssign.objectId" size="small"/>
                <div style="display: flex; align-items: center">
                    <span v-if="objectAssign.assignObject != null && objectAssign.objectType != 'B'">{{objectAssign.assignObject.code}}</span>
                    <span v-else-if="objectAssign.biDataSet != null && objectAssign.objectType == 'B'">{{objectAssign.biDataSet.code}}</span>
                    <span v-else>{{objectAssign.objectId}}</span>
                </div>
            </el-form-item>
        </el-col>
    
        <el-col :span="12">
            <el-form-item label="绑定类型" required prop="assignType">
                <el-select v-model="objectAssign.assignType" :disabled="objectAssign.objectType == 'B'"  class="m-2" placeholder="Select" size="small">
                    <el-option
                            v-for="item in assignTypeOptions"
                            :key="item.value"
                            :label="item.label"
                            :value="item.value"
                     />
                </el-select>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="objectAssign.objectType != 'B'">
        <el-col :span="12">
            <el-form-item label="关键属性" required prop="keyAttrId">
                <el-select v-model="objectAssign.keyAttrId" clearable class="m-2" placeholder="Select" size="small">
                    <el-option
                            v-for="item in objectAssign.attributes"
                            :key="item.id"
                            :label="item.code"
                            :value="item.id"
                     />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="关联属性">
                <el-select v-model="objectAssign.relAttrId" clearable class="m-2" placeholder="Select" size="small">
                    <el-option
                            v-for="item in objectAssign.attributes"
                            :key="item.id"
                            :label="item.code"
                            :value="item.id"
                     />
                </el-select>
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12" v-if="objectAssign.objectType != 'B'">
            <el-form-item label="主对象关联属性">
                <el-select v-model="objectAssign.mainRelAttrId" clearable class="m-2" placeholder="Select" size="small">
                    <el-option v-if="mainObjectAssign != null"
                            v-for="item in mainObjectAssign.attributes"
                            :key="item.id"
                            :label="item.code"
                            :value="item.id"
                     />
                </el-select>
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="显示顺序" required prop="displayOrder">
                <el-input v-model="objectAssign.displayOrder" size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="创建时间">
                <el-input v-model="objectAssign.createTime" readonly size="small" />
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="更新时间">
                <el-input v-model="objectAssign.updateTime" readonly size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    
    <!-- 绑定对象属性信息 -->
    <br/>
    <el-row :gutter="4" v-if="objectAssign != null">
        <el-col :span="1"></el-col>
        <el-col :span="23">
            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 6px">
            <div class="toolbar1">
                <span class="title">对象属性</span>&nbsp;&nbsp;&nbsp;&nbsp;
                <el-link @click="showFuncObjectAttrOptionsCopyFromOtherPage()"  type="primary" >复制(从其他功能)</el-link>
            </div>
            </el-divider>
        </el-col>
    </el-row>
    <el-row :gutter="4" v-if="objectAssign != null">
        <el-col :span="1"/>
        <el-col :span="23">
            <el-table :data="objectAssign.attributes" stripe style="width: 100%">
                <el-table-column  label="选项设置" width="100">
                    <template #default="scope">
                        <el-link @click="showAttrOptionsEditPage(objectAssign.id,scope.row.id,scope.row.code)"  type="primary">设置</el-link>&nbsp;
                        <el-link  type="primary"  @click="deleteAttrOptions(objectAssign.id,scope.row.id)">删除</el-link>
                    </template>
                </el-table-column>
                    <el-table-column prop="code" label="属性" width="320" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span>{{scope.row.code}}&nbsp;({{scope.row.name}})</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="isListAttr" label="是否列表" width="80" >
                         <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.isListAttr == 'Y'">是</span>
                              <span v-if="scope.row.isListAttr == 'N'">否</span>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column prop="attrType" label="属性类型" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                                <span v-if="scope.row.attrType == 'J'">Java原生类型({{scope.row.attrClass}})</span>
                                <span v-if="scope.row.attrType == 'D'">领域对象({{scope.row.attrClassObject.code}})</span>
                                <span v-if="scope.row.attrType == 'V'">值传递对象({{scope.row.attrClassObject.code}})</span>
                            </div>
                        </template>
                    </el-table-column>
                    
                </el-table>
        </el-col>
    </el-row>
</el-form>
            </el-scrollbar>
        </div>
    </template>
    <template #footer>
        <div style="flex: auto">
            <el-button @click="hideObjectAssignEditPage()">取消</el-button>
            <el-button type="primary" @click="saveFuncObjectAssign()">保存</el-button>
        </div>
    </template>
</el-dialog>

<!-- 编辑对象属性选项信息 -->
<editObjectAttrOptions v-if="showAttrOptionsEdit==true && objectAssign != null" :showAttrOptionsEdit="showAttrOptionsEdit" :objectAttrOptions="objectAttrOptions" :objectAssigns="objectAssigns" @updateObjectAttrOptionsToList="updateObjectAttrOptionsToList" @hideObjectAttrOptionsEditPage="hideObjectAttrOptionsEditPage"/>
<!-- 复制功能区域绑定属性信息(从其他功能) -->
<funcObjectAttrOptionsCopyFromOther v-if="showFuncObjectAttrOptionsCopyFromOther==true" :showFuncObjectAttrOptionsCopyFromOther="showFuncObjectAttrOptionsCopyFromOther" :systemId="objectAssign.systemId" :targetFuncId="objectAssign.funcId" :targetObjectAssignId="objectAssign.id" @refreshFuncObjectAttrOptionsToList="refreshFuncObjectAttrOptionsToList" @hideFuncObjectAttrOptionsCopyFromOtherPage="hideFuncObjectAttrOptionsCopyFromOtherPage"/>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

import editObjectAttrOptions from './workbench-funcObjectAttrOptionsEdit.vue'
import funcObjectAttrOptionsCopyFromOther from './workbench-funcObjectAttrOptionsCopyFromOther.vue'

export default {
    props: ['objectAssign','showObjectAssignEdit', 'mainObjectAssign','objectAssigns'],
    
    emits: ['updateObjectAssignToList','hideObjectAssignEditPage'],
    
    setup(props, {attrs, emit, slots}) {
        const updateObjectAssignToList = (objectAssign) => {
            emit('updateObjectAssignToList', objectAssign);
        };
        
        const hideObjectAssignEditPage = () => {
            emit('hideObjectAssignEditPage');
        };
        
        return {
        	updateObjectAssignToList,
        	hideObjectAssignEditPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showObjectAssignEdit;
            },
            set(value) {
                this.hideObjectAssignEditPage();
            }
        }
    },
    components: {
    	editObjectAttrOptions,
        funcObjectAttrOptionsCopyFromOther
    },
    
    data() {
    	const assignTypeOptions = ref(null);
    	
    	const showAttrOptionsEdit = ref(false);
    	const objectAttrOptions = ref(null);

        const showFuncObjectAttrOptionsCopyFromOther = ref(false);

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "objectType": [
                { required: true, message: '对象类型不能为空！', trigger: 'blur' }
            ],
            "objectId": [
                { required: true, message: '绑定对象不能为空！', trigger: 'blur' }
            ],
            "assignType": [
                { required: true, message: '绑定类型不能为空！', trigger: 'blur' }
            ],
            "keyAttrId": [
                { required: true, message: '关键属性不能为空！', trigger: 'blur' }
            ],
            "displayOrder": [
                { required: true, message: '顺序不能为空！', trigger: 'blur' },
                { pattern: /^[0-9]*$/, message: '顺序必须是整数类型！', trigger: 'blur' }
            ]
        });
    	
        return {
        	assignTypeOptions,
        	showAttrOptionsEdit,
        	objectAttrOptions,
            showFuncObjectAttrOptionsCopyFromOther,
            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['ASSIGN_OBJECT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.assignTypeOptions = data.data['ASSIGN_OBJECT_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	saveFuncObjectAssign() {
            var url = "/lcdp-proxy/lowcode/platform/fe/api/funcObjectAssign/save";
            this.$refs.objectAssignForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.objectAssign).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            this.updateObjectAssignToList(data.data);
                            ElMessage(`更新对象绑定信息成功!`);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        showAttrOptionsEditPage(objectAssignId, attributeId, attributeCode) {
        	var url = "/lcdp-proxy/lowcode/platform/fe/api/funcObjectAttrOptions/query/" +objectAssignId + "/" + attributeId ;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                    this.objectAttrOptions = data.data;
                    this.objectAttrOptions.attributeCode = attributeCode;
                    this.showAttrOptionsEdit = true;
                }else {
                	this.objectAttrOptions = {
                		id : null,
                		objectAssignId : objectAssignId,
                		attributeId : attributeId,
                        attributeCode : attributeCode,
                		systemId : this.objectAssign.systemId,
                		tenantId : this.objectAssign.tenantId
                	}
                	this.showAttrOptionsEdit = true;
                }
            });
        },
        deleteAttrOptions(objectAssignId, attributeId) {
        	var url = "/lcdp-proxy/lowcode/platform/fe/api/funcObjectAttrOptions/delete/" +objectAssignId + "/" + attributeId ;
        	axiosClient.post(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                	if(this.objectAssign.attrOptionMap != null) {
                		delete this.objectAssign.attrOptionMap[attributeId];
                	}
                	ElMessage(`删除对象属性可选值信息成功!`);
                }
            });
        },
        hideObjectAttrOptionsEditPage() {
        	this.objectAttrOptions = null;
            this.showAttrOptionsEdit = true;
        },
        updateObjectAttrOptionsToList(data) {
        	if(this.objectAssign.attrOptionMap != null) {
                this.objectAssign.attrOptionMap[data.attributeId] = data;
            }
        	this.objectAttrOptions = null;
            this.showAttrOptionsEdit = true;
        },
        showFuncObjectAttrOptionsCopyFromOtherPage() {
            this.showFuncObjectAttrOptionsCopyFromOther = true;
        },
        hideFuncObjectAttrOptionsCopyFromOtherPage() {
            this.showFuncObjectAttrOptionsCopyFromOther = false;
        },
        refreshFuncObjectAttrOptionsToList(data) {
            var attrOptionMap = {};
            if(data != null) {
                for(var index = 0; index<data.length; index++) {
                    attrOptionMap[data[index].attributeId] = data[index];
                }
            }
            this.objectAssign.attrOptionMap = attrOptionMap;
            this.showFuncObjectAttrOptionsCopyFromOther = false;
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