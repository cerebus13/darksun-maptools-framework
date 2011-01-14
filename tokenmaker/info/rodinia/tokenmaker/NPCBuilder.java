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
public class NPCBuilder extends JDialog {

    private NPC myNPC = null;
    private Object[] myNPCarray = null;
    private JButton saveButton;
    private JButton exitButton;
    private File portraitFile;
    private JButton portraitButton;
    private ImageIcon myPortrait = null;
    private JRadioButton blakeyRadio;
    private JRadioButton joeRadio;
    private JRadioButton xmlRadio;

    /**
     * We construct this frame from a base NPC object.
     * 
     * @param npc - the NPC to build.
     */
    public NPCBuilder(NPC npc)
    {
        this.setTitle(npc.getName());

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
        southPanel.setLayout(new GridLayout(2, 3));
        ButtonGroup radioGroup = new ButtonGroup();
        joeRadio = new JRadioButton("Joe token");
        blakeyRadio = new JRadioButton("Blakey token");
        xmlRadio = new JRadioButton("Monster XML");
        radioGroup.add(joeRadio);
        radioGroup.add(blakeyRadio);
        radioGroup.add(xmlRadio);
        joeRadio.setSelected(true);
        southPanel.add(joeRadio);
        southPanel.add(blakeyRadio);
        southPanel.add(xmlRadio);
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
        this.setTitle("Multiple NPCs");
	
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
        southPanel.setLayout(new GridLayout(2, 3));
        ButtonGroup radioGroup = new ButtonGroup();
        joeRadio = new JRadioButton("Joe token");
        blakeyRadio = new JRadioButton("Blakey token");
        xmlRadio = new JRadioButton("Monster XML");
        radioGroup.add(joeRadio);
        radioGroup.add(blakeyRadio);
        radioGroup.add(xmlRadio);
        joeRadio.setSelected(true);
        southPanel.add(joeRadio);
        southPanel.add(blakeyRadio);
        southPanel.add(xmlRadio);
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
        try
        {
            int returnVal = JFileChooser.CANCEL_OPTION;
            if (myNPCarray == null)
            {
                JFileChooser chooser = new JFileChooser(TokenMaker.npcSavePath);
                String suffix;
                if (!xmlRadio.isSelected())
                {
                    suffix = ".rptok";
                    chooser.addChoosableFileFilter(new FileNameExtensionFilter("MapTool Token", "rptok"));
                }
                else
                {
                    suffix = ".monster";
                    chooser.addChoosableFileFilter(new FileNameExtensionFilter("Monster XML", "monster"));
                }
                returnVal = chooser.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File saveFile = chooser.getSelectedFile();
                    if (!saveFile.getName().endsWith(suffix)) {
                        saveFile = new File(saveFile.getAbsolutePath() + suffix);
                    }
                    String pt = "Basic";
                    if (joeRadio.isSelected())
                        pt = "Joe 4e Monster";
                    else if (xmlRadio.isSelected())
                        pt = "XML";
                    NPCToken token = new NPCToken(myNPC,pt,joeRadio.isSelected());
                    token.setPortrait(portraitFile);
                    token.setTokenFile(saveFile);
                    token.setTokenName(saveFile.getName().replace(suffix, ""));
                    if (xmlRadio.isSelected())
                        token.saveXML();
                    else
                        token.save();
                    TokenMaker.npcSavePath = chooser.getCurrentDirectory().getAbsolutePath();
                }
            }
            else
            {
                String suffix;
                JFileChooser chooser = new JFileChooser(TokenMaker.npcSavePath);
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                returnVal = chooser.showOpenDialog(this);
                if (returnVal == JFileChooser.APPROVE_OPTION)
                {
                    File saveFolder = chooser.getSelectedFile();
                    for (Object n : myNPCarray)
                    {
                        NPC myN = (NPC) n;
                        myN.getHtml();
                        if (xmlRadio.isSelected())
                            suffix = ".monster";
                        else
                            suffix = ".rptok";
                        File saveFile = new File(saveFolder.getAbsolutePath() + "\\" + myN.getName() + suffix);
                        String pt = "Basic";
                        if (joeRadio.isSelected())
                            pt = "Joe 4e Monster";
                        else if (xmlRadio.isSelected())
                            pt = "XML";
                        NPCToken token = new NPCToken(myN,pt,joeRadio.isSelected());
                        token.setPortrait(portraitFile);
                        token.setTokenFile(saveFile);
                        token.setTokenName(myN.getName());
                        if (xmlRadio.isSelected())
                            token.saveXML();
                        else
                            token.save();
                    }
                    TokenMaker.npcSavePath = chooser.getCurrentDirectory().getAbsolutePath();
                }
                closeBuilder();
            }
            if (returnVal == JFileChooser.APPROVE_OPTION)
                JOptionPane.showMessageDialog(this, "Done building!");
        }
        catch (Exception e) {
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
