package tokenmaker2;

/**
 * PowerCompendiumEntry models a power entry from the wizards of the coast DDi
 * Compendium It deals with displaying the power in a nice fashion.
 * 
 * @author Blakey, Summer 2010
 */
public class PowerCompendiumEntry extends CompendiumEntry
{

    public static final String powerPHP_local  = "http://localhost/ddi/power.php?id=";
    public static final String powerPHP_remote = "http://www.wizards.com/dndinsider/compendium/power.aspx?id=";

    Power                      myPower;

    public PowerCompendiumEntry(Power power)
    {
        super(((TokenMaker.isRemote) ? powerPHP_remote : powerPHP_local)
                + power.getId());
        myPower = power;
        // stick a new line before the power name - it looks better.
        myHTML = myHTML.replace("</span>" + myPower.getName(), "</span><br/>"
                + myPower.getName());
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
                        "class=\"atwillpower\"",
                        "style=\"font:aerial;font-size:1.09em;font-weight: bold;line-height:2;padding-left:15px;margin:0;color:white;background: #619869;\" class=\"atwillpower\"");
        myHTML = myHTML
                .replace(
                        "class=\"encounterpower\"",
                        "style=\"font:aerial;font-size:1.09em;font-weight: bold;line-height:2;padding-left:15px;margin:0;color:white;background: #961334;\" class=\"encounterpower\"");
        myHTML = myHTML
                .replace(
                        "class=\"dailypower\"",
                        "style=\"font:aerial;font-size:1.09em;font-weight: bold;line-height:2;padding-left:15px;margin:0;color:white;background: #4d4d4f;\" class=\"dailypower\"");
        myHTML = myHTML
                .replace(
                        "class=\"utilitypower\"",
                        "style=\"font:aerial;font-size:1.09em;font-weight: bold;line-height:2;padding-left:15px;margin:0;color:white;background: #1c3d5f;\" class=\"utilitypower\"");

        myHTML = myHTML
                .replace(
                        "class=\"level\"",
                        "style=\"font:aerial;display: block;margin-top: 0;text-align: right;position:relative;top:-60px;;\" class=\"level\"");
        myHTML = myHTML
                .replace(
                        "class=\"flavor\"",
                        "style=\"font:aerial;display: block;padding: 2px 15px;margin: 0;background: #d6d6c2;\" class=\"flavor\"");
        myHTML = myHTML
                .replace(
                        "class=\"powerstat\"",
                        "style=\"font:aerial;padding: 0px 0px 0px 15px;margin: 0;background: #FFFFFF;\" class=\"powerstat\"");

    }
}
