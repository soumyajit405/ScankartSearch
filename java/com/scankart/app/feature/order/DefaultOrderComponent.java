
package com.scankart.app.feature.order;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.scankart.app.AppStartupRunner;
import com.scankart.app.dto.ErrorResponse;
import com.scankart.app.dto.RestResponse;
import com.scankart.app.model.AllInstoreOrderItem;
import com.scankart.app.model.AllInstoreOrderStatus;
import com.scankart.app.model.AllInventory;
import com.scankart.app.model.AllMerchant;
import com.scankart.app.model.AllMerchantAttribute;
import com.scankart.app.model.AllMerchantAttributeRepository;
import com.scankart.app.model.AllMerchantRepository;
import com.scankart.app.model.AllOrder;
import com.scankart.app.model.AllOrderAttr;
import com.scankart.app.model.AllOrderAttrRepository;
import com.scankart.app.model.AllOrderItemsRepository;
import com.scankart.app.model.AllOrderRepository;
import com.scankart.app.model.AllOrderStatusRepository;
import com.scankart.app.model.AllOtp;
import com.scankart.app.model.AllUser;
import com.scankart.app.model.AllUserLogin;
import com.scankart.app.model.AllUserLoginRepository;
import com.scankart.app.model.AllUserRepository;
import com.scankart.app.model.LoginType;
import com.scankart.app.model.OfferRepository;
import com.scankart.app.model.OrderStatusPl;
import com.scankart.app.model.OrderTypePl;
import com.scankart.app.model.UserOffer;
import com.scankart.app.model.UserOfferTx;
import com.scankart.app.model.UserOfferTxRepository;
import com.scankart.app.util.CommonUtility;
import com.scankart.app.util.CustomMessages;
import com.scankart.app.util.NotificationGenerator;
import com.scankart.app.util.NotificationGeneratorImpl;
import com.scankart.app.util.NotificationGeneratorImplA;
import com.scankart.app.util.ValidatorUtility;


@Transactional
@Repository
@Service("DefaultOrderComponent")
public class DefaultOrderComponent implements OrderComponent{


	@Autowired 
	AllUserRepository allUserRepo;

	@Autowired 
	AllUserLoginRepository allUserLoginRepo;

	@Autowired 
	AllMerchantRepository allMerchantRepo;
	
	@Autowired 
	AllMerchantAttributeRepository allMerchantAttrRepo;
	
	@Autowired 
	AllOrderRepository allOrderRepo;
	
	@Autowired 
	AllOrderStatusRepository allOrderStatusRepo;
	

	@Autowired 
	AllOrderItemsRepository allOrderItemRepo;
	
	@Autowired 
	AllOrderAttrRepository allOrderAttrRepo;
	
	@Autowired 
	ValidatorUtility validatorUtil;
	
	@Autowired
	OfferRepository offerRepo;
	
	@Autowired
	UserOfferTxRepository userOfferTxRepo;
	
	@Autowired
	RestTemplate restTemplate;
	
	@Override
	public RestResponse createOrder(HashMap<String,Object> orderDetails,String apiKey) {
		
		RestResponse response = new RestResponse();
		try {
			HashMap<String, Object> validationMessage = new HashMap<String, Object>();
			
			int merchantId = (Integer) orderDetails.get("merchantId");
			int userId = (Integer) orderDetails.get("userId");
			int validationStatus = validatorUtil.validateUser(apiKey, userId, 1);
			if (validationStatus == 0 || validationStatus == -1) {
				validationMessage.put("message", CustomMessages.getCustomMessages("UAU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			AllUser user = allUserRepo.getUserById(userId);
			AllMerchant merchant = allUserRepo.getMerchantById(merchantId);
			if (user == null || merchant == null ) {
				validationMessage.put("message", CustomMessages.getCustomMessages("NSU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);	
				return response;
			}
			List<HashMap<String,String>> listOfItems = (List<HashMap<String,String>>)orderDetails.get("items");
			List<HashMap<String,String>> listOfAttr = (List<HashMap<String,String>>)orderDetails.get("attr");
			int orderCount = allOrderRepo.findOrderCount()+1;
			OrderTypePl orderType = allOrderRepo.getOrderType("Instore Online Shopping");
			AllOrder allorder = new AllOrder();
			allorder.setId(orderCount);
			allorder.setAllMerchant(merchant);
			allorder.setAllUser(allUserRepo.getUserById(userId));
			allorder.setPurchaseDate(new Timestamp(System.currentTimeMillis()));
			allorder.setOrderType(orderType);
			allorder.setStatus("CURRENT");
			allOrderRepo.saveAndFlush(allorder);
			
			OrderStatusPl orderStatus = allOrderStatusRepo.getOrderStatusPl("CURRENT");
			AllInstoreOrderStatus allOrderStatus = new AllInstoreOrderStatus();
			allOrderStatus.setOrderStatusPl(orderStatus);
			allOrderStatus.setAllOrder(allorder);
			allOrderStatusRepo.saveAndFlush(allOrderStatus);
			
			float totalPrice=0;
			int itemCount = allOrderItemRepo.getCountOfOrderItems()+1;
			for (int i =0;i<listOfItems.size();i++) {
				HashMap<String,String> item = (HashMap<String,String>)listOfItems.get(i);
				AllInstoreOrderItem allOrderitem = new AllInstoreOrderItem();
				allOrderitem.setAddedBy("User");
				allOrderitem.setId(itemCount);
				allOrderitem.setMerchantInvId(item.get("id"));
				allOrderitem.setQuantity(Integer.parseInt(item.get("quantity")));
				allOrderitem.setTotalPrice(Float.parseFloat(item.get("price")));
				totalPrice = totalPrice +Float.parseFloat(item.get("price"));
				allOrderitem.setAllOrder(allorder);
				itemCount++;
				allOrderItemRepo.saveAndFlush(allOrderitem);
			}
			
			int attrCount = allOrderAttrRepo.getCountOfOrderAttr()+1;
			for (int i =0;i<listOfAttr.size();i++) {
				HashMap<String,String> attr = (HashMap<String,String>)listOfAttr.get(i);
				AllOrderAttr allOrderAttr = new AllOrderAttr();
				allOrderAttr.setAllOrder(allorder);
				allOrderAttr.setId(attrCount);
				allOrderAttr.setOrderAttributeTypePl(allOrderAttrRepo.getOrderAttrByName(listOfAttr.get(i).get("name")));
				allOrderAttr.setValue(listOfAttr.get(i).get("value"));
				attrCount++;
				allOrderAttrRepo.saveAndFlush(allOrderAttr);
			}
			attrCount++;
			CommonUtility cm = new CommonUtility();
			String otp = cm.generateOTPCode();
			AllOrderAttr allOrderAttr = new AllOrderAttr();
			allOrderAttr.setAllOrder(allorder);
			allOrderAttr.setId(attrCount);
			allOrderAttr.setOrderAttributeTypePl(allOrderAttrRepo.getOrderAttrByName("CONFIRM_OTP"));
			allOrderAttr.setValue(otp);
			allOrderAttrRepo.saveAndFlush(allOrderAttr);
			
			//Generate Notifications 
			ArrayList<String> players = allMerchantRepo.getAllPlayersForMErchant(merchant.getId());
			if (players.size() > 0) {
			  NotificationGeneratorImpl obj = new NotificationGeneratorImpl(); 
		        NotificationGenerator ngenerator = new NotificationGeneratorImplA(); 
		        obj.registerNotificationGenerator(ngenerator); 
		        obj.sendNotificationToMerchant("New Order. Please check :",orderCount ,players); 
			}
			
			validationMessage.put("message", CustomMessages.getCustomMessages("AS"));
			validationMessage.put("key", 200);
			validationMessage.put("confirmOTP", otp);
			validationMessage.put("orderId", orderCount);
			response.setResponse(validationMessage);
					
			} catch( Exception e) {
			e.printStackTrace();
			ErrorResponse errorResponse = new ErrorResponse(CustomMessages.getCustomMessages("ISE"), 500);
			response.setResponse(errorResponse);
		}
		return response;
	}
	
	
	@Override
	public RestResponse updateOrderByMerchant(HashMap<String,Object> orderDetails,String apiKey) {
		
		RestResponse response = new RestResponse();
		try {
			HashMap<String, Object> validationMessage = new HashMap<String, Object>();
			
			int merchantId = (Integer) orderDetails.get("merchantId");
			int userId = (Integer) orderDetails.get("userId");
			int validationStatus = validatorUtil.validateUser(apiKey, merchantId, 2);
			if (validationStatus == 0 || validationStatus == -1) {
				validationMessage.put("message", CustomMessages.getCustomMessages("UAU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			AllUser user = allUserRepo.getUserById(userId);
			AllMerchant merchant = allUserRepo.getMerchantById(merchantId);
			if (user == null || merchant == null ) {
				validationMessage.put("message", CustomMessages.getCustomMessages("NSU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);	
				return response;
			}
			List<HashMap<String,String>> listOfItems = (List<HashMap<String,String>>)orderDetails.get("items");
			List<HashMap<String,String>> listOfAttr = (List<HashMap<String,String>>)orderDetails.get("attr");
			AllOrder allorder = allOrderRepo.getOrderDetails((Integer) orderDetails.get("orderId"));
			float totalPrice=0;
			int itemCount = allOrderItemRepo.getCountOfOrderItems()+1;
			allOrderItemRepo.deleteOrderItems(allorder.getId());
			for (int i =0;i<listOfItems.size();i++) {
				HashMap<String,String> item = (HashMap<String,String>)listOfItems.get(i);
				AllInstoreOrderItem allOrderitem = new AllInstoreOrderItem();
				allOrderitem.setAddedBy("User");
				allOrderitem.setId(itemCount);
				allOrderitem.setMerchantInvId(item.get("id"));
				allOrderitem.setQuantity(Integer.parseInt(item.get("quantity")));
				allOrderitem.setTotalPrice(Float.parseFloat(item.get("price")));
				totalPrice = totalPrice +Float.parseFloat(item.get("price"));
				allOrderitem.setAllOrder(allorder);
				itemCount++;
				allOrderItemRepo.saveAndFlush(allOrderitem);
			}
			
			int attrCount = allOrderAttrRepo.getCountOfOrderAttr()+1;
			for (int i =0;i<listOfAttr.size();i++) {
				HashMap<String,String> attr = (HashMap<String,String>)listOfAttr.get(i);
				if (attr.get("name").equalsIgnoreCase("TOTAL_AMOUNT")) {
					AllOrderAttr allOrderAttr = allOrderAttrRepo.getOrderAttrByName(allorder.getId(), attr.get("name"));
					allOrderAttrRepo.updateOrderAttr(allorder.getId(), allOrderAttr.getId(),Float.toString(totalPrice));
				}
			}
			
			validationMessage.put("message", CustomMessages.getCustomMessages("AS"));
			validationMessage.put("key", 200);
			response.setResponse(validationMessage);
					
			} catch( Exception e) {
			e.printStackTrace();
			ErrorResponse errorResponse = new ErrorResponse(CustomMessages.getCustomMessages("ISE"), 500);
			response.setResponse(errorResponse);
		}
		return response;
	}
	
	@Override
	public RestResponse confirmPayment(HashMap<String,Object> orderDetails,String apiKey) {
		
		RestResponse response = new RestResponse();
		try {
			HashMap<String, Object> validationMessage = new HashMap<String, Object>();
			
			//int merchantId = (Integer) orderDetails.get("merchantId");
			int userId = (Integer) orderDetails.get("userId");
			AllOrder allorder = null;
			int validationStatus = validatorUtil.validateUser(apiKey, userId, 1);
			if (validationStatus == 0 || validationStatus == -1) {
				validationMessage.put("message", CustomMessages.getCustomMessages("UAU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			AllUser user = allUserRepo.getUserById(userId);
			// AllMerchant merchant = allUserRepo.getMerchantById(merchantId);
			if (user == null ) {
				validationMessage.put("message", CustomMessages.getCustomMessages("NSU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);	
				return response;
			}
			//List<HashMap<String,String>> listOfItems = (List<HashMap<String,String>>)orderDetails.get("items");
			List<HashMap<String,String>> listOfAttr = (List<HashMap<String,String>>)orderDetails.get("attr");
			 allorder = allOrderRepo.getOrderDetails((Integer) orderDetails.get("orderId"));
			//float totalPrice=(float) orderDetails.get("totalPrice");
			int offerId = (int)orderDetails.get("OFFER_ID");
			int attrCount = allOrderAttrRepo.getCountOfOrderAttr()+1;
			if (offerId == -1) {
				for (int i =0;i<listOfAttr.size();i++) {
					HashMap<String,String> attr = (HashMap<String,String>)listOfAttr.get(i);
					if (attr.get("name").equalsIgnoreCase("TOTAL_AMOUNT")) {
						AllOrderAttr allOrderAttr = allOrderAttrRepo.getOrderAttrByName(allorder.getId(), attr.get("name"));
						allOrderAttrRepo.updateOrderAttr(allorder.getId(), allOrderAttr.getId(),attr.get("value"));
						String payTxId =(String) orderDetails.get("PAY_TXID");
						String payType = (String) orderDetails.get("PAY_TYPE");
						AllOrderAttr orderAttr = new AllOrderAttr();
						orderAttr.setAllOrder(allorder);
						orderAttr.setId(attrCount);
						orderAttr.setOrderAttributeTypePl(allOrderAttrRepo.getOrderAttrByName("PAY_TXID"));
						orderAttr.setValue(payTxId);
						attrCount++;
						allOrderAttrRepo.saveAndFlush(allOrderAttr);
						 orderAttr = new AllOrderAttr();
						orderAttr.setAllOrder(allorder);
						orderAttr.setId(attrCount);
						orderAttr.setOrderAttributeTypePl(allOrderAttrRepo.getOrderAttrByName("PAY_TYPE"));
						orderAttr.setValue(payType);
						allOrderAttrRepo.saveAndFlush(allOrderAttr);
						attrCount++;
					}
				}	
			} else {
				for (int i =0;i<listOfAttr.size();i++) {
					HashMap<String,String> attr = (HashMap<String,String>)listOfAttr.get(i);
					if (attr.get("name").equalsIgnoreCase("TOTAL_AMOUNT")) {
						AllOrderAttr allOrderAttr = allOrderAttrRepo.getOrderAttrByName(allorder.getId(), attr.get("name"));
						allOrderAttrRepo.updateOrderAttr(allorder.getId(), allOrderAttr.getId(),attr.get("value"));
						String payTxId =(String) orderDetails.get("PAY_TXID");
						String payType = (String) orderDetails.get("PAY_TYPE");
						AllOrderAttr orderAttr = new AllOrderAttr();
						orderAttr.setAllOrder(allorder);
						orderAttr.setId(attrCount);
						orderAttr.setOrderAttributeTypePl(allOrderAttrRepo.getOrderAttrByName("PAY_TXID"));
						orderAttr.setValue(payTxId);
						attrCount++;
						allOrderAttrRepo.saveAndFlush(allOrderAttr);
						 orderAttr = new AllOrderAttr();
						orderAttr.setAllOrder(allorder);
						orderAttr.setId(attrCount);
						orderAttr.setOrderAttributeTypePl(allOrderAttrRepo.getOrderAttrByName("PAY_TYPE"));
						orderAttr.setValue(payType);
						allOrderAttrRepo.saveAndFlush(allOrderAttr);
						attrCount++;
					} else {
						AllOrderAttr orderAttr = new AllOrderAttr();
						orderAttr.setAllOrder(allorder);
						orderAttr.setId(attrCount);
						orderAttr.setOrderAttributeTypePl(allOrderAttrRepo.getOrderAttrByName(listOfAttr.get(i).get("name")));
						orderAttr.setValue(listOfAttr.get(i).get("value"));
						attrCount++;
						allOrderAttrRepo.saveAndFlush(orderAttr);
					}
				}
				UserOffer userOffer = offerRepo.getUserOffer(offerId);
				UserOfferTx userOfferTx = new UserOfferTx();
				userOfferTx.setAllUser(user);
				userOfferTx.setOffer(userOffer.getOffer());
				userOfferTx.setStatus("ACT");
				userOfferTxRepo.saveAndFlush(userOfferTx);
			      
				
			}
			
			Object js = generateCoupon(userId,allorder.getAllMerchant().getId());
			JSONObject offerReceived = (JSONObject)js;
			ArrayList<String> players = allMerchantRepo.getAllPlayersForMErchant(allorder.getAllMerchant().getId());
			if (players.size() > 0) {
			  NotificationGeneratorImpl obj = new NotificationGeneratorImpl(); 
		        NotificationGenerator ngenerator = new NotificationGeneratorImplA(); 
		        obj.registerNotificationGenerator(ngenerator); 
		        obj.sendNotificationToMerchant("Order Paid. Please check :",allorder.getId() ,players);
			}
			validationMessage.put("message", CustomMessages.getCustomMessages("AS"));
			validationMessage.put("data", offerReceived.toString());
			validationMessage.put("key", 200);
			response.setResponse(validationMessage);
					
			} catch( Exception e) {
			e.printStackTrace();
			ErrorResponse errorResponse = new ErrorResponse(CustomMessages.getCustomMessages("ISE"), 500);
			response.setResponse(errorResponse);
		}
		return response;
	}
	
	@Override
	public RestResponse cancelOrder(HashMap<String,Object> orderDetails,String apiKey) {
		
		RestResponse response = new RestResponse();
		try {
			HashMap<String, Object> validationMessage = new HashMap<String, Object>();
			
			//int merchantId = (Integer) orderDetails.get("merchantId");
			int userId = (Integer) orderDetails.get("userId");
			AllOrder allorder = null;
			int validationStatus = validatorUtil.validateUser(apiKey, userId, 1);
			if (validationStatus == 0 || validationStatus == -1) {
				validationMessage.put("message", CustomMessages.getCustomMessages("UAU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			AllUser user = allUserRepo.getUserById(userId);
			// AllMerchant merchant = allUserRepo.getMerchantById(merchantId);
			if (user == null ) {
				validationMessage.put("message", CustomMessages.getCustomMessages("NSU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);	
				return response;
			}
			allorder = allOrderRepo.getOrderDetails((Integer) orderDetails.get("orderId"));
			if (allorder.getStatus().equalsIgnoreCase("COMPLETED") || allorder.getStatus().equalsIgnoreCase("PAID") || allorder.getStatus().equalsIgnoreCase("CANCELLED BY CUSTOMER") || allorder.getStatus().equalsIgnoreCase("CANCELLED BY MERCHANT"))
			{
				validationMessage.put("message", CustomMessages.getCustomMessages("OC"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);	
				return response;
			}
			allOrderRepo.updateOrderStatus("CANCELLED BY USER", allorder.getId());
			OrderStatusPl cancelStatus = allOrderStatusRepo.getOrderStatusPl("CANCELLED BY USER");
			AllInstoreOrderStatus instoreOrderStatus = new AllInstoreOrderStatus();
			instoreOrderStatus.setAllOrder(allorder);
			instoreOrderStatus.setDate(new Timestamp(System.currentTimeMillis()));
			instoreOrderStatus.setOrderStatusPl(cancelStatus);
			allOrderStatusRepo.saveAndFlush(instoreOrderStatus);
			
			int attrCount = allOrderAttrRepo.getCountOfOrderAttr()+1;
			AllOrderAttr allOrderAttr = new AllOrderAttr();
			allOrderAttr.setAllOrder(allorder);
			allOrderAttr.setId(attrCount);
			allOrderAttr.setOrderAttributeTypePl(allOrderAttrRepo.getOrderAttrByName("CANCEL_REASON"));
			allOrderAttr.setValue((String)orderDetails.get("cancelReason"));
			allOrderAttrRepo.saveAndFlush(allOrderAttr);
			attrCount++;
			 allOrderAttr = new AllOrderAttr();
			 allOrderAttr.setId(attrCount);
				allOrderAttr.setAllOrder(allorder);
				allOrderAttr.setOrderAttributeTypePl(allOrderAttrRepo.getOrderAttrByName("CANCEL_DESCRIPTION"));
				allOrderAttr.setValue((String)orderDetails.get("cancelDescription"));
				allOrderAttrRepo.saveAndFlush(allOrderAttr);
			
			ArrayList<String> players = allMerchantRepo.getAllPlayersForMErchant(allorder.getAllMerchant().getId());
			if (players.size() > 0) {
			  NotificationGeneratorImpl obj = new NotificationGeneratorImpl(); 
		        NotificationGenerator ngenerator = new NotificationGeneratorImplA(); 
		        obj.registerNotificationGenerator(ngenerator); 
		        obj.sendNotificationToMerchant("Order Cancelled. Please check :",allorder.getId() ,players);
			}
			validationMessage.put("message", CustomMessages.getCustomMessages("US"));
			validationMessage.put("key", 200);
			response.setResponse(validationMessage);
					
			} catch( Exception e) {
			e.printStackTrace();
			ErrorResponse errorResponse = new ErrorResponse(CustomMessages.getCustomMessages("ISE"), 500);
			response.setResponse(errorResponse);
		}
		return response;
	}
	
	@Override
	public RestResponse cancelOrderByMerchant(HashMap<String,Object> orderDetails,String apiKey) {
		
		RestResponse response = new RestResponse();
		try {
			HashMap<String, Object> validationMessage = new HashMap<String, Object>();
			
			//int merchantId = (Integer) orderDetails.get("merchantId");
			int merchantId = (Integer) orderDetails.get("merchantId");
			AllOrder allorder = null;
			int validationStatus = validatorUtil.validateUser(apiKey, merchantId, 2);
			if (validationStatus == 0 || validationStatus == -1) {
				validationMessage.put("message", CustomMessages.getCustomMessages("UAU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			AllMerchant allMerchant = allMerchantRepo.getMerchantById(merchantId);
			// AllMerchant merchant = allUserRepo.getMerchantById(merchantId);
			if (allMerchant == null ) {
				validationMessage.put("message", CustomMessages.getCustomMessages("NSU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);	
				return response;
			}
			allorder = allOrderRepo.getOrderDetails((Integer) orderDetails.get("orderId"));
			if (!allorder.getStatus().equalsIgnoreCase("COMPLETED") || allorder.getStatus().equalsIgnoreCase("PAID") || allorder.getStatus().equalsIgnoreCase("CANCELLED BY CUSTOMER") || allorder.getStatus().equalsIgnoreCase("CANCELLED BY MERCHANT"))
			{
				validationMessage.put("message", CustomMessages.getCustomMessages("OC"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);	
				return response;
			}
			allOrderRepo.updateOrderStatus("CANCELLED BY MERCHANT", allorder.getId());
			OrderStatusPl cancelStatus = allOrderStatusRepo.getOrderStatusPl("CANCELLED BY MERCHANT");
			AllInstoreOrderStatus instoreOrderStatus = new AllInstoreOrderStatus();
			instoreOrderStatus.setAllOrder(allorder);
			instoreOrderStatus.setDate(new Timestamp(System.currentTimeMillis()));
			instoreOrderStatus.setOrderStatusPl(cancelStatus);
			allOrderStatusRepo.saveAndFlush(instoreOrderStatus);
			
			int attrCount = allOrderAttrRepo.getCountOfOrderAttr()+1;
			AllOrderAttr allOrderAttr = new AllOrderAttr();
			allOrderAttr.setAllOrder(allorder);
			allOrderAttr.setOrderAttributeTypePl(allOrderAttrRepo.getOrderAttrByName("CANCEL_REASON"));
			allOrderAttr.setValue((String)orderDetails.get("cancelReason"));
			allOrderAttrRepo.saveAndFlush(allOrderAttr);
			attrCount++;
			 allOrderAttr = new AllOrderAttr();
				allOrderAttr.setAllOrder(allorder);
				allOrderAttr.setOrderAttributeTypePl(allOrderAttrRepo.getOrderAttrByName("CANCEL_DESCRIPTION"));
				allOrderAttr.setValue((String)orderDetails.get("cancelDescription"));
				allOrderAttrRepo.saveAndFlush(allOrderAttr);
			
			String players = allUserLoginRepo.getPlayerForUser(1,allorder.getAllUser().getId());
			if (players != null) {
			  NotificationGeneratorImpl obj = new NotificationGeneratorImpl(); 
		        NotificationGenerator ngenerator = new NotificationGeneratorImplA(); 
		        obj.registerNotificationGenerator(ngenerator); 
		        obj.sendNotificationForUser("Order Cancelled. Please check :",allorder.getId() ,players);
			}
			
			validationMessage.put("message", CustomMessages.getCustomMessages("US"));
			validationMessage.put("key", 200);
			response.setResponse(validationMessage);
					
			} catch( Exception e) {
			e.printStackTrace();
			ErrorResponse errorResponse = new ErrorResponse(CustomMessages.getCustomMessages("ISE"), 500);
			response.setResponse(errorResponse);
		}
		return response;
	}
	@Override
	public RestResponse createOrderByMerchant(HashMap<String,Object> orderDetails,String apiKey) {
		
		RestResponse response = new RestResponse();
		try {
			HashMap<String, Object> validationMessage = new HashMap<String, Object>();
			
			int merchantId = (Integer) orderDetails.get("merchantId");
			int userId = (Integer) orderDetails.get("userId");
			int validationStatus = validatorUtil.validateUser(apiKey, merchantId, 2);
			if (validationStatus == 0 || validationStatus == -1) {
				validationMessage.put("message", CustomMessages.getCustomMessages("UAU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			AllUser user = allUserRepo.getUserById(userId);
			AllMerchant merchant = allUserRepo.getMerchantById(merchantId);
			if (user == null || merchant == null ) {
				validationMessage.put("message", CustomMessages.getCustomMessages("NSU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);	
				return response;
			}
			List<HashMap<String,String>> listOfItems = (List<HashMap<String,String>>)orderDetails.get("items");
			List<HashMap<String,String>> listOfAttr = (List<HashMap<String,String>>)orderDetails.get("attr");
			int orderCount = allOrderRepo.findOrderCount()+1;
			OrderTypePl orderType = allOrderRepo.getOrderType("Instore Online Shopping");
			AllOrder allorder = new AllOrder();
			allorder.setId(orderCount);
			allorder.setAllMerchant(allMerchantRepo.getMerchantById(merchantId));
			allorder.setAllUser(allUserRepo.getUserById(userId));
			allorder.setPurchaseDate(new Timestamp(System.currentTimeMillis()));
			allorder.setOrderType(orderType);
			allorder.setStatus("CURRENT");
			allOrderRepo.saveAndFlush(allorder);
			
			OrderStatusPl orderStatus = allOrderStatusRepo.getOrderStatusPl("COMPLETED");
			AllInstoreOrderStatus allOrderStatus = new AllInstoreOrderStatus();
			allOrderStatus.setOrderStatusPl(orderStatus);
			allOrderStatus.setAllOrder(allorder);
			allOrderStatusRepo.saveAndFlush(allOrderStatus);
			
			float totalPrice=0;
			int itemCount = allOrderItemRepo.getCountOfOrderItems()+1;
			for (int i =0;i<listOfItems.size();i++) {
				HashMap<String,String> item = (HashMap<String,String>)listOfItems.get(i);
				AllInstoreOrderItem allOrderitem = new AllInstoreOrderItem();
				allOrderitem.setAddedBy("Merchant");
				allOrderitem.setId(itemCount);
				allOrderitem.setMerchantInvId(item.get("id"));
				allOrderitem.setQuantity(Integer.parseInt(item.get("quantity")));
				allOrderitem.setTotalPrice(Float.parseFloat(item.get("price")));
				totalPrice = totalPrice +Float.parseFloat(item.get("price"));
				allOrderitem.setAllOrder(allorder);
				itemCount++;
				allOrderItemRepo.saveAndFlush(allOrderitem);
			}
			
			int attrCount = allOrderAttrRepo.getCountOfOrderAttr()+1;
			for (int i =0;i<listOfAttr.size();i++) {
				HashMap<String,String> attr = (HashMap<String,String>)listOfAttr.get(i);
				AllOrderAttr allOrderAttr = new AllOrderAttr();
				allOrderAttr.setAllOrder(allorder);
				allOrderAttr.setId(attrCount);
				allOrderAttr.setOrderAttributeTypePl(allOrderAttrRepo.getOrderAttrByName(listOfAttr.get(i).get("name")));
				allOrderAttr.setValue(listOfAttr.get(i).get("value"));
				attrCount++;
				allOrderAttrRepo.saveAndFlush(allOrderAttr);
			}
			attrCount++;
			CommonUtility cm = new CommonUtility();
			String otp = cm.generateOTPCode();
			AllOrderAttr allOrderAttr = new AllOrderAttr();
			allOrderAttr.setAllOrder(allorder);
			allOrderAttr.setId(attrCount);
			allOrderAttr.setOrderAttributeTypePl(allOrderAttrRepo.getOrderAttrByName("CONFIRM_OTP"));
			allOrderAttr.setValue(otp);
			allOrderAttrRepo.saveAndFlush(allOrderAttr);
			
			
			validationMessage.put("message", CustomMessages.getCustomMessages("AS"));
			validationMessage.put("key", 200);
			validationMessage.put("confirmOTP", otp);
			validationMessage.put("orderId", orderCount);
			response.setResponse(validationMessage);
					
			} catch( Exception e) {
			e.printStackTrace();
			ErrorResponse errorResponse = new ErrorResponse(CustomMessages.getCustomMessages("ISE"), 500);
			response.setResponse(errorResponse);
		}
		return response;
	}
	
	
	@Override
	public RestResponse getOrderDetailsByMerchant(int merchantId, String status, String apiKey, int limitCount) {
		
		RestResponse response = new RestResponse();
		List<AllOrder> allorder = new ArrayList<>();
		try {
			int validationStatus = validatorUtil.validateUser(apiKey, merchantId, 2);
			if (validationStatus == 0 || validationStatus == -1) {
				HashMap<String, Object> validationMessage = new HashMap<String, Object>();
				validationMessage.put("message", CustomMessages.getCustomMessages("UAU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			int totalCount =0;
			String records = AppStartupRunner.configValues.get("Records");
			if (status.equalsIgnoreCase("CURRENT")) {
				totalCount= allOrderRepo.getOrderCount(merchantId, "CURRENT");
				allorder = allOrderRepo.getOrderByMerchant(merchantId,"CURRENT",limitCount,Integer.parseInt(records));
			} else if (status.equalsIgnoreCase("COMPLETED")){
				totalCount= allOrderRepo.getOrderCount(merchantId, "COMPLETED");
				allorder = allOrderRepo.getOrderByMerchant(merchantId,"COMPLETED",limitCount,Integer.parseInt(records));
			}
			List<AllOrderDto> allOrdeDto = new ArrayList<AllOrderDto>();
			if (allorder.size() > 0) {
				for (AllOrder allOrder : allorder) {
				//	AllInstoreOrderStatus instoreOrderStatus = getLatestStatus(allOrder.getAllInstoreOrderStatuses());
					AllOrderDto allOrderDto = new AllOrderDto();
				List<AllOrderAttrDto> listOfOrderAttrDto = new ArrayList<AllOrderAttrDto>();
				List<AllOrderAttr> listOfAttr = allOrder.getAllOrderAttrs();
				for (AllOrderAttr allOrderAttr : listOfAttr) {
					AllOrderAttrDto allOrderAttrDto = new AllOrderAttrDto();
					allOrderAttrDto.setId(allOrderAttr.getId());
					allOrderAttrDto.setName(allOrderAttr.getOrderAttributeTypePl().getName());
					allOrderAttrDto.setValue(allOrderAttr.getValue());
					listOfOrderAttrDto.add(allOrderAttrDto);
				}
				allOrderDto.setListOfOrderAttr(listOfOrderAttrDto);
				List<AllOrderItemsDto> listOfOrderItemsDto = new ArrayList<AllOrderItemsDto>();
				List<AllInstoreOrderItem> listOfItems = allOrder.getAllOrderItems();	
				for (AllInstoreOrderItem allOrderItem : listOfItems) {
					AllOrderItemsDto allOrderItemsDto = new AllOrderItemsDto();
					AllInventory inv = allOrderItemRepo.getInvName(Integer.parseInt(allOrderItem.getMerchantInvId()));
					allOrderItemsDto.setId(inv.getId());
					allOrderItemsDto.setName(inv.getName());
					allOrderItemsDto.setPrice(Float.toString(allOrderItem.getTotalPrice()));
					allOrderItemsDto.setQuantity(Integer.toString(allOrderItem.getQuantity()));
					listOfOrderItemsDto.add(allOrderItemsDto);
				}
				allOrderDto.setListOfItems(listOfOrderItemsDto);
				allOrderDto.setId(allOrder.getId());
				allOrderDto.setUserId(allOrder.getAllUser().getId());
				allOrderDto.setOrderType(allOrder.getOrderType().getName());
				allOrderDto.setUserName(allOrder.getAllUser().getName());
				allOrderDto.setPurchaseDate(allOrder.getPurchaseDate());
				allOrderDto.setStatus(allOrder.getStatus());
				allOrdeDto.add(allOrderDto);
				}
			}	
			HashMap<String, Object> validationMessage = new HashMap<String, Object>();
			validationMessage.put("message", CustomMessages.getCustomMessages("AS"));
			validationMessage.put("data", allOrdeDto);
			validationMessage.put("totalCount", totalCount);
			validationMessage.put("limit", limitCount+Integer.parseInt(records));
			validationMessage.put("key", 200);
			response.setResponse(validationMessage);
					
			} catch( Exception e) {
			e.printStackTrace();
			ErrorResponse errorResponse = new ErrorResponse(CustomMessages.getCustomMessages("ISE"), 500);
			response.setResponse(errorResponse);
		}
		return response;
	}
	
	@Override
	public RestResponse getOrderDetailsByCustomer(int userId, String status, String apiKey, int limitCount) {
		
		RestResponse response = new RestResponse();
		List<AllOrder> allorder = new ArrayList<>();
		try {
			int validationStatus = validatorUtil.validateUser(apiKey, userId, 1);
			if (validationStatus == 0 || validationStatus == -1) {
				HashMap<String, Object> validationMessage = new HashMap<String, Object>();
				validationMessage.put("message", CustomMessages.getCustomMessages("UAU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			int totalCount =0;
			String records = AppStartupRunner.configValues.get("Records");
			if (status.equalsIgnoreCase("CURRENT")) {
				totalCount= allOrderRepo.getOrderCountForUser(userId, "CURRENT");
				allorder = allOrderRepo.getOrderByUser(userId,"CURRENT",limitCount,Integer.parseInt(records));
			} else if (status.equalsIgnoreCase("COMPLETED")){
				totalCount= allOrderRepo.getOrderCountForUser(userId, "COMPLETED");
				allorder = allOrderRepo.getOrderByUser(userId,"COMPLETED",limitCount,Integer.parseInt(records));
			}
			List<AllOrderDto> allOrdeDto = new ArrayList<AllOrderDto>();
			if (allorder.size() > 0) {
				for (AllOrder allOrder : allorder) {
					AllOrderDto allOrderDto = new AllOrderDto();
				//	AllInstoreOrderStatus instoreOrderStatus = getLatestStatus(allOrder.getAllInstoreOrderStatuses());
				List<AllOrderAttrDto> listOfOrderAttrDto = new ArrayList<AllOrderAttrDto>();
				List<AllOrderAttr> listOfAttr = allOrder.getAllOrderAttrs();
				for (AllOrderAttr allOrderAttr : listOfAttr) {
					AllOrderAttrDto allOrderAttrDto = new AllOrderAttrDto();
					allOrderAttrDto.setId(allOrderAttr.getId());
					allOrderAttrDto.setName(allOrderAttr.getOrderAttributeTypePl().getName());
					allOrderAttrDto.setValue(allOrderAttr.getValue());
					listOfOrderAttrDto.add(allOrderAttrDto);
				}
				allOrderDto.setListOfOrderAttr(listOfOrderAttrDto);
				List<AllOrderItemsDto> listOfOrderItemsDto = new ArrayList<AllOrderItemsDto>();
				List<AllInstoreOrderItem> listOfItems = allOrder.getAllOrderItems();	
				for (AllInstoreOrderItem allOrderItem : listOfItems) {
					AllOrderItemsDto allOrderItemsDto = new AllOrderItemsDto();
					AllInventory inv = allOrderItemRepo.getInvName(Integer.parseInt(allOrderItem.getMerchantInvId()));
					allOrderItemsDto.setId(inv.getId());
					allOrderItemsDto.setName(inv.getName());
					allOrderItemsDto.setPrice(Float.toString(allOrderItem.getTotalPrice()));
					allOrderItemsDto.setQuantity(Integer.toString(allOrderItem.getQuantity()));
					listOfOrderItemsDto.add(allOrderItemsDto);
				}
				allOrderDto.setListOfItems(listOfOrderItemsDto);
				allOrderDto.setId(allOrder.getId());
				allOrderDto.setUserId(allOrder.getAllUser().getId());
				allOrderDto.setOrderType(allOrder.getOrderType().getName());
				allOrderDto.setUserName(allOrder.getAllUser().getName());
				allOrderDto.setPurchaseDate(allOrder.getPurchaseDate());
				allOrderDto.setStatus(allOrder.getStatus());
				allOrderDto.setMerchantName(allOrder.getAllMerchant().getName());
				allOrdeDto.add(allOrderDto);
				}
			}	
			HashMap<String, Object> validationMessage = new HashMap<String, Object>();
			validationMessage.put("message", CustomMessages.getCustomMessages("RS"));
			validationMessage.put("data", allOrdeDto);
			validationMessage.put("totalCount", totalCount);
			validationMessage.put("limit", limitCount+Integer.parseInt(records));
			validationMessage.put("key", 200);
			response.setResponse(validationMessage);
					
			} catch( Exception e) {
			e.printStackTrace();
			ErrorResponse errorResponse = new ErrorResponse(CustomMessages.getCustomMessages("ISE"), 500);
			response.setResponse(errorResponse);
		}
		return response;
	}
	
	@Override
	public RestResponse confirmOrderByMerchant(HashMap<String,Object> orderDetails, String apiKey) {
		
		HashMap<String, Object> validationMessage = new HashMap<String, Object>();
		RestResponse response = new RestResponse();
		try {
			int validationStatus = validatorUtil.validateUser(apiKey, (int)orderDetails.get("userId"), 2);
			if (validationStatus == 0 || validationStatus == -1) {
				validationMessage.put("message", CustomMessages.getCustomMessages("UAU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			
			int matched=0;
			AllOrder order = allOrderRepo.getOrderDetails((int)orderDetails.get("orderId"));
			if (order == null ) {
				validationMessage.put("message", CustomMessages.getCustomMessages("NDF"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);	
				return response;
			}
			AllInstoreOrderStatus orderStatus = allOrderRepo.getOrderStatus((Integer) orderDetails.get("orderId"));
			if (orderStatus.getOrderStatusPl().getName().equalsIgnoreCase("COMPLETED")) {
				validationMessage.put("message", CustomMessages.getCustomMessages("OAV"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			List<AllOrderAttr> listOfAttr = order.getAllOrderAttrs();
			for (AllOrderAttr orderattr: listOfAttr) {
				if (orderattr.getOrderAttributeTypePl().getName().equalsIgnoreCase("CONFIRM_OTP")) {
					if (orderattr.getValue().equalsIgnoreCase((String)orderDetails.get("OTP"))) {
						matched =1;
						break;
					}
				}
			}
			if (matched == 1) {
				allOrderRepo.updateOrderStatus("COMPLETED", order.getId());
				OrderStatusPl completeOrderStatus = allOrderStatusRepo.getOrderStatusPl("COMPLETED");
				AllInstoreOrderStatus instoreOrderStatus = new AllInstoreOrderStatus();
				instoreOrderStatus.setAllOrder(order);
				instoreOrderStatus.setDate(new Timestamp(System.currentTimeMillis()));
				instoreOrderStatus.setOrderStatusPl(completeOrderStatus);
				allOrderStatusRepo.saveAndFlush(instoreOrderStatus);
				validationMessage.put("message", CustomMessages.getCustomMessages("VS"));
			} else {
				validationMessage.put("message", CustomMessages.getCustomMessages("WOT"));
			}
			validationMessage.put("key", 200);
			response.setResponse(validationMessage);
					
			} catch( Exception e) {
			e.printStackTrace();
			ErrorResponse errorResponse = new ErrorResponse(CustomMessages.getCustomMessages("ISE"), 500);
			response.setResponse(errorResponse);
		}
		return response;
	}
	
	@Override
	public RestResponse searchOrderByMerchant(int merchantId, String status, String apiKey, String value) {
		
		RestResponse response = new RestResponse();
		List<AllOrder> allorder = new ArrayList<>();
		try {
			int validationStatus = validatorUtil.validateUser(apiKey, merchantId, 2);
			if (validationStatus == 0 || validationStatus == -1) {
				HashMap<String, Object> validationMessage = new HashMap<String, Object>();
				validationMessage.put("message", CustomMessages.getCustomMessages("UAU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			if (status.equalsIgnoreCase("CURRENT")) {
			allorder = allOrderRepo.searchOrderByMerchant(value,merchantId, "CURRENT");
			} else if (status.equalsIgnoreCase("COMPLETED")){
				allorder = allOrderRepo.searchOrderByMerchant(value,merchantId, "COMPLETED");
			}
			List<AllOrderDto> allOrdeDto = new ArrayList<AllOrderDto>();
			if (allorder.size() > 0) {
				for (AllOrder allOrder : allorder) {
					AllOrderDto allOrderDto = new AllOrderDto();
				List<AllOrderAttrDto> listOfOrderAttrDto = new ArrayList<AllOrderAttrDto>();
				List<AllOrderAttr> listOfAttr = allOrder.getAllOrderAttrs();
				for (AllOrderAttr allOrderAttr : listOfAttr) {
					AllOrderAttrDto allOrderAttrDto = new AllOrderAttrDto();
					allOrderAttrDto.setId(allOrderAttr.getId());
					allOrderAttrDto.setName(allOrderAttr.getOrderAttributeTypePl().getName());
					allOrderAttrDto.setValue(allOrderAttr.getValue());
					listOfOrderAttrDto.add(allOrderAttrDto);
				}
				allOrderDto.setListOfOrderAttr(listOfOrderAttrDto);
				List<AllOrderItemsDto> listOfOrderItemsDto = new ArrayList<AllOrderItemsDto>();
				List<AllInstoreOrderItem> listOfItems = allOrder.getAllOrderItems();	
				for (AllInstoreOrderItem allOrderItem : listOfItems) {
					AllOrderItemsDto allOrderItemsDto = new AllOrderItemsDto();
					AllInventory inv = allOrderItemRepo.getInvName(Integer.parseInt(allOrderItem.getMerchantInvId()));
					allOrderItemsDto.setId(inv.getId());
					allOrderItemsDto.setName(inv.getName());
					allOrderItemsDto.setPrice(Float.toString(allOrderItem.getTotalPrice()));
					allOrderItemsDto.setQuantity(Integer.toString(allOrderItem.getQuantity()));
					listOfOrderItemsDto.add(allOrderItemsDto);
				}
				allOrderDto.setListOfItems(listOfOrderItemsDto);
				allOrderDto.setId(allOrder.getId());
				allOrderDto.setUserId(allOrder.getAllUser().getId());
				allOrderDto.setOrderType(allOrder.getOrderType().getName());
				allOrderDto.setUserName(allOrder.getAllUser().getName());
				allOrderDto.setPurchaseDate(allOrder.getPurchaseDate());
				allOrderDto.setStatus(allOrder.getAllInstoreOrderStatuses().get(allOrder.getAllInstoreOrderStatuses().size()-1).getOrderStatusPl().getName());
				allOrdeDto.add(allOrderDto);
				}
			}	
			HashMap<String, Object> validationMessage = new HashMap<String, Object>();
			validationMessage.put("message", CustomMessages.getCustomMessages("AS"));
			validationMessage.put("data", allOrdeDto);
			validationMessage.put("key", 200);
			response.setResponse(validationMessage);
					
			} catch( Exception e) {
			e.printStackTrace();
			ErrorResponse errorResponse = new ErrorResponse(CustomMessages.getCustomMessages("ISE"), 500);
			response.setResponse(errorResponse);
		}
		return response;
	}
	
	public Object generateCoupon(int userId, int merchantId) {
		JSONObject obj = null;
		JSONObject js1 = null;
		try {
		      HttpHeaders headers = new HttpHeaders();
		      headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
		      HttpEntity <String> entity = new HttpEntity<String>(headers);
		      
		        obj=new JSONObject(restTemplate.exchange("http://localhost:8089/generateOffer/"+userId+"/"+merchantId, HttpMethod.GET, entity, String.class).getBody());
		         js1= (JSONObject)obj.get("response");
		        
		} catch (Exception e) {
			
		}
		return js1.get("data");
		
	}
	
	private AllInstoreOrderStatus getLatestStatus(List<AllInstoreOrderStatus> statuses) {
		
		try {
			// Forecast object which has highest temperature
		    Comparator<AllInstoreOrderStatus> comparator = Comparator.comparing(AllInstoreOrderStatus::getDate);
		    AllInstoreOrderStatus latestStatus = statuses.stream().filter(status -> status.getDate() != null).max(comparator).get();
		    return latestStatus;


		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
		}
