// filepath: /F:/软件工程课程设计/zk_algo/src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import LoginPage from '@/components/User/LoginPage.vue';
import RegisterPage from '@/components/User/RegisterPage.vue'; 
import ChangePasswordPage from '@/components/User/ChangePasswordPage.vue';
import AlgorithmDetail from '@/components/AlgorithmMessage/AlgorithmDetail.vue';  

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: LoginPage
  },
  {
    path: '/register',
    name: 'Register',
    component: RegisterPage
  },
  {
    path: '/change-password',
    name: 'ChangePassword',
    component: ChangePasswordPage
  },
  {
    path: '/algorithm-detail',
    name: 'AlgorithmDetail',
    component: AlgorithmDetail
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;