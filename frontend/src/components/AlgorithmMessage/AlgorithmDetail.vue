<template>
    <div class="fetch-all-button">
        <button class="fetch-all-button" @click="fetchAllAlgorithms">获取所有算法信息</button>
    </div>
    <div class="algorithm-detail">
        <div class="search-bar">
            <input v-model="algorithmId" placeholder="请输入算法ID" />
            <button @click="fetchAlgorithmInfo">查询</button>
        </div>
        <div v-if="algorithmInfo" class="algorithm-info">
            <h2>算法信息</h2>
            <p><strong>算法ID:</strong> {{ algorithmInfo.algorithmId }}</p>
            <p><strong>算法名称:</strong> {{ algorithmInfo.algorithmName }}</p>
            <p><strong>算法描述:</strong> {{ algorithmInfo.description }}</p>
            <p><strong>算法难度:</strong> {{ algorithmInfo.difficultyLevel }}</p>
        </div>
        <div  class="algorithm-info">
            <h2>所有算法信息</h2>
            <ul>
                <li v-for="algorithm in algorithms" :key="algorithm.algorithmId" class="algorithm-item">
                    <div class="algorithm-info" style="margin-bottom: 20px;">
                        <p><strong>算法ID:</strong> {{ algorithm.algorithmId }}</p>
                        <p><strong>算法名称:</strong> {{ algorithm.algorithmName }}</p>
                        <p><strong>算法描述:</strong> {{ algorithm.description }}</p>
                        <p><strong>算法难度:</strong> {{ algorithm.difficultyLevel }}</p>
                    </div>
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
import axios from 'axios';
import { ref, onMounted, computed } from 'vue';

export default {
    name: 'AlgorithmDetail',
    setup() {
        const algorithmId = ref('');
        const algorithmInfo = ref(null);
        const allAlgorithms = ref([{ algorithmId: 0, algorithmName: '', description: '', difficultyLevel: '' }]);
        
        
        const algorithms = computed(() => {
            return allAlgorithms.value;
        });

        const fetchAlgorithmInfo = async () => {
            try {
                const userToken = localStorage.getItem('userToken');
                if (!userToken) {
                    alert('请先登录');
                    return;
                }
                console.log('Using token:', userToken); // 添加调试信息
                const response = await axios.get(`http://121.43.120.166:10020/algorithm/info/${algorithmId.value}`, {
                    headers: {
                        Authorization: userToken,
                    },
                });
                algorithmInfo.value = response.data;
            } catch (error) {
                console.error('Error fetching algorithm info:', error);
            }
        };

        const fetchAllAlgorithms = async () => {
            try {
                const userToken = localStorage.getItem('userToken');
                if (!userToken) {
                    alert('请先登录');
                    return;
                }
                // console.log('Using token:', userToken); // 添加调试信息
                const response = await axios.get('http://121.43.120.166:10020/algorithm/all', {
                    headers: {
                        Authorization: userToken,
                    },
                });
                console.log('Data:', response.data); // 添加调试信息
                allAlgorithms.value = response.data;
                console.log('All algorithms:', allAlgorithms.value); // 添加调试信息
        
            } catch (error) {
                console.error('Error fetching all algorithms:', error);
            }
        };

        onMounted(() => {
            // fetchAllAlgorithms();
        });

        return {
            algorithmId,
            algorithmInfo,
            fetchAlgorithmInfo,
            fetchAllAlgorithms,
            algorithms,
        };
    },
};
</script>

<style scoped>
.algorithm-detail {
    padding: 20px;
}

.search-bar {
    display: flex;
    align-items: center;
    margin-bottom: 20px;
}

.search-bar input {
    flex: 1;
    padding: 8px;
    margin-right: 10px;
}

.search-bar button {
    width: 10%;
  padding: 10px;
  background-color: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.fetch-all-button button {
    width: 15%;
  padding: 10px;
  background-color: #409eff;
  color: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.algorithm-info {
    border: 1px solid #ccc;
    padding: 20px;
    border-radius: 4px;
}

.algorithm-info h2 {
    margin-top: 0;
}
</style>