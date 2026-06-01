<template>
	<div class="wrapper">
		<div style="margin: 200px auto; background-color: #fff; width: 350px; height: 300px; padding: 20px; border-radius: 10px;">
			<div style="margin: 20px 0; text-align: center; font-size: 24px;"><b>用户登录</b></div>
			<el-form ref="loginForm" :model="loginForm" :rules="loginRules" label-width="45px" class="login-form">
				<el-input size="medium" style="margin: 10px 0;" placeholder="请输入用户名" prefix-icon="el-icon-user" v-model="loginForm.username"></el-input>
				<el-input size="medium" style="margin: 10px 0;" placeholder="请输入密码" prefix-icon="el-icon-lock" show-password v-model="loginForm.password"></el-input>
				<div style="margin: 10px 0; text-align: right;">
					<el-button type="primary" size="small" autocomplete="off" @click="submitForm('loginForm')">登录</el-button>
					<el-button type="warning" size="small" autocomplete="off" @click="goToRegister">注册</el-button>
				</div>
			</el-form>
		</div>
	</div>
</template>

<script>
import axios from 'axios';

export default {
	name: "Login",
	data() {
		return {
			// 表单数据模型
			loginForm: {
				username: '',
				password: ''
			},
			// 表单验证规则
			loginRules: {
			    username: [
			        { required: true, message: '请输入用户名', trigger: 'blur' },
			        { min: 1, max: 30, message: '长度在 1 到 30 个字符', trigger: 'blur' }
			    ],
			    password: [
			        { required: true, message: '请输入密码', trigger: 'blur'},
			        { min: 1, max: 30, message: '长度在 1 到 30 个字符', trigger: 'blur' }
			    ]
			}
		} 
	},  //data结束
	methods: {
		submitForm(formName) {
		    this.$refs[formName].validate((valid) => {
		        if (valid) {
		            // 如果验证通过，弹出登录信息
		            //alert('提交登录信息: ' + JSON.stringify(this.loginForm));
		            // 这里应该添加登录的 API 调用逻辑
		            let _this = this;
		            axios.get('http://localhost:8181/user/Login',{params:_this.loginForm}).then(function(resp){
						//console.log(resp.data.data);  //获取查询出的数据并打印
		                if(resp.data.code == -2){
		                    _this.$alert('密码错误','提示',{
		                        confirmButtonText:'确定'
		                    });
		                }
		                if(resp.data.code == -1){
		                    _this.$alert('用户不存在','提示',{
		                        confirmButtonText:'确定'
		                    });
		                }
		                if(resp.data.code == 0){
		                    _this.$router.replace('/');  //去主页
		                }
						if(resp.data.code == 1){  //普通用户登录成功
						    _this.$router.replace('/');  //去主页
						}
		            });
		        } else {
		            // 如果验证失败，打印错误信息到控制台
		            console.log('表单验证失败');
		            return false;
		        }
		    });
		},
		goToRegister() {
		    this.$router.replace('/register');
		}
	}
}
</script>

<style>
	.wrapper {
		height: 100vh;
		background-image: url('../assets/login_bg.jpg');
		background-size: cover;
		overflow: hidden;
	}
</style>