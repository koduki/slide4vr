<template>
  <div id="overlay" v-show="visible">
    <div id="content">
      <p>「C:\Users\$USER\Documents\My Games\VirtualCast\config.json」に貼り付けてください。</p>
      <hr />
      <div>
        <pre>{{jsonTemplate }}</pre>
        <button @click="close">close</button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "WhiteBoardModal",
  props: {
    slideKey: String,
  },
  data() {
    return {
      visible: false,
      jsonTemplate: "",
    };
  },
  methods: {
    show: function () {
      this.visible = true;
    },
    close: function () {
      this.visible = false;
    },
    loadJsonTemplate: function (key) {
      const uri = process.env.VUE_APP_API_BASE_URL + "/slide/" + key;
      const config = {
        headers: {
          Authorization: "Bearer " + this.$store.state.user.token,
        },
      };
      this.axios.get(uri, config).then((response) => {
        this.jsonTemplate = JSON.stringify(response.data, null, "  ")
          .slice(1, -2)
          .trim();
      });
    },
  },
  watch: {
    slideKey: function (key) {
      this.loadJsonTemplate(key);
    },
  },
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
#overlay {
  /*　要素を重ねた時の順番　*/
  z-index: 1;

  /*　画面全体を覆う設定　*/
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);

  /*　画面の中央に要素を表示させる設定　*/
  display: flex;
  align-items: center;
  justify-content: center;
}
#content {
  z-index: 2;
  width: 80%;
  padding: 1em;
  background: #fff;
}
</style>