UPDATE `hbird_user_sign_in_award`
SET 
	   <#if bean.cycleAwardStatus ?exists>
		   cycle_award_status = :bean.cycleAwardStatus,
		</#if>
	   <#if bean.getTimes ?exists>
		   get_times = get_times+:bean.getTimes,
		</#if>
	   <#if bean.delflag ?exists>
		   delflag = :bean.delflag,
		   </#if>
		   update_date=now()
WHERE user_info_id = :bean.userInfoId and cycle=:bean.cycle;