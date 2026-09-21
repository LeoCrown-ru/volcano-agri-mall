<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" label-width="80px">
      <el-form-item label="轮播图" prop="imageUrl">
        <el-button type="primary" @click="openProductDialog">选择商品</el-button>
        <span v-if="form.imageUrl" style="margin-left: 10px;">已选择 {{ parsedImageUrl.length }} 个商品</span>
        <!-- 展示已选商品图片 -->
        <div v-if="form.imageUrl" class="selected-products">
          <el-image
            v-for="(product, index) in parsedImageUrl"
            :key="index"
            :src="product.imageUrl"
            fit="cover"
            style="width: 100px; height: 100px; margin-right: 10px; margin-top: 10px;"
          >
            <div slot="error" class="image-fallback">
              <i class="el-icon-picture-outline" />
            </div>
          </el-image>
        </div>
      </el-form-item>
      <el-form-item label="内容描述富文本">
        <editor v-model="form.description" :min-height="192" />
      </el-form-item>
    </el-form>

    <div class="footer">
      <el-button type="primary" @click="submitForm">保存</el-button>
    </div>

    <!-- 商品选择弹框 -->
    <el-dialog
      title="选择商品"
      :visible.sync="productDialogVisible"
      width="80%"
      :before-close="handleDialogClose"
    >
      <el-table
        ref="productTable"
        :data="productList"
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" />
        <el-table-column label="商品图片" width="120">
          <template slot-scope="scope">
            <el-image
              :src="scope.row.imageUrl"
              fit="cover"
              style="width: 100px; height: 100px;"
            >
              <div slot="error" class="image-fallback">
                <i class="el-icon-picture-outline" />
              </div>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" />
        <el-table-column prop="description" label="商品描述" />
      </el-table>
      <!-- 分页组件 -->
      <el-pagination
        style="margin-top: 20px; text-align: right;"
        :current-page="pagination.currentPage"
        :page-size="pagination.pageSize"
        :total="pagination.total"
        @current-change="handlePageChange"
        layout="prev, pager, next"
      />
      <div slot="footer" class="dialog-footer">
        <el-button @click="productDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSelection">确定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getContent, updateContent } from "@/api/cms/content";
import { listProduct } from "@/api/cms/product";

export default {
  name: "ContentEditor",
  data() {
    return {
      // 表单数据
      form: {
        id: null,
        imageUrl: null,
        description: null,
      },
      // 表单校验
      rules: {
        imageUrl: [
          { required: true, message: "轮播图不能为空", trigger: "blur" },
        ],
        description: [
          { required: true, message: "内容描述富文本不能为空", trigger: "blur" },
        ],
      },
      // 商品选择弹框相关
      productDialogVisible: false,
      productList: [], // 商品列表
      selectedProducts: [], // 选中的商品
      // 分页配置
      pagination: {
        currentPage: 1, // 当前页码
        pageSize: 10, // 每页条数
        total: 0, // 总条数
      },
    };
  },
  computed: {
    // 解析 imageUrl 为数组
    parsedImageUrl() {
      if (this.form.imageUrl) {
        try {
          return JSON.parse(this.form.imageUrl);
        } catch (e) {
          console.error("解析 imageUrl 失败", e);
          return [];
        }
      }
      return [];
    },
    // 获取已选商品的 ID 列表
    selectedProductIds() {
      return this.parsedImageUrl.map((product) => product.id);
    },
  },
  created() {
    this.loadData();
  },
  methods: {
    /** 加载数据 */
    loadData() {
      getContent().then((response) => {
        this.form = response.data;
      });
    },
    /** 提交表单 */
    submitForm() {
      this.$refs["form"].validate((valid) => {
        if (valid) {
          updateContent(this.form).then(() => {
            this.$modal.msgSuccess("保存成功");
          });
        }
      });
    },
    /** 打开商品选择弹框 */
    openProductDialog() {
      this.productDialogVisible = true;
      this.loadProductList();
    },
    /** 加载商品列表 */
    loadProductList() {
      const params = {
        pageNum: this.pagination.currentPage,
        pageSize: this.pagination.pageSize,
      };
      listProduct(params).then((response) => {
        if (response.code === 200) {
          this.productList = response.rows; // 商品列表数据
          this.pagination.total = response.total; // 总条数
          // 默认勾选已选商品
          this.$nextTick(() => {
            this.setDefaultSelection();
          });
        } else {
          this.$message.error(response.msg || "加载商品列表失败");
        }
      });
    },
    /** 设置默认勾选 */
    setDefaultSelection() {
      if (this.productList.length > 0 && this.selectedProductIds.length > 0) {
        this.productList.forEach((product) => {
          if (this.selectedProductIds.includes(product.id)) {
            this.$refs.productTable.toggleRowSelection(product, true);
          }
        });
      }
    },
    /** 处理表格选择变化 */
    handleSelectionChange(selection) {
      this.selectedProducts = selection;
    },
    /** 确认选择 */
    confirmSelection() {
      if (this.selectedProducts.length === 0) {
        this.$message.warning("请至少选择一个商品");
        return;
      }
      // 组装选中的商品的 id 和 imageUrl
      const selectedData = this.selectedProducts.map((product) => ({
        id: product.id,
        imageUrl: product.imageUrl,
      }));
      // 将 List 转换为 String 并赋值给 imageUrl
      this.form.imageUrl = JSON.stringify(selectedData);
      this.productDialogVisible = false;
      // 自动保存到后端
      this.submitForm();
    },
    /** 关闭弹框时的处理 */
    handleDialogClose(done) {
      this.selectedProducts = [];
      done();
    },
    /** 分页页码变化 */
    handlePageChange(page) {
      this.pagination.currentPage = page;
      this.loadProductList();
    },
  },
};
</script>

<style scoped>
.app-container {
  padding: 20px;
}

.footer {
  margin-top: 20px;
  text-align: center;
}

.image-fallback {
  width: 100px;
  height: 100px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #909399;
}

.selected-products {
  display: flex;
  flex-wrap: wrap;
  margin-top: 10px;
}
</style>
