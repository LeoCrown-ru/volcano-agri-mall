<template>
  <div class="product-page">
    <div class="search-header">
      <div class="search-wrapper">
        <div class="search-box">
          <input 
            type="text" 
            v-model="searchText" 
            placeholder="搜索商品名称或描述..." 
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <button class="search-btn" @click="handleSearch">
            <span class="search-icon">🔍</span>
          </button>
        </div>
        <div class="filter-btns">
          <button 
            class="filter-btn" 
            :class="{ active: sortType === 'hot' }"
            @click="sortType = 'hot'; handleSearch()"
          >
            <span>🔥</span> 热门
          </button>
          <button 
            class="filter-btn" 
            :class="{ active: sortType === 'price_asc' }"
            @click="sortType = 'price_asc'; handleSearch()"
          >
            <span>⬆️</span> 价格升序
          </button>
          <button 
            class="filter-btn" 
            :class="{ active: sortType === 'price_desc' }"
            @click="sortType = 'price_desc'; handleSearch()"
          >
            <span>⬇️</span> 价格降序
          </button>
        </div>
      </div>
    </div>

    <div class="main-content">
      <aside class="category-sidebar">
        <div class="sidebar-title">
          <span class="title-icon">📁</span>
          商品分类
        </div>
        <ul class="category-list">
          <li 
            class="category-item" 
            :class="{ active: selectedCategory === '' }"
            @click="selectedCategory = ''; handleSearch()"
          >
            <span class="item-icon">🏠</span>
            全部商品
          </li>
          <li 
            v-for="category in categories" 
            :key="category.id"
            class="category-item"
            :class="{ active: selectedCategory === category.id }"
            @click="selectedCategory = category.id; handleSearch()"
          >
            <span class="item-icon">{{ category.icon }}</span>
            {{ category.name }}
            <ul v-if="category.children && category.children.length" class="category-sub-list" @click.stop>
              <li 
                v-for="child in category.children" 
                :key="child.id"
                class="category-sub-item"
                :class="{ active: selectedCategory === child.id }"
                @click="selectedCategory = child.id; handleSearch()"
              >
                {{ child.name }}
              </li>
            </ul>
          </li>
        </ul>
        
        <div class="sidebar-decoration">
          <div class="deco-circle deco-1"></div>
          <div class="deco-circle deco-2"></div>
        </div>
      </aside>

      <main class="product-grid">
        <div class="grid-header">
          <span class="result-count">
            共找到 <span class="count-number">{{ total }}</span> 件商品
          </span>
        </div>
        
        <div class="products-wrapper">
          <div 
            v-for="product in displayList" 
            :key="product.id"
            class="product-card"
            @click="goToDetail(product.id)"
          >
            <div class="product-image-wrapper">
              <img :src="product.imageUrl" alt="商品图片" class="product-image" />
              <div class="image-overlay"></div>
              <div v-if="product.discount" class="discount-badge">
                {{ product.discount }}折
              </div>
            </div>
            <div class="product-info">
              <h3 class="product-name">{{ product.name }}</h3>
              <p class="product-desc">{{ product.description }}</p>
              <div class="product-bottom">
                <div class="price-section">
                  <span class="current-price">¥{{ product.price }}</span>
                  <span v-if="product.originalPrice" class="original-price">
                    ¥{{ product.originalPrice }}
                  </span>
                </div>
                <div class="sales-count">
                  <span class="sales-icon">👁️</span>
                  {{ product.viewCount }}
                </div>
              </div>
            </div>
          </div>
        </div>

        <div v-if="total > pageSize" class="pagination-wrapper">
          <el-pagination
            :current-page="currentPage"
            :page-size="pageSize"
            :total="total"
            @current-change="handlePageChange"
            background
            layout="prev, pager, next, jumper"
          />
        </div>
      </main>
    </div>
  </div>
</template>

<script>
import { searchProduct } from "@/api/cms/product";
import { listCategory } from "@/api/cms/category";

const categoryIconMap = {
  "300101": "🍚", // 石板大米
  "300102": "🌾", // 杂粮谷物
  "300103": "🍄", // 菌菇山珍
  "300104": "🍯", // 蜂产品
  "300105": "🌰", // 山货坚果
};

export default {
  name: "Product",
  data() {
    return {
      searchText: "",
      sortType: "hot",
      selectedCategory: "",
      currentPage: 1,
      pageSize: 9,
      total: 0,
      productList: [],
      allProducts: [],
      displayList: [],
      categories: [],
    };
  },
  created() {
    this.fetchCategories();
    this.fetchProducts();
  },
  methods: {
    async fetchProducts() {
      try {
        const params = {
          page: this.currentPage,
          pageSize: this.pageSize,
          searchContent: this.searchText,
          categoryId: this.selectedCategory,
          sort: this.sortType,
        };
        const response = await searchProduct(params);
        if (response.code === 0) {
          let list = [];
          if (Array.isArray(response.data)) {
            list = response.data;
          } else if (response.data.list) {
            list = response.data.list;
          }
          this.allProducts = list;
          this.total = list.length;
          this.sliceProducts();
        }
      } catch (error) {
        console.error("获取商品列表失败:", error);
      }
    },
    async fetchCategories() {
      try {
        const response = await listCategory();
        if (response.code === 0) {
          const treeData = response.data;
          this.categories = treeData.map((cat, index) => ({
            id: cat.id,
            name: cat.name,
            icon: categoryIconMap[cat.id] || "🌿",
            children: (cat.children || []).map(child => ({
              id: child.id,
              name: child.name,
            })),
          }));
        }
      } catch (error) {
        console.error("获取分类列表失败:", error);
        this.categories = [
          { id: "1", name: "石板大米", icon: "🍚" },
          { id: "2", name: "杂粮谷物", icon: "🌾" },
          { id: "3", name: "菌菇山珍", icon: "🍄" },
          { id: "4", name: "蜂产品", icon: "🍯" },
          { id: "5", name: "山货坚果", icon: "🌰" },
        ];
      }
    },
    flattenTree(tree) {
      let result = [];
      for (const item of tree) {
        result.push(item);
        if (item.children && item.children.length > 0) {
          result = result.concat(this.flattenTree(item.children));
        }
      }
      return result;
    },
    handleSearch() {
      this.currentPage = 1;
      this.fetchProducts();
    },
    handlePageChange(page) {
      this.currentPage = page;
      this.sliceProducts();
    },
    sliceProducts() {
      const start = (this.currentPage - 1) * this.pageSize;
      this.displayList = this.allProducts.slice(start, start + this.pageSize);
    },
    goToDetail(productId) {
      this.$router.push({ name: "ProductDetail", params: { id: productId } });
    },
  },
};
</script>

<style scoped>
.product-page {
  min-height: 100vh;
  padding: 20px 40px;
}

.search-header {
  max-width: 1400px;
  margin: 0 auto 30px;
}

.search-wrapper {
  display: flex;
  align-items: center;
  gap: 20px;
}

.search-box {
  flex: 1;
  display: flex;
  background: #ffffff;
  border-radius: 30px;
  box-shadow: 0 4px 20px rgba(67, 160, 71, 0.08);
  overflow: hidden;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-box:focus-within {
  box-shadow: 0 6px 30px rgba(67, 160, 71, 0.15);
}

.search-input {
  flex: 1;
  padding: 16px 24px;
  font-size: 15px;
  border: none;
  background: transparent;
  color: #1a1a2e;
}

.search-input::placeholder {
  color: #8a8a9a;
}

.search-btn {
  padding: 16px 24px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  border: none;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.search-btn:hover {
  transform: scale(1.05);
}

.search-icon {
  font-size: 18px;
}

.filter-btns {
  display: flex;
  gap: 12px;
}

.filter-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 12px 20px;
  background: #ffffff;
  border: 1.5px solid rgba(67, 160, 71, 0.2);
  border-radius: 25px;
  font-size: 14px;
  color: #4a4a68;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
}

.filter-btn:hover {
  border-color: #43a047;
  color: #43a047;
}

.filter-btn.active {
  background: linear-gradient(135deg, rgba(67, 160, 71, 0.1) 0%, rgba(102, 187, 106, 0.08) 100%);
  border-color: #43a047;
  color: #43a047;
}

.main-content {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  gap: 30px;
}

.category-sidebar {
  width: 220px;
  background: #ffffff;
  border-radius: 24px;
  padding: 25px;
  box-shadow: 0 4px 20px rgba(67, 160, 71, 0.08);
  position: sticky;
  top: 100px;
  height: fit-content;
}

.sidebar-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(67, 160, 71, 0.1);
}

.title-icon {
  margin-right: 8px;
}

.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 14px 16px;
  border-radius: 14px;
  cursor: pointer;
  font-size: 14px;
  color: #4a4a68;
  transition: all 0.3s ease;
  position: relative;
}

.category-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(180deg, #43a047 0%, #66bb6a 100%);
  border-radius: 0 4px 4px 0;
  transform: scaleY(0);
  transition: transform 0.3s ease;
}

.category-item:hover {
  background: rgba(67, 160, 71, 0.06);
  padding-left: 20px;
}

.category-item:hover::before {
  transform: scaleY(1);
}

.category-item.active {
  background: rgba(67, 160, 71, 0.1);
  color: #43a047;
}

.category-item.active::before {
  transform: scaleY(1);
}

.category-sub-list {
  list-style: none;
  padding: 0;
  margin: 0;
  flex-basis: 100%;
}

.category-sub-item {
  padding: 10px 12px 10px 20px;
  border-radius: 10px;
  cursor: pointer;
  font-size: 13px;
  color: #8a8a9a;
  transition: all 0.3s ease;
}

.category-sub-item:hover {
  background: rgba(67, 160, 71, 0.05);
  color: #43a047;
}

.category-sub-item.active {
  color: #43a047;
  font-weight: 600;
}

.item-icon {
  font-size: 16px;
}

.sidebar-decoration {
  position: absolute;
  top: 50%;
  right: -30px;
  transform: translateY(-50%);
  pointer-events: none;
}

.deco-circle {
  position: absolute;
  border-radius: 50%;
  opacity: 0.2;
}

.deco-1 {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
}

.deco-2 {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #66bb6a 0%, #a5d6a7 100%);
  top: 30px;
  right: -20px;
}

.product-grid {
  flex: 1;
}

.grid-header {
  margin-bottom: 20px;
}

.result-count {
  font-size: 14px;
  color: #8a8a9a;
}

.count-number {
  font-size: 18px;
  font-weight: 700;
  color: #43a047;
}

.products-wrapper {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.product-card {
  background: #ffffff;
  border-radius: 24px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 20px rgba(67, 160, 71, 0.08);
}

.product-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 15px 40px rgba(67, 160, 71, 0.18);
}

.product-image-wrapper {
  position: relative;
  width: 100%;
  height: 280px;
  overflow: hidden;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease;
}

.product-card:hover .product-image {
  transform: scale(1.1);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(180deg, transparent 60%, rgba(67, 160, 71, 0.1) 100%);
}

.discount-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  padding: 6px 14px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  color: #ffffff;
  font-size: 12px;
  font-weight: 600;
  border-radius: 20px;
  z-index: 2;
}

.product-info {
  padding: 20px;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a2e;
  margin-bottom: 8px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-desc {
  font-size: 13px;
  color: #8a8a9a;
  margin-bottom: 15px;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-section {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.current-price {
  font-size: 22px;
  font-weight: 700;
  color: #43a047;
}

.original-price {
  font-size: 14px;
  color: #8a8a9a;
  text-decoration: line-through;
}

.sales-count {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: #8a8a9a;
}

.sales-icon {
  font-size: 12px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 40px;
}

:deep(.el-pagination) {
  padding: 15px 25px;
  background: #ffffff;
  border-radius: 30px;
  box-shadow: 0 4px 20px rgba(67, 160, 71, 0.08);
}

:deep(.el-pager li) {
  margin: 0 4px;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  line-height: 40px;
}

:deep(.el-pager li.is-active) {
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  color: #ffffff;
}

:deep(.el-pagination button) {
  color: #8a8a9a;
}

:deep(.el-pagination button:hover) {
  color: #43a047;
}

@media (max-width: 1200px) {
  .products-wrapper {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .category-sidebar {
    width: 200px;
  }
}

@media (max-width: 768px) {
  .main-content {
    flex-direction: column;
  }
  
  .category-sidebar {
    width: 100%;
    position: static;
  }
  
  .products-wrapper {
    grid-template-columns: 1fr;
  }
  
  .search-wrapper {
    flex-direction: column;
  }
  
  .search-box {
    width: 100%;
  }
}
</style>