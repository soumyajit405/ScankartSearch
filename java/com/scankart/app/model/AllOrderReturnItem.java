package com.scankart.app.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the all_order_return_items database table.
 * 
 */
@Entity
@Table(name="all_order_return_items")
@NamedQuery(name="AllOrderReturnItem.findAll", query="SELECT a FROM AllOrderReturnItem a")
public class AllOrderReturnItem implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private int id;

	//bi-directional many-to-one association to AllOrder
	@ManyToOne
	@JoinColumn(name="order_id")
	private AllOrder allOrder;

	//bi-directional many-to-one association to AllInventory
	@ManyToOne
	@JoinColumn(name="inv_id")
	private AllInventory allInventory;

	public AllOrderReturnItem() {
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public AllOrder getAllOrder() {
		return this.allOrder;
	}

	public void setAllOrder(AllOrder allOrder) {
		this.allOrder = allOrder;
	}

	public AllInventory getAllInventory() {
		return this.allInventory;
	}

	public void setAllInventory(AllInventory allInventory) {
		this.allInventory = allInventory;
	}

}