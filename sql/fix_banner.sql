-- 修复首页轮播图配置（引号必须完整保留）
UPDATE `cmd_index_content` SET
  `image_url` = '[{"id":"310001","imageUrl":"http://127.0.0.1:9300/statics/2026/08/29/dami.jpg"},{"id":"310003","imageUrl":"http://127.0.0.1:9300/statics/2026/08/29/muer.jpg"},{"id":"310013","imageUrl":"http://127.0.0.1:9300/statics/2026/08/29/songzi.jpg"},{"id":"310004","imageUrl":"http://127.0.0.1:9300/statics/2026/08/29/fengmi.jpg"}]',
  `description` = '<h2>火山石板岩，来自火山熔岩台地的馈赠</h2><p>精选火山灰土壤培育的农副产品，镜泊湖水灌溉，天然好味，新鲜直达。</p>',
  `update_time` = NOW()
WHERE `id` = '1';
