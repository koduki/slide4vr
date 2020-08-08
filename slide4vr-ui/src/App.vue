<template>
  <div id="app">
    <div
      class="d-flex flex-column flex-md-row align-items-center p-3 px-md-4 mb-3 bg-white border-bottom shadow-sm"
    >
      <h5 class="my-0 mr-md-auto font-weight-normal">
        <router-link class="title-logo" to="home">Slide4VR</router-link>
      </h5>
      <div v-show="$store.state.user.id">
        <router-link  class="btn btn-outline-primary" to="create">スライドを登録</router-link>
        <a class="btn" href="#">
          <img class=".avatar" :src="$store.state.user.pic" width="32px" height="32px" alt />
        </a>
      </div>
    </div>
    <router-view />
  </div>
</template>

<style>
.avatar {
  border-radius: 100%;
  overflow: hidden;
  display: inline-block;
}

.title-logo a {
  pointer-events: none;
  cursor: default;
  text-decoration: none;
  color: #000000;
}
</style>
