<template>
  <nav class="navbar">
    <div class="navbar-left">
      <div class="navbar-logo" @click="goToHome">
        <div class="logo-icon">
          <span class="logo-heart">🌾</span>
        </div>
        <span class="logo-text">火山石板岩</span>
      </div>
      <div class="navbar-slogan">火山岩土滋养 · 寒地农珍</div>
    </div>

    <div class="navbar-center">
      <div 
        class="nav-item" 
        :class="{ active: $route.path === '/' }"
        @click="goToHome"
      >
        <span class="nav-icon">🏠</span>
        <span class="nav-label">首页</span>
      </div>
      <div 
        class="nav-item" 
        :class="{ active: $route.path === '/product' }"
        @click="goToProductList"
      >
        <span class="nav-icon">🛍️</span>
        <span class="nav-label">商品列表</span>
      </div>
      <div v-if="token" 
        class="nav-item" 
        :class="{ active: $route.path === '/my-orders' }"
        @click="goToPage('my-orders')"
      >
        <span class="nav-icon">📦</span>
        <span class="nav-label">我的订单</span>
      </div>
      <div v-if="token" 
        class="nav-item" 
        :class="{ active: $route.path === '/my-cart' }"
        @click="goToPage('my-cart')"
      >
        <span class="nav-icon">🛒</span>
        <span class="nav-label">购物车</span>
      </div>
    </div>

    <div v-if="token" class="navbar-right">
      <div class="user-menu-wrapper" @mouseenter="showUserMenu = true" @mouseleave="showUserMenu = false">
        <div class="user-trigger">
          <img :src="avatar" alt="Avatar" class="user-avatar" />
          <span class="user-indicator"></span>
        </div>
        <Transition name="dropdown">
          <div v-if="showUserMenu" class="user-dropdown">
            <div class="dropdown-header">
              <div class="header-avatar">
                <img :src="avatar" alt="User" />
              </div>
              <div class="header-info">
                <div class="info-name">我的账户</div>
                <div class="info-desc">欢迎回来</div>
              </div>
            </div>
            <div class="dropdown-divider"></div>
            <ul class="dropdown-menu">
              <li @click="goToPage('personal-center')" class="dropdown-item">
                <span class="item-icon">👤</span>
                <span>个人中心</span>
              </li>
              <li @click="goToPage('favorites')" class="dropdown-item">
                <span class="item-icon">❤️</span>
                <span>收藏夹</span>
              </li>
              <li @click="goToPage('my-reviews')" class="dropdown-item">
                <span class="item-icon">⭐</span>
                <span>我的评价</span>
              </li>
              <li @click="goToPage('browse-history')" class="dropdown-item">
                <span class="item-icon">👀</span>
                <span>浏览记录</span>
              </li>
              <li @click="handleLogout" class="dropdown-item logout">
                <span class="item-icon">🚪</span>
                <span>退出登录</span>
              </li>
            </ul>
          </div>
        </Transition>
      </div>
    </div>

    <div v-else class="navbar-right">
      <button class="login-btn" @click="goToLogin">
        <span class="btn-icon">🔑</span>
        <span class="btn-text">登录</span>
      </button>
    </div>
  </nav>
</template>

<script>
import avatar from '@/assets/avatar.png';
import { getToken, getUser } from "@/utils/auth";

export default {
  data() {
    return {
      showUserMenu: false,
      token: '',
      avatar: avatar,
    };
  },
  created() {
    this.initUserData();
  },
  methods: {
    initUserData() {
      try {
        this.token = getToken() || '';
        const user = getUser();
        if (user && user.userAvatar) {
          this.avatar = user.userAvatar;
        }
      } catch (error) {
        console.error('初始化用户数据失败:', error);
        this.token = '';
        this.avatar = avatar;
      }
    },
    goToHome() {
      if (this.$route.path !== "/") {
        this.$router.push("/");
      }
    },
    goToProductList() {
      if (this.$route.path !== "/product") {
        this.$router.push("/product");
      }
    },
    goToLogin() {
      if (this.$route.path !== "/login") {
        this.$router.push("/login");
      }
    },
    goToPage(pageName) {
      this.showUserMenu = false;
      if (this.$route.name !== pageName) {
        this.$router.push({ name: pageName });
      }
    },
    handleLogout() {
      localStorage.removeItem("Front-Token");
      localStorage.removeItem("Avatar");
      this.token = "";
      this.avatar = avatar;
      this.showUserMenu = false;
      this.$router.push("/login");
    },
  },
  watch: {
    $route() {
      this.initUserData();
    },
  },
};
</script>

<style scoped>
.navbar {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 80px;
  background: rgba(255, 255, 255, 0.96);
  backdrop-filter: blur(20px);
  -webkit-backdrop-filter: blur(20px);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 50px;
  z-index: 1000;
  box-shadow: 0 2px 25px rgba(67, 160, 71, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.navbar:hover {
  box-shadow: 0 4px 35px rgba(67, 160, 71, 0.12);
}

.navbar-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.navbar-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.logo-icon {
  width: 45px;
  height: 45px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 15px rgba(67, 160, 71, 0.3);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.navbar-logo:hover .logo-icon {
  transform: scale(1.08);
  box-shadow: 0 6px 20px rgba(67, 160, 71, 0.4);
}

.logo-heart {
  font-size: 22px;
  color: #ffffff;
}

.logo-text {
  font-size: 22px;
  font-weight: 700;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: 1.5px;
}

.navbar-slogan {
  font-size: 12px;
  color: #8a8a9a;
  font-weight: 400;
  letter-spacing: 2px;
  margin-left: 10px;
  padding-left: 20px;
  border-left: 1px solid rgba(67, 160, 71, 0.15);
}

.navbar-center {
  display: flex;
  gap: 40px;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  padding: 10px 24px;
  cursor: pointer;
  border-radius: 16px;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
}

.nav-item::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 0;
  height: 3px;
  background: linear-gradient(90deg, #43a047 0%, #66bb6a 100%);
  border-radius: 3px;
  transition: width 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.nav-item:hover {
  background: rgba(67, 160, 71, 0.06);
}

.nav-item:hover::before {
  width: 60%;
}

.nav-item.active {
  background: rgba(67, 160, 71, 0.1);
}

.nav-item.active::before {
  width: 80%;
}

.nav-icon {
  font-size: 18px;
}

.nav-label {
  font-size: 13px;
  font-weight: 500;
  color: #4a4a68;
}

.nav-item.active .nav-label {
  color: #43a047;
  font-weight: 600;
}

.navbar-right {
  display: flex;
  align-items: center;
}

.user-menu-wrapper {
  position: relative;
}

.user-trigger {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  cursor: pointer;
  border-radius: 30px;
  background: rgba(67, 160, 71, 0.08);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.user-trigger:hover {
  background: rgba(67, 160, 71, 0.15);
  box-shadow: 0 4px 15px rgba(67, 160, 71, 0.2);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  border: 2px solid rgba(67, 160, 71, 0.3);
  transition: border-color 0.4s ease;
}

.user-trigger:hover .user-avatar {
  border-color: #43a047;
}

.user-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: linear-gradient(135deg, #66bb6a 0%, #81c784 100%);
  box-shadow: 0 0 8px rgba(102, 187, 106, 0.5);
}

.user-dropdown {
  position: absolute;
  top: calc(100% + 15px);
  right: 0;
  width: 220px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 15px 50px rgba(67, 160, 71, 0.15);
  z-index: 1001;
  overflow: hidden;
  border: 1px solid rgba(67, 160, 71, 0.08);
}

.user-dropdown::before {
  content: '';
  position: absolute;
  top: -6px;
  right: 20px;
  width: 12px;
  height: 12px;
  background: #ffffff;
  border-left: 1px solid rgba(67, 160, 71, 0.08);
  border-top: 1px solid rgba(67, 160, 71, 0.08);
  transform: rotate(45deg);
}

.dropdown-header {
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, rgba(67, 160, 71, 0.08) 0%, rgba(102, 187, 106, 0.05) 100%);
}

.header-avatar img {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  border: 3px solid rgba(67, 160, 71, 0.2);
}

.header-info {
  display: flex;
  flex-direction: column;
}

.info-name {
  font-size: 15px;
  font-weight: 600;
  color: #1a1a2e;
}

.info-desc {
  font-size: 12px;
  color: #8a8a9a;
  margin-top: 2px;
}

.dropdown-divider {
  height: 1px;
  background: rgba(67, 160, 71, 0.1);
}

.dropdown-menu {
  list-style: none;
  padding: 8px 0;
  margin: 0;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 20px;
  cursor: pointer;
  font-size: 14px;
  color: #4a4a68;
  transition: all 0.3s ease;
  position: relative;
}

.dropdown-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, #43a047 0%, #66bb6a 100%);
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.dropdown-item:hover {
  background: rgba(67, 160, 71, 0.08);
  color: #43a047;
  padding-left: 24px;
}

.dropdown-item:hover::before {
  transform: scaleY(1);
}

.item-icon {
  font-size: 16px;
}

.dropdown-item.logout {
  color: #e57373;
}

.dropdown-item.logout:hover {
  background: rgba(229, 115, 115, 0.1);
  color: #ef5350;
}

.login-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 28px;
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  border: none;
  border-radius: 30px;
  cursor: pointer;
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 15px rgba(67, 160, 71, 0.3);
  position: relative;
}

.login-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.6s ease;
}

.login-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(67, 160, 71, 0.4);
}

.login-btn:hover::before {
  left: 100%;
}

.btn-icon {
  font-size: 14px;
}

.dropdown-enter-active,
.dropdown-leave-active {
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.dropdown-enter-from,
.dropdown-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}
</style>