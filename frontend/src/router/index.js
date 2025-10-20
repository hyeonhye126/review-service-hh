import { createRouter, createWebHashHistory } from 'vue-router';

const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/',
      component: () => import('../components/pages/Index.vue'),
    },
    {
      path: '/customers',
      component: () => import('../components/ui/CustomerGrid.vue'),
    },
    {
      path: '/orders',
      component: () => import('../components/ui/OrderGrid.vue'),
    },
    {
      path: '/stores',
      component: () => import('../components/ui/StoreGrid.vue'),
    },
    {
      path: '/reviews',
      component: () => import('../components/ui/ReviewGrid.vue'),
    },
  ],
})

export default router;
