<template>
    <br/>
    <el-form  :model="businessSystemGrant" label-width="120px" :rules="validateRules" ref="businessSystemGrantForm">
        <el-row :gutter="4">
            <el-col :span="6">              
                <el-form-item label="授权用户" required prop="userId">
                    <el-input type="hidden" v-model="businessSystemGrant.userId" size="small"/>
                    <el-input v-model="userName"  readonly size="small">
                        <template #append>
                            <el-button  size="small" @click="popupSelect()"><el-icon><search /></el-icon></el-button>
                        </template>
                    </el-input>
                </el-form-item>
            </el-col> 
            <el-col :span="18"> </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="6">              
                <el-form-item label="授权系统：">
                    
                </el-form-item>
            </el-col> 
            <el-col :span="18"> </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="2"></el-col>
            <el-col :span="22">
                <el-form-item label="" label-width="0px">
                    <el-checkbox-group v-model="businessSystemGrant.grantedSystems"  class="ml-4">
                        <el-checkbox v-if="businessSystems != null" v-for="item in businessSystems" :value="item.id" size="large">{{item.name + " : " + item.description}}</el-checkbox>
                    </el-checkbox-group>
                </el-form-item>
            </el-col>          
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24"></el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="18"></el-col>
            <el-col :span="6">
                <el-button type="primary" size="small" @click="saveBusinessSystemGrant()">授权</el-button>
            </el-col>          
        </el-row>
    </el-form>

    <div>
	    <el-dialog v-model="showDynamicComponent" v-if="showDynamicComponent == true" title="弹出选择" width="900px" append-to-body>
            <component :is="dynamicComponent" :funcId="popupSubFuncId" :params="params" @hideSubFunc="hidePopupSelectSubFunc" @objectSelected="objectSelected" @clearSelect="clearSelect"/>
        </el-dialog>
	</div>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref, defineAsyncComponent, shallowRef } from 'vue';
import { ElMessageBox, ElMessage } from 'element-plus'
import {getDynamicComponet} from '../../funcTpl/js/common.js';

export default {
	components: {
		
	},
	
    data() {    
    	const businessSystemGrant = ref({});
        const userName = ref(null);
        const businessSystems = ref(null);

        const dynamicComponent = ref(null);
        const showDynamicComponent = ref(false);
        const popupSubFuncId = ref("c6153ad03ac64a9097fc07ba68932b4c");
        const params = ref(null);

        const validateRules = ref({
            "userId": [
                { required: true, message: '授权用户不能为空！', trigger: 'blur' }
            ]
        });

        return {
            businessSystemGrant,
            userName,
            businessSystems,
            validateRules,

            dynamicComponent,
            showDynamicComponent,
            popupSubFuncId,
            params
        }
    },
    watch: {
        'businessSystemGrant.userId' : function(value, old) {
            this.loadBusinessSystemGrantInfo(value);
        }
    },
    mounted() {
    	axiosClient.get("/lcdp-proxy/lowcode/platform/be/api/businessSystem/list").then((response) => {
            var data = response.data;
            if(data != null && data.status == "200" && data.data != null && data.data.length != null) {
                this.businessSystems = data.data;
                if(this.businessSystems != null && this.businessSystems.length > 0) {
                    this.businessSystems.sort((a,b)=> {
                        const dateA = new Date(a.createTime);
                        const dateB = new Date(b.createTime);
                        if(dateA == dateB) {
                            return 0;
                        }else if(dateA < dateB) {
                            return -1;
                        }else {
                            return 1;
                        }
                    });
                }
            }
        }).catch((ex) => {
            ElMessage.error(`非法访问！` + ex);
        });
    },
    methods: {
    	saveBusinessSystemGrant() {
            this.$refs.businessSystemGrantForm.validate((valid, fields)=> {
                if(valid) {
                    ElMessageBox.confirm('是否进行业务系统授权操作？').then(() => {
                        var url = "/lcdp-proxy/lowcode/platform/be/api/businessSystem/grant";
                        axiosClient.post(url, this.businessSystemGrant).then((response) => {
                            var data = response.data; 
                            if(data != null && data.status == "200" && data.data > 0) {
                                ElMessage(`业务系统授权成功！请提醒用户重新登录以加载权限信息！`);
                            }
                        }).catch((ex) => {
                            ElMessage.error(`业务系统授权失败！` + ex);
                        });   
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
        },
        loadBusinessSystemGrantInfo() {
            if(this.businessSystemGrant.userId == null || this.businessSystemGrant.userId.trim().length == 0) {
                this.businessSystemGrant.grantedSystems = null;
                return;
            }
            var url = "/lcdp-proxy/lowcode/platform/be/api/businessSystem/loadGrantInfo/" + this.businessSystemGrant.userId;
            axiosClient.post(url).then((response) => {
                var data = response.data; 
                if(data != null && data.status == "200" && data.data != null && data.data.grantedSystems != null) {
                    this.businessSystemGrant.grantedSystems = [];
                    for(var i in data.data.grantedSystems) {
                        for(var j in this.businessSystems){
                            if(data.data.grantedSystems[i] == this.businessSystems[j].id) {
                                this.businessSystemGrant.grantedSystems.push(data.data.grantedSystems[i]);
                                break;
                            }
                        }
                    }
                }
            }); 
        },
        popupSelect() {
            axiosClient.get("/lcdp-proxy/lowcode/fe/api/funcDefine/loadFuncDefineAndTpl/" + this.popupSubFuncId).then((response) => {
                var data = response.data; 
                if(data != null && data.data != null && data.data.id != null) {
                    this.dynamicComponent = shallowRef(defineAsyncComponent(getDynamicComponet(data.data.funcTemplate.url)));
                    this.params = escape(JSON.stringify({}));
                    this.showDynamicComponent = true;
                }
            });
        },
        hidePopupSelectSubFunc() {
            this.showDynamicComponent = false;
            this.dynamicComponent = null;
        },
        clearSelect() {
            this.businessSystemGrant.userId = null;
            this.userName = null;

            this.showDynamicComponent = false;
            this.dynamicComponent = null;
        },
        objectSelected(data) {
        	if(data != null) {//如果返回对象不为空
        		this.businessSystemGrant.userId = data.id;
                this.userName = data.userName + "(" + data.realName + ")";
            }
        	
        	this.showDynamicComponent = false;
            this.dynamicComponent = null;
        }
    }
 }
</script>