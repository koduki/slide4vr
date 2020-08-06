<template>
  <div class="signin">
    <h1>サインイン</h1>
    <button @click="signInWithGoogle">Googleで認証</button>
    <button @click="signInWithTwitter">Twitterで認証</button>
    <div v-show="$store.state.user.id">
      <p>ID: {{$store.state.user.id}}</p>
      <p>
        名前:
        <img :src="$store.state.user.pic" width="24px" height="24px" />
        {{$store.state.user.name}}
      </p>
      <p>有効期限: {{$store.state.user.expiration_time}}</p>
    </div>
  </div>
</template>

<script>
import Auth from "@/modules/auth";
export default {
  name: "Signin",
  data() {
    return {};
  },
  methods: {
    signInWithGoogle: function () {
      Auth.loginWithGoogle();
    },
    signInWithTwitter: function () {
      Auth.loginWithTwitter();
    },
  },
};
</script>