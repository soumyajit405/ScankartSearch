package com.scankart.app.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the all_order_return database table.
 * 
 */
@Entity
@Table(name="all_order_return")
@NamedQuery(name="AllOrderReturn.findAll", query="SELECT a FROM AllOrderReturn a")
public class AllOrderReturn implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	private float amount;

	private String status;

	private float tax;

	//bi-directional many-to-one association to AllOrder
	@ManyToOne
	@JoinColumn(name="order_id")
	private AllOrder allOrder;

	public AllOrderReturn() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public float getAmount() {
		return this.amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public float getTax() {
		return this.tax;
	}

	public void setTax(float tax) {
		this.tax = tax;
	}

	public AllOrder getAllOrder() {
		return this.allOrder;
	}

	public void setAllOrder(AllOrder allOrder) {
		this.allOrder = allOrder;
	}

}