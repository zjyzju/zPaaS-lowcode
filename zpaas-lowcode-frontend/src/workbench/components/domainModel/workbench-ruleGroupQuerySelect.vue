<!-- 校验规则组查询选择页 -->
<template>
<el-dialog v-model="showFlag" title="校验规则组选择" width="800px">
    <template #header>
        <span class="title">校验规则组选择</span>
    </template>
    <div>
        <el-row :gutter="4">
            <el-col :span="12">
                <el-form-item label="对象类型">
                      <el-select v-model="objectType" class="m-2" placeholder="Select" size="small">
                           <el-option
                                  v-for="item in objectTypeOptions"
                                  :key="item.value"
                                  :label="item.label"
                                  :value="item.value"
                            />
                      </el-select>
                </el-form-item>
            </el-col>
            <el-col :span="9">
                <el-form-item label="校验对象">
                    <el-input type="hidden" v-model="objectId"   size="small"/>
                    <el-input v-model="objectCode" :readonly="true"  size="small"/>
                    
                </el-form-item>
            </el-col>
            <el-col :span="3">
                <el-form-item label="" label-width="10px">
                    <el-link  type="primary" @click="selectObject()" ><label>选择</label></el-link>&nbsp;
                    <el-link  type="primary" @click="clearObject()" ><label>清空</label></el-link>                                                
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="20" class="toolbar-right">
                <el-button type="primary" size="small" @click="queryValidateRuleGroup()">查询</el-button>&nbsp;&nbsp;&nbsp;&nbsp;
            </el-col>
            <el-col :span="4" class="toolbar-right">&nbsp;</el-col>
        </el-row>

        <!-- 校验规则组列表 -->
        <br/>
        <el-row :gutter="4">
            <el-col :span="1"></el-col>
            <el-col :span="23">
                <el-divider content-position="left"  style="margin-top: 1px;margin-bottom: 8px">
                <div class="toolbar1">
                    <el-icon><histogram /></el-icon><span>校验规则组列表</span>&nbsp;&nbsp;&nbsp;&nbsp;
                </div>
                </el-divider>
            </el-col>
        </el-row>
        <el-scrollbar  height="350px">
            <el-form  label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="ruleGroupSelectTable" :data="ruleGroups" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
                            <el-table-column  label="操作" width="60">
                                <template #default="scope">
                                    <el-link  type="primary" @click="editValidateRuleGroup(scope.row.id)">编辑</el-link>
                                </template>
                            </el-table-column>
                            <el-table-column prop="id" label="校验规则组标识" width="251" />
                            <el-table-column prop="name" label="校验规则组名称" width="180" />
                            <el-table-column prop="objectType" label="校验对象类型" width="130" >
                                <template #default="scope">
						            <div style="display: flex; align-items: center">
						              <span v-if="scope.row.objectType == 'D'">领域对象</span>
						              <span v-if="scope.row.objectType == 'V'">值传递对象</span>
						              <span v-if="scope.row.objectType == 'M'">服务方法</span>
						            </div>
						        </template>
                            </el-table-column>
                            <el-table-column prop="objectCode" label="校验对象类"/>
                        </el-table>
                    </el-col>
                </el-row>
                
            </el-form>
        </el-scrollbar>
    </div>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hideRuleGroupSelectPage()">取消</el-button>
            <el-button type="primary" @click="selectRuleGroup()">确定</el-button>
        </span>
    </template>
</el-dialog>
<!-- 选择领域对象信息 -->
<domainObjectSelect v-if="showDomainObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideDomainObjectSelectPage="hideDomainObjectSelectPage" :showDomainObjectSelect="showDomainObjectSelect"  :managedObjectsForSelect="managedDObjectsForSelect"/>
<!-- 选择值传递对象信息 -->
<valueObjectSelect v-if="showValueObjectSelect"  @managedObjectSelected="managedObjectSelected" @hideValueObjectSelectPage="hideValueObjectSelectPage" :showValueObjectSelect="showValueObjectSelect"  :managedObjectsForSelect="managedRObjectsForSelect"/>
<!-- 校验规则组编辑 -->
<editRuleGroup v-if="showRuleGroupEdit==true && ruleGroupInfo != null" :showRuleGroupEdit="showRuleGroupEdit" :ruleGroupInfo="ruleGroupInfo" @hideEditRuleGroupPage="hideEditRuleGroupPage" @addNewRuleGroupToList="addNewRuleGroupToList"/>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'
import domainObjectSelect from './workbench-domainObjectSelect.vue';
import valueObjectSelect from './workbench-valueObjectSelect.vue';
import editRuleGroup from './workbench-ruleGroupEdit.vue'

export default {
    props: ['showRuleGroupSelect', 'systemId'],
    
    emits: [ 'hideRuleGroupSelectPage', 'ruleGroupSelected'],
    
    setup(props, {attrs, emit, slots}) {
        const hideRuleGroupSelectPage = () => {
            emit('hideRuleGroupSelectPage');
        };
        
        const ruleGroupSelected = (mappings, type) => {
            emit('ruleGroupSelected', mappings, type);
        };
        return {
        	hideRuleGroupSelectPage,
        	ruleGroupSelected
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showRuleGroupSelect;
            },
            set(value) {
                this.hideRuleGroupSelectPage();
            }
        }
    },
    components: {
    	domainObjectSelect,
        valueObjectSelect ,
        editRuleGroup
    },
    
    data() {
    	const objectTypeOptions = ref(null);
        const objectType = ref("D");
        const objectId = ref(null);
        const objectCode = ref(null);
        const managedDObjectsForSelect = ref([]);
        const showDomainObjectSelect = ref(false);
        const showValueObjectSelect = ref(false);
        const managedRObjectsForSelect = ref([]);
    	
    	const currentRow = ref();

        const ruleGroups = ref(null);

        const showRuleGroupEdit = ref(false);
    	const ruleGroupInfo = ref(null);
    	
        return {
        	objectTypeOptions,
            objectType,
            objectId,
            objectCode,
            managedDObjectsForSelect,
            managedRObjectsForSelect,
            showDomainObjectSelect,
            showValueObjectSelect,
    		currentRow,
            showRuleGroupEdit,
        	ruleGroupInfo,
            ruleGroups
        }
    },
    mounted() {
        axiosClient.post("/lcdp-proxy/lowcode/platform/be/api/dict/getDictLabelValues", ['OBJECT_TYPE_HAS_ATTRIBUTE']).then((response) => {
            var data = response.data; 
            if(data != null && data.status == "200" && data.data != null) {
                this.objectTypeOptions = data.data['OBJECT_TYPE_HAS_ATTRIBUTE'];
            }
        }).catch(ex => {
            ElMessage.error(`加载字典数据失败!\n` + ex);
        });
    },
    methods: {
    	handleCurrentChange(val) {
    		this.currentRow = val;
    	},
    	selectRuleGroup() {
    		if(this.currentRow == null || this.currentRow.id == null) {
    			ElMessage.error(`请先选择一条校验规则组！`);
    			return;
    		}
    		this.ruleGroupSelected(this.currentRow);
        },
        selectObject() {
            if("D" == this.objectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/domainObject/list/" + this.systemId;
                axiosClient.get(url).then((response) => {
                    var data = response.data; 
                    if(data != null && data.status == "200" && data.data != null) {
                        this.managedDObjectsForSelect = data.data;
                        this.showDomainObjectSelect = true;
                    }
                });
            }else if("V" == this.objectType) {
                var url = "/lcdp-proxy/lowcode/platform/be/api/valueObject/list/" + this.systemId;
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
        clearObject(){
            this.objectId = "";
            this.objectCode = "";
        },
        managedObjectSelected(obj) {
            this.objectId = obj.id;
            this.objectCode = obj.code;
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
        },
        editValidateRuleGroup(ruleGroupId){
        	var url = "/lcdp-proxy/lowcode/platform/be/api/validateRuleGroup/load/" + ruleGroupId;
        	axiosClient.post(url).then((response) => {
                var data = response.data;
                if(data != null && data.status == "200" && data.data != null) {
            	    this.ruleGroupInfo = data.data; 
            	    this.showRuleGroupEdit = true;
                }
            });
        },
        hideEditRuleGroupPage() {
        	this.showRuleGroupEdit = false;
        	this.ruleGroupInfo = null;
        },
        addNewRuleGroupToList(ruleGroup, isNew) {
        	if(isNew) {
        		this.ruleGroupsForSelect.push(ruleGroup);
        	}else {
        		for(var i in this.ruleGroupsForSelect) {
        			if(this.ruleGroupsForSelect[i].id == ruleGroup.id) {
        				this.ruleGroupsForSelect[i] = ruleGroup;
        				break;
        			}
        		}
        	}
        	
        	this.showRuleGroupEdit = false;
        },
        queryValidateRuleGroup() {
            if(this.objectType ==null || this.objectType.trim().length == 0 || this.objectId == null || this.objectId.trim().length == 0) {
                ElMessage.error(`请先选择对象类型和校验对象！`);
    			return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/validateRuleGroup/listByObject/" + this.systemId + "/" + this.objectType + "/" + this.objectId;
            axiosClient.get(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null) {
                    this.ruleGroups = data.data;
                }
            });
        }
    }
}

</script>