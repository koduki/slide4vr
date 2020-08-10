import Vue from "vue";
import Vuex from "vuex";
import createPersistedState from "vuex-persistedstate";

import user from "@/store/modules/user";

Vue.use(Vuex);

const store = new Vuex.Store({
  modules: {
    user,
  },
  plugins: [createPersistedState({ storage: window.sessionStorage })],
});

export default store;
