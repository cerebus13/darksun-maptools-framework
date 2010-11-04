package info.rodinia.tokenmaker;

import info.rodinia.tokenmaker.TokenMaker.Attribute;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Weapon class holds information on specific weapons, such as a
 * "+3 frost greataxe". It is designed to hold information from the DDi
 * compendium.
 * 
 * @author Blakey - Summer 2010
 * 
 */
public class Weapon {

    private String magicUrl = "";
    private String weaponUrl = "";
    private String name = "";
    private String damage = "";
    private String group = "";
    private String magicCrit = "";
    private boolean highCrit = false;
    private String criticalDamage = null;
    private String criticalType = "";
    private PC pc;

    /**
     * Create a weapon where you have the URL of the weapon and magical type.
     * This will go and populate the important parts of the weapon, like the
     * critical value
     * 
     * @param name
     * @param weaponUrl
     */
    public Weapon(PC pc, String name, String weaponUrl, String magicUrl) {
	this.pc = pc;
	this.name = name;
	this.weaponUrl = weaponUrl;
	this.magicUrl = magicUrl;

    }

    public Weapon(PC pc, String name) {
	this(pc, name, "", "");
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public boolean isHighCrit() {
	return highCrit;
    }

    public String getMagicCrit() {
	return magicCrit;
    }

    public void setMagicCrit(String magicCrit) {
	this.magicCrit = magicCrit;
    }

    public String getDamage() {
	return damage;
    }

    public String getCriticalDamage() {
	if (criticalDamage == null) {
	    updateCriticalInfo();
	}
	return criticalDamage;
    }

    public void setCriticalDamage(String criticalDamage) {
	this.criticalDamage = criticalDamage;
    }

    public void addCriticalDamage(String criticalDamage) {
	this.criticalDamage += criticalDamage;
    }

    public String getCriticalType() {
	return criticalType;
    }

    public void setCriticalType(String criticalType) {
	this.criticalType = criticalType;
    }

    private void updateCriticalInfo() {
	criticalDamage = "";
	// now go and look up the critical damage string for this weapon
	if (!magicUrl.equals("")) {
	    MagicWeaponCompendiumEntry mwce = new MagicWeaponCompendiumEntry(
		    magicUrl);
	    magicCrit = mwce.getMagicCritical();
	}
	if (!weaponUrl.equals("")) {
	    WeaponCompendiumEntry wce = new WeaponCompendiumEntry(weaponUrl);
	    highCrit = wce.isHighCrit();
	    damage = wce.getDamage();
	    group = wce.getGroup();
	}

	// Check for devastating critical feat
	if (pc.hasFeat("Devastating Critical"))
	    addCriticalDamage("+1d10");

	// Now high crit weapons
	if (isHighCrit()) {
	    String damage = getDamage();
	    int number = Integer.parseInt(damage.substring(0, damage
		    .indexOf("d")));
	    int size = Integer.parseInt(damage
		    .substring(damage.indexOf("d") + 1));
	    int tier = ((pc.getLevel() - 1) / 10) + 1;
	    number = number * tier;
	    damage = "+" + number + "d" + size;
	    addCriticalDamage(damage);
	}

	// Check to see if the character is wearing any special crit-bonus
	// equipment
	if (pc.hasEquipment("Executioner's Bracers (heroic tier)"))
	    addCriticalDamage("+1d6");
	if (pc.hasEquipment("Executioner's Bracers (paragon tier)"))
	    addCriticalDamage("+2d6");
	if (pc.hasEquipment("Executioner's Bracers (epic tier)"))
	    addCriticalDamage("+3d6");

	// Deal with the magical bonus to critical damage
	String magicCrit = getMagicCrit();

	// Hack for Graceful weapon: -
	if (magicCrit.contains("+ Dexterity modifier damage x")) {
	    int mod = Integer.parseInt(magicCrit.replace(
		    "+ Dexterity modifier damage x", ""));
	    int dex = pc.getAttributes().get(Attribute.Dexterity);
	    int dexmod = ((dex) - 10) / 2;
	    magicCrit = "+" + (mod * dexmod);
	    addCriticalDamage(magicCrit);
	    setCriticalType("");
	} else {
	    // ditch the word "damage"
	    String type = "";
	    String dam = "";
	    magicCrit = magicCrit.replace("damage", "");
	    // now parse out the final magic damage and type (if any)
	    Pattern pattern = Pattern.compile("(\\+(\\d+)d(\\d+))(\\s*)(.*)");
	    Matcher matcher = pattern.matcher(magicCrit);

	    if (matcher.find()) {
		dam = matcher.group(1);
		type = matcher.group(5);
		addCriticalDamage(dam);
		setCriticalType(type);
	    }
	}
    }

}
