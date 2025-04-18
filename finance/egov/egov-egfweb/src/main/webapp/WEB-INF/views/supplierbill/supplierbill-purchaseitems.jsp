<style>
.purchase-table {
	border-collapse: collapse;
	width: 100%;
}
.purchase-table th, .purchase-table td {
	border: 1px solid #ddd;
	padding: 8px;
	text-align: left;
}

.purchase-table th {
	background-color: #f2f2f2;
}
</style>

<div class="panel-heading">
	<div class="panel-title">Purchase-Items</div>
</div>
<div id="dynamicPurchaseItemList" class="panel-body"></div>

<script>
var purchaseList;

function getPurchaseItemsByOrderId() {
    var orderNumber = $("#purchaseOrder").val();
    
    $.ajax({
        url: '/services/EGF/supplierbill/get/purchaseItems?orderNumber=' + orderNumber,
        type: 'GET',
        contentType: 'application/json',
        success: function (dataList) {
            document.getElementById("purchaseObject").value = JSON.stringify(dataList);
            var jsonObjectArray = [];
            
            // Prepare jsonObjectArray based on dataList
            for (var i = 0; i < dataList.length; i++) {
                var item = dataList[i];
                
                var jsonObject = {
                    id: item.id,
                    itemCode: item.itemCode,
                    unit: item.unit,
                    unitRate: item.unitRate,
                    quantity: item.quantity,
                    unitValueWithGst: item.unitValueWithGst,
                    amount: item.amount,
                    orderNumber: item.purchaseOrder.orderNumber,
                    purchaseOrderId: item.purchaseOrder.id,
                    orderValue: item.purchaseOrder.orderValue
                };
                jsonObjectArray.push(jsonObject);
            }
            
            // Append purchaseObject JSON
            document.getElementById("purchaseObject").value = JSON.stringify(jsonObjectArray);
            
            // Clear any previous table content
            $('#dynamicPurchaseItemList').empty();
            
            var tableHTML = '<table id="tbldebitdetails1" border="1" class="purchase-table">';
            tableHTML += '<tr>';
            tableHTML += '<th>Item Code</th>';
            tableHTML += '<th>Unit Rate</th>';
            tableHTML += '<th contenteditable="true">Billed-Quantity</th>';
            tableHTML += '<th contenteditable="true">Unit Value With GST</th>';
            tableHTML += '<th contenteditable="true">Quantity</th>';
            tableHTML += '<th contenteditable="true">Amount</th>';
            tableHTML += '</tr>';
            
            // Loop through the data and generate table rows
            for (var i = 0; i < dataList.length; i++) {
                var data = dataList[i];
                tableHTML += '<tr>';
                tableHTML += '<td>' + data.itemCode + '</td>';
                tableHTML += '<td id="unitRate_' + i + '" contenteditable="true" class="editable unitRate">' + data.unitRate + '</td>';
                tableHTML += '<td id="billed-quantity_' + i + '" contenteditable="true" class="editable quantity">' + data.quantity + '</td>';
                tableHTML += '<td id="unitValueWithGst_' + i + '" contenteditable="true" class="editable unitValueWithGst">' + data.unitValueWithGst + '</td>';
                tableHTML += '<td id="quantity_' + i + '" contenteditable="true" class="editable quantity"></td>';
                tableHTML += '<td id="amount_' + i + '" class="amount"></td>';
                tableHTML += '</tr>';
            }

            // Append generated table HTML
            $('#dynamicPurchaseItemList').append(tableHTML);

            // Update the amount based on user input
            $(document).on('input', '.editable.quantity', function () {
                var row = $(this).closest('tr');
                var unitRate = parseFloat(row.find('.unitRate').text()) || 0;
                var quantity = parseFloat($(this).text()) || 0;
                var unitValueWithGst = parseFloat(row.find('.unitValueWithGst').text()) || 0;
                var amount = unitValueWithGst * quantity;

                // Update the amount field in the current row
                row.find('.amount').text(amount.toFixed(2));

                // Update total amount
                updateTotalAmount();
            });

            // Function to update the total amount
            function updateTotalAmount() {
                var totalAmount = 0;

                // Loop through each row and accumulate the amounts
                $('#tbldebitdetails1 tbody tr').each(function () {
                    var rowAmount = parseFloat($(this).find('.amount').text()) || 0;
                    totalAmount += rowAmount;
                });

                // Update total amount in the footer
                $('#totalAmount').text(totalAmount.toFixed(2));

                // Update the supplier's net payable amount
                var netPayableAmount = amountConverter(totalAmount);
                document.getElementById('billamount').value = netPayableAmount;
                $("#supplierNetPayableAmount").html(netPayableAmount);
            }

            // Function to format the amount
            function amountConverter(amt) {
                return amt.toFixed(2);
            }

            // Event listener for quantity change to check availability
            $(document).on('input', '.editable.quantity', function () {
                var row = $(this).closest('tr');
                var orderNumber = row.find('.orderNumber').text(); // Get order number from the row
                var quantity = parseFloat($(this).text()) || 0;

                $.ajax({
                    type: 'POST',
                    url: '/services/EGF/supplierbill/checkQuantity',
                    data: { orderNumber: orderNumber, quantity: quantity },
                    success: function (response) {
                        if (response === 'available') {
                            alert('Quantity is available.');
                        } else {
                            alert('Quantity is unavailable or exceeds available quantity.');
                        }
                    },
                    error: function () {
                        alert('Error checking quantity.');
                    }
                });
            });
        },
        error: function (error) {
            console.error('Error retrieving data:', error);
        }
    });
}
</script>
