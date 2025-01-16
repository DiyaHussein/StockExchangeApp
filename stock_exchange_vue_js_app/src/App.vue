<template>
  <div id="app">
    <LoginComponent v-if="showLogin" @register="toggleView" @logged-in="handleLogin" />
    <RegisterComponent v-else-if="showRegister" @login="toggleView" />
    <DashboardComponent v-else />
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
    };
  },
  methods: {
    toggleView() {
      this.showLogin = !this.showLogin;
      this.showRegister = !this.showRegister;
    },
    handleLogin(user) {
      console.log("Logged in as:", user);
      this.showLogin = false;
      this.showRegister = false;
    },
  },
};
</script>
