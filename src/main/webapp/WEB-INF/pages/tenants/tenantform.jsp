<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<script type="text/javascript" src="<c:url value="/js/modules/utils/select2builder.js"/>"></script>
<script type="text/javascript" src="<c:url value="/libs/rivets/rivets.js"/>"></script>
<script type="text/javascript" src="<c:url value="/js/modules/tenants/tenants.js"/>"></script>
<div class="box box-info" id="acct_model">
    
	<form id="account-form" class="form-horizontal" enctype="multipart/form-data">
		<div class="box-body">
		    <input type="hidden" name="acctId" id="acctId-pk">
			<div class="form-group form-required">
				<div class="col-md-6">
					<label for="houseId" class="control-label col-md-5">First
						Name</label>
					<div class="col-md-7">
						<input type="text" name="fname" id="fname" class="form-control"
							placeholder="First Name" required>
					</div>
				</div>
				<div class="col-md-6">
					<label for="houseName" class="control-label col-md-5">Other
						Names</label>
					<div class="col-md-7">
						<input type="text" name="otherNames" id="other-names" class="form-control"
							placeholder="Other Names" required>
					</div>
				</div>
			</div>
			<div class="form-group form-required">
				<div class="col-md-6">
					<label for="houseId" class="control-label col-md-5">Tenant ID</label>
					<div class="col-md-7">
						<input type="text" name="idPassportNo" id="idno" class="form-control"
							placeholder="" required disabled>
					</div>
				</div>
				<div class="col-md-6">
					<label for="noOfUnits" class="control-label col-md-5">Tenant Type</label>
					<div class="col-md-7">
						 <select class="form-control" id="sel2" name="status">
							        <option value="">Select Tenant Type</option>
							        <option value="I">Individual</option>
								    <option value="C">Corporate </option>
								  </select>
					</div>
				</div>
			</div>
			<div class="form-group form-required">
				<div class="col-md-6">
					<label for="houseId" class="control-label col-md-5">Pin No</label>
					<div class="col-md-7">
						<input type="email" name="email" id="email" class="form-control"
							placeholder="Pin No" required>
					</div>
				</div>
				<div class="col-md-6">
					<label for="noOfUnits" class="control-label col-md-5">Email</label>
					<div class="col-md-7">
						<input type="text" name="phoneNo" id="phone-no" class="form-control"
							placeholder="Email" required>
					</div>
				</div>
			</div>
			<div class="form-group form-required">
			    <div class="col-md-6">
					<label for="noOfUnits" class="control-label col-md-5">Address</label>
					<div class="col-md-7">
						<textarea rows="3" cols=30 class="form-control" name="address" id="address"></textarea>
					</div>
				
				</div>
				
				<div class="col-md-6">
					<label for="noOfUnits" class="control-label col-md-5">ID/Passport No</label>
					<div class="col-md-7">
						<input type="text" name="pinNo" id="pinNo" class="form-control"
							placeholder="ID/Passport No" required>
					</div>
				
				</div>
				
			</div>
			<div class="form-group form-required">
				<div class="col-md-6">
					<label for="houseId" class="control-label col-md-5">Status</label>
					<div class="col-md-7">
						 <select class="form-control" id="sel2" name="status">
							        <option value="">Select Status</option>
							        <option value="A">Active</option>
								    <option value="I">Inactive</option>
								  </select>
					</div>
				</div>
				<div class="col-md-6">
			    </div>	
			</div>
			<div class="form-group form-required">
				<div class="col-md-6">
					<label for="houseId" class="control-label col-md-5">Branch Registered</label>
					<div class="col-md-7">
						 <input type="hidden" id="obId" name="branch" rv-value="tenant.branch.brnCode"/>
		                       <input type="hidden" id="ob-name">
		                        <div id="ten-branch" class="form-control" 
				                                 select2-url="<c:url value="/protected/rental/setups/branches"/>" >
				                                 
				               </div> 
					</div>
				</div>
				<div class="col-md-6">
						
				</div>
			</div>
			 <div class='spacer'></div>
				        <h4>Other Information</h4>
				        <hr>
                 <div class="form-group">
							<label for="reg-wet" class="col-md-3 control-label">Date of Birth</label>

							<div class="col-md-3">
            	<div class='input-group date datepicker-input'>
						 <input type='text' class="form-control pull-right" name="dob" id="dob" />
				                    <div class="input-group-addon">
				                        <span class="glyphicon glyphicon-calendar"></span>
				                    </div>
					</div>
					</div>
				</div>
				 <div class="form-group">
							<label for="reg-wet" class="col-md-3 control-label">Date Terminated</label>

							<div class="col-md-3">
            	<div class='input-group date datepicker-input'>
						 <input type='text' class="form-control pull-right" name="dob" id="dob" />
				                    <div class="input-group-addon">
				                        <span class="glyphicon glyphicon-calendar"></span>
				                    </div>
					</div>
					</div>
				</div>
			<div class='spacer'></div>
                 
			<div class="form-group">

				<div class="col-md-6 form-required">
					<label for="file" class="control-label col-md-5">
						Photo</label>
					<div class="col-md-7">
						<div class="kv-avatar center-block" style="width: 200px">
							<input name="file" type="file" id="avatar" class="file-loading">

						</div>
					</div>
				</div>

			</div>
			 <div class='spacer'></div>
				        <h4>Unit Allocation Details</h4>
				        <hr>
				  <div class="form-group form-required">
				<div class="col-md-6">
					<label for="houseId" class="control-label col-md-5">Branch Allocated</label>
					<div class="col-md-7">
					     <input type="hidden" id="ob-alloc-id" name="branch" rv-value="tenant.branch.brnCode"/>
						 <div id="unit-branch" class="form-control" 
				                                 select2-url="<c:url value="/protected/rental/setups/branches"/>" >
				                                 
				               </div> 
					</div>
				</div>
				<div class="col-md-6">
					<label for="houseName" class="control-label col-md-5">Property Allocated</label>
					<div class="col-md-7">
						 <div id="acct-branch" class="form-control" 
				                                 select2-url="<c:url value="/protected/rental/setups/branches"/>" >
				                                 
				               </div> 
					</div>
				</div>
			</div>
			<div class="form-group form-required">
				<div class="col-md-6">
					<label for="houseId" class="control-label col-md-5">Unit Allocated</label>
					<div class="col-md-7">
						 <div id="acct-branch" class="form-control" 
				                                 select2-url="<c:url value="/protected/rental/setups/branches"/>" >
				                                 
				               </div> 
					</div>
				</div>
				
			</div>
			<div class="form-group">

				<div class="col-md-6">
					<label for="houseName" class="control-label col-md-5">Date Allocated</label>
					<div class="col-md-7">
						<div class='input-group date datepicker-input'>
						 <input type='text' class="form-control pull-right" name="dob" id="dob" disabled />
				                    <div class="input-group-addon">
				                        <span class="glyphicon glyphicon-calendar"></span>
				                    </div>
					</div>
					</div>
				</div>
				</div>
				 <div class="form-group">
				<div class="col-md-6">
		                    <label for="orgShtDesc" class="control-label col-md-5">Tenancy Status</label>
		                    <div class="col-md-7">
		                      <p class="form-control-static">Active</p>
		                    </div>    
		                    </div>   
				<div class="col-md-6">
		                    <label for="orgShtDesc" class="control-label col-md-5">Date Cancelled</label>
		                    <div class="col-md-7">
		                      <p class="form-control-static"> </p>
		                    </div>    
		                    </div>   
			</div>

		</div>
<div class="box-footer">
     
     <input type="submit" class="btn btn-info pull-left" style="margin-right: 10px;" value="Save and New">
	 <a href="<c:url value='/protected/setups/accts'/> " class="btn btn-info pull-left">Cancel</a>
	 </div>
	</form>
	</div>