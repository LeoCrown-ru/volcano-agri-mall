<template>
  <li
      @mouseenter="handleCategoryHover(category)"
      @mouseleave="handleCategoryLeave(category)"
  >
    <span @click="fetchProducts(category)">{{ category.name }}</span>
    <div v-if="category.showChildren && category.children && category.children.length" class="sub-sub-category-dropdown">
      <ul>
        <category-item
            v-for="child in category.children"
            :key="child.id"
            :category="child"
            @fetch-products="$emit('fetch-products', child)"
        />
      </ul>
    </div>
  </li>
</template>

<script>
export default {
  props: {
    category: Object,
  },
  methods: {
    handleCategoryHover(category) {
      category.showChildren = true;
    },
    handleCategoryLeave(category) {
      category.showChildren = false;
    },
    fetchProducts(category) {
      if (!category.children || category.children.length === 0) {
        this.$emit("fetch-products", category);
      }
    },
  },
};
</script>

<style scoped>
li {
  position: relative;
  cursor: pointer;
  padding: 12px 20px;
  font-size: 15px;
  color: #333333;
  transition: all 0.3s ease;
  border-radius: 10px;
}

li:hover {
  background: rgba(67, 160, 71, 0.1);
  color: #43a047;
}

span {
  font-weight: 500;
}

.sub-sub-category-dropdown {
  position: absolute;
  top: 0;
  left: 100%;
  background-color: #ffffff;
  border: 1px solid rgba(67, 160, 71, 0.1);
  border-radius: 12px;
  box-shadow: 0 8px 25px rgba(67, 160, 71, 0.1);
  min-width: 200px;
  z-index: 1000;
}

.sub-sub-category-dropdown ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.sub-sub-category-dropdown li {
  padding: 12px 20px;
  cursor: pointer;
  color: #333333;
  position: relative;
  border-radius: 0;
}

.sub-sub-category-dropdown li:hover {
  background: rgba(67, 160, 71, 0.1);
  color: #43a047;
}
</style>
