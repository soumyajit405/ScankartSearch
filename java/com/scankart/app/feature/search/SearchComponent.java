package com.scankart.app.feature.search;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import com.scankart.app.dto.RestResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;

public interface SearchComponent 
{
    
    public RestResponse searchStore(String type,int userId, String apiKey,String latitude, String longitude, int limitCount);
    
    public RestResponse searchStoreByName(String name,int userId, String apiKey,String latitude, String longitude, int limitCount);
    
    public RestResponse getDetailsByCode(String attrName,int merchantId,String barCode, String apiKey, int userId);
    
    public RestResponse getDetailsByCodeByMerchant(String attrName,int merchantId,String barCode, String apiKey);
        
}
