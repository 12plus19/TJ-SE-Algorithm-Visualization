<template>
  <div class="login-container">
    <div class="login-box">
      <h2>AI&nbsp;算</h2>
      <form @submit.prevent="handleLogin">
        <div class="form-group">
          <label for="email">邮箱</label>
          <input id="email" v-model="email" type="text" placeholder="请输入邮箱" />
        </div>
        <div class="form-group">
            <label for="password">密码</label>
            <input id="password" v-model="password" type="password" placeholder="请输入密码" />
            </div>
            <div class="form-group" style="display: flex; justify-content: space-between; align-items: center;">
            <button type="button" class="change-password-btn" @click="goToChangePassword">修改密码</button>
            </div>
        <button type="submit" class="login-btn">登录</button>
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
          localStorage.setItem('userToken', token);
          alert('登录成功', token);
          console.log('登录成功:', token);
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

    return {
      email,
      password,
      handleLogin
    };
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100vh;
  background-color: #f0f2f5;
}

.login-box {
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

.login-btn {
  width: 100%;
  padding: 10px;
  background-color: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.login-btn:hover {
  background-color: #66b1ff;
}
</style>