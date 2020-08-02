import Vue from 'vue'
import VueRouter from 'vue-router'
import Index from '../views/Index.vue'
import Create from '../views/Create.vue'
import Signin from '../views/Signin.vue'

Vue.use(VueRouter)

  const routes = [
  {
    path: '/',
    name: 'Index',
    component: Index
  },
  {
    path: '/create',
    name: 'Create',
    component: Create
  },
  {
    path: '/signin',
    name: 'Signin',
    component: Signin
  }
]

const router = new VueRouter({
  mode: 'history',
  base: process.env.BASE_URL,
  routes
})

export default router
