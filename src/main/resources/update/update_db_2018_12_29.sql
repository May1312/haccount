-- 资产表   ui-ue改版


CREATE TABLE `hbird_assets` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `assets_type` int(2) DEFAULT NULL COMMENT '资产类型 1：现金 2：支付宝 3：微信 4：理财 5：社保 6：借记/储蓄卡 7：公交/校园等 8：出借待收 9：负债待还 10：其他账户 11：公积金 12：信用卡',
  `assets_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '资产类型名称',
  `icon` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '图标url',
  `priority` int(2) DEFAULT NULL COMMENT '优先级 升序',
  `status` int(1) DEFAULT NULL COMMENT '0:下线  1:上线',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

alter table hbird_assets comment '资产表';

INSERT INTO `hbird_assets`(`id`, `assets_type`, `assets_name`, `icon`, `priority`, `status`, `update_date`, `create_date`) VALUES (1, 1, '现金', 'http://label.image.fengniaojizhang.cn/assets/icon_zcxianjin_normal.png', 1, 1, NULL, '2018-12-28 17:21:24');
INSERT INTO `hbird_assets`(`id`, `assets_type`, `assets_name`, `icon`, `priority`, `status`, `update_date`, `create_date`) VALUES (2, 2, '支付宝', 'http://label.image.fengniaojizhang.cn/assets/icon_zczhifubao_normal.png', 2, 1, NULL, '2018-12-28 17:22:50');
INSERT INTO `hbird_assets`(`id`, `assets_type`, `assets_name`, `icon`, `priority`, `status`, `update_date`, `create_date`) VALUES (3, 3, '微信', 'http://label.image.fengniaojizhang.cn/assets/icon_zcweixin_normal.png', 3, 1, NULL, '2018-12-28 17:24:10');
INSERT INTO `hbird_assets`(`id`, `assets_type`, `assets_name`, `icon`, `priority`, `status`, `update_date`, `create_date`) VALUES (4, 12, '信用卡', 'http://label.image.fengniaojizhang.cn/assets/icon_zcxyk_normal.png', 4, 1, NULL, '2018-12-28 17:25:13');
INSERT INTO `hbird_assets`(`id`, `assets_type`, `assets_name`, `icon`, `priority`, `status`, `update_date`, `create_date`) VALUES (5, 4, '理财', 'http://label.image.fengniaojizhang.cn/assets/icon_zclicai_normal.png', 5, 1, NULL, '2018-12-28 17:25:52');
INSERT INTO `hbird_assets`(`id`, `assets_type`, `assets_name`, `icon`, `priority`, `status`, `update_date`, `create_date`) VALUES (6, 5, '社保', 'http://label.image.fengniaojizhang.cn/assets/icon_zcshebao_normal.png', 6, 1, NULL, '2018-12-28 17:26:28');
INSERT INTO `hbird_assets`(`id`, `assets_type`, `assets_name`, `icon`, `priority`, `status`, `update_date`, `create_date`) VALUES (7, 11, '公积金', 'http://label.image.fengniaojizhang.cn/assets/icon_zcgjj_normal.png', 7, 1, NULL, '2018-12-28 17:28:40');
INSERT INTO `hbird_assets`(`id`, `assets_type`, `assets_name`, `icon`, `priority`, `status`, `update_date`, `create_date`) VALUES (8, 6, '借记/储蓄卡', 'http://label.image.fengniaojizhang.cn/assets/icon_zcyinhangka_normal.png', 8, 1, NULL, '2018-12-28 17:28:42');
INSERT INTO `hbird_assets`(`id`, `assets_type`, `assets_name`, `icon`, `priority`, `status`, `update_date`, `create_date`) VALUES (9, 7, '公交/校园/等充值卡', 'http://label.image.fengniaojizhang.cn/assets/icon_zcgongjiaoka_normal.png', 9, 1, NULL, '2018-12-28 17:29:52');
INSERT INTO `hbird_assets`(`id`, `assets_type`, `assets_name`, `icon`, `priority`, `status`, `update_date`, `create_date`) VALUES (10, 8, '出借待收', 'http://label.image.fengniaojizhang.cn/assets/icon_zcjiekuan_normal.png', 10, 1, NULL, '2018-12-28 17:30:34');
INSERT INTO `hbird_assets`(`id`, `assets_type`, `assets_name`, `icon`, `priority`, `status`, `update_date`, `create_date`) VALUES (11, 9, '负债待还', 'http://label.image.fengniaojizhang.cn/assets/icon_zcqiankuan_normal.png', 11, 1, NULL, '2018-12-28 17:31:17');
INSERT INTO `hbird_assets`(`id`, `assets_type`, `assets_name`, `icon`, `priority`, `status`, `update_date`, `create_date`) VALUES (12, 10, '其他账户', 'http://label.image.fengniaojizhang.cn/assets/icon_zcqita_normal.png', 12, 1, NULL, '2018-12-28 17:31:55');

-- 添加字段
alter table hbird_water_order Add column assets_id int DEFAULT 0 comment '账户id(资产类型id)';
alter table hbird_water_order Add column assets_name varchar(64) comment '账户名称';

alter table hbird_user_assets Add column mark int(1) DEFAULT 0 comment '是否默认标记  0:非默认 1:默认';
alter table hbird_user_assets Add column assets_name varchar(64) comment '资产类型名称';

-- 修改用户--资产表

UPDATE hbird_user_assets AS base2,
( SELECT assets_type, assets_name FROM hbird_assets ) AS base3
SET base2.assets_name = base3.assets_name
WHERE
	base2.assets_type = base3.assets_type;


-- 同步 hbird_spend_type   hbird_income_type   提前创建两张表表


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for hbird_spend_type_bac
-- ----------------------------
DROP TABLE IF EXISTS `hbird_spend_type_bac`;
CREATE TABLE `hbird_spend_type_bac`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '类目id',
  `spend_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '支出类目名称',
  `parent_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '父级类目',
  `icon` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标',
  `status` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '状态(0:下线,1:上线)',
  `priority` int(11) NULL DEFAULT NULL COMMENT '优先级',
  `mark` int(2) NULL DEFAULT NULL COMMENT '常用字段,0:非常用,1:常用',
  `update_date` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `create_date` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `delflag` int(2) NULL DEFAULT NULL COMMENT '删除标记',
  `del_date` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统支出类目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hbird_spend_type_bac
-- ----------------------------
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f72fec0163f818eea4001b', '饮食', NULL, NULL, '1', 1, NULL, '2018-06-15 11:29:06', '2018-06-13 15:42:54', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f72fec0163f8198bfd001d', '交通', NULL, NULL, '1', 2, NULL, '2018-06-14 10:55:18', '2018-06-13 15:43:34', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f72fec0163f819b2f7001f', '娱乐', NULL, NULL, '1', 3, NULL, '2018-06-14 10:55:18', '2018-06-13 15:43:44', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f72fec0163f819daee0021', '购物', NULL, NULL, '1', 4, NULL, '2018-06-14 10:55:18', '2018-06-13 15:43:54', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f72fec0163f81a22920023', '形象', NULL, NULL, '1', 5, NULL, '2018-08-22 15:37:33', '2018-06-13 15:44:12', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f72fec0163f81a74fb0025', '家庭日常', NULL, NULL, '1', 6, NULL, '2018-08-22 15:37:51', '2018-06-13 15:44:34', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f72fec0163f81a9ae40027', '社交', NULL, NULL, '1', 7, NULL, '2018-08-22 15:38:06', '2018-06-13 15:44:43', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f72fec0163f81ac3410029', '生活服务', NULL, NULL, '1', 9, NULL, '2018-08-22 15:38:06', '2018-06-13 15:44:54', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f72fec0163f81b013e002b', '学习培训', NULL, NULL, '1', 11, NULL, '2018-08-22 15:38:06', '2018-06-13 15:45:09', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f81e65390000', '医疗', NULL, NULL, '1', 10, NULL, '2018-08-22 15:38:06', '2018-06-13 15:48:52', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f81ebf160002', '运动健康', NULL, NULL, '1', 8, NULL, '2018-08-22 15:38:06', '2018-06-13 15:49:15', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f81ee5050004', '旅行', NULL, NULL, '1', 13, NULL, '2018-08-22 15:43:21', '2018-06-13 15:49:24', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f81f0c8c0006', '金融', NULL, NULL, '1', 12, NULL, '2018-08-22 15:39:48', '2018-06-13 15:49:34', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f81f59040008', '其他', NULL, NULL, '1', 14, NULL, '2018-08-22 15:43:21', '2018-06-13 15:49:54', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f83d33320016', '饮食', '2c91dbe363f72fec0163f818eea4001b', 'http://label.image.fengniaojizhang.cn/1547087975355_icon_yinshi_normal @3x.png', '1', 1, 1, '2019-01-10 10:39:38', '2018-06-13 16:22:30', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f83deeba0018', '水果', '2c91dbe363f72fec0163f818eea4001b', 'http://label.image.fengniaojizhang.cn/1547088539579_icon_shuiguo_normal@3x.png', '1', 3, 1, '2019-01-10 10:49:01', '2018-06-13 16:23:18', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f83f80e5001a', '零食', '2c91dbe363f72fec0163f818eea4001b', 'http://label.image.fengniaojizhang.cn/1547088556305_icon_lingshi_normal@3x.png', '1', 5, 1, '2019-01-10 10:49:18', '2018-06-13 16:25:01', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f8407099001c', '烟酒', '2c91dbe363f72fec0163f818eea4001b', 'http://label.image.fengniaojizhang.cn/1547088575984_icon_yanjiu_normal@3x.png', '1', 6, 1, '2019-01-10 10:49:37', '2018-06-13 16:26:03', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f84c33c10021', '乳品', '2c91dbe363f72fec0163f818eea4001b', 'http://label.image.fengniaojizhang.cn/1547088594211_icon_rupin_normal@3x.png', '1', 7, 0, '2019-01-10 10:49:55', '2018-06-13 16:38:54', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f84ce7dc0023', '饮品', '2c91dbe363f72fec0163f818eea4001b', 'http://label.image.fengniaojizhang.cn/1547185879274_icon_yinpin_normal@3x.png', '1', 8, 1, '2019-01-11 13:51:22', '2018-06-13 16:39:40', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f84d73be0025', '蔬菜', '2c91dbe363f72fec0163f818eea4001b', 'http://label.image.fengniaojizhang.cn/1547088827706_icon_shucai_normal@3x.png', '1', 9, 1, '2019-01-10 10:53:50', '2018-06-13 16:40:16', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f84e7e710029', '肉类', '2c91dbe363f72fec0163f818eea4001b', 'http://label.image.fengniaojizhang.cn/1547088936579_icon_roulei_normal@3x.png', '1', 10, 0, '2019-01-10 10:55:39', '2018-06-13 16:41:24', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f850d4ed002b', '海鲜', '2c91dbe363f72fec0163f818eea4001b', 'http://label.image.fengniaojizhang.cn/1547088959221_icon_haixian_normal@3x.png', '1', 11, 0, '2019-01-10 10:56:01', '2018-06-13 16:43:57', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f8514de9002d', '豆制品', '2c91dbe363f72fec0163f818eea4001b', 'http://label.image.fengniaojizhang.cn/1547088981350_Icon_douzhipin_normal@3x.png', '1', 12, 0, '2019-01-10 10:56:26', '2018-06-13 16:44:28', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f851a291002f', '蛋类', '2c91dbe363f72fec0163f818eea4001b', 'http://label.image.fengniaojizhang.cn/1547090804027_icon_danlei_normal@3x.png', '1', 13, 0, '2019-01-10 11:26:46', '2018-06-13 16:44:50', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f852344d0031', '粮油', '2c91dbe363f72fec0163f818eea4001b', 'http://label.image.fengniaojizhang.cn/1547090863423_icon_liangyou_normal@3x.png', '1', 14, 0, '2019-01-10 11:27:47', '2018-06-13 16:45:27', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f8537b960033', '酱料', '2c91dbe363f72fec0163f818eea4001b', 'http://label.image.fengniaojizhang.cn/1547090891584_icon_jiangliao_normal@3x.png', '1', 15, 0, '2019-01-10 11:28:13', '2018-06-13 16:46:51', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f8545d550035', '厨具', '2c91dbe363f72fec0163f818eea4001b', 'http://label.image.fengniaojizhang.cn/1547090930944_icon_chuju_normal@3x.png', '1', 16, 0, '2019-01-10 11:28:52', '2018-06-13 16:47:49', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f8598cee0037', '交通通勤', '2c91dbe363f72fec0163f8198bfd001d', 'http://label.image.fengniaojizhang.cn/1547090977604_icon_jttq_normal@3x.png', '1', 2, 1, '2019-01-10 11:29:39', '2018-06-13 16:53:28', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f85c0809003b', '设备工具', '2c91dbe363f72fec0163f8198bfd001d', 'http://label.image.fengniaojizhang.cn/1547091289529_icon_sbgj_normal@3x.png', '1', 4, 0, '2019-01-10 11:35:02', '2018-06-13 16:56:11', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f85d6dc7003f', '加油', '2c91dbe363f72fec0163f8198bfd001d', 'http://label.image.fengniaojizhang.cn/1547091426551_icon_jiayou_normal@3x.png', '1', 17, 1, '2019-01-10 11:37:10', '2018-06-13 16:57:43', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f86d0fc30041', '充电', '2c91dbe363f72fec0163f8198bfd001d', 'http://label.image.fengniaojizhang.cn/1547091446449_icon_chongdian_normal@3x.png', '1', 18, 0, '2019-01-10 11:37:29', '2018-06-13 17:14:47', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f86dc73d0043', '停车', '2c91dbe363f72fec0163f8198bfd001d', 'http://label.image.fengniaojizhang.cn/1547091464674_icon_tingche_normal@3x.png', '1', 19, 1, '2019-01-10 11:37:47', '2018-06-13 17:15:34', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f86e893c0045', '飞行', '2c91dbe363f72fec0163f8198bfd001d', 'http://label.image.fengniaojizhang.cn/1547091483917_icon_feixing_normal@3x.png', '1', 20, 0, '2019-01-10 11:38:08', '2018-06-13 17:16:24', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f870604b0047', '船舶', '2c91dbe363f72fec0163f8198bfd001d', 'http://label.image.fengniaojizhang.cn/1547091576472_icon_chuanbo_normal@3x.png', '1', 21, 0, '2019-01-10 11:39:40', '2018-06-13 17:18:24', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f89c59540053', '过路费', '2c91dbe363f72fec0163f8198bfd001d', 'http://label.image.fengniaojizhang.cn/1547091599232_icon_glf_normal@3x.png', '1', 22, 0, '2019-01-10 11:40:02', '2018-06-13 18:06:26', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f89ce3990055', '汽车', '2c91dbe363f72fec0163f8198bfd001d', 'http://label.image.fengniaojizhang.cn/1547092352985_icon_qiche_normal@3x.png', '1', 23, 0, '2019-01-10 11:52:38', '2018-06-13 18:07:02', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f89d417c0057', '保险', '2c91dbe363f72fec0163f8198bfd001d', 'http://label.image.fengniaojizhang.cn/1547092373299_icon_baoxian_normal@3x.png', '1', 24, 0, '2019-01-10 11:52:57', '2018-06-13 18:07:26', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f89da7f00059', '保养', '2c91dbe363f72fec0163f8198bfd001d', 'http://label.image.fengniaojizhang.cn/1547092390888_icon_baoyang_normal@3x.png', '1', 25, 0, '2019-01-10 11:53:13', '2018-06-13 18:07:52', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f89e4f40005b', '牌照', '2c91dbe363f72fec0163f8198bfd001d', 'http://label.image.fengniaojizhang.cn/1547092430361_icon_paizhao_normal@3x.png', '1', 26, 0, '2019-01-10 11:53:55', '2018-06-13 18:08:35', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f89f8d66005d', '娱乐', '2c91dbe363f72fec0163f819b2f7001f', 'http://label.image.fengniaojizhang.cn/1547092459539_icon_yule_normal@3x.png', '1', 27, 1, '2019-01-10 11:54:22', '2018-06-13 18:09:56', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f8a01013005f', 'ktv', '2c91dbe363f72fec0163f819b2f7001f', 'http://label.image.fengniaojizhang.cn/1547092474336_icon_ktv_normal@3x.png', '1', 28, 1, '2019-01-10 11:54:36', '2018-06-13 18:10:30', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f8aa40fa0061', '电影', '2c91dbe363f72fec0163f819b2f7001f', 'http://label.image.fengniaojizhang.cn/1547092489172_icon_dianying_normal@3x.png', '1', 29, 1, '2019-01-10 11:54:51', '2018-06-13 18:21:37', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f8aa97f00063', '酒吧', '2c91dbe363f72fec0163f819b2f7001f', 'http://label.image.fengniaojizhang.cn/1547092628172_icon_jiuba_normal@3x.png', '1', 30, 0, '2019-01-10 11:57:10', '2018-06-13 18:22:00', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f8aae6280065', '轰趴', '2c91dbe363f72fec0163f819b2f7001f', 'http://label.image.fengniaojizhang.cn/1547097114354_icon_hongpa_normal@3x.png', '1', 31, 0, '2019-01-10 13:11:57', '2018-06-13 18:22:20', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f8ab40330067', 'DIY', '2c91dbe363f72fec0163f819b2f7001f', 'http://label.image.fengniaojizhang.cn/1547097127607_icon_diy_normal@3x.png', '1', 32, 0, '2019-01-10 13:12:11', '2018-06-13 18:22:43', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f8abbcba0069', '棋牌', '2c91dbe363f72fec0163f819b2f7001f', 'http://label.image.fengniaojizhang.cn/1547097145001_icon_qipai_normal@3x.png', '1', 33, 0, '2019-01-10 13:12:27', '2018-06-13 18:23:15', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f8ac29ba006b', '真人CS', '2c91dbe363f72fec0163f819b2f7001f', 'http://label.image.fengniaojizhang.cn/1547097177964_icon_zrcs_normal@3x.png', '1', 34, 0, '2019-01-10 13:13:00', '2018-06-13 18:23:43', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f8aca49e006d', '密室逃脱', '2c91dbe363f72fec0163f819b2f7001f', 'http://label.image.fengniaojizhang.cn/1547097194815_icon_mstt_normal@3x.png', '1', 35, 0, '2019-01-10 13:13:17', '2018-06-13 18:24:14', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f81ded0163f8b76479007c', '购物', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097211224_icon_gouwu_normal@3x.png', '1', 36, 1, '2019-01-10 13:13:33', '2018-06-13 18:35:58', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f8b9390163f8c5deab0008', '钻石', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097224642_icon_zuanshi_normal@3x.png', '1', 37, 0, '2019-01-10 13:13:47', '2018-06-13 18:51:47', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f8b9390163f8c865680016', '手表', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097238669_icon_shoubiao_normal@3x.png', '1', 38, 0, '2019-01-10 13:14:01', '2018-06-13 18:54:33', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f8b9390163f8c905820019', '数码', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097252401_icon_shuma_normal@3x.png', '1', 39, 0, '2019-01-10 13:14:15', '2018-06-13 18:55:14', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f8b9390163f8d05f9f0024', '日化', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097266079_icon_rihua_normal@3x.png', '1', 40, 1, '2019-01-10 13:14:27', '2018-06-13 19:03:16', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f8b9390163fc36f2010027', '日用', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097773416_icon_riyong_normal@3x.png', '1', 41, 1, '2019-01-10 13:22:55', '2018-06-14 10:54:09', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363f8b9390163fc393ed4002b', '鲜花', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097786528_icon_xianhua_normal@3x.png', '1', 42, 0, '2019-01-10 13:23:08', '2018-06-14 10:56:40', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc41dd320004', '计生用品', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097800831_icon_jsyp_normal@3x.png', '1', 43, 1, '2019-01-10 13:23:24', '2018-06-14 11:06:05', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc460c4b0008', '内衣配饰', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097819056_icon_nyps_normal@3x.png', '1', 44, 1, '2019-01-10 13:23:41', '2018-06-14 11:10:39', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc537b96000a', '箱包', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097833823_icon_xiangbao_normal@3x.png', '1', 45, 0, '2019-01-10 13:23:56', '2018-06-14 11:25:20', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc647c1d000c', '服饰', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097846931_icon_fushi_normal@3x.png', '1', 46, 1, '2019-01-10 13:24:10', '2018-06-14 11:43:54', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc64ebc8000e', '鞋袜', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097869310_icon_xiezi_normal@3x.png', '1', 47, 1, '2019-01-10 13:24:32', '2018-06-14 11:44:22', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc660af90010', '家具', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097884780_icon_jiaju_normal@3x.png', '1', 48, 0, '2019-01-10 13:24:48', '2018-06-14 11:45:36', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc6699ec0012', '家电', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097901280_icon_jiadian_normal@3x.png', '1', 53, 0, '2019-01-10 13:25:05', '2018-06-14 11:46:13', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc66f9440014', '网络服务', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097919321_icon_wlfw_normal@3x.png', '1', 49, 1, '2019-01-10 13:25:22', '2018-06-14 11:46:37', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc6b16c20016', '母婴', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547097965684_icon_muyin_normal@3x.png', '1', 50, 0, '2019-01-10 13:26:09', '2018-06-14 11:51:07', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc6d32aa0018', '游戏电玩', '2c91dbe363f72fec0163f819b2f7001f', 'http://label.image.fengniaojizhang.cn/1547097980336_icon_yxdw_normal@3x.png', '1', 51, 1, '2019-01-10 13:26:23', '2018-06-14 11:53:25', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc6f66fd001b', 'SPA', '2c91dbe363f72fec0163f81a22920023', 'http://label.image.fengniaojizhang.cn/1547097993659_icon_spa_normal@3x.png', '1', 52, 0, '2019-01-10 13:26:36', '2018-06-14 11:55:49', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc7002b6001e', '护肤化妆', '2c91dbe363f72fec0163f81a22920023', 'http://label.image.fengniaojizhang.cn/1547098008562_icon_hfhz_normal@3x.png', '1', 54, 1, '2019-01-10 13:26:52', '2018-06-14 11:56:29', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc7127f60020', '美发', '2c91dbe363f72fec0163f81a22920023', 'http://label.image.fengniaojizhang.cn/1547098029423_icon_meifa_normal@3x.png', '1', 55, 1, '2019-01-10 13:27:13', '2018-06-14 11:57:44', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc71b7ac0022', '纹身', '2c91dbe363f72fec0163f81a22920023', 'http://label.image.fengniaojizhang.cn/1547098044171_icon_wenshen_normal@3x.png', '1', 56, 0, '2019-01-10 13:27:27', '2018-06-14 11:58:21', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc72d5e30025', '美容', '2c91dbe363f72fec0163f81a22920023', 'http://label.image.fengniaojizhang.cn/1547098160595_icon_meirong_normal@3x.png', '1', 57, 1, '2019-01-10 13:29:24', '2018-06-14 11:59:34', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fc73f6f80027', '饰品', '2c91dbe363f72fec0163f81a22920023', 'http://label.image.fengniaojizhang.cn/1547098177073_icon_shipin_normal@3x.png', '1', 58, 1, '2019-01-10 13:29:39', '2018-06-14 12:00:48', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fcb54222002b', '水电煤', '2c91dbe363f72fec0163f81a74fb0025', 'http://label.image.fengniaojizhang.cn/1547098207205_icon_sdm_normal@3x.png', '1', 59, 1, '2019-01-10 13:30:09', '2018-06-14 13:12:07', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fcb5fec7002d', '话费', '2c91dbe363f72fec0163f81a74fb0025', 'http://label.image.fengniaojizhang.cn/1547098272905_icon_tongxun_normal@3x.png', '1', 60, 1, '2019-01-10 13:31:16', '2018-06-14 13:12:56', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fcb6b34d002f', '宠物', '2c91dbe363f72fec0163f81a74fb0025', 'http://label.image.fengniaojizhang.cn/1547098312540_icon_chongwu_normal@3x.png', '1', 61, 1, '2019-01-10 13:31:54', '2018-06-14 13:13:42', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fcb7c6cf0031', '摄影', '2c91dbe363f72fec0163f81a74fb0025', 'http://label.image.fengniaojizhang.cn/1547098324776_icon_sheying_normal@3x.png', '1', 62, 0, '2019-01-10 13:32:07', '2018-06-14 13:14:52', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fcf72aa20034', '盆栽', '2c91dbe363f72fec0163f81a74fb0025', 'http://label.image.fengniaojizhang.cn/1547098337075_icon_penzai_normal@3x.png', '1', 63, 0, '2019-01-10 13:32:20', '2018-06-14 14:24:07', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fcf798c30036', '装修', '2c91dbe363f72fec0163f81a74fb0025', 'http://label.image.fengniaojizhang.cn/1547098351893_icon_zhuangxiu_normal@3x.png', '1', 64, 0, '2019-01-10 13:32:34', '2018-06-14 14:24:35', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fcf83e530038', '住房', '2c91dbe363f72fec0163f81a74fb0025', 'http://label.image.fengniaojizhang.cn/1547098366333_icon_zhufang_normal@3x.png', '1', 65, 1, '2019-01-10 13:32:48', '2018-06-14 14:25:17', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fcf9a54b003a', '物业', '2c91dbe363f72fec0163f81a74fb0025', 'http://label.image.fengniaojizhang.cn/1547098379680_icon_wuye_normal@3x.png', '1', 67, 0, '2019-01-10 13:33:02', '2018-06-14 14:26:49', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fcfa221e003c', '网费', '2c91dbe363f72fec0163f81a74fb0025', 'http://label.image.fengniaojizhang.cn/1547098392027_icon_wangfei_normal@3x.png', '1', 66, 1, '2019-01-10 13:33:14', '2018-06-14 14:27:21', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fcfba8a0003e', '社交', '2c91dbe363f72fec0163f81a9ae40027', 'http://label.image.fengniaojizhang.cn/1547098404335_icon_shejiao_normal@3x.png', '1', 68, 1, '2019-01-10 13:33:26', '2018-06-14 14:29:01', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fcfc2f8b0040', '丧葬', '2c91dbe363f72fec0163f81a9ae40027', 'http://label.image.fengniaojizhang.cn/1547098418099_icon_sangzang_normal@3x.png', '1', 69, 0, '2019-01-10 13:33:40', '2018-06-14 14:29:36', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd18473f0042', '嫁娶', '2c91dbe363f72fec0163f81a9ae40027', 'http://label.image.fengniaojizhang.cn/1547098431521_icon_jiaqu_normal@3x.png', '1', 70, 0, '2019-01-10 13:33:54', '2018-06-14 15:00:17', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd1b174f0044', '红包', '2c91dbe363f72fec0163f81a9ae40027', 'http://label.image.fengniaojizhang.cn/1547098491774_icon_hongbao_normal@3x.png', '1', 71, 1, '2019-01-10 13:34:53', '2018-06-14 15:03:21', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd1d5b770047', '捐赠', '2c91dbe363f72fec0163f81a9ae40027', 'http://label.image.fengniaojizhang.cn/1547108681564_icon_juanzeng_normal@3x.png', '1', 72, 0, '2019-01-10 16:24:44', '2018-06-14 15:05:50', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd1de9930049', '礼品/金', '2c91dbe363f72fec0163f81a9ae40027', 'http://label.image.fengniaojizhang.cn/1547108700883_icon_lipin_normal@3x.png', '1', 73, 0, '2019-01-10 16:25:02', '2018-06-14 15:06:26', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd23f8d6004b', '亲友', '2c91dbe363f72fec0163f81a9ae40027', 'http://label.image.fengniaojizhang.cn/1547108714553_icon_qinyou_normal@3x.png', '1', 75, 1, '2019-01-10 16:25:18', '2018-06-14 15:13:03', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd245e05004d', '搬家', '2c91dbe363f72fec0163f81ac3410029', 'http://label.image.fengniaojizhang.cn/1547108730925_icon_banjia_normal@3x.png', '1', 74, 0, '2019-01-10 16:25:32', '2018-06-14 15:13:29', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd253d70004f', '维修', '2c91dbe363f72fec0163f81ac3410029', 'http://label.image.fengniaojizhang.cn/1547108742372_icon_weixiu_normal@3x.png', '1', 76, 0, '2019-01-10 16:25:44', '2018-06-14 15:14:26', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd25e58b0051', '保洁', '2c91dbe363f72fec0163f81ac3410029', 'http://label.image.fengniaojizhang.cn/1547185979744_icon_baojie_normal@3x.png', '1', 77, 0, '2019-01-11 13:53:01', '2018-06-14 15:15:09', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd26cca00053', '快递', '2c91dbe363f72fec0163f81ac3410029', 'http://label.image.fengniaojizhang.cn/1547108792578_icon_kuaidi_normal@3x.png', '1', 79, 1, '2019-01-10 16:26:34', '2018-06-14 15:16:08', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd27e2540055', '律师', '2c91dbe363f72fec0163f81ac3410029', 'http://label.image.fengniaojizhang.cn/1547108806698_icon_lvshi_normal@3x.png', '1', 78, 0, '2019-01-10 16:26:50', '2018-06-14 15:17:20', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd289b760057', '保姆', '2c91dbe363f72fec0163f81ac3410029', 'http://label.image.fengniaojizhang.cn/1547108908420_icon_baomu_normal@3x.png', '1', 80, 0, '2019-01-10 16:28:31', '2018-06-14 15:18:07', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd29157b0059', '换锁', '2c91dbe363f72fec0163f81ac3410029', 'http://label.image.fengniaojizhang.cn/1547099479647_icon_huansuo_normal@3x.png', '1', 81, 0, '2019-01-10 13:51:22', '2018-06-14 15:18:38', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd2df0be005b', '书籍教材', '2c91dbe363f72fec0163f81b013e002b', 'http://label.image.fengniaojizhang.cn/1547099493192_icon_sjjc_normal@3x.png', '1', 82, 1, '2019-01-10 13:51:34', '2018-06-14 15:23:56', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd2f3653005e', '办公', '2c91dbe363f72fec0163f81b013e002b', 'http://label.image.fengniaojizhang.cn/1547099504912_icon_bangong_normal@3x.png', '1', 83, 1, '2019-01-10 13:51:46', '2018-06-14 15:25:20', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd3256e50062', '音乐培训', '2c91dbe363f72fec0163f81b013e002b', 'http://label.image.fengniaojizhang.cn/1547099518168_icon_yypx_normal@3x.png', '1', 85, 0, '2019-01-10 13:52:01', '2018-06-14 15:28:45', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd32fe130064', '美术培训', '2c91dbe363f72fec0163f81b013e002b', 'http://label.image.fengniaojizhang.cn/1547099535154_icon_mspx_normal@3x.png', '1', 84, 0, '2019-01-10 13:52:17', '2018-06-14 15:29:28', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd5d2e6d0067', '舞蹈培训', '2c91dbe363f72fec0163f81b013e002b', 'http://label.image.fengniaojizhang.cn/1547099549319_icon_wdpx_normal@3x.png', '1', 86, 0, '2019-01-10 13:52:31', '2018-06-14 16:15:32', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd5e4ef50069', '外语', '2c91dbe363f72fec0163f81b013e002b', 'http://label.image.fengniaojizhang.cn/1547099563748_icon_waiyu_normal@3x.png', '1', 87, 0, '2019-01-10 13:52:47', '2018-06-14 16:16:46', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd5f6bbb006b', '证书', '2c91dbe363f72fec0163f81b013e002b', 'http://label.image.fengniaojizhang.cn/1547099577591_icon_zhengshu_normal@3x.png', '1', 88, 0, '2019-01-10 13:53:01', '2018-06-14 16:17:59', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd5fd4d9006d', '在线学习', '2c91dbe363f72fec0163f81b013e002b', 'http://label.image.fengniaojizhang.cn/1547186009282_在线学习_30px@3x.png', '1', 89, 1, '2019-01-11 13:53:31', '2018-06-14 16:18:26', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd6081cc006f', '学费', '2c91dbe363f72fec0163f81b013e002b', 'http://label.image.fengniaojizhang.cn/1547185902499_icon_xuefei_normal@3x.png', '1', 90, 0, '2019-01-11 13:51:44', '2018-06-14 16:19:10', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd61e3700071', '医疗', '2c91dbe363f81ded0163f81e65390000', 'http://label.image.fengniaojizhang.cn/1547099659039_icon_yiliao_normal@3x.png', '1', 91, 0, '2019-01-10 13:54:21', '2018-06-14 16:20:41', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd65fb720073', '体检', '2c91dbe363f81ded0163f81e65390000', 'http://label.image.fengniaojizhang.cn/1547099671014_icon_tijian_normal@3x.png', '1', 92, 0, '2019-01-10 13:54:33', '2018-06-14 16:25:09', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd68f6480075', '药物', '2c91dbe363f81ded0163f81e65390000', 'http://label.image.fengniaojizhang.cn/1547099683939_icon_yaowu_normal@3x.png', '1', 93, 0, '2019-01-10 13:54:47', '2018-06-14 16:28:25', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd6acfc20077', '医美', '2c91dbe363f81ded0163f81e65390000', 'http://label.image.fengniaojizhang.cn/1547099698228_icon_yimei_normal@3x.png', '1', 94, 0, '2019-01-10 13:55:00', '2018-06-14 16:30:26', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd6b44490079', '牙医', '2c91dbe363f81ded0163f81e65390000', 'http://label.image.fengniaojizhang.cn/1547099710454_icon_yayi_normal@3x.png', '1', 95, 0, '2019-01-10 13:55:13', '2018-06-14 16:30:56', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd6be282007b', '住院', '2c91dbe363f81ded0163f81e65390000', 'http://label.image.fengniaojizhang.cn/1547099723268_icon_zhuyuan_normal@3x.png', '1', 97, 0, '2019-01-10 13:55:26', '2018-06-14 16:31:36', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd6c46d8007d', 'ICU', '2c91dbe363f81ded0163f81e65390000', 'http://label.image.fengniaojizhang.cn/1547099782589_Group 94@3x.png', '1', 96, 0, '2019-01-10 13:56:24', '2018-06-14 16:32:02', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd6cee1f007f', '新生儿', '2c91dbe363f81ded0163f81e65390000', 'http://label.image.fengniaojizhang.cn/1547099803064_icon_xinshenger_normal@3x.png', '1', 98, 0, '2019-01-10 13:56:45', '2018-06-14 16:32:45', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd6d47150081', '孕妇', '2c91dbe363f81ded0163f81e65390000', 'http://label.image.fengniaojizhang.cn/1547099817724_icon_yunfu_normal@3x.png', '1', 99, 0, '2019-01-10 13:57:00', '2018-06-14 16:33:07', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd6e57c60083', '心理医生', '2c91dbe363f81ded0163f81e65390000', 'http://label.image.fengniaojizhang.cn/1547099845684_icon_xinliyisheng_normal@3x.png', '1', 100, 0, '2019-01-10 13:57:28', '2018-06-14 16:34:17', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd6ffc840085', '运动健身', '2c91dbe363f81ded0163f81ebf160002', 'http://label.image.fengniaojizhang.cn/1547099899572_icon_yundongjianshen_normal@3x.png', '1', 101, 1, '2019-01-10 13:58:22', '2018-06-14 16:36:05', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd75e5120091', '汗蒸', '2c91dbe363f81ded0163f81ebf160002', 'http://label.image.fengniaojizhang.cn/1547099933622_icon_hanzheng_normal@3x.png', '1', 103, 0, '2019-01-10 13:58:57', '2018-06-14 16:42:32', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd7630cf0093', '按摩', '2c91dbe363f81ded0163f81ebf160002', 'http://label.image.fengniaojizhang.cn/1547099952626_icon_anmo_normal@3x.png', '1', 102, 0, '2019-01-10 13:59:19', '2018-06-14 16:42:51', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd77052d0095', '养生', '2c91dbe363f81ded0163f81ebf160002', 'http://label.image.fengniaojizhang.cn/1547100107069_icon_yangsheng_normal@3x.png', '1', 104, 0, '2019-01-10 14:01:55', '2018-06-14 16:43:46', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd7a981b009d', '电话卡', '2c91dbe363f81ded0163f81ee5050004', 'http://label.image.fengniaojizhang.cn/1547100130091_icon_dianhuaka_normal@3x.png', '1', 105, 0, '2019-01-10 14:02:13', '2018-06-14 16:47:40', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fd9447d000b9', '乐器', '2c91dbe363f72fec0163f81b013e002b', 'http://label.image.fengniaojizhang.cn/1547100144446_icon_yueqi_normal@3x.png', '1', 106, 0, '2019-01-10 14:02:28', '2018-06-14 17:15:43', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fe22f4d80123', '火车高铁', '2c91dbe363f72fec0163f8198bfd001d', 'http://label.image.fengniaojizhang.cn/1547100163063_icon_huoche_normal@3x.png', '1', 107, 0, '2019-01-10 14:02:45', '2018-06-14 19:51:34', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe363fc3f800163fe239b1a0125', '热气球', '2c91dbe363f72fec0163f8198bfd001d', 'http://label.image.fengniaojizhang.cn/1547100183438_icon_rqq_normal@3x.png', '1', 109, 0, '2019-01-10 14:03:05', '2018-06-14 19:52:16', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af701655f8ebcf8006b', '罚款', '2c91dbe363f72fec0163f8198bfd001d', 'http://label.image.fengniaojizhang.cn/1547100196114_icon_fakuan_normal@3x.png', '1', 110, 0, '2019-01-10 14:03:20', '2018-08-22 10:55:15', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af701656053e6fc0081', '游戏动漫', '2c91dbe363f72fec0163f819daee0021', 'http://label.image.fengniaojizhang.cn/1547100212698_icon_yxdm_normal@3x.png', '1', 108, 0, '2019-01-10 14:03:35', '2018-08-22 14:30:36', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af701656055aaad0084', '保险', '2c91dbe363f81ded0163f81f0c8c0006', 'http://label.image.fengniaojizhang.cn/1547092373299_icon_baoxian_normal@3x.png', '1', 111, 0, '2019-01-10 14:07:01', '2018-08-22 14:32:32', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af7016560565b730086', '还款', '2c91dbe363f81ded0163f81f0c8c0006', 'http://label.image.fengniaojizhang.cn/1547100431141_icon_huankuan_normal@3x.png', '1', 112, 0, '2019-01-10 14:07:14', '2018-08-22 14:33:17', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af7016560572f870088', '借款', '2c91dbe363f81ded0163f81f0c8c0006', 'http://label.image.fengniaojizhang.cn/1547100445536_icon_jiekuan_normal@3x.png', '1', 113, 0, '2019-01-10 14:07:28', '2018-08-22 14:34:11', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af7016560578b15008a', '赔偿', '2c91dbe363f81ded0163f81f0c8c0006', 'http://label.image.fengniaojizhang.cn/1547186037991_icon_peichang_normal@3x.png', '1', 114, 0, '2019-01-11 13:53:59', '2018-08-22 14:34:35', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af701656057d85b008c', '手续费', '2c91dbe363f81ded0163f81f0c8c0006', 'http://label.image.fengniaojizhang.cn/1547100476856_icon_shouxufei_normal@3x.png', '1', 115, 0, '2019-01-10 14:07:59', '2018-08-22 14:34:54', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af701656058cc1c008e', '税收', '2c91dbe363f81ded0163f81f0c8c0006', 'http://label.image.fengniaojizhang.cn/1547100493899_icon_shuishou_normal@3x.png', '1', 116, 0, '2019-01-10 14:08:17', '2018-08-22 14:35:57', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af70165605a24fd0090', '包车', '2c91dbe363f81ded0163f81ee5050004', 'http://label.image.fengniaojizhang.cn/1547100562551_icon_baoche_normal@3x.png', '1', 117, 0, '2019-01-10 14:09:25', '2018-08-22 14:37:25', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af70165605b1ef20094', '翻译', '2c91dbe363f81ded0163f81ee5050004', 'http://label.image.fengniaojizhang.cn/1547100575442_icon_fanyi_normal@3x.png', '1', 118, 0, '2019-01-10 14:09:38', '2018-08-22 14:38:29', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af70165605ba3980096', '护照', '2c91dbe363f81ded0163f81ee5050004', 'http://label.image.fengniaojizhang.cn/1547100369733_icon_huzhao_normal@3x.png', '1', 119, 0, '2019-01-10 14:06:12', '2018-08-22 14:39:03', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af7016560689de9009a', '寄存', '2c91dbe363f81ded0163f81ee5050004', 'http://label.image.fengniaojizhang.cn/1547100326537_icon_jicun_normal@3x.png', '1', 120, 0, '2019-01-10 14:05:29', '2018-08-22 14:53:14', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af70165606909ef009c', '酒店', '2c91dbe363f81ded0163f81ee5050004', 'http://label.image.fengniaojizhang.cn/1547100614565_icon_jiudian_normal@3x.png', '1', 122, 1, '2019-01-10 14:10:17', '2018-08-22 14:53:41', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af70165606e969400a1', '旅行', '2c91dbe363f81ded0163f81ee5050004', 'http://label.image.fengniaojizhang.cn/1547100627339_icon_lvxing_normal@3x.png', '1', 121, 0, '2019-01-10 14:10:29', '2018-08-22 14:59:45', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af70165606ef15700a3', '门票', '2c91dbe363f81ded0163f81ee5050004', 'http://label.image.fengniaojizhang.cn/1547100639472_icon_menpiao_normal@3x.png', '1', 123, 0, '2019-01-10 14:10:42', '2018-08-22 15:00:08', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af7016560705a5600a7', '物流', '2c91dbe363f72fec0163f81ac3410029', 'http://label.image.fengniaojizhang.cn/1547100742817_icon_wuliu_normal@3x.png', '1', 124, 1, '2019-01-10 14:12:27', '2018-08-22 15:01:41', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af701656072b31400af', '足疗', '2c91dbe363f81ded0163f81ebf160002', 'http://label.image.fengniaojizhang.cn/1547100756195_icon_zuliao_normal@3x.png', '1', 125, 0, '2019-01-10 14:12:40', '2018-08-22 15:04:14', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af701656073ee6000b1', '保健', '2c91dbe363f81ded0163f81ebf160002', 'http://label.image.fengniaojizhang.cn/1547100770326_icon_baojian_normal@3x.png', '1', 126, 0, '2019-01-10 14:12:52', '2018-08-22 15:05:35', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af7016560745a7800b3', '场馆', '2c91dbe363f81ded0163f81ebf160002', 'http://label.image.fengniaojizhang.cn/1547100785582_icon_changguan_normal@3x.png', '1', 127, 0, '2019-01-10 14:13:08', '2018-08-22 15:06:03', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af7016560757ea900b7', '私教', '2c91dbe363f81ded0163f81ebf160002', 'http://label.image.fengniaojizhang.cn/1547100797265_icon_sijiao_normal@3x.png', '1', 128, 0, '2019-01-10 14:13:19', '2018-08-22 15:07:18', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af701656075ced600b9', '运动设备', '2c91dbe363f81ded0163f81ebf160002', 'http://label.image.fengniaojizhang.cn/1547100821034_icon_yundongzhuangbei_normal@3x.png', '1', 129, 1, '2019-01-10 14:13:44', '2018-08-22 15:07:38', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af7016560765f4500bb', '其他', '2c91dbe363f81ded0163f81f59040008', 'http://label.image.fengniaojizhang.cn/1547100835791_icon_qita_normal@3x.png', '1', 130, 1, '2019-01-10 14:13:58', '2018-08-22 15:08:15', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af7016560769a6300bd', '慈善', '2c91dbe363f81ded0163f81f59040008', 'http://label.image.fengniaojizhang.cn/1547100940614_icon_cishan_normal@3x.png', '1', 131, 0, '2019-01-10 14:15:43', '2018-08-22 15:08:30', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af701656076e40d00bf', '彩票', '2c91dbe363f81ded0163f81f59040008', 'http://label.image.fengniaojizhang.cn/1547100952851_icon_caipiao_normal@3x.png', '1', 132, 0, '2019-01-10 14:15:55', '2018-08-22 15:08:49', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af7016560776ecb00c1', '意外损失', '2c91dbe363f81ded0163f81f59040008', 'http://label.image.fengniaojizhang.cn/1547100969540_icon_yiwaisunshi_normal@3x.png', '1', 133, 0, '2019-01-10 14:16:12', '2018-08-22 15:09:25', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af70165610bd67e011a', '股票', '2c91dbe363f81ded0163f81f0c8c0006', 'http://label.image.fengniaojizhang.cn/1547105624209_icon_zhicgp_normal@3x.png', '1', 134, 0, '2019-01-10 15:33:47', '2018-08-22 17:51:30', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af70165610c5b10011c', '基金', '2c91dbe363f81ded0163f81f0c8c0006', 'http://label.image.fengniaojizhang.cn/1547105813353_icon_zhicjj_normal@3x.png', '1', 135, 0, '2019-01-10 15:36:55', '2018-08-22 17:52:04', NULL, NULL);
INSERT INTO `hbird_spend_type_bac` VALUES ('2c91dbe7655b9af70165610cc296011e', '利率', '2c91dbe363f81ded0163f81f0c8c0006', 'http://label.image.fengniaojizhang.cn/1547105855569_icon_zhiclc_normal@3x.png', '1', 136, 0, '2019-01-10 15:37:41', '2018-08-22 17:52:31', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;

--  收入标签

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for hbird_income_type_bac
-- ----------------------------
DROP TABLE IF EXISTS `hbird_income_type_bac`;
CREATE TABLE `hbird_income_type_bac`  (
  `id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'id',
  `income_name` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收入类目名称',
  `parent_id` varchar(36) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '收入父级类目',
  `icon` varchar(256) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标',
  `status` varchar(2) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '状态(0:下线,1:上线)',
  `priority` int(11) NULL DEFAULT NULL COMMENT '优先级',
  `mark` int(2) NULL DEFAULT NULL COMMENT '常用标记,0:不常用,1:常用',
  `update_date` datetime(0) NULL DEFAULT NULL COMMENT '更新时间',
  `create_date` datetime(0) NULL DEFAULT NULL COMMENT '创建时间',
  `delflag` int(2) NULL DEFAULT NULL COMMENT '删除标记',
  `del_date` datetime(0) NULL DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统收入类目表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of hbird_income_type_bac
-- ----------------------------
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363f8b9390163f8c6403b000c', '劳动', NULL, NULL, '1', 1, NULL, '2018-06-13 18:52:12', '2018-06-13 18:52:12', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363f8b9390163f8c66ba90010', '运气', NULL, NULL, '1', 2, NULL, '2018-06-13 18:52:23', '2018-06-13 18:52:23', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363f8b9390163f8c691a00012', '理财', NULL, NULL, '1', 3, NULL, '2018-06-13 18:52:33', '2018-06-13 18:52:33', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363f8b9390163f8c6abc60014', '其他', NULL, NULL, '1', 4, NULL, '2018-06-13 18:52:40', '2018-06-13 18:52:40', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe44a92b0131', '工资', '2c91dbe363f8b9390163f8c6403b000c', 'http://label.image.fengniaojizhang.cn/1547101472812_icon_gongzi_normal@3x(1).png', '1', 1, 1, '2019-01-10 14:24:34', '2018-06-14 20:28:23', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe4504190133', '兼职', '2c91dbe363f8b9390163f8c6403b000c', 'http://label.image.fengniaojizhang.cn/1547101483088_icon_jianzhi_normal@3x.png', '1', 2, 1, '2019-01-11 15:03:20', '2018-06-14 20:28:46', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe453d5a0135', '奖金', '2c91dbe363f8b9390163f8c6403b000c', 'http://label.image.fengniaojizhang.cn/1547101497785_icon_jiangjin_normal@3x.png', '1', 3, 1, '2019-01-11 15:03:20', '2018-06-14 20:29:01', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe4696430139', '补助', '2c91dbe363f8b9390163f8c66ba90010', 'http://label.image.fengniaojizhang.cn/1547101610963_icon_buzhu_normal@3x.png', '1', 4, 1, '2019-01-11 15:03:21', '2018-06-14 20:30:29', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe46fc04013b', '红包', '2c91dbe363f8b9390163f8c66ba90010', 'http://label.image.fengniaojizhang.cn/1547101622973_icon_hongbao_normal@3x.png', '1', 5, 1, '2019-01-11 15:03:21', '2018-06-14 20:30:55', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe47524c013d', '退款', '2c91dbe363f8b9390163f8c66ba90010', 'http://label.image.fengniaojizhang.cn/1547101634806_icon_tuikuan_normal@3x.png', '1', 6, 0, '2019-01-11 15:03:21', '2018-06-14 20:31:17', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe479775013f', '零花钱', '2c91dbe363f8b9390163f8c66ba90010', 'http://label.image.fengniaojizhang.cn/1547103685920_icon_linhuaqian_normal@3x.png', '1', 7, 1, '2019-01-11 15:03:21', '2018-06-14 20:31:35', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe4831400141', '礼金', '2c91dbe363f8b9390163f8c66ba90010', 'http://label.image.fengniaojizhang.cn/1547103697708_icon_lijin_normal@3x.png', '1', 8, 0, '2019-01-11 15:03:21', '2018-06-14 20:32:14', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe4888bc0143', '中奖', '2c91dbe363f8b9390163f8c66ba90010', 'http://label.image.fengniaojizhang.cn/1547103709219_icon_zhongjiang_normal@3x.png', '1', 9, 0, '2019-01-11 15:03:21', '2018-06-14 20:32:37', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe48fee20145', '意外收入', '2c91dbe363f8b9390163f8c66ba90010', 'http://label.image.fengniaojizhang.cn/1547103720151_icon_yiwaishouru_normal@3x.png', '1', 10, 0, '2019-01-11 15:03:21', '2018-06-14 20:33:07', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe4a8a09014f', '投资理财', '2c91dbe363f8b9390163f8c691a00012', 'http://label.image.fengniaojizhang.cn/1547103818156_icon_touzilicai_normal@3x.png', '1', 11, 1, '2019-01-11 15:03:21', '2018-06-14 20:34:48', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe4afdec0151', '股票', '2c91dbe363f8b9390163f8c691a00012', 'http://label.image.fengniaojizhang.cn/1547103846491_icon_gupiao_normal@3x.png', '1', 12, 0, '2019-01-11 15:03:21', '2018-06-14 20:35:18', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe4b6e0e0154', '报销', '2c91dbe363f8b9390163f8c6403b000c', 'http://label.image.fengniaojizhang.cn/1547103868272_icon_baoxiao_normal@3x.png', '1', 13, 1, '2019-01-11 15:03:21', '2018-06-14 20:35:46', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe4bf8e20156', '基金', '2c91dbe363f8b9390163f8c691a00012', 'http://label.image.fengniaojizhang.cn/1547103892063_icon_huobijijin_normal@3x.png', '1', 14, 0, '2019-01-11 15:03:21', '2018-06-14 20:36:22', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe4db311015f', 'P2P', '2c91dbe363f8b9390163f8c691a00012', 'http://label.image.fengniaojizhang.cn/1547103904007_icon_p2p_normal@3x.png', '1', 15, 0, '2019-01-11 15:03:21', '2018-06-14 20:38:15', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe4e27390161', '数字货币', '2c91dbe363f8b9390163f8c691a00012', 'http://label.image.fengniaojizhang.cn/1547103922022_icon_shuzihuobi_normal@3x.png', '1', 16, 0, '2019-01-11 15:03:21', '2018-06-14 20:38:45', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe4e78c10163', '活期理财', '2c91dbe363f8b9390163f8c691a00012', 'http://label.image.fengniaojizhang.cn/1547103937532_icon_huoqilicai_normal@3x.png', '1', 17, 1, '2019-01-11 15:03:21', '2018-06-14 20:39:06', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe4ee4af0165', '国债债券', '2c91dbe363f8b9390163f8c691a00012', 'http://label.image.fengniaojizhang.cn/1547103952879_icon_guozhai_normal@3x.png', '1', 18, 0, '2019-01-11 15:03:21', '2018-06-14 20:39:33', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe4f42c20167', '利息', '2c91dbe363f8b9390163f8c691a00012', 'http://label.image.fengniaojizhang.cn/1547104791204_icon_lixi_normal@3x.png', '1', 19, 0, '2019-01-11 15:03:21', '2018-06-14 20:39:57', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe4fa03c0169', '银行理财', '2c91dbe363f8b9390163f8c691a00012', 'http://label.image.fengniaojizhang.cn/1547104804019_icon_yinhanglicai_normal@3x.png', '1', 20, 1, '2019-01-11 15:03:21', '2018-06-14 20:40:21', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe50154d016b', '信托', '2c91dbe363f8b9390163f8c691a00012', 'http://label.image.fengniaojizhang.cn/1547104843240_icon_xintuo_normal@3x.png', '1', 21, 0, '2019-01-11 15:03:21', '2018-06-14 20:40:51', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe5066c1016d', '大宗商品', '2c91dbe363f8b9390163f8c691a00012', 'http://label.image.fengniaojizhang.cn/1547104922923_icon_guijinshu_normal@3x.png', '1', 22, 0, '2019-01-11 15:03:21', '2018-06-14 20:41:12', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c91dbe363fc3f800163fe50de18016f', '其他', '2c91dbe363f8b9390163f8c6abc60014', 'http://label.image.fengniaojizhang.cn/1547104951879_icon_qita_normal@3x.png', '1', 23, 1, '2019-01-11 15:03:21', '2018-06-14 20:41:43', NULL, NULL);
INSERT INTO `hbird_income_type_bac` VALUES ('2c928082682735de01683bb8b01800b8', '收款', '2c91dbe363f8b9390163f8c6abc60014', 'http://label.image.fengniaojizhang.cn/1547190179480_icon_shoukuan_normal@3x.png', '1', 24, 0, '2019-01-11 15:03:21', '2019-01-11 15:03:06', NULL, NULL);

SET FOREIGN_KEY_CHECKS = 1;


UPDATE hbird_spend_type AS base2
INNER JOIN ( SELECT icon, spend_name AS typename FROM `hbird_spend_type_bac` WHERE parent_id IS NOT NULL ) AS base1 ON base2.spend_name = base1.typename
SET base2.icon = base1.icon where parent_id is not null;


UPDATE hbird_income_type AS base2
INNER JOIN ( SELECT icon, income_name AS typename FROM `hbird_income_type_bac` WHERE parent_id IS NOT NULL ) AS base1 ON base2.income_name = base1.typename
SET base2.icon = base1.icon where parent_id is not null;


-- 更新用户自有标签表
UPDATE hbird_user_private_label AS base2
INNER JOIN (
	SELECT
		icon,
		spend_name AS typename,
		1 as property
	FROM
		`hbird_spend_type_bac`
	WHERE
		parent_id IS NOT NULL UNION ALL
	SELECT
		icon,
		income_name AS typename,
		2 as property
	FROM
		`hbird_income_type_bac`
	WHERE
		parent_id IS NOT NULL
	) AS base1 ON base2.type_name = base1.typename
	SET base2.icon = base1.icon where base2.property = base1.property;


UPDATE hbird_water_order AS base2
INNER JOIN (
	SELECT
		icon,
		spend_name AS typename,
		1 as property
	FROM
		`hbird_spend_type_bac`
	WHERE
		parent_id IS NOT NULL UNION ALL
	SELECT
		icon,
		income_name AS typename,
		2 as property
	FROM
		`hbird_income_type_bac`
	WHERE
		parent_id IS NOT NULL
	) AS base1 ON base2.type_name = base1.typename
	SET base2.icon = base1.icon where base2.type_name is not null and base2.order_type = base1.property;

-- 手动删除线上缓存中 系统标签和用户个人标签吧