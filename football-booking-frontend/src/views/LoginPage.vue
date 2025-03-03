<template>
  <v-container>
    <v-row justify="center">
      <v-col cols="12" sm="6">
        <v-card>
          <v-card-title>Login</v-card-title>
          <v-card-text>
            <v-text-field v-model="username" label="Username" required></v-text-field>
            <v-text-field v-model="password" label="Password" type="password" required></v-text-field>
            <v-btn color="primary" @click="login" :disabled="loading" :loading="loading">Login</v-btn>
            <p v-if="error" style="color: red">{{ error }}</p>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
export default {
  data() {
    return {
      username: '',
      password: '',
      error: '',
      loading: false,
    };
  },
  methods: {
    async login() {
      this.loading = true;
      this.error = '';
      try {
        const response = await this.$axios.post('/api/auth/login', {
          username: this.username,
          password: this.password,
        });
        const { token, role } = response.data.data;
        localStorage.setItem('token', token);
        localStorage.setItem('role', role.replace('ROLE_', '')); // Lấy role (ADMIN hoặc USER)
        this.$router.push('/');
      } catch (err) {
        this.error = err.response?.data?.message || 'Đăng nhập thất bại!';
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>