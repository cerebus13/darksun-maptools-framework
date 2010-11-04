package info.rodinia.tokenmaker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeaponCompendiumEntry extends CompendiumEntry {

    public WeaponCompendiumEntry(String stringUrl) {
	super(stringUrl);
    }

    public String getDamage() {
	String damage = "";
	// Parse HTML for damage string
	Pattern pattern = Pattern.compile(".*?<b>Damage</b>: (.+?)<.*");
	Matcher matcher = pattern.matcher(myHTML);
	if (matcher.find()) {
	    damage = matcher.group(1);
	}
	return damage;
    }

    public String getGroup() {
	String group = "";
	// Parse HTML for group string
	//<b>Group</b>: <br/>Axe
	Pattern pattern = Pattern.compile(".*?<b>Group</b>: <br/>(.+?)\\(.*");
	Matcher matcher = pattern.matcher(myHTML);
	if (matcher.find()) {
	    group = matcher.group(1);
	}
	return group;
    }
    
    public boolean isHighCrit() {
	return myHTML.contains("High Crit");
    }
}