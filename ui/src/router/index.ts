import { createRouter, createWebHistory } from 'vue-router'
import Appliances from '@/containers/Appliances.vue'

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Appliances',
      component: Appliances
    },
    {
      path: '/test', // For now, keep the alarms test available at this route
      name: 'Alarms',
      component: () => import('@/containers/Alarms.vue')
    },
    {
      path: '/:pathMatch(.*)*', // catch other paths and redirect
      redirect: '/'
    }
  ]
})

export default router
