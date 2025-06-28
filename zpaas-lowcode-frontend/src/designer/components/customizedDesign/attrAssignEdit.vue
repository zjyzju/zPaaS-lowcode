<template> 
    <el-form  :model="attrAssign" label-width="60px" :rules="validateRules" ref="attrAssignForm">
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="对象" >
                    <el-select v-model="attrAssign.objectAssignId" class="m-2" placeholder="Select" size="small" :disabled="attrAssign.objectAssignId != null">
                           <el-option
                                  v-for="item in objectAssigns"
                                  :key="item.id"
                                  :label="item.assignObject != null ? item.assignObject.name : item.biDataSet.name"
                                  :value="item.id"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="属性" required prop="attributeId">
                    <el-select v-model="attrAssign.attributeId" class="m-2" placeholder="Select" size="small"  :disabled="attrAssign.attributeId != null">
                           <el-option v-if="currAssignObject != null"
                                  v-for="item in currAssignObject.attributes"
                                  :key="item.id"
                                  :label="item.code"
                                  :value="item.id"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="名称" prop="displayName">
                    <el-input  v-model="attrAssign.displayName"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="类型" required prop="displayType">
                      <el-select v-model="attrAssign.displayType" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in displayTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="必须">
                    <el-radio-group v-model="attrAssign.required" class="ml-4">
                        <el-radio value="Y" size="large">是</el-radio>
                        <el-radio value="N" size="large">否</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="只读">
                    <el-radio-group v-model="attrAssign.readonly" class="ml-4">
                        <el-radio value="Y" size="large">是</el-radio>
                        <el-radio value="N" size="large">否</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="行数">
                    <el-input  v-model="attrAssign.rowSpan"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="列数">
                    <el-input  v-model="attrAssign.colSpan"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="宽度">
                    <el-input  v-model="attrAssign.displayWidth"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="样式">
                    <el-input type="textarea" rows="5" v-model="attrAssign.displayCfg"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';

export default {
    props: ['attrAssign','objectAssigns', 'assignObject'],
    
    emits: [],
    
    setup (props, {attrs, emit, slots}) {
        
        return {
            
        }
    },
    computed: {
        
    },
    components: {
        
    },
    
    data() {   
        const displayTypeOptions = ref(null);

        const biDisplayTypeOptions = ref(null);

        const validateRules = ref({
            "objectId": [
                { required: true, message: '归属对象不能为空！', trigger: 'blur' }
            ],
            "attributeId": [
                { required: true, message: '绑定属性不能为空！', trigger: 'blur' }
            ],
            "displayType": [
                { required: true, message: '显示类型不能为空！', trigger: 'blur' }
            ],
        });

        const currAssignObject = ref(null);
        
        return {
            displayTypeOptions,
            biDisplayTypeOptions,
            validateRules,
            currAssignObject
        }
    },
    watch: {
        'attrAssign.objectAssignId': function(val, old){
            if(val == null || val.trim().length == 0) {
                return;
            }
            for(var index in this.objectAssigns) {
                if(this.objectAssigns[index].id == val) {
                    this.currAssignObject = this.objectAssigns[index];
                    this.attrAssign.objectType = this.currAssignObject.objectType;
                    this.attrAssign.objectId = this.currAssignObject.objectId;
                    break;
                }
            }
        },
        'attrAssign.attributeId': function(val, old){
            if(val == null || val.trim().length == 0) {
                return;
            }
            for(var index in this.currAssignObject.attributes) {
                if(this.currAssignObject.attributes[index].id == val) {
                    this.attrAssign.attribute = this.currAssignObject.attributes[index];
                    break;
                }
            }
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['DISPLAY_TYPE','BI_DISPLAY_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.displayTypeOptions = data.data['DISPLAY_TYPE'];
                this.biDisplayTypeOptions = data.data['BI_DISPLAY_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        this.currAssignObject = this.assignObject;
    },
    methods: {
    }
 }
</script>