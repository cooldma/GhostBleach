# GhostBleach

Community approved Minecraft utility mod for Fabric 1.19.4.

## Showcase
<details>
 <summary>Images</summary>

 ![](https://media.discordapp.net/attachments/1102329002087174165/1102331358765908018/image.png)

 ![](https://media.discordapp.net/attachments/1102329002087174165/1116373223966257274/image.png)

</details>

## Installation
### For normal people

Go to releases page, download the jar, put it into your minecraft mods folder, and run Fabric.
*Do NOT use this with optifabric or it will not work.*

### For developers

Download the branch with the version you want to work on.  
Start A Command Prompt/Terminal in the main folder.  
Generate the needed files for your preferred IDE.  

***IntelliJ (Recommended)***

  On Windows:
  > gradlew genIdeaWorkspace
  
  On Linux:
  > chmod +x ./gradlew  
  >./gradlew genIdeaWorkspace

  In idea click File > Open.
  Select build.gradle in the main folder.
  Select Open as Project.

***Eclipse***

  On Windows:
  > gradlew genSources eclipse
  
  On Linux:
  > chmod +x ./gradlew  
  >./gradlew genSources eclipse

  Start a new workspace in eclipse.
  Click File > Import... > Gradle > Gradle Project.
  Select the Main folder.

***Other IDE's***

  Use [this link](https://fabricmc.net/wiki/tutorial:setup) for more information.
  It should be pretty similar to the eclipse and idea setup.

## Recommended Mods

Here are some nice to have mods that are compatible with GhostBleach, none of these require Fabric API.

### [Multiconnect](https://github.com/Earthcomputer/multiconnect) or [ViaFabric](https://github.com/ViaVersion/ViaFabric)
This mod allows you to connect to any 1.8-1.19 server from a 1.19 client.

### [Sodium](https://www.curseforge.com/minecraft/mc-mods/sodium), [Lithium](https://www.curseforge.com/minecraft/mc-mods/lithium) and [Phosphor](https://www.curseforge.com/minecraft/mc-mods/phosphor)
These mods help optimise your game by making it smoother and run faster.

## License

If you are distributing a custom version of GhostBleach or a mod with ported features of GhostBleach, you are **required** to disclose the source code, state changes, use a compatible license, and follow the [license terms](https://github.com/BleachDev/BleachHack/blob/master/LICENSE).
