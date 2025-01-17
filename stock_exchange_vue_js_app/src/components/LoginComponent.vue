<template>
  <div>
    <h1>Login</h1>
    <form @submit.prevent="login">
      <input v-model="username" placeholder="Username" />
      <input v-model="password" placeholder="Password" type="password" />
      <button type="submit">Login</button>
    </form>
    <button @click="navigateToRegister">Set up account</button>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      username: "",
      password: "",
    };
  },
  methods: {
    login() {
      axios.post('http://localhost:8080/api/users/login', {
        username: this.username,
        password: this.password,
      })
          .then(response => {
            const user = response.data;
            localStorage.setItem('userId', user.id); // Save user ID
            this.$router.push('/dashboard'); // Redirect to the dashboard
          })
          .catch(err => {
            if (err.response && err.response.status === 401) {
              this.errorMessage = 'Invalid username or password';
            } else {
              console.error('Login failed:', err);
              this.errorMessage = 'An error occurred. Please try again.';
            }
          });
    },
    navigateToRegister() {
      this.$router.push("/register");
    },
  }
};
</script>