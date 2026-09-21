<template>
  <div class="personal-center">
    <div class="user-profile-card">
      <div class="profile-bg"></div>
      <div class="profile-content">
        <div class="avatar-section">
          <div class="avatar-container">
            <img :src="user.userAvatar || avatar" alt="用户头像" class="user-avatar" />
            <div class="avatar-upload">
              <input type="file" id="avatarUploadInput" class="upload-input" @change="handleAvatarUpload" accept="image/*" />
              <label for="avatarUploadInput" class="upload-label">
                <span class="upload-icon">📷</span>
              </label>
            </div>
          </div>
          <div class="user-info">
            <h2 class="user-name">{{ user.userName }}</h2>
            <p class="user-id">ID: {{ user.id }}</p>
            <div class="user-balance">
              <span class="balance-label">账户余额</span>
              <span class="balance-amount">¥{{ user.userBalance || 0 }}</span>
              <button class="recharge-btn" @click="handleRecharge">
                <span>💰</span> 充值
              </button>
            </div>
          </div>
        </div>
        
        <div class="quick-stats">
          <div class="stat-card" @click="goToPage('my-orders')">
            <span class="stat-icon">📦</span>
            <div class="stat-info">
              <span class="stat-num">{{ orderCount }}</span>
              <span class="stat-text">我的订单</span>
            </div>
          </div>
          <div class="stat-card" @click="goToPage('my-cart')">
            <span class="stat-icon">🛒</span>
            <div class="stat-info">
              <span class="stat-num">{{ cartCount }}</span>
              <span class="stat-text">购物车</span>
            </div>
          </div>
          <div class="stat-card" @click="goToPage('favorites')">
            <span class="stat-icon">❤️</span>
            <div class="stat-info">
              <span class="stat-num">{{ favoriteCount }}</span>
              <span class="stat-text">收藏</span>
            </div>
          </div>
          <div class="stat-card" @click="goToPage('my-reviews')">
            <span class="stat-icon">⭐</span>
            <div class="stat-info">
              <span class="stat-num">{{ reviewCount }}</span>
              <span class="stat-text">评价</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="main-content">
      <div class="edit-section">
        <h3 class="section-title">
          <span class="title-icon">✏️</span>
          编辑资料
        </h3>
        <el-form :model="userForm" class="user-form" label-width="100px">
          <el-form-item label="用户名">
            <el-input v-model="userForm.userName" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="userForm.email" type="email" placeholder="请输入邮箱" />
          </el-form-item>
          <el-form-item label="手机号">
            <el-input v-model="userForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="性别">
            <el-radio-group v-model="userForm.gender">
              <el-radio label="0">男</el-radio>
              <el-radio label="1">女</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item>
            <button class="save-btn" @click="handleSave">
              <span class="btn-icon">💾</span>
              保存修改
            </button>
          </el-form-item>
        </el-form>
      </div>

      <div class="menu-section">
        <h3 class="section-title">
          <span class="title-icon">📋</span>
          更多选项
        </h3>
        <ul class="menu-list">
          <li class="menu-item" @click="goToPage('browse-history')">
            <span class="menu-icon">👀</span>
            <span class="menu-text">浏览记录</span>
            <span class="menu-arrow">→</span>
          </li>
          <li class="menu-item" @click="handleChangePwd">
            <span class="menu-icon">🔐</span>
            <span class="menu-text">修改密码</span>
            <span class="menu-arrow">→</span>
          </li>
          <li class="menu-item" @click="handleLogout">
            <span class="menu-icon">🚪</span>
            <span class="menu-text">退出登录</span>
            <span class="menu-arrow">→</span>
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script>
import { getById, updatePersonalData, recharge } from "@/api/ums/user";
import { fileUpload } from "@/api/publish/file";
import { getUser, setUser } from "@/utils/auth";
import avatar from "@/assets/avatar.png";
import { removeToken } from "@/utils/auth";

export default {
  name: "PersonalCenter",
  data() {
    return {
      user: {},
      userForm: {
        userName: "",
        email: "",
        phone: "",
        gender: "0",
      },
      avatar,
      orderCount: 0,
      cartCount: 0,
      favoriteCount: 0,
      reviewCount: 0,
    };
  },
  created() {
    this.loadUserInfo();
  },
  methods: {
    async loadUserInfo() {
      try {
        const response = await getById();
        if (response.code === 0) {
          this.user = response.data;
          this.userForm.userName = this.user.userName || "";
          this.userForm.email = this.user.email || "";
          this.userForm.phone = this.user.phone || "";
          this.userForm.gender = this.user.gender?.toString() || "0";
          this.orderCount = this.user.orderCount || 0;
          this.cartCount = this.user.cartCount || 0;
          this.favoriteCount = this.user.favoriteCount || 0;
          this.reviewCount = this.user.reviewCount || 0;
        }
      } catch (error) {
        console.error("获取用户信息失败:", error);
      }
    },
    async handleAvatarUpload(event) {
      const file = event.target.files[0];
      if (!file) return;
      event.target.value = "";
      try {
        // 压缩图片：限制最长边 500px，压缩后远小于网关 256KB 限制
        const compressed = await this.compressImage(file);
        const formData = new FormData();
        formData.append("file", compressed, "avatar.jpg");
        const uploadRes = await fileUpload(formData);
        if (uploadRes.code === 200 && uploadRes.data && uploadRes.data.url) {
          const avatarUrl = uploadRes.data.url;
          const updateRes = await updatePersonalData({ userAvatar: avatarUrl });
          if (updateRes.code === 0) {
            this.$message.success("头像更新成功");
            this.user.userAvatar = avatarUrl;
            // 同步更新本地缓存的用户信息，导航栏头像随之刷新
            const cachedUser = getUser();
            if (cachedUser) {
              setUser({ ...cachedUser, userAvatar: avatarUrl });
            }
          } else {
            this.$message.error("头像保存失败");
          }
        } else {
          this.$message.error("图片上传失败");
        }
      } catch (error) {
        console.error("头像上传失败:", error);
        this.$message.error("头像上传失败，请重试");
      }
    },
    compressImage(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader();
        reader.onload = (e) => {
          const img = new Image();
          img.onload = () => {
            const MAX = 500;
            let w = img.width;
            let h = img.height;
            const ratio = Math.min(MAX / w, MAX / h);
            if (ratio < 1) {
              w = Math.round(w * ratio);
              h = Math.round(h * ratio);
            }
            const canvas = document.createElement("canvas");
            canvas.width = w;
            canvas.height = h;
            canvas.getContext("2d").drawImage(img, 0, 0, w, h);
            canvas.toBlob((blob) => resolve(blob), "image/jpeg", 0.8);
          };
          img.onerror = () => reject(new Error("图片解析失败"));
          img.src = e.target.result;
        };
        reader.onerror = () => reject(new Error("文件读取失败"));
        reader.readAsDataURL(file);
      });
    },
    async handleSave() {
      try {
        const response = await updatePersonalData(this.userForm);
        if (response.code === 0) {
          this.$message.success("修改成功");
          this.loadUserInfo();
        }
      } catch (error) {
        console.error("修改用户信息失败:", error);
      }
    },
    async handleRecharge() {
      const result = await this.$prompt("请输入充值金额", "充值", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        inputPattern: /^[0-9]+(\.[0-9]{1,2})?$/,
        inputErrorMessage: "请输入有效的金额",
      });
      if (result) {
        const amount = result.value || result;
        if (!amount || parseFloat(amount) <= 0) {
          this.$message.error("请输入有效的充值金额");
          return;
        }
        try {
          const response = await recharge(parseFloat(amount));
          if (response.code === 0) {
            this.$message.success("充值成功");
            this.loadUserInfo();
          } else {
            this.$message.error(response.msg || "充值失败");
          }
        } catch (error) {
          console.error("充值失败:", error);
          this.$message.error("充值失败，请重试！");
        }
      }
    },
    handleChangePwd() {
      this.$message.info("修改密码功能开发中");
    },
    handleLogout() {
      removeToken();
      this.$router.push("/login");
    },
    goToPage(pageName) {
      this.$router.push({ name: pageName });
    },
  },
};
</script>

<style scoped>
.personal-center {
  min-height: 100vh;
  padding: 30px 40px;
  max-width: 1000px;
  margin: 0 auto;
}

.user-profile-card {
  position: relative;
  border-radius: 28px;
  overflow: hidden;
  margin-bottom: 30px;
  box-shadow: 0 15px 50px rgba(67, 160, 71, 0.15);
}

.profile-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 180px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 50%, #a5d6a7 100%);
}

.profile-content {
  position: relative;
  padding: 100px 40px 40px;
}

.avatar-section {
  display: flex;
  align-items: flex-start;
  gap: 30px;
  margin-bottom: 35px;
}

.avatar-container {
  position: relative;
  width: 140px;
  height: 140px;
  margin-top: -60px;
}

.user-avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 4px solid #ffffff;
  box-shadow: 0 8px 25px rgba(67, 160, 71, 0.3);
  object-fit: cover;
}

.avatar-upload {
  position: absolute;
  bottom: 5px;
  right: 5px;
}

.upload-input {
  display: none;
}

.upload-label {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 35px;
  height: 35px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  border-radius: 50%;
  cursor: pointer;
  border: 3px solid #ffffff;
  transition: all 0.3s ease;
}

.upload-label:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 15px rgba(67, 160, 71, 0.4);
}

.upload-icon {
  font-size: 14px;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.user-id {
  font-size: 14px;
  color: #8a8a9a;
  margin-bottom: 15px;
}

.user-balance {
  display: flex;
  align-items: center;
  gap: 15px;
}

.balance-label {
  font-size: 14px;
  color: #8a8a9a;
}

.balance-amount {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.recharge-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: rgba(67, 160, 71, 0.1);
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  color: #43a047;
  cursor: pointer;
  transition: all 0.3s ease;
}

.recharge-btn:hover {
  background: rgba(67, 160, 71, 0.15);
  transform: translateY(-2px);
}

.quick-stats {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  background: rgba(67, 160, 71, 0.05);
  border-radius: 18px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.stat-card:hover {
  background: rgba(67, 160, 71, 0.1);
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(67, 160, 71, 0.15);
}

.stat-icon {
  font-size: 28px;
}

.stat-info {
  display: flex;
  flex-direction: column;
}

.stat-num {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a2e;
}

.stat-text {
  font-size: 13px;
  color: #8a8a9a;
}

.main-content {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

.edit-section,
.menu-section {
  background: #ffffff;
  border-radius: 24px;
  padding: 35px;
  box-shadow: 0 8px 30px rgba(67, 160, 71, 0.08);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 25px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(67, 160, 71, 0.1);
}

.title-icon {
  margin-right: 10px;
}

.user-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

:deep(.el-form-item__label) {
  color: #666666;
  font-weight: 500;
}

:deep(.el-input__inner) {
  background: #fafafa;
  border: 1.5px solid #e9ecef;
  border-radius: 12px;
  color: #333333;
}

:deep(.el-input__inner:focus) {
  border-color: #43a047;
  box-shadow: 0 0 0 3px rgba(67, 160, 71, 0.1);
}

.save-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  border: none;
  border-radius: 14px;
  font-size: 15px;
  font-weight: 600;
  color: #ffffff;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 6px 20px rgba(67, 160, 71, 0.3);
}

.save-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(67, 160, 71, 0.4);
}

.btn-icon {
  font-size: 16px;
}

.menu-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 18px 20px;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.menu-item:hover {
  background: rgba(67, 160, 71, 0.08);
  padding-left: 25px;
}

.menu-icon {
  font-size: 20px;
}

.menu-text {
  flex: 1;
  font-size: 15px;
  color: #4a4a68;
}

.menu-arrow {
  font-size: 16px;
  color: #cccccc;
}

@media (max-width: 768px) {
  .avatar-section {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .user-balance {
    justify-content: center;
  }
  
  .quick-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .main-content {
    grid-template-columns: 1fr;
  }
}
</style>