import Vue from "vue";
import App from "./App.vue";
import router from "./router";
import axios from "axios";
import VueAxios from "vue-axios";
import VueSweetalert2 from "vue-sweetalert2";
import store from "@/store";
import Auth from "@/modules/auth";
import "../node_modules/bootstrap/dist/css/bootstrap.min.css";
import "../node_modules/@fortawesome/fontawesome-free/css/fontawesome.min.css";
import "../node_modules/sweetalert2/dist/sweetalert2.min.css";

Vue.config.productionTip = false;
Vue.use(VueSweetalert2);
Vue.use(VueAxios, axios);

Auth.init();

new Vue({
  router,
  render: (h) => h(App),
  store,
}).$mount("#app");
