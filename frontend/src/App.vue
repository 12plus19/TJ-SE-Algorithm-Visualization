<template>
  <div id="app">
    <!-- 路由切换的内容会显示在这个地方 -->
    <router-view />
  </div>
</template>

<script>
import { defineComponent } from 'vue';

export default defineComponent({
  name: 'App',
  setup() {
    return {};
  }
});
</script>

<style>
/* 你可以在这里添加全局样式 */
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
  padding: 0;
  max-width: 1536px;
  margin: 0;
}
</style>
