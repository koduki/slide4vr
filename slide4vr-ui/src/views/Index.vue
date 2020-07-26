<template>
  <div class="container">
    <div class="card">
      <div class="card-header">
        <h2>スライド一覧</h2>
      </div>
      <div class="container">
        <table class="table">
          <tr>
            <th>アップロード日</th>
            <th>タイトル</th>
            <th>　</th>
          </tr>
          <tr v-for="item in slides" :key="item._id">
            <td>{{ item.created_at | moment }}</td>
            <td>{{ item.title }}</td>
            <td>
              <input type="hidden" :value="item.key" />
              <button @click="openModal">表示する</button>
            </td>
          </tr>
        </table>
      </div>
    </div>
    <whiteboard-modal :slide-key="slideKey" ref="modal"></whiteboard-modal>
  </div>
</template>

<script>
// import moment from "moment";
import WhiteboardModal from "../components/WhiteboardModal.vue";

export default {
  components: {
    WhiteboardModal,
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
      isModalVisible: false,
    };
  },
  created: function () {
    this.fetchSlide();
  },
  methods: {
    fetchSlide() {
      const uri = process.env.VUE_APP_API_BASE_URL + "/slide";
      this.axios.get(uri).then((response) => {
        this.slides = response.data;
      });
    },
    openModal: function (event) {
      this.slideKey = event.target.parentElement.getElementsByTagName("input")[0].value
      this.$refs.modal.show();
    },
  },
};
</script>
