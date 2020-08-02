<template>
  <div class="signin">
    <h1>サインイン</h1>
    <button @click="signInWithGoogle">Googleで認証</button>
    <button @click="signInWithTwitter">Twitterで認証</button>
    <div v-show="user.id">
      <p>ID: {{user.id}}</p>
      <p>
        名前:
        <img :src="user.pic" width="24px" height="24px" />
        {{user.name}}
      </p>
      <p>有効期限: {{user.expiration_time}}</p>
    </div>
  </div>
</template>

<script>
import firebase from "firebase";

export default {
  name: "Signin",
  data() {
    return {
      user: {
        id: "",
        name: "",
        expiration_time: "",
        pic: "",
      },
    };
  },
  methods: {
    signInWithGoogle: function () {
      this.signIn(new firebase.auth.GoogleAuthProvider());
    },
    signInWithTwitter: function () {
      this.signIn(new firebase.auth.TwitterAuthProvider());
    },
    signIn: function (provider) {
      const callApi = (token) => {
        const url = "http://localhost:8080/secured";
        const config = {
          headers: {
            Authorization: "Bearer " + token,
          },
        };
        this.axios.get(url, config).then((response) => {
          console.log(response.data);
          window.u = response.data;
          this.user.id = response.data.id;
          this.user.name = response.data.name;
          this.user.expiration_time = new Date(
            response.data.expiration_time * 1000
          );
          this.user.pic = response.data.picture;
        });
      };

      firebase
        .auth()
        .signInWithPopup(provider)
        .then((res) => {
          res.user
            .getIdToken()
            .then(callApi)
            .catch((error) => {
              console.log(error);
              this.errorMessage = error.message;
              this.showError = true;
            });
        });
    },
  },
};
</script>