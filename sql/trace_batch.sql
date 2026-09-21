-- =====================================================
-- 溯源信息批量生成：给全部农副产品商品自动挂溯源
-- 按商品分类匹配产地/批次/生长记录/加工信息文案
-- 执行方式：Navicat 对 shop_cloud 执行
-- =====================================================

-- 1. 清掉已有溯源记录（避免重复）
DELETE FROM `cms_product_trace`;

-- 2. 批量生成：每个商品一条溯源记录
INSERT INTO `cms_product_trace` (`id`, `product_id`, `origin`, `batch_no`, `grow_record`, `process_info`, `create_time`, `update_time`)
SELECT
  CONCAT('TRACE-', p.`id`),
  p.`id`,
  CASE p.`category_id`
    WHEN '300101' THEN '黑龙江省宁安市渤海镇火山熔岩台地'
    WHEN '300102' THEN '黑龙江省宁安市渤海镇火山熔岩台地'
    WHEN '300103' THEN '长白山余脉林区'
    WHEN '300104' THEN '长白山高山蜜源地'
    WHEN '300105' THEN '长白山林区'
  END AS `origin`,
  CASE p.`category_id`
    WHEN '300101' THEN CONCAT('PLATE-2026-', RIGHT(p.`id`, 4))
    WHEN '300102' THEN CONCAT('GRAIN-2026-', RIGHT(p.`id`, 4))
    WHEN '300103' THEN CONCAT('MUSH-2026-', RIGHT(p.`id`, 4))
    WHEN '300104' THEN CONCAT('HONEY-2026-', RIGHT(p.`id`, 4))
    WHEN '300105' THEN CONCAT('NUT-2026-', RIGHT(p.`id`, 4))
  END AS `batch_no`,
  CASE p.`category_id`
    WHEN '300101' THEN '选用火山灰土壤培育，镜泊湖水灌溉，全生长周期不施化肥，人工除草，日照充足，生长期约150天。'
    WHEN '300102' THEN '火山灰土壤种植，天然山泉水灌溉，不施化肥，人工除草，光照充足。'
    WHEN '300103' THEN '林下仿野生培育，天然落叶腐殖土，山泉滋养，无农药无添加。'
    WHEN '300104' THEN '高山蜜源植物丰富，远离工业区，天然无污染，花期长蜜质纯。'
    WHEN '300105' THEN '天然林下生长，自然成熟，山泉灌溉，无人工催熟。'
  END AS `grow_record`,
  CASE p.`category_id`
    WHEN '300101' THEN '稻谷自然晾晒，低温烘干，石碾脱壳，保留胚芽，真空包装锁鲜。'
    WHEN '300102' THEN '自然晾晒，低温烘干，精选去杂，真空包装。'
    WHEN '300103' THEN '人工采摘，自然晾晒，低温烘干，锁住鲜味。'
    WHEN '300104' THEN '低温过滤，无添加无浓缩，保留天然活性成分。'
    WHEN '300105' THEN '自然晾晒，低温烘焙，锁住香脆口感。'
  END AS `process_info`,
  NOW(),
  NOW()
FROM `cms_product` p
WHERE p.`category_id` IN ('300101','300102','300103','300104','300105');
