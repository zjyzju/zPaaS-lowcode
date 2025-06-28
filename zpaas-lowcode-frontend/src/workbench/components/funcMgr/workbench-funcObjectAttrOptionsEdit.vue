<!-- 编辑属性选项 -->
<template>
<el-dialog v-model="showFlag"  top="8vh" v-if="showAttrOptionsEdit ==true && objectAttrOptions != null" :title="'编辑属性选项(' + objectAttrOptions.attributeCode + ')'">
    <template #header>
        <span class="title">编辑属性选项({{objectAttrOptions.attributeCode }})</span>
    </template>
 <el-scrollbar height="65vh">
    <el-form  :model="objectAttrOptions" label-width="120px">
        <el-row :gutter="4">
            <el-col :span="12">
                <el-input type="hidden" v-model="objectAttrOptions.id"  size="small"/>
                <el-input type="hidden" v-model="objectAttrOptions.objectAssignId"  size="small"/>
                <el-input type="hidden" v-model="objectAttrOptions.attributeId" size="small"/>
                <el-form-item label="联动类型">
                      <el-select v-model="objectAttrOptions.interactionType" clearable class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in interactionTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="可选值配置方式">
                      <el-select v-model="objectAttrOptions.optionCfgType" clearable class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in optionCfgTypeOptions"
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
                <el-form-item label="可选值配置">
                    <el-input type="textarea" v-model="objectAttrOptions.optionCfg" rows="5" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="上级对象">
                      <el-select v-model="objectAttrOptions.parentObjectAssignId" clearable class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in objectAssigns"
                                  :key="item.id"
                                  :label="item.assignObject.code"
                                  :value="item.id"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                <el-form-item label="上级属性">
                      <el-select v-model="objectAttrOptions.parentAttributeId" clearable class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in parentAttributes"
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
                <el-form-item label="显示隐藏配置">
                    <el-input type="textarea" v-model="objectAttrOptions.displayHideCfg" rows="5" size="small" placeholder='{"value1":"true","value2":"false","value3":"true"}'/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="弹出选择配置">
                    <el-input type="textarea" v-model="objectAttrOptions.popupCfg" rows="5" size="small" placeholder='{"subFuncId":"子功能标识", "params":[{"valueType":"F","value":"固定值","targetAttr":"目标属性编码"},{"valueType":"A","value":"属性编码","targetAttr":"目标属性编码"}],"returnParams":{"lavelAttr":"名称属性","valueAttr":"值属性"}}'/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="查询类型">
                      <el-select v-model="objectAttrOptions.queryType" clearable class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in queryTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="12">
                &nbsp;
            </el-col>
        </el-row>
    </el-form>
</el-scrollbar>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideObjectAttrOptionsEditPage">取消</el-button>
            <el-button type="primary" @click="saveObjectAttrOptions()">确定</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script >
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus';



export default {
	props: ['showAttrOptionsEdit','objectAttrOptions','objectAssigns'],
    
    emits: ['updateObjectAttrOptionsToList', 'hideObjectAttrOptionsEditPage'],
    
    setup (props, {attrs, emit, slots}) {
        const hideObjectAttrOptionsEditPage = () => {
            emit('hideObjectAttrOptionsEditPage')
        };
        const updateObjectAttrOptionsToList = (updateParam) => {
        	emit('updateObjectAttrOptionsToList', updateParam)
        };
        
        return {
        	updateObjectAttrOptionsToList,
        	hideObjectAttrOptionsEditPage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showAttrOptionsEdit;
            },
            set(value) {
                this.hideObjectAttrOptionsEditPage();
            }
        }
    },
    components: {
        
    },
    watch: {        
        "objectAttrOptions.parentObjectAssignId": {
        	handler: function(newVal, oldVal) {
        		this.parentAttributes = [];
        		if(newVal != null && newVal.trim().length > 0) {
        			for(var i in this.objectAssigns) {
        				if(this.objectAssigns[i].id == newVal) {
        					this.parentAttributes = this.objectAssigns[i].attributes;
        					break;
        				}
        			}
        		}
        	}
        }
    },
    data() {
        const optionCfgTypeOptions = ref(null);
        
        const queryTypeOptions = ref(null);
        
        const interactionTypeOptions = ref(null);
        
        const parentAttributes = ref([]);
        
        return {
        	optionCfgTypeOptions,
        	interactionTypeOptions,
        	queryTypeOptions,
        	parentAttributes
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OPTION_CFG_TYPE','QUERY_SUPPORT_TYPE','INTERACTION_SUPPORT_TYPE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.optionCfgTypeOptions = data.data['OPTION_CFG_TYPE'];
                this.queryTypeOptions = data.data['QUERY_SUPPORT_TYPE'];
                this.interactionTypeOptions = data.data['INTERACTION_SUPPORT_TYPE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });

        if(this.objectAttrOptions.parentObjectAssignId != null && this.objectAttrOptions.parentObjectAssignId.trim().length > 0) {
            for(var i in this.objectAssigns) {
                if(this.objectAssigns[i].id == this.objectAttrOptions.parentObjectAssignId) {
                    this.parentAttributes = this.objectAssigns[i].attributes;
                    break;
                }
            }
        }
    },
   methods: {
    	saveObjectAttrOptions() {
            var url = "/lcdp-proxy/lowcode/platform/fe/api/funcObjectAttrOptions/save";
            if(this.objectAttrOptions != null && this.objectAttrOptions.options == null) {
                this.objectAttrOptions.options = [];
            }
            axiosClient.post(url,this.objectAttrOptions).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    ElMessage(`保存属性可选值信息成功!`);
                    this.updateObjectAttrOptionsToList(data.data);
                }
            });
        }
        
    }
}

</script>