<template>
  <div id="app">
    <LoginComponent v-if="currentView === 'login'" @register="switchToRegister" @logged-in="switchToDashboard" />
    <RegisterComponent v-else-if="currentView === 'register'" @login="switchToLogin" />
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
      currentView: 'login', // Tracks which view to display ('login', 'register', or 'dashboard')
    };
  },
  methods: {
    switchToLogin() {
      this.currentView = 'login';
    },
    switchToRegister() {
      this.currentView = 'register';
    },
    switchToDashboard(user) {
      console.log('Switching to dashboard...');
      this.currentView = 'dashboard';
    },
  },
};
</script>
