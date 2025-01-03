<template>
  <div class="algorithm-comment">
    <!-- 评论输入框 -->
    <div class="comment-input">
      <el-input
        v-model="newComment"
        type="textarea"
        :rows="3"
        placeholder="请输入您的评论"
      />
      <el-button 
        type="primary" 
        @click="submitComment"
        :loading="submitting"
      >发表评论</el-button>
    </div>

    <!-- 评论列表 -->
    <div class="comment-list" v-loading="loading">
      <!-- 使用递归组件来显示嵌套评论 -->
      <div v-for="comment in rootComments" :key="comment.commentId" class="comment-container">
        <div class="comment-item">
          <!-- 主评论 -->
          <div class="main-comment">
            <div class="comment-header">
              <span class="user-id">用户: {{ comment.userId }}</span>
              <span class="comment-time">{{ formatTime(comment.commentTime) }}</span>
            </div>
            <div class="comment-content">{{ comment.content }}</div>
            <div class="comment-actions">
              <el-button link @click="showReplyInput(comment)">回复</el-button>
              <!-- 添加调试信息 -->
              <span style="color: #999; font-size: 12px;">
                (ID: {{ comment.commentId }}, 回复数: {{ getCommentReplies(comment.commentId).length }})
              </span>
              <!-- 展开按钮 -->
              <el-button 
                v-if="hasReplies(comment.commentId)"
                link 
                @click="toggleReplies(comment.commentId)"
              >
                {{ isExpanded(comment.commentId) ? '收起详情' : `展开详情(${getCommentReplies(comment.commentId).length})` }}
              </el-button>
            </div>
          </div>

          <!-- 回复输入框 -->
          <div v-if="activeReply === comment.commentId" class="reply-input">
            <el-input
              v-model="replyContent"
              type="textarea"
              :rows="2"
              placeholder="请输入回复内容"
            />
            <el-button 
              type="primary" 
              size="small" 
              @click="submitReply(comment)"
              :loading="submitting"
            >提交回复</el-button>
            <el-button size="small" @click="cancelReply">取消</el-button>
          </div>
        </div>

        <!-- 一级回复（始终显示） -->
        <div class="replies-container" 
             v-if="isExpanded(comment.commentId)">
          <div v-for="reply in getCachedReplies(comment.commentId)" 
               :key="reply.commentId" 
               class="nested-comment">
            <div class="comment-item">
              <div class="comment-header">
                <span class="user-id">用户: {{ reply.userId }}</span>
                <span class="comment-time">{{ formatTime(reply.commentTime) }}</span>
              </div>
              <div class="comment-content">{{ reply.content }}</div>
              <div class="comment-actions">
                <el-button link @click="showReplyInput(reply)">回复</el-button>
                <span style="color: #999; font-size: 12px;">
                  (ID: {{ reply.commentId }}, 回复数: {{ getCachedReplies(reply.commentId).length }})
                </span>
                <el-button 
                  v-if="hasReplies(reply.commentId)"
                  link 
                  @click.stop="toggleReplies(reply.commentId)"
                >
                  {{ isExpanded(reply.commentId) ? '收起详情' : `展开详情(${getCachedReplies(reply.commentId).length})` }}
                </el-button>
              </div>
            </div>

            <!-- 回复输入框 -->
            <div v-if="activeReply === reply.commentId" class="reply-input">
              <el-input
                v-model="replyContent"
                type="textarea"
                :rows="2"
                placeholder="请输入回复内容"
              />
              <el-button 
                type="primary" 
                size="small" 
                @click="submitReply(reply)"
                :loading="submitting"
              >提交回复</el-button>
              <el-button size="small" @click="cancelReply">取消</el-button>
            </div>

            <!-- 修改递归显示部分 -->
            <div class="nested-replies" v-if="isExpanded(reply.commentId)">
              <div class="nested-replies">
                <nested-replies 
                  :replies="getCachedReplies(reply.commentId)"
                  @reply="showReplyInput"
                />
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch, computed, provide, inject, defineComponent } from 'vue'
import { ElMessage } from 'element-plus'
import axios from 'axios'
import dayjs from 'dayjs'
import relativeTime from 'dayjs/plugin/relativeTime'
import 'dayjs/locale/zh-cn'
import { currentAlgorithmId } from '@/store/algorithmStore'

dayjs.extend(relativeTime)
dayjs.locale('zh-cn')

// 定义嵌套回复组件
const NestedReplies = defineComponent({
  name: 'NestedReplies',
  props: {
    replies: {
      type: Array,
      required: true
    }
  },
  emits: ['reply'],
  setup(props, { emit }) {
    const { 
      formatTime, 
      getCommentReplies, 
      hasReplies, 
      isExpanded, 
      toggleReplies,
      activeReply,
      replyContent,
      submitting,
      submitReply,
      cancelReply,
      expandedReplies
    } = inject('commentMethods');

    // 添加调试日志
    const handleToggle = (commentId) => {
      console.log('NestedReplies 组件触发展开/折叠:', commentId);
      toggleReplies(commentId);
    };

    return {
      formatTime,
      getCommentReplies,
      hasReplies,
      isExpanded,
      toggleReplies,
      activeReply,
      replyContent,
      submitting,
      submitReply,
      cancelReply,
      emit,
      handleToggle,
      expandedReplies
    }
  },
  template: `
    <div v-for="reply in replies" :key="reply.commentId" class="nested-comment">
      <div class="comment-item">
        <div class="comment-header">
          <span class="user-id">用户: {{ reply.userId }}</span>
          <span class="comment-time">{{ formatTime(reply.commentTime) }}</span>
        </div>
        <div class="comment-content">{{ reply.content }}</div>
        <div class="comment-actions">
          <el-button link @click="$emit('reply', reply)">回复</el-button>
          <span style="color: #999; font-size: 12px;">
            (ID: {{ reply.commentId }}, 回复数: {{ getCachedReplies(reply.commentId).length }})
          </span>
          <el-button 
            v-if="hasReplies(reply.commentId)"
            link 
            @click.stop="toggleReplies(reply.commentId)"
          >
            {{ isExpanded(reply.commentId) ? '收起详情' : \`展开详情(\${getCachedReplies(reply.commentId).length})\` }}
          </el-button>
        </div>
      </div>

      <!-- 回复输入框 -->
      <div v-if="activeReply === reply.commentId" class="reply-input">
        <el-input
          v-model="replyContent"
          type="textarea"
          :rows="2"
          placeholder="请输入回复内容"
        />
        <el-button 
          type="primary" 
          size="small" 
          @click="submitReply(reply)"
          :loading="submitting"
        >提交回复</el-button>
        <el-button size="small" @click="cancelReply">取消</el-button>
      </div>

      <!-- 修改递归显示部分 -->
      <div class="nested-replies" v-if="isExpanded(reply.commentId)">
        <div class="replies-container">
          <nested-replies 
            :replies="getCachedReplies(reply.commentId)"
            @reply="$emit('reply', $event)"
          />
        </div>
      </div>
    </div>
  `
})

export default {
  name: 'AlgorithmComment',
  components: {
    NestedReplies
  },
  setup() {
    const comments = ref([])
    const newComment = ref('')
    const replyContent = ref('')
    const activeReply = ref(null)
    const loading = ref(false)
    const submitting = ref(false)

    // 获取根评论（没有 parentId 的评论）
    const rootComments = computed(() => {
      return comments.value.filter(comment => !comment.parentId || comment.parentId === "");
    });

    // 获取某个评论的所有回复
    const getCommentReplies = async (parentId) => {
      const userToken = localStorage.getItem('userToken');
      try {
        const response = await axios.get(
          `http://121.43.120.166:10020/comment/algorithm/${currentAlgorithmId.value}/parent/${parentId}`,
          {
            headers: {
              Authorization: userToken
            }
          }
        );
        
        console.log(`获取评论 ${parentId} 的回复:`, response.data);
        return response.data || [];
      } catch (error) {
        console.error('获取子评论失败:', error);
        return [];
      }
    };

    // 获取评论列表
    const fetchComments = async () => {
      const userToken = localStorage.getItem('userToken');
      loading.value = true;
      console.log('开始获取评论，algorithmId:', currentAlgorithmId.value);
      
      try {
        const response = await axios.get(`http://121.43.120.166:10020/comment/algorithm/${currentAlgorithmId.value}`, {
          headers: {
            Authorization: userToken
          }
        });
        
        console.log('获取评论响应:', response.data);
        if (Array.isArray(response.data)) {
          console.log('评论数量:', response.data.length);
          comments.value = response.data; // 直接使用返回的数据
        } else {
          console.error('返回的数据不是数组格式:', response.data);
          comments.value = [];
        }
        
      } catch (error) {
        console.error('获取评论失败:', error);
        console.error('错误详情:', {
          message: error.message,
          response: error.response?.data,
          status: error.response?.status
        });
        ElMessage.error(`获取评论失败: ${error.message}`);
        comments.value = [];
      } finally {
        loading.value = false;
      }
    }

    // 提交评论
    const submitComment = async () => {
      if (!newComment.value.trim()) {
        ElMessage.warning('评论内容不能为空')
        return
      }

      submitting.value = true
      const userToken = localStorage.getItem('userToken');
      const userId = localStorage.getItem('userId');

      if (!userToken || !userId) {
        ElMessage.error('请先登录');
        submitting.value = false;
        return;
      }

      // 构造请求数据
      const commentData = {
        userId: userId,
        algorithmId: currentAlgorithmId.value,
        content: newComment.value,
        parentId: ""  // 改为空字符串而不是 null
      };

      console.log('准备提交评论，请求数据:', commentData);
      console.log('使用的 token:', userToken);

      try {
        const response = await axios.post('http://121.43.120.166:10020/comment', commentData, {
          headers: {
            'Authorization': userToken,
            'Content-Type': 'application/json'
          }
        });

        console.log('创建评论成功:', response.data);
        newComment.value = '';
        ElMessage.success('评论成功');
        await fetchComments();
      } catch (error) {
        console.error('提交评论失败:', error);
        console.error('错误详情:', {
          message: error.message,
          response: error.response?.data,
          status: error.response?.status,
          requestData: commentData // 记录发送的数据
        });
        if (error.response?.data?.message) {
          ElMessage.error(`评论失败: ${error.response.data.message}`);
        } else {
          ElMessage.error('评论失败，请稍后重试');
        }
      } finally {
        submitting.value = false;
      }
    }

    // 提交回复
    const submitReply = async (parentComment) => {
      if (!replyContent.value.trim()) {
        ElMessage.warning('回复内容不能为空')
        return
      }

      submitting.value = true
      const userToken = localStorage.getItem('userToken');
      const userId = localStorage.getItem('userId');

      if (!userToken || !userId) {
        ElMessage.error('请先登录');
        submitting.value = false;
        return;
      }

      // 构造请求数据
      const replyData = {
        userId: userId,
        algorithmId: currentAlgorithmId.value,
        content: replyContent.value,
        parentId: parentComment.commentId
      };

      console.log('准备提交回复，请求数据:', replyData);

      try {
        // 创建评论
        const response = await axios.post('http://121.43.120.166:10020/comment', replyData, {
          headers: {
            'Authorization': userToken,
            'Content-Type': 'application/json'
          }
        });

        console.log('创建回复成功:', response.data);
        
        // 清理输入和状态
        replyContent.value = '';
        activeReply.value = null;
        
        // 更新缓存的回复列表
        if (repliesCache.value.has(parentComment.commentId)) {
          const currentReplies = repliesCache.value.get(parentComment.commentId) || [];
          repliesCache.value.set(parentComment.commentId, [...currentReplies, response.data]);
        }
        
        // 如果父评论未展开，则展开它
        if (!expandedReplies.value.has(parentComment.commentId)) {
          await toggleReplies(parentComment.commentId);
        } else {
          // 如果已经展开，则刷新回复列表
          const newReplies = await getCommentReplies(parentComment.commentId);
          repliesCache.value.set(parentComment.commentId, newReplies);
        }

        ElMessage.success('回复成功');
      } catch (error) {
        console.error('提交回复失败:', error);
        console.error('错误详情:', {
          message: error.message,
          response: error.response?.data,
          status: error.response?.status,
          requestData: replyData
        });
        if (error.response?.data?.message) {
          ElMessage.error(`回复失败: ${error.response.data.message}`);
        } else {
          ElMessage.error('回复失败，请稍后重试');
        }
      } finally {
        submitting.value = false;
      }
    };

    // 显示回复输入框
    const showReplyInput = (comment) => {
      activeReply.value = comment.commentId
      replyContent.value = ''
    }

    // 取消回复
    const cancelReply = () => {
      activeReply.value = null
      replyContent.value = ''
    }

    // 格式化时间
    const formatTime = (time) => {
      return dayjs(time).fromNow()
    }

    // 监听算法ID变化
    watch(currentAlgorithmId, (newId) => {
      console.log('算法ID变化，重新获取评论:', newId);
      fetchComments();
    });

    // 添加展开/折叠状态管理
    const expandedReplies = ref(new Set());
    const repliesCache = ref(new Map()); // 添加缓存来存储子评论

    // 检查是否有回复
    const hasReplies = async (commentId) => {
      try {
        // 先检查缓存
        if (repliesCache.value.has(commentId)) {
          const cachedReplies = repliesCache.value.get(commentId);
          return cachedReplies.length > 0;
        }
        
        // 如果没有缓存，则请求服务器
        const replies = await getCommentReplies(commentId);
        const hasReply = replies.length > 0;
        console.log(`检查评论 ${commentId} 是否有回复:`, hasReply);
        return hasReply;
      } catch (error) {
        console.error('检查回复失败:', error);
        return false;
      }
    };

    // 检查是否已展开
    const isExpanded = (commentId) => {
      const expanded = expandedReplies.value.has(commentId);
      console.log(`检查评论 ${commentId} 是否展开:`, expanded);
      return expanded;
    };

    // 切换展开/折叠状态
    const toggleReplies = async (commentId) => {
      console.log('触发 toggleReplies:', commentId);
      
      if (expandedReplies.value.has(commentId)) {
        // 收起评论
        console.log('收起评论:', commentId);
        expandedReplies.value.delete(commentId);
        repliesCache.value.delete(commentId); // 清除缓存
      } else {
        // 展开评论，获取子评论
        console.log('展开评论:', commentId);
        expandedReplies.value.add(commentId);
        const replies = await getCommentReplies(commentId);
        repliesCache.value.set(commentId, replies);
      }
    };

    // 获取缓存的子评论
    const getCachedReplies = (commentId) => {
      return repliesCache.value.get(commentId) || [];
    };

    onMounted(() => {
      fetchComments()
    })

    // 提供方法给子组件使用
    provide('commentMethods', {
      formatTime,
      getCommentReplies,
      hasReplies,
      isExpanded,
      toggleReplies,
      activeReply,
      replyContent,
      submitting,
      submitReply,
      cancelReply,
      expandedReplies,
      getCachedReplies
    });

    return {
      comments,
      newComment,
      replyContent,
      activeReply,
      loading,
      submitting,
      submitComment,
      submitReply,
      showReplyInput,
      cancelReply,
      formatTime,
      rootComments,
      getCommentReplies,
      hasReplies,
      isExpanded,
      toggleReplies,
      getCachedReplies
    }
  }
}
</script>

<style scoped>
.algorithm-comment {
  padding: 30px;
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

/* 评论输入框样式 */
.comment-input {
  margin-bottom: 30px;
  padding: 24px;
  background-color: #f8f9fa;
  border-radius: 12px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.comment-input:focus-within {
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.comment-input .el-button {
  margin-top: 16px;
  padding: 12px 24px;
  font-weight: 500;
}

/* 评论列表样式 */
.comment-list {
  margin-top: 20px;
}

.comment-container {
  margin-bottom: 30px;
  padding: 20px;
  background-color: #ffffff;
  border-radius: 12px;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.comment-container:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.comment-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  padding-bottom: 12px;
  border-bottom: 1px solid #ebeef5;
}

.user-id {
  font-weight: 500;
  color: #303133;
}

.comment-time {
  color: #909399;
  font-size: 13px;
}

.comment-content {
  margin: 16px 0;
  color: #303133;
  line-height: 1.6;
  font-size: 14px;
}

.comment-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #ebeef5;
}

/* 回复区域样式 */
.replies-container {
  margin-left: 40px;
  padding-left: 20px;
  border-left: 2px solid #e4e7ed;
}

.nested-comment {
  margin-top: 16px;
  padding: 16px;
  background-color: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.nested-comment:hover {
  background-color: #f2f6fc;
}

/* 回复输入框样式 */
.reply-input {
  margin: 16px 0;
  padding: 16px;
  background-color: #ffffff;
  border-radius: 8px;
  border: 1px solid #dcdfe6;
}

.reply-input .el-button {
  margin-top: 12px;
  margin-right: 12px;
}

/* 按钮样式优化 */
.el-button.is-link {
  font-size: 13px;
  padding: 4px 8px;
}

.el-button.is-link:hover {
  color: #409eff;
  background-color: #ecf5ff;
  border-radius: 4px;
}

/* 加载状态样式 */
.el-loading-spinner {
  margin-top: 20px;
}

/* 动画效果 */
.nested-replies {
  transition: all 0.3s ease-in-out;
}

.replies-container {
  transition: all 0.3s ease-in-out;
}
</style>