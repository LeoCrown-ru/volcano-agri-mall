<template>
  <div class="product-detail-page">
    <div class="detail-container" v-loading="loading">
      <div class="product-gallery">
        <div class="gallery-main">
          <el-image
            :src="product.imageUrl"
            alt="商品图片"
            class="main-image"
            fit="cover"
            :preview-src-list="[product.imageUrl]"
          />
          <div class="image-overlay">
            <div class="overlay-content">
              <span class="overlay-icon">👆</span>
              <span class="overlay-text">点击查看大图</span>
            </div>
          </div>
        </div>
        <div class="gallery-thumbnails">
          <div 
            v-for="(img, index) in product.images" 
            :key="index"
            class="thumbnail-item"
            :class="{ active: currentImageIndex === index }"
            @click="currentImageIndex = index"
          >
            <img :src="img" alt="缩略图" />
          </div>
        </div>
      </div>

      <div class="product-info">
        <div class="info-header">
          <div v-if="product.discount" class="discount-tag">
            {{ product.discount }}折优惠
          </div>
          <div class="share-btn" @click="handleShare">
            <span class="share-icon">📤</span>
            <span>分享</span>
          </div>
        </div>
        
        <h1 class="product-title">{{ product.name }}</h1>
        <p class="product-description">{{ product.description }}</p>
        
        <div class="price-section">
          <div class="current-price-wrap">
            <span class="price-label">现价</span>
            <span class="current-price">¥{{ displayPrice }}</span>
          </div>
          <div v-if="displayOriginalPrice > 0" class="original-price-wrap">
            <span class="original-price">¥{{ displayOriginalPrice }}</span>
            <span class="save-amount">省 ¥{{ (displayOriginalPrice - displayPrice).toFixed(2) }}</span>
          </div>
        </div>

        <div class="stats-row">
          <div class="stat-item">
            <span class="stat-value">{{ product.viewCount }}</span>
            <span class="stat-label">浏览量</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ product.saleCount }}</span>
            <span class="stat-label">已售</span>
          </div>
          <div class="stat-divider"></div>
          <div class="stat-item">
            <span class="stat-value">{{ product.reviewCount }}</span>
            <span class="stat-label">评价</span>
          </div>
        </div>

        <div class="spec-section">
          <div class="section-title">
            <span class="title-icon">📦</span>
            规格选择
          </div>
          <div class="spec-options">
            <button 
              v-for="spec in product.specs" 
              :key="spec"
              class="spec-btn"
              :class="{ active: selectedSpec === spec }"
              @click="selectedSpec = spec"
            >
              {{ spec }}
            </button>
          </div>
        </div>

        <div class="quantity-section">
          <div class="section-title">
            <span class="title-icon">🔢</span>
            数量
          </div>
          <div class="quantity-control">
            <button class="qty-btn" @click="decreaseQty">-</button>
            <span class="qty-value">{{ quantity }}</span>
            <button class="qty-btn" @click="increaseQty">+</button>
          </div>
        </div>

        <div class="action-buttons">
          <button class="action-btn secondary" @click="handleViewTrace">
            <span class="btn-icon">🌾</span>
            <span>查看溯源</span>
          </button>
          <button class="action-btn secondary" @click="handleFavorite">
            <span class="btn-icon">{{ isFavorite ? '❤️' : '🤍' }}</span>
            <span>{{ isFavorite ? '已收藏' : '收藏' }}</span>
          </button>
          <button class="action-btn secondary" @click="handleAddToCart">
            <span class="btn-icon">🛒</span>
            <span>加入购物车</span>
          </button>
          <button class="action-btn primary" @click="handleBuyNow">
            <span class="btn-icon">💳</span>
            <span>立即购买</span>
          </button>
        </div>
      </div>
    </div>

    <div class="reviews-section">
      <div class="section-header">
        <h2 class="section-title">
          <span class="title-icon">⭐</span>
          用户评价
        </h2>
        <span class="review-count">共 {{ reviews.length }} 条评价</span>
      </div>
      
      <div class="reviews-list">
        <div v-for="review in reviews" :key="review.id" class="review-card">
          <div class="review-header">
            <div class="reviewer-info">
              <div class="reviewer-avatar">
                <span class="avatar-icon">👤</span>
              </div>
              <div class="reviewer-detail">
                <span class="reviewer-name">{{ review.userName }}</span>
                <span class="review-time">{{ formatDate(review.createTime) }}</span>
              </div>
            </div>
            <div class="review-rating">
              <span v-for="i in 5" :key="i" class="star">
                {{ i <= review.score ? '★' : '☆' }}
              </span>
            </div>
          </div>
          <p class="review-content">{{ review.content }}</p>
          <div v-if="review.imageUrl" class="review-images">
            <img :src="review.imageUrl" alt="评价图片" class="review-img" />
          </div>
        </div>
      </div>

      <div v-if="reviews.length === 0" class="empty-reviews">
        <span class="empty-icon">📝</span>
        <span class="empty-text">暂无评价，快来做第一个评价的人吧！</span>
      </div>
    </div>

    <!-- 溯源信息弹窗 -->
    <el-dialog title="溯源信息" :visible.sync="traceDialogOpen" width="560px">
      <div v-if="traceInfo" class="trace-info">
        <div class="trace-row">
          <span class="trace-label">商品名称</span>
          <span class="trace-value">{{ product.name }}</span>
        </div>
        <div class="trace-row">
          <span class="trace-label">产地</span>
          <span class="trace-value">{{ traceInfo.origin || '暂无' }}</span>
        </div>
        <div class="trace-row">
          <span class="trace-label">批次号</span>
          <span class="trace-value">{{ traceInfo.batchNo || '暂无' }}</span>
        </div>
        <div class="trace-row trace-block">
          <span class="trace-label">生长记录</span>
          <span class="trace-value">{{ traceInfo.growRecord || '暂无' }}</span>
        </div>
        <div class="trace-row trace-block">
          <span class="trace-label">加工信息</span>
          <span class="trace-value">{{ traceInfo.processInfo || '暂无' }}</span>
        </div>
      </div>
      <div v-else class="trace-empty">
        <span class="trace-empty-icon">🌾</span>
        <span class="trace-empty-text">该商品暂无溯源信息</span>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getProduct } from "@/api/cms/product";
import { getProductTraceByProductId } from "@/api/cms/productTrace";
import { listReview } from "@/api/ums/review";
import { addCollect, listFavorite } from "@/api/ums/favorite";
import { addProductInCart } from "@/api/ums/cart";
import { initOrder } from "@/api/oms/order";
import { getToken } from "@/utils/auth";

export default {
  name: "ProductDetail",
  data() {
    return {
      product: {
        id: "",
        name: "",
        description: "",
        price: 0,
        originalPrice: 0,
        discount: 0,
        imageUrl: "",
        images: [],
        specs: [],
        viewCount: 0,
        saleCount: 0,
        reviewCount: 0,
      },
      reviews: [],
      selectedSpec: "",
      quantity: 1,
      currentImageIndex: 0,
      isFavorite: false,
      loading: false,
      traceInfo: null,
      traceDialogOpen: false,
    };
  },
  computed: {
    // 促销后的实际显示价格
    displayPrice() {
      const p = Number(this.product.price) || 0;
      if (this.product.isDiscount === true) {
        // 折扣：现价 = 原价 × 折扣率
        return (p * (Number(this.product.discountNum) || 1)).toFixed(2);
      } else if (this.product.isDiscount === false && this.product.discountNum) {
        // 优惠：现价 = 原价 - 优惠金额（不低于 0）
        return Math.max(p - Number(this.product.discountNum), 0).toFixed(2);
      }
      return p.toFixed(2);
    },
    // 有促销时展示的原价（划线价）
    displayOriginalPrice() {
      if (this.product.isDiscount === true || this.product.isDiscount === false) {
        return (Number(this.product.price) || 0).toFixed(2);
      }
      return 0;
    },
  },
  created() {
    this.loadCurrentProduct();
  },
  watch: {
    '$route'(to, from) {
      if (to.params.id !== from.params.id) {
        this.loadCurrentProduct();
      }
    }
  },
  methods: {
    loadCurrentProduct() {
      const productId = this.$route.params.id;
      if (productId) {
        this.loadProduct(productId);
        this.loadReviews(productId);
        this.checkFavoriteStatus(productId);
      }
    },
    async loadProduct(productId) {
      this.loading = true;
      try {
        const response = await getProduct(productId);
        if (response.code === 0) {
          this.product = response.data;
          if (this.product.specs && this.product.specs.length > 0) {
            this.selectedSpec = this.product.specs[0];
          } else {
            this.product.specs = [];
          }
          if (!this.product.images || this.product.images.length === 0) {
            this.product.images = this.product.imageUrl ? [this.product.imageUrl] : [];
          }
        }
      } catch (error) {
        console.error("获取商品详情失败:", error);
      } finally {
        this.loading = false;
      }
    },
    async loadReviews(productId) {
      try {
        const response = await getProduct(productId);
        if (response.code === 0) {
          this.reviews = response.data.productReviewList || [];
        }
      } catch (error) {
        console.error("获取评价列表失败:", error);
      }
    },
    async checkFavoriteStatus(productId) {
      try {
        const response = await listFavorite();
        if (response.code === 0) {
          this.isFavorite = response.data.some(item => item.productId === productId);
        }
      } catch (error) {
        console.error("检查收藏状态失败:", error);
      }
    },
    async handleFavorite() {
      if (!getToken()) {
        this.$router.push("/login");
        return;
      }
      try {
        const response = await addCollect(this.product.id);
        if (response.code === 0) {
          this.isFavorite = !this.isFavorite;
          this.$message.success(this.isFavorite ? "收藏成功" : "取消收藏成功");
        }
      } catch (error) {
        console.error("收藏操作失败:", error);
      }
    },
    async handleAddToCart() {
      if (!getToken()) {
        this.$router.push("/login");
        return;
      }
      try {
        const params = {
          productId: this.product.id,
          quantity: this.quantity,
          spec: this.selectedSpec,
        };
        const response = await addProductInCart(params);
        if (response.code === 0) {
          this.$message.success("加入购物车成功");
        }
      } catch (error) {
        console.error("加入购物车失败:", error);
      }
    },
    async handleBuyNow() {
      if (!getToken()) {
        this.$router.push("/login");
        return;
      }
      if (!this.product || !this.product.id) {
        this.$message.error("商品信息加载失败，请刷新页面！");
        return;
      }
      try {
        // 传原价即可，后端会按促销统一重算（避免双重折扣）
        const price = Number(this.product.price) || 0;
        const orderData = {
          omsPlaceOrderProductItemList: [{
            productId: this.product.id,
            quantity: this.quantity,
            price: price,
          }],
          totalAmount: price * this.quantity,
          paymentMethod: "在线支付",
          userAddress: "",
          isCar: false,
        };
        const response = await initOrder(orderData);
        if (response && response.code === 0 && response.data && response.data.id) {
          const orderId = response.data.id;
          this.$router.push({
            name: "order-detail",
            params: {
              orderId: orderId,
            },
          });
        } else {
          this.$message.error("创建订单失败，请重试！");
        }
      } catch (error) {
        console.error("创建订单失败:", error);
        this.$message.error((error && error.message) || "创建订单失败，请重试！");
      }
    },
    async handleViewTrace() {
      if (!this.product || !this.product.id) {
        this.$message.error("商品信息加载失败，请刷新页面！");
        return;
      }
      try {
        const response = await getProductTraceByProductId(this.product.id);
        if (response.code === 0) {
          this.traceInfo = response.data;
        } else {
          this.traceInfo = null;
        }
        this.traceDialogOpen = true;
      } catch (error) {
        console.error("查询溯源信息失败:", error);
        this.traceInfo = null;
        this.traceDialogOpen = true;
      }
    },
    handleShare() {
      this.$message.info("分享功能开发中");
    },
    increaseQty() {
      if (this.quantity < 10) {
        this.quantity++;
      }
    },
    decreaseQty() {
      if (this.quantity > 1) {
        this.quantity--;
      }
    },
    formatDate(date) {
      return new Date(date).toLocaleString();
    },
  },
};
</script>

<style scoped>
.product-detail-page {
  min-height: 100vh;
  padding: 30px 40px;
}

.detail-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  gap: 50px;
  margin-bottom: 50px;
}

.product-gallery {
  flex: 1;
}

.gallery-main {
  position: relative;
  width: 100%;
  height: 500px;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 10px 40px rgba(67, 160, 71, 0.15);
  margin-bottom: 20px;
}

.main-image {
  width: 100%;
  height: 100%;
  display: block;
  cursor: zoom-in;
  transition: transform 0.6s ease;
}

.gallery-main:hover .main-image {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s ease;
  pointer-events: none;
}

.gallery-main:hover .image-overlay {
  background: rgba(67, 160, 71, 0.1);
}

.overlay-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.gallery-main:hover .overlay-content {
  opacity: 1;
}

.overlay-icon {
  font-size: 32px;
}

.overlay-text {
  font-size: 14px;
  color: #ffffff;
  background: rgba(67, 160, 71, 0.8);
  padding: 8px 16px;
  border-radius: 20px;
}

.gallery-thumbnails {
  display: flex;
  gap: 12px;
}

.thumbnail-item {
  width: 80px;
  height: 80px;
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  border: 3px solid transparent;
  transition: all 0.3s ease;
}

.thumbnail-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.thumbnail-item.active {
  border-color: #43a047;
  box-shadow: 0 4px 15px rgba(67, 160, 71, 0.3);
}

.thumbnail-item:hover {
  border-color: rgba(67, 160, 71, 0.5);
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.discount-tag {
  padding: 8px 20px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  color: #ffffff;
  font-size: 13px;
  font-weight: 600;
  border-radius: 20px;
}

.share-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 16px;
  background: rgba(67, 160, 71, 0.1);
  border-radius: 20px;
  cursor: pointer;
  font-size: 13px;
  color: #43a047;
  transition: all 0.3s ease;
}

.share-btn:hover {
  background: rgba(67, 160, 71, 0.15);
}

.share-icon {
  font-size: 14px;
}

.product-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
  margin-bottom: 12px;
  line-height: 1.3;
}

.product-description {
  font-size: 15px;
  color: #8a8a9a;
  line-height: 1.7;
  margin-bottom: 25px;
}

.price-section {
  background: linear-gradient(135deg, rgba(67, 160, 71, 0.08) 0%, rgba(102, 187, 106, 0.05) 100%);
  padding: 25px;
  border-radius: 20px;
  margin-bottom: 25px;
}

.current-price-wrap {
  display: flex;
  align-items: baseline;
  gap: 10px;
  margin-bottom: 10px;
}

.price-label {
  font-size: 14px;
  color: #8a8a9a;
}

.current-price {
  font-size: 42px;
  font-weight: 700;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.original-price-wrap {
  display: flex;
  align-items: center;
  gap: 12px;
}

.original-price {
  font-size: 16px;
  color: #8a8a9a;
  text-decoration: line-through;
}

.save-amount {
  font-size: 14px;
  color: #66bb6a;
  font-weight: 600;
  padding: 4px 12px;
  background: rgba(102, 187, 106, 0.1);
  border-radius: 12px;
}

.stats-row {
  display: flex;
  align-items: center;
  gap: 30px;
  margin-bottom: 25px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #1a1a2e;
}

.stat-label {
  font-size: 12px;
  color: #8a8a9a;
  margin-top: 4px;
}

.stat-divider {
  width: 1px;
  height: 40px;
  background: rgba(67, 160, 71, 0.2);
}

.spec-section,
.quantity-section {
  margin-bottom: 25px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #4a4a68;
  margin-bottom: 15px;
}

.title-icon {
  margin-right: 8px;
}

.spec-options {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.spec-btn {
  padding: 12px 24px;
  background: #ffffff;
  border: 1.5px solid rgba(67, 160, 71, 0.2);
  border-radius: 12px;
  font-size: 14px;
  color: #4a4a68;
  cursor: pointer;
  transition: all 0.3s ease;
}

.spec-btn:hover {
  border-color: #43a047;
  color: #43a047;
}

.spec-btn.active {
  background: linear-gradient(135deg, rgba(67, 160, 71, 0.1) 0%, rgba(102, 187, 106, 0.08) 100%);
  border-color: #43a047;
  color: #43a047;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 20px;
}

.qty-btn {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: rgba(67, 160, 71, 0.1);
  border: none;
  font-size: 20px;
  color: #43a047;
  cursor: pointer;
  transition: all 0.3s ease;
}

.qty-btn:hover {
  background: rgba(67, 160, 71, 0.2);
  transform: scale(1.1);
}

.qty-value {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a2e;
  min-width: 40px;
  text-align: center;
}

.action-buttons {
  display: flex;
  gap: 15px;
  margin-top: auto;
}

.action-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 16px;
  border-radius: 16px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.action-btn.secondary {
  background: #ffffff;
  border: 1.5px solid rgba(67, 160, 71, 0.2);
  color: #4a4a68;
}

.action-btn.secondary:hover {
  border-color: #43a047;
  color: #43a047;
  box-shadow: 0 4px 15px rgba(67, 160, 71, 0.15);
}

.action-btn.primary {
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  color: #ffffff;
  box-shadow: 0 6px 20px rgba(67, 160, 71, 0.35);
}

.action-btn.primary::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s ease;
}

.action-btn.primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(67, 160, 71, 0.45);
}

.action-btn.primary:hover::before {
  left: 100%;
}

.btn-icon {
  font-size: 16px;
}

.reviews-section {
  max-width: 900px;
  margin: 0 auto;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 30px;
}

.review-count {
  font-size: 14px;
  color: #8a8a9a;
}

.reviews-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.review-card {
  background: #ffffff;
  padding: 25px;
  border-radius: 20px;
  box-shadow: 0 4px 15px rgba(67, 160, 71, 0.06);
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.reviewer-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.reviewer-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(67, 160, 71, 0.1) 0%, rgba(102, 187, 106, 0.08) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.avatar-icon {
  font-size: 20px;
}

.reviewer-detail {
  display: flex;
  flex-direction: column;
}

.reviewer-name {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a2e;
}

.review-time {
  font-size: 12px;
  color: #8a8a9a;
}

.review-rating {
  font-size: 16px;
}

.star {
  color: #ffc107;
}

.review-content {
  font-size: 15px;
  color: #4a4a68;
  line-height: 1.7;
  margin-bottom: 15px;
}

.review-images {
  display: flex;
  gap: 12px;
}

.review-img {
  width: 120px;
  height: 120px;
  object-fit: cover;
  border-radius: 12px;
}

.empty-reviews {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 60px;
  background: #ffffff;
  border-radius: 20px;
  box-shadow: 0 4px 15px rgba(67, 160, 71, 0.06);
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.empty-text {
  font-size: 15px;
  color: #8a8a9a;
}

.trace-info {
  padding: 10px 0;
}

.trace-row {
  display: flex;
  padding: 12px 0;
  border-bottom: 1px dashed rgba(67, 160, 71, 0.15);
}

.trace-row:last-child {
  border-bottom: none;
}

.trace-label {
  width: 90px;
  flex-shrink: 0;
  font-size: 14px;
  font-weight: 600;
  color: #4a4a68;
}

.trace-value {
  flex: 1;
  font-size: 14px;
  color: #1a1a2e;
  line-height: 1.7;
}

.trace-block .trace-value {
  white-space: pre-wrap;
}

.trace-empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 0;
}

.trace-empty-icon {
  font-size: 48px;
  margin-bottom: 15px;
}

.trace-empty-text {
  font-size: 15px;
  color: #8a8a9a;
}

@media (max-width: 900px) {
  .detail-container {
    flex-direction: column;
  }
  
  .gallery-main {
    height: 400px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
  
  .action-btn {
    padding: 14px;
  }
}
</style>