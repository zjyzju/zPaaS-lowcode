<!-- 复制功能区域属性 -->
<template>
<el-dialog v-model="showFlag" v-if="showFuncRegionAttrAssignCopyFromOther ==true" title="复制功能区域绑定属性">
    <template #header>
        <span class="title">复制功能区域绑定属性</span>
    </template>
    <el-form  :model="{}" label-width="120px">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="选择源功能">
                      <el-select v-model="fromFuncId" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in funcDefines"
                                  :key="item.id"
                                  :label="item.name"
                                  :value="item.id"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="选择源区域">
                      <el-select v-model="fromRegionId" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in funcRegions"
                                  :key="item.id"
                                  :label="item.name"
                                  :value="item.id"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideFuncRegionAttrAssignCopyPageFromOther">取消</el-button>
            <el-button type="primary" @click="copyFuncRegionAttrAssign()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';


export default {
	props: ['showFuncRegionAttrAssignCopyFromOther','targetFuncId', 'targetRegionId', 'systemId'],
    
    emits: ['refreshFuncRegionAttrAssignToList', 'hideFuncRegionAttrAssignCopyPageFromOther'],
    
    setup (props, {attrs, emit, slots}) {
        const hideFuncRegionAttrAssignCopyPageFromOther = () => {
            emit('hideFuncRegionAttrAssignCopyPageFromOther')
        };
        const refreshFuncRegionAttrAssignToList = (updateParam) => {
        	emit('refreshFuncRegionAttrAssignToList', updateParam)
        };
        
        return {
        	refreshFuncRegionAttrAssignToList,
        	hideFuncRegionAttrAssignCopyPageFromOther
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showFuncRegionAttrAssignCopyFromOther;
            },
            set(value) {
                this.hideFuncRegionAttrAssignCopyPageFromOther();
            }
        }
    },
    components: {
        
    },
    data() {
    	const fromFuncId = ref(null);
        const fromRegionId = ref(null);
        const funcDefines = ref(null);
        const funcRegions = ref(null)
        
        return {
        	fromFuncId,
        	fromRegionId,
        	funcDefines,
        	funcRegions
        }
    },
    mounted() {
    	var url = "/lcdp-proxy/lowcode/platform/fe/api/funcDefine/listTplFunc/" + this.systemId;
        axiosClient.post(url).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.funcDefines = data.data;
            }
        });
    },
    watch: {
    	'fromFuncId': function(val, old){
            this.fromRegionId = "";
            this.funcRegions = null;
            if(this.fromFuncId != null && this.fromFuncId.length > 0){
            	var url = "/lcdp-proxy/lowcode/platform/fe/api/funcRegion/list/" + this.systemId + "/" + this.fromFuncId;
                axiosClient.post(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.funcRegions = data.data;
                    }
                });
            }
        }
    },
    methods: {
    	copyFuncRegionAttrAssign() {
    		if(this.fromRegionId == null || this.fromFuncId == null) {
    			ElMessage.error(`请先选源区域！`);
    			return;
    		}
    		
            var url = "/lcdp-proxy/lowcode/platform/fe/api/funcRegionAttrAssign/copyFromOther/" + this.fromFuncId + "/" + this.fromRegionId + "/" + this.targetFuncId + "/" + this.targetRegionId;
            axiosClient.post(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    ElMessage(`复制功能区域绑定属性信息成功!`);
                    this.refreshFuncRegionAttrAssignToList(data.data);
                }
            });
        }
        
    }
}

</script>