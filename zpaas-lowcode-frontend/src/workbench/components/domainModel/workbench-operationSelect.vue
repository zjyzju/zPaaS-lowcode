<!-- 方法选择 -->
<template>
<el-dialog v-model="showFlag" title="方法选择">
    <template #header>
        <span class="title">方法选择</span>
    </template>
    <div>
    <el-form  :model="showFlag" label-width="120px">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="对象类型">
                      <el-select v-model="objectType" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in objectTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="9">
                <el-form-item label="方法归属对象">
                    <el-input type="hidden" v-model="objectId"   size="small"/>
                    <el-input v-model="objectCode" :readonly="true"  size="small"/>
                    
                </el-form-item>
            </el-col>
            <el-col :span="3">
                <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectObject()" ><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearObject()" ><label>清空</label></el-link>                                                
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="方法">
                    <el-select v-model="operationId" class="m-2" clearable placeholder="Select" size="small">
                        <el-option
                                v-for="item in operations"
                                :key="item.id"
                                :label="item.code"
                                :value="item.id"
                            />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12"></el-col>
        </el-row>
    </el-form>
    </div>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideOperationSelectPage()">取消</el-button>
            <el-button type="primary" @click="selectOperation()">确定</el-button>
        </span>
    </template>
	
</el-dialog>

<!-- 选择调用服务对象信息 -->
<serviceObjectSelect v-if="showServiceObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideServiceObjectSelectPage="hideServiceObjectSelectPage" :showServiceObjectSelect="showServiceObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>
<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideDomainObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'

import serviceObjectSelect from './workbench-serviceObjectSelect.vue'
import domainObjectSelect from './workbench-domainObjectSelect.vue';

export default {
    props: ['showOperationSelect','systemId'],
    
    emits: ['hideOperationSelectPage', 'operationSelected'],
    
    setup (props, {attrs, emit, slots}) {
        const operationSelected = (operation)=> {
            emit('operationSelected', operation);
        };
        
        const hideOperationSelectPage = () => {
            emit('hideOperationSelectPage');	
        };
        
        return {
        	hideOperationSelectPage,
        	operationSelected
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showOperationSelect;
            },
            set(value) {
                this.hideOperationSelectPage();
            }
        }
    },
    components: {
    	serviceObjectSelect,
        domainObjectSelect
    },
    data() { 
        const objectTypeOptions = ref(null);           
    	const managedObjectsForSelect = ref(null);
        const showServiceObjectSelect = ref(false);
        const showDomainObjectSelect = ref(false);
        
        const objectType = ref("S");
        const objectId = ref(null);
        const objectCode = ref(null);
        const operationId = ref(null);
        
        const operations = ref([]);
        
        return {
        	showServiceObjectSelect,
            managedObjectsForSelect,
            showDomainObjectSelect,
            objectType,
            objectId,
            objectCode,
            operationId,
            objectTypeOptions,
            
            operations
        }
    },
    watch: {
    	
    },
    mounted() {
    	axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_TYPE_HAS_OPERATION']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectTypeOptions = data.data['OBJECT_TYPE_HAS_OPERATION'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	selectOperation() {
    		if(this.operationId == null || this.operationId.trim().length == 0) {
    			ElMessage.error(`请先选择一个方法！`);
    			return;
    		}
            this.operations.forEach(operation=> {
                if(operation.id == this.operationId) {
                    this.operationSelected(operation);
                }
            });
    		
        },
        selectObject() {
            if("D" == this.objectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedObjectsForSelect = data.data;
                        this.showDomainObjectSelect = true;
                        this.showServiceObjectSelect = false;
                    }
                });
            }else if("S" == this.objectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/serviceObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedObjectsForSelect = data.data;
                        this.showServiceObjectSelect = true;
                        this.showDomainObjectSelect = false;
                    }
                });
            }else {
                return;
            }
        },
        clearObject(){
            this.objectId = "";
            this.objectCode = "";
            this.operationId = "";
            this.operations = [];
        },
        managedObjectSelected(obj) {
            this.objectId = obj.id;
            this.objectCode = obj.code;
            this.managedObjectsForSelect = null;
            this.showDomainObjectSelect = false;
            this.showServiceObjectSelect = false;
            var url = "/lcdp-proxy/lowcode/platform/be/api/operation/listNoParam/" + this.objectType + "/" + this.objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) {
                    this.operationId = ""; 
                    this.operations = data.data;
                }
            });
        },
        hideDomainObjectSelectPage(){
             this.managedObjectsForSelect = null;
             this.showDomainObjectSelect = false;
        },
        hideServiceObjectSelectPage(){
            this.managedObjectsForSelect = null;
            this.showServiceObjectSelect = false;
        }
    }
}
</script>