<template>
  <v-container>
    <v-row>
      <v-col v-for="(field, index) in fields" :key="field?.id || index" cols="12" sm="6" md="4">
        <v-card class="field-card" elevation="2">
          <v-img :src="field?.image || field?.imageUrl" height="200px" v-if="field?.image || field?.imageUrl"></v-img>
          <v-card-title>{{ field?.name || 'N/A' }}</v-card-title>
          <v-card-subtitle>{{ field?.address || 'N/A' }}</v-card-subtitle>
          <v-card-text>
            <p class="price-text">Price: {{ formatPrice(field?.price_per_hour || field?.price_per_hour || 0) }} VND/hour</p>
            <p>Status: {{ field?.status || 'N/A' }}</p>
            <v-btn @click="$emit('book', field)" color="primary" small>Book</v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  props: {
    fields: {
      type: Array,
      default: () => [],
    },
  },
  methods: {
    formatPrice(value) {
      if (!value) return '0';
      // Chuyển thành số và định dạng với dấu phân cách hàng nghìn
      const numberValue = Number(value);
      return numberValue.toLocaleString('vi-VN');
    },
  },
};
</script>

<style scoped>
.field-card {
  transition: all 0.3s ease;
}

.field-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
}

.price-text {
  font-weight: bold;
  color: #2c3e50;
  margin-bottom: 8px;
}
</style>