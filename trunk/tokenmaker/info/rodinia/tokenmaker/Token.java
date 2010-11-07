package info.rodinia.tokenmaker;

import info.rodinia.tokenmaker.TokenMaker.Attribute;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


import org.apache.commons.lang.StringEscapeUtils;

/**
 * Token models a MapTool token. It is a superclass which is specialised by
 * {@link NPCToken} and {@link PCToken}.
 * 
 * @author Blakey, Summer 2010
 * 
 */
public class Token {

    // map tool version we are making these tokens for.
    private static final String mapToolVersion = "1.3.b66";
    /**
     * this is my special macro which allows a token to display it's HTML
     * formatted character sheet inside MapTool
     */
    protected static final String characterSheet = "Character Sheet";

    // some predefined strings for token sizes
    protected static final String fineGUID = "fwABAc1lFSoBAAAAKgABAQ==";
    protected static final String diminuativeGUID = "fwABAc1lFSoCAAAAKgABAQ==";
    protected static final String tinyGUID = "fwABAc5lFSoDAAAAKgABAA==";
    protected static final String smallGUID = "fwABAc5lFSoEAAAAKgABAA==";
    protected static final String mediumGUID = "fwABAc9lFSoFAAAAKgABAQ==";
    protected static final String largeGUID = "fwABAdBlFSoGAAAAKgABAA==";
    protected static final String hugeGUID = "fwABAdBlFSoHAAAAKgABAA==";
    protected static final String gargantuanGUID = "fwABAdFlFSoIAAAAKgABAQ==";
    protected static final String collosalGUID = "fwABAeFlFSoJAAAAKgABAQ==";

    // our private data about the token. These are all just a bunch of defaults
    // generally.
    protected String name = null;
    private String tokenBaGUID = "wKgCA0jwi2MzAAAAQIgAAA==";
    private boolean beingImpersonated = false;
    private String md5Key = null;
    private int x = 0;
    private int y = 0;
    private int z = 0;
    private int anchorX = 0;
    private int anchorY = 0;
    private double sizeScale = 1.0;
    private int lastX = 0;
    private int lastY = 0;
    private boolean snapToScale = true;
    private int width = 290;
    private int height = 262;
    private double scaleX = 1.0;
    private double scaleY = 1.0;

    private boolean snapToGrid = true;
    private boolean isVisible = true;
    private int ownerType = 0;
    private String tokenShape = "SQUARE";
    private String tokenType = "PC";
    private String layer = "TOKEN";
    private String propertyType = "Basic";
    private boolean isFlippedX = false;
    private boolean isFlippedY = false;
    private boolean hasSight = true;

    // portrait info
    private File md5KeyFile;
    private File md5Portrait;
    private File myPortrait;

    // token file info
    private String tokenName = null;
    private File tokenFile;

    // underlying character
    protected Character me;
    
    public Token() {
	this("token"); // default name
    }

    public Token(String name) {
	this.tokenName = name;
    }


    public Token(Character me) {
	this.me = me;
    }
    
    
    /**
     * Allow a caller to set up the source portrait file for this token.
     * 
     * @param port
     *            - the file you want to use as this token's portrait file
     */
    public void setPortrait(File port) {
	try {
	    myPortrait = port;
	    InputStream in = new FileInputStream(myPortrait);
	    MD5Key md5 = new MD5Key(in);
	    md5Key = md5.toString();
	    md5KeyFile = new File(md5Key);
	    md5Portrait = new File(md5Key + ".png");

	} catch (Exception e) {
	    System.err.println("Error setting the portrait: " + e);
	}
    }

    public void setTokenType(String tokenType) {
	this.tokenType = tokenType;
    }

    public void setTokenFile(File file) {
	tokenFile = file;
    }

    // allow a caller to set up the actual token name
    public void setTokenName(String name) {
	tokenName = name;
    }

    // allow a caller to set up the name of the PC/NPC
    public void setName(String name) {
	this.name = name;
    }

    /**
     * Save this token off to disk. This method knows all about the internal
     * format of a MapTool token and it replicates it and saves the resulting
     * file to disk.
     */
    public void save() {
	if (tokenName == null || tokenFile == null)
	    return;
	makeTokenDir();
	saveAssets();
	saveContentXML();
	savePropertiesXML();
	createThumbnail();
	makeZip();
	deleteDir(new File(tokenName));
    }

    // recursively zip up a directory
    public void zipDir(String dir2zip, ZipOutputStream zos) {
	try {
	    // create a new File object based on the directory we have to zip
	    // File
	    File zipDir = new File(dir2zip);
	    // get a listing of the directory content
	    String[] dirList = zipDir.list();
	    byte[] readBuffer = new byte[2156];
	    int bytesIn = 0;
	    // loop through dirList, and zip the files
	    for (int i = 0; i < dirList.length; i++) {
		File f = new File(zipDir, dirList[i]);
		if (f.isDirectory()) {
		    // if the File object is a directory, call this
		    // function again to add its content recursively
		    String filePath = f.getPath();
		    zipDir(filePath, zos);
		    // loop again
		    continue;
		}
		// if we reached here, the File object f was not a directory
		// create a FileInputStream on top of f
		FileInputStream fis = new FileInputStream(f);
		// create a new zip entry
		String nameToZip = f.getPath();
		nameToZip = nameToZip.replace("\\", "/");
		int start = nameToZip.indexOf("/");
		nameToZip = nameToZip.substring(start + 1);
		ZipEntry anEntry = new ZipEntry(nameToZip);
		// place the zip entry in the ZipOutputStream object
		zos.putNextEntry(anEntry);
		// now write the content of the file to the ZipOutputStream
		while ((bytesIn = fis.read(readBuffer)) != -1) {
		    zos.write(readBuffer, 0, bytesIn);
		}
		// close the Stream
		fis.close();
		zos.closeEntry();
	    }
	} catch (Exception e) {
	    // handle exception
	}
    }

    private boolean deleteDir(File dir) {
	if (dir.isDirectory()) {
	    String[] children = dir.list();
	    for (int i = 0; i < children.length; i++) {
		boolean success = deleteDir(new File(dir, children[i]));
		if (!success) {
		    return false;
		}
	    }
	}

	// The directory is now empty so delete it
	return dir.delete();
    }

    private void makeZip() {
	try {
	    // create a ZipOutputStream to zip the data to
	    ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(
		    tokenFile));
	    // call the zipDir method
	    zipDir(tokenName, zos);
	    zos.close();
	} catch (Exception e) {
	    System.err.println("Error creating zip file: " + e);
	}
    }

    private void makeTokenDir() {
	try {
	    // Create the Token Directory
	    if (tokenName != null)
		new File(tokenName).mkdir();
	} catch (Exception e) {
	    System.err.println("Error making a directory for the token: " + e);
	}
    }

    private void copyPortrait() {
	try {
	    InputStream in = new FileInputStream(myPortrait);
	    OutputStream out = new FileOutputStream(tokenName + "/assets/"
		    + md5Portrait);
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
		out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	} catch (Exception e) {
	    System.err.println("Error copying portrait file: " + e);
	}
    }

    private void createThumbnail() {
	try {
	    InputStream in = new FileInputStream(myPortrait);
	    OutputStream out = new FileOutputStream(tokenName + "/thumbnail");
	    byte[] buf = new byte[1024];
	    int len;
	    while ((len = in.read(buf)) > 0) {
		out.write(buf, 0, len);
	    }
	    in.close();
	    out.close();
	} catch (Exception e) {
	    System.err.println("Error creating thumbnail: " + e);
	}
    }

    private void saveAssets() {
	try {
	    if (tokenName == null)
		return;

	    // Create the Assets Directory
	    new File(tokenName + "/assets").mkdir();

	    String picName = myPortrait.getName();
	    int end = picName.indexOf(".");
	    picName = picName.substring(0, end);
	    // Write out the asset XML file
	    FileWriter w = new FileWriter(tokenName + "/assets/" + md5KeyFile);
	    BufferedWriter writer = new BufferedWriter(w);
	    writer.write("<net.rptools.maptool.model.Asset>\n");
	    writer.write("  <id>\n");
	    writer.write("    <id>" + md5Key + "</id>\n");
	    writer.write("  </id>\n");
	    writer.write("  <name>" + picName + "</name>\n");
	    writer.write("  <extension>png</extension>\n");
	    writer.write("  <image/>\n");
	    writer.write("</net.rptools.maptool.model.Asset>");
	    writer.close();

	    // now write out the portrait file
	    copyPortrait();

	} catch (Exception e) {
	    System.err.println("Error saving assets for token " + name + ": "
		    + e);
	}

    }

    private void savePropertiesXML() {
	try {
	    File file = new File(tokenName + "/properties.xml");
	    FileWriter w = new FileWriter(file);
	    BufferedWriter writer = new BufferedWriter(w);
	    writer.write("<map>\n");
	    writer.write("  <entry>\n");
	    writer.write("    <string>version</string>\n");
	    writer.write("    <string>" + mapToolVersion + "</string>\n");
	    writer.write("  </entry>\n");
	    writer.write("</map>");
	    writer.close();
	} catch (Exception e) {
	    System.err.println("Error saving contents file for token " + name
		    + ": " + e);
	}
    }

    private void saveContentXML() {
	try {
	    File file = new File(tokenName + "/content.xml");
	    FileWriter w = new FileWriter(file);
	    BufferedWriter writer = new BufferedWriter(w);
	    writer.write("<net.rptools.maptool.model.Token>\n");
	    writer.write("  <id>\n");
	    writer.write("    <baGUID>" + tokenBaGUID + "</baGUID>\n");
	    writer.write("  </id>\n");
	    writer.write("  <beingImpersonated>" + beingImpersonated
		    + "</beingImpersonated>\n");
	    writer.write("  <imageAssetMap>\n");
	    writer.write("    <entry>\n");
	    writer.write("      <null/>\n");
	    writer.write("      <net.rptools.lib.MD5Key>\n");
	    writer.write("        <id>" + md5Key + "</id>\n");
	    writer.write("      </net.rptools.lib.MD5Key>\n");
	    writer.write("    </entry>\n");
	    writer.write("  </imageAssetMap>\n");
	    writer.write("  <x>" + x + "</x>\n");
	    writer.write("  <y>" + y + "</y>\n");
	    writer.write("  <z>" + z + "</z>\n");
	    writer.write("  <anchorX>" + anchorX + "</anchorX>\n");
	    writer.write("  <anchorY>" + anchorY + "</anchorY>\n");
	    writer.write("  <sizeScale>" + sizeScale + "</sizeScale>\n");
	    writer.write("  <lastX>" + lastX + "</lastX>\n");
	    writer.write("  <lastY>" + lastY + "</lastY>\n");
	    writer.write("  <snapToScale>" + snapToScale + "</snapToScale>\n");
	    writer.write("  <width>" + width + "</width>\n");
	    writer.write("  <height>" + height + "</height>\n");
	    writer.write("  <scaleX>" + scaleX + "</scaleX>\n");
	    writer.write("  <scaleY>" + scaleY + "</scaleY>\n");

	    // size output next
	    writeSizeMap(writer);

	    writer.write("  <snapToGrid>" + snapToGrid + "</snapToGrid>\n");
	    writer.write("  <isVisible>" + isVisible + "</isVisible>\n");
	    writer.write("  <name>" + name + "</name>\n");
	    writer.write("  <ownerType>" + ownerType + "</ownerType>\n");
	    writer.write("  <tokenShape>" + tokenShape + "</tokenShape>\n");
	    writer.write("  <tokenType>" + tokenType + "</tokenType>\n");
	    writer.write("  <layer>" + layer + "</layer>\n");
	    writer.write("  <propertyType>" + propertyType
		    + "</propertyType>\n");
	    writer.write("  <isFlippedX>" + isFlippedX + "</isFlippedX>\n");
	    writer.write("  <isFlippedY>" + isFlippedY + "</isFlippedY>\n");
	    writer.write("  <hasSight>" + hasSight + "</hasSight>\n");
	    writer.write("  <state/>\n");

	    writePropertyMap(writer);
	    writeMacros(writer);

	    writer.write("  <speechMap/>\n");
	    writer.write("</net.rptools.maptool.model.Token>");
	    writer.close();

	} catch (Exception e) {
	    System.err.println("Error saving contents file for token " + name
		    + ": " + e);
	}
    }

    protected void writeSizeMap(BufferedWriter writer) {
	try {
	    writer.write("  <sizeMap>\n");
	    writer.write("    <entry>\n");
	    writer
		    .write("      <java-class>net.rptools.maptool.model.SquareGrid</java-class>\n");
	    writer.write("      <net.rptools.maptool.model.GUID>\n");
	    writer.write("        <baGUID>");

	    if (me.getSize().equals("Fine"))
		writer.write(fineGUID);
	    if (me.getSize().equals("Diminuative"))
		writer.write(diminuativeGUID);
	    if (me.getSize().equals("Tiny"))
		writer.write(tinyGUID);
	    if (me.getSize().equals("Small"))
		writer.write(smallGUID);
	    if (me.getSize().equals("Medium"))
		writer.write(mediumGUID);
	    if (me.getSize().equals("Large"))
		writer.write(largeGUID);
	    if (me.getSize().equals("Huge"))
		writer.write(hugeGUID);
	    if (me.getSize().equals("Gargantuan"))
		writer.write(gargantuanGUID);
	    if (me.getSize().equals("Collosal"))
		writer.write(collosalGUID);

	    writer.write("</baGUID>\n");
	    writer.write("      </net.rptools.maptool.model.GUID>\n");
	    writer.write("    </entry>\n");
	    writer.write("  </sizeMap>\n");
	} catch (Exception e) {
	    System.err.println("Error writing size map: " + e);
	}
    }

    protected void writePropertyMap(BufferedWriter writer) {
	try {
	    writer.write("  <propertyMap>\n");
	    writer.write("    <store>\n");

	    // Level
	    writer.write("      <entry>\n");
	    writer.write("        <string>level</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>Level</key>\n");
	    writer.write("          <value class=\"string\">" + me.getLevel()
		    + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");

	    // SaveBonus
	    writer.write("      <entry>\n");
	    writer.write("        <string>savebonus</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>SaveBonus</key>\n");
	    writer.write("          <value class=\"string\">"
		    + me.getSavingThrows() + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");

	    // Initiative
	    writer.write("      <entry>\n");
	    writer.write("        <string>initiative</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>Initiative</key>\n");
	    writer.write("          <value class=\"string\">"
		    + me.getInitiative() + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");
	    writer.write("      <entry>\n");
	    writer.write("        <string>initrolls</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>InitRolls</key>\n");
	    writer.write("          <value class=\"string\">"
		    + me.getInitRolls() + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");

	    // APs
	    writer.write("      <entry>\n");
	    writer.write("        <string>aps</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>APs</key>\n");
	    writer.write("          <value class=\"string\">"
		    + me.getActionPoints() + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");

	    // Hit Points
	    writer.write("      <entry>\n");
	    writer.write("        <string>currenthp</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>CurrentHP</key>\n");
	    writer.write("          <value class=\"string\">" + me.getHP()
		    + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");
	    writer.write("      <entry>\n");
	    writer.write("        <string>maxhp</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>MaxHP</key>\n");
	    writer.write("          <value class=\"string\">" + me.getHP()
		    + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");

	    // Defences
	    writer.write("      <entry>\n");
	    writer.write("        <string>ac</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>AC</key>\n");
	    writer.write("          <value class=\"string\">" + me.getAC()
		    + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");
	    writer.write("      <entry>\n");
	    writer.write("        <string>fortitude</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>Fortitude</key>\n");
	    writer.write("          <value class=\"string\">"
		    + me.getFortitude() + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");
	    writer.write("      <entry>\n");
	    writer.write("        <string>reflex</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>Reflex</key>\n");
	    writer.write("          <value class=\"string\">" + me.getReflex()
		    + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");
	    writer.write("      <entry>\n");
	    writer.write("        <string>will</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>Will</key>\n");
	    writer.write("          <value class=\"string\">" + me.getWill()
		    + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");

	    // Attributes
	    writer.write("      <entry>\n");
	    writer.write("        <string>attributes</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>Attributes</key>\n");

	    writer.write("          <value class=\"string\">"
		    + me.getAttributesList() + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");

	    // Skills
	    writer.write("      <entry>\n");
	    writer.write("        <string>skills</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>Skills</key>\n");
	    writer.write("          <value class=\"string\">"
		    + me.getSkillsList() + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");

	    // Daily Items Usage
	    writer.write("      <entry>\n");
	    writer.write("        <string>dailyitems</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>DailyItems</key>\n");
	    writer.write("          <value class=\"string\">"
		    + me.getItemUsage() + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");

	    // Healing Surges
	    writer.write("      <entry>\n");
	    writer.write("        <string>maxsurges</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>MaxSurges</key>\n");
	    writer.write("          <value class=\"string\">"
		    + me.getHealingSurges() + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");
	    writer.write("      <entry>\n");
	    writer.write("        <string>currentsurges</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>CurrentSurges</key>\n");
	    writer.write("          <value class=\"string\">"
		    + me.getHealingSurges() + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");

	    // Healing Surge Value
	    writer.write("      <entry>\n");
	    writer.write("        <string>extrasurgevalue</string>\n");
	    writer
		    .write("        <net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("          <key>ExtraSurgeValue</key>\n");
	    writer.write("          <value class=\"string\">"
		    + me.getHealingSurgeValue() + "</value>\n");
	    writer
		    .write("          <outer-class reference=\"../../../..\"/>\n");
	    writer
		    .write("        </net.rptools.CaseInsensitiveHashMap_-KeyValue>\n");
	    writer.write("      </entry>\n");

	    writer.write("    </store>\n");
	    writer.write("  </propertyMap>\n");
	} catch (Exception e) {
	    System.err.println("Error writing property map: " + e);
	}
    }

    protected String buildHtml() {
	String html = "<div id=\"detail\">\n\n";
	html += "<h1 style=\"font:aerial;font-size:1.09em;font-weight: bold;line-height:2;padding-left:15px;margin:0;color:white;background: #4e5c2e;\" class=\"pc\">";
	html += "<span style=\"color:white\">[r:macroLink(getName(), \"endTurn@Lib:Blakey\", \"all\", \"\", currentToken())]</span><br/>\n";
	html += "<span style=\"font:aerial;display: block;position: relative;z-index: 99;top: -0.75em;height: 1em;font-weight: normal;font-size: 0.917em;\" class=\"race\">"
		+ me.getDescription() + "</span><br/>\n";
	html += "<span style=\"font:aerial;display: block;margin-top: 0;text-align: right;position:relative;top:-60px;;\" class=\"level\">Level "
		+ me.getLevel()
		+ " "
		+ me.getRole()
		+ "<span class=\"xp\"> XP "
		+ me.getXp()
		+ "</span></span> </h1>\n\n";
	html += "<p style=\"font:aerial;display: block;padding: 2px 15px;margin: 0;background: #d6d6c2;\" class=\"flavor\">";
	html += "<b>[r:macroLink(\"Initiative\", \"setMyInitiative@Lib:Blakey\", \"all\", \"\", currentToken())]</b> +"
		+ me.getInitiative()
		+ " &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>Senses</b> "
		+ me.getSenses() + "<br/>\n";

	if (me.getAura() != "") {
	    html += "<b>" + me.getAura() + "</b> " + me.getAuraDescription()
		    + "<br/>";
	}

	html += "<b>[r:macroLink(\"HP\", \"hpGain@Lib:Blakey\", \"all\", \"\", currentToken())]</b> [R: currentHP] / [R: maxHP]";

	html += "; <b>[r:macroLink(\"Bloodied\", \"hpLose@Lib:Blakey\", \"all\", \"\", currentToken())]</b> "
		+ (me.getHP() / 2);
	html += "; <b>[r:macroLink(\"Surges\", \"secondWind@Lib:Blakey\", \"all\", \"\", currentToken())]</b> [R: currentSurges] / [R:maxSurges]<br/>\n";

	if (me.getRegeneration() != "")
	    html += "<b>Regeneration</b> " + me.getRegeneration() + " <br/>\n";

	html += "<b>AC</b> " + me.getAC() + "; <b>Fortitude</b> "
		+ me.getFortitude() + ", <b>Reflex</b> " + me.getReflex()
		+ ", <b>Will</b> " + me.getWill() + "<br/>\n";

	if (me.getImmune() != "")
	    html += "<b>Immune</b> " + me.getImmune() + " ";
	if (me.getResist() != "")
	    html += "<b>Resist</b> " + me.getResist() + " ";
	if (me.getVulnerable() != "")
	    html += "<b>Vulnerable</b> " + me.getVulnerable() + " ";
	if (me.getImmune() != "" || me.getResist() != ""
		|| me.getVulnerable() != "")
	    html += "<br/>\n";

	html += "<b>[r:macroLink(\"Saving Throws\", \"rollSavingThrow@Lib:Blakey\", \"all\", \"\", currentToken())]</b> +"
		+ me.getSavingThrows() + "<br/>\n";
	html += "<b>Speed</b> " + me.getSpeed() + "<br/>\n";
	if (me.getActionPoints() != 0)
	    html += "<b>[r:macroLink(\"Action Points\", \"spendAP@Lib:Blakey\", \"all\", \"\", currentToken())]</b> [R: APs]<br/>\n";
	if (me.getItemUsage() != 0)
	    html += "<b>[r:macroLink(\"Dailies\", \"spendDailyItem@Lib:Blakey\", \"all\", \"\", currentToken())]</b> [R: DailyItems]</p>\n\n";

	// now the powers
	// do them in Usage order...
	for (Power p : me.getPowers()) {
	    if (p.getActionType().toLowerCase().contains("trait")) {
		html += buildPowerHtml(p) + "\n\n";
	    }
	}
	for (Power p : me.getPowers()) {
	    if (p.getActionType().toLowerCase().contains("triggered")) {
		html += buildPowerHtml(p) + "\n\n";
	    }
	}
	for (Power p : me.getPowers()) {
	    if (p.getUsage().toLowerCase().contains("at-will")) {
		html += buildPowerHtml(p) + "\n\n";
	    }
	}
	for (Power p : me.getPowers()) {
	    if (p.getUsage().toLowerCase().contains("recharge")) {
		html += buildPowerHtml(p) + "\n\n";
	    }
	}
	for (Power p : me.getPowers()) {
	    if (p.getUsage().toLowerCase().contains("encounter")) {
		html += buildPowerHtml(p) + "\n\n";
	    }
	}
	for (Power p : me.getPowers()) {
	    if (p.getUsage().toLowerCase().contains("daily")) {
		html += buildPowerHtml(p) + "\n\n";
	    }
	}

	html += "\n\n";
	html += "<p style=\"font:aerial;display: block;padding: 2px 15px;margin: 0;background: #c3c6ad;\" class=\"flavor alt\"><b>Alignment</b> "
		+ me.getAlignment()
		+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b> Languages</b> "
		+ me.getLanguages() + "<br/>\n";
	html += "<b>Skills</b> "
		+ me.getSkillsHtml() + "<br/>\n\n";

	int str = me.getAttributes().get(Attribute.Strength);
	int dex = me.getAttributes().get(Attribute.Dexterity);
	int con = me.getAttributes().get(Attribute.Constitution);
	int intel = me.getAttributes().get(Attribute.Intelligence);
	int wis = me.getAttributes().get(Attribute.Wisdom);
	int cha = me.getAttributes().get(Attribute.Charisma);

	html += "<b>[r:macroLink(\"Str\", \"rollAbilityCheck@Lib:Blakey\", \"all\", \"\", currentToken())]</b> "
		+ str
		+ " (+"
		+ (me.getLevel() / 2 + ((str - 10) / 2))
		+ ") &nbsp;&nbsp;&nbsp;&nbsp;&nbsp\n";
	html += "<b>Dex</b> " + dex + " (+"
		+ (me.getLevel() / 2 + ((dex - 10) / 2))
		+ ") &nbsp;&nbsp;&nbsp;&nbsp;&nbsp\n";
	html += "<b>Wis</b> " + wis + " (+"
		+ (me.getLevel() / 2 + ((wis - 10) / 2)) + ")<br/>\n";
	html += "<b>Con</b> " + con + " (+"
		+ (me.getLevel() / 2 + ((con - 10) / 2))
		+ ") &nbsp;&nbsp;&nbsp;&nbsp;&nbsp\n";
	html += "<b>Int</b> " + intel + " (+"
		+ (me.getLevel() / 2 + ((intel - 10) / 2))
		+ ") &nbsp;&nbsp;&nbsp;&nbsp;&nbsp\n";
	html += "<b>Cha</b> " + cha + " (+"
		+ (me.getLevel() / 2 + ((cha - 10) / 2)) + ")</p>\n\n";

	if (me instanceof PC) {
	    html += "<p style=\"font:aerial;display: block;padding: 2px 15px;margin: 0;background: #c3c6ad;\" class=\"flavor alt\">";
	    // List our Class Features, Feats, Rituals and Equipment
	    if (((PC) me).getClassFeatures().size() != 0) {
		html += "<b>Class Features</b> ";
		for (String s : ((PC) me).getClassFeatures()) {
		    html += s + ", ";
		}
		html += "<br/>\n";
	    }

	    // feats
	    if (((PC) me).getFeats().size() != 0) {
		html += "<b>Feats</b> ";
		int count = 0;
		for (Feat f : ((PC) me).getFeats()) {
		    if (count != 0)
			html += ", ";
		    count++;
		    String macroLink = "[r:macroLink(\"" + f.getName()
			    + "\", \"" + f.getName()
			    + "@TOKEN\", \"none\", \"\", currentToken())]";
		    html += macroLink;
		}
		html += "<br/>\n";
	    }

	    // rituals
	    if (((PC) me).getRituals().size() != 0) {
		html += "<b>Rituals</b> ";
		int count = 0;
		for (Ritual r : ((PC) me).getRituals()) {
		    if (count != 0)
			html += ", ";
		    count++;
		    String macroLink = "[r:macroLink(\"" + r.getName()
			    + "\", \"" + r.getName()
			    + "@TOKEN\", \"none\", \"\", currentToken())]";
		    html += macroLink;
		}
		html += "<br/>\n";
	    }

	    // gear
	    if (((PC) me).getEquipment().size() != 0) {
		html += "<b>Equipment</b> ";
		int count = 0;
		for (Equipment e : ((PC) me).getEquipment()) {
		    if (count != 0)
			html += ", ";
		    count++;
		    String macroLink = "[r:macroLink(\"" + e.getName()
			    + "\", \"" + e.getName()
			    + "@TOKEN\", \"none\", \"\", currentToken())]";
		    html += macroLink;
		}
		html += "<br/>\n";
	    }
	    html += "</p>\n\n";
	}
	// Special Section for Actions
	html += "<b>Actions to Perform</b><br/>\n";
	html += "[r:macroLink(\"Take a Rest\", \"chooseRestTask@Lib:Blakey\", \"all\", \"\", currentToken())]<br/>\n";
	html += "[r:macroLink(\"Reach a Milestone\", \"gainMilestone@Lib:Blakey\", \"all\", \"\", currentToken())]<br/>\n";
	html += "</p>\n\n";

	html += "</div>\n\n";

	// we have to convert the HTML to nice tokens that wont
	// phase XML it's inside.
	html = StringEscapeUtils.escapeHtml(html);
	// convert stuff that MapTool coughs over.
	html = html.replace("&ndash;", "-");
	html = html.replace("&mdash;", " - ");
	html = html.replace("&rsquo;", "");
	html = html.replace("'", "");

	return html;
    }

    protected String buildPowerHtml(Power p) {
	String html = null;
	String font = "background: #c3c6ad;"; // default
	String linkFont = "";

	if (p.getUsage().toLowerCase().contains("at-will")) {
	    font = "color:white;background: #619869;";
	    linkFont = "color:white";
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
	String macroLink = "<span style=\"" + linkFont + "\">[r:macroLink(\""
		+ p.getName() + "\", \"" + p.getName()
		+ "@TOKEN\", \"none\", \"\", currentToken())]</span>";

	if (p.getName().contains("Basic Attack"))
	    macroLink = p.getName(); // no link for basic attacks.

	html += macroLink + " (" + p.getActionType() + ", " + p.getUsage()
		+ ")";

	if (p.isInSpellbook()) {
	    html += " - in Spellbook";
	}
	
	html += "</p>";

	// get all the weapons equipped
	html += "<p style=\"font:aerial;display: block;padding: 2px 15px 2px 30px;margin: 0;background: #d6d6c2;\" class=\"flavorIndent\">";
	if (p.getWeapons().size() != 0) {
	    int count = 0;
	    String bonusHealing = "";
	    for (PowerWeapon pw : p.getWeapons()) {
		if (!pw.getName().equals("Unarmed")) {
		    if (count > 0)
			html += ", ";
		    count++;
		    html += buildPowerWeaponHtml(pw);
		}
		else {
		    	// see if there is any healing to remember
			if (pw.getHealing() != "") {
			    Pattern pattern = Pattern
				    .compile(".*regain an additional (\\d+) hit points.*");
			    Matcher matcher = pattern.matcher(pw.getHealing());
			    if (matcher.find()) {
				bonusHealing = matcher.group(1);
			    }
			}
		}
	    }

	    // if by this stage we haven't put out a weapon, then put out a
	    // "Use Power" option
	    if (count == 0) {
		
		html += "[r:macroLink(\"Use Power\", \"UsePower@Lib:Blakey\", \"all\", \"Power="
			+ p.getName().replace(" ", "+")
			+ ";Usage="
			+ p.getUsage().replace(" ", "+");
		
		if (!bonusHealing.equals("")) html += ";BonusHealing=" + bonusHealing; 
		html += "\", currentToken())];";
	    }

	} else {
	    html += "[r:macroLink(\"Use Power\", \"UsePower@Lib:Blakey\", \"all\", \"Power="
		    + p.getName().replace(" ", "+")
		    + ";Usage="
		    + p.getUsage().replace(" ", "+") + "\", currentToken())];";
	}
	

	return html;
    }

    protected String buildPowerWeaponHtml(PowerWeapon pw) {
	String criticalDamage = pw.getCriticalDamage();
	criticalDamage = criticalDamage.replace("+", "plus"); // needed to pass
	// plus signs as
	// macro arg.
	String criticalType = pw.getCriticalType();

	// Build up the macro link for this attack
	String html = "[r:macroLink(\"" + pw.getName()
		+ "\", \"MakeAttackRoll@Lib:Blakey\", \"all\", " + "\"Weapon="
		+ pw.getName() + ";Critical=" + criticalDamage
		+ ";CriticalType=" + criticalType + ";AttackBonus="
		+ pw.getAttackBonus() + ";Defence=" + pw.getDefence()
		+ ";Power=" + pw.getPower().getName().replace(" ", "+")
		+ ";Usage=" + pw.getPower().getUsage().replace(" ", "+");

	// Special Case for Sneak Attack.
	if (pw.getConditions() != "") {
	    Pattern pattern = Pattern
		    .compile(".*\\+(\\dd\\d) to damage once per round.*");
	    Matcher matcher = pattern.matcher(pw.getConditions());
	    if (matcher.find()) {
		String sneakAttack = matcher.group(1);
		html += ";SneakAttack=" + sneakAttack;
	    }
	}

	// Bolstering Blood
	if (me instanceof PC && ((PC) me).hasFeature("Bolstering Blood") && // we
		// have
		// Bolstering
		// Blood class feature
		!pw.getDamageString().equals("") && // the power does damage
		!pw.getPower().getUsage().toLowerCase().equals("at-will")) { // the
	    // power
	    // is
	    // not
	    // an
	    // at-will
	    // power
	    html += ";BolsteringBlood=true";
	}


	// Add the damage portion.
	if (!pw.getDamageString().equals("")) {
	    String damageConverted = pw.getDamageString().replace("+", "plus");
	    html += ";DamageDice=" + damageConverted + ";DamageType="
		    + pw.getDamageType();
	}
	html += "\", currentToken())]";

	/*
	 * - commented out this section to stop outputing to hit values and a
	 * new line. html += " (+" + pw.getAttackBonus() + " vs " +
	 * pw.getDefence(); if (!pw.getDamageString().equals("")) html += "; " +
	 * pw.getDamageString() + " " + pw.getDamageType() + " damage"; html +=
	 * ").<br/>";
	 */
	return html;

    }

    protected void writeMacros(BufferedWriter writer) {
	try {
	    // get the base HTML for the token
	    String html = buildHtml();

	    // holder method that writes nothing - should be overridden by
	    // subclass.
	    writer.write("  <macroPropertiesMap>\n");
	    int idx = 1;

	    // write all the power macros out
	    for (Power p : me.getPowers()) {
		writePowerMacro(writer, p, idx++);
	    }

	    // write all the feat, ritual and equipment macros out
	    if (me instanceof PC) {
		for (Feat f : ((PC) me).getFeats()) {
		    writeFeatMacro(writer, f, idx++);
		}
		for (Ritual r : ((PC) me).getRituals()) {
		    writeRitualMacro(writer, r, idx++);
		}
		for (Equipment e : ((PC) me).getEquipment()) {
		    writeEquipmentMacro(writer, e, idx++);
		}
	    }

	    // now create a 'Character Sheet' macro
	    Power displayStats = new Power(characterSheet);
	    String macro = "[frame(\"Character Sheet\"): {" + html + "}]";
	    displayStats.setDetail(macro);
	    writePowerMacro(writer, displayStats, idx++);

	    writer.write("  </macroPropertiesMap>\n");

	} catch (Exception e) {
	    System.err.println("Error writing power macros: " + e);
	}
    }

    protected String getPowerText(Power p) {
	return p.getHTML();
    }

    protected void writePowerMacro(BufferedWriter writer, Power p, int idx) {

	if (p.getName().equals("Melee Basic Attack")
		|| p.getName().equals("Ranged Basic Attack"))
	    return;

	String hotkey = "None";
	if (!p.getName().equals(characterSheet)) {
	    // get the HTML for the power from the compendium
	    String html = getPowerText(p);

	    // convert the HTML to nice tokens that wont phase XML it's inside.
	    html = StringEscapeUtils.escapeHtml(html);
	    // convert stuff that MapTool coughs over.
	    html = html.replace("&ndash;", "-");
	    html = html.replace("&mdash;", " - ");
	    html = html.replace("&bull;", " - ");
	    html = html.replace("&rsquo;", "");
	    html = html.replace("&ldquo;", "\"");
	    html = html.replace("&rdquo;", "\"");
	    html = html.replace("'", "");
	    html = html.replace("[W]", "W");

	    // finally build a frame for the html to live in.
	    String command = "[frame(\"Compendium\"): {" + html + "}]";
	    p.setDetail(command);
	} else {
	    hotkey = "F12";
	}

	// ditch quotes from name
	String powerName = p.getName().replace("'", "").replace("\"", "");

	String colorKey = "blue";
	String fontColorKey = "white";

	if (p.getUsage() != null
		&& p.getUsage().toLowerCase().contains("at-will")) {
	    colorKey = "green";
	    fontColorKey = "black";
	}
	if (p.getUsage() != null
		&& p.getUsage().toLowerCase().contains("encounter")) {
	    colorKey = "red";
	    fontColorKey = "white";
	}
	if (p.getUsage() != null
		&& p.getUsage().toLowerCase().contains("daily")) {
	    colorKey = "black";
	    fontColorKey = "white";
	}
	if (p.getUsage() != null
		&& p.getUsage().toLowerCase().contains("recharge")) {
	    colorKey = "magenta";
	    fontColorKey = "white";
	}

	try {
	    writer.write("    <entry>\n");
	    writer.write("      <int>" + idx + "</int>\n");
	    writer
		    .write("      <net.rptools.maptool.model.MacroButtonProperties>\n");
	    writer.write("        <saveLocation>Token</saveLocation>\n");
	    writer.write("        <index>" + idx + "</index>\n");
	    writer.write("        <colorKey>" + colorKey + "</colorKey>\n");
	    writer.write("        <hotKey>" + hotkey + "</hotKey>\n");
	    writer.write("        <command>" + p.getDetail() + "</command>\n");
	    writer.write("        <label>" + powerName + "</label>\n");
	    writer.write("        <group>" + p.getUsage() + "</group>\n");
	    writer.write("        <sortby>" + p.getLevel() + "</sortby>\n");
	    writer.write("        <autoExecute>true</autoExecute>\n");
	    writer.write("        <includeLabel>false</includeLabel>\n");
	    writer.write("        <applyToTokens>false</applyToTokens>\n");
	    writer.write("        <fontColorKey>" + fontColorKey
		    + "</fontColorKey>\n");
	    writer.write("        <fontSize>1.00em</fontSize>\n");
	    writer.write("        <minWidth>133</minWidth>\n");
	    writer.write("        <maxWidth></maxWidth>\n");
	    writer.write("        <allowPlayerEdits>true</allowPlayerEdits>\n");
	    writer.write("        <toolTip></toolTip>\n");
	    writer.write("        <commonMacro>false</commonMacro>\n");
	    writer.write("        <compareGroup>true</compareGroup>\n");
	    writer
		    .write("        <compareSortPrefix>true</compareSortPrefix>\n");
	    writer.write("        <compareCommand>true</compareCommand>\n");
	    writer
		    .write("        <compareIncludeLabel>true</compareIncludeLabel>\n");
	    writer
		    .write("        <compareAutoExecute>true</compareAutoExecute>\n");
	    writer
		    .write("        <compareApplyToSelectedTokens>true</compareApplyToSelectedTokens>\n");
	    writer
		    .write("      </net.rptools.maptool.model.MacroButtonProperties>\n");
	    writer.write("    </entry>\n");
	} catch (Exception e) {
	    System.err.println("Error writing power macros to content file: "
		    + e);
	}
    }

    protected String getFeatText(Feat f) {
	return f.getHTML();
    }

    protected void writeFeatMacro(BufferedWriter writer, Feat f, int idx) {
	// get the HTML for the feat from the compendium
	String html = getFeatText(f);

	// convert the HTML to nice tokens that wont phase XML it's inside.
	html = StringEscapeUtils.escapeHtml(html);
	// convert stuff that MapTool coughs over.
	html = html.replace("&ndash;", "-");
	html = html.replace("&mdash;", " - ");
	html = html.replace("&bull;", " - ");
	html = html.replace("&rsquo;", "");
	html = html.replace("&ldquo;", "\"");
	html = html.replace("&rdquo;", "\"");
	html = html.replace("'", "");
	html = html.replace("[W]", "W");

	// finally build a frame for the html to live in.
	String command = "[frame(\"Compendium\"): {" + html + "}]";
	f.setDetail(command);

	// ditch quotes from name
	String featName = f.getName().replace("'", "").replace("\"", "");

	try {
	    writer.write("    <entry>\n");
	    writer.write("      <int>" + idx + "</int>\n");
	    writer
		    .write("      <net.rptools.maptool.model.MacroButtonProperties>\n");
	    writer.write("        <saveLocation>Token</saveLocation>\n");
	    writer.write("        <index>" + idx + "</index>\n");
	    writer.write("        <colorKey>blue</colorKey>\n");
	    writer.write("        <hotKey>None</hotKey>\n");
	    writer.write("        <command>" + f.getDetail() + "</command>\n");
	    writer.write("        <label>" + featName + "</label>\n");
	    writer.write("        <group>Feats</group>\n");
	    writer.write("        <sortby></sortby>\n");
	    writer.write("        <autoExecute>true</autoExecute>\n");
	    writer.write("        <includeLabel>false</includeLabel>\n");
	    writer.write("        <applyToTokens>false</applyToTokens>\n");
	    writer.write("        <fontColorKey>white</fontColorKey>\n");
	    writer.write("        <fontSize>1.00em</fontSize>\n");
	    writer.write("        <minWidth>133</minWidth>\n");
	    writer.write("        <maxWidth></maxWidth>\n");
	    writer.write("        <allowPlayerEdits>true</allowPlayerEdits>\n");
	    writer.write("        <toolTip></toolTip>\n");
	    writer.write("        <commonMacro>false</commonMacro>\n");
	    writer.write("        <compareGroup>true</compareGroup>\n");
	    writer
		    .write("        <compareSortPrefix>true</compareSortPrefix>\n");
	    writer.write("        <compareCommand>true</compareCommand>\n");
	    writer
		    .write("        <compareIncludeLabel>true</compareIncludeLabel>\n");
	    writer
		    .write("        <compareAutoExecute>true</compareAutoExecute>\n");
	    writer
		    .write("        <compareApplyToSelectedTokens>true</compareApplyToSelectedTokens>\n");
	    writer
		    .write("      </net.rptools.maptool.model.MacroButtonProperties>\n");
	    writer.write("    </entry>\n");
	} catch (Exception e) {
	    System.err
		    .println("Error writing feat macro to content file: " + e);
	}
    }

    protected String getRitualText(Ritual r) {
	return r.getHTML();
    }

    protected void writeRitualMacro(BufferedWriter writer, Ritual r, int idx) {
	// get the HTML for the feat from the compendium
	String html = getRitualText(r);

	// convert the HTML to nice tokens that wont phase XML it's inside.
	html = StringEscapeUtils.escapeHtml(html);
	// convert stuff that MapTool coughs over.
	html = html.replace("&ndash;", "-");
	html = html.replace("&mdash;", " - ");
	html = html.replace("&bull;", " - ");
	html = html.replace("&rsquo;", "");
	html = html.replace("&ldquo;", "\"");
	html = html.replace("&rdquo;", "\"");
	html = html.replace("'", "");
	html = html.replace("[W]", "W");

	// finally build a frame for the html to live in.
	String command = "[frame(\"Compendium\"): {" + html + "}]";
	r.setDetail(command);

	// ditch quotes from name
	String ritualName = r.getName().replace("'", "").replace("\"", "");

	try {
	    writer.write("    <entry>\n");
	    writer.write("      <int>" + idx + "</int>\n");
	    writer
		    .write("      <net.rptools.maptool.model.MacroButtonProperties>\n");
	    writer.write("        <saveLocation>Token</saveLocation>\n");
	    writer.write("        <index>" + idx + "</index>\n");
	    writer.write("        <colorKey>blue</colorKey>\n");
	    writer.write("        <hotKey>None</hotKey>\n");
	    writer.write("        <command>" + r.getDetail() + "</command>\n");
	    writer.write("        <label>" + ritualName + "</label>\n");
	    writer.write("        <group>Rituals</group>\n");
	    writer.write("        <sortby></sortby>\n");
	    writer.write("        <autoExecute>true</autoExecute>\n");
	    writer.write("        <includeLabel>false</includeLabel>\n");
	    writer.write("        <applyToTokens>false</applyToTokens>\n");
	    writer.write("        <fontColorKey>white</fontColorKey>\n");
	    writer.write("        <fontSize>1.00em</fontSize>\n");
	    writer.write("        <minWidth>133</minWidth>\n");
	    writer.write("        <maxWidth></maxWidth>\n");
	    writer.write("        <allowPlayerEdits>true</allowPlayerEdits>\n");
	    writer.write("        <toolTip></toolTip>\n");
	    writer.write("        <commonMacro>false</commonMacro>\n");
	    writer.write("        <compareGroup>true</compareGroup>\n");
	    writer
		    .write("        <compareSortPrefix>true</compareSortPrefix>\n");
	    writer.write("        <compareCommand>true</compareCommand>\n");
	    writer
		    .write("        <compareIncludeLabel>true</compareIncludeLabel>\n");
	    writer
		    .write("        <compareAutoExecute>true</compareAutoExecute>\n");
	    writer
		    .write("        <compareApplyToSelectedTokens>true</compareApplyToSelectedTokens>\n");
	    writer
		    .write("      </net.rptools.maptool.model.MacroButtonProperties>\n");
	    writer.write("    </entry>\n");
	} catch (Exception e) {
	    System.err
		    .println("Error writing feat macro to content file: " + e);
	}
    }

    protected String getEquipmentText(Equipment e) {
	return e.getHTML();
    }

    protected void writeEquipmentMacro(BufferedWriter writer, Equipment e,
	    int idx) {
	// get the HTML for the feat from the compendium
	String html = getEquipmentText(e);

	// convert the HTML to nice tokens that wont phase XML it's inside.
	html = StringEscapeUtils.escapeHtml(html);
	// convert stuff that MapTool coughs over.
	html = html.replace("&ndash;", "-");
	html = html.replace("&mdash;", " - ");
	html = html.replace("&bull;", " - ");
	html = html.replace("&rsquo;", "");
	html = html.replace("&ldquo;", "\"");
	html = html.replace("&rdquo;", "\"");
	html = html.replace("'", "");
	html = html.replace("[W]", "W");

	// finally build a frame for the html to live in.
	String command = "[frame(\"Compendium\"): {" + html + "}]";
	e.setDetail(command);

	// ditch quotes from name
	String equipmentName = e.getName().replace("'", "").replace("\"", "");

	try {
	    writer.write("    <entry>\n");
	    writer.write("      <int>" + idx + "</int>\n");
	    writer
		    .write("      <net.rptools.maptool.model.MacroButtonProperties>\n");
	    writer.write("        <saveLocation>Token</saveLocation>\n");
	    writer.write("        <index>" + idx + "</index>\n");
	    writer.write("        <colorKey>blue</colorKey>\n");
	    writer.write("        <hotKey>None</hotKey>\n");
	    writer.write("        <command>" + e.getDetail() + "</command>\n");
	    writer.write("        <label>" + equipmentName + "</label>\n");
	    writer.write("        <group>Equipment</group>\n");
	    writer.write("        <sortby></sortby>\n");
	    writer.write("        <autoExecute>true</autoExecute>\n");
	    writer.write("        <includeLabel>false</includeLabel>\n");
	    writer.write("        <applyToTokens>false</applyToTokens>\n");
	    writer.write("        <fontColorKey>white</fontColorKey>\n");
	    writer.write("        <fontSize>1.00em</fontSize>\n");
	    writer.write("        <minWidth>133</minWidth>\n");
	    writer.write("        <maxWidth></maxWidth>\n");
	    writer.write("        <allowPlayerEdits>true</allowPlayerEdits>\n");
	    writer.write("        <toolTip></toolTip>\n");
	    writer.write("        <commonMacro>false</commonMacro>\n");
	    writer.write("        <compareGroup>true</compareGroup>\n");
	    writer
		    .write("        <compareSortPrefix>true</compareSortPrefix>\n");
	    writer.write("        <compareCommand>true</compareCommand>\n");
	    writer
		    .write("        <compareIncludeLabel>true</compareIncludeLabel>\n");
	    writer
		    .write("        <compareAutoExecute>true</compareAutoExecute>\n");
	    writer
		    .write("        <compareApplyToSelectedTokens>true</compareApplyToSelectedTokens>\n");
	    writer
		    .write("      </net.rptools.maptool.model.MacroButtonProperties>\n");
	    writer.write("    </entry>\n");
	} catch (Exception ex) {
	    System.err.println("Error writing feat macro to content file: "
		    + ex);
	}
    }
}