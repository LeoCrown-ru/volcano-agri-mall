<template>
  <div class="favorites">
    <h3 class="page-title">收藏记录</h3>
    <el-table :data="favorites" style="width: 100%" v-loading="loading">
      <el-table-column label="序号" align="center" width="100">
        <template slot-scope="scope">{{ scope.$index + 1 }}</template>
      </el-table-column>
      <el-table-column label="商品图片">
        <template #default="scope">
          <img :src="scope.row.productImage" alt="商品图片" class="product-image" @click="goToDetail(scope.row.productId)" />
        </template>
      </el-table-column>
      <el-table-column label="商品名称">
        <template #default="scope">
          <span class="product-name" @click="goToDetail(scope.row.productId)">{{ scope.row.productName }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="收藏时间" />
      <el-table-column label="操作">
        <template #default="scope">
          <el-button type="text" @click="removeFavorite(scope.row)">取消收藏</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { listFavorite, delFavorite } from "@/api/ums/favorite";

export default {
  data() {
    return {
      favorites: [], // 收藏记录列表
      loading: false, // 加载状态
    };
  },
  created() {
    this.fetchFavorites(); // 初始化时获取收藏记录
  },
  methods: {
    // 获取收藏记录
    async fetchFavorites() {
      this.loading = true; // 开始 loading
      try {
        const response = await listFavorite();
        this.favorites = response.rows; // 设置收藏记录数据
      } catch (error) {
        console.error("获取收藏记录失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
    // 取消收藏
    async removeFavorite(item) {
      this.loading = true; // 开始 loading
      try {
        await delFavorite(item.id); // 调用取消收藏接口
        this.$message.success("已取消收藏！"); // 提示成功
        this.fetchFavorites(); // 刷新收藏记录列表
      } catch (error) {
        console.error("取消收藏失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
    // 跳转到商品详情页
    goToDetail(productId) {
      this.$router.push({ name: "ProductDetail", params: { id: productId } });
    },
  },
};
</script>

<style scoped>
.favorites {
  max-width: 1300px;
  margin: 30px auto;
  padding: 35px;
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 8px 30px rgba(67, 160, 71, 0.1);
}

.page-title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 30px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.product-image {
  width: 70px;
  height: 70px;
  object-fit: cover;
  border-radius: 12px;
  cursor: pointer;
  border: 2px solid rgba(67, 160, 71, 0.2);
  transition: transform 0.3s ease;
}

.product-image:hover {
  transform: scale(1.1);
}

.product-name {
  cursor: pointer;
  color: #43a047;
  font-weight: 600;
  font-size: 14px;
  transition: color 0.3s ease;
}

.product-name:hover {
  color: #66bb6a;
}

:deep(.el-table) {
  background: #ffffff;
  border-radius: 16px;
  border: 1px solid rgba(67, 160, 71, 0.1);
}

:deep(.el-table__header-wrapper) {
  background: rgba(67, 160, 71, 0.1);
  border-radius: 16px 16px 0 0;
}

:deep(.el-table__header-wrapper th) {
  color: #666666;
  font-weight: 600;
  font-size: 14px;
}

:deep(.el-table__body tr) {
  color: #333333;
}

:deep(.el-table__body tr:hover) {
  background: rgba(67, 160, 71, 0.05);
}

:deep(.el-table__cell) {
  border-bottom: 1px solid rgba(67, 160, 71, 0.1);
}

:deep(.el-button--text) {
  color: #43a047;
  font-weight: 600;
  font-size: 14px;
}

:deep(.el-button--text:hover) {
  color: #66bb6a;
}
</style>
