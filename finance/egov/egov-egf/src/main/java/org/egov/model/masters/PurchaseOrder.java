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
package org.egov.model.masters;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;

import org.egov.commons.EgwStatus;
import org.egov.commons.Fund;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.commons.utils.EntityType;
import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.persistence.validator.annotation.OptionalPattern;
import org.egov.infra.persistence.validator.annotation.Unique;
import org.egov.model.bills.EgBillPurchaseItemsDetails;
import org.egov.utils.FinancialConstants;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.SafeHtml;

@Entity
@Table(name = "EGF_PURCHASEORDER")
@Unique(fields = { "orderNumber" }, id = "id", tableName = "EGF_PURCHASEORDER", enableDfltMsg = true)
@SequenceGenerator(name = PurchaseOrder.SEQ_EGF_PURCHASEORDER, sequenceName = PurchaseOrder.SEQ_EGF_PURCHASEORDER, allocationSize = 1)
public class PurchaseOrder extends AbstractAuditable implements EntityType {

	private static final long serialVersionUID = 2642863347862704111L;

	public static final String SEQ_EGF_PURCHASEORDER = "SEQ_EGF_PURCHASEORDER";

	@Id
	@GeneratedValue(generator = SEQ_EGF_PURCHASEORDER, strategy = GenerationType.SEQUENCE)
	private Long id;

	@SafeHtml
	@Length(max = 100, message = "Maximum of 100 Characters allowed for Order Number")
	@OptionalPattern(regex = FinancialConstants.alphaNumericwithspecialcharForContraWOAndSupplierName, message = "Special Characters are not allowed in Order Number")
	@Column(updatable = false)
	private String orderNumber;

	@SafeHtml
	@Length(max = 100, message = "Maximum of 100 Characters allowed for Name")
	@OptionalPattern(regex = FinancialConstants.alphaNumericwithspecialcharForContraWOAndSupplierName, message = "Special Characters are not allowed in Name")
	private String name;

	private Date orderDate;

	@ManyToOne
	@JoinColumn(name = "supplier")
	private Supplier supplier;

	@Min(1)
	private BigDecimal orderValue;

	@Min(1)
	private BigDecimal advancePayable;

	@SafeHtml
	private String description;

	@ManyToOne
	@JoinColumn(name = "fund")
	private Fund fund;

	@SafeHtml
	private String department;

	@ManyToOne
	@JoinColumn(name = "scheme")
	private Scheme scheme;

	@ManyToOne
	@JoinColumn(name = "subScheme")
	private SubScheme subScheme;

	@SafeHtml
	private String sanctionNumber;

	private Date sanctionDate;

	private Boolean active;

	@Transient
	private String departmentName;

	@Transient
	private Boolean editAllFields;

	@OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PurchaseItems> purchaseItems = new ArrayList<>();

	@OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EgBillPurchaseItemsDetails> egBillPurchaseItemsDetails;

	public List<PurchaseItems> getPurchaseItems() {
		return purchaseItems;
	}

	public void setPurchaseItems(List<PurchaseItems> purchaseItems) {
		this.purchaseItems = purchaseItems;
	}

	@Override
	public String getBankname() {
		return null;
	}

	@Override
	public String getBankaccount() {
		return null;
	}

	@Override
	public String getPanno() {
		return null;
	}

	@Override
	public String getTinno() {
		return null;
	}

	@Override
	public String getIfsccode() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getModeofpay() {
		return null;
	}

	@Override
	public String getCode() {
		return orderNumber;
	}

	@Override
	public Integer getEntityId() {
		return null;
	}

	@Override
	public String getEntityDescription() {
		return null;
	}

	@Override
	public EgwStatus getEgwStatus() {
		return null;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	protected void setId(Long id) {
		this.id = id;
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public Date getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Supplier getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier supplier) {
		this.supplier = supplier;
	}

	public BigDecimal getOrderValue() {
		return orderValue;
	}

	public void setOrderValue(BigDecimal orderValue) {
		this.orderValue = orderValue;
	}

	public BigDecimal getAdvancePayable() {
		return advancePayable;
	}

	public void setAdvancePayable(BigDecimal advancePayable) {
		this.advancePayable = advancePayable;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Fund getFund() {
		return fund;
	}

	public void setFund(Fund fund) {
		this.fund = fund;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Scheme getScheme() {
		return scheme;
	}

	public void setScheme(Scheme scheme) {
		this.scheme = scheme;
	}

	public SubScheme getSubScheme() {
		return subScheme;
	}

	public void setSubScheme(SubScheme subScheme) {
		this.subScheme = subScheme;
	}

	public String getSanctionNumber() {
		return sanctionNumber;
	}

	public void setSanctionNumber(String sanctionNumber) {
		this.sanctionNumber = sanctionNumber;
	}

	public Date getSanctionDate() {
		return sanctionDate;
	}

	public void setSanctionDate(Date sanctionDate) {
		this.sanctionDate = sanctionDate;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDepartmentName() {
		return departmentName;
	}

	public void setDepartmentName(String departmentName) {
		this.departmentName = departmentName;
	}

	public Boolean getEditAllFields() {
		return editAllFields;
	}

	public void setEditAllFields(Boolean editAllFields) {
		this.editAllFields = editAllFields;
	}

}
