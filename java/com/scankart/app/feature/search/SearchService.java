package com.scankart.app.feature.search;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import com.scankart.app.dto.RestResponse;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;

@CrossOrigin
@RestController
public interface SearchService 
{
    
    @RequestMapping(value ="searchStore/{userId}/{type}/{latitude}/{longitude}/{limitCount}" , method =  RequestMethod.GET , headers =  "Accept=application/json" )
    public RestResponse searchStore(@PathVariable("type") String type, @PathVariable("userId") int userId, @RequestHeader(value="api-key") String apiKey,@PathVariable("latitude") String latitude,@PathVariable("longitude") String longitude,@PathVariable("limitCount") int limitCount);
    
    @RequestMapping(value ="getDetailsByCode/{merchantId}/{userId}/{attrName}/{barCode}" , method =  RequestMethod.GET , headers =  "Accept=application/json" )
    public RestResponse getDetailsByCode(@PathVariable("attrName") String attrName, @PathVariable("merchantId") int merchantId,@PathVariable("barCode") String barCode, @RequestHeader(value="api-key") String apiKey, @PathVariable("userId") int userId);
    
    @RequestMapping(value ="getDetailsByCodeByMerchant/{merchantId}/{attrName}/{barCode}" , method =  RequestMethod.GET , headers =  "Accept=application/json" )
    public RestResponse getDetailsByCodeByMerchant(@PathVariable("attrName") String attrName, @PathVariable("merchantId") int merchantId,@PathVariable("barCode") String barCode, @RequestHeader(value="api-key") String apiKey);
    
    @RequestMapping(value ="searchStoreByName/{userId}/{name}/{latitude}/{longitude}/{limitCount}" , method =  RequestMethod.GET , headers =  "Accept=application/json" )
    public RestResponse searchStoreByName(@PathVariable("name") String name, @PathVariable("userId") int userId, @RequestHeader(value="api-key") String apiKey,@PathVariable("latitude") String latitude,@PathVariable("longitude") String longitude,@PathVariable("limitCount") int limitCount);
        
}
