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
    register() {
      // Logic for registration (e.g., axios request to backend)
    },
  },
};
</script>
