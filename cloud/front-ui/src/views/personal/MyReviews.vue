<template>
  <div class="review-container">
    <h3 class="page-title">评价管理</h3>
    <el-tabs v-model="activeTab">
      <el-tab-pane label="待评价" name="notReviewed">
        <el-card v-for="(order, index) in notReviewList" :key="index" class="order-card">
          <div class="order-header">
            <span>订单号：{{ order.orderId }}</span>
          </div>
          <el-table :data="order.cmsProductList" style="width: 100%" v-loading="loading">
            <el-table-column label="商品图片">
              <template #default="scope">
                <img :src="scope.row.imageUrl" alt="商品图片" class="product-image" @click="goToDetail(scope.row.id)" />
              </template>
            </el-table-column>
            <el-table-column label="商品名称">
              <template #default="scope">
                <span class="product-name" @click="goToDetail(scope.row.id)">{{ scope.row.name }}</span>
              </template>
            </el-table-column>
            <el-table-column label="商品价格" prop="price"></el-table-column>
            <el-table-column label="操作">
              <template #default="scope">
                <el-button type="primary" size="small" @click="openReviewDialog(scope.row, order.orderId)">评价</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>
      <el-tab-pane label="我的评价" name="myReviews">
        <el-table :data="reviews" style="width: 100%" v-loading="loading">
          <el-table-column label="序号" align="center" width="100">
            <template #default="scope">{{ scope.$index + 1 }}</template>
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
          <el-table-column prop="content" label="评价内容" />
          <el-table-column prop="rating" label="评分" />
          <el-table-column prop="createTime" label="评价时间" />
          <el-table-column label="操作">
            <template #default="scope">
              <el-button type="text" @click="deleteReview(scope.row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
    </el-tabs>

    <!-- 评价弹框 -->
    <el-dialog title="商品评价" :visible.sync="reviewDialogVisible" width="30%">
      <el-form :model="reviewForm" label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="reviewForm.rating" :max="5" show-text></el-rate>
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input type="textarea" :rows="4" v-model="reviewForm.content" placeholder="请输入评价内容"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="reviewDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitReview">提交</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { listNotReview, listReview, addReview, delReview } from "@/api/ums/review";

export default {
  data() {
    return {
      activeTab: "notReviewed",
      notReviewList: [],
      reviews: [],
      reviewDialogVisible: false,
      reviewForm: {
        productId: "",
        orderId: "",
        content: "",
        rating: 0,
      },
      loading: false,
    };
  },
  created() {
    this.fetchNotReviewList();
    this.fetchReviews();
  },
  methods: {
    async fetchNotReviewList() {
      this.loading = true;
      try {
        const response = await listNotReview();
        this.notReviewList = response.rows;
      } catch (error) {
        console.error("获取待评价列表失败:", error);
      } finally {
        this.loading = false;
      }
    },
    async fetchReviews() {
      this.loading = true;
      try {
        const response = await listReview();
        this.reviews = response.rows;
      } catch (error) {
        console.error("获取评价列表失败:", error);
      } finally {
        this.loading = false;
      }
    },
    openReviewDialog(product, orderId) {
      this.reviewForm.productId = product.id;
      this.reviewForm.orderId = orderId;
      this.reviewDialogVisible = true;
    },
    async submitReview() {
      try {
        await addReview(this.reviewForm);
        this.$message.success("评价提交成功！");
        this.reviewDialogVisible = false;
        this.fetchNotReviewList();
        this.fetchReviews();
      } catch (error) {
        this.$message.error("评价提交失败，请重试！");
      }
    },
    async deleteReview(item) {
      try {
        await delReview(item.id);
        this.$message.success("评价已删除！");
        this.fetchReviews();
      } catch (error) {
        this.$message.error("删除失败，请重试！");
      }
    },
    goToDetail(productId) {
      this.$router.push({ name: "ProductDetail", params: { id: productId } });
    },
  },
};
</script>

<style scoped>
.review-container {
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

.order-card {
  margin-bottom: 25px;
  border-radius: 20px;
  box-shadow: 0 4px 15px rgba(67, 160, 71, 0.08);
  background: #ffffff;
  border: 1px solid rgba(67, 160, 71, 0.1);
}

.order-header {
  padding: 15px 20px;
  border-bottom: 1px solid rgba(67, 160, 71, 0.1);
}

.order-header span {
  color: #666666;
  font-size: 14px;
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

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  border-color: #43a047;
  border-radius: 10px;
  padding: 8px 20px;
  font-weight: 600;
  box-shadow: 0 4px 15px rgba(67, 160, 71, 0.3);
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(67, 160, 71, 0.4);
}

:deep(.el-tabs__header) {
  margin-bottom: 30px;
}

:deep(.el-tabs__item) {
  color: #666666;
  font-size: 15px;
  font-weight: 500;
  padding: 0 30px;
}

:deep(.el-tabs__item.is-active) {
  color: #43a047;
}

:deep(.el-tabs__active-bar) {
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  height: 3px;
  border-radius: 3px;
}

:deep(.el-dialog) {
  background: #ffffff;
  border: 1px solid rgba(67, 160, 71, 0.1);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(67, 160, 71, 0.15);
}

:deep(.el-dialog__header) {
  border-bottom: 1px solid rgba(67, 160, 71, 0.1);
}

:deep(.el-dialog__title) {
  color: #333333;
  font-weight: 600;
}

:deep(.el-form-item__label) {
  color: #666666;
}

:deep(.el-textarea__inner) {
  background: #fafafa;
  border: 1.5px solid #e9ecef;
  border-radius: 12px;
  color: #333333;
}

:deep(.el-rate__icon) {
  color: rgba(67, 160, 71, 0.3);
}

:deep(.el-rate__icon.is-full) {
  color: #43a047;
}
</style>
