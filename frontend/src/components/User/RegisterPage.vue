<template>
    <div class="register-container">
      <div class="register-box">
        <h2>注册</h2>
        <form @submit.prevent="handleRegister">
          <div class="form-group">
            <label for="userId">用户名</label>
            <input id="userId" v-model="userId" type="text" placeholder="请输入用户名" />
          </div>
          <div class="form-group">
            <label for="email">邮箱</label>
            <input id="email" v-model="email" type="email" placeholder="请输入邮箱" />
          </div>
          <div class="form-group">
            <label for="verificationCode">验证码</label>
            <div class="verification-code-group">
              <input id="verificationCode" v-model="verificationCode" type="text" placeholder="请输入验证码" class="verification-input" />
              <button type="button" @click="sendVerificationCode" :disabled="isSendingCode" class="send-code-btn">
                {{ isSendingCode ? '发送中...' : '发送验证码' }}
              </button>
            </div>
          </div>
          <div class="form-group">
            <label for="password">密码</label>
            <input id="password" v-model="password" type="password" placeholder="请输入密码" />
          </div>
          <div class="form-group">
            <label for="confirmPassword">确认密码</label>
            <input id="confirmPassword" v-model="confirmPassword" type="password" placeholder="请确认密码" />
          </div>
          <button type="submit" class="register-btn">注册</button>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import axios from 'axios';
  
  export default {
    name: 'RegisterPage',
    setup() {
      const userId = ref('');
      const email = ref('');
      const password = ref('');
      const confirmPassword = ref('');
      const verificationCode = ref('');
      const isSendingCode = ref(false);
      const router = useRouter();
  
      const handleRegister = async () => {
        // 检查所有必需字段是否已填写
        if (!userId.value || !email.value || !password.value || !confirmPassword.value || !verificationCode.value) {
          alert('请填写所有必需字段');
          return;
        }
  
        // 检查邮箱格式
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(email.value)) {
          alert('请输入有效的邮箱地址');
          return;
        }
  
        // 检查密码和确认密码是否一致
        if (password.value !== confirmPassword.value) {
          alert('密码和确认密码不一致');
          return;
        }
  
        // 发送 POST 请求到后端
        try {
          const response = await axios.post('http://121.43.120.166:10020/user/register', 
          {
            userId: userId.value,
            email: email.value,
            password: password.value,

          },
          {
            params: {
              code: verificationCode.value
            },
            headers: {
              'Content-Type': 'application/json'
            },
            timeout: 100000
          });
  
          // 根据需求处理响应
          if (response.status === 200) {
            console.log('注册成功:', response.data);
            alert('用户注册成功');
            // 注册成功后跳转到登录页面
            router.push('/login');
          } else {
            console.error('注册失败:', response.data);
            alert('注册失败，请重试');
          }
        } catch (error) {
          if (error.response) {
            console.error('服务器返回的错误信息:', error.response.data);
            alert(`注册失败: ${error.response.data.error}`);
          } else {
            console.error('请求错误:', error.message);
            alert('注册失败，请重试');
          }
        }
      };
  
      const sendVerificationCode = async () => {
        isSendingCode.value = true;
        console.log('Sending email:', email.value);
        try {
            await axios.post('http://121.43.120.166:10020/user/sendCode', null, {
                params: {
                    email: email.value
                },
                headers: {
                    'Content-Type': 'application/json'
                },
                timeout: 5000
            });
          alert('验证码已发送到您的邮箱');
        } catch (error) {
          console.error('发送验证码失败:', email.value);
          alert('发送验证码失败，请重试', email.value);
        } finally {
          isSendingCode.value = false;
        }
      };
  
      return {
        userId,
        email,
        password,
        confirmPassword,
        verificationCode,
        isSendingCode,
        handleRegister,
        sendVerificationCode
      };
    }
  };
  </script>
  
  <style scoped>
  .register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100vh;
    background-color: #f0f2f5;
  }
  
  .register-box {
    width: 320px;
    padding: 40px;
    background: #fff;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    text-align: center;
    border-radius: 8px;
  }
  
  h2 {
    margin-bottom: 24px;
  }
  
  .form-group {
    text-align: left;
    margin-bottom: 16px;
  }
  
  label {
    display: block;
    margin-bottom: 8px;
  }
  
  input {
    width: 100%;
    padding: 8px;
    box-sizing: border-box;
  }
  
  .register-btn {
    width: 100%;
    padding: 10px;
    background-color: #409eff;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .register-btn:hover {
    background-color: #66b1ff;
  }
  
  .verification-code-group {
    display: flex;
    align-items: center;
  }
  
  .verification-input {
    flex: 1;
    margin-right: 10px;
  }
  
  .send-code-btn {
    padding: 8px 12px;
    background-color: #409eff;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .send-code-btn:disabled {
    background-color: #a0cfff;
    cursor: not-allowed;
  }
  </style>