import Vue from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios';
import VueAxios from 'vue-axios';
import VueSweetalert2 from 'vue-sweetalert2';
import firebase from 'firebase';
import '../node_modules/bootstrap/dist/css/bootstrap.min.css';
import '../node_modules/sweetalert2/dist/sweetalert2.min.css';

Vue.config.productionTip = false;
Vue.use(VueSweetalert2);
Vue.use(VueAxios, axios);

var config = {
  apiKey: process.env.VUE_APP_AUTH_API_KEY,
  authDomain: process.env.VUE_APP_AUTH_API_DOMAIN,
};
console.log(config)
firebase.initializeApp(config);

new Vue({
  router,
  render: h => h(App)
}).$mount('#app')