package ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Toolkit;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import main.ServerController;

public class ServerGUI extends JFrame {

	private static final long serialVersionUID = -2255002464550713814L;
	
	private final static int DEFAULT_WIDTH = 1;
	private final static int DEFAULT_HEIGHT = 1;
	
	private final static int PANEL_WIDTH = 500;
	private final static int PANEL_HEIGHT = 500;
	
	private final static int FILL_HORIZONTAL = GridBagConstraints.HORIZONTAL;
	private final static int FILL_VERTICAL = GridBagConstraints.VERTICAL;
	private final static int FILL_BOTH = GridBagConstraints.BOTH;
	private final static int FILL_NONE = GridBagConstraints.NONE;
	
	private JLabel lblServerPort;
	private JTextField txtServerPort;
	
	private static JTextArea txtConversation;
	private JScrollPane conversationPane;
	
	private JTextField txtMessage;
	private JScrollPane messagePane;
	
	private JButton btnListen;
	private JButton btnSend;
	private UISetup setup;
	
	public ServerGUI (String title) {
		super(title);
		setup = new UISetup();
		initComponents();

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
		// Initialize event container
		ServerController.EventContainer eventContainer = new ServerController.EventContainer();
		
		// Add  action listeners to components
		txtMessage.addActionListener(eventContainer.pressEnterToSend(txtMessage));
		
		btnListen.addActionListener(eventContainer.clickToListenOnPort(txtServerPort));

		btnSend.addActionListener(eventContainer.clickToSend(txtMessage));

	}
	
	/**
     * Show the message on conversation window
     */
    public static void showMessage(String message){
	    	SwingUtilities.invokeLater(
	    		new Runnable (){
	    			public void run(){
	    				txtConversation.append("\n");
	    				txtConversation.append(message);
	    			}
	    		});
    }
}
