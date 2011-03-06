package info.rodinia.tokenmaker;


/**
 * NPCCompendiumEntry models a Monster entry in the Wizards online compendium.
 * 
 * @author Blakey, Summer 2010.
 */
public class NPCCompendiumEntry extends CompendiumEntry {

    /**
     * Constructor for NPCCompendiumEntry.
     * This builds a NPCCompendiumEntry object from an NPC object, which
     * is assumed to already have an ID built into it.
     * This ID is used to look up the entry in the Compendium and
     * populate the object.
     * <p>
     * During the construction of the NPCCompendiumEntry the NPC object it was
     * passed is also updated when the HTML of the Compendium page is 
     * parsed.
     *   
     * @param nPC - the object containing the NPC to build.
     * 
     * @TODO - This isn't very neat at all.   The NPC passed in is effectively being 
     * populated behind the scenes which feels like bad design to me.
     * I need to redesign this interface.
     */
    public NPCCompendiumEntry(NPC nPC) {
	super("http://localhost/ddi/monster.php?id="
		+ nPC.getId());
    }
    
    @Override
    protected void formatHTML() {
	super.formatHTML();	// make sure we do the generic stuff

	// add some formatting codes
	myHTML = myHTML
		.replace(
			"class=\"monster\"",
			"style=\"font:aerial;font-size:1.09em;font-weight: bold;line-height:2;padding-left:15px;margin:0;color:white;background: #4e5c2e;\" class=\"monster\"");
	myHTML = myHTML
		.replace(
			"class=\"type\"",
			"style=\"font:aerial;display: block;position: relative;z-index: 99;top: -0.75em;height: 1em;font-weight: normal;font-size: 0.917em;\" class=\"type\"");
	myHTML = myHTML
		.replace(
			"class=\"level\"",
			"style=\"font:aerial;display: block;margin-top: 0;text-align: right;position:relative;top:-60px;;\" class=\"level\"");
	myHTML = myHTML
		.replace(
			"class=\"flavorIndent\"",
			"style=\"font:aerial;display: block;padding: 2px 15px 2px 30px;margin: 0;background: #d6d6c2;\" class=\"flavorIndent\"");
	myHTML = myHTML
		.replace(
			"class=\"flavor alt\"",
			"style=\"font:aerial;display: block;padding: 2px 15px;margin: 0;background: #c3c6ad;\" class=\"flavor alt\"");
	myHTML = myHTML
		.replace(
			"class=\"flavor\"",
			"style=\"font:aerial;display: block;padding: 2px 15px;margin: 0;background: #d6d6c2;\" class=\"flavor\"");

	
    }
}
