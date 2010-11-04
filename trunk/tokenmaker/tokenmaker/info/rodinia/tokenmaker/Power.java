package info.rodinia.tokenmaker;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Power models D&D 4E powers, both those used by players and monsters
 * 
 * @author Blakey, Summer 2010
 */
public class Power {

    // compendium info
    private int id = 0;
    private String html = null;

    // Actual D&D stats side of things
    private String name = ""; // e.g. "Melee Basic Attack"
    private int level = 0;
    private String usage = ""; // e.g. "At-Will", "Encounter"
    private String actionType = ""; // e.g. "Standard Action", "Move"
    private ArrayList<PowerWeapon> weapons = new ArrayList<PowerWeapon>();
    private String detail = "";
    private boolean inSpellbook = false;

    // the HTML we'll put out to the stat card
    private String statCard = null;

    public Power(Power copy) {
	setId(copy.getId());
	this.html = null;
	this.name = copy.name;
	this.level = copy.level;
	this.usage = copy.usage;
	this.actionType= copy.actionType;
	copyWeapons(copy);
	this.detail = copy.detail;
    }
    
    // do a deep copy of the weapons
    private void copyWeapons(Power copy) {
	weapons = new ArrayList<PowerWeapon>();
	for (PowerWeapon w : copy.getWeapons()) {
	    PowerWeapon newpw = new PowerWeapon(w);
	    newpw.setPower(this);
	    weapons.add(newpw);
	}
    }
    
    public Power(String name) {
	this.name = name;
    }

    public Power(int id) {
	this.id = id;
    }

    public String getHTML() {
	if (id == 0)
	    return "";
	if (html == null) {
	    PowerCompendiumEntry com = new PowerCompendiumEntry(this);
	    html = com.getHTML();
	    html = html.replace("<img src=\"http://www.wizards.com/dndinsider/compendium/images/bullet.gif\" alt=\"\"/>", "-");
	}
	return html;
    }

    public String toString() {
	String result = name + " (" + actionType + ", " + usage + ")";
	// if (weapons.size() != 0) result += weapons.get(0);
	return result;
    }

    public boolean isInSpellbook() {
        return inSpellbook;
    }

    public void setInSpellbook(boolean inSpellbook) {
        this.inSpellbook = inSpellbook;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public void setUsage(String usage) {
	this.usage = usage;
    }

    public void setActionType(String actionType) {
	this.actionType = actionType;
    }

    public void addWeapon(PowerWeapon weapon) {
	weapons.add(weapon);
    }

    public void setWeapons(ArrayList<PowerWeapon> weapons) {
	this.weapons = weapons;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public int getLevel() {
	return level;
    }

    public void setLevel(int level) {
	this.level = level;
    }

    public String getUsage() {
	return usage;
    }

    public String getActionType() {
	return actionType;
    }

    public ArrayList<PowerWeapon> getWeapons() {
	return weapons;
    }

    public String getDetail() {
	return detail;
    }

    public void setDetail(String detail) {
	this.detail = detail;
    }
}
