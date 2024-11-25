package EtherHack.Ether;

import EtherHack.annotations.LuaEvents;
import EtherHack.annotations.SubscribeLuaEvent;
import EtherHack.utils.EventSubscriber;
import EtherHack.utils.Logger;
import java.util.ArrayList;
import zombie.Lua.LuaManager;

public class EtherLuaManager {
   public final String pathToLuaMainFile = "EtherHack/lua/EtherHackMenu.lua";
   public ArrayList luaFilesList = new ArrayList();

   public EtherLuaManager() {
      EventSubscriber.register(this);
   }

   @LuaEvents({@SubscribeLuaEvent(
   eventName = "OnResetLua"
), @SubscribeLuaEvent(
   eventName = "OnMainMenuEnter"
)})
   public void loadLua() {
      Logger.printLog("Loading EtherLua...");
      EtherLuaCompiler.getInstance().addWordToBlacklistLuaCompiler("EtherMain");
      EtherLuaCompiler.getInstance().addPathToWhiteListLuaCompiler("EtherHack/lua/EtherHackMenu.lua");
      LuaManager.RunLua("EtherHack/lua/EtherHackMenu.lua", false);
   }
}
