<!-- 校验规则组查看 -->
<template>
<el-dialog v-model="showFlag" title="校验规则组查看" width="800px">
    <template #header>
        <span class="title">校验规则组查看</span>
    </template>
    <div>
        <el-link type="primary" @click="showRuleGroupCreatePage()">新建校验规则组</el-link>
    </div>
    <div>
        <el-scrollbar  height="350px">
            <el-form v-if="showRuleGroupSelect && ruleGroupsForSelect"  label-width="120px">
                 <el-row :gutter="4">
                    <el-col :span="24">
                        <el-table ref="ruleGroupSelectTable" :data="ruleGroupsForSelect" @current-change="handleCurrentChange" stripe highlight-current-row style="width: 100%">
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
        </span>
    </template>
</el-dialog>
<!-- 校验规则组新建 -->
<editRuleGroup v-if="showRuleGroupEdit==true && ruleGroupInfo != null" :showRuleGroupEdit="showRuleGroupEdit" :ruleGroupInfo="ruleGroupInfo" @hideEditRuleGroupPage="hideEditRuleGroupPage" @addNewRuleGroupToList="addNewRuleGroupToList"/>
</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import editRuleGroup from './workbench-ruleGroupEdit.vue'

export default {
    props: ['showRuleGroupSelect', 'ruleGroupsForSelect', 'systemId', 'objectType', 'objectId','tenantId'],
    
    emits: [ 'hideRuleGroupSelectPage'],
    
    setup(props, {attrs, emit, slots}) {
        const hideRuleGroupSelectPage = () => {
            emit('hideRuleGroupSelectPage');
        };
        
        return {
        	hideRuleGroupSelectPage
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
    	editRuleGroup
    },
    
    data() {
    	const showRuleGroupEdit = ref(false);
    	const ruleGroupInfo = ref(null);
    	
    	const currentRow = ref();
    	
        return {
        	showRuleGroupEdit,
        	ruleGroupInfo,
    		currentRow
        }
    },
    
    methods: {
    	handleCurrentChange(val) {
    		this.currentRow = val;
    	},
    	showRuleGroupCreatePage() {
        	this.ruleGroupInfo = {
                    id:"",
                    name:"",
                    objectType:this.objectType,
                    objectId:this.objectId,
                    status: "1",
                    systemId: this.systemId,    
                    tenantId: this.tenantId,
                    createTime: null,
                    updateTime: null,
                    validateRules:[]
            };
            this.showRuleGroupEdit = true;
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
        }
    }
}

</script>