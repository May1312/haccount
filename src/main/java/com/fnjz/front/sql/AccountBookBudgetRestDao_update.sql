UPDATE `hbird_account`.`hbird_accountbook_budget`
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
		   update_date = NOW()
WHERE id = :budget.id