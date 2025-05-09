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
<style type="text/css">
#container iframe {
	width: 100%;
    height: 100%;
    padding-top: 131px;
    border: none;
}

.page-container .main-content{
    position: absolute;
    display: block;
    vertical-align: top;
    padding: 20px;
    width: 100%;
    padding-bottom: 0px;
    bottom: 0px;
}

#container {
	width: 100%;
	height: 550px;
	padding: 0;
	overflow-y: auto;
}

@media print {
	input#btnPrint {
		display: none;
	}
}

@media print {
	input#printPDF {
		display: none;
	}
}

@media print {
	input#printXLS {
		display: none;
	}
}
</style>

<body>
	<div id="container">
		<iframe id="report" name="report"
			src='/services/EGF/budget/budgetReport-ajaxGenerateULBWiseHtml.action?model.ulb=<s:property value="ulb"/>&model.financialYear.id=<s:property value="model.financialYear.id"/>&model.type=<s:property value="model.type"/>'></iframe>
	</div>

	<s:form name="budgetDetailReportULBForm" action="budgetReport"
		theme="simple">
		<input type="hidden" name="model.ulb"
			value='<s:property value="ulb"/>' />
		<input type="hidden" name="model.financialYear.id"
			value='<s:property value="model.financialYear.id"/>' />		
		<input type="hidden" name="model.type"
			value='<s:property value="model.type"/>' />
		<div id="buttons" class="buttonbottom">
			<input type="button" id="btnPrint"
				onclick="javascript:report.print();" value="PRINT"
				class="buttonsubmit" />
			<s:submit value="SAVE AS PDF"
				onclick="submitForm('generateULBWisePdf')"
				cssClass="buttonsubmit" />
			<s:submit value="SAVE AS EXCEL"
				onclick="submitForm('generateULBWiseXls')"
				cssClass="buttonsubmit" />
			<input type="button" name="button2" id="button2" value="Close" class="btn btn-default" onclick="window.parent.postMessage('close','*');window.close();">
		</div>
	</s:form>
	<script>
		function submitForm(method) {
			document.budgetDetailReportULBForm.action = "/services/EGF/budget/budgetReport-"
					+ method + ".action";
			jQuery(budgetDetailReportULBForm).append(jQuery('<input>', {
		        type : 'hidden',
		        name : '${_csrf.parameterName}',
		        value : '${_csrf.token}'
		    }));
			document.budgetDetailReportULBForm.submit();
		}
	</script>
</body>
