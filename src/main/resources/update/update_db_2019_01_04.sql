

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
-- 3	应用商店	vivo		无		应用商店_vivo_无	channel_1   xxxx         ———    vivo商店打包时把”channel_1”,打进去
-- 4     应用商店	华为		无		应用商店_华为_无	channel_2  xxxx         ———    华为商店打包时把”channel_2”,打进去
-- 5     信息流	广点通	亿玛		信息流_广点通_亿玛	channel_3  xxxx         ———    www.hbird.com/landing.html?channel=channel_3
insert into hbird_channel(`channel_type`,`channel_media`,`channel_corporation`,`channel_name`,`channel_nid`,`create_time`)
values ('应用商店','后台下载','无','应用商店_后台下载_无','default_channel',now());
insert into hbird_channel(`channel_type`,`channel_media`,`channel_corporation`,`channel_name`,`channel_nid`,`create_time`)
values ('应用商店','木蚂蚁','无','应用商店_木蚂蚁_无','mumayi',now());
insert into hbird_channel(`channel_type`,`channel_media`,`channel_corporation`,`channel_name`,`channel_nid`,`create_time`)
values ('应用商店','奇虎','无','应用商店_奇虎_无','qihoo',now());
insert into hbird_channel(`channel_type`,`channel_media`,`channel_corporation`,`channel_name`,`channel_nid`,`create_time`)
values ('应用商店','应用宝','无','应用商店_应用宝_无','yingyongbao',now());
insert into hbird_channel(`channel_type`,`channel_media`,`channel_corporation`,`channel_name`,`channel_nid`,`create_time`)
values ('应用商店','小米','无','应用商店_小米_无','xiaomi',now());
insert into hbird_channel(`channel_type`,`channel_media`,`channel_corporation`,`channel_name`,`channel_nid`,`create_time`)
values ('应用商店','百度','无','应用商店_百度_无','baidu',now());
insert into hbird_channel(`channel_type`,`channel_media`,`channel_corporation`,`channel_name`,`channel_nid`,`create_time`)
values ('应用商店','华为','无','应用商店_华为_无','huawei',now());
insert into hbird_channel(`channel_type`,`channel_media`,`channel_corporation`,`channel_name`,`channel_nid`,`create_time`)
values ('应用商店','阿里巴巴','无','应用商店_阿里巴巴_无','alibaba',now());
insert into hbird_channel(`channel_type`,`channel_media`,`channel_corporation`,`channel_name`,`channel_nid`,`create_time`)
values ('应用商店','vivo','无','应用商店_vivo_无','vivo',now());
insert into hbird_channel(`channel_type`,`channel_media`,`channel_corporation`,`channel_name`,`channel_nid`,`create_time`)
values ('应用商店','oppo','无','应用商店_oppo_无','oppo',now());
insert into hbird_channel(`channel_type`,`channel_media`,`channel_corporation`,`channel_name`,`channel_nid`,`create_time`)
values ('应用商店','魅族','无','应用商店_魅族_无','meizu',now());
insert into hbird_channel(`channel_type`,`channel_media`,`channel_corporation`,`channel_name`,`channel_nid`,`create_time`)
values ('应用商店','搜狗','无','应用商店_搜狗_无','sougou',now());
insert into hbird_channel(`channel_type`,`channel_media`,`channel_corporation`,`channel_name`,`channel_nid`,`create_time`)
values ('应用商店','安智','无','应用商店_安智_无','anzhi',now());
insert into hbird_channel(`channel_type`,`channel_media`,`channel_corporation`,`channel_name`,`channel_nid`,`create_time`)
values ('应用商店','机锋','无','应用商店_机锋_无',' jifeng',now());

-- 埋点记录表
drop table if exists hbird_buried_point;
-- 埋点记录表
CREATE TABLE `hbird_buried_point` (
	`id` BIGINT ( 18 ) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`user_info_id` int(11) NOT NULL COMMENT '用户详情id',
	`device_num` VARCHAR ( 64 ) DEFAULT NULL COMMENT '唯一标识 终端设备号（移动端） union（小程序）',
	`point_type_id` int ( 3 ) NOT NULL COMMENT '埋点类型id',
	`client_id` VARCHAR ( 24 ) NOT NULL COMMENT '记录用户本次操作使用的客户端是什么，例如:iphone，android，ipad，androidpad，小程序，公众号等',
	`brand` VARCHAR ( 24 ) DEFAULT NULL COMMENT '手机品牌',
  `model` VARCHAR ( 24 ) DEFAULT NULL COMMENT '手机型号',
  `wechat_version` VARCHAR ( 24 ) DEFAULT NULL COMMENT '微信版本号',
  `system` VARCHAR ( 24 ) DEFAULT NULL COMMENT '操作系统版本号',
  `platform` VARCHAR ( 24 ) DEFAULT NULL COMMENT '客户端平台',
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
-- 首页
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('首页浏览',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('记账按钮',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('预算编辑按钮',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('切换账本按钮',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('切换月份',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('邀请好友记账按钮',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('邀请收起按钮',now());
-- 记账页
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('切换日期按钮',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('备注输入框',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('选择购买心情',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('点击更多类别添加按钮',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('点击完成',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('切换收入',now());
-- 数据页
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('数据页浏览',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('统计-支出/收入切换',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('分析按钮',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('存钱效率新建按钮',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('存钱效率编辑按钮',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('预算完成率新建按钮',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('预算完成率编辑按钮',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('资产按钮',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('资产时间设置按钮',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('资产设置',now());
-- 领票票
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('领票票页浏览',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('签到按钮',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('轮播图点击量',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('丰丰票商城',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('邀请好友注册',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('记一笔账',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('记账达到3笔',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('邀请达到5人',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('设置存钱效率',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('完善个人资料',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('设置预算',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('添加到我的小程序',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('日签保存图片',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('商品格子点击总量',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('兑换按钮',now());
insert into hbird_buried_point_type(`point_desc`,`create_time`) values ('确认兑换按钮',now());


-- hbird_user_info 增加 channel_id (渠道标识id)
--  hbird_water 增加 client_id (记录每笔所用终端)