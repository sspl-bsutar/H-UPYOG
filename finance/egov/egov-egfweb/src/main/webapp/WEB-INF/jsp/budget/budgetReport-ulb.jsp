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

<!-- Author : Megha 27/03/2025 -->
<%@ include file="/includes/taglibs.jsp"%>
<%@ page language="java"%>
<%@ page import="org.egov.budget.model.*"%>
<jsp:include page="budgetHeader.jsp">
	<jsp:param name="heading" value="Budget Report - ULBwise" />
</jsp:include>
<s:actionmessage theme="simple" />
<s:actionerror />
<s:fielderror />
<script>
	function popUp(url) {
		newwindow=window.open(url,'name','height=200,width=150');
		if (window.focus) {newwindow.focus()}
		return false;
	}
</script>
<s:actionmessage theme="simple" />
<s:form name="budgetDetailReportULBForm" action="budgetReport" id = "budgetDetailReportULBForm"
	theme="simple">
	<div class="formmainbox">
        	
		<div class="subheadnew"><s:text name="lbl.budget.report.ulbwise"/> </div>
		<%@include file="budgetReport-ulbform.jsp"%>
		<div class="buttonbottom" style="padding-bottom: 10px;">
			<s:submit key="lbl.search" onclick="return validateFinYear('printULBWiseReport');"
				cssClass="buttonsubmit" />
			<s:reset name="button" type="submit" cssClass="button" id="button"
					key="lbl.reset"/>
			<input type="button" value="<s:text name='lbl.close'/>"
					onclick="window.parent.postMessage('close','*');window.close();" class="button" />
		</div>
	</div>
</s:form>
<script>
	document.getElementById('function').style.display="none";
	document.getElementById('function_label').style.visibility="hidden";
</script>