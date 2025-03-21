<template>
  <v-container class="login-container">
    <v-row justify="center">
      <v-col cols="12" sm="6">
        <v-card class="login-card" elevation="5">
          <v-card-title class="primary white--text">Login</v-card-title>
          <v-card-text>
            <v-form @submit.prevent="login">
              <v-text-field
                v-model="username"
                label="Username"
                required
                prepend-icon="mdi-account"
              ></v-text-field>
              <v-text-field
                v-model="password"
                label="Password"
                type="password"
                required
                prepend-icon="mdi-lock"
              ></v-text-field>
              <v-btn
                color="primary"
                type="submit"
                :disabled="loading"
                :loading="loading"
                block
              >
                Login
              </v-btn>
              <p v-if="error" class="error-text">{{ error }}</p>
            </v-form>
            <p class="text-center mt-3">
              Don't have an account? <router-link to="/register">Register here</router-link>
            </p>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import axios from 'axios';

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
        const url = '/api/auth/login'; // URL được gửi
        console.log('Sending login request to:', url);
        console.log('Request data:', { username: this.username, password: this.password });
        const response = await axios.post(url, {
          username: this.username,
          password: this.password,
        }, {
          headers: { 'Content-Type': 'application/json' }
        });
        console.log('Login response:', response.data);
        const { token, role } = response.data.data;
        localStorage.setItem('token', token);
        localStorage.setItem('role', role.replace('ROLE_', ''));
        this.$router.push('/');
      } catch (err) {
        if (err.response && typeof err.response.data === 'string' && err.response.data.includes('<html')) {
          this.error = `Server returned an unexpected HTML response (Status: ${err.response.status}). Check backend URL or proxy.`;
        } else {
          this.error = err.response?.data?.message || err.message || 'Đăng nhập thất bại!';
        }
        console.error('Login error:', err.response ? err.response.data : err.message);
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-card {
  padding: 20px;
  border-radius: 10px;
}

.error-text {
  color: red;
  text-align: center;
  margin-top: 10px;
}
</style>