-- 小程序渠道统计表
create table hbird_register_channel
(
   id                   int not null AUTO_INCREMENT comment 'id',
   user_info_id         int comment '用户id',
   channel              varchar(64) comment '渠道来源',
   type                 int(1) comment '定义:1 小程序',
   update_date          datetime comment '更新时间',
   create_date          datetime comment '创建日期',
   primary key (id)
);
alter table hbird_register_channel comment '小程序注册用户-渠道表';