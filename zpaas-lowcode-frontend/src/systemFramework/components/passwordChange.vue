<!-- 密码修改 -->
<template>
<el-dialog v-model="showFlag" title="密码修改">
    <el-form  :model="passwordChange" label-width="120px" :rules="validateRules" ref="passwordChangeForm">
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="用户名" required prop="userName">
                    <el-input v-model="passwordChange.userName" readonly size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="原密码" prop="passwordOrigin">
                    <el-input v-model="passwordOrigin" type="password" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="新密码" required prop="newPassword">
                    <el-input v-model="passwordChange.newPassword" type="password" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
        <el-row :gutter="4">
            <el-col :span="24">
                <el-form-item label="新密码确认" required prop="newPasswordConfirm">
                    <el-input v-model="passwordChange.newPasswordConfirm" type="password" size="small"/>
                </el-form-item>
            </el-col>
        </el-row>
    </el-form>
    <template #footer>
        <span class="dialog-footer">
            <el-button @click="hidePasswordChangePage()">取消</el-button>
            <el-button type="primary" @click="changePassword()">修改</el-button>
        </span>
    </template>
</el-dialog>

</template>

<script>
import axiosClient from '/src/js/utils/axios.js';
import { ref } from 'vue';
import { ElMessage } from 'element-plus'
import sha256 from 'crypto-js/sha256';

export default {
    props: ['passwordChange', 'showPasswordChangePage'],
    
    emits: [ 'hidePasswordChangePage'],
    
    setup(props, {attrs, emit, slots}) {
        const hidePasswordChangePage = () => {
            emit('hidePasswordChangePage');
        };
        
        return {
        	hidePasswordChangePage
        }
    },
    computed: {
        showFlag: {
            get() {
                return this.showPasswordChangePage
            },
            set(value) {
                this.showPasswordChangePage();
            }
        }
    },
    data() {
    	const passwordOrigin = ref("");
        const validateRules = ref({
            "userName": [
                { required: true, message: '用户名不能为空！', trigger: 'blur' }
            ],
            "newPassword": [
                { required: true, message: '新密码不能为空！', trigger: 'blur' }
            ],
            "newPasswordConfirm": [
                { required: true, message: '新密码确认不能为空！', trigger: 'blur' }
            ]
        });

        return {
        	passwordOrigin,
            validateRules
        }
    },
    
    methods: {
    	changePassword() {
            var url = "/lcdp-proxy/lowcode/service/sm/user/changePassword";
            
            if(this.passwordChange.newPassword != this.passwordChange.newPasswordConfirm) {
                ElMessage.error(`输入的两次新密码不一致！`);
                return;
            }
            if(this.passwordOrigin == null || this.passwordOrigin.trim().length == 0) {
                ElMessage.error(`原密码不能为空！`);
                return;
            }
            
            var timestamp=new Date().getTime();
            var encodedPwd = sha256(this.passwordOrigin).toString();
            this.passwordChange.password = sha256(encodedPwd+timestamp).toString();
            this.passwordChange.requestTime = timestamp
            
            this.$refs.passwordChangeForm.validate((valid, fields)=> {
                if(valid) {
                    axiosClient.post(url, {"reqData": {"passwordChangeVo": this.passwordChange}}).then((response) => {
                        var data = response.data; 
                        if(data != null && data.data > 0) {
                            ElMessage(`修改密码成功！`);
                            this.hidePasswordChangePage();
                        }else {
                            ElMessage.error(`修改用户密码失败！`);
                        }
                        
                    });
                }else {
                    console.log("参数校验失败！", fields);
                }
            });
            
        }
    }
}

</script>
