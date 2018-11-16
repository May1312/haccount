INSERT INTO `hbird_account_book`
( ab_name, status, create_date , create_by,create_name,account_book_type_id,member,update_date)
VALUES
	(  :accountBookRestEntity.abName,
	 :accountBookRestEntity.status,
	 NOW(),
	 :accountBookRestEntity.createBy,
	 :accountBookRestEntity.createName,
	 :accountBookRestEntity.accountBookTypeId,
	 1,
	 NOW());