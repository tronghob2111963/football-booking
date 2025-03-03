import Vue from 'vue';
import App from './App.vue';
import router from './router';
import Vuetify from 'vuetify';
import 'vuetify/dist/vuetify.min.css';
import axios from './axios';

Vue.config.productionTip = false;
Vue.use(Vuetify);

const vuetify = new Vuetify(); // Khởi tạo Vuetify

new Vue({
  router,
  vuetify,
  render: h => h(App),
}).$mount('#app');

Vue.prototype.$axios = axios; // Đăng ký Axios toàn cục