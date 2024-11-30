## [Ether Hack v2.7](https://github.com/Quzile/Project-Zomboid-EtherHack/releases/tag/v2.7) - 2023-07-31
- Fixed a bug with the repair (It was written -1)
- Added the ability to switch the state of the Lua execution blocker (You need to turn it on before entering the server, otherwise you need to reconnect to the server)

## [Ether Hack v2.6](https://github.com/Quzile/Project-Zomboid-EtherHack/releases/tag/v2.6) - 2023-07-31
- Fixed a bug when pressing the `Repair vehicle` button 
- Fixed a bug with debugging mode when it was impossible to use object editing
- Fixed a bug with editing items when there were errors when working with items in the inventory
- Fixed `bad words` to prohibit compilation of files, as it turned out, some mods fell under this criterion, now compilation is prohibited if traces of Bikinitools, mentions of EtherHack or attempts to record logs are noticed.
- Fixed a bug with the inability to use the vehicle

## [Ether Hack v2.5](https://github.com/Quzile/Project-Zomboid-EtherHack/releases/tag/v2.5) - 2023-07-31
- Fixed some bugs

## [Ether Hack v2.4](https://github.com/Quzile/Project-Zomboid-EtherHack/releases/tag/v2.4) - 2023-07-31
- Fixed some APIs
- Fixed a bug with the display of the Player Editor panel
- Fixed auto-repair (The count of repairs is set to 0)
- Added the function of issuing ingredients to the selected recipe
- Added night vision function
- Added a feature to disable zombie attacks
- Added the function of quick execution of actions
- Added the function of centering the character in a movable minimap
- Added the ability to edit objects (open/closing windows and doors, installing fuel in generators, etc.)
- Added protection against detection of EtherHack by custom anti-cheats (Blocks execution of all files where there is a mention of EtherHack, writing logs or about in-game cheats)
- Added a config system (In order for the config to load at the start of the game, it needs to be named `startup`)

## [Ether Hack v2.3](https://github.com/Quzile/Project-Zomboid-EtherHack/releases/tag/v2.3) - 2023-07-28
- Fixed translator logic
- Added a movable minimap
- Added the function of increasing the viewing angle to 360 degrees
- Added the function of unlimited condition of the object in the hands
- Added the function of auto-repair items in the inventory
- Added the function of exploring all available crafting recipes (if the crafting panel was open, you need to reopen)
- Added cheat mechanics menu
- Added cheat medicine menu
- Added display of the distance to the target when drawing ESP lines
- Added build and farming cheat (they are logged by user anti-cheats)
- Added indication of the presence of user log systems (BikiniTools, etc.)
- Added teleportation function - RMB on the cheat map/minimap (works stably for short distances, so the limit is 100 units.)

*Note:*
*The presence of a custom log system does not necessarily indicate a ban on the use of cheats marked "Logged BikiniTools", it just says that
certain plugins have been noticed on the server that extend the functionality of event logging, but use these functions at your own risk. According to the test results, mainly
logging of cheat functions occurred from the BikiniTools side*

## [Ether Hack v2.2](https://github.com/Quzile/Project-Zomboid-EtherHack/releases/tag/v2.2) - 2023-07-27
- Changed the text color in `ESP`
- Changed the API method of creation items
- Added the function of displaying lines to vehicles and players
- Added stubs for languages (in all unavailable languages - English)

## [Ether Hack v2.1](https://github.com/Quzile/Project-Zomboid-EtherHack/releases/tag/v2.1) - 2023-07-26
- Fixed API and LUA initialization system
- Fixed and improved user interface
- Fixed event subscripting, you can now add multiple annotations to a single method
- Fixed many APIs rewritten in Java
- Renamed the lua dependencies connection method - `requireExtra`.
- Item creation and character editing moved to separate tabs
- Added a custom system for loading images into Lua - `getExtraTexture`.
- Added ability to enable display of "zombie" and "transport" tags through walls
- Added a panel with minimap: displaying players, transport and zombies

*Note:
Functions on the minimap may not work as expected. This is caused by the engine, because on the client side only information is available at once.
client side, information is available only within one chunk (Map Cell) at a time. Outside of it
players (zombies and vehicles) will not be shown or will be "phantom".*

*Translation was done through a translator, so there may be inaccuracies.*

## [Ether Hack v2.0](https://github.com/Quzile/Project-Zomboid-EtherHack/releases/tag/v2.0) - 2023-07-15

- The main panel of the UI has been changed, now the functions are divided into categories
- Changed the subscription system for game lua events
- Removed unnecessary dependencies, which reduced the weight of the executable file
- Fixed the function of issuing character traits points, now it is not necessary to go to the main menu after death
- Fixed a bug that does not allow you to create your own server with a cheat
- Added more subtle character state management functions
- Added functions for displaying nicknames and information about players through walls
- Added features `unlimited ammo`
- Added support for custom translations
- Added two standard languages: `EN` and `RU`

## [Ether Hack v1.1](https://github.com/Quzile/Project-Zomboid-EtherHack/releases/tag/v1.1) - 2023-07-09

- Fixed API and GUI initialization
- Fixed feature `Set Max Default Skills`, now all available skills are set to the maximum level
- Removed `ghostmode` because its implementation duplicates `invisible`
- The `Set Max Default Skills` button has been moved to the `Player Editor` panel
- Added the ability to reload the Lua GUI with the `Home` key
- Added the `Unlimited Carry` feature
- Added the `MultiHit Zombie` feature
- Added the `Unlimited Endurance` feature
- Added the `Disable Fatigue` feature
- Added the `Disable Hunger` feature
- Added the `Disable Thirst` feature
- Added the `Disable Character Needs` feature
- added a button to open the admin panel
- Added the ability to load additional lua dependencies via the `EtherRequire` method
- Added Credits when installing the cheat
- Added logo display at game launch

## [Ether Hack v1.0](https://github.com/Quzile/Project-Zomboid-EtherHack/releases/tag/EtherHack-1.0) - 2023-07-05

- The logic of the version report has been changed
- Changed the system of injections into game files
- UI rendering logic changed - now via LUA
- Integrated support for custom Lua methods from Java
- Added new cheat functions
- Added the ability to use developer mode in multiplayer
