<template>
  <v-container>
    <v-row>
      <v-col v-for="field in fields" :key="field.id" cols="12" sm="6" md="4">
        <v-card>
          <v-img :src="field.image" height="200px" v-if="field.image"></v-img>
          <v-card-title>{{ field.name }}</v-card-title>
          <v-card-subtitle>{{ field.address }}</v-card-subtitle>
          <v-card-text>
            <p>Price: {{ field.price_per_hour }} VND/hour</p>
            <p>Status: {{ field.status }}</p>
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
};
</script>