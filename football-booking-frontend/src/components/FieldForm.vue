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
    <v-file-input
      v-model="fieldImage"
      label="Upload Image"
      accept="image/*"
      @change="onFileChange"
    ></v-file-input>
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
        imageUrl: '',
      },
      fieldImage: null,
      error: '',
      loading: false,
    };
  },
  methods: {
    onFileChange(file) {
      this.fieldImage = file;
    },
    async submitField() {
      this.loading = true;
      this.error = '';
      try {
        const formData = new FormData();
        formData.append('name', this.field.name);
        formData.append('address', this.field.address || '');
        formData.append('pricePerHour', this.field.pricePerHour);
        formData.append('status', this.field.status);
        if (this.fieldImage) {
          formData.append('image', this.fieldImage);
        }

        const response = await this.$axios.post('/api/field/create-field', formData, {
          headers: { 'Content-Type': 'multipart/form-data' },
        });

        this.$emit('saved', response.data.data);
      } catch (err) {
        this.error = err.response?.data?.message || 'Không thể thêm sân!';
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>