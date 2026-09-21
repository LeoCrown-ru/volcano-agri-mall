<template>
  <div class="cart-page">
    <div class="page-header">
      <h1 class="page-title">
        <span class="title-icon">🛒</span>
        我的购物车
      </h1>
      <div class="header-actions">
        <button class="action-btn" @click="handleClearAll">
          <span>🗑️</span> 清空购物车
        </button>
      </div>
    </div>

    <div class="cart-content">
      <div v-if="cartItems.length > 0" class="cart-list">
        <div 
          v-for="item in cartItems" 
          :key="item.id" 
          class="cart-item-card"
        >
          <div class="item-checkbox">
            <input 
              type="checkbox" 
              v-model="item.selected" 
              class="checkbox"
              @change="updateTotal"
            />
          </div>
          
          <div class="item-image" @click="goToDetail(item.productId)">
            <img :src="item.cmsProductList && item.cmsProductList[0] ? item.cmsProductList[0].imageUrl : ''" alt="商品图片" />
          </div>
          
          <div class="item-info">
            <h3 class="item-name">{{ item.cmsProductList && item.cmsProductList[0] ? item.cmsProductList[0].name : '' }}</h3>
            <p class="item-spec">{{ item.cmsProductList && item.cmsProductList[0] ? item.cmsProductList[0].spec : '' }}</p>
            <div class="item-bottom">
              <span class="item-price">¥{{ item.cmsProductList && item.cmsProductList[0] ? item.cmsProductList[0].price : 0 }}</span>
              <div class="quantity-control">
                <button class="qty-btn" @click="decreaseQty(item)">-</button>
                <span class="qty-value">{{ item.quantity }}</span>
                <button class="qty-btn" @click="increaseQty(item)">+</button>
              </div>
            </div>
          </div>
          
          <div class="item-actions">
            <button class="delete-btn" @click="handleDelete(item)">
              <span>🗑️</span>
            </button>
          </div>
        </div>
      </div>

      <div v-else class="empty-cart">
        <div class="empty-icon">🛒</div>
        <h3 class="empty-title">购物车是空的</h3>
        <p class="empty-desc">快去挑选心仪的商品吧！</p>
        <button class="empty-btn" @click="goToProductList">
          <span>🛍️</span> 去购物
        </button>
      </div>
    </div>

    <div v-if="cartItems.length > 0" class="cart-footer">
      <div class="footer-left">
        <label class="select-all">
          <input 
            type="checkbox" 
            :checked="allSelected" 
            @change="toggleSelectAll"
            class="checkbox"
          />
          <span>全选</span>
        </label>
        <span class="selected-count">已选 {{ selectedCount }} 件</span>
      </div>
      
      <div class="footer-right">
        <div class="total-section">
          <span class="total-label">合计：</span>
          <span class="total-price">¥{{ totalPrice.toFixed(2) }}</span>
        </div>
        <button class="checkout-btn" @click="handleCheckout">
          <span>💳</span> 去结算
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { listCart, removeProductInCart } from "@/api/ums/cart";

export default {
  name: "MyCart",
  data() {
    return {
      cartItems: [],
    };
  },
  computed: {
    allSelected() {
      return this.cartItems.length > 0 && this.cartItems.every(item => item.selected);
    },
    selectedCount() {
      return this.cartItems.filter(item => item.selected).length;
    },
    totalPrice() {
      return this.cartItems
        .filter(item => item.selected && item.cmsProductList && item.cmsProductList[0])
        .reduce((sum, item) => {
          const price = item.cmsProductList[0].price || 0;
          const quantity = item.quantity || 0;
          return sum + (price * quantity);
        }, 0);
    },
  },
  created() {
    this.loadCart();
  },
  methods: {
    async loadCart() {
      try {
        const response = await listCart();
        if (response.code === 0) {
          const cartList = response.data.rows || response.data;
          this.cartItems = cartList.map(item => ({
            ...item,
            selected: true,
          }));
        }
      } catch (error) {
        console.error("获取购物车失败:", error);
      }
    },
    async increaseQty(item) {
      if (item.quantity < 10) {
        item.quantity++;
      }
    },
    async decreaseQty(item) {
      if (item.quantity > 1) {
        item.quantity--;
      }
    },
    async handleDelete(item) {
      try {
        await removeProductInCart({ productId: item.productId });
        this.cartItems = this.cartItems.filter(i => i.id !== item.id);
        this.$message.success("删除成功");
      } catch (error) {
        console.error("删除购物车项失败:", error);
      }
    },
    async handleClearAll() {
      try {
        for (const item of this.cartItems) {
          await removeProductInCart({ productId: item.productId });
        }
        this.cartItems = [];
        this.$message.success("清空成功");
      } catch (error) {
        console.error("清空购物车失败:", error);
      }
    },
    toggleSelectAll(event) {
      const checked = event.target.checked;
      this.cartItems.forEach(item => {
        item.selected = checked;
      });
    },
    updateTotal() {
    },
    goToDetail(productId) {
      this.$router.push({ name: "ProductDetail", params: { id: productId } });
    },
    goToProductList() {
      this.$router.push("/product");
    },
    handleCheckout() {
      if (this.selectedCount === 0) {
        this.$message.warning("请选择要结算的商品");
        return;
      }
      this.$message.success("正在提交订单...");
      this.$router.push({ name: "my-orders" });
    },
  },
};
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  padding: 30px 40px;
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a1a2e;
}

.title-icon {
  margin-right: 12px;
}

.header-actions {
  display: flex;
  gap: 15px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: rgba(239, 83, 80, 0.1);
  border: none;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
  color: #ef5350;
  cursor: pointer;
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: rgba(239, 83, 80, 0.15);
}

.cart-content {
  flex: 1;
}

.cart-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.cart-item-card {
  display: flex;
  align-items: center;
  gap: 20px;
  background: #ffffff;
  padding: 25px;
  border-radius: 22px;
  box-shadow: 0 4px 20px rgba(67, 160, 71, 0.06);
  transition: all 0.3s ease;
}

.cart-item-card:hover {
  box-shadow: 0 8px 30px rgba(67, 160, 71, 0.12);
}

.item-checkbox {
  padding: 10px;
}

.checkbox {
  width: 20px;
  height: 20px;
  accent-color: #43a047;
  cursor: pointer;
}

.item-image {
  width: 100px;
  height: 100px;
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid rgba(67, 160, 71, 0.1);
}

.item-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.item-info {
  flex: 1;
}

.item-name {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 8px;
}

.item-spec {
  font-size: 13px;
  color: #8a8a9a;
  margin-bottom: 15px;
}

.item-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price {
  font-size: 22px;
  font-weight: 700;
  color: #43a047;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 15px;
}

.qty-btn {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: rgba(67, 160, 71, 0.1);
  border: none;
  font-size: 18px;
  color: #43a047;
  cursor: pointer;
  transition: all 0.3s ease;
}

.qty-btn:hover {
  background: rgba(67, 160, 71, 0.2);
  transform: scale(1.1);
}

.qty-value {
  font-size: 18px;
  font-weight: 600;
  color: #1a1a2e;
  min-width: 30px;
  text-align: center;
}

.item-actions {
  display: flex;
  gap: 10px;
}

.delete-btn {
  padding: 10px;
  background: rgba(239, 83, 80, 0.1);
  border: none;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.delete-btn:hover {
  background: rgba(239, 83, 80, 0.2);
  transform: scale(1.1);
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 80px;
  background: #ffffff;
  border-radius: 28px;
  box-shadow: 0 8px 30px rgba(67, 160, 71, 0.08);
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 20px;
}

.empty-title {
  font-size: 22px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 10px;
}

.empty-desc {
  font-size: 15px;
  color: #8a8a9a;
  margin-bottom: 30px;
}

.empty-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 14px 32px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  border: none;
  border-radius: 30px;
  font-size: 15px;
  font-weight: 600;
  color: #ffffff;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 6px 20px rgba(67, 160, 71, 0.35);
}

.empty-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(67, 160, 71, 0.45);
}

.cart-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 25px 30px;
  background: #ffffff;
  border-radius: 22px;
  box-shadow: 0 8px 30px rgba(67, 160, 71, 0.12);
  margin-top: 30px;
}

.footer-left {
  display: flex;
  align-items: center;
  gap: 20px;
}

.select-all {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #4a4a68;
  cursor: pointer;
}

.selected-count {
  font-size: 14px;
  color: #8a8a9a;
}

.footer-right {
  display: flex;
  align-items: center;
  gap: 25px;
}

.total-section {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.total-label {
  font-size: 15px;
  color: #8a8a9a;
}

.total-price {
  font-size: 28px;
  font-weight: 700;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.checkout-btn {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 16px 40px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  border: none;
  border-radius: 30px;
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 8px 25px rgba(67, 160, 71, 0.4);
  position: relative;
  overflow: hidden;
}

.checkout-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.6s ease;
}

.checkout-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 40px rgba(67, 160, 71, 0.5);
}

.checkout-btn:hover::before {
  left: 100%;
}

@media (max-width: 768px) {
  .cart-item-card {
    flex-direction: column;
    align-items: flex-start;
  }
  
  .item-bottom {
    width: 100%;
    margin-top: 15px;
  }
  
  .cart-footer {
    flex-direction: column;
    gap: 20px;
  }
  
  .footer-right {
    width: 100%;
    justify-content: space-between;
  }
}
</style>