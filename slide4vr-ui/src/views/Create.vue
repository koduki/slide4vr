<template>
  <div class="container">
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
            <label>ファイル(pptx):</label>
            <div class="custom-file">
              <input type="file" class="custom-file-input" id="inputFile" @change="onDrop" />
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
export default {
  data() {
    return {
      item: {
        title: "",
        files: [],
      },
      filepath: "ここにスライドをアップロード",
      message: "",
    };
  },
  methods: {
    postSlide() {
      const uri = process.env.VUE_APP_API_BASE_URL + "/slide";
      const data = new FormData();

      data.append("title", this.item.title);
      data.append("slide", this.item.files[0]);

      this.axios
        .post(uri, data)
        .then(() => {
          this.$swal({
            icon: "success",
            text: "Upload Success!",
          });
          //   this.$router.push({ name: "Index" });
        })
        .catch((error) => {
          this.message = `status: ${error.response.status}, message: ${error.response.data}`;
        });
    },
    onDrop: function (event) {
      this.item.files = event.target.files;
      this.filepath = this.item.files[0].name;
    },
  },
};
</script>