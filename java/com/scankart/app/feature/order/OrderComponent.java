package com.scankart.app.feature.order;

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

public interface OrderComponent 
{
    
    public RestResponse createOrder(HashMap<String,Object> orderDetails, String apiKey);
    
    public RestResponse createOrderByMerchant(HashMap<String,Object> orderDetails, String apiKey);
    
    public RestResponse updateOrderByMerchant(HashMap<String,Object> orderDetails, String apiKey);
    
    public RestResponse getOrderDetailsByMerchant(int merchantId, String status, String apiKey, int limitCount);
    
    public RestResponse getOrderDetailsByCustomer(int userId, String status, String apiKey, int limitCount);
    
    public RestResponse confirmOrderByMerchant(HashMap<String,Object> orderDetails, String apiKey);
    
    public RestResponse searchOrderByMerchant(int merchantId, String status, String apiKey, String value);
    
    public RestResponse confirmPayment(HashMap<String,Object> orderDetails,String apiKey);
    
    public RestResponse cancelOrder(HashMap<String,Object> orderDetails,String apiKey);
    
    public RestResponse cancelOrderByMerchant(HashMap<String,Object> orderDetails,String apiKey);
}
