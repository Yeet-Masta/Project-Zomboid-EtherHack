package EtherHack.Ether;

import EtherHack.annotations.SubscribeLuaEvent;
import EtherHack.utils.EventSubscriber;
import EtherHack.utils.Logger;
import zombie.Lua.LuaManager;
import java.util.ArrayList;

/**
 * Класс для управления Lua-скриптами в EtherHack.
 */
public class EtherLuaManager {
    public final String pathToLuaMainFile = "EtherHack/lua/EtherHackMenu.lua";
    public ArrayList<String> luaFilesList = new ArrayList<>();

    /**
     * Конструктор класса EtherLuaManager.
     * Регистрирует объект в качестве подписчика событий.
     */
    public EtherLuaManager() {
        EventSubscriber.register(this);
    }

    /**
     * Загрузка пользовательских Lua контекст игры
     */
    @SubscribeLuaEvent(
            eventName = "OnResetLua"
    )
    @SubscribeLuaEvent(
            eventName = "OnMainMenuEnter"
    )
    public void loadLua() {
        Logger.printLog("Loading EtherLua...");

        EtherLuaCompiler.getInstance().addWordToBlacklistLuaCompiler("EtherMain");
        EtherLuaCompiler.getInstance().addPathToWhiteListLuaCompiler(pathToLuaMainFile);

        LuaManager.RunLua(pathToLuaMainFile, false);
    }
}
