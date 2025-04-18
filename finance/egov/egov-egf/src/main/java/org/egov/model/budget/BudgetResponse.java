package org.egov.model.budget;

import java.math.BigDecimal;

public class BudgetResponse {
	private BigDecimal budgetAmount;

	public BudgetResponse(BigDecimal budgetAmount) {
		super();
		this.budgetAmount = budgetAmount;
	}

	public BigDecimal getBudgetAmount() {
		return budgetAmount;
	}

	public void setBudgetAmount(BigDecimal budgetAmount) {
		this.budgetAmount = budgetAmount;
	}

}
