package info.rodinia.tokenmaker;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/**
 * NPCBuilder is the GUI class that actually does the work of creating an NPC token on disk.
 * It provides a frame which displays a monster's compendium entry
 * and a button to select and preview a portrait for that monster.
 * Pressing the save button gives a save file dialog box and saves the NPC off as a MapTool token.
 *   
 * @author Blakey, Summer 2010
 */
public class NPCBuilder extends JFrame {

    private NPC myNPC = null;
    private Object[] myNPCarray = null;
    private JButton saveButton;
    private JButton exitButton;
    private File portraitFile;
    private JButton portraitButton;
    private ImageIcon myPortrait = null;

    /**
     * We construct this frame from a base NPC object.
     * 
     * @param npc - the NPC to build.
     */
    public NPCBuilder(NPC npc) {
	super(npc.getName());

	//System.setProperty("http.proxyHost","212.118.224.147") ;
	//System.setProperty("http.proxyPort", "80") ;
	
	myNPC = npc;
	try {
	    setLayout(new BorderLayout());
	    setSize(400, 600);

	    // Create the listeners we're going to use
	    MyButtonListener buttonListener = new MyButtonListener();
	    KeyboardListener keyListener = new KeyboardListener();

	    // Create a panel to deal with setting up the portrait and save file
	    // and actually save it
	    // Portrait
	    JPanel portraitPanel = new JPanel();
	    portraitButton = new JButton("Portrait") {
		@Override
		public void paintComponent(Graphics g) {

		    if (myPortrait == null) {
			super.paintComponent(g);
			return;
		    } else {
			g.drawImage(myPortrait.getImage(), 0, 0, getWidth(),
				getHeight(), null);
		    }
		}
	    };
	    portraitPanel.add(portraitButton);
	    portraitButton.setPreferredSize(new Dimension(100, 100));
	    portraitButton.addActionListener(buttonListener);
	    portraitButton.addKeyListener(keyListener);
	    add(portraitPanel, BorderLayout.NORTH);

	    // Create an editor pane in the middle
	    JPanel editorPanel = new JPanel();
	    JEditorPane editorPane = new JEditorPane(new HTMLEditorKit()
		    .getContentType(), npc.getHtml());

	    editorPane.setEditable(false);
	    JScrollPane editorScrollPane = new JScrollPane(editorPane);
	    editorScrollPane
		    .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    editorScrollPane.setPreferredSize(new Dimension(350, 400));
	    editorScrollPane.setMinimumSize(new Dimension(10, 10));
	    editorPanel.add(editorScrollPane);
	    add(editorPanel, BorderLayout.CENTER);
	    // force the scroll pane to start at the top.
	    editorPane.setCaretPosition(0);

	    // Create a panel to deal with building
	    JPanel southPanel = new JPanel();
	    saveButton = new JButton("Save");
	    saveButton.addActionListener(buttonListener);
	    saveButton.addKeyListener(keyListener);
	    saveButton.setEnabled(false);
	    exitButton = new JButton("Exit");
	    exitButton.addActionListener(buttonListener);
	    exitButton.addKeyListener(keyListener);
	    southPanel.add(saveButton);
	    southPanel.add(exitButton);
	    add(southPanel, BorderLayout.SOUTH);

	} catch (Exception e) {
	    System.err.println("Error creating the NPC Builder dialog: " + e);
	}

    }
    
    /**
     * Constructor that takes in an array of NPCs (as an Object).
     * 
     * @param npc - the NPC array to build.
     */
    public NPCBuilder(Object[] npc) {
	super("Multiple NPCs");
	
	myNPCarray = npc;
	try {
	    setLayout(new BorderLayout());
	    setSize(400, 600);

	    // Create the listeners we're going to use
	    MyButtonListener buttonListener = new MyButtonListener();
	    KeyboardListener keyListener = new KeyboardListener();

	    // Create a panel to deal with setting up the portrait and save file
	    // and actually save it
	    // Portrait
	    JPanel portraitPanel = new JPanel();
	    portraitButton = new JButton("Portrait") {
		@Override
		public void paintComponent(Graphics g) {

		    if (myPortrait == null) {
			super.paintComponent(g);
			return;
		    } else {
			g.drawImage(myPortrait.getImage(), 0, 0, getWidth(),
				getHeight(), null);
		    }
		}
	    };
	    portraitPanel.add(portraitButton);
	    portraitButton.setPreferredSize(new Dimension(100, 100));
	    portraitButton.addActionListener(buttonListener);
	    portraitButton.addKeyListener(keyListener);
	    add(portraitPanel, BorderLayout.NORTH);

	    // Create a panel to deal with building
	    JPanel southPanel = new JPanel();
	    saveButton = new JButton("Save");
	    saveButton.addActionListener(buttonListener);
	    saveButton.addKeyListener(keyListener);
	    saveButton.setEnabled(false);
	    exitButton = new JButton("Exit");
	    exitButton.addActionListener(buttonListener);
	    exitButton.addKeyListener(keyListener);
	    southPanel.add(saveButton);
	    southPanel.add(exitButton);
	    add(southPanel, BorderLayout.SOUTH);

	} catch (Exception e) {
	    System.err.println("Error creating the NPC Builder dialog: " + e);
	}

    }

    private void selectPortraitFile() {
	try {
	    JFileChooser chooser = new JFileChooser(TokenMaker.npcImagePath);
	    ImagePreviewPanel preview = new ImagePreviewPanel();
	    chooser.setAccessory(preview);
	    chooser.addPropertyChangeListener(preview);
	    chooser.addChoosableFileFilter(new FileNameExtensionFilter(
		    "Portrait", "png", "jpg", "jpeg"));
	    int returnVal = chooser.showOpenDialog(this);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
		portraitFile = chooser.getSelectedFile();
		myPortrait = new ImageIcon(portraitFile.getAbsolutePath());
		portraitButton.setIcon(myPortrait);
		TokenMaker.npcImagePath = chooser.getCurrentDirectory()
			.getAbsolutePath();
		saveButton.setEnabled(true);
	    }
	} catch (Exception e) {
	    System.err.println("Error selecting a portrait: " + e);
	}
    }

    private void closeBuilder() {
	setVisible(false);
    }

    private void saveNPC() {
	try {
	    if (myNPCarray == null)
	    {
        	    JFileChooser chooser = new JFileChooser(TokenMaker.npcSavePath);
        	    chooser.addChoosableFileFilter(new FileNameExtensionFilter(
        		    "MapTool Token", "rptok"));
        	    int returnVal = chooser.showOpenDialog(this);
        	    if (returnVal == JFileChooser.APPROVE_OPTION) 
        	    {
        		File saveFile = chooser.getSelectedFile();
        		if (!saveFile.getName().endsWith(".rptok")) {
        		    saveFile = new File(saveFile.getAbsolutePath() + ".rptok");
        		}
        		NPCToken token = new NPCToken(myNPC);
        		token.setPortrait(portraitFile);
        		token.setTokenFile(saveFile);
        		token.setTokenName(saveFile.getName().replace(".rptok", ""));
        		token.save();
        		TokenMaker.npcSavePath = chooser.getCurrentDirectory()
        			.getAbsolutePath();
        	    }
	    }
	    else
	    {
		JFileChooser chooser = new JFileChooser(TokenMaker.npcSavePath);
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); 
    	    	int returnVal = chooser.showOpenDialog(this);
    	    	if (returnVal == JFileChooser.APPROVE_OPTION)
    	    	{
    	    	    File saveFolder = chooser.getSelectedFile();
    	    	    for (Object n : myNPCarray)
    	    	    {
    	    		NPC myN = (NPC) n;
    	    		File saveFile = new File(saveFolder.getAbsolutePath() + "\\" + myN.getName() + ".rptok");
    	    		NPCToken token = new NPCToken(myN);
        	    	token.setPortrait(portraitFile);
            	    	token.setTokenFile(saveFile);
            	    	token.setTokenName(myN.getName());
            	    	token.save();
    	    	    }
    	    	    TokenMaker.npcSavePath = chooser.getCurrentDirectory().getAbsolutePath();
    	    	}
    	    	closeBuilder();
	    }
	} catch (Exception e) {
	    System.err.println("Error saving your NPC(s): " + e);
	}
    }

    class MyButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent event) {
	    if (event.getSource() == portraitButton) {
		selectPortraitFile();
	    }
	    if (event.getSource() == exitButton) {
		closeBuilder();
	    }
	    if (event.getSource() == saveButton) {
		saveNPC();
	    }
	}
    }

    class KeyboardListener extends KeyAdapter {
	public void keyTyped(KeyEvent event) {
	    if (event.getSource() == portraitButton) {
		selectPortraitFile();
	    }
	    if (event.getSource() == exitButton) {
		closeBuilder();
	    }
	    if (event.getSource() == saveButton) {
		saveNPC();
	    }
	}
    }
}
