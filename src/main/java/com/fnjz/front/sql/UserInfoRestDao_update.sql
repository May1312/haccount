UPDATE `hbird_account`.`hbird_user_info`
SET 
	   <#if userInfoRestEntity.nickName ?exists>
		   nick_name = :userInfoRestEntity.nickName,
		</#if>
	   <#if userInfoRestEntity.sex ?exists>
		   sex = :userInfoRestEntity.sex,
		</#if>
	   <#if userInfoRestEntity.birthday ?exists>
		   birthday = :userInfoRestEntity.birthday,
		</#if>
	    <#if userInfoRestEntity.provinceId ?exists>
		   province_id = :userInfoRestEntity.provinceId,
		</#if>
	   <#if userInfoRestEntity.provinceName ?exists>
		   province_name = :userInfoRestEntity.provinceName,
		</#if>
	   <#if userInfoRestEntity.cityId ?exists>
		   city_id = :userInfoRestEntity.cityId,
		</#if>
	    <#if userInfoRestEntity.cityName ?exists>
		   city_name = :userInfoRestEntity.cityName,
		</#if>
	   <#if userInfoRestEntity.profession ?exists>
		   profession = :userInfoRestEntity.profession,
		</#if>
	    <#if userInfoRestEntity.position ?exists>
		   position = :userInfoRestEntity.position,
		</#if>
		<#if userInfoRestEntity.age ?exists>
		   age = :userInfoRestEntity.age,
		</#if>
		<#if userInfoRestEntity.constellation ?exists>
		   constellation = :userInfoRestEntity.constellation,
		</#if>
		<#if userInfoRestEntity.avatarUrl ?exists>
		   avatar_url = :userInfoRestEntity.avatarUrl,
		</#if>
		   update_date = NOW()
WHERE id = :userInfoRestEntity.id