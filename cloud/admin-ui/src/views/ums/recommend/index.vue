<template>
  <div class="recommend-page">
    <div class="page-header">
      <h2 class="title">个性化推荐</h2>
      <p class="subtitle">根据您的订单、收藏和浏览记录为您推荐商品</p>
    </div>

    <div class="recommend-tabs">
      <el-tabs v-model="activeTab" type="card" @tab-click="handleTabClick">
        <el-tab-pane label="综合推荐" name="personal">
          <div class="recommend-content">
            <div v-if="personalRecommendations.length === 0" class="empty-state">
              <el-icon class="empty-icon" size="48">
                <component :is="Icons.Empty" />
              </el-icon>
              <p>暂无推荐商品</p>
            </div>
            <div v-else class="product-grid">
              <div
                v-for="productId in personalRecommendations"
                :key="productId"
                class="product-card"
              >
                <div class="product-image">
                  <img
                    :src="`https://picsum.photos/seed/${productId}/200/200`"
                    :alt="`商品${productId}`"
                  />
                </div>
                <div class="product-info">
                  <h3 class="product-name">推荐商品 {{ productId }}</h3>
                  <p class="product-desc">基于您的购物偏好推荐</p>
                  <div class="product-price">¥{{ (Math.random() * 1000).toFixed(2) }}</div>
                  <button class="add-cart-btn">加入购物车</button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="订单推荐" name="orders">
          <div class="recommend-content">
            <div v-if="orderRecommendations.length === 0" class="empty-state">
              <el-icon class="empty-icon" size="48">
                <component :is="Icons.ShoppingCart" />
              </el-icon>
              <p>暂无订单记录</p>
            </div>
            <div v-else class="product-grid">
              <div
                v-for="productId in orderRecommendations"
                :key="productId"
                class="product-card"
              >
                <div class="product-image">
                  <img
                    :src="`https://picsum.photos/seed/order${productId}/200/200`"
                    :alt="`商品${productId}`"
                  />
                </div>
                <div class="product-info">
                  <h3 class="product-name">订单推荐 {{ productId }}</h3>
                  <p class="product-desc">基于您的订单记录推荐</p>
                  <div class="product-price">¥{{ (Math.random() * 1000).toFixed(2) }}</div>
                  <button class="add-cart-btn">加入购物车</button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="收藏推荐" name="favorites">
          <div class="recommend-content">
            <div v-if="favoriteRecommendations.length === 0" class="empty-state">
              <el-icon class="empty-icon" size="48">
                <component :is="Icons.Heart" />
              </el-icon>
              <p>暂无收藏记录</p>
            </div>
            <div v-else class="product-grid">
              <div
                v-for="productId in favoriteRecommendations"
                :key="productId"
                class="product-card"
              >
                <div class="product-image">
                  <img
                    :src="`https://picsum.photos/seed/fav${productId}/200/200`"
                    :alt="`商品${productId}`"
                  />
                </div>
                <div class="product-info">
                  <h3 class="product-name">收藏推荐 {{ productId }}</h3>
                  <p class="product-desc">基于您的收藏记录推荐</p>
                  <div class="product-price">¥{{ (Math.random() * 1000).toFixed(2) }}</div>
                  <button class="add-cart-btn">加入购物车</button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>

        <el-tab-pane label="浏览推荐" name="history">
          <div class="recommend-content">
            <div v-if="historyRecommendations.length === 0" class="empty-state">
              <el-icon class="empty-icon" size="48">
                <component :is="Icons.History" />
              </el-icon>
              <p>暂无浏览记录</p>
            </div>
            <div v-else class="product-grid">
              <div
                v-for="productId in historyRecommendations"
                :key="productId"
                class="product-card"
              >
                <div class="product-image">
                  <img
                    :src="`https://picsum.photos/seed/hist${productId}/200/200`"
                    :alt="`商品${productId}`"
                  />
                </div>
                <div class="product-info">
                  <h3 class="product-name">浏览推荐 {{ productId }}</h3>
                  <p class="product-desc">基于您的浏览记录推荐</p>
                  <div class="product-price">¥{{ (Math.random() * 1000).toFixed(2) }}</div>
                  <button class="add-cart-btn">加入购物车</button>
                </div>
              </div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import * as recommendApi from '@/api/ums/recommend'
import { Empty, ShoppingCart, Heart, History } from '@element-plus/icons-vue'

const Icons = { Empty, ShoppingCart, Heart, History }

const activeTab = ref('personal')
const personalRecommendations = ref([])
const orderRecommendations = ref([])
const favoriteRecommendations = ref([])
const historyRecommendations = ref([])

const userId = ref(1)
const limit = ref(8)

const loadPersonalRecommendations = async () => {
  try {
    const response = await recommendApi.getPersonalRecommendations(userId.value, limit.value)
    if (response.code === 200) {
      personalRecommendations.value = response.data
    }
  } catch (error) {
    console.error('加载综合推荐失败:', error)
  }
}

const loadOrderRecommendations = async () => {
  try {
    const response = await recommendApi.getRecommendationsByOrders(userId.value, limit.value)
    if (response.code === 200) {
      orderRecommendations.value = response.data
    }
  } catch (error) {
    console.error('加载订单推荐失败:', error)
  }
}

const loadFavoriteRecommendations = async () => {
  try {
    const response = await recommendApi.getRecommendationsByFavorites(userId.value, limit.value)
    if (response.code === 200) {
      favoriteRecommendations.value = response.data
    }
  } catch (error) {
    console.error('加载收藏推荐失败:', error)
  }
}

const loadHistoryRecommendations = async () => {
  try {
    const response = await recommendApi.getRecommendationsByHistory(userId.value, limit.value)
    if (response.code === 200) {
      historyRecommendations.value = response.data
    }
  } catch (error) {
    console.error('加载浏览推荐失败:', error)
  }
}

const handleTabClick = (tab) => {
  switch (tab.name) {
    case 'personal':
      if (personalRecommendations.value.length === 0) {
        loadPersonalRecommendations()
      }
      break
    case 'orders':
      if (orderRecommendations.value.length === 0) {
        loadOrderRecommendations()
      }
      break
    case 'favorites':
      if (favoriteRecommendations.value.length === 0) {
        loadFavoriteRecommendations()
      }
      break
    case 'history':
      if (historyRecommendations.value.length === 0) {
        loadHistoryRecommendations()
      }
      break
  }
}

onMounted(() => {
  loadPersonalRecommendations()
})
</script>

<style scoped>
.recommend-page {
  padding: 20px;
  min-height: 100vh;
  background-color: #f8f9fa;
}

.page-header {
  margin-bottom: 30px;
}

.title {
  font-size: 28px;
  font-weight: bold;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.recommend-tabs {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
}

.recommend-content {
  padding: 20px;
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f3f4f6;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

.product-info {
  padding: 16px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 13px;
  color: #6b7280;
  margin: 0 0 12px 0;
}

.product-price {
  font-size: 20px;
  font-weight: bold;
  color: #dc2626;
  margin: 0 0 12px 0;
}

.add-cart-btn {
  width: 100%;
  padding: 10px;
  background: linear-gradient(135deg, #dc2626, #f97316);
  color: #fff;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: opacity 0.3s ease;
}

.add-cart-btn:hover {
  opacity: 0.9;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #9ca3af;
}

.empty-icon {
  margin-bottom: 16px;
  color: #d1d5db;
}
</style>