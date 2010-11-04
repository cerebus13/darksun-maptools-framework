package info.rodinia.tokenmaker;

import java.io.BufferedWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * NPCToken models an NPC MapTool token. It subclasses Token, which does most of
 * the work. NPCtoken specialises by providing overrides to the methods that
 * write out power macros.
 * 
 * @author Blakey, Summer 2010
 * 
 */
public class NPCToken extends Token {
    // actual stats behind this token
    private NPC myNpc;

    public NPCToken(NPC newNpc) {
	super(newNpc);
	myNpc = newNpc;
	setTokenType("NPC");
	setName(myNpc.getName());
    }

    @Override
    protected String buildPowerHtml(Power p) {
	String html = null;
	String font = "background: #c3c6ad;"; // default
	String linkFont = "";
	
	if (p.getUsage().toLowerCase().contains("at-will")) {
	    font = "color:white;background: #619869;";
	    linkFont= "color:white";
	}
	
	if (p.getUsage().toLowerCase().contains("recharge")) {
	    font = "[R: if (json.contains(ExpendedRechargePowers, \""
		    + p.getName()
		    + "\"), \"color:purple;background: white;\", \"color:white;background: purple;\")]";
	    linkFont = "[R: if (json.contains(ExpendedRechargePowers, \""
		    + p.getName()
		    + "\"), \"color:purple;\", \"color:white;\")]";
	}
	
	if (p.getUsage().toLowerCase().contains("encounter")) {
	    font = "[R: if (json.contains(ExpendedEncounterPowers, \""
		    + p.getName()
		    + "\"), \"color:#961334;background: white;\", \"color:white;background: #961334;\")]";
	    linkFont = "[R: if (json.contains(ExpendedEncounterPowers, \""
		    + p.getName()
		    + "\"), \"color:#961334;\", \"color:white;\")]";
	}
	
	if (p.getUsage().toLowerCase().contains("daily")) {
	    font = "[R: if (json.contains(ExpendedDailyPowers, \""
		    + p.getName()
		    + "\"), \"color:#4d4d4f;background: white;\", \"color:white;background: #4d4d4f;\")]";
	    linkFont = "[R: if (json.contains(ExpendedDailyPowers, \""
		    + p.getName()
		    + "\"), \"color:#4d4d4f;\", \"color:white;\")]";
	}
	
	html = "<p style=\"font:aerial;display: block;padding: 2px 15px;margin: 0;"
		+ font + "\" class=\"flavor alt\">";
	
	// add the macro link with the right colour scheme
	String macroLink = "<span style=\"" + linkFont + "\">[r:macroLink(\"" + p.getName() + "\", \""
		+ p.getName() + "@TOKEN\", \"none\", \"\", currentToken())]</span> ";
	
	if (p.getName().contains("Basic Attack"))
	    macroLink = p.getName(); // no link for basic attacks.
	
	html += macroLink;
	
	// display action type
	if (p.getActionType() != "") html += p.getActionType();
	
	if (p.getUsage().contains("recharge")) html += "; " + p.getUsage();
	html += "</p>";

	// Convert recharge rolls into a macro link
	html = html
		.replace("recharge 4",
			"<span style=\"" + linkFont + "\">[r:macroLink(\"recharge\", \"RechargeRoll@Lib:Blakey\", \"all\", \"Recharge=4;Power="+p.getName()+"\", currentToken())]</span> 456");
	html = html
		.replace(
			"recharge 5",
			"<span style=\"" + linkFont + "\">[r:macroLink(\"recharge\", \"RechargeRoll@Lib:Blakey\", \"all\", \"Recharge=5;Power="+p.getName()+"\", currentToken())]</span> 56");
	html = html
		.replace("recharge 6",
			"<span style=\"" + linkFont + "\">[r:macroLink(\"recharge\", \"RechargeRoll@Lib:Blakey\", \"all\", \"Recharge=6;Power="+p.getName()+"\", currentToken())]</span> 6");

	// Now put out the power details.
	html += "<p style=\"font:aerial;display: block;padding: 2px 15px 2px 30px;margin: 0;background: #d6d6c2;\" class=\"flavorIndent\">";
	html += addMacroLinks(p);
	html += "</p>\n\n";
	return html;

    }

    @Override
    protected String buildPowerWeaponHtml(PowerWeapon pw) {
	return "";
    }

    @Override
    protected String buildHtml() {
	String html = myNpc.getHtml(); // populate monster!
	html = super.buildHtml();
	return html;
    }

    private String addMacroLinks(Power p) {
	String html = p.getDetail();
	boolean foundAttack = false;
	StringBuffer sb = new StringBuffer();
	// Convert attack rolls into a macro link
	Pattern pattern = Pattern
		.compile("\\+(\\d+) vs (\\S+);\\s([0-9]+d[0-9]+)?\\s?[\\+\\-]?\\s?([^a-zA-Z]?[0-9]?)\\s(.*?)\\s*([a-z]+\\s)*damage");
	Matcher matcher = pattern.matcher(html);

	while (matcher.find()) {
	    foundAttack = true;
	    String link = matcher.group(0);
	    link = link.replace("vs", "vs."); // stops secondary match
	    link = link.replace("damage", "Damage"); // stops secondary match
	    String attack = matcher.group(1);
	    String defence = matcher.group(2);
	    String dice = matcher.group(3);
	    String plus = matcher.group(4);
	    String type = matcher.group(6);
	    if (type == null)
		type = "";
	    if (dice == null)
		dice = "";
	    String replacement = "[r:macroLink(\""
		    + link
		    + "\", \"MakeAttackRoll@Lib:Blakey\", \"all\", \"AttackBonus="
		    + attack + ";Defence=" + defence
		    + ";Power="+p.getName()+";DamageDice=" + dice + "plus" + plus
		    + ";Usage="+p.getUsage()
		    + ";DamageType=" + type + "\", currentToken())]";
	    matcher.appendReplacement(sb, replacement);
	}
	matcher.appendTail(sb);
	html = sb.toString();

	sb = new StringBuffer();
	// Convert lone attack rolls into a macro link
	pattern = Pattern.compile("(\\+(\\d+) vs (\\S+))");
	matcher = pattern.matcher(html);

	while (matcher.find()) {
	    foundAttack = true;
	    String attack = matcher.group(2);
	    String defence = matcher.group(3);
	    String link = matcher.group(1);
	    String replacement = "[r:macroLink(\""
		    + link
		    + "\", \"MakeAttackRoll@Lib:Blakey\", \"all\", \"AttackBonus="
		    + attack + ";Defence=" + defence
		    + ";Power="+p.getName()+";Usage="+p.getUsage()+
		    "\", currentToken())]";
	    matcher.appendReplacement(sb, replacement);
	}
	matcher.appendTail(sb);
	html = sb.toString();
	sb = new StringBuffer();

	// Convert MM3 stat block attack rolls into a macro link
	pattern = Pattern
		.compile("\\+(\\d+) vs. (\\S+) Hit: ([0-9]+d[0-9]+)?\\s?[\\+\\-]?\\s?([^a-zA-Z]?[0-9]?)\\s(.*?)\\s*([a-z]+\\s)*damage");
	matcher = pattern.matcher(html);

	while (matcher.find()) {
	    foundAttack = true;
	    String link = matcher.group(0);
	    link = link.replace("damage", "Damage"); // stops secondary match
	    String attack = matcher.group(1);
	    String defence = matcher.group(2);
	    String dice = matcher.group(3);
	    String plus = matcher.group(4);
	    String type = matcher.group(6);
	    if (type == null)
		type = "";
	    if (dice == null)
		dice = "";
	    String replacement = "[r:macroLink(\""
		    + link
		    + "\", \"MakeAttackRoll@Lib:Blakey\", \"all\", \"AttackBonus="
		    + attack + ";Defence=" + defence
		    + ";Power="+p.getName()+";DamageDice=" + dice + "plus" + plus
		    + ";Usage="+p.getUsage()
		    + ";DamageType=" + type + "\", currentToken())]";
	    matcher.appendReplacement(sb, replacement);
	}
	matcher.appendTail(sb);
	html = sb.toString();	

	// Convert lone damage rolls into a macro link
	pattern = Pattern
		.compile("(([0-9]+d[0-9]+)\\s?[\\+\\-]?\\s?([^a-zA-Z]?[0-9]?)\\s([a-z]+\\s)*damage)");
	matcher = pattern.matcher(html);
	sb = new StringBuffer();
	while (matcher.find()) {
	    String dice = matcher.group(2);
	    String plus = matcher.group(3);
	    String type = matcher.group(4);
	    if (type == null)
		type = "";
	    String link = matcher.group(1);
	    link.replace("damage", "Damage");
	    String replacement = "[r:macroLink(\""
		    + link
		    + "\", \"MakeDamageRoll@Lib:Blakey\", \"all\", \"DamageDice="
		    + dice + "plus" + plus + ";DamageType=" + type
		    + "\", currentToken())]";
	    matcher.appendReplacement(sb, replacement);
	}
	matcher.appendTail(sb);
	html = sb.toString();

	// Convert lone fixed damage into a macro link
	pattern = Pattern
		.compile("(([0-9]+)\\s([a-z]+\\s)*damage)");
	matcher = pattern.matcher(html);
	sb = new StringBuffer();
	while (matcher.find()) {
	    String dice = "0";
	    String plus = matcher.group(2);
	    String type = matcher.group(3);
	    if (type == null)
		type = "";
	    String link = matcher.group(1);
	    link.replace("damage", "Damage");
	    String replacement = "[r:macroLink(\""
		    + link
		    + "\", \"MakeDamageRoll@Lib:Blakey\", \"all\", \"DamageDice="
		    + dice + "plus" + plus + ";DamageType=" + type
		    + "\", currentToken())]";
	    matcher.appendReplacement(sb, replacement);
	}
	matcher.appendTail(sb);
	html = sb.toString();

	// if this power has no attack specified, add a UsePower macro link to use it.
	if (!foundAttack) {
	    html += "<br/>[r:macroLink(\"Use "+p.getName()+"\", \"UsePower@Lib:Blakey\", \"all\", \"Power="
		    + p.getName().replace(" ", "+")
		    + ";Usage="
		    + p.getUsage().replace(" ", "+") + "\", currentToken())]";
	}
	return html;
    }

    protected String getPowerText(Power p) {
	return p.getDetail();
    }
}
