		<%--
  ~    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
  ~    accountability and the service delivery of the government  organizations.
  ~
  ~     Copyright (C) 2017  eGovernments Foundation
  ~
  ~     The updated version of eGov suite of products as by eGovernments Foundation
  ~     is available at http://www.egovernments.org
  ~
  ~     This program is free software: you can redistribute it and/or modify
  ~     it under the terms of the GNU General Public License as published by
  ~     the Free Software Foundation, either version 3 of the License, or
  ~     any later version.
  ~
  ~     This program is distributed in the hope that it will be useful,
  ~     but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~     GNU General Public License for more details.
  ~
  ~     You should have received a copy of the GNU General Public License
  ~     along with this program. If not, see http://www.gnu.org/licenses/ or
  ~     http://www.gnu.org/licenses/gpl.html .
  ~
  ~     In addition to the terms of the GPL license to be adhered to in using this
  ~     program, the following additional terms are to be complied with:
  ~
  ~         1) All versions of this program, verbatim or modified must carry this
  ~            Legal Notice.
  ~            Further, all user interfaces, including but not limited to citizen facing interfaces,
  ~            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
  ~            derived works should carry eGovernments Foundation logo on the top right corner.
  ~
  ~            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
  ~            For any further queries on attribution, including queries on brand guidelines,
  ~            please contact contact@egovernments.org
  ~
  ~         2) Any misrepresentation of the origin of the material is prohibited. It
  ~            is required that all modified versions of this material be marked in
  ~            reasonable ways as different from the original version.
  ~
  ~         3) This license does not grant any rights to any user of the program
  ~            with regards to rights under trademark law for use of the trade names
  ~            or trademarks of eGovernments Foundation.
  ~
  ~   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
  ~
  --%>

















<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<div class="main-content">
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title">
						<spring:message code="lbl.grantAmountTransfer" text="Fund Disbursement Order" />
					</div>
				</div>
				         <div class="panel-body custom">
				         <div class="row add-border">
				           <div class="col-xs-3 add-margin">
                         <spring:message code="grantAmountTransfer.date" text="Date" />
                           </div>
                       <div class="col-sm-3 add-margin view-content">${grantAmountTransfer.date}</div> 
                          <div class="col-xs-3 add-margin">
                         <spring:message code="grantAmountTransfer.ulbName" text="ULB Name" />
                           </div>
                       <div class="col-sm-3 add-margin view-content">${grantAmountTransfer.ulbName}</div> 
						<div class="col-xs-3 add-margin">
							<spring:message code="grantAmountTransfer.ulbCode"
								text="ULB Code" />
						</div>
						<div class="col-sm-3 add-margin view-content">${grantAmountTransfer.ulbCode}</div>
					</div>
					<div class="row add-border">
						<div class="col-xs-3 add-margin">
							<spring:message code="grantAmountTransfer.grantType" text="Grant Type" />
							
						</div>
						<div class="col-sm-3 add-margin view-content">${grantAmountTransfer.code}						
						</div>
						<div class="col-xs-3 add-margin">
							<spring:message code="grantAmountTransfer.grant" text="Name" />
						</div>
						<div class="col-sm-3 add-margin view-content">${grantAmountTransfer.name}</div>
					</div>
					<div class="row add-border">
						<div class="col-xs-3 add-margin">
							<spring:message code="grantAmountTransfer.bankAccountType"
								text="Bank Account Type" />
						</div>
						<div class="col-sm-3 add-margin view-content">${grantAmountTransfer.bankAccountType}</div>
						<div class="col-xs-3 add-margin">
						
							<spring:message code="grantAmountTransfer.banks" text="Bank & Branch Id" />
						</div>
						<div class="col-sm-3 add-margin view-content">${grantAmountTransfer.bankId}</div>
					</div>
					<div class="row add-border">
						<div class="col-xs-3 add-margin">
							<spring:message code="grantAmountTransfer.bankAccountNumber" text="Bank Account Number" />
						</div>
						<div class="col-sm-3 add-margin view-content">${grantAmountTransfer.bankAccountNumber}</div>
						
						<div class="col-xs-3 add-margin">
							<spring:message code="grantAmountTransfer.ifscCode" text="Bank IFSC" />
						</div>
						<div class="col-sm-3 add-margin view-content">${grantAmountTransfer.bankIFSC}</div>
					</div>
				</div>
			</div>
		</div>
		<div class="row text-center">
			<div class="add-margin">
				<c:if test="${mode == 'view'}">
					<a href="javascript:void(0)" class="btn btn-default"
						onclick="self.close()"><spring:message code='lbl.close'
							text="Close" /></a>
				</c:if>
				<c:if test="${mode == 'create'}">
					<a href="javascript:void(0)" class="btn btn-default"
						onclick="javascript:window.parent.postMessage('close','*');"><spring:message
							code='lbl.close' text="Close" /></a>
				</c:if>
			</div>
		</div>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
		
<%-- 		
<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ include file="/includes/taglibs.jsp"%>
<div class="main-content">
	<div class="row">
		<div class="col-md-12">
			<div class="panel panel-primary" data-collapsed="0">
				<div class="panel-heading">
					<div class="panel-title">
						<spring:message code="lbl.grantAmountTransfer" text="Fund Disbursement Order" />
					</div>
				</div>
				
				<div class="row add-border">
						<div class="col-xs-3 add-margin">
							<spring:message code="grantAmountTransfer.ulbName" text="ULB Name" />
						</div>
						<div class="col-sm-3 add-margin view-content">${grantAmountTransfer.ulbName}</div>
						<div class="col-xs-3 add-margin">
							<spring:message code="grantAmountTransfer.ulbCode"
								text="ULB Code" />
						</div>
						<div class="col-sm-3 add-margin view-content">${grantAmountTransfer.ulbCode}</div>
					</div>
					
				<div class="panel-body custom">
					<div class="row add-border">
						<div class="col-xs-3 add-margin">
							<spring:message code="grantAmountTransfer.grantType" text="Grant Type" />
							
						</div>
						<div class="col-sm-3 add-margin view-content">${grantAmountTransfer.code}						
					
						</div>
						<div class="col-xs-3 add-margin">
							<spring:message code="grantAmountTransfer.grant" text="Name" />
						</div>
						<div class="col-sm-3 add-margin view-content">${grantAmountTransfer.name}</div>
					</div>
					
					
					
				
					
					
					<div class="row add-border">
						<div class="col-xs-3 add-margin">
							<spring:message code="grantAmountTransfer.bankAccountType"
								text="Bank Account Type" />
						</div>
						<div class="col-sm-3 add-margin view-content">${grantAmountTransfer.bankAccountType}</div>
						<div class="col-xs-3 add-margin">
						
							<spring:message code="grantAmountTransfer.banks" text="banks" />
						</div>
						<div class="col-sm-3 add-margin view-content">${grantAmountTransfer.bank.name}</div>
					</div>
					
					
					
					<div class="row add-border">
						<div class="col-xs-3 add-margin">
							<spring:message code="grantAmountTransfer.bankAccountNumber" text="Bank Account Number" />
						</div>
						<div class="col-sm-3 add-margin view-content">${grantAmountTransfer.bankAccountNumber}</div>
						
						<div class="col-xs-3 add-margin">
							<spring:message code="grantAmountTransfer.ifscCode" text="Bank IFSC" />
						</div>
						<div class="col-sm-3 add-margin view-content">${grantAmountTransfer.bankIFSC}</div>
					</div>
					
					
					
				</div>
			</div>
		</div>
		<div class="row text-center">
			<div class="add-margin">
				<c:if test="${mode == 'view'}">
					<a href="javascript:void(0)" class="btn btn-default"
						onclick="self.close()"><spring:message code='lbl.close'
							text="Close" /></a>
				</c:if>
				<c:if test="${mode == 'create'}">
					<a href="javascript:void(0)" class="btn btn-default"
						onclick="javascript:window.parent.postMessage('close','*');"><spring:message
							code='lbl.close' text="Close" /></a>
				</c:if>
			</div>
		</div>
 --%>		
		
		

