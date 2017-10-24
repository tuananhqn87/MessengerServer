package database;

import java.sql.*;
import java.text.SimpleDateFormat;

public class DBQuery {
	private final String DB_URL = "jdbc:mysql://localhost:3306/messenger_db";
	private final String USER = "jdbc";
	private final String PASSWORD = "jdbc_password";
	private final String CHAT_RECORDS_TABLE = "chat_records";
	
	private static Connection connection;
	
	public DBQuery() {
		
		clearChatRecordsTable();
		
	}
	
	/**
	 * Method to insert chat records to database
	 * 
	 * @param sender The sender of chat message to save to "sender" column
	 * @param receiver The receiver of chat message to save to "receiver" column
	 * @param message The message to save to "message" column
	 */
	public void insert(String sender, String receiver, String message) {
		
		java.util.Date date = new java.util.Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = dateFormat.format(date);
		// SQL insertion string
		String sqlQuery = " insert into "+ CHAT_RECORDS_TABLE
				+ " (sender, receiver, message, time)"
		        + " values (?, ?, ?, ?)";
		setConnection(DB_URL,USER,PASSWORD);
		
		// Try-with-resources to automatically close PreparedStatement
		try (PreparedStatement statement = getConnection().prepareStatement(sqlQuery);) {
			
			statement.setString(1, sender);
			statement.setString(2, receiver);
			statement.setString(3, message);
			statement.setString(4, time);
			
			// Execute prepared statement
			statement.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}
	
	/**
	 * Method to delete chat records table every time chat server starts
	 */
	private void clearChatRecordsTable(){
		//SQL deletion string
		String sqlQuery = "delete from "+ CHAT_RECORDS_TABLE;
		setConnection(DB_URL,USER,PASSWORD);
		
		// Try-with-resources to automatically close PreparedStatement
		try (PreparedStatement statement = getConnection().prepareStatement(sqlQuery);) {
			
			statement.execute();
			
		} catch (SQLException e){
			e.printStackTrace();
		} finally {
			closeConnection();
		}
	}
	
	private void setConnection(String url, String username, String password){
		
		try {
			connection = DriverManager.getConnection(url,username,password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	
	private static Connection getConnection() {
		return connection;
	}
	
	private void closeConnection() {
		try {
			if (getConnection().isValid(0)) {
				getConnection().close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
