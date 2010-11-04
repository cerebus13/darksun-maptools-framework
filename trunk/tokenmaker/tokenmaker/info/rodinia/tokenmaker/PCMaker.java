package info.rodinia.tokenmaker;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.event.*;
import javax.swing.filechooser.*;

/**
 * PCMaker provides a GUI class to build a PC Token.
 * 
 * @author Blakey, Summer 2010
 */
public class PCMaker extends JFrame {

    private JTextField nameText;
    private JButton loadButton;
    private JTextField raceText;
    private JTextField classText;
    private JTextField levelText;
    private PowerList powerList;
    private JButton portraitButton;
    private JButton saveButton;
    private JButton exitButton;
    private PC myPC = null;
    private ImageIcon myPortrait = null;
    private JTextArea statsLabel;
    private File saveFile;
    private JLabel statusBar;


    public static void main(String[] args) {
	TokenMaker.loadState();
	PCMaker frame = new PCMaker();
	TokenMaker.saveState();
    }

    /**
     * Builds the main frame.
     * 
     * @throws HeadlessException
     */
    public PCMaker() throws HeadlessException {
	super("Player Maker");
	setSize(400, 600);
	setLayout(new BorderLayout());

	// make my listeners
	MyButtonListener eventListener = new MyButtonListener();
	KeyboardListener keyListener = new KeyboardListener();
	ListListener listListener = new ListListener();
	ListFocusListener focusListener = new ListFocusListener();

	// The north panel contains the PCs basic stats
	JPanel northPanel = new JPanel();
	northPanel.setLayout(new GridLayout(3, 1));

	JPanel namePanel = new JPanel();
	JLabel nameLabel = new JLabel("Name:");
	nameText = new JTextField(20);
	nameText.setEditable(false);

	namePanel.add(nameLabel);
	namePanel.add(nameText);
	northPanel.add(namePanel);

	JPanel racePanel = new JPanel();
	JLabel raceLabel = new JLabel("Race:");
	raceText = new JTextField(20);
	raceText.setEditable(false);
	racePanel.add(raceLabel);
	racePanel.add(raceText);
	northPanel.add(racePanel);

	JPanel classPanel = new JPanel();
	JLabel classLabel = new JLabel("Class:");
	classText = new JTextField(20);
	classText.setEditable(false);
	JLabel levelLabel = new JLabel("Level:");
	levelText = new JTextField(2);
	levelText.setEditable(false);
	classPanel.add(classLabel);
	classPanel.add(classText);
	classPanel.add(levelLabel);
	classPanel.add(levelText);
	northPanel.add(classPanel);
	add(northPanel, BorderLayout.NORTH);

	// Add a portrait and a list of the PCs powers in the middle
	JPanel centerPanel = new JPanel();

	// Stats
	String statsTemplate = "Defenses: AC --, Ref  --, Fort --, Will --\n";
	statsTemplate += "HP: -- (Surges --)\n";
	statsTemplate += "Passive Perception: --\n";
	statsTemplate += "Passive Insight: --\n";
	statsTemplate += "Initiative: --\n";
	statsTemplate += "Speed: --";

	statsLabel = new JTextArea(statsTemplate);
	statsLabel.setEditable(false);
	statsLabel.setBorder(BorderFactory.createLineBorder(Color.black));
	statsLabel.setMinimumSize(new Dimension(100, 100));
	centerPanel.add(statsLabel);

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
	portraitButton.addActionListener(eventListener);
	portraitButton.addKeyListener(keyListener);
	portraitButton.setEnabled(false);
	centerPanel.add(portraitPanel, BorderLayout.CENTER);

	/*
	 * Skills List skillsLabel = new JTextArea("  Skills List  ");
	 * skillsLabel.setEditable(false);
	 * skillsLabel.setBorder(BorderFactory.createLineBorder(Color.black));
	 * add(skillsLabel, BorderLayout.EAST);
	 */

	powerList = new PowerList();
	powerList.addMouseListener(new ActionJList(powerList));
	powerList.addListSelectionListener(listListener);
	powerList.addFocusListener(focusListener);
	powerList.addKeyListener(keyListener);

	JScrollPane powerScrollPane = new JScrollPane(powerList);
	powerScrollPane.setPreferredSize(new Dimension(350, 300));
	powerScrollPane.setMinimumSize(new Dimension(10, 10));
	centerPanel.add(powerScrollPane);
	statusBar = new JLabel("Press Load button to load a new PC");

	centerPanel.add(statusBar);
	add(centerPanel, BorderLayout.CENTER);

	// Add a and Exit button on the bottom.
	JPanel southPanel = new JPanel();
	loadButton = new JButton("Load");
	loadButton.addActionListener(eventListener);
	loadButton.addKeyListener(keyListener);

	saveButton = new JButton("Save");
	saveButton.addActionListener(eventListener);
	saveButton.addKeyListener(keyListener);

	exitButton = new JButton("Exit");
	exitButton.addActionListener(eventListener);
	exitButton.addKeyListener(keyListener);

	southPanel.add(loadButton);
	southPanel.add(saveButton);
	southPanel.add(exitButton);
	add(southPanel, BorderLayout.SOUTH);

	addWindowFocusListener(new WindowAdapter() {
	    public void windowGainedFocus(WindowEvent e) {
		loadButton.requestFocusInWindow();
	    }
	});

	// quickly check that we have login details.
	File file = new File(TokenMaker.ddiFile);
	if (!file.exists()) {
	    DDi ddi = new DDi(this);
	}

	setVisible(true);
    }

    /**
     * Pop up a frame with the power description in it. We need to go off to the
     * internet to get these details.
     */
    private void showPower() {
	Power p = (Power) powerList.getSelectedValue();
	statusBar.setText("Getting Compendium Entry for " + p.getName());
	this.validate();
	// only now should we look up the ID for the power.
	p.setId(CompendiumSearcher.getPowerID(p.getName()));
	if (p.getId() != 0) {
	    PowerFrame pf = new PowerFrame(p);
	    pf.setVisible(true);
	}
	statusBar.setText("Done");
    }

    /**
     * Let the user select a new portrait for their token.
     */
    private void choosePortrait() {
	try {
	    JFileChooser chooser = new JFileChooser(TokenMaker.pcImagePath);
	    ImagePreviewPanel preview = new ImagePreviewPanel();
	    chooser.setAccessory(preview);
	    chooser.addPropertyChangeListener(preview);
	    chooser.addChoosableFileFilter(new FileNameExtensionFilter(
		    "Portrait", "png", "jpg", "jpeg"));
	    int returnVal = chooser.showOpenDialog(this);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
		myPC.setPortrait(chooser.getSelectedFile());
		myPortrait = new ImageIcon(myPC.getPortrait().getAbsolutePath());
		portraitButton.setIcon(myPortrait);
		TokenMaker.pcImagePath = chooser.getCurrentDirectory()
			.getAbsolutePath();
	    }
	} catch (Exception e) {
	    System.err.println("Error selecting a portrait: " + e);
	}
    }

    /**
     * Save the PC off as a MapTool token.
     */
    private void savePC() {
	try {
	    JFileChooser chooser = new JFileChooser(TokenMaker.pcSavePath);
	    chooser.addChoosableFileFilter(new FileNameExtensionFilter(
		    "MapTool Token", "rptok"));
	    int returnVal = chooser.showOpenDialog(this);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
		saveFile = chooser.getSelectedFile();
		if (!saveFile.getName().endsWith(".rptok")) {
		    saveFile = new File(saveFile.getAbsolutePath() + ".rptok");
		}
		PCToken token = new PCToken(myPC);
		token.setTokenFile(saveFile);
		token.setTokenName(saveFile.getName().replace(".rptok", ""));
		token.save();
		statusBar.setText(myPC.getName() + " saved successfully.");
		TokenMaker.pcSavePath = chooser.getCurrentDirectory()
			.getAbsolutePath();
	    }

	} catch (Exception e) {
	    statusBar.setText("Error saving " + myPC.getName());
	    this.validate();
	    System.err.println("Error saving your PC: " + e);
	}
    }

    /**
     * Load the PC from disk file. The user selects the disk file and then the
     * PCDataAccessor class is used to open and parse this file to create the PC
     * which we will display
     */
    private void loadPC() {
	try {
	    JFileChooser chooser = new JFileChooser(TokenMaker.pcLoadPath);
	    chooser.addChoosableFileFilter(new FileNameExtensionFilter(
		    "Character Builder Save File", "dnd4e"));
	    int returnVal = chooser.showOpenDialog(this);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {

		// load the PC object from the save file
		PCDataAccessor data = new PCDataAccessor();
		myPC = data.loadSaveFile(chooser.getSelectedFile());
		statusBar.setText(myPC.getName() + " loaded successfully.");

		// now populate all our fields from the PC object
		nameText.setText(myPC.getName());
		raceText.setText(myPC.getRace());
		classText.setText(myPC.getPCClass());
		levelText.setText("" + myPC.getLevel());
		statsLabel.setText(myPC.getStats());
		myPortrait = new ImageIcon(myPC.getPortrait().getAbsolutePath());
		portraitButton.setIcon(myPortrait);
		portraitButton.setEnabled(true);
		// skillsLabel.setText(myPlayer.getSkillsList());
		ArrayList<Power> powers = new ArrayList<Power>();
		powers = myPC.getPowers();
		powerList.setListData(powers.toArray());

		// remember where we went to look for the file.
		TokenMaker.pcLoadPath = chooser.getCurrentDirectory()
			.getAbsolutePath();
	    }
	} catch (Exception e) {
	    System.err.println("Error loading PC: " + e);
	    System.err.println("Traceback: " + e.getStackTrace());
	}
    }



    class MyButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent event) {
	    if (event.getSource() == saveButton) {
		savePC();
	    } else if (event.getSource() == portraitButton) {
		choosePortrait();
	    } else if (event.getSource() == loadButton) {
		loadPC();
	    } else if (event.getSource() == exitButton) {
		TokenMaker.saveState();
		System.exit(0);
	    }
	}
    }

    class ActionJList extends MouseAdapter {
	protected JList list;

	public ActionJList(JList l) {
	    list = l;
	}

	public void mouseClicked(MouseEvent e) {
	    if (e.getClickCount() == 2) {
		int index = list.locationToIndex(e.getPoint());
		ListModel dlm = list.getModel();
		Object item = dlm.getElementAt(index);
		;
		list.ensureIndexIsVisible(index);
		showPower();
	    }
	}
    }

    class KeyboardListener extends KeyAdapter {
	public void keyTyped(KeyEvent e) {
	    if (e.getSource() == exitButton) {
		System.exit(0);
	    } else if (e.getSource() == saveButton) {
		savePC();
	    } else if (e.getSource() == portraitButton) {
		choosePortrait();
	    } else if (e.getSource() == loadButton) {
		loadPC();
	    }
	}
    }

    class ListListener implements ListSelectionListener {
	public void valueChanged(ListSelectionEvent e) {

	}
    }

    class ListFocusListener implements FocusListener {
	@Override
	public void focusGained(FocusEvent e) {
	    powerList.setSelectedIndex(0);
	}

	@Override
	public void focusLost(FocusEvent e) {
	    // TODO Auto-generated method stub

	}
    }

    class PowerList extends JList {
	public String getToolTipText(MouseEvent event) {
	    // Get the mouse location
	    Point point = event.getPoint();

	    // Get the item in the list box at the mouse location
	    int index = this.locationToIndex(point);
	    if ((this.getModel().getElementAt(index)) instanceof String)
		return "";
	    if ((this.getModel().getElementAt(index)) instanceof Power) {

		// Get the value of the item in the list
		Power p = null;
		if (getModel() != null) {
		    p = (Power) this.getModel().getElementAt(index);
		}
		if (p != null && p.getWeapons().size() != 0)
		    return p.toString() + " - "
			    + p.getWeapons().get(0).toString();
		else
		    return "";
	    }
	    return "";

	}

    }
}
