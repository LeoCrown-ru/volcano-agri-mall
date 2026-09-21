-- =====================================================
-- 清理：删除与毕设主题无关的旧商品及其关联数据
-- 只保留：商品 31% 开头（农副产品），分类 30% 开头（农副产品分类树）
-- =====================================================

-- 1. 关联数据（购物车/收藏/浏览记录/评价/促销关联 中引用旧商品的记录）
DELETE FROM `ums_cart` WHERE `product_id` NOT LIKE '31%';
DELETE FROM `ums_favorite` WHERE `product_id` NOT LIKE '31%';
DELETE FROM `ums_browse_history` WHERE `product_id` NOT LIKE '31%';
DELETE FROM `ums_product_review` WHERE `product_id` NOT LIKE '31%';
DELETE FROM `cms_product_promotion` WHERE `product_id` NOT LIKE '31%';

-- 2. 订单：删引用旧商品的订单项，再删已无订单项的订单
DELETE FROM `oms_order_item` WHERE `product_id` NOT LIKE '31%';
DELETE FROM `oms_order` WHERE `id` NOT IN (SELECT DISTINCT `order_id` FROM `oms_order_item`);

-- 3. 旧商品
DELETE FROM `cms_product` WHERE `id` NOT LIKE '31%';

-- 4. 旧分类（只保留农副产品 30% 开头的分类树）
DELETE FROM `cms_product_category` WHERE `id` NOT LIKE '30%';
