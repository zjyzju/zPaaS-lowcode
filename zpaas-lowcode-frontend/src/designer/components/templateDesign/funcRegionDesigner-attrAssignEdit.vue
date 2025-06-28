<template> 
    <el-form  :model="funcRegionAttrAssign" label-width="60px" :rules="validateRules" ref="funcRegionAttrAssignForm">
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="属性" required prop="attributeId">
                    <el-select v-model="funcRegionAttrAssign.attributeId" class="m-2" placeholder="Select" size="small" disabled>
                           <el-option
                                  v-for="item in assignObject.attributes"
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
                <el-form-item label="名称" required prop="displayName">
                    <el-input  v-model="funcRegionAttrAssign.displayName"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="类型" required prop="displayType">
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
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="必须">
                    <el-radio-group v-model="funcRegionAttrAssign.required" class="ml-4">
                        <el-radio value="Y" size="large">是</el-radio>
                        <el-radio value="N" size="large">否</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="只读">
                    <el-radio-group v-model="funcRegionAttrAssign.readonly" class="ml-4">
                        <el-radio value="Y" size="large">是</el-radio>
                        <el-radio value="N" size="large">否</el-radio>
                    </el-radio-group>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="行数">
                    <el-input  v-model="funcRegionAttrAssign.rowSpan"  size="small"/>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="列数">
                    <el-input  v-model="funcRegionAttrAssign.colSpan"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="宽度">
                    <el-input  v-model="funcRegionAttrAssign.displayWidth"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="样式">
                    <el-input type="textarea" rows="5" v-model="funcRegionAttrAssign.displayCfg"  size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>
</template>
<script>
import { ref } from 'vue';

export default {
    props: ['funcRegionAttrAssign','objectAssigns', 'assignObject', 'displayTypeOptions'],
    
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

        const validateRules = ref({
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
        });
        
        return {
            validateRules
        }
    },
    mounted() {
        
    },
    methods: {
    }
 }
</script>