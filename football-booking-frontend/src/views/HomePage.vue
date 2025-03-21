<template>
  <v-container>
    <h1>Football Field Booking</h1>
    <v-snackbar v-model="successSnackbar" color="success" timeout="3000">
      Thêm sân thành công!
    </v-snackbar>
    <p v-if="error" style="color: red">{{ error }}</p>

    <h2>Available Fields</h2>
    <v-btn v-if="isAdmin" color="primary" @click="openFieldForm">Add New Field</v-btn>
    <field-list :fields="fields" @book="handleBook" />

    <h2>Items</h2>
    <v-btn color="primary" @click="openItemForm">Add New Item</v-btn>
    <v-table>
      <thead>
        <tr>
          <th>Image</th>
          <th>Name</th>
          <th>Price</th>
          <th>Stock</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="item in items" :key="item.id">
          <td>
            <img :src="item.imageUrl" alt="Item Image" width="50" height="50" v-if="item.imageUrl" />
            <span v-else>No Image</span>
          </td>
          <td>{{ item.name }}</td>
          <td>{{ item.price }}</td>
          <td>{{ item.stock }}</td>
        </tr>
      </tbody>
    </v-table>

    <v-dialog v-model="showFieldForm" max-width="600">
      <v-card>
        <v-card-title>Add New Field</v-card-title>
        <v-card-text>
          <field-form @saved="fieldSaved" @cancel="showFieldForm = false" />
        </v-card-text>
      </v-card>
    </v-dialog>

    <v-dialog v-model="showItemForm" max-width="600">
      <v-card>
        <v-card-title>Add New Item</v-card-title>
        <v-card-text>
          <item-form @saved="itemSaved" @cancel="showItemForm = false" />
        </v-card-text>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import FieldList from '../components/FieldList.vue';
import FieldForm from '../components/FieldForm.vue';
import ItemForm from '../components/ItemForm.vue';

export default {
  components: { FieldList, FieldForm, ItemForm },
  data() {
    return {
      fields: [],
      items: [],
      error: '',
      successSnackbar: false,
      showFieldForm: false,
      showItemForm: false,
    };
  },
  computed: {
    isAdmin() {
      return localStorage.getItem('role') === 'ADMIN';
    },
  },
  mounted() {
    this.fetchFields();
    this.fetchItems();
  },
  methods: {
    async fetchFields() {
      try {
        const response = await this.$axios.get('/api/field/list-field', {
          params: { pageNo: 1, pageSize: 20 },
        });
        console.log('Fetched fields:', response.data);
        this.fields = response.data.data?.items || [];
      } catch (err) {
        this.error = err.response?.data?.message || 'Không thể tải danh sách sân!';
      }
    },
    async fetchItems() {
      try {
        const response = await this.$axios.get('/api/items', {
          params: { pageNo: 1, pageSize: 20 },
        });
        this.items = response.data.data || [];
      } catch (err) {
        this.error = err.response?.data?.message || 'Không thể tải danh sách vật phẩm!';
      }
    },
    async handleBook(bookingData) {
      try {
        const response = await this.$axios.post('/api/bookings', bookingData);
        this.error = '';
        console.log('Booking successful:', response.data);
      } catch (err) {
        this.error = err.response?.data?.message || 'Có lỗi khi đặt sân!';
      }
    },
    openFieldForm() {
      this.showFieldForm = true;
    },
    openItemForm() {
      this.showItemForm = true;
    },
    async fieldSaved(field) {
      console.log('Field saved:', field);
      if (field && field.id) {
        this.successSnackbar = true;
        this.showFieldForm = false;
        // Làm mới danh sách sân
        await this.fetchFields();
      } else {
        console.error('Invalid field data:', field);
        this.error = 'Không thể thêm sân do dữ liệu không hợp lệ!';
      }
    },
    itemSaved(item) {
      this.items.push(item);
      this.showItemForm = false;
    },
  },
};
</script>