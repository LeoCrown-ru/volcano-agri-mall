<template>
  <div class="home-page">
    <section class="hero-section">
      <div class="hero-bg">
        <div class="bg-circle circle-1"></div>
        <div class="bg-circle circle-2"></div>
        <div class="bg-circle circle-3"></div>
      </div>
      <div class="hero-content">
        <div class="hero-text">
          <div class="hero-tagline">火山岩土 · 天然好味</div>
          <h1 class="hero-title">
            <span class="title-gradient">火山甄选</span>
            <br />寒地农珍新体验
          </h1>
          <p class="hero-desc">
            源自火山熔岩台地的自然馈赠，每一粒米、每一朵菌，都承载着黑土地最朴实的味道。
          </p>
          <div class="hero-stats">
            <div class="stat-item">
              <div class="stat-number">1000+</div>
              <div class="stat-label">精选商品</div>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <div class="stat-number">50万+</div>
              <div class="stat-label">满意用户</div>
            </div>
            <div class="stat-divider"></div>
            <div class="stat-item">
              <div class="stat-number">99.8%</div>
              <div class="stat-label">好评率</div>
            </div>
          </div>
        </div>
        <div class="hero-carousel">
          <el-carousel :interval="4000" arrow="always" height="380px" indicator-position="none" class="carousel">
            <el-carousel-item v-for="(image, index) in parsedCarouselImages" :key="index">
              <div class="carousel-card" @click="goToDetail(image.id)">
                <img :src="image.imageUrl" alt="轮播图" class="carousel-image" />
              </div>
            </el-carousel-item>
          </el-carousel>
        </div>
      </div>
    </section>

    <section class="feature-section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-icon">✨</span>
          我们的优势
        </h2>
        <div class="section-subtitle">为什么选择我们？</div>
      </div>
      <div class="features-grid">
        <div class="feature-card" v-for="(feature, index) in features" :key="index">
          <div class="feature-icon">{{ feature.icon }}</div>
          <h3 class="feature-title">{{ feature.title }}</h3>
          <p class="feature-desc">{{ feature.desc }}</p>
        </div>
      </div>
    </section>

    <section class="promotion-section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-icon">🎁</span>
          限时特惠
        </h2>
        <div class="section-subtitle">超值优惠，不容错过</div>
        <div class="section-note">🎉 首购专享特惠，复购恢复原价</div>
      </div>
      <div class="promotion-grid" v-loading="loading">
        <div 
          class="promotion-card" 
          v-for="promotion in promotionList" 
          :key="promotion.id"
          @click="goToPromotionDetail(promotion.id)"
        >
          <div class="promotion-badge">
            {{ promotion.type === 0 ? '折扣' : '优惠券' }}
          </div>
          <div class="promotion-content">
            <h3 class="promotion-name">{{ promotion.name }}</h3>
            <div class="promotion-value">
              <span v-if="promotion.type === 0">
                <span class="value-number">{{ promotion.discount }}</span>折优惠
              </span>
              <span v-else>
                满减 <span class="value-number">¥{{ promotion.couponValue }}</span>
              </span>
            </div>
            <div class="promotion-date">
              {{ formatDate(promotion.startTime) }} - {{ formatDate(promotion.endTime) }}
            </div>
          </div>
          <div class="promotion-decoration"></div>
        </div>
      </div>
    </section>

    <section class="about-section">
      <div class="about-content">
        <div class="about-text">
          <h2 class="about-title">
            <span class="title-gradient">关于我们</span>
          </h2>
          <p class="about-desc" v-html="description"></p>
        </div>
        <div class="about-decoration">
          <div class="decoration-line line-1"></div>
          <div class="decoration-line line-2"></div>
          <div class="decoration-dots">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import { getContent } from "@/api/cms/content";
import { listPromotion } from "@/api/cms/promotion";

export default {
  name: "Home",
  data() {
    return {
      carouselImages: "",
      description: "",
      promotionList: [],
      loading: false,
      features: [
        {
          icon: "🎯",
          title: "品质保证",
          desc: "严格筛选每一件商品，确保品质无忧"
        },
        {
          icon: "🚚",
          title: "极速配送",
          desc: "全国包邮，最快24小时送达"
        },
        {
          icon: "🛡️",
          title: "售后保障",
          desc: "7天无理由退换，购物更安心"
        },
        {
          icon: "💳",
          title: "安全支付",
          desc: "多种支付方式，保障交易安全"
        }
      ]
    };
  },
  computed: {
    parsedCarouselImages() {
      if (this.carouselImages) {
        try {
          return JSON.parse(this.carouselImages);
        } catch (e) {
          console.error("解析轮播图数据失败:", e);
          return [];
        }
      }
      return [];
    },
  },
  created() {
    this.loadContent();
    this.loadPromotions();
  },
  methods: {
    async loadContent() {
      try {
        const response = await getContent();
        if (response.code === 0) {
          this.carouselImages = response.data.imageUrl;
          this.description = response.data.description;
        }
      } catch (error) {
        console.error("获取首页内容失败:", error);
      } finally {
        this.loading = false;
      }
    },
    async loadPromotions() {
      this.loading = true;
      try {
        const response = await listPromotion();
        if (response.code === 0) {
          this.promotionList = response.data;
        }
      } catch (error) {
        console.error("获取促销活动列表失败:", error);
      } finally {
        this.loading = false;
      }
    },
    goToDetail(productId) {
      this.$router.push({ name: "ProductDetail", params: { id: productId } });
    },
    goToPromotionDetail(promotionId) {
      this.$router.push({ name: "PromotionDetail", params: { id: promotionId } });
    },
    formatDate(date) {
      return new Date(date).toLocaleDateString();
    },
  },
};
</script>

<style scoped>
.home-page {
  min-height: 100vh;
}

.hero-section {
  position: relative;
  padding: 60px 40px;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.15;
}

.circle-1 {
  width: 500px;
  height: 500px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  top: -150px;
  right: -100px;
  animation: float 8s ease-in-out infinite;
}

.circle-2 {
  width: 300px;
  height: 300px;
  background: linear-gradient(135deg, #66bb6a 0%, #a5d6a7 100%);
  bottom: -50px;
  left: 100px;
  animation: float 6s ease-in-out infinite reverse;
}

.circle-3 {
  width: 200px;
  height: 200px;
  background: linear-gradient(135deg, #a5d6a7 0%, #c8e6c9 100%);
  top: 50%;
  left: -50px;
  animation: float 10s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-20px) scale(1.05); }
}

.hero-content {
  position: relative;
  z-index: 1;
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 60px;
  padding-top: 40px;
}

.hero-text {
  flex: 1;
}

.hero-tagline {
  font-size: 14px;
  color: #43a047;
  font-weight: 600;
  letter-spacing: 4px;
  margin-bottom: 16px;
  text-transform: uppercase;
}

.hero-title {
  font-size: 48px;
  font-weight: 700;
  color: #1a1a2e;
  line-height: 1.2;
  margin-bottom: 20px;
}

.title-gradient {
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.hero-desc {
  font-size: 16px;
  color: #8a8a9a;
  line-height: 1.8;
  margin-bottom: 30px;
  max-width: 500px;
}

.hero-stats {
  display: flex;
  align-items: center;
  gap: 30px;
  background: rgba(255, 255, 255, 0.8);
  padding: 25px 35px;
  border-radius: 20px;
  box-shadow: 0 8px 30px rgba(67, 160, 71, 0.1);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-number {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.stat-label {
  font-size: 13px;
  color: #8a8a9a;
  margin-top: 4px;
}

.hero-carousel {
  flex: 1;
  display: flex;
  align-items: flex-start;
}

.carousel {
  width: 100%;
  height: 380px;
}

.carousel-card {
  position: relative;
  width: 100%;
  height: 100%;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 15px 50px rgba(67, 160, 71, 0.2);
}

.carousel-image {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: #f5f8f5;
  transition: transform 0.6s ease;
}

.carousel-card:hover .carousel-image {
  transform: scale(1.05);
}

.stat-divider {
  width: 1px;
  height: 50px;
  background: rgba(67, 160, 71, 0.2);
}

.feature-section {
  padding: 80px 40px;
  background: rgba(67, 160, 71, 0.03);
}

.section-header {
  text-align: center;
  margin-bottom: 50px;
}

.section-title {
  font-size: 32px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 12px;
}

.title-icon {
  margin-right: 10px;
}

.section-subtitle {
  font-size: 14px;
  color: #8a8a9a;
  letter-spacing: 2px;
}

.section-note {
  font-size: 13px;
  color: #43a047;
  margin-top: 6px;
  font-weight: 500;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.feature-card {
  background: #ffffff;
  padding: 40px 30px;
  border-radius: 24px;
  text-align: center;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 20px rgba(67, 160, 71, 0.08);
  position: relative;
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
  background: linear-gradient(90deg, #43a047 0%, #66bb6a 50%, #a5d6a7 100%);
  transform: scaleX(0);
  transition: transform 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.feature-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 40px rgba(67, 160, 71, 0.18);
}

.feature-card:hover::before {
  transform: scaleX(1);
}

.feature-icon {
  font-size: 48px;
  margin-bottom: 20px;
}

.feature-title {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 12px;
}

.feature-desc {
  font-size: 14px;
  color: #8a8a9a;
  line-height: 1.6;
}

.promotion-section {
  padding: 80px 40px;
}

.promotion-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  max-width: 1400px;
  margin: 0 auto;
}

.promotion-card {
  position: relative;
  background: #ffffff;
  border-radius: 24px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 20px rgba(67, 160, 71, 0.08);
}

.promotion-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 40px rgba(67, 160, 71, 0.18);
}

.promotion-badge {
  position: absolute;
  top: 20px;
  right: 20px;
  padding: 8px 18px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  color: #ffffff;
  font-size: 12px;
  font-weight: 600;
  border-radius: 20px;
  z-index: 2;
}

.promotion-content {
  padding: 35px;
  position: relative;
  z-index: 1;
}

.promotion-name {
  font-size: 20px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 16px;
}

.promotion-value {
  font-size: 18px;
  color: #43a047;
  font-weight: 600;
  margin-bottom: 12px;
}

.value-number {
  font-size: 32px;
  font-weight: 700;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.promotion-date {
  font-size: 13px;
  color: #8a8a9a;
}

.promotion-decoration {
  position: absolute;
  bottom: -20px;
  right: -20px;
  width: 100px;
  height: 100px;
  background: linear-gradient(135deg, rgba(67, 160, 71, 0.05) 0%, rgba(102, 187, 106, 0.03) 100%);
  border-radius: 50%;
}

.about-section {
  padding: 80px 40px;
  background: rgba(67, 160, 71, 0.03);
}

.about-content {
  max-width: 1000px;
  margin: 0 auto;
  position: relative;
}

.about-title {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 24px;
  text-align: center;
}

.about-desc {
  font-size: 16px;
  color: #4a4a68;
  line-height: 2;
  text-align: center;
}

.about-desc :deep(p) {
  margin-bottom: 16px;
}

.about-decoration {
  position: absolute;
  top: 50%;
  right: -100px;
  transform: translateY(-50%);
}

.decoration-line {
  position: absolute;
  width: 100px;
  height: 3px;
  background: linear-gradient(90deg, #43a047 0%, transparent 100%);
}

.line-1 {
  top: 0;
  transform: rotate(-30deg);
}

.line-2 {
  top: 30px;
  transform: rotate(30deg);
}

.decoration-dots {
  position: absolute;
  top: 60px;
  display: flex;
  gap: 10px;
}

.dot {
  width: 8px;
  height: 8px;
  background: #43a047;
  border-radius: 50%;
  opacity: 0.4;
}

.dot:nth-child(2) {
  opacity: 0.6;
}

.dot:nth-child(3) {
  opacity: 0.8;
}

@media (max-width: 1200px) {
  .hero-content {
    flex-direction: column;
    text-align: center;
  }
  
  .hero-text {
    margin-bottom: 40px;
  }
  
  .features-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .promotion-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .hero-title {
    font-size: 32px;
  }
  
  .hero-stats {
    flex-direction: column;
    gap: 20px;
  }
  
  .stat-divider {
    width: 50px;
    height: 1px;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .promotion-grid {
    grid-template-columns: 1fr;
  }
}
</style>