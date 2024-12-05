<h1 align="center">Project-Zomboid-EtherHack</h1>
<img src="demo/EtherLogo.png" alt="EtherHack Logo">
<p align="center">
  <img src="https://img.shields.io/github/v/release/Yeet-Masta/Project-Zomboid-EtherHack" alt="GitHub release (latest by date)">
  <img src="https://img.shields.io/github/license/Yeet-Masta/Project-Zomboid-EtherHack" alt="GitHub">
  <img src="https://img.shields.io/github/commit-activity/t/Yeet-Masta/Project-Zomboid-EtherHack" alt="GitHub commit activity (branch)">
  <img src="https://img.shields.io/badge/Java-17-green" alt="Java 17">
  <img src="https://img.shields.io/github/issues/Yeet-Masta/Project-Zomboid-EtherHack" alt="GitHub issues">
</p>

## NOTICE!!!
There are repo's going around pretending to be EtherHack or just a PZ cheat (https://github.com/ayhantunay/Project-Zomboid-Cheat) and I HIGHLY ADVISE YOU DON"T DOWNLOAD THESE!!! They are asking you to download completely arbitrary DLL files and disable your fucking anti-virus!!! Please for the love of god, don't download this shit. Use your head!!!

This is a cheat written in Java(API) and LUA(GUI) for Project Zomboid. It is aimed at providing the game with additional functionality that allows users to get some benefits. Please use responsibly and understand the consequences that may arise as a result of improper use.
The performance of the cheat was tested on the latest version of the game `41.78.16 (Steam)` (July 09, 2023).

Edit: Finally after all this time! the project has been restored!!! I know the code quality is shit, but lest be honest it's better than nothing. And as a wise man once said "It Just Works" soooooooo. But, if any of you guys want to contribute, PLEASE! my dumb ass can't code that well and I really want this project to live on so any contrabutions would be helpful and apreaciated! Also, FUCK https://github.com/asledgehammer/EtherHammer !!! EAT SHIT!

Edit edit: wtf is [this???](https://imgur.com/a/rat1-v5T7l31)

Warning!!! Please check out [this github repo](https://github.com/Yeet-Masta/Project-Zomboid/tree/arkiease)

[Check this out!!!](https://www.unknowncheats.me/forum/other-mmorpg-and-strategy/645408-project-zomboid-cheat-valkur.html) This is freaking dope!!!

You can watch the cheat [installation video](https://www.youtube.com/watch?v=Olx7O3HyeZc) and see its main functions in action.

## Table of Contents
- [Cheat functionality](#cheat-functionality)
- [Demo](#demo)
- [Getting Started](#getting-started)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Uninstallation](#uninstallation)
- [Usage](#usage)
- [For developer](#for-developer)
- [Contributing](#contributing)
- [Disclaimer](#disclaimer)
- [License](#license)
- [Contact](#contact)

## Cheat functionality

| Function                   |  Working in multiplayer  | Working in a co-op  | Description                                                                                                                                                                                                                                                     |
|----------------------------|:------------------------:|:-------------------:|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Debug Mode Bypass          |          -/+(*)          |          +          | Allows you to use the developer mode in multiplayer (when starting the game with -debug connection is not possible). When you click the right mouse button, additional menus appear, including changing clothes, repairing cars, teleporting on the map, etc.   |
| MultiHit Zombie            |            +             |          +          | Enables multi-hit zombie mode                                                                                                                                                                                                                                   |
| Invisible                  |          -/+(*)          |          +          | Allows you to become invisible to everyone                                                                                                                                                                                                                      |
| God Mode                   |          -/+(*)          |          +          | Gives immortality to the character                                                                                                                                                                                                                              |
| No Clip                    |          -/+(*)          |          +          | Ability to pass through walls and objects                                                                                                                                                                                                                       |
| Unlimited Carry            |            +             |          +          | Enable infinite load capacity, including for third-party containers                                                                                                                                                                                             |
| Unlimited Endurance        |            +             |          +          | Enable unlimited endurance                                                                                                                                                                                                                                      |
| Disable Fatigue            |            +             |          +          | Disables the need for sleep                                                                                                                                                                                                                                     |
| Disable Hunger             |            +             |          +          | Disables the need for food                                                                                                                                                                                                                                      |
| Disable Thirst             |            +             |          +          | Disables the need for water                                                                                                                                                                                                                                     |
| Disable Character Needs    |            +             |          +          | Disables all the needs of the character, sets its characteristics to the maximum - positive - level (stress, panic, etc.)                                                                                                                                       |
| Add x100 Trait Point(beta) |         -/+ (**)         |          +          | Adds +100 points to the character creation menu                                                                                                                                                                                                                 |
| Game Debugger              |            +             |          +          | Opens the debugging window                                                                                                                                                                                                                                      |
| Items Creator              |            +             |          +          | The item creation menu, the ability to sort, search, etc.                                                                                                                                                                                                       |
| Player Editor              |            +             |          +          | The character editing menu, the ability to add skills, perks, etc.                                                                                                                                                                                              |
| Get Admin Access           |          -/+(*)          |          +          | Obtaining administrator rights on the server                                                                                                                                                                                                                    |
| Open Admin Menu            |          -/+(*)          |          +          | Opens the admin window                                                                                                                                                                                                                                          |

(*) - These functions work in multiplayer, provided that some types of anti-cheats are disabled, otherwise it will kick. As a rule, for servers with mods, some types are disabled, for example, [type 12](https://www.unknowncheats.me/forum/other-mmorpg-and-strategy/522818-project-zomboid-anti-cheat-types.html), but for full operation, [type 8](https://www.unknowncheats.me/forum/other-mmorpg-and-strategy/522818-project-zomboid-anti-cheat-types.html) is required to be disabled

(**) - It only works when creating a character from the main menu, that is, points will not be added to the menu after death. Solution: after death, log out of the server and connect again.


## Demo
![1](demo/1.jpg)
![2](demo/2.jpg)
![3](demo/3.jpg)
![4](demo/4.jpg)
![5](demo/5.jpg)
![6](demo/6.jpg)
![7](demo/7.jpg)
![8](demo/8.jpg)
![9](demo/9.jpg)

## Getting Started

This section will provide information on how to get a local copy of the project up and running.

### Prerequisites

This tool requires:

-   [Java 17](https://www.oracle.com/java/technologies/downloads/) or newer
-   Steam copy of [Project Zomboid](https://store.steampowered.com/app/108600/Project_Zomboid/)

### Installation

1. Download and install Java on your computer
2. Make sure that the path to Java is set in your environment variables

(For Windows: `WIN + X` -> `"System"` -> `"Advanced System Parameters"` -> `"Environment Variables"` -> `Double click on "Path"` -> `Insert path to java, for example "C:\Program Files\Java\jdk-20\bin"`)

3. Clone the repository
4. Open the project in the IDE and build the executable via Gradle `.jar` file
5. Move the created `.jar` to the root folder of the game 

(For example, `c:\Steam\steamapps\common\ProjectZomboid`)

6. Open the console in the root folder and run the following command: 

```
java -jar ./EtherHack-{yourVersion}.jar --install
```

`{yourVersion}` - Specify your version of the cheat

For example, for release 1.1, the command will look like this:
```
java -jar ./EtherHack-1.1.jar --install
```
### Uninstallation
Open the console in the root folder and run the following command:
```
java -jar ./EtherHack-{yourVersion}.jar --uninstall
```

`{yourVersion}` - Specify your version of the cheat

For example, for release 1.1, the command will look like this:
```
java -jar ./EtherHack-1.1.jar --uninstall
```

## Usage

After successfully installing the cheat, you need to log in to the game. When loading, the cheat logo will appear in front of the main logo, in the game itself (menu and in the game session) in the lower left corner there will be information about the cheat. The name of the game window will also change.

To open the cheat menu, press `Insert`

To reload the LuaGUI, press `Home`, but first make sure that all the cheat windows are closed, otherwise an error will appear.

## For developer
If you are a developer and want to expand the functionality, you can do it as follows:

In the project folder located on the path `src/main/resources/EtherHack/lua` there is a single file responsible for rendering the entire user interface of the cheat. You can edit it using examples from game files.

Also, Eterhack provides the ability to add custom methods from Java to lua, for this in the `src/main/java/EtherHack/Ether/EtherAPI.java` needs to find the `public static class GlobalEtherAPI` and add it according to the available examples:

```java
@LuaMethod(
         name = "yourMethodName",
        global = true
)
public static String yourMethodName() {
    return "Test!";
}
```
After adding and reassembling the cheat, you will be able to call this method in lua:

```lua
print(yourMethodName());
--Output log: Test!
```
In addition, you can load third-party Lua in any other Lua through the `EtherRequire` method:

```lua
EtherRequire "path/to/your.lua"
```

The path to Lua must be specified relative to the root folder of the game
## Contributing

We welcome contributions from the community. If you want to contribute, please fork the repository and create a pull request with your changes.

## Disclaimer

This software is provided 'as-is', without any express or implied warranty. In no event will the author be held liable for any damages arising from the use of this software. Use of this software may also violate the terms of service of the game and could lead to your account being banned. Use at your own risk.

## License

This project is under `MIT License` - see the LICENSE file for details.

## Contact

If you have any questions, feel free to reach out to me at `spaceguy234@gmail.com`
Also here's the unofficial official [discord](https://discord.gg/4CMAVqEey2) server
