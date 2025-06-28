<!-- 可用数据建议 -->
<template>
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
                <el-tab-pane label="方法参数" name="first">
                    <el-scrollbar height="300px" >
                        <el-table :data="params" stripe style="width: 100%">
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
                            <el-table-column prop="paramType" label="参数类型"  width="280" >
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
			    <el-tab-pane label="过程数据" name="second">
                    <el-scrollbar height="300px" >
                        <el-table :data="inProcessDatas" stripe style="width: 100%">
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
                            <el-table-column prop="nodeName" label="输出节点" width="280" />
                        </el-table>
                    </el-scrollbar>
                </el-tab-pane>
			    <el-tab-pane label="领域对象数据" name="third">
                    <el-scrollbar height="300px" >
                        <el-table :data="domainObjectValues" stripe style="width: 100%">
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
                            <el-table-column prop="nodeName" label="输出节点" width="280" />
                        </el-table>
                    </el-scrollbar>
			    </el-tab-pane>
			    <el-tab-pane label="属主对象数据" name="fourth">
                    <el-scrollbar height="300px" >
	                    <el-table :data="ownerAttributes" stripe style="width: 100%">
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
			    <el-tab-pane label="其他数据" name="fifth">
                    <el-scrollbar height="300px" >
                        <el-row :gutter="4">
					        <el-col :span="24">
					            <el-form-item label="上传文件数据：" label-width="120px">
					                根据客户端上传文件动态确定
					            </el-form-item>
					        </el-col>
					    </el-row>
					    <el-row :gutter="4">
                            <el-col :span="24">
                                <el-form-item label="登录会话数据：" label-width="120px">
                                    {{this.loginSessionKey}}
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </el-scrollbar>
			    </el-tab-pane>
			</el-tabs>
        </el-col>
    </el-row>

</template>

<script>
import { ref } from 'vue';

export default {
    props: ['operation', 'params', 'ownerAttributes', 'domainObjectValues', 'inProcessDatas', 'loginSessionKey'],
    
    emits: [],
    
    setup(props, {attrs, emit, slots}) {
        
        
        return {
        	
        }
    },
    
    components: {
    	
    },
    
    computed: {
        
    },
    data() {
    	const activeName = ref('first');
    	
        return {
        	activeName
        }
    },
    mounted() {
    	
    },
    
    methods: {
    	handleClick(tab, event) {
        	
        }
        
    }
}

</script>
