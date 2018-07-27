INSERT INTO `hbird_account`.`hbird_accountbook_budget`
( account_book_id, budget_money, time , fixed_large_expenditure,fixed_life_expenditure,create_date,create_by )
VALUES
	(  :budget.accountBookId,
	 :budget.budgetMoney,
	 DATE_FORMAT(NOW(),'%Y-%m'),
	 :budget.fixedLargeExpenditure,
	 :budget.fixedLifeExpenditure,
	 NOW(),
	 :budget.createBy);