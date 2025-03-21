<template>
  <v-form @submit.prevent="$emit('saved', { id: 1, name: 'Sample Item', price: 10000, stock: 10 })">
    <v-text-field label="Name" value="Sample Item" readonly></v-text-field>
    <v-text-field label="Price" value="10000" readonly></v-text-field>
    <v-text-field label="Stock" value="10" readonly></v-text-field>
    <v-btn type="submit">Save</v-btn>
    <v-btn @click="$emit('cancel')">Cancel</v-btn>
  </v-form>
</template>

<script>
export default {};
</script>