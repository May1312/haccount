INSERT INTO `hbird_accountbook_budget`
( account_book_id, budget_money, time , fixed_large_expenditure,fixed_life_expenditure,create_date,create_by,begin_time,end_time,scene_type )
VALUES
	(  :budget.accountBookId,
	 :budget.budgetMoney,
	 :budget.time,
	 :budget.fixedLargeExpenditure,
	 :budget.fixedLifeExpenditure,
	 NOW(),
	 :budget.createBy,
	 :budget.beginTime,
	 :budget.endTime,
	 :budget.sceneType);