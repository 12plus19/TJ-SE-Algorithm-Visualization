<template>
  <div class="register-container">
    <div class="register-box">
      <h2>AI&nbsp;算</h2>
      <div class="welcome-text">欢迎注册，开启您的算法学习之旅</div>
      
      <form @submit.prevent="handleRegister" class="register-form">
        <div class="form-group">
          <label for="userId">用户名</label>
          <div class="input-wrapper">
            <i class="el-icon-user"></i>
            <input 
              id="userId" 
              v-model="userId" 
              type="text" 
              placeholder="请输入用户名"
              autocomplete="off"
            />
          </div>
        </div>

        <div class="form-group">
          <label for="email">邮箱</label>
          <div class="input-wrapper">
            <i class="el-icon-message"></i>
            <input 
              id="email" 
              v-model="email" 
              type="email" 
              placeholder="请输入邮箱"
              autocomplete="off"
            />
          </div>
        </div>

        <div class="form-group">
          <label for="verificationCode">验证码</label>
          <div class="verification-code-group">
            <div class="input-wrapper code-input">
              <i class="el-icon-key"></i>
              <input 
                id="verificationCode" 
                v-model="verificationCode" 
                type="text" 
                placeholder="请输入验证码"
                autocomplete="off"
              />
            </div>
            <button 
              type="button" 
              @click="sendVerificationCode" 
              :disabled="isSendingCode" 
              class="send-code-btn"
            >
              {{ isSendingCode ? '发送中...' : '发送验证码' }}
            </button>
          </div>
        </div>

        <div class="form-group">
          <label for="password">密码</label>
          <div class="input-wrapper">
            <i class="el-icon-lock"></i>
            <input 
              id="password" 
              v-model="password" 
              type="password" 
              placeholder="请输入密码"
            />
          </div>
        </div>

        <div class="form-group">
          <label for="confirmPassword">确认密码</label>
          <div class="input-wrapper">
            <i class="el-icon-lock"></i>
            <input 
              id="confirmPassword" 
              v-model="confirmPassword" 
              type="password" 
              placeholder="请确认密码"
            />
          </div>
        </div>

        <button type="submit" class="register-btn" :disabled="isSendingCode">注册</button>

        <div class="login-link">
          已有账号？<a href="/login">立即登录</a>
        </div>
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
          router.push('/');
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
              timeout: 100000
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
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7eb 100%);
  padding: 20px;
}

.register-box {
  width: 100%;
  max-width: 440px;
  padding: 40px;
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.register-box:hover {
  transform: translateY(-5px);
}

h2 {
  text-align: center;
  color: #2c3e50;
  font-size: 32px;
  margin-bottom: 10px;
  font-weight: 600;
}

.welcome-text {
  text-align: center;
  color: #606266;
  font-size: 16px;
  margin-bottom: 30px;
}

.register-form {
  margin-top: 30px;
}

.form-group {
  margin-bottom: 24px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #606266;
  font-size: 14px;
  font-weight: 500;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-wrapper i {
  position: absolute;
  left: 12px;
  color: #909399;
  font-size: 16px;
}

input {
  width: 100%;
  padding: 12px 12px 12px 40px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s ease;
  background-color: #f8fafc;
}

input:focus {
  border-color: #409eff;
  background-color: #fff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.verification-code-group {
  display: flex;
  gap: 12px;
}

.code-input {
  flex: 1;
}

.send-code-btn {
  padding: 0 20px;
  height: 42px;
  background: #409eff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.send-code-btn:hover:not(:disabled) {
  background: #66b1ff;
  transform: translateY(-1px);
}

.send-code-btn:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}

.register-btn {
  width: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #409eff 0%, #007fff 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  margin-top: 20px;
  transition: all 0.3s ease;
}

.register-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
}

.register-btn:disabled {
  background: #a0cfff;
  cursor: not-allowed;
}

.login-link {
  text-align: center;
  margin-top: 20px;
  color: #606266;
  font-size: 14px;
}

.login-link a {
  color: #409eff;
  text-decoration: none;
  font-weight: 500;
  transition: color 0.3s ease;
}

.login-link a:hover {
  color: #66b1ff;
  text-decoration: underline;
}

@media (max-width: 480px) {
  .register-box {
    padding: 30px 20px;
  }

  .verification-code-group {
    flex-direction: column;
  }

  .send-code-btn {
    width: 100%;
  }
}
</style>