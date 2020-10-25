package com.scankart.app.util;

import java.sql.SQLException;
import java.util.ArrayList;


public class NotificationGeneratorImpl {
	
	private NotificationGenerator ngenerator;
	
	public void registerNotificationGenerator(NotificationGenerator ngenerator) 
    { 
        this.ngenerator = ngenerator; 
    } 
	
	public void sendNotificationToMerchant(String message,final int orderId, final ArrayList<String> playerIds) 
    { 
  
        // An Async task always executes in new thread 
        new Thread(new Runnable() { 
            public void run() 
            { 
                // check if listener is registered. 
                if (ngenerator != null) { 
  
                    // invoke the callback method of class A 
                    try {
						ngenerator.generateNotificationForMerchant(message, orderId,playerIds);
					} catch (ClassNotFoundException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
                } 
            } 
        }).start(); 
    }
	
	
	public void sendNotificationForUser(String message,final int orderId, final String playerId) 
    { 
  
        // An Async task always executes in new thread 
        new Thread(new Runnable() { 
            public void run() 
            { 
                // check if listener is registered. 
                if (ngenerator != null) { 
  
                    // invoke the callback method of class A 
                    try {
						ngenerator.generateNotificationForUser(message, orderId,message);
					} catch (ClassNotFoundException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
                } 
            } 
        }).start(); 
    }

}


