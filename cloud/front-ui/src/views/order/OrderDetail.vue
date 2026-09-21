<template>
  <div class="order-detail">
    <h3>订单详情</h3>
    <el-form label-width="120px">
      <!-- 下单信息 -->
      <el-form-item label="下单用户">
        <el-input v-model="userName" disabled />
      </el-form-item>
      <el-form-item label="下单时间">
        <el-input v-model="createTime" disabled />
      </el-form-item>
      <el-form-item label="订单状态">
        <el-input v-model="orderStatusText" disabled />
      </el-form-item>

      <!-- 商品信息 -->
      <el-form-item label="商品信息">
        <el-table :data="orderItems" border>
          <el-table-column label="商品图片">
            <template #default="scope">
              <img :src="scope.row.cmsProduct.imageUrl" alt="商品图片" style="width: 50px; height: 50px; object-fit: cover;" />
            </template>
          </el-table-column>
          <el-table-column prop="cmsProduct.name" label="商品名称" />
          <el-table-column prop="quantity" label="数量" />
          <el-table-column prop="price" label="单价" />
          <el-table-column label="操作" width="80">
            <template #default="scope">
              <el-button v-if="!scope.row.isReview && (orderStatus === 1 || orderStatus === 3)" type="warning" size="mini" @click="openReviewDialog(scope.row)">评价</el-button>
              <span v-else-if="scope.row.isReview" style="color:#999;">已评价</span>
              <span v-else style="color:#bbb;">支付后可评价</span>
            </template>
          </el-table-column>
        </el-table>
      </el-form-item>

      <!-- 评价弹窗 -->
      <el-dialog title="写评价" :visible.sync="reviewDialogVisible" width="400px">
        <el-form :model="reviewForm" label-width="80px">
          <el-form-item label="评分">
            <el-rate v-model="reviewForm.rating" :max="5" show-text />
          </el-form-item>
          <el-form-item label="评价内容">
            <el-input type="textarea" :rows="4" v-model="reviewForm.content" placeholder="说说你的使用感受吧~" />
          </el-form-item>
        </el-form>
        <span slot="footer">
          <el-button @click="reviewDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReview">提交评价</el-button>
        </span>
      </el-dialog>

      <!-- 收货地址 -->
      <el-form-item label="收货地址">
        <el-button type="primary" @click="showAddressDialog">选择收货地址</el-button>
        <div v-if="selectedAddress" class="address-info">
          <p>联系人：{{ selectedAddress.phone }}</p>
          <p>地址：{{ selectedAddress.address }}</p>
        </div>
      </el-form-item>

      <!-- 总金额 -->
      <el-form-item label="总金额">
        <el-input v-model="totalAmount" disabled />
      </el-form-item>

      <!-- 支付按钮 -->
      <el-form-item v-if="orderStatus === 0">
        <el-button type="primary" @click="placeOrder">支付</el-button>
        <el-button type="danger" @click="cancelOrder">取消支付</el-button>
      </el-form-item>
    </el-form>

    <!-- 地址选择弹框 -->
    <el-dialog title="选择收货地址" :visible.sync="addressDialogVisible" width="30%">
      <el-table v-if="addressList.length > 0" :data="addressList" @row-click="handleAddressSelect">
        <el-table-column prop="phone" label="联系人" />
        <el-table-column prop="address" label="地址" />
      </el-table>
      <div v-else class="empty-address">
        <p style="text-align:center; color:#999; margin-bottom:15px;">暂无保存的地址，请手动输入</p>
      </div>
      <div style="margin-top:15px;">
        <el-input v-model="newAddress.phone" placeholder="联系人电话" style="margin-bottom:10px;" />
        <el-input v-model="newAddress.address" placeholder="请输入收货地址" type="textarea" :rows="3" />
      </div>
      <span slot="footer">
        <el-button @click="addressDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmAddress">确认</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getById } from "@/api/ums/user";
import { getOrder, placeOrder, cancelOrder } from "@/api/oms/order";
import { addReview } from "@/api/ums/review";

export default {
  data() {
    return {
      orderId: this.$route.params.orderId, // 从路由获取订单ID
      orderItems: [], // 订单商品项
      addressList: [], // 用户地址列表
      selectedAddress: null, // 选中的地址
      totalAmount: 0, // 总金额
      userName: "", // 下单用户名
      createTime: "", // 下单时间
      orderStatus: 0, // 订单状态
      addressDialogVisible: false, // 地址选择弹框是否可见
      newAddress: { phone: "", address: "" }, // 新地址输入
      reviewDialogVisible: false, // 评价弹窗
      reviewForm: { rating: 5, content: "", productId: "", orderId: "" }, // 评价表单
    };
  },
  computed: {
    // 订单状态文本
    orderStatusText() {
      const statusMap = {
        0: "待支付",
        1: "已支付",
        2: "已取消",
        3: "已完成",
        4: "已退款",
      };
      return statusMap[this.orderStatus] || "未知状态";
    },
  },
  created() {
    this.fetchOrderDetail(); // 获取订单详情
    this.fetchUserAddress(); // 获取用户地址
  },
  methods: {
    // 获取订单详情
    async fetchOrderDetail() {
      try {
        const response = await getOrder(this.orderId); // 调用 getOrder 接口
        if (response.code === 0) {
          this.orderItems = response.data.omsOrderItemList; // 订单商品项
          this.totalAmount = response.data.totalAmount; // 总金额
          this.userName = response.data.userName; // 下单用户名
          this.createTime = response.data.createTime; // 下单时间
          this.orderStatus = response.data.orderStatus; // 订单状态

          // 如果订单状态不为 0，解析 userAddress 到 selectedAddress
          if (this.orderStatus !== 0 && response.data.userAddress) {
            this.selectedAddress = JSON.parse(response.data.userAddress);
          }
        } else {
          this.$message.error("获取订单详情失败：" + response.msg);
        }
      } catch (error) {
        console.error("获取订单详情失败:", error);
        this.$message.error("获取订单详情失败，请重试！");
      }
    },
    // 获取用户地址
    async fetchUserAddress() {
      try {
        const response = await getById(); // 获取用户信息
        if (response.code === 0) {
          this.addressList = JSON.parse(response.data.userAddress || "[]"); // 解析用户地址
        }
      } catch (error) {
        console.error("获取用户地址失败:", error);
        this.$message.error("获取用户地址失败，请重试！");
      }
    },
    // 显示地址选择弹框
    showAddressDialog() {
      this.addressDialogVisible = true;
    },
    // 处理地址选择
    handleAddressSelect(address) {
      this.selectedAddress = address; // 设置选中的地址
      this.addressDialogVisible = false; // 关闭弹框
    },
    // 确认手动输入的地址
    confirmAddress() {
      if (!this.newAddress.address) {
        this.$message.warning("请输入收货地址");
        return;
      }
      this.selectedAddress = { 
        phone: this.newAddress.phone || "未填写", 
        address: this.newAddress.address 
      };
      this.addressDialogVisible = false;
    },
    // 打开评价弹窗
    openReviewDialog(row) {
      this.reviewForm.productId = row.productId;
      this.reviewForm.orderId = this.orderId;
      this.reviewForm.rating = 5;
      this.reviewForm.content = "";
      this.reviewDialogVisible = true;
    },
    // 提交评价
    async submitReview() {
      if (!this.reviewForm.content) {
        this.$message.warning("请输入评价内容");
        return;
      }
      try {
        const resp = await addReview(this.reviewForm);
        if (resp.code === 0) {
          this.$message.success("评价成功！");
          this.reviewDialogVisible = false;
          this.fetchOrderDetail();
        } else {
          this.$message.error(resp.msg || "评价失败");
        }
      } catch (error) {
        console.error("提交评价失败:", error);
        this.$message.error("提交评价失败，请重试");
      }
    },
    // 支付订单
    async placeOrder() {
      if (!this.selectedAddress) {
        this.$message.error("请选择收货地址！");
        return;
      }

      // 构造请求参数
      const orderData = {
        orderId: this.orderId,
        omsPlaceOrderProductItemList: this.orderItems.map(item => ({
          productId: item.productId,
          quantity: item.quantity,
          price: item.price,
        })),
        totalAmount: this.totalAmount,
        paymentMethod: "在线支付", // 假设支付方式为在线支付
        userAddress: JSON.stringify(this.selectedAddress), // 序列化 selectedAddress
        isCar: false, // 假设不是购物车
      };

      try {
        const response = await placeOrder(orderData); // 调用 placeOrder 接口
        if (response.code === 0) {
          this.$message.success("支付成功！");
          this.$router.push({ path: "/my-orders" }); // 跳转到我的订单
        } else {
          this.$message.error("支付失败：" + response.msg);
        }
      } catch (error) {
        console.error("支付失败:", error);
        this.$message.error("支付失败，请重试！");
      }
    },
    // 取消支付
    async cancelOrder() {
      try {
        const response = await cancelOrder({
          id: this.orderId,
          orderStatus: 2, // 已取消
        });

        if (response.code === 0) {
          this.$message.success("订单已取消！");
          this.$router.push({ path: "/my-orders" }); // 跳转到我的订单
        } else {
          this.$message.error("取消订单失败：" + response.msg);
        }
      } catch (error) {
        console.error("取消订单失败:", error);
        this.$message.error("取消订单失败，请重试！");
      }
    },
  },
};
</script>

<style scoped>
.order-detail {
  max-width: 900px;
  margin: 30px auto;
  padding: 40px;
  background: #ffffff;
  border-radius: 24px;
  box-shadow: 0 8px 30px rgba(67, 160, 71, 0.1);
}

.order-detail h3 {
  font-size: 28px;
  font-weight: 700;
  margin-bottom: 35px;
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.address-info {
  margin-top: 15px;
  padding: 20px;
  border: 1px solid rgba(67, 160, 71, 0.15);
  border-radius: 16px;
  background: rgba(67, 160, 71, 0.05);
}

.address-info p {
  color: #333333;
  margin: 8px 0;
  font-size: 15px;
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

:deep(.el-input__inner) {
  background: #fafafa;
  border: 1.5px solid #e9ecef;
  border-radius: 12px;
  color: #333333;
}

:deep(.el-form-item__label) {
  color: #666666;
  font-weight: 500;
}

:deep(.el-button--primary) {
  background: linear-gradient(135deg, #43a047 0%, #66bb6a 100%);
  border-color: #43a047;
  border-radius: 12px;
  padding: 12px 30px;
  font-weight: 600;
  box-shadow: 0 6px 20px rgba(67, 160, 71, 0.3);
  transition: all 0.3s ease;
}

:deep(.el-button--primary:hover) {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(67, 160, 71, 0.4);
}

:deep(.el-button--danger) {
  background: linear-gradient(135deg, #e53935 0%, #ef5350 100%);
  border-color: #ef5350;
  border-radius: 12px;
  padding: 12px 30px;
  font-weight: 600;
  box-shadow: 0 6px 20px rgba(229, 57, 53, 0.3);
  transition: all 0.3s ease;
}

:deep(.el-button--danger:hover) {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(229, 57, 53, 0.4);
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

.product-image {
  border-radius: 12px;
  border: 2px solid rgba(67, 160, 71, 0.2);
}
</style>
