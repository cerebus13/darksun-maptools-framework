# Introduction #

A simple journal detailing the coding work I've gotten done on a given day.

### 11/04/2010 ###

  * Grabbed Tokenmaker code from the other googlecode page.
  * Installed Eclipse as a Java IDE and was able to wrestle the program into working on it.
  * Downloaded an SVN plugin for Eclipse and was finally able to integrate code checkin/out functionality, then checked in the Tokenmaker code so I could start making changes.
  * Figured out how to add Source to the search functionality with Tokenmaker, but discovered that Wizards uses a short integer (like 203) to represent your choice rather than the string of the book name.  Will have to go through and get all of the book numbers later but for now I'll just use the Dark Sun stuff and the 3 monster manuals.
  * Got some good progress with [r129](https://code.google.com/p/darksun-maptools-framework/source/detail?r=129)

### 07/12/2010 ###

  * Fixed CallHP to stop breaking properties.
  * Fixed Skill check macros.
  * Updated global initiative macros to speed up combat starts a bit.
  * Removed Luck points from properties.
  * Added some auto removal of Halos, and auto remove NPCs from init when they die.

### 06/05/2010 ###

  * Redid Vulns and Resists to work very similar to how Sam had done attack/defense modifiers.  Involved making 40+ new properties for each resist/vuln combination, but this method will make it a lot easier to use and should be speedier.
  * Updated onCampaignLoad for some typos from Sam.
  * Checked in updateState since it was missing.

### 05/30/2010 ###

  * Checked in ([r81](https://code.google.com/p/darksun-maptools-framework/source/detail?r=81)) with a few small macro changes and a lot of changes to the NPC property set.  Been working on my actual maps for the campaign and just had to make a few tweaks here and there to get things working.  Streamlined the NPC props a lot so creating monster tokens is quicker.

### 04/27/2010 ###

  * Checked in all of my code work from the Demo.  Might be some bugs present but I think it is better to get it all checked in so Sam can start more work.
  * Saved a new campaign file from this release, [r79](https://code.google.com/p/darksun-maptools-framework/source/detail?r=79).

### 04/24/2010 ###

  * Got Fighter and Wizard demo tokens made.
  * Created a new chain of execution for utility type powers that don't attack or heal.  SecondaryTargetScreen -> DoSecondary.
  * Fixed a stupid bug in setStateJson function that was leaking a switchToken statement everywhere.  Made me think that evalMacro had a bug in it, but apparently the bug is that if you switch token in a User-Defined function that doesn't have a new stack that it never goes out of scope and thus the switch stays forever.
  * Need to rethink how we are piggy backing Self targetting mods/vulns/resists/whatever.  Found a situation where you might use a power that targets nobody, but you still want the Self effects to fire, and nothing would happen.  Think I should just make a DoSelf function to grab all the Self junk from the various parameter Jsons and process it once by itself which should hopefully speed up execution a bit.
  * Take a quick look at the Mark code again to see if I can speed that up at all because it is a bit of a bear right now playing as a Fighter.

### 04/21/2010 ###

  * Did testing of my State handling junk in TargetScreen, and the Multi-target "detail" form for doing modifiers.  A bit of tweaking and it looks to be reasonably stable.
  * Played with the TortiseSVN merge tool a bit, but I'm either not very good at it or it is rather lacking.
  * Having trouble with Sam's code, get error: Argument index 2 out of range (max = 1) in function arg.  Happens immediately upon calling decodeModDefenseJson and I'm not sure why.
    * Nevermind figured it out.  Sam had decodeModDefenseJson and applyModDefenseJson defined in onCampaignLoad to point to their VulnResist counterparts still through a likely copy/paste error.  Not sure if his code ever worked with it done this way, will need to do more testing.

### 04/12/2010 ###

  * Drycoded all of the Self and Target state handling for stuff like Prone/Restrained inside of TargetScreen.  The only one I put into DoAttack was Weakened, because that just halves damage anyway.  This is all dry-coded and untested which I'll get to later.
  * Looking at how things work it would be better to do the Multi-target form inside of the targ loop in TargetScreen.  I could move the code for BonusTotal into the targ loop and just adjust from there based on what the result was from each subsequent Input.  I could even make this all up into another macro called TargetScreen2 or somesuch to avoid the growing CODE block in here.

### 04/11/2010 ###

  * Big check-in ([r59](https://code.google.com/p/darksun-maptools-framework/source/detail?r=59)) to cover all of the various crap for healing powers.  Modified CallAttack to use an Object Json Parameter to make power macros look a lot cleaner and use less empty junk.
  * Made a self-heal atkType so Second Wind would work, ([r60](https://code.google.com/p/darksun-maptools-framework/source/detail?r=60)).
  * Started mucking with Campaign properties to get Defense Mods working better.  Discovered that a dynamic property that sums up an array Json property to display a number actually lags the client a bit, so I won't be able to go that route.  Instead I'll make a DefMod list for each defense that is just an array of mods, and players will need to be smart enough not to apply a mod over and over again.  I think I'll make a few checkboxes for the Target Screen to allow a player to NOT apply DefMods.  Doesn't matter for States/Vulns/Resists since they don't stack the same as Defenses do.
  * Need to have DoAttack respect states on tokens.  Here is a short list:
    * Attacker:
      * Weakened : 1/2 damage
      * Restrained : -2 atk
      * Prone : -2 atk
      * Blinded : targets have full concealment (-5 atk) unless attack is Area in keywords
    * Defender:
      * Dazed : grant CA
      * Helpless : grant CA
      * Prone : +2 defenses vs. attacks with Ranged in keyword
      * Restrained : grant CA
      * Stunned : grant CA
      * Surprised : grant CA
      * Unconscious : same as helpless, and -5 all defenses
  * Had a good idea on how to get the multi-target form to work.  Since I call DoAttack for each target, I could have an Input box pop up for each one if the Player selects "Detailed Modifiers" from the main TargetScreen.  Have each detailed popup default to the values chosen from TargetScreen, but the player could tweak CA off or bump a MiscAtk value up for a target or 2.  Have each screen show the name (and picture?) of current target, and the power name just to remind them what they're doing.
  * Going along with the Defense mods work I've been doing I was thinking I should make an additional tooltip called like "Applications" that would show a table of "Target Name|Had blah applied to them".  Would let you know quick if a player accidentally double stacked a defmod, and gives a quick summary of everything else the power did besides the damage already being shown.  Probably place it after the damage table, and just have it be a word with a customTooltip showing the full table text to spare chat spam.

### 04/10/2010 ###

  * Added Swarm support to DoAttack2, and a Swarm property to tokens.  Had to create a whole separate wiki page dedicated to random comments on this framework because there are so many oddities you need to know to get it to work right.
  * Got Vulns/Resists working in DoAttack2.  Added a small bit to check for "All" in any of the 4 jsons related to these and to always apply the affect for it despite what Keywords may be on an attack.  Further testing before check-in.
  * Had to seperate keywords into keywords/damageTypes in CallAttack so I can parse through just the actual damage types when determining Resists.  Forgot that Resists have the goofy wording when you are going against multiple damage types: "Your resistance is ineffective against combined damage types unless you have resistance to each of the damage types, and then only the weakest of the resistances applies."
  * Spent a ton of time polishing the code in DoAttack2 to get Resists to work right.  Ran into a couple of retard Maptools issues:
    * The json.subset() function DOES NOT EXIST despite what the maptools wiki says.
    * Using lots of nested code with json lookups in if() statements doesn't work for dick, so had to use lots of temp variables to store function calls and crap that I shouldn't have to.
    * Using the Set-Notation functions for jsons (union,intersection,difference) is a pain in the ass, avoid it unless you have no other recourse.  In my case I was forced to either use this or do a damn for-loop parsing of paired jsons because json.subset() is missing.
  * Checked in code for Vuln/Resist mess, ([r52](https://code.google.com/p/darksun-maptools-framework/source/detail?r=52)).  Might test some more and make revisions if needed.
  * Milestone stuff done, and even made a small GM macro to give all the PCs a milestone without them having to click anything.  ([r55](https://code.google.com/p/darksun-maptools-framework/source/detail?r=55))
  * Started working on Healing Powers.  Lot of stuff to get done here as I need to basically clone the entire work flow for Damage Powers and strip out all the extra crap.  I'm thinking for the first iteration of the framework I won't bother allowing Heals to apply status/Vuln/Resist and crap to targets since it's pretty rare.  Also am not going to implement powers that basically call a DIFFERENT power depending on if you hit or miss (such as how Healing Strike heals a player if you hit a monster).  Future wishlist on that stuff.
  * Lots of work left to do on healing.  Going to replace Attack/Damage with TempHP/Heals I believe in the work flow, so lots of cloning to do.  Also going to try and make a few macros a bit more generic so I don't have 4 or 5 copies floating around when I could just have made 1 shared among them all.

### 04/07/2010 ###

  * Added Insubstantial to Campaign Props, and then implemented it inside DoAttack2.  Added comments for spots where I am going to deal with Resists/Vulns and even Swarms above Insubstantial...because it applies last.
  * Cleaned up a possible bug in DoAttack2.
  * Fixed DefineFunction calls for decodeVulnResistJson and applyVulnResistJson so they get a new variable scope.

### 04/06/2010 ###

  * Got the correct format for a nested Object/Array Json worked out and put into the wiki for the CallAttack macro comments.
  * Finally got Resists/Vulns to apply to target and self through a passed Json in a power.
  * With _application_ of Vulns/Resists worked out, now I need to work on getting DoAttack2 to respect them based on Keywords of power and Keywords of Resist/Vuln that are present.  Don't forget to handle Generic...
  * Had a Piercing (resist avoidance) property I wanted to implement for powers or as a property on a token, curious if I should bother with that now or just leave it on the wish list.
  * When working on DoAttack2 I should toss a property on tokens to indicate if they are Insubstantial and half damage.  Swarms also half damage from melee/ranged attacks, but have a variable Vuln to Area/Close so I'm not sure how to implement that quite yet.