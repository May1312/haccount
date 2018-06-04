INSERT INTO `hbird_account`.`hbird_user_info`
( mobile, password, mobile_system , mobile_system_version,mobile_manufacturer,mobile_device,login_ip, register_date,wechat_auth,nick_name,sex,province_name,city_name,avatar_url )
VALUES
	(  :userInfoRestEntity.mobile,
	 :userInfoRestEntity.password,
	 :userInfoRestEntity.mobileSystem,
	 :userInfoRestEntity.mobileSystemVersion,
	 :userInfoRestEntity.mobileManufacturer,
	 :userInfoRestEntity.mobileDevice,
	 :userInfoRestEntity.loginIp,
	 NOW(),
	 :userInfoRestEntity.wechatAuth,
  :userInfoRestEntity.nickName,
  :userInfoRestEntity.sex,
  :userInfoRestEntity.provinceName,
  :userInfoRestEntity.cityName,
  :userInfoRestEntity.avatarUrl);