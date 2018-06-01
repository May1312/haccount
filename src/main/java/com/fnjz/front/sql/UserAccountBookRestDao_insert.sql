INSERT INTO `hbird_account`.`hbird_user_account_book`
( user_info_id, account_book_id, user_type , create_date,create_by,create_name )
VALUES
	(  :userAccountBookRestEntity.userInfoId,
	 :userAccountBookRestEntity.accountBookId,
	 :userAccountBookRestEntity.userType,
	 NOW(),
	 :userAccountBookRestEntity.createBy,
	 :userAccountBookRestEntity.createName);