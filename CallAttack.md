# Introduction #

Because the comment system in Maptools actually just hides the execution of lines that are commented any macros that you write will actually run.  This forced me to create a page just to comment this larger macro.

# Details #

```
<!-- CallAttack macro -->

<!-- Process parameters -->

<!-- atkKey = Integer switch used to determine which attack/damage roll combination to use: -->
<!-- 1 = Single/Multi -->
<!-- 2 = Single/Multi No Damage -->
<!-- 3 = Single/Multi No Attack -->
<!-- 4 = AOE -->
<!-- 5 = AOE No Damage -->
<!-- 6 = AOE No Attack -->
<!-- 7 = Single/Multi heal -->
<!-- 8 = AOE heal -->
<!-- 20 = Utility -->
[h:atkKey         = json.get(macro.args, 0)]
[h:atkName        = json.get(macro.args, 1)]
[h:atkType        = json.get(macro.args, 2)] <!--  At-Will = 1, Encounter = 2, Daily = 3 -->
[h:atkTypeName    = json.get(macro.args, 3)]
[h:keywords       = json.get(macro.args, 4)]
[h:actionType     = json.get(macro.args, 5)]
[h:targetDefense  = json.get(macro.args, 6)]
[h:hitStatAdded   = json.get(macro.args, 7)]
[h:rangeText      = json.get(macro.args, 8)]
[h:numTargetsText = json.get(macro.args, 9)]
[h:atkMod         = json.get(macro.args, 10)]
[h:damMod         = json.get(macro.args, 11)]
[h:damRoll        = json.get(macro.args, 12)]
[h:critDamRoll    = json.get(macro.args, 13)]
[h,if(json.length(macro.args)>14): maxRange     = json.get(macro.args, 14);maxRange=-1]
[h,if(json.length(macro.args)>15): numTargets   = json.get(macro.args, 15);numTargets=0] <!-- only needed for Single or Multi-attack powers, AOE hits all in a range filtered only by PC/NPC depending -->
[h,if(json.length(macro.args)>16): effectText   = json.get(macro.args, 16);effectText=""]
[h,if(json.length(macro.args)>17): extraRoll    = json.get(macro.args, 17);extraRoll=""]
[h,if(json.length(macro.args)>18): useMagicItem = json.get(macro.args, 18);useMagicItem=0]
[h,if(json.length(macro.args)>19): friendlyFire = json.get(macro.args, 19);friendlyFire=0] <!-- 0 NPC, 1 PC, 2 Both -->
[h,if(json.length(macro.args)>20): otherSource  = json.get(macro.args, 20);otherSource=""] <!-- name of other token to use as source for figuring targets based on range -->
[h,if(json.length(macro.args)>21): missDamageRoll = json.get(macro.args, 21);missDamageRoll=-1] <!-- damage to roll on a miss, will not apply to minions. a -2 means do 1/2 of normal damRoll from previous -->
<!-- targetStateJson: Json with states to apply to target.  In the format of Key:Value, Key = a number 0-3, Value = json of State names -->
<!-- note: avoid using Marked states, they are covered below -->
<!-- Keys: 0 = apply state on miss, 1 = apply state on hit, 2 = apply state on crit, 3 = always apply state -->
[h,if(json.length(macro.args)>22): targetStateJson = json.get(macro.args, 22);targetStateJson="{}"] 
<!-- selfStateJson: Json with states to apply to self.  In the format of Key:Value, Key = a number 0-3, Value = json of State names-->
<!-- Keys: 0 = apply state on miss, 1 = apply state on hit, 2 = apply state on crit, 3 = always apply state -->
[h,if(json.length(macro.args)>23): selfStateJson = json.get(macro.args, 23);selfStateJson="{}"]
 <!-- Json array of when to mark target.  0 = miss, 1 = hit, 2 = crit, 3 = always -->
[h,if(json.length(macro.args)>24): markJson = json.get(macro.args,24);markJson="[]"]
<!-- Json Object of when to apply a resist/vuln to target/self.  0 = miss, 1 = hit, 2 = crit, 3 = always -->
<!-- Nested Json Object keys: 0 = Vuln to target, 1 = Vuln to self, 2 = Resist to target, 3 = Resist to self -->
<!-- Alternating array of element type, then value : sqBracket Cold,5,Fire,10,All,15 sqBracket -->
<!-- Example: {"0":{"0":["Cold",5],"1":["Cold",5]},"1":{"2":["Fire",5]},"2":{"3":["Fire",5,"Cold",5]},"3":{"0":["Fire",5],"2":["Fire",5]}} -->
<!-- Example: On miss applies Vuln Cold 5 to target and Vuln Cold 5 to self; On hit applies Resist Fire 5 to target; -->
<!--          On crit applies Resist Fire 5 and Resist Cold 5 to self; On always applies Vuln Fire 5 to target and   -->
<!--          Resist Fire 5 to target -->
[h,if(json.length(macro.args)>25): vulnResistJson = json.get(macro.args,25);vulnResistJson="{}"]
```