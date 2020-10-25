package com.scankart.app.util;

import java.sql.SQLException;
import java.util.ArrayList;


public interface NotificationGenerator {
	
	void generateNotificationForMerchant(String message ,int orderId, ArrayList<String> playerIds) throws ClassNotFoundException, SQLException;

	void generateNotificationForUser(String message ,int orderId, String playerIds) throws ClassNotFoundException, SQLException;
}
