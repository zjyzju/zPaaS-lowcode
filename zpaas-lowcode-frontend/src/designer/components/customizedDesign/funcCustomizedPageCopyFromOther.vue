<!-- 复制自定义页面 -->
<template>
<el-dialog v-model="showFlag" v-if="showFuncCustomizedPageCopy ==true" title="复制自定义页面">
    <template #header>
        <span class="title">复制自定义页面设计</span>
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
                <el-form-item label="选择源页面">
                      <el-select v-model="fromPageId" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in funcCustomizedPages"
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
            <el-button @click="hideFuncCustomizedPageCopyPage">取消</el-button>
            <el-button type="primary" @click="copyFuncCustomizedPage()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus';


export default {
	props: ['showFuncCustomizedPageCopy','targetFuncId', 'targetPageId', 'systemId'],
    
    emits: ['reloadCustomizedPage', 'hideFuncCustomizedPageCopyPage'],
    
    setup (props, {attrs, emit, slots}) {
        const hideFuncCustomizedPageCopyPage = () => {
            emit('hideFuncCustomizedPageCopyPage')
        };
        const reloadCustomizedPage = () => {
        	emit('reloadCustomizedPage', )
        };
        
        return {
        	reloadCustomizedPage,
        	hideFuncCustomizedPageCopyPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showFuncCustomizedPageCopy;
            },
            set(value) {
                this.hideFuncCustomizedPageCopyPage();
            }
        }
    },
    components: {
        
    },
    data() {
    	const fromFuncId = ref(null);
        const fromPageId = ref(null);
        const funcDefines = ref(null);
        const funcCustomizedPages = ref(null)
        
        return {
        	fromFuncId,
        	fromPageId,
        	funcDefines,
        	funcCustomizedPages
        }
    },
    mounted() {
        this.fromFuncId = this.targetFuncId;
    	var url = "/lcdp-proxy/lowcode/platform/fe/api/funcDefine/listCustomizedFunc/" + this.systemId;
        axiosClient.post(url).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null && data.data != null) {
                this.funcDefines = data.data;
            }
        });
    },
    watch: {
    	'fromFuncId': function(val, old){
            this.fromPageId = "";
            this.funcCustomizedPages = null;
            if(this.fromFuncId != null && this.fromFuncId.length > 0){
            	var url = "/lcdp-proxy/lowcode/platform/fe/api/customizedPage/list/" + this.systemId + "/" + this.fromFuncId;
                axiosClient.post(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null && data.data != null) {
                        this.funcCustomizedPages = data.data;
                    }
                });
            }
        }
    },
    methods: {
    	copyFuncCustomizedPage() {
    		if(this.fromPageId == null || this.fromFuncId == null) {
    			ElMessage.error(`请先选源区域！`);
    			return;
    		}
    		
            var url = "/lcdp-proxy/lowcode/platform/fe/api/customizedPage/copyFromOther/" + this.fromFuncId + "/" + this.fromPageId + "/" + this.targetFuncId + "/" + this.targetPageId;
            axiosClient.post(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null && data.data != null) {
                    ElMessage(`复制定义页面信息成功!`);
                    this.reloadCustomizedPage();
                }
            }).catch((ex) => {
                ElMessage.error(`复制定义页面信息失败:` + ex);
            }) ;
        }
        
    }
}

</script>