<template>
  <div id="app">
    <div
      class="d-flex flex-column flex-md-row align-items-center p-3 px-md-4 mb-3 bg-white border-bottom shadow-sm"
    >
      <h5 class="my-0 mr-md-auto title-logo">
        <router-link to="home">
          <img src="/slide4vr.png" />
          <span class="banner">
            Slide
            <span class="banner-4">4</span>VR β 
          </span>
        </router-link>
      </h5>
      <div v-show="$store.state.user.id">
        <router-link class="btn btn-outline-primary" to="create">スライドを登録</router-link>
        <a class="btn" href="#">
          <img class=".avatar" :src="$store.state.user.pic" width="32px" height="32px" alt />
        </a>
      </div>
    </div>
    <router-view />
  </div>
</template>

<style>
@import url("https://fonts.googleapis.com/css2?family=Mitr:wght@600&display=swap");

.avatar {
  border-radius: 100%;
  overflow: hidden;
  display: inline-block;
}

.title-logo {
  height: 32px;
}
.title-logo img {
  margin-top: -10px;
  height: 100%;
}

.title-logo span.banner {
  margin-left: 0px;
  font-size: 150%;
  font-family: "Mitr", sans-serif;
}
.title-logo span.banner-4 {
  margin-left: -8px;
  color: rgb(42, 212, 255);
}

.title-logo a:link {
  color: #000000;
  text-decoration: none;
}
.title-logo a:visited {
  color: #000000;
  text-decoration: none;
}
.title-logo a:hover {
  color: #000000;
  text-decoration: none;
}
.title-logo a:active {
  color: #000000;
  text-decoration: none;
}
</style>
