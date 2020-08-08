<template>
  <div class="top">
    <h1>バーチャルキャストでプレゼンをしよう！</h1>
    <p>
      VRの世界でプレゼンをしてみたいと思いませんか？ VR上でなら好きなアバターで配信をすることが出来ます。
      <br />バーチャルキャストにはプレゼンができるように「
      <a
        href="https://virtualcast.jp/wiki/doku.php?id=virtualcast:item:whiteboard"
      >ホワイトボード</a>」というアイテムがあります。これを使って画像を切り替えてプレゼンをすることが出来ます！
      <br />ホワイトボードに表示する画像はどこかインターネット上の公開サーバに置いてやる必要があります。Google Driveなどで頑張る事も出来ますが少し面倒です。
      <br />Slide4VRはパワーポイントのPPTXファイルをアップロードすると自動で画像に変換してリンクをバーチャルキャストのJSON形式で返すことが出来るサービスです。
      <br />Slide4VRを使ってどんどんプレゼンをしていきましょうー。
    </p>

    <h2>SNS認証でログインする</h2>
    <button @click="signInWithGoogle">Googleで認証</button>
    <button @click="signInWithTwitter">Twitterで認証</button>
  </div>
</template>

<style scoped>
.top {
  text-align: center;
}
</style>

<script>
import Auth from "@/modules/auth";
export default {
  name: "Login",
  data() {
    return {};
  },
  methods: {
    signInWithGoogle: function () {
      const self = this;
      Auth.loginWithGoogle(() => self.$router.push("home"));
    },
    signInWithTwitter: function () {
      const self = this;
      Auth.loginWithTwitter(() => self.$router.push("home"));
    },
  },
};
</script>