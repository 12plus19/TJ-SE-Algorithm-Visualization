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
            <span>当前进度: {{ currentQuestion + 1 }}/{{ questions.length }}</span>
            <div class="progress-bar">
              <div 
                class="progress-fill" 
                :style="{ width: `${(currentQuestion + 1) * 5}%` }"
              ></div>
            </div>
          </div>
  
          <!-- 题目区域 -->
          <div class="question-area">
            <h3>题目 {{ currentQuestion + 1 }}</h3>
            <div class="question-content">
              {{ questions[currentQuestion].content }}
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
  import { useRouter } from 'vue-router';
  import axios from 'axios';
  
  export default {
    name: 'ExercisePage',
    setup() {
      const router = useRouter();
      const loading = ref(true);
      const error = ref(null);
      const questions = ref([]);
      const currentQuestion = ref(0);
      const userAnswers = ref([]);
      const showResults = ref(false);
      const questionScores = ref([]);
      const totalScore = ref(0);
  
      // 获取练习题
      const fetchQuestions = async () => {
        const algorithmId = localStorage.getItem('algorithmId');
        const token = localStorage.getItem('userToken');
        console.log('获取题目时的token:', token); // debug输出
        console.log('获取题目时的algorithmId:', algorithmId); // debug输出
  
        try {
          const response = await axios.get(
            `http://121.43.120.166:10020/exercise/random/${algorithmId}`,
            {
              headers: {
                'Authorization': token
              }
            }
          );
          questions.value = response.data;
          userAnswers.value = new Array(response.data.length).fill('');
          loading.value = false;
        } catch (err) {
          error.value = '获取题目失败';
          loading.value = false;
        }
        localStorage.setItem('userToken', token);
      };
  
      // 提交答案
      const submitExercise = async () => {
        const token = localStorage.getItem('userToken');

  console.log('提交答案时的token:', token); // debug输出
        if (!token) {
          alert('未登录或登录已过期，请重新登录');
         router.push('/login'); // 假设有登录页面路由
         return;
       }
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
                'Authorization': token,
              }}
            );
            console.log('请求成功，响应数据：', response.data);
            scores.push(response.data);
          }
          questionScores.value = scores;
          totalScore.value = scores.reduce((sum, score) => sum + score, 0);
          showResults.value = true;
        } catch (err) {
          console.log('请求失败，错误信息：', err.response || err);
        if (err.response?.status === 401) {
          console.log(token);
          alert('登录已过期，请重新登录');
          localStorage.removeItem('userToken'); // 清除无效token
          router.push('/login');
        } else {
          alert('提交答案失败');
          console.error(err);
        }}
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
        closeResults
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
    background-color: #f0f2f5;
    padding: 20px;
  }
  
  .exercise-box {
    width: 100%;
    max-width: 800px;
    padding: 40px;
    background: #fff;
    box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    border-radius: 8px;
  }
  
  .loading, .error {
    text-align: center;
    padding: 20px;
  }
  
  .progress-info {
    margin-bottom: 20px;
  }
  
  .progress-bar {
    height: 10px;
    background-color: #f0f0f0;
    border-radius: 5px;
    overflow: hidden;
    margin-top: 10px;
  }
  
  .progress-fill {
    height: 100%;
    background-color: #409eff;
    transition: width 0.3s ease;
  }
  
  .question-area {
    margin-bottom: 30px;
  }
  
  .question-content {
    padding: 20px;
    background-color: #f8f9fa;
    border-radius: 4px;
    margin: 10px 0;
  }
  
  .answer-area {
    margin-bottom: 30px;
  }
  
  .answer-area textarea {
    width: 100%;
    padding: 10px;
    border: 1px solid #ddd;
    border-radius: 4px;
    resize: vertical;
    margin-top: 10px;
  }
  
  .navigation-buttons {
    display: flex;
    justify-content: space-between;
    margin-bottom: 20px;
  }
  
  .navigation-buttons button {
    padding: 8px 20px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    background-color: #409eff;
    color: white;
  }
  
  .navigation-buttons button:disabled {
    background-color: #a0cfff;
    cursor: not-allowed;
  }
  
  .submit-btn {
    background-color: #67c23a !important;
  }
  
  .submit-btn:disabled {
    background-color: #b3e19d !important;
  }
  
  .question-navigation {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
    margin-top: 20px;
  }
  
  .question-nav-btn {
    width: 36px;
    height: 36px;
    border: 1px solid #ddd;
    border-radius: 4px;
    background: white;
    cursor: pointer;
  }
  
  .question-nav-btn.current {
    background-color: #409eff;
    color: white;
    border-color: #409eff;
  }
  
  .question-nav-btn.answered {
    background-color: #f0f9eb;
    border-color: #67c23a;
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
    background: white;
    padding: 30px;
    border-radius: 8px;
    width: 90%;
    max-width: 500px;
  }
  
  .results-content {
    margin: 20px 0;
  }
  
  .total-score {
    font-size: 24px;
    font-weight: bold;
    text-align: center;
    color: #409eff;
  }
  
  .score-details {
    margin-top: 20px;
    max-height: 300px;
    overflow-y: auto;
  }
  
  .score-item {
    display: flex;
    justify-content: space-between;
    padding: 8px 0;
    border-bottom: 1px solid #eee;
  }
  
  .close-btn {
    width: 100%;
    padding: 10px;
    background-color: #409eff;
    color: white;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    margin-top: 20px;
  }
  
  .close-btn:hover {
    background-color: #66b1ff;
  }
  </style>