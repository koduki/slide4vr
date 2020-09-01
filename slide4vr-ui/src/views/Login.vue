<template>
  <div>
    <section class="pt-4 pt-md-11">
      <!-- Main -->
      <div class="container">
        <div class="row align-items-center">
          <div class="col-12 col-md-5 col-lg-5 order-md-2">
            <!-- Image -->
            <img
              src="/slide4vr.svg"
              class="img-fluid mw-md-150 mw-lg-130 mb-6 mb-md-0 aos-init aos-animate"
              alt="..."
              data-aos="fade-up"
              data-aos-delay="100"
            />
          </div>
          <div class="col-12 col-md-8 col-lg-7 order-md-1 aos-init aos-animate" data-aos="fade-up">
            <!-- Heading -->
            <h1 class="display-4 text-center text-md-left">
              バーチャルキャストで
              <br />
              <span class="text-primary">プレゼン</span>をしよう！
            </h1>

            <!-- Text -->
            <div class="lead text-center text-md-left text-muted mb-6 mb-lg-8">
              <p>
                VRの世界でプレゼンをしてみたいと思いませんか？
                <a
                  href="https://virtualcast.jp/"
                  target="_blank"
                >VirtualCast</a>を使えば好きなアバターでスライドを使って本当に発表をしているかのように配信をすることが出来ます。
              </p>
              <p>「やってみたいけど準備が大変そう。。。」Slide4VRはそんなあなたを支援します。</p>
            </div>
          </div>
        </div>
        <!-- / .row -->
      </div>
      <!-- / .container -->
    </section>

    <!-- Login -->
    <section class="py-8 py-md-11 border-bottom login">
      <div class="container">
        <div class="row">
          <div class="col-12">
            <!-- Buttons -->
            <div class="text-center text-md-left">
              <h2>SNS認証でログインする</h2>

              <button type="button" class="btn" @click="signInWithGoogle">
                <img src="/btn_google_signin_dark_normal_web@2x.png" alt="Googleでログイン" />
              </button>
              <button type="button" class="btn" @click="signInWithTwitter">
                <img src="/Twitter.png" alt="Twitterでログイン" />
              </button>
            </div>
          </div>
        </div>
        <!-- / .row -->
      </div>
      <!-- / .container -->
    </section>

    <!-- Features -->
    <section class="py-8 py-md-11 border-bottom feature">
      <div class="container">
        <div class="row">
          <div class="col-12 col-md-4 aos-init aos-animate" data-aos="fade-up">
            <!-- Icon -->
            <div class="icon text-primary mb-3">
              <img src="/powerpoint.jpg" width="24px" />
              <img src="/icon_1r_24.png" width="24px" />
            </div>

            <!-- Heading -->
            <h3>PDFやPowerPointファイルをアップロードするだけ</h3>

            <!-- Text -->
            <p class="text-muted mb-6 mb-md-0">
              バーチャルキャストのプレゼンに使う「
              <a
                href="https://virtualcast.jp/wiki/doku.php?id=virtualcast:item:whiteboard"
                target="_blank"
              >ホワイトボード</a>」はインターネット上の「誰でもアクセスできる状態」場所に置いた画像を表示する事が出来ます。
              <br />Slide4VRではアップロードしたPDFまたはPPTXファイルを自動で画像に変換してWebからアクセス出来る場所に配置します。自分でサーバを用意する必要はありません。
            </p>
          </div>
          <div class="col-12 col-md-4 aos-init aos-animate" data-aos="fade-up" data-aos-delay="50">
            <!-- Icon -->
            <div class="icon text-primary mb-3">
              <img src="whiteboard.svg" height="24" />
            </div>

            <!-- Heading -->
            <h3>ホワイトボード用のJSONを自動生成</h3>

            <!-- Text -->
            <p class="text-muted mb-6 mb-md-0">
              ホワイトボード用のURLをVirtualCastの設定画面から一つ一つ指定するのは大変です。Slide4VRではPDFまたはPPTXから変換した画像のURLを自動で
              <a
                href="https://virtualcast.jp/wiki/doku.php?id=virtualcast:config#whiteboard"
                target="_blank"
              >config.json形式</a>に変換して表示します。設定ファイルにコピペするだけでOKなので面倒な作業を大幅に短縮できます。
            </p>
          </div>
          <div class="col-12 col-md-4 aos-init aos-animate" data-aos="fade-up" data-aos-delay="100">
            <!-- Icon -->
            <div class="icon text-primary mb-3">
              <img src="/btn_google_light_normal_ios.svg" width="24px" />
              <img src="/Twitter_Social_Icon_Circle_Color.svg" width="24px" />
            </div>

            <!-- Heading -->
            <h3>いつものSNSアカウントでログインできる</h3>

            <!-- Text -->
            <p
              class="text-muted mb-0"
            >Slide4VRではセキュリティと利便性の観点から独自の認証機能は持たずにSNS認証のみをサポートします。既にもっているGoogleやTwitterと言ったアカウントを使って手間いらずかつ安心してご利用いただけます。</p>
          </div>
        </div>
        <!-- / .row -->
      </div>
      <!-- / .container -->
    </section>
    <footer role="contentinfo" class="py-6 lh-1 bg-white">
      <div class="container">
        <div class="row">
          <div class="col-12 text-center text-sm">
            <p class="mb-0">Powered by なからぼ</p>
          </div>
        </div>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.login {
  margin: 50px;
}
.feature {
  padding-bottom: 50px;
}
footer {
  margin: 50px;
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