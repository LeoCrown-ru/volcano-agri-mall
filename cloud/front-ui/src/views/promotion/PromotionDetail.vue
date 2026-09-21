<template>
  <div class="promotion-detail">
    <!-- 活动信息 -->
    <div class="promotion-info">
      <h2 class="text-center">{{ promotion.name }}</h2>
      <div class="promotion-type text-center">
        {{ promotion.type === 0 ? `折扣: ${promotion.discount}折` : `优惠: ¥${promotion.couponValue}` }}
      </div>
      <div class="promotion-time text-center">
        {{ formatDate(promotion.startTime) }} - {{ formatDate(promotion.endTime) }}
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="product-list">
      <h3 class="text-center">活动商品</h3>
      <el-row :gutter="20">
        <el-col v-for="product in promotion.productList" :key="product.id" :span="12">
          <div @click="goProduct(product.id)">
            <el-card class="product-card">
              <img :src="product.imageUrl" class="product-image" alt="商品图片" />
              <div class="product-name">{{ product.name }}</div>
              <div class="product-price">
                <span class="original-price">¥{{ product.price }}</span>
                <span v-if="promotion.type === 0" class="discounted-price">
                ¥{{ (product.price * promotion.discount).toFixed(2) }}
              </span>
                <span v-else class="discounted-price">
                ¥{{ (product.price - promotion.couponValue).toFixed(2) }}
              </span>
              </div>
            </el-card>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { getPromotion } from "@/api/cms/promotion";

export default {
  name: "PromotionDetail",
  data() {
    return {
      promotion: {
        id: "",
        name: "",
        type: 0,
        discount: 0,
        couponValue: 0,
        startTime: "",
        endTime: "",
        productList: [],
      },
    };
  },
  created() {
    this.loadPromotionDetail();
  },
  methods: {
    /** 加载活动详情 */
    async loadPromotionDetail() {
      const promotionId = this.$route.params.id;
      const response = await getPromotion(promotionId);
      if (response.code === 0) {
        this.promotion = response.data;
      }
    },
    /** 格式化日期 */
    formatDate(date) {
      return new Date(date).toLocaleDateString();
    },
    goProduct(productId){
      this.$router.push({ name: "ProductDetail", params: { id: productId } });
    }
  },
};
</script>

<style scoped>
.promotion-detail {
  padding: 30px;
  max-width: 1300px;
  margin: 0 auto;
}

.promotion-info {
  position: relative;
  margin-bottom: 40px;
  padding: 45px;
  border-radius: 28px;
  border: 1px solid rgba(67, 160, 71, 0.15);
  background: linear-gradient(135deg, rgba(67, 160, 71, 0.08) 0%, rgba(102, 187, 106, 0.05) 100%);
  box-shadow: 0 8px 30px rgba(67, 160, 71, 0.1);
  overflow: hidden;
}

.promotion-info::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, rgba(67, 160, 71, 0.15) 0%, transparent 70%);
  border-radius: 50%;
}

.text-center {
  text-align: center;
}

.promotion-info h2 {
  position: relative;
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 20px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  z-index: 1;
}

.promotion-type {
  position: relative;
  color: #43a047;
  margin-top: 15px;
  font-size: 22px;
  font-weight: 600;
  z-index: 1;
}

.promotion-time {
  position: relative;
  color: #666666;
  margin-top: 15px;
  font-size: 15px;
  z-index: 1;
}

.product-list {
  margin-top: 30px;
}

.product-list h3 {
  font-size: 24px;
  font-weight: 700;
  text-align: center;
  margin-bottom: 30px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.product-card {
  text-align: center;
  background: #ffffff;
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid rgba(67, 160, 71, 0.1);
  transition: all 0.4s ease;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(67, 160, 71, 0.06);
}

.product-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 15px 40px rgba(67, 160, 71, 0.15);
  border-color: rgba(67, 160, 71, 0.3);
}

.product-image {
  width: 100%;
  height: 280px;
  object-fit: cover;
}

.product-name {
  font-size: 16px;
  margin-top: 20px;
  color: #333333;
  font-weight: 500;
}

.product-price {
  margin-top: 12px;
  padding-bottom: 20px;
}

.original-price {
  text-decoration: line-through;
  color: #999999;
  margin-right: 12px;
  font-size: 14px;
}

.discounted-price {
  color: #43a047;
  font-weight: 700;
  font-size: 22px;
}
</style>
