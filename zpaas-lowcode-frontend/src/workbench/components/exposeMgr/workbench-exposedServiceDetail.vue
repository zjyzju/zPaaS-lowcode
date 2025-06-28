<!--查看暴露服务信息 -->
<template>
<el-dialog v-model="showFlag" title="查看暴露服务信息" width="900px">
    <template #header>
        <span class="title">查看暴露服务信息</span>
    </template>
    <el-form  :model="exposedOperation" label-width="120px">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="发布标识">
                    <el-input v-model="exposedOperation.id" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="发布状态">
                    <el-select v-model="exposedOperation.status" class="m-2" disabled placeholder="Select" size="small">
                           <el-option
                                  v-for="item in statusOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                    </el-select>
                </el-form-item>
            </el-col>            
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="服务编码">
                    <el-input v-model="exposedOperation.serviceCode" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="方法编码">
                    <el-input v-model="exposedOperation.operationCode" readonly size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="HttpMethod">
                    <el-select v-model="exposedOperation.httpMethod" class="m-2" disabled placeholder="Select" size="small">
                           <el-option
                                  v-for="item in httpMethodOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="请求URL">
                    <el-input v-model="exposedOperation.httpMapping" readonly size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="创建时间">
                    <el-input v-model="exposedOperation.createTime" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="更新时间">
                    <el-input v-model="exposedOperation.updateTime" readonly size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
        
        <el-row :gutter="4">
		    <el-col :span="24">
		        <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 12px">
		        <div class="toolbar1">
		            <el-icon><histogram /></el-icon><span>参数</span>&nbsp;&nbsp;&nbsp;&nbsp;
		        </div>
		        </el-divider>
		    </el-col>
		</el-row>
        <el-row :gutter="4">
		    <el-col :span="1"/>
		    <el-col :span="23">
		        <el-table :data="exposedOperation.parameters" stripe style="width: 100%">
		            <el-table-column prop="code" label="编码" width="240" />
		            <el-table-column prop="name" label="名称" width="210" />
		            <el-table-column prop="isInParam" label="是否入参" width="80" >
		                <template #default="scope">
		                    <div style="display: flex; align-items: center">
		                        <span v-if="scope.row.isInParam == 'Y'">是</span>
		                        <span v-if="scope.row.isInParam == 'N'">否</span>
		                    </div>
		                </template>
		            </el-table-column>
		            <el-table-column prop="isListParam" label="是否列表" width="80" >
		                <template #default="scope">
		                    <div style="display: flex; align-items: center">
		                        <span v-if="scope.row.isListParam == 'Y'">是</span>
		                        <span v-if="scope.row.isListParam == 'N'">否</span>
		                    </div>
		                </template>
		            </el-table-column>
		            <el-table-column prop="paramType" label="参数类型" width="240" >
		                <template #default="scope">
		                    <div style="display: flex; align-items: center">
		                        <span v-if="scope.row.paramType == 'J'">Java原生类型({{scope.row.paramClass}})</span>
		                        <span v-if="scope.row.paramType == 'D'">领域对象({{scope.row.paramClassObject.code}})</span>
		                        <span v-if="scope.row.paramType == 'V'">值传递对象({{scope.row.paramClassObject.code}})</span>
		                    </div>
		                </template>
		            </el-table-column>
		            
		        </el-table>
		    </el-col>
		</el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideExposedServiceDetailPage()">取消</el-button>
        </span>
    </template>
</el-dialog>
</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

export default {
    props: ['exposedOperation', 'showExposedServiceDetail'],
    
    emits: ['hideExposedServiceDetailPage'],
    
    setup(props, {attrs, emit, slots}) {
        const hideExposedServiceDetailPage= () => {
            emit('hideExposedServiceDetailPage');
        }
        
        return {
        	hideExposedServiceDetailPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showExposedServiceDetail;
            },
            set(value) {
                this.hideExposedServiceDetailPage();
            }
        }
    },
    data() {
        const httpMethodOptions = ref(null);
        
        const statusOptions = ref(null);
        
        return {
            httpMethodOptions,
            statusOptions
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_HTTP_METHOD','PUB_STATUS']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.httpMethodOptions = data.data['PUB_HTTP_METHOD'];
                this.statusOptions = data.data['PUB_STATUS'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },  
    methods: {
    	
    }
}

</script>