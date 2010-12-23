package info.rodinia.tokenmaker;

import java.util.ArrayList;

/**
 * Equipment models D&D 4E equipment.
 * All I'm really doing here is keeping an HTML representation of the equipment on the token for ease of look up during the game.
 * @author Blakey - Summer 2010
 *
 */
public class Equipment {
    // compendium info
    private int id = 0;
    private String html = null;
    private String name = "";
    private String detail = "";
    private String url = "";
    private boolean equipped = false;

    public Equipment(String name) {
	this.name = name;
    }

    public String getHTML() {
	if (url == "")
	    return "";
	if (html == null) {
	    EquipmentCompendiumEntry com = new EquipmentCompendiumEntry(this);
	    html = com.getHTML();
	}
	return html;
    }

    public boolean isEquipped() {
        return equipped;
    }

    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }

    public String toString() {
	return name;
    }

    public int getId() {
	return id;
    }

    public void setId(int id) {
	this.id = id;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
