<!-- 复制绑定对象属性选项信息 -->
<template>
<el-dialog v-model="showFlag" v-if="showFuncObjectAttrOptionsCopyFromOther ==true" title="复制绑定对象属性选项">
    <template #header>
        <span class="title">复制绑定对象属性选项</span>
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
                <el-form-item label="选择源绑定对象">
                      <el-select v-model="fromObjectAssignId" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in objectAssigns"
                                  :key="item.id"
                                  :label="item.assignObject.code"
                                  :value="item.id"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
        </el-row>
        
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideFuncObjectAttrOptionsCopyFromOtherPage()">取消</el-button>
            <el-button type="primary" @click="copyFuncObjectAttrOptions()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';


export default {
	props: ['showFuncObjectAttrOptionsCopyFromOther','targetFuncId', 'targetObjectAssignId', 'systemId'],
    
    emits: ['refreshFuncObjectAttrOptionsToList', 'hideFuncObjectAttrOptionsCopyFromOtherPage'],
    
    setup (props, {attrs, emit, slots}) {
        const hideFuncObjectAttrOptionsCopyFromOtherPage = () => {
            emit('hideFuncObjectAttrOptionsCopyFromOtherPage')
        };
        const refreshFuncObjectAttrOptionsToList = (updateParam) => {
        	emit('refreshFuncObjectAttrOptionsToList', updateParam)
        };
        
        return {
        	refreshFuncObjectAttrOptionsToList,
        	hideFuncObjectAttrOptionsCopyFromOtherPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showFuncObjectAttrOptionsCopyFromOther;
            },
            set(value) {
                this.hideFuncObjectAttrOptionsCopyFromOtherPage();
            }
        }
    },
    components: {
        
    },
    data() {
    	const fromFuncId = ref(null);
        const fromObjectAssignId = ref(null);
        const funcDefines = ref(null);
        const objectAssigns = ref(null)
        
        return {
        	fromFuncId,
        	fromObjectAssignId,
        	funcDefines,
        	objectAssigns
        }
    },
    mounted() {
    	var url = "/lcdp-proxy/lowcode/platform/fe/api/funcDefine/list/" + this.systemId;
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
            this.objectAssigns = null;
            if(this.fromFuncId != null && this.fromFuncId.length > 0){
            	var url = "/lcdp-proxy/lowcode/platform/fe/api/funcObjectAssign/listByFunc/" + this.fromFuncId;
                axiosClient.post(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.objectAssigns = data.data;
                    }
                });
            }
        }
    },
    methods: {
    	copyFuncObjectAttrOptions() {
    		if(this.fromRegionId == null || this.fromFuncId == null) {
    			ElMessage.error(`请先选源区域！`);
    			return;
    		}
    		
            var url = "/lcdp-proxy/lowcode/platform/fe/api/funcObjectAssign/copyOptionsFromOther/" + this.fromObjectAssignId + "/" + this.targetObjectAssignId;
            axiosClient.post(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    ElMessage(`复制功能绑定对象属性选项信息成功!请注意添加上级属性相关信息（未复制）！`);
                    this.refreshFuncObjectAttrOptionsToList(data.data);
                }
            });
        }
        
    }
}

</script>