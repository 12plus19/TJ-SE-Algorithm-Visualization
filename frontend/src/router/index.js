// filepath: /F:/软件工程课程设计/zk_algo/src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import LoginPage from '@/components/User/LoginPage.vue';
import RegisterPage from '@/components/User/RegisterPage.vue'; 
import ChangePasswordPage from '@/components/User/ChangePasswordPage.vue';
import AlgorithmDetail from '@/components/AlgorithmMessage/AlgorithmDetail.vue';  
import UserInfo from '@/components/UserInfo/UserInfo.vue';
import ExercisePage from '@/components/Exercise/ExercisePage.vue';

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
  },
  {
    path: '/user-info',
    name: 'UserInfo',
    component: UserInfo
  },
  {
    path: '/exercisepage',
    name: 'ExercisePage',
    component: ExercisePage
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;