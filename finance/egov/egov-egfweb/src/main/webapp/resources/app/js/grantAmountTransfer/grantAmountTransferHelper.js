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
 
 

jQuery('#btnsearch').click(function(e) {
    callAjaxSearch();
});

$(document).ready(function(){  

    refreshViewFromRadionOptionValue($('input[name=transferType]:checked').val(), true);
    
    $('input[type=radio][name=transferType]').change(function() {
        refreshViewFromRadionOptionValue(this.value, false);
    });
    
});

function refreshViewFromRadionOptionValue(optionValue, isFromPageLoad)
{
    if(optionValue === "FIRM")
    {
        $('#transferAmount').show(); 
        $("#transferAmount").attr('required','required');

    }
    else if(optionValue === "INDIVIDUALS")
    {
        $('#transferAmount').hide();
        $('*[required]').removeAttr('required');   
    }
}
/*function fillUlbCode() {
 //   var ulbMapData = document.getElementById("ulbMap").value; 
    var ulbMap = JSON.parse(ulbMap);
    
    var selectedValue = document.getElementById("ulbName").value; 
    // Check if the selected value is in the ulbMap
    if (ulbMap.hasOwnProperty(selectedValue)) {
        var code = ulbMap[selectedValue]; 
        document.getElementById("ulbCode").value = code; 
    } else {
        // Clear the ulbCode field if the selected value is not in the ulbMap
        document.getElementById("ulbCode").value = "";
    }
}*/
function fillUlbCode() {

    var selectedValue = document.getElementById("ulbName").value; 
    // Check if the selected value is in the ulbMap
    document.getElementById("ulbCode").value = selectedValue; 
   /* if (ulbMap.hasOwnProperty(selectedValue)) {
        var code = ulbMap[selectedValue]; 
        
    } else {
        // Clear the ulbCode field if the selected value is not in the ulbMap
        document.getElementById("ulbCode").value = "";
    }*/
}

function getFormData($form) {
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};

    $.map(unindexed_array, function(n, i) {
        indexed_array[n['name']] = n['value'];
    });

    return indexed_array;
}

function callAjaxSearch() {
    drillDowntableContainer = jQuery("#resultTable");
    jQuery('.report-section').removeClass('display-hide');
    reportdatatable = drillDowntableContainer
            .dataTable({
                ajax : {
                    url : "/services/EGF/grantsfund/ajaxsearch/" + $('#mode').val(),
                    type : "POST",
                    "data" : getFormData(jQuery('form'))
                },
                "fnRowCallback" : function(row, data, index) {
                    $(row).on(
                            'click',
                            function() {
                                console.log(data.id);
                                window.open('/services/EGF/grantsfund/' + $('#mode').val()
                                        + '/' + data.id, '',
                                        'width=800, height=600');
                            });
                },
                "bDestroy" : true,
                dom: "<'row'<'col-xs-12 pull-right'f>r>t<'row buttons-margin'<'col-md-3 col-xs-6'i><'col-md-3  col-xs-6'l><'col-md-3 col-xs-6'B><'col-md-3 col-xs-6 text-right'p>>",
                buttons: [
                          {
                            extend: 'print',
                            title: 'Fund Disbursement  Order Details',
                            filename: 'Fund Disbursement  Order  Details'
                        },{
                            extend: 'pdf',
                            title: 'Fund Disbursement Order Details',
                            filename:'Fund Disbursement Order Details'
                        },{
                            extend: 'excel',
                            message : 'Fund Disbursement Order Details',
                            filename: 'Fund Disbursement  Order Details'
                        }
                        ],
                aaSorting : [],
                columns : [{
                    "data" : "ulbName",
                    "sClass" : "text-left"
                }, {
                    "data" : "ulbCode",
                    "sClass" : "text-left"
                }, {
                    "data" : "code",
                    "sClass" : "text-left"
                }, {
                    "data" : "name",
                    "sClass" : "text-left"
                }, {
                    "data" : "amount",
                    "sClass" : "text-left"
                } ]
            });
}

