package com.scankart.app.feature.order;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scankart.app.dto.RestResponse;


@Service("DefaultOrderService")
public class DefaultOrderService implements OrderService{

	@Autowired
	OrderComponent orderComponent;

	@Override
	public RestResponse createOrder(HashMap<String,Object> orderDetails, String apiKey) {
		// TODO Auto-generated method stub
		return orderComponent.createOrder(orderDetails, apiKey);
	}
	
	@Override
	public RestResponse createOrderByMerchant(HashMap<String,Object> orderDetails, String apiKey) {
		// TODO Auto-generated method stub
		return orderComponent.createOrderByMerchant(orderDetails, apiKey);
	}
	
	@Override
	public RestResponse updateOrderByMerchant(HashMap<String,Object> orderDetails, String apiKey) {
		// TODO Auto-generated method stub
		return orderComponent.updateOrderByMerchant(orderDetails, apiKey);
	}
	
	@Override
	public RestResponse getOrdersByMerchant(int merchantId, String status, String apikey, int limitCount) {
		// TODO Auto-generated method stub
		return orderComponent.getOrderDetailsByMerchant(merchantId, status, apikey, limitCount);
	}
	
	@Override
	public RestResponse getOrderDetailsByCustomer(int userId, String status, String apikey, int limitCount) {
		// TODO Auto-generated method stub
		return orderComponent.getOrderDetailsByCustomer(userId, status, apikey, limitCount);
	}
	
	@Override
	public RestResponse searchOrderByMerchant(int merchantId, String status, String apikey,String value) {
		// TODO Auto-generated method stub
		return orderComponent.searchOrderByMerchant(merchantId, status, apikey, value);
	}
	
	@Override
	public RestResponse confirmOrderByMerchant(HashMap<String,Object> orderDetails,String apiKey) {
		// TODO Auto-generated method stub
		return orderComponent.confirmOrderByMerchant(orderDetails,apiKey);
	}
	
	@Override
	public RestResponse confirmPayment(HashMap<String,Object> orderDetails,String apiKey) {
		// TODO Auto-generated method stub
		return orderComponent.confirmPayment(orderDetails,apiKey);
	}
	
	@Override
	public RestResponse cancelOrder(HashMap<String,Object> orderDetails,String apiKey) {
		// TODO Auto-generated method stub
		return orderComponent.cancelOrder(orderDetails,apiKey);
	}
	
	@Override
	public RestResponse cancelOrderByMerchant(HashMap<String,Object> orderDetails,String apiKey) {
		// TODO Auto-generated method stub
		return orderComponent.cancelOrderByMerchant(orderDetails,apiKey);
	}

	@Override
	public RestResponse healthCheck() {
		// TODO Auto-generated method stub
		RestResponse response = new RestResponse();
		response.setResponse("Up and Running");
		return response;
	}
	
	
}
