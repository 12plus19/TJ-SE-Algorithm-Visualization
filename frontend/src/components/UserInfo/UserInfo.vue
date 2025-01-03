<template>
  <div class="user-info-container">
    <div class="user-info-box">
      <h2 class="title-container">
    <button class="back-to-visualization-btn" @click="goToVisualizationPage">
        返回可视化页面
    </button>
    <span class="title">个人信息</span>
</h2>
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
              :style="{ width: `${userInfo.progress*100 }%` }"
            ></div>
            <span class="progress-text">{{ userInfo.progress * 100}}%</span>
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
                    :style="{ width: `${item.progress*100}%` }"
                  ></div>
                  <span class="detail-progress-text">{{ item.progress*100}}%</span>
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

    const goToVisualizationPage = () => {
      router.push('/algorithm-visual1');
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
      showChangePasswordModal,
      goToVisualizationPage
    };
  }
};
</script>

<style scoped>
 .title-container {
    display: flex;
    justify-content: center;  /* 水平居中 */
    align-items: center;      /* 垂直居中 */
    position: relative;
  }

  .back-to-visualization-btn {
    position: absolute;
    left: 0;                  /* 按钮固定在左侧 */
    margin-left: 20px;        /* 适当的左侧间距 */
  }

  .title {
    margin-left: 40px;        /* 给个人信息加点间距，避免和按钮重叠 */
  }

.user-info-container {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  min-height: 100vh;
  background-color: #f5f7fa;
  padding: 40px 20px;
}

.user-info-box {
  width: 100%;
  max-width: 800px;
  padding: 40px;
  background: #fff;
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
  border-radius: 12px;
}

h2 {
  text-align: center;
  color: #2c3e50;
  font-size: 28px;
  margin-bottom: 40px;
  font-weight: 600;
}

h3 {
  color: #409eff;
  font-size: 20px;
  margin: 30px 0 20px;
  padding-bottom: 10px;
  border-bottom: 2px solid #ebeef5;
}

.back-to-visualization-btn {
  background-color: #007BFF; /* 蓝色背景 */
  color: white; /* 白色文字 */
  font-weight: bold; /* 加粗文字 */
  padding: 10px 20px; /* 内边距 */
  border: none; /* 无边框 */
  border-radius: 5px; /* 圆角 */
  cursor: pointer; /* 鼠标指针 */
}

.back-to-visualization-btn:hover {
  background-color: #0056b3; /* 悬停时更深的蓝色背景 */
}

.info-section {
  background-color: #f8f9fa;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 30px;
}

.info-item {
  display: flex;
  align-items: center;
  margin: 16px 0;
  font-size: 15px;
}

.info-item label {
  min-width: 100px;
  color: #606266;
  font-weight: 500;
}

.info-item span {
  color: #303133;
  margin-left: 20px;
}

.progress-section {
  background-color: #f8f9fa;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 30px;
}

.progress-bar {
  height: 24px;
  background-color: #e4e7ed;
  border-radius: 12px;
  overflow: hidden;
  position: relative;
  margin: 20px 0;
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
  font-size: 14px;
  font-weight: 500;
}

.progress-details {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin: 20px 0;
}

.progress-item {
  display: flex;
  align-items: center;
  background-color: #fff;
  padding: 16px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.progress-item label {
  color: #606266;
  font-weight: 500;
  margin-right: 12px;
}

.progress-item span {
  color: #409eff;
  font-weight: 600;
}

.learning-details {
  margin-top: 30px;
}

.learning-details h4 {
  color: #2c3e50;
  font-size: 18px;
  margin-bottom: 20px;
}

.learning-item {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.algorithm-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}

.detail-progress-bar {
  flex: 1;
  height: 20px;
  background-color: #e4e7ed;
  border-radius: 10px;
  overflow: hidden;
  margin: 0 20px;
  position: relative;
}

.detail-progress-fill {
  height: 100%;
  background-color: #409eff;
  transition: width 0.3s ease;
}

.detail-progress-text {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  color: #fff;
  font-size: 12px;
}

.learning-status {
  display: flex;
  gap: 20px;
  margin-top: 12px;
}

.learning-status span {
  padding: 6px 12px;
  border-radius: 4px;
  font-size: 13px;
  background-color: #f0f2f5;
  color: #909399;
}

.status-complete {
  background-color: #f0f9eb !important;
  color: #67c23a !important;
}

.change-password-btn {
  width: 100%;
  padding: 14px;
  background-color: #409eff;
  color: #fff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  margin-top: 30px;
}

.change-password-btn:hover {
  background-color: #66b1ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(64,158,255,0.3);
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
  z-index: 1000;
}

.modal {
  background: #fff;
  padding: 30px;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 4px 16px rgba(0,0,0,0.2);
}

.modal h3 {
  text-align: center;
  margin-bottom: 24px;
  color: #2c3e50;
}

.modal .form-group {
  margin-bottom: 20px;
}

.modal input {
  width: 100%;
  padding: 12px;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.modal input:focus {
  border-color: #409eff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(64,158,255,0.2);
}

.modal-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 30px;
}

.modal-buttons button {
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.submit-btn {
  background-color: #409eff;
  color: #fff;
  border: none;
}

.submit-btn:hover {
  background-color: #66b1ff;
  transform: translateY(-1px);
}

.cancel-btn {
  background-color: #f4f4f5;
  color: #606266;
  border: none;
}

.cancel-btn:hover {
  background-color: #e9e9eb;
  transform: translateY(-1px);
}
</style>