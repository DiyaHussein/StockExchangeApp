<template>
  <div>
    <h1>Register</h1>
    <form @submit.prevent="registerUser">
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
    registerUser() {
      const newUser = {
        name: this.username,
        password: this.password,
        balance: this.balance || 0, // Initialize balance to 0
        stocks: {}, // Initialize stocks as an empty list
      };

      axios.post('http://localhost:8080/api/users', newUser)
          .then(() => {
            this.$router.push('/login'); // Redirect after successful registration
          })
          .catch(err => {
            console.error('Error creating user:', err.response?.data || err.message);
            this.errorMessage = 'Failed to create user. Please try again.';
          });
    },
  },
};
</script>