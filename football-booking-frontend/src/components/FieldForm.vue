<template>
  <v-form @submit.prevent="submitField">
    <v-text-field v-model="field.name" label="Name" required></v-text-field>
    <v-text-field v-model="field.address" label="Address"></v-text-field>
    <v-text-field v-model="field.pricePerHour" label="Price per Hour" type="number" required></v-text-field>
    <v-select
      v-model="field.status"
      :items="['AVAILABLE', 'UNAVAILABLE']"
      label="Status"
      required
    ></v-select>
    <v-btn color="primary" type="submit" :disabled="loading" :loading="loading">Save</v-btn>
    <v-btn color="secondary" @click="$emit('cancel')">Cancel</v-btn>
    <p v-if="error" style="color: red">{{ error }}</p>
  </v-form>
</template>

<script>
export default {
  data() {
    return {
      field: {
        name: '',
        address: '',
        pricePerHour: 0,
        status: 'AVAILABLE',
      },
      error: '',
      loading: false,
    };
  },
  methods: {
    async submitField() {
      this.loading = true;
      this.error = '';
      try {
        // Đảm bảo dữ liệu gửi đi khớp với entity Field
        const fieldData = {
          name: this.field.name,
          address: this.field.address || '',
          pricePerHour: Number(this.field.pricePerHour), // Đảm bảo là số
          status: this.field.status,
          imageUrl: null, // Backend sẽ set nếu có image
        };

        const response = await this.$axios.post('/api/field/create-field', fieldData, {
          headers: { 'Content-Type': 'application/json' },
        });

        console.log('Created field:', response.data);
        this.$emit('saved', response.data.data);
      } catch (err) {
        // Xử lý lỗi chi tiết hơn
        if (err.response) {
          this.error = err.response.data?.message || 'Không thể thêm sân! Vui lòng kiểm tra dữ liệu.';
        } else {
          this.error = 'Có lỗi xảy ra khi kết nối với server!';
        }
        console.error('Error creating field:', err.response ? err.response.data : err.message);
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>