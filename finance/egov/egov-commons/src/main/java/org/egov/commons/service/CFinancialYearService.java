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

package org.egov.commons.service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFiscalPeriod;
import org.egov.commons.contracts.CFinanancialYearSearchRequest;
import org.egov.commons.repository.CFinancialYearRepository;
import org.egov.infra.utils.DateUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

@Service
@Transactional(readOnly = true)
public class CFinancialYearService {

	private final CFinancialYearRepository cFinancialYearRepository;

	@Autowired
	public CFinancialYearService(final CFinancialYearRepository cFinancialYearRepository) {
		this.cFinancialYearRepository = cFinancialYearRepository;
	}

	@Transactional
	public CFinancialYear create(final CFinancialYear cFinancialYear) {
		return cFinancialYearRepository.save(cFinancialYear);
	}

	@Transactional
	public CFinancialYear update(final CFinancialYear cFinancialYear) {
		return cFinancialYearRepository.save(cFinancialYear);
	}

	public List<CFinancialYear> findAll() {
		return cFinancialYearRepository.findAll(new Sort(Sort.Direction.ASC, "finYearRange"));
	}

	public List<CFinancialYear> getAllFinancialYears() {
		return cFinancialYearRepository.getAllFinancialYears();
	}

	public CFinancialYear findOne(final Long id) {
		return cFinancialYearRepository.findOne(id);
	}

	//start code modified by raju
		//old code 
		/*
		 * public List<CFinancialYear> search(final CFinanancialYearSearchRequest
		 * cFinanancialYearSearchRequest) { if
		 * (cFinanancialYearSearchRequest.getFinYearRange() != null) { return
		 * cFinancialYearRepository.findByFinancialYearRange(
		 * cFinanancialYearSearchRequest.getFinYearRange()); } else return findAll(); }
		 */
		
		// new code
		public List<CFinancialYear> search(final CFinanancialYearSearchRequest cFinanancialYearSearchRequest) {
			if (cFinanancialYearSearchRequest.getFinYearRange() != null && !cFinanancialYearSearchRequest.getFinYearRange().isEmpty())
			{
				return cFinancialYearRepository.findByFinancialYearRange(cFinanancialYearSearchRequest.getFinYearRange());
				}
			else if (cFinanancialYearSearchRequest.getStartingDate() != null && cFinanancialYearSearchRequest.getEndingDate() != null) {
				return  cFinancialYearRepository.getFinancialYearByDateRange(cFinanancialYearSearchRequest.getStartingDate(), cFinanancialYearSearchRequest.getEndingDate());
		       
			}   
			else
				return findAll();
		}

		// end code 

	public Date getNextFinancialYearStartingDate() {
		final List<CFinancialYear> cFinYear = cFinancialYearRepository.getFinYearLastDate();
		final Calendar cal = Calendar.getInstance();
		cal.setTime(cFinYear.get(0).getEndingDate());
		cal.add(Calendar.DATE, +1);
		return cal.getTime();
	}

	public CFiscalPeriod findByFiscalName(final String name) {
		return cFinancialYearRepository.findByFiscalName(name);
	}

	public CFinancialYear getFinancialYearByDate(Date date) {
		return cFinancialYearRepository.getFinancialYearByDate(date);
	}

	public List<CFinancialYear> getFinancialYearNotClosed() {
		return cFinancialYearRepository.findByIsClosedFalseOrderByFinYearRangeDesc();
	}

	public CFinancialYear getFinacialYearByYearRange(String finYearRange) {
		return cFinancialYearRepository.findByFinYearRange(finYearRange);
	}

	public List<CFinancialYear> getFinancialYears(List<Long> financialYearList) {
		return cFinancialYearRepository.findByIdIn(financialYearList);
	}

	public CFinancialYear getPreviousFinancialYearForDate(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, -1);
		return cFinancialYearRepository.getFinancialYearByDate(cal.getTime());
	}

	public CFinancialYear getCurrentFinancialYear() {
		return getFinancialYearByDate(new Date());
	}

	public CFinancialYear getLatestFinancialYear() {
		return getFinancialYearByDate(new DateTime().withMonthOfYear(4).withDayOfMonth(1).toDate());
	}

	public List<CFinancialYear> getFinancialYearNotClosedAndActive() {
		return cFinancialYearRepository.findByIsClosedFalseAndIsActiveForPostingTrueOrderByFinYearRangeDesc();
	}

	public void validateMandatoryFields(CFinancialYear financialYear, final BindingResult errors)
			throws ParseException {
		final Date nextStartingDate = getNextFinancialYearStartingDate();
		if (financialYear.getStartingDate().after(financialYear.getEndingDate())) {
			errors.reject("msg.startdate.enddate.greater",
					new String[] { DateUtils.getDefaultFormattedDate(financialYear.getStartingDate()) }, null);
		}
		if (financialYear.getStartingDate().equals(nextStartingDate)) {
			errors.reject("msg.enter.valid.startdate",
					new String[] { DateUtils.getDefaultFormattedDate(financialYear.getStartingDate()) }, null);
		}
		for (CFiscalPeriod fiscalperiod : financialYear.getcFiscalPeriod()) {
			if (fiscalperiod.getName() == null || StringUtils.isEmpty(fiscalperiod.getName()))
				errors.reject("msg.enter.fiscal.period.name",
						new String[] { DateUtils.getDefaultFormattedDate(financialYear.getStartingDate()) }, null);
			if (fiscalperiod.getStartingDate() == null)
				errors.reject("msg.enter.startdate",
						new String[] { DateUtils.getDefaultFormattedDate(fiscalperiod.getStartingDate()) }, null);
			if (fiscalperiod.getEndingDate() == null)
				errors.reject("msg.enter.endingdate",
						new String[] { DateUtils.getDefaultFormattedDate(fiscalperiod.getEndingDate()) }, null);
			if (fiscalperiod.getStartingDate() != null && fiscalperiod.getEndingDate() != null) {
				if (fiscalperiod.getStartingDate().after(fiscalperiod.getEndingDate()))
					errors.reject("msg.startdate.enddate.greater",
							new String[] { DateUtils.getDefaultFormattedDate(fiscalperiod.getStartingDate()) }, null);
				if (fiscalperiod.getStartingDate().equals(nextStartingDate)) {
					errors.reject("msg.enter.valid.startdate",
							new String[] { DateUtils.getDefaultFormattedDate(fiscalperiod.getStartingDate()) }, null);
				}
			}
		}
	}
}