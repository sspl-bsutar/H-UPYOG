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
package org.egov.egf.supplierbill.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.script.ScriptContext;

import org.apache.commons.lang.StringUtils;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFinancialYear;
import org.egov.commons.service.CFinancialYearService;
import org.egov.commons.service.ChartOfAccountDetailService;
import org.egov.commons.service.FundService;
import org.egov.dao.budget.BudgetDetailsHibernateDAO;
import org.egov.egf.autonumber.SupplierBillNumberGenerator;
import org.egov.egf.autonumber.WorksBillNumberGenerator;
import org.egov.egf.billsubtype.service.EgBillSubTypeService;
import org.egov.egf.dashboard.event.FinanceEventType;
import org.egov.egf.dashboard.event.listener.FinanceDashboardService;
import org.egov.egf.expensebill.repository.DocumentUploadRepository;
import org.egov.egf.supplierbill.repository.SupplierBillRepository;
import org.egov.egf.utils.FinancialUtils;
import org.egov.eis.entity.Assignment;
import org.egov.infra.admin.master.entity.AppConfig;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.AppConfigService;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.microservice.models.Designation;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.script.service.ScriptService;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.infra.utils.autonumber.AutonumberServiceBeanResolver;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.workflow.matrix.entity.WorkFlowMatrix;
import org.egov.infra.workflow.service.SimpleWorkflowService;
import org.egov.model.bills.DocumentUpload;
import org.egov.model.bills.EgBillPayeedetails;
import org.egov.model.bills.EgBilldetails;
import org.egov.model.bills.EgBillregister;
import org.egov.model.budget.BudgetGroup;
import org.egov.pims.commons.Position;
import org.egov.services.masters.SchemeService;
import org.egov.services.masters.SubSchemeService;
import org.egov.services.voucher.VoucherService;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.hibernate.Session;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author venki
 */

@Service
@Transactional(readOnly = true)
public class SupplierBillService {

	private static final Logger LOG = LoggerFactory.getLogger(SupplierBillService.class);

	@Autowired
	private final SupplierBillRepository supplierBillRepository;
	@Autowired
	private final ScriptService scriptExecutionService;

	@Autowired
	protected AppConfigValueService appConfigValuesService;

	@Autowired
	private DocumentUploadRepository documentUploadRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private EgBillSubTypeService egBillSubTypeService;

	@Autowired
	private SchemeService schemeService;

	@Autowired
	private SubSchemeService subSchemeService;

	@Autowired
	private FinancialUtils financialUtils;

	@Autowired
	private AutonumberServiceBeanResolver beanResolver;

	@Autowired
	private SecurityUtils securityUtils;

	@Autowired
	@Qualifier(value = "voucherService")
	private VoucherService voucherService;

	@Autowired
	@Qualifier("workflowService")
	private SimpleWorkflowService<EgBillregister> egBillregisterRegisterWorkflowService;

	@Autowired
	private FundService fundService;

	@Autowired
	private ChartOfAccountDetailService chartOfAccountDetailService;

	@Autowired
	private AppConfigService appConfigService;

	@Autowired
	private BudgetDetailsHibernateDAO budgetDetailsHibernateDAO;

	@Autowired
	private CFinancialYearService cFinancialYearService;

	@Autowired
	private MicroserviceUtils microServiceUtil;

	@Autowired
	FinanceDashboardService finDashboardService;

	@Autowired
	public SupplierBillService(final SupplierBillRepository supplierBillRepository,
			final ScriptService scriptExecutionService) {
		this.supplierBillRepository = supplierBillRepository;
		this.scriptExecutionService = scriptExecutionService;
	}

	public Session getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}

	public EgBillregister getById(final Long id) {
		return supplierBillRepository.findOne(id);
	}

	public EgBillregister getByBillnumber(final String billNumber) {
		return supplierBillRepository.findByBillnumber(billNumber);
	}

	@Transactional
	public EgBillregister create(final EgBillregister egBillregister) {
		return supplierBillRepository.save(egBillregister);
	}

	@Transactional
	public EgBillregister create(final EgBillregister egBillregister, final Long approvalPosition,
			final String approvalComent, final String additionalRule, final String workFlowAction,
			final String approvalDesignation) {
		if (StringUtils.isBlank(egBillregister.getBilltype()))
			egBillregister.setBilltype(FinancialConstants.BILLTYPE_FINAL_BILL);
		egBillregister.getEgBillregistermis().setEgBillregister(egBillregister);
		egBillregister.getEgBillregistermis().setLastupdatedtime(new Date());

		if (egBillregister.getEgBillregistermis().getFund() != null
				&& egBillregister.getEgBillregistermis().getFund().getId() != null)
			egBillregister.getEgBillregistermis()
					.setFund(fundService.findOne(egBillregister.getEgBillregistermis().getFund().getId()));
		if (egBillregister.getEgBillregistermis().getEgBillSubType() != null
				&& egBillregister.getEgBillregistermis().getEgBillSubType().getId() != null)
			egBillregister.getEgBillregistermis().setEgBillSubType(
					egBillSubTypeService.getById(egBillregister.getEgBillregistermis().getEgBillSubType().getId()));
		if (egBillregister.getEgBillregistermis().getSchemeId() != null)
			egBillregister.getEgBillregistermis().setScheme(
					schemeService.findById(egBillregister.getEgBillregistermis().getSchemeId().intValue(), false));
		else
			egBillregister.getEgBillregistermis().setScheme(null);
		if (egBillregister.getEgBillregistermis().getSubSchemeId() != null)
			egBillregister.getEgBillregistermis().setSubScheme(subSchemeService
					.findById(egBillregister.getEgBillregistermis().getSubSchemeId().intValue(), false));
		else
			egBillregister.getEgBillregistermis().setSubScheme(null);

		if (isBillNumberGenerationAuto())
			egBillregister.setBillnumber(getNextBillNumber(egBillregister));

		// commented as budget check was disabled
		try {
			checkBudgetAndGenerateBANumber(egBillregister);
		} catch (final ValidationException e) {
			throw new ValidationException(e.getErrors());
		}

		final EgBillregister savedEgBillregister = supplierBillRepository.save(egBillregister);

		if (workFlowAction.equals(FinancialConstants.CREATEANDAPPROVE)) {
			if (FinancialConstants.STANDARD_EXPENDITURETYPE_PURCHASE.equals(egBillregister.getExpendituretype()))
				savedEgBillregister.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.SBILL,
						FinancialConstants.SUPPLIERBILL_APPROVED_STATUS));
		} else {
			savedEgBillregister.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.SBILL,
					FinancialConstants.SUPPLIERBILL_CREATED_STATUS));
			createSupplierBillRegisterWorkflowTransition(savedEgBillregister, approvalPosition, approvalComent,
					additionalRule, workFlowAction, approvalDesignation);
		}
		List<DocumentUpload> files = egBillregister.getDocumentDetail() == null ? null
				: egBillregister.getDocumentDetail();
		final List<DocumentUpload> documentDetails;
		documentDetails = financialUtils.getDocumentDetails(files, savedEgBillregister,
				FinancialConstants.FILESTORE_MODULEOBJECT);
		if (!documentDetails.isEmpty()) {
			savedEgBillregister.setDocumentDetail(documentDetails);
			persistDocuments(documentDetails);
		}

		// TODO: add the code to handle new screen for view bills of all type
		if (savedEgBillregister.getEgBillregistermis().getSourcePath() == null
				|| StringUtils.isBlank(savedEgBillregister.getEgBillregistermis().getSourcePath()))
			savedEgBillregister.getEgBillregistermis()
					.setSourcePath("/services/EGF/supplierbill/view/" + savedEgBillregister.getId().toString());
		EgBillregister egbillReg = supplierBillRepository.save(savedEgBillregister);
		finDashboardService.publishEvent(FinanceEventType.billCreateOrUpdate, egbillReg);
		return egbillReg;
	}

	public void checkBudgetAndGenerateBANumber(final EgBillregister egBillregister) {
		final ScriptContext scriptContext = ScriptService.createContext("voucherService", voucherService, "bill",
				egBillregister);
		scriptExecutionService.executeScript("egf.bill.budgetcheck", scriptContext);
	}

	@Transactional
	public EgBillregister update(final EgBillregister egBillregister, final Long approvalPosition,
			final String approvalComent, final String additionalRule, final String workFlowAction, final String mode,
			final String approverDesignation) {
		EgBillregister updatedegBillregister = null;
		if ("edit".equals(mode)) {
			egBillregister.setPassedamount(egBillregister.getBillamount());
			egBillregister.getEgBillregistermis().setLastupdatedtime(new Date());

			if (egBillregister.getEgBillregistermis().getFund() != null
					&& egBillregister.getEgBillregistermis().getFund().getId() != null)
				egBillregister.getEgBillregistermis()
						.setFund(fundService.findOne(egBillregister.getEgBillregistermis().getFund().getId()));
			if (egBillregister.getEgBillregistermis().getSchemeId() != null)
				egBillregister.getEgBillregistermis().setScheme(
						schemeService.findById(egBillregister.getEgBillregistermis().getSchemeId().intValue(), false));
			else
				egBillregister.getEgBillregistermis().setScheme(null);
			if (egBillregister.getEgBillregistermis().getSubSchemeId() != null)
				egBillregister.getEgBillregistermis().setSubScheme(subSchemeService
						.findById(egBillregister.getEgBillregistermis().getSubSchemeId().intValue(), false));
			else
				egBillregister.getEgBillregistermis().setSubScheme(null);

			updatedegBillregister = supplierBillRepository.save(egBillregister);

			egBillregister.getEgBillregistermis().setBudgetaryAppnumber(null);

			// commented as budget check was disabled
			// try {
			// checkBudgetAndGenerateBANumber(egBillregister);
			// } catch (final ValidationException e) {
			// throw new ValidationException(e.getErrors());
			// }

		}
		if (updatedegBillregister != null) {
			if (workFlowAction.equals(FinancialConstants.CREATEANDAPPROVE))
				updatedegBillregister.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.SBILL,
						FinancialConstants.SUPPLIERBILL_APPROVED_STATUS));
			else {
				supplierBillRegisterStatusChange(updatedegBillregister, workFlowAction);
				createSupplierBillRegisterWorkflowTransition(updatedegBillregister, approvalPosition, approvalComent,
						additionalRule, workFlowAction, approverDesignation);
			}
			updatedegBillregister = supplierBillRepository.save(updatedegBillregister);
		} else {
			if (workFlowAction.equals(FinancialConstants.CREATEANDAPPROVE))
				egBillregister.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.SBILL,
						FinancialConstants.SUPPLIERBILL_APPROVED_STATUS));
			else {
				supplierBillRegisterStatusChange(egBillregister, workFlowAction);
				createSupplierBillRegisterWorkflowTransition(egBillregister, approvalPosition, approvalComent,
						additionalRule, workFlowAction, "");
			}
			updatedegBillregister = supplierBillRepository.save(egBillregister);
		}
		finDashboardService.publishEvent(FinanceEventType.billCreateOrUpdate, updatedegBillregister);

		return updatedegBillregister;
	}

	public void supplierBillRegisterStatusChange(final EgBillregister egBillregister, final String workFlowAction) {
		if (null != egBillregister && null != egBillregister.getStatus()
				&& null != egBillregister.getStatus().getCode())
			if (FinancialConstants.SUPPLIERBILL_CREATED_STATUS.equals(egBillregister.getStatus().getCode())
					&& egBillregister.getState() != null
					&& workFlowAction.equalsIgnoreCase(FinancialConstants.BUTTONAPPROVE))
				egBillregister.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.SBILL,
						FinancialConstants.SUPPLIERBILL_APPROVED_STATUS));
			else if (workFlowAction.equals(FinancialConstants.BUTTONREJECT))
				egBillregister.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.SBILL,
						FinancialConstants.SUPPLIERBILL_REJECTED_STATUS));
			else if (FinancialConstants.SUPPLIERBILL_REJECTED_STATUS.equals(egBillregister.getStatus().getCode())
					&& workFlowAction.equals(FinancialConstants.BUTTONCANCEL))
				egBillregister.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.SBILL,
						FinancialConstants.SUPPLIERBILL_CANCELLED_STATUS));
			else if (FinancialConstants.SUPPLIERBILL_REJECTED_STATUS.equals(egBillregister.getStatus().getCode())
					&& workFlowAction.equals(FinancialConstants.BUTTONFORWARD))
				egBillregister.setStatus(financialUtils.getStatusByModuleAndCode(FinancialConstants.SBILL,
						FinancialConstants.SUPPLIERBILL_CREATED_STATUS));

	}

	@Transactional(readOnly = true)
	public boolean isBillNumberGenerationAuto() {
		final List<AppConfigValues> configValuesByModuleAndKey = appConfigValuesService.getConfigValuesByModuleAndKey(
				FinancialConstants.MODULE_NAME_APPCONFIG, FinancialConstants.KEY_BILLNUMBER_APPCONFIG);
		if (!configValuesByModuleAndKey.isEmpty())
			return "Y".equals(configValuesByModuleAndKey.get(0).getValue());
		else
			return false;
	}

	private String getNextBillNumber(final EgBillregister bill) {
		if (FinancialConstants.STANDARD_EXPENDITURETYPE_WORKS.equals(bill.getExpendituretype())) {
			final WorksBillNumberGenerator b = beanResolver.getAutoNumberServiceFor(WorksBillNumberGenerator.class);
			return b.getNextNumber(bill);
		} else {
			final SupplierBillNumberGenerator b = beanResolver
					.getAutoNumberServiceFor(SupplierBillNumberGenerator.class);
			return b.getNextNumber(bill);
		}
	}

	public void validateSubledgeDetails(EgBillregister egBillregister) {
		final List<EgBillPayeedetails> payeeDetails = new ArrayList<>();
		for (final EgBillPayeedetails payeeDetail : egBillregister.getBillPayeedetails()) {
			CChartOfAccountDetail coaDetail = chartOfAccountDetailService.getByGlcodeIdAndDetailTypeId(
					payeeDetail.getEgBilldetailsId().getGlcodeid().longValue(),
					payeeDetail.getAccountDetailTypeId().intValue());
			if (coaDetail != null)
				payeeDetails.add(payeeDetail);
		}
		egBillregister.getBillPayeedetails().clear();
		egBillregister.setBillPayeedetails(payeeDetails);
	}

	public void createSupplierBillRegisterWorkflowTransition(final EgBillregister egBillregister,
			final Long approvalPosition, final String approvalComent, final String additionalRule,
			final String workFlowAction, final String approvalDesignation) {
		if (LOG.isDebugEnabled())
			LOG.debug(" Create WorkFlow Transition Started  ...");
		final User user = securityUtils.getCurrentUser();
		final DateTime currentDate = new DateTime();
		Assignment wfInitiator = null;
		Map<String, String> finalDesignationNames = new HashMap<>();
		final String currState = "";
		String stateValue = "";
		if (null != egBillregister.getId())
			// wfInitiator =
			// assignmentService.getPrimaryAssignmentForUser(egBillregister.getCreatedBy());
			wfInitiator = this.getCurrentUserAssignmet(egBillregister.getCreatedBy());
		if (FinancialConstants.BUTTONREJECT.equalsIgnoreCase(workFlowAction)) {
			stateValue = FinancialConstants.WORKFLOW_STATE_REJECTED;
			egBillregister.transition().progressWithStateCopy()
					.withSenderName(user.getUsername() + "::" + user.getName()).withComments(approvalComent)
					.withStateValue(stateValue).withDateInfo(currentDate.toDate()).withOwner(wfInitiator.getPosition())
					.withNextAction("").withNatureOfTask(FinancialConstants.WORKFLOWTYPE_SBILL_DISPLAYNAME);
		} else {
			// if (null != approvalPosition && approvalPosition != -1 &&
			// !approvalPosition.equals(Long.valueOf(0)))
			// wfInitiator =
			// assignmentService.getAssignmentsForPosition(approvalPosition).get(0);
			WorkFlowMatrix wfmatrix;
			Designation designation = this.getDesignationDetails(approvalDesignation);
			Position owenrPos = new Position();
			owenrPos.setId(approvalPosition);

			wfmatrix = egBillregisterRegisterWorkflowService.getWfMatrix(egBillregister.getStateType(), null, null,
					additionalRule, FinancialConstants.WF_STATE_FINAL_APPROVAL_PENDING, null);

			if (wfmatrix != null && wfmatrix.getCurrentDesignation() != null) {
				final List<String> finalDesignationName = Arrays.asList(wfmatrix.getCurrentDesignation().split(","));
				for (final String desgName : finalDesignationName)
					if (desgName != null && !"".equals(desgName.trim()))
						finalDesignationNames.put(desgName.toUpperCase(), desgName.toUpperCase());
			}

			if (null == egBillregister.getState()) {

				if (designation != null && finalDesignationNames.get(designation.getName().toUpperCase()) != null)
					stateValue = FinancialConstants.WF_STATE_FINAL_APPROVAL_PENDING;

				wfmatrix = egBillregisterRegisterWorkflowService.getWfMatrix(egBillregister.getStateType(), null, null,
						additionalRule, currState, null);

				if (stateValue.isEmpty())
					stateValue = wfmatrix.getNextState();

				egBillregister.transition().start().withSenderName(user.getUsername() + "::" + user.getName())
						.withComments(approvalComent).withStateValue(stateValue).withDateInfo(new Date())
						.withOwner(owenrPos).withNextAction(wfmatrix.getNextAction())
						.withNatureOfTask(FinancialConstants.WORKFLOWTYPE_SBILL_DISPLAYNAME).withCreatedBy(user.getId())
						.withtLastModifiedBy(user.getId());
			} else if (FinancialConstants.BUTTONCANCEL.equalsIgnoreCase(workFlowAction)) {
				stateValue = FinancialConstants.WORKFLOW_STATE_CANCELLED;
				egBillregister.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
						.withComments(approvalComent).withStateValue(stateValue).withDateInfo(currentDate.toDate())
						.withNextAction("").withNatureOfTask(FinancialConstants.WORKFLOWTYPE_SBILL_DISPLAYNAME);
			} else if (FinancialConstants.BUTTONAPPROVE.equalsIgnoreCase(workFlowAction)) {
				wfmatrix = egBillregisterRegisterWorkflowService.getWfMatrix(egBillregister.getStateType(), null, null,
						additionalRule, egBillregister.getCurrentState().getValue(), null);

				if (stateValue.isEmpty())
					stateValue = wfmatrix.getNextState();

				egBillregister.transition().end().withSenderName(user.getUsername() + "::" + user.getName())
						.withComments(approvalComent).withStateValue(stateValue).withDateInfo(new Date())
						.withNextAction(wfmatrix.getNextAction())
						.withNatureOfTask(FinancialConstants.WORKFLOWTYPE_SBILL_DISPLAYNAME);
			} else {
				if (designation != null && finalDesignationNames.get(designation.getName().toUpperCase()) != null)
					stateValue = FinancialConstants.WF_STATE_FINAL_APPROVAL_PENDING;

				wfmatrix = egBillregisterRegisterWorkflowService.getWfMatrix(egBillregister.getStateType(), null, null,
						additionalRule, egBillregister.getCurrentState().getValue(), null);

				if (stateValue.isEmpty())
					stateValue = wfmatrix.getNextState();

				egBillregister.transition().progressWithStateCopy()
						.withSenderName(user.getUsername() + "::" + user.getName()).withComments(approvalComent)
						.withStateValue(stateValue).withDateInfo(new Date()).withOwner(owenrPos)
						.withNextAction(wfmatrix.getNextAction())
						.withNatureOfTask(FinancialConstants.WORKFLOWTYPE_SBILL_DISPLAYNAME);
			}
		}
		if (LOG.isDebugEnabled())
			LOG.debug(" WorkFlow Transition Completed  ...");
	}

	public Long getApprovalPositionByMatrixDesignation(final EgBillregister egBillregister, final String additionalRule,
			final String mode, final String workFlowAction) {
		Long approvalPosition = null;
		final WorkFlowMatrix wfmatrix = egBillregisterRegisterWorkflowService.getWfMatrix(egBillregister.getStateType(),
				null, null, additionalRule, egBillregister.getCurrentState().getValue(), null);
		if (egBillregister.getState() != null && !egBillregister.getState().getHistory().isEmpty()
				&& egBillregister.getState().getOwnerPosition() != null)
			approvalPosition = egBillregister.getState().getOwnerPosition();
		else if (wfmatrix != null)
			approvalPosition = financialUtils.getApproverPosition(wfmatrix.getNextDesignation(),
					egBillregister.getState(), egBillregister.getCreatedBy());
		if (workFlowAction.equals(FinancialConstants.BUTTONCANCEL))
			approvalPosition = null;

		return approvalPosition;
	}

	public boolean isBillNumUnique(final String billnumber) {
		final EgBillregister bill = getByBillnumber(billnumber);
		return bill == null;
	}

	private Map<String, Object> getBudgetDetails(EgBillregister egBillregister, EgBilldetails billDetail,
			Map<String, Object> budgetDataMap, Map<String, Object> paramMap) {

		Map<String, Object> budgetApprDetailsMap = new HashMap<>();
		BigDecimal currentBillAmount;
		BigDecimal soFarAppropriated;
		BigDecimal actualAmount;

		if (egBillregister.getEgBillregistermis().getVoucherHeader() != null) {
			budgetDataMap.put(Constants.ASONDATE,
					egBillregister.getEgBillregistermis().getVoucherHeader().getVoucherDate());
		} else {
			budgetDataMap.put(Constants.ASONDATE, egBillregister.getBilldate());
		}
		CFinancialYear financialYearById = egBillregister.getEgBillregistermis().getFinancialyear();

		budgetDataMap.put(Constants.FUNCTIONID, billDetail.getFunctionid().longValue());
		budgetDataMap.put("fromdate", financialYearById.getStartingDate());
		budgetDataMap.put("glcode", billDetail.getChartOfAccounts().getGlcode());
		budgetDataMap.put("glcodeid", billDetail.getChartOfAccounts().getId());
		List<BudgetGroup> budgetHeadByGlcode = budgetDetailsHibernateDAO
				.getBudgetHeadByGlcode(billDetail.getChartOfAccounts());
		budgetDataMap.put("budgetheadid", budgetHeadByGlcode);
		budgetDataMap.put("isReport", "true");
		BigDecimal budgetedAmtForYear = budgetDetailsHibernateDAO.getBudgetedAmtForYear(budgetDataMap);
		paramMap.put("budgetedAmtForYear", budgetedAmtForYear);
		if (LOG.isDebugEnabled())
			LOG.debug("budgetedAmtForYear .......... " + budgetedAmtForYear);

		budgetDataMap.put("budgetApprNumber", egBillregister.getEgBillregistermis().getBudgetaryAppnumber());

		if (LOG.isDebugEnabled())
			LOG.debug("Getting actuals .............................");
		BigDecimal actualAmtFromVoucher = budgetDetailsHibernateDAO
				.getActualBudgetUtilizedForBudgetaryCheck(budgetDataMap);
		if (LOG.isDebugEnabled())
			LOG.debug("actualAmtFromVoucher .............................. " + actualAmtFromVoucher);
		budgetDataMap.put(Constants.ASONDATE, egBillregister.getBilldate());
		BigDecimal actualAmtFromBill = budgetDetailsHibernateDAO.getBillAmountForBudgetCheck(budgetDataMap);
		if (LOG.isDebugEnabled())
			LOG.debug("actualAmtFromBill .............................. " + actualAmtFromBill);

		actualAmount = actualAmtFromVoucher != null ? actualAmtFromVoucher : BigDecimal.ZERO;
		actualAmount = actualAmtFromBill != null ? actualAmount.add(actualAmtFromBill) : actualAmount;
		if (LOG.isDebugEnabled())
			LOG.debug("actualAmount ...actualAmtFromVoucher+actualAmtFromBill........ " + actualAmount);

		if (billDetail.getDebitamount() != null && billDetail.getDebitamount().compareTo(BigDecimal.ZERO) != 0) {
			actualAmount = actualAmount.subtract(billDetail.getDebitamount());
			currentBillAmount = billDetail.getDebitamount();

		} else {
			actualAmount = actualAmount.subtract(billDetail.getCreditamount());
			currentBillAmount = billDetail.getCreditamount();
		}
		if (LOG.isDebugEnabled())
			LOG.debug("actualAmount ...actualAmount-billamount........ " + actualAmount);
		BigDecimal balance = budgetedAmtForYear;

		balance = balance.subtract(actualAmount);
		soFarAppropriated = actualAmount;
		if (LOG.isDebugEnabled())
			LOG.debug("soFarAppropriated ...actualAmount==soFarAppropriated........ " + soFarAppropriated);
		if (LOG.isDebugEnabled())
			LOG.debug("balance ...budgetedAmtForYear-actualAmount........ " + balance);
		BigDecimal cumilativeIncludingCurrentBill = soFarAppropriated.add(currentBillAmount);
		BigDecimal currentBalanceAvailable = balance.subtract(currentBillAmount);
		budgetApprDetailsMap.put("allocatedBudgetForYear", budgetedAmtForYear);
		budgetApprDetailsMap.put("actualAmount", soFarAppropriated);
		budgetApprDetailsMap.put("balance", balance);
		budgetApprDetailsMap.put("supplierIncurredIncludingCurrentBill", cumilativeIncludingCurrentBill);
		budgetApprDetailsMap.put("currentBalanceAvailable", currentBalanceAvailable);
		budgetApprDetailsMap.put("accountCode", billDetail.getChartOfAccounts().getGlcode());

		return budgetApprDetailsMap;

	}

	public List<Map<String, Object>> getBudgetDetailsForBill(EgBillregister billregister) {

		Map<String, Object> budgetDataMap = new HashMap<>();
		Map<String, Object> paramMap = new HashMap<>();
		List<Map<String, Object>> budgetDetailList = new ArrayList<>();
		Set<EgBilldetails> egBillDetailes = billregister.getEgBilldetailes();
		boolean budgetCheck = false;
		CFinancialYear financialYear = cFinancialYearService.getFinancialYearByDate(billregister.getBilldate());
		billregister.getEgBillregistermis().setFinancialyear(financialYear);

		AppConfig appConfig = appConfigService
				.getAppConfigByModuleNameAndKeyName(FinancialConstants.MODULE_NAME_APPCONFIG, "budgetCheckRequired");
		if (appConfig != null && !appConfig.getConfValues().isEmpty()) {
			if ("Y".equalsIgnoreCase(appConfig.getConfValues().get(0).getValue())) {
				budgetCheck = true;
			}
			getRequiredDataForBudget(billregister, budgetDataMap);
		}

		for (EgBilldetails detail : egBillDetailes) {
			if (detail.getDebitamount() != null && detail.getDebitamount().compareTo(BigDecimal.ZERO) != 0) {
				Map<String, Object> budgetApprDetails;

				CChartOfAccounts coa = detail.getChartOfAccounts();
				if (budgetCheck && coa.getBudgetCheckReq() != null && coa.getBudgetCheckReq()) {
					budgetApprDetails = getBudgetDetails(billregister, detail, budgetDataMap, paramMap);
					budgetDetailList.add(budgetApprDetails);
				}

			}
		}
		return budgetDetailList;

	}

	private void getRequiredDataForBudget(EgBillregister egBillregister, Map<String, Object> budgetDataMap) {

		budgetDataMap.put("financialyearid", egBillregister.getEgBillregistermis().getFinancialyear().getId());
		budgetDataMap.put(Constants.DEPTID, egBillregister.getEgBillregistermis().getDepartmentcode());
		if (egBillregister.getEgBillregistermis().getFunctionaryid() != null)
			budgetDataMap.put(Constants.FUNCTIONARYID,
					egBillregister.getEgBillregistermis().getFunctionaryid().getId());
		if (egBillregister.getEgBillregistermis().getScheme() != null)
			budgetDataMap.put(Constants.SCHEMEID, egBillregister.getEgBillregistermis().getScheme().getId());
		if (egBillregister.getEgBillregistermis().getSubScheme() != null)
			budgetDataMap.put(Constants.SUBSCHEMEID, egBillregister.getEgBillregistermis().getSubScheme().getId());
		budgetDataMap.put(Constants.FUNDID, egBillregister.getEgBillregistermis().getFund().getId());
		budgetDataMap.put(Constants.BOUNDARYID, egBillregister.getDivision());

	}

	public void persistDocuments(final List<DocumentUpload> documentDetailsList) {
		if (documentDetailsList != null && !documentDetailsList.isEmpty())
			for (final DocumentUpload doc : documentDetailsList)
				documentUploadRepository.save(doc);
	}

	public List<DocumentUpload> findByObjectIdAndObjectType(final Long objectId, final String objectType) {
		return documentUploadRepository.findByObjectIdAndObjectType(objectId, objectType);
	}

	private Assignment getCurrentUserAssignmet(Long userId) {
		// Long userId = ApplicationThreadLocals.getUserId();
		List<EmployeeInfo> emplist = microServiceUtil.getEmployee(userId, null, null, null);
		Assignment assignment = new Assignment();
		if (null != emplist && emplist.size() > 0 && emplist.get(0).getAssignments().size() > 0) {
			Position position = new Position();
			position.setId(emplist.get(0).getAssignments().get(0).getPosition());
			assignment.setPosition(position);

			org.egov.pims.commons.Designation designation = new org.egov.pims.commons.Designation();
			Designation _desg = this.getDesignationDetails(emplist.get(0).getAssignments().get(0).getDesignation());
			designation.setCode(_desg.getCode());
			designation.setName(_desg.getName());
			assignment.setDesignation(designation);

			org.egov.infra.admin.master.entity.Department department = new org.egov.infra.admin.master.entity.Department();
			Department _dept = this.getDepartmentDetails(emplist.get(0).getAssignments().get(0).getDepartment());
			department.setCode(_dept.getCode());
			department.setName(_dept.getName());

			return assignment;
		}
		return null;
	}

	private Department getDepartmentDetails(String deptCode) {

		Department dept = microServiceUtil.getDepartmentByCode(deptCode);
		return dept;

	}

	private Designation getDesignationDetails(String desgnCode) {
		List<Designation> desgnList = microServiceUtil.getDesignation(desgnCode);
		return !desgnList.isEmpty() ? desgnList.get(0) : null;
	}

	public synchronized String generateSupBillNumber() {
		// Get the current year
		int year = LocalDate.now().getYear() % 100; // Get the last two digits of the year

		// Get the current financial year
		String financialYear = getFinancialYear();

		Optional<String> latestBillNumber = getLastSupplierBillNumber();

		if (latestBillNumber.isPresent()) {
			String[] lastNumber = { "" }; // Array to hold the last number

			latestBillNumber.ifPresent(input -> {
				String pattern = "(\\d+)$";
				Pattern r = Pattern.compile(pattern);
				Matcher m = r.matcher(input);

				if (m.find()) {
					lastNumber[0] = m.group(1);
				} else {
					System.out.println("No match found.");
				}
			});

			// Construct the bill number in the format: sup/financial year/number
			String billNumber = "Sup/001/" + financialYear + "/" + "0000" + (Integer.parseInt(lastNumber[0]) + 1);

			return billNumber;

		} else {
			return "Sup/001/" + financialYear + "/" + "00001";
		}
	}

	// added by Raju
	private static String getFinancialYear() {
		LocalDate today = LocalDate.now();
		int year = today.getYear();
		int month = today.getMonthValue();

		String financialYear;
		if (month >= 4) {
			// Financial year starts from April
			financialYear = String.format("%02d", year % 100) + "-" + String.format("%02d", (year + 1) % 100);
		} else {
			financialYear = String.format("%02d", (year - 1) % 100) + "-" + String.format("%02d", year % 100);
		}
		return financialYear;
	}

	// added by Raju
	public Optional<String> getLastSupplierBillNumber() {
		return supplierBillRepository.findMaxBillNumberStartingWithSup();
	}

}
