<template>
  <div class="login-page">
    <!-- 背景装饰 -->
    <div class="background-decoration">
      <div class="floating-circle circle-1"></div>
      <div class="floating-circle circle-2"></div>
      <div class="floating-circle circle-3"></div>
      <div class="floating-circle circle-4"></div>
      <div class="wave wave-1"></div>
      <div class="wave wave-2"></div>
      <div class="wave wave-3"></div>
    </div>

    <!-- 主内容区 -->
    <div class="login-wrapper">
      <!-- 左侧文案装饰区 -->
      <div class="brand-section">
        <div class="brand-logo">
          <span class="logo-icon">🌾</span>
        </div>
        <h1 class="brand-title">火山石板岩</h1>
        <p class="brand-subtitle">品味天然好味，源自火山岩土</p>
        <div class="brand-features">
          <div class="feature-item">
            <span class="feature-icon">🌾</span>
            <span class="feature-text">火山农品</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">📍</span>
            <span class="feature-text">产地溯源</span>
          </div>
          <div class="feature-item">
            <span class="feature-icon">🌱</span>
            <span class="feature-text">新鲜直达</span>
          </div>
        </div>
        <div class="brand-quote">
          <p>"每一粒米，都来自火山熔岩台地的馈赠"</p>
        </div>
      </div>

      <!-- 右侧登录表单区 -->
      <div class="form-section">
        <div class="form-card">
          <div class="form-header">
            <h2 class="form-title">{{ isLogin ? '欢迎回来' : '创建账号' }}</h2>
            <p class="form-subtitle">{{ isLogin ? '请登录您的账号' : '开启您的购物之旅' }}</p>
          </div>

          <el-form :model="formData" :rules="rules" ref="formRef" class="login-form">
            <div class="input-group">
              <label class="input-label">用户名</label>
              <el-input 
                v-model="formData.username" 
                placeholder="请输入用户名"
                class="custom-input"
              >
                <i class="el-icon-user" slot="prefix"></i>
              </el-input>
            </div>

            <div class="input-group">
              <label class="input-label">密码</label>
              <el-input 
                v-model="formData.password" 
                type="password" 
                placeholder="请输入密码" 
                show-password
                class="custom-input"
              >
                <i class="el-icon-lock" slot="prefix"></i>
              </el-input>
            </div>

            <div v-if="!isLogin" class="input-group">
              <label class="input-label">确认密码</label>
              <el-input 
                v-model="formData.confirmPassword" 
                type="password" 
                placeholder="请确认密码" 
                show-password
                class="custom-input"
              >
                <i class="el-icon-lock" slot="prefix"></i>
              </el-input>
            </div>

            <div v-if="isLogin" class="remember-me">
              <el-checkbox v-model="rememberMe">记住我</el-checkbox>
              <a href="#" class="forgot-password">忘记密码?</a>
            </div>

            <el-button type="primary" class="submit-button" @click="handleSubmit" :loading="loading">
              {{ isLogin ? '登 录' : '注 册' }}
            </el-button>
          </el-form>

          <div class="form-divider">
            <span class="divider-line"></span>
            <span class="divider-text">或者</span>
            <span class="divider-line"></span>
          </div>

          <div class="social-login">
            <button class="social-btn wechat">
              <span class="social-icon">💬</span>
              <span>微信登录</span>
            </button>
            <button class="social-btn weibo">
              <span class="social-icon">🔴</span>
              <span>微博登录</span>
            </button>
          </div>

          <p class="toggle-text" @click="toggleForm">
            {{ isLogin ? '还没有账号？立即注册' : '已有账号？立即登录' }}
          </p>
        </div>

        <div class="security-badge">
          <span class="security-icon">🔒</span>
          <span>安全加密 · 隐私保护</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { login, register } from '@/api/ums/user';
import { setToken, setUser } from '@/utils/auth';

export default {
  data() {
    return {
      isLogin: true,
      loading: false,
      rememberMe: false,
      formData: {
        username: '',
        password: '',
        confirmPassword: '',
      },
      rules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
        confirmPassword: [{ required: true, message: '请确认密码', trigger: 'blur' }],
      },
    };
  },
  methods: {
    toggleForm() {
      this.isLogin = !this.isLogin;
    },
    async handleSubmit() {
      this.loading = true;
      try {
        let res;
        if (this.isLogin) {
          res = await login(this.formData);
          setToken(res.data.token);
          setUser(res.data.user);
          this.$message.success('登录成功！');
          this.$router.push('/').then(() => {
            window.location.reload();
          });
        } else {
          if (this.formData.password !== this.formData.confirmPassword) {
            this.$message.error('两次密码输入不一致');
            return;
          }
          res = await register(this.formData);
          this.$message.success('注册成功，请登录！');
          this.isLogin = true;
        }
      } catch (error) {
        this.$message.error(error.message || '操作失败');
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped>
.login-page {
  position: relative;
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #fef7f8 0%, #faf0f2 50%, #f5e6e8 100%);
  overflow: hidden;
  font-family: 'SF Pro Display', -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
  overflow: hidden;
}

.floating-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.5;
  animation: float 6s ease-in-out infinite;
}

.circle-1 {
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, rgba(67, 160, 71, 0.3) 0%, transparent 70%);
  top: -100px;
  right: -100px;
  animation-delay: 0s;
}

.circle-2 {
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(102, 187, 106, 0.3) 0%, transparent 70%);
  bottom: -50px;
  left: -50px;
  animation-delay: 2s;
}

.circle-3 {
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, rgba(255, 200, 209, 0.4) 0%, transparent 70%);
  top: 40%;
  left: 10%;
  animation-delay: 1s;
}

.circle-4 {
  width: 150px;
  height: 150px;
  background: radial-gradient(circle, rgba(67, 160, 71, 0.2) 0%, transparent 70%);
  bottom: 30%;
  right: 15%;
  animation-delay: 3s;
}

@keyframes float {
  0%, 100% { transform: translateY(0) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(5deg); }
}

.wave {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 200%;
  height: 100px;
  background: url("data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1440 320'%3E%3Cpath fill='%2343a047' fill-opacity='0.05' d='M0,224L48,213.3C96,203,192,181,288,181.3C384,181,480,203,576,218.7C672,235,768,245,864,234.7C960,224,1056,192,1152,181.3C1248,171,1344,181,1392,186.7L1440,192L1440,320L1392,320C1344,320,1248,320,1152,320C1056,320,960,320,864,320C768,320,672,320,576,320C480,320,384,320,288,320C192,320,96,320,48,320L0,320Z'%3E%3C/path%3E%3C/svg%3E");
  animation: waveMove 10s linear infinite;
}

.wave-2 {
  animation-delay: -3s;
  opacity: 0.5;
}

.wave-3 {
  animation-delay: -7s;
  opacity: 0.3;
}

@keyframes waveMove {
  0% { transform: translateX(0); }
  100% { transform: translateX(-50%); }
}

.login-wrapper {
  position: relative;
  display: flex;
  width: 900px;
  height: 550px;
  z-index: 10;
}

.brand-section {
  flex: 1;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  border-radius: 30px 0 0 30px;
  padding: 60px 40px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  color: #ffffff;
  box-shadow: -10px 0 40px rgba(67, 160, 71, 0.3);
}

.brand-logo {
  width: 80px;
  height: 80px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 30px;
  backdrop-filter: blur(10px);
}

.logo-icon {
  font-size: 36px;
}

.brand-title {
  font-size: 36px;
  font-weight: 700;
  margin-bottom: 10px;
  letter-spacing: 2px;
}

.brand-subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin-bottom: 40px;
  letter-spacing: 1px;
}

.brand-features {
  display: flex;
  flex-direction: column;
  gap: 20px;
  margin-bottom: 40px;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 15px;
}

.feature-icon {
  font-size: 24px;
}

.feature-text {
  font-size: 15px;
  font-weight: 500;
}

.brand-quote {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 16px;
  padding: 25px;
  backdrop-filter: blur(10px);
}

.brand-quote p {
  font-size: 15px;
  font-style: italic;
  line-height: 1.8;
  opacity: 0.95;
}

.form-section {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.form-card {
  background: #ffffff;
  border-radius: 24px;
  padding: 50px 45px;
  box-shadow: 0 20px 60px rgba(67, 160, 71, 0.15);
  width: 100%;
}

.form-header {
  text-align: center;
  margin-bottom: 40px;
}

.form-title {
  font-size: 28px;
  font-weight: 700;
  color: #2d2d2d;
  margin-bottom: 8px;
}

.form-subtitle {
  font-size: 14px;
  color: #999999;
}

.login-form {
  display: flex;
  flex-direction: column;
  gap: 25px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-label {
  font-size: 13px;
  font-weight: 600;
  color: #666666;
  letter-spacing: 0.5px;
}

.custom-input {
  background: #f8f9fa;
  border: 1.5px solid #e9ecef;
  border-radius: 12px;
  transition: all 0.3s ease;
}

.custom-input:hover {
  border-color: #66bb6a;
}

.custom-input:focus {
  border-color: #43a047;
  box-shadow: 0 0 0 3px rgba(67, 160, 71, 0.1);
}

:deep(.custom-input .el-input__inner) {
  padding: 15px 20px;
  font-size: 14px;
  color: #2d2d2d;
  background: transparent;
}

:deep(.custom-input .el-input__inner::placeholder) {
  color: #cccccc;
}

:deep(.custom-input .el-input__prefix) {
  color: #66bb6a;
}

.remember-me {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

:deep(.remember-me .el-checkbox__label) {
  font-size: 13px;
  color: #666666;
}

:deep(.remember-me .el-checkbox__inner) {
  border-color: #e9ecef;
}

:deep(.remember-me .el-checkbox__inner.is-checked) {
  background: #43a047;
  border-color: #43a047;
}

.forgot-password {
  font-size: 13px;
  color: #43a047;
  text-decoration: none;
  transition: color 0.3s ease;
}

.forgot-password:hover {
  color: #2e7d32;
}

.submit-button {
  height: 52px;
  font-size: 15px;
  font-weight: 600;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  border: none;
  border-radius: 14px;
  color: #ffffff;
  transition: all 0.3s ease;
  box-shadow: 0 8px 25px rgba(67, 160, 71, 0.35);
}

.submit-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 12px 35px rgba(67, 160, 71, 0.45);
}

.submit-button:active {
  transform: translateY(0);
}

.form-divider {
  display: flex;
  align-items: center;
  gap: 15px;
  margin: 25px 0;
}

.divider-line {
  flex: 1;
  height: 1px;
  background: linear-gradient(to right, transparent, #e9ecef, transparent);
}

.divider-text {
  font-size: 13px;
  color: #999999;
}

.social-login {
  display: flex;
  gap: 15px;
}

.social-btn {
  flex: 1;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  border: 1.5px solid #e9ecef;
  border-radius: 10px;
  background: #ffffff;
  font-size: 13px;
  color: #666666;
  cursor: pointer;
  transition: all 0.3s ease;
}

.social-btn:hover {
  border-color: #43a047;
  color: #43a047;
  background: #fff5f6;
}

.social-icon {
  font-size: 18px;
}

.toggle-text {
  text-align: center;
  font-size: 13px;
  color: #999999;
  margin-top: 25px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.toggle-text:hover {
  color: #43a047;
}

.security-badge {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 25px;
  font-size: 12px;
  color: #999999;
}

.security-icon {
  font-size: 14px;
}
</style>