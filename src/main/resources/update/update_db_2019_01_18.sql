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

CREATE TABLE `hbird_user_festival_tags` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_info_id` int(11) DEFAULT NULL COMMENT '用户详情id',
  `tags_id` int(11) DEFAULT NULL COMMENT '贴纸-标语id',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_info_id` (`user_info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户-节日贴纸-标语关联表';