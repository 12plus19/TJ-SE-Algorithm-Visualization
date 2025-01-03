import { ref } from 'vue'

export const currentAlgorithmId = ref('1') // 默认为1

export const setCurrentAlgorithmId = (id) => {
  currentAlgorithmId.value = id
  console.log('当前算法ID更新为:', id)
} 