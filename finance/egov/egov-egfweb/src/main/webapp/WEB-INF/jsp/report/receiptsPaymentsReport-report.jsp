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


<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
<link href="/services/EGF/resources/css/budget.css?rnd=${app_release_no}" rel="stylesheet"
  type="text/css" />
<style type="text/css">
@media print {
  #non-printable {
    display: none;
  }
}
</style>

<script>
var callback = {
    success: function(o){
      document.getElementById('result').innerHTML=o.responseText;
      undoLoadingMask();
      },
      failure: function(o) {
        undoLoadingMask();
        }
    }
function disableAsOnDate(){
  if(document.getElementById('period').value != "Date"){
    document.getElementById('asOndate').disabled = true;
    document.getElementById('financialYear').disabled = false;
  }else{
    document.getElementById('financialYear').disabled = true;
    document.getElementById('asOndate').disabled = false;
  }
}

function validateMandatoryFields(){

  if(document.getElementById('period').value=="Select")
  {
    bootbox.alert('<s:text name="msg.please.select.period"/>');
    return false;
  }
    
  if(document.getElementById('period').value!="Date"){
    if(document.getElementById('financialYear').value==0){
      bootbox.alert('<s:text name="msg.please.select.financial.year"/>');
      return false;
    }
  }
  if(document.getElementById('period').value=="Date" && document.getElementById('asOndate').value==""){
    bootbox.alert('<s:text name="msg.please.enter.as.onDate"/>');
    return false;
  }
  return true;
}
function getData(){
  if(validateMandatoryFields()){
    var csrfToken = document.getElementById('csrfTokenValue').value;
    doLoadingMask();
    var url = '/services/EGF/report/receiptsandPayments-ajaxPrintReceiptsPaymentsReport.action?showDropDown=false&model.period='+document.getElementById('period').value+'&model.currency='+document.getElementById('currency').value+'&model.financialYear.id='+document.getElementById('financialYear').value+'&model.asOndate='+document.getElementById('asOndate').value+'&_csrf='+csrfToken;
    YAHOO.util.Connect.asyncRequest('POST', url, callback, null);
    return true;
    }
    
  return false;
}
//function showAllMinorSchedules(){
  //if(validateMandatoryFields()){
    //var url= ('/services/EGF/report/cashFlowReport-generateScheduleReport.action?showDropDown=false&model.period='+document.getElementById('period').value+'&model.currency='+document.getElementById('currency').value+'&model.financialYear.id='+document.getElementById('financialYear').value+'&model.asOndate='+document.getElementById('asOndate').value,'','resizable=yes,height=650,width=900,scrollbars=yes,left=30,top=30,status=no');
  //return true;
    //}
  //return false;
//}



//addedby dmee
function ViewDetail(){
   if(validateMandatoryFields()){
   var csrfToken = document.getElementById('csrfTokenValue').value;
  doLoadingMask();
  var url = '/services/EGF/report/receiptsandPaymentsReport-generateDetailCodeReport.action?showDropDown=false&model.period='+document.getElementById('period').value+'&model.currency='+document.getElementById('currency').value+'&model.financialYear.id='+document.getElementById('financialYear').value+'&model.asOndate='+document.getElementById('asOndate').value+'&_csrf='+csrfToken;
    YAHOO.util.Connect.asyncRequest('POST', url, callback, null);
    return true;
   }
 return false;
   }


function showSchedule(majorCode, scheduleNo){
  if(validateMandatoryFields()){
    window.open('/services/EGF/report/receiptsandPaymentsReport-generatereceiptsandPaymentsSubReport.action?showDropDown=false&model.period='+document.getElementById('period').value+'&model.currency='+document.getElementById('currency').value+'&model.financialYear.id='+document.getElementById('financialYear').value+'&model.department.code='+document.getElementById('department').value+'&model.asOndate='+document.getElementById('asOndate').value+'&endDate='+document.getElementById('asOndate').value+'&asOnDateRange='+document.getElementById('asOndate').value,'','height=650,width=900,scrollbars=yes,left=30,top=30,status=no');
  return true;
    }
  return false;
}
</script>
<style>
th.bluebgheadtd {
  padding: 0px;
  margin: 0px;
}

.extracontent {
  font-weight: bold;
  font-size: xx-small;
  color: #CC0000;
}
</style>
<div id="non-printable">
  <s:form name="receiptsPaymentsReport" action="receiptsPaymentsReport"
    theme="simple">
    <div class="formmainbox">
      <div class="formheading"></div>
      <div class="subheadnew"><s:text name="ReceiptsPaymentsReport"/> </div>
      <input type="hidden" id="csrfTokenValue" name="${_csrf.parameterName}" value="${_csrf.token}"/>
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="10%" class="bluebox">&nbsp;</td>
          <td width="15%" class="bluebox"><s:text name="report.period" />:<span
            class="mandatory1">*</span></td>
          <td width="22%" class="bluebox"><s:select name="period"
              id="period"
              list="#{'Select':'---Choose---','Date':'Date','Yearly':'Yearly','Half Yearly':'Half Yearly'}"
              onclick="disableAsOnDate()" value="%{model.period}" /></td>
          <td class="bluebox" width="12%"><s:text
              name="report.financialYear" />:<span class="mandatory1">*</span></td>
          <td width="41%" class="bluebox"><s:select name="financialYear"
              id="financialYear" list="dropdownData.financialYearList"
              listKey="id" listValue="finYearRange" headerKey="0"
              headerValue="%{getText('lbl.choose.options')}" value="%{model.financialYear.id}" />
          </td>
        </tr>
        <tr>
          <td class="greybox">&nbsp;</td>
          <td class="greybox"><s:text name="report.asOnDate" />:</td>
          <td class="greybox"><s:textfield name="asOndate" id="asOndate"
              cssStyle="width:100px" /><a
            href="javascript:show_calendar('cashFlowReport.asOndate');"
            style="text-decoration: none">&nbsp;<img
              src="/services/egi/resources/erp2/images/calendaricon.gif" border="0" /></a>(dd/mm/yyyy)
          </td>
          <td class="greybox"><s:text name="report.rupees" />:<span
            class="mandatory1">*</span></td>
          <td class="greybox"><s:select name="currency" id="currency"
              list="#{'Rupees':'Rupees','Thousands':'Thousands','Lakhs':'Lakhs'}"
              value="%{model.currency}" /></td>
        </tr>
        <tr>
          <td></td>
        </tr>
      </table>
      <div align="left" class="mandatory1">
        
        <s:text name="report.mandatory.fields" />
      </div>
      <div class="buttonbottom" style="padding-bottom: 10px;">
        <input type="button" value="<s:text name='lbl.submit'/>" class="buttonsubmit"
          onclick="return getData()" /> <input name="button" type="button"
          class="buttonsubmit" id="button3" value="<s:text name='lbl.print'/>"
          onclick="window.print()" />&nbsp;&nbsp; <%-- <input type="button"
          value="<s:text name='lbl.view.all.minor.schedules'/>" class="buttonsubmit"
          onclick="return showAllMinorSchedules()" /> --%> &nbsp;&nbsp; <input
          type="button" value="<s:text name='lbl.view.all.schedules'/>" class="buttonsubmit"
          onclick="return ViewDetail()" /> &nbsp;&nbsp;
      </div>
      <div align="left" class="extracontent">
        To print the report, please ensure the following settings:<br /> 1.
        Paper size: A4<br /> 2. Paper Orientation: Landscape <br />
      </div>
    </div>
  </s:form>
</div>
<script>
disableAsOnDate();
</script>
<div id="result"></div>
