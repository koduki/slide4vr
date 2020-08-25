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
        <h2>{{this.$store.state.user.name}} さんのスライド一覧</h2>
      </div>
      <div class="container">
        <table class="table">
          <tr>
            <th>アップロード日</th>
            <th>タイトル</th>
            <th>-</th>
          </tr>
          <tr v-for="item in slides" :key="item._id">
            <td>{{ item.created_at | moment }}</td>
            <td>
              <input type="hidden" :value="item.key" />
              <a
                v-if="item.is_uploaded==true"
                href="."
                @click.prevent.stop="onClickShow"
              >{{ item.title }}</a>
              <span v-if="item.is_uploaded==false">{{ item.title }}</span>
            </td>
            <td>
              <span v-if="item.is_uploaded==true">
                <button type="button" class="btn btn-success">
                  <svg
                    width="1em"
                    height="1em"
                    viewBox="0 0 16 16"
                    class="bi bi-check"
                    fill="currentColor"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      fill-rule="evenodd"
                      d="M10.97 4.97a.75.75 0 0 1 1.071 1.05l-3.992 4.99a.75.75 0 0 1-1.08.02L4.324 8.384a.75.75 0 1 1 1.06-1.06l2.094 2.093 3.473-4.425a.236.236 0 0 1 .02-.022z"
                    />
                  </svg>
                </button>
              </span>
              <span v-else>
                <button class="btn btn-primary" type="button" disabled>
                  <span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
                </button>
              </span>
              <span style="margin-left:20px">
                <input type="hidden" :value="item.key" />
                <button class="btn btn-danger" @click="onClickDelete">
                  <svg
                    width="1em"
                    height="1em"
                    viewBox="0 0 16 16"
                    class="bi bi-trash-fill"
                    fill="currentColor"
                    xmlns="http://www.w3.org/2000/svg"
                  >
                    <path
                      fill-rule="evenodd"
                      d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5a.5.5 0 0 0-1 0v7a.5.5 0 0 0 1 0v-7z"
                    />
                  </svg>
                </button>
              </span>
            </td>
          </tr>
        </table>
      </div>
    </div>
    <whiteboard-modal :slide-key="slideKey" ref="modal"></whiteboard-modal>
  </div>
</template>

<script>
import WhiteboardModal from "@/components/WhiteboardModal.vue";
import Loading from "vue-loading-overlay";
import "vue-loading-overlay/dist/vue-loading.css";

export default {
  components: {
    WhiteboardModal,
    loading: Loading,
  },
  filters: {
    moment(date) {
      return date;
      // return moment(date).format("YYYY/MM/DD HH:mm");
    },
  },
  data() {
    return {
      slides: [],
      slideKey: "",
      isLoading: false,
      isModalVisible: false,
      fullPage: true,
      timer: "",
    };
  },
  created: function () {
    this.isLoading = true;
    this.fetchSlide();
    this.timer = setInterval(this.fetchSlide, 10 * 1000);
  },
  beforeDestroy() {
    clearInterval(this.timer);
  },
  methods: {
    fetchSlide() {
      const uri = process.env.VUE_APP_API_BASE_URL + "/slide";

      const config = {
        headers: {
          Authorization: "Bearer " + this.$store.state.user.token,
        },
      };
      this.axios.get(uri, config).then((response) => {
        this.slides = response.data;
        this.slides.sort((a, b) => (a.created_at < b.created_at ? 1 : -1));
        this.isLoading = false;
      });
    },
    onClickDelete: function (event) {
      const key = event.target.parentElement.getElementsByTagName("input")[0]
        .value;

      const uri = process.env.VUE_APP_API_BASE_URL + "/slide/" + key;
      this.isLoading = true;

      const config = {
        headers: {
          Authorization: "Bearer " + this.$store.state.user.token,
        },
      };
      this.axios.delete(uri, config).then((response) => {
        this.fetchSlide();
      });
    },
    onCancel: function () {
      console.log("User cancelled the loader.");
    },
    onClickShow: function (event) {
      this.slideKey = event.target.parentElement.getElementsByTagName(
        "input"
      )[0].value;
      this.$refs.modal.show();
    },
    cancelAutoUpdate() {
      clearInterval(this.timer);
    },
  },
};
</script>
