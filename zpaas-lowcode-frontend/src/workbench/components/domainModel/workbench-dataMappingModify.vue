<!-- 修改数据映射信息 -->
<template>
<el-dialog v-model="showFlag" title="修改数据映射信息" width="800px">
    <template #header>
        <span class="title">修改数据映射信息</span>
    </template>
    <el-form  :model="modifyDataMapping" label-width="120px" :rules="validateRules" ref="modifyDataMappingForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="标识" required prop="id">
                    <el-input v-model="modifyDataMapping.id" readonly  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="modifyDataMapping.name"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="asFromOrTo == 'F'">
            <el-col :span="12">
                <el-form-item label="源对象类型" required prop="fromObjectType">
                    <el-select v-model="modifyDataMapping.fromObjectType" class="m-2" placeholder="Select" size="small" disabled>
                        <el-option v-for="item in objectTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="源对象" required prop="fromObjectId">
                    <el-input v-model="modifyDataMapping.fromObjectId" type="hidden" readonly size="small"/>
                    <el-input v-model="modifyDataMapping.fromObject.code" readonly size="small"/>
                </el-form-item>
            </el-col>
        </el-row>  
        <el-row :gutter="4" v-if="asFromOrTo == 'F'">
            <el-col :span="9">
                <el-form-item label="目标对象类型" required prop="toObjectType">
                    <el-select v-model="modifyDataMapping.toObjectType" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in objectTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
             <el-col :span="3">
                <el-form-item label="" label-width="10px">
                    <el-link type="primary" @click="selectToObject()" ><label>选择</label></el-link>&nbsp;
                    <el-link type="primary" @click="clearToObject()" ><label>清空</label></el-link> 
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="目标对象" required prop="toObjectId">
                    <el-input v-model="modifyDataMapping.toObjectId" type="hidden" readonly size="small"/>
                    <el-input v-model="modifyDataMapping.toObject.code" readonly size="small"/>
                </el-form-item>
            </el-col>
           
        </el-row>
        
          
        <el-row :gutter="4" v-if="asFromOrTo == 'T'">
            <el-col :span="9">
                <el-form-item label="源对象类型" required prop="fromObjectType">
                    <el-select v-model="modifyDataMapping.fromObjectType" class="m-2" placeholder="Select" size="small">
                        <el-option v-for="item in objectTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
             <el-col :span="3">
                <el-form-item label="" label-width="10px">
                    <el-link type="primary" @click="selectFromObject()" ><label>选择</label></el-link>&nbsp;
                    <el-link type="primary" @click="clearFromObject()" ><label>清空</label></el-link> 
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="源对象" required prop="fromObjectId">
                    <el-input v-model="modifyDataMapping.fromObjectId" type="hidden" readonly size="small"/>
                    <el-input v-model="modifyDataMapping.fromObject.code" readonly size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="asFromOrTo == 'T'">
            <el-col :span="12">
                <el-form-item label="目标对象类型" required prop="toObjectType">
                    <el-select v-model="modifyDataMapping.toObjectType" class="m-2" placeholder="Select" size="small" disabled>
                        <el-option v-for="item in objectTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="目标对象" required prop="toObjectId">
                    <el-input v-model="modifyDataMapping.toObjectId" type="hidden" readonly size="small"/>
                    <el-input v-model="modifyDataMapping.toObject.code" readonly size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
           <el-col :span="24">
                <el-form-item label="子映射信息">
                    <el-input v-model="modifyDataMapping.subDataMappings" type="textarea" rows="3" placeholder='{"attributeCode1":"dataMappingId1", "attributeCode2":"dataMappingId2"}' size="small" />
                </el-form-item>
            </el-col>
        </el-row>
        
        <el-row :gutter="4">
                    <el-col :span="24">
                        <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 12px">
                        <div class="toolbar1">
                            <span class="title">属性映射</span>&nbsp;&nbsp;&nbsp;&nbsp;
                        </div>
                        </el-divider>
                    </el-col>
                </el-row>
        <el-row :gutter="4">
		    <el-col :span="2"></el-col>
		    <el-col :span="20">
		        <el-row :gutter="4">
		            <el-col :span="12">
		                <el-form-item label="源对象属性">
		                    <el-select v-model="objectId1" class="m-2" placeholder="Select" size="small" clearable>
		                        <el-option v-for="item in modifyDataMapping.fromObject.attributes" :key="item.id" :label="item.code" :value="item.id" />
		                    </el-select>
		                </el-form-item>
		            </el-col>
		            <el-col :span="12">
		                <el-form-item label="目标对象属性">
                            <el-select v-model="objectId2" class="m-2" placeholder="Select" size="small" clearable>
                                <el-option v-for="item in modifyDataMapping.toObject.attributes" :key="item.id" :label="item.code" :value="item.id" />
                            </el-select>
                        </el-form-item>
		            </el-col>
		        </el-row>
		    </el-col>  
		    <el-col :span="2"></el-col>                 
		</el-row>
        <el-row :gutter="4">
		    <el-col :span="2"></el-col>
		    <el-col :span="20">
		        <el-row :gutter="4">
		            <el-col :span="12">
		                <el-form-item label="转换方法">
		                    <el-select v-model="transferMethod" class="m-2" placeholder="Select" size="small" clearable>
		                        <el-option v-for="item in transferMethods" :key="item.id" :label="item.name" :value="item.id" />
		                    </el-select>
		                </el-form-item>
		            </el-col>
		            <el-col :span="12">
		                <el-form-item label="转换配置">
                            <el-input v-model="transferCfg" type="textarea" rows="3" class="m-2" size="small"/>
                        </el-form-item>
		            </el-col>
		        </el-row>
		    </el-col>  
		    <el-col :span="2"><el-link  type="primary" @click="addAttributeMapping()">添加</el-link></el-col>                 
		</el-row>
		<el-row :gutter="4" v-if="modifyDataMapping.attributeMappings">
		    <el-col :span="1"/>
		    <el-col :span="23">
		    <el-scrollbar height="200px">
		        <el-table :data="modifyDataMapping.attributeMappings" stripe style="width: 100%">
		            <el-table-column  label="操作" width="60">
		                <template #default="scope">
		                    <el-link  type="primary" @click="deleteAttributeMapping(scope.$index)">删除</el-link>
		                </template>
		            </el-table-column>
		            <el-table-column prop="fromAttribute.code" label="源属性编码" width="180" />
		            <el-table-column prop="fromAttribute.name" label="源属性名称" width="180" />
		            <el-table-column prop="toAttribute.code" label="目标属性编码" width="180" />
		            <el-table-column prop="toAttribute.name" label="目标属性名称" width="180"/>
                    <el-table-column prop="transferMethod" label="转换方法" width="180" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center"  v-for="rowMethod in transferMethods">
                                <span v-if="scope.row.transferMethod == rowMethod.id">{{ rowMethod.name }}</span>
                            </div>
                        </template>
                    </el-table-column>
		            <el-table-column prop="transferCfg" label="转换配置" width="880"/>
		        </el-table>
		    </el-scrollbar>
		    </el-col>
		</el-row>
    </el-form>

    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideDataMappingModifyPage()">取消</el-button>
            <el-button type="primary" @click="updateDataMapping()">确定</el-button>
        </span>
    </template>
</el-dialog>

<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideManagedObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>

<!-- 选择值传递对象信息 -->
<valueObjectSelect v-if="showValueObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideValueObjectSelectPage="hideManagedObjectSelectPage" :showValueObjectSelect="showValueObjectSelect"  :managedObjectsForSelect="managedObjectsForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'

import domainObjectSelect from './workbench-domainObjectSelect.vue'
import valueObjectSelect from './workbench-valueObjectSelect.vue'

export default {
    props: ['modifyDataMapping','showDataMappingModify','asFromOrTo'],
    
    emits: ['updateDataMappingToList', 'hideDataMappingModifyPage'],
    
    setup (props, {attrs, emit, slots}) {
        const updateDataMappingToList = (modifyDataMapping)=> {
            emit('updateDataMappingToList', modifyDataMapping);
        };
        
        const hideDataMappingModifyPage = () => {
            emit('hideDataMappingModifyPage');	
        };
        
        return {
        	updateDataMappingToList,
        	hideDataMappingModifyPage
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showDataMappingModify;
            },
            set(value) {
                this.hideDataMappingModifyPage();
            }
        }
    },
    components: {
        domainObjectSelect,
        valueObjectSelect
    },
    data() {            
    	const showDomainObjectSelect = ref(false);
        const managedObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        const objectId1 = ref(null);
        const objectId2 = ref(null);
        const transferMethods = ref(null);
        const transferMethod = ref(null);
        const transferCfg = ref(null);
        
        const objectTypeOptions = [
            {
                value: 'D',
                label: '领域对象',
            },
            {
                value: 'V',
                label: '值传递对象',
            }
        ];

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "fromObjectType": [
                { required: true, message: '源对象类型不能为空！', trigger: 'blur' }
            ],
            "fromObjectId": [
                { required: true, message: '源对象不能为空！', trigger: 'blur' }
            ],
            "toObjectType": [
                { required: true, message: '目标对象类型不能为空！', trigger: 'blur' }
            ],
            "toObjectId": [
                { required: true, message: '目标对象不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
        	showDomainObjectSelect,
            managedObjectsForSelect,
            showValueObjectSelect,
            objectId1,
            objectId2,
            objectTypeOptions,
            transferMethods,
            transferMethod,
            transferCfg,
            validateRules
        }
    },
    watch: {       
        'modifyDataMapping.fromObjectType': function(val, old){
        	this.modifyDataMapping.fromObjectId = "";
            this.modifyDataMapping.attributeMappings = [];
            this.modifyDataMapping.fromObject = {};
        },
        'modifyDataMapping.toObjectType': function(val, old){
            this.modifyDataMapping.toObjectId = "";
            this.modifyDataMapping.attributeMappings = [];
            this.modifyDataMapping.toObject = {};
        }
    
    },  
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_TYPE_HAS_ATTRIBUTE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectTypeOptions = data.data['OBJECT_TYPE_HAS_ATTRIBUTE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	if(this.modifyDataMapping.fromObjectId != null && this.modifyDataMapping.fromObjectId != "" && this.modifyDataMapping.fromObjectType != null && this.modifyDataMapping.fromObjectType != "") {
    		var url;
    		if(this.modifyDataMapping.fromObjectType == "D") {
    			url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/byId/" + this.modifyDataMapping.fromObjectId ;
    		}else {
    			url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/byId/" + this.modifyDataMapping.fromObjectId ;
    		}
    		
            axiosClient.post(url).then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) {
            	    this.modifyDataMapping.fromObject = data.data;             
                }   
            });
    	}
    	
    	if(this.modifyDataMapping.toObjectId != null && this.modifyDataMapping.toObjectId != "" && this.modifyDataMapping.toObjectType != null && this.modifyDataMapping.toObjectType != "") {
            var url;
            if(this.modifyDataMapping.toObjectType == "D") {
                url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/byId/" + this.modifyDataMapping.toObjectId ;
            }else {
                url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/byId/" + this.modifyDataMapping.toObjectId ;
            }
            axiosClient.post(url).then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) {
                    this.modifyDataMapping.toObject = data.data;               
                } 
            });
        }

        var url = "/lcdp-proxy/lowcode/platform/be/api/dataTransferMethod/list";
        axiosClient.post(url).then((response) => {
            var data = response.data;
            if(data != null && data.status == "200" && data.data != null) {
                this.transferMethods = data.data;                
            }
        });
    },
    methods: {
    	updateDataMapping() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/dataMapping/updateWithAttributeMapping";
            this.$refs.modifyDataMappingForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.modifyDataMapping).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`修改数据映射成功`);
                            this.updateDataMappingToList(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        selectFromObject() {
        	if(this.modifyDataMapping.fromObjectType == null || this.modifyDataMapping.fromObjectType == "") {
        		ElMessage(`请先选择源对象类型！`);
        	}
        	
        	var url;
        	if(this.modifyDataMapping.fromObjectType == "D") {
        		url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.modifyDataMapping.systemId;
        	}else {
        		url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/list/" + this.modifyDataMapping.systemId;
        	}
             
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.managedObjectsForSelect = data.data;
                    if(this.modifyDataMapping.fromObjectType == "D") {
                        this.showDomainObjectSelect = true;
                        this.showRepresetationObjectSelect = false;
                    }else {
                        this.showDomainObjectSelect = false;
                        this.showValueObjectSelect = true;
                    }
                }
            });
        },
        clearFromObject(){
        	this.modifyDataMapping.fromObjectId = "";
            this.modifyDataMapping.attributeMappings = [];
            this.modifyDataMapping.fromObject = {};
        },
        managedObjectSelected(obj) {
        	var url;
        	if(this.asFromOrTo == "F") {
                this.modifyDataMapping.toObjectId = obj.id;
                this.modifyDataMapping.attributeMappings = [];
                if(this.modifyDataMapping.toObjectType == "D") {
                    url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/byId/" + this.modifyDataMapping.toObjectId ;
                }else {
                    url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/byId/" + this.modifyDataMapping.toObjectId ;
                }
            }else if(this.asFromOrTo == "T") {
                this.modifyDataMapping.fromObjectId = obj.id;
                this.modifyDataMapping.attributeMappings = [];
                if(this.modifyDataMapping.fromObjectType == "D") {
                    url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/byId/" + this.modifyDataMapping.fromObjectId ;
                }else {
                    url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/byId/" + this.modifyDataMapping.fromObjectId ;
                }
            }      	
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    if(this.asFromOrTo == "F") {
                        this.modifyDataMapping.toObject = data.data;
                    }else if(this.asFromOrTo == "T") {
                        this.modifyDataMapping.fromObject = data.data;
                    }
                    this.managedObjectsForSelect = null;
                    this.showDomainObjectSelect = false;
                    this.showValueObjectSelect = false;
                }
            });
        },
        hideManagedObjectSelectPage(){
             this.managedObjectsForSelect = null;
             this.showDomainObjectSelect = false;
             this.showValueObjectSelect = false;
        },
        selectToObject() {
            if(this.modifyDataMapping.toObjectType == null || this.modifyDataMapping.toObjectType == "") {
                ElMessage(`请先选择目标对象类型！`);
            }
            
            var url;
            if(this.modifyDataMapping.toObjectType == "D") {
                url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.modifyDataMapping.systemId;
            }else {
                url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/list/" + this.modifyDataMapping.systemId;
            }
             
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.managedObjectsForSelect = data.data;
                    if(this.modifyDataMapping.toObjectType == "D") {
                        this.showDomainObjectSelect = true;
                        this.showValueObjectSelect = false;
                    }else {
                        this.showDomainObjectSelect = false;
                        this.showValueObjectSelect = true;
                    }
                }
            });
        },
        clearToObject(){
            this.modifyDataMapping.toObjectId = "";
            this.modifyDataMapping.attributeMappings = [];
            this.modifyDataMapping.toObject = {};
        },
        addAttributeMapping() {
        	if(this.objectId1 == null || this.objectId1 == "" || this.objectId2 == null || this.objectId2 == "") {
        		ElMessage.error(`请先选择源对象属性和目标对象属性！`);
        		return;
        	}
        	var newAttributeMapping = {
        			id: "",
        			fromAttributeId: this.objectId1,
        			toAttributeId: this.objectId2,
        			dataMappingId: this.modifyDataMapping.id,
                    transferMethod: this.transferMethod,
                    transferCfg: this.transferCfg,
        			tenantId : this.modifyDataMapping.tenantId,
                    systemId : this.modifyDataMapping.systemId,
                    createTime : "",
                    updateTime : ""
        	}
        	this.modifyDataMapping.fromObject.attributes.forEach((attr)=>{
        		if(attr.id == newAttributeMapping.fromAttributeId) {
        			newAttributeMapping.fromAttribute = attr;
        			return;
        		}
        	});
        	this.modifyDataMapping.toObject.attributes.forEach((attr)=>{
                if(attr.id == newAttributeMapping.toAttributeId) {
                    newAttributeMapping.toAttribute = attr;
                    return;
                }
            });
        	if(this.modifyDataMapping.attributeMappings == null) {
        		this.modifyDataMapping.attributeMappings = [];
        	}
        	this.modifyDataMapping.attributeMappings.push(newAttributeMapping);
            this.modifyDataMapping.attributeMappings.sort((a,b)=>{
                if(a.fromAttribute == null) {
                    return -1;
                }else if(b.fromAttribute == null) {
                    return 1;
                }else {
                    return a.fromAttribute.code.localeCompare(b.fromAttribute.code);
                }
            });
        },
        deleteAttributeMapping(row) {
        	this.modifyDataMapping.attributeMappings.splice(row,1);
        }
    }
}
</script>