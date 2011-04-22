package tokenmaker2;

import java.util.ArrayList;

/**
 * Power models D&D 4E powers, both those used by players and monsters
 * 
 * @author Blakey, Summer 2010
 */
public class Power
{

    // compendium info
    private int                    id          = 0;
    private String                 html        = null;

    // Actual D&D stats side of things
    // e.g. "Melee Basic Attack"
    private String                 name        = "";
    private int                    level       = 0;
    // e.g. "At-Will", "Encounter"
    private String                 usage       = "";
    // e.g. "Standard Action", "Move"
    private String                 actionType  = "";
    private ArrayList<PowerWeapon> weapons     = new ArrayList<PowerWeapon>();
    private String                 detail      = "";
    private boolean                inSpellbook = false;
    private String                 keywords    = "";
    // Blakey's parser doesn't do MM3 auras well, but it is hard to skip them
    // so instead I'll let them fly and just flag this true if the word "Aura"
    // shows up and then skip adding it as a power
    private boolean                isJunkAura  = false;
    private boolean                isBasicAtk  = false;
    private String                 icon        = "";
    private String                 range       = "";
    private String                 requires    = "";
    private int                    atkBonus    = 0;
    private String                 atkDefense  = "";
    private String                 immunes     = "";
    private String                 effect      = "";

    // the HTML we'll put out to the stat card
    @SuppressWarnings("unused")
    private String                 statCard    = null;

    public Power(Power copy)
    {
        setId(copy.getId());
        this.html = null;
        this.name = copy.name;
        this.level = copy.level;
        this.usage = copy.usage;
        this.actionType = copy.actionType;
        copyWeapons(copy);
        this.detail = copy.detail;
    }

    // do a deep copy of the weapons
    private void copyWeapons(Power copy)
    {
        weapons = new ArrayList<PowerWeapon>();
        for (PowerWeapon w : copy.getWeapons())
        {
            PowerWeapon newpw = new PowerWeapon(w);
            newpw.setPower(this);
            weapons.add(newpw);
        }
    }

    public Power(String name)
    {
        this.name = name;
    }

    public Power(int id)
    {
        this.id = id;
    }

    public String getHTML()
    {
        if (id == 0)
            return "";
        if (html == null)
        {
            PowerCompendiumEntry com = new PowerCompendiumEntry(this);
            html = com.getHTML();
            html = html.replace("<img src=\""
                    + ((TokenMaker.isRemote) ? TokenMaker.imagePath_remote
                            : TokenMaker.imagePath_local)
                    + "bullet.gif\" alt=\"\"/>", "-");
        }
        return html;
    }

    @Override
    public String toString()
    {
        String result = name + " (" + actionType + ", " + usage + ")";
        // if (weapons.size() != 0) result += weapons.get(0);
        return result;
    }

    public boolean isInSpellbook()
    {
        return inSpellbook;
    }

    public void setInSpellbook(boolean inSpellbook)
    {
        this.inSpellbook = inSpellbook;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public void setUsage(String usage)
    {
        this.usage = usage;
    }

    public void setActionType(String actionType)
    {
        this.actionType = actionType;
    }

    public void addWeapon(PowerWeapon weapon)
    {
        weapons.add(weapon);
    }

    public void setWeapons(ArrayList<PowerWeapon> weapons)
    {
        this.weapons = weapons;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        this.level = level;
    }

    public String getUsage()
    {
        return usage;
    }

    public String getActionType()
    {
        return actionType;
    }

    public ArrayList<PowerWeapon> getWeapons()
    {
        return weapons;
    }

    public String getDetail()
    {
        return detail;
    }

    public void setDetail(String detail)
    {
        this.detail = detail;
    }

    public String getKeywords()
    {
        return keywords;
    }

    public void setKeywords(String s)
    {
        keywords = s;
    }

    public boolean getIsJunkAura()
    {
        return isJunkAura;
    }

    public void setIsJunkAura(Boolean b)
    {
        isJunkAura = b;
    }

    public boolean getIsBasicAtk()
    {
        return isBasicAtk;
    }

    public void setIsBasicAtk(Boolean b)
    {
        isBasicAtk = b;
    }

    public String getIcon()
    {
        return icon;
    }

    public void setIcon(String s)
    {
        icon = s;
    }

    public String getRange()
    {
        return range;
    }

    public void setRange(String s)
    {
        range = s;
    }

    public String getRequires()
    {
        return requires;
    }

    public void setRequires(String s)
    {
        requires = s;
    }

    public int getAtkBonus()
    {
        return atkBonus;
    }

    public void setAtkBonus(int i)
    {
        atkBonus = i;
    }

    public String getAtkDefense()
    {
        return atkDefense;
    }

    public void setAtkDefense(String s)
    {
        atkDefense = s;
    }

    public String getImmunes()
    {
        return immunes;
    }

    public void setImmunes(String s)
    {
        immunes = s;
    }

    public String getEffect()
    {
        return effect;
    }

    public void setEffect(String s)
    {
        effect = s;
    }
}
