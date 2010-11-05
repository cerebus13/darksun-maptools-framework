package info.rodinia.tokenmaker;

import info.rodinia.tokenmaker.TokenMaker.Attribute;
import info.rodinia.tokenmaker.TokenMaker.Skill;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.HTMLEditorKit.ParserCallback;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * NPC holds all the info about an NPC/Monster. It is designed to model all the
 * D&D stats for a monster and it also knows a little about its Wizard's
 * Compendium entry. This is held on the monster so that we only have to go off
 * to the internet to read this monster's page once and thereafter we keep the
 * info local on the NPC object.
 * 
 * @author Blakey, Summer 2010
 */
public class NPC extends Character {

    // Compendium entry info
    private int id = 0;

    // private String senses;
    private String improvedSkills;
    private StatBlock statBlock = StatBlock.MM1;
    private String actionType = "";

    /**
     * State is used for parsing HTML of a monster entry. It tells us what tag
     * we have last found so we know what to do with the text we have just
     * found.
     */
    private enum State {
	NONE, POWERTYPE, NAME, LEVEL, XP, TYPE, STATS, INITIATIVE, SENSES, HP, BLOODIED, REGENERATION, AC, FORTITUDE, REFLEX, WILL, IMMUNE, RESIST, VULNERABLE, SAVINGTHROWS, SPEED, ACTIONPOINTS, POWERNAME, POWERUSAGE, POWERDETAIL, POWERRECHARGE, ALIGNMENT, LANGUAGES, SKILLS, STRENGTH, CONSTITUTION, DEXTERITY, INTELLIGENCE, WISDOM, CHARISMA
    }

    private enum StatBlock {
	MM1, MM3
    }

    public NPC(int id) {
	this(id, "", 0, "", "");
    }

    public NPC(int id, String name, int level, String groupRole,
	    String combatRole) {
	super();
	this.id = id;
	this.name = name;
	this.level = level;
	this.groupRole = groupRole;
	this.combatRole = combatRole;

	attributes.put(Attribute.Strength, 10);
	attributes.put(Attribute.Constitution, 10);
	attributes.put(Attribute.Dexterity, 10);
	attributes.put(Attribute.Intelligence, 10);
	attributes.put(Attribute.Wisdom, 10);
	attributes.put(Attribute.Charisma, 10);

    }

    /**
     * Get the HTML for this monster. Should we not have any then we'll go off
     * to the compendium and get it.
     * 
     * @return the HTML source for the monster.
     */
    @Override
    public String getHtml() {
	if (html == null) {
	    NPCCompendiumEntry comp = new NPCCompendiumEntry(this);
	    html = comp.getHTML();
	    /**
	     * Seeing as we've just got the fresh HTML, lets parse it and build
	     * a better monster from it.
	     */
	    parseHTML();
	}
	return html;
    }

    public int getId() {
	return id;
    }

    @Override
    public int getItemUsage() {
	return (0);
    }

    public String getSenses() {
	return senses;
    }

    public void addPower(Power p) {
	powers.add(p);
    }

    @Override
    public String toString() {
	return name + " (Level " + level + " " + groupRole + " " + combatRole
		+ ")";
    }

    /**
     * Go and set up the default values for all the improvedSkills, based on a
     * monsters level and attributes. Then override these base values with any
     * that are explicitly set on the monster's web page.
     */
    private void setupSkills() {
	// work out default bonuses based on level and attributes
	int strBonus = (level / 2)
		+ ((attributes.get(Attribute.Strength) - 10) / 2);
	int conBonus = (level / 2)
		+ ((attributes.get(Attribute.Constitution) - 10) / 2);
	int dexBonus = (level / 2)
		+ ((attributes.get(Attribute.Dexterity) - 10) / 2);
	int intBonus = (level / 2)
		+ ((attributes.get(Attribute.Intelligence) - 10) / 2);
	int wisBonus = (level / 2)
		+ ((attributes.get(Attribute.Wisdom) - 10) / 2);
	int chaBonus = (level / 2)
		+ ((attributes.get(Attribute.Charisma) - 10) / 2);

	// these are the NPCs base skills
	skills.put(Skill.Acrobatics, dexBonus);
	skills.put(Skill.Arcana, intBonus);
	skills.put(Skill.Athletics, strBonus);
	skills.put(Skill.Bluff, chaBonus);
	skills.put(Skill.Diplomacy, chaBonus);
	skills.put(Skill.Dungeoneering, wisBonus);
	skills.put(Skill.Endurance, conBonus);
	skills.put(Skill.Heal, wisBonus);
	skills.put(Skill.History, intBonus);
	skills.put(Skill.Insight, wisBonus);
	skills.put(Skill.Intimidate, chaBonus);
	skills.put(Skill.Nature, wisBonus);
	skills.put(Skill.Perception, wisBonus);
	skills.put(Skill.Religion, wisBonus);
	skills.put(Skill.Stealth, dexBonus);
	skills.put(Skill.Streetwise, chaBonus);
	skills.put(Skill.Thievery, dexBonus);

	// if we have some 'improved skills', override them now
	if (improvedSkills != null) {
	    int start;
	    if ((start = improvedSkills.indexOf("Acrobatics")) != -1)
		skills.put(Skill.Acrobatics, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("Arcana")) != -1)
		skills.put(Skill.Arcana, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("Athletics")) != -1)
		skills.put(Skill.Athletics, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("Bluff")) != -1)
		skills.put(Skill.Bluff, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("Diplomacy")) != -1)
		skills.put(Skill.Diplomacy, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("Dungeoneering")) != -1)
		skills.put(Skill.Dungeoneering, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("Endurance")) != -1)
		skills.put(Skill.Endurance, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("Heal")) != -1)
		skills.put(Skill.Heal, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("History")) != -1)
		skills.put(Skill.History, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("Insight")) != -1)
		skills.put(Skill.Insight, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("Intimidate")) != -1)
		skills.put(Skill.Intimidate, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("Nature")) != -1)
		skills.put(Skill.Nature, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("Perception")) != -1)
		skills.put(Skill.Perception, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("Religion")) != -1)
		skills.put(Skill.Religion, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("Stealth")) != -1)
		skills.put(Skill.Stealth, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("Streetwise")) != -1)
		skills.put(Skill.Streetwise, stringToInt(improvedSkills
			.substring(start)));
	    if ((start = improvedSkills.indexOf("Thievery")) != -1)
		skills.put(Skill.Thievery, stringToInt(improvedSkills
			.substring(start)));

	}
	// Perception should be got from senses
	skills.put(Skill.Perception, stringToInt(senses));
    }

    /**
     * Helper function which takes a string which should contain an Integer but
     * which might contain extra letters, and returns the integer only.
     */
    private int stringToInt(String str) {
	int result = 0;
	Pattern pattern = Pattern.compile(".*?(\\d+).*");
	Matcher matcher = pattern.matcher(str);
	if (matcher.matches()) {
	    result = Integer.parseInt(matcher.group(1));
	}
	// System.out.println(str + " == "+result);
	return result;
    }

    private void parseAura() {
	Pattern pattern = Pattern
		.compile("<b>(.+)</b>((.+)aura(\\s+)(\\d+)(.+))<br/>");
	Matcher matcher = pattern.matcher(html);
	if (matcher.find()) {
	    aura = matcher.group(1);
	    auraDescription = matcher.group(2);
	}
    }

    /**
     * Parse the monster's HTML source and from this grab out its stats. This
     * function uses the inner class NPCParser to do the parsing of the HTML and
     * populate the internal NPC member.
     * 
     * Again this is not a very neat approach and needs redesign.
     */
    private void parseHTML() {
	try {
	    NPCParser parser = new NPCParser();
	    Reader reader = new BufferedReader(new StringReader(getHtml()));
	    new ParserDelegator().parse(reader, parser, false);
	    setupSkills();
	    parseAura(); // awkward git to get out so do it separately.
	} catch (Exception e) {
	    System.err.println("Error parsing npc's HTML: " + e);
	    e.printStackTrace();
	}
    }

    /**
     * Inner class that does the parsing of a monster's HTML text to get to the
     * underlying stats we are interested in.
     * 
     * @author Blakey
     */
    class NPCParser extends ParserCallback {

	private Power myPower = null;
	private State state = State.NONE;

	/**
	 * Catches each tag as it is read. If it thinks this is a significant
	 * tag is sets a State flag so that the handleText method can then do
	 * something intelligent with the text it finds.
	 * <p>
	 * This method really just handles setting states.
	 */
	public void handleStartTag(Tag tag, MutableAttributeSet attrSet, int pos) {

	    if (tag == Tag.H1) {
		state = State.NAME;
	    }

	    if (tag == Tag.H2) {
		state = State.POWERTYPE;
	    }

	    if (tag == Tag.SPAN
		    && ((String) attrSet.getAttribute(HTML.Attribute.CLASS))
			    .equals("level")) {
		state = State.LEVEL;
	    }

	    if (tag == Tag.SPAN
		    && ((String) attrSet.getAttribute(HTML.Attribute.CLASS))
			    .equals("type")) {
		state = State.TYPE;
	    }

	    if (tag == Tag.SPAN
		    && ((String) attrSet.getAttribute(HTML.Attribute.CLASS))
			    .equals("xp")) {
		state = State.XP;
	    }

	    if (tag == Tag.P) {
		String c = (String) attrSet.getAttribute(HTML.Attribute.CLASS);
		// class="flavor" seems to mean our stat block
		if (c != null && c.equals("flavor")) {
		    state = State.STATS;
		    // class="flavor alt" means a power begins here
		} else if (c != null && c.equals("flavor alt")) {
		    state = State.POWERNAME;
		    // class="flavorIndent" means a power's detaill
		} else if (c != null && c.equals("flavorIndent")) {
		    state = State.POWERDETAIL;
		    // some other <p> so we are out of any special state
		} else {
		    state = State.NONE;
		}
	    }

	    if (tag == Tag.TABLE
		    && ((String) attrSet.getAttribute(HTML.Attribute.CLASS))
			    .equals("bodytable")) {
		state = State.STATS;
		statBlock = StatBlock.MM3;
	    }

	}

	public void handleSimpleTag(Tag tag, MutableAttributeSet attrSet,
		int pos) {
	    // If we are in 'recharge' mode and see an image
	    if (tag == Tag.IMG && state == state.POWERRECHARGE) {
		String src = (String) attrSet.getAttribute(HTML.Attribute.SRC);
		src = src.replace("http://www.wizards.com/dnd/images/symbol/",
			"");
		src = src.replace("a.gif", "");
		myPower.setUsage("recharge " + src);
		if (statBlock == StatBlock.MM1) {
		    myPower.setActionType(myPower.getActionType() + " " + src
			    + ")"); // eg "(standard, recharge 4)"
		}
		state = state.NONE;
	    }
	}

	/**
	 * Based on whatever state we are currently in, process the text we
	 * found. This method assumes handleStartTag is setting up the right
	 * states for it to switch off.
	 * 
	 * @TODO - This method effectively initialises the NPC member of the
	 *       NPCCompendiumEntry class. I don't like this approach as it is
	 *       all going on behind the scenes. I think it should be changed to
	 *       build an NPC from the Compendium page and return it.
	 */
	public void handleText(char[] data, int pos) {
	    Pattern pattern;
	    Matcher matcher;

	    String str = (new String(data)).trim();

	    switch (state) {
	    case NAME:
		// we already have a name so skip over this part.
		if (name.equals(""))
		    setName(str);
		break;
	    case LEVEL:
		pattern = Pattern.compile("Level\\s+(\\d+)\\s+(.+)");
		matcher = pattern.matcher(str);
		if (matcher.matches()) {
		    level = Integer.parseInt(matcher.group(1));
		    combatRole = matcher.group(2);
		}
		break;
	    case TYPE:
		pattern = Pattern.compile("(\\S+)\\s+(.+)");
		matcher = pattern.matcher(str);
		if (matcher.matches()) {
		    size = matcher.group(1);
		    description = matcher.group(2);
		}
		break;
	    case XP:
		xp = stringToInt(str);
		break;
	    case POWERTYPE:
		actionType = str;
		break;
	    // if State == STATS, we are about to get a Label
	    // so gobble it up and set the state based on what it said.
	    case STATS:
		if (str.equals("Alignment")) {
		    state = State.ALIGNMENT; // process end stats
		}
		if (str.equals("Equipment")) {
		    state = State.NONE;
		}
		if (str.equals("Languages")) {
		    state = State.LANGUAGES; // process end stats
		}
		if (str.equals("Initiative")) {
		    state = State.INITIATIVE;
		}
		if (str.equals("Perception")) {
		    state = State.SENSES;
		}
		if (str.equals("Senses")) {
		    state = State.SENSES;
		}
		if (str.equals("HP")) {
		    state = State.HP;
		}
		if (str.equals("Bloodied")) {
		    state = State.BLOODIED;
		}
		if (str.equals("Regeneration")) {
		    state = State.REGENERATION;
		}
		if (str.equals("AC")) {
		    state = State.AC;
		}
		if (str.equals("Fortitude")) {
		    state = State.FORTITUDE;
		}
		if (str.equals("Reflex")) {
		    state = State.REFLEX;
		}
		if (str.equals("Will")) {
		    state = State.WILL;
		}
		if (str.equals("Immune")) {
		    state = State.IMMUNE;
		}
		if (str.equals("Resist")) {
		    state = State.RESIST;
		}
		if (str.equals("Vulnerable")) {
		    state = State.VULNERABLE;
		}
		if (str.equals("Saving Throws")) {
		    state = State.SAVINGTHROWS;
		}
		if (str.equals("Speed")) {
		    state = State.SPEED;
		}
		if (str.equals("Action Points")) {
		    state = State.ACTIONPOINTS;
		}
		if (str.equals("Skills")) {
		    state = State.SKILLS;
		}
		if (str.equals("Str")) {
		    state = State.STRENGTH;
		}
		if (str.equals("Con")) {
		    state = State.CONSTITUTION;
		}
		if (str.equals("Dex")) {
		    state = State.DEXTERITY;
		}
		if (str.equals("Int")) {
		    state = State.INTELLIGENCE;
		}
		if (str.equals("Wis")) {
		    state = State.WISDOM;
		}
		if (str.equals("Cha")) {
		    state = State.CHARISMA;
		}
		break;
	    case INITIATIVE:
		initiative = stringToInt(str);
		state = State.STATS; // go look for another stat block label
		break;
	    case SENSES:
		if (str.contains("Perception")) { // old style stat block
		    senses = str;
		} else { // MM3+ stat block
		    senses = "Perception " + str;
		}
		state = State.STATS;
		break;
	    case HP:
		HP = stringToInt(str);
		state = State.STATS;
		break;
	    case BLOODIED:
		bloodied = stringToInt(str);
		state = State.STATS;
		break;
	    case REGENERATION:
		regeneration = str;
		state = State.STATS;
		break;
	    case AC:
		AC = stringToInt(str);
		state = State.STATS;
		break;
	    case FORTITUDE:
		fortitude = stringToInt(str);
		state = State.STATS;
		break;
	    case REFLEX:
		reflex = stringToInt(str);
		state = State.STATS;
		break;
	    case WILL:
		will = stringToInt(str);
		state = State.STATS;
		break;
	    case IMMUNE:
		immune = str;
		state = State.STATS;
		break;
	    case RESIST:
		resist = str;
		state = State.STATS;
		break;
	    case VULNERABLE:
		vulnerable = str;
		state = State.STATS;
		break;
	    case SAVINGTHROWS:
		savingThrows = stringToInt(str);
		state = State.STATS;
		break;
	    case SPEED:
		speed = str;
		state = State.STATS;
		break;
	    case ACTIONPOINTS:
		actionPoints = stringToInt(str);
		state = State.STATS;
		break;
	    case SKILLS:
		improvedSkills = str;
		state = State.STATS;
		break;
	    case STRENGTH:
		attributes.put(Attribute.Strength, stringToInt(str));
		state = State.STATS;
		break;
	    case CONSTITUTION:
		attributes.put(Attribute.Constitution, stringToInt(str));
		state = State.STATS;
		break;
	    case DEXTERITY:
		attributes.put(Attribute.Dexterity, stringToInt(str));
		state = State.STATS;
		break;
	    case INTELLIGENCE:
		attributes.put(Attribute.Intelligence, stringToInt(str));
		state = State.STATS;
		break;
	    case WISDOM:
		attributes.put(Attribute.Wisdom, stringToInt(str));
		state = State.STATS;
		break;
	    case CHARISMA:
		attributes.put(Attribute.Charisma, stringToInt(str));
		state = State.STATS;
		break;
	    case ALIGNMENT:
		alignment = StringEscapeUtils.escapeHtml(str).replace("&nbsp;",
			"");
		state = State.STATS;
		break;
	    case LANGUAGES:
		languages = str;
		state = State.STATS;
		break;
	    case POWERNAME:
		// if we are already building a power and we get a new one, add
		// the one we were building
		if (statBlock == StatBlock.MM3 && myPower != null) {
		    addPower(myPower);
		    myPower = null;
		}
		// Alignment and Equipment appear to be on the same tag as
		// Powers.
		if (str.equals("Alignment")) {
		    state = State.ALIGNMENT; // process end stats
		    break;
		}
		if (str.equals("Equipment")) {
		    state = State.NONE;
		    return;
		}
		if (str.equals("Skills")) {
		    state = State.SKILLS;
		    return;
		}
		if (str.equals("Str")) {
		    state = State.STATS;
		    return;
		}
		myPower = new Power(str);
		state = State.POWERUSAGE;
		break;
	    case POWERUSAGE:
		if (statBlock == StatBlock.MM3) {
		    String action = actionType.substring(0,
			    actionType.length() - 1);
		    myPower.setActionType(action);
		    myPower.setUsage(str);
		    if (str.contains("Recharge")) {
			state = state.POWERRECHARGE;
		    }
		    break;
		}

		// Remember all this string as the ActionType
		myPower.setActionType(str);

		// set up power usage
		if (str.contains("at-will"))
		    myPower.setUsage("at-will");
		if (str.contains("encounter"))
		    myPower.setUsage("encounter");
		// deal with recharging
		if (str.contains("recharge")) {
		    if (str.contains("recharges ")) {
			int idx = str.indexOf("recharges ");
			myPower.setUsage(str.substring(idx));
		    } else {
			state = state.POWERRECHARGE;
		    }
		}
		if (str.contains("daily"))
		    myPower.setUsage("daily");
		if (state != state.POWERRECHARGE)
		    state = State.NONE;
		break;
	    case POWERDETAIL:
		if (statBlock == StatBlock.MM3) {
		    myPower.setDetail(myPower.getDetail() + " " + str);
		    break;
		}
		myPower.setDetail(str);
		addPower(myPower);
		state = State.NONE;
		break;
	    default:
		break;
	    }
	}

    };

}
