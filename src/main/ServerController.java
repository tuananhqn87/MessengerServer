package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JTextField;

import database.DBQuery;
import ui.ServerGUI;

public class ServerController {
	private final static String XTER_PREFIX = "xTer: ";
	private final static String HANNAH_PREFIX = "Hannah: ";
	private final static String PORT_REGEX = "[0-9]+";
	private final static String END_COMMAND = "end";
	private final static String DISCONNECT = "Server ended the connection!";
	private final static String WAIT_FOR_CONNECTING = "Waiting for someone to connect... ";
	private final static String CONNECTED_TO = "Now connected to ";
	private final static String CONNECTED = "You are now connected to the server!";
	
	private static String remoteUser;
	private static String localUser;
	
	private static DBQuery dbQuery;
	private static ServerSocket serverSocket;
	private static Socket socket;
	private static ObjectOutputStream outputStream;
	private static ObjectInputStream inputStream;
	private static boolean isStreamsSetup;

	static void startRunning(int port) throws IOException {
		dbQuery = new DBQuery();
		serverSocket = new ServerSocket(port);
		while(true){
			try {
				waitForConnection();
				setupStreams();
				chattingInstance();
			} catch (EOFException eof){
				eof.printStackTrace();
				ServerGUI.showMessage(DISCONNECT);
			}
		} 
	}
	
	/** 
	 * Wait for connection 
	 */
    static void waitForConnection() throws IOException {
	    	ServerGUI.showMessage(WAIT_FOR_CONNECTING);
	    	socket = serverSocket.accept();
	    	ServerGUI.showMessage(CONNECTED_TO + socket.getInetAddress().getHostName());
    }

    /**
     * Set up streams
     */
    static void setupStreams() throws IOException {
	    	// Create output stream from socket
	    	outputStream = new ObjectOutputStream(socket.getOutputStream());
	    	outputStream.flush();
	    	
	    	// Create input stream from socket
	    	inputStream = new ObjectInputStream(socket.getInputStream());

	    	isStreamsSetup = true;
    }
    
    /**
     * Maintain chatting's instance
     */
    static void chattingInstance() throws IOException {
    		ServerGUI.showMessage(CONNECTED);
	    	sendMessage(CONNECTED);
	    receiveMessage();
    }
    
    static void receiveMessage() throws IOException {
    		String message = null;
	    	 do {
	    		try {
	    			message = (String) inputStream.readObject();
	    			ServerGUI.showMessage(message);
	    			dbQuery.insert(remoteUser, localUser, message);
	    		} catch (ClassNotFoundException e) {
	    			e.printStackTrace();
	    		}
	    	} while(!message.equalsIgnoreCase(END_COMMAND));
    }
    
    static void sendMessage(String message) throws IOException {
    		if (isStreamsSetup) {
	    		// Write the message object to output stream
	    		outputStream.writeObject(message);
	    		// Flush output stream
	    		outputStream.flush();
	    		// Insert message to database
	    		dbQuery.insert(localUser, remoteUser, message);
	    		// Show message
	    		ServerGUI.showMessage("Me: " + message);
    		} else {
    			ServerGUI.showMessage("Server is not ready for a connection!");
    		}
    }
    /**
     * Method to close the connection
     */
    public static void closeConnection() {
        try {
	        	outputStream.close();
	        	inputStream.close();
            socket.close();
            isStreamsSetup = false;
        } catch (IOException e) {
        		e.printStackTrace();
        }
    }
    
    public static class EventContainer {
    		
    		/**
    		 * An action listener method to listen for enter key be pressed
    		 * 
    		 * @param textField The text field where user inputs the message
    		 * @return The ActionListener of pressing enter event
    		 */
    		public ActionListener pressEnterToSend(JTextField textField) {
    			return new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						try {
							sendMessage(e.getActionCommand());
						} catch (IOException ioe) {
							ioe.printStackTrace();
						}
						textField.setText("");
					}
				};
    		}
    		
    		/**
    		 * An action listener method to listen for send button be clicked
    		 * 
    		 * @param textField The text field where user inputs the message
    		 * @return The ActionListener of clicking Listen button event
    		 */
    		public ActionListener clickToSend(JTextField textField) {
    			return new ActionListener() {
    				@Override
    				public void actionPerformed(ActionEvent e) {
    					String message = textField.getText();
    					try {
    						sendMessage(message);
    					} catch (IOException ioe) {
    						ioe.printStackTrace();
    					}
    					textField.setText("");
    				}
    			};
    		}
    		
    		/**
    		 * An action listener method to listen for Listen button be clicked
    		 * 
    		 * @param serverPortField The text field where user inputs the port to be connected to
    		 * @return The ActionListener of clicking Listen button event
    		 */
    		public ActionListener clickToListenOnPort(JTextField serverPortField) {
    			
    			ActionListener listener = new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						String serverPortText = serverPortField.getText();
			    			int port;
			    			if(!serverPortText.equals("") && serverPortText.matches(PORT_REGEX)){
							port = Integer.parseInt(serverPortText);
							if(port > 1024 && port <= 65535){
								new Thread() {
									public void run() {
										try {
											startRunning(port);
										} catch (IOException ioe) {
											ioe.printStackTrace();
										}
									}
								}.start();
							} else {
								ServerGUI.showMessage("Your port number must be between 1024 and 65535");
								ServerGUI.showMessage("Failed to start");
							}
						} else if (serverPortText.equals("")){
							ServerGUI.showMessage("Please provide port number");
						} else {
							ServerGUI.showMessage("Port has to be a number between 1024 and 65535"
									+ "\nFailed to start");
						}
					}
				};
    			
    			return listener;
    		}
    	
    }
}
