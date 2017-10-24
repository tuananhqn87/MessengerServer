package main;


import javax.swing.JFrame;

import ui.ServerGUI;

public class ServerProgram {

	public static void main(String[] args) {
		ServerGUI server = new ServerGUI("Messenger Server");
		server.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		server.createAndShowGUI();
	}
}
