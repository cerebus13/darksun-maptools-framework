package info.rodinia.tokenmaker;

import info.rodinia.tokenmaker.TokenMaker.Attribute;
import info.rodinia.tokenmaker.TokenMaker.Skill;

import java.io.BufferedWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JLabel;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * PCToken models a PC MapTool token. It subclasses Token, which does most of
 * the work. PCtoken specialises by providing overrides to the methods that
 * write out power macros.
 * 
 * @author Blakey, Summer 2010
 * 
 */
public class PCToken extends Token {

    public PCToken(PC newPC) {
	super(newPC);
	setTokenType("PC");
	setPortrait(newPC.getPortrait());
	setName(me.getName());
    }
    
    /**
     * Build a HTML representation for this PC
     * based on the same thing as monsters have.
     */
    
}
