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
	super("http://localhost/ddi/feat.php?id="
		+ feat.getId());
	myFeat = feat;
    }

    @Override
    protected void formatHTML() {
	super.formatHTML();	// make sure we do the generic stuff

	// add full path to the images
	myHTML = myHTML.replace("<img src=\"images/","<img src=\"http://localhost/ddi/images/");

	// add some formatting codes
	myHTML = myHTML
		.replace(
			"class=\"player\"",
			"style=\"font:aerial;font-size:1.35em;font-weight: bold;line-height:2;padding-left:15px;margin:0;color:white;background: #1d3d5e;\" class=\"player\"");

    }

}
