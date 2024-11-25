package EtherHack.Ether;

import EtherHack.utils.Logger;
import EtherHack.utils.PlayerUtils;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

public final class EtherLuaMethods {
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
   public static ArrayList getConfigList() {
      ArrayList var0 = new ArrayList();

      try {
         Path var1 = Paths.get("EtherHack/config");
         List var2 = Files.list(var1).filter(EtherLuaMethods::lambda$getConfigList$0).toList();
         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            Path var4 = (Path)var3.next();
            String var5 = var4.getFileName().toString().replace(".properties", "");
            var0.add(var5);
         }

         return var0;
      } catch (IOException var6) {
         Logger.printLog("An error occurred while getting the list of config files: " + String.valueOf(var6));
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

   @LuaMethod(
      name = "safePlayerTeleport",
      global = true
   )
   public static void safePlayerTeleport(int var0, int var1) {
      EtherMain.getInstance().etherAPI.isPlayerInSafeTeleported = true;
      IsoPlayer var2 = IsoPlayer.getInstance();
      float var3 = var2.z;
      float var4 = (float)var0 - var2.x;
      float var5 = (float)var1 - var2.y;
      float var6 = var3 - var2.z;
      float var7 = Math.abs(var4);
      float var8 = Math.abs(var5);
      float var9 = Math.abs(var6);

      while(var7 > 0.0F || var8 > 0.0F || var9 > 0.0F) {
         float var10 = 1.0F;
         float var11 = Math.min(Math.min(var7, var10), 1.0F);
         float var12 = Math.min(Math.min(var8, var10), 1.0F);
         float var13 = Math.min(Math.min(var9, var10), 1.0F);
         var7 -= var11;
         var8 -= var12;
         var9 -= var13;
         if (var4 < 0.0F) {
            var11 = -var11;
         }

         if (var5 < 0.0F) {
            var12 = -var12;
         }

         if (var6 < 0.0F) {
            var13 = -var13;
         }

         var2.setX(var2.x + var11);
         var2.setY(var2.y + var12);
         var2.setZ(var2.z + var13);
         var2.setLx(var2.getX());
         var2.setLy(var2.getY());
         var2.setLz(var2.getZ());
         GameClient.instance.sendPlayer(var2);
         if (GameClient.connection != null && PlayerPacket.l_send.playerPacket.set(var2)) {
            ByteBufferWriter var14 = GameClient.connection.startPacket();
            PacketTypes.PacketType.PlayerUpdateReliable.doPacket(var14);
            PlayerPacket.l_send.playerPacket.write(var14);
            PacketTypes.PacketType.PlayerUpdateReliable.send(GameClient.connection);
         }
      }

      EtherMain.getInstance().etherAPI.isPlayerInSafeTeleported = false;
   }

   @LuaMethod(
      name = "isPlayerInSafeTeleported",
      global = true
   )
   public static boolean isPlayerInSafeTeleported() {
      return EtherMain.getInstance().etherAPI.isPlayerInSafeTeleported;
   }

   @LuaMethod(
      name = "learnAllRecipes",
      global = true
   )
   public static void learnAllRecipes() {
      IsoPlayer var0 = IsoPlayer.getInstance();
      if (var0 != null) {
         ArrayList var1 = ScriptManager.instance.getAllRecipes();
         if (var1 != null) {
            Iterator var2 = var1.iterator();

            while(var2.hasNext()) {
               Recipe var3 = (Recipe)var2.next();
               if (var3.getOriginalname() != null) {
                  var0.learnRecipe(var3.getOriginalname());
               }
            }
         }
      }

   }

   @LuaMethod(
      name = "giveItem",
      global = true
   )
   public static void giveItem(InventoryItem var0, int var1) {
      IsoPlayer var2 = IsoPlayer.getInstance();
      if (var2 != null) {
         for(int var3 = 0; var3 < var1; ++var3) {
            var2.getInventory().AddItem(var0);
         }
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

   @LuaMethod(
      name = "toggleDisableWetness",
      global = true
   )
   public static void toggleDisableWetness(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableWetness = var0;
   }

   @LuaMethod(
      name = "isDisableWetness",
      global = true
   )
   public static boolean isDisableWetness() {
      return EtherMain.getInstance().etherAPI.isDisableWetness;
   }

   @LuaMethod(
      name = "toggleDisableInfectionLevel",
      global = true
   )
   public static void toggleDisableInfectionLevel(boolean var0) {
      EtherMain.getInstance().etherAPI.isDisableInfectionLevel = var0;
   }

   @LuaMethod(
      name = "isDisableInfectionLevel",
      global = true
   )
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

   @LuaMethod(
      name = "isDisableFakeInfectionLevel",
      global = true
   )
   public static boolean isDisableFakeInfectionLevel() {
      return EtherMain.getInstance().etherAPI.isDisableFakeInfectionLevel;
   }

   @LuaMethod(
      name = "toggleOptimalCalories",
      global = true
   )
   public static void toggleOptimalCalories(boolean var0) {
      EtherMain.getInstance().etherAPI.isOptimalCalories = var0;
   }

   @LuaMethod(
      name = "isOptimalCalories",
      global = true
   )
   public static boolean isOptimalCalories() {
      return EtherMain.getInstance().etherAPI.isOptimalCalories;
   }

   @LuaMethod(
      name = "toggleOptimalWeight",
      global = true
   )
   public static void toggleOptimalWeight(boolean var0) {
      EtherMain.getInstance().etherAPI.isOptimalWeight = var0;
   }

   @LuaMethod(
      name = "isOptimalWeight",
      global = true
   )
   public static boolean isOptimalWeight() {
      return EtherMain.getInstance().etherAPI.isOptimalWeight;
   }

   @LuaMethod(
      name = "toggleEnableUnlimitedCarry",
      global = true
   )
   public static void toggleEnableUnlimitedCarry(boolean var0) {
      EtherMain.getInstance().etherAPI.isUnlimitedCarry = var0;
   }

   @LuaMethod(
      name = "isEnableUnlimitedCarry",
      global = true
   )
   public static boolean isEnableUnlimitedCarry() {
      return EtherMain.getInstance().etherAPI.isUnlimitedCarry;
   }

   @LuaMethod(
      name = "getAntiCheat12Status",
      global = true
   )
   public static boolean getAntiCheat12Status() {
      return ServerOptions.instance.getBoolean("AntiCheatProtectionType12");
   }

   @LuaMethod(
      name = "getAntiCheat8Status",
      global = true
   )
   public static boolean getAntiCheat8Status() {
      return ServerOptions.instance.getBoolean("AntiCheatProtectionType8");
   }

   @LuaMethod(
      name = "requireExtra",
      global = true
   )
   public static void requireExtra(String var0) {
      String var1 = var0.endsWith(".lua") ? var0 : var0 + ".lua";
      if (!EtherMain.getInstance().etherLuaManager.luaFilesList.contains(var1)) {
         EtherMain.getInstance().etherLuaManager.luaFilesList.add(var1);
      }

      Path var2 = Paths.get(var1);
      String var3 = var2.getFileName().toString();
      var3 = var3.substring(0, var3.lastIndexOf("."));
      EtherLuaCompiler.getInstance().addWordToBlacklistLuaCompiler(var3);
      EtherLuaCompiler.getInstance().addPathToWhiteListLuaCompiler(var1);
      LuaManager.RunLua(var1);
   }

   @LuaMethod(
      name = "getExtraTexture",
      global = true
   )
   public static Texture getExtraTexture(String var0) {
      if (!var0.endsWith(".png")) {
         Logger.printLog("Incorrect path to the image file. Required .png");
         return null;
      } else {
         ConcurrentHashMap<String, Texture> var1 = EtherMain.getInstance().etherAPI.textureCache;
         if (var1.containsKey(var0)) {
            return (Texture)var1.get(var0);
         } else {
            try {
               FileInputStream var2 = new FileInputStream(Paths.get(var0).toFile());
               BufferedInputStream var3 = new BufferedInputStream(var2);
               Texture var4 = new Texture(var0, var3, false);
               var1.put(var0, var4);
               return var4;
            } catch (Exception var5) {
               Logger.printLog("Error when reading the image: " + String.valueOf(var5));
               return null;
            }
         }
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

   @LuaMethod(
      name = "hackAdminAccess",
      global = true
   )
   public static void hackAdminAccess() {
      Iterator var0 = GameClient.instance.getPlayers().iterator();

      while(var0.hasNext()) {
         IsoPlayer var1 = (IsoPlayer)var0.next();
         if (var1.isLocalPlayer()) {
            var1.accessLevel = "admin";
            var1.accessLevel.equals("admin");
         }
      }

   }

   @LuaMethod(
      name = "getAccentUIColor",
      global = true
   )
   public static Color getAccentUIColor() {
      return EtherMain.getInstance().etherAPI.mainUIAccentColor;
   }

   private static boolean lambda$getConfigList$0(Path var0) {
      return var0.toString().endsWith(".properties");
   }
}
