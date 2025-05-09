
/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.egf.web.actions.report;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.egf.model.Statement;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.services.report.ReceiptsPaymentsScheduleService;
import org.egov.services.report.ReceiptsPaymentsService;
import org.egov.utils.Constants;
import org.egov.utils.ReportHelper;
import org.hibernate.FlushMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

@ParentPackage("egov")
@Results({ @Result(name = "report", location = "receiptsPaymentsReport-report.jsp"),
		//@Result(name = "scheduleResults", location = "receiptsPaymentsReport-scheduleResults.jsp"),
		@Result(name = "allScheduleResults", location = "cashFlowReport-allScheduleResults.jsp"),
		//@Result(name = "results", location = "cashFlowReport-results.jsp"),
		@Result(name = "detailResults", location = "receiptPaymentsReport-detailResults.jsp"),
		@Result(name = "results", location = "receiptsPayments-results.jsp"),
		@Result(name = "PDF", type = "stream", location = Constants.INPUT_STREAM, params = { Constants.INPUT_NAME,
				Constants.INPUT_STREAM, Constants.CONTENT_TYPE, "application/pdf", Constants.CONTENT_DISPOSITION,
				"no-cache;filename=receiptsandPaymentsStatement.pdf" }),
		@Result(name = "XLS", type = "stream", location = Constants.INPUT_STREAM, params = { Constants.INPUT_NAME,
				Constants.INPUT_STREAM, Constants.CONTENT_TYPE, "application/xls", Constants.CONTENT_DISPOSITION,
				"no-cache;filename=receiptsandPaymentsStatement.xls" }) })
public class ReceiptsPaymentsReportAction extends BaseFormAction {
	/**
	 *
	 */
	private static final long serialVersionUID = 91711010096900620L;
	private static final String INCOME_EXPENSE_PDF = "PDF";
	private static final String INCOME_EXPENSE_XLS = "XLS";
	private static SimpleDateFormat FORMATDDMMYYYY = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
	InputStream inputStream;
	ReportHelper reportHelper;
	
	//receiptsandPaymentsStatement
	Statement receiptsandPaymentsStatement = new Statement();
	ReceiptsPaymentsScheduleService receiptsPaymentsScheduleService;
	private String majorCode;
	private String minorCode;
	private String scheduleNo;
	private String financialYearId;
	// private String asOndate;
	private Date todayDate;
	private String asOnDateRange;
	private String period;
	private Integer fundId;
	private final StringBuffer heading = new StringBuffer();
	private StringBuffer scheduleheading = new StringBuffer();
	private StringBuffer statementheading = new StringBuffer();
	private Map<String, String> reportMap = new HashMap<>();
	List<CChartOfAccounts> listChartOfAccounts;
	private boolean detailReport = false;
	
	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	@Autowired
	private EgovMasterDataCaching masterDataCache;

	@Autowired
	private CityService cityService;
	
	@Autowired
	private ReceiptsPaymentsService  receiptsPaymentsService;
	
	

	public Map<String, String> getReportMap() {
		return reportMap;
	}

	public void setReportMap(Map<String, String> reportMap) {
		this.reportMap = reportMap;
	}

	public void setReceiptsPaymentsService(final ReceiptsPaymentsService receiptsPaymentsService) {
		this.receiptsPaymentsService = receiptsPaymentsService;
	}

	public void setReceiptsPaymentsScheduleService(final ReceiptsPaymentsScheduleService receiptsPaymentsScheduleService) {
		this.receiptsPaymentsScheduleService = receiptsPaymentsScheduleService;
	}

	public void setReportHelper(final ReportHelper reportHelper) {
		this.reportHelper = reportHelper;
	}

	public InputStream getInputStream() {
		return inputStream;
	}

	public Statement getReceiptsandPaymentsStatement() {
		return receiptsandPaymentsStatement;
	}
//CashFlowReportAction
	
	public ReceiptsPaymentsReportAction() {
		addRelatedEntity("department", Department.class);
		addRelatedEntity("function", CFunction.class);
		addRelatedEntity("functionary", Functionary.class);
		addRelatedEntity("financialYear", CFinancialYear.class);
		addRelatedEntity("field", Boundary.class);
		addRelatedEntity("fund", Fund.class);
	}

	@Override
	public void prepare() {
		persistenceService.getSession().setDefaultReadOnly(true);
		persistenceService.getSession().setFlushMode(FlushMode.MANUAL);
		super.prepare();
		if (!parameters.containsKey("showDropDown")) {
			addDropdownData("departmentList", masterDataCache.get("egi-department"));
			addDropdownData("functionList", masterDataCache.get("egi-function"));
//            addDropdownData("functionaryList", masterDataCache.get("egi-functionary"));
			addDropdownData("fundDropDownList", masterDataCache.get("egi-fund"));
//            addDropdownData("fieldList", masterDataCache.get("egi-ward"));
			addDropdownData("financialYearList", getPersistenceService()
					.findAllBy("from CFinancialYear where isActive=true  order by finYearRange desc "));
		}
	}

	protected void setRelatedEntitesOn() {
		setTodayDate(new Date());
		if (receiptsandPaymentsStatement.getFund() != null && receiptsandPaymentsStatement.getFund().getId() != null
				&& receiptsandPaymentsStatement.getFund().getId() != 0) {
			receiptsandPaymentsStatement.setFund(
					(Fund) getPersistenceService().find("from Fund where id=?", receiptsandPaymentsStatement.getFund().getId()));
			heading.append(" in " + receiptsandPaymentsStatement.getFund().getName());
		}
		if (receiptsandPaymentsStatement.getDepartment() != null && receiptsandPaymentsStatement.getDepartment().getCode() != null
				&& !"null".equalsIgnoreCase(receiptsandPaymentsStatement.getDepartment().getCode())) {
//            incomeExpenditureStatement.setDepartment((Department) getPersistenceService().find("from Department where id=?",
//                    incomeExpenditureStatement.getDepartment().getId()));
			Department dept = microserviceUtils.getDepartmentByCode(receiptsandPaymentsStatement.getDepartment().getCode());
			receiptsandPaymentsStatement.setDepartment(dept);
			heading.append(" in " + receiptsandPaymentsStatement.getDepartment().getName() + " Department");
		} else
			receiptsandPaymentsStatement.setDepartment(null);
		if (receiptsandPaymentsStatement.getFinancialYear() != null && receiptsandPaymentsStatement.getFinancialYear().getId() != null
				&& receiptsandPaymentsStatement.getFinancialYear().getId() != 0) {
			receiptsandPaymentsStatement.setFinancialYear((CFinancialYear) getPersistenceService()
					.find("from CFinancialYear where id=?", receiptsandPaymentsStatement.getFinancialYear().getId()));
			heading.append(" for the Financial Year " + receiptsandPaymentsStatement.getFinancialYear().getFinYearRange());
		}
		if (receiptsandPaymentsStatement.getFunction() != null && receiptsandPaymentsStatement.getFunction().getId() != null
				&& receiptsandPaymentsStatement.getFunction().getId() != 0) {
			receiptsandPaymentsStatement.setFunction((CFunction) getPersistenceService().find("from CFunction where id=?",
					receiptsandPaymentsStatement.getFunction().getId()));
			heading.append(" in Function Code " + receiptsandPaymentsStatement.getFunction().getName());
		}
		if (receiptsandPaymentsStatement.getField() != null && receiptsandPaymentsStatement.getField().getId() != null
				&& receiptsandPaymentsStatement.getField().getId() != 0) {
			receiptsandPaymentsStatement.setField((Boundary) getPersistenceService().find("from Boundary where id=?",
					receiptsandPaymentsStatement.getField().getId()));
			heading.append(" in the field value" + receiptsandPaymentsStatement.getField().getName());
		}

		if (receiptsandPaymentsStatement.getFunctionary() != null && receiptsandPaymentsStatement.getFunctionary().getId() != null
				&& receiptsandPaymentsStatement.getFunctionary().getId() != 0) {
			receiptsandPaymentsStatement.setFunctionary((Functionary) getPersistenceService().find("from Functionary where id=?",
					receiptsandPaymentsStatement.getFunctionary().getId()));
			heading.append(" and " + receiptsandPaymentsStatement.getFunctionary().getName() + " Functionary");
		}

	}

	public void setReceiptsandPaymentsStatement(final Statement receiptsPaymentsStatement) {
		this.receiptsandPaymentsStatement = receiptsPaymentsStatement;
	}

	@Override
	public Object getModel() {
		return receiptsandPaymentsStatement;
	}
	
	
	//receiptsandPaymentsReport-generateReceiptandPaymentsReport.action

	@Action(value = "/report/receiptsandPaymentsReport-generateReceiptandPaymentsReport")
	public String generateReceiptsandPaymentsReport() {
		// populateDataSource();
    System.out.println("Helloooo++++++++++++++++++++++++++++++");
		return "report";
	}

	
	
	/*
	 * @ReadOnly
	 *      
	 * @Action(value = "/report/cashFlowReport-generateCashFlowSubReport") public
	 * String generateCashFlowSubReport() { setDetailReport(false);
	 * populateDataSourceForSchedule(); return "scheduleResults"; }
	 * 
	 * @ReadOnly
	 * 
	 * @Action(value = "/report/cashFlowReport-generateScheduleReport") public
	 * String generateScheduleReport() { populateDataSourceForAllSchedules(); return
	 * "allScheduleResults"; }
	 * 
	 * @ReadOnly
	 * 
	 * @Action(value = "/report/cashFlowReport-generateDetailCodeReport") public
	 * String generateDetailCodeReport() { setDetailReport(true);
	 * populateSchedulewiseDetailCodeReport(); return "scheduleResults"; }
	 * 
	 * private void populateSchedulewiseDetailCodeReport() { setRelatedEntitesOn();
	 * scheduleheading.append("Csh and Flow Schedule Statement").append(heading); if
	 * (receiptsandPaymentsStatement.getFund() != null && receiptsandPaymentsStatement.getFund().getId()
	 * != null && receiptsandPaymentsStatement.getFund().getId() != 0) { final List<Fund>
	 * fundlist = new ArrayList<Fund>(); fundlist.add(receiptsandPaymentsStatement.getFund());
	 * receiptsandPaymentsStatement.setFunds(fundlist);
	 * cashFlowScheduleService.populateDetailcode(receiptsandPaymentsStatement);
	 * 
	 * } else { receiptsandPaymentsStatement.setFunds(cashFlowService.getFunds());
	 * cashFlowScheduleService.populateDetailcode(receiptsandPaymentsStatement); } }
	 * 
	 * 
	 * private void populateDataSourceForSchedule() { setDetailReport(false);
	 * setRelatedEntitesOn();
	 * 
	 * scheduleheading.append("Cash Flow Schedule Statement").append(heading); if
	 * (receiptsandPaymentsStatement.getFund() != null && receiptsandPaymentsStatement.getFund().getId()
	 * != null && receiptsandPaymentsStatement.getFund().getId() != 0) { final List<Fund>
	 * fundlist = new ArrayList<Fund>(); fundlist.add(receiptsandPaymentsStatement.getFund());
	 * receiptsandPaymentsStatement.setFunds(fundlist);
	 * cashFlowScheduleService.populateDataForLedgerSchedule(receiptsandPaymentsStatement,
	 * parameters.get("majorCode")[0]);
	 * 
	 * } else { receiptsandPaymentsStatement.setFunds(cashFlowService.getFunds());
	 * cashFlowScheduleService.populateDataForLedgerSchedule(receiptsandPaymentsStatement,
	 * parameters.get("majorCode")[0]);
	 * 
	 * } }
	 * 
	 * private void populateDataSourceForAllSchedules() { setRelatedEntitesOn(); if
	 * (receiptsandPaymentsStatement.getFund() != null && receiptsandPaymentsStatement.getFund().getId()
	 * != null && receiptsandPaymentsStatement.getFund().getId() != 0) { final List<Fund>
	 * fundlist = new ArrayList<Fund>(); fundlist.add(receiptsandPaymentsStatement.getFund());
	 * receiptsandPaymentsStatement.setFunds(fundlist);
	 * cashFlowScheduleService.populateDataForAllSchedules(receiptsandPaymentsStatement); }
	 * else { //receiptsandPaymentsStatement.setFunds(cashFlowService.getFunds());
	 * cashFlowScheduleService.populateDataForAllSchedules(receiptsandPaymentsStatement); } }
	 * 
	 */
	public String printReceiptsandPaymentsReport() {
		populateDataSource();
		return "report";
	}
  //report/receiptsandPayments-ajaxPrintReceiptsPaymentsReport.action?
	
	//receiptsandPayments-ajaxPrintReceiptsPaymentsReport.action
	
	@Action(value = "/report/receiptsandPayments-ajaxPrintReceiptsPaymentsReport")
	public String ajaxPrintCashFlowReport() {
		// populateDataSource();
		populateReceiptsandPaymentsReportResult();
		return "results";
	}

	
	//added by me
	@Action(value ="/report/receiptsandPaymentsReport-generateDetailCodeReport")
	public String ajaxPrintReceiptsandPaymentsReportResult() {
		//statementheading.append("Cash-Flow-Statement").append(heading);
		// populateDataSource();
		populateReceiptsandPaymentsReportResult();
		return "detailResults";
	}
	
//me
	private void populateReceiptsandPaymentsReportResult() {
		statementheading.append("Receipts-Payments-Report").append(heading);
		if (receiptsPaymentsService== null)
			System.out.println("Receipt Payments service null hai");
		reportMap= receiptsPaymentsService.fetchReportData();
		

	}

	
	  protected void populateDataSource() {
	  
	  setRelatedEntitesOn();
	  
	  statementheading.append("Receipts-Payments-Statement").append(heading); if
	  (receiptsandPaymentsStatement.getFund() != null && receiptsandPaymentsStatement.getFund().getId()
	  != null && receiptsandPaymentsStatement.getFund().getId() != 0) { final List<Fund>
	  fundlist = new ArrayList<Fund>(); fundlist.add(receiptsandPaymentsStatement.getFund());
	  receiptsandPaymentsStatement.setFunds(fundlist);
	  receiptsPaymentsService.populateRPStatement(receiptsandPaymentsStatement); } else {
	  //receiptsandPaymentsStatement.setFunds(receiptsPaymentsService.getFunds());
	  receiptsPaymentsService.populateRPStatement(receiptsandPaymentsStatement); } 
	  }
	 
	  
	  
	  
	  
	  

	  
	  /***
	  @Action(value = "/report/cashFlowReport-generateCashFlowPdf") public String
	  generateCahFlowPdf() throws JRException, IOException {
	  //populateDataSource(); final String heading = ReportUtil.getCityName()
	  +" "+(cityService.getCityGrade()==null ? "" :cityService.getCityGrade()) +
	  "\\n" + statementheading.toString(); final String subtitle =
	  "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate()); final JasperPrint
	  jasper = reportHelper.generateCashFlowReportJasperPrint(receiptsandPaymentsStatement,
	  heading, getPreviousYearToDate(), getCurrentYearToDate(), subtitle, true);
	  inputStream = reportHelper.exportPdf(inputStream, jasper); return
	  INCOME_EXPENSE_PDF; }
	  
	  @ReadOnly
	  
	  @Action(value = "/report/cashFlowReport-generateDetailCodePdf") public String
	  generateDetailCodePdf() throws JRException, IOException {
	  populateSchedulewiseDetailCodeReport(); final String heading =
	  ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? ""
	  :cityService.getCityGrade()) + "\\n" + statementheading.toString(); final
	  String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate());
	  final JasperPrint jasper =
	  reportHelper.generatecashFlowReportJasperPrint(receiptsandPaymentsStatement, heading,
	  getPreviousYearToDate(), getCurrentYearToDate(), subtitle, true); inputStream
	  = reportHelper.exportPdf(inputStream, jasper); return INCOME_EXPENSE_PDF; }
	  
	  @ReadOnly
	  
	  @Action(value = "/report/cashFlowReport-generateDetailCodeXls") public String
	  generateDetailCodeXls() throws JRException, IOException {
	  populateSchedulewiseDetailCodeReport(); final String heading =
	  ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? ""
	  :cityService.getCityGrade()) + "\\n" + statementheading.toString(); final
	  String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate())
	  + "                                               "; final JasperPrint jasper
	  = reportHelper.generateCashFlowReportJasperPrint(receiptsandPaymentsStatement, heading,
	  getPreviousYearToDate(), getCurrentYearToDate(), subtitle, true); inputStream
	  = reportHelper.exportXls(inputStream, jasper); return INCOME_EXPENSE_XLS; }
	  
	  public String getUlbName() { final Query query =
	  persistenceService.getSession().createSQLQuery(
	  "select name from companydetail"); final List<String> result = query.list();
	  if (result != null) return result.get(0); return ""; }
	  
	  
	  @ReadOnly
	  
	  @Action(value = "/report/cashFlowReport-generateCashFlowXls") public String
	  generateCashFlowXls() throws JRException, IOException {
	  //populateDataSource(); final String heading = ReportUtil.getCityName()
	  +" "+(cityService.getCityGrade()==null ? "" :cityService.getCityGrade()) +
	  "\\n" + statementheading.toString(); final String subtitle =
	  "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate()) +
	  "                                               "; final JasperPrint jasper =
	  reportHelper.generateCashFlowReportJasperPrint(receiptsandPaymentsStatement, heading,
	  getPreviousYearToDate(), getCurrentYearToDate(), subtitle, true); inputStream
	  = reportHelper.exportXls(inputStream, jasper); return INCOME_EXPENSE_XLS; }
	  
	  @ReadOnly
	  
	  @Action(value = "/report/cashFlowReport-generateSchedulePdf") public String
	  generateSchedulePdf() throws JRException, IOException {
	  populateDataSourceForAllSchedules(); final JasperPrint jasper =
	  reportHelper.generateFinancialStatementReportJasperPrint(receiptsandPaymentsStatement,
	  getText("report.ie.heading"), heading.toString(), getPreviousYearToDate(),
	  getCurrentYearToDate(), false); inputStream =
	  reportHelper.exportPdf(inputStream, jasper); return INCOME_EXPENSE_PDF; }
	  
	  @ReadOnly
	  
	  @Action(value = "/report/cashFlowReport-generateScheduleXls") public String
	  generateScheduleXls() throws JRException, IOException {
	  populateDataSourceForAllSchedules(); final JasperPrint jasper =
	  reportHelper.generateFinancialStatementReportJasperPrint(receiptsandPaymentsStatement,
	  getText("report.ie.heading"), heading.toString(), getPreviousYearToDate(),
	  getCurrentYearToDate(), false); inputStream =
	  reportHelper.exportXls(inputStream, jasper); return INCOME_EXPENSE_XLS; }
	  
	  @ReadOnly
	  
	  @Action(value = "/report/cashFlowReport-generateCashFlowSchedulePdf") public
	  String generateCashFlowSchedulePdf() throws JRException, IOException{
	  populateDataSourceForSchedule(); final String heading =
	  ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? ""
	  :cityService.getCityGrade()) + "\\n" + scheduleheading.toString(); final
	  String subtitle = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate())
	  + "                                             "; final JasperPrint jasper =
	  reportHelper.generateCashFlowReportJasperPrint(receiptsandPaymentsStatement, heading,
	  getPreviousYearToDate(), getCurrentYearToDate(), subtitle, false);
	  inputStream = reportHelper.exportPdf(inputStream, jasper); return
	  INCOME_EXPENSE_PDF; }
	  
	  @ReadOnly
	  
	  @Action(value = "/report/cashFlowReport-generateCashFlowScheduleXls") public
	  String generateCashFlowScheduleXls() throws JRException, IOException {
	  populateDataSourceForSchedule(); final String heading =
	  ReportUtil.getCityName() +" "+(cityService.getCityGrade()==null ? ""
	  :cityService.getCityGrade()) + "\\n" + scheduleheading.toString(); // Blank
	  space for space didvidion between left and right corner final String subtitle
	  = "Report Run Date-" + FORMATDDMMYYYY.format(getTodayDate()) +
	  "					  						 "; final JasperPrint jasper =
	  reportHelper.generateCashFlowReportJasperPrint(receiptsandPaymentsStatement, heading,
	  getPreviousYearToDate(), getCurrentYearToDate(), subtitle, false);
	  inputStream = reportHelper.exportXls(inputStream, jasper); return
	  INCOME_EXPENSE_XLS; }
	  
	  public String getCurrentYearToDate() { return
	  receiptsPaymentsService.getFormattedDate(receiptsPaymentsService.getToDate(receiptsandPaymentsStatement)
	  ); }
	  
	  public String getPreviousYearToDate() { return
	  receiptsPaymentsService.getFormattedDate(receiptsPaymentsService.getPreviousYearFor(
	  receiptsPaymentsService .getToDate(receiptsandPaymentsStatement))); }
	  
	  public String getCurrentYearFromDate() { return
	  cashFlowService.getFormattedDate(cashFlowService.getFromDate(
	  receiptsandPaymentsStatement)); }
	  
	  public String getPreviousYearFromDate() { return
	  cashFlowService.getFormattedDate(cashFlowService.getPreviousYearFor(
	  cashFlowService .getFromDate(receiptsandPaymentsStatement))); }
	 */
	  
	public Date getTodayDate() {
		return todayDate;
	}

	public void setTodayDate(final Date todayDate) {
		this.todayDate = todayDate;
	}

	public String getMajorCode() {
		return majorCode;
	}

	public void setMajorCode(final String majorCode) {
		this.majorCode = majorCode;
	}

	public String getMinorCode() {
		return minorCode;
	}

	public void setMinorCode(final String minorCode) {
		this.minorCode = minorCode;
	}

	public String getScheduleNo() {
		return scheduleNo;
	}

	public void setScheduleNo(final String scheduleNo) {
		this.scheduleNo = scheduleNo;
	}

	public List<CChartOfAccounts> getListChartOfAccounts() {
		return listChartOfAccounts;
	}

	public void setListChartOfAccounts(final List<CChartOfAccounts> listChartOfAccounts) {
		this.listChartOfAccounts = listChartOfAccounts;
	}

	public String getFinancialYearId() {
		return financialYearId;
	}

	public void setFinancialYearId(final String financialYearId) {
		this.financialYearId = financialYearId;
	}

	public Integer getFundId() {
		return fundId;
	}

	public void setFundId(final Integer fundId) {
		this.fundId = fundId;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(final String period) {
		this.period = period;
	}

	public String getAsOnDateRange() {
		return asOnDateRange;
	}

	public void setAsOnDateRange(final String asOnDateRange) {
		this.asOnDateRange = asOnDateRange;
	}

	public StringBuffer getScheduleheading() {
		return scheduleheading;
	}

	public void setScheduleheading(final StringBuffer scheduleheading) {
		this.scheduleheading = scheduleheading;
	}

	public StringBuffer getStatementheading() {
		return statementheading;
	}

	public void setStatementheading(final StringBuffer statementheading) {
		this.statementheading = statementheading;
	}

	public boolean isDetailReport() {
		return detailReport;
	}

	public void setDetailReport(final boolean detailReport) {
		this.detailReport = detailReport;
	}


	
}