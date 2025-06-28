<!-- 方法编辑页 -->
 <template>
<el-dialog v-model="showFlag" width="1000px" append-to-body>
    <template #header>
        <span class="title">方法编辑</span>
    </template>
    <template #default>
        <div>
            <el-form v-if="operationInfo" :model="operationInfo" label-width="120px" :rules="validateRules" ref="operationInfoForm">
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="标识" required prop="operation.id">
                            <el-input v-model="operationInfo.operation.id" readonly  size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="编码" required prop="operation.code">
                            <el-input v-model="operationInfo.operation.code"  size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="名称" required prop="operation.name">
                            <el-input v-model=operationInfo.operation.name  size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="状态" required prop="operation.status">
                            <el-select v-model="operationInfo.operation.status" disabled class="m-2" placeholder="Select" size="small">
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
                        <el-form-item label="业务流标识" required prop="operation.businessFlowId">
                            <el-input v-model="operationInfo.operation.businessFlowId" readonly  size="small"/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        
                    </el-col>
                </el-row>
               
               <el-row :gutter="4">
                    <el-col :span="24">&nbsp;</el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 12px">
                        <div class="toolbar1">
                            <span class="title">参数</span>&nbsp;&nbsp;&nbsp;&nbsp;
                            <el-link  type="primary" @click="showParameterCreatePage()">新建</el-link>
                        </div>
                        </el-divider>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="operationInfo.parameters">
                    <el-col :span="1"/>
                    <el-col :span="23">
                        <el-table :data="operationInfo.parameters" stripe style="width: 100%">
                            <el-table-column  label="操作" width="95">
                                <template #default="scope">
                                    <el-link  type="primary" @click="showParameterEditPage(scope.row.id)">编辑</el-link>&nbsp;&nbsp;
                                    <el-link  type="primary" @click="deleteParameter(scope.row.id, scope.row.code)">删除</el-link>
                                </template>
                            </el-table-column>
                            <el-table-column prop="code" label="编码" width="220" />
                            <el-table-column prop="name" label="名称" width="140" />
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
                            <el-table-column prop="paramType" label="参数类型" >
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
                
                <el-row :gutter="4">
                    <el-col :span="24">&nbsp;</el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="24">&nbsp;</el-col>
                </el-row>
                <el-row :gutter="4" v-if="operationInfo.operation.objectType == 'S'">
                    <el-col :span="24">
                        <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 12px">
                        <div class="toolbar1">
                            <el-icon><histogram /></el-icon><span>入参校验规则设置</span>
                        </div>
                        </el-divider>
                    </el-col>
                </el-row>
                <el-row :gutter="4" v-if="operationInfo.operation.objectType == 'S'">
			        <el-col :span="12">
			            <el-form-item label="入参校验规则">
			                <el-input type="hidden" v-model="operationInfo.operation.ruleGroupId" readonly  size="small" />
			                <el-input v-model="ruleGroupName" readonly  size="small" />
			            </el-form-item>
			        </el-col>
			        <el-col :span="12">
			            <el-form-item label="" label-width="10px">
			                <el-link  type="primary" @click="selectRuleGroup()" ><label>选择</label></el-link>&nbsp;
			                <el-link  type="primary" @click="clearRuleGroup()" ><label>清空</label></el-link>                                                
			            </el-form-item>
			        </el-col>
			    </el-row>
            </el-form>
        </div>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="cancelOperationClick">取消</el-button>
        <el-button type="primary" @click="saveOperation">保存</el-button>
      </div>
    </template>
</el-dialog>

<!-- 参数新建 -->
<createParameter v-if="showParameterCreate==true && newParameter != null" :showParameterCreate="showParameterCreate" :newParameter="newParameter" @checkOutParam="checkOutParam" @hideNewParamPage="hideNewParamPage" @addNewParameterToList="addNewParameterToList"/>
<!-- 参数编辑 -->
<editParameter v-if="showParameterEdit==true && editParameter != null" :showParameterEdit="showParameterEdit" :editParameter="editParameter" @checkOutParam="checkOutParam" @hideEditParamPage="hideEditParamPage" @updateParameterToList="updateParameterToList"/>
<!-- 选择校验规则组信息 -->
<ruleGroupSelect v-if="showRuleGroupSelect"  @ruleGroupSelected="ruleGroupSelected" @hideRuleGroupSelectPage="hideRuleGroupSelectPage" :showRuleGroupSelect="showRuleGroupSelect"  :ruleGroupsForSelect="ruleGroupsForSelect" :systemId="operationInfo.operation.systemId" objectType="M" :objectId="operationInfo.operation.id" :tenantId="operationInfo.operation.tenantId"/>

</template>


<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import createParameter from './workbench-parameterCreate.vue'
import editParameter from './workbench-parameterEdit.vue'
import ruleGroupSelect from './workbench-ruleGroupSelect.vue'


export default {
	props: ['operationInfo','showOperationEdit'],
	
	emits: ['hideEditOperationPage', 'updateOperationToList'],
	
	components: {
		createParameter,
		editParameter,
		ruleGroupSelect
	},
	
	setup (props, {attrs, emit, slots}) {
		const cancelEditOperation = ()=> {
	    	emit('hideEditOperationPage');
	    };
	    
	    const updateOperationToList = (updateOperation)=> {
            emit('updateOperationToList', updateOperation);
        };
	    
	    return {
	    	cancelEditOperation,
	    	updateOperationToList
	    };
	},
	computed: {
        showFlag: {
            get() {
                return this.showOperationEdit;
            },
            set(value) {
                this.cancelEditOperation();
            }
        }
    },
    data() {    		
		
		const statusOptions = ref(null);
		
		const showParameterCreate = ref(false);
		const showParameterEdit = ref(false);

		const newParameter = ref({});
		const editParameter = ref({});
		
		const showRuleGroupSelect = ref(false);
        const ruleGroupsForSelect = ref(null);
		const ruleGroupName = ref(null);

        const validateRules = ref({
            "operation.id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "operation.code": [
                { required: true, message: '编码不能为空！', trigger: 'blur' }
            ],
            "operation.name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "operation.businessFlowId": [
                { required: true, message: '业务流标识不能为空！', trigger: 'blur' }
            ],
            "operation.status": [
                { required: true, message: '状态不能为空！', trigger: 'blur' }
            ]
        });
		
        return {
        	statusOptions,
        	showParameterCreate,
        	showParameterEdit,
        	newParameter,
        	editParameter,
        	
        	showRuleGroupSelect,
        	ruleGroupsForSelect,
        	ruleGroupName,
            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_STATUS']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.statusOptions = data.data['PUB_STATUS'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	if(this.operationInfo.operation.ruleGroupId != null && this.operationInfo.operation.ruleGroupId != "") {
            var queryCodeUrl = "/lcdp-proxy/lowcode/platform/be/api/validateRuleGroup/queryName/" + this.operationInfo.operation.ruleGroupId;
            axiosClient.get(queryCodeUrl).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.ruleGroupName = data.data;
                }
            });
        }	
    },
    methods: {
    	cancelOperationClick() {
    		this.cancelEditOperation();
        },     
        saveOperation() {
            this.$refs.operationInfoForm.validate((valid, fields)=> {
                if(valid) {
                    var url = "/lcdp-proxy/lowcode/platform/be/api/operation/save";
                    axiosClient.post(url, this.operationInfo.operation).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                            ElMessage(`更新方法信息成功，共更新(`+data.data+`)条属性`);
                            this.updateOperationToList(this.operationInfo.operation);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        showParameterCreatePage() {
        	this.newParameter = {
                id:"",
                code:"",
                name:"",
                isInParam: "Y",
                isListParam:"N",
                paramType:"J",
                paramClass:"",
                displayOrder:"",
                operationId: this.operationInfo.operation.id,
                systemId: this.operationInfo.operation.systemId,
                tenantId: this.operationInfo.operation.tenantId,
                paramClassObject: {}
            };
        	
            this.showParameterCreate = true;
        },
        checkOutParam(newParam, callback) {
        	var valid = true;
            if(newParam.isInParam == "N" && this.operationInfo.parameters != null && this.operationInfo.parameters.length > 0) {
                this.operationInfo.parameters.forEach((item)=> {
                    if(item.isInParam == "N" && item.id != newParam.id) {
                        valid = false;
                        return ;
                    }
                });
            }
            callback(valid);
        },
        addNewParameterToList(newParam) {
        	var parameterList = this.operationInfo.parameters;
            if(parameterList == null) {
                parameterList = [];
                this.operationInfo.parameters = parameterList;
            }
            parameterList.push(newParam);
            this.operationInfo.parameters.sort((a,b)=>{return a.displayOrder-b.displayOrder});
            this.showParameterCreate = false;
        },
        showParameterEditPage(paramId) {
            if(this.operationInfo.parameters != null && this.operationInfo.parameters.length > 0) {
            	this.operationInfo.parameters.forEach((item)=> {
                    if(item.id == paramId) {
                        this.editParameter = {
                                id:item.id,
                                code:item.code,
                                name:item.name,
                                isInParam: item.isInParam,
                                isListParam:item.isListParam,
                                paramType:item.paramType,
                                paramClass:item.paramClass,
                                displayOrder:item.displayOrder,
                                operationId: item.operationId,
                                systemId: item.systemId,
                                tenantId: item.tenantId,
                                createTime: item.createTime,
                                updateTime: item.updateTime,
                                paramClassObject:item.paramClassObject
                            };
                        this.showParameterEdit = true;
                        return;
                    }
                });
            }
        },
        updateParameterToList(updateParam) {
        	console.log(updateParam);
        	if(this.operationInfo.parameters != null && this.operationInfo.parameters.length > 0) {
        		this.operationInfo.parameters.forEach((item)=> {
                    if(item.id == updateParam.id) {                                
                        item.code = updateParam.code,
                        item.name = updateParam.name,
                        item.isInParam = updateParam.isInParam,
                        item.isListParam = updateParam.isListParam,
                        item.paramType = updateParam.paramType,
                        item.paramClass = updateParam.paramClass,
                        item.displayOrder = updateParam.displayOrder,
                        item.operationId = updateParam.operationId,
                        item.systemId = updateParam.systemId,
                        item.tenantId = updateParam.tenantId,
                        item.createTime = updateParam.createTime,
                        item.updateTime = updateParam.updateTime,
                        item.paramClassObject = updateParam.paramClassObject
                        this.showParameterEdit = false;
                        return;
                    }
                });
        		this.operationInfo.parameters.sort((a,b)=>{return a.displayOrder-b.displayOrder});
            }
        },
        hideNewParamPage() {
        	this.showParameterCreate = false;
        	this.newParameter = null;
        },
        hideEditParamPage() {
            this.showParameterEdit = false;
            this.editParameter = null;
        },
        deleteParameter(paramId, paramCode) {
        	ElMessageBox.confirm(
        		    '该参数(' + paramCode + ')将被删除. 是否继续?',
        		    '警告',
        		    {
        		      confirmButtonText: '确定',
        		      cancelButtonText: '取消',
        		      type: 'warning',
        		    }
        		  ).then(() => {
        			  var url = "/lcdp-proxy/lowcode/platform/be/api/parameter/delete/" + paramId;
                      axiosClient.post(url).then((response) => {
                          var data = response.data; 
                          if(data != null && data.status == "200" && data.data != null && data.data > 0) {
                              if(this.operationInfo.parameters != null && this.operationInfo.parameters.length > 0) {
                            	  for(var i in  this.operationInfo.parameters) {
                            		  if( this.operationInfo.parameters[i].id == paramId) {
                            			  this.operationInfo.parameters.splice(i,1);
                            			  break;
                            		  }
                            	  }
                              }
                              ElMessage(`删除参数(`+paramCode+`)成功。`);
                          }
                      }).catch(()=>{
                    	  ElMessage.error(`删除参数(`+paramCode+`)失败！`);
                      });
        		  }).catch(()=>{
                  });
        },
        selectRuleGroup() {
        	var hasInParams = false;
        	if(this.operationInfo.parameters != null && this.operationInfo.parameters.length > 0) {
                for(var i in  this.operationInfo.parameters) {
                    if( this.operationInfo.parameters[i].isInParam == 'Y') {
                    	hasInParams = true;
                        break;
                    }
                }
            }
        	if(!hasInParams) {
        		ElMessage(`还未配置输入参数！`);
        		return;
        	}
            var url = "/lcdp-proxy/lowcode/platform/be/api/validateRuleGroup/listByObject/" + this.operationInfo.operation.systemId + "/M/" + this.operationInfo.operation.id;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.ruleGroupsForSelect = data.data;
                    this.showRuleGroupSelect = true;
                }
            });
        },
        clearRuleGroup(){
            this.operationInfo.operation.ruleGroupId = "";
            this.ruleGroupName = "";
        },
        ruleGroupSelected(obj) {
            this.operationInfo.operation.ruleGroupId = obj.id;
            this.ruleGroupName = obj.name;
            this.ruleGroupsForSelect = null;
            this.showRuleGroupSelect = false;
        },
        hideRuleGroupSelectPage(){
            this.ruleGroupsForSelect = null;
            this.showRuleGroupSelect = false;
       }
    }
}

</script>