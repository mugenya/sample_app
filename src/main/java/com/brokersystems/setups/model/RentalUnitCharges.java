package com.brokersystems.setups.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlTransient;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name="rental_units_charges")
public class RentalUnitCharges extends AuditBaseEntity {
	
	public RentalUnitCharges() {
		// TODO Auto-generated constructor stub
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="charge_id")
	private Long chargeId;
	
	@XmlTransient
	// @JsonIgnore
	@ManyToOne
	@JoinColumn(name="charge_group",nullable=false)
	private ChargeRatesGroup group;
	
	@XmlTransient
	// @JsonIgnore
	@ManyToOne
	@JoinColumn(name="charge_rate_type",nullable=false)
	private RateTypes rateType;
	
	@Column(name="charge_amount", nullable=false)
	private BigDecimal amount;
	
	@Column(name="charge_freq", nullable=false,length=20)
	private String frequency;
	
	@Column(name="charge_taxable")
	private boolean taxable;
	
	@Column(name="charge_tax_rate_type",length=1)
	private String taxRateType;
	
	@Column(name="charge_tax_type",length=5)
	private String taxType;
	
	@Column(name="charge_tax_value")
	private BigDecimal taxValue;
	
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	@Column(name="charge_wef", nullable=false)
	private Date wefDate;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy")
	@Temporal(TemporalType.DATE)
	@Column(name="charge_wet")
	private Date wetDate;
	
	@Column(name="charge_refundable")
	private boolean refundable;

	public Long getChargeId() {
		return chargeId;
	}

	public void setChargeId(Long chargeId) {
		this.chargeId = chargeId;
	}

	public String getTaxRateType() {
		return taxRateType;
	}

	public void setTaxRateType(String taxRateType) {
		this.taxRateType = taxRateType;
	}


	public RateTypes getRateType() {
		return rateType;
	}

	public void setRateType(RateTypes rateType) {
		this.rateType = rateType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getFrequency() {
		return frequency;
	}

	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}

	public boolean isTaxable() {
		return taxable;
	}

	public void setTaxable(boolean taxable) {
		this.taxable = taxable;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public BigDecimal getTaxValue() {
		return taxValue;
	}

	public void setTaxValue(BigDecimal taxValue) {
		this.taxValue = taxValue;
	}

	public Date getWefDate() {
		return wefDate;
	}

	public void setWefDate(Date wefDate) {
		this.wefDate = wefDate;
	}

	public Date getWetDate() {
		return wetDate;
	}

	public void setWetDate(Date wetDate) {
		this.wetDate = wetDate;
	}

	public boolean isRefundable() {
		return refundable;
	}

	public void setRefundable(boolean refundable) {
		this.refundable = refundable;
	}

	public ChargeRatesGroup getGroup() {
		return group;
	}

	public void setGroup(ChargeRatesGroup group) {
		this.group = group;
	}
	
	
	

}