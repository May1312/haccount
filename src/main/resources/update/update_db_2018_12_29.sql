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

UPDATE hbird_spend_type AS base2
INNER JOIN ( SELECT icon, spend_name AS typename FROM `hbird_spend_type_test` WHERE parent_id IS NOT NULL ) AS base1 ON base2.spend_name = base1.typename
SET base2.icon = base1.icon where parent_id is not null;


UPDATE hbird_income_type AS base2
INNER JOIN ( SELECT icon, income_name AS typename FROM `hbird_income_type_test` WHERE parent_id IS NOT NULL ) AS base1 ON base2.income_name = base1.typename
SET base2.icon = base1.icon where parent_id is not null;


-- 更新用户自有标签表
UPDATE hbird_user_private_label AS base2
INNER JOIN (
	SELECT
		icon,
		spend_name AS typename
	FROM
		`hbird_spend_type_test`
	WHERE
		parent_id IS NOT NULL UNION ALL
	SELECT
		icon,
		income_name AS typename
	FROM
		`hbird_income_type_test`
	WHERE
		parent_id IS NOT NULL
	) AS base1 ON base2.type_name = base1.typename
	SET base2.icon = base1.icon;


UPDATE hbird_water_order AS base2
INNER JOIN (
	SELECT
		icon,
		spend_name AS typename
	FROM
		`hbird_spend_type_test`
	WHERE
		parent_id IS NOT NULL UNION ALL
	SELECT
		icon,
		income_name AS typename
	FROM
		`hbird_income_type_test`
	WHERE
		parent_id IS NOT NULL
	) AS base1 ON base2.type_name = base1.typename
	SET base2.icon = base1.icon where base2.type_name is not null;