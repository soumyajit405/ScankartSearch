package com.scankart.app.util;

 
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;




public class PushHelper {
	
	private static final String webServiceURI = "https://onesignal.com/api/v1/notifications";

	public String  pushToUser(String message,String playerId,String orderId) throws ClassNotFoundException, SQLException {
		//JDBCConnection connref=new JDBCConnection();
		//Class.forName("com.mysql.jdbc.Driver");
		PreparedStatement st=null;
	//	conn = connref.getOracleConnection();
		
		
	String strJsonBody="";
		try {
			
			   String jsonResponse;
			//   Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("www-proxy.idc.oracle.com", 80));
			   URL url = new URL("https://onesignal.com/api/v1/notifications");
			   HttpURLConnection con = (HttpURLConnection)url.openConnection();
			   con.setUseCaches(false);
			   con.setDoOutput(true);
			   con.setDoInput(true);

			   con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			   con.setRequestProperty("Authorization", "Basic M2U0YmFhNzEtNWRjZS00ZmE1LTgzYTYtYmJlMzI1YzdiYzk0");
			   con.setRequestMethod("POST");
    strJsonBody = "{"
                      +   "\"app_id\": \"f51d638e-8980-40eb-93cc-c1c26a722d8d\","
                      +   "\"include_player_ids\": [\""+playerId+"\"],"
                      +   "\"data\": {\"response\": \"notifyorders\"},"
                      +   "\"contents\": {\"en\": \""+message+" "+orderId+"\"},"
                        +   "\"priority\": \"10\""
                      + "}";
			   byte[] sendBytes = strJsonBody.getBytes("UTF-8");
			   con.setFixedLengthStreamingMode(sendBytes.length);

			   OutputStream outputStream = con.getOutputStream();
			   outputStream.write(sendBytes);

			   int httpResponse = con.getResponseCode();
			   System.out.println("httpResponse: " + httpResponse);

			   if (  httpResponse >= HttpURLConnection.HTTP_OK
			      && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
			      Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
			      jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
			      scanner.close();
			   }
			   else {
			      Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
			      jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
			      scanner.close();
			   }
			   System.out.println("jsonResponse:\n" + jsonResponse);
		   
			} catch(Exception e) {
			   e.printStackTrace();
			}
return "success";	
	}

	public String  pushtoMerchants(String message,ArrayList<String> listOfPlayerIds,String wod) throws ClassNotFoundException, SQLException {
		
		
		System.out.println("listOfPlayerIds count " +listOfPlayerIds.size());
		Connection conn=null;
		//JDBCConnection connref=new JDBCConnection();
		//Class.forName("com.mysql.jdbc.Driver");
		PreparedStatement st=null;
	//	conn = connref.getOracleConnection();

		
		StringBuffer buffer=new StringBuffer("(");
		String strJsonBody="";
		try {
			
			   String jsonResponse;
			   
			   URL url = new URL("https://onesignal.com/api/v1/notifications");
			  

	//ArrayList<String> list=commondbhelper.getEmailFromLoginId(buffer.toString());
	
	for(int i=0;i<listOfPlayerIds.size();i++)
	{
		 strJsonBody = "{"
                 +   "\"app_id\": \"f51d638e-8980-40eb-93cc-c1c26a722d8d\","
                 +   "\"include_player_ids\": [\""+listOfPlayerIds.get(i)+"\"],"
                 +   "\"data\": {\"response\": \"notifyorders\"},"
                 +   "\"contents\": {\"en\": \" "+message+""+wod+"\"},"
                   +   "\"priority\": \"10\""
                 + "}";
		 HttpURLConnection con = (HttpURLConnection)url.openConnection();
		   con.setUseCaches(false);
		   con.setDoOutput(true);
		   con.setDoInput(true);

		   con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		   con.setRequestProperty("Authorization", "Basic M2U0YmFhNzEtNWRjZS00ZmE1LTgzYTYtYmJlMzI1YzdiYzk0");
		   con.setRequestMethod("POST");
	

			   System.out.println("strJsonBody:\n" + strJsonBody);

			   byte[] sendBytes = strJsonBody.getBytes("UTF-8");
			 //  con.setFixedLengthStreamingMode(sendBytes.length);

			   OutputStream outputStream = con.getOutputStream();
			   outputStream.write(sendBytes);

			   int httpResponse = con.getResponseCode();
			   System.out.println("httpResponse: " + httpResponse);

			   if (  httpResponse >= HttpURLConnection.HTTP_OK
			      && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
			      Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
			      jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
			      scanner.close();
			      con.disconnect();
			   }
			   else {
			      Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
			      jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
			      scanner.close();
			      con.disconnect();
			   }
			   System.out.println("jsonResponse:\n" + jsonResponse);
	}
			} catch(Exception e) {
			   e.printStackTrace();
			}
return "success";	
	}
	
	public String  pushTest(String playerId,String message) throws ClassNotFoundException, SQLException {
		
		
		 
		Connection conn=null;
		//JDBCConnection connref=new JDBCConnection();
		//Class.forName("com.mysql.jdbc.Driver");
		PreparedStatement st=null;
	//	conn = connref.getOracleConnection();
		
		
	String strJsonBody="";
		try {
			
			   String jsonResponse;
			   
			   URL url = new URL("https://onesignal.com/api/v1/notifications");
			   HttpURLConnection con = (HttpURLConnection)url.openConnection();
			   con.setUseCaches(false);
			   con.setDoOutput(true);
			   con.setDoInput(true);

			   con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
			   con.setRequestProperty("Authorization", "Basic YTU4ZDJiMmEtMzFiOS00M2JhLTkwMmEtM2YzZDZiMDU3NWI5");
			   con.setRequestMethod("POST");
	
	//commondbhelper.getPlayerIdForServiceMan(wod);

    strJsonBody = "{"
                      +   "\"app_id\": \"843d9906-6279-49e6-9075-8ce45f8f14c5\","
                      + "\"android_sound\": \"brings\","
                      +   "\"include_player_ids\": [\""+playerId+"\"],"
                      +   "\"data\": {\"response\": \"notifyorders\"},"
                      +   "\"contents\": {\"en\": \""+message+" -\"},"
                        +   "\"priority\": \"10\""
                      + "}";



		   System.out.println("strJsonBody:\n" + strJsonBody);

			   byte[] sendBytes = strJsonBody.getBytes("UTF-8");
			   con.setFixedLengthStreamingMode(sendBytes.length);

			   OutputStream outputStream = con.getOutputStream();
			   outputStream.write(sendBytes);

			   int httpResponse = con.getResponseCode();
			   System.out.println("httpResponse: " + httpResponse);

			   if (  httpResponse >= HttpURLConnection.HTTP_OK
			      && httpResponse < HttpURLConnection.HTTP_BAD_REQUEST) {
			      Scanner scanner = new Scanner(con.getInputStream(), "UTF-8");
			      jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
			      scanner.close();
			   }
			   else {
			      Scanner scanner = new Scanner(con.getErrorStream(), "UTF-8");
			      jsonResponse = scanner.useDelimiter("\\A").hasNext() ? scanner.next() : "";
			      scanner.close();
			   }
			   System.out.println("jsonResponse:\n" + jsonResponse);
		   
			} catch(Exception e) {
			   e.printStackTrace();
			}
return "success";	
	}

	public static void main(String args[])
	{
		
		//MailUtility mu=new MailUtility();
		//mu.sendEmail("soumyajit405@gmail.com", "Test");
		PushHelper ph=new PushHelper();
		try {
			ph.pushTest("1a6faf7d-3d63-41d3-a1ee-7a97e51f5b29", "welcome");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
