UPDATE `hbird_account`.`hbird_water_order`
SET 
	   <#if warterOrderRestEntity.accountBookId ?exists>
		   account_book_id = :warterOrderRestEntity.accountBookId,
		</#if>
	   <#if warterOrderRestEntity.money ?exists>
		   money = :warterOrderRestEntity.money,
		</#if>
	   <#if warterOrderRestEntity.orderType ?exists>
		   order_type = :warterOrderRestEntity.orderType,
		</#if>
	    <#if warterOrderRestEntity.isStaged ?exists>
		   is_staged = :warterOrderRestEntity.isStaged,
		</#if>
	   <#if warterOrderRestEntity.spendHappiness ?exists>
		   spend_happiness = :warterOrderRestEntity.spendHappiness,
		</#if>
	   <#if warterOrderRestEntity.useDegree ?exists>
		   useDegree = :warterOrderRestEntity.useDegree,
		</#if>
	    <#if warterOrderRestEntity.typePid ?exists>
		   type_pid = :warterOrderRestEntity.typePid,
		</#if>
	   <#if warterOrderRestEntity.typePname ?exists>
		   type_pname = :warterOrderRestEntity.typePname,
		</#if>
	    <#if warterOrderRestEntity.typeId ?exists>
		   type_id = :warterOrderRestEntity.typeId,
		</#if>
		<#if warterOrderRestEntity.typeName ?exists>
		   type_name = :warterOrderRestEntity.typeName,
		</#if>
		<#if warterOrderRestEntity.parentId ?exists>
		   parent_id = :warterOrderRestEntity.parentId,
		</#if>
		<#if warterOrderRestEntity.pictureUrl ?exists>
		   picture_url = :warterOrderRestEntity.pictureUrl,
		</#if>
		<#if warterOrderRestEntity.updateDate ?exists>
		   update_date = :warterOrderRestEntity.updateDate,
		</#if>
		<#if warterOrderRestEntity.chargeDate ?exists>
		   charge_date = :warterOrderRestEntity.chargeDate,
		</#if>
		<#if warterOrderRestEntity.updateBy ?exists>
		   update_by = :warterOrderRestEntity.updateBy,
		</#if>
		<#if warterOrderRestEntity.updateName ?exists>
		   update_name = :warterOrderRestEntity.updateName,
		</#if>
		<#if warterOrderRestEntity.remark ?exists>
		   remark = :warterOrderRestEntity.remark,
		</#if>

WHERE id = :warterOrderRestEntity.id