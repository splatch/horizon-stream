import { createRouter, createWebHistory } from 'vue-router'
import Login from '@/containers/Login.vue'
import Dashboard from '@/containers/Dashboard.vue'
import useToken from '@/composables/useToken'

const { isAuthenticated } = useToken()

const router = createRouter({
  history: createWebHistory(),
  routes: [
    {
      path: '/',
      name: 'Dashboard',
      component: Dashboard
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/:pathMatch(.*)*', // catch other paths and redirect
      redirect: '/'
    }
  ]
})

router.beforeEach(async (to) => {
  if (!isAuthenticated.value && to.name !== 'Login') {
    return { name: 'Login' }
  }
})

export default router
