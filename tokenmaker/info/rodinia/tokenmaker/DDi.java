package info.rodinia.tokenmaker;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

import javax.swing.*;


/**
 * DDi allows the user to enter their DDi Login details.   
 * These are saved off to the file system for future reference
 * 
 * @author Blakey - Summer 2010
 *
 */

public class DDi extends JDialog {
    private JButton saveButton;
    private JButton exitButton;
    private JTextField username;
    private JPasswordField password;

    public DDi(JFrame frame) {
	super(frame, "Enter DDi Login Details", true);
	try {
	    setLayout(new BorderLayout());
	    setSize(350, 150);

	    // Create the listeners we're going to use
	    MyButtonListener buttonListener = new MyButtonListener();
	    KeyboardListener keyListener = new KeyboardListener();

	    JPanel centralPanel = new JPanel();
	    
	    // Create a simple panel for username
	    JPanel userPanel = new JPanel();
	    JLabel userLabel = new JLabel("Username:");
	    userPanel.add(userLabel);
	    username = new JTextField("your@email.here", 20);
	    userPanel.add(username);
	    centralPanel.add(userPanel);
	    // Create a simple panel for password
	    JPanel passPanel = new JPanel();
	    JLabel passLabel = new JLabel("Password:");
	    passPanel.add(passLabel);
	    password = new JPasswordField("", 20);
	    passPanel.add(password);
	    centralPanel.add(passPanel);
	    
	    add(centralPanel, BorderLayout.CENTER);


	    // Create a panel to deal with save/cancel
	    JPanel southPanel = new JPanel();
	    saveButton = new JButton("Save");
	    saveButton.addActionListener(buttonListener);
	    saveButton.addKeyListener(keyListener);
	    exitButton = new JButton("Cancel");
	    exitButton.addActionListener(buttonListener);
	    exitButton.addKeyListener(keyListener);
	    southPanel.add(saveButton);
	    southPanel.add(exitButton);
	    add(southPanel, BorderLayout.SOUTH);
	    
	    setVisible(true);

	} catch (Exception e) {
	    System.err.println("Error creating the NPC Builder dialog: " + e);
	}

    }

    private void close() {
	System.exit(-1);
    }
    
    private void save() {
	try {
	    File file = new File(TokenMaker.ddiFile);
	    FileWriter w = new FileWriter(file);
	    BufferedWriter writer = new BufferedWriter(w);
	    writer.write(username.getText() + "\n");
	    char[] pw = password.getPassword();
	    String pass = "";
	    for (char c : pw) pass += c;
	    writer.write(pass + "\n");
	    writer.close();
	    setVisible(false);
	}
	catch (Exception e) {
	    System.err.println("Error creating DDi login file: " +e);
	}
    }

    class MyButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent event) {
	    if (event.getSource() == exitButton) {
		close();
	    }
	    if (event.getSource() == saveButton) {
		save();
	    }
	}
    }

    class KeyboardListener extends KeyAdapter {
	public void keyTyped(KeyEvent event) {
	    if (event.getSource() == exitButton) {
		close();
	    }
	    if (event.getSource() == saveButton) {
		save();
	    }
	}
    }
}
