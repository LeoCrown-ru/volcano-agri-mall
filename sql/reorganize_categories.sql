-- =====================================================
-- 分类重组：5 个顶级分类 + 蜂产品补 2 个商品
-- 执行方式：Navicat 对 shop_cloud 执行
-- =====================================================

-- 1. 五个子分类提升为顶级分类（parent_id 置空）
UPDATE `cms_product_category` SET `parent_id` = NULL WHERE `id` IN ('300101', '300102', '300103', '300104', '300105');

-- 2. 删除"农副产品"总类目（商品未直接挂在它下面，可安全删除）
DELETE FROM `cms_product_category` WHERE `id` = '300001';

-- 3. 蜂产品补货：蜂王浆、蜂花粉（图片放好后再执行，或先执行后图片就位自动显示）
INSERT INTO `cms_product` (`id`, `name`, `description`, `price`, `stock`, `category_id`, `product_status`, `image_url`, `create_time`, `update_time`) VALUES
('310017', '长白山蜂王浆 250g', '新鲜蜂王浆，乳白膏状，富含多种营养，早晚空腹含服更佳。', 128.00, 150, '300104', 1, 'http://127.0.0.1:9300/statics/2026/08/29/fengwangjiang.jpg', NOW(), NOW()),
('310018', '蜂花粉 300g', '天然蜂花粉颗粒，金黄细粒，营养丰富，温水冲服或拌酸奶食用。', 45.00, 200, '300104', 1, 'http://127.0.0.1:9300/statics/2026/08/29/fenghuafen.jpg', NOW(), NOW());
