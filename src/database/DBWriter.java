package database;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DBWriter {
	private final static String DB_URL = "jdbc:mysql://localhost:3306/messenger_db";
	private final static String USER = "jdbc";
	private final static String PASSWORD = "jdbc_password";
	private final static String CHAT_RECORDS_TABLE = "chat_records";
	
	
	public static void insert(String sender, String receiver, String message){
		
		java.util.Date date = new java.util.Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = dateFormat.format(date);
		try {
			// Get a connection to database
			Connection connection = DriverManager.getConnection(DB_URL,USER,PASSWORD);
			
			// SQL insertion string
			String sqlQuery = " insert into "+ CHAT_RECORDS_TABLE
					+ " (sender, receiver, message, time)"
			        + " values (?, ?, ?, ?)";
			// Create a statement
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.setString(1, sender);
			statement.setString(2, receiver);
			statement.setString(3, message);
			statement.setString(4, time);
			
			// Execute prepared statement
			statement.execute();
			
			// Close connection
			connection.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public static void delete(){
		try {
			// Get a connection to database
			Connection connection = DriverManager.getConnection(DB_URL,USER,PASSWORD);
			//SQL deletion string
			String sqlQuery = "delete from "+ CHAT_RECORDS_TABLE;
			// Create a statement
			PreparedStatement statement = connection.prepareStatement(sqlQuery);
			statement.execute();
			
			// Close connection
			connection.close();
		} catch (SQLException e){
			e.printStackTrace();
		}
	}
}
