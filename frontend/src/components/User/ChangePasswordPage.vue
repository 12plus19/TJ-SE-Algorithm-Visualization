<template>
    <div class="change-password-container">
      <div class="change-password-box">
        <h2>修改密码</h2>
        <form @submit.prevent="handleChangePassword">
          <div class="form-group">
            <label for="userId">用户名</label>
            <input id="userId" v-model="userId" type="text" placeholder="请输入用户名" />
          </div>
          <div class="form-group">
            <label for="oldPassword">原密码</label>
            <input id="oldPassword" v-model="oldPassword" type="password" placeholder="请输入原密码" />
          </div>
          <div class="form-group">
            <label for="newPassword">新密码</label>
            <input id="newPassword" v-model="newPassword" type="password" placeholder="请输入新密码" />
          </div>
          <button type="submit" class="change-password-btn">修改密码</button>
        </form>
      </div>
    </div>
  </template>
  
  <script>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import axios from 'axios';
  
  export default {
    name: 'ChangePasswordPage',
    setup() {
      const userId = ref('');
      const oldPassword = ref('');
      const newPassword = ref('');
      const router = useRouter();
  
      const handleChangePassword = async () => {
        // 检查所有必需字段是否已填写
        if (!userId.value.trim() || !oldPassword.value.trim() || !newPassword.value.trim()) {
          alert('请填写所有必需字段');
          return;
        }
  
        // 发送 PUT 请求到后端
        try {
          console.log('发送修改密码请求参数:', {
            userId: userId.value,
            oldPassword: oldPassword.value,
            newPassword: newPassword.value
          });
  
          const response = await axios.put('http://121.43.120.166:10020/user/changePassword', null, {
            params: {
              userId: userId.value,
              oldPassword: oldPassword.value,
              newPassword: newPassword.value
            },
            headers: {
              'Content-Type': 'application/json'
            }
          });
  
          // 根据需求处理响应
          if (response.status === 200) {
            console.log('密码修改成功:', response.data);
            alert('密码修改成功');
            // 密码修改成功后跳转到登录页面
            router.push('/login');
          } else {
            console.error('密码修改失败:', response.data);
            alert('密码修改失败，请重试');
          }
        } catch (error) {
    if (error.response) {
      // 服务器返回的错误信息
      console.error('服务器返回的错误信息:', error.response.data);
      alert(`密码修改失败: ${error.response.data.error || error.response.data.message || '未知错误'}`);
    } else if (error.request) {
      // 请求发出但没有响应
      console.error('请求没有响应:', error.request);
      alert('密码修改失败，请检查网络或稍后重试');
    } else {
      // 其他错误
      console.error('请求错误:', error.message);
      alert('密码修改失败，请重试');
    }
  }
      };
  
      return {
        userId,
        oldPassword,
        newPassword,
        handleChangePassword
      };
    }
  };
  </script>
  
  <style scoped>
  .change-password-container {
    display: flex;
    justify-content: center;
    align-items: center;
    width: 100%;
    height: 100vh;
    background-color: #f0f2f5;
  }
  
  .change-password-box {
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
  
  .change-password-btn {
    width: 100%;
    padding: 10px;
    background-color: #409eff;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
  }
  
  .change-password-btn:hover {
    background-color: #66b1ff;
  }
  </style>