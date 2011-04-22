package info.rodinia.tokenmaker;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.*;

import info.rodinia.tokenmaker.TokenMaker.Attribute;
import info.rodinia.tokenmaker.TokenMaker.Skill;

import org.apache.commons.lang.ArrayUtils;

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

    public NPCToken(NPC newNpc, String propType, boolean isJoe) {
        super(newNpc, isJoe);
        myNpc = newNpc;
        setTokenType("NPC");
        setName(myNpc.getName());
        setPropertyType(propType);
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
//		.compile("\\+(\\d+) vs (\\S+);\\s([0-9]+d[0-9]+)?\\s?[\\+\\-]?\\s?([^a-zA-Z]?[0-9]?)\\s*(.*?)\\s*damage");
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
	    String type = matcher.group(5);
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
	    link = link.replace("damage", "Damage");
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
	    link = link.replace("damage", "Damage");
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

    // Save as an XML file with a .monster extension
    public void saveXML()
    {
        if (tokenName == null || tokenFile == null)
            return;
        // Write out the XML
        try
        {
            FileWriter w = new FileWriter(tokenFile);
            BufferedWriter writer = new BufferedWriter(w);
            writer.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            writer.write("<Monster xsi:type=\"Monster\" xmlns:loader=\"http://www.wizards.com/listloader\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n");
            // Abilities
            writer.write("  <AbilityScores>\n");
            writer.write("    <Values>\n");
            writer.write("      <AbilityScoreNumber xsi:type=\"AbilityScoreNumber\" FinalValue=\"" + me.getAttributes().get(Attribute.Strength) + "\">\n");
            writer.write("        <Name>Strength</Name>\n");
            writer.write("      </AbilityScoreNumber>\n");
            writer.write("      <AbilityScoreNumber xsi:type=\"AbilityScoreNumber\" FinalValue=\"" + me.getAttributes().get(Attribute.Constitution) + "\">\n");
            writer.write("        <Name>Constitution</Name>\n");
            writer.write("      </AbilityScoreNumber>\n");
            writer.write("      <AbilityScoreNumber xsi:type=\"AbilityScoreNumber\" FinalValue=\"" + me.getAttributes().get(Attribute.Dexterity) + "\">\n");
            writer.write("        <Name>Dexterity</Name>\n");
            writer.write("      </AbilityScoreNumber>\n");
            writer.write("      <AbilityScoreNumber xsi:type=\"AbilityScoreNumber\" FinalValue=\"" + me.getAttributes().get(Attribute.Intelligence) + "\">\n");
            writer.write("        <Name>Intelligence</Name>\n");
            writer.write("      </AbilityScoreNumber>\n");
            writer.write("      <AbilityScoreNumber xsi:type=\"AbilityScoreNumber\" FinalValue=\"" + me.getAttributes().get(Attribute.Wisdom) + "\">\n");
            writer.write("        <Name>Wisdom</Name>\n");
            writer.write("      </AbilityScoreNumber>\n");
            writer.write("      <AbilityScoreNumber xsi:type=\"AbilityScoreNumber\" FinalValue=\"" + me.getAttributes().get(Attribute.Charisma) + "\">\n");
            writer.write("        <Name>Charisma</Name>\n");
            writer.write("      </AbilityScoreNumber>\n");
            writer.write("    </Values>\n");
            writer.write("  </AbilityScores>\n");
            // Defenses
            writer.write("  <Defenses>\n");
            writer.write("    <Values>\n");
            writer.write("      <SimpleAdjustableNumber xsi:type=\"SimpleAdjustableNumber\" FinalValue=\"" + me.getAC() + "\">\n");
            writer.write("        <Name>AC</Name>\n");
            writer.write("      </SimpleAdjustableNumber>\n");
            writer.write("      <SimpleAdjustableNumber xsi:type=\"SimpleAdjustableNumber\" FinalValue=\"" + me.getFortitude() + "\">\n");
            writer.write("        <Name>Fortitude</Name>\n");
            writer.write("      </SimpleAdjustableNumber>\n");
            writer.write("      <SimpleAdjustableNumber xsi:type=\"SimpleAdjustableNumber\" FinalValue=\"" + me.getReflex() + "\">\n");
            writer.write("        <Name>Reflex</Name>\n");
            writer.write("      </SimpleAdjustableNumber>\n");
            writer.write("      <SimpleAdjustableNumber xsi:type=\"SimpleAdjustableNumber\" FinalValue=\"" + me.getWill() + "\">\n");
            writer.write("        <Name>Will</Name>\n");
            writer.write("      </SimpleAdjustableNumber>\n");
            writer.write("    </Values>\n");
            writer.write("  </Defenses>\n");
            // Useless?
            writer.write("  <AttackBonuses />\n");
            // Skills
            writer.write("  <Skills>\n");
            writer.write("    <Values>\n");
            if (!myNpc.getImprovedSkills().isEmpty())
            {
                for (String skillName : myNpc.getImprovedSkills().split(", "))
                {
                    skillName = skillName.split(" ")[0];
                    writer.write("      <SkillNumber xsi:type=\"SkillNumber\" FinalValue=\"" + me.getSkills().get(Skill.valueOf(skillName)) + "\">\n");
                    writer.write("        <Trained>true</Trained>\n");
                    writer.write("        <Name>" + skillName + "</Name>\n");
                    writer.write("      </SkillNumber>\n");
                }
            }
            writer.write("    </Values>\n");
            writer.write("  </Skills>\n");
            // Size
            writer.write("  <Size>\n");
            writer.write("    <ReferencedObject>\n");
            writer.write("      <Name>" + me.getSize() + "</Name>\n");
            writer.write("    </ReferencedObject>\n");
            writer.write("  </Size>\n");
            // Origin
            // split Description into: Origin and Type (Keywords)
            String[] split = me.getDescription().split(" ", 2);
            String ori = split[0].trim();
            ori = ori.substring(0,1).toUpperCase() + ori.substring(1);
            writer.write("  <Origin>\n");
            writer.write("    <ReferencedObject>\n");
            writer.write("      <Name>" + ori + "</Name>\n");
            writer.write("    </ReferencedObject>\n");
            writer.write("  </Origin>\n");
            // Type
            // split Description minus the Origin into: Type and Keywords
            // Compendium sometimes adds another word to the Type after a comma, so split that off too
            String[] split2 = split[1].split("\\(",2);
            String[] typeArray = split2[0].split(",",2);
            String typ = typeArray[0].trim();
            typ = typ.substring(0,1).toUpperCase() + typ.substring(1);
            writer.write("  <Type>\n");
            writer.write("    <ReferencedObject>\n");
            writer.write("      <Name>" + typ + "</Name>\n");
            writer.write("    </ReferencedObject>\n");
            writer.write("  </Type>\n");
            // isLeader
            String useLeader = "false";
            if (me.getCombatRole().toLowerCase().contains("leader"))
                useLeader = "true";
            writer.write("  <IsLeader>" + useLeader + "</IsLeader>\n");
            // GroupRole
            writer.write("  <GroupRole>\n");
            writer.write("    <ReferencedObject>\n");
            writer.write("      <Name>" + me.getGroupRole() + "</Name>\n");
            writer.write("    </ReferencedObject>\n");
            writer.write("  </GroupRole>\n");
            // Items
            writer.write("  <Items>\n");
            String[] itemSplit = me.getEquipmentSimple().split(",");
            int itemID = 1;
            for (String items : itemSplit)
            {
                // clean up the item name a bit
                items = items.replace(".", "");
                items = items.replace(":", "");
                items = items.trim();
                if (items.length() == 0)
                    continue;
                // use a regex to find a quantity
                int quantity = 1;
                Pattern pattern;
                Matcher matcher;
                pattern = Pattern.compile(".+\\((\\d+)\\)");
                matcher = pattern.matcher(items);
                if (matcher.matches())
                    quantity = Integer.parseInt(matcher.group(1));
                writer.write("    <ItemAndQuantity>\n");
                writer.write("      <Quantity>" + quantity + "</Quantity>\n");
                writer.write("      <Item id=\"ITEM" + itemID + "\">\n");
                writer.write("        <ReferencedObject>\n");
                writer.write("          <ID>ITEM" + itemID + "</ID>\n");
                writer.write("          <Name>" + items + "</Name>\n");
                writer.write("        </ReferencedObject>\n");
                writer.write("      </Item>\n");
                writer.write("    </ItemAndQuantity>\n");
                itemID += 1;
            }
            writer.write("  </Items>\n");
            // Languages
            writer.write("  <Languages>\n");
            int langID = 0;
            for (String lang : me.getLanguages().split(", "))
            {
                writer.write("    <ObjectReference id=\"LANG" + langID + "\">\n");
                writer.write("      <ReferencedObject>\n");
                writer.write("        <ID>LANG" + langID + "</ID>\n");
                writer.write("        <Name>" + lang + "</Name>\n");
                writer.write("      </ReferencedObject>\n");
                writer.write("    </ObjectReference>\n");
                langID += 1;
            }
            writer.write("  </Languages>\n");
            // Alignment
            writer.write("  <Alignment id=\"1\">\n");
            writer.write("    <ReferencedObject>\n");
            writer.write("      <ID>1</ID>\n");
            writer.write("      <Name>" + me.getAlignment() + "</Name>\n");
            writer.write("    </ReferencedObject>\n");
            writer.write("  </Alignment>\n");
            // Senses
            writer.write("  <Senses>\n");
            for (String sense : me.getSenses().replace(",",";").split("; "))
            {
                writer.write("    <SenseReference>\n");
                writer.write("      <ReferencedObject>\n");
                writer.write("        <Name>" + sense + "</Name>\n");
                writer.write("      </ReferencedObject>\n");
                writer.write("    </SenseReference>\n");
            }
            writer.write("  </Senses>\n");
            // Regeneration
            if (me.getRegeneration().isEmpty())
                writer.write("  <Regeneration FinalValue=\"" + 0 + "\">\n");
            else
                writer.write("  <Regeneration FinalValue=\"" + me.getRegeneration() + "\">\n");
            writer.write("    <Name>Regeneration</Name>\n");
            writer.write("  </Regeneration>\n");
            // Tactics - not implemented in Compendium yet
            writer.write("  <Tactics></Tactics>\n");
            // Description - Masterplan doesn't import this for some reason
            writer.write("  <Description>" + me.getRealDescription() + "</Description>\n");
            // Keywords
            writer.write("  <Keywords>\n");
            if (split2.length > 1)
            {
                String keywords = split2[1].replace(")","").trim();
                for (String key : keywords.split(", "))
                {
                    writer.write("    <ObjectReference>\n");
                    writer.write("      <ReferencedObject>\n");
                    writer.write("        <Name>" + key + "</Name>\n");
                    writer.write("      </ReferencedObject>\n");
                    writer.write("    </ObjectReference>\n");
                }
            }
            writer.write("  </Keywords>\n");
            // Powers - TBD
            writer.write("  <Powers>\n");
            // Aura first
            if (!me.getAura().isEmpty())
            {
                writer.write("    <MonsterTrait xsi:type=\"MonsterTrait\">\n");
                writer.write("      <Range FinalValue=\"" + me.getAuraRange() + "\">\n");
                writer.write("        <Name>Range</Name>\n");
                writer.write("        <DefaultBonus xsi:type=\"AddNumberBonus\">\n");
                writer.write("          <Value>" + me.getAuraRange() + "</Value>\n");
                writer.write("        </DefaultBonus>\n");
                writer.write("      </Range>\n");
                writer.write("      <Details>" + me.getAuraDescription() + "</Details>\n");
                writer.write("      <Name>" + me.getAura() + "</Name>\n");
                writer.write("      <Type>Trait</Type>\n");
                writer.write("      <IsBasic>false</IsBasic>\n");
                // Keywords aren't parsed by Masterplan for auras, ignore for now
//                writer.write("      <Keywords>\n");
//                writer.write("        <ObjectReference>\n");
//                writer.write("          <ReferencedObject>\n");
//                writer.write("            <Name>" + (me.getAuraKeywords() != null ? me.getAuraKeywords() : "") + "</Name>\n");
//                writer.write("          </ReferencedObject>\n");
//                writer.write("        </ObjectReference>\n");
//                writer.write("      </Keywords>\n");
                writer.write("      <Tier>0</Tier>\n");
                writer.write("    </MonsterTrait>\n");
            }
            // loop through other powers
            for (Power p : me.getPowers())
            {
                if (p.getActionType().isEmpty() || p.getActionType().toLowerCase().contains("trait"))
                {
                    // if no action type, or contains the word trait, it is a Trait
                    writer.write("    <MonsterTrait xsi:type=\"MonsterTrait\">\n");
                    writer.write("      <Range FinalValue=\"0\">\n");
                    writer.write("        <Name>Range</Name>\n");
                    writer.write("        <DefaultBonus xsi:type=\"AddNumberBonus\">\n");
                    writer.write("          <Value>0</Value>\n");
                    writer.write("        </DefaultBonus>\n");
                    writer.write("      </Range>\n");
                    writer.write("      <Details>" + p.getDetail() + "</Details>\n");
                    writer.write("      <Name>" + p.getName() + "</Name>\n");
                    writer.write("      <Type>Trait</Type>\n");
                    writer.write("      <IsBasic>false</IsBasic>\n");
                    //writer.write("      <IsBasic>" + (p.getIsBasicAtk() ? "true" : "false") + "</IsBasic>\n");
                    writer.write("      <Keywords>\n");
                    for (String key : p.getKeywords().split(", "))
                    {
                        writer.write("        <ObjectReference>\n");
                        writer.write("          <ReferencedObject>\n");
                        writer.write("            <Name>" + key + "</Name>\n");
                        writer.write("          </ReferencedObject>\n");
                        writer.write("        </ObjectReference>\n");
                    }
                    writer.write("      </Keywords>\n");
                    writer.write("      <Tier>0</Tier>\n");
                    writer.write("    </MonsterTrait>\n");
                }
                else
                {
                    // otherwise, a normal power
                    // Notes: Masterplan does a pretty crappy job of parsing powers.  Adventure Tools has a <FailedSavingThrows> tag for obvious uses,
                    // but Masterplan ignores it.  It does read and parse the <Hit>, <Miss>, <Effect> tags though...but all it does is append the
                    // <Damage> and <Description> of each section along with whatever <Name> you give in front of it all shoved into the Details tab.
                    // For my uses I'll drop <FailedSavingThrows>, and I'm going to just put everything into <Hit> tag under <Description> for now.
                    // By not parsing the damage out into <Damage> tags I get an extra space after the text from the <Name> tag but I can manually
                    // edit that around since I have to probably touch every power (especially auras) on these after creation just to be sure it worked.
                    // Masterplan doesn't seem to handle the new conditionals from MM3 files either, so I can skip that too and I'll just have to type
                    // it in by hand.
                    writer.write("    <MonsterPower xsi:type=\"MonsterPower\">\n");
                    writer.write("      <Action>" + p.getActionType() + "</Action>\n");
                    writer.write("      <Usage>" + p.getUsage() + "</Usage>\n");
                    writer.write("      <Attacks>\n");
                    writer.write("        <MonsterAttack>\n");
                    if (!p.getRange().trim().isEmpty())
                        writer.write("          <Range>" + p.getRange().trim() + "</Range>\n");
                    // If there is no target defense print an Effect node with a blank AttackBonuses node
                    if (p.getAtkDefense().isEmpty())
                    {
                        writer.write("          <Effect>\n");
                        writer.write("            <Name>Effect</Name>\n");
                        writer.write("            <Description>" + p.getDetail().trim() + "</Description>\n");
                        writer.write("          </Effect>\n");
                        writer.write("          <AttackBonuses />\n");
                    }
                    else
                    {
                        writer.write("          <Hit>\n");
                        writer.write("            <Name>Hit</Name>\n");
                        writer.write("            <Description>" + p.getDetail().trim() + "</Description>\n");
                        writer.write("          </Hit>\n");
                        writer.write("          <AttackBonuses>\n");
                        writer.write("            <MonsterPowerAttackNumber FinalValue=\"" + p.getAtkBonus() + "\">\n");
                        writer.write("              <Defense>\n");
                        writer.write("                <ReferencedObject>\n");
                        // Have to write Will instead of Willpower for Masterplan parsing
                        writer.write("                  <DefenseName>" + (p.getAtkDefense().startsWith("Will") ? "Will" : p.getAtkDefense()) + "</DefenseName>\n");
                        writer.write("                </ReferencedObject>\n");
                        writer.write("              </Defense>\n");
                        writer.write("            </MonsterPowerAttackNumber>\n");
                        writer.write("          </AttackBonuses>\n");
                    }
                    writer.write("        </MonsterAttack>\n");
                    writer.write("      </Attacks>\n");
                    writer.write("      <Name>" + p.getName() + "</Name>\n");
                    writer.write("      <IsBasic>" + (p.getIsBasicAtk() ? "true" : "false") + "</IsBasic>\n");
                    writer.write("      <Keywords>\n");
                    for (String key : p.getKeywords().split(", "))
                    {
                        writer.write("        <ObjectReference>\n");
                        writer.write("          <ReferencedObject>\n");
                        writer.write("            <Name>" + key + "</Name>\n");
                        writer.write("          </ReferencedObject>\n");
                        writer.write("        </ObjectReference>\n");
                    }
                    writer.write("      </Keywords>\n");
                    writer.write("    </MonsterPower>\n");
//                    <MonsterPower xsi:type="MonsterPower">
//                      <Action>Standard</Action>
//                      <Usage>At-Will</Usage>
//                      <Attacks>
//                        <MonsterAttack>
//                          <Hit>
//                            <Name>Hit</Name>
//                            <Description>damage</Description>
//                          </Hit>
//                          <AttackBonuses>
//                            <MonsterPowerAttackNumber FinalValue="7">
//                              <Defense>
//                                <ReferencedObject>
//                                  <DefenseName>AC</DefenseName>
//                                </ReferencedObject>
//                              </Defense>
//                            </MonsterPowerAttackNumber>
//                          </AttackBonuses>
//                        </MonsterAttack>
//                      </Attacks>
//                      <Name>Longsword</Name>
//                      <IsBasic>true</IsBasic>
//                      <Keywords>
//                        <ObjectReference>
//                          <ReferencedObject>
//                            <Name>Weapon</Name>
//                          </ReferencedObject>
//                        </ObjectReference>
//                        <ObjectReference>
//                          <ReferencedObject>
//                            <Name>Iamhuge</Name>
//                          </ReferencedObject>
//                        </ObjectReference>
//                      </Keywords>
//                    </MonsterPower>
                }
            }
            writer.write("  </Powers>\n");
            // Initiative
            writer.write("  <Initiative xsi:type=\"SimpleAdjustableNumber\" FinalValue=\"" + me.getInitiative() + "\">\n");
            writer.write("    <Name>Initiative</Name>\n");
            writer.write("  </Initiative>\n");
            // Hitpoints
            writer.write("  <HitPoints xsi:type=\"SimpleAdjustableNumber\" FinalValue=\""+ me.getHP() + "\">\n");
            writer.write("    <Name>HitPoints</Name>\n");
            writer.write("  </HitPoints>\n");
            // Action Points - skipped, hard coded based on GroupRole
            // Land Speed
            // Speed is stored in 1 long string, need to split it up
            String[] landSpeed = me.getSpeed().split(", ",2);
            writer.write("  <LandSpeed>\n");
            writer.write("    <ReferencedObject>\n");
            writer.write("      <Name>Land</Name>\n");
            writer.write("    </ReferencedObject>\n");
            writer.write("    <Speed FinalValue=\"" + landSpeed[0] + "\">\n");
            writer.write("      <Name>Speed</Name>\n");
            writer.write("    </Speed>\n");
            writer.write("  </LandSpeed>\n");
            // Speeds (the rest of them)
            // Speeds have a <Details> block for adding things like (hover), but you can cheat and put it all in Name
            writer.write("  <Speeds>\n");
            if (landSpeed.length > 1)
            {
                int speedID = 0;
                for (String speed : landSpeed[1].split(", "))
                {
                    int quantity = 1;
                    Pattern pattern;
                    Matcher matcher;
                    pattern = Pattern.compile("\\D+(\\d+).*");
                    matcher = pattern.matcher(speed);
                    if (matcher.matches())
                        quantity = Integer.parseInt(matcher.group(1));
                    writer.write("    <CreatureSpeed id=\"SPEED" + speedID + "\">\n");
                    writer.write("      <ReferencedObject>\n");
                    writer.write("        <ID>LANG" + speedID + "</ID>\n");
                    writer.write("        <Name>" + speed + "</Name>\n");
                    writer.write("      </ReferencedObject>\n");
                    writer.write("      <Speed FinalValue=\"" + quantity + "\">\n");
                    writer.write("        <Name>Speed</Name>\n");
                    writer.write("      </Speed>\n");
                    writer.write("    </CreatureSpeed>\n");
                    speedID += 1;
                }
            }
            writer.write("  </Speeds>\n");
            // Saving Throws - skipped, they're hard coded based on GroupRole
            // Weaknesses/Vulns
            writer.write("  <Weaknesses>\n");
            for (String vulns : me.getVulnerable().split(", "))
            {
                // clean up the vuln name a bit
                vulns = vulns.replace(";", "");
                vulns = vulns.trim();
                if (vulns.length() == 0)
                    continue;
                String[] vulnSplit = vulns.split(" ");
                if (vulnSplit.length < 2)
                    continue;
                writer.write("    <CreatureSusceptibility>\n");
                writer.write("      <ReferencedObject>\n");
                writer.write("        <Name>" + vulnSplit[1].substring(0, 1).toUpperCase() + vulnSplit[1].substring(1) + "</Name>\n");
                writer.write("      </ReferencedObject>\n");
                writer.write("      <Amount FinalValue=\"" + vulnSplit[0] + "\">\n");
                writer.write("        <Name>Resistance</Name>\n");
                writer.write("      </Amount>\n");
                writer.write("    </CreatureSusceptibility>\n");
            }
            writer.write("  </Weaknesses>\n");
            // Immunities
            writer.write("  <Immunities>\n");
            for (String immunes : me.getImmune().split(", "))
            {
                // clean up the immunes name a bit
                immunes = immunes.replace(";", "");
                immunes = immunes.trim();
                if (immunes.length() == 0)
                    continue;
                writer.write("    <ObjectReference>\n");
                writer.write("      <ReferencedObject>\n");
                writer.write("        <Name>" + immunes.substring(0, 1).toUpperCase() + immunes.substring(1) + "</Name>\n");
                writer.write("      </ReferencedObject>\n");
                writer.write("    </ObjectReference>\n");
            }
            writer.write("  </Immunities>\n");
            // Resistances
            writer.write("  <Resistances>\n");
            // Sometimes the compendium swaps Resists with Immunes on accident, like the Atropal, token code handles that but I don't bother for Masterplan
            for (String resists : me.getResistances().split(", "))
            {
                // clean up the resist name a bit
                resists = resists.replace(";", "");
                resists = resists.trim();
                if (resists.length() == 0)
                    continue;
                String[] resistSplit = resists.split(" ");
                if (resistSplit.length < 2)
                    continue;
                writer.write("    <CreatureSusceptibility>\n");
                writer.write("      <ReferencedObject>\n");
                writer.write("        <Name>" + resistSplit[1].substring(0, 1).toUpperCase() + resistSplit[1].substring(1) + "</Name>\n");
                writer.write("      </ReferencedObject>\n");
                writer.write("      <Amount FinalValue=\"" + resistSplit[0] + "\">\n");
                writer.write("        <Name>Resistance</Name>\n");
                writer.write("      </Amount>\n");
                writer.write("    </CreatureSusceptibility>\n");
            }
            writer.write("  </Resistances>\n");
            // Sourcebook (useless?)
            writer.write("  <SourceBook />\n");
            // Level
            writer.write("  <Level>" + me.getLevel() + "</Level>\n");
            // Experience value
            writer.write("  <Experience FinalValue=\"" + me.getXp() + "\">\n");
            writer.write("    <Name>Experience</Name>\n");
            writer.write("  </Experience>\n");
            // Role
            // Need to do a bit of parsing on the Role property
            String useRole = "No Role";
            for (String s : me.getCombatRole().split(" "))
            {
                if (ArrayUtils.contains(TokenMaker.Roles, s.toLowerCase()))
                {
                    useRole = s;
                    break;
                }
            }
            writer.write("  <Role>\n");
            writer.write("    <ReferencedObject>\n");
            writer.write("      <Name>" + useRole + "</Name>\n");
            writer.write("    </ReferencedObject>\n");
            writer.write("  </Role>\n");
            // Name
            writer.write("  <Name>" + me.getName() + "</Name>\n");

            writer.write("</Monster>");
            writer.close();
        }
        catch (Exception e)
        {
            System.err.println("Error saving XML for token " + name + ": " + e);
        }
    }
}
