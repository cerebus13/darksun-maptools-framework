package info.rodinia.tokenmaker;

/**
 * RitualCompendiumEntry models a ritual entry from the wizards of the coast DDi Compendium
 * It deals with displaying the ritual in a nice fashion.
 * 
 * @author Blakey, Summer 2010
 */
public class RitualCompendiumEntry extends CompendiumEntry {

    Ritual myRitual;
    
    public RitualCompendiumEntry(Ritual ritual) {
	super("http://www.wizards.com/dndinsider/compendium/ritual.aspx?id="
		+ ritual.getId());
	myRitual = ritual;
    }
}
