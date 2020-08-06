import Vue from "vue";
import VueRouter from "vue-router";
import Index from "../views/Index.vue";
import Create from "../views/Create.vue";
import Signin from "../views/Signin.vue";

// store
import Store from "@/store";

Vue.use(VueRouter);

const routes = [
  {
    path: "/",
    name: "Index",
    component: Index,
    meta: { requiresAuth: true },
  },
  {
    path: "/create",
    name: "Create",
    component: Create,
    meta: { requiresAuth: true },
  },
  {
    path: "/signin",
    name: "Signin",
    component: Signin,
  },
];

const router = new VueRouter({
  mode: "history",
  base: process.env.BASE_URL,
  routes,
});

router.beforeEach((to, from, next) => {
  if (
    to.matched.some((record) => record.meta.requiresAuth) &&
    !Store.state.user.token
  ) {
    next({ path: "/signin", query: { redirect: to.fullPath } });
  } else {
    next();
  }
});

export default router;