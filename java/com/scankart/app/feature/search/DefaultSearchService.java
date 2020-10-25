package com.scankart.app.feature.search;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scankart.app.dto.RestResponse;


@Service("DefaultSearchService")
public class DefaultSearchService implements SearchService{

	@Autowired
	SearchComponent searchComponent;

	@Override
	public RestResponse searchStore(String type,int userId, String apiKey,String latitude, String longitude, int limitCount) {
		// TODO Auto-generated method stub
		return searchComponent.searchStore(type,userId, apiKey,latitude,longitude,limitCount);
	}
	
	@Override
	public RestResponse searchStoreByName(String name,int userId, String apiKey,String latitude, String longitude, int limitCount) {
		// TODO Auto-generated method stub
		return searchComponent.searchStoreByName(name,userId, apiKey,latitude,longitude,limitCount);
	}
	
	@Override
	public RestResponse getDetailsByCode(String attrName,int merchantId,String barCode, String apiKey, int userId) {
		// TODO Auto-generated method stub
		return searchComponent.getDetailsByCode(attrName, merchantId, barCode, apiKey, userId);
	}

	@Override
	public RestResponse getDetailsByCodeByMerchant(String attrName,int merchantId,String barCode, String apiKey) {
		// TODO Auto-generated method stub
		return searchComponent.getDetailsByCodeByMerchant(attrName, merchantId, barCode, apiKey);
	}
	
}
