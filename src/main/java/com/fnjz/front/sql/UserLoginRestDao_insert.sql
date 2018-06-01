INSERT INTO `hbird_account`.`hbird_user_login`
( mobile, password,login_ip, register_date,wechat_auth,user_info_id )
VALUES
	(  :userLoginRestEntity.mobile,
	 :userLoginRestEntity.password,
	 :userLoginRestEntity.loginIp,
	 NOW(),
	 :userLoginRestEntity.wechatAuth,
	 :userLoginRestEntity.userInfoId);