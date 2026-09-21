-- =====================================================
-- 火山石板岩特色农副产品电商系统
-- 增量模块：商品溯源信息（cms_product_trace）
-- 执行方式：在 Navicat / 命令行中，对 shop_cloud 数据库执行本文件
-- =====================================================

-- 1. 溯源信息表
DROP TABLE IF EXISTS `cms_product_trace`;
CREATE TABLE `cms_product_trace` (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NOT NULL COMMENT '溯源ID',
  `product_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NOT NULL COMMENT '商品ID',
  `origin` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '产地',
  `batch_no` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '批次号',
  `grow_record` text CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci COMMENT '生长记录',
  `process_info` text CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci COMMENT '加工信息',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci COMMENT='商品溯源信息';

-- 2. 后台菜单：溯源管理（挂在"商品管理"目录 2164 下，ID 用 3000 段避免冲突）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `query`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`) VALUES
(3000, '溯源管理', 2164, 6, 'productTrace', 'cms/trace/index', NULL, 1, 0, 'C', '0', '0', 'cms:productTrace:list', 'tree', 'admin', NOW(), '', NULL, '商品溯源管理菜单'),
(3001, '溯源查询', 3000, 1, '#', '', NULL, 1, 0, 'F', '0', '0', 'cms:productTrace:query', '#', 'admin', NOW(), '', NULL, ''),
(3002, '溯源新增', 3000, 2, '#', '', NULL, 1, 0, 'F', '0', '0', 'cms:productTrace:add', '#', 'admin', NOW(), '', NULL, ''),
(3003, '溯源修改', 3000, 3, '#', '', NULL, 1, 0, 'F', '0', '0', 'cms:productTrace:edit', '#', 'admin', NOW(), '', NULL, ''),
(3004, '溯源删除', 3000, 4, '#', '', NULL, 1, 0, 'F', '0', '0', 'cms:productTrace:remove', '#', 'admin', NOW(), '', NULL, ''),
(3005, '溯源导出', 3000, 5, '#', '', NULL, 1, 0, 'F', '0', '0', 'cms:productTrace:export', '#', 'admin', NOW(), '', NULL, '');

-- 3. 授权：给所有角色挂上溯源菜单（role_id=1 超管必须，其他角色按需）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT r.role_id, m.menu_id
FROM sys_role r
JOIN (SELECT 3000 AS menu_id UNION ALL SELECT 3001 UNION ALL SELECT 3002
      UNION ALL SELECT 3003 UNION ALL SELECT 3004 UNION ALL SELECT 3005) m;

-- 4. 演示数据（说明）：
-- 当前商品库里的商品都是原系统的演示数据（数码/图书），没有农产品。
-- 等后台加入"火山石板岩大米"等农副产品商品后，再在"溯源管理"页面
-- 给对应商品录入溯源信息即可，无需手动插 SQL。
