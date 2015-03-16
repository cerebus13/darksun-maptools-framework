# Introduction #

These are just a series of general comments concerning the framework.

### Resists/Vulnerabilities ###

  1. If a token has Swarm=1 property then when an attack goes to apply damage to them DoAttack2 checks to see if the attack has an "Area" keyword.  Any attack without an "Area" keyword is assumed to be Swarm resistable, so the damage is halved.  "Area" keyword attacks will deal additional damage to the Swarm equal to their SwarmVuln property which must be set according to what their monster entry says.

### Token Properties ###

  1. Values for Vulns/Resists should always be positive as the code in DoAttack2 determines whether to add or subtract it from the damage done.
  1. Never put a comma in the name of a token as it breaks calls to GetState() against that token.

### Parameters to CallAttack Macro ###

  1. The damageTypes parameter is just for keywords on a power that can be resisted/vuln'd against, such as Cold/Fire/Psychic.
  1. To indicate an area attack place "Area" in the keywords parameter, not the damageTypes parameters.
  1. Every attack in the game must have 1 of these 3 following words in their keywords parameter: Melee, Area, Ranged.