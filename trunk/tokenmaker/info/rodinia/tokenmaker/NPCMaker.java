package info.rodinia.tokenmaker;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;

import javax.swing.*;
import javax.swing.event.*;

/**
 * This GUI class provides a frame to allow a user to search for specific NPCs
 * from the compendium and from there to launch the NPCBuilder frame which
 * actually builds the monster token.
 * 
 * @author Blakey, Summer 2010
 * 
 */
public class NPCMaker extends JFrame {

    private JButton findButton;
    private JList npcList;
    private JTextField nameText;
    private JTextField levelMinText;
    private JTextField levelMaxText;
    private JButton buildButton;
    private JButton exitButton;
    private JComboBox roleBox;
    private JComboBox typeBox;
    private JComboBox sourceBox;
    private JRadioButton localRadio;
    private JRadioButton remoteRadio;

    public static void main(String[] args)
    {
        TokenMaker.loadState();
        NPCMaker frame = new NPCMaker();
        TokenMaker.saveState();
    }

    /**
     * Creates a frame which provides a search panel at the top, a list of
     * matching monsters in the middle and buttons to build or exit at the
     * bottom.
     * 
     * @throws HeadlessException
     */
    public NPCMaker() throws HeadlessException {
	super("NPC Maker");
	setSize(400, 600);
	setLayout(new BorderLayout());

	// make my listeners
	MyButtonListener eventListener = new MyButtonListener();
	KeyboardListener keyListener = new KeyboardListener();
	ListListener listListener = new ListListener();
	ListFocusListener focusListener = new ListFocusListener();

	// Add a search panel in the top
	JPanel northPanel = new JPanel();
	northPanel.setLayout(new GridLayout(7, 1));

	JPanel namePanel = new JPanel();
	JLabel nameLabel = new JLabel("Name:");
	nameText = new JTextField(20);
	nameText.addActionListener(eventListener);
	findButton = new JButton("Find");
	findButton.addActionListener(eventListener);
	findButton.addKeyListener(keyListener);
	namePanel.add(nameLabel);
	namePanel.add(nameText);
	namePanel.add(findButton);
	northPanel.add(namePanel);

	JPanel levelPanel = new JPanel();
	JLabel levelLabel = new JLabel("Levels:");
	levelMinText = new JTextField(2);
	levelMinText.addActionListener(eventListener);
	JLabel levelToLabel = new JLabel("to:");
	levelMaxText = new JTextField(2);
	levelMaxText.addActionListener(eventListener);
	levelPanel.add(levelLabel);
	levelPanel.add(levelMinText);
	levelPanel.add(levelToLabel);
	levelPanel.add(levelMaxText);
	northPanel.add(levelPanel);

	JPanel rolePanel = new JPanel();
	JLabel roleLabel = new JLabel("Role:");
	String[] roles = { "", "Lurker", "Soldier", "Artilery", "Brute",
		"Controller", "Skirmisher" };
	roleBox = new JComboBox(roles);
	rolePanel.add(roleLabel);
	rolePanel.add(roleBox);
	northPanel.add(rolePanel);

	JPanel typePanel = new JPanel();
	JLabel typeLabel = new JLabel("Type:");
	String[] types = { "", "Standard", "Minion", "Elite", "Solo" };
	typeBox = new JComboBox(types);
	typePanel.add(typeLabel);
	typePanel.add(typeBox);
	northPanel.add(typePanel);
	
	JPanel sourcePanel = new JPanel();
	JLabel sourceLabel = new JLabel("Source:");
	String[] sources = { "", "Dark Sun Creature Catalog", "Dark Sun Campaign Setting", "Monster Manual", "Monster Manual 2", "Monster Manual 3" };
	sourceBox = new JComboBox(sources);
	sourcePanel.add(sourceLabel);
	sourcePanel.add(sourceBox);
	northPanel.add(sourcePanel);

    // add a local and remote Compendium execution radio button
    JPanel radioPanel = new JPanel();
    ButtonGroup radioGroup = new ButtonGroup();
    localRadio = new JRadioButton("Local compendium");
    remoteRadio = new JRadioButton("Remote compendium");
    radioGroup.add(localRadio);
    radioGroup.add(remoteRadio);
    remoteRadio.setSelected(true);
    radioPanel.add(localRadio);
    radioPanel.add(remoteRadio);
    northPanel.add(radioPanel);

	add(northPanel, BorderLayout.NORTH);

	// Add a List of matching npcs in the middle
	JPanel centerPanel = new JPanel();

	npcList = new JList();
	//npcList.addMouseListener(new ActionJList(npcList));
	npcList.addListSelectionListener(listListener);
	npcList.addFocusListener(focusListener);
	npcList.addKeyListener(keyListener);
	//npcList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

	JScrollPane npcScrollPane = new JScrollPane(npcList);
	npcScrollPane.setPreferredSize(new Dimension(350, 350));
	npcScrollPane.setMinimumSize(new Dimension(10, 10));
	centerPanel.add(npcScrollPane);
	add(centerPanel, BorderLayout.CENTER);

	// Add a Build and Exit button on the bottom.
	JPanel southPanel = new JPanel();
	buildButton = new JButton("Build");
	buildButton.addActionListener(eventListener);
	buildButton.addKeyListener(keyListener);
	buildButton.setEnabled(false);

	exitButton = new JButton("Exit");
	exitButton.addActionListener(eventListener);
	exitButton.addKeyListener(keyListener);

	southPanel.add(buildButton);
	southPanel.add(exitButton);
	add(southPanel, BorderLayout.SOUTH);

	// quickly check that we have login details.
	File file = new File(TokenMaker.ddiFile);
	if (!file.exists()) {
	    DDi ddi = new DDi(this);
	}

	setVisible(true);
    }

    /**
     * Do the search for matching monsters. Populate the list with the results.
     */
    private void performFind() {
	setCursor(Cursor.WAIT_CURSOR);
	ArrayList<NPC> npcs = new ArrayList<NPC>();
	int levelMin = -1;
	String lmin = levelMinText.getText();
	if (!lmin.equals(""))
	    levelMin = Integer.parseInt(lmin);
	int levelMax = -1;
	String lmax = levelMaxText.getText();
	if (!lmax.equals(""))
	    levelMax = Integer.parseInt(lmax);
	String role = (String) roleBox.getSelectedItem();
	if (role.equals(""))
	    role = "null";
	String type = (String) typeBox.getSelectedItem();
	if (type.equals(""))
	    type = "null";
	// Wizards uses integers to represent their books so we have to parse ALL of them from text into their correct number
	String source = (String) sourceBox.getSelectedItem();
	if (source.equals(""))
	    source = "null";
	else if (source.equals("Dark Sun Campaign Setting"))
	    source = "203";
	else if (source.equals("Monster Manual"))
	    source = "2";
	else if (source.equals("Monster Manual 2"))
	    source = "18";
	else if (source.equals("Monster Manual 3"))
	    source = "201";
    else if (source.equals("Dark Sun Creature Catalog"))
        source = "202";
	npcs = (ArrayList<NPC>) CompendiumSearcher.getNPCs(nameText.getText(),
		levelMin, levelMax, role, type, source);
	npcList.setListData(npcs.toArray());
	setCursor(Cursor.DEFAULT_CURSOR);
	buildButton.setEnabled(false);
    }

    /**
     * Build us an NPC based on the selected entry in the list.
     */
    private void buildNPC() {
        if (npcList.getSelectedIndex() == -1)
            return; // nothing selected - do nothing.

        // single selection
        if (npcList.getSelectedIndices().length == 1)
        {
            NPC nPC = (NPC) npcList.getSelectedValue();
            NPCBuilder npcDiag = new NPCBuilder(nPC);
            npcDiag.setModal(true);
            npcDiag.setVisible(true);
        }
        else // multi-selection
        {
            NPCBuilder npcDiag = new NPCBuilder(npcList.getSelectedValues());
            npcDiag.setModal(true);
            npcDiag.setVisible(true);
        }
    }

    /**
     * Button listener inner class
     */
    class MyButtonListener implements ActionListener {
	public void actionPerformed(ActionEvent event) {
	    if (event.getSource() == findButton) {
		performFind();
	    } else if (event.getSource() == buildButton) {
            buildNPC();
	    } else if (event.getSource() == exitButton) {
		TokenMaker.saveState();
		System.exit(0);
	    } 
        else if (event.getSource() == nameText)
        {
            if (!event.getActionCommand().equals(""))
            {
                performFind();
            }
	    }
        else if (event.getSource() == localRadio || event.getSource() == remoteRadio)
        {
            if (remoteRadio.isSelected())
                TokenMaker.isRemote = true;
            else if (localRadio.isSelected())
                TokenMaker.isRemote = false;
	    }
	}
    }

    /**
     * Mouse listener which listens for double clicks on a list entry and fires
     * off a build on any entry that was so clicked.
     */
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
		buildNPC();
	    }
	}
    }

    /**
     * Provides a listener that allows keyboard selction of buttons
     */
    class KeyboardListener extends KeyAdapter {
	public void keyTyped(KeyEvent e) {
	    if (e.getSource() == findButton) {
		performFind();
	    } else if (e.getSource() == exitButton) {
		System.exit(0);
	    } else if (e.getSource() == buildButton) {
		buildNPC();
	    } else if (e.getSource() == npcList) {
		//buildNPC();
	    }

	}
    }

    /**
     * A listener which enables the build button only if there is something
     * selected to build.
     */
    class ListListener implements ListSelectionListener {
	public void valueChanged(ListSelectionEvent e) {
	    buildButton.setEnabled(true);
	}
    }

    /**
     * A listener which makes sure we always start with the top item selected
     */
    class ListFocusListener implements FocusListener {
	@Override
	public void focusGained(FocusEvent e) {
	    npcList.setSelectedIndex(0);
	}

	@Override
	public void focusLost(FocusEvent e) {
	    // TODO Auto-generated method stub

	}
    }
}