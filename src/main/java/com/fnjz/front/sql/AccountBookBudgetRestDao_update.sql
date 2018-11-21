UPDATE `hbird_accountbook_budget`
SET
	   <#if budget.budgetMoney ?exists>
		   budget_money = :budget.budgetMoney,
		</#if>
	   <#if budget.fixedLargeExpenditure ?exists>
		   fixed_large_expenditure = :budget.fixedLargeExpenditure,
		</#if>
	   <#if budget.fixedLifeExpenditure ?exists>
		   fixed_life_expenditure = :budget.fixedLifeExpenditure,
		</#if>
		<#if budget.updateBy ?exists>
		   update_by = :budget.updateBy,
		</#if>
		   update_date = NOW(),
		<#if budget.beginTime ?exists>
		   begin_time = :budget.beginTime,
		</#if>
		<#if budget.endTime ?exists>
		   end_time = :budget.endTime,
		</#if>
		<#if budget.sceneType ?exists>
		   scene_type = :budget.sceneType
		</#if>
WHERE id = :budget.id