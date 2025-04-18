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

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c"%>
<%@ taglib prefix="s" uri="/WEB-INF/tags/struts-tags.tld"%>
  <%@ taglib prefix="st" uri="/struts-tags" %>
<%@ taglib prefix="egov" tagdir="/WEB-INF/tags"%>
<style>
.input-container {
    position: relative;
    width: 70%;
}

.suggestions-box {
    max-height: 150px;
    overflow-y: auto;
    border-top: none;
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
    background-color: #fff;
    z-index: 9999;
}

.suggestions-box1 {
    max-height: 150px;
    overflow-y: auto;
    border-top: none;
    box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1);
    background-color: #fff;
    z-index: 9999;
}
.suggestion-item {
    padding: 5px;
    cursor: pointer;
    background-color: #fff;
    border: 1px solid #ccc;
}

.suggestion-item:hover {
    background-color: #f0f0f0;
}

</style>



<tr>
    <td class="bluebox"><s:text name="masters.subscheme.search.fund" />
        <s:if test="%{defaultFundId==-1}">
            <span class="mandatory"></span>
        </s:if></td>
    <td class="bluebox">
        <s:select name="fundId" id="fundId"
                  list="dropdownData.fundList" listKey="id" listValue="name"
                  headerKey="-1" headerValue="----Choose----"
                  onchange="loadChanges(this)" value="%{fundId.id}" />
    </td>
    <s:if test="%{defaultFundId!=-1}">
        <script>
            document.getElementById("fundId").value = '<s:property value="defaultFundId"/>';
        </script>
    </s:if>
</tr>

<tr>
   
    <td class="greybox"><s:text name="masters.subscheme.search.scheme" /><span class="mandatory"></span></td>
    <s:hidden name="schemeId" id="schemeId" />
    <td class="greybox">
        <!-- Wrap the input and suggestion box in a container -->
        <div class="input-container" style="position: relative;">
            <s:textfield value="%{subScheme.scheme.name}" name="subScheme.scheme.name" id="subScheme.scheme.name"
                         autocomplete="off" onInput="autocompleteSchemeBy20LG()" onBlur="splitssSchemeCode(this)"
                         style="width: 70%; padding-right: 20px;" />
            <!-- Suggestions list appears below the input field -->
            <div id="suggestions_box" class="suggestions-box" style="display: none; position: absolute; top: 100%; left: 0; width: 70%; background-color: white; border: 1px solid #ccc; box-sizing: border-box; z-index: 9999;" ></div> 
        </div>
    </td>
 
    <td class="greybox"><s:text name="masters.subscheme.search" /><span class="mandatory"></span></td>
<s:hidden name="subSchemeId" id="subSchemeId" />

<td class="greybox">
    <div class="input-container" style="position: relative;">
        <s:textfield value="%{subScheme.name}" name="subScheme.name" id="subScheme.name" autocomplete="off" 
                      onBlur="splitSubSchemeCode(this);checkuniquenesscode();" />
        
        <!-- Suggestions box to show the sub-schemes -->
        <div id="suggestions_box1" class="suggestions-box1" 
             style="display: none; position: absolute; top: 100%; left: 0; width: 70%; background-color: white; 
                    border: 1px solid #ccc; box-sizing: border-box; z-index: 9999;">
        </div> 
    </div>
</td>

</tr>

<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script>
function loadChanges(obj) {
    var bankObj = document.getElementById('bank_branch');
    var bankAccountObj = document.getElementById('bankaccount');
    if (bankObj != null) {
        bankObj.options[0].selected = true;
        if (obj.options[obj.selectedIndex].value != -1)
            populatebank_branch({fundId: obj.options[obj.selectedIndex].value});
    }
    if (bankAccountObj != null)
        bankAccountObj.options[0].selected = true;
}

function autocompleteSchemeBy20LG() {
    const input = document.getElementById('subScheme.scheme.name');
    const suggestionsBox = document.getElementById('suggestions_box');
    const inputValue = input.value.trim();

    if (inputValue.length === 0) {
        suggestionsBox.style.display = 'none';
        input.style.height = 'auto'; 
        return;
    }

  
    input.style.height = 'auto';
    input.style.height = input.scrollHeight + 'px'; 

    // Prepare the query parameters for the AJAX request
    const startsWith = inputValue;
    const fundId = document.getElementById('fundId').value;

    $.ajax({
        url: '/services/EGF/voucher/common-ajaxLoadSchemeBy20.action',
        type: 'GET',
        data: {
            startsWith: startsWith, 
            fundId: fundId         
        },
        dataType: 'html',
        success: function(response) {
           
            const schemes = response.trim().split("\n"); 

            suggestionsBox.innerHTML = '';

            if (schemes.length > 0) {
                suggestionsBox.style.display = 'block'; 
                schemes.forEach(function(scheme) {
                    const div = document.createElement('div');
                    div.classList.add('suggestion-item');

                    const [schemeName, schemeId] = scheme.split(":");
                   
                    div.textContent = schemeName;
                    div.onclick = function() {
                        selectScheme(schemeName, schemeId); 
                    };

                    suggestionsBox.appendChild(div);
                });
            } else {
                suggestionsBox.style.display = 'none';
            }
        },
        error: function(xhr, status, error) {
            console.error('Error fetching schemes:', error);
        }
    });
}


function selectScheme(schemeName, schemeId) {
    const input = document.getElementById('subScheme.scheme.name');
    const schemeIdField = document.getElementById('schemeId');
    const suggestionsBox = document.getElementById('suggestions_box');

    input.value = schemeName;
    schemeIdField.value = schemeId;
    
    suggestionsBox.style.display = 'none';
     
     autocompleteSubSchemeBy20LG(schemeId);
    
}



function autocompleteSubSchemeBy20LG(schemeId) {
  
    $.ajax({
        url: '/services/EGF/voucher/common-ajaxLoadSubSchemeBy20.action', 
        type: 'GET',
        data: { schemeId: schemeId }, 
        dataType: 'json',
        success: function(response) {
            
            const suggestionsBox1 = document.getElementById('suggestions_box1');
            suggestionsBox1.innerHTML = ''; 

           
            if (response && response.length > 0) {
                suggestionsBox1.style.display = 'block'; 

               
                response.forEach(function(subScheme) {
                    const div = document.createElement('div');
                    div.classList.add('suggestion-item');
                    div.textContent = subScheme.name; 
                    div.setAttribute('data-id', subScheme.id); 
                 
                    div.onclick = function() {
                        selectSubScheme(subScheme.id, subScheme.name);
                    };

                    suggestionsBox1.appendChild(div); 
                });
            } else {
                suggestionsBox1.style.display = 'none';
                alert('No sub-schemes found for this scheme.');
            }
        },
        error: function(xhr, status, error) {
            console.error('Error fetching sub-schemes:', error);
            alert('Error fetching sub-schemes. Please try again later.');
        }
    });
}

function selectSubScheme(id, name) {
    const inputField = document.getElementById('subScheme.name');
    const hiddenSubSchemeIdField = document.getElementById('subSchemeId');
    inputField.value = name;
    hiddenSubSchemeIdField.value = id; 

   
    document.getElementById('suggestions_box1').style.display = 'none';
}

</script>

