<template>
  <div v-if="isLoading" class="loading-overlay">
    <div class="loading-spinner">
      <i class="el-icon-loading"></i>
      <span>加载中...</span>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      isLoading: false,
    };
  },
  mounted() {
    const vm = this;
    this.$root.$on('loading-start', function() {
      vm.isLoading = true;
    });
    this.$root.$on('loading-end', function() {
      vm.isLoading = false;
    });
  },
  beforeDestroy() {
    this.$root.$off('loading-start');
    this.$root.$off('loading-end');
  },
};
</script>

<style scoped>
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.loading-spinner {
  text-align: center;
}

.el-icon-loading {
  font-size: 48px;
  color: #43a047;
}

.loading-spinner span {
  display: block;
  margin-top: 15px;
  font-size: 16px;
  color: #666666;
}
</style>
