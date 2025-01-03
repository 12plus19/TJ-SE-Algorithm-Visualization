<template>
    <div class="algorithm-visual">
        <header>
            <button class="info-btn" @click="goToAlgorithmMessage()">详细信息</button>
            
            <button class="profile-btn">个人信息</button>
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

export default {
    components: {
    AlgorithmForum
  },
    name: 'AlgorithmVisual',
    setup() {
        const algorithms = ref([]);

        const computedAlgorithms = computed(() => {
            return algorithms.value;
        });

        const router = useRouter();

        const fetchAlgorithms = async () => {
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

        const goToAlgorithmMessage = () => {
            // let Id = '';
            // switch(algorithmId) {
            //     case '1':
            //         Id = '1';
            //         break;
            //     case '2':
            //         Id = '2';
            //         break;
            //     case '3':
            //         Id = '3';
            //         break;
            //     default:
            //         Id = '1'; // 默认页面
            // }
            const algorithmId = '1'; // 示例ID
            router.push({ name: 'AlgorithmDetail', params: { id: algorithmId } });
            // 跳转到算法信息页面
        };

        const goToAlgorithmPage = (algorithmName) => {
            let routeName = '';
            let algorithmId = '1'; // 默认ID
            
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
                    routeName = 'AlgorithmVisual3';
                    algorithmId = '2';
            }
            
            setCurrentAlgorithmId(algorithmId); // 设置当前算法ID
            router.push({ name: routeName, params: { name: algorithmName } });
        };


        onMounted(() => {
            fetchAlgorithms();
        });

        return {
            goToAlgorithmMessage,
            fetchAlgorithms,
            computedAlgorithms,
            goToAlgorithmPage,
        };
    },
};
</script>

<style scoped>
.info-btn:hover, .profile-btn:hover {
                background-color: #003d80; /* Even darker blue on hover */
                transition: background-color 0.3s ease;
            }
.algorithm-visual {
    display: flex;
    flex-direction: column;
    min-height: 100vh; /* 改为最小高度，允许内容超出 */
}

header {
    display: flex;
    justify-content: space-between;
    padding: 10px;
    background-color: #007BFF;
    color: white;
}

.info-btn, .profile-btn {
    padding: 5px 10px;
    background-color: #0056b3;
    color: white;
    border: none;
    cursor: pointer;
}

main {
    height: 600px; /* 保持固定高度 */
    display: flex;
    margin: 10px 0;
}

.algorithm-list {
    width: 200px;
    background-color: #e0f7ff;
    padding: 10px;
}

.visualization-area {
    flex: 1;
    background-color: #ffffff;
    margin: 10px;
    border: 1px solid #007BFF;
    border-radius: 10px;
}

.code-area {
    flex: 1;
    background-color: #ffffff;
    margin: 10px;
    border: 1px solid #000000;
    border-radius: 10px;
}

footer {
    padding: 10px;
    background-color: #007BFF;
    color: white;
    border-radius: 10px;
}

.forum {
    background-color: #e0f7ff;
    border-radius: 10px;
    padding: 10px;
}
</style>