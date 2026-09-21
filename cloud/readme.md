# 电商平台项目说明文档

## 一、项目概述

本项目是一个基于SpringCloud微服务架构的电商平台，采用SpringBoot + Vue技术栈，实现商品管理、订单管理、用户管理等核心功能。

## 二、技术栈

| 分类 | 技术 | 版本 |
| :--- | :--- | :--- |
| 语言 | Java | 1.8 |
| 框架 | SpringBoot | 2.7.x |
| ORM | MyBatis-Plus | 3.x |
| 前端框架 | Vue | 2.x |
| 数据库 | MySQL | 8.0+ |
| 注册中心 | Nacos | 2.x |
| 前端组件库 | Element UI | - |

## 三、项目结构

```
cloud/
├── cloud-auth/              # 认证服务
├── cloud-gateway/           # 网关服务
├── cloud-api/               # API接口定义
├── cloud-common/            # 公共模块
│   ├── cloud-common-core/   # 核心工具类
│   ├── cloud-common-security/ # 安全认证
│   ├── cloud-common-redis/  # Redis工具
│   └── cloud-common-business/ # 业务实体
├── cloud-modules/           # 业务模块
│   ├── cloud-modules-system/ # 系统管理
│   ├── cloud-modules-cms/   # 内容管理(商品/促销)
│   ├── cloud-modules-ums/   # 用户管理
│   └── cloud-modules-oms/   # 订单管理
├── admin-ui/                # 后台管理前端
└── front-ui/                # 前台用户前端
```

## 四、系统功能模块

### 1、登录模块
- 用户登录
- 用户注册

### 2、用户前台
- 首页轮播图
- 商品列表展示
- 商品详情
- 商品搜索
- 商品分类
- 购物车管理
- 订单管理
- 商品收藏
- 商品评价
- 个人中心
- 余额充值

### 3、后台管理
- 首页统计图表
- 商品管理
- 分类管理
- 促销管理
- 订单管理
- 评价管理
- 收藏管理
- 用户管理
- 管理员管理
- 轮播图管理

## 五、项目启动方式

### 1、后端启动
```bash
# 启动Nacos（进入nacos/bin目录）
startup.cmd -m standalone

# 启动后端服务（IDEA中运行）
# 按住Shift全选cloud-auth、cloud-gateway、cloud-modules下各模块启动
```

### 2、前端后台启动
```bash
cd cloud/admin-ui
npm install
npm run dev
```

### 3、前端前台启动
```bash
cd cloud/front-ui
npm install
npm run serve
```

## 六、访问地址

| 模块 | 地址 |
| :--- | :--- |
| Nacos | http://localhost:8848/nacos |
| 后台管理 | http://localhost:8080 |
| 用户前台 | http://localhost:8081 |

## 七、项目特点

- ✅ SpringCloud微服务架构
- ✅ 前后端分离
- ✅ 完整的商品管理系统
- ✅ 订单管理系统
- ✅ 用户权限管理
- ✅ 促销活动管理
- ✅ 响应式前端设计