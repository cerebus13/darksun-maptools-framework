package info.rodinia.tokenmaker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MagicWeaponCompendiumEntry extends CompendiumEntry{

    public MagicWeaponCompendiumEntry(String stringUrl) {
	super(stringUrl);
    }

    public String getMagicCritical() {
	String critical = "";
	// Parse HTML for critical string
	Pattern pattern = Pattern.compile(".*?<b>Critical</b>: (.+?)<.*");
	Matcher matcher = pattern.matcher(myHTML);
	if (matcher.find()) {
	    critical = matcher.group(1);
	}
	//System.out.println("Crit: " + critical);
	return critical;
    }
}
