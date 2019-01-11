-- 积分活动表
create table hbird_integrals_activity
(
   id                   int not null AUTO_INCREMENT comment '主键id',
   total_users          int(11) default 0 comment '参与人数',
   success_users        int(11) default 0 comment '成功人数',
   fail_users           int(11) default 0 comment '失败人数',
   total_integrals      decimal(11,2) default 0 comment '池内总积分数',
   false_total_users          int(11) default 0 comment '编辑参与人数',
   false_success_users        int(11) default 0 comment '编辑成功人数',
   false_fail_users           int(11) default 0 comment '编辑失败人数',
   add_integrals      decimal(11,2) default 0 comment '放水积分数',
   add_money      double(11,2) default 0 comment '放水金额',
   add_min_money      double(11,2) default 0 comment '最低投入收益',
   add_max_money      double(11,2) default 0 comment '最高投入收益',
   update_date          datetime comment '更新时间',
   create_date          datetime comment '创建时间',
   status               int(1) DEFAULT NULL COMMENT '上线状态  上线状态:0_下线 1_上线',
   primary key (id)
);
alter table hbird_integrals_activity comment '积分活动表';


-- 用户--积分活动参与记录表
create table hbird_user_integrals_activity
(
   id                   int not null AUTO_INCREMENT comment '主键id',
   user_info_id          int(11) default 0 comment '用户id',
   ia_id          int(11) default 0 comment '积分活动id',
   integrals        decimal(11,2) default 0 comment '参与积分数',
   get_integrals    decimal(11,2) default 0 comment '获取的积分数',
   status          int(1) default 0 comment '活动参与状态  1:已报名 2:已记账 3:挑战成功 4:挑战失败',
   update_date          datetime default null comment '更新时间',
   create_date          datetime default null comment '创建时间,即参与时间',
   charge_date          datetime default null comment '完成记账时间',
   end_date          datetime default null comment '结束时间',
   primary key (id),
   key `idx_integrals` ( `integrals` ),
   key `idx_create_date` ( `create_date` ),
   key `idx_ia_id` ( `ia_id` )
);
alter table hbird_user_integrals_activity comment '用户--积分活动参与记录表';


-- 用户--积分活动-积分区间范围表
create table hbird_user_integrals_activity_range
(
   id                   int not null AUTO_INCREMENT comment '主键id',
   integrals        decimal(11,2) default 0 comment '参与积分数',
   update_date          datetime default null comment '更新时间',
   create_date          datetime default null comment '创建时间',
   status               int(1) DEFAULT NULL COMMENT '上线状态  上线状态:0_下线 1_上线',
   primary key (id)
);
alter table hbird_user_integrals_activity_range comment '用户--积分活动-积分区间范围表';