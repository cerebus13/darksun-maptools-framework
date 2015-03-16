# Introduction #

Roadmap of plans for the Eclipse Phase framework.  Focus on keeping it a lot more lightweight than the Darksun work, and instead use it more as a reference and do most of the rolling and calculations by hand.

# Property Related #

  1. Create the basic Player property set to include the Eclipse Phase specific kinda stuff like Background, Faction, Morph, etc.
  1. Hardcode the Aptitude properties into Base, Morph Bonus, and Total (which is derived from the first 2).
  1. Implement the rest of the derived properties such as Trauma Threshold and so on.
  1. Split skills into 2 groupings: Active and Knowledge.  Store them dynamically most likely using 2 parallel Jsons at the same time.  A Json array that just stores the name (for sorting), and then a Json object that stores all pertinent info where it is keyed by the same data in the array.
  1. Rep stats hardcoded properties.
  1. Traits and sleights will be best as Json arrays.
  1. Muse properties will just be scaled down versions of the player properties.
  1. Weapon properties using a similar dual json parallel that skills are using.
  1. Get a good default setup for Skills since everyone should have a similar jumping off point.  Weapons/traits and so on can start empty.

# Functions #

  1. Make a generic 2d10 rolling UDF that takes in some variables and just does the typical Eclipse Phase roll 2d10 and try to get under a Target Number.  Have it handle Criticals, Success/Failure, MoS/MoF, Excellent and Severe.
  1. Campaign macro that creates a simple input for doing a manual 2d10 roll where you can type in Target Number, pick modifiers from a list of radios -30 to +30 or type in a specific modifier, and perhaps a bit of descriptive desc just to print to the screen.
  1. Figure out a good scheme for printing rolls so they look nice using better HTML and maybe some tooltip stuff.
  1. Implement initiative in 1 of 2 ways:
    * Sam's method of using Init Round mod 4 and checking if a character's speed > this result and then they get a turn.  Will need to make a custom NEXT INIT button for each player to use as a Campaign macro.  Has the risk of getting really messed up if your rounds get out of order when a player at the end clicks to fast, so perhaps not allow any Init movement unless your impersonated token is the current token?
    * Simpler approach is to just see if tokens can be added more than once to the init panel.  If yes then just create a function that adds all selected, sorts, then adds tokens again in respective Speed 2/3/4 sweeps after the main pack sorted by their init as needed.
  1. Make a semi-generic attack macro (perhaps campaign level?) that can run against either selected or impersonated macro to look for and use whatever weapon you have slotted into weap1/weap2/weap3/weap4.  Would make it easy to give GM a loadout of X weapons per token clickable by a function key, and players as well.  Plug into weapon properties to keep track of ammo which is probably the most important record keeping aspect of this.
    * Can do a little bit of automation by detecting if the weapon is a gun, and if so present radio buttons of the type of shot you are going to take to subtract the correct amount of ammo.  Not sure if it should also add the correct bonus to your attack roll or not, but it may not be able to since some rolls are multi-step such as: hit roll, dodge roll, damage roll, damage resist roll; and your Full Auto shot affects say roll #3.
  1. Aptitude roll function that includes the various flavors of them such as x2, x1.5 or whatever from the books.

# Lib tokens #

  1. Morph library.  Prioritize with core book first, then the splat books after in order of release.
  1. NPC library.  Housing all NPCs from all books for easy grabbing of stats.
  1. Weapon library.  Guns, melee, whatever.  Player and NPC tokens could then just store weapon name, ammo, and perhaps mods to the weapon and look up all static info from the lib token.
  1. Glossary library.  Store book text concerning Skill definitions, Traits, Psi Sleights, Weapons, etc...

# Char Sheet HTML #

  1. Aptitude header should have a macrolink to edit all Base Apititudes.  Avoiding the idea of adding modifiers into tokens at all..we will keep track of those on paper.
  1. Aptitude should display as: Aptitude name (macrolink to glossary), value (macrolink to aptitude check).
  1. Dynamically generate the table of skills into 2 or 3 columns sorted alphabetically in a vertical direction.
  1. Skill header should include a link to Add Skill, Delete Skill
  1. Display skills as Skill Name (macrolinked to the glossary entry for that skill), a superscript e (macro link to edit that skill), value (macrolinked to skill roll), defaulted Aptitude (macrolinked to an Aptitude roll).
  1. Damage & Stress should have their own section with macrolinks to add/remove/edit from each of the 4.
  1. Weapons can just be a quick list at first, and later add macrolinks to the glossary.  Have add/edit/remove links.
  1. Have an active weapon list that shows which weapon is in slot1 through X, and links to set them to gear you have or clear them.  This list will link into the global campaign attack macros.  Also display pertinant information about active weapons here such as Ammo (perhaps durability?), and links to reload or spend actions doing things to them.
  1. Traits list eventually macrolinked to glossary.
  1. Psi Sleights list eventually macrolinked to glossary.  Depending on complexity of Sleights perhaps can macrolink some of them to rolls, but we'll see.
  1. Morph section of sheet will be more or less static HTML to perhaps show lists of data.  Macrolinked to Morph glossary entries eventually.  Header can have links to change base morph type.
    * Might need a section of Morph based traits that come as presets, and then a modified section where players can add new ones or put in a mod that says remove such and such base morph trait.  Might be overthinking this and just pull in a static list of traits when guys swap morphs and let them edit the Traits they have back in.  Keep lots of token backups...