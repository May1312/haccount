INSERT INTO `hbird_account`.`hbird_water_order`
(`account_book_id`, `money`, `order_type`, `is_staged`, `spend_happiness`, `use_degree`, `type_pid`, `type_pname`, `type_id`, `type_name`, `parent_id`, `picture_url`, `create_date`, `charge_date`, `delflag`, `create_by`, `create_name`, `remark`)
VALUES(
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
		NOW(),
		   :charge.chargeDate,
		   :charge.delflag,
		   :charge.createBy,
		   :charge.createName,
		   :charge.remark
);