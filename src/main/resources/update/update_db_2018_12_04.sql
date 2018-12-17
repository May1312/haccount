/*
-- 对比表名
select t.TABLE_NAME , t1.TABLE_NAME
from (
select * from information_schema.TABLES t where t.table_schema='hbird_account_utf8mb4' and t.TABLE_NAME like 'hbird_%'
)t left join (
select * from information_schema.TABLES t where t.table_schema='hbird_account_test' and t.TABLE_NAME like 'hbird_%'
)t1 on t.TABLE_NAME = t1.TABLE_NAME;

-- 对比列名 
select t.*,t1.*
from (
select t.TABLE_NAME,t.COLUMN_NAME,t.COLUMN_TYPE,t.COLUMN_COMMENT from information_schema.columns t where t.table_schema='hbird_account_utf8mb4' and t.TABLE_NAME like 'hbird_%'
)t left join (
select t.TABLE_NAME,t.COLUMN_NAME,t.COLUMN_TYPE from information_schema.columns t where t.table_schema='hbird_account_test' and t.TABLE_NAME like 'hbird_%'
)t1 on t.TABLE_NAME = t1.TABLE_NAME and t.COLUMN_NAME = t1.COLUMN_NAME
where t1.COLUMN_NAME is null or t.COLUMN_TYPE != t1.COLUMN_TYPE

*/
-- 多账本升级sql

-- 新建4张表 
-- hbird_account_book_type	

DROP TABLE IF EXISTS `hbird_account_book_type`;
CREATE TABLE `hbird_account_book_type` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `ab_type_name` varchar(64) DEFAULT NULL COMMENT '账本类型名称',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `icon_describe` varchar(255) DEFAULT NULL COMMENT '带描述字样图标',
  `type_budget` int(1) DEFAULT NULL COMMENT '账本属性(与预算时间的设置相关) 1:普通日常账本 2:场景账本(即需设置预算起始时间)',
  `priority` int(11) DEFAULT NULL COMMENT '优先级',
  `status` int(1) DEFAULT NULL COMMENT '上线状态  上线状态:0_下线 1_上线',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建人',
  `create_name` varchar(32) DEFAULT NULL COMMENT '创建人名字',
  `delflag` tinyint(4) DEFAULT NULL COMMENT '删除标记',
  `del_date` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='账本类型表 多账本 可选择的类型';


-- hbird_account_book_type_label	
DROP TABLE IF EXISTS `hbird_account_book_type_label`;
CREATE TABLE `hbird_account_book_type_label` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `ab_type_id` int(2) DEFAULT NULL COMMENT '账本类型id',
  `sys_label_id` varchar(36) DEFAULT NULL COMMENT '系统标签三级id',
  `parent_id` varchar(36) DEFAULT NULL COMMENT '系统标签二级id',
  `label_type` int(1) DEFAULT NULL COMMENT '标签类型 1:支出 2:收入',
  `priority` int(11) DEFAULT NULL COMMENT '优先级',
  `mark` int(2) DEFAULT NULL COMMENT '常用标记,0:不常用,1:常用',
  `status` int(1) DEFAULT NULL COMMENT '上线状态  上线状态:0_下线 1_上线',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(32) DEFAULT NULL COMMENT '创建人',
  `create_name` varchar(32) DEFAULT NULL COMMENT '创建人名字',
  `delflag` tinyint(4) DEFAULT NULL COMMENT '删除标记',
  `del_date` datetime DEFAULT NULL COMMENT '删除时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `ab_type_id` (`ab_type_id`,`sys_label_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 ROW_FORMAT=DYNAMIC COMMENT='账本类型-系统标签关联表';

-- hbird_message	
DROP TABLE IF EXISTS `hbird_message`;
CREATE TABLE `hbird_message` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_info_id` int(11) DEFAULT NULL COMMENT '用户id',
  `content` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '消息内容',
  `status` int(1) DEFAULT NULL COMMENT '读取状态:  1:已读 2:未读',
  `update_date` datetime DEFAULT NULL COMMENT '更新时间',
  `create_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` int(11) DEFAULT NULL COMMENT '创建人',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='消息通知表';


-- hbird_user_private_label
DROP TABLE IF EXISTS `hbird_user_private_label`;
CREATE TABLE `hbird_user_private_label` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'id',
  `user_info_id` int(11) DEFAULT NULL COMMENT '用户详情id',
  `type_pid` varchar(36) DEFAULT NULL COMMENT '二级类目id',
  `type_pname` varchar(64) DEFAULT NULL COMMENT '二级栏目名称',
  `type_id` varchar(36) DEFAULT NULL COMMENT '三级类目id',
  `type_name` varchar(64) DEFAULT NULL COMMENT '三级类目名称',
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `priority` int(11) DEFAULT NULL COMMENT '优先级',
  `property` int(1) DEFAULT NULL COMMENT '标签属性 1:支出 2:收入',
  `type` int(1) DEFAULT NULL COMMENT '标签类型 1:系统分配  2:用户自建',
  `status` int(1) DEFAULT NULL COMMENT '标签状态 1:有效  0:失效',
  `account_book_id` int(11) DEFAULT NULL COMMENT '账本id',
  `ab_type_label_id` int(11) DEFAULT NULL COMMENT '账本类型标签表对应id',
  `ab_type_id` int(11) DEFAULT NULL COMMENT '账本类型id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='用户自有类目标签表';

DROP TABLE IF EXISTS `hbird_user_info_add_field`;
CREATE TABLE `hbird_user_info_add_field` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_info_id` int(11) DEFAULT NULL COMMENT '用户id',
  `open_id` varchar(32) DEFAULT NULL COMMENT '小程序openId',
  `type` int(1) DEFAULT NULL COMMENT '定义1:openId',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8mb4 COMMENT='用户详情表扩展字段';

DROP TABLE IF EXISTS `hbird_wxapplet_message_temp`;
CREATE TABLE `hbird_wxapplet_message_temp` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_info_id` int(11) DEFAULT NULL COMMENT '用户详情id',
  `open_id` varchar(32) DEFAULT NULL COMMENT '小程序openId',
  `form_id` varchar(32) DEFAULT NULL COMMENT '服务通知formId',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='小程序服务通知临时表';

DROP TABLE IF EXISTS `hbird_wxapplet_account_notify_temp`;
CREATE TABLE `hbird_wxapplet_account_notify_temp` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_info_id` int(11) DEFAULT NULL COMMENT '用户详情id',
  `open_id` varchar(32) DEFAULT NULL COMMENT '小程序openId',
  `form_id` varchar(32) DEFAULT NULL COMMENT '服务通知formId',
  `spend` decimal(32,2) DEFAULT NULL COMMENT '月度支出',
  `income` decimal(32,2) DEFAULT NULL COMMENT '月度收入',
  `create_date` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='月度账单数据存放临时表';

-- 增加索引
alter table hbird_user_private_label add index INDEX_TYPE_ID(type_id) ;
alter table hbird_user_private_label add index INDEX_USER_INFO_ID(user_info_id) ;
alter table hbird_water_order add index INDEX_UPDATE_BY(update_by) ;
alter table hbird_user_account_book add index INDEX_USER_INFO_ID(user_info_id) ;

-- 新建列 13列
alter table hbird_account_book	Add column icon	varchar(36)	 COMMENT '账本图标';
alter table hbird_account_book	Add column account_book_type_id	int(2)	 COMMENT '账本类型id';
alter table hbird_account_book	Add column member	int(1)	 COMMENT '账本成员数';
alter table hbird_accountbook_budget	Add column begin_time	datetime	 COMMENT '场景账本 开始时间';
alter table hbird_accountbook_budget	Add column end_time	datetime	 COMMENT '场景账本 结束时间';
alter table hbird_accountbook_budget	Add column scene_type	int(1)	 COMMENT '场景账本预算完成率统计类型  1 日  2  周  3 月  4 年';
alter table hbird_user_account_book	Add column default_flag	int(1)	 COMMENT '是否为默认账本 1:默认账本 2:非默认';
alter table hbird_user_account_book	Add column bind_flag	int(1)	 COMMENT '初始绑定标识，仅作为受邀用户标识 1:绑定账本，未同步数据  2:已绑定，已同步';
alter table hbird_user_comm_type_priority	Add column account_book_id	int(11)	 COMMENT '所属账本id';
alter table hbird_user_comm_type_priority	Add column ab_type_id	int(11)	 COMMENT '账本类型id';
alter table hbird_user_comm_use_type_offline_check	Add column user_info_id	int(11)	 COMMENT '用户详情id';
alter table hbird_water_order	Add column user_private_label_id	int(11)	 COMMENT '用户自有标签id';
alter table hbird_water_order	Add column icon	varchar(256)	 COMMENT '图标';

alter table hbird_user_comm_type_priority	Add column relation_old	varchar(10240) COMMENT '备份排序json';


update hbird_user_comm_type_priority set relation_old = relation;
                            



-- 添加一条日常账本 数据  ok
INSERT INTO `hbird_account_book_type`(`id`, `ab_type_name`, `icon`, `icon_describe`, `type_budget`, `priority`, `status`, `update_date`, `create_date`, `create_by`, `create_name`, `delflag`, `del_date`) VALUES (1, '日常账本', 'http://label.image.fengniaojizhang.cn/1542963634648_icon_richang_normal@3x.png', 'http://label.image.fengniaojizhang.cn/1542963598316_icon_richang_preseed@3x.png', 1, 1, 1, NULL, now(), 'admin', '管理员', NULL, NULL);
INSERT INTO `hbird_account_book_type`(`id`, `ab_type_name`, `icon`, `icon_describe`, `type_budget`, `priority`, `status`, `update_date`, `create_date`, `create_by`, `create_name`, `delflag`, `del_date`) VALUES (2, '结婚账本', 'http://label.image.fengniaojizhang.cn/1543817783919_icon_jiehun_normal@3x.png', 'http://label.image.fengniaojizhang.cn/1543817803215_Icon_jiehun_normal@3x.png', 2, 1, 1, NULL, now(), 'admin', '管理员', NULL, NULL);
INSERT INTO `hbird_account_book_type`(`id`, `ab_type_name`, `icon`, `icon_describe`, `type_budget`, `priority`, `status`, `update_date`, `create_date`, `create_by`, `create_name`, `delflag`, `del_date`) VALUES (3, '旅行账本', 'http://label.image.fengniaojizhang.cn/1543817851062_icon_lvxing_normal@3x.png', 'http://label.image.fengniaojizhang.cn/1543817835431_Icon_lvxing_normal@3x.png', 2, 7, 1, NULL, now(), 'admin', '管理员', NULL, NULL);
INSERT INTO `hbird_account_book_type`(`id`, `ab_type_name`, `icon`, `icon_describe`, `type_budget`, `priority`, `status`, `update_date`, `create_date`, `create_by`, `create_name`, `delflag`, `del_date`) VALUES (4, '人情账本', 'http://label.image.fengniaojizhang.cn/1543822640198_icon_renqing_normal@3x.png', 'http://label.image.fengniaojizhang.cn/1543822653172_Icon_renqing_normal@3x.png', 2, 9, 1, NULL, now(), 'admin', '管理员', NULL, NULL);
INSERT INTO `hbird_account_book_type`(`id`, `ab_type_name`, `icon`, `icon_describe`, `type_budget`, `priority`, `status`, `update_date`, `create_date`, `create_by`, `create_name`, `delflag`, `del_date`) VALUES (5, '生意账本', 'http://label.image.fengniaojizhang.cn/1543817950166_icon_shengyi_normal@3x.png', 'http://label.image.fengniaojizhang.cn/1543817966082_Icon_shengyi_normal@3x.png', 2, 6, 1, NULL, now(), 'admin', '管理员', NULL, NULL);
INSERT INTO `hbird_account_book_type`(`id`, `ab_type_name`, `icon`, `icon_describe`, `type_budget`, `priority`, `status`, `update_date`, `create_date`, `create_by`, `create_name`, `delflag`, `del_date`) VALUES (6, '装修账本', 'http://label.image.fengniaojizhang.cn/1543818058634_icon_zhuangxiu_normal@3x.png', 'http://label.image.fengniaojizhang.cn/1543818041152_Icon_zhuangxiu_normal@3x.png', 2, 2, 1, NULL, now(), 'admin', '管理员', NULL, NULL);
	
-- 账本类型对应标签表插入 默认初始化  赋予日常账本类型标签   支出 ok
INSERT INTO hbird_account_book_type_label ( `ab_type_id`, `sys_label_id`, `label_type`,`priority`, `mark`, `status` ,parent_id) (
	SELECT
		1 as ab_type_id,
		id AS sys_label_id,
		1 AS label_type,
		priority,
		mark,
		status,
		parent_id
	FROM
	hbird_spend_type where `status` = 1 and parent_id is not null
	);

-- 收入  ok
INSERT INTO hbird_account_book_type_label ( `ab_type_id`, `sys_label_id`, `label_type`,`priority`, `mark`, `status`,parent_id ) (
	SELECT
		1 as ab_type_id,
		id AS sys_label_id,
		2 AS label_type,
		priority,
		mark,
		status,
		parent_id
	FROM
	hbird_income_type where `status` = 1 and parent_id is not null
	);

-- 绑定用户--账本类型id 关系 获取账本类型默认赋予  1 日常账本类型id   添加到 用户常用关系排序表中  ok
UPDATE hbird_user_comm_type_priority set ab_type_id = 1;
	


--  更新用户常用支出表中   支出三级类目name icon      运行缓慢  ok
-- 修改 标签名称，icon ok
update hbird_user_comm_use_spend t inner join hbird_spend_type t1 on t.spend_type_id = t1.id
set t.spend_type_name = t1.spend_name,t.icon = t1.icon,t.priority = t1.priority where t.spend_type_name is null and t.icon is null;

-- 更新icon url
update hbird_user_comm_use_spend set `icon`=replace(`icon`,'p9twjlzxw.bkt.clouddn.com','label.image.fengniaojizhang.cn');

-- 修改PID ok
update hbird_user_comm_use_spend t inner join hbird_spend_type t1 on t.spend_type_id = t1.id
set t.spend_type_pid = t1.parent_id where t.spend_type_pid is null;

-- 修改PNAME  ok
update hbird_user_comm_use_spend t inner join hbird_spend_type t1 on t.spend_type_pid = t1.id
set t.spend_type_pname = t1.spend_name where t.spend_type_pname is null;
	

--  更新用户常收入中   支出三级类目name icon      运行缓慢  ok
-- 用户常用收入表  更新收入三级类目name icon ok
update hbird_user_comm_use_income t inner join hbird_income_type t1 on t.income_type_id = t1.id
set t.income_type_name = t1.income_name,t.icon = t1.icon,t.priority = t1.priority where t.income_type_name is null and t.icon is null;

-- 更新icon url
update hbird_user_comm_use_income set `icon`=replace(`icon`,'p9twjlzxw.bkt.clouddn.com','label.image.fengniaojizhang.cn');

-- 修改PID ok
update hbird_user_comm_use_income t inner join hbird_income_type t1 on t.income_type_id = t1.id
set t.income_type_pid = t1.parent_id where t.income_type_pid is null;

-- 修改PNAME  ok
update hbird_user_comm_use_income t inner join hbird_income_type t1 on t.income_type_pid = t1.id
set t.income_type_pname = t1.income_name where t.income_type_pname is null;

-- 删除老版自有标签表重复数据
CREATE TEMPORARY TABLE tmp_table (id int(8) NOT NULL);
insert into tmp_table
select min(t.id) from hbird_user_comm_use_spend t
GROUP BY t.user_info_id,t.spend_type_id,t.spend_type_name HAVING count(t.spend_type_name) > 1;

delete from hbird_user_comm_use_spend where id in (
	select id from tmp_table
);

TRUNCATE table tmp_table;
insert into tmp_table
select min(t.id) from hbird_user_comm_use_income t
GROUP BY t.user_info_id,t.income_type_id HAVING count(t.income_type_name) > 1;

delete from hbird_user_comm_use_income where id in (
	select id from tmp_table
);

TRUNCATE table hbird_user_private_label;

-- 将用户常用支出表中数据迁移到用户自有类目标签表  ok
INSERT INTO `hbird_user_private_label` ( `user_info_id`, `type_pid`, `type_pname`, `type_id`, `type_name`, `icon`, `priority`, `property`, `type`, `status`,ab_type_id) (
	SELECT
		user_info_id,
		spend_type_pid AS type_pid,
		spend_type_pname AS type_pname,
		spend_type_id AS type_id,
		spend_type_name AS type_name,
		icon,
		priority,
		1,
		1,
		1,
		1
	FROM
	hbird_user_comm_use_spend 
	);
	
	
-- 将用户常用收入表中数据迁移到用户自有类目标签表  ok
INSERT INTO `hbird_user_private_label` ( `user_info_id`, `type_pid`, `type_pname`, `type_id`, `type_name`, `icon`, `priority`, `property`, `type`, `status`,ab_type_id) (
	SELECT
		user_info_id,
		income_type_pid AS type_pid,
		income_type_pname AS type_pname,
		income_type_id AS type_id,
		income_type_name AS type_name,
		icon,
		priority,
		2,
		1,
		1,
		1
	FROM
	hbird_user_comm_use_income 
	);

-- 赋值 流水表中  update_date update_by 与 create_date create_by 一致 ok
update hbird_water_order set update_by = create_by where update_by is null;
update hbird_water_order set update_date=create_date where update_date is null;


-- 补全用户上一版删除的自有标签，用户流水关联显示

-- 237
-- 补全新版自有标签中，老版删除的标签  支出的  196
INSERT INTO `hbird_user_private_label` ( `user_info_id`, `type_pid`, `type_pname`, `type_id`, 
`type_name`, `icon`, `priority`, `property`, `type`, `status`,ab_type_id) 
	SELECT
		t.update_by as user_info_id,
		t1.parent_id AS type_pid,
		null AS type_pname,
		t.type_id AS type_id,
		t.type_name AS type_name,
		icon,
		priority,
		1,
		1,
		1,
		1
from (
	select t.update_by,t.type_id,t.type_name
		from (
			select DISTINCT t.update_by,t.type_id ,t.type_name
			from hbird_water_order t where t.type_id is not null and order_type = 1
		)t left join hbird_user_private_label t1 on t.update_by = t1.user_info_id and t.type_id = t1.type_id
		where t1.type_id is null
)t inner join (
 select * from hbird_spend_type t where t.parent_id is not null
	) t1 on t.type_id = t1.id;

-- 补全新版自有标签中，老版删除的标签  收入的  
INSERT INTO `hbird_user_private_label` ( `user_info_id`, `type_pid`, `type_pname`, `type_id`, 
`type_name`, `icon`, `priority`, `property`, `type`, `status`,ab_type_id) 
	SELECT
		t.update_by as user_info_id,
		t1.parent_id AS type_pid,
		null AS type_pname,
		t.type_id AS type_id,
		t.type_name AS type_name,
		icon,
		priority,
		2,
		1,
		1,
		1
from (
	select t.update_by,t.type_id,t.type_name
		from (
			select DISTINCT t.update_by,t.type_id ,t.type_name
			from hbird_water_order t where t.type_id is not null  and order_type = 2
		)t left join hbird_user_private_label t1 on t.update_by = t1.user_info_id and t.type_id = t1.type_id
		where t1.type_id is null
)t left join (
	select * from hbird_income_type t where t.parent_id is not null
) t1 on t.type_id = t1.id;


update hbird_user_private_label t inner join hbird_spend_type t1 on t.type_id = t1.id set t.type_pname = t1.spend_name where t.type_pname is null;

update hbird_user_private_label t inner join hbird_income_type t1 on t.type_id = t1.id set t.type_pname = t1.income_name where t.type_pname is null;




-- 赋值ab_type_label_id  判断是否为已添加标签需要 ok
update hbird_user_private_label t inner join hbird_account_book_type_label t1 
on  t.ab_type_id = t1.ab_type_id and t.type_id = t1.sys_label_id
set ab_type_label_id = t1.id;



-- 修改流水中与用户自有标签表关联数据 ok
update hbird_water_order t inner join hbird_user_private_label t1 on 
t.update_by = t1.user_info_id and t.type_id = t1.type_id
set t.user_private_label_id = t1.id ,t.icon = t1.icon;

update hbird_water_order t inner join hbird_user_private_label t1 on 
t.update_by = t1.user_info_id and t.type_id = t1.type_id
set t.type_name = t1.type_name where t.type_name is null;

	
--  账本表绑定账本类型id  1 成员数初始化为1！！！！！！！！！！！！！！！！！！！！！ OK
update `hbird_account_book` set account_book_type_id=1,member=1,update_date=now();



-- 用户账本关联表  添加是否为用户默认账本标识 ok
update `hbird_user_account_book` set default_flag=1,delflag=0;

-- 设置初始值 账本名称 OK
update hbird_account_book set ab_name='默认账本';




--  插入 用户固定大额支出数  绑定用户id ok
INSERT INTO hbird_accountbook_budget ( fixed_life_expenditure, fixed_large_expenditure, create_by, create_date ) 
select t1.fixed_life_expenditure, t1.fixed_large_expenditure, t1.create_by, t1.create_date 
from (
	select t.create_by,max(t.time) as time
  from hbird_accountbook_budget t where fixed_life_expenditure is not null or fixed_large_expenditure is not null GROUP BY t.create_by
)t inner join hbird_accountbook_budget t1 on t.create_by = t1.create_by and t.time = t1.time;


-- 注掉原有定时任务
UPDATE `t_s_timetask` 
SET `CREATE_BY` = 'admin',
`CREATE_DATE` = '2015-04-02 19:22:49',
`CREATE_NAME` = '管理员',
`CRON_EXPRESSION` = '0 0/1 * * * ?',
`IS_EFFECT` = '0',
`IS_START` = '0',
`TASK_DESCRIBE` = '消息中间件定时任务',
`TASK_ID` = 'smsSendTaskCronTrigger',
`CLASS_NAME` = 'org.jeecgframework.web.system.sms.util.task.SmsSendTask',
`RUN_SERVER_IP` = '本地',
`RUN_SERVER` = '本地',
`UPDATE_BY` = 'admin',
`UPDATE_DATE` = '2018-02-28 18:45:08',
`UPDATE_NAME` = '管理员' 
WHERE
	`ID` = '402880e74c79dd47014c79de88f70001';
	
INSERT INTO `t_s_timetask`(`ID`, `CREATE_BY`, `CREATE_DATE`, `CREATE_NAME`, `CRON_EXPRESSION`, `IS_EFFECT`, `IS_START`, `TASK_DESCRIBE`, `TASK_ID`, `CLASS_NAME`, `RUN_SERVER_IP`, `RUN_SERVER`, `UPDATE_BY`, `UPDATE_DATE`, `UPDATE_NAME`) VALUES ('402880e74c79dd47014c79de88f70002', 'admin', '2018-11-29 16:01:38', '管理员', '0 0 9 1 1-12 ? *', '1', '1', '账单通知定时任务', 'accountNotifyTaskCronTrigger', 'com.fnjz.front.timer.AccountNotify', '本地', '本地', 'admin', '2018-11-29 16:01:38', '管理员');
INSERT INTO `t_s_timetask`(`ID`, `CREATE_BY`, `CREATE_DATE`, `CREATE_NAME`, `CRON_EXPRESSION`, `IS_EFFECT`, `IS_START`, `TASK_DESCRIBE`, `TASK_ID`, `CLASS_NAME`, `RUN_SERVER_IP`, `RUN_SERVER`, `UPDATE_BY`, `UPDATE_DATE`, `UPDATE_NAME`) VALUES ('402880e74c79dd47014c79de88f70003', 'admin', '2018-11-29 16:01:38', '管理员', '0 0 3 1 1-12 ? *', '1', '1', '账单通知定时预处理任务', 'prepareAccountNotifyTaskCronTrigger', 'com.fnjz.front.timer.PrepareAccountNotify', '本地', '本地', 'admin', '2018-11-29 16:01:38', '管理员');



