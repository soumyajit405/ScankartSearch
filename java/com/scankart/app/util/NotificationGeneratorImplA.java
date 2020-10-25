package com.scankart.app.util;

import java.sql.SQLException;
import java.util.ArrayList;


public class NotificationGeneratorImplA implements NotificationGenerator {
 
		  
	    @Override
		public void generateNotificationForMerchant(String message, int orderId, ArrayList<String> playerIds) throws ClassNotFoundException, SQLException {
	    	PushHelper pushhelper=new PushHelper();
			pushhelper.pushtoMerchants(message,playerIds, Integer.toString(orderId));
			
		}

	    @Override
		public void generateNotificationForUser(String message, int orderId, String playerId) throws ClassNotFoundException, SQLException {
	    	PushHelper pushhelper=new PushHelper();
			pushhelper.pushToUser(message,playerId, Integer.toString(orderId));
			
		}
} 

