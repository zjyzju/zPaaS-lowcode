<!-- 复制功能区域属性 -->
<template>
<el-dialog v-model="showFlag" v-if="showFuncRegionAttrAssignCopy ==true && funcRegions != null" title="复制功能区域绑定属性">
    <template #header>
        <span class="title">复制功能区域绑定属性</span>
    </template>
    <el-form  :model="{}" label-width="120px">
        <el-row :gutter="4">
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
            <el-col :span="12">
                &nbsp;
            </el-col>
        </el-row>
        
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideFuncRegionAttrAssignCopyPage">取消</el-button>
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
	props: ['showFuncRegionAttrAssignCopy','funcRegions', 'targetRegionId'],
    
    emits: ['refreshFuncRegionAttrAssignToList', 'hideFuncRegionAttrAssignCopyPage'],
    
    setup (props, {attrs, emit, slots}) {
        const hideFuncRegionAttrAssignCopyPage = () => {
            emit('hideFuncRegionAttrAssignCopyPage')
        };
        const refreshFuncRegionAttrAssignToList = (updateParam) => {
        	emit('refreshFuncRegionAttrAssignToList', updateParam)
        };
        
        return {
        	refreshFuncRegionAttrAssignToList,
        	hideFuncRegionAttrAssignCopyPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showFuncRegionAttrAssignCopy;
            },
            set(value) {
                this.hideFuncRegionAttrAssignCopyPage();
            }
        }
    },
    components: {
        
    },
    data() {
        const fromRegionId = ref(null);
        
        return {
        	fromRegionId
        }
    },
    
    methods: {
    	copyFuncRegionAttrAssign() {
    		var fromRegion = null;
    		for(var i in this.funcRegions) {
    			if(this.funcRegions[i].id == this.fromRegionId) {
    				fromRegion = this.funcRegions[i];
    				break;
    			}
    		}
    		if(fromRegion == null) {
    			ElMessage.error(`请先选源区域！`);
    			return;
    		}
            var url = "/lcdp-proxy/lowcode/platform/fe/api/funcRegionAttrAssign/copy/" + fromRegion.funcId + "/" + fromRegion.id + "/" + this.targetRegionId;
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