<template>
  <div id="app">
    <LoginComponent v-if="showLogin" @register="toggleView" />
    <RegisterComponent v-else @login="toggleView" />
  </div>
</template>

<script>
import LoginComponent from './components/LoginComponent.vue';
import RegisterComponent from './components/RegisterComponent.vue';

export default {
  components: {
    LoginComponent,
    RegisterComponent,
  },
  data() {
    return {
      showLogin: true,
    };
  },
  methods: {
    toggleView() {
      this.showLogin = !this.showLogin;
    },
  },
};
</script>
