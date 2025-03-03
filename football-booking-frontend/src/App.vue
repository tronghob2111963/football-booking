<template>
  <v-app>
    <v-app-bar app color="primary" dark>
      <v-toolbar-title>Football Booking</v-toolbar-title>
      <v-spacer></v-spacer>
      <v-btn v-if="isAuthenticated" @click="logout" color="error">Logout</v-btn>
    </v-app-bar>

    <v-main>
      <router-view></router-view>
    </v-main>
  </v-app>
</template>

<script>
export default {
  computed: {
    isAuthenticated() {
      return !!localStorage.getItem('token');
    },
  },
  methods: {
    logout() {
      localStorage.removeItem('token');
      localStorage.removeItem('role');
      this.$router.push('/login');
    },
  },
};
</script>