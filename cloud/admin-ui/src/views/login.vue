<template>
  <div class="login">
    <el-form ref="loginForm" :model="loginForm" :rules="loginRules" class="login-form">
      <h3 class="title">商城后台管理</h3>
      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          type="text"
          auto-complete="off"
          placeholder="账号"
        >
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          auto-complete="off"
          placeholder="密码"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaOnOff">
        <el-input
          v-model="loginForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter.native="handleLogin"
        >
          <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
        </el-input>
        <div class="login-code">
          <img :src="codeUrl" @click="getCode" class="login-code-img"/>
        </div>
      </el-form-item>
      <el-checkbox v-model="loginForm.rememberMe" style="margin:0px 0px 25px 0px;">记住密码</el-checkbox>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          style="width:100%;"
          @click.native.prevent="handleLogin"
        >
          <span v-if="!loading">登 录</span>
          <span v-else>登 录 中...</span>
        </el-button>
        <div style="float: right;" v-if="register">
          <router-link class="link-type" :to="'/register'">立即注册</router-link>
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-login-footer">
<!--      <span>Copyright © 2018-2022 cloud.vip All Rights Reserved.</span>-->
    </div>
  </div>
</template>

<script>
import { getCodeImg } from "@/api/login";
import Cookies from "js-cookie";
import { encrypt, decrypt } from '@/utils/jsencrypt'

export default {
  name: "Login",
  data() {
    return {
      codeUrl: "",
      loginForm: {
        username: "admin",
        password: "admin123",
        rememberMe: false,
        code: "",
        uuid: ""
      },
      loginRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      // 验证码开关
      captchaOnOff: true,
      // 注册开关
      register: false,
      redirect: undefined
    };
  },
  watch: {
    $route: {
      handler: function(route) {
        this.redirect = route.query && route.query.redirect;
      },
      immediate: true
    }
  },
  created() {
    this.getCode();
    this.getCookie();
  },
  methods: {
    getCode() {
      getCodeImg().then(res => {
        this.captchaOnOff = res.captchaOnOff === undefined ? true : res.captchaOnOff;
        if (this.captchaOnOff) {
          this.codeUrl = "data:image/gif;base64," + res.img;
          this.loginForm.uuid = res.uuid;
        }
      });
    },
    getCookie() {
      const username = Cookies.get("username");
      const password = Cookies.get("password");
      const rememberMe = Cookies.get('rememberMe')
      this.loginForm = {
        username: username === undefined ? this.loginForm.username : username,
        password: password === undefined ? this.loginForm.password : decrypt(password),
        rememberMe: rememberMe === undefined ? false : Boolean(rememberMe)
      };
    },
    handleLogin() {
      this.$refs.loginForm.validate(valid => {
        if (valid) {
          this.loading = true;
          if (this.loginForm.rememberMe) {
            Cookies.set("username", this.loginForm.username, { expires: 30 });
            Cookies.set("password", encrypt(this.loginForm.password), { expires: 30 });
            Cookies.set('rememberMe', this.loginForm.rememberMe, { expires: 30 });
          } else {
            Cookies.remove("username");
            Cookies.remove("password");
            Cookies.remove('rememberMe');
          }
          this.$store.dispatch("Login", this.loginForm).then(() => {
            this.$router.push({ path: this.redirect || "/" }).catch(()=>{});
          }).catch(() => {
            this.loading = false;
            if (this.captchaOnOff) {
              this.getCode();
            }
          });
        }
      });
    }
  }
};
</script>

<style rel="stylesheet/scss" lang="scss">
@import "~@/assets/styles/variables.scss";

.login {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #0f172a 0%, #1e293b 50%, #0f172a 100%);
  position: relative;
  overflow: hidden;

  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(99, 102, 241, 0.15) 0%, transparent 50%);
    animation: pulse 8s ease-in-out infinite;
  }

  &::after {
    content: '';
    position: absolute;
    top: 20%;
    right: 20%;
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, rgba(139, 92, 246, 0.1) 0%, transparent 60%);
    border-radius: 50%;
    animation: float 6s ease-in-out infinite;
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.5;
  }
  50% {
    transform: scale(1.1);
    opacity: 0.8;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-20px);
  }
}

.title {
  margin: 0 auto 32px auto;
  text-align: center;
  color: $text-primary;
  font-size: 28px;
  font-weight: 700;
  letter-spacing: 2px;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: -12px;
    left: 50%;
    transform: translateX(-50%);
    width: 48px;
    height: 4px;
    background: $primary-gradient;
    border-radius: 2px;
  }
}

.login-form {
  border-radius: $border-radius-lg;
  background: $bg-secondary;
  border: 1px solid $border-color;
  width: 420px;
  padding: 40px;
  box-shadow: $shadow-lg;
  position: relative;
  backdrop-filter: blur(10px);
  animation: slideUp 0.5s ease;

  .el-input {
    height: 48px;
    background: $bg-primary;
    border: 1px solid $border-color;
    border-radius: $border-radius-sm;
    transition: all $transition-fast;

    &:focus-within {
      border-color: $primary-color;
      box-shadow: 0 0 0 3px rgba(99, 102, 241, 0.1);
    }

    input {
      height: 48px;
      background: transparent;
      color: $text-primary;
      font-size: $font-size-base;
    }
  }

  .input-icon {
    height: 48px;
    width: 18px;
    margin-left: 4px;
    color: $text-muted;
  }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.login-tip {
  font-size: $font-size-sm;
  text-align: center;
  color: $text-muted;
}

.login-code {
  width: 33%;
  height: 48px;
  float: right;
  display: flex;
  align-items: center;
  justify-content: center;
  background: $bg-primary;
  border: 1px solid $border-color;
  border-radius: $border-radius-sm;
  cursor: pointer;
  transition: all $transition-fast;

  &:hover {
    border-color: $border-light;
  }

  img {
    cursor: pointer;
    vertical-align: middle;
    max-width: 100%;
    max-height: 100%;
    border-radius: $border-radius-sm;
  }
}

.el-login-footer {
  height: 50px;
  line-height: 50px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: $text-muted;
  font-family: $font-family;
  font-size: $font-size-xs;
  letter-spacing: 1px;
}

.login-code-img {
  height: 40px;
  border-radius: $border-radius-sm;
}

.el-button--primary {
  height: 48px;
  background: $primary-gradient;
  border: none;
  border-radius: $border-radius-sm;
  font-size: $font-size-base;
  font-weight: 600;
  transition: all $transition-fast;

  &:hover {
    transform: translateY(-2px);
    box-shadow: $shadow-glow;
  }

  &:active {
    transform: translateY(0);
  }
}

.el-checkbox {
  .el-checkbox__label {
    color: $text-secondary;
    font-size: $font-size-sm;
  }

  .el-checkbox__input.is-checked .el-checkbox__inner {
    background: $primary-color;
    border-color: $primary-color;
  }
}

.el-link {
  color: $primary-light;
  font-size: $font-size-sm;

  &:hover {
    color: $primary-color;
  }
}
</style>
