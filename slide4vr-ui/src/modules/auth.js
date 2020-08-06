import firebase from "firebase";
import axios from "axios";
import store from "@/store";

export default {
  init() {
    const config = {
      apiKey: process.env.VUE_APP_AUTH_API_KEY,
      authDomain: process.env.VUE_APP_AUTH_API_DOMAIN,
    };
    firebase.initializeApp(config);
  },
  loginWithTwitter() {
    this.login(new firebase.auth.TwitterAuthProvider());
  },
  loginWithGoogle() {
    this.login(new firebase.auth.GoogleAuthProvider());
  },
  login(provider) {
    const callApi = (token) => {
      const url = "http://localhost:8080/secured";
      const config = {
        headers: {
          Authorization: "Bearer " + token,
        },
      };
      axios.get(url, config).then((response) => {
        store.dispatch("user/store", {
          id: response.data.id,
          token: token,
          name: response.data.name,
          expiration_time: new Date(response.data.expiration_time * 1000),
          pic: response.data.picture,
        });
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
};
