package info.rodinia.tokenmaker;

/**
 * EquipmentCompendiumEntry models a equipment entry from the wizards of the coast DDi Compendium
 * It deals with displaying the equipment in a nice fashion.
 * 
 * @author Blakey, Summer 2010
 */
public class EquipmentCompendiumEntry extends CompendiumEntry {

    Equipment myEquipment;
    
    public EquipmentCompendiumEntry(Equipment equipment) {
	super(equipment.getUrl());
	myEquipment = equipment;
    }
}
