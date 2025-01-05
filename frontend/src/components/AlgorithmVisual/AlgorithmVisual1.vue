<template>
    <div class="algorithm-visual">
        <header>
            <div style="display: flex; align-items: center; width: 100%;">
                <div style="flex: 1;">
                    <button class="info-btn" style="padding: 10px 20px; margin-right: 10px;" @click="goToAlgorithmMessage()"><strong>详细信息</strong></button>
                    <button class="info-btn" style="padding: 10px 20px; margin-right: 10px;" @click="goToExercise()"><strong>练习题</strong></button>
                    <button class="info-btn" style="padding: 10px 20px; margin-right: 10px;" @click="showHistoryModal = true"><strong>历史记录</strong></button>
                </div>
                <!-- 历史记录弹窗 -->
<div class="modal-overlay" v-if="showHistoryModal" @click.self="showHistoryModal = false">
    <div class="modal-content">
        <div class="modal-header">
            <h3>代码历史记录</h3>
            <button class="close-btn" @click="showHistoryModal = false">&times;</button>
        </div>
        <div class="modal-body">
            <div v-if="historyCodes.length > 0" class="history-list">
                <div v-for="record in historyCodes" 
                     :key="record.codeId" 
                     class="history-item"
                     @click="loadHistoryCode(record)">
                    <div class="history-info">
                        <span class="code-id">记录ID: {{ record.codeId }}</span>
                        <span class="save-time">保存时间: {{ formatTime(record.saveTime) }}</span>
                    </div>
                </div>
            </div>
            <div v-else class="empty-message">
                暂无历史记录
            </div>
        </div>
    </div>
</div>

                <div style="flex: 2; text-align: center;">
                    <h1>算法可视化平台</h1>
                </div>
                <div style="flex: 1; text-align: right;">
                    <button class="profile-btn" style="padding: 10px 20px; margin-right: 10px;" @click="goToUserInfo()"><strong>个人信息</strong></button>
                    <button class="profile-btn" style="padding: 10px 20px;" @click="saveCode()"><strong>保存代码</strong></button>
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
                
                <div class="chessboard" :style="boardStyle">
    <div v-for="(row, i) in board" :key="i" class="row">
        <div v-for="(cell, j) in row" 
             :key="`${i}-${j}`" 
             :class="['cell', ((i + j) % 2 === 0) ? 'white' : 'black']">
            <div v-if="cell === 1" 
                 class="queen" 
                 :class="{ 'queen-animated': isAnimating && lastPlaced.row === i && lastPlaced.col === j }">
                ♕
            </div>
        </div>
    </div>
</div>
                <div class="control-panel">
                    <button @click="runCode" class="run-btn" :disabled="isAnimating">运行代码</button>
                    <button @click="resetBoard" class="reset-btn" :disabled="isAnimating">重置棋盘</button>
                    <button @click="stopAnimation" class="stop-btn" v-if="isAnimating">停止动画</button>
                    <div class="speed-control">
                        <label>动画速度：</label>
                        <input type="range" v-model.number="animationSpeed" min="100" max="2000" step="100">
                        <span>{{ animationSpeed }}ms</span>
                    </div>
                </div>
            </section>
            <section class="code-area">
                <div class="editor-container">
                    <div class="editor-header" style = "white-space: pre-wrap">
                        <span>代码编辑器</span>
                    </div>
                    <textarea
                        v-model="code"
                        class="code-editor"
                        spellcheck="false"
                        @input="handleInput"
                    ></textarea>
                </div>
                <div class="output-container">
                    <div class="output-header">
                        <span>输出结果</span>
                    </div>
                    <div class="output-content">
                        {{ output }}
                    </div>
                </div>
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
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import AlgorithmForum from '@/components/Comment/AlgorithmForum.vue';
import { setCurrentAlgorithmId } from '@/store/algorithmStore';
import { ElMessage } from 'element-plus'; 
export default {
    components: {
        AlgorithmForum
    },
    name: 'AlgorithmVisual',
    setup() {
        const algorithms = ref([]);
        const board = ref([]);
        const isAnimating = ref(false);
        const animationSpeed = ref(500);
        const lastPlaced = ref({ row: -1, col: -1 });
        const animationTimeout = ref(null);
        const output = ref('');
        const animationSteps = ref([]);
        const userId = localStorage.getItem('userId');
        const userToken = localStorage.getItem('userToken');
        const submitting = ref(false);


        // 默认代码模板
        const code = ref(`// 可自定义皇后数量的N皇后问题解法
function solveNQueens(n) {
    const board = Array(n).fill().map(() => Array(n).fill(0));
    const solutions = [];
    
    function isValid(row, col) {
        // 检查同一列
        for (let i = 0; i < row; i++) {
            if (board[i][col] === 1) return false;
        }
        
        // 检查左上对角线
        for (let i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
            if (board[i][j] === 1) return false;
        }
        
        // 检查右上对角线
        for (let i = row - 1, j = col + 1; i >= 0 && j < n; i--, j++) {
            if (board[i][j] === 1) return false;
        }
        
        return true;
    }
    
    function backtrack(row) {
        if (row === n) {
            solutions.push(board.map(row => [...row]));
            return;
        }
        
        for (let col = 0; col < n; col++) {
            if (isValid(row, col)) {
                board[row][col] = 1;
                // 添加暂停点，用于动画展示
                if (window.addAnimationStep) {
                    window.addAnimationStep(board.map(row => [...row]), row, col);
                }
                backtrack(row + 1);
                board[row][col] = 0;
                // 添加回溯的暂停点
                if (window.addAnimationStep) {
                    window.addAnimationStep(board.map(row => [...row]), row, col);
                }
            }
        }
    }
    
    backtrack(0);
    return solutions;
}

// 运行N皇后问题
const n = 8; // 使用当前设置的皇后数量
const solutions = solveNQueens(n);
return solutions[0]; // 返回第一个解
`);
        const computedAlgorithms = computed(() => {
            return algorithms.value;
        });

        const showHistoryModal = ref(false)
        const historyCodes = ref([])

        // 格式化时间
const formatTime = (timeStr) => {
  return new Date(timeStr).toLocaleString()
}

// 加载选中的历史记录
const loadHistoryCode = (record) => {
  code.value = record.content
  showHistoryModal.value = false
}

        const saveCode = async () => { 
            const algorithmId = '1';
            const userId = localStorage.getItem('userId');
            const userToken = localStorage.getItem('userToken');
            try {
                const codeCreateResponse = await axios.post(
                    'http://121.43.120.166:10020/code',
                    {
                        content:code.value,
                    },
                    {
                        params: {
                            userId: userId,
                            algorithmId: algorithmId
                        },
                        headers: {
                            Authorization: userToken,
                        },

                    }
                );
                if (codeCreateResponse.status === 200) {
                    alert('代码保存成功');
                }
            }catch (error) {
                console.error('Error saving code:', error);
            }
        }

        const getNewCode = async () => {
            const myCodes=ref([]);
            const algorithmId = '1';
            const userId = localStorage.getItem('userId');
            const userToken = localStorage.getItem('userToken');
            const newCode=ref('');
            try {
                const codeResponse = await axios.get(
                    'http://121.43.120.166:10020/code/codes',
                    {
                        params: {
                            userId: userId,
                            algorithmId: algorithmId
                        },
                        headers: {
                            Authorization: userToken,
                        },
                    }
                );
                if (codeResponse.status === 200) {
                    myCodes.value = codeResponse.data;
                    console.log('获取代码成功:', myCodes.value);
                    historyCodes.value=myCodes.value;
                    // 找到 saveTime 最大的代码项
                    const latestCode = myCodes.value.reduce((latest, current) => {
                        console.log(current.saveTime, latest.saveTime)
                        return (new Date(current.saveTime) > new Date(latest.saveTime)) ? current : latest;
                    }, myCodes.value[0]);

                    console.log('最新代码项:', latestCode);

                    // 将 newCode 设置为最新的代码内容
                    newCode.value = latestCode.content || '';
                    if (newCode.value === '') {
                        newCode.value = code.value;
                    }

                    newCode.value = newCode.value.slice(12, -2);
                    const newcode = newcode.replace(/\\n/g, '\n');
                    console.log('最新代码:', newCode.value);

                    // 更新编辑器内容
                    code.value = newCode.value;
                }
            } catch (error) {
                console.error('Error fetching codes:', error);
            }
        }

        const getBoardSizeFromCode = () => {
            // 从代码中解析n的值
            const match = code.value.match(/const\s+n\s*=\s*(\d+)/);
            return match ? parseInt(match[1]) : 8; // 默认值为8
        };

        const boardStyle = computed(() => {
            const currentSize = getBoardSizeFromCode();
            const cellSize = Math.floor(400 / currentSize);
            const totalSize = cellSize * currentSize;
            
            return {
                width: totalSize + 'px',
                height: totalSize + 'px',
                gridTemplate: `repeat(${currentSize}, 1fr) / repeat(${currentSize}, 1fr)`,
                fontSize: `${Math.floor(cellSize * 0.6)}px`
            }
        });

        const router = useRouter();

        watch(code, () => {
            resetBoard(); // 当代码改变时重置棋盘
        });

        const handleInput = (e) => {
            code.value = e.target.value;
        };

        const resetBoard = () => {
            const currentSize = getBoardSizeFromCode();
            board.value = Array(currentSize).fill(null).map(() => Array(currentSize).fill(0));
            output.value = '';
            isAnimating.value = false;
            if (animationTimeout.value) {
                clearTimeout(animationTimeout.value);
            }
        };

        const stopAnimation = () => {
            isAnimating.value = false;
            if (animationTimeout.value) {
                clearTimeout(animationTimeout.value);
            }
        };

        const runCode = async () => {
            try {
                resetBoard();
                animationSteps.value = [];
                isAnimating.value = true;

                // 添加动画步骤收集函数
                window.addAnimationStep = (boardState, row, col) => {
                    animationSteps.value.push({
                        board: boardState,
                        row: row,
                        col: col
                    });
                };

                // 执行代码
                new Function(code.value)();

                // 开始动画展示
                let stepIndex = 0;
                const animate = () => {
                    if (!isAnimating.value || stepIndex >= animationSteps.value.length) {
                        isAnimating.value = false;
                        return;
                    }

                    const step = animationSteps.value[stepIndex];
                    board.value = step.board;
                    lastPlaced.value = { row: step.row, col: step.col };

                    stepIndex++;
                    animationTimeout.value = setTimeout(animate, animationSpeed.value);
                };

                animate();
                output.value = '正在运行动画展示...';

            } catch (error) {
                output.value = `代码执行错误：${error.message}`;
                isAnimating.value = false;
            }
        };

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
            const algorithmId = '1'; // 示例ID
            // router.push({ name: 'AlgorithmDetail', params: { id: algorithmId } });
            window.open('https://www.codeproject.com/Articles/11865/Graphical-Solution-to-Eight-Queen-Problem', '_blank');
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
            let algorithmId = '1';
            
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
                    routeName = 'AlgorithmVisual1';
                    algorithmId = '1';
            }
            
            setCurrentAlgorithmId(algorithmId);
            router.push({ name: routeName, params: { name: algorithmName } });
        };

        const goToUserInfo = () => {
            router.push({ name: 'UserInfo' });
        };


        const goToExercise = () => {
            const algorithmId = computedAlgorithms.value[0].algorithmId; // 示例ID，实际上应从选中的算法数据中获取
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
            resetBoard();
            getNewCode();
        });

        return {
            formatTime,
            loadHistoryCode,
            showHistoryModal,
            getNewCode,
            saveCode,
            goToAlgorithmMessage,
            fetchAlgorithms,
            computedAlgorithms,
            goToAlgorithmPage,
            goToUserInfo,
            goToExercise,
            code,
            output,
            board,
            handleInput,
            runCode,
            resetBoard,
            isAnimating,
            animationSpeed,
            lastPlaced,
            stopAnimation,
            boardStyle
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
    height: 700px;
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
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.code-area {
    flex: 1;
    background-color: #ffffff;
    margin: 0;
    border: 2px solid #2c3e50;
    border-radius: 10px;
    padding: 15px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
}

.editor-container {
    height: 120%;
    margin-bottom: 10px;
}

.editor-header {
    background-color: #2c3e50;
    color: white;
    padding: 8px;
    border-top-left-radius: 6px;
    border-top-right-radius: 6px;
}

.code-editor {
    width: 100%;
    height: calc(100% - 36px);
    padding: 10px;
    font-family: 'Courier New', monospace;
    font-size: 14px;
    line-height: 1.5;
    border: 1px solid #ddd;
    border-bottom-left-radius: 6px;
    border-bottom-right-radius: 6px;
    resize: none;
    background-color: #1e1e1e;
    color: #d4d4d4;
}

.output-container {
    height: 40%;
}

.output-header {
    background-color: #2c3e50;
    color: white;
    padding: 8px;
    border-top-left-radius: 6px;
    border-top-right-radius: 6px;
}

.output-content {
    height: calc(100% - 36px);
    padding: 10px;
    background-color: #f5f5f5;
    border: 1px solid #ddd;
    border-bottom-left-radius: 6px;
    border-bottom-right-radius: 6px;
    overflow-y: auto;
    font-family: 'Courier New', monospace;
    white-space: pre-wrap;
}

.chessboard {
    border: 2px solid #2c3e50;
  margin: 20px auto;
  background-color: #b58863;
  display: grid;
  max-width: 100%; /* 确保在小屏幕上不会溢出 */
  max-height: 100vh; /* 确保不会超过视口高度 */
}

.cell {
    width: 100%;
    height: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    position: relative;
}

.white {
    background-color: #f0d9b5;
}

.black {
    background-color: #b58863;
}

.queen {
  color: #000;
  text-shadow: 1px 1px 2px rgba(0,0,0,0.3);
  /* 删除固定的font-size，使用计算后的大小 */
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}

@media (max-width: 1200px) {
  .chessboard {
    /* 在小屏幕上动态调整大小 */
    width: min(300px, 90vw) !important;
    height: min(300px, 90vw) !important;
  }
  
  .queen {
    /* 在小屏幕上相应调整皇后大小 */
    font-size: calc(min(300px, 90vw) / (var(--board-size, 8) * 1.5)) !important;
  }
}

@keyframes queenPlaced {
    0% {
        transform: scale(0);
        opacity: 0;
    }
    50% {
        transform: scale(1.2);
    }
    100% {
        transform: scale(1);
        opacity: 1;
    }
}

.control-panel {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 10px;
    margin-top: 20px;
    width: 100%;
}

.size-control {
    margin-bottom: 20px;
    display: flex;
    align-items: center;
    gap: 10px;
}

.size-control input {
    width: 60px;
    padding: 5px;
    border: 1px solid #ccc;
    border-radius: 4px;
}

.speed-control {
    display: flex;
    align-items: center;
    gap: 10px;
    margin-top: 10px;
}

.speed-control input {
    width: 150px;
}

.run-btn, .reset-btn, .stop-btn {
    padding: 8px 16px;
    border: none;
    border-radius: 4px;
    cursor: pointer;
    font-weight: bold;
    transition: all 0.3s ease;
}

.run-btn {
    background-color: #4CAF50;
    color: white;
}

.reset-btn {
    background-color: #f44336;
    color: white;
}

.stop-btn {
    background-color: #ff9800;
    color: white;
}

.run-btn:hover, .reset-btn:hover, .stop-btn:hover {
    transform: translateY(-2px);
    box-shadow: 0 2px 8px rgba(0,0,0,0.2);
}

.run-btn:disabled, .reset-btn:disabled {
    background-color: #cccccc;
    cursor: not-allowed;
    transform: none;
    box-shadow: none;
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

    .chessboard {
        width: 300px;
        height: 300px;
    }
}

html {
    scroll-behavior: smooth;
}

.algorithm-visual * {
    transition: all 0.3s ease;
}

.history-btn {
  padding: 8px 16px;
  background-color: #4CAF50;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  margin: 10px;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: white;
  border-radius: 8px;
  width: 80%;
  max-width: 600px;
  max-height: 80vh;
}

.modal-header {
  padding: 15px;
  border-bottom: 1px solid #ddd;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.modal-body {
  padding: 15px;
  overflow-y: auto;
}

.history-item {
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: background-color 0.2s;
}

.history-item:hover {
  background-color: #f5f5f5;
}

.history-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.code-id {
  font-weight: bold;
  color: #333;
}

.save-time {
  color: #666;
  font-size: 0.9em;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
}

.history-list {
  max-height: 60vh;
  overflow-y: auto;
}

@media (max-width: 768px) {
  .modal-content {
    width: 95%;
    margin: 10px;
  }
  
  .history-info {
    flex-direction: column;
    gap: 5px;
  }
}
</style>