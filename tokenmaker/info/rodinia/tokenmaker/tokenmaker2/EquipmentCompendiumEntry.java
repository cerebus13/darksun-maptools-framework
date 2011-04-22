package tokenmaker2;

/**
 * EquipmentCompendiumEntry models a equipment entry from the wizards of the
 * coast DDi Compendium It deals with displaying the equipment in a nice
 * fashion.
 * 
 * @author Blakey, Summer 2010
 */
public class EquipmentCompendiumEntry extends CompendiumEntry
{

    Equipment myEquipment;

    public EquipmentCompendiumEntry(Equipment equipment)
    {
        super(equipment.getUrl());
        myEquipment = equipment;
    }

    @Override
    protected void formatHTML()
    {
        super.formatHTML(); // make sure we do the generic stuff

        // add full path to the images
        myHTML = myHTML.replace("<img src=\"images/", "<img src=\""
                + ((TokenMaker.isRemote) ? TokenMaker.imagePath_remote
                        : TokenMaker.imagePath_local));

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
