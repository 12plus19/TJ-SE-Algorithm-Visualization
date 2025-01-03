// filepath: /F:/软件工程课程设计/zk_algo/src/router/index.js
import { createRouter, createWebHistory } from 'vue-router';
import LoginPage from '@/components/User/LoginPage.vue';
import RegisterPage from '@/components/User/RegisterPage.vue'; 
import ChangePasswordPage from '@/components/User/ChangePasswordPage.vue';
import AlgorithmDetail from '@/components/AlgorithmMessage/AlgorithmDetail.vue';  
import AlgorithmVisual1 from '@/components/AlgorithmVisual/AlgorithmVisual1.vue';
import AlgorithmVisual2 from '@/components/AlgorithmVisual/AlgorithmVisual2.vue';
import AlgorithmVisual3 from '@/components/AlgorithmVisual/AlgorithmVisual3.vue';
import AlgorithmMessage from '@/components/AlgorithmMessage/AlgorithmMessage.vue';
import AlgorithmForum from '@/components/Comment/AlgorithmForum.vue';
import UserInfo from '@/components/UserInfo/UserInfo.vue';
import ExercisePage from '@/components/Exercise/ExercisePage.vue';

const routes = [
  {
    path: '/',
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
    path: '/algorithm-visual1',
    name: 'AlgorithmVisual1',
    component: AlgorithmVisual1
  },
  {
    path: '/algorithm-visual2',
    name: 'AlgorithmVisual2',
    component: AlgorithmVisual2,
    props: true,
  },
  {
    path: '/algorithm-visual3',
    name: 'AlgorithmVisual3',
    component: AlgorithmVisual3
  },
  {
    path: '/algorithm-message',
    name: 'AlgorithmMessage',
    component: AlgorithmMessage
  },
  {
    path: '/user-info',
    name: 'UserInfo',
    component: UserInfo
  },
  {
    path: '/exercise',
    name: 'Exercise',
    component: ExercisePage
  },
  {
    path: '/forum/:algorithmId',
    name: 'AlgorithmForum',
    component: AlgorithmForum,
    props: true,
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;