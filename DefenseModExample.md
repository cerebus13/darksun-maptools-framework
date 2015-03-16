
```
<!-- Json Object of when to apply a Defense Modifier to target/self.  0 = miss, 1 = hit, 2 = crit, 3 = always -->
<!-- Nested Json Object keys: 0 = Target, 1 = Self -->
<!-- Array of Defense modifier Json object made up of 4 Key/Value pairs (modName, modValue, modDef [0=all,1=AC,2=Fort,3=Ref,4=Will], and modOwner) -->
<!-- Example: {"3":{"1":[{"modDef":1, "modName":"Priest's Shield", "modValue":1, "modOwner": "Udo"}]}} -->
<!-- Example: 3 = Perform Always, Nested 1 = To Self, apply Modifier Priest's Shield, as issued by Udo, to increase AC by 1 -->
```