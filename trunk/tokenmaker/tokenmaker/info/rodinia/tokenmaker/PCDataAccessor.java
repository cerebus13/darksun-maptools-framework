package info.rodinia.tokenmaker;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import info.rodinia.tokenmaker.TokenMaker.Attribute;
import info.rodinia.tokenmaker.TokenMaker.Skill;

/**
 * PCDataAccessor deals with reading in a PC's data from a file outside the
 * program. It could be overridden to provide a new method of getting PC data
 * into the program.
 * 
 * @author Blakey, Summer 2010
 * 
 */
public class PCDataAccessor {

    private Document myDom;

    /**
     * This method reads in a file and returns a PC object built from the
     * contents of the file. The file is assumed to be a DDi Character Builder
     * save file but this function could be overridden to provide access to a
     * different form of input file.
     * 
     * @param file
     *            - the file we are loading
     * @return the PC we have built from the file.
     */
    public PC loadSaveFile(File file) {
	parseXML(file);
	PC pc = buildPC();
	return pc;
    }

    // go and parse my XML file
    private void parseXML(File file) {
	// get the factory
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	try {
	    // Using factory get an instance of document builder
	    DocumentBuilder db = dbf.newDocumentBuilder();
	    // parse using builder to get DOM representation of the XML file
	    myDom = db.parse(file.toString());
	} catch (ParserConfigurationException pce) {
	    pce.printStackTrace();
	} catch (SAXException se) {
	    se.printStackTrace();
	} catch (IOException ioe) {
	    ioe.printStackTrace();
	}
    }

    public PC buildPC() {
	String name = getName();
	PC pc = new PC(name);
	pc.setClass(getPCClass());
	pc.setRace(getRace());
	pc.setLevel(getLevel());
	pc.setXp(getXP());
	pc.setVision(getVision());
	pc.setResistances(getResistances());
	pc.setPortrait(getPortrait());

	pc.setAC(getAC());
	pc.setFortitude(getFortitude());
	pc.setReflex(getReflex());
	pc.setWill(getWill());
	pc.setInitiative(getInitiative());
	pc.setSpeed(getSpeed());
	pc.setHealingSurges(getHealingSurges());
	pc.setHealingSurgeValue(getHealingSurgeValue());
	pc.setSavingThrows(getSavingThrows());
	pc.setHP(getHP());

	pc.setAttributes(getAttributes());
	pc.setSkills(getSkills());
	pc.setFeats(getFeats());
	pc.setEquipment(getEquipment());
	pc.setRituals(getRituals());
	pc.setClassFeatures(getClassFeatures());
	pc.setAlignment(getAlignment());
	pc.setLanguages(getLanguages());
	pc.setInitRolls(getInitRolls());
	pc.setPowers(getPowers(pc));

	return pc;
    }

    private boolean isInSpellBook(int spell) {
	boolean result = false;
	Element root = myDom.getDocumentElement();
	NodeList stats = root.getElementsByTagName("textstring");
	if (stats != null && stats.getLength() > 0) {
	    for (int i = 0; i < stats.getLength(); i++) {
		Element stat = (Element) stats.item(i);
		if (stat.getAttribute("name").equals("LAYOUT_DATAPowerCard")) {
		    String powercards = stat.getTextContent();
		    DocumentBuilderFactory dbf = DocumentBuilderFactory
			    .newInstance();
		    try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Reader reader = new CharArrayReader(powercards
				.toCharArray());
			Document dom = db.parse(new InputSource(reader));

			// now parse this lot to find what we are after...
			Element top = dom.getDocumentElement();
			NodeList items = top
				.getElementsByTagName("PowerCardBasic");
			if (items != null && items.getLength() > 0) {
			    for (int j = 0; j < items.getLength(); j++) {
				Element item = (Element) items.item(j);
				String spellBook = item
					.getAttribute("IsInSpellbook");
				if (spellBook.equals("True")) {
				    String powerIdStr = getElementContent(item,
					    "RuleID");
				    int powerId = Integer.parseInt(powerIdStr
					    .replace("ID_FMP_POWER_", ""));
				    if (powerId == spell)
					return true;
				}
			    }
			}

			items = top.getElementsByTagName("PowerCardUtility");
			if (items != null && items.getLength() > 0) {
			    for (int j = 0; j < items.getLength(); j++) {
				Element item = (Element) items.item(j);
				String spellBook = item
					.getAttribute("IsInSpellbook");
				if (spellBook.equals("True")) {
				    String powerIdStr = getElementContent(item,
					    "RuleID");
				    int powerId = Integer.parseInt(powerIdStr
					    .replace("ID_FMP_POWER_", ""));
				    if (powerId == spell)
					return true;
				}
			    }
			}

		    } catch (Exception e) {
			System.err.println("Error checking spellbook: " + e);
		    }
		}
	    }
	}
	return result;
    }

    public String getSpeed() {
	String ret = "";
	Element root = myDom.getDocumentElement();
	NodeList stats = root.getElementsByTagName("Stat");
	if (stats != null && stats.getLength() > 0) {
	    for (int i = 0; i < stats.getLength(); i++) {
		Element stat = (Element) stats.item(i);
		String statValue = stat.getAttribute("value");

		Element aliasNode = (Element) stat
			.getElementsByTagName("alias").item(0);
		String statName = aliasNode.getAttribute("name");

		if (statName.equals("Speed"))
		    ret = statValue;
	    }
	}
	return ret;
    }

    public int getAC() {
	int ret = 0;
	Element root = myDom.getDocumentElement();
	NodeList stats = root.getElementsByTagName("Stat");
	if (stats != null && stats.getLength() > 0) {
	    for (int i = 0; i < stats.getLength(); i++) {
		Element stat = (Element) stats.item(i);
		String statValue = stat.getAttribute("value");

		Element aliasNode = (Element) stat
			.getElementsByTagName("alias").item(0);
		String statName = aliasNode.getAttribute("name");

		if (statName.equals("AC"))
		    ret = Integer.parseInt(statValue);
	    }
	}
	return ret;
    }

    public int getFortitude() {
	int ret = 0;
	Element root = myDom.getDocumentElement();
	NodeList stats = root.getElementsByTagName("Stat");
	if (stats != null && stats.getLength() > 0) {
	    for (int i = 0; i < stats.getLength(); i++) {
		Element stat = (Element) stats.item(i);
		String statValue = stat.getAttribute("value");

		Element aliasNode = (Element) stat
			.getElementsByTagName("alias").item(0);
		String statName = aliasNode.getAttribute("name");

		if (statName.equals("Fortitude Defense"))
		    ret = Integer.parseInt(statValue);
	    }
	}
	return ret;
    }

    public int getReflex() {
	int ret = 0;
	Element root = myDom.getDocumentElement();
	NodeList stats = root.getElementsByTagName("Stat");
	if (stats != null && stats.getLength() > 0) {
	    for (int i = 0; i < stats.getLength(); i++) {
		Element stat = (Element) stats.item(i);
		String statValue = stat.getAttribute("value");

		Element aliasNode = (Element) stat
			.getElementsByTagName("alias").item(0);
		String statName = aliasNode.getAttribute("name");

		if (statName.equals("Reflex Defense"))
		    ret = Integer.parseInt(statValue);
	    }
	}
	return ret;
    }

    public int getWill() {
	int ret = 0;
	Element root = myDom.getDocumentElement();
	NodeList stats = root.getElementsByTagName("Stat");
	if (stats != null && stats.getLength() > 0) {
	    for (int i = 0; i < stats.getLength(); i++) {
		Element stat = (Element) stats.item(i);
		String statValue = stat.getAttribute("value");

		Element aliasNode = (Element) stat
			.getElementsByTagName("alias").item(0);
		String statName = aliasNode.getAttribute("name");

		if (statName.equals("Will Defense"))
		    ret = Integer.parseInt(statValue);
	    }
	}
	return ret;
    }

    public int getHP() {
	int ret = 0;
	Element root = myDom.getDocumentElement();
	NodeList stats = root.getElementsByTagName("Stat");
	if (stats != null && stats.getLength() > 0) {
	    for (int i = 0; i < stats.getLength(); i++) {
		Element stat = (Element) stats.item(i);
		String statValue = stat.getAttribute("value");

		Element aliasNode = (Element) stat
			.getElementsByTagName("alias").item(0);
		String statName = aliasNode.getAttribute("name");

		if (statName.equals("Hit Points"))
		    ret = Integer.parseInt(statValue);
	    }
	}
	return ret;
    }

    public int getHealingSurges() {
	int ret = 0;
	Element root = myDom.getDocumentElement();
	NodeList stats = root.getElementsByTagName("Stat");
	if (stats != null && stats.getLength() > 0) {
	    for (int i = 0; i < stats.getLength(); i++) {
		Element stat = (Element) stats.item(i);
		String statValue = stat.getAttribute("value");

		Element aliasNode = (Element) stat
			.getElementsByTagName("alias").item(0);
		String statName = aliasNode.getAttribute("name");

		if (statName.equals("Healing Surges"))
		    ret = Integer.parseInt(statValue);
	    }
	}
	return ret;
    }

    public int getHealingSurgeValue() {
	int ret = 0;
	Element root = myDom.getDocumentElement();
	NodeList stats = root.getElementsByTagName("Stat");
	if (stats != null && stats.getLength() > 0) {
	    for (int i = 0; i < stats.getLength(); i++) {
		Element stat = (Element) stats.item(i);
		String statValue = stat.getAttribute("value");

		Element aliasNode = (Element) stat
			.getElementsByTagName("alias").item(0);
		String statName = aliasNode.getAttribute("name");

		if (statName.equals("Healing Surge Value"))
		    ret = Integer.parseInt(statValue);
	    }
	}
	return ret;
    }

    public int getInitiative() {
	int ret = 0;
	Element root = myDom.getDocumentElement();
	NodeList stats = root.getElementsByTagName("Stat");
	if (stats != null && stats.getLength() > 0) {
	    for (int i = 0; i < stats.getLength(); i++) {
		Element stat = (Element) stats.item(i);
		String statValue = stat.getAttribute("value");

		Element aliasNode = (Element) stat
			.getElementsByTagName("alias").item(0);
		String statName = aliasNode.getAttribute("name");

		if (statName.equals("Initiative"))
		    ret = Integer.parseInt(statValue);
	    }
	}
	return ret;
    }

    public int getSavingThrows() {
	int ret = 0;
	Element root = myDom.getDocumentElement();
	NodeList stats = root.getElementsByTagName("Stat");
	if (stats != null && stats.getLength() > 0) {
	    for (int i = 0; i < stats.getLength(); i++) {
		Element stat = (Element) stats.item(i);
		String statValue = stat.getAttribute("value");

		Element aliasNode = (Element) stat
			.getElementsByTagName("alias").item(0);
		String statName = aliasNode.getAttribute("name");

		if (statName.equals("Saving Throws"))
		    ret = Integer.parseInt(statValue);
	    }
	}
	return ret;
    }

    public TreeMap<Skill, Integer> getSkills() {
	TreeMap<Skill, Integer> skills = new TreeMap<Skill, Integer>();
	Element root = myDom.getDocumentElement();
	// get a nodelist of <Stat> elements
	NodeList stats = root.getElementsByTagName("Stat");
	if (stats != null && stats.getLength() > 0) {
	    for (int i = 0; i < stats.getLength(); i++) {
		Element stat = (Element) stats.item(i);
		String statValue = stat.getAttribute("value");

		Element aliasNode = (Element) stat
			.getElementsByTagName("alias").item(0);
		String statName = aliasNode.getAttribute("name");

		if (statName.equals("Acrobatics"))
		    skills.put(Skill.Acrobatics, Integer.parseInt(statValue));
		if (statName.equals("Arcana"))
		    skills.put(Skill.Arcana, Integer.parseInt(statValue));
		if (statName.equals("Athletics"))
		    skills.put(Skill.Athletics, Integer.parseInt(statValue));
		if (statName.equals("Bluff"))
		    skills.put(Skill.Bluff, Integer.parseInt(statValue));
		if (statName.equals("Diplomacy"))
		    skills.put(Skill.Diplomacy, Integer.parseInt(statValue));
		if (statName.equals("Dungeoneering"))
		    skills
			    .put(Skill.Dungeoneering, Integer
				    .parseInt(statValue));
		if (statName.equals("Endurance"))
		    skills.put(Skill.Endurance, Integer.parseInt(statValue));
		if (statName.equals("Heal"))
		    skills.put(Skill.Heal, Integer.parseInt(statValue));
		if (statName.equals("History"))
		    skills.put(Skill.History, Integer.parseInt(statValue));
		if (statName.equals("Insight"))
		    skills.put(Skill.Insight, Integer.parseInt(statValue));
		if (statName.equals("Intimidate"))
		    skills.put(Skill.Intimidate, Integer.parseInt(statValue));
		if (statName.equals("Nature"))
		    skills.put(Skill.Nature, Integer.parseInt(statValue));
		if (statName.equals("Perception"))
		    skills.put(Skill.Perception, Integer.parseInt(statValue));
		if (statName.equals("Religion"))
		    skills.put(Skill.Religion, Integer.parseInt(statValue));
		if (statName.equals("Stealth"))
		    skills.put(Skill.Stealth, Integer.parseInt(statValue));
		if (statName.equals("Streetwise"))
		    skills.put(Skill.Streetwise, Integer.parseInt(statValue));
		if (statName.equals("Thievery"))
		    skills.put(Skill.Thievery, Integer.parseInt(statValue));
	    }
	}
	return skills;
    }

    // Go and find the PC name from the document.
    public String getName() {
	// get the root element
	Element root = myDom.getDocumentElement();

	// get the Name from the Details.
	Element detailsNode = (Element) root.getElementsByTagName("Details")
		.item(0);
	String name = getElementContent(detailsNode, "name");
	return name;
    }

    // Go and find the PC XP from the document.
    public int getXP() {
	// get the root element
	Element docEle = myDom.getDocumentElement();

	// get the Name from the Details.
	Element detailsNode = (Element) docEle.getElementsByTagName("Details")
		.item(0);
	String xpStr = getElementContent(detailsNode, "Experience").trim();
	if (xpStr.equals("")) {
	    return 0;
	} else {
	    return Integer.parseInt(xpStr);
	}
    }

    public ArrayList<String> getClassFeatures() {
	ArrayList<String> features = new ArrayList<String>();
	// get the root element
	Element docEle = myDom.getDocumentElement();

	Element ret = (Element) docEle
		.getElementsByTagName("RulesElementTally").item(0);
	NodeList nl = ret.getElementsByTagName("RulesElement");
	if (nl != null && nl.getLength() > 0) {
	    for (int j = 0; j < nl.getLength(); j++) {
		// get the Power element
		Element el = (Element) nl.item(j);
		String ruleType = el.getAttribute("type");
		if (!ruleType.equals("Class Feature")) {
		    continue;
		} else {
		    String feature = el.getAttribute("name");
		    if (!features.contains(feature))
			features.add(feature);
		}
	    }
	}
	// System.out.println("Class Features: " + features);
	return features;
    }

    public ArrayList<Feat> getFeats() {
	ArrayList<Feat> feats = new ArrayList<Feat>();
	// get the root element
	Element docEle = myDom.getDocumentElement();

	Element ret = (Element) docEle
		.getElementsByTagName("RulesElementTally").item(0);
	NodeList nl = ret.getElementsByTagName("RulesElement");
	if (nl != null && nl.getLength() > 0) {
	    for (int i = 0; i < nl.getLength(); i++) {
		// get the Power element
		Element el = (Element) nl.item(i);
		String ruleType = el.getAttribute("type");
		if (!ruleType.equals("Feat")) {
		    continue;
		} else {
		    String name = el.getAttribute("name");
		    String idStr = el.getAttribute("internal-id").replace(
			    "ID_FMP_FEAT_", "");
		    int id = 0;
		    if (!idStr.startsWith("ID_INTERNAL_FEAT_")) {
			id = Integer.parseInt(idStr);
		    }
		    Feat feat = new Feat(id);
		    feat.setName(name);
		    feats.add(feat);
		}
	    }

	}
	// System.out.println("Feats: " + feats);
	return feats;
    }

    public ArrayList<Equipment> getEquipment() {
	ArrayList<Equipment> equipment = new ArrayList<Equipment>();
	// get the root element
	Element docEle = myDom.getDocumentElement();

	Element ret = (Element) docEle.getElementsByTagName("LootTally")
		.item(0);
	NodeList nl = ret.getElementsByTagName("loot");
	if (nl != null && nl.getLength() > 0) {
	    for (int i = 0; i < nl.getLength(); i++) {
		// get the Power element
		Element el = (Element) nl.item(i);
		String equipCount = el.getAttribute("equip-count");
		if (equipCount.equals("0")) {
		    continue;
		} else {
		    Element e = (Element) el.getElementsByTagName(
			    "RulesElement").item(0);
		    String name = e.getAttribute("name");
		    String url = e.getAttribute("url");
		    Equipment item = new Equipment(name);
		    item.setUrl(url);
		    equipment.add(item);
		}
	    }
	}
	// System.out.println("Equipment: " + equipment);
	return equipment;
    }

    public ArrayList<Ritual> getRituals() {
	ArrayList<Ritual> rituals = new ArrayList<Ritual>();
	// get the root element
	Element docEle = myDom.getDocumentElement();

	Element ret = (Element) docEle.getElementsByTagName("LootTally")
		.item(0);
	NodeList nl = ret.getElementsByTagName("loot");
	if (nl != null && nl.getLength() > 0) {
	    for (int i = 0; i < nl.getLength(); i++) {
		// get the Power element
		Element el = (Element) nl.item(i);
		Element e = (Element) el.getElementsByTagName("RulesElement")
			.item(0);
		String type = e.getAttribute("type");
		if (type.equals("Ritual")) {
		    String name = e.getAttribute("name");
		    String idStr = e.getAttribute("internal-id").replace(
			    "ID_FMP_RITUAL_", "");
		    int id = Integer.parseInt(idStr);
		    Ritual ritual = new Ritual(id);
		    ritual.setName(name);
		    rituals.add(ritual);
		}
	    }
	}
	// System.out.println("Rituals: " + rituals);
	return rituals;
    }

    // Go and find the PC class from the document.
    public String getPCClass() {
	String pcClass = "Unknown";
	// get the root element
	Element docEle = myDom.getDocumentElement();

	Element ret = (Element) docEle
		.getElementsByTagName("RulesElementTally").item(0);
	NodeList nl = ret.getElementsByTagName("RulesElement");
	if (nl != null && nl.getLength() > 0) {
	    for (int i = 0; i < nl.getLength(); i++) {
		// get the Power element
		Element el = (Element) nl.item(i);
		String ruleType = el.getAttribute("type");
		if (!ruleType.equals("Class")) {
		    continue;
		} else {
		    pcClass = el.getAttribute("name");
		    break;
		}
	    }
	}
	return pcClass;
    }

    // Go and find the PC alignment from the document.
    public String getAlignment() {
	String alignment = "Unknown";
	// get the root element
	Element docEle = myDom.getDocumentElement();

	Element ret = (Element) docEle
		.getElementsByTagName("RulesElementTally").item(0);
	NodeList nl = ret.getElementsByTagName("RulesElement");
	if (nl != null && nl.getLength() > 0) {
	    for (int i = 0; i < nl.getLength(); i++) {
		// get the Power element
		Element el = (Element) nl.item(i);
		String ruleType = el.getAttribute("type");
		if (!ruleType.equals("Alignment")) {
		    continue;
		} else {
		    alignment = el.getAttribute("name");
		    break;
		}
	    }
	}
	return alignment;
    }

    // Go and find the PC vision from the document.
    public String getVision() {
	String vision = "Unknown";
	// get the root element
	Element docEle = myDom.getDocumentElement();

	Element ret = (Element) docEle
		.getElementsByTagName("RulesElementTally").item(0);
	NodeList nl = ret.getElementsByTagName("RulesElement");
	if (nl != null && nl.getLength() > 0) {
	    for (int i = 0; i < nl.getLength(); i++) {
		// get the Power element
		Element el = (Element) nl.item(i);
		String ruleType = el.getAttribute("type");
		if (!ruleType.equals("Vision")) {
		    continue;
		} else {
		    vision = el.getAttribute("name");
		    break;
		}
	    }
	}
	return vision;
    }

    // Go and find the PC race from the document.
    public String getRace() {
	String race = "Unknown";
	// get the root element
	Element docEle = myDom.getDocumentElement();

	Element ret = (Element) docEle
		.getElementsByTagName("RulesElementTally").item(0);
	NodeList nl = ret.getElementsByTagName("RulesElement");
	if (nl != null && nl.getLength() > 0) {
	    for (int i = 0; i < nl.getLength(); i++) {
		// get the Power element
		Element el = (Element) nl.item(i);
		String ruleType = el.getAttribute("type");
		if (!ruleType.equals("Race")) {
		    continue;
		} else {
		    race = el.getAttribute("name");
		    break;
		}
	    }
	}
	return race;
    }

    // Go and find the PC level from the document.
    public int getLevel() {
	// get the root element
	Element docEle = myDom.getDocumentElement();

	// get the Name from the Details.
	Element detailsNode = (Element) docEle.getElementsByTagName("Details")
		.item(0);
	int level = Integer.parseInt(getElementContent(detailsNode, "Level"));
	return level;
    }

    // Go and find the portrait file from the document.
    public File getPortrait() {
	// get the root element
	Element docEle = myDom.getDocumentElement();

	// get the Name from the Details.
	Element detailsNode = (Element) docEle.getElementsByTagName("Details")
		.item(0);
	String name = getElementContent(detailsNode, "Portrait");
	name = name.replace("file://", "");
	return new File(name);
    }

    public String getLanguages() {
	String languages = "";
	// get the root element
	Element root = myDom.getDocumentElement();

	Element ret = (Element) root.getElementsByTagName("RulesElementTally")
		.item(0);
	NodeList nl = ret.getElementsByTagName("RulesElement");
	if (nl != null && nl.getLength() > 0) {
	    for (int j = 0; j < nl.getLength(); j++) {
		// get the element
		Element el = (Element) nl.item(j);
		String ruleType = el.getAttribute("type");
		if (!ruleType.equals("Language")) {
		    continue;
		} else {
		    if (!languages.equals(""))
			languages += ", ";
		    languages += el.getAttribute("name");
		}
	    }
	}
	return languages;
    }

    public String getResistances() {
	String resistances = "";
	// get the root element
	Element root = myDom.getDocumentElement();
	NodeList nl = root.getElementsByTagName("Stat");
	if (nl != null)
	    for (int i = 0; i < nl.getLength(); i++) {
		Element el = (Element) nl.item(i);
		String value = el.getAttribute("value");
		NodeList list = el.getElementsByTagName("alias");
		if (list != null)
		    for (int j = 0; j < list.getLength(); j++) {
			Element alias = (Element) list.item(j);
			String name = alias.getAttribute("name");
			if (name.contains("resist:")) {
			    if (!resistances.equals(""))
				resistances += ", ";
			    resistances += name.substring(7);
			    resistances += " " + value;
			}
		    }
	    }
	return resistances;
    }

    public TreeMap<Attribute, Integer> getAttributes() {
	TreeMap<Attribute, Integer> attributes = new TreeMap<Attribute, Integer>();
	// get the root element
	Element root = myDom.getDocumentElement();
	NodeList nl = root.getElementsByTagName("Stat");
	if (nl != null)
	    for (int i = 0; i < nl.getLength(); i++) {
		Element el = (Element) nl.item(i);
		String value = el.getAttribute("value");
		NodeList list = el.getElementsByTagName("alias");
		if (list != null)
		    for (int j = 0; j < list.getLength(); j++) {
			Element alias = (Element) list.item(j);
			String name = alias.getAttribute("name");
			if (name.equals("Strength")) {
			    attributes.put(Attribute.Strength, Integer
				    .parseInt(value));
			}
			if (name.equals("Constitution")) {
			    attributes.put(Attribute.Constitution, Integer
				    .parseInt(value));
			}
			if (name.equals("Dexterity")) {
			    attributes.put(Attribute.Dexterity, Integer
				    .parseInt(value));
			}
			if (name.equals("Intelligence")) {
			    attributes.put(Attribute.Intelligence, Integer
				    .parseInt(value));
			}
			if (name.equals("Wisdom")) {
			    attributes.put(Attribute.Wisdom, Integer
				    .parseInt(value));
			}
			if (name.equals("Charisma")) {
			    attributes.put(Attribute.Charisma, Integer
				    .parseInt(value));
			}
		    }
	    }
	return attributes;
    }

    public int getInitRolls() {
	int initRolls = 1;
	Element root = myDom.getDocumentElement();

	// get a nodelist of <Power> elements
	ArrayList<Power> powers = new ArrayList<Power>();
	NodeList nl = root.getElementsByTagName("textstring");
	if (nl != null && nl.getLength() > 0) {
	    for (int i = 0; i < nl.getLength(); i++) {
		Element el = (Element) nl.item(i);
		String value = el.getAttribute("name");
		if (value.equals("USER_NOTE_Danger Sense"))
		    initRolls = 2;
		if (value.equals("USER_NOTE_Foresight"))
		    initRolls = 2;
	    }
	}
	return initRolls;
    }

    /**
     * Count the number of times this power comes up in the "Usage" text.
     * 
     * @param name
     * @return
     */
    public int countUsage(String name) {
	int count = 0;
	Element root = myDom.getDocumentElement();

	// get a nodelist of <Power> elements
	ArrayList<Power> powers = new ArrayList<Power>();
	NodeList nl = root.getElementsByTagName("textstring");
	if (nl != null && nl.getLength() > 0) {
	    for (int i = 0; i < nl.getLength(); i++) {
		Element el = (Element) nl.item(i);
		String value = el.getAttribute("name");
		if (value.startsWith("USAGE_" + name))
		    count++;
	    }
	}
	return count;
    }

    public ArrayList<Power> getPowers(PC pc) {
	// get the root element
	Element docEle = myDom.getDocumentElement();

	// get a nodelist of <Power> elements
	ArrayList<Power> powers = new ArrayList<Power>();
	NodeList nl = docEle.getElementsByTagName("Power");
	if (nl != null && nl.getLength() > 0) {
	    for (int i = 0; i < nl.getLength(); i++) {

		// get the Power element
		Element el = (Element) nl.item(i);
		Power p = getPower(pc, el);

		// Special case for Healing Word and Inspiring Word (etc)
		// These can be used multiple times per encounter so
		// make multiple copies of them.
		if (p.getName().equals("Healing Word")
			|| p.getName().equals("Inspiring Word")
			|| p.getName().equals("Rune of Mending")
			|| p.getName().equals("Majestic Word")) {
		    for (int j = 1; j <= countUsage(p.getName()); j++) {
			Power newp = new Power(p);
			newp.setName(p.getName() + j);
			powers.add(newp);
		    }
		} else {
		    powers.add(p); // just add a normal power
		}
	    }
	}
	return powers;
    }

    public Power getPower(PC pc, Element powerElement) {

	// for each <Power> element get text value of 'name'
	String powerName = powerElement.getAttribute("name");
	Power p = new Power(powerName);

	if (!powerName.contains("Basic Attack")) {
	    // sadly we have to scan whole document for the ID
	    Element root = myDom.getDocumentElement();
	    Element ret = (Element) root.getElementsByTagName(
		    "RulesElementTally").item(0);
	    NodeList nl = ret.getElementsByTagName("RulesElement");
	    if (nl != null && nl.getLength() > 0) {
		for (int i = 0; i < nl.getLength(); i++) {
		    // get the Power element
		    Element el = (Element) nl.item(i);
		    String ruleType = el.getAttribute("type");
		    if (!ruleType.equals("Power")) {
			continue;
		    } else {
			if (powerName.equals(el.getAttribute("name"))) {
			    p.setId(Integer
				    .parseInt(el.getAttribute("internal-id")
					    .replace("ID_FMP_POWER_", "")));
			    if (isInSpellBook(p.getId())) {
				p.setInSpellbook(true);
			    }
			    break;
			}
		    }
		}
	    }
	}
	// now get the specifics (i.e. usage and type)
	NodeList nl = powerElement.getElementsByTagName("specific");
	if (nl != null && nl.getLength() > 0) {
	    for (int i = 0; i < nl.getLength(); i++) {
		// get the specific element
		Element specifics = (Element) nl.item(i);
		String temp = specifics.getAttribute("name");
		if (temp.equals("Power Usage")) {
		    p.setUsage(specifics.getTextContent().trim());
		} else if (temp.equals("Action Type")) {
		    p.setActionType(specifics.getTextContent().trim());
		}

	    }
	}
	p.setWeapons(getWeapons(pc, powerElement, p));
	// System.out.println("Found Power " +p.getName()+ ".  In spellbook: " +
	// p.isInSpellbook());
	return p;
    }

    public ArrayList<PowerWeapon> getWeapons(PC pc, Element powerElement,
	    Power power) {
	ArrayList<PowerWeapon> weapons = new ArrayList<PowerWeapon>();

	// get the Weapon options
	NodeList weaponList = powerElement.getElementsByTagName("Weapon");
	if (weaponList != null && weaponList.getLength() > 0) {
	    for (int i = 0; i < weaponList.getLength(); i++) {
		// get the specific element
		Element weaponElement = (Element) weaponList.item(i);
		String weaponName = weaponElement.getAttribute("name");

		// Now get all the attack specific info from the weapon node
		// Get the compendium URL for this weapon
		String weaponUrl = "";
		String magicUrl = "";
		NodeList rules = weaponElement
			.getElementsByTagName("RulesElement");
		if (rules != null && rules.getLength() > 0) {
		    for (int j = 0; j < rules.getLength(); j++) {
			// get the specific element
			Element rule = (Element) rules.item(j);
			String type = rule.getAttribute("type");
			if (type.equals("Magic Item")) {
			    magicUrl = rule.getAttribute("url");
			}
			if (type.equals("Weapon")) {
			    weaponUrl = rule.getAttribute("url");
			}
		    }
		}

		Weapon weapon;
		if ((weapon = pc.getWeapon(weaponName)) == null) {
		    weapon = new Weapon(pc, weaponName, weaponUrl, magicUrl);
		    pc.addWeapon(weapon);
		}

		int attackBonus = Integer.parseInt(getElementContent(
			weaponElement, "AttackBonus"));
		String damage = getElementContent(weaponElement, "Damage");
		String damageType = getElementContent(weaponElement,
			"DamageType");
		String attackStat = getElementContent(weaponElement,
			"AttackStat");
		String defense = getElementContent(weaponElement, "Defense");
		if (defense.contains("AC"))
		    defense = "AC";
		if (defense.contains("Fortitude"))
		    defense = "Fortitude";
		if (defense.contains("Reflex"))
		    defense = "Reflex";
		if (defense.contains("Will"))
		    defense = "Will";

		String hitComponents = getElementContent(weaponElement,
			"HitComponents");
		String damageComponents = getElementContent(weaponElement,
			"DamageComponents");
		String conditions = getElementContent(weaponElement,
			"Conditions");
		String healing = getElementContent(weaponElement, "Healing");

		// Now add the weapon to the list of weapons for the power.
		PowerWeapon powerWeapon = new PowerWeapon(power, weapon,
			attackBonus, damage, damageType, attackStat, defense,
			hitComponents, damageComponents, conditions, healing);
		weapons.add(powerWeapon);
	    }
	}
	return weapons;
    }

    public String getElementContent(Element el, String node) {
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
