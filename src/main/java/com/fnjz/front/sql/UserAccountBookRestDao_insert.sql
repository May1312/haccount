INSERT INTO `hbird_user_account_book`
( user_info_id, account_book_id, user_type , create_date,create_by,create_name,default_flag,delflag )
VALUES
	(  :userAccountBookRestEntity.userInfoId,
	 :userAccountBookRestEntity.accountBookId,
	 :userAccountBookRestEntity.userType,
	 NOW(),
	 :userAccountBookRestEntity.createBy,
	 :userAccountBookRestEntity.createName,
	 :userAccountBookRestEntity.defaultFlag,
	 0);