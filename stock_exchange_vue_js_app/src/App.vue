<template>
  <div id="app">
    <router-view />
    <LoginComponent v-if="showLogin" @register="toggleView" @logged-in="handleLogin" />
    <RegisterComponent v-else-if="showRegister" @login="toggleView" />
    <DashboardComponent v-else :user="currentUser" />
  </div>
</template>

<script>
import LoginComponent from './components/LoginComponent.vue';
import RegisterComponent from './components/RegisterComponent.vue';
import DashboardComponent from './components/DashboardComponent.vue';

export default {
  components: {
    LoginComponent,
    RegisterComponent,
    DashboardComponent,
  },
  data() {
    return {
      showLogin: true,
      showRegister: false,
      currentUser: null, // Store logged-in user data
    };
  },
  methods: {
    toggleView() {
      this.showLogin = !this.showLogin;
      this.showRegister = !this.showRegister;
    },
    handleLogin(user) {
      console.log("Logged in as:", user);
      this.currentUser = user; // Save user data
      this.showLogin = false;
      this.showRegister = false;
    },
  },
};
</script>
