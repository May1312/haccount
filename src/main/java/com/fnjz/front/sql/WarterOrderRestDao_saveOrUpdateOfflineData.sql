INSERT INTO `hbird_account`.`hbird_water_order`
(`id`,`account_book_id`, `money`, `order_type`, `is_staged`, `spend_happiness`, `use_degree`, `type_pid`, `type_pname`, `type_id`, `type_name`, `parent_id`, `picture_url`, `create_date`,`update_date`, `charge_date`, `delflag`, `del_date`,`create_by`, `create_name`, `update_by`, `update_name`,`remark`)
VALUES(
       :charge.id,
		   :charge.accountBookId,
		   :charge.money,
		   :charge.orderType,
		   :charge.isStaged,
		   :charge.spendHappiness,
		   :charge.useDegree,
		   :charge.typePid,
		   :charge.typePname,
		   :charge.typeId,
		   :charge.typeName,
		   :charge.parentId,
		   :charge.pictureUrl,
		   :charge.createDate,
		   :charge.updateDate,
		   :charge.chargeDate,
		   :charge.delflag,
		   :charge.delDate,
		   :charge.createBy,
		   :charge.createName,
		   :charge.updateBy,
		   :charge.updateName,
		   :charge.remark
) ON DUPLICATE KEY UPDATE `money`=:charge.money,`order_type`=:charge.orderType,`spend_happiness`=:charge.spendHappiness,`type_pid`=:charge.typePid,`type_pname`=:charge.typePname,`type_id`=:charge.typeId,`type_name`=:charge.typeName,`update_date`=:charge.updateDate,`charge_date`=:charge.chargeDate,`delflag`=:charge.delflag,`del_date`=:charge.delDate,`update_by`=:charge.updateBy,`update_name`=:charge.updateName,`remark`=:charge.remark;