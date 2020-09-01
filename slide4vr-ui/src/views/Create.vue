<template>
  <div class="container">
    <loading
      :active.sync="isLoading"
      :can-cancel="true"
      :on-cancel="onCancel"
      :is-full-page="fullPage"
    ></loading>
    <div class="card">
      <div class="card-header">
        <h2>スライドのアップロード</h2>
      </div>
      <div class="container">
        <form v-on:submit.prevent="postSlide">
          <div v-show="message" class="alert alert-danger">{{message}}</div>
          <div class="form-group">
            <label>スライドタイトル:</label>
            <input type="text" class="form-control" v-model="item.title" />
          </div>
          <div class="form-group">
            <label>ファイル(pdf, pptx):</label>
            <div class="custom-file">
              <input type="file" accept=".pptx, .pdf" class="custom-file-input" id="inputFile" @change="onDrop" />
              <label class="custom-file-label" for="inputFile">{{filepath}}</label>
            </div>
          </div>
          <div>
            <input type="submit" class="btn btn-primary" value="アップロード" />
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import Loading from "vue-loading-overlay";
import "vue-loading-overlay/dist/vue-loading.css";

export default {
  components: {
    loading: Loading,
  },
  data() {
    return {
      item: {
        title: "",
        files: [],
      },
      filepath: "ここにスライドをアップロード",
      message: "",
      isLoading: false,
      fullPage: true,
    };
  },
  methods: {
    postSlide() {
      const uri = process.env.VUE_APP_API_BASE_URL + "/slide";
      const data = new FormData();

      data.append("title", this.item.title);
      data.append("slide", this.item.files[0]);

      this.isLoading = true;

      const config = {
        headers: {
          Authorization: "Bearer " + this.$store.state.user.token,
        },
      };
      this.axios
        .post(uri, data, config)
        .then(() => {
          this.$swal({
            icon: "success",
            text: "Upload Success!",
          });
          this.isLoading = false;
          this.$router.push({ name: "Home" });
        })
        .catch((error) => {
          this.isLoading = false;
          this.message = `status: ${error.response.status}, message: ${error.response.data}`;
        });
    },
    onCancel: function () {
      console.log("User cancelled the loader.");
    },
    onDrop: function (event) {
      this.item.files = event.target.files;
      this.filepath = this.item.files[0].name;
    },
  },
};
</script>