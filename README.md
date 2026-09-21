# 火山石板岩特色农副产品电商系统

> 基于 Spring Cloud Alibaba 的微服务电商系统（本科毕业设计）

## 项目简介

面向火山石板岩地区特色农副产品，解决"有产品无渠道、有品质无认证"的产销与信任问题。系统采用微服务架构拆分业务模块，前台面向普通用户提供完整购物流程，后台面向管理员提供全链路运营管理，构成从商品上架到售后的完整电商闭环。

## 技术栈

| 层次 | 技术 |
|------|------|
| 微服务框架 | Spring Cloud Alibaba（Nacos 注册与配置中心、Spring Cloud Gateway、OpenFeign） |
| 基础框架 | RuoYi-Cloud |
| 持久层 | MyBatis-Plus、MySQL |
| 缓存 | Redis |
| 前端 | Vue 2 + Element UI（后台 `admin-ui` / 前台 `front-ui`） |

## 功能模块

**普通用户前台**
注册登录 / 浏览农副产品 / 加入购物车 / 提交订单 / 查看订单 / 商品评价 / 商品收藏 / 浏览记录 / 个人信息管理 / 溯源信息查询

**超级管理员后台**
用户信息管理 / 商品信息管理 / 商品分类管理 / 首页内容管理 / 促销活动管理 / 订单管理 / 订单商品管理 / 购物车管理 / 评价管理 / 收藏管理 / 浏览记录管理 / 溯源信息管理

前台做业务、后台做管控，前后台功能成对闭合。

## 系统亮点

1. **溯源模块**：商品关联"生产—加工—流通"全链路溯源数据，前台商品详情页与订单页可查询，为特色农产品提供产地信任背书。
2. **差异化促销规则**：结合用户历史订单与活动有效期双重判定，实现「首购享活动价、复购按原价」的动态定价逻辑。
3. **订单软删除**：用户侧删除仅置 `del_flag`，后台仍完整保留交易数据，保证交易可追溯。
4. **订单商品快照**：下单时将商品名称、单价等冗余进订单明细，商品后续改价、改名不影响历史订单展示。

## 目录结构

```
cloud/
  cloud-gateway/        # API 网关：统一入口、路由转发
  cloud-auth/           # 认证中心：登录鉴权、令牌签发
  cloud-api/            # Feign 远程调用接口定义
  cloud-common/         # 公共模块：核心、缓存、安全、日志、数据源等
  cloud-modules/
    cloud-modules-system/   # 系统管理
    cloud-modules-cms/      # 内容/商品/溯源管理
    cloud-modules-oms/      # 订单管理
    cloud-modules-ums/      # 用户管理
    cloud-modules-file/     # 文件服务
    cloud-modules-gen/      # 代码生成
  admin-ui/             # 后台管理前端（Vue 2 + Element UI）
  front-ui/             # 前台商城前端（Vue 2 + Element UI）
sql/                    # 数据库脚本
```

## 本地运行

1. 启动 MySQL、Redis、Nacos。
2. 按 `sql/README.md` 顺序导入数据库脚本。
3. 依次启动 `cloud-gateway`、`cloud-auth` 及各业务模块（`cloud-modules-*`）。
4. 前端：进入 `cloud/admin-ui` 或 `cloud/front-ui`，执行 `npm install` 后 `npm run serve`。

> 数据库、Redis、MinIO 等连接信息位于各模块的 `application-dev.yml`，请按本地环境自行修改后再启动。
