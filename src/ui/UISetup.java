package ui;

import java.awt.BorderLayout;
import java.awt.ComponentOrientation;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class UISetup {
	
	private final static int DEFAULT_PADDING = 5;

	public UISetup(){
		
	}
	
	JPanel createPanel(int width, int height) {
		// Create panel of send mail's form then set panel's size
		JPanel chatPanel = new JPanel();
		Dimension panelSize = chatPanel.getPreferredSize();
		panelSize.width = width;
		panelSize.height = height;
		chatPanel.setPreferredSize(panelSize);
		chatPanel.setLayout(new GridBagLayout());
		chatPanel.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		return chatPanel;
	}
	
	/**
	 * Create and show GUI on screen
	 */
	JFrame createFrame(String title, int closeOperation, JPanel panel) {
		// Create form's frame and set layout
		JFrame frame  = new JFrame(title);
		frame.setDefaultCloseOperation(closeOperation);
		frame.setLayout(new BorderLayout());
		
		// Add panel to frame
		frame.add(panel);
		
		// Packing frame with components
		frame.pack();
		// Set location of frame after packing
		setFrameLocationOnScreen(frame);
		// Show the frame
		frame.setVisible(true);
		
		return frame;
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
	
	GridBagConstraints getContraints(int gridx, int gridy, 
			int gridwidth, int gridheight,
			double weightx, double weighty,
			int constraintFill) {
		// Create new GridBag constraint
		GridBagConstraints c = new GridBagConstraints();
		
		// Set constraint's position
		c.gridx = gridx;
		c.gridy = gridy;
		
		// Set constraint's dimension
		c.gridwidth = gridwidth;
		c.gridheight = gridheight;
		
		// Set constraint weights
		c.weightx = weightx;
		c.weighty = weighty;
		
		// Set constraint's fill
		c.fill = constraintFill;
		
		// Set constraint Insets
		c.insets = new Insets(DEFAULT_PADDING,DEFAULT_PADDING,DEFAULT_PADDING,DEFAULT_PADDING);
		
		return c;
	}
}
