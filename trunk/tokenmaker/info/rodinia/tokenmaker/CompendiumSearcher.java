package info.rodinia.tokenmaker;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * CompendiumSearcher is a class that deals with the Wizard of the Coast DDi
 * Compendium. It provides a simple interface of static methods to search for
 * npcs and powers and return basic info about ones matching your search.
 * 
 * @author Blakey, Summer 2010.
 * 
 */
public class CompendiumSearcher {

    // Define a bunch of static URLs and strings to help build URLS
    private static final String mainSearchURL = "http://www.wizards.com/dndinsider/compendium/compendiumsearch.asmx/KeywordSearchWithFilters?Keywords=";
    private static final String npcSearchURL = "&tab=Monster&NameOnly=true&Filters=";
    private static final String powerSearchURL = "&tab=Power&NameOnly=true&Filters=";
    private static final String weaponSearchURL = "&tab=Item&NameOnly=true&Filters=";

    // define the search criteria for monsters
    private static int levelMin = -1;
    private static int levelMax = -1;
    private static String keyword = "null";
    private static String mainRole = "null";
    private static String groupRole = "null";
    private static int xpMin = -1;
    private static int xpMax = -1;
    private static String source = "null";
    
    private static String npcFilters = levelMin+"|"+levelMax+"|"+mainRole+"|"+groupRole+"|"+keyword+"|"+xpMin+"|"+xpMax+"|"+source;
    private static String powerFilters = "null|null|null|-1|-1|null|null";
    private static String weaponFilters = "Weapon|-1|-1|-1|-1|-1|-1|-1";

    
    /**
     * Given a complete filter give me back the npcs that match that name.
     * Note that full word matches only are performed, so if you ask it to search 
     * for "Gobl" you will not get back any Goblins.
     * Passing in "Goblin" will give you back all entries with the word Goblin in them.
     * This is a case insensitive match. 
     * 
     * @param pattern - the name we are going to search for.
     * @return a List of NPCs who have a name that matches.
     */
    public static List<NPC> getNPCs(String npcName, int levelMin, int levelMax, String keyword, String mainRole, String groupRole,
	    int xpMin, int xpMax, String source) {
	ArrayList<NPC> nPCs = null;
	try {
	    String searchURL = getNPCSearchURL(npcName, levelMin, levelMax, keyword, mainRole, groupRole, xpMin, xpMax, source);
	    Document document = parseXML(searchURL);
	    nPCs = (ArrayList<NPC>) parseNPCs(document);
	} catch (Exception e) {
	    System.err.println("Error reading from Compendium: " + e);
	}
	return nPCs;
    }

    /**
     * Given a partial filter give me back the npcs that match that name.
     * Note that full word matches only are performed, so if you ask it to search 
     * for "Gobl" you will not get back any Goblins.
     * Passing in "Goblin" will give you back all entries with the word Goblin in them.
     * This is a case insensitive match. 
     * 
     * @param pattern - the name we are going to search for.
     * @return a List of NPCs who have a name that matches.
     */
    public static List<NPC> getNPCs(String pattern, int levelMin, int levelMax, String mainRole, String groupRole) {
	// use defaults to fill in the spaces
	return getNPCs(pattern, levelMin, levelMax, keyword, mainRole, groupRole, xpMin, xpMax, source);
    }

    
    /**
     * Given a NAME filter give me back the npcs that match that name.
     * Note that full word matches only are performed, so if you ask it to search 
     * for "Gobl" you will not get back any Goblins.
     * Passing in "Goblin" will give you back all entries with the word Goblin in them.
     * This is a case insensitive match. 
     * 
     * @param pattern - the name we are going to search for.
     * @return a List of NPCs who have a name that matches.
     */
    public static List<NPC> getNPCs(String pattern) {
	// use all the defaults.
	return getNPCs(pattern, levelMin, levelMax, keyword, mainRole, groupRole, xpMin, xpMax, source);
    }

    /**
     * Given a NAME filter give me back the Powers that match that name.
     * Note that full word matches only are performed, so if you ask it to search 
     * for "Divi" you will not get back any Divine powers.
     * Passing in "Divine" will give you back all entries with the word Divine in them.
     * This is a case insensitive match. 
     * 
     * @param pattern - the name we are going to search for.
     * @return a List of Powers who have a name that matches.
     */
    public static List<Power> getPowers(String pattern) {
	ArrayList<Power> powers = null;
	try {
	    String searchURL = getPowerSearchURL(pattern);
	    Document document = parseXML(searchURL);
	    powers = (ArrayList<Power>) parsePowers(document);
	} catch (Exception e) {
	    System.err.println("Error reading from Compendium: " + e);
	}
	return powers;
    }

    /**  
     * Given a precise power name, get me the ID back.
     * This method assumes you have a complete power name, not a partial one.
     * It is probably called with a full power name gathered from other places,
     * like the DDi save file for a PC.
     * It will search the compendium and give you the power's unique database ID.
     * This would usually then be used to get a full PowerCompendiumEntry entry for that power.
     * In the case that we matched more than one power, we'll give back the first ID number only.
     *  
     * @param powerName to search for.
     * @return the ID of the power in the online Compendium database.
     */
    public static int getPowerID(String powerName) {
	ArrayList<Integer> ids = null;
	try {
	    String searchURL = getPowerSearchURL(powerName);
	    Document document = parseXML(searchURL);
	    ids = (ArrayList<Integer>) parseIDs(document);
	} catch (Exception e) {
	    System.err.println("Error reading from Compendium: " + e);
	}
	if (ids.size() > 0)
	    return ids.get(0);	// if we did get more than 1 match, just return the first.
	else
	    return 0;	// no match
    }

    public static int getWeaponID(String weaponName, int enhancement) {
	ArrayList<Integer> ids = null;
	try {
	    String searchURL = getWeaponSearchURL(weaponName, enhancement);
	    Document document = parseXML(searchURL);
	    ids = (ArrayList<Integer>) parseIDs(document);
	} catch (Exception e) {
	    System.err.println("Error reading from Compendium: " + e);
	}
	if (ids.size() > 0)
	    return ids.get(0);	// if we did get more than 1 match, just return the first.
	else
	    return 0;	// no match
    }

    // Given a full filter, get me the URL to use to search for it.
    private static String getNPCSearchURL(String npcName, int levelMin, int levelMax, String keyword, String mainRole, String groupRole,
	    int xpMin, int xpMax, String source) {
	npcFilters = levelMin+"|"+levelMax+"|"+mainRole+"|"+groupRole+"|"+keyword+"|"+xpMin+"|"+xpMax+"|"+source;
	return mainSearchURL + npcName + npcSearchURL + npcFilters;
    }

    // Given a full filter, get me the URL to use to search for it.
    private static String getWeaponSearchURL(String weaponName, int enhancement) {
	weaponFilters = "Weapon|-1|-1|-1|-1|"+enhancement+"|"+enhancement+"|-1";
	return mainSearchURL + weaponName + weaponSearchURL + weaponFilters;
    }
    
    // Given a power name filter, get me the URL to use to search for it.
    private static String getPowerSearchURL(String powerName) {
	return mainSearchURL + powerName + powerSearchURL + powerFilters;
    }


    // Walk the DOM and build a list of ID node values
    private static List<Integer> parseIDs(Document document) {
	ArrayList<Integer> ids = new ArrayList<Integer>();
	// get the root element
	Element root = document.getDocumentElement();
	NodeList idList = root.getElementsByTagName("ID");
	for (int j = 0; j < idList.getLength(); j++) {
	    Element idElem = (Element) idList.item(j);
	    ids.add(Integer.parseInt(idElem.getTextContent()));
	}
	return ids;
    }

    // Walk the DOM and build a set of npcs
    private static List<NPC> parseNPCs(Document document) {
	ArrayList<NPC> npcs = new ArrayList<NPC>();
	// get the root element
	Element root = document.getDocumentElement();

	NodeList npcList = root.getElementsByTagName("Monster");
	for (int j = 0; j < npcList.getLength(); j++) {
	    Element npcElem = (Element) npcList.item(j);
	    int id = Integer.parseInt(getElementContent(npcElem, "ID"));
	    String name = getElementContent(npcElem, "Name");
	    int level = Integer.parseInt(getElementContent(npcElem, "Level"));
	    String groupRole = getElementContent(npcElem, "GroupRole");
	    String combatRole = getElementContent(npcElem, "CombatRole");
	    NPC npc = new NPC(id, name, level, groupRole, combatRole);
	    npcs.add(npc);
	}
	return npcs;
    }

    // Walk the DOM and build a set of powers
    private static List<Power> parsePowers(Document document) {
	ArrayList<Power> powers = new ArrayList<Power>();
	// get the root element
	Element root = document.getDocumentElement();

	NodeList powerList = root.getElementsByTagName("Power");
	for (int j = 0; j < powerList.getLength(); j++) {
	    Element powerElem = (Element) powerList.item(j);
	    Power p = new Power(getElementContent(powerElem, "Name"));
	    p.setId(Integer.parseInt(getElementContent(powerElem, "ID")));
	    p.setLevel(Integer.parseInt(getElementContent(powerElem, "Level")));
	    p.setActionType(getElementContent(powerElem, "ActionType"));
	    powers.add(p);
	}
	return powers;
    }

    /** 
     * Parse any given XML file and return it as a DOM.
     * This is a bog standard DOM implementation.
     * @param filename - a source XML file.
     * @return a DOM representation of the file.
     */
    private static Document parseXML(String filename) {
	Document document = null;
	// get the factory
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	try {
	    // Using factory get an instance of document builder
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    // parse using builder to get DOM representation of the XML file
	    document = db.parse(filename);
	} catch (ParserConfigurationException pce) {
	    pce.printStackTrace();
	} catch (SAXException se) {
	    se.printStackTrace();
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}
	return document;
    }

    /**
     * A little helper function to take an element and a node
     * and return the content of the first match.
     * It is used when we know there will only be one match.
     *  
     * @param el - the source element
     * @param node - the node we are after inside the element
     * @return the content of the node
     */
    private static String getElementContent(Element el, String node) {
	String result = null;
	try {
	    result = el.getElementsByTagName(node).item(0).getTextContent()
		    .trim();
	} catch (NullPointerException e) {
	    result = "";
	}
	return result;
    }
}
