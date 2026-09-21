<template>
  <div id="app">
    <NavBar v-if="!isLoginPage" />
    <Loading />
    <div :class="['main-content', { 'no-navbar': isLoginPage }]">
      <router-view />
    </div>
  </div>
</template>

<script>
import NavBar from "@/components/NavBar.vue";
import Loading from '@/components/Loading.vue';

export default {
  name: 'App',
  components: {
    NavBar,
    Loading,
  },
  computed: {
    isLoginPage() {
      return this.$route.path === '/login';
    },
  },
};
</script>

<style>
:root {
  --primary: #43a047;
  --primary-light: #66bb6a;
  --primary-dark: #b33d5c;
  --secondary: #a5d6a7;
  --accent: #c8e6c9;
  --bg-start: #fffbfc;
  --bg-end: #faf0f2;
  --card: #ffffff;
  --text-primary: #1a1a2e;
  --text-secondary: #4a4a68;
  --text-muted: #8a8a9a;
  --border: rgba(67, 160, 71, 0.12);
  --border-light: rgba(67, 160, 71, 0.06);
  --shadow-sm: 0 2px 8px rgba(67, 160, 71, 0.08);
  --shadow-md: 0 4px 16px rgba(67, 160, 71, 0.1);
  --shadow-lg: 0 8px 32px rgba(67, 160, 71, 0.15);
  --shadow-xl: 0 12px 40px rgba(67, 160, 71, 0.18);
  --radius-sm: 8px;
  --radius-md: 12px;
  --radius-lg: 20px;
  --radius-xl: 28px;
  --radius-full: 9999px;
}

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html {
  scroll-behavior: smooth;
}

body {
  font-family: 'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  background: linear-gradient(135deg, var(--bg-start) 0%, var(--bg-end) 50%, #f5e6e8 100%);
  background-attachment: fixed;
  min-height: 100vh;
  color: var(--text-primary);
  line-height: 1.6;
  letter-spacing: 0.02em;
}

#app {
  min-height: 100vh;
}

.main-content {
  padding-top: 85px;
  min-height: calc(100vh - 85px);
}

.main-content.no-navbar {
  padding-top: 0;
  min-height: 100vh;
}

::-webkit-scrollbar {
  width: 6px;
}

::-webkit-scrollbar-track {
  background: rgba(67, 160, 71, 0.05);
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, var(--primary), var(--primary-light));
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--primary);
}

a {
  text-decoration: none;
  color: inherit;
}

button {
  cursor: pointer;
  border: none;
  outline: none;
}

input, textarea {
  outline: none;
}

.gradient-text {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-shadow {
  box-shadow: var(--shadow-md);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.card-shadow:hover {
  box-shadow: var(--shadow-lg);
}

.elegant-btn {
  position: relative;
  padding: 12px 32px;
  font-size: 15px;
  font-weight: 600;
  color: #ffffff;
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  border: none;
  border-radius: var(--radius-full);
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 15px rgba(67, 160, 71, 0.3);
}

.elegant-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s ease;
}

.elegant-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(67, 160, 71, 0.4);
}

.elegant-btn:hover::before {
  left: 100%;
}
</style>