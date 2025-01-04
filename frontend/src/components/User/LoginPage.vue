<template>
  <div class="login-container">
    <div class="login-box">
      <h2>AI&nbsp;算</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="email">邮箱</label>
          <div class="input-wrapper">
            <i class="el-icon-message"></i>
            <input 
              id="email" 
              v-model="email" 
              type="text" 
              placeholder="请输入邮箱" 
            />
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
        <div class="form-actions">
          <button type="submit" class="login-btn">登录</button>
          <div class="register-link">
            没有账号？<a @click="goToRegister">点击注册</a>
          </div>
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
  name: 'LoginPage',
  setup() {
    const email = ref('');
    const password = ref('');
    const router = useRouter();

    const handleLogin = async () => {
      // 检查所有必需字段是否已填写
      if (!email.value.trim() || !password.value.trim()) {
        alert('请填写所有必需字段');
        return;
      }

      // 发送 POST 请求到后端
      try {
        const response = await axios.post('http://121.43.120.166:10020/user/login', null, {
          params: {
            email: email.value,
            password: password.value
          },
          headers: {
            'Content-Type': 'application/json'
          }
        });

        // 根据需求处理响应
        if (response.status === 200) {
          const token = response.headers['authorization'];
          const userId = response.data.userId;

          localStorage.setItem('userToken', token);
          localStorage.setItem('userId', userId);
          localStorage.setItem('algorithmId', '1');
          alert('登录成功', token);
          console.log('userId:', userId);
          console.log('登录成功:', token);
          console.log('response:', response.data);
          router.push('/algorithm-message');
        } else {
          console.error('登录失败:', response.data);
          alert('登录失败，请重试');
        }
      } catch (error) {
        if (error.response) {
          console.error('服务器返回的错误信息:', error.response.data);
          alert(`登录失败: ${error.response.data.error}`);
        } else {
          console.error('请求错误:', error.message);
          alert('登录失败，请重试');
        }
      }
    };

    const goToRegister = () => {
      router.push('/register');
    };

    return {
      email,
      password,
      handleLogin,
      goToRegister
    };
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 20px;
  background-image: url('../../assets/log.jpg'); /* 添加背景图 */
  background-size: cover;
}
.login-box {
  width: 380px;
  padding: 40px;
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

h2 {
  text-align: center;
  color: #2c3e50;
  margin-bottom: 30px;
  font-size: 24px;
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

.login-btn {
  width: 100%;
  padding: 12px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  margin-bottom: 16px;
}

.login-btn:hover {
  background-color: #66b1ff;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.form-actions {
  margin-top: 30px;
}

.register-link {
  text-align: center;
  margin-top: 16px;
  color: #606266;
  font-size: 14px;
}

.register-link a {
  color: #409eff;
  text-decoration: none;
  font-weight: 500;
  cursor: pointer;
  transition: color 0.3s ease;
}

.register-link a:hover {
  color: #66b1ff;
  text-decoration: underline;
}
</style>