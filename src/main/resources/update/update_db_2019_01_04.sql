

-- 推广渠道标识表
drop table if exists hbird_channel;

CREATE TABLE `hbird_channel` (
	`id` INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`channel_type` VARCHAR ( 64 ) NOT NULL COMMENT '渠道类型',
	`channel_media` VARCHAR ( 64 ) NOT NULL COMMENT '渠道媒体',
	`channel_corporation` VARCHAR ( 64 ) NOT NULL DEFAULT '无' COMMENT '合作公司[例如信息流的优化公司，或者短信推广时短信公司]/有含义描述/无',
	`channel_name` VARCHAR ( 50 ) DEFAULT '' COMMENT '渠道名称：渠道类型_渠道媒体_合作公司',
	`channel_nid` VARCHAR ( 128 ) NOT NULL DEFAULT '' COMMENT '渠道标识',
	`create_time` datetime NOT NULL COMMENT '创建时间',
	PRIMARY KEY ( `id` ),
	KEY `INDEX_CHANNEL_NID` ( `channel_nid` )
) ENGINE = INNODB AUTO_INCREMENT = 0 DEFAULT CHARSET = utf8 COMMENT '推广渠道标识表';
insert into hbird_channel() values ();

-- 埋点记录表
drop table if exists hbird_buried_point;
-- 埋点记录表
CREATE TABLE `hbird_buried_point` (
	`id` BIGINT ( 18 ) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`user_info_id` int(11) NOT NULL COMMENT '用户详情id',
	`union_id` VARCHAR ( 64 ) NOT NULL COMMENT '微信unionid',
	`device_num` VARCHAR ( 64 ) DEFAULT NULL COMMENT '终端设备号',
	`point_type_id` int ( 3 ) NOT NULL COMMENT '埋点类型id',
	`client_id` VARCHAR ( 24 ) NOT NULL COMMENT '记录用户本次操作使用的客户端是什么，例如:iphone，android，ipad，androidpad，小程序，公众号等',
	`brand` VARCHAR ( 24 ) DEFAULT NULL COMMENT '手机品牌',
  `model` VARCHAR ( 24 ) DEFAULT NULL COMMENT '手机型号',
  `wechat_version` VARCHAR ( 24 ) DEFAULT NULL COMMENT '微信版本号',
  `system` VARCHAR ( 24 ) DEFAULT NULL COMMENT '操作系统版本号',
	`create_time` datetime NOT NULL COMMENT '创建时间',
	PRIMARY KEY ( `id` ),
	KEY `INDEX_POINT_TYPE_ID` ( `point_type_id` ),
	KEY `INDEX_USER_INFO_ID` ( `user_info_id` ),
	KEY `INDEX_CREATE_TIME` ( `create_time` )
) ENGINE = INNODB AUTO_INCREMENT = 0 DEFAULT CHARSET = utf8 COMMENT '埋点记录表';

-- 埋点类型表
drop table if exists hbird_buried_point_type;
-- 埋点类型表
CREATE TABLE `hbird_buried_point_type` (
	`id` INT ( 11 ) NOT NULL AUTO_INCREMENT COMMENT '埋点类型id主键',
	`point_desc` VARCHAR ( 64 ) NOT NULL COMMENT '埋点描述 例:记账按钮',
	`create_time` datetime NOT NULL COMMENT '创建时间',
	PRIMARY KEY ( `id` )
) ENGINE = INNODB AUTO_INCREMENT = 0 DEFAULT CHARSET = utf8 COMMENT '埋点类型表';


--- hbird_user_info 增加 channel_id (渠道标识id)
--  hbird_water 增加 client_id (记录每笔所用终端)