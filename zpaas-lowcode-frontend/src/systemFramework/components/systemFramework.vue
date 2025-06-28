<template>
<div class="common-layout">
<el-container class="layout-container-main"> 
    <!-- header begin -->
    <el-header style="text-align: right; font-size: 12px; width: 100%;" height="52px">
	    <div class="toolbar2">
	        <el-row :gutter="4">
	            <el-col :span="5" style="width:260px;"  class="toolbar1">
	                <el-icon  size="35px"><grid /></el-icon>
	                <label style="font-size:22px">zPaaS低代码开发平台</label>       
	            </el-col>
	            <el-col :span="15" style="width:900px">
	                <div class="toolbar" style="width:100%">
	                    <label style="font-size:22px;" ></label>
	                </div>
	            </el-col>
	            <el-col :span="4" style="width:200px;">
	                <div class="toolbar" style="width:100%;">
	                    <el-dropdown>
	                        <el-icon style="margin-right: 8px; margin-top: 1px"><setting color="#ffffff"/></el-icon>
	                        <template #dropdown>
	                            <el-dropdown-menu>
	                                <el-dropdown-item @click="changePassword()">修改密码</el-dropdown-item>
	                                <el-dropdown-item @click="exit()">退出登录</el-dropdown-item>
	                            </el-dropdown-menu>
	                        </template>
	                    </el-dropdown>
	                    <span>{{this.loginInfo.loginUser.realName}}，您好！</span>
	                </div>   
	            </el-col>
	        </el-row>   
	    </div>   
	</el-header>
	<!-- header end -->
	<el-divider style="margin-top: 1px;margin-bottom: 1px"></el-divider>
    <el-container style="height: 90vh;">
        <!-- left side begin -->
        <el-aside width="235px">
		    <systemFrameworkNavigate :loginInfo="loginInfo" @handleClick="handleClick"/>
		</el-aside>
        <!-- left side end -->
        <el-divider direction="vertical" border-style="solid" style="height:100%;margin-left: 1px;margin-right: 1px"></el-divider>
        
	    <el-main>
            <div  style="width:100%; overflow-x: hidden">
	            <br/>
		        <el-row :gutter="4">
                   <el-col :span="24">
                       <el-breadcrumb separator=">" v-if="currentMenu != null && currentMenu.url != null">
                           <el-breadcrumb-item ><span class="title">菜单</span></el-breadcrumb-item>
                           <el-breadcrumb-item ><span style="color:rgba(90, 156, 248, 1)">{{this.currentMenu.rightName}}</span></el-breadcrumb-item>
                        </el-breadcrumb>
                   </el-col>
                </el-row>
		        <br/>
	            <el-row :gutter="4">
                   <el-col :span="24"> 
                        <!--<iframe id="menuFrame" v-if="currentMenu.url1 != null" width="100%" height="100%" frameborder="0" :src="currentMenu.url1"/>-->
                        <micro-app 
                            name="systemFramework-app"
                            :url="url"
                            @created='created'
                            @beforemount='beforemount'
                            @mounted='mounted'
                            @unmount='unmount'
                            @error='error'
                            :data="sendData"
                            @datachange='handleDataChange'
                            iframe
                            >
                        </micro-app>
                   </el-col>
                </el-row>
            </div>
        </el-main>
    </el-container>
</el-container>
</div>

<!-- 修改密码 -->
<passwordChange v-if="showPasswordChangePage==true && passwordChangeVo != null" @hidePasswordChangePage="hidePasswordChangePage" :passwordChange="passwordChangeVo" :showPasswordChangePage="showPasswordChangePage"/>
</template>
<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import systemFrameworkNavigate from './systemFramework-navigate.vue'
import passwordChange from './passwordChange.vue'
import micro  from '@micro-zoe/micro-app';

export default {
    components: {
    	systemFrameworkNavigate,
    	passwordChange,
    },
    
    data() {  
    	const loginInfo = ref({"loginUser":{},"menuRights":[]});
        const currentMenu = ref({});
        const showPasswordChangePage = ref(false);
        const passwordChangeVo = ref(null);
        const url = ref(null);
        const mounted1 = ref(false);
        const mountCount = ref(0)
        
        const sendData = ref({
            data:'基座给子应用的数据string'
        });

    	
        return {
        	loginInfo,
        	currentMenu,
        	passwordChangeVo,
            showPasswordChangePage,
            sendData,
            url,
            mounted1,
            mountCount
        }
    },
    mounted() {
        this.loadUserInfo();
    },
    methods: {
    	loadUserInfo() {
            axiosClient.post("/lcdp-proxy/lowcode/service/sm/loadUserInfo", {}).then((response) => {
                var data = response.data; 
                if(data != null && data.data != null && data.data.loginUser != null) {
                	this.loginInfo = data.data;
                }else {
                	window.location = "/login.html";
                }
            }).catch((e) => {
            	console.log(e);
            	window.location = "/login.html";
            });
    	},
    	exit() {
    		axiosClient.post("/lcdp-proxy/lowcode/service/sm/logout", {}).then((response) => {
    			window.location = "/login.html";
            }).catch((e) => {
                console.log(e);
                window.location = "/login.html";
            });
    	},
        
    	handleClick(right) {
            this.currentMenu = right;
            this.url = this.currentMenu.url;
            //console.log("change menu to : ", this.currentMenu);
            this.reoladSubPage();
        },

        reoladSubPage() {
            setTimeout(()=> {
                if(!this.mounted1) {
                    micro.reload('systemFramework-app');
                    this.mountCount++;
                    if(this.mountCount < 30) {
                        this.reoladSubPage();
                    }
                }
            }, 1500);
        },
    	hidePasswordChangePage() {
            this.passwordChangeVo = null;
            this.showPasswordChangePage = false;
            this.rawPassword = null;
        },
        changePassword() {
        	this.passwordChangeVo = {};
            this.passwordChangeVo.userId = this.loginInfo.loginUser.id;
            this.passwordChangeVo.userName = this.loginInfo.loginUser.userName;
            this.showPasswordChangePage = true;
        },
        handleDataChange(e){
            console.log('来自子应用的数据：', e.detail.data)
        },

        sendChildData(){
            micro.setData('app1', {type: '新的数据'})
        },

        created(e){
            //console.log('micro-app元素被创建',e );
        },

        beforemount(e){
            //console.log('即将被渲染', e);
        },
        mounted(e){
            //console.log(this.mountCount, '已经渲染完成');
            this.mounted1 = true;
            this.mountCount = 0;
        },

        unmount(e){
            //console.log('已经卸载', e);
            this.mounted1 = false;
        },

        error(e){
            console.log('渲染出错', e);
        }
    }
}
</script>

<style scoped>
.el-menu-vertical-demo:not(.el-menu--collapse) {
  width: 200px;
  min-height: 400px;
}


.layout-container-main .el-header {
  position: relative;
  background-image: linear-gradient(to right, rgba(97, 116, 209, 1), rgba(90, 156, 248, 1));
  background-size: cover; 
  color:#ffffff
}
.layout-container-main .el-aside {
  color: var(--el-text-color-primary);
  background: #ffffff;
}
.layout-container-main .el-menu {
  border-right: none;
}
.layout-container-main .el-menu .el-menu-item {
  height:38px;
  line-height:38px;
}
.layout-container-main .el-main {
  padding: 0;
}

.layout-container-main .toolbar1 {
  display: inline-flex;
  align-items: center;
  justify-content: left;
  height: 100%;
  right: 20px;
}

.layout-container-main .toolbar {
  display: inline-flex;
  align-items: center;
  justify-content: center; 
  height: 100%;
  right: 20px;
}

.layout-container-main .toolbar-right {
  display: inline-flex;
  align-items: center;
  justify-content: right; 
  height: 100%;
  right: 20px;
}

.layout-container-main .toolbar2 {
  width: 100%;
  display: inline-flex;
  align-items: center;
  justify-content: center; 
  height: 100%;
  right: 20px;
}

.title {
  position: relative;
  padding-left: 13px;
  margin: 24px 0;
}

.title:before {
  content: "";
  background-color: rgba(90, 156, 248, 1);
  width: 4px;
  height: 14px;
  position: absolute;
  left: 0;
  top: 50%;
  margin-top: -8px;
  -webkit-border-radius: 3px;
  -moz-border-radius: 3px;
  border-radius: 3px;
  margin-left: 3px;
}
</style>