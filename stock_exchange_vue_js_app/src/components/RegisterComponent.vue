<template>
  <div>
    <h1>Register</h1>
    <form @submit.prevent="register">
      <input v-model="username" placeholder="Username/Email" />
      <input v-model="fullName" placeholder="Full Name" />
      <input v-model="balance" placeholder="Balance" type="number" />
      <input v-model="password" placeholder="Password" type="password" />
      <button type="submit">Register</button>
    </form>
    <button @click="$emit('login')">Back to Login</button>
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      username: "",
      fullName: "",
      balance: 0,
      password: "",
    };
  },
  methods: {
    async register() {
      try {
        const response = await axios.post("http://localhost:8080/api/users", {
          name: this.username,
          balance: this.balance,
          password: this.password,
        });
        alert("Registration successful! Please log in.");
        console.log(response.data); // Handle successful registration response
        this.$emit("login"); // Switch back to login view
      } catch (error) {
        alert("Registration failed. Please try again.");
        console.error(error);
      }
    },
  },
};
</script>