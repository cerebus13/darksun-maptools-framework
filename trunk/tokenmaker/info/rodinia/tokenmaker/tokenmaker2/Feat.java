package tokenmaker2;

//import java.util.ArrayList;

/**
 * Feat models D&D 4E feats. All I'm really doing here is keeping an HTML
 * representation of the feat on the token for ease of look up during the game.
 * 
 * @author Blakey - Summer 2010
 * 
 */
public class Feat
{
    // compendium info
    private int    id     = 0;
    private String html   = null;
    private String name   = "";
    private String detail = "";

    public Feat(int id)
    {
        this.id = id;
    }

    public String getHTML()
    {
        if (id == 0)
            return "";
        if (html == null)
        {
            FeatCompendiumEntry com = new FeatCompendiumEntry(this);
            html = com.getHTML();
        }
        return html;
    }

    public String toString()
    {
        return name;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getDetail()
    {
        return detail;
    }

    public void setDetail(String detail)
    {
        this.detail = detail;
    }

}
