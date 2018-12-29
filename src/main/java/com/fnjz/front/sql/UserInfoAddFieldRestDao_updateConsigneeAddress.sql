UPDATE `hbird_user_info_add_field`
SET 
	   <#if bean.consigneeName ?exists>
		   consignee_name = :bean.consigneeName,
		</#if>
	   <#if bean.consigneeMobile ?exists>
		   consignee_mobile = :bean.consigneeMobile,
		</#if>
	   <#if bean.consigneeProvince ?exists>
		   consignee_province = :bean.consigneeProvince,
		</#if>
	    <#if bean.consigneeCity ?exists>
		   consignee_city = :bean.consigneeCity,
		</#if>
	   <#if bean.consigneeDistrict ?exists>
		   consignee_district = :bean.consigneeDistrict,
		</#if>
	   <#if bean.consigneeDetail ?exists>
		   consignee_detail = :bean.consigneeDetail,
		   </#if>
		   update_date=now()
WHERE id = :bean.id