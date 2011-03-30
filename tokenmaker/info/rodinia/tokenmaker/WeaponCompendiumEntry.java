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
	// <b>Group</b>: <br/>Axe
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

    @Override
    protected void formatHTML() {
	super.formatHTML(); // make sure we do the generic stuff

	// add full path to the images
	myHTML = myHTML
		.replace("<img src=\"images/","<img src=\"" + ((TokenMaker.isRemote) ? TokenMaker.imagePath_remote : TokenMaker.imagePath_local));

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