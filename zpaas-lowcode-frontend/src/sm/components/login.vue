

<template>
<el-container>
	<div id="login-box">
		<h1>zPaaS低代码开发平台</h1>
		
	<el-form label-width="60px">
        <el-row :gutter="4">
            <el-col :span="24">              
                <el-form-item label="用户名">
                    <template #label>
	                    <el-icon size="25px"><user /></el-icon>
	                </template>
                    <el-input v-model="userName" placeholder="请输入用户名" style="width:95%" size="large"/>
                </el-form-item>
            </el-col>  
        </el-row>
        <el-row :gutter="24">
            <el-col :span="24">
                <el-form-item label="密码">
                    <template #label>
                        <el-icon size="25px"><lock /></el-icon>
                    </template>
                    <el-input v-model="rawPassword" type="password" placeholder="请输入密码"  @keyup.enter="login()"  style="width:95%" size="large"/>
                </el-form-item>
            </el-col>           
        </el-row>
        <el-row :gutter="24">
            <el-col :span="24">
                <el-button size="large" @click="login()">登&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;录</el-button>
            </el-col>
        </el-row>
    </el-form>
    </div>
</el-container>

<!-- 修改密码 -->
<passwordChange v-if="showPasswordChangePage==true && passwordChangeVo != null" @hidePasswordChangePage="hidePasswordChangePage" :passwordChange="passwordChangeVo" :showPasswordChangePage="showPasswordChangePage"/>


</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'
import sha256 from 'crypto-js/sha256';

import passwordChange from '../../systemFramework/components/passwordChange.vue'

export default {
    components: {
    	passwordChange
    },
    
    data() {    
    	const loginRequest = ref({});
    	const userName = ref("");
    	const rawPassword = ref("");
    	const showPasswordChangePage = ref(false);
    	const passwordChangeVo = ref(null);
        
        return {
        	loginRequest,
        	userName,
        	rawPassword,
        	passwordChangeVo,
        	showPasswordChangePage
        }
    },
    mounted() {
        
    },
    methods: {
        login() {
        	if(this.userName == null || this.userName.trim().length == 0 || 
        			this.rawPassword == null || this.rawPassword.trim().length == 0) {
        		ElMessage.error(`请输入用户名和密码！`);
        		return;
        	}
        	var timestamp=new Date().getTime();
        	var encodedPwd = sha256(this.rawPassword).toString();
        	this.loginRequest.password = sha256(encodedPwd+timestamp).toString();
            this.loginRequest.loginTime = timestamp;
            this.loginRequest.userName = this.userName;
            
            console.log(this.loginRequest);
        	
            axiosClient.post("/lcdp-proxy/lowcode/service/sm/login", 
            		{"reqData": {"loginRequest": this.loginRequest}}, 
            		{headers:{'userName':this.loginRequest.userName,'password':this.loginRequest.password,'loginTime':this.loginRequest.loginTime}}).then((response) => {
                var data = response.data; 
                var result = data.data;
                if(result != null) {
                	if(result.authResult == '1') {
                		window.location="/systemFramework.html";
                	}else if(result.authResult == '2' && result.userId != null) {
                		ElMessage(`请先修改密码！`);
                		this.passwordChangeVo = {};
                		this.passwordChangeVo.userId = result.userId;
                		this.passwordChangeVo.userName = this.loginRequest.userName;
                        this.showPasswordChangePage = true;
                    }else {
                    	ElMessage.error(`登录失败！错误信息：` + result.msg);
                    }
                    
                }else {
                    ElMessage.error(`登录失败！`);
                }
            }).catch((error)=>{
                ElMessage.error(`登录失败！` + error);
            });;          
       },
       hidePasswordChangePage() {
    	   this.passwordChangeVo = null;
    	   this.showPasswordChangePage = false;
    	   this.rawPassword = null;
       }
    }
 }
</script>
<style scoped>


</style>