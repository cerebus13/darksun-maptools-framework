package info.rodinia.tokenmaker;

/**
 * FeatCompendiumEntry models a feat entry from the wizards of the coast DDi Compendium
 * It deals with displaying the feat in a nice fashion.
 * 
 * @author Blakey, Summer 2010
 */
public class FeatCompendiumEntry extends CompendiumEntry {

    Feat myFeat;
    
    public FeatCompendiumEntry(Feat feat) {
	super("http://www.wizards.com/dndinsider/compendium/feat.aspx?id="
		+ feat.getId());
	myFeat = feat;
    }
}
