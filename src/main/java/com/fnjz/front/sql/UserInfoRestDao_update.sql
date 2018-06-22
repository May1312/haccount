UPDATE `hbird_account`.`hbird_user_info`
SET 
	   <#if userInfoRestEntity.accountBookId ?exists>
		   nick_name = :userInfoRestEntity.nickName,
		</#if>
	   <#if userInfoRestEntity.money ?exists>
		   sex = :userInfoRestEntity.sex,
		</#if>
	   <#if userInfoRestEntity.orderType ?exists>
		   birthday = :userInfoRestEntity.birthday,
		</#if>
	    <#if userInfoRestEntity.isStaged ?exists>
		   province_id = :userInfoRestEntity.provinceId,
		</#if>
	   <#if userInfoRestEntity.spendHappiness ?exists>
		   province_name = :userInfoRestEntity.provinceName,
		</#if>
	   <#if userInfoRestEntity.useDegree ?exists>
		   city_id = :userInfoRestEntity.cityId,
		</#if>
	    <#if userInfoRestEntity.typePid ?exists>
		   city_name = :userInfoRestEntity.cityName,
		</#if>
	   <#if userInfoRestEntity.typePname ?exists>
		   profession = :userInfoRestEntity.profession,
		</#if>
	    <#if userInfoRestEntity.typeId ?exists>
		   position = :userInfoRestEntity.position,
		</#if>
		<#if userInfoRestEntity.typeName ?exists>
		   age = :userInfoRestEntity.age,
		</#if>
		<#if userInfoRestEntity.parentId ?exists>
		   constellation = :userInfoRestEntity.constellation,
		</#if>
		<#if userInfoRestEntity.pictureUrl ?exists>
		   avatar_url = :userInfoRestEntity.avatarUrl,
		</#if>
		   update_date = NOW()
WHERE id = :userInfoRestEntity.id