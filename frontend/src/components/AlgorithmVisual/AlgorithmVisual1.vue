<template>
    <div class="algorithm-visual">
        <header>
            <div style="display: flex; align-items: center; width: 100%;">
                <div style="flex: 1;">
                    <button class="info-btn" style="padding: 10px 20px; margin-right: 10px;" @click="goToAlgorithmMessage()"><strong>详细信息</strong></button>
                    <button class="info-btn" style="padding: 10px 20px;" @click="goToAlgorithmMessage()"><strong>练习题</strong></button>
                </div>
                <div style="flex: 2; text-align: center;">
                    <h1>算法可视化平台</h1>
                </div>
                <div style="flex: 1; text-align: right;">
                    <button class="profile-btn" style="padding: 10px 20px"><strong>个人信息</strong></button>
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
            <section class="rating-area">
                <label for="rating">评分:</label>
                <input type="range" id="rating" v-model="rating" min="0" max="100" step="1" style="writing-mode: bt-lr; height: 200px;">
                <p>当前评分: {{ rating }}</p>
            </section>
        <section class="save-button-area">
            <button @click="saveRating">保存评分</button>
        </section>
        </main>
        <footer>
            <div class="forum">
                <!-- 论坛 -->
            </div>
        </footer>
    </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

export default {
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
            switch (algorithmName) {
                case '八皇后问题':
                    routeName = 'AlgorithmVisual1';
                    break;
                case '机器学习':
                    routeName = 'AlgorithmVisual2';
                    break;
                case 'Prim算法':
                    routeName = 'AlgorithmVisual3';
                    break;
                default:
                    routeName = 'AlgorithmVisual1'; // 默认页面
            }
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
    height: 100vh;
}

header {
    display: flex;
    justify-content: space-between;
    padding: 10px;
    background-color: #007bef; /* Blue background */
    color: white; /* White text */
}

.info-btn, .profile-btn {
    padding: 5px 10px;
    background-color: #0056b3; /* Darker blue */
    color: white; /* White text */
    border: none;
    cursor: pointer;
}

main {
    display: flex;
    flex: 1;
}

.algorithm-list {
    width: 200px;
    background-color: #e0f7ff; /* Light blue */
    padding: 10px;
}

.visualization-area {
    flex: 1;
    background-color: #ffffff;
    margin: 10px;
    border: 1px solid #007BFF; /* Blue border */
    border-radius: 10px; /* Rounded corners */
}
.code-area {
    flex: 1;
    background-color: #ffffff;
    margin: 10px;
    border: 1px solid #000000; /* Black border */
    border-radius: 10px; /* Rounded corners */
}

footer {
    padding: 10px;
    background-color: #007BFF; /* Blue background */
    color: white; /* White text */
    border-radius: 10px; /* Rounded corners */
}

.forum {
    height: 100px;
    background-color: #e0f7ff; /* Light blue */
    border-radius: 10px; /* Rounded corners */
}
</style>