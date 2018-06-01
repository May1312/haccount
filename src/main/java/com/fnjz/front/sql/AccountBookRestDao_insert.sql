INSERT INTO `hbird_account`.`hbird_account_book`
( ab_name, status, create_date , create_by,create_name )
VALUES
	(  :accountBookRestEntity.abName,
	 :accountBookRestEntity.status,
	 NOW(),
	 :accountBookRestEntity.createBy,
	 :accountBookRestEntity.createName);