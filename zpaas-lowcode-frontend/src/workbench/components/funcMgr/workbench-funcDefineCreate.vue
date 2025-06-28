<!-- 新建系统功能 -->
<template>
<el-dialog v-model="showFlag" title="新建系统功能" width="800px">
    <template #header>
        <span class="title">新建系统功能</span>
    </template>
    <el-form  :model="newFuncDefine" label-width="120px" :rules="validateRules" ref="newFuncDefineForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="newFuncDefine.name"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="描述">
                    <el-input v-model="newFuncDefine.description"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="归属业务域" required prop="domainId">
                    <el-select v-model="newFuncDefine.domainId" v-if="businessDomains"  class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in businessDomains"
                                  :key="item.businessDomain.id"
                                  :label="item.businessDomain.name"
                                  :value="item.businessDomain.id"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="功能模板" required prop="templateId">
                    <el-select v-model="newFuncDefine.templateId" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in funcTemplates"
                                  :key="item.id"
                                  :label="item.name"
                                  :value="item.id"
                            />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="创建方式">
                    <el-radio-group v-model="createMode" disabled v-if="newFuncDefine.funcTemplate != null && (newFuncDefine.funcTemplate.templateType == 'R' || newFuncDefine.funcTemplate.templateType == 'V')" class="ml-4">
                        <el-radio value="B" size="large">根据BI数据集</el-radio>
                    </el-radio-group>
                    <el-radio-group v-model="createMode" v-else class="ml-4">
                        <el-radio value="D" size="large">根据领域对象</el-radio>
                        <el-radio value="T" size="large">根据数据库表</el-radio>
                        <el-radio value="V" size="large">根据值传递对象</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="生成级别">
                    <el-radio-group v-model="generateLevel" disabled class="ml-4">
                        <el-radio value="A" size="large">生成全部</el-radio>
                        <el-radio value="F" size="large">生成框架</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="功能模式">
                    <el-radio-group v-model="funcPattern" disabled class="ml-4">
                        <el-radio value="D" size="large">动态配置</el-radio>
                        <el-radio value="C" size="large">生成代码</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="createMode == 'T'">
            <el-col :span="12">
                <el-form-item label="领域对象业务域">
                    <el-select v-model="businessDomainId" class="m-2" placeholder="Select" size="small">
                           <el-option v-for="item in businessDomains"
                                :key="item.businessDomain.id"
                                :label="item.businessDomain.name"
                                :value="item.businessDomain.id"
                            />
                    </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="数据库">
                    <el-select v-model="dbSchemaId" class="m-2" placeholder="Select" size="small">
                           <el-option v-for="item in dbSchemas"
                                :key="item.id"
                                :label="item.name"
                                :value="item.id"
                            />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4" v-if="createMode == 'T'">
            <el-col :span="12">
                <el-form-item label="忽略的表名前缀">
                    <el-input v-model="ignorePrefixString" size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="说明：">
                允许输入多个，以逗号分隔
                </el-form-item>
            </el-col>
        </el-row>
        
        <!-- 绑定对象信息 -->
	    <br/>
	    <el-row :gutter="4" v-if="newFuncDefine != null">
	        <el-col :span="1"></el-col>
	        <el-col :span="23">
	            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
	            <div class="toolbar1">
	                <el-icon><histogram /></el-icon><span>绑定对象</span>&nbsp;&nbsp;&nbsp;&nbsp;
	                <el-link @click="showObjectAssignCreatePage()"  type="primary" >新建</el-link>
	            </div>
	            </el-divider>
	        </el-col>
	    </el-row>
	    <el-row :gutter="4" v-if="newFuncDefine != null">
	        <el-col :span="1"/>
	        <el-col :span="23">
	            <el-table :data="newFuncDefine.objectAssigns" stripe style="width: 100%">
	                <el-table-column  label="操作" width="60">
	                    <template #default="scope">
	                        <el-link  type="primary"  @click="deleteObjectAssign(scope.$index)">删除</el-link>
	                    </template>
	                </el-table-column>
	                    <el-table-column prop="objectType" label="绑定对象类型" width="110" >
	                        <template #default="scope">
	                            <div style="display: flex; align-items: center">
	                              <span v-if="scope.row.objectType == 'D'">领域对象</span>
	                              <span v-if="scope.row.objectType == 'V'">值传递对象</span>
	                              <span v-if="scope.row.objectType == 'T'">数据库表</span>
                                  <span v-if="scope.row.objectType == 'B'">BI数据集</span>
	                            </div>
	                        </template>
	                    </el-table-column>
	                    <el-table-column prop="objectId" label="绑定对象" width="150" >
	                        <template #default="scope">
	                            <div style="display: flex; align-items: center">
	                              <span v-if="scope.row.assignObject != null">{{scope.row.assignObject.code}}</span>
	                              <span v-if="scope.row.assignObject == null">{{scope.row.objectId}}</span>
	                            </div>
	                        </template>
	                    </el-table-column>
	                    <el-table-column prop="assignType" label="绑定类型" width="90" >
	                        <template #default="scope">
	                            <div style="display: flex; align-items: center">
	                              <span v-if="scope.row.assignType == 'M'">主对象</span>
	                              <span v-if="scope.row.assignType == 'S'">从对象</span>
	                            </div>
	                        </template>
	                    </el-table-column>
	                    <el-table-column prop="keyAttrId" label="关键属性" width="130">
	                        <template #default="scope">
	                            <div style="display: flex; align-items: center">
	                              <span v-if="scope.row.keyAttribute != null">{{scope.row.objectType == 'S' ? scope.row.keyAttribute.content.code : scope.row.keyAttribute.code}}</span>
	                              <span v-if="scope.row.keyAttribute == null">{{scope.row.keyAttrId}}</span>
	                            </div>
	                        </template>
	                    </el-table-column>
	                    <el-table-column prop="relAttrId" label="关联属性" width="130">
                            <template #default="scope">
                                <div style="display: flex; align-items: center">
                                  <span v-if="scope.row.relAttribute != null">{{scope.row.relAttribute.code}}</span>
                                  <span v-if="scope.row.relAttribute == null">{{scope.row.relAttrId}}</span>
                                </div>
                            </template>
                        </el-table-column>
	                    <el-table-column prop="mainRelAttrId" label="主对象关联属性" width="140" >
	                        <template #default="scope">
	                            <div style="display: flex; align-items: center">
	                              <span v-if="scope.row.mainRelAttribute != null">{{scope.row.mainRelAttribute.code}}</span>
	                              <span v-if="scope.row.mainRelAttribute == null">{{scope.row.mainRelAttrId}}</span>
	                            </div>
	                        </template>
	                    </el-table-column>
	                    <el-table-column prop="displayOrder" label="显示顺序"/>
	            </el-table>
	        </el-col>
	    </el-row>
	    
	    <!-- 功能区域信息 -->
	    <br/><br/><br/>
	    <el-row :gutter="4" v-if="newFuncDefine != null && newFuncDefine.funcTemplate != null && newFuncDefine.funcTemplate.templateType != 'C'">
	        <el-col :span="1"></el-col>
	        <el-col :span="23">
	            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
	            <div class="toolbar1">
	                <el-icon><histogram /></el-icon><span>功能区域</span>&nbsp;&nbsp;&nbsp;&nbsp;
	            </div>
	            </el-divider>
	        </el-col>
	    </el-row>
	    <el-row :gutter="4" v-if="newFuncDefine != null && newFuncDefine.funcTemplate != null && newFuncDefine.funcTemplate.templateType != 'C'">
	        <el-col :span="1"/>
	        <el-col :span="23">
	            <el-table :data="newFuncDefine.funcRegions" stripe style="width: 100%">
	                <el-table-column prop="name" label="区域名称" width="180" />
	                <el-table-column prop="tplRegionId" label="区域模板" width="420" >
	                        <template #default="scope">
	                            <div style="display: flex; align-items: center">
	                              <span v-if="scope.row.tplRegion != null">{{scope.row.tplRegionId}}({{scope.row.tplRegion.name}})</span>
	                              <span v-if="scope.row.tplRegion == null">{{scope.row.tplRegionId}}</span>
	                            </div>
	                        </template>
	                </el-table-column>
	            </el-table>
	        </el-col>
	    </el-row>

        <el-row :gutter="4" v-if="newFuncDefine != null && newFuncDefine.funcTemplate != null && newFuncDefine.funcTemplate.templateType == 'C'">
	        <el-col :span="1"></el-col>
	        <el-col :span="23">
	            <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
	            <div class="toolbar1">
	                <el-icon><histogram /></el-icon><span>功能自定义页面</span>&nbsp;&nbsp;&nbsp;&nbsp;
	            </div>
	            </el-divider>
	        </el-col>
	    </el-row>
	    <el-row :gutter="4" v-if="newFuncDefine != null && newFuncDefine.funcTemplate != null && newFuncDefine.funcTemplate.templateType == 'C'">
	        <el-col :span="1"/>
	        <el-col :span="23">
	            <el-table :data="newFuncDefine.customizedPages" stripe style="width: 100%">
	                <el-table-column prop="name" label="页面名称" width="180" />
	                <el-table-column prop="isMainPage" label="是否主页面" width="220" >
                        <template #default="scope">
                            <div style="display: flex; align-items: center">
                              <span v-if="scope.row.isMainPage == 'Y'">是</span>
                              <span v-else-if="scope.row.isMainPage == 'N'">否</span>
                              <span v-else>{{ scope.row.isMainPage }}</span>
                            </div>
                        </template>
                    </el-table-column>
	            </el-table>
	        </el-col>
	    </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideFuncDefineCreatePage()">取消</el-button>
            <el-button type="primary" @click="funcDefineCreateBack()">上一步</el-button>
            <el-button type="primary" @click="createFuncDefine()">确定</el-button>
        </span>
    </template>
</el-dialog>

<!-- 新建对象绑定信息 -->
<createObjectAssign v-if="showObjectAssignCreate==true && newObjectAssign != null" :showObjectAssignCreate="showObjectAssignCreate" :newObjectAssign="newObjectAssign" :dbSchemaId="dbSchemaId" :mainObjectAssign="mainObjectAssign" @updateNewObjectAssignToList="updateNewObjectAssignToList" @hideObjectAssignCreatePage="hideObjectAssignCreatePage"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'

import createObjectAssign from './workbench-funcObjectAssignSet.vue'

export default {
    props: ['newFuncDefine','showFuncDefineCreate', 'businessDomains', 'dbSchemas'],
    
    emits: ['addNewFuncDefineToList', 'hideFuncDefineCreatePage', 'funcDefineCreateBack'],
    
    setup (props, {attrs, emit, slots}) {
        const addNewFuncDefineToList = (newFuncDefine)=> {
            emit('addNewFuncDefineToList', newFuncDefine);
        };
        
        const hideFuncDefineCreatePage = () => {
            emit('hideFuncDefineCreatePage');	
        };
        
        const funcDefineCreateBack = () => {
            emit('funcDefineCreateBack'); 
        };
        
        return {
        	addNewFuncDefineToList,
        	hideFuncDefineCreatePage,
        	funcDefineCreateBack
        };
    },
    computed: {
        showFlag: {
            get() {
                return this.showFuncDefineCreate;
            },
            set(value) {
                this.hideFuncDefineCreatePage();
            }
        }
    },
    components: {
    	createObjectAssign
    },
    data() {            
        const funcTemplates = ref([]);
        
        const createMode = ref("D");       
        const generateLevel = ref("A");
        const funcPattern = ref("D");
        const businessDomainId = ref("");
        const dbSchemaId = ref("");
        const ignorePrefixString = ref("");
        
        const showObjectAssignCreate = ref(false);
        const newObjectAssign = ref(null);
        const mainObjectAssign = ref(null);

        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "templateId": [
                { required: true, message: '功能模板不能为空！', trigger: 'blur' }
            ],
            "domainId": [
                { required: true, message: '归属业务域不能为空！', trigger: 'blur' }
            ]
        });
        
        return {
        	funcTemplates,
        	createMode,
        	generateLevel,
        	funcPattern,
        	businessDomainId,
        	ignorePrefixString,
        	dbSchemaId,
        	
        	showObjectAssignCreate,
            newObjectAssign,
            mainObjectAssign,

            validateRules
        }
    },
    watch: {
    	'newFuncDefine.templateId': function(val, old){
            if(val == null) {
                return;
            }
            this.funcTemplateChanged(val);
        },
        'createMode': function(val, old){
            if(val == null) {
                return;
            }
            this.newFuncDefine.objectAssigns = [];
        }
    },
    mounted() {
    	var url = "/lcdp-proxy/lowcode/platform/fe/api/funcTemplate/queryAll";
        axiosClient.get(url).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.funcTemplates = data.data;
                this.funcTemplateChanged(data.data[0].id);
            }
        })
    },
    methods: {
    	funcTemplateChanged(templateId) {
    		this.newFuncDefine.templateId = templateId;
    		this.newFuncDefine.funcTemplate = null;
    		if(this.funcTemplates != null) {
    			for(var i in this.funcTemplates) {
    				if(this.funcTemplates[i].id == templateId) {
    					this.newFuncDefine.funcTemplate = this.funcTemplates[i];
    					break;
    				}
    			}
    		}
    		if(this.newFuncDefine.funcTemplate != null) {
                if(this.newFuncDefine.funcTemplate.templateType == 'R' || this.newFuncDefine.funcTemplate.templateType == 'V'){
                    this.createMode = "B";
                }else {
                    if(this.createMode == null || this.createMode == '' || this.createMode == 'S' || this.createMode == 'B') {
                        this.createMode = "D";
                    }
                }
    			this.newFuncDefine.funcRegions = [];
                this.newFuncDefine.customizedPages = [];
    			if(this.newFuncDefine.funcTemplate.funcTemplateComposites != null) {
    				for(var i in this.newFuncDefine.funcTemplate.funcTemplateComposites) {
    					var regionTpl = this.newFuncDefine.funcTemplate.funcTemplateComposites[i].funcTemplateRegion; 
                        if(this.newFuncDefine.funcTemplate.templateType == 'C') {//自定义功能
                            if(regionTpl.regionType != 'CM') {//只自动生成主页面
                                continue;
                            }
                            var customizedPage = {
                                id: "",
                                name: regionTpl.name,
                                funcId: "",
                                pageCfg: null,
                                isMainPage: "Y",
                                systemId: this.newFuncDefine.systemId,
                                tenantId: this.newFuncDefine.tenantId,
                                createTime: "",
                                updateTime: ""
                            }
                            this.newFuncDefine.customizedPages.push(customizedPage);
                        }else {//功能模板
                            var funcRegion = {
                                id: "",
                                name: regionTpl.name,
                                funcId: "",
                                tplCompositeId: this.newFuncDefine.funcTemplate.funcTemplateComposites[i].id,
                                tplRegionId: regionTpl.id,
                                systemId: this.newFuncDefine.systemId,
                                tenantId: this.newFuncDefine.tenantId,
                                createTime: "",
                                updateTime: "",
                                tplRegion: regionTpl
                            }
                            this.newFuncDefine.funcRegions.push(funcRegion);
                        }
    					
    				}
    			}
    		}
    	},
    	createFuncDefine() {
    		if(this.newFuncDefine.objectAssigns == null || this.newFuncDefine.objectAssigns.length ==0) {
    			ElMessage.error(`未设置绑定对象！`);
    			return;
    		}
    		var mainObjectCount = 0;
            for(var i in this.newFuncDefine.objectAssigns) {
    			if(this.newFuncDefine.objectAssigns[i].assignType == 'M') {
    				mainObjectCount++;
    			}
    		}
            if(mainObjectCount != 1) {
            	ElMessage.error(`有且只能设置一个主绑定对象！`);
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/fe/api/funcDefine/generateAll";
            var funcDefineGenerate = {
            	createMode : this.createMode,
                generateLevel : this.generateLevel,
                funcPattern : this.funcPattern,
                businessDomainId : this.businessDomainId,
                ignorePrefixString : this.ignorePrefixString,
                dbSchemaId : this.dbSchemaId,
                newFuncDefine : this.newFuncDefine
            }
            this.$refs.newFuncDefineForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,funcDefineGenerate).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data.id != null) {
                            ElMessage(`新增系统功能成功！请刷新工作台！`);
                            this.addNewFuncDefineToList(data.data);
                        }
                    }).catch((error)=> {
                        ElMessage.error(`新增系统功能失败:` + error);
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        showObjectAssignCreatePage() {
        	var objectType = "D";
        	if(this.createMode == 'T') {
        		if(this.dbSchemaId == null || this.dbSchemaId == "" || this.businessDomainId == null || this.businessDomainId == "") {
                    ElMessage(`请先选择业务域和数据库！`);
                    return;
                }
        		objectType = "T";
        	}else if(this.createMode == 'B') {
                objectType = "B";
            }else if(this.createMode == 'V') {
                objectType = "V";
            }

            if(this.newFuncDefine.funcTemplate != null && this.newFuncDefine.funcTemplate.templateType == 'R' &&
                this.newFuncDefine.objectAssigns != null && this.newFuncDefine.objectAssigns.length > 0 ) {
                ElMessage(`统计报表功能模板只允许绑定一个数据集！`);
                return;
            }
        	
            this.newObjectAssign =  {
                id : null,
                funcId : this.newFuncDefine.id,
                objectType : objectType,
                objectId : "",
                assignType : "M",
                keyAttrId : null,
                relAttrId : null,
                mainRelAttrId : null,
                assignObject : {},
                systemId : this.newFuncDefine.systemId,
                tenantId : this.newFuncDefine.tenantId
            };
            if(this.newFuncDefine.objectAssigns != null && this.newFuncDefine.objectAssigns.length > 0) {
                for(var i in this.newFuncDefine.objectAssigns) {
                	if(this.newFuncDefine.objectAssigns[i].assignType == 'M') {
                		this.mainObjectAssign = this.newFuncDefine.objectAssigns[i];
                		break;
                	}
                }
            }
            this.showObjectAssignCreate = true;
        },
        updateNewObjectAssignToList(data) {
        	if(this.newFuncDefine.objectAssigns == null || this.newFuncDefine.objectAssigns.length == 0) {
                this.newFuncDefine.objectAssigns = [];
            }
            this.newFuncDefine.objectAssigns.push(data);
            this.newFuncDefine.objectAssigns.sort((a,b)=>{
                return a.displayOrder-b.displayOrder;
            });
            this.newObjectAssign = null;
            this.showObjectAssignCreate = false;
        },
        hideObjectAssignCreatePage() {
            this.newObjectAssign = null;
            this.showObjectAssignCreate = false;
        },
        deleteObjectAssign(index) {
        	ElMessageBox.confirm(
                    '该绑定对象将被删除. 是否继续?',
                    '警告',
                    {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning',
                    }
                 ).then(() => {
                	 if(this.newFuncDefine.objectAssigns != null && this.newFuncDefine.objectAssigns.length > 0) {
                         for(var i in this.newFuncDefine.objectAssigns) {
                             if(i == index) {
                                 this.newFuncDefine.objectAssigns.splice(i,1);
                                 break;
                             }
                         }
                     }
                 }).catch(()=>{});
        },
    }
}
</script>