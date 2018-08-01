UPDATE `hbird_account`.`hbird_water_order`
SET
	   <#if warterOrderRestEntity.money ?exists>
		   money = :warterOrderRestEntity.money,
		</#if>
	   <#if warterOrderRestEntity.orderType ?exists>
		   order_type = :warterOrderRestEntity.orderType,
		</#if>
		<#if warterOrderRestEntity.orderType ?exists>
      <#if warterOrderRestEntity.orderType ==1>
        <#if warterOrderRestEntity.isStaged ?exists>
        /*设置支出类型*/
		      is_staged = :warterOrderRestEntity.isStaged,
		    </#if>
		    /*设置愉悦度*/
        <#if warterOrderRestEntity.spendHappiness ?exists>
		      spend_happiness = :warterOrderRestEntity.spendHappiness,
		    </#if>
       <#elseif  warterOrderRestEntity.orderType ==2>
       /*收入类型 愉悦度/支出类型必须置为null*/
       spend_happiness = :warterOrderRestEntity.spend_happiness,
       is_staged = :warterOrderRestEntity.isStaged,
      </#if>
      /*orderType 为null情况下*/
      <#else>
        <#if warterOrderRestEntity.spendHappiness ?exists>
		      spend_happiness = :warterOrderRestEntity.spendHappiness,
		    </#if>
		    <#if warterOrderRestEntity.isStaged ?exists>
		      is_staged = :warterOrderRestEntity.isStaged,
		    </#if>
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