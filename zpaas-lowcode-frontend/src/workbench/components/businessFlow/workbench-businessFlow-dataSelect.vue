<!-- 可用数据辅助选择 -->
<template>
<el-dialog v-model="showFlag" title="可用数据辅助选择" width="800px">
    <template #header>
        <span class="title">可用数据辅助选择</span>
    </template>
    <el-row :gutter="4">
        <el-col :span="12">
            <el-form-item label="方法编码" label-width="80px">
                <el-input v-model="operation.code" readonly size="small"/>
            </el-form-item>
        </el-col>
        <el-col :span="12">
            <el-form-item label="方法名称" label-width="80px">
                <el-input v-model="operation.name" readonly size="small" />
            </el-form-item>
        </el-col>
    </el-row>
    <el-row :gutter="4">
        <el-col :span="24">
            <el-tabs v-model="activeName">
                <el-tab-pane label="方法参数" name="I" v-if="isContextTypeContained('I')">
                    <el-scrollbar height="300px" >
                        <el-table :data="params" stripe style="width: 100%" @row-click="handleCurrentChange"  highlight-current-row>
                            <el-table-column prop="code" label="参数编码" width="160" />
                            <el-table-column prop="name" label="参数名称" width="160" />
                            <el-table-column prop="isInParam" label="入参" width="60" >
                                <template #default="scope">
                                    <div style="display: flex; align-items: center">
                                        <span v-if="scope.row.isInParam == 'Y'">是</span>
                                        <span v-if="scope.row.isInParam == 'N'">否</span>
                                    </div>
                                </template>
                            </el-table-column>
                            <el-table-column prop="isListParam" label="列表" width="60" >
                                <template #default="scope">
                                    <div style="display: flex; align-items: center">
                                        <span v-if="scope.row.isListParam == 'Y'">是</span>
                                        <span v-if="scope.row.isListParam == 'N'">否</span>
                                    </div>
                                </template>
                            </el-table-column>
                            <el-table-column prop="paramType" label="参数类型"  width="320" >
                                <template #default="scope">
                                    <div style="display: flex; align-items: center">
                                        <span v-if="scope.row.paramType == 'J'">Java原生类型({{scope.row.paramClass}})</span>
                                        <span v-if="scope.row.paramType == 'D'">领域对象({{scope.row.paramClassObject.code}})</span>
                                        <span v-if="scope.row.paramType == 'V'">值传递对象({{scope.row.paramClassObject.code}})</span>
                                    </div>
                                </template>
                            </el-table-column>
                        </el-table>
                    </el-scrollbar>
                </el-tab-pane>
			    <el-tab-pane label="过程数据" name="P" v-if="isContextTypeContained('P')">
                    <el-scrollbar height="300px" >
                        <el-table :data="inProcessDatas" stripe style="width: 100%"  @row-click="handleCurrentChange" highlight-current-row>
                            <el-table-column prop="key" label="关键字" width="210" />
                            <el-table-column prop="objectType" label="类型" width="180" >
                                <template #default="scope">
                                    <div style="display: flex; align-items: center">
                                        <span v-if="scope.row.objectType == 'J'">Java原生类型({{scope.row.objectClass}})</span>
                                        <span v-if="scope.row.objectType == 'D'">领域对象({{scope.row.objectCode}})</span>
                                        <span v-if="scope.row.objectType == 'V'">值传递对象({{scope.row.objectCode}})</span>
                                    </div>
                                </template>
                            </el-table-column>
                            <el-table-column prop="nodeName" label="输出节点" width="360" />
                        </el-table>
                    </el-scrollbar>
                </el-tab-pane>
			    <el-tab-pane label="领域对象数据" name="D" v-if="isContextTypeContained('D')">
                    <el-scrollbar height="300px" >
                        <el-table :data="domainObjectValues" stripe style="width: 100%" @row-click="handleCurrentChange"  highlight-current-row>
                            <el-table-column prop="key" label="关键字" width="180" />
                            <el-table-column prop="objectType" label="类型" width="180" >
                                <template #default="scope">
                                    <div style="display: flex; align-items: center">
                                        <span v-if="scope.row.objectType == 'J'">Java原生类型({{scope.row.objectClass}})</span>
                                        <span v-if="scope.row.objectType == 'D'">领域对象({{scope.row.objectCode}})</span>
                                        <span v-if="scope.row.objectType == 'V'">值传递对象({{scope.row.objectCode}})</span>
                                    </div>
                                </template>
                            </el-table-column>
                            <el-table-column prop="nodeName" label="输出节点" width="380" />
                        </el-table>
                    </el-scrollbar>
			    </el-tab-pane>
			    <el-tab-pane label="属主对象数据" name="O"  v-if="isContextTypeContained('O')">
                    <el-scrollbar height="300px" >
	                    <el-table :data="ownerAttributes" stripe style="width: 100%" @row-click="handleCurrentChange"  highlight-current-row>
			                <el-table-column prop="code" label="属性编码" width="180" />
		                    <el-table-column prop="name" label="属性名称" width="180" />
		                    <el-table-column prop="isListAttr" label="列表" width="60" >
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
	                </el-scrollbar>
			    </el-tab-pane>
			    <el-tab-pane label="其他数据" name="other">
                    <el-scrollbar height="300px" >
                        <el-row :gutter="4"  v-if="isContextTypeContained('N')">
					        <el-col :span="24">
					            <el-form-item label="预处理数据：" label-width="120px" >
                                    <el-link  type="primary" @click="handleCurrentChange('', 'N')" ><label>预处理数据</label></el-link>
					            </el-form-item>
					        </el-col>
					    </el-row>
                        <el-row :gutter="4"  v-if="isContextTypeContained('U')">
					        <el-col :span="24">
					            <el-form-item label="上传文件数据：" label-width="120px" >
                                    <el-link  type="primary" @click="handleCurrentChange('', 'U')" ><label>根据客户端上传文件动态确定</label></el-link>
					            </el-form-item>
					        </el-col>
					    </el-row>
					    <el-row :gutter="4"  v-if="isContextTypeContained('P')">
                            <el-col :span="24">
                                <el-form-item label="登录会话数据：" label-width="120px">
                                    <el-link  type="primary" @click="handleCurrentChange(this.loginSessionKey, 'loginSession')" ><label>{{this.loginSessionKey}}</label></el-link>
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </el-scrollbar>
			    </el-tab-pane>
			</el-tabs>
        </el-col>
    </el-row>
    <template #footer>
        <span class="dialog-footer">
            <el-row :gutter="4" >
			    <el-col :span="8">
			        <el-form-item label="上下文类型：" label-width="140px" >
                        <el-text class="mx-1">{{ getContextTypeName(this.contextType) }}</el-text>
			        </el-form-item>
			    </el-col>
                <el-col :span="8">
			        <el-form-item label="上下文关键字：" label-width="140px" >
                        <el-text class="mx-1">{{ this.contextKey }}</el-text>
			        </el-form-item>
			    </el-col>
                <el-col :span="8">
			        <el-button @click="hideDataSelectPage()">取消</el-button>
                    <el-button type="primary" @click="select()">选择</el-button>
			    </el-col>
			</el-row>
            
        </span>
    </template>
</el-dialog>
</template>

<script>
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['operation', 'params', 'ownerAttributes', 'domainObjectValues', 'inProcessDatas', 'loginSessionKey', 'showAvailableDataSelect', 'contextTypes'],
    
    emits: [ 'hideDataSelectPage', 'selectAvailabeData'],
    
    setup(props, {attrs, emit, slots}) {
        const hideDataSelectPage = () => {
            emit('hideDataSelectPage');
        };
        
        const selectAvailabeData = (contextType, contextKey) => {
        	emit('selectAvailabeData', contextType, contextKey);
        };
        
        return {
        	hideDataSelectPage,
        	selectAvailabeData
        }
    },
    
    components: {
    	
    },
    
    computed: {
        showFlag: {
            get() {
                return this.showAvailableDataSelect;
            },
            set(value) {
                this.hideDataSelectPage();
            }
        }
    },

    data() {
    	const activeName = ref('I');
        const contextType = ref(null);
        const contextKey = ref(null);
    	
        return {
        	activeName,
            contextType,
            contextKey
        }
    },
    mounted() {
    	if(!this.isContextTypeContained('I')) {
            this.activeName = 'P';
        }
    },
    
    methods: {
    	handleCurrentChange(val, subType) {
            if(this.activeName == "I") {
                this.contextType = this.activeName;
                this.contextKey = val.code;
            }else if(this.activeName == "O") {
                this.contextType = this.activeName;
                this.contextKey = val.code;
            }else if(this.activeName == "P"){
                this.contextType = this.activeName;
                this.contextKey = val.key;
            }else if(this.activeName == "D"){
                this.contextType = this.activeName;
                this.contextKey = val.key;
            }else if(this.activeName == "other"){
                if(subType == "U") {
                    this.contextType = subType;
                    this.contextKey = null;
                }else if(subType == "loginSession") {
                    this.contextType = "P";
                    this.contextKey = val;
                }else if(subType == "N") {
                    this.contextType = subType;
                    this.contextKey = null;
                }
                
            }
        },
        select() {
            if(this.contextType == null) {
                ElMessage.error(`请先选择一条数据！`);
                return;
            }
            this.selectAvailabeData(this.contextType, this.contextKey);
        },
        isContextTypeContained(type) {
            if(this.contextTypes == null || this.contextTypes.length == 0) {
                return false;
            }
            for(var i in this.contextTypes) {
                if(this.contextTypes[i].value == type) {
                    return true;
                }
            }
            return false;
        },
        getContextTypeName(type) {
            if(this.contextTypes == null || this.contextTypes.length == 0) {
                return null;
            }
            for(var i in this.contextTypes) {
                if(this.contextTypes[i].value == type) {
                    return this.contextTypes[i].label;
                }
            }
            return null;
        }
    }
}

</script>
