package EtherHack.Ether;

import EtherHack.GameClientWrapper;
import EtherHack.annotations.LuaEvents;
import EtherHack.annotations.SubscribeLuaEvent;
import EtherHack.utils.ColorUtils;
import EtherHack.utils.ConfigUtils;
import EtherHack.utils.EventSubscriber;
import EtherHack.utils.Exposer;
import EtherHack.utils.Logger;
import EtherHack.utils.PlayerUtils;
import EtherHack.utils.Rendering;
import EtherHack.utils.VehicleUtils;
import EtherHack.utils.ZombieUtils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.j2se.J2SEPlatform;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.Platform;
import zombie.SandboxOptions;
import zombie.Lua.LuaManager;
import zombie.characterTextures.BloodBodyPartType;
import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Color;
import zombie.core.Core;
import zombie.core.textures.Texture;
import zombie.inventory.InventoryItem;
import zombie.inventory.types.HandWeapon;
import zombie.iso.IsoWorld;
import zombie.network.GameClient;
import zombie.network.GameServer;
import zombie.network.ServerOptions;
import zombie.network.ZomboidNetData;
import zombie.ui.UIFont;
import zombie.vehicles.BaseVehicle;

import static zombie.Lua.LuaManager.env;

public class EtherAPI {
   private final ProtectionManagerX protectionManager;
   private Exposer exposer;
   private final SafeEtherLuaMethods etherLuaMethods = new SafeEtherLuaMethods();
   final ConcurrentHashMap<String, Texture> textureCache = new ConcurrentHashMap<>();
   private final SafeAPI safeAPI = SafeAPI.getInstance();
   private final ConcurrentHashMap<String, float[]> originalWeaponStats = new ConcurrentHashMap<>();
   public Color mainUIAccentColor;
   public Color vehiclesUIColor;
   public Color zombiesUIColor;
   public Color playersUIColor;
   public boolean isPlayerInSafeTeleported;
   public boolean isMultiHitZombies;
   public boolean isExtraDamage;
   public boolean isTimedActionCheat;
   public boolean isEnableGodMode;
   public boolean isEnableNoclip;
   public boolean isEnableInvisible;
   public boolean isEnableNightVision;
   public boolean isZombieDontAttack;
   public boolean isNoRecoil;
   public boolean isBypassDebugMode;
   public boolean isUnlimitedCarry;
   public boolean isUnlimitedCondition;
   public boolean isUnlimitedEndurance;
   public boolean isUnlimitedAmmo;
   public boolean isAutoRepairItems;
   public boolean isDisableFatigue;
   public boolean isDisableHunger;
   public boolean isDisableThirst;
   public boolean isDisableDrunkenness;
   public boolean isDisableAnger;
   public boolean isDisableFear;
   public boolean isDisablePain;
   public boolean isDisablePanic;
   public boolean isDisableMorale;
   public boolean isDisableStress;
   public boolean isDisableSickness;
   public boolean isDisableStressFromCigarettes;
   public boolean isDisableSanity;
   public boolean isDisableBoredomLevel;
   public boolean isDisableUnhappynessLevel;
   public boolean isDisableWetness;
   public boolean isDisableInfectionLevel;
   public boolean isDisableFakeInfectionLevel;
   public boolean isOptimalCalories;
   public boolean isOptimalWeight;
   public boolean isVisualsEnable;
   public boolean isVisualsPlayersEnable;
   public boolean isVisualsVehiclesEnable;
   public boolean isVisualsZombiesEnable;
   public boolean isVisualDrawToLocalPlayer;
   public boolean isVisualDrawPlayerNickname;
   public boolean isVisualDrawCredits;
   public boolean isVisualDrawPlayerInfo;
   public boolean isVisualDrawLineToVehicle;
   public boolean isVisualDrawLineToPlayers;
   public boolean isVisualEnable360Vision;
   public boolean isMapDrawLocalPlayer;
   public boolean isMapDrawAllPlayers;
   public boolean isMapDrawVehicles;
   public boolean isMapDrawZombies;

   public void saveConfig(String var1) {
      String var2 = "EtherHack/config/" + var1 + ".properties";
      Properties var3 = new Properties();
      var3.setProperty("mainUIAccentColor", ColorUtils.colorToString(this.mainUIAccentColor));
      var3.setProperty("vehiclesUIColor", ColorUtils.colorToString(this.vehiclesUIColor));
      var3.setProperty("zombiesUIColor", ColorUtils.colorToString(this.zombiesUIColor));
      var3.setProperty("playersUIColor", ColorUtils.colorToString(this.playersUIColor));
      var3.setProperty("isPlayerInSafeTeleported", Boolean.toString(this.isPlayerInSafeTeleported));
      var3.setProperty("isMultiHitZombies", Boolean.toString(this.isMultiHitZombies));
      var3.setProperty("isPlayerInSafeTeleported", Boolean.toString(this.isPlayerInSafeTeleported));
      var3.setProperty("isMultiHitZombies", Boolean.toString(this.isMultiHitZombies));
      var3.setProperty("isExtraDamage", Boolean.toString(this.isExtraDamage));
      var3.setProperty("isTimedActionCheat", Boolean.toString(this.isTimedActionCheat));
      var3.setProperty("isEnableGodMode", Boolean.toString(this.isEnableGodMode));
      var3.setProperty("isEnableNoclip", Boolean.toString(this.isEnableNoclip));
      var3.setProperty("isEnableInvisible", Boolean.toString(this.isEnableInvisible));
      var3.setProperty("isEnableNightVision", Boolean.toString(this.isEnableNightVision));
      var3.setProperty("isZombieDontAttack", Boolean.toString(this.isZombieDontAttack));
      var3.setProperty("isNoRecoil", Boolean.toString(this.isNoRecoil));
      var3.setProperty("isBypassDebugMode", Boolean.toString(this.isBypassDebugMode));
      var3.setProperty("isUnlimitedCarry", Boolean.toString(this.isUnlimitedCarry));
      var3.setProperty("isUnlimitedCondition", Boolean.toString(this.isUnlimitedCondition));
      var3.setProperty("isUnlimitedEndurance", Boolean.toString(this.isUnlimitedEndurance));
      var3.setProperty("isUnlimitedAmmo", Boolean.toString(this.isUnlimitedAmmo));
      var3.setProperty("isAutoRepairItems", Boolean.toString(this.isAutoRepairItems));
      var3.setProperty("isDisableFatigue", Boolean.toString(this.isDisableFatigue));
      var3.setProperty("isDisableHunger", Boolean.toString(this.isDisableHunger));
      var3.setProperty("isDisableThirst", Boolean.toString(this.isDisableThirst));
      var3.setProperty("isDisableDrunkenness", Boolean.toString(this.isDisableDrunkenness));
      var3.setProperty("isDisableAnger", Boolean.toString(this.isDisableAnger));
      var3.setProperty("isDisableFear", Boolean.toString(this.isDisableFear));
      var3.setProperty("isDisablePain", Boolean.toString(this.isDisablePain));
      var3.setProperty("isDisablePanic", Boolean.toString(this.isDisablePanic));
      var3.setProperty("isDisableMorale", Boolean.toString(this.isDisableMorale));
      var3.setProperty("isDisableStress", Boolean.toString(this.isDisableStress));
      var3.setProperty("isDisableSickness", Boolean.toString(this.isDisableSickness));
      var3.setProperty("isDisableStressFromCigarettes", Boolean.toString(this.isDisableStressFromCigarettes));
      var3.setProperty("isDisableSanity", Boolean.toString(this.isDisableSanity));
      var3.setProperty("isDisableBoredomLevel", Boolean.toString(this.isDisableBoredomLevel));
      var3.setProperty("isDisableUnhappynessLevel", Boolean.toString(this.isDisableUnhappynessLevel));
      var3.setProperty("isDisableWetness", Boolean.toString(this.isDisableWetness));
      var3.setProperty("isDisableInfectionLevel", Boolean.toString(this.isDisableInfectionLevel));
      var3.setProperty("isDisableFakeInfectionLevel", Boolean.toString(this.isDisableFakeInfectionLevel));
      var3.setProperty("isOptimalCalories", Boolean.toString(this.isOptimalCalories));
      var3.setProperty("isOptimalWeight", Boolean.toString(this.isOptimalWeight));
      var3.setProperty("isVisualsEnable", Boolean.toString(this.isVisualsEnable));
      var3.setProperty("isVisualsPlayersEnable", Boolean.toString(this.isVisualsPlayersEnable));
      var3.setProperty("isVisualsVehiclesEnable", Boolean.toString(this.isVisualsVehiclesEnable));
      var3.setProperty("isVisualsZombiesEnable", Boolean.toString(this.isVisualsZombiesEnable));
      var3.setProperty("isVisualDrawToLocalPlayer", Boolean.toString(this.isVisualDrawToLocalPlayer));
      var3.setProperty("isVisualDrawPlayerNickname", Boolean.toString(this.isVisualDrawPlayerNickname));
      var3.setProperty("isVisualDrawCredits", Boolean.toString(this.isVisualDrawCredits));
      var3.setProperty("isVisualDrawPlayerInfo", Boolean.toString(this.isVisualDrawPlayerInfo));
      var3.setProperty("isVisualDrawLineToVehicle", Boolean.toString(this.isVisualDrawLineToVehicle));
      var3.setProperty("isVisualDrawLineToPlayers", Boolean.toString(this.isVisualDrawLineToPlayers));
      var3.setProperty("isVisualEnable360Vision", Boolean.toString(this.isVisualEnable360Vision));
      var3.setProperty("isMapDrawLocalPlayer", Boolean.toString(this.isMapDrawLocalPlayer));
      var3.setProperty("isMapDrawAllPlayers", Boolean.toString(this.isMapDrawAllPlayers));
      var3.setProperty("isMapDrawVehicles", Boolean.toString(this.isMapDrawVehicles));
      var3.setProperty("isMapDrawZombies", Boolean.toString(this.isMapDrawZombies));

      try {
         FileOutputStream var4 = new FileOutputStream(var2);

         try {
            var3.store(var4, (String)null);
         } catch (Throwable var8) {
            try {
               var4.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         var4.close();
      } catch (IOException var9) {
         Logger.printLog("Error while saving config: " + var9);
      }

   }

   public void loadConfig(String var1) {
      String var2 = "EtherHack/config/" + var1 + ".properties";
      Properties var3 = new Properties();

      try {
         FileInputStream var4 = new FileInputStream(var2);

         try {
            var3.load(var4);
         } catch (Throwable var8) {
            try {
               var4.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         var4.close();
      } catch (IOException var9) {
         Logger.printLog("The config file was not found. Loading canceled.");
         return;
      }

      this.mainUIAccentColor = ConfigUtils.getColorFromConfig(var3, "mainUIAccentColor", new Color(56, 239, 125));
      this.vehiclesUIColor = ConfigUtils.getColorFromConfig(var3, "vehiclesUIColor", new Color(150, 150, 200));
      this.zombiesUIColor = ConfigUtils.getColorFromConfig(var3, "zombiesUIColor", new Color(255, 150, 100));
      this.playersUIColor = ConfigUtils.getColorFromConfig(var3, "playersUIColor", new Color(255, 50, 100));
      this.isPlayerInSafeTeleported = ConfigUtils.getBooleanFromConfig(var3, "isPlayerInSafeTeleported", false);
      this.isMultiHitZombies = ConfigUtils.getBooleanFromConfig(var3, "isMultiHitZombies", false);
      this.isExtraDamage = ConfigUtils.getBooleanFromConfig(var3, "isExtraDamage", false);
      this.isTimedActionCheat = ConfigUtils.getBooleanFromConfig(var3, "isTimedActionCheat", false);
      this.isEnableGodMode = ConfigUtils.getBooleanFromConfig(var3, "isEnableGodMode", false);
      this.isEnableNoclip = ConfigUtils.getBooleanFromConfig(var3, "isEnableNoclip", false);
      this.isEnableInvisible = ConfigUtils.getBooleanFromConfig(var3, "isEnableInvisible", false);
      this.isEnableNightVision = ConfigUtils.getBooleanFromConfig(var3, "isEnableNightVision", false);
      this.isZombieDontAttack = ConfigUtils.getBooleanFromConfig(var3, "isZombieDontAttack", false);
      this.isNoRecoil = ConfigUtils.getBooleanFromConfig(var3, "isNoRecoil", false);
      this.isBypassDebugMode = ConfigUtils.getBooleanFromConfig(var3, "isBypassDebugMode", false);
      this.isUnlimitedCarry = ConfigUtils.getBooleanFromConfig(var3, "isUnlimitedCarry", false);
      this.isUnlimitedCondition = ConfigUtils.getBooleanFromConfig(var3, "isUnlimitedCondition", false);
      this.isUnlimitedEndurance = ConfigUtils.getBooleanFromConfig(var3, "isUnlimitedEndurance", false);
      this.isUnlimitedAmmo = ConfigUtils.getBooleanFromConfig(var3, "isUnlimitedAmmo", false);
      this.isAutoRepairItems = ConfigUtils.getBooleanFromConfig(var3, "isAutoRepairItems", false);
      this.isDisableFatigue = ConfigUtils.getBooleanFromConfig(var3, "isDisableFatigue", false);
      this.isDisableHunger = ConfigUtils.getBooleanFromConfig(var3, "isDisableHunger", false);
      this.isDisableThirst = ConfigUtils.getBooleanFromConfig(var3, "isDisableThirst", false);
      this.isDisableDrunkenness = ConfigUtils.getBooleanFromConfig(var3, "isDisableDrunkenness", false);
      this.isDisableAnger = ConfigUtils.getBooleanFromConfig(var3, "isDisableAnger", false);
      this.isDisableFear = ConfigUtils.getBooleanFromConfig(var3, "isDisableFear", false);
      this.isDisablePain = ConfigUtils.getBooleanFromConfig(var3, "isDisablePain", false);
      this.isDisablePanic = ConfigUtils.getBooleanFromConfig(var3, "isDisablePanic", false);
      this.isDisableMorale = ConfigUtils.getBooleanFromConfig(var3, "isDisableMorale", false);
      this.isDisableStress = ConfigUtils.getBooleanFromConfig(var3, "isDisableStress", false);
      this.isDisableSickness = ConfigUtils.getBooleanFromConfig(var3, "isDisableSickness", false);
      this.isDisableStressFromCigarettes = ConfigUtils.getBooleanFromConfig(var3, "isDisableStressFromCigarettes", false);
      this.isDisableSanity = ConfigUtils.getBooleanFromConfig(var3, "isDisableSanity", false);
      this.isDisableBoredomLevel = ConfigUtils.getBooleanFromConfig(var3, "isDisableBoredomLevel", false);
      this.isDisableUnhappynessLevel = ConfigUtils.getBooleanFromConfig(var3, "isDisableUnhappynessLevel", false);
      this.isDisableWetness = ConfigUtils.getBooleanFromConfig(var3, "isDisableWetness", false);
      this.isDisableInfectionLevel = ConfigUtils.getBooleanFromConfig(var3, "isDisableInfectionLevel", false);
      this.isDisableFakeInfectionLevel = ConfigUtils.getBooleanFromConfig(var3, "isDisableFakeInfectionLevel", false);
      this.isOptimalCalories = ConfigUtils.getBooleanFromConfig(var3, "isOptimalCalories", false);
      this.isOptimalWeight = ConfigUtils.getBooleanFromConfig(var3, "isOptimalWeight", false);
      this.isVisualsEnable = ConfigUtils.getBooleanFromConfig(var3, "isVisualsEnable", false);
      this.isVisualsPlayersEnable = ConfigUtils.getBooleanFromConfig(var3, "isVisualsPlayersEnable", false);
      this.isVisualsVehiclesEnable = ConfigUtils.getBooleanFromConfig(var3, "isVisualsVehiclesEnable", false);
      this.isVisualsZombiesEnable = ConfigUtils.getBooleanFromConfig(var3, "isVisualsZombiesEnable", false);
      this.isVisualDrawToLocalPlayer = ConfigUtils.getBooleanFromConfig(var3, "isVisualDrawToLocalPlayer", false);
      this.isVisualDrawPlayerNickname = ConfigUtils.getBooleanFromConfig(var3, "isVisualDrawPlayerNickname", false);
      this.isVisualDrawCredits = ConfigUtils.getBooleanFromConfig(var3, "isVisualDrawCredits", true);
      this.isVisualDrawPlayerInfo = ConfigUtils.getBooleanFromConfig(var3, "isVisualDrawPlayerInfo", false);
      this.isVisualDrawLineToVehicle = ConfigUtils.getBooleanFromConfig(var3, "isVisualDrawLineToVehicle", false);
      this.isVisualDrawLineToPlayers = ConfigUtils.getBooleanFromConfig(var3, "isVisualDrawLineToPlayers", false);
      this.isVisualEnable360Vision = ConfigUtils.getBooleanFromConfig(var3, "isVisualEnable360Vision", false);
      this.isMapDrawLocalPlayer = ConfigUtils.getBooleanFromConfig(var3, "isMapDrawLocalPlayer", true);
      this.isMapDrawAllPlayers = ConfigUtils.getBooleanFromConfig(var3, "isMapDrawAllPlayers", false);
      this.isMapDrawVehicles = ConfigUtils.getBooleanFromConfig(var3, "isMapDrawVehicles", false);
      this.isMapDrawZombies = ConfigUtils.getBooleanFromConfig(var3, "isMapDrawZombies", false);
   }

   private void initStartupConfig() {
      Properties var1 = new Properties();

      try {
         FileInputStream var2 = new FileInputStream("EtherHack/config/startup.properties");

         try {
            var1.load(var2);
         } catch (Throwable var6) {
            try {
               var2.close();
            } catch (Throwable var5) {
               var6.addSuppressed(var5);
            }

            throw var6;
         }

         var2.close();
      } catch (IOException var7) {
         Logger.printLog("Startup file not found. Loading default settings.");
      }

      this.mainUIAccentColor = ConfigUtils.getColorFromConfig(var1, "mainUIAccentColor", new Color(56, 239, 125));
      this.vehiclesUIColor = ConfigUtils.getColorFromConfig(var1, "vehiclesUIColor", new Color(150, 150, 200));
      this.zombiesUIColor = ConfigUtils.getColorFromConfig(var1, "zombiesUIColor", new Color(255, 150, 100));
      this.playersUIColor = ConfigUtils.getColorFromConfig(var1, "playersUIColor", new Color(255, 50, 100));
      this.isPlayerInSafeTeleported = ConfigUtils.getBooleanFromConfig(var1, "isPlayerInSafeTeleported", false);
      this.isMultiHitZombies = ConfigUtils.getBooleanFromConfig(var1, "isMultiHitZombies", false);
      this.isExtraDamage = ConfigUtils.getBooleanFromConfig(var1, "isExtraDamage", false);
      this.isTimedActionCheat = ConfigUtils.getBooleanFromConfig(var1, "isTimedActionCheat", false);
      this.isEnableGodMode = ConfigUtils.getBooleanFromConfig(var1, "isEnableGodMode", false);
      this.isEnableNoclip = ConfigUtils.getBooleanFromConfig(var1, "isEnableNoclip", false);
      this.isEnableInvisible = ConfigUtils.getBooleanFromConfig(var1, "isEnableInvisible", false);
      this.isEnableNightVision = ConfigUtils.getBooleanFromConfig(var1, "isEnableNightVision", false);
      this.isZombieDontAttack = ConfigUtils.getBooleanFromConfig(var1, "isZombieDontAttack", false);
      this.isNoRecoil = ConfigUtils.getBooleanFromConfig(var1, "isNoRecoil", false);
      this.isBypassDebugMode = ConfigUtils.getBooleanFromConfig(var1, "isBypassDebugMode", false);
      this.isUnlimitedCarry = ConfigUtils.getBooleanFromConfig(var1, "isUnlimitedCarry", false);
      this.isUnlimitedCondition = ConfigUtils.getBooleanFromConfig(var1, "isUnlimitedCondition", false);
      this.isUnlimitedEndurance = ConfigUtils.getBooleanFromConfig(var1, "isUnlimitedEndurance", false);
      this.isUnlimitedAmmo = ConfigUtils.getBooleanFromConfig(var1, "isUnlimitedAmmo", false);
      this.isAutoRepairItems = ConfigUtils.getBooleanFromConfig(var1, "isAutoRepairItems", false);
      this.isDisableFatigue = ConfigUtils.getBooleanFromConfig(var1, "isDisableFatigue", false);
      this.isDisableHunger = ConfigUtils.getBooleanFromConfig(var1, "isDisableHunger", false);
      this.isDisableThirst = ConfigUtils.getBooleanFromConfig(var1, "isDisableThirst", false);
      this.isDisableDrunkenness = ConfigUtils.getBooleanFromConfig(var1, "isDisableDrunkenness", false);
      this.isDisableAnger = ConfigUtils.getBooleanFromConfig(var1, "isDisableAnger", false);
      this.isDisableFear = ConfigUtils.getBooleanFromConfig(var1, "isDisableFear", false);
      this.isDisablePain = ConfigUtils.getBooleanFromConfig(var1, "isDisablePain", false);
      this.isDisablePanic = ConfigUtils.getBooleanFromConfig(var1, "isDisablePanic", false);
      this.isDisableMorale = ConfigUtils.getBooleanFromConfig(var1, "isDisableMorale", false);
      this.isDisableStress = ConfigUtils.getBooleanFromConfig(var1, "isDisableStress", false);
      this.isDisableSickness = ConfigUtils.getBooleanFromConfig(var1, "isDisableSickness", false);
      this.isDisableStressFromCigarettes = ConfigUtils.getBooleanFromConfig(var1, "isDisableStressFromCigarettes", false);
      this.isDisableSanity = ConfigUtils.getBooleanFromConfig(var1, "isDisableSanity", false);
      this.isDisableBoredomLevel = ConfigUtils.getBooleanFromConfig(var1, "isDisableBoredomLevel", false);
      this.isDisableUnhappynessLevel = ConfigUtils.getBooleanFromConfig(var1, "isDisableUnhappynessLevel", false);
      this.isDisableWetness = ConfigUtils.getBooleanFromConfig(var1, "isDisableWetness", false);
      this.isDisableInfectionLevel = ConfigUtils.getBooleanFromConfig(var1, "isDisableInfectionLevel", false);
      this.isDisableFakeInfectionLevel = ConfigUtils.getBooleanFromConfig(var1, "isDisableFakeInfectionLevel", false);
      this.isOptimalCalories = ConfigUtils.getBooleanFromConfig(var1, "isOptimalCalories", false);
      this.isOptimalWeight = ConfigUtils.getBooleanFromConfig(var1, "isOptimalWeight", false);
      this.isVisualsEnable = ConfigUtils.getBooleanFromConfig(var1, "isVisualsEnable", false);
      this.isVisualsPlayersEnable = ConfigUtils.getBooleanFromConfig(var1, "isVisualsPlayersEnable", false);
      this.isVisualsVehiclesEnable = ConfigUtils.getBooleanFromConfig(var1, "isVisualsVehiclesEnable", false);
      this.isVisualsZombiesEnable = ConfigUtils.getBooleanFromConfig(var1, "isVisualsZombiesEnable", false);
      this.isVisualDrawToLocalPlayer = ConfigUtils.getBooleanFromConfig(var1, "isVisualDrawToLocalPlayer", false);
      this.isVisualDrawPlayerNickname = ConfigUtils.getBooleanFromConfig(var1, "isVisualDrawPlayerNickname", false);
      this.isVisualDrawCredits = ConfigUtils.getBooleanFromConfig(var1, "isVisualDrawCredits", true);
      this.isVisualDrawPlayerInfo = ConfigUtils.getBooleanFromConfig(var1, "isVisualDrawPlayerInfo", false);
      this.isVisualDrawLineToVehicle = ConfigUtils.getBooleanFromConfig(var1, "isVisualDrawLineToVehicle", false);
      this.isVisualDrawLineToPlayers = ConfigUtils.getBooleanFromConfig(var1, "isVisualDrawLineToPlayers", false);
      this.isVisualEnable360Vision = ConfigUtils.getBooleanFromConfig(var1, "isVisualEnable360Vision", false);
      this.isMapDrawLocalPlayer = ConfigUtils.getBooleanFromConfig(var1, "isMapDrawLocalPlayer", true);
      this.isMapDrawAllPlayers = ConfigUtils.getBooleanFromConfig(var1, "isMapDrawAllPlayers", false);
      this.isMapDrawVehicles = ConfigUtils.getBooleanFromConfig(var1, "isMapDrawVehicles", false);
      this.isMapDrawZombies = ConfigUtils.getBooleanFromConfig(var1, "isMapDrawZombies", false);
   }

   public EtherAPI() {
      this.initStartupConfig();
      EventSubscriber.register(this);
      this.protectionManager = ProtectionManagerX.getInstance();
   }

   @LuaEvents({
           @SubscribeLuaEvent(eventName = "OnResetLua"),
           @SubscribeLuaEvent(eventName = "OnMainMenuEnter")
   })
   public void loadAPI() {
      Logger.printLog("Loading protected EtherAPI...");
      protectionManager.initializeProtection();

      // Initialize protection first
      protectionManager.initializeProtection();

      // Install event protection first
      EventProtector.getInstance().installProtection();

      // Then continue with normal API loading
      if (this.exposer != null) {
         this.exposer.destroy();
      }

      // Use protected exposer with proper SafeEtherLuaMethods instance
      this.exposer = new SafeExposer(LuaManager.converterManager,
              LuaManager.platform,
              LuaManager.env);

      SafeEtherLuaMethods protectedMethods = (SafeEtherLuaMethods) createProtectedMethods();
      this.exposer.exposeAPI(protectedMethods);

      // Additional initialization after protection is in place
      initializeProtectedState();
   }

   private SafeEtherLuaMethods createProtectedMethods() {
      return new SafeEtherLuaMethods() {
         public Object invokeMethod(String name, Object... args) {
            return protectionManager.invokeFunction(name, args);
         }
      };
   }

   // Implement packet handlers
   public void handleNetworkPacket(String command, Map<String, Object> data) {
      protectionManager.handlePacket(command, data);
   }

   private void initializeProtectedState() {
      try {
         if (GameClient.connection != null) {
            // Set connection as validated
            setFieldValue(GameClient.connection);

            // Use wrapper to clear network data
            GameClientWrapper wrapper = GameClientWrapper.get();
            wrapper.clearIncomingNetData();
         }
      } catch (Exception e) {
         Logger.printLog("Error initializing protected state: " + e.getMessage());
      }
   }

   private void clearPendingHandshakes() {
      try {
         // Clear any queued network events
          GameClientWrapper wrapper = GameClientWrapper.get();
          // Get and clear the queue
          ArrayList<ZomboidNetData> netData = wrapper.getIncomingNetData();
          if (netData != null) {
             netData.clear();
          }
      } catch (Exception e) {
         Logger.printLog("Error clearing handshakes: " + e.getMessage());
      }
   }

   private static void setFieldValue(Object obj) {
      try {
         java.lang.reflect.Field field = obj.getClass().getDeclaredField("validated");
         field.setAccessible(true);
         field.set(obj, true);
      } catch (Exception e) {
         Logger.printLog("Error setting field value: " + e.getMessage());
      }
   }

   // Inner class for safe method exposure
   private class SafeExposer extends Exposer {
      public SafeExposer(KahluaConverterManager m, Platform p, KahluaTable e) {
         super(m, (J2SEPlatform) p, e);
      }

      // Override the exposeGlobalFunctions method instead
      public void exposeAPI(EtherLuaMethods methods) {
         for (Method method : methods.getClass().getMethods()) {
            if (method.isAnnotationPresent(se.krka.kahlua.integration.annotations.LuaMethod.class)) {
               String originalName = method.getName();
               String safeName = safeAPI.getSafeName(originalName);
               exposeGlobalFunction(method, safeName);
            }
         }
      }

      private void exposeGlobalFunction(Method method, String name) {
         exposeMethod(method.getDeclaringClass(), method, name, env);
      }
   }

   // Wrapper for Lua methods with protection
   public class SafeEtherLuaMethods extends EtherLuaMethods {
      public Object callMethod(String name, Object... args) {
         String originalName = safeAPI.getOriginalName(name);
         if (originalName != null) {
            try {
               Method method = this.getClass().getMethod(originalName, getParameterTypes(args));
               return method.invoke(this, args);
            } catch (Exception e) {
               Logger.printLog("Error calling method " + originalName + ": " + e.getMessage());
            }
         }
         return null;
      }

      private Class<?>[] getParameterTypes(Object[] args) {
         Class<?>[] types = new Class<?>[args.length];
         for (int i = 0; i < args.length; i++) {
            types[i] = args[i].getClass();
         }
         return types;
      }
   }

   public void resetWeaponsStats() {
      IsoPlayer var1 = IsoPlayer.getInstance();
      if (var1 != null) {
         ArrayList var2 = var1.getInventory().getItems();
         if (var2 != null && !var2.isEmpty()) {
            Iterator var3 = var2.iterator();

            while(true) {
               InventoryItem var4;
               HandWeapon var5;
               do {
                  do {
                     if (!var3.hasNext()) {
                        return;
                     }

                     var4 = (InventoryItem)var3.next();
                  } while(!(var4 instanceof HandWeapon));

                  var5 = (HandWeapon)var4;
               } while(!var4.getStringItemType().equals("RangedWeapon") && !var4.getStringItemType().equals("MeleeWeapon"));

               String var6 = var5.getFullType();
               if (this.originalWeaponStats.containsKey(var6)) {
                  float[] var7 = (float[])this.originalWeaponStats.get(var6);
                  var5.setExtraDamage(var7[0]);
                  var5.setMaxDamage(var7[1]);
                  var5.setMinDamage(var7[2]);
                  var5.setMaxRange(var7[3]);
                  var5.setMinRange(var7[4]);
                  var5.setHitChance((int)var7[5]);
                  var5.setCritDmgMultiplier(var7[6]);
               }
            }
         }
      }
   }

   private void updateLocalPlayerFeatures() {
      IsoPlayer var1 = IsoPlayer.getInstance();
      if (var1 != null) {
         InventoryItem var2 = var1.getPrimaryHandItem();
         HandWeapon var3;
         if (this.isExtraDamage && var2 != null && (var2.getStringItemType().equals("RangedWeapon") || var2.getStringItemType().equals("MeleeWeapon")) && var2 instanceof HandWeapon) {
            var3 = (HandWeapon)var2;
            String var4 = var3.getFullType();
            if (!this.originalWeaponStats.containsKey(var4)) {
               this.originalWeaponStats.put(var4, new float[]{var3.getExtraDamage(), var3.getMaxDamage(), var3.getMinDamage(), var3.getMaxRange(), var3.getMinRange(), (float)var3.getHitChance(), var3.getCritDmgMultiplier()});
            }

            var3.setExtraDamage(100000.0F);
            var3.setMaxDamage(1000000.0F);
            var3.setMinDamage(1000000.0F);
            var3.setMaxRange(10000.0F);
            var3.setMinRange(0.0F);
            var3.setHitChance(100);
            var3.setCritDmgMultiplier(100000.0F);
         }

         if ((Boolean)SandboxOptions.instance.getOptionByName("MultiHitZombies").asConfigOption().getValueAsObject() != this.isMultiHitZombies) {
            SandboxOptions.instance.set("MultiHitZombies", this.isMultiHitZombies);
         }

         if (var1.isTimedActionInstantCheat() != this.isTimedActionCheat) {
            var1.setTimedActionInstantCheat(this.isTimedActionCheat);
         }

         if (var1.isWearingNightVisionGoggles() != this.isEnableNightVision) {
            var1.setWearingNightVisionGoggles(this.isEnableNightVision);
         }

         if (var1.isGodMod() != this.isEnableGodMode) {
            var1.setGodMod(this.isEnableGodMode);
         }

         if (var1.isNoClip() != this.isEnableNoclip) {
            var1.setNoClip(this.isEnableNoclip);
         }

         if (var1.isInvisible() != this.isEnableInvisible) {
            var1.setInvisible(this.isEnableInvisible);
         }

         if (var1.isZombiesDontAttack() != this.isZombieDontAttack) {
            var1.setZombiesDontAttack(this.isZombieDontAttack);
         }

         if (this.isNoRecoil && var2 != null && var2.getStringItemType().equals("RangedWeapon") && var2 instanceof HandWeapon) {
            var3 = (HandWeapon)var2;
            var3.setRecoilDelay(0);
            var3.setCriticalChance(100.0F);
            var3.setAlwaysKnockdown(true);
            var3.setAimingTime(0);
         }

         if (this.isUnlimitedAmmo && var2 != null && var2.getStringItemType().equals("RangedWeapon")) {
            var2.setCurrentAmmoCount(var2.getMaxAmmo());
         }

         if (this.isUnlimitedCondition && var2 != null) {
            if (var2.getHaveBeenRepaired() > 1) {
               var2.setHaveBeenRepaired(1);
            }

            var2.setCondition(var2.getConditionMax());
         }

         if (this.isAutoRepairItems) {
            ArrayList var7 = var1.getInventory().getItems();
            if (var7 != null && !var7.isEmpty()) {
               Iterator var8 = var7.iterator();

               label175:
               while(true) {
                  InventoryItem var5;
                  do {
                     if (!var8.hasNext()) {
                        break label175;
                     }

                     var5 = (InventoryItem)var8.next();
                  } while(var5 == null);

                  if (var5.isBroken()) {
                     var5.setBroken(false);
                  }

                  var5.setHaveBeenRepaired(1);
                  if (var5.getVisual() != null) {
                     for(int var6 = 0; var6 < BloodBodyPartType.MAX.index(); ++var6) {
                        var5.getVisual().removeHole(var6);
                        var5.getVisual().removeDirt();
                        var5.getVisual().removeBlood();
                     }
                  }

                  var5.setWet(false);
                  var5.setInfected(false);
                  var5.setCondition(var5.getConditionMax());
               }
            }
         }

         if (this.isUnlimitedEndurance) {
            var1.getStats().setEndurance(1.0F);
         }

         if (this.isDisableFatigue) {
            var1.getStats().setFatigue(0.0F);
         }

         if (this.isDisableHunger) {
            var1.getStats().setHunger(0.0F);
         }

         if (this.isDisableThirst) {
            var1.getStats().setThirst(0.0F);
         }

         if (this.isDisableDrunkenness) {
            var1.getStats().setDrunkenness(0.0F);
         }

         if (this.isDisableAnger) {
            var1.getStats().setAnger(0.0F);
         }

         if (this.isDisableFear) {
            var1.getStats().setFear(0.0F);
         }

         if (this.isDisablePain) {
            var1.getStats().setPain(0.0F);
         }

         if (this.isDisablePanic) {
            var1.getStats().setPanic(0.0F);
         }

         if (this.isDisableMorale) {
            var1.getStats().setMorale(1.0F);
         }

         if (this.isDisableStress) {
            var1.getStats().setStress(0.0F);
         }

         if (this.isDisableSickness) {
            var1.getStats().setSickness(0.0F);
         }

         if (this.isDisableStressFromCigarettes) {
            var1.getStats().setStressFromCigarettes(0.0F);
         }

         if (this.isDisableSanity) {
            var1.getStats().setSanity(1.0F);
         }

         if (this.isDisableBoredomLevel) {
            var1.getBodyDamage().setBoredomLevel(0.0F);
         }

         if (this.isDisableUnhappynessLevel) {
            var1.getBodyDamage().setUnhappynessLevel(0.0F);
         }

         if (this.isDisableWetness) {
            var1.getBodyDamage().setWetness(0.0F);
         }

         if (this.isDisableInfectionLevel) {
            var1.getBodyDamage().setInfectionLevel(0.0F);
         }

         if (this.isDisableFakeInfectionLevel) {
            var1.getBodyDamage().setFakeInfectionLevel(0.0F);
         }

         if (this.isOptimalCalories) {
            var1.getNutrition().setCalories(1200.0F);
         }

         if (this.isOptimalWeight) {
            var1.getNutrition().setWeight(80.0);
         }

      }
   }

   private void bypassDebugMode() {
      boolean var1 = GameClient.bIngame;
      boolean var2 = ServerOptions.instance.getBoolean("AntiCheatProtectionType12");
      boolean var3 = GameServer.bServer;
      boolean var4 = GameServer.bCoop;
      Core.bDebug = var1 && this.isBypassDebugMode && (!var2 && var3 || var4 || !var3);
   }

   @SubscribeLuaEvent(
      eventName = "OnPostUIDraw"
   )
   public void updateVisuals() {
      try {
         this.updatePlayersVisuals();
         this.updateVehiclesVisuals();
         this.updateZombiesVisuals();
         this.updateUltraPlayerVision();
      } catch (Exception ignored) {
      }

   }

   public void updateUltraPlayerVision() {
      if (this.isVisualEnable360Vision) {
         ArrayList var1 = IsoWorld.instance.getCell().getVehicles();
         if (var1 != null && !var1.isEmpty()) {
            Iterator var2 = var1.iterator();

            while(var2.hasNext()) {
               BaseVehicle var3 = (BaseVehicle)var2.next();
               var3.setAlpha(100.0F);
            }
         }

         ArrayList var6 = IsoWorld.instance.getCell().getZombieList();
         if (var6 != null && !var6.isEmpty()) {
            Iterator var7 = var6.iterator();

            while(var7.hasNext()) {
               IsoZombie var4 = (IsoZombie)var7.next();
               var4.setAlpha(100.0F);
            }
         }

         ArrayList var8 = GameClient.instance.getPlayers();
         if (var8 != null && !var8.isEmpty()) {
            Iterator var9 = var8.iterator();

            while(var9.hasNext()) {
               IsoPlayer var5 = (IsoPlayer)var9.next();
               if (!var5.isLocalPlayer()) {
                  var5.setAlpha(100.0F);
               }
            }
         }

      }
   }

   private void updateVehiclesVisuals() {
      if (this.isVisualsEnable && this.isVisualsVehiclesEnable) {
         IsoPlayer var1 = IsoPlayer.getInstance();
         if (var1 != null) {
            ArrayList var2 = IsoWorld.instance.getCell().getVehicles();
            float var3 = PlayerUtils.getScreenPositionX(var1);
            float var4 = PlayerUtils.getScreenPositionY(var1);
            float var5 = this.vehiclesUIColor.a;
            float var6 = this.vehiclesUIColor.r;
            float var7 = this.vehiclesUIColor.g;
            float var8 = this.vehiclesUIColor.b;
            if (var2 != null || !var2.isEmpty()) {
               Iterator var9 = var2.iterator();

               while(var9.hasNext()) {
                  BaseVehicle var10 = (BaseVehicle)var9.next();
                  float var11 = VehicleUtils.getScreenPositionX(var10);
                  float var12 = VehicleUtils.getScreenPositionY(var10);
                  Rendering.drawTextCenterWithShadow("ID:" + var10.getScriptName(), UIFont.Small, var11, var12, var6, var7, var8, var5);
                  Rendering.drawTextCenterWithShadow(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_VehicleSpeed") + var10.getMaxSpeed(), UIFont.Small, var11, var12 + 10.0F, var6, var7, var8, var5);
                  if (this.isVisualDrawLineToVehicle) {
                     int var13 = (int)PlayerUtils.getDistanceBetweenPlayerAndVehicle(var1, var10);
                     int var14 = Math.max(30, Math.min(150, var13));
                     float var15 = (float)Math.sqrt(Math.pow((double)(var11 - var3), 2.0) + Math.pow((double)(var12 - var4), 2.0));
                     float var16 = (float)var14 / var15;
                     float var17 = var3 + var16 * (var11 - var3);
                     float var18 = var4 + 60.0F + var16 * (var12 - var4);
                     Rendering.drawLine((int)var11, (int)var12, (int)var3, (int)var4 + 60, var6, var7, var8, 0.8F, 1);
                     Rendering.drawTextCenterWithShadow(String.valueOf(var13), UIFont.Small, var17, var18, var6, var7, var8, var5);
                  }
               }

            }
         }
      }
   }

   private void updateZombiesVisuals() {
      if (this.isVisualsEnable && this.isVisualsZombiesEnable) {
         IsoPlayer var1 = IsoPlayer.getInstance();
         if (var1 != null) {
            ArrayList var2 = IsoWorld.instance.getCell().getZombieList();
            float var3 = this.zombiesUIColor.a;
            float var4 = this.zombiesUIColor.r;
            float var5 = this.zombiesUIColor.g;
            float var6 = this.zombiesUIColor.b;
            if (var2 != null || !var2.isEmpty()) {
               Iterator var7 = var2.iterator();

               while(var7.hasNext()) {
                  IsoZombie var8 = (IsoZombie)var7.next();
                  float var9 = ZombieUtils.getScreenPositionX(var8);
                  float var10 = ZombieUtils.getScreenPositionY(var8);
                  int var11 = (int)(var8.getHealth() * 100.0F);
                  Rendering.drawTextCenterWithShadow(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_ZombieTitle"), UIFont.Small, var9, var10, var4, var5, var6, var3);
                  Rendering.drawTextCenterWithShadow(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_ZombieHealth") + var11, UIFont.Small, var9, var10 + 10.0F, var4, var5, var6, var3);
               }

            }
         }
      }
   }

   private void updatePlayersVisuals() {
      if (this.isVisualsEnable && this.isVisualsPlayersEnable) {
         IsoPlayer var1 = IsoPlayer.getInstance();
         if (var1 != null) {
            ArrayList var2 = GameClient.instance.getPlayers();
            float var3 = PlayerUtils.getScreenPositionX(var1);
            float var4 = PlayerUtils.getScreenPositionY(var1);
            float var5 = this.playersUIColor.a;
            float var6 = this.playersUIColor.r;
            float var7 = this.playersUIColor.g;
            float var8 = this.playersUIColor.b;

            if (var2 != null || !var2.isEmpty()) {
               Iterator var9 = var2.iterator();

               while(true) {
                  IsoPlayer var10;
                  float var11;
                  float var12;
                  do {
                     if (!var9.hasNext()) {
                        return;
                     }

                     var10 = (IsoPlayer)var9.next();
                     var11 = PlayerUtils.getScreenPositionX(var10);
                     var12 = PlayerUtils.getScreenPositionY(var10);
                  } while(var10.isLocalPlayer() && !this.isVisualDrawToLocalPlayer);

                  if (this.isVisualDrawPlayerNickname) {
                     Rendering.drawTextCenterWithShadow(var10.getUsername(), UIFont.Small, var11, var12 - 30.0F, var6, var7, var8, var5);
                  }

                  if (this.isVisualDrawPlayerInfo) {
                     String var13 = var10.getPrimaryHandItem() != null ? var10.getPrimaryHandItem().getDisplayName() : "None";
                     String var14 = var10.getSecondaryHandItem() != null ? var10.getSecondaryHandItem().getDisplayName() : "None";
                     Rendering.drawTextCenterWithShadow(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_PrimaryHand") + var13, UIFont.Small, var11, var12 + 70.0F, var6, var7, var8, var5);
                     Rendering.drawTextCenterWithShadow(EtherMain.getInstance().etherTranslator.getTranslate("UI_VisualsDraws_SecondaryHand") + var14, UIFont.Small, var11, var12 + 80.0F, var6, var7, var8, var5);
                  }

                  if (!var10.isLocalPlayer() && this.isVisualDrawLineToPlayers) {
                     // Remove distance check to show all players
                     int var19 = (int)PlayerUtils.getDistanceBetweenPlayers(var10, var1);
                     int var20 = Math.max(30, Math.min(150, var19));
                     float var15 = (float)Math.sqrt(Math.pow((double)(var11 - var3), 2.0) + Math.pow((double)(var12 - var4), 2.0));
                     float var16 = (float)var20 / var15;
                     float var17 = var3 + var16 * (var11 - var3);
                     float var18 = var4 + 60.0F + var16 * (var12 - var4);
                     Rendering.drawLine((int)var11, (int)var12, (int)var3, (int)var4 + 60, var6, var7, var8, 0.8F, 1);
                     Rendering.drawTextCenterWithShadow(String.valueOf(var19), UIFont.Small, var17, var18, var6, var7, var8, var5);
                  }
               }
            }
         }
      }
   }
   @SubscribeLuaEvent(eventName = "OnRenderTick")
   public synchronized void updateAPI() {
      try {
         updateLocalPlayerFeatures();
         bypassDebugMode();
      } catch (Exception e) {
         Logger.printLog("Error in updateAPI: " + e.getMessage());
      }
   }
}
