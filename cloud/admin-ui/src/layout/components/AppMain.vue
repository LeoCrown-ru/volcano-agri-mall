<template>
  <section class="app-main">
    <transition name="fade-transform" mode="out-in">
      <keep-alive :include="cachedViews">
        <router-view :key="key" />
      </keep-alive>
    </transition>
  </section>
</template>

<script>
export default {
  name: 'AppMain',
  computed: {
    cachedViews() {
      return this.$store.state.tagsView.cachedViews
    },
    key() {
      return this.$route.path
    }
  }
}
</script>

<style lang="scss" scoped>
@import "~@/assets/styles/variables.scss";

.app-main {
  min-height: calc(100vh - $header-height);
  width: 100%;
  position: relative;
  overflow: hidden;
  background: $bg-primary;
}

.fixed-header+.app-main {
  padding-top: $header-height;
}

.hasTagsView {
  .app-main {
    min-height: calc(100vh - #{$header-height + $tags-height});
  }

  .fixed-header+.app-main {
    padding-top: #{$header-height + $tags-height};
  }
}
</style>

<style lang="scss">
@import "~@/assets/styles/variables.scss";

.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 17px;
  }
}

.el-table {
  background: $bg-card;
  border: 1px solid $border-color;
  border-radius: $border-radius;
  overflow: hidden;

  .el-table__header {
    background: $bg-secondary;

    th {
      background: $bg-secondary !important;
      color: $text-secondary;
      font-weight: 600;
      border-bottom: 1px solid $border-color;
    }
  }

  .el-table__body {
    tr {
      transition: background $transition-fast;

      &:hover {
        background: $bg-hover !important;
      }

      &.current-row {
        background: rgba(220, 38, 38, 0.08) !important;
      }
    }

    td {
      border-bottom: 1px solid $border-color;
      color: $text-primary;
    }
  }
}

.el-pagination {
  .el-pagination__total {
    color: $text-muted;
  }

  .el-pager li {
    background: $bg-card;
    border: 1px solid $border-color;
    color: $text-secondary;
    border-radius: $border-radius-sm;
    margin: 0 4px;
    transition: all $transition-fast;

    &:hover {
      background: $bg-hover;
      color: $text-primary;
    }

    &.active {
      background: $primary-gradient;
      border-color: $primary-color;
      color: #fff;
    }
  }

  .btn-prev,
  .btn-next {
    background: $bg-card;
    border: 1px solid $border-color;
    color: $text-secondary;
    border-radius: $border-radius-sm;
    transition: all $transition-fast;

    &:hover {
      background: $bg-hover;
      color: $text-primary;
    }
  }
}

.el-form-item__label {
  color: $text-secondary;
  font-weight: 500;
}

.el-input__inner {
  background: $bg-card;
  border: 1px solid $border-color;
  color: $text-primary;
  border-radius: $border-radius-sm;
  transition: all $transition-fast;

  &:focus {
    border-color: $primary-color;
    box-shadow: 0 0 0 3px rgba(220, 38, 38, 0.1);
  }
}

.el-select {
  .el-input__inner {
    background: $bg-card;
    border-color: $border-color;
    color: $text-primary;
  }
}

.el-select-dropdown {
  background: $bg-card;
  border: 1px solid $border-color;
  border-radius: $border-radius;

  .el-select-dropdown__item {
    color: $text-secondary;

    &:hover {
      background: $bg-hover;
    }

    &.selected {
      background: rgba(220, 38, 38, 0.1);
      color: $primary-color;
    }
  }
}

.el-dialog {
  background: $bg-card;
  border: 1px solid $border-color;
  border-radius: $border-radius-lg;
  box-shadow: $shadow-lg;

  .el-dialog__header {
    background: $bg-secondary;
    border-bottom: 1px solid $border-color;

    .el-dialog__title {
      color: $text-primary;
      font-size: $font-size-lg;
      font-weight: 600;
    }
  }

  .el-dialog__body {
    color: $text-secondary;
  }

  .el-dialog__footer {
    border-top: 1px solid $border-color;
    background: $bg-secondary;
  }
}

.el-button {
  border-radius: $border-radius-sm;
  font-weight: 500;
  transition: all $transition-fast;

  &--primary {
    background: $primary-gradient;
    border: none;
    color: #fff;

    &:hover {
      transform: translateY(-2px);
      box-shadow: $shadow-glow;
    }
  }

  &--success {
    background: $success-color;
    border: none;
    color: #fff;
  }

  &--warning {
    background: $warning-color;
    border: none;
    color: #fff;
  }

  &--danger {
    background: $danger-color;
    border: none;
    color: #fff;
  }

  &--info {
    background: $info-color;
    border: none;
    color: #fff;
  }

  &--text {
    color: $text-muted;

    &:hover {
      color: $text-primary;
    }
  }

  &--plain {
    background: transparent;
    border: 1px solid $border-color;
    color: $text-secondary;

    &.el-button--primary {
      border-color: $primary-color;
      color: $primary-color;

      &:hover {
        background: rgba(220, 38, 38, 0.1);
      }
    }

    &.el-button--success {
      border-color: $success-color;
      color: $success-color;

      &:hover {
        background: rgba(22, 163, 74, 0.1);
      }
    }

    &.el-button--warning {
      border-color: $warning-color;
      color: $warning-color;

      &:hover {
        background: rgba(202, 138, 4, 0.1);
      }
    }

    &.el-button--danger {
      border-color: $danger-color;
      color: $danger-color;

      &:hover {
        background: rgba(220, 38, 38, 0.1);
      }
    }
  }
}

.el-switch {
  .el-switch__core {
    background: $bg-tertiary;
    border-color: $border-color;

    &.is-checked {
      background: $primary-gradient;
    }
  }

  .el-switch__label {
    color: $text-muted;
  }
}

.el-tree {
  background: transparent;

  .el-tree-node__content {
    color: $text-secondary;
    transition: all $transition-fast;

    &:hover {
      background: $bg-hover;
    }
  }

  .el-tree-node.is-current > .el-tree-node__content {
    background: rgba(220, 38, 38, 0.1);
    color: $primary-color;
  }
}

.el-tag {
  border-radius: $border-radius-sm;
  font-size: $font-size-xs;
  padding: 4px 12px;
}

.el-breadcrumb {
  .el-breadcrumb__item {
    .el-breadcrumb__inner {
      color: $text-muted;

      &.is-link {
        color: $primary-color;

        &:hover {
          color: $primary-light;
        }
      }
    }

    .el-breadcrumb__separator {
      color: $text-muted;
      margin: 0 8px;
    }
  }
}

.el-dropdown-menu {
  background: $bg-card;
  border: 1px solid $border-color;
  border-radius: $border-radius;
  box-shadow: $shadow-lg;

  .el-dropdown-menu__item {
    color: $text-secondary;
    transition: all $transition-fast;

    &:hover {
      background: $bg-hover;
      color: $text-primary;
    }
  }
}

.el-upload-dragger {
  background: $bg-card;
  border: 2px dashed $border-color;
  border-radius: $border-radius;
  transition: all $transition-fast;

  &:hover {
    border-color: $primary-color;
    background: rgba(220, 38, 38, 0.05);
  }
}

.el-scrollbar {
  .el-scrollbar__bar {
    &.is-vertical {
      .el-scrollbar__thumb {
        background: $border-color;
        border-radius: 4px;
      }
    }
  }
}
</style>