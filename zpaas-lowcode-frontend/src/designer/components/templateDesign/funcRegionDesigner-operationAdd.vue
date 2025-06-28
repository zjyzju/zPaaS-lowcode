<template> 
    <el-form  :model="newFuncRegionOperation" label-width="60px" :rules="validateRules" ref="newFuncRegionOperationForm">
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="名称" required prop="name">
                    <el-input  v-model="newFuncRegionOperation.name" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="类型" required prop="operationType">
                      <el-select v-model="newFuncRegionOperation.operationType" clearable class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in operationTypeOptions"
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
                <el-form-item label="归属">
                    <el-select v-model="newFuncRegionOperation.objectAssignId" class="m-2" clearable placeholder="Select" size="small">
                           <el-option
                                  v-for="item in objectAssigns"
                                  :key="item.id"
                                  :label="item.assignObject.name"
                                  :value="item.id"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
            </el-col>
            <el-col :span="12">
                <el-button size="small" type="primary" @click="addOperation()">新增</el-button>
            </el-col>
        </el-row>
    </el-form>
</template>
<script>
import { ref } from 'vue';

export default {
    props: ['funcRegion','objectAssigns', 'operationTypeOptions'],
    
    emits: ['addFuncRegionOperation'],
    
    setup (props, {attrs, emit, slots}) {
        const addFuncRegionOperation = (regionOperation) => {
            emit('addFuncRegionOperation', regionOperation);
        };

        return {
            addFuncRegionOperation
        }
    },
    computed: {
        
    },
    components: {
        
    },
    
    data() {   
        const validateRules = ref({
            "name": [
                { required: true, message: '操作名称不能为空！', trigger: 'blur' }
            ],
            "operationType": [
                { required: true, message: '操作类型不能为空！', trigger: 'blur' }
            ],
        });

        const newFuncRegionOperation = ref({
            funcId: this.funcRegion.funcId,
            funcRegionId: this.funcRegion.id,
            systemId: this.funcRegion.systemId,
            tenantId: this.funcRegion.tenantId
        });
        
        return {
            validateRules,
            newFuncRegionOperation
        }
    },
    mounted() {
        
    },
    methods: {
        addOperation() {
            this.addFuncRegionOperation(this.newFuncRegionOperation);
            this.newFuncRegionOperation = {
                funcId: this.funcRegion.funcId,
                funcRegionId: this.funcRegion.id,
                systemId: this.funcRegion.systemId,
                tenantId: this.funcRegion.tenantId
            };
        }
    }
 }
</script>