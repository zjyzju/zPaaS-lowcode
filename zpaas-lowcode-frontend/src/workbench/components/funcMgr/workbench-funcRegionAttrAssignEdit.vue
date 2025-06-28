<!-- 编辑功能区域属性 -->
<template>
<el-dialog v-model="showFlag" v-if="showFuncRegionAttrAssignEdit ==true && funcRegionAttrAssign != null" title="编辑功能区域绑定属性">
    <template #header>
        <span class="title">编辑功能区域绑定属性</span>
    </template>
    <el-form  :model="funcRegionAttrAssign" label-width="120px" :rules="validateRules" ref="funcRegionAttrAssignForm">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="标识" required prop="id">
                    <el-input v-model="funcRegionAttrAssign.id" readonly size="small"/>
                    <el-input type="hidden" v-model="funcRegionAttrAssign.funcId"  size="small"/>
                    <el-input type="hidden" v-model="funcRegionAttrAssign.funcRegionId" size="small"/>
                    
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="归属对象" required prop="objectId">
                    <el-input type="hidden" v-model="funcRegionAttrAssign.objectAssignId" size="small"/>
                    <el-input type="hidden" v-model="funcRegionAttrAssign.objectType" size="small"/>
                    <el-select v-model="funcRegionAttrAssign.objectId" class="m-2" placeholder="Select" size="small">
                         <el-option
                                  v-for="item in objectAssigns"
                                  :key="item.assignObject.id"
                                  :label="item.assignObject.code"
                                  :value="item.assignObject.id"
                          />
                    </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="绑定属性" required prop="attributeId">
                    <el-select v-model="funcRegionAttrAssign.attributeId" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in assignObject.attributes"
                                  :key="item.id"
                                  :label="item.code"
                                  :value="item.id"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="显示名称" required prop="displayName">
                    <el-input  v-model="funcRegionAttrAssign.displayName"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="显示类型" required prop="displayType">
                      <el-select v-model="funcRegionAttrAssign.displayType" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in displayTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="是否只读">
                    <el-radio-group v-model="funcRegionAttrAssign.readonly" class="ml-4">
                        <el-radio value="Y" size="large">是</el-radio>
                        <el-radio value="N" size="large">否</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="占用行数">
                    <el-input  v-model="funcRegionAttrAssign.rowSpan"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="占用列数">
                    <el-input  v-model="funcRegionAttrAssign.colSpan"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="显示宽度">
                    <el-input  v-model="funcRegionAttrAssign.displayWidth"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="显示顺序" required prop="displayOrder">
                    <el-input  v-model="funcRegionAttrAssign.displayOrder"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="是否必须">
                    <el-radio-group v-model="funcRegionAttrAssign.required" class="ml-4">
                        <el-radio value="Y" size="large">是</el-radio>
                        <el-radio value="N" size="large">否</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="显示样式配置">
                    <el-input type="textarea" rows="3" v-model="funcRegionAttrAssign.displayCfg"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideFuncRegionAttrAssignEditPage">取消</el-button>
            <el-button type="primary" @click="saveFuncRegionAttrAssign()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';


export default {
	props: ['showFuncRegionAttrAssignEdit','funcRegionAttrAssign', 'objectMap','objectAssigns'],
    
    emits: ['updateFuncRegionAttrAssignToList', 'hideFuncRegionAttrAssignEditPage'],
    
    setup (props, {attrs, emit, slots}) {
        const hideFuncRegionAttrAssignEditPage = () => {
            emit('hideFuncRegionAttrAssignEditPage')
        };
        const updateFuncRegionAttrAssignToList = (updateParam) => {
        	emit('updateFuncRegionAttrAssignToList', updateParam)
        };
        
        return {
        	updateFuncRegionAttrAssignToList,
        	hideFuncRegionAttrAssignEditPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showFuncRegionAttrAssignEdit;
            },
            set(value) {
                this.hideFuncRegionAttrAssignEditPage();
            }
        }
    },
    components: {
        
    },
    watch: {        
    	'funcRegionAttrAssign.objectId': function(val, old){
            if(this.objectAssigns != null) {
                for(var i in this.objectAssigns) {
                    if(this.objectAssigns[i].objectId == val) {
                        this.funcRegionAttrAssign.objectType = this.objectAssigns[i].objectType;
                        this.funcRegionAttrAssign.objectAssignId = this.objectAssigns[i].id;
                        this.assignObject = this.objectAssigns[i]
                        break;
                    }
                }
            }
        }
    },
    data() {
        const displayTypeOptions = ref(null);
        
        const assignObject = ref({});

        const validateRules = ref({
            "id": [
                { required: true, message: '标识不能为空！', trigger: 'blur' }
            ],
            "objectId": [
                { required: true, message: '归属对象不能为空！', trigger: 'blur' }
            ],
            "attributeId": [
                { required: true, message: '绑定属性不能为空！', trigger: 'blur' }
            ],
            "displayName": [
                { required: true, message: '显示名称不能为空！', trigger: 'blur' }
            ],
            "displayType": [
                { required: true, message: '显示类型不能为空！', trigger: 'blur' }
            ],
            "displayOrder": [
                { required: true, message: '顺序不能为空！', trigger: 'blur' },
                { pattern: /^[0-9]*$/, message: '顺序必须是整数类型！', trigger: 'blur' }
            ]
        });
        
        return {
        	displayTypeOptions,
        	assignObject,
            validateRules
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['DISPLAY_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.displayTypeOptions = data.data['DISPLAY_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

    	if(this.objectAssigns != null) {
            for(var i in this.objectAssigns) {
                if(this.objectAssigns[i].objectId == this.funcRegionAttrAssign.objectId) {
                    this.assignObject = this.objectAssigns[i]
                    break;
                }
            }
        }
    },
    methods: {
    	saveFuncRegionAttrAssign() {
            var url = "/lcdp-proxy/lowcode/platform/fe/api/funcRegionAttrAssign/save";
            this.$refs.funcRegionAttrAssignForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url,this.funcRegionAttrAssign).then((response) => {
                        var data = response.data; 
                        if(data != null && data.status == "200" && data.data != null) {
                            ElMessage(`保存功能区域绑定属性信息成功!`);
                            this.updateFuncRegionAttrAssignToList(data.data);
                        }
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        }
        
    }
}

</script>