package EtherHack.Ether;

import EtherHack.utils.Logger;
import EtherHack.utils.PlayerUtils;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import se.krka.kahlua.integration.annotations.LuaMethod;
import se.krka.kahlua.vm.KahluaTable;
import zombie.Lua.LuaManager;
import zombie.characters.IsoPlayer;
import zombie.core.Color;
import zombie.core.network.ByteBufferWriter;
import zombie.core.textures.Texture;
import zombie.inventory.InventoryItem;
import zombie.network.GameClient;
import zombie.network.PacketTypes;
import zombie.network.ServerOptions;
import zombie.network.packets.PlayerPacket;
import zombie.scripting.ScriptManager;
import zombie.scripting.objects.Recipe;

public class EtherLuaMethods {
   private static EtherLuaMethods instance = null;
   private final SafeAPI safeAPI = SafeAPI.getInstance();
   private static final Map<String, Object> methodCache = new HashMap<>();
   @LuaMethod(
      name = "getZombieUIColor",
      global = true
   )
   public static Color getZombieUIColor() {
      return EtherMain.getInstance().etherAPI.zombiesUIColor;
   }

   @LuaMethod(
      name = "setZombieUIColor",
      global = true
   )
   public static void setZombieUIColor(float var0, float var1, float var2) {
      Color var3 = new Color(var0, var1, var2);
      EtherMain.getInstance().etherAPI.zombiesUIColor = var3;
   }

   @LuaMethod(
      name = "getVehicleUIColor",
      global = true
   )
   public static Color getVehicleUIColor() {
      return EtherMain.getInstance().etherAPI.vehiclesUIColor;
   }

   @LuaMethod(
      name = "setVehicleUIColor",
      global = true
   )
   public static void setVehicleUIColor(float var0, float var1, float var2) {
      Color var3 = new Color(var0, var1, var2);
      EtherMain.getInstance().etherAPI.vehiclesUIColor = var3;
   }

   @LuaMethod(
      name = "getPlayersUIColor",
      global = true
   )
   public static Color getPlayersUIColor() {
      return EtherMain.getInstance().etherAPI.playersUIColor;
   }

   @LuaMethod(
      name = "setPlayersUIColor",
      global = true
   )
   public static void setPlayersUIColor(float var0, float var1, float var2) {
      Color var3 = new Color(var0, var1, var2);
      EtherMain.getInstance().etherAPI.playersUIColor = var3;
   }

   @LuaMethod(
      name = "setAccentUIColor",
      global = true
   )
   public static void setAccentUIColor(float var0, float var1, float var2) {
      Color var3 = new Color(var0, var1, var2);
      EtherMain.getInstance().etherAPI.mainUIAccentColor = var3;
   }

   @LuaMethod(
      name = "deleteConfig",
      global = true
   )
   public static void deleteConfig(String var0) {
      Path var1 = Paths.get("EtherHack/config/" + var0 + ".properties");

      try {
         Files.deleteIfExists(var1);
      } catch (IOException var3) {
         Logger.printLog("The file '" + var0 + "' does not exist. Deletion canceled. Exception: " + var3.getMessage());
      }

   }

   @LuaMethod(
           name = "getConfigList",
           global = true
   )
   public static ArrayList<String> getConfigList() {
      ArrayList<String> configFiles = new ArrayList<>();

      try {
         Path configFolderPath = Paths.get("EtherHack/config");

         // Create directories if they don't exist
         if (!Files.exists(configFolderPath)) {
            Files.createDirectories(configFolderPath);
            return configFiles; // Return empty list since directory was just created
         }

         List<Path> fileList = Files.list(configFolderPath)
                 .filter(file -> file.toString().endsWith(".properties"))
                 .toList();

         for(Path filePath: fileList){
            String fileName = filePath.getFileName().toString().replace(".properties","");
            configFiles.add(fileName);
         }

         return configFiles;

      } catch (IOException e) {
         Logger.printLog("An error occurred while getting the list of config files: " + e);
         return null;
      }
   }

   @LuaMethod(
      name = "loadConfig",
      global = true
   )
   public static void loadConfig(String var0) {
      EtherMain.getInstance().etherAPI.loadConfig(var0);
   }

   @LuaMethod(
      name = "saveConfig",
      global = true
   )
   public static void saveConfig(String var0) {
      EtherMain.getInstance().etherAPI.saveConfig(var0);
   }

   @LuaMethod(name = "safePlayerTeleport", global = true)
   public static void safePlayerTeleport(int x, int y) {
      String key = SafeAPI.getInstance().generateVerificationKey();
      try {
         EtherMain.getInstance().etherAPI.isPlayerInSafeTeleported = true;
         IsoPlayer player = IsoPlayer.getInstance();

         float z = player.z;
         float dx = x - player.x;
         float dy = y - player.y;
         float dz = z - player.z;

         float absDx = Math.abs(dx);
         float absDy = Math.abs(dy);
         float absDz = Math.abs(dz);

         while (absDx > 0 || absDy > 0 || absDz > 0) {
            float step = 1.0f;
            float stepX = Math.min(Math.min(absDx, step), 1.0f);
            float stepY = Math.min(Math.min(absDy, step), 1.0f);
            float stepZ = Math.min(Math.min(absDz, step), 1.0f);

            absDx -= stepX;
            absDy -= stepY;
            absDz -= stepZ;

            if (dx < 0) stepX = -stepX;
            if (dy < 0) stepY = -stepY;
            if (dz < 0) stepZ = -stepZ;

            player.setX(player.x + stepX);
            player.setY(player.y + stepY);
            player.setZ(player.z + stepZ);
            player.setLx(player.getX());
            player.setLy(player.getY());
            player.setLz(player.getZ());

            GameClient.instance.sendPlayer(player);

            if (GameClient.connection != null &&
                    PlayerPacket.l_send.playerPacket.set(player)) {
               ByteBufferWriter writer = GameClient.connection.startPacket();
               PacketTypes.PacketType.PlayerUpdateReliable.doPacket(writer);
               PlayerPacket.l_send.playerPacket.write(writer);
               PacketTypes.PacketType.PlayerUpdateReliable.send(GameClient.connection);
            }
         }

         EtherMain.getInstance().etherAPI.isPlayerInSafeTeleported = false;
      } catch (Exception e) {
         Logger.printLog("Error in safePlayerTeleport: " + e.getMessage());
         EtherMain.getInstance().etherAPI.isPlayerInSafeTeleported = false;
      }
   }

   @LuaMethod(name = "isPlayerInSafeTeleported", global = true)
   public static boolean isPlayerInSafeTeleported() {
      return EtherMain.getInstance().etherAPI.isPlayerInSafeTeleported;
   }

   // Recipe and item manipulation
   @LuaMethod(name = "learnAllRecipes", global = true)
   public static void learnAllRecipes() {
      String key = SafeAPI.getInstance().generateVerificationKey();
      try {
         IsoPlayer player = IsoPlayer.getInstance();
         if (player != null) {
            ArrayList<Recipe> recipes = ScriptManager.instance.getAllRecipes();
            if (recipes != null) {
               for (Recipe recipe : recipes) {
                  if (recipe.getOriginalname() != null) {
                     player.learnRecipe(recipe.getOriginalname());
                  }
               }
            }
         }
      } catch (Exception e) {
         Logger.printLog("Error in learnAllRecipes: " + e.getMessage());
      }
   }

   @LuaMethod(name = "giveItem", global = true)
   public static void giveItem(InventoryItem item, int count) {
      String key = SafeAPI.getInstance().generateVerificationKey();
      try {
         IsoPlayer player = IsoPlayer.getInstance();
         if (player != null) {
            for (int i = 0; i < count; i++) {
               player.getInventory().AddItem(item);
            }
         }
      } catch (Exception e) {
         Logger.printLog("Error in giveItem: " + e.getMessage());
      }
   }

   @LuaMethod(
      name = "giveItem",
      global = true
   )
   public static void giveItem(String var0, int var1) {
      IsoPlayer var2 = IsoPlayer.getInstance();
      if (var2 != null) {
         for(int var3 = 0; var3 < var1; ++var3) {
            var2.getInventory().AddItem(var0);
         }
      }

   }

   @LuaMethod(
      name = "getDistanceBetweenPlayers",
      global = true
   )
   public static float getDistanceBetweenPlayers(IsoPlayer var0, IsoPlayer var1) {
      return PlayerUtils.getDistanceBetweenPlayers(var0, var1);
   }

   @LuaMethod(
      name = "isBlockCompileLuaWithBadWords",
      global = true
   )
   public static boolean isBlockCompileLuaWithBadWords() {
      return EtherLuaCompiler.getInstance().isBlockCompileLuaWithBadWords;
   }

   @LuaMethod(
      name = "toggleBlockCompileLuaWithBadWords",
      global = true
   )
   public static void toggleBlockCompileLuaWithBadWords(boolean var0) {
      EtherLuaCompiler.getInstance().isBlockCompileLuaWithBadWords = var0;
   }

   @LuaMethod(
      name = "isBlockCompileLuaAboutEtherHack",
      global = true
   )
   public static boolean isBlockCompileLuaAboutEtherHack() {
      return EtherLuaCompiler.getInstance().isBlockCompileLuaAboutEtherHack;
   }

   @LuaMethod(
      name = "toggleBlockCompileLuaAboutEtherHack",
      global = true
   )
   public static void toggleBlockCompileLuaAboutEtherHack(boolean var0) {
      EtherLuaCompiler.getInstance().isBlockCompileLuaAboutEtherHack = var0;
   }

   @LuaMethod(
      name = "isBlockCompileDefaultLua",
      global = true
   )
   public static boolean isBlockCompileDefaultLua() {
      return EtherLuaCompiler.getInstance().isBlockCompileDefaultLua;
   }

   @LuaMethod(
      name = "toggleBlockCompileDefaultLua",
      global = true
   )
   public static void toggleBlockCompileDefaultLua(boolean var0) {
      EtherLuaCompiler.getInstance().isBlockCompileDefaultLua = var0;
   }

   @LuaMethod(
      name = "isEnableInvisible",
      global = true
   )
   public static boolean isEnableInvisible() {
      return EtherMain.getInstance().etherAPI.isEnableInvisible;
   }

   @LuaMethod(
      name = "toggleInvisible",
      global = true
   )
   public static void toggleInvisible(boolean var0) {
      EtherMain.getInstance().etherAPI.isEnableInvisible = var0;
   }

   @LuaMethod(
      name = "isZombieDontAttack",
      global = true
   )
   public static boolean isZombieDontAttack() {
      return EtherMain.getInstance().etherAPI.isZombieDontAttack;
   }

   @LuaMethod(
      name = "toggleZombieDontAttack",
      global = true
   )
   public static void toggleZombieDontAttack(boolean var0) {
      EtherMain.getInstance().etherAPI.isZombieDontAttack = var0;
   }

   @LuaMethod(
      name = "isEnableNoclip",
      global = true
   )
   public static boolean isEnableNoclip() {
      return EtherMain.getInstance().etherAPI.isEnableNoclip;
   }

   @LuaMethod(
      name = "toggleNoclip",
      global = true
   )
   public static void toggleNoclip(boolean var0) {
      EtherMain.getInstance().etherAPI.isEnableNoclip = var0;
   }

   @LuaMethod(
      name = "isEnableGodMode",
      global = true
   )
   public static boolean isEnableGodMode() {
      return EtherMain.getInstance().etherAPI.isEnableGodMode;
   }

   @LuaMethod(
      name = "toggleGodMode",
      global = true
   )
   public static void toggleGodMode(boolean var0) {
      EtherMain.getInstance().etherAPI.isEnableGodMode = var0;
   }

   @LuaMethod(
      name = "isEnableNightVision",
      global = true
   )
   public static boolean isEnableNightVision() {
      return EtherMain.getInstance().etherAPI.isEnableNightVision;
   }

   @LuaMethod(
      name = "toggleNightVision",
      global = true
   )
   public static void toggleNightVision(boolean var0) {
      EtherMain.getInstance().etherAPI.isEnableNightVision = var0;
   }

   @LuaMethod(
      name = "isNoRecoil",
      global = true
   )
   public static boolean isNoRecoil() {
      return EtherMain.getInstance().etherAPI.isNoRecoil;
   }

   @LuaMethod(
      name = "toggleNoRecoil",
      global = true
   )
   public static void toggleNoRecoil(boolean var0) {
      EtherMain.getInstance().etherAPI.isNoRecoil = var0;
   }

   @LuaMethod(
      name = "isAutoRepairItems",
      global = true
   )
   public static boolean isAutoRepairItems() {
      return EtherMain.getInstance().etherAPI.isAutoRepairItems;
   }

   @LuaMethod(
      name = "toggleAutoRepairItems",
      global = true
   )
   public static void toggleAutoRepairItems(boolean var0) {
      EtherMain.getInstance().etherAPI.isAutoRepairItems = var0;
   }

   @LuaMethod(
      name = "resetWeaponsStats",
      global = true
   )
   public static void resetWeaponsStats() {
      EtherMain.getInstance().etherAPI.resetWeaponsStats();
   }

   @LuaMethod(
      name = "isExtraDamage",
      global = true
   )
   public static boolean isExtraDamage() {
      return EtherMain.getInstance().etherAPI.isExtraDamage;
   }

   @LuaMethod(
      name = "toggleExtraDamage",
      global = true
   )
   public static void toggleExtraDamage(boolean var0) {
      EtherMain.getInstance().etherAPI.isExtraDamage = var0;
   }

   @LuaMethod(
      name = "isTimedActionCheat",
      global = true
   )
   public static boolean isTimedActionCheat() {
      return EtherMain.getInstance().etherAPI.isTimedActionCheat;
   }

   @LuaMethod(
      name = "toggleTimedActionCheat",
      global = true
   )
   public static void toggleTimedActionCheat(boolean var0) {
      EtherMain.getInstance().etherAPI.isTimedActionCheat = var0;
   }

   @LuaMethod(
      name = "isMultiHitZombies",
      global = true
   )
   public static boolean isMultiHitZombies() {
      return EtherMain.getInstance().etherAPI.isMultiHitZombies;
   }

   @LuaMethod(
      name = "toggleMultiHitZombies",
      global = true
   )
   public static void toggleMultiHitZombies(boolean var0) {
      EtherMain.getInstance().etherAPI.isMultiHitZombies = var0;
   }

   @LuaMethod(
      name = "isUnlimitedCondition",
      global = true
   )
   public static boolean isUnlimitedCondition() {
      return EtherMain.getInstance().etherAPI.isUnlimitedCondition;
   }

   @LuaMethod(
      name = "toggleUnlimitedCondition",
      global = true
   )
   public static void toggleUnlimitedCondition(boolean var0) {
      EtherMain.getInstance().etherAPI.isUnlimitedCondition = var0;
   }

   @LuaMethod(
      name = "isVisualEnable360Vision",
      global = true
   )
   public static boolean isVisualEnable360Vision() {
      return EtherMain.getInstance().etherAPI.isVisualEnable360Vision;
   }

   @LuaMethod(
      name = "toggleVisualEnable360Vision",
      global = true
   )
   public static void toggleVisualEnable360Vision(boolean var0) {
      EtherMain.getInstance().etherAPI.isVisualEnable360Vision = var0;
   }

   @LuaMethod(
      name = "isVisualDrawLineToPlayers",
      global = true
   )
   public static boolean isVisualDrawLineToPlayers() {
      return EtherMain.getInstance().etherAPI.isVisualDrawLineToPlayers;
   }

   @LuaMethod(
      name = "toggleVisualDrawLineToPlayers",
      global = true
   )
   public static void toggleVisualDrawLineToPlayers(boolean var0) {
      EtherMain.getInstance().etherAPI.isVisualDrawLineToPlayers = var0;
   }

   @LuaMethod(
      name = "isVisualDrawLineToVehicle",
      global = true
   )
   public static boolean isVisualDrawLineToVehicle() {
      return EtherMain.getInstance().etherAPI.isVisualDrawLineToVehicle;
   }

   @LuaMethod(
      name = "toggleVisualDrawLineToVehicle",
      global = true
   )
   public static void toggleVisualDrawLineToVehicle(boolean var0) {
      EtherMain.getInstance().etherAPI.isVisualDrawLineToVehicle = var0;
   }

   @LuaMethod(
      name = "isMapDrawZombies",
      global = true
   )
   public static boolean isMapDrawZombies() {
      return EtherMain.getInstance().etherAPI.isMapDrawZombies;
   }

   @LuaMethod(
      name = "toggleMapDrawZombies",
      global = true
   )
   public static void toggleMapDrawZombies(boolean var0) {
      EtherMain.getInstance().etherAPI.isMapDrawZombies = var0;
   }

   @LuaMethod(
      name = "isMapDrawVehicles",
      global = true
   )
   public static boolean isMapDrawVehicles() {
      return EtherMain.getInstance().etherAPI.isMapDrawVehicles;
   }

   @LuaMethod(
      name = "toggleMapDrawVehicles",
      global = true
   )
   public static void toggleMapDrawVehicles(boolean var0) {
      EtherMain.getInstance().etherAPI.isMapDrawVehicles = var0;
   }

   @LuaMethod(
      name = "isMapDrawAllPlayers",
      global = true
   )
   public static boolean isMapDrawAllPlayers() {
      return EtherMain.getInstance().etherAPI.isMapDrawAllPlayers;
   }

   @LuaMethod(
      name = "toggleMapDrawAllPlayers",
      global = true
   )
   public static void toggleMapDrawAllPlayers(boolean var0) {
      EtherMain.getInstance().etherAPI.isMapDrawAllPlayers = var0;
   }

   @LuaMethod(
      name = "isMapDrawLocalPlayer",
      global = true
   )
   public static boolean isMapDrawLocalPlayer() {
      return EtherMain.getInstance().etherAPI.isMapDrawLocalPlayer;
   }

   @LuaMethod(
      name = "toggleMapDrawLocalPlayer",
      global = true
   )
   public static void toggleMapDrawLocalPlayer(boolean var0) {
      EtherMain.getInstance().etherAPI.isMapDrawLocalPlayer = var0;
   }

   @LuaMethod(
      name = "isVisualDrawPlayerInfo",
      global = true
   )
   public static boolean isVisualDrawPlayerInfo() {
      return EtherMain.getInstance().etherAPI.isVisualDrawPlayerInfo;
   }

   @LuaMethod(
      name = "toggleVisualDrawPlayerInfo",
      global = true
   )
   public static void toggleVisualDrawPlayerInfo(boolean var0) {
      EtherMain.getInstance().etherAPI.isVisualDrawPlayerInfo = var0;
   }

   @LuaMethod(
      name = "isVisualsZombiesEnable",
      global = true
   )
   public static boolean isVisualsZombiesEnable() {
      return EtherMain.getInstance().etherAPI.isVisualsZombiesEnable;
   }

   @LuaMethod(
      name = "toggleVisualsZombiesEnable",
      global = true
   )
   public static void toggleVisualsZombiesEnable(boolean var0) {
      EtherMain.getInstance().etherAPI.isVisualsZombiesEnable = var0;
   }

   @LuaMethod(
      name = "isVisualsVehiclesEnable",
      global = true
   )
   public static boolean isVisualsVehiclesEnable() {
      return EtherMain.getInstance().etherAPI.isVisualsVehiclesEnable;
   }

   @LuaMethod(
      name = "toggleVisualsVehiclesEnable",
      global = true
   )
   public static void toggleVisualsVehiclesEnable(boolean var0) {
      EtherMain.getInstance().etherAPI.isVisualsVehiclesEnable = var0;
   }

   @LuaMethod(
      name = "isVisualsPlayersEnable",
      global = true
   )
   public static boolean isVisualsPlayersEnable() {
      return EtherMain.getInstance().etherAPI.isVisualsPlayersEnable;
   }

   @LuaMethod(
      name = "toggleVisualsPlayersEnable",
      global = true
   )
   public static void toggleVisualsPlayersEnable(boolean var0) {
      EtherMain.getInstance().etherAPI.isVisualsPlayersEnable = var0;
   }

   @LuaMethod(
      name = "isVisualDrawCredits",
      global = true
   )
   public static boolean isVisualDrawCredits() {
      return EtherMain.getInstance().etherAPI.isVisualDrawCredits;
   }

   @LuaMethod(
      name = "toggleVisualDrawCredits",
      global = true
   )
   public static void toggleVisualDrawCredits(boolean var0) {
      EtherMain.getInstance().etherAPI.isVisualDrawCredits = var0;
   }

   @LuaMethod(
      name = "isVisualDrawPlayerNickname",
      global = true
   )
   public static boolean isVisualDrawPlayerNickname() {
      return EtherMain.getInstance().etherAPI.isVisualDrawPlayerNickname;
   }

   @LuaMethod(
      name = "toggleVisualDrawPlayerNickname",
      global = true
   )
   public static void toggleVisualDrawPlayerNickname(boolean var0) {
      EtherMain.getInstance().etherAPI.isVisualDrawPlayerNickname = var0;
   }

   @LuaMethod(
      name = "isVisualDrawToLocalPlayer",
      global = true
   )
   public static boolean isVisualDrawToLocalPlayer() {
      return EtherMain.getInstance().etherAPI.isVisualDrawToLocalPlayer;
   }

   @LuaMethod(
      name = "toggleVisualDrawToLocalPlayer",
      global = true
   )
   public static void toggleVisualDrawToLocalPlayer(boolean var0) {
      EtherMain.getInstance().etherAPI.isVisualDrawToLocalPlayer = var0;
   }

   @LuaMethod(
      name = "isVisualsEnable",
      global = true
   )
   public static boolean isVisualsEnable() {
      return EtherMain.getInstance().etherAPI.isVisualsEnable;
   }

   @LuaMethod(
      name = "toggleVisualsEnable",
      global = true
   )
   public static void toggleVisualsEnable(boolean var0) {
      EtherMain.getInstance().etherAPI.isVisualsEnable = var0;
   }

   @LuaMethod(
      name = "isBypassDebugMode",
      global = true
   )
   public static boolean isBypassDebugMode() {
      return EtherMain.getInstance().etherAPI.isBypassDebugMode;
   }

   @LuaMethod(
      name = "toggleBypassDebugMode",
      global = true
   )
   public static void toggleBypassDebugMode(boolean var0) {
      EtherMain.getInstance().etherAPI.isBypassDebugMode = var0;
   }

   @LuaMethod(
      name = "toggleUnlimitedEndurance",
      global = true
   )
   public static void toggleUnlimitedEndurance(boolean var0) {
      EtherMain.getInstance().etherAPI.isUnlimitedEndurance = var0;
   }

   @LuaMethod(
      name = "isUnlimitedEndurance",
      global = true
   )
   public static boolean isUnlimitedEndurance() {
      return EtherMain.getInstance().etherAPI.isUnlimitedEndurance;
   }

   @LuaMethod(
      name = "toggleUnlimitedAmmo",
      global = true
   )
   public static void toggleUnlimitedAmmo(boolean var0) {
      EtherMain.getInstance().etherAPI.isUnlimitedAmmo = var0;
   }

   @LuaMethod(
      name = "isUnlimitedAmmo",
      global = true
   )
   public static boolean isUnlimitedAmmo() {
      return EtherMain.getInstance().etherAPI.isUnlimitedAmmo;
   }

   @LuaMethod(
      name = "toggleDisableFatigue",
      global = true
   )
   public static void toggleDisableFatigue(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableFatigue = var0;
   }

   @LuaMethod(
      name = "isDisableFatigue",
      global = true
   )
   public static boolean isDisableFatigue() {
      return EtherMain.getInstance().etherAPI.isDisableFatigue;
   }

   @LuaMethod(
      name = "toggleDisableHunger",
      global = true
   )
   public static void toggleDisableHunger(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableHunger = var0;
   }

   @LuaMethod(
      name = "isDisableHunger",
      global = true
   )
   public static boolean isDisableHunger() {
      return EtherMain.getInstance().etherAPI.isDisableHunger;
   }

   @LuaMethod(
      name = "toggleDisableThirst",
      global = true
   )
   public static void toggleDisableThirst(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableThirst = var0;
   }

   @LuaMethod(
      name = "isDisableThirst",
      global = true
   )
   public static boolean isDisableThirst() {
      return EtherMain.getInstance().etherAPI.isDisableThirst;
   }

   @LuaMethod(
      name = "toggleDisableDrunkenness",
      global = true
   )
   public static void toggleDisableDrunkenness(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableDrunkenness = var0;
   }

   @LuaMethod(
      name = "isDisableDrunkenness",
      global = true
   )
   public static boolean isDisableDrunkenness() {
      return EtherMain.getInstance().etherAPI.isDisableDrunkenness;
   }

   @LuaMethod(
      name = "toggleDisableAnger",
      global = true
   )
   public static void toggleDisableAnger(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableAnger = var0;
   }

   @LuaMethod(
      name = "isDisableAnger",
      global = true
   )
   public static boolean isDisableAnger() {
      return EtherMain.getInstance().etherAPI.isDisableAnger;
   }

   @LuaMethod(
      name = "toggleDisableFear",
      global = true
   )
   public static void toggleDisableFear(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableFear = var0;
   }

   @LuaMethod(
      name = "isDisableFear",
      global = true
   )
   public static boolean isDisableFear() {
      return EtherMain.getInstance().etherAPI.isDisableFear;
   }

   @LuaMethod(
      name = "toggleDisablePain",
      global = true
   )
   public static void toggleDisablePain(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisablePain = var0;
   }

   @LuaMethod(
      name = "isDisablePain",
      global = true
   )
   public static boolean isDisablePain() {
      return EtherMain.getInstance().etherAPI.isDisablePain;
   }

   @LuaMethod(
      name = "toggleDisablePanic",
      global = true
   )
   public static void toggleDisablePanic(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisablePanic = var0;
   }

   @LuaMethod(
      name = "isDisablePanic",
      global = true
   )
   public static boolean isDisablePanic() {
      return EtherMain.getInstance().etherAPI.isDisablePanic;
   }

   @LuaMethod(
      name = "toggleDisableMorale",
      global = true
   )
   public static void toggleDisableMorale(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableMorale = var0;
   }

   @LuaMethod(
      name = "isDisableMorale",
      global = true
   )
   public static boolean isDisableMorale() {
      return EtherMain.getInstance().etherAPI.isDisableMorale;
   }

   @LuaMethod(
      name = "toggleDisableStress",
      global = true
   )
   public static void toggleDisableStress(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableStress = var0;
   }

   @LuaMethod(
      name = "isDisableStress",
      global = true
   )
   public static boolean isDisableStress() {
      return EtherMain.getInstance().etherAPI.isDisableStress;
   }

   @LuaMethod(
      name = "toggleDisableSickness",
      global = true
   )
   public static void toggleDisableSickness(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableSickness = var0;
   }

   @LuaMethod(
      name = "isDisableSickness",
      global = true
   )
   public static boolean isDisableSickness() {
      return EtherMain.getInstance().etherAPI.isDisableSickness;
   }

   @LuaMethod(
      name = "toggleDisableStressFromCigarettes",
      global = true
   )
   public static void toggleDisableStressFromCigarettes(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableStressFromCigarettes = var0;
   }

   @LuaMethod(
      name = "isDisableStressFromCigarettes",
      global = true
   )
   public static boolean isDisableStressFromCigarettes() {
      return EtherMain.getInstance().etherAPI.isDisableStressFromCigarettes;
   }

   @LuaMethod(
      name = "toggleDisableSanity",
      global = true
   )
   public static void toggleDisableSanity(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableSanity = var0;
   }

   @LuaMethod(
      name = "isDisableSanity",
      global = true
   )
   public static boolean isDisableSanity() {
      return EtherMain.getInstance().etherAPI.isDisableSanity;
   }

   @LuaMethod(
      name = "toggleDisableBoredomLevel",
      global = true
   )
   public static void toggleDisableBoredomLevel(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableBoredomLevel = var0;
   }

   @LuaMethod(
      name = "isDisableBoredomLevel",
      global = true
   )
   public static boolean isDisableBoredomLevel() {
      return EtherMain.getInstance().etherAPI.isDisableBoredomLevel;
   }

   @LuaMethod(
      name = "toggleDisableUnhappynessLevel",
      global = true
   )
   public static void toggleDisableUnhappynessLevel(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableUnhappynessLevel = var0;
   }

   @LuaMethod(
      name = "isDisableUnhappynessLevel",
      global = true
   )
   public static boolean isDisableUnhappynessLevel() {
      return EtherMain.getInstance().etherAPI.isDisableUnhappynessLevel;
   }

   @LuaMethod(name = "toggleDisableWetness", global = true)
   public static void toggleDisableWetness(boolean value) {
      EtherMain.getInstance().etherAPI.isDisableWetness = value;
   }

   @LuaMethod(name = "isDisableWetness", global = true)
   public static boolean isDisableWetness() {
      return EtherMain.getInstance().etherAPI.isDisableWetness;
   }

   @LuaMethod(name = "toggleDisableInfectionLevel", global = true)
   public static void toggleDisableInfectionLevel(boolean value) {
      EtherMain.getInstance().etherAPI.isDisableInfectionLevel = value;
   }

   @LuaMethod(name = "isDisableInfectionLevel", global = true)
   public static boolean isDisableInfectionLevel() {
      return EtherMain.getInstance().etherAPI.isDisableInfectionLevel;
   }

   @LuaMethod(
      name = "toggleDisableFakeInfectionLevel",
      global = true
   )
   public static void toggleDisableFakeInfectionLevel(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableFakeInfectionLevel = var0;
   }

   @LuaMethod(name = "isDisableFakeInfectionLevel", global = true)
   public static boolean isDisableFakeInfectionLevel() {
      return EtherMain.getInstance().etherAPI.isDisableFakeInfectionLevel;
   }

   @LuaMethod(name = "toggleOptimalCalories", global = true)
   public static void toggleOptimalCalories(boolean value) {
      EtherMain.getInstance().etherAPI.isOptimalCalories = value;
   }

   @LuaMethod(name = "isOptimalCalories", global = true)
   public static boolean isOptimalCalories() {
      return EtherMain.getInstance().etherAPI.isOptimalCalories;
   }

   @LuaMethod(name = "toggleOptimalWeight", global = true)
   public static void toggleOptimalWeight(boolean value) {
      EtherMain.getInstance().etherAPI.isOptimalWeight = value;
   }

   @LuaMethod(name = "isOptimalWeight", global = true)
   public static boolean isOptimalWeight() {
      return EtherMain.getInstance().etherAPI.isOptimalWeight;
   }

   @LuaMethod(name = "toggleEnableUnlimitedCarry", global = true)
   public static void toggleEnableUnlimitedCarry(boolean value) {
      EtherMain.getInstance().etherAPI.isUnlimitedCarry = value;
   }

   @LuaMethod(name = "isEnableUnlimitedCarry", global = true)
   public static boolean isEnableUnlimitedCarry() {
      return EtherMain.getInstance().etherAPI.isUnlimitedCarry;
   }

   @LuaMethod(name = "getAntiCheat12Status", global = true)
   public static boolean getAntiCheat12Status() {
      String verificationKey = SafeAPI.getInstance().generateVerificationKey();
      methodCache.put(verificationKey, ServerOptions.instance.getBoolean("AntiCheatProtectionType12"));
      return (Boolean) methodCache.remove(verificationKey);
   }

   @LuaMethod(name = "getAntiCheat8Status", global = true)
   public static boolean getAntiCheat8Status() {
      // Add method verification
      String verificationKey = SafeAPI.getInstance().generateVerificationKey();
      methodCache.put(verificationKey, ServerOptions.instance.getBoolean("AntiCheatProtectionType8"));
      return (Boolean) methodCache.remove(verificationKey);
   }

   @LuaMethod(name = "requireExtra", global = true)
   public static void requireExtra(String file) {
      String key = SafeAPI.getInstance().generateVerificationKey();
      try {
         String luaFile = file.endsWith(".lua") ? file : file + ".lua";
         if (!EtherMain.getInstance().etherLuaManager.luaFilesList.contains(luaFile)) {
            EtherMain.getInstance().etherLuaManager.luaFilesList.add(luaFile);
         }
         EtherLuaCompiler.getInstance().addWordToBlacklistLuaCompiler(
                 luaFile.substring(0, luaFile.lastIndexOf("."))
         );
         EtherLuaCompiler.getInstance().addPathToWhiteListLuaCompiler(luaFile);
         LuaManager.RunLua(luaFile);
      } catch (Exception e) {
         Logger.printLog("Error in requireExtra: " + e.getMessage());
      }
   }

   @LuaMethod(name = "getExtraTexture", global = true)
   public static Texture getExtraTexture(String path) {
      String key = SafeAPI.getInstance().generateVerificationKey();
      try {
         if (!path.endsWith(".png")) {
            Logger.printLog("Incorrect path to the image file. Required .png");
            return null;
         }

         ConcurrentHashMap<String, Texture> textureCache =
                 EtherMain.getInstance().etherAPI.textureCache;

         if (textureCache.containsKey(path)) {
            return textureCache.get(path);
         }

         try (FileInputStream fis = new FileInputStream(Paths.get(path).toFile());
              BufferedInputStream bis = new BufferedInputStream(fis)) {
            Texture texture = new Texture(path, bis, false);
            textureCache.put(path, texture);
            return texture;
         }
      } catch (Exception e) {
         Logger.printLog("Error reading image: " + e.getMessage());
         return null;
      }
   }

   @LuaMethod(
      name = "getTranslate",
      global = true
   )
   public static String getTranslate(String var0, KahluaTable var1) {
      return EtherMain.getInstance().etherTranslator.getTranslate(var0, var1);
   }

   @LuaMethod(
      name = "getTranslate",
      global = true
   )
   public static String getTranslate(String var0) {
      return EtherMain.getInstance().etherTranslator.getTranslate(var0);
   }

   @LuaMethod(name = "hackAdminAccess", global = true)
   public static void hackAdminAccess() {
      String key = SafeAPI.getInstance().generateVerificationKey();
      try {
         for (IsoPlayer player : GameClient.instance.getPlayers()) {
            if (player.isLocalPlayer()) {
               player.accessLevel = "admin";
            }
         }
      } catch (Exception e) {
         Logger.printLog("Error in hackAdminAccess: " + e.getMessage());
      }
   }

   @LuaMethod(
      name = "getAccentUIColor",
      global = true
   )
   public static Color getAccentUIColor() {
      return EtherMain.getInstance().etherAPI.mainUIAccentColor;
   }

   protected void cleanMethodCache() {
      methodCache.clear();
   }

   // Singleton pattern
   public static EtherLuaMethods getInstance() {
      if (instance == null) {
         instance = new EtherLuaMethods();
      }
      return instance;
   }
}