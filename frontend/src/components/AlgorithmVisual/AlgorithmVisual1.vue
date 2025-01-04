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
                <div class="board">
                    <div v-for="(row, y) in board" :key="`row-${y}`" class="row">
                        <div v-for="(cell, x) in row" :key="`cell-${x}-${y}`" class="cell"
                            :class="{ selected: isSelected(x, y) }" :style="{ backgroundColor: getCellColor(x, y) }">
                        </div>
                    </div>
                    <div class="queens">
                        <transition-group name="queen" tag="div">
                            <div v-for="(queen, i) in queens" :key="`queen-${i}`" class="queen"
                                :class="{ 'visible': queenVisibility[i] }"
                                :style="{ left: `${queen[1] * 50}px`, top: `${queen[0] * 50}px` }">
                                <div class="queen-image"></div>
                            </div>
                        </transition-group>
                    </div>
                </div>
                <button class="QueenButton" @click="solveNQueens">Solve N Queens</button>
            </section>
            <section class="code-area">

                <div ref="luaEditor" class="lua-editor">
                    
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
import { ref, computed, onMounted, watch} from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import AlgorithmForum from '@/components/Comment/AlgorithmForum.vue'
import { setCurrentAlgorithmId } from '@/store/algorithmStore'
import { ElMessage } from 'element-plus';

//llbuff

import ace from 'ace-builds'
import 'ace-builds/css/ace.css'
//import 'ace-builds/webpack-resolver' // 全量导入
// #endregion
// #region 代码提示
import 'ace-builds/src-noconflict/ext-language_tools'
// #endregion
// #region 代码校验
ace.config.setModuleUrl(
 'ace/mode/base_worker',
 require('file-loader?esModule=false!ace-builds/src-noconflict/worker-base.js')
)
ace.config.setModuleUrl(
 'ace/mode/lua_worker',
 require('file-loader?esModule=false!ace-builds/src-noconflict/worker-lua.js')
)
// #endregion
// #region 主题
import 'ace-builds/src-noconflict/theme-chrome'
// #endregion
// #region 其他功能
import 'ace-builds/src-noconflict/ext-searchbox'
import 'ace-builds/src-noconflict/ext-keybinding_menu'
import 'ace-builds/src-noconflict/ext-settings_menu'
// 定义辅助函数
function isSelected(x, y, selectedCell) {
    return selectedCell.value[0] === y && selectedCell.value[1] === x;
}

function getCellColor(x, y, selectedCell) {
    if (isSelected(x, y, selectedCell)) {
        return '#ffa';
    }
    return (x + y) % 2 === 0 ? '#ccc' : '#fff';
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}
const currentQueen = ref(0);
async function solveNQueens(N, board, queens, selectedCell, nQ, toContinue, queenVisibility,sleepTime) {
    // Reset the board, queens, and currentQueen  
    currentQueen.value = N;
    await sleep(sleepTime.value+500);
    currentQueen.value = 0;
    board.value = Array.from({ length: N }, () => Array(N).fill(0));
    queens.value = Array.from({ length: N }, () => [-1, -1]);
    selectedCell.value = [-1, -1];
    toContinue.value = true;
    queenVisibility.value = Array.from({ length: N }, () => false);
    await nQ(currentQueen.value);
}

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
                    routeName = 'AlgorithmVisual2';
                    algorithmId = '1';
            }
            
            setCurrentAlgorithmId(algorithmId); // 设置当前算法ID
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
        });

        //llbuff
        let setupFunctionSource = `  
const N = 5
const board = ref(Array.from({ length: N }, () => Array(N).fill(0)))
const queens = ref(Array.from({ length: N }, () => [-1, -1]))
const selectedCell = ref([-1, -1])
let currentQueen = 0
const toContinue = ref(true)
const sleepTime = ref(100)

function validState(row, col, currentQueen) {
    // Check if the current position is valid for the queen
    for (let i = 0; i < currentQueen; i++) {
        const [queenRow, queenCol] = queens.value[i]
        if (queenRow === row || queenCol === col || Math.abs(queenRow - row) === Math.abs(queenCol - col)) {
            return false
        }
    }
    return true
}

async function nQ(currentQueen) {
    if (currentQueen === 0) {
        board.value = Array.from({ length: N }, () => Array(N).fill(0));
        queens.value = Array.from({ length: N }, () => [-1, -1]);
        selectedCell.value = [-1, -1];
        toContinue.value = true;
    }
    if (currentQueen >= N) {
        return
    }

    for (let row = 0; row < N; row++) {
        queens.value[currentQueen] = [row, currentQueen]
        await new Promise((resolve) => setTimeout(resolve, sleepTime.value))
        if (validState(row, currentQueen, currentQueen)) {
            queens.value[currentQueen] = [row, currentQueen]
            selectedCell.value = [row, currentQueen]

            // Update the board to show the current queen
            board.value[row][currentQueen] = 1
            console.log("坐标：", row, currentQueen)

            await new Promise((resolve) => setTimeout(resolve, sleepTime.value))
            if (currentQueen === N - 1) {
                console.log("找到了！", queens.value)
                toContinue.value = false
                return
            }
            currentQueen++
            await nQ(currentQueen)
            if (toContinue.value === false) {
                return
            }
            // If the current position is not valid, reset the selected cell and board
            selectedCell.value = [-1, -1]
            board.value[row][currentQueen] = 0
            currentQueen--
        }
    }
}

return {
    N,
    board,
    queens,
    nQ,
    currentQueen,
    selectedCell,
    validState,
    toContinue,
    sleepTime,
}
`
        const setupCode = ref(setupFunctionSource)
        

        let setup = null
        function defineSetupFunction() {
            console.log(setupCode.value)
            setup = new Function(
                'ref',
                `  
          ${setupCode.value}  
         
        `
            )(ref)
            return setup;
        }
        const toReturn = defineSetupFunction();
        const updateSetupFunction = (code) => {
            setupCode.value = code
            setupFunctionSource = code
            const newSetup = defineSetupFunction();
            // 更新 toReturn 的属性  
            Object.assign(toReturn, newSetup);
            Object.assign(setup, newSetup);
            // 在 Vue 组件中刷新当前路由  
            router.go();
            // 刷新当前页面  
            location.reload();
        }  
        const queenVisibility = ref(Array.from({ length: toReturn.N }, () => false)); 
        // 创建 updateQueenVisibility 方法
        function updateQueenVisibility(index,N) {
            if (index >= 0 && index < N) {
                queenVisibility.value[index] = true;
            }
        }
        // 使用 watch 来监视 queens 的变化
        watch(toReturn.queens, (newQueens, ) => {
            newQueens.forEach((queen, index) => {
                // 如果新的皇后位置不是 [-1, -1] 并且旧的位置是 [-1, -1] 或者位置有变化，则更新 visibility
                if (queen[0] !== -1 && queen[1] !== -1) {
                    updateQueenVisibility(index, toReturn.N);

                }
            });
        }, { deep: true });

        //llbuff结束


        return {
            goToAlgorithmMessage,
            fetchAlgorithms,
            computedAlgorithms,
            goToAlgorithmPage,
            goToUserInfo,
            goToExercise,

            //llbuff
            N: toReturn.N,
            board: toReturn.board,
            queens: toReturn.queens,
            nQ: toReturn.nQ,
            validState: toReturn.validState,
            toContinue: toReturn.toContinue,
            sleepTime: toReturn.sleepTime,

            setupCode,
            updateSetupFunction,  

            isSelected: (x, y) => isSelected(x, y, toReturn.selectedCell),
            getCellColor: (x, y) => getCellColor(x, y, toReturn.selectedCell),
            solveNQueens: () => solveNQueens(toReturn.N, toReturn.board, toReturn.queens, toReturn.selectedCell, toReturn.nQ, toReturn.toContinue, queenVisibility, toReturn.sleepTime),
            
            queenVisibility
            //llbuff结束
        };
    },

    //llbuff
    data() {
    // 使用 JSDoc 注释来类型化 editorOptions
    /** @type {import('ace-builds').Ace.EditorOptions} */
    const editorOptions = {
      mode: 'ace/mode/lua',
      theme: 'ace/theme/chrome',
      tabSize: 2,
      selectionStyle: 'text',
      dragEnabled: true,
      useWorker: true,
      enableAutoIndent: true,
      showLineNumbers: true,
      useSoftTabs: true,
      fadeFoldWidgets: true,
      showPrintMargin: false,
      highlightActiveLine: true,
      highlightSelectedWord: true,
      autoScrollEditorIntoView: true,
      copyWithEmptySelection: true,
      enableLiveAutocompletion: true,
      enableSnippets: true,
    };
    return {
      content: this.value || '',
      editorOptions, // 将 editorOptions 作为数据属性返回
      editor: null,  // 初始化编辑器实例为 null
    };
  },
  computed: {
    /** @return {import('ace-builds').Ace.Editor} */
    _editor() {
      return this.editor
    }
  },
  watch: {
    value(nval) {
      this.syncContent(nval)
    },
    options(nval) {
      this.syncOptions(nval)
    }
  },
  // #endregion
  
  // #region 生命周期
  created() {},
  mounted() {
    this.initEditor()
  },
  beforeUnmount() {
    this._editor.destroy()
  },
  // #endregion

  methods: {
    /**
     * 同步内容
     */
      initEditor() {
          if (this.editor) {
              this.editor.destroy();
          }

          // 确保 DOM 元素存在并且已经渲染
          this.$nextTick(() => {
              this.editor = ace.edit(this.$refs.luaEditor, this.editorOptions);

              // 设置初始内容
              this.editor.setValue(this.setupCode, 1); // 第二个参数 1 表示不触发 change 事件

              // 监听编辑器值的变化
              this.editor.session.on('change', () => {
                  this.content = this.editor.getValue();
                  this.$emit('input', this.content);
              });
          });
      },
    syncContent(value = this.value) {
      if (this.content != value) {
        this._editor?.setValue(value, 1)
      }
    },
    /**
     * 同步配置
     */
    syncOptions(option = this.option) {
      let keys = Object.keys(option)
      for (let key of keys) {
        // 禁止修改固定的option
        if (key in this.editorOptions) continue
        this._editor?.setOption(key, option[key])
      }
    }
  }
  //llbuff结束
};
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

/* .visualization-area {
    flex: 1;
    background-color: #ffffff;
    margin: 0;
    border: 2px solid #007BFF;
    border-radius: 10px;
    padding: 15px;
    box-shadow: 0 2px 12px rgba(0, 123, 255, 0.1);
} */
.visualization-area {
    flex: 1;
    background-color: #ffffff;
    margin: 10px;
    border: 1px solid #007BFF; /* Blue border */
    border-radius: 10px; /* Rounded corners */
    display: flex;
    flex-direction: column;
    justify-content: flex-end; /* 垂直方向底部对齐 */
    align-items: center; /* 水平方向居中对齐 */
    padding-bottom: 20px; /* 底部内边距 */
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

.lua-editor {
  width: 100%;
  height: 100%;
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

.board {  
    display: flex; 
    flex-wrap: wrap; 
    width: 500px; 
    height: 500px; 
    position: relative; 
    margin: auto; 
    margin-top: 10px;
    z-index: 100000;
    background-color: #f0f4b4; /* 背景色 */  
}  

.cell {  
  width: 50px;  
  height: 50px;  
}  

.selected {  
  background-color: #ffa !important;  
}  

.queen {  
  position: absolute;  
  display:flex;
  width: 50px;  
  height: 50px;  
  left: 0px; /* 如果(1,1)是第一格则为0px，否则为50px */
  top: 0px;  /* 如果(1,1)是第一格则为0px，否则为50px */
  visibility: hidden;
}

.queen.visible {
    visibility: visible;
}

.circle {  
  display:flex;
  width: 40px;  
  height: 40px;  
  border-radius: 50%;  
  background-color: red;  
  position: relative;  
  top: 5px;  
  left: 5px;  
}  

.queen-image {  
  position: absolute;
  top: 50%;                       /* 垂直居中 */
  left: 50%;                      /* 水平居中 */
  transform: translate(-50%, -50%);
  width: 80%;
  height: 80%;  
  background-image: url('../../assets/queen2.png');  
  background-size: cover;  
  background-position: center;
}

.QueenButton {
    margin-top: 20px; /* 顶部外边距 */
    padding: 10px 20px;
    background-color: #007BFF;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
}

.QueenButton:hover {
    background-color: #0056b3;
}

.QueenButton:focus {
  outline: none; /* 去除按下时的边框 */
}

.QueenButton:active {
  background-color: #004085; /* 按下时的背景色 */
  transform: scale(1); /* 按下时恢复原大小 */
}

</style>