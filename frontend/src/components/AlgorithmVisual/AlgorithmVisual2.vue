<template>
    <div class="algorithm-visual">
        <header>
            <div style="display: flex; align-items: center; width: 100%;">
                <div style="flex: 1;">
                    <button class="info-btn" style="padding: 10px 20px; margin-right: 10px;" @click="goToAlgorithmMessage()"><strong>详细信息</strong></button>
                    <button class="info-btn" style="padding: 10px 20px;" @click="goToExercise()"><strong>练习题</strong></button>
                </div>
                <div style="flex: 2; text-align: center;">
                    <h1>算法可视化平台</h1>
                </div>
                <div style="flex: 1; text-align: right;">
                    <button class="profile-btn" style="padding: 10px 20px;" @click="goToUserInfo()"><strong>个人信息</strong></button>
                </div>
            </div>
        </header>
        <main>
            <aside class="algorithm-list">
                <ul>
                    <li v-for="algorithm in computedAlgorithms" :key="algorithm.algorithmName">
                        <p @click="goToAlgorithmPage(algorithm.algorithmName)"><strong>{{ algorithm.algorithmName }}</strong></p>
                    </li>
                    
                </ul>
            </aside>
            <section class="visualization-area">
                <!-- 可视化区域 -->
            </section>
            <section class="code-area">
                <!-- 可视化区域 -->
            </section>
        </main>
        <footer>
            <div class="forum">
                <AlgorithmForum />
            </div>
        </footer>
    </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import AlgorithmForum from '@/components/Comment/AlgorithmForum.vue'
import { setCurrentAlgorithmId } from '@/store/algorithmStore'
import { ElMessage } from 'element-plus';

export default {
    components: {
        AlgorithmForum
    },
    name: 'AlgorithmVisual',
    setup() {
        const algorithms = ref([]);
        const userId = localStorage.getItem('userId');
        const userToken = localStorage.getItem('userToken');
        const submitting = ref(false);

        const computedAlgorithms = computed(() => {
            return algorithms.value;
        });

        const router = useRouter();

        const fetchAlgorithms = async () => {
            submitting.value = true;
            try {
                const userToken = localStorage.getItem('userToken');
                if (!userToken) {
                    alert('请先登录');
                    return;
                }
                const response = await axios.get('http://121.43.120.166:10020/algorithm/all', {
                    headers: {
                        Authorization: userToken,
                    },
                });
                algorithms.value = response.data;
        
            } catch (error) {
                console.error('Error fetching algorithms:', error);
            }
        };

        const goToAlgorithmMessage = async() => {
            const algorithmId = '2'; // 示例ID
            router.push({ name: 'AlgorithmDetail', params: { id: algorithmId } });
            try {
            const progressResponse = await axios.put(
            'http://121.43.120.166:10020/learningProgress/hasRead',
            null,
            {
              params: {
                userId: userId,
                algorithmId: algorithmId
              },
              headers: {
                Authorization: userToken,
              },
              timeout: 100000
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

        const goToAlgorithmPage = (algorithmName) => {
            let routeName = '';
            let algorithmId = '2'; // 默认ID
            
            switch (algorithmName) {
                case '八皇后问题':
                    routeName = 'AlgorithmVisual1';
                    algorithmId = '1';
                    break;
                case '机器学习':
                    routeName = 'AlgorithmVisual2';
                    algorithmId = '2';
                    break;
                case 'Prim算法':
                    routeName = 'AlgorithmVisual3';
                    algorithmId = '3';
                    break;
                default:
                    routeName = 'AlgorithmVisual2';
                    algorithmId = '2';
            }
            
            setCurrentAlgorithmId(algorithmId); // 设置当前算法ID
            router.push({ name: routeName, params: { name: algorithmName } });
        };

        const goToUserInfo = () => {
            router.push({ name: 'UserInfo' });
        };

        const goToExercise = () => {
            const algorithmId = computedAlgorithms.value[1].algorithmId; // 示例ID，实际上应从选中的算法数据中获取
            const userToken = localStorage.getItem('userToken');
            if (!userToken) {
                alert('请先登录');
                return;
            }
            // 跳转到对应算法的练习题页面，并传递 algorithmId 和 userToken
            router.push({ 
                name: 'Exercise', 
                query: { 
                    algorithmId: algorithmId, 
                    userToken: userToken 
                }
            });
            console.log('algorithmId:', algorithmId);
        };

        onMounted(() => {
            fetchAlgorithms();
        });

        return {
            goToAlgorithmMessage,
            fetchAlgorithms,
            computedAlgorithms,
            goToAlgorithmPage,
            goToUserInfo,
            goToExercise,
        };
    },
}
</script>


<style scoped>
.algorithm-visual {
    display: flex;
    flex-direction: column;
    min-height: 100vh;
    width: 100%;
    margin: 0;
    padding: 20px;
    overflow-x: hidden;
    background-color: #f5f7fa;
}

header {
    display: flex;
    justify-content: space-between;
    padding: 15px 25px;
    background-color: #007BFF;
    color: white;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
}

.info-btn, .profile-btn {
    padding: 8px 16px;
    background-color: #0056b3;
    color: white;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
    font-size: 14px;
}

.info-btn:hover, .profile-btn:hover {
    background-color: #003d80;
    transform: translateY(-2px);
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

main {
    height: 600px;
    display: flex;
    gap: 20px;
    width: 100%;
    padding: 20px;
    background-color: white;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.algorithm-list {
    width: 220px;
    background-color: #e0f7ff;
    padding: 15px;
    border-radius: 8px;
    box-shadow: inset 0 2px 8px rgba(0, 123, 255, 0.1);
}

.algorithm-list ul {
    list-style: none;
    padding: 0;
}

.algorithm-list li {
    margin-bottom: 12px;
    transition: all 0.3s ease;
}

.algorithm-list li p {
    padding: 10px 15px;
    background-color: white;
    border-radius: 6px;
    cursor: pointer;
    transition: all 0.3s ease;
    color: #2c3e50;
}

.algorithm-list li p:hover {
    background-color: #007BFF;
    color: white;
    transform: translateX(5px);
    box-shadow: 0 2px 8px rgba(0, 123, 255, 0.2);
}

.visualization-area {
    flex: 1;
    background-color: #ffffff;
    margin: 0;
    border: 2px solid #007BFF;
    border-radius: 10px;
    padding: 15px;
    box-shadow: 0 2px 12px rgba(0, 123, 255, 0.1);
}

.code-area {
    flex: 1;
    background-color: #ffffff;
    margin: 0;
    border: 2px solid #2c3e50;
    border-radius: 10px;
    padding: 15px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

footer {
    margin-top: 20px;
    padding: 20px;
    background-color: #007BFF;
    color: white;
    border-radius: 12px;
    width: 100%;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.forum {
    background-color: #ffffff;
    border-radius: 8px;
    padding: 20px;
    width: 100%;
    box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 添加响应式设计 */
@media (max-width: 1200px) {
    main {
        flex-direction: column;
        height: auto;
    }

    .algorithm-list {
        width: 100%;
        margin-bottom: 20px;
    }

    .visualization-area,
    .code-area {
        width: 100%;
        margin: 10px 0;
    }
}

/* 添加平滑滚动 */
html {
    scroll-behavior: smooth;
}

/* 添加内容过渡效果 */
.algorithm-visual * {
    transition: all 0.3s ease;
}
</style>