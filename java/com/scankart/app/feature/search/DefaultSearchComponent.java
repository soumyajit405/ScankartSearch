package com.scankart.app.feature.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.scankart.app.AppStartupRunner;
import com.scankart.app.dto.ErrorResponse;
import com.scankart.app.dto.RestResponse;
import com.scankart.app.model.AllInventoryAttr;
import com.scankart.app.model.AllInventoryAttrRepository;
import com.scankart.app.model.AllMerchant;
import com.scankart.app.model.AllMerchantAttribute;
import com.scankart.app.model.AllMerchantAttributeRepository;
import com.scankart.app.model.AllMerchantRepository;
import com.scankart.app.model.AllOrder;
import com.scankart.app.model.AllOrderRepository;
import com.scankart.app.model.BusinessTypePl;
import com.scankart.app.util.CustomMessages;
import com.scankart.app.util.DistanceChecker;
import com.scankart.app.util.ValidatorUtility;


@Transactional
@Repository
@Service("DefaultSearchComponent")
public class DefaultSearchComponent implements SearchComponent{


	@Autowired 
	AllMerchantRepository allMerchantRepo;
	
	@Autowired 
	AllMerchantAttributeRepository allMerchantAttrRepo;
	
	@Autowired 
	AllInventoryAttrRepository allInvAttrRepo;
	
	@Autowired 
	ValidatorUtility validatorUtil;
	
	@Autowired 
	AllOrderRepository allOrderRepo;
	
	@Override
	public RestResponse searchStore(String type,int userId, String apiKey, String latitude, String longitude, int limitCount) {
		List<AllMerchantDto> listOfMerchantsdto = new ArrayList<AllMerchantDto>();
		StringBuffer merchantLatitude = new StringBuffer();
		StringBuffer merchantLongitude = new StringBuffer();
		DistanceChecker distanceCheckUtil = new DistanceChecker();
		RestResponse response = new RestResponse();
		String records = AppStartupRunner.configValues.get("Records");
		String radius = AppStartupRunner.configValues.get("Radius");
		int totalCount =0;
		try {
			int validationStatus = validatorUtil.validateUser(apiKey,userId,1);
			if (validationStatus == 0 || validationStatus == -1) {
				HashMap<String, Object> validationMessage = new HashMap<String, Object>();
				validationMessage.put("message", CustomMessages.getCustomMessages("UAU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			List<AllMerchant> listOfMerchants= new ArrayList<>();
			
			if (type.equalsIgnoreCase("ALL")) {
				totalCount = allMerchantRepo.getAllMerchantCount();
				listOfMerchants= allMerchantRepo.getAllMerchants(limitCount,Integer.parseInt(records));
			} else {
				BusinessTypePl businessType= allMerchantRepo.getBusinessType(type);
				totalCount = allMerchantRepo.getAllMerchantsByTypeCount(type);
				listOfMerchants= allMerchantRepo.getAllMerchantsByType(businessType.getId(),limitCount,Integer.parseInt(records));
			}
			for (AllMerchant merchant: listOfMerchants) {
				merchantLatitude.setLength(0);
				merchantLongitude.setLength(0);
				List<AllMerchantAttribute> listOfMerchantAttr = allMerchantAttrRepo.getMerchantAttributeById(merchant.getId());
				List<AllMerchantAttrDto> listOfMerchantAttrdto = new ArrayList<AllMerchantAttrDto>();
				for (AllMerchantAttribute merchantAttr: listOfMerchantAttr) {
					AllMerchantAttrDto allMerchantAttrdto = new AllMerchantAttrDto();
					allMerchantAttrdto.setId(merchantAttr.getId());
					allMerchantAttrdto.setName(merchantAttr.getBusinessAttrTypePl().getName());
					if (merchantAttr.getBusinessAttrTypePl().getName().equalsIgnoreCase("latitude")) {
						merchantLatitude.append(merchantAttr.getValue());
					} else if (merchantAttr.getBusinessAttrTypePl().getName().equalsIgnoreCase("longitude")) {
						merchantLongitude.append(merchantAttr.getValue());
					}
					allMerchantAttrdto.setValue(merchantAttr.getValue());
					listOfMerchantAttrdto.add(allMerchantAttrdto);
				}
				double distance =  distanceCheckUtil.distanceBetween(latitude, longitude, merchantLatitude.toString(), merchantLongitude.toString());
				if (distance > Double.parseDouble(radius)) {
					continue;
				}
				List<AllOrder> listOfOrders = allOrderRepo.getOrderByMerchant(merchant.getId(), "CREATED");
				AllMerchantDto allMerchantDto = new AllMerchantDto();
				allMerchantDto.setId(merchant.getId());
				allMerchantDto.setName(merchant.getName());
				
				AllMerchantAttrDto distanceFromCustomer = new AllMerchantAttrDto();
				// allMerchantAttrdto.setId(merchantAttr.getId());
				distanceFromCustomer.setName("Distance");
				distanceFromCustomer.setValue(Double.toString(distance));
				listOfMerchantAttrdto.add(distanceFromCustomer);
				

				AllMerchantAttrDto liveCustomers = new AllMerchantAttrDto();
				// allMerchantAttrdto.setId(merchantAttr.getId());
				liveCustomers.setName("Live Customers");
				liveCustomers.setValue(Integer.toString(listOfOrders.size()));
				listOfMerchantAttrdto.add(liveCustomers);
					
				allMerchantDto.setAllMerchantAttributesDto(listOfMerchantAttrdto);
				listOfMerchantsdto.add(allMerchantDto);
			}
			HashMap<String, Object> validationMessage = new HashMap<String, Object>();
			validationMessage.put("message", CustomMessages.getCustomMessages("FS"));
			validationMessage.put("key", 200);
			validationMessage.put("data", listOfMerchantsdto);
			validationMessage.put("totalCount", totalCount);
			validationMessage.put("limit", limitCount+Integer.parseInt(records));
			response.setResponse(validationMessage);
					
			} catch( Exception e) {
			e.printStackTrace();
			ErrorResponse errorResponse = new ErrorResponse(CustomMessages.getCustomMessages("ISE"), 500);
			response.setResponse(errorResponse);
		}
		return response;
	}
	
	@Override
	public RestResponse searchStoreByName(String name,int userId, String apiKey, String latitude, String longitude, int limitCount) {
		List<AllMerchantDto> listOfMerchantsdto = new ArrayList<AllMerchantDto>();
		StringBuffer merchantLatitude = new StringBuffer();
		StringBuffer merchantLongitude = new StringBuffer();
		DistanceChecker distanceCheckUtil = new DistanceChecker();
		RestResponse response = new RestResponse();
		String records = AppStartupRunner.configValues.get("Records");
		String radius = AppStartupRunner.configValues.get("Radius");
		int totalCount =0;
		try {
			int validationStatus = validatorUtil.validateUser(apiKey,userId,1);
			if (validationStatus == 0 || validationStatus == -1) {
				HashMap<String, Object> validationMessage = new HashMap<String, Object>();
				validationMessage.put("message", CustomMessages.getCustomMessages("UAU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			List<AllMerchant> listOfMerchants= new ArrayList<>();
			
				totalCount = allMerchantRepo.getAllMerchantCountByName(name.toUpperCase());
				listOfMerchants= allMerchantRepo.getAllMerchantsByName(limitCount,limitCount+Integer.parseInt(records),name.toUpperCase());
			
			for (AllMerchant merchant: listOfMerchants) {
				merchantLatitude.setLength(0);
				merchantLongitude.setLength(0);
				List<AllMerchantAttribute> listOfMerchantAttr = allMerchantAttrRepo.getMerchantAttributeById(merchant.getId());
				List<AllMerchantAttrDto> listOfMerchantAttrdto = new ArrayList<AllMerchantAttrDto>();
				for (AllMerchantAttribute merchantAttr: listOfMerchantAttr) {
					AllMerchantAttrDto allMerchantAttrdto = new AllMerchantAttrDto();
					allMerchantAttrdto.setId(merchantAttr.getId());
					allMerchantAttrdto.setName(merchantAttr.getBusinessAttrTypePl().getName());
					if (merchantAttr.getBusinessAttrTypePl().getName().equalsIgnoreCase("latitude")) {
						merchantLatitude.append(merchantAttr.getValue());
					} else if (merchantAttr.getBusinessAttrTypePl().getName().equalsIgnoreCase("longitude")) {
						merchantLongitude.append(merchantAttr.getValue());
					}
					allMerchantAttrdto.setValue(merchantAttr.getValue());
					listOfMerchantAttrdto.add(allMerchantAttrdto);
				}
				double distance =  distanceCheckUtil.distanceBetween(latitude, longitude, merchantLatitude.toString(), merchantLongitude.toString());
				if (distance > Double.parseDouble(radius)) {
					continue;
				}
				List<AllOrder> listOfOrders = allOrderRepo.getOrderByMerchant(merchant.getId(), "CREATED");
				AllMerchantDto allMerchantDto = new AllMerchantDto();
				allMerchantDto.setId(merchant.getId());
				allMerchantDto.setName(merchant.getName());
				
				AllMerchantAttrDto distanceFromCustomer = new AllMerchantAttrDto();
				// allMerchantAttrdto.setId(merchantAttr.getId());
				distanceFromCustomer.setName("Distance");
				distanceFromCustomer.setValue(Double.toString(distance));
				listOfMerchantAttrdto.add(distanceFromCustomer);
				

				AllMerchantAttrDto liveCustomers = new AllMerchantAttrDto();
				// allMerchantAttrdto.setId(merchantAttr.getId());
				liveCustomers.setName("Live Customers");
				liveCustomers.setValue(Integer.toString(listOfOrders.size()));
				listOfMerchantAttrdto.add(liveCustomers);
					
				allMerchantDto.setAllMerchantAttributesDto(listOfMerchantAttrdto);
				listOfMerchantsdto.add(allMerchantDto);
			}
			HashMap<String, Object> validationMessage = new HashMap<String, Object>();
			validationMessage.put("message", CustomMessages.getCustomMessages("FS"));
			validationMessage.put("key", 200);
			validationMessage.put("data", listOfMerchantsdto);
			validationMessage.put("totalCount", totalCount);
			validationMessage.put("limit", limitCount+Integer.parseInt(records));
			response.setResponse(validationMessage);
					
			} catch( Exception e) {
			e.printStackTrace();
			ErrorResponse errorResponse = new ErrorResponse(CustomMessages.getCustomMessages("ISE"), 500);
			response.setResponse(errorResponse);
		}
		return response;
	}
	
	
	@Override
	public RestResponse getDetailsByCode(String attrName,int merchantId,String barCode, String apiKey, int userId) {
		RestResponse response = new RestResponse();
		try {
			// List<AllInventoryAttrDto> listallInvAttrDto = new ArrayList<AllInventoryAttrDto>();
			HashMap<String, Object> validationMessage = new HashMap<String, Object>();
			int validationStatus = validatorUtil.validateUser(apiKey,userId,1);
			if (validationStatus == 0 || validationStatus == -1) {
				validationMessage.put("message", CustomMessages.getCustomMessages("UAU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			AllMerchant allMerchant = allMerchantRepo.getMerchantById(merchantId);
			
			if (allMerchant == null ) {
				validationMessage.put("message", CustomMessages.getCustomMessages("NSU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			AllInventoryAttr inventoryAttr=allInvAttrRepo.getInventoryByCode(attrName, barCode, merchantId);
			
			if (inventoryAttr != null) {
			ItemWithBarcodeDto itdo= new ItemWithBarcodeDto();
			itdo.setName(inventoryAttr.getAllInventory().getName());
			itdo.setId(inventoryAttr.getAllInventory().getId());
			
				
				List<AllInventoryAttr> listOfInvAttr = allInvAttrRepo.getInventoryByCode(merchantId, inventoryAttr.getAllInventory().getId());
				for (AllInventoryAttr invAttr: listOfInvAttr) {
					if (invAttr.getAllInventoryAttrPl().getName().equalsIgnoreCase("Price")) {
						itdo.setPrice(invAttr.getValue());
					} else if (invAttr.getAllInventoryAttrPl().getName().equalsIgnoreCase("Image")){
						itdo.setImageName(invAttr.getValue());
					}
//					AllInventoryAttrDto allInvAttrDto = new AllInventoryAttrDto();
//					allInvAttrDto.setId(invAttr.getAllInventory().getId());
//					allInvAttrDto.setName(invAttr.getAllInventoryAttrPl().getName());
//					allInvAttrDto.setValue(invAttr.getValue());
//					listallInvAttrDto.add(allInvAttrDto);
				}
			validationMessage.put("message", CustomMessages.getCustomMessages("FS"));
			validationMessage.put("data", itdo);
			validationMessage.put("key", 200);
			response.setResponse(validationMessage);
			} else {
				validationMessage.put("message", CustomMessages.getCustomMessages("NDF"));
				//validationMessage.put("data", allMerchantAttrDto);
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
			}
					
			} catch( Exception e) {
			e.printStackTrace();
			ErrorResponse errorResponse = new ErrorResponse(CustomMessages.getCustomMessages("ISE"), 500);
			response.setResponse(errorResponse);
		}
		return response;
	}
	
	@Override
	public RestResponse getDetailsByCodeByMerchant(String attrName,int merchantId,String barCode, String apiKey) {
		RestResponse response = new RestResponse();
		try {
			// List<AllInventoryAttrDto> listallInvAttrDto = new ArrayList<AllInventoryAttrDto>();
			HashMap<String, Object> validationMessage = new HashMap<String, Object>();
			int validationStatus = validatorUtil.validateUser(apiKey,merchantId,2);
			if (validationStatus == 0 || validationStatus == -1) {
				validationMessage.put("message", CustomMessages.getCustomMessages("UAU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			AllMerchant allMerchant = allMerchantRepo.getMerchantById(merchantId);
			
			if (allMerchant == null ) {
				validationMessage.put("message", CustomMessages.getCustomMessages("NSU"));
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
				return response;
			}
			AllInventoryAttr inventoryAttr=allInvAttrRepo.getInventoryByCode(attrName, barCode, merchantId);
			
			if (inventoryAttr != null) {
			ItemWithBarcodeDto itdo= new ItemWithBarcodeDto();
			itdo.setName(inventoryAttr.getAllInventory().getName());
			itdo.setId(inventoryAttr.getAllInventory().getId());
			
				
				List<AllInventoryAttr> listOfInvAttr = allInvAttrRepo.getInventoryByCode(merchantId, inventoryAttr.getAllInventory().getId());
				for (AllInventoryAttr invAttr: listOfInvAttr) {
					if (invAttr.getAllInventoryAttrPl().getName().equalsIgnoreCase("Price")) {
						itdo.setPrice(invAttr.getValue());
					} else if (invAttr.getAllInventoryAttrPl().getName().equalsIgnoreCase("Image")){
						itdo.setImageName(invAttr.getValue());
					}
//					AllInventoryAttrDto allInvAttrDto = new AllInventoryAttrDto();
//					allInvAttrDto.setId(invAttr.getAllInventory().getId());
//					allInvAttrDto.setName(invAttr.getAllInventoryAttrPl().getName());
//					allInvAttrDto.setValue(invAttr.getValue());
//					listallInvAttrDto.add(allInvAttrDto);
				}
			validationMessage.put("message", CustomMessages.getCustomMessages("FS"));
			validationMessage.put("data", itdo);
			validationMessage.put("key", 200);
			response.setResponse(validationMessage);
			} else {
				validationMessage.put("message", CustomMessages.getCustomMessages("NDF"));
				//validationMessage.put("data", allMerchantAttrDto);
				validationMessage.put("key", 200);
				response.setResponse(validationMessage);
			}
					
			} catch( Exception e) {
			e.printStackTrace();
			ErrorResponse errorResponse = new ErrorResponse(CustomMessages.getCustomMessages("ISE"), 500);
			response.setResponse(errorResponse);
		}
		return response;
	}
}
