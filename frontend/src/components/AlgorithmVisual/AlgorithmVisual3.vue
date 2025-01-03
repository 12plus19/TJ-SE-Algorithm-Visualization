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
                        <p @click="goToAlgorithmPage(algorithm.algorithmName)"><strong>{{ algorithm.algorithmName
                                }}</strong></p>
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
                                <div class="circle"></div>
                            </div>
                        </transition-group>
                    </div>
                </div>
                <button @click="solveNQueens">Solve N Queens</button>
            </section>
            <section class="code-area">
                <!-- 可视化区域 -->
                <div ref="luaEditor" class="lua-editor">
                    
                </div>
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
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
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
    name: 'AlgorithmVisual',
    props: {
        value: {
            type: String,
            default: '', // 默认为空字符串
        },
        options: {
            type: Object,
            default: () => ({}),
        },
    },
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
                    routeName = 'AlgorithmVisual3'; // 默认页面
            }
            router.push({ name: routeName, params: { name: algorithmName } });
        };


        onMounted(() => {
            fetchAlgorithms();
        });

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
        return {
            goToAlgorithmMessage,
            fetchAlgorithms,
            computedAlgorithms,
            goToAlgorithmPage,

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
        };
    },

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
    background-color: #007BFF; /* Blue background */
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

.lua-editor {
  width: 100%;
  height: 100%;
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

</style>