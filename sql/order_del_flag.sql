-- 订单表增加删除标记（软删除：顾客删除后后台仍保留）
ALTER TABLE `oms_order` ADD COLUMN `del_flag` tinyint(1) DEFAULT 0 COMMENT '删除标记:0-未删,1-已删';
