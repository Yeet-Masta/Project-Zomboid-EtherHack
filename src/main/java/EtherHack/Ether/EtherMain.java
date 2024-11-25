package EtherHack.Ether;

import EtherHack.utils.Logger;

public class EtherMain {
   private static EtherMain instance;
   public EtherTranslator etherTranslator;
   public EtherCredits etherCredits;
   public EtherLuaManager etherLuaManager;
   public EtherAPI etherAPI;

   private EtherMain() {
   }

   public void init() {
      Logger.printLog("Initializing EtherHack...");
      this.etherTranslator = new EtherTranslator();
      this.etherTranslator.loadTranslations();
      this.etherCredits = new EtherCredits();
      this.etherAPI = new EtherAPI();
      this.etherAPI.loadAPI();
      this.etherLuaManager = new EtherLuaManager();
      this.etherLuaManager.loadLua();
      Logger.printLog("Initialization EtherHack was completed!");
   }

   public static EtherMain getInstance() {
      if (instance == null) {
         instance = new EtherMain();
      }

      return instance;
   }
}
