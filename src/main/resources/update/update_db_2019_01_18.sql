-- 猪年春节 + 情人节 贴纸活动
CREATE TABLE `hbird_festival_tags` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `festival_type` int(1) DEFAULT NULL COMMENT '贴纸类型 1:春节  2:情人节',
  `icon_type` int(1) DEFAULT NULL COMMENT 'icon类型 1:贴纸icon 2:标语icon',
  `icon` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '贴纸icon',
  `description` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '描述',
  `status` int(1) unsigned NOT NULL DEFAULT '0' COMMENT '解锁状态  默认0:未解锁   1:已解锁',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='节日贴纸-标语表';
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (1, 1, 1, 'https://head.image.fengniaojizhang.cn/webOther/a1.png', '猪头', 1, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (2, 1, 2, 'https://head.image.fengniaojizhang.cn/webOther/b1.png', '黄金万两', 1, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (3, 1, 1, 'https://head.image.fengniaojizhang.cn/webOther/a2.png', '锦鲤', 1, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (4, 1, 2, 'https://head.image.fengniaojizhang.cn/webOther/b2.png', '逢赌必赢', 1, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (5, 1, 1, 'https://head.image.fengniaojizhang.cn/webOther/a3.png', '爆竹', 1, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (6, 1, 2, 'https://head.image.fengniaojizhang.cn/webOther/b3.png', '“猪”事顺遂', 1, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (7, 1, 1, 'https://head.image.fengniaojizhang.cn/webOther/a4.png', '灯笼', 1, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (8, 1, 2, 'https://head.image.fengniaojizhang.cn/webOther/b4.png', '奉旨发财', 1, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (9, 1, 1, 'https://head.image.fengniaojizhang.cn/webOther/a5.png', '红包', 0, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (10, 1, 2, 'https://head.image.fengniaojizhang.cn/webOther/b5.png', '锦鲤附体', 0, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (11, 1, 1, 'https://head.image.fengniaojizhang.cn/webOther/a6.png', '猪鼻子', 0, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (12, 1, 2, 'https://head.image.fengniaojizhang.cn/webOther/b6.png', '想胖三斤', 0, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (13, 1, 1, 'https://head.image.fengniaojizhang.cn/webOther/a7.png', '猪耳朵', 0, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (14, 1, 2, 'https://head.image.fengniaojizhang.cn/webOther/b7.png', '吃不胖符', 0, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (15, 1, 1, 'https://head.image.fengniaojizhang.cn/webOther/a8.png', '财神帽子', 0, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (16, 1, 2, 'https://head.image.fengniaojizhang.cn/webOther/b8.png', '一切顺利符', 0, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (17, 2, 1, 'https://head.image.fengniaojizhang.cn/webOther/q_a1.png', '向你丢心', 1, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (18, 2, 2, 'https://head.image.fengniaojizhang.cn/webOther/q_b1.png', '笑不出来', 1, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (19, 2, 1, 'https://head.image.fengniaojizhang.cn/webOther/q_a2.png', '咖啡杯', 1, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (20, 2, 2, 'https://head.image.fengniaojizhang.cn/webOther/q_b2.png', '汪汪汪', 1, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (21, 2, 1, 'https://head.image.fengniaojizhang.cn/webOther/q_a3.png', '爱心', 0, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (22, 2, 2, 'https://head.image.fengniaojizhang.cn/webOther/q_b3.png', '嫌弃', 0, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (23, 2, 1, 'https://head.image.fengniaojizhang.cn/webOther/q_a4.png', '发射爱心', 0, now());
INSERT INTO `hbird_festival_tags`(`id`, `festival_type`, `icon_type`, `icon`, `description`, `status`, `create_date`) VALUES (24, 2, 2, 'https://head.image.fengniaojizhang.cn/webOther/q_b4.png', '今日份狗粮', 0, now());



CREATE TABLE `hbird_user_festival_tags` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_info_id` int(11) DEFAULT NULL COMMENT '用户详情id',
  `tags_id` int(11) DEFAULT NULL COMMENT '贴纸-标语id',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_info_id` (`user_info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-节日贴纸-标语关联表';