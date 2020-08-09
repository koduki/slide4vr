import firebase from "firebase/app";
import "firebase/auth";
import store from "@/store";

export default {
  init() {
    const config = {
      apiKey: process.env.VUE_APP_AUTH_API_KEY,
      authDomain: process.env.VUE_APP_AUTH_API_DOMAIN,
    };
    firebase.initializeApp(config);
  },
  loginWithTwitter(callback) {
    this.login(new firebase.auth.TwitterAuthProvider(), callback);
  },
  loginWithGoogle(callback) {
    this.login(new firebase.auth.GoogleAuthProvider(), callback);
  },
  login(provider, callback) {
    firebase
      .auth()
      .signInWithPopup(provider)
      .then((res) => {
        res.user
          .getIdToken()
          .then(function(token) {
            store.dispatch("user/store", {
              id: res.user.uid,
              token: token,
              name: res.user.displayName,
              pic: res.user.photoURL,
            });
            callback();
          })
          .catch((error) => {
            console.log(error);
            this.errorMessage = error.message;
            this.showError = true;
          });
      });
  },
};
