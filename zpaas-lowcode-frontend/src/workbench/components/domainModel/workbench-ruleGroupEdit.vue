<!-- 校验规则组编辑页 -->
 <template>
<el-dialog v-model="showFlag" width="900px" append-to-body>
    <template #header>
        <span class="title">校验规则组编辑</span>
    </template>
    <template #default>
        <div>
        <el-scrollbar>
            <el-form v-if="showRuleGroupEdit ==true && ruleGroupInfo != null" :model="ruleGroupInfo" label-width="120px" :rules="validateRules" ref="ruleGroupInfoForm">
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="标识">
                            <el-input v-model="ruleGroupInfo.id"  size="small" readonly/>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="名称" required prop="name">
                            <el-input v-model="ruleGroupInfo.name"  size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="12">
                        <el-form-item label="校验对象类型" required prop="objectType">
                            <span v-if="ruleGroupInfo.objectType == 'D'">领域对象</span>
                            <span v-if="ruleGroupInfo.objectType == 'V'">值传递对象</span>
                            <span v-if="ruleGroupInfo.objectType == 'M'">服务方法</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="校验对象"  required prop="objectId">
                            <el-input v-model="validateObject.code"  size="small"/>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-form-item label="子校验规则组">
                            <el-input v-model="ruleGroupInfo.subRuleGroups" type="textarea" rows="3" placeholder='{"attributeCode1":"ruleGroupId1", "attributeCode2":"ruleGroupId2"}' size="small" />
                        </el-form-item>
                    </el-col>
                </el-row>
                <br/>
                
                <el-row :gutter="4">
                    <el-col :span="24">
                        <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 12px">
                        <div class="toolbar1">
                            <span class="title">校验规则</span>&nbsp;&nbsp;&nbsp;&nbsp;
                        </div>
                        </el-divider>
                    </el-col>
                </el-row>
                
                <el-row :gutter="4">
                    <el-col :span="2"></el-col>
                    <el-col :span="20">
		                <el-row :gutter="4" v-if="ruleGroupInfo.objectType != 'M'">
		                    <el-col :span="12">
		                        <el-form-item label="校验属性">
		                            <el-select v-model="validateAttr" class="m-2" placeholder="Select" size="small">
		                                <el-option v-for="item in validateObject.attributes" :key="item.id" :label="item.code" :value="item.id" />
		                            </el-select>
		                        </el-form-item>
		                    </el-col>
		                    <el-col :span="12">
		                        
		                    </el-col>
		                </el-row>
		                <el-row :gutter="4" v-if="ruleGroupInfo.objectType == 'M'">
                            <el-col :span="12">
                                <el-form-item label="校验属性">
                                    <el-select v-model="validateAttr" class="m-2" placeholder="Select" size="small" clearable>
                                        <el-option v-for="(value, key) in validateObject.inParams" :key="key" :label="key" :value="key" />
                                    </el-select>
                                </el-form-item>
                            </el-col>
                            <el-col :span="12" v-if="validateAttr != null && validateObject.inParams != null && validateObject.inParams[validateAttr] != null && Array.isArray(validateObject.inParams[validateAttr])">
                                <el-form-item label="子属性">
                                    <el-select v-model="subValidateAttr" class="m-2" placeholder="Select" size="small" clearable>
                                        <el-option v-for="item in validateObject.inParams[validateAttr]" :key="item.id" :label="item.code" :value="item.id" />
                                    </el-select>
                                </el-form-item>
                            </el-col>
                        </el-row>
                        <el-row :gutter="4">
                            <el-col :span="12">
                                <el-form-item label="校验规则类型">
                                    <el-select v-model="validateRuleType" class="m-2" placeholder="Select" size="small" clearable>
                                        <el-option v-for="item in validateRuleTypes" :key="item.id" :label="item.name" :value="item.id" />
                                    </el-select>
                                </el-form-item>
                            </el-col>
                            <el-col :span="12"></el-col>
                        </el-row>
                            
                            <!-- 空值校验规则 -->
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.EmptyValidator'">
                                <el-col :span="12">
                                    <el-form-item label=" ">
			                            <el-checkbox v-model="validateRuleValue.unNull" label="不能为null" />
		                            </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    <el-form-item label=" ">
                                        <el-checkbox v-model="validateRuleValue.unEmpty" label="不能为空" />
                                    </el-form-item>
                                </el-col>
                            </el-row>

                            <!-- 自定义校验规则 -->
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.CustomizedValidator'">
                                <el-col :span="24">
                                    <el-form-item label="实现类">
			                            <el-input v-model="validateRuleValue.validatorClass"  size="small"/>
		                            </el-form-item>
                                </el-col>
                                <el-col :span="24">
                                    <el-form-item label="校验配置信息2">
			                            <el-input v-model="ruleValue2" type="textarea" rows="3"  size="small"/>
		                            </el-form-item>
                                </el-col>
                                <el-col :span="24">
                                    <el-form-item label="校验配置信息3">
			                            <el-input v-model="ruleValue3" type="textarea" rows="3" size="small"/>
		                            </el-form-item>
                                </el-col>
                                
                            </el-row>
                            
                            <!-- 范围校验规则 -->
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.RangeValidator'">
                                <el-col :span="12">
                                    <el-form-item label="起始值">
                                        <el-input v-model="validateRuleValue.startValue"  size="small"/>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    <el-form-item label="终止值">
                                        <el-input v-model="validateRuleValue.endValue"  size="small"/>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            
                            <!-- 长度校验规则 -->
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.LengthValidator'">
                                <el-col :span="12">
                                    <el-form-item label="最小长度">
                                        <el-input v-model="validateRuleValue.minLength"  size="small"/>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    <el-form-item label="最大长度">
                                        <el-input v-model="validateRuleValue.maxLength"  size="small"/>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            
                            <!-- 类型校验规则 -->
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.TypeValidator'">
                                <el-col :span="12">
                                    <el-form-item label="属性类型">
	                                    <el-select v-model="validateRuleValue.attrType" class="m-2" placeholder="Select" size="small">
	                                        <el-option v-for="item in primitiveJavaTypes" :key="item.value" :label="item.label" :value="item.value" />
	                                    </el-select>
	                                </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    &nbsp;
                                </el-col>
                            </el-row>
                            
                            <!-- 正则表达式校验规则 -->
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.RegExprValidator'">
                                <el-col :span="12">
                                    <el-form-item label="正则表达式">
                                        <el-input v-model="validateRuleValue.regExpr" type="textarea" rows="3" size="small"/>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    &nbsp;
                                </el-col>
                            </el-row>
                            
                            <!-- SpringEL表达式校验规则 -->
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.SpringELValidator'">
                                <el-col :span="24">
                                    <el-form-item label="SpringEL表达式">
                                        <el-input v-model="validateRuleValue.springELExpr" type="textarea" rows="3" size="small"/>
                                    </el-form-item>
                                </el-col>
                            </el-row>

                            <!-- Sql校验规则 -->
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.SqlValidator'">
                                <el-col :span="24">
                                    <el-form-item label="校验语句">
                                        <el-input v-model="validateRuleValue.validateSql" type="textarea" rows="2" size="small"/>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.SqlValidator'">
                                <el-col :span="12">
                                    <el-form-item label="条件列表">
                                        <el-input v-model="validateRuleValue.conditions"  size="small" placeholder='["/","$.orderNumber"]'/>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    <el-form-item label="数据源">
                                        <el-input v-model="validateRuleValue.datasource"  size="small"/>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.SqlValidator'">
                                <el-col :span="24">
                                    <el-form-item label="SpringEL表达式">
                                        <el-input v-model="validateRuleValue.springELExpr" type="textarea" rows="2" size="small" placeholder="#{#value  &&  #srcObject.get('code')}"/>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            
                            <!-- 可选值校验规则 -->
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.OptionValidator'">
                                <el-col :span="12">
                                    <el-form-item label="指定可选值">
                                        <el-input v-model="validateRuleValue.optionValues" size="small" placeholder='["1","2","3"]'/>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    <el-form-item label="指定字典编码">
                                        <el-input v-model="validateRuleValue.optionDictName"  size="small"/>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.OptionValidator'">
                                <el-col :span="16">
                                    <el-form-item label="指定可选值SQL">
                                        <el-input v-model="validateRuleValue.optionSql"  size="small"/>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="8">
                                    <el-form-item label=" ">
                                        <el-select v-model="validateRuleValue.optionDB" class="m-2" placeholder="Select" size="small">
                                            <el-option v-for="item in dbSchemas" :key="item.id" :label="item.name" :value="item.id" />
                                        </el-select>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            
                            <!-- 日期格式校验规则 -->
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.DateValidator'">
                                <el-col :span="12">
                                    <el-form-item label="日期格式">
                                        <el-input v-model="validateRuleValue.dateFormat"  size="small"/>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    <el-form-item label="相对日期">
                                        <el-input v-model="validateRuleValue.relativeDate"  size="small"/>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.DateValidator'">
                                
                                <el-col :span="8">
                                    <el-form-item label="校验类型">
                                        <el-select v-model="validateRuleValue.validateType" class="m-2" placeholder="Select" size="small">
                                            <el-option v-for="item in dateValidateTypes" :key="item.value" :label="item.label" :value="item.value" />
                                        </el-select>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="8">
                                    <el-form-item label="配置值1">
                                        <el-input v-model="validateRuleValue.validateValue1"  size="small"/>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="8">
                                    <el-form-item label="配置值2">
                                        <el-input v-model="validateRuleValue.alidateValue2"  size="small"/>
                                    </el-form-item>
                                </el-col>
                            </el-row>

                            <!-- IP地址校验规则 -->
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.IpAddrValidator'">
                                <el-col :span="12">
                                    <el-form-item label="IP类型">
                                        <el-select v-model="validateRuleValue.ipType" class="m-2" placeholder="Select" size="small">
                                            <el-option v-for="item in ipTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                                        </el-select>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    <el-form-item label=" ">
                                        <el-checkbox v-model="validateRuleValue.isCidr" label="是否CIDR" />
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.IpAddrValidator'">
                                <el-col :span="12">
                                    <el-form-item label=" ">
                                        <el-checkbox v-model="validateRuleValue.isMultiIp" label="是否多IP" />
                                    </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    <el-form-item label="分隔符">
                                        <el-input v-model="validateRuleValue.splitFlag" placeholder="默认是逗号','" size="small"/>
                                    </el-form-item>
                                </el-col>
                            </el-row>

                            <!-- URL地址校验规则 -->
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.UrlValidator'">
                                <el-col :span="12">
                                    <el-form-item label="URL类型">
                                        <el-select v-model="validateRuleValue.urlType" class="m-2" placeholder="Select" size="small">
                                            <el-option v-for="item in urlTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
                                        </el-select>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    <el-form-item label=" ">
                                        <el-checkbox v-model="validateRuleValue.isMultiUrl" label="是否允许多个" />
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row :gutter="4" v-if="currentValidateRuleType == 'cn.zpaas.lowcode.be.engine.ability.validator.UrlValidator'">
                                <el-col :span="12">
                                    <el-form-item label="分隔符">
                                        <el-input v-model="validateRuleValue.splitFlag" placeholder="默认是逗号','" size="small"/>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    
                                </el-col>
                            </el-row>
                            
                            <!-- 公共属性 -->
                            <el-row :gutter="4">
                                <el-col :span="12">
                                    <el-form-item label="错误码">
                                        <el-input v-model="validateRuleValue.errorCode"  size="small"/>
                                    </el-form-item>
                                </el-col>
                                <el-col :span="12">
                                    <el-form-item label=" ">
                                        <el-checkbox v-model="validateRuleValue.interrupt" label="是否中断" />
                                    </el-form-item>
                                </el-col>
                            </el-row>
                            <el-row :gutter="4">
                                <el-col :span="24">
                                    <el-form-item label="提示信息">
                                        <el-input v-model="validateRuleValue.message"  size="small"/>
                                    </el-form-item>
                                </el-col>
                            </el-row>
                    </el-col>  
                    <el-col :span="2"><el-link  type="primary" @click="addValidateRule()">添加</el-link></el-col>                 
                </el-row>
                
                
                
                <el-row :gutter="4" v-if="ruleGroupInfo.validateRules">
                    <el-col :span="1"/>
                    <el-col :span="23">
                        <el-table :data="ruleGroupInfo.validateRules" stripe style="width: 100%">
                            <el-table-column  label="操作" width="60">
                                <template #default="scope">
                                    <el-link  type="primary" @click="deleteValidateRule(scope.$index)">删除</el-link>
                                </template>
                            </el-table-column> 
                            <el-table-column prop="attributeCode" label="属性" width="180" />
                            <el-table-column prop="subAttributeCode" label="子属性" width="180" />
                            <el-table-column prop="validateRuleType.name" label="校验规则类型" width="140" />
                            <el-table-column prop="ruleValue1" label="校验配置信息" width="240" />
                            <el-table-column prop="ruleValue2" label="校验配置信息" width="240" />
                            <el-table-column prop="ruleValue3" label="校验配置信息" width="240" />
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
        </div>
    </template>
    <template #footer>
      <div style="flex: auto">
        <el-button @click="hideEditRuleGroupPage">取消</el-button>
        <el-button type="primary" @click="saveRuleGroup">保存</el-button>
      </div>
    </template>
</el-dialog>
</template>


<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'



export default {
	props: ['ruleGroupInfo','showRuleGroupEdit'],
	
	emits: ['hideEditRuleGroupPage', 'addNewRuleGroupToList'],
	
	setup (props, {attrs, emit, slots}) {
		const hideEditRuleGroupPage = ()=> {
	    	emit('hideEditRuleGroupPage');
	    };
	    
	    const addNewRuleGroupToList = (ruleGroup, isNew)=> {
            emit('addNewRuleGroupToList', ruleGroup, isNew);
        };
	    
	    return {
	    	hideEditRuleGroupPage,
	    	addNewRuleGroupToList
	    };
	},
	computed: {
        showFlag: {
            get() {
                return this.showRuleGroupEdit;
            },
            set(value) {
                this.hideEditRuleGroupPage();
            }
        }
    },
    data() {    	
		const validateRuleType = ref("");
		const validateAttr = ref("");
		const subValidateAttr = ref("");
		const validateRuleTypes = ref([]);
		const dbSchemas = ref([]);
		const validateObject = ref({});
		const validateRuleValue = ref({});
		const currentValidateRuleType = ref("");
        const ruleValue2 = ref("");
        const ruleValue3 = ref("");
		
		const primitiveJavaTypes = ref(null);
		
		const dateValidateTypes = ref(null);
		
        const ipTypeOptions = ref(null);

        const urlTypeOptions = ref(null);

        const validateRules = ref({
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "objectType": [
                { required: true, message: '校验对象类型不能为空！', trigger: 'blur' }
            ],
            "objectId": [
                { required: true, message: '校验对象不能为空！', trigger: 'blur' }
            ]
        });

        return {
        	validateRuleType,
        	validateAttr,
        	subValidateAttr,
        	validateRuleTypes,
        	dateValidateTypes,
        	dbSchemas,
        	validateObject,
        	validateRuleValue,
        	currentValidateRuleType,
        	primitiveJavaTypes,
            ruleValue2,
            ruleValue3,

            ipTypeOptions,
            urlTypeOptions,
            validateRules
        }
    },    
    watch: {        
        'validateRuleType': function(val, old){
        	this.validateRuleValue = {};
            if(val == null || val == "") {
            	this.currentValidateRuleType = "";
            }else {
            	if(this.validateRuleTypes != null && this.validateRuleTypes.length > 0) {
            		for(var i in this.validateRuleTypes) {
            			if(this.validateRuleTypes[i].id == val) {
            				this.currentValidateRuleType = this.validateRuleTypes[i].ruleClass;
            				break;
            			}
            		}
            	}
            }
            this.ruleValue2 = "";
            this.ruleValue3 = "";
        }
    
    },   
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_PRIMITIVE_JAVA_TYPE','DATE_VALIDATE_TYPE','VALIDATE_IP_TYPE','VALIDATE_URL_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.primitiveJavaTypes = data.data['PUB_PRIMITIVE_JAVA_TYPE'];
                this.dateValidateTypes = data.data['DATE_VALIDATE_TYPE'];
                this.ipTypeOptions = data.data['VALIDATE_IP_TYPE'];
                this.urlTypeOptions = data.data['VALIDATE_URL_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
        this.initValidateRuleGroupEdit();
    },   
    methods: {
    	initValidateRuleGroupEdit() {
    		var url = "/lcdp-proxy/lowcode/platform/be/api/validateRuleGroup/init";
            var initInfo = {
            	systemId: this.ruleGroupInfo.systemId,
            	tenantId: this.ruleGroupInfo.tenantId,
            	objectType: this.ruleGroupInfo.objectType,
            	objectId: this.ruleGroupInfo.objectId
            };
            axiosClient.post(url, initInfo).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.validateRuleTypes = data.data.validateRuleTypes;
                    this.dbSchemas = data.data.dbSchemas;
                    if("M" == this.ruleGroupInfo.objectType) {
                        data.data.operation.inParams = data.data.inParams
                        this.validateObject = data.data.operation;
                        this.ruleGroupInfo.object = data.data.operation;
                    }else {
                        this.validateObject = data.data.targetObject;
                        this.ruleGroupInfo.object = data.data.targetObject;
                    }
                    this.ruleGroupInfo.validateRules.sort((a,b)=>{ return a.attributeCode.localeCompare(b.attributeCode)});
                }
            });
    	},
    	saveRuleGroup() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/validateRuleGroup/save";
            var isNew = false;
            if(this.ruleGroupInfo.id == null || this.ruleGroupInfo.id == "") {
            	isNew = true;
            }
            this.$refs.ruleGroupInfoForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url, this.ruleGroupInfo).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            ElMessage(`校验规则组信息保存成功！`);
                            data.data.objectCode = this.validateObject.code;
                            this.addNewRuleGroupToList(data.data , isNew);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        addValidateRule() {
        	if(this.validateAttr == null || this.validateAttr == "" || this.validateRuleType == null || this.validateRuleType == "") {
        		 ElMessage(`请先设置校验属性和校验规则类型！`);
        		 return;
        	}
        	
        	var attributeCode = null;
        	var subAttributeCode = null;
        	if("M" == this.ruleGroupInfo.objectType) {
        		attributeCode = this.validateAttr;
        		if(this.subValidateAttr != null && this.validateObject.inParams[this.validateAttr] != null && Array.isArray(this.validateObject.inParams[this.validateAttr])) {
        			for(var i in this.validateObject.inParams[this.validateAttr]){
                        if(this.validateObject.inParams[this.validateAttr][i].id == this.subValidateAttr) {
                            subAttributeCode = this.validateObject.inParams[this.validateAttr][i].code;
                            break;
                        }
                    }
        		}
        	}else {
        		if(this.validateObject.attributes != null) {
                    for(var i in this.validateObject.attributes){
                        if(this.validateObject.attributes[i].id == this.validateAttr) {
                            attributeCode = this.validateObject.attributes[i].code;
                            break;
                        }
                    }
                }
        	}
        	
        	var ruleType = [];
        	if(this.validateRuleTypes != null) {
        		for(var i in this.validateRuleTypes) {
        			if(this.validateRuleTypes[i].id == this.validateRuleType) {
        				ruleType = this.validateRuleTypes[i];
        				break;
        			}
        		}
        	}
        	var newValidateRule = {
        		id: null,
        		ruleGroupId: null,
        		attributeId: this.validateAttr,
        		subAttributeId: this.subValidateAttr,
        		ruleTypeId: this.validateRuleType,
        		validateRuleValue: this.validateRuleValue,
        		ruleValue1: JSON.stringify(this.validateRuleValue),
        		ruleValue2: this.ruleValue2,
        		ruleValue3: this.ruleValue3,
        		tenantId: this.ruleGroupInfo.tenantId,
        		systemId: this.ruleGroupInfo.systemId,
        		createTime: null,
        		updateTime: null,
        		attributeCode: attributeCode,
        		subAttributeCode: subAttributeCode,
        		validateRuleType: ruleType
        	};
        	this.ruleGroupInfo.validateRules.push(newValidateRule);
            this.ruleGroupInfo.validateRules.sort((a,b)=>{return a.attributeCode.localeCompare(b.attributeCode)});
        },
        deleteValidateRule(row) {
        	this.ruleGroupInfo.validateRules.splice(row,1);
        }
    }
}

</script>