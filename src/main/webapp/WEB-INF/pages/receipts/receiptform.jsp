<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<script type="text/javascript"
	src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript"
	src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/transactions/receipts.js"/>"></script>

<div class="box box-info" id="acct_model">
     <div class="box-header with-border">
              <h3 class="box-title">Enter Receipt Details</h3>
            </div>
            <div class="box-footer">
            <a
				href="<c:url value='/protected/transactions/receipts/receiptList'/> " 
				class="btn btn-info pull-right" style="margin-right: 10px;">Back</a> 
            <input type="button" class="btn btn-info pull-right"
				style="margin-right: 10px;" value="Print" id="btn-add-receipt"> 
			<input type="button"  id="btn-print-receipt"
				class="btn btn-info pull-right" style="margin-right: 10px;"
				value="Save Unprinted">
			
				
				</div>
	<div class="box-body">	
	  <form id="receipt-form" class="form-horizontal">
	     <div class="form-group form-required">
				<div class="col-md-6">
					<label for="brn-id" class="col-md-6 control-label">Currency
						</label>

					<div class="col-md-6">
						<input type="hidden" id="cur-id" name="currCode"
							rv-value="receipt.currency.curCode" /> 
						<input type="hidden" id="cur-name">
						<div id="curr-div" class="form-control"
							select2-url="<c:url value="/protected/transactions/invoices/currencies"/>">

						</div>
					</div>
				</div>
				<div class="col-md-6">
					<label for="brn-id" class="col-md-5 control-label">Payment
						Mode</label>

					<div class="col-md-7">
						<input type="hidden" id="pymt-id" name="payId"
							rv-value="invoice.paymentmodes.pmId" />
						<input type="hidden" id="pymt-desc">
						<div id="div-paymodes" class="form-control"
							select2-url="<c:url value="/protected/transactions/invoices/paymentmodes"/>">

						</div>
					</div>


				</div>


			</div>
			 <div class="form-group form-required">
				<div class="col-md-6">
					<label for="brn-id" class="col-md-6 control-label">Amount
						</label>

					<div class="col-md-6">
						 <input type="text" class="form-control" id="rct-amount"
									name="receiptAmount"  required>
					</div>
				</div>
				<div class="col-md-6">
					<label for="brn-id" class="col-md-5 control-label">Received
						From</label>

					<div class="col-md-7">
						<input type="text" class="form-control" id="paid-by"
									name="paidBy"  required>
					</div>

				</div>


			</div>
			 <div class="form-group form-required">
			 <div class="col-md-6">
					<label for="brn-id" class="col-md-6 control-label">Narration
						</label>

					<div class="col-md-6">
						<textarea rows="3" cols=30 class="form-control" name="receiptDesc" id="narration"></textarea>
					</div>

				</div>
				<div class="col-md-6">
					<label for="brn-id" class="col-md-5 control-label">Receipt Date
						</label>

					<div class="col-md-7">
						<div class='input-group date datepicker-input'>
				                    <input type='text' class="form-control" name="receiptDate" id="receipt-date" />
				                    <span class="input-group-addon">
				                        <span class="glyphicon glyphicon-calendar"></span>
				                    </span>
				                </div>
					</div>
				</div>
				


			</div>
			 <div class="form-group">
				<div class="col-md-6">
					<label for="brn-id" class="col-md-6 control-label">Payment Ref
						</label>

					<div class="col-md-6">
						<input type="text" class="form-control" id="payment-ref"
									name="paymentRef"  required>
					</div>
				</div>
				<div class="col-md-6">
					<label for="brn-id" class="col-md-5 control-label">Document Date
						</label>

					<div class="col-md-7">
						<div class='input-group date datepicker-input'>
				                    <input type='text' class="form-control" name="documentDate" id="doc-date" />
				                    <span class="input-group-addon">
				                        <span class="glyphicon glyphicon-calendar"></span>
				                    </span>
				                </div>
					</div>

				</div>


			</div>
			 <div class="form-group">
				<div class="col-md-6">
					<label for="brn-id" class="col-md-6 control-label">Manual Ref
						</label>

					<div class="col-md-6">
						<input type="text" class="form-control" id="manual-ref"
									name="manualRef"  required>
					</div>
				</div>
				<div class="col-md-6">
					

				</div>


			</div>
	  
	  </form>
	  <div id="rates-details-div">
	  <div class="box-header with-border">
     	<h3 class="box-title">Allocate to Invoice Transactions</h3>
	    <hr>
	    
            <input type="button" id="add-det-btn"
				class="btn btn-info pull-left" style="margin-right: 10px;"
				value="Add">
	    </div>
	    <div class="box-body">	
	    <table id="rct-detail-tbl" class="table table-hover table-bordered">
		<thead>
			<tr>

				<th>Trans No</th>
				<th>Invoice Number</th>
				<th>Date</th>
				<th>Tenant</th>
				<th>Narration</th>
				<th>Amount</th>
				<th>Balance</th>
				<th>Allocated Amount</th>
				<th></th>
			</tr>
		</thead>
		
	</table>
	</div>
	</div>
	</div>
	</div>
	
	<div class="modal fade" id="tenantTransModal" tabindex="-1" role="dialog"
		aria-labelledby="tenantTransModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
					<h4 class="modal-title" id="tenantTransModalLabel">
						Add new Invoice Transaction
					</h4>
				</div>
				<div class="modal-body">
				    <form id="search-form" class="form-horizontal pull-left">
						<div class="form-group">

							<div class="col-md-12">
								<label for="brn-id" class="col-md-6 control-label">Invoice
									Number</label>

								<div class="col-md-6">
									<input type='text' class="form-control pull-right"
										id="inv-search-number" />
								</div>
						</div>
						</div>
						<div class="form-group">
							<div class="col-md-12">
								<label for="brn-id" class="col-md-6 control-label">Tenant
									First Name</label>

								<div class="col-md-6">
									<input type='text' class="form-control pull-right"
										id="inv-search-name" />
								</div>
							</div>
						</div>
						<div class="form-group">
							<div class="col-md-12">
								<label for="brn-id" class="col-md-6 control-label">Tenant
									Other Names</label>

								<div class="col-md-6">
									<input type='text' class="form-control pull-right"
										id="inv-search-other-names" />
								</div>
							</div>
						</div>
						<div class="form-group">
							<input type="button" class="btn btn-info pull-right"
								style="margin-right: 10px;" value="Search"
								id="btn-search-invoice">
						</div>


					</form>
					<table id="modal-rct-detail-tbl" class="table table-hover table-bordered">
					<thead>
						<tr>
						    <th></th>
							<th>Trans No</th>
							<th>Invoice Number</th>
							<th>Date</th>
							<th>Tenant</th>
							<th>Amount</th>
							<th>Balance</th>
							
						</tr>
					</thead>
				</table>
				</div>
				<div class="modal-footer">
					<button data-loading-text="Saving..." id="btn-add-selected-exit"
						type="button" class="btn btn-primary">
						Add Selected and Close
					</button>
					<button data-loading-text="Saving..." id="btn-add-selected"
						type="button" class="btn btn-primary">
						Add Selected
					</button>
					<button type="button" class="btn btn-default" data-dismiss="modal">
						Cancel
					</button>
				</div>
			</div>
		</div>
	</div>