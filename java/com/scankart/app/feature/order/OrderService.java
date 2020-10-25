package com.scankart.app.feature.order;

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
public interface OrderService 
{
    
    @RequestMapping(value ="createOrder" , method =  RequestMethod.POST , headers =  "Accept=application/json" )
    public RestResponse createOrder(@RequestBody HashMap<String,Object> orderDetails, @RequestHeader(value="api-key") String apiKey);
    
    @RequestMapping(value ="createOrderByMerchant" , method =  RequestMethod.POST , headers =  "Accept=application/json" )
    public RestResponse createOrderByMerchant(@RequestBody HashMap<String,Object> orderDetails, @RequestHeader(value="api-key") String apiKey);
    
    @RequestMapping(value ="updateOrderByMerchant" , method =  RequestMethod.POST , headers =  "Accept=application/json" )
    public RestResponse updateOrderByMerchant(@RequestBody HashMap<String,Object> orderDetails, @RequestHeader(value="api-key") String apiKey);
    
    @RequestMapping(value ="getOrdersByMerchant/{merchantId}/{status}/{limitCount}" , method =  RequestMethod.GET , headers =  "Accept=application/json" )
    public RestResponse getOrdersByMerchant(@PathVariable("merchantId") int id, @PathVariable("status") String status, @RequestHeader(value="api-key") String apiKey,@PathVariable("limitCount") int  limitCount);
    
    @RequestMapping(value ="getOrderDetailsByCustomer/{userId}/{status}/{limitCount}" , method =  RequestMethod.GET , headers =  "Accept=application/json" )
    public RestResponse getOrderDetailsByCustomer(@PathVariable("userId") int id, @PathVariable("status") String status, @RequestHeader(value="api-key") String apiKey,@PathVariable("limitCount") int  limitCount);
    
    @RequestMapping(value ="searchOrderByMerchant/{merchantId}/{status}/{value}" , method =  RequestMethod.GET , headers =  "Accept=application/json" )
    public RestResponse searchOrderByMerchant(@PathVariable("merchantId") int id, @PathVariable("status") String status, @RequestHeader(value="api-key") String apiKey, @PathVariable("value") String value);
    
    @RequestMapping(value ="health" , method =  RequestMethod.GET , headers =  "Accept=application/json" )
    public RestResponse healthCheck();
    
    @RequestMapping(value ="confirmOrderByMerchant" , method =  RequestMethod.POST , headers =  "Accept=application/json" )
    public RestResponse confirmOrderByMerchant(@RequestBody HashMap<String,Object> orderDetails, @RequestHeader(value="api-key") String apiKey);
    
    @RequestMapping(value ="confirmPayment" , method =  RequestMethod.POST , headers =  "Accept=application/json" )
    public RestResponse confirmPayment(@RequestBody HashMap<String,Object> orderDetails, @RequestHeader(value="api-key") String apiKey);
    
    @RequestMapping(value ="cancelOrder" , method =  RequestMethod.PUT , headers =  "Accept=application/json" )
    public RestResponse cancelOrder(@RequestBody HashMap<String,Object> orderDetails, @RequestHeader(value="api-key") String apiKey);
    
    @RequestMapping(value ="cancelOrderByMerchant" , method =  RequestMethod.PUT , headers =  "Accept=application/json" )
    public RestResponse cancelOrderByMerchant(@RequestBody HashMap<String,Object> orderDetails, @RequestHeader(value="api-key") String apiKey);
}
