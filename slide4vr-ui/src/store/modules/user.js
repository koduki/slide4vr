export default {
  namespaced: true,
  state: {
    id: "",
    name: "",
    token: "",
    pic: "",
  },
  mutations: {
    store(state, user) {
      state.id = user.id;
      state.token = user.token;
      state.name = user.name;
      state.pic = user.pic;
    },
    drop(state) {
      state.id = state.token = state.name = state.pic = "";
    },
  },
  actions: {
    store(context, user) {
      context.commit("store", user);
    },
    drop(context) {
      context.commit("drop");
    },
  },
};
