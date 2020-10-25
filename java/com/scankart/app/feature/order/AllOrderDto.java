package com.scankart.app.feature.order;


import java.io.Serializable;
import javax.persistence.*;

import com.scankart.app.model.AllMerchantAttribute;

import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@Setter
@Getter
public class AllOrderDto  {

	private int id;

	private String userName;
	
	private Timestamp purchaseDate;
	
	private List<AllOrderAttrDto> listOfOrderAttr;
	
	private List<AllOrderItemsDto> listOfItems;
	
	private String orderType;
	
	private String status;
	
	private int userId;
	
	private String merchantName;
	
	private String merchantId;
	
}