<template>
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
    </div>
</template>

<script>
import axios from 'axios';
import { ref } from 'vue';
export default {
    setup() {
        const algorithmId = ref('');
        const algorithmInfo = ref(null);

        const fetchAlgorithmInfo = async () => {
            try {
                const response = await axios.get(`http://121.43.120.166:10020/algorithm/info/${algorithmId.value}`, {
                    headers: {
                        Authorization: 'eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOiJhNjJkODM0Ni1hMmExLTRlNjAtOGJjYS03Yjk4ZTViM2IxOTUiLCJleHAiOjE3MzUzNDE4Njh9.ZMh_JiZAhjFJI6qpstdAjgEymLmjEVEsxTdgbu9kTVU',
                    },
                });
                algorithmInfo.value = response.data;
            } catch (error) {
                console.error('Error fetching algorithm info:', error);
            }
        };

        return {
            algorithmId,
            algorithmInfo,
            fetchAlgorithmInfo,
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
    padding: 8px 16px;
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
/* Add any additional styles here */
