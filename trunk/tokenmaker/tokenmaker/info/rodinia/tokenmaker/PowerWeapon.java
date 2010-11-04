package info.rodinia.tokenmaker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PowerWeapon models each weapon a PC owns and might use a power with.
 * In the Character Builder each PC can own and equip multiple weapons and 
 * stats for each attack power are calculated and stored for each such weapon.
 * This class models this.
 * 
 * @author Blakey, Summer 2010
 */
public class PowerWeapon {
    private Power power;
    private Weapon weapon;
    private int attackBonus;
    private String damageString;
    private String damageType = "";
    private String attackStat;
    private String defence;
    private String hitComponents;
    private String damageComponents;
    private String conditions;
    private String healing;
 
    /**
     * Build a PowerWeapon entry.
     * @param powerName - the name of the power this weapon/attack belongs to
     * @param name - the name of the weapon itself (e.g. +3 rapier)
     * @param attackBonus - the bonus to hit (.e.g. +12)
     * @param damageString - damage (e.g. 1d12+2)
     * @param damageType - type of damage (e.g. force)
     * @param attackStat - which attribute this is based off (e.g. Strength)
     * @param defence - which defence it attacks (e.g. Reflex
     * @param hitComponents - how the bonus to hit is made up  
     * @param damageComponents - how the bonus to damage is made up.
     * @param conditions - any conditions that go with this attack (e.g. Sneak Attack)
     * @param healing - any special healing extras 
     */
    public PowerWeapon(Power power, Weapon weapon, int attackBonus, String damageString,
	    String damageType, String attackStat, String defence,
	    String hitComponents, String damageComponents, String conditions, String healing) {
	this.power = power;
	this.weapon = weapon;
	this.attackBonus = attackBonus;
	this.damageString = damageString;
	this.damageType = damageType;
	this.attackStat = attackStat;
	this.defence = defence;
	this.hitComponents = hitComponents;
	this.damageComponents = damageComponents;
	this.conditions = conditions;
	this.healing = healing;
    }

    public PowerWeapon(PowerWeapon copy) {
	this(copy.getPower(), copy.getWeapon(), copy.getAttackBonus(), copy.getDamageString(), copy.getDamageType(), copy.getAttackStat(),
		copy.getDefence(), copy.getHitComponents(), copy.getDamageComponents(), copy.getConditions(), copy.getHealing());
    }
 
     public int getAttackBonus() {
        return attackBonus;
    }

    public void setAttackBonus(int attackBonus) {
        this.attackBonus = attackBonus;
    }

    public String getDamageString() {
        return damageString;
    }

    public void setDamageString(String damageString) {
        this.damageString = damageString;
    }

    public String getDamageType() {
        return damageType;
    }

    public void setDamageType(String damageType) {
        this.damageType = damageType;
    }

    public String getAttackStat() {
        return attackStat;
    }

    public void setAttackStat(String attackStat) {
        this.attackStat = attackStat;
    }

    public String getDefence() {
        return defence;
    }

    public void setDefence(String defence) {
        this.defence = defence;
    }

    public String getHitComponents() {
        return hitComponents;
    }

    public void setHitComponents(String hitComponents) {
        this.hitComponents = hitComponents;
    }

    public String getDamageComponents() {
        return damageComponents;
    }

    public void setDamageComponents(String damageComponents) {
        this.damageComponents = damageComponents;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }

    public String getHealing() {
        return healing;
    }

    public void setHealing(String healing) {
        this.healing = healing;
    }

    public String getName() {
        return weapon.getName();
    }    

    public String toString() {
	return weapon.getName() + " (+" + attackBonus + " vs " + defence + "; "
		+ damageString + " " + damageType + " damage)";
    }


    public Power getPower() {
        return power;
    }

    public void setPower(Power power) {
        this.power = power;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public String getCriticalDamage() {
        return getWeapon().getCriticalDamage();
    }

    public String getCriticalType() {
        return getWeapon().getCriticalType();
    }

}
