-- =====================================================
-- 促销活动：清掉旧的数码类活动，重建农产品促销
-- 执行方式：Navicat 对 shop_cloud 执行
-- =====================================================

-- 1. 删除旧的 3 个活动（与农产品无关）
DELETE FROM `cms_promotion` WHERE `id` IN ('3001', '3002', '3003');
DELETE FROM `cms_product_promotion` WHERE `promotion_id` IN ('3001', '3002', '3003');

-- 2. 新建 3 个农产品促销活动
INSERT INTO `cms_promotion` (`id`, `name`, `type`, `discount`, `coupon_value`, `start_time`, `end_time`, `create_time`, `update_time`) VALUES
('3201', '石板大米新米尝鲜', 0, 0.85, NULL, '2026-08-29 00:00:00', '2026-12-31 23:59:59', NOW(), NOW()),
('3202', '菌菇山珍特惠', 1, NULL, 20.00, '2026-08-29 00:00:00', '2026-12-31 23:59:59', NOW(), NOW()),
('3203', '山货坚果零食季', 0, 0.90, NULL, '2026-08-29 00:00:00', '2026-12-31 23:59:59', NOW(), NOW());

-- 3. 活动-商品关联
-- 石板大米新米尝鲜（8.5折）：5kg、礼盒装、10kg 家庭装
INSERT INTO `cms_product_promotion` (`id`, `product_id`, `promotion_id`, `create_time`, `update_time`) VALUES
('320001', '310001', '3201', NOW(), NOW()),
('320002', '310005', '3201', NOW(), NOW()),
('320003', '310016', '3201', NOW(), NOW());
-- 菌菇山珍特惠（满减优惠20）：秋木耳、榛蘑、猴头菇
INSERT INTO `cms_product_promotion` (`id`, `product_id`, `promotion_id`, `create_time`, `update_time`) VALUES
('320004', '310003', '3202', NOW(), NOW()),
('320005', '310009', '3202', NOW(), NOW()),
('320006', '310010', '3202', NOW(), NOW());
-- 山货坚果零食季（9折）：松子、榛子、蓝莓干
INSERT INTO `cms_product_promotion` (`id`, `product_id`, `promotion_id`, `create_time`, `update_time`) VALUES
('320007', '310013', '3203', NOW(), NOW()),
('320008', '310014', '3203', NOW(), NOW()),
('320009', '310015', '3203', NOW(), NOW());
