package info.rodinia.tokenmaker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MagicWeaponCompendiumEntry extends CompendiumEntry {

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
	// System.out.println("Crit: " + critical);
	return critical;
    }

    @Override
    protected void formatHTML() {
	super.formatHTML(); // make sure we do the generic stuff

	// add full path to the images
	myHTML = myHTML
		.replace("<img src=\"images/",
			"<img src=\"http://www.wizards.com/dndinsider/compendium/images/");

	// add some formatting codes
	myHTML = myHTML
		.replace(
			"class=\"magicitem\"",
			"style=\"font:aerial;font-size:1.35em;font-weight: bold;line-height:2;padding-left:15px;margin:0;color:white;background: #d8941d;\" class=\"magicitem\"");
	myHTML = myHTML
		.replace(
			"class=\"player\"",
			"style=\"font:aerial;font-size:1.35em;font-weight: bold;line-height:2;padding-left:15px;margin:0;color:white;background: #1d3d5e;\" class=\"player\"");

    }
}
