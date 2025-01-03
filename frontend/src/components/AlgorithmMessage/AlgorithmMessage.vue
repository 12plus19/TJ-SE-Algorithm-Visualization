<template>
    <div class="algorithm-container">
        <div class="header-actions">
            <button class="primary-button fetch-button" @click="fetchAllAlgorithms">
                <span class="button-icon">📋</span>
                获取所有算法
            </button>
        </div>

        <div class="algorithm-content">
            <div class="search-section">
                <div class="search-bar">
                    <input 
                        v-model="algorithmId" 
                        placeholder="请输入算法ID" 
                        class="search-input"
                    />
                    <button @click="fetchAlgorithmInfo" class="search-button">
                        <span class="button-icon">🔍</span>
                        查询
                    </button>
                </div>
            </div>
        </div>

        <transition name="fade">
            <div v-if="showAlgorithmInfo && algorithmInfo" class="info-card">
                <div class="card-header">
                    <h2>算法信息</h2>
                </div>
                <div class="card-content">
                    <div class="info-item">
                        <span class="label">算法ID:</span>
                        <span class="value">{{ algorithmInfo.algorithmId }}</span>
                    </div>
                    <div class="info-item">
                         <span class="label">算法名称:</span>
                        <span class="value">{{ algorithmInfo.algorithmName }}</span>
                    </div>
                    <div class="info-item">
                        <span class="label">算法描述:</span>
                        <span class="value">{{ algorithmInfo.description }}</span>
                    </div>
                    <div class="info-item">
                        <span class="label">算法难度:</span>
                        <span class="value difficulty">{{ algorithmInfo.difficultyLevel }}</span>
                    </div>
                </div>
            </div>
        </transition>
            
        <transition name="fade">
            <div class="all-algorithms-section" v-if="showAllAlgorithms">
                <h2 class="section-title">所有算法</h2>
                <div class="algorithms-list">
                    <div v-for="algorithm in algorithms" 
                         :key="algorithm.algorithmId" 
                         class="algorithm-item">
                        <div class="algorithm-content">
                            <div class="algorithm-header">
                                <h3>{{ algorithm.algorithmName }}</h3>
                                <span class="algorithm-badge">#{{ algorithm.algorithmId }}</span>
                            </div>
                            <div class="algorithm-meta">
                                <span class="difficulty-tag" 
                                      :class="getDifficultyClass(algorithm.difficultyLevel)">
                                    {{ algorithm.difficultyLevel }}
                                </span>
                            </div>
                            <button @click="goToAlgorithmVisual(algorithm.algorithmId)" 
                                    class="view-details-btn">
                                查看详情
                                <span class="arrow-icon">→</span>
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </transition>
    </div>
</template>

<script>
import axios from 'axios';
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';

export default {
    name: 'AlgorithmDetail',
    setup() {
        const router = useRouter();
        const algorithmId = ref('');
        const algorithmInfo = ref(null);
        const allAlgorithms = ref([]);
        
        const showAllAlgorithms = ref(false);
        const showAlgorithmInfo = ref(false);
        
        const algorithms = computed(() => {
            return allAlgorithms.value;
        });

        const getDifficultyClass = (difficulty) => {
            const classes = {
                '简单': 'easy',
                '中等': 'medium',
                '困难': 'hard',
            };
            return classes[difficulty] || 'default';
        };

        const fetchAlgorithmInfo = async () => {
            try {
                const userToken = localStorage.getItem('userToken');
                if (!userToken) {
                    alert('请先登录');
                    return;
                }

                const response = await axios.get(`http://121.43.120.166:10020/algorithm/info/${algorithmId.value}`, {
                    headers: {
                        Authorization: userToken,
                    },
                });
                algorithmInfo.value = response.data;
                showAlgorithmInfo.value = true;
                showAllAlgorithms.value = false;
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
                const response = await axios.get('http://121.43.120.166:10020/algorithm/all', {
                    headers: {
                        Authorization: userToken,
                    },
                });
                allAlgorithms.value = response.data;
                showAllAlgorithms.value = true;
                showAlgorithmInfo.value = false;
            } catch (error) {
                console.error('Error fetching all algorithms:', error);
            }
        };

        const goToAlgorithmVisual = (algorithmId) => {
            let routeName = '';
            switch (algorithmId) {
                case '1':
                    routeName = 'AlgorithmVisual1';
                    break;
                case '2':
                    routeName = 'AlgorithmVisual2';
                    break;
                case '3':
                    routeName = 'AlgorithmVisual3';
                    break;
                default:
                    routeName = 'AlgorithmVisual1';
            }
            router.push({ name: routeName, params: { name: algorithmId } });
        };

        onMounted(() => {
            fetchAllAlgorithms();
        });

        return {
            getDifficultyClass,
            algorithmId,
            algorithmInfo,
            fetchAlgorithmInfo,
            fetchAllAlgorithms,
            algorithms,
            showAllAlgorithms,
            showAlgorithmInfo,
            goToAlgorithmVisual,
        };
    },
};
</script>

<style scoped>
.algorithm-container {
    padding: 3rem 2rem;
    max-width: 1400px;
    margin: 0 auto;
    min-height: 100vh;
    background: linear-gradient(135deg, #e6f3ff 0%, #f0f7ff 50%, #e6f3ff 100%);
    position: relative;
    overflow: hidden;
}

.algorithm-container::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 400px;
    background: linear-gradient(145deg, #1890ff08 0%, #096dd908 100%);
    clip-path: polygon(0 0, 100% 0, 100% 85%, 0 100%);
    z-index: 0;
}

.header-actions {
    margin-bottom: 3rem;
    text-align: center;
    position: relative;
    z-index: 1;
}

.primary-button {
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    color: white;
    border: none;
    padding: 16px 32px;
    border-radius: 12px;
    font-weight: 600;
    font-size: 1.1rem;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.165, 0.84, 0.44, 1);
    box-shadow: 
        0 4px 15px rgba(24, 144, 255, 0.25),
        0 2px 4px rgba(24, 144, 255, 0.15),
        inset 0 2px 4px rgba(255, 255, 255, 0.3);
    min-width: 200px;
}

.primary-button:hover {
    transform: translateY(-2px);
    box-shadow: 
        0 8px 25px rgba(24, 144, 255, 0.3),
        0 4px 10px rgba(24, 144, 255, 0.2),
        inset 0 2px 4px rgba(255, 255, 255, 0.4);
}

.search-section {
    margin: 0 auto 3rem;
    max-width: 800px;
    position: relative;
    z-index: 1;
}

.search-bar {
    display: flex;
    gap: 1rem;
    background: rgba(255, 255, 255, 0.9);
    padding: 1rem;
    border-radius: 16px;
    box-shadow: 
        0 4px 20px rgba(24, 144, 255, 0.1),
        0 2px 8px rgba(24, 144, 255, 0.08),
        inset 0 0 0 1px rgba(255, 255, 255, 0.9);
    backdrop-filter: blur(10px);
}

.search-input {
    flex: 1;
    padding: 16px 20px;
    border: 2px solid rgba(24, 144, 255, 0.2);
    border-radius: 12px;
    font-size: 1.05rem;
    transition: all 0.3s ease;
    background: white;
    color: #1e293b;
    box-shadow: 
        inset 0 2px 4px rgba(0, 0, 0, 0.02),
        0 1px 2px rgba(255, 255, 255, 1);
}

.search-input:focus {
    border-color: #1890ff;
    outline: none;
    box-shadow: 
        0 0 0 3px rgba(24, 144, 255, 0.15),
        inset 0 2px 4px rgba(0, 0, 0, 0.02);
}

.search-button {
    padding: 14px 28px;
    background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
    color: white;
    min-width: 120px;
    border: none;
    border-radius: 12px;
    cursor: pointer;
    font-weight: 600;
    transition: all 0.3s ease;
}

.search-button:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(24, 144, 255, 0.2);
}

.info-card {
    background: white;
    border-radius: 20px;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
    margin-bottom: 2rem;
    overflow: hidden;
}

.card-header {
    padding: 1.75rem;
    border-bottom: 1px solid rgba(24, 144, 255, 0.1);
    background: linear-gradient(to right, rgba(24, 144, 255, 0.05), transparent);
}

.card-header h2 {
    margin: 0;
    color: #1a365d;
    font-size: 1.5rem;
}

.card-content {
    padding: 1.75rem;
}

.info-item {
    display: flex;
    margin-bottom: 1.25rem;
    padding: 1.25rem;
    border-radius: 12px;
    background: rgba(24, 144, 255, 0.03);
    border: 1px solid rgba(24, 144, 255, 0.1);
    box-shadow: 
        0 2px 4px rgba(0, 0, 0, 0.02),
        inset 0 2px 4px rgba(255, 255, 255, 0.8);
}

.label {
    font-weight: 600;
    color: #2d3748;
    min-width: 100px;
}

.value {
    color: #4a5568;
}

.all-algorithms-section {
    padding: 2rem;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 20px;
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
}

.section-title {
    font-size: 2rem;
    color: #1a365d;
    text-align: center;
    margin-bottom: 2rem;
    font-weight: 700;
    background: linear-gradient(135deg, #1890ff, #096dd9);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
}

.algorithms-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 1.5rem;
    padding: 1rem;
}

.algorithm-item {
    background: white;
    border-radius: 16px;
    padding: 1.5rem;
    transition: all 0.3s ease;
    border: 1px solid rgba(24, 144, 255, 0.1);
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.algorithm-item:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 25px rgba(24, 144, 255, 0.15);
}

.algorithm-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 1rem;
}

.algorithm-header h3 {
    font-size: 1.25rem;
    color: #2d3748;
    margin: 0;
}

.algorithm-badge {
    background: rgba(24, 144, 255, 0.1);
    color: #1890ff;
    padding: 0.25rem 0.75rem;
    border-radius: 999px;
    font-size: 0.875rem;
    font-weight: 500;
}

.algorithm-meta {
    margin: 1rem 0;
}

.difficulty-tag {
    padding: 0.5rem 1rem;
    border-radius: 999px;
    font-size: 0.875rem;
    font-weight: 600;
    display: inline-flex;
    align-items: center;
}

.difficulty-tag.easy {
    background: #e6ffec;
    color: #16a34a;
}

.difficulty-tag.medium {
    background: #fff7ed;
    color: #ea580c;
}

.difficulty-tag.hard {
    background: #fee2e2;
    color: #dc2626;
}

.view-details-btn {
    width: 100%;
    padding: 0.75rem 1rem;
    background: linear-gradient(135deg, #1890ff, #096dd9);
    color: white;
    border: none;
    border-radius: 12px;
    font-weight: 600;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 0.5rem;
    transition: all 0.3s ease;
}

.view-details-btn:hover {
    background: linear-gradient(135deg, #40a9ff, #1890ff);
    transform: translateY(-2px);
}

.arrow-icon {
    transition: transform 0.3s ease;
}

.view-details-btn:hover .arrow-icon {
    transform: translateX(4px);
}

.fade-enter-active,
.fade-leave-active {
    transition: opacity 0.3s ease, transform 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
    opacity: 0;
    transform: translateY(20px);
}

@media (max-width: 768px) {
    .algorithm-container {
        padding: 2rem 1rem;
    }

    .algorithms-list {
        grid-template-columns: 1fr;
    }

    .search-bar {
        flex-direction: column;
    }

    .search-button {
        width: 100%;
    }

    .all-algorithms-section {
        padding: 1rem;
    }

    .section-title {
        font-size: 1.5rem;
    }
}

@keyframes fadeIn {
    from {
        opacity: 0;
        transform: translateY(20px);
    }
    to {
        opacity: 1;
        transform: translateY(0);
    }
}

.algorithm-item {
    animation: fadeIn 0.5s ease-out;
    animation-fill-mode: both;
}

.algorithm-item:nth-child(n) {
    animation-delay: calc(0.1s * n);
}
</style>