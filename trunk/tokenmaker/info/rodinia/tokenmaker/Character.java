package info.rodinia.tokenmaker;

import info.rodinia.tokenmaker.TokenMaker.Attribute;
import info.rodinia.tokenmaker.TokenMaker.Skill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

/**
 * A superclass designed to model all the
 * D&D stats for a monster or pc. 
 * 
 * @author Blakey, Summer 2010
 */
public class Character {

    // Compendium entry info
    protected String html = null;

    // D&D stats section
    protected String name;
    // D&D stats section
    protected String combatRole; // artillery, brute, controller, lurker,
    // skirmisher, soldier, leader
    protected String groupRole = "Standard"; // standard, elite, solo, minion

    protected int level = 0; // level.
    protected String size = "Medium";
    protected ArrayList<Power> powers = new ArrayList<Power>();
    protected int healingSurges;
    protected int healingSurgeValue = 0;
    protected int xp;

    protected int initiative = 0;
    protected int initRolls = 1;	// how many times you roll init
    protected String senses = "";
    protected String aura = "";
    protected String auraDescription = "";
    protected int auraRange = 1;
    protected String auraKeywords = "";
    protected int HP = 0;
    protected int bloodied = 0;
    protected String regeneration = "";
    protected int AC = 10;
    protected int fortitude = 10;
    protected int reflex = 10;
    protected int will = 10;
    protected String immune = "";
    protected String resist = "";
    protected String vulnerable = "";
    protected int savingThrows = 0;
    protected String speed = "6";
    protected int actionPoints = 0;
    protected String alignment = "Unaligned";
    protected String equipment = "";
    protected String languages = "";
    protected String description = "";
    protected String realDescription = "";

    protected TreeMap<Skill, Integer> skills = new TreeMap<Skill, Integer>();
    protected TreeMap<Attribute, Integer> attributes = new TreeMap<Attribute, Integer>();

    public String getDescription() {
        return description;
    }

    public String getAura() {
        return aura;
    }

    public void setAura(String aura) {
        this.aura = aura;
    }

    public String getAuraDescription() {
        return auraDescription;
    }

    public void setAuraDescription(String auraDescription) {
        this.auraDescription = auraDescription;
    }

    public int getAuraRange()
    {
        return auraRange;
    }

    public void setAuraRange(int r)
    {
        this.auraRange = r;
    }

    public String getAuraKeywords()
    {
        return auraKeywords;
    }

    public void setAuraKeywords(String s)
    {
        this.auraKeywords = s;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHtml() {
        return html;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCombatRole() {
	return combatRole;
    }
    
    public String getRole() {
	return groupRole + " " + combatRole;
    }
    
    public String getGroupRole() {
	return groupRole;
    }

   public String getResistances() {
	return resist;
    }

    public void setResistances(String resistances) {
	this.resist = resistances;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getItemUsage() {
        return (((level - 1) / 10) + 1);
    }

    public int getHealingSurges() {
        return healingSurges;
    }

    public void setHealingSurges(int healingSurges) {
        this.healingSurges = healingSurges;
    }

    public int getHealingSurgeValue() {
        return healingSurgeValue;
    }

    public void setHealingSurgeValue(int healingSurgeValue) {
        this.healingSurgeValue = healingSurgeValue;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public ArrayList<Power> getPowers() {
        return powers;
    }

    public void setPowers(ArrayList<Power> powers) {
        this.powers = powers;
    }

    public int getInitiative() {
        return initiative;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public int getInitRolls() {
        return initRolls;
    }


    public void setInitRolls(int initRolls) {
        this.initRolls = initRolls;
    }

   public String getSenses() {
        return senses;
    }

    public void setSenses(String senses) {
        this.senses = senses;
    }

    public int getHP() {
        return HP;
    }

    public void setHP(int hP) {
        HP = hP;
        setBloodied(hP/2);
    }

    public int getBloodied() {
        return bloodied;
    }

    public void setBloodied(int bloodied) {
        this.bloodied = bloodied;
    }

    public String getRegeneration() {
        return regeneration;
    }

    public void setRegeneration(String regeneration) {
        this.regeneration = regeneration;
    }

    public int getAC() {
        return AC;
    }

    public void setAC(int aC) {
        AC = aC;
    }

    public int getFortitude() {
        return fortitude;
    }

    public void setFortitude(int fortitude) {
        this.fortitude = fortitude;
    }

    public int getReflex() {
        return reflex;
    }

    public void setReflex(int reflex) {
        this.reflex = reflex;
    }

    public int getWill() {
        return will;
    }

    public void setWill(int will) {
        this.will = will;
    }

    public String getImmune() {
        return immune;
    }

    public void setImmune(String immune) {
        this.immune = immune;
    }

    public String getResist() {
        return resist;
    }

    public void setResist(String resist) {
        this.resist = resist;
    }

    public String getVulnerable() {
        return vulnerable;
    }

    public void setVulnerable(String vulnerable) {
        this.vulnerable = vulnerable;
    }

    public int getSavingThrows() {
        return savingThrows;
    }

    public void setSavingThrows(int savingThrows) {
        this.savingThrows = savingThrows;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public int getActionPoints() {
        return actionPoints;
    }

    public void setActionPoints(int actionPoints) {
        this.actionPoints = actionPoints;
    }

    public String getAlignment() {
        return alignment;
    }

    public void setAlignment(String alignment) {
        this.alignment = alignment;
    }

    public String getEquipmentSimple() {
        return equipment;
    }

    public void setEquipmentSimple(String equipment) {
        this.equipment = equipment;
    }

    public String getLanguages() {
        return languages;
    }

    public void setLanguages(String languages) {
        this.languages = languages;
    }

    public String getRealDescription()
    {
        return realDescription;
    }

    public void setRealDescription(String s)
    {
        this.realDescription = s;
    }

    public TreeMap<Skill, Integer> getSkills() {
        return skills;
    }

    public void setSkills(TreeMap<Skill, Integer> skills) {
        this.skills = skills;
    }

    public TreeMap<Attribute, Integer> getAttributes() {
        return attributes;
    }

    public void setAttributes(TreeMap<Attribute, Integer> attributes) {
        this.attributes = attributes;
    }

    public int getXp() {
	return xp;
    }

    public void setXp(int xp) {
	this.xp = xp;
    }

    public String getSkillsHtml() {
	String skillsList = "";
	// Skills set
	Collection<Integer> v = skills.values();
	Collection<Skill> k = skills.keySet();
	Iterator<Integer> vi = v.iterator();
	Iterator<Skill> ki = k.iterator();

	int skillNumber = 0;
	while (ki.hasNext()) {
	    if (skillsList != "")
		skillsList += ", ";
	    skillsList += "[r:macroLink(\""+ki.next()+"\", \"rollSkillCheck@Lib:Blakey\", \"all\", \"SkillNumber="+skillNumber+"\", currentToken())] ";
	    int skillValue = vi.next();
	    if (skillValue >= 0) skillsList += "+";
	    skillsList += skillValue; 
	    skillNumber++;
 	}
	return skillsList;
    }
    
    public String getSkillsList() {
	String skillList = "";
	Collection<Integer> v = skills.values();
	Collection<Skill> k = skills.keySet();
	Iterator<Integer> vi = v.iterator();
	Iterator<Skill> ki = k.iterator();
	while (ki.hasNext()) {
	    skillList+= ki.next() + "=" +vi.next()+"; ";
	}
	return skillList;
    }
    
    public String getAttributesList() {
	String attrList = "";
	Collection<Integer> v = attributes.values();
	Collection<Attribute> k = attributes.keySet();
	Iterator<Integer> vi = v.iterator();
	Iterator<Attribute> ki = k.iterator();
	while (ki.hasNext()) {
	    attrList+= ki.next() + "=" +vi.next()+"; ";
	}
	return attrList;
    }

    /**
     * A debug method which will print out (to System.out.println) a
     * representation of the Character.
     */
    public void print() {
	String out = "\n\n" + toString() + "\n";
	out += size + "; XP "+xp+ "\n";
	out += "Initiative +" + initiative + "    ";
	out += "Senses " + senses + "\n";
	out += "HP " + HP + "; Bloodied " + bloodied + "\n";
	if (regeneration != null)
	    out += "Regeneration " + regeneration + "\n";
	out += "AC " + AC + "; Fort " + fortitude + ", Reflex " + reflex
		+ ", Will " + will + "\n";
	if (immune != null)
	    out += "Immune " + immune;
	if (resist != null)
	    out += "Resist  " + resist;
	if (vulnerable != null)
	    out += "Vulnerable " + vulnerable;
	if (immune != null || resist != null || vulnerable != null)
	    out += "\n";
	out += "Saving Throws +" + savingThrows + "\n";
	out += "Speed " + speed + "\n";
	out += "Action Points " + actionPoints + "\n";
	for (Power p : powers) {
	    String usage = p.getUsage();
	    String action = p.getActionType();
	    out += p.getName();
	    if (usage != null || action != null)
		out += " (";
	    if (action != null)
		out += action;
	    if (usage != null && action != null)
		out += ", ";
	    if (usage != null)
		out += usage;
	    if (usage != null || action != null)
		out += ")";
	    out += "\n";
	    out += p.getDetail() + "\n";
	}
	
	out += getSkillsList() + "\n";
	out += getAttributesList() + "\n";
	out += "Alignment " + getAlignment() + "    Languages " + getLanguages() + "\n";
	System.out.println(out);
    }
}
