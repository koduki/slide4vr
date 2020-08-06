export default {
  namespaced: true,
  state: {
    id: "",
    name: "",
    token: "",
    pic: "",
    expiration_time: "",
  },
  mutations: {
    store(state, user) {
      state.id = user.id;
      state.token = user.token;
      state.name = user.name;
      state.pic = user.pic;
      state.expiration_time = user.expiration_time;
    },
    drop(state) {
      state.id = state.token = state.name = state.pic = state.expiration_time = "";
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
