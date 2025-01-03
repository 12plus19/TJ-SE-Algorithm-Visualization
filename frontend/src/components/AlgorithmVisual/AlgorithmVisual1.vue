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
                <h2>论坛</h2>
                <button class="add-comment-btn" @click="showCommentModal = true">+</button>
                <modal v-if="showCommentModal" @close="showCommentModal = false">
                    <template v-slot:header>
                        <h3>添加评论</h3>
                    </template>
                    <template v-slot:body>
                        <textarea v-model="newComment" placeholder="请输入评论内容"></textarea>
                    </template>
                    <template v-slot:footer>
                        <button @click="submitComment">提交</button>
                        <button @click="showCommentModal = false">取消</button>
                    </template>
                </modal>
                <ul>
                    <li v-for="post in forumPosts" :key="post.commentId">
                        <p><strong>{{ post.content }}</strong></p>
                        <p>{{ post.commentTime }}</p>
                    </li>
                </ul>
            </div>
        </footer>
    </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

export default {
    name: 'AlgorithmVisual1',
    setup() {
        const algorithms = ref([]);
        const forumPosts = ref([]);
        const showCommentModal = ref(false);
        const newComment = ref('');
        const rating = ref(50);
        const router = useRouter();

        const computedAlgorithms = computed(() => {
            return algorithms.value;
        });

        const fetchForumPosts = async () => {
            try {
                const response = await axios.get('http://example.com/api/forum-posts');
                forumPosts.value = response.data;
            } catch (error) {
                console.error('Error fetching forum posts:', error);
            }
        };

        const submitComment = async () => {
            try {
                const response = await axios.post('http://example.com/api/forum-posts', {
                    content: newComment.value,
                });
                forumPosts.value.push(response.data);
                newComment.value = '';
                showCommentModal.value = false;
            } catch (error) {
                console.error('Error submitting comment:', error);
            }
        };

        const saveRating = () => {
            console.log('当前评分:', rating.value);
            // 这里可以添加保存评分的逻辑
        };

        const goToAlgorithmMessage = () => {
            router.push('/algorithm-message');
        };

        const goToExercise = () => {
            router.push('/exercise');
        };

        const goToUserInfo = () => {
            router.push('/user-info');
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
            fetchForumPosts();
        });

        return {
            algorithms,
            forumPosts,
            showCommentModal,
            newComment,
            rating,
            computedAlgorithms,
            fetchForumPosts,
            submitComment,
            saveRating,
            goToAlgorithmMessage,
            goToExercise,
            goToUserInfo,
            goToAlgorithmPage,
        };
    },
};
</script>

<style scoped>
.algorithm-visual {
    padding: 20px;
    display: flex;
    flex-wrap: wrap; /* 允许子元素换行 */
    justify-content: space-between; /* 设置子元素的间距 */
}

header, footer {
    width: 100%;
    text-align: center;
}

.info-btn, .profile-btn {
    background-color: #409eff;
    color: #fff;
    border: none;
    border-radius: 4px;
    cursor: pointer;
}

.info-btn:hover, .profile-btn:hover {
    background-color: #66b1ff;
}

/* 侧边栏的样式 */
.algorithm-list {
    flex: 1 1 20%; /* 侧边栏占20%的宽度，允许自适应 */
    padding: 20px;
}

.visualization-area, .code-area, .rating-area, .save-button-area {
    flex: 1 1 30%; /* 各区域占30%的宽度 */
    padding: 20px;
    min-width: 200px; /* 设置最小宽度，防止在窄屏下挤压 */
}

.forum {
    clear: both;
    padding: 20px;
    width: 100%;
}

.add-comment-btn {
    background-color: #409eff;
    color: #fff;
    border: none;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    cursor: pointer;
}

.add-comment-btn:hover {
    background-color: #66b1ff;
}
</style>
