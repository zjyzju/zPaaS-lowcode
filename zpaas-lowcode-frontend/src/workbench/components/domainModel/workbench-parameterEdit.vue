<!-- 编辑参数 -->
<template>
<el-dialog v-model="showFlag" v-if="showParameterEdit ==true && editParameter != null" title="编辑参数">
    <template #header>
        <span class="title">编辑参数</span>
    </template>
    <el-form  :model="editParameter" label-width="120px" :rules="validateRules" ref="editParameterForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="标识" required prop="id">
                    <el-input v-model="editParameter.id" readonly size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">              
                <el-form-item label="编码" required prop="code">
                    <el-input v-model="editParameter.code"  size="small"/>
                </el-form-item>
            </el-col>            
        </el-row>
        <el-row :gutter="4">           
            <el-col :span="12">
                <el-form-item label="名称" required prop="name">
                    <el-input v-model="editParameter.name"  size="small"/>
                </el-form-item>
            </el-col>
             <el-col :span="12">
                <el-form-item label="是否入参" required prop="isInParam">
                      <el-select v-model="editParameter.isInParam" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in yesOrNoOptions"
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
                <el-form-item label="参数类型" required prop="paramType">
                      <el-select v-model="editParameter.paramType" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in paramTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="9">
                <el-form-item label="参数类" required prop="paramClass">
                    <el-input type="hidden" v-model="editParameter.paramClass" v-if="editParameter.paramType!='J'"  size="small"/>
                    <el-input v-model="editParameter.paramClassObject.code" :readonly="true"  v-if="editParameter.paramType!='J'" size="small"/>
                    
                    <el-input v-model="editParameter.paramClass" v-if="editParameter.paramType=='J'"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="3" v-if="editParameter.paramType!='J'">
                <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectParamClass()" ><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearParamClass()" ><label>清空</label></el-link>                                                
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="是否列表" required prop="isListParam">
                      <el-select v-model="editParameter.isListParam" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in yesOrNoOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="顺序" required prop="displayOrder">
                    <el-input v-model="editParameter.displayOrder"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideEditParamPage()">取消</el-button>
            <el-button type="primary" @click="saveParameter()">确定</el-button>
        </span>
    </template>
</el-dialog>

<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideDomainObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedDObjectsForSelect"/>
<!-- 选择值传递对象信息 -->
<valueObjectSelect v-if="showValueObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideValueObjectSelectPage="hideValueObjectSelectPage" :showValueObjectSelect="showValueObjectSelect"  :managedObjectsForSelect="managedRObjectsForSelect"/>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';
import domainObjectSelect from './workbench-domainObjectSelect.vue';
import valueObjectSelect from './workbench-valueObjectSelect.vue';


export default {
	name: 'editParameter',
	
    props: ['showParameterEdit','editParameter'],
    
    emits: ['checkOutParam', 'updateParameterToList', 'hideEditParamPage'],
    
    setup (props, {attrs, emit, slots}) {
    	const checkOutParamValid = (param)=> {
            var valid = true;
            emit('checkOutParam', param, (result)=>{valid = result});
            return valid;
        };
        const hideEditParamPage = () => {
            emit('hideEditParamPage')
        };
        const updateParameterToList = (updateParam) => {
        	emit('updateParameterToList', updateParam)
        };
        
        return {
            checkOutParamValid,
            updateParameterToList,
            hideEditParamPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showParameterEdit;
            },
            set(value) {
                this.hideEditParamPage();
            }
        }
    },
    components: {
        domainObjectSelect,
        valueObjectSelect
    },
    watch: {        
        'editParameter.paramType': function(val, old){
            this.editParameter.paramClass = "";
            this.editParameter.paramClassObject = {};
        }
    
    },
    data() {
        const paramTypeOptions = ref(null);
        
        const yesOrNoOptions = ref(null);
        
        const showDomainObjectSelect = ref(false);
        const managedDObjectsForSelect = ref(null);
        const managedRObjectsForSelect = ref(null);
        const showValueObjectSelect = ref(false);
        
        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "code": [
                { required: true, message: '编码不能为空！', trigger: 'blur' }
            ],
            "name": [
                { required: true, message: '名称不能为空！', trigger: 'blur' }
            ],
            "paramType": [
                { required: true, message: '参数类型不能为空！', trigger: 'blur' }
            ],
            "paramClass": [
                { required: true, message: '参数类不能为空！', trigger: 'blur' }
            ],
            "isListParam": [
                { required: true, message: '是否列表不能为空！', trigger: 'blur' }
            ],
            "isInParam": [
                { required: true, message: '是否入参不能为空！', trigger: 'blur' }
            ],
            "displayOrder": [
                { required: true, message: '顺序不能为空！', trigger: 'blur' },
                { pattern: /^[0-9]*$/, message: '顺序必须是整数类型！', trigger: 'blur' }
            ]
        });

        return {
            paramTypeOptions,
            yesOrNoOptions,
            showDomainObjectSelect,
            showValueObjectSelect,
            managedRObjectsForSelect,
            managedDObjectsForSelect,
            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['PUB_YES_OR_NO','OBJECT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.yesOrNoOptions = data.data['PUB_YES_OR_NO'];
                this.paramTypeOptions = data.data['OBJECT_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	saveParameter() {
            var url = "/lcdp-proxy/lowcode/platform/be/api/parameter/save";
            var valid = this.checkOutParamValid(this.editParameter)
            if(!valid) {
                ElMessage.error(`只允许配置一个出参！`);
                return;
            }
            this.$refs.editParameterForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.editParameter).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            ElMessage(`更新参数信息成功`);
                            this.updateParameterToList(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        },
        selectParamClass() {
            if("D" == this.editParameter.paramType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.editParameter.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedDObjectsForSelect = data.data;
                        this.showDomainObjectSelect = true;
                    }
                });
            }else if("V" == this.editParameter.paramType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/list/" + this.editParameter.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedRObjectsForSelect = data.data;
                        this.showValueObjectSelect = true;
                    }
                });
            }else {
                return;
            }
        },
        clearParamClass(){
            this.editParameter.paramClass = "";
            this.editParameter.paramClassObject = {};
        },
        managedObjectSelected(obj) {
            this.editParameter.paramClass = obj.id;
            this.editParameter.paramClassObject = obj;
            this.managedDObjectsForSelect = null;
            this.managedRObjectsForSelect = null;
            this.showDomainObjectSelect = false;
            this.showValueObjectSelect = false;
        },
        hideDomainObjectSelectPage(){
             this.managedDObjectsForSelect = null;
             this.showDomainObjectSelect = false;
        },
        hideValueObjectSelectPage(){
            this.managedRObjectsForSelect = null;
            this.showValueObjectSelect = false;
       }
    }
}

</script>