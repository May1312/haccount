/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     2018/12/5 13:47:18                           */
/*==============================================================*/


-- 徽章类型表
drop table if exists hbird_badge_type;

-- 徽章表
drop table if exists hbird_badge;

-- 徽章类型-记账标签 关联表
drop table if exists hbird_badge_type_label;

-- 分享话术表
drop table if exists hbird_share_words;

-- 用户-徽章领取 关联表
drop table if exists hbird_user_badge;

-- 用户详情表扩展字段
-- drop table if exists hbird_user_info_add_field;



-- '徽章类型  1:攒钱徽章 2:工资徽章 3:兼职徽章 4:奖金徽章 5:投资徽章'
create table hbird_badge_type
(
   id                   int not null AUTO_INCREMENT comment 'id',
   name                 varchar(32) comment '徽章类型名称',
   priority             int comment '优先级',
   create_date          datetime comment '创建日期',
   update_date          datetime comment '更新时间',
   status               int(1) DEFAULT NULL COMMENT '上线状态  上线状态:0_下线 1_上线',
   primary key (id)
);
alter table hbird_badge_type comment '徽章类型表';


/*==============================================================*/
/* Table: hbird_badge                                           */
/*==============================================================*/

create table hbird_badge
(
   id                   int not null AUTO_INCREMENT comment 'id',
   badge_type_id        int not null comment '徽章类型id',
   badge_name        VARCHAR(36) comment '徽章名称',
   description          varchar(32) comment '徽章描述',
   unlock_icon          varchar(256) comment '徽章解锁图标',
   lock_icon            varchar(256) comment '徽章锁定图标',
   percentage           double(2,2) comment '徽章进阶百分比',
   priority             int comment '优先级',
   words                varchar(64) comment '话术',
   status               int(1) DEFAULT NULL COMMENT '上线状态  上线状态:0_下线 1_上线',
   create_date          datetime comment '创建日期',
   update_date          datetime comment '更新时间',
   primary key (id)
);

alter table hbird_badge comment '徽章表';

/*==============================================================*/
/* Table: hbird_badge                                           */
/*==============================================================*/
create table hbird_badge_type_label
(
   id                   int not null AUTO_INCREMENT comment 'id',
   badge_type_id        varchar(32) not null comment '徽章类型id',
   type_id             varchar(36) comment '系统标签id uuid    三级类目id',
   label_type            int(1) DEFAULT NULL COMMENT '标签类型 1:支出 2:收入',
   create_date          datetime comment '创建日期',
   update_date          datetime comment '更新时间',
   primary key (id)
);

alter table hbird_badge_type_label comment '徽章类型-记账标签 关联表';

/*==============================================================*/
/* Table: hbird_share_words                                     */
/*==============================================================*/
create table hbird_share_words
(
   id                   int not null AUTO_INCREMENT comment 'id',
   festival             varchar(32) comment '节日名称',
   festival_day            date comment '节日日期',
   words                varchar(64) comment '话术',
   icon                varchar(256) comment '图片',
   status               int(1) DEFAULT NULL COMMENT '上线状态  上线状态:0_下线 1_上线',
   update_date          datetime comment '更新时间',
   create_date          datetime comment '创建日期',
   primary key (id)
);

alter table hbird_share_words comment '日签分享话术表';
-- 添加基本数据

/*==============================================================*/
/* Table: hbird_user_badge                                      */
/*==============================================================*/
create table hbird_user_badge
(
   id                   int not null AUTO_INCREMENT comment 'id',
   user_info_id         int not null comment '用户详情id',
   badge_id             int comment '徽章id',
   badge_type_id             int comment '徽章类型id',
   salary                double(32,2) comment '保留字段  工资类型对应解锁日所设置的工资数',
   rank                 int comment '领取排名',
   create_date          datetime comment '创建日期',
   update_date          datetime comment '更新时间',
   primary key (id)
);

alter table hbird_user_badge comment '用户-徽章领取 关联表';

/*==============================================================*/
/* Table: hbird_user_info_add_field                             */
/*==============================================================*/
-- create table hbird_user_info_add_field
-- (
   -- id                   int not null comment 'id',
   -- user_info_id         int not null comment '用户详情id',
   -- open_id              varchar(32) comment '小程序openId',
   -- type                 int(1) comment '定义1:openId',
   -- consignee_name       varchar(32) comment '收货人',
   -- consignee_mobile     varchar(32) comment '收货人手机',
   -- consignee_province   varchar(32) comment '收货省份',
   -- consignee_city       varchar(32) comment '收货城市',
   -- consignee_district   varchar(32) comment '收货区县',
   -- consignee_detail     varchar(64) comment '收货地址详情',
   -- create_date          datetime comment '创建日期',
   -- update_date          datetime comment '更新时间',
   -- primary key (id)
-- );

-- alter table hbird_user_info_add_field comment '用户详情表扩展字段';

alter table hbird_user_info_add_field	Add column consignee_name varchar(32) comment '收货人';
alter table hbird_user_info_add_field	Add column consignee_mobile varchar(32) comment '收货人手机';
alter table hbird_user_info_add_field	Add column consignee_province varchar(32) comment '收货省份';
alter table hbird_user_info_add_field	Add column consignee_city varchar(32) comment '收货城市';
alter table hbird_user_info_add_field	Add column consignee_district varchar(32) comment '收货区县';
alter table hbird_user_info_add_field	Add column consignee_detail varchar(64) comment '收货地址详情';
alter table hbird_user_info_add_field	Add column update_date datetime comment '更新时间';
-- 修改openId
alter table hbird_user_info_add_field CHANGE open_id wxapplet_open_id  VARCHAR(32) comment '小程序 openId';
alter table hbird_user_info_add_field Add column wechat_open_id VARCHAR(32) comment '移动应用 openId';
alter table hbird_user_info_add_field Add column official_open_id VARCHAR(32) comment '公众号  openId';
alter table hbird_user_info_add_field DROP COLUMN type;


--  hbird_goods 商品表
	-- 商品类型 goods_type  添加虚拟/实物/红包兑换类型

	alter table hbird_goods	Add column goods_type int(1) COMMENT '商品类型 1:虚拟  2:实物  3:红包';
	-- 现有数据初始化为虚拟商品类型
	update hbird_goods set goods_type=1;

-- hbird_shopping_mall_integral_exchange 兑换记录表
	-- 快递名称 express_company  快递公司
	-- 快递名称 express_number  快递单号

	alter table hbird_shopping_mall_integral_exchange	Add column express_company varchar(32) COMMENT '快递公司';
	alter table hbird_shopping_mall_integral_exchange	Add column express_number varchar(32) COMMENT '快递单号';
	alter table hbird_shopping_mall_integral_exchange	Add column consignee_name varchar(32) COMMENT '收货人';
	alter table hbird_shopping_mall_integral_exchange	Add column consignee_mobile varchar(32) COMMENT '收货人手机';
	alter table hbird_shopping_mall_integral_exchange	Add column consignee_province varchar(32) COMMENT '收货省份';
	alter table hbird_shopping_mall_integral_exchange	Add column consignee_city varchar(32) COMMENT '收货城市';
	alter table hbird_shopping_mall_integral_exchange	Add column consignee_district varchar(32) COMMENT '收货区县';
	alter table hbird_shopping_mall_integral_exchange	Add column consignee_detail varchar(64) COMMENT '收货地址详情';


--  hbird_home_window 首页弹窗管理表
    --  popup_type 弹窗类型

	alter table hbird_home_window	Add column popup_type int(1) COMMENT '弹窗类型  1:弹窗  2:浮窗 60*60';


-- 新手任务  每日任务  签到奖励  mysql记录

create table hbird_user_sign_in_award
(
   id                   int not null AUTO_INCREMENT comment 'id',
   user_info_id         int not null comment '用户详情id',
   category_of_behavior  varchar(32) comment '行为类别  新手任务  每日任务  签到奖励',
   cycle                 int(2) comment '签到周期定义  值可能为7/14/21/28',
   cycle_award_status             int(1) comment '奖励领取状态 1:未领取 2:已领取 3:不可领',
   get_times             int(2) DEFAULT 0 comment '可领取次数 默认0',
   create_date          datetime comment '创建日期',
   update_date          datetime comment '更新时间',
   delflag int(1) DEFAULT 0 COMMENT '删除标记   0有效  1:失效',
   primary key (id)
);

alter table hbird_user_sign_in_award comment '用户连签奖励领取情况表';


-- 添加索引
alter table hbird_user_info_add_field add index INDEX_USER_INFO_ID(user_info_id) ;


-- 修改 用户-积分流水表 积分数 数据类型  double
alter table hbird_user_integral add column integral_num_double double(11, 2) comment 'double 积分数';
update hbird_user_integral set integral_num_double = CAST(integral_num AS DECIMAL);

-- 修改 用户对应总积分数关系表 积分数数据类型  decimal
alter table hbird_user_total_integrals add column integral_num_decimal decimal(11, 2) comment 'decimal 总积分数';
update hbird_user_total_integrals set integral_num_decimal = CAST(integral_num AS DECIMAL);