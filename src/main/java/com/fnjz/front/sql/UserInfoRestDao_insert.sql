INSERT INTO `hbird_user_info`
( mobile, password, mobile_system , mobile_system_version,mobile_manufacturer,mobile_device,android_channel,login_ip, register_date,wechat_auth,nick_name,sex,province_name,city_name,avatar_url,user_type )
VALUES
	(  :userInfoRestEntity.mobile,
	 :userInfoRestEntity.password,
	 :userInfoRestEntity.mobileSystem,
	 :userInfoRestEntity.mobileSystemVersion,
	 :userInfoRestEntity.mobileManufacturer,
	 :userInfoRestEntity.mobileDevice,
	 :userInfoRestEntity.androidChannel,
	 :userInfoRestEntity.loginIp,
	 NOW(),
	 :userInfoRestEntity.wechatAuth,
  :userInfoRestEntity.nickName,
  :userInfoRestEntity.sex,
  :userInfoRestEntity.provinceName,
  :userInfoRestEntity.cityName,
  :userInfoRestEntity.avatarUrl,
  :userInfoRestEntity.userType);