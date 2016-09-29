package com.brokersystems.invtransactions.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.brokersystems.invtransactions.model.QTenantInvoice;
import com.brokersystems.invtransactions.model.QTenantInvoiceDetails;
import com.brokersystems.invtransactions.model.RevisionForm;
import com.brokersystems.invtransactions.model.TenantInvoice;
import com.brokersystems.invtransactions.model.TenantInvoiceBean;
import com.brokersystems.invtransactions.model.TenantInvoiceDetails;
import com.brokersystems.invtransactions.model.TenantInvoiceDetailsBean;
import com.brokersystems.invtransactions.model.Transactions;
import com.brokersystems.invtransactions.repository.InvoiceDetailsRepository;
import com.brokersystems.invtransactions.repository.InvoiceRepository;
import com.brokersystems.invtransactions.repository.TransactionRepository;
import com.brokersystems.invtransactions.service.InvoiceService;
import com.brokersystems.server.datatables.DataTablesRequest;
import com.brokersystems.server.datatables.DataTablesResult;
import com.brokersystems.server.exception.BadRequestException;
import com.brokersystems.server.utils.FormatUtils;
import com.brokersystems.server.utils.UserUtils;
import com.brokersystems.setup.repository.CurrencyRepository;
import com.brokersystems.setup.repository.OrgBranchRepository;
import com.brokersystems.setup.repository.PaymentModeRepo;
import com.brokersystems.setup.repository.RateTypeRepository;
import com.brokersystems.setup.repository.RentalUnitChargeRepo;
import com.brokersystems.setup.repository.RentalUnitsRepository;
import com.brokersystems.setup.repository.TenantAllocRepo;
import com.brokersystems.setup.repository.TenantRepository;
import com.brokersystems.setups.model.Currencies;
import com.brokersystems.setups.model.PaymentModes;
import com.brokersystems.setups.model.QAccountDef;
import com.brokersystems.setups.model.QCurrencies;
import com.brokersystems.setups.model.QOrgBranch;
import com.brokersystems.setups.model.QPaymentModes;
import com.brokersystems.setups.model.QRentalUnitCharges;
import com.brokersystems.setups.model.QTenantDef;
import com.brokersystems.setups.model.RentalUnitCharges;
import com.brokersystems.setups.model.RentalUnits;
import com.brokersystems.setups.model.TenantDef;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;

@Service
public class InvoiceServiceImpl implements InvoiceService {

	@Autowired
	private InvoiceRepository invoiceRepo;

	@Autowired
	private CurrencyRepository currencyRepo;

	@Autowired
	private PaymentModeRepo paymentRepo;

	@Autowired
	private TenantRepository tenantRepo;

	@Autowired
	private RentalUnitChargeRepo chargesRepo;

	@Autowired
	private InvoiceDetailsRepository invoceDetRepo;

	@Autowired
	private RateTypeRepository ratetypeRepo;

	@Autowired
	private OrgBranchRepository branchRepo;
	
	@Autowired
	private UserUtils userUtils;
	
	@Autowired
	private TransactionRepository transRepo;
	

	@Override
	public DataTablesResult<TenantInvoice> findAllInvoices(DataTablesRequest request) throws IllegalAccessException {
		Page<TenantInvoice> page = invoiceRepo.findAll(request.searchPredicate(QTenantInvoice.tenantInvoice), request);
		return new DataTablesResult(request, page);
	}

	@Override
	@Transactional(readOnly = true)
	public Page<TenantDef> findActiveTenants(String paramString, Pageable paramPageable) {
		return invoiceRepo.findTenantsWithoutContracts(paramString, paramPageable);
	}

	@Override
	public Page<Currencies> findCurrencyForSelect(String paramString, Pageable paramPageable) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QCurrencies.currencies.enabled.eq(true).and(QCurrencies.currencies.isNotNull());
		} else {
			pred = QCurrencies.currencies.enabled.eq(true).and(QCurrencies.currencies.curName.containsIgnoreCase(paramString));
		}
		
		return currencyRepo.findAll(pred, paramPageable);
	}

	@Override
	public Page<PaymentModes> findPaymentModesForSelect(String paramString, Pageable paramPageable) {
		Predicate pred = null;
		if (paramString == null || StringUtils.isBlank(paramString)) {
			pred = QPaymentModes.paymentModes.isNotNull();
		} else {
			pred = QPaymentModes.paymentModes.pmDesc.containsIgnoreCase(paramString);
		}
		return paymentRepo.findAll(pred, paramPageable);
	}

	@Override
	@Modifying
	@Transactional(readOnly = false)
	public TenantInvoiceBean createInvoice(TenantInvoice invoice) throws BadRequestException {
		if (invoice.getTenantId() == null) {
			throw new BadRequestException("Tenant is Mandatory");
		}
		if (invoice.getBranchId() == null) {
			throw new BadRequestException("Branch is Mandatory");
		}
		if (invoice.getPayId() == null) {
			throw new BadRequestException("Payment Mode is Mandatory");
		}
		if (invoice.getCurrCode() == null) {
			throw new BadRequestException("Currency is Mandatory");
		}
		//
		if (invoice.getInvoiceDate().after(new Date())) {
			throw new BadRequestException("Invoice Date cannot be in future " + invoice.getInvoiceDate() + " now "
					+ new Date() + " " + invoice.getWefDate() + " " + invoice.getWetDate());
		}

		if (invoice.getWefDate() == null) {
			throw new BadRequestException("Invoice Date From is Mandatory");
		}
		if(invoice.getInvoiceId()==null){
	      
			Long count = invoiceRepo.getActiveTenancyCount(invoice.getTenantId());
			
			if(count > 0){
				throw new BadRequestException("An Existing Invoice for the tenant for the invoice period exists....");
			}
		}
		
		if(invoice.getInvoiceNumber()==null){
		
		final String invNumber = String.format("%05d", invoiceRepo.count() + 1);
		invoice.setInvoiceNumber("INV" + invNumber);
		invoice.setRevisionNumber("INV" +invNumber+"/1");
		}
		invoice.setStatus("D");
		invoice.setTransType("NT");
		
		if (invoice.getInvoiceId() != null) {
			TenantInvoice editInvoice = invoiceRepo.findOne(invoice.getInvoiceId());
			if(editInvoice == null) throw new BadRequestException("The invoice does not exist. Cannot Authorize");
			if("A".equalsIgnoreCase(editInvoice.getStatus())){
				throw new BadRequestException("Cannot Save..Invoice already authorized");
			}
			
			List<TenantInvoiceDetails> invdet = invoceDetRepo.getInvoiceDetails(invoice.getInvoiceId());
			invoceDetRepo.delete(invdet);
			
		}

		BigDecimal totalTax = BigDecimal.ZERO;
		BigDecimal grossAmount = BigDecimal.ZERO;
		BigDecimal installMent = BigDecimal.ZERO;
		List<TenantInvoiceDetails> invList = new ArrayList<>();
        BigDecimal multiRate =new BigDecimal(getCalculateRate(invoice.getFrequency()));
		for (TenantInvoiceDetails invoiceDet : invoice.getDetails()) {
			BigDecimal taxValue = BigDecimal.ZERO;
			
			RentalUnitCharges charge = chargesRepo.findOne(invoiceDet.getChargeId());
			
			if (charge.isTaxable()) {
				String rateType = charge.getTaxRateType();
				BigDecimal rate = charge.getTaxValue();
				if (rateType != null) {
					if ("P".equalsIgnoreCase(rateType)) {
						taxValue = rate.divide(new BigDecimal(100)).multiply(invoiceDet.getAmount());
					} else
						taxValue = rate;
				}
				if(charge.getFrequency().equalsIgnoreCase("Monthly")){
					totalTax = totalTax.add(taxValue.multiply(multiRate));
				}
				else  if(charge.getFrequency().equalsIgnoreCase("One-Off")){
					totalTax = totalTax.add(taxValue.multiply(BigDecimal.ONE));
				}
			}
			
			invoiceDet.setCharge(charge);
			if (invoiceDet.getRateTyoeId() != null)
				invoiceDet.setRateType(ratetypeRepo.findOne(invoiceDet.getRateTyoeId()));
			if(charge.getFrequency().equalsIgnoreCase("Monthly")){
				grossAmount = grossAmount.add(invoiceDet.getAmount().multiply(multiRate));
				 installMent = installMent.add(invoiceDet.getAmount().multiply(multiRate));
				invoiceDet.setNetAmount(invoiceDet.getAmount().subtract(taxValue).multiply(multiRate));
				invoiceDet.setAmount(invoiceDet.getAmount().multiply(multiRate));
			}
			else if(charge.getFrequency().equalsIgnoreCase("One-Off")){
				grossAmount = grossAmount.add(invoiceDet.getAmount());
				invoiceDet.setNetAmount(invoiceDet.getAmount().subtract(taxValue));
				invoiceDet.setAmount(invoiceDet.getAmount());
				//if(!charge.isRefundable()) installMent = installMent.add(invoiceDet.getAmount().subtract(taxValue));
			}
			
			
			invoiceDet.setInvoice(invoice);
			invList.add(invoiceDet);

		}
		invoceDetRepo.save(invList);
		if (invoice.getBranchId() != null)
			invoice.setBranch(branchRepo.findOne(invoice.getBranchId()));
		if (invoice.getCurrCode() != null)
			invoice.setTransCurrency(currencyRepo.findOne(invoice.getCurrCode()));
		if (invoice.getPayId() != null)
			invoice.setPaymentMode(paymentRepo.findOne(invoice.getPayId()));
		if (invoice.getTenantId() != null)
			invoice.setTenant(tenantRepo.findOne(invoice.getTenantId()));
		invoice.setInvDetails(invList);
		invoice.setInvAmount(grossAmount);
		invoice.setNetAmount((grossAmount.subtract(totalTax)));
		invoice.setTaxAmount(totalTax);
		invoice.setInstallmentAmount(installMent);
		invoice.setRenewalDate(FormatUtils.addDays(invoice.getWetDate(), 1));
		TenantInvoice created = invoiceRepo.save(invoice);
		
		List<TenantInvoiceDetailsBean> details = new ArrayList<>();
		for (TenantInvoiceDetails child : created.getInvDetails()) {
			if(child.getCharge().getFrequency().equalsIgnoreCase("Monthly"))
				details.add(new TenantInvoiceDetailsBean(child.getDetailId(), child.getCharge().getChargeId(),
						child.getRateType().getRateId(), child.getRateType().getRateType(), child.getAmount().divide(multiRate, 2, RoundingMode.HALF_EVEN),
						child.getNetAmount().divide(multiRate, 2, RoundingMode.HALF_EVEN)));
			else if(child.getCharge().getFrequency().equalsIgnoreCase("One-Off"))
			details.add(new TenantInvoiceDetailsBean(child.getDetailId(), child.getCharge().getChargeId(),
					child.getRateType().getRateId(), child.getRateType().getRateType(), child.getAmount(),
					child.getNetAmount()));
		}
		return new TenantInvoiceBean(created.getInvoiceNumber(), created.getInvoiceId(),
				FormatUtils.formatCurrency(created.getInvAmount()), FormatUtils.formatCurrency(created.getTaxAmount()),
				FormatUtils.formatCurrency(created.getNetAmount()),
				created.getTenant().getFname() + " " + created.getTenant().getOtherNames(),created.getRenewalDate(),FormatUtils.formatCurrency(invoice.getInstallmentAmount()) ,invoice.getRevisionNumber(), details);

	}

	@Override
	@Transactional(readOnly = true)
	public TenantInvoice findByInvoiceId(Long invoiceId) throws BadRequestException {
		TenantInvoice invoice = invoiceRepo.findOne(invoiceId);
		if (invoice == null)
			throw new BadRequestException("The Invoice Does not Exist....");
		return invoice;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RentalUnitCharges> getActiveCharges(long unitCode, Date invoiceDate) throws BadRequestException {
		return chargesRepo.getActiveUnitCharges(unitCode, invoiceDate);
		
	}

	@Override
	@Transactional(readOnly = true)
	public TenantInvoiceBean queryInvoice(Long invoiceId) throws BadRequestException {
		TenantInvoice invoice = findByInvoiceId(invoiceId);
		List<TenantInvoiceDetails> invdet = invoceDetRepo.getInvoiceDetails(invoiceId);
		List<TenantInvoiceDetailsBean> details = new ArrayList<>();
		BigDecimal calcRate = new BigDecimal(getCalculateRate(invoice.getFrequency()));
		for (TenantInvoiceDetails child : invdet) {
			if(child.getCharge().getFrequency().equalsIgnoreCase("Monthly"))
				details.add(new TenantInvoiceDetailsBean(child.getDetailId(), child.getCharge().getChargeId(),
						child.getRateType().getRateId(), child.getRateType().getRateType(), child.getAmount().divide(calcRate, 2, RoundingMode.HALF_EVEN),
						child.getNetAmount().divide(calcRate, 2, RoundingMode.HALF_EVEN)));
			else if(child.getCharge().getFrequency().equalsIgnoreCase("One-Off"))
			details.add(new TenantInvoiceDetailsBean(child.getDetailId(), child.getCharge().getChargeId(),
					child.getRateType().getRateId(), child.getRateType().getRateType(), child.getAmount(),
					child.getNetAmount()));
		}

		TenantInvoiceBean bean = new TenantInvoiceBean(invoice.getInvoiceNumber(), invoice.getInvoiceId(),
				FormatUtils.formatCurrency(invoice.getInvAmount()), FormatUtils.formatCurrency(invoice.getTaxAmount()),
				FormatUtils.formatCurrency(invoice.getNetAmount()),
				invoice.getTenant().getFname() + " " + invoice.getTenant().getOtherNames(), invoice.getInvoiceDate(),
				invoice.getWefDate(), invoice.getWetDate(), invoice.getFrequency(), invoice.getStatus(),
				invoice.getTenant().getTenId(), invoice.getTransCurrency(), invoice.getBranch(),
				invoice.getPaymentMode(), invoice.getRenewalDate(),FormatUtils.formatCurrency(invoice.getInstallmentAmount()),invoice.getRevisionNumber(), details);
		return bean;
	}

	@Override
	public Long getCalculateRate(String frequency) throws BadRequestException{
		if(frequency==null) throw new BadRequestException("Frequency cannot be null");
		if("M".equalsIgnoreCase(frequency)) return 1l;
		else if("Q".equalsIgnoreCase(frequency)) return 3L;
		else if("S".equalsIgnoreCase(frequency)) return 6L;
		else if("A".equalsIgnoreCase(frequency)) return 12L;
		return null;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RentalUnitCharges> getNewCharges(long unitCode, Date invoiceDate, long tencode) throws BadRequestException {
		return chargesRepo.getNewActiveCharges(unitCode, invoiceDate,tencode);
	}

	@Override
	@Modifying
	@Transactional(readOnly = false)
	public void authorizeInvoice(Long invoiceId) throws BadRequestException {
		if(invoiceId == null) throw new BadRequestException("The invoice does not exist. Cannot Authorize");
		TenantInvoice invoice = invoiceRepo.findOne(invoiceId);
		if(invoice == null) throw new BadRequestException("The invoice does not exist. Cannot Authorize");
		List<TenantInvoiceDetails> tenDetails = invoceDetRepo.getInvoiceDetails(invoiceId);
		if("A".equalsIgnoreCase(invoice.getStatus())){
			throw new BadRequestException("Cannot Authorize..Invoice already authorized");
		}
		BigDecimal sum =tenDetails.stream().map(a -> a.getNetAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
		if(sum.compareTo(invoice.getNetAmount())!=0){
			throw new BadRequestException("Invoice details totals does not match with parent invoice totals "+invoice.getNetAmount()+" ; "+sum);
		}
		TenantInvoice prevInvoice =  invoice.getPreviousTrans();
		if(!"CN".equalsIgnoreCase(invoice.getTransType())){
		Transactions trans = new Transactions();
		trans.setAuthoriedBy(userUtils.getCurrentUser().getUsername());
		trans.setAuthorized("Y");
		trans.setRefno(invoice.getInvoiceNumber());
		trans.setTenant(invoice.getTenant());
		trans.setTransAmount(invoice.getInvAmount());
		trans.setTransBalance(invoice.getInvAmount());
		trans.setTransCommission(BigDecimal.ZERO);
		trans.setTransCurrency(invoice.getTransCurrency());
		trans.setTransDC("D");
		trans.setTransNetAmt(invoice.getNetAmount());
		trans.setTransTaxes(invoice.getTaxAmount());
		trans.setTranstype("INV");
		trans.setTransSettledAmt(BigDecimal.ZERO);
		transRepo.save(trans);
		}
		invoice.setStatus("A");
		invoice.setAuthBy(userUtils.getCurrentUser());
		invoice.setPreviousTrans(invoice);
		if("CN".equalsIgnoreCase(invoice.getTransType())){
			invoice.setCurrentStatus("CN");
		}else
		invoice.setCurrentStatus("A");
		
		invoiceRepo.save(invoice);
		if(prevInvoice!=null){
			prevInvoice.setCurrentStatus("RV");
			invoiceRepo.save(prevInvoice);
		}
		
		
	}

	@Override
	public DataTablesResult<TenantInvoiceDetails> findInvoiceDetails(DataTablesRequest request, Long invoiceCode)
			throws IllegalAccessException {
	     QTenantInvoice invoices = QTenantInvoiceDetails.tenantInvoiceDetails.invoice;
	     Predicate pred = invoices.invoiceId.eq(invoiceCode);
	     Page<TenantInvoiceDetails> page = invoceDetRepo.findAll(pred, request);
		 return new DataTablesResult(request, page);
	}

	@Override
	public DataTablesResult<TenantInvoice> findActiveInvoices(DataTablesRequest request, String invoiceNumber,
			String firstName,String otherNames) throws IllegalAccessException {
		QTenantDef tenant = QTenantInvoice.tenantInvoice.tenant;
		Predicate pred1 = null;
		if (firstName == null || StringUtils.isBlank(firstName)) {
			firstName = "";
		}
		pred1 = tenant.fname.containsIgnoreCase(firstName);
		
		if (otherNames == null || StringUtils.isBlank(otherNames)) {
			otherNames = "";
		}
		
		pred1 = tenant.fname.containsIgnoreCase(firstName).and(tenant.otherNames.containsIgnoreCase(otherNames));
		
		if (invoiceNumber == null || StringUtils.isBlank(invoiceNumber)) {
			invoiceNumber = "";
		}
		
		pred1 = QTenantInvoice.tenantInvoice.currentStatus.eq("A").and(QTenantInvoice.tenantInvoice.invoiceNumber.containsIgnoreCase(invoiceNumber)).and(tenant.fname.containsIgnoreCase(firstName).and(tenant.otherNames.containsIgnoreCase(otherNames)));	
		 Page<TenantInvoice> page = invoiceRepo.findAll(pred1, request);
		 return new DataTablesResult(request, page);
	}

	@Override
	@Modifying
	@Transactional(readOnly = false)
	public Long reviseTransaction(RevisionForm revisionForm)  throws BadRequestException, InvocationTargetException, IllegalAccessException{
		if(revisionForm.getInvoiceId()==null || revisionForm.getInvoiceId()==null)
			throw new BadRequestException("Invoice to Revise cannot be Empty....");
		
		TenantInvoice invoice = invoiceRepo.findOne(revisionForm.getInvoiceId());
		
		List<TenantInvoiceDetails> details = invoice.getInvDetails();
		
		
		if(invoice==null) throw new BadRequestException("Invoice does not exist in the system..Select a valid invoice");
		
		List<TenantInvoiceDetails> destinationDetails = new ArrayList<>();
		
		TenantInvoice destination = new TenantInvoice();
		destination.setBranch(invoice.getBranch());
		destination.setFrequency(invoice.getFrequency());
		destination.setInstallmentAmount(invoice.getInstallmentAmount());
		destination.setInvoiceDate(new Date());
		destination.setPaymentMode(invoice.getPaymentMode());
		destination.setInvoiceNumber(invoice.getInvoiceNumber());
		destination.setInvAmount(invoice.getInvAmount());
		destination.setNetAmount(invoice.getNetAmount());
		destination.setTaxAmount(invoice.getTaxAmount());
		destination.setTenant(invoice.getTenant());
		destination.setTransCurrency(invoice.getTransCurrency());
		if("RV".equalsIgnoreCase(revisionForm.getRevisionType())){
			destination.setTransType("RV");
			destination.setWefDate(revisionForm.getEffectiveDate());
			Date wetDate = FormatUtils.addDays(FormatUtils.addMonths(revisionForm.getEffectiveDate(), FormatUtils.calculateFrequencyRate(invoice.getFrequency())),-1);
			destination.setWetDate(wetDate);
			destination.setRenewalDate(FormatUtils.addDays(wetDate, 1));
		}
		else if("CN".equalsIgnoreCase(revisionForm.getRevisionType())){
			destination.setTransType("CN");
			destination.setWetDate(new Date());
		}
		
		destination.setPreviousTrans(invoice);
		Long count = invoiceRepo.count(QTenantInvoice.tenantInvoice.invoiceNumber.eq(invoice.getInvoiceNumber()));
		destination.setInvoiceId(null);
		destination.setCurrentStatus("D");
		destination.setStatus("D");
		destination.setRevisionNumber(invoice.getInvoiceNumber()+"/"+(count+1));
		destination.setAuthBy(null);
		for(TenantInvoiceDetails det:details){
			TenantInvoiceDetails dest = new TenantInvoiceDetails();
			dest.setAmount(det.getAmount());
			dest.setCharge(det.getCharge());
			dest.setInvoice(destination);
			dest.setNetAmount(det.getNetAmount());
			dest.setRateType(det.getRateType());
			destinationDetails.add(dest);
		}
		destination.setInvDetails(destinationDetails);
		invoceDetRepo.save(destinationDetails);
		TenantInvoice saved =invoiceRepo.save(destination);
		return saved.getInvoiceId();	
		
	}

	


	
	

}
