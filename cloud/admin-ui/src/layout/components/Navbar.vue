<template>
  <div class="navbar">
    <hamburger id="hamburger-container" :is-active="sidebar.opened" class="hamburger-container" @toggleClick="toggleSideBar" />

    <breadcrumb id="breadcrumb-container" class="breadcrumb-container" v-if="!topNav"/>
    <top-nav id="topmenu-container" class="topmenu-container" v-if="topNav"/>

    <div class="right-menu">

      <el-dropdown class="avatar-container right-menu-item hover-effect" trigger="click">
        <div class="avatar-wrapper">
          <img :src="avatar" class="user-avatar">
          <i class="el-icon-caret-bottom" />
        </div>
        <el-dropdown-menu slot="dropdown">
          <router-link to="/user/profile">
            <el-dropdown-item>个人中心</el-dropdown-item>
          </router-link>
          <el-dropdown-item @click.native="setting = true">
            <span>布局设置</span>
          </el-dropdown-item>
          <el-dropdown-item divided @click.native="logout">
            <span>退出登录</span>
          </el-dropdown-item>
        </el-dropdown-menu>
      </el-dropdown>
    </div>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import Breadcrumb from '@/components/Breadcrumb'
import TopNav from '@/components/TopNav'
import Hamburger from '@/components/Hamburger'
import Screenfull from '@/components/Screenfull'
import SizeSelect from '@/components/SizeSelect'
import Search from '@/components/HeaderSearch'
import RuoYiGit from '@/components/RuoYi/Git'
import RuoYiDoc from '@/components/RuoYi/Doc'

export default {
  components: {
    Breadcrumb,
    TopNav,
    Hamburger,
    Screenfull,
    SizeSelect,
    Search,
    RuoYiGit,
    RuoYiDoc
  },
  computed: {
    ...mapGetters([
      'sidebar',
      'avatar',
      'device'
    ]),
    setting: {
      get() {
        return this.$store.state.settings.showSettings
      },
      set(val) {
        this.$store.dispatch('settings/changeSetting', {
          key: 'showSettings',
          value: val
        })
      }
    },
    topNav: {
      get() {
        return this.$store.state.settings.topNav
      }
    }
  },
  methods: {
    toggleSideBar() {
      this.$store.dispatch('app/toggleSideBar')
    },
    async logout() {
      this.$confirm('确定注销并退出系统吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$store.dispatch('LogOut').then(() => {
          location.href = '/index';
        })
      }).catch(() => {});
    }
  }
}
</script>

<style lang="scss" scoped>
@import "~@/assets/styles/variables.scss";

.navbar {
  height: $header-height;
  overflow: hidden;
  position: relative;
  background: $bg-card;
  border-bottom: 1px solid $border-color;

  .hamburger-container {
    line-height: $header-height;
    height: 100%;
    float: left;
    cursor: pointer;
    transition: all $transition-fast;
    padding: 0 16px;
    -webkit-tap-highlight-color: transparent;

    &:hover {
      background: $bg-hover;
    }
  }

  .breadcrumb-container {
    float: left;
    padding-left: 16px;
  }

  .topmenu-container {
    position: absolute;
    left: 64px;
  }

  .errLog-container {
    display: inline-block;
    vertical-align: top;
  }

  .right-menu {
    float: right;
    height: 100%;
    display: flex;
    align-items: center;
    padding-right: 16px;

    &:focus {
      outline: none;
    }

    .right-menu-item {
      display: flex;
      align-items: center;
      justify-content: center;
      padding: 0 12px;
      height: 40px;
      margin: 0 4px;
      font-size: 18px;
      color: $text-secondary;
      border-radius: $border-radius-sm;
      transition: all $transition-fast;

      &.hover-effect {
        cursor: pointer;

        &:hover {
          background: $bg-hover;
          color: $text-primary;
        }
      }
    }

    .avatar-container {
      display: flex;
      align-items: center;
      padding: 0 12px;
      height: 40px;
      margin-left: 8px;
      border-radius: $border-radius-sm;
      cursor: pointer;
      transition: all $transition-fast;

      &:hover {
        background: $bg-hover;
      }

      .avatar-wrapper {
        display: flex;
        align-items: center;
        position: relative;

        .user-avatar {
          cursor: pointer;
          width: 36px;
          height: 36px;
          border-radius: 50%;
          object-fit: cover;
          border: 2px solid $border-color;
        }

        .el-icon-caret-bottom {
          cursor: pointer;
          position: relative;
          margin-left: 8px;
          font-size: 14px;
          color: $text-muted;
          transition: color $transition-fast;
        }
      }
    }
  }
}
</style>