<template>
  <el-row :gutter="40" class="panel-group">
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('newVisitis')">
        <div class="card-panel-icon-wrapper icon-people">
          <svg-icon icon-class="peoples" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            访客
          </div>
          <count-to :start-val="0" :end-val="102400" :duration="2600" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('messages')">
        <div class="card-panel-icon-wrapper icon-message">
          <svg-icon icon-class="message" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            消息
          </div>
          <count-to :start-val="0" :end-val="81212" :duration="3000" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('purchases')">
        <div class="card-panel-icon-wrapper icon-money">
          <svg-icon icon-class="money" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            金额
          </div>
          <count-to :start-val="0" :end-val="9280" :duration="3200" class="card-panel-num" />
        </div>
      </div>
    </el-col>
    <el-col :xs="12" :sm="12" :lg="6" class="card-panel-col">
      <div class="card-panel" @click="handleSetLineChartData('shoppings')">
        <div class="card-panel-icon-wrapper icon-shopping">
          <svg-icon icon-class="shopping" class-name="card-panel-icon" />
        </div>
        <div class="card-panel-description">
          <div class="card-panel-text">
            订单
          </div>
          <count-to :start-val="0" :end-val="13600" :duration="3600" class="card-panel-num" />
        </div>
      </div>
    </el-col>
  </el-row>
</template>

<script>
import CountTo from 'vue-count-to'

export default {
  components: {
    CountTo
  },
  methods: {
    handleSetLineChartData(type) {
      this.$emit('handleSetLineChartData', type)
    }
  }
}
</script>

<style lang="scss" scoped>
@import "~@/assets/styles/variables.scss";

.panel-group {
  margin-top: 24px;

  .card-panel-col {
    margin-bottom: 24px;
  }

  .card-panel {
    height: 140px;
    cursor: pointer;
    font-size: $font-size-sm;
    position: relative;
    overflow: hidden;
    color: $text-secondary;
    background: $bg-card;
    border: 1px solid $border-color;
    border-radius: $border-radius;
    transition: all $transition-normal;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 4px;
      height: 100%;
      background: $primary-gradient;
      opacity: 0;
      transition: opacity $transition-fast;
    }

    &:hover {
      transform: translateY(-4px);
      box-shadow: $shadow-lg;
      border-color: $border-light;

      &::before {
        opacity: 1;
      }

      .card-panel-icon-wrapper {
        transform: scale(1.1);

        .card-panel-icon {
          filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.2));
        }
      }

      .card-panel-num {
        color: $primary-color;
      }
    }

    .icon-people {
      background: linear-gradient(135deg, #f87171 0%, #ef4444 100%);
      box-shadow: 0 4px 20px rgba(248, 113, 113, 0.3);
    }

    .icon-message {
      background: $primary-gradient;
      box-shadow: 0 4px 20px rgba(220, 38, 38, 0.3);
    }

    .icon-money {
      background: linear-gradient(135deg, #f97316 0%, #ea580c 100%);
      box-shadow: 0 4px 20px rgba(249, 115, 22, 0.3);
    }

    .icon-shopping {
      background: linear-gradient(135deg, #16a34a 0%, #15803d 100%);
      box-shadow: 0 4px 20px rgba(22, 163, 74, 0.3);
    }

    .card-panel-icon-wrapper {
      float: left;
      margin: 24px 0 0 24px;
      padding: 20px;
      border-radius: $border-radius;
      transition: all $transition-normal;

      .card-panel-icon {
        font-size: 44px;
        color: #fff;
        transition: all $transition-fast;
      }
    }

    .card-panel-description {
      float: right;
      margin: 28px 24px;
      margin-left: 0;
      text-align: right;

      .card-panel-text {
        line-height: 20px;
        color: $text-muted;
        font-size: $font-size-sm;
        margin-bottom: 8px;
        text-transform: uppercase;
        letter-spacing: 1px;
      }

      .card-panel-num {
        font-size: 32px;
        font-weight: 700;
        color: $text-primary;
        line-height: 1.2;
        transition: color $transition-fast;
      }
    }
  }
}

@media (max-width: 768px) {
  .panel-group .card-panel {
    height: 120px;

    .card-panel-icon-wrapper {
      margin: 16px 0 0 16px;
      padding: 16px;

      .card-panel-icon {
        font-size: 36px;
      }
    }

    .card-panel-description {
      margin: 20px 16px;

      .card-panel-num {
        font-size: 24px;
      }
    }
  }
}

@media (max-width: 550px) {
  .card-panel-description {
    display: none;
  }

  .card-panel-icon-wrapper {
    float: none !important;
    width: 100%;
    height: 100%;
    margin: 0 !important;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: $border-radius !important;

    .card-panel-icon {
      display: block;
      margin: 0 auto !important;
      float: none !important;
    }
  }
}
</style>