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
public class AllOrderItemsDto  {

	private int id;

	private String name;
	
	private String quantity;
	
	private String price;
		
}