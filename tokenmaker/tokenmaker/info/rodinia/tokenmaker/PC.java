package info.rodinia.tokenmaker;

import info.rodinia.tokenmaker.TokenMaker.Attribute;
import info.rodinia.tokenmaker.TokenMaker.Skill;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PC holds all the info about an PC. It is designed to model all the D&D stats
 * for a player character and it also knows a little about its Wizard's
 * Compendium entry. This is held on the PC so that we only have to go off to
 * the save file to read this PC's page once and thereafter we keep the info
 * local on the PC object.
 * 
 * @author Blakey, Summer 2010
 */
public class PC extends Character {

    // my DDi save file on disk
    private File saveFile;

    // D&D statistics
    private String race;
    private String PCClass;
    private String vision;
    private File portrait; // my picture
    private ArrayList<Weapon> weapons;
    private ArrayList<String> classFeatures;
    private ArrayList<Feat> feats;
    private ArrayList<Equipment> equipment;
    private ArrayList<Ritual> rituals;

    public PC(String name) {
	super();
	this.name = name;
	this.actionPoints = 1;
	weapons = new ArrayList<Weapon>();
	classFeatures = new ArrayList<String>();
	feats = new ArrayList<Feat>();
	equipment = new ArrayList<Equipment>();
    }


    public String toString() {
	return name + " (Level " + level + " " + race + " " + PCClass + ")";
    }

    public boolean hasRitual(String ritual) {
	for (Ritual r : rituals) {
	    if (r.getName().equals(ritual)) {
		return true;
	    }
	}
	return false;
    }
    
    public ArrayList<Ritual> getRituals() {
        return rituals;
    }


    public void setRituals(ArrayList<Ritual> rituals) {
        this.rituals = rituals;
    }

    public boolean hasFeat(String feat) {
	for (Feat f : feats) {
	    if (f.getName().equals(feat)) {
		return true;
	    }
	}
	return false;
    }
    
    public ArrayList<Feat> getFeats() {
        return feats;
    }


    public void setFeats(ArrayList<Feat> feats) {
        this.feats = feats;
    }

    public boolean hasEquipment(String item) {
	for (Equipment e : equipment) {
	    if (e.getName().equals(item)) {
		return true;
	    }
	}
	return false;
    }
    
    public ArrayList<Equipment> getEquipment() {
        return equipment;
    }


    public void setEquipment(ArrayList<Equipment> equipment) {
        this.equipment = equipment;
    }

    public boolean hasFeature(String feature) {
	for (String s : classFeatures) {
	    if (s.equals(feature)) {
		return true;
	    }
	}
	return false;
    }
    
    public ArrayList<String> getClassFeatures() {
        return classFeatures;
    }


    public void setClassFeatures(ArrayList<String> classFeatures) {
        this.classFeatures = classFeatures;
    }


    public void addWeapon(Weapon weapon) {
	weapons.add(weapon);
    }

    public ArrayList<Weapon> getWeapons() {
	return weapons;
    }

    public Weapon getWeapon(String name) {
	Weapon weapon = null;
	for (Weapon w : weapons) {
	    if (w.getName().equals(name)) {
		weapon = w;
		break;
	    }
	}
	return weapon;
    }

    public String getVision() {
	return vision;
    }

    public void setVision(String vision) {
	this.vision = vision;
    }
    
    @Override
    public String getDescription() {
	return getRace();
    }

    @Override
    public String getRole() {
	return getPCClass();
    }

    /**
     * Used by PCMaker to display a PC's base stats
     * 
     * @return the stats.
     */
    public String getStats() {
	String result = "Defenses: AC " + AC + ", Ref " + reflex + ", Fort "
		+ fortitude + ", Will " + will + "\n";
	result += "HP: " + HP + " (Surges " + healingSurges + ")\n";
	result += "Initiative: +" + initiative + "\n";
	result += "Speed: " + speed;
	return result;
    }

    public File getPortrait() {
	return portrait;
    }

    public void setPortrait(File myPortrait) {
	this.portrait = myPortrait;
    }

    public String getRace() {
	return race;
    }

    public String getPCClass() {
	return PCClass;
    }

    public void setRace(String myRace) {
	this.race = myRace;
    }

    public void setClass(String myClass) {
	this.PCClass = myClass;
    }


    public String getSenses() {
	return "Perception +" + getSkills().get(Skill.Perception) + "; "+ getVision() + " vision\n";
    }


    public File getSaveFile() {
	return saveFile;
    }

    public void setSaveFile(File saveFile) {
	this.saveFile = saveFile;
    }


    public void setPCClass(String pCClass) {
	PCClass = pCClass;
    }
}
