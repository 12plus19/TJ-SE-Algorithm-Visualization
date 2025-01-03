<template>
  <div class="user-info-container">
    <div class="user-info-box">
      <h2>个人信息</h2>
      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="error" class="error">{{ error }}</div>
      <div v-else class="user-details">
        <div class="info-section">
          <h3>基本信息</h3>
          <div class="info-item">
            <label>邮箱：</label>
            <span>{{ userInfo.email }}</span>
          </div>
          <div class="info-item">
            <label>用户ID: </label>
            <span>{{ userInfo.userId }}</span>
          </div>
          <div class="info-item">
            <label>密码: </label>
            <span>{{ userInfo.password }}</span>
          </div>
        </div>

        <div class="progress-section">
          <h3>学习进度</h3>
          <div class="progress-bar">
            <div 
              class="progress-fill" 
              :style="{ width: `${userInfo.progress}%` }"
            ></div>
            <span class="progress-text">{{ userInfo.progress }}%</span>
          </div>
          <div class="progress-details">
            <div class="progress-item">
              <label>已完成课程：</label>
              <span>{{ userInfo.completedCourses }} 个</span>
            </div>
            <div class="progress-item">
              <label>总课程数：</label>
              <span>{{ userInfo.totalCourses }} 个</span>
            </div>
          </div>

          <!-- 添加详细的学习进度信息 -->
          <div class="learning-details" v-if="userInfo.learningDetails">
            <h4>详细进度</h4>
            <div v-for="(item, index) in userInfo.learningDetails" :key="index" class="learning-item">
              <div class="algorithm-info">
                <span>算法 {{ item.algorithmId }}</span>
                <div class="detail-progress-bar">
                  <div 
                    class="detail-progress-fill" 
                    :style="{ width: `${item.progress}%` }"
                  ></div>
                  <span class="detail-progress-text">{{ item.progress }}%</span>
                </div>
              </div>
              <div class="learning-status">
                <span :class="{ 'status-complete': item.read }">阅读{{ item.read ? '已完成' : '未完成' }}</span>
                <span :class="{ 'status-complete': item.watched }">观看{{ item.watched ? '已完成' : '未完成' }}</span>
                <span :class="{ 'status-complete': item.written }">编写{{ item.written ? '已完成' : '未完成' }}</span>
                <span :class="{ 'status-complete': item.done }">练习{{ item.done ? '已完成' : '未完成' }}</span>
              </div>
            </div>
          </div>
        </div>

        <button class="change-password-btn" @click="showChangePasswordModal">
          修改密码
        </button>
      </div>
    </div>

    <!-- 修改密码模态框 -->
    <div v-if="showModal" class="modal-overlay">
      <div class="modal">
        <h3>修改密码</h3>
        <form @submit.prevent="handlePasswordChange">
          <div class="form-group">
            <label for="oldPassword">原密码</label>
            <input
              id="oldPassword"
              v-model="passwordForm.oldPassword"
              type="password"
              placeholder="请输入原密码"
            />
          </div>
          <div class="form-group">
            <label for="newPassword">新密码</label>
            <input
              id="newPassword"
              v-model="passwordForm.newPassword"
              type="password"
              placeholder="请输入新密码"
            />
          </div>
          <div class="form-group">
            <label for="confirmPassword">确认新密码</label>
            <input
              id="confirmPassword"
              v-model="passwordForm.confirmPassword"
              type="password"
              placeholder="请确认新密码"
            />
          </div>
          <div class="modal-buttons">
            <button type="submit" class="submit-btn">确认修改</button>
            <button type="button" class="cancel-btn" @click="showModal = false">
              取消
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

export default {
  name: 'UserInfo',
  setup() {
    const router = useRouter();
    const userInfo = ref({});
    const loading = ref(true);
    const error = ref(null);
    const showModal = ref(false);
    const password = JSON.parse(localStorage.getItem('userData'))?.password;
    const passwordForm = ref({
      oldPassword: '',
      newPassword: '',
      confirmPassword: ''
    });

    // 检查用户是否登录
    const checkAuth = () => {
      const token = localStorage.getItem('userToken');
      if (!token) {
        router.push('/login');
      }
      return token;
    };

    const token = checkAuth();
    const userId = localStorage.getItem('userId');

    console.log('userId:', userId);
    console.log('token:', token);

    // 获取用户信息
    const fetchUserInfo = async () => {
      if (!token || !userId) {
        error.value = '认证信息缺失';
        loading.value = false;
        return;
      }

      try {
        // 获取基本信息
        const userResponse = await axios.get(`http://121.43.120.166:10020/user/info/${userId}`, {
          headers: {
            'Authorization': token
          }
        });

        let progressData = [];
        try {
          // 获取学习进度信息
          const encodeUserId = encodeURIComponent(userId);
          const progressResponse = await axios.get(`http://121.43.120.166:10020/learningProgress/all/${encodeUserId}`, {
            headers: {
              'Authorization': token
            }
          });
          progressData = Array.isArray(progressResponse.data) ? progressResponse.data : [];
        } catch (progressErr) {
          // 如果获取进度数据失败，使用空数组作为默认值
          console.log('无法获取学习进度数据，使用默认值');
          progressData = [];
        }

        // 计算平均学习进度
        const totalProgress = progressData.reduce((sum, item) => {
          const progress = parseFloat(item.progress) || 0;
          return sum + progress;
        }, 0);
        const averageProgress = progressData.length > 0 
          ? Math.round((totalProgress / progressData.length) * 100) / 100
          : 0;

        // 计算完成的课程数量
        const completedCourses = progressData.filter(item => 
          item.read && item.watched && item.written && item.done
        ).length;

        userInfo.value = {
          ...userResponse.data,
          progress: averageProgress,
          completedCourses: completedCourses,
          totalCourses: progressData.length,
          learningDetails: progressData // 保存详细的学习进度数据
        };

        loading.value = false;
      } catch (err) {
        console.error('Error details:', err);
        error.value = '获取用户信息失败';
        loading.value = false;
      }
    };

    // 修改密码
    const handlePasswordChange = async () => {
      const token = checkAuth();
      if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
        alert('两次输入的新密码不一致');
        return;
      }

      try {
        await axios.put(
          'http://121.43.120.166:10020/user/changePassword',null,
          {
            headers: {
              'Authorization': token
            },
            params: {
              userId: userId,
              oldPassword: passwordForm.value.oldPassword,
              newPassword: passwordForm.value.newPassword
            }
          }
        );
        alert('密码修改成功');
        showModal.value = false;
        passwordForm.value = {
          oldPassword: '',
          newPassword: '',
          confirmPassword: ''
        };
      } catch (err) {
        alert(err.response?.data?.message || '密码修改失败');
      }
    };

    const showChangePasswordModal = () => {
      showModal.value = true;
    };

    onMounted(() => {
      fetchUserInfo();
    });

    return {
      userInfo,
      loading,
      error,
      showModal,
      passwordForm,
      password,
      handlePasswordChange,
      showChangePasswordModal
    };
  }
};
</script>

<style scoped>
.user-info-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f0f2f5;
  padding: 20px;
}

.user-info-box {
  width: 100%;
  max-width: 600px;
  padding: 40px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  border-radius: 8px;
}

.loading, .error {
  text-align: center;
  padding: 20px;
}

.info-section, .progress-section {
  margin-bottom: 30px;
}

.info-item {
  margin: 10px 0;
  display: flex;
  align-items: center;
}

.info-item label {
  width: 80px;
  font-weight: bold;
}

.progress-bar {
  height: 20px;
  background-color: #f0f0f0;
  border-radius: 10px;
  overflow: hidden;
  position: relative;
  margin: 15px 0;
}

.progress-fill {
  height: 100%;
  background-color: #409eff;
  transition: width 0.3s ease;
}

.progress-text {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #fff;
  font-size: 12px;
}

.progress-details {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
}

.change-password-btn {
  width: 100%;
  padding: 10px;
  background-color: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin-top: 20px;
}

.change-password-btn:hover {
  background-color: #66b1ff;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
}

.modal {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  width: 90%;
  max-width: 400px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
}

.form-group input {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.modal-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
}

.submit-btn, .cancel-btn {
  padding: 8px 20px;
  border-radius: 4px;
  cursor: pointer;
}

.submit-btn {
  background-color: #409eff;
  color: #fff;
  border: none;
}

.cancel-btn {
  background-color: #fff;
  color: #666;
  border: 1px solid #ddd;
}

.submit-btn:hover {
  background-color: #66b1ff;
}

.cancel-btn:hover {
  background-color: #f5f5f5;
}
</style>