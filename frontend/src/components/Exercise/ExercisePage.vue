<template>
    <div class="exercise-container">
      <div class="exercise-box">
        <h2>算法练习</h2>
        
        <!-- 加载状态 -->
        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="error" class="error">{{ error }}</div>
        
        <!-- 练习题内容 -->
        <div v-else class="exercise-content">
          <!-- 练习进度 -->
          <div class="progress-info">
            <span>当前进度: {{ getAnsweredCount() }}/{{ questions.length }}</span>
            <div class="progress-bar">
              <div 
                class="progress-fill" 
                :style="{ width: `${(getAnsweredCount()/questions.length*100) }%` }"
              ></div>
            </div>
          </div>
  
          <!-- 题目区域 -->
          <div class="question-area">
            <h3>题目 {{ currentQuestion + 1 }}</h3>
            <div class="question-content" style = "white-space: pre-wrap">
              {{ questions[currentQuestion].question }}
            </div>
          </div>
  
          <!-- 答题区域 -->
          <div class="answer-area">
            <label for="answer">你的答案：</label>
            <textarea
              id="answer"
              v-model="userAnswers[currentQuestion]"
              rows="6"
              placeholder="请在此输入你的答案..."
            ></textarea>
          </div>
  
          <!-- 导航按钮 -->
          <div class="navigation-buttons">
            <button 
              @click="previousQuestion" 
              :disabled="currentQuestion === 0"
            >
              上一题
            </button>
            <button 
              v-if="currentQuestion < questions.length - 1" 
              @click="nextQuestion"
            >
              下一题
            </button>
            <button 
              v-else 
              @click="submitExercise" 
              class="submit-btn"
              :loading="submitting"
            >
              提交答案
            </button>
          </div>
  
          <!-- 题目导航 -->
          <div class="question-navigation">
            <button 
              v-for="(_, index) in questions" 
              :key="index"
              :class="[
                'question-nav-btn',
                { 
                  'current': index === currentQuestion,
                  'answered': userAnswers[index]
                }
              ]"
              @click="goToQuestion(index)"
            >
              {{ index + 1 }}
            </button>
          </div>
        </div>
  
        <!-- 结果模态框 -->
        <div v-if="showResults" class="modal-overlay">
          <div class="modal">
            <h3>练习结果</h3>
            <div class="results-content">
              <p class="total-score">总分：{{ totalScore }}</p>
              <div class="score-details">
                <div v-for="(score, index) in questionScores" :key="index" class="score-item">
                  <span>题目 {{ index + 1 }}：</span>
                  <span>{{ score }} 分</span>
                </div>
              </div>
            </div>
            <div class="modal-buttons">
              <button @click="closeResults" class="close-btn">关闭</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { ref, onMounted } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import axios from 'axios';
  import { ElMessage } from 'element-plus';
  
  export default {
    name: 'ExercisePage',
    setup() {
      const route = useRoute();
      const router = useRouter();
      const loading = ref(true);
      const error = ref(null);
      const questions = ref([]);
      const currentQuestion = ref(0);
      const userAnswers = ref([]);
      const showResults = ref(false);
      const questionScores = ref([]);
      const totalScore = ref(0);
      const submitting = ref(false);
      const userToken = localStorage.getItem('userToken');
      const algorithmId = route.query.algorithmId;
      console.log('RoutealgorithmId', route.query.algorithmId);
      console.log('algorithmId', algorithmId);

      const getAnsweredCount = () => {
        return userAnswers.value.filter(answer => answer.trim() !== '').length;
      };
  
      // 获取练习题
      const fetchQuestions = async () => {
        
        console.log('获取题目时的token:', userToken); // debug输出
        console.log('获取题目时的algorithmId:', algorithmId); // debug输出
  
        try {
          const response = await axios.get(
            `http://121.43.120.166:10020/exercise/random/${algorithmId}`,
            {
              headers: {
                'Authorization': userToken
              }
            }
          );
          questions.value = response.data;
          console.log('获取题目成功，题目数据：', response.data);
          userAnswers.value = new Array(response.data.length).fill('');
          loading.value = false;
        } catch (err) {
          error.value = '获取题目失败';
          loading.value = false;
        }
        localStorage.setItem('userToken', userToken);
      };
  
      // 提交答案
      const submitExercise = async () => {
        const userId = localStorage.getItem('userId');

        if (!userToken || !userId || !algorithmId) {
          ElMessage.error('请先登录');
          return;
        }

        submitting.value = true;

        try {
          const scores = [];
          for (let i = 0; i < questions.value.length; i++) {
            const answer = userAnswers.value[i]?.trim() || "0";

            const response = await axios.post(
              'http://121.43.120.166:10020/exercise/checkAnswer',null,
              {
                exerciseId: questions.value[i].id,
                answer: answer
              },
              {
              headers: {
                'Authorization': userToken,
              }}
            );
            console.log('请求成功，响应数据：', response.data);
            scores.push(response.data);
          }
          questionScores.value = scores;
          totalScore.value = scores.reduce((sum, score) => sum + score, 0);
          showResults.value = true;

          // 更新学习进度
          const progressResponse = await axios.put(
            'http://121.43.120.166:10020/learningProgress/hasDone',
            null,
            {
              query: {
                userId: userId,
                algorithmId: algorithmId
              },
              timeout: 100000
            },
            {

              headers: {
                Authorization: userToken,
              },
            }
          );

          // 处理进度更新响应
          if (progressResponse.status === 200) {
            ElMessage.success(progressResponse.data);
            console.log('学习进度更新成功:', progressResponse.data);
          }

        } catch (error) {
          console.error('更新进度失败:', error);
          console.log('userId', userId);
          console.log('algorithmId', algorithmId);
          console.log('userToken', userToken);
          if (error.response?.data) {
            ElMessage.error(error.response.data);
          } else {
            ElMessage.error('提交失败，请重试');
          }
        } finally {
          submitting.value = false;
        }
      };
  
      // 导航函数
      const nextQuestion = () => {
        if (currentQuestion.value < questions.value.length - 1) {
          currentQuestion.value++;
        }
      };
  
      const previousQuestion = () => {
        if (currentQuestion.value > 0) {
          currentQuestion.value--;
        }
      };
  
      const goToQuestion = (index) => {
        currentQuestion.value = index;
      };
  
      const closeResults = () => {
        showResults.value = false;
        router.push('/dashboard'); // 假设完成后返回仪表板
      };
  
      onMounted(() => {
        fetchQuestions();
      });
  
      return {
        getAnsweredCount,
        loading,
        error,
        questions,
        currentQuestion,
        userAnswers,
        showResults,
        questionScores,
        totalScore,
        nextQuestion,
        previousQuestion,
        goToQuestion,
        submitExercise,
        closeResults,
        submitting,
        algorithmId,
      };
    }
  };
  </script>
  
  <style scoped>
  .exercise-container {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    background: linear-gradient(135deg, #f6f8fc 0%, #dce4f2 100%);
    padding: 20px;
  }
  
  .exercise-box {
    width: 100%;
    max-width: 800px;
    padding: 40px;
    background: rgba(255, 255, 255, 0.95);
    box-shadow: 
      0 10px 30px rgba(0, 0, 0, 0.1),
      0 1px 8px rgba(0, 0, 0, 0.08),
      0 20px 40px rgba(64, 158, 255, 0.06);
    border-radius: 20px;
    transition: all 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
    border: 1px solid rgba(255, 255, 255, 0.8);
    backdrop-filter: blur(10px);
  }
  
  .exercise-box:hover {
    transform: translateY(-5px);
    box-shadow: 
      0 20px 40px rgba(0, 0, 0, 0.12),
      0 2px 10px rgba(0, 0, 0, 0.08),
      0 30px 60px rgba(64, 158, 255, 0.08);
  }
  
  h2 {
    color: #1a3865;
    font-size: 2.2em;
    margin-bottom: 35px;
    text-align: center;
    font-weight: 700;
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
    letter-spacing: 0.5px;
  }
  
  .loading, .error {
    text-align: center;
    padding: 35px;
    color: #415980;
    font-size: 1.2em;
    font-weight: 500;
  }
  
  .progress-info {
    margin-bottom: 30px;
    color: #415980;
    font-size: 1.1em;
    font-weight: 500;
  }
  
  .progress-bar {
    height: 14px;
    background: linear-gradient(to right, #f0f4f9, #e6edf7);
    border-radius: 8px;
    overflow: hidden;
    margin-top: 15px;
    box-shadow: 
      inset 0 2px 4px rgba(0, 0, 0, 0.08),
      0 1px 2px rgba(255, 255, 255, 0.8);
    border: 1px solid rgba(255, 255, 255, 0.6);
  }
  
  .progress-fill {
    height: 100%;
    background: linear-gradient(90deg, #3a8ee6, #5ca9ff);
    transition: width 0.5s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 2px 4px rgba(64, 158, 255, 0.3);
  }
  
  .question-area {
    text-align: left;
    margin-bottom: 40px;
    background: white;
    padding: 25px;
    border-radius: 15px;
    box-shadow: 
      0 4px 12px rgba(0, 0, 0, 0.05),
      0 1px 3px rgba(0, 0, 0, 0.03);
    border: 1px solid rgba(236, 240, 246, 0.8);
  }
  
  .question-content {
    padding: 25px;
    background: linear-gradient(to bottom, #f8fafd, #f3f6fb);
    border-radius: 12px;
    margin: 15px 0;
    box-shadow: 
      inset 0 2px 4px rgba(0, 0, 0, 0.02),
      0 1px 3px rgba(0, 0, 0, 0.05);
    line-height: 1.7;
    color: #2c3e50;
    border: 1px solid rgba(226, 232, 240, 0.8);
  }
  
  .answer-area {
    margin-bottom: 40px;
  }
  
  .answer-area label {
    color: #415980;
    font-weight: 600;
    font-size: 1.05em;
    display: block;
    margin-bottom: 8px;
  }
  
  .answer-area textarea {
    width: 100%;
    padding: 18px;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    resize: vertical;
    margin-top: 12px;
    font-size: 1.05em;
    transition: all 0.3s ease;
    line-height: 1.6;
    box-shadow: 
      inset 0 2px 4px rgba(0, 0, 0, 0.02),
      0 1px 3px rgba(0, 0, 0, 0.05);
    background: linear-gradient(to bottom, #ffffff, #fafbfd);
  }
  
  .answer-area textarea:focus {
    border-color: #4096ff;
    outline: none;
    box-shadow: 
      0 0 0 4px rgba(64, 158, 255, 0.15),
      inset 0 2px 4px rgba(0, 0, 0, 0.02);
    background: white;
  }
  
  .navigation-buttons {
    display: flex;
    justify-content: space-between;
    margin-bottom: 30px;
    gap: 15px;
  }
  
  .navigation-buttons button {
    padding: 12px 28px;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    background: linear-gradient(135deg, #4096ff 0%, #1677ff 100%);
    color: white;
    font-weight: 600;
    font-size: 1.05em;
    transition: all 0.3s cubic-bezier(0.165, 0.84, 0.44, 1);
    box-shadow: 
      0 4px 12px rgba(64, 158, 255, 0.25),
      0 1px 3px rgba(64, 158, 255, 0.15);
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
    letter-spacing: 0.3px;
  }
  
  .navigation-buttons button:hover {
    transform: translateY(-2px);
    box-shadow: 
      0 6px 16px rgba(64, 158, 255, 0.3),
      0 2px 4px rgba(64, 158, 255, 0.2);
    background: linear-gradient(135deg, #4096ff 0%, #2589ff 100%);
  }
  
  .navigation-buttons button:active {
    transform: translateY(1px);
    box-shadow: 
      0 2px 8px rgba(64, 158, 255, 0.25),
      0 1px 2px rgba(64, 158, 255, 0.15);
  }
  
  .navigation-buttons button:disabled {
    background: linear-gradient(135deg, #bfdbfe 0%, #93c5fd 100%);
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
  }
  
  .submit-btn {
    background: linear-gradient(135deg, #52c41a 0%, #389e0d 100%) !important;
    box-shadow: 
      0 4px 12px rgba(82, 196, 26, 0.25),
      0 1px 3px rgba(82, 196, 26, 0.15) !important;
  }
  
  .submit-btn:hover {
    background: linear-gradient(135deg, #52c41a 0%, #46a716 100%) !important;
    box-shadow: 
      0 6px 16px rgba(82, 196, 26, 0.3),
      0 2px 4px rgba(82, 196, 26, 0.2) !important;
  }
  
  .question-navigation {
    display: flex;
    flex-wrap: wrap;
    gap: 12px;
    margin-top: 30px;
    justify-content: center;
    padding: 15px;
    background: linear-gradient(to bottom, #f8fafd, #f3f6fb);
    border-radius: 15px;
    box-shadow: 
      inset 0 2px 4px rgba(0, 0, 0, 0.02),
      0 1px 3px rgba(0, 0, 0, 0.05);
    border: 1px solid rgba(226, 232, 240, 0.8);
  }
  
  .question-nav-btn {
    width: 44px;
    height: 44px;
    border: 2px solid #e2e8f0;
    border-radius: 12px;
    background: white;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.165, 0.84, 0.44, 1);
    font-weight: 600;
    font-size: 1.1em;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  }
  
  .question-nav-btn:hover {
    transform: translateY(-2px);
    box-shadow: 
      0 4px 12px rgba(0, 0, 0, 0.08),
      0 2px 4px rgba(0, 0, 0, 0.06);
    border-color: #4096ff;
  }
  
  .question-nav-btn.current {
    background: linear-gradient(135deg, #4096ff 0%, #1677ff 100%);
    color: white;
    border: none;
    box-shadow: 
      0 4px 12px rgba(64, 158, 255, 0.3),
      0 2px 4px rgba(64, 158, 255, 0.2);
  }
  
  .question-nav-btn.answered {
    background: linear-gradient(to bottom, #f0fdf4, #dcfce7);
    border-color: #52c41a;
    color: #52c41a;
    box-shadow: 
      0 2px 8px rgba(82, 196, 26, 0.15),
      0 1px 2px rgba(82, 196, 26, 0.1);
  }
  
  .modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.4);
    display: flex;
    justify-content: center;
    align-items: center;
    backdrop-filter: blur(8px);
    z-index: 1000;
  }
  
  .modal {
    background: white;
    padding: 40px;
    border-radius: 20px;
    width: 90%;
    max-width: 540px;
    box-shadow: 
      0 20px 40px rgba(0, 0, 0, 0.2),
      0 10px 20px rgba(0, 0, 0, 0.1);
    animation: modalFadeIn 0.4s cubic-bezier(0.165, 0.84, 0.44, 1);
    border: 1px solid rgba(255, 255, 255, 0.8);
  }
  
  @keyframes modalFadeIn {
    from {
      opacity: 0;
      transform: translateY(30px) scale(0.95);
    }
    to {
      opacity: 1;
      transform: translateY(0) scale(1);
    }
  }
  
  .results-content {
    margin: 30px 0;
  }
  
  .total-score {
    font-size: 36px;
    font-weight: 700;
    text-align: center;
    color: #4096ff;
    margin-bottom: 25px;
    text-shadow: 0 1px 2px rgba(64, 158, 255, 0.1);
  }
  
  .score-details {
    margin-top: 25px;
    max-height: 320px;
    overflow-y: auto;
    padding: 15px;
    background: linear-gradient(to bottom, #f8fafd, #f3f6fb);
    border-radius: 12px;
    box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.02);
  }
  
  .score-details::-webkit-scrollbar {
    width: 8px;
  }
  
  .score-details::-webkit-scrollbar-track {
    background: #f1f5f9;
    border-radius: 4px;
  }
  
  .score-details::-webkit-scrollbar-thumb {
    background: #94a3b8;
    border-radius: 4px;
    border: 2px solid #f1f5f9;
  }
  
  .score-details::-webkit-scrollbar-thumb:hover {
    background: #64748b;
  }
  
  .score-item {
    display: flex;
    justify-content: space-between;
    padding: 14px 16px;
    border-bottom: 1px solid #e2e8f0;
    color: #415980;
    font-weight: 500;
    transition: background-color 0.2s ease;
    border-radius: 8px;
  }
  
  .score-item:hover {
    background-color: rgba(255, 255, 255, 0.7);
  }
  
  .close-btn {
    width: 100%;
    padding: 14px;
    background: linear-gradient(135deg, #4096ff 0%, #1677ff 100%);
    color: white;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    margin-top: 30px;
    font-weight: 600;
    font-size: 1.1em;
    transition: all 0.3s cubic-bezier(0.165, 0.84, 0.44, 1);
    box-shadow: 
      0 4px 12px rgba(64, 158, 255, 0.25),
      0 1px 3px rgba(64, 158, 255, 0.15);
    text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
    letter-spacing: 0.5px;
  }
  
  .close-btn:hover {
    transform: translateY(-2px);
    box-shadow: 
      0 6px 16px rgba(64, 158, 255, 0.3),
      0 2px 4px rgba(64, 158, 255, 0.2);
    background: linear-gradient(135deg, #4096ff 0%, #2589ff 100%);
  }
  
  .close-btn:active {
    transform: translateY(1px);
    box-shadow: 
      0 2px 8px rgba(64, 158, 255, 0.25),
      0 1px 2px rgba(64, 158, 255, 0.15);
  }
</style>