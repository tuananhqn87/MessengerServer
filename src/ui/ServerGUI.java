package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import database.DBWriter;



public class ServerGUI extends JFrame {
	private final static int DEFAULT_WIDTH = 1;
	private final static int DEFAULT_HEIGHT = 1;
	
	private final static int PANEL_WIDTH = 500;
	private final static int PANEL_HEIGHT = 500;
	
	private final static int FILL_HORIZONTAL = GridBagConstraints.HORIZONTAL;
	private final static int FILL_VERTICAL = GridBagConstraints.VERTICAL;
	private final static int FILL_BOTH = GridBagConstraints.BOTH;
	private final static int FILL_NONE = GridBagConstraints.NONE;
	
	private final static String REGEX = "[0-9]+";
	
	private final static String XTER_PREFIX = "xTer: ";
	private final static String HANNAH_PREFIX = "Hannah: ";

	private JLabel lblServerPort;
	private JTextField txtServerPort;
	
	private JTextArea txtConversation;
	private JScrollPane conversationPane;
	
	private JTextField txtMessage;
	private JScrollPane messagePane;
	
	private JButton btnListen;
	private JButton btnSend;
	private UISetup setup;
	
	private ServerSocket serverSocket;
	private Socket socket;
	private ObjectOutputStream outputStream;
	private ObjectInputStream inputStream;
	
	private int listeningPort;
//	private int dbId;
	
	public ServerGUI (String title) {
		super(title);
		setup = new UISetup();
		initComponents();
//		dbId = 0;
	}
	
	/**
	 * Create and show GUI on screen
	 */
	public void createAndShowGUI () {
		
		// Create panel of send mail's form then set panel's size
		JPanel chatPanel = setup.createPanel(PANEL_WIDTH, PANEL_HEIGHT);
		
		// Add all component to panel
		addComponents(chatPanel);
		
		createFrame(chatPanel);
		addBehaviors();
	}
	
	/**
	 * Method to initialize components' variables
	 */
	private void initComponents() {
		
		lblServerPort = new JLabel("Port");
		txtServerPort = new JTextField();
		
		txtConversation = new JTextArea();
		txtConversation.setFont(new Font("Serif", Font.TRUETYPE_FONT, 16));
		txtConversation.setLineWrap(true);
		txtConversation.setWrapStyleWord(true);
		txtConversation.setEditable(false);
		conversationPane = new JScrollPane(txtConversation);
		
		txtMessage = new JTextField();
		txtMessage.setFont(new Font("Serif", Font.TRUETYPE_FONT, 16));
		
		messagePane = new JScrollPane(txtMessage);
		
		btnListen = new JButton("Listen");
		btnSend = new JButton("SEND");
	}
	
	private void createFrame(JPanel panel) {
		// set layout
		this.setLayout(new BorderLayout());
		
		// Add panel to frame
		this.add(panel);
		
		// Packing frame with components
		this.pack();
		// Set location of frame after packing
		setFrameLocationOnScreen(this);
		// Show the frame
		this.setVisible(true);
	}
	
	/**
	 * Method to set location of a frame on screen when it's shown
	 * @param frame The frame object which will be set location
	 */
	private void setFrameLocationOnScreen(JFrame frame) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(screenSize.width/2 - frame.getSize().width/2, 
						  screenSize.height/2 - frame.getSize().height/2);
	}
	
	/**
	 * Method to add all components to content pane, 
	 * components will be arranged accordingly on the pane
	 * @param contentPane The content pane where components are arranged on.
	 */
	private void addComponents(Container contentPane) {
		
		JComponent components[] = { lblServerPort,
									txtServerPort,
									btnListen,
									conversationPane,
									messagePane,
									btnSend };
		
		GridBagConstraints constraints[] = {
				setup.getContraints(0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, 0.0, 0.0, FILL_HORIZONTAL),
				setup.getContraints(1, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, 1.0, 0.0, FILL_HORIZONTAL),
				setup.getContraints(2, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, 0, 0, FILL_NONE),
				setup.getContraints(0, 2, 3, DEFAULT_HEIGHT, 1.0, 1.0, FILL_BOTH),
				setup.getContraints(0, 3, 2, 2, 1.0, 0, FILL_BOTH),
				setup.getContraints(2, 3, DEFAULT_WIDTH, 2, 0, 0, FILL_VERTICAL)};
		
		for (int i = 0; i < components.length; i++) {
			contentPane.add(components[i], constraints[i]);
		}
	}
	
	/**
	 * Add behaviors of components
	 */
	public void addBehaviors() {
		enableTyping(false);
		txtMessage.addActionListener(
				new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						sendMessage(e.getActionCommand());
						txtMessage.setText("");
					}
				});
		
		btnListen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(){
					public void run(){
						String serverPort = txtServerPort.getText();
						if(serverPort != null && serverPort.matches(REGEX)){
							int port = Integer.parseInt(serverPort);
							if(port > 1024 && port <= 65535){
								setListeningPort(port);
								startRunning();
							} else {
								showMessage("\nYour port number must be between 1024 and 65535");
								showMessage("\nFailed to start");
							}
						} else {
							showMessage("\nPort has to be a number between 1024 and 65535"
									+ "\nFailed to start");
						}
					}
				}.start();
				
			}
		});
		
		btnSend.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String message = txtMessage.getText();
				sendMessage(message);
				txtMessage.setText("");
			}
		});
	}
	public void startRunning() {
		try{
			serverSocket = new ServerSocket(getListeningPort());
			while(true){
				try {
					DBWriter.delete();
					waitForConnection();
					setupStreams();
					chattingInstance();
				} catch (EOFException eof){
					showMessage("\nServer ended the connection! ");
				} finally {
					closeConnection();
				}
			}
		} catch (IOException e){
			txtConversation.setText(txtConversation.getText() + "\n" 
					+ e.getMessage());
		}
	}
	
	/** 
	 * Wait for connection 
	 */
    private void waitForConnection() throws IOException {
    	showMessage("\nWaiting for someone to connect... ");
    	socket = serverSocket.accept();
    	showMessage("\nNow connected to " + socket.getInetAddress().getHostName());
    }

    /**
     * Set up streams
     */
    private void setupStreams() throws IOException {
    	// Create output stream from socket
    	outputStream = new ObjectOutputStream(socket.getOutputStream());
    	outputStream.flush();
    	
    	// Create input stream from socket
    	inputStream = new ObjectInputStream(socket.getInputStream());
    }
    
    /**
     * Maintain chatting's instance
     */
    private void chattingInstance() throws IOException {
    	String message = "You are now connected! ";
    	sendMessage(message);
    	enableTyping(true);
    	do{
    		try{
    			message = (String) inputStream.readObject();
    			showMessage("\n" + message);
    			DBWriter.insert("Xter", "Hannah", message);
    		} catch (ClassNotFoundException classNotFoundException){
    			showMessage("\nThe user has sent an unknown object!");
    			
    		}
    	} while(!message.equalsIgnoreCase(XTER_PREFIX + "End"));
    }
    
    /**
     * Show the message on conversation window
     */
    private void showMessage(String message){
    	SwingUtilities.invokeLater(
    		new Runnable (){
    			public void run(){
    				txtConversation.append(message);
    			}
    		});
    }
    
    private void sendMessage(String message){
    	try {
    		outputStream.writeObject(HANNAH_PREFIX + message);
    		outputStream.flush();
    		DBWriter.insert("Hannah", "Xter", message);
    		showMessage("\nMe: " + message);
    	} catch(IOException ioException){
    		txtConversation.append("\nERROR: CANNOT SEND MESSAGE, PLEASE RETRY!");
    	}
    }
    /** Close the socket */
    public void closeConnection() {
        try {
        	outputStream.close();
        	inputStream.close();
            socket.close();
        } catch (IOException e) {
        	e.printStackTrace();
        }
    }
	
    private void enableTyping(boolean isEnabled){
    	SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					txtMessage.setEditable(isEnabled);
				}
			});
    }
    
    public void setListeningPort(int port){
    	this.listeningPort = port;
    }
    
    public int getListeningPort(){
    	return listeningPort;
    }
}
