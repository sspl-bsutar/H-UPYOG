package org.egov.model.bills;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.egov.model.masters.PurchaseOrder;

@Entity
@Table(name="eg_billregisterwithpurchaseitems")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(name = EgBillPurchaseItemsDetails.SEQ_EG_BILLREGISTERWITHPURCHASEITEMS, sequenceName = EgBillPurchaseItemsDetails.SEQ_EG_BILLREGISTERWITHPURCHASEITEMS, allocationSize = 1)
public class EgBillPurchaseItemsDetails implements java.io.Serializable{
	
	public static final String SEQ_EG_BILLREGISTERWITHPURCHASEITEMS = "seq_eg_billregisterwithpurchaseitems";
	
	private static final long serialVersionUID = -4312140421386028968L;
	
	@Id
	@GeneratedValue(generator = SEQ_EG_BILLREGISTERWITHPURCHASEITEMS, strategy = GenerationType.SEQUENCE)
	private Long id;
	
	@ManyToOne()
	@JoinColumn(name = "billregister_id", referencedColumnName = "billnumber")	
	private EgBillregister egBillregister;
	
	@Column(name="itemcode")
	private String itemCode;
	@Column(name="unitrate")
	private Long unitRate;
	@Column(name="quantity")
	private Integer quantity;
	@Column(name="amount")
	private Double amount;
	@Column(name="ordernumber")
	private String ordernumber;
	@Column(name="createdby")
	private Long createdby;
	@Column(name="lastmodifiedby")
	private Long lastmodifiedby;
	@Column (name="gstrate")
	private Long gstRate;
	@Column (name="unitvaluewithgst")
	private double unitValueWithGst;
	
	
	@ManyToOne()
	@JoinColumn(name = "purchaseorder_id", referencedColumnName = "id")	
	private PurchaseOrder purchaseOrder;
	
	
	public PurchaseOrder getPurchaseOrder() {
		return purchaseOrder;
	}
	public void setPurchaseOrder(PurchaseOrder purchaseOrder) {
		this.purchaseOrder = purchaseOrder;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public EgBillregister getEgBillregister() {
		return egBillregister;
	}
	public void setEgBillregister(EgBillregister egBillregister) {
		this.egBillregister = egBillregister;
	}
	public String getItemCode() {
		return itemCode;
	}
	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
	
	
	public Long getUnitRate() {
		return unitRate;
	}
	public void setUnitRate(Long unitRate) {
		this.unitRate = unitRate;
	}
	public Integer getQuantity() {
		return quantity;
	}
	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}
	public Double getAmount() {
		return amount;
	}
	public void setAmount(Double amount) {
		this.amount = amount;
	}
	public String getOrdernumber() {
		return ordernumber;
	}
	public void setOrdernumber(String ordernumber) {
		this.ordernumber = ordernumber;
	}
	
	public Long getCreatedby() {
		return createdby;
	}
	public void setCreatedby(Long createdby) {
		this.createdby = createdby;
	}
	public Long getLastmodifiedby() {
		return lastmodifiedby;
	}
	public void setLastmodifiedby(Long lastmodifiedby) {
		this.lastmodifiedby = lastmodifiedby;
	}
	
	public Long getGstRate() {
		return gstRate;
	}
	public void setGstRate(Long gstRate) {
		this.gstRate = gstRate;
	}
	
	public double getUnitValueWithGst() {
		return unitValueWithGst;
	}
	public void setUnitValueWithGst(double unitValueWithGst) {
		this.unitValueWithGst = unitValueWithGst;
	}

	
	public EgBillPurchaseItemsDetails() {
		// TODO Auto-generated constructor stub
	}
	
	

}
