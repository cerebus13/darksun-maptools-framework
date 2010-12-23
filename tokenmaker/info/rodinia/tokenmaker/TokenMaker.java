package info.rodinia.tokenmaker;

import java.io.*;

/**
 * TokenMaker allows the creation of MapTool tokens, both for Player Characters (PCs) and 
 * Monsters or Non Player Characters (NPCs).
 * <p>
 * TokenMaker, when started will pop up a little dialog box asking you if you wish to make a
 * PC or an NPC.  This choice can also be made at the command line by calling the program with 
 * a -p or -n flag.
 * <p>
 * The PC Maker part of the program allows the user to read in DDi Character Builder save files
 * and generate tokens based on those files.  The NPC maker side of the program allows the user to
 * search for monsters/NPCs by name and then generate NPC tokens for any monsters they select.
 * <p>
 * TokenMaker relies on a DDi subscription and when it needs to connect to the Compendium to get 
 * Monster or Power data out it reads DDi subscription info from the file system and uses this to connect.
 * It does this over the same HTTP connection that you use when you use a browser so it is <i>relatively</i>
 * secure.
 * <p>
 * No data is kept inside the program about DDi subscriptions nor are any of the monsters and powers that are 
 * accessed saved on disk, other than that the HTML for a monster is built into the token for display during 
 * a game session.   TokenMaker makes no attempt to gather and save information accessed from the compendium
 * so every time you start TokenMaker up and ask it to get some info it will go and get it afresh - it only caches 
 * data about given monsters and powers it has looked up while it is still running.
 * 
 * @author Blakey, Summer 2010.
 *
 * Last Blakey update: r72
 *
 */
public class TokenMaker {
    /**
     * All the SKILL names
     */
    public enum Skill {
	Acrobatics, Arcana, Athletics, Bluff, Diplomacy, Dungeoneering, Endurance, Heal, History, 
	Insight, Intimidate, Nature, Perception, Religion, Stealth, Streetwise, Thievery
    }
    
    public enum Attribute {
	Strength, Constitution, Dexterity, Intelligence, Wisdom, Charisma
    }

    public static final String[] Elements = {"acid", "cold", "fire", "force", "lightning", "necrotic", "poison", "psychic", "radiant", "thunder"};
    public static final String[] Roles = {"artillery", "brute", "controller", "lurker", "skirmisher", "soldier", "leader"};
    public static final String[] AtkIcons = {"S1.gif", "S2.gif", "S3.gif", "S4.gif", "Z1a.gif", "Z2a.gif", "Z3a.gif", "Z4a.gif"};

    public static String npcImagePath = null;
    public static String npcSavePath = null;
    public static String pcImagePath = null;
    public static String pcLoadPath = null;
    public static String pcSavePath = null;
    //public static final String homeDir = System.getenv("USERPROFILE")
    public static final String homeDir = System.getProperty("user.home")
	    + "/.tokenmaker";
    public static final String statusFile = homeDir + "/.status";
    public static final String ddiFile = homeDir + "/ddi.dat";

    public static void main(String[] args) {

	loadState();

	if (args.length != 0 && args[0].equals("-n")) {
	    NPCMaker frame = new NPCMaker();    
	} else if (args.length != 0 && args[0].equals("-p")) {
	    PCMaker frame = new PCMaker();
	} else { 
	    //PCMaker frame = new PCMaker();
	    //CompendiumEntry ce = new CompendiumEntry("http://www.wizards.com/dndinsider/compendium/monster.aspx?id=239");
	    //System.out.println(ce.getHTML());
	    //NPC monster = new NPC(4718);	// old style stat block
	    NPC monster = new NPC(4895);		// new style stat block
	    monster.getHtml();
	    monster.print();
	    //TokenMakerFrame frame = new TokenMakerFrame();
	}

	saveState();
    }

    public static void saveState() {
	try {
	    File home = new File(homeDir);
	    if (!home.exists()) {
		home.mkdir();
	    }
	    File file = new File(statusFile);
	    FileWriter w = new FileWriter(file);
	    BufferedWriter writer = new BufferedWriter(w);
	    writer.write(npcImagePath + "\n");
	    writer.write(npcSavePath + "\n");
	    writer.write(pcImagePath + "\n");
	    writer.write(pcLoadPath + "\n");
	    writer.write(pcSavePath + "\n");
	    writer.close();

	} catch (Exception e) {
	    System.err.println("Error loading state: " + e);
	}
    }

    public static void loadState() {
	try {
	    File home = new File(homeDir);
	    if (!home.exists()) {
		home.mkdir();
	    }
    
	    File file = new File(statusFile);
	    FileReader r = new FileReader(file);
	    BufferedReader reader = new BufferedReader(r);
	    npcImagePath = reader.readLine();
	    npcSavePath = reader.readLine();
	    pcImagePath = reader.readLine();
	    pcLoadPath = reader.readLine();
	    pcSavePath = reader.readLine();
	    reader.close();
	} catch (FileNotFoundException fnf) {
	    // do nothing as we don't have to have a file at the start.
	} catch (Exception e) {
	    System.err.println("Error loading state: " + e);
	}

    }
}
