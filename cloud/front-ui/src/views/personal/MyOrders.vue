<template>
  <div class="my-orders">
    <h3 class="title">我的订单</h3>
    <div class="toolbar">
      <el-button
        type="danger"
        plain
        size="mini"
        icon="el-icon-delete"
        :disabled="selectedOrders.length === 0"
        @click="handleDeleteSelected"
      >删除选中（{{ selectedOrders.length }}）</el-button>
      <span class="toolbar-tip">勾选订单后可批量删除</span>
    </div>
    <el-table :data="orders" style="width: 100%" v-loading="loading" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="序号" align="center" width="100">
        <template slot-scope="scope">
          {{ scope.$index + 1 }}
        </template>
      </el-table-column>
      <el-table-column label="总价">
        <template #default="scope">
          ￥{{ scope.row.totalAmount }}
        </template>
      </el-table-column>
      <el-table-column label="支付方式" prop="paymentMethod" />
      <el-table-column label="订单状态">
        <template #default="scope">
          <el-tag :type="getOrderStatusTagType(scope.row.orderStatus)">
            {{ getOrderStatusText(scope.row.orderStatus) }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" />
      <el-table-column label="操作" width="180" align="center">
        <template #default="scope">
          <el-button type="text" @click="goToOrderDetail(scope.row.id)">查看详情</el-button>
          <el-button
            type="text"
            style="color: #f56c6c;"
            @click="handleDelete([scope.row.id])"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script>
import { listOrder, delOrder } from "@/api/oms/order";

export default {
  data() {
    return {
      orders: [], // 订单列表
      loading: false, // 加载状态
      selectedOrders: [], // 选中的订单
    };
  },
  created() {
    this.fetchOrders();
  },
  methods: {
    // 获取订单列表
    async fetchOrders() {
      this.loading = true; // 开始 loading
      try {
        const response = await listOrder();
        this.orders = response.data;
      } catch (error) {
        console.error("获取订单列表失败:", error);
      } finally {
        this.loading = false; // 结束 loading
      }
    },
    // 跳转到订单详情页面
    goToOrderDetail(orderId) {
      this.$router.push({
        path: "/order-detail/" + orderId,
      });
    },
    // 多选变化
    handleSelectionChange(selection) {
      this.selectedOrders = selection;
    },
    // 删除选中
    handleDeleteSelected() {
      if (this.selectedOrders.length === 0) {
        this.$message.warning("请先勾选要删除的订单");
        return;
      }
      const ids = this.selectedOrders.map((item) => item.id);
      this.handleDelete(ids);
    },
    // 删除订单（支持单个/批量）
    handleDelete(ids) {
      const idList = Array.isArray(ids) ? ids : [ids];
      this.$confirm(`确认删除选中的 ${idList.length} 个订单吗？删除后不可恢复`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning",
      })
        .then(() => {
          return delOrder(idList.join(","));
        })
        .then((res) => {
          if (res.code === 0) {
            this.$message.success("删除成功");
            this.fetchOrders();
          } else {
            this.$message.error(res.msg || "删除失败");
          }
        })
        .catch(() => {});
    },
    // 获取订单状态中文描述
    getOrderStatusText(status) {
      switch (status) {
        case 0:
          return "待支付";
        case 1:
          return "已支付";
        case 2:
          return "已取消";
        case 3:
          return "已完成";
        case 4:
          return "已退款";
        default:
          return "未知状态";
      }
    },
    // 获取订单状态对应的标签类型
    getOrderStatusTagType(status) {
      switch (status) {
        case 0:
          return "warning"; // 待支付
        case 1:
          return "success"; // 已支付
        case 2:
          return "info"; // 已取消
        case 3:
          return ""; // 已完成
        case 4:
          return "danger"; // 已退款
        default:
          return "info"; // 未知状态
      }
    },
  },
};
</script>

<style scoped>
.my-orders {
  max-width: 1300px;
  margin: 30px auto;
  padding: 35px;
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 8px 30px rgba(67, 160, 71, 0.1);
}

.title {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 30px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 20px;
}

.toolbar-tip {
  font-size: 13px;
  color: #8a8a9a;
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

:deep(.el-tag) {
  font-size: 13px;
  padding: 6px 14px;
  border-radius: 20px;
  background: rgba(67, 160, 71, 0.15);
  border: 1px solid rgba(67, 160, 71, 0.3);
  color: #43a047;
}

:deep(.el-tag.el-tag--warning) {
  background: rgba(255, 193, 7, 0.15);
  border-color: rgba(255, 193, 7, 0.4);
  color: #ffc107;
}

:deep(.el-tag.el-tag--success) {
  background: rgba(76, 175, 80, 0.15);
  border-color: rgba(76, 175, 80, 0.4);
  color: #66bb6a;
}

:deep(.el-tag.el-tag--info) {
  background: rgba(33, 150, 243, 0.15);
  border-color: rgba(33, 150, 243, 0.4);
  color: #64b5f6;
}

:deep(.el-tag.el-tag--danger) {
  background: rgba(244, 67, 54, 0.15);
  border-color: rgba(244, 67, 54, 0.4);
  color: #ef5350;
}

:deep(.el-button--text) {
  color: #43a047;
  font-weight: 600;
  font-size: 14px;
}
</style>
