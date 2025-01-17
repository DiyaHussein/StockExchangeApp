<template>
  <div>
    <h1>Login</h1>
    <form @submit.prevent="login">
      <input v-model="username" placeholder="Username" />
      <input v-model="password" placeholder="Password" type="password" />
      <button type="submit">Login</button>
    </form>
    <button @click="$emit('register')">Set up an account</button>
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
    async login() {
      try {
        const response = await axios.post("http://localhost:8080/api/users/login", {
          username: this.username,
          password: this.password,
        });
        alert(`Welcome, ${response.data.name}`);
        console.log(response.data); // Handle successful login response (e.g., save token or user details)
      } catch (error) {
        alert("Invalid username or password. Please try again.");
        console.error(error);
      }
    },
  },
};
</script>