package EtherHack.Ether;

import EtherHack.hooks.OnUIElementPostRenderHook;
import EtherHack.hooks.interfaces.IOnUIElementPostRenderListener;
import EtherHack.utils.Logger;
import org.lwjglx.input.Keyboard;
import zombie.Lua.LuaManager;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class EtherGUI implements IOnUIElementPostRenderListener {
    /**
     * Кнопка для перезагрузки UI
     */
    private final int keyReload = Keyboard.KEY_HOME;

    /**
     * Была ли нажата кнопка перезагрузки UI
     */
    private AtomicBoolean wasKeyReloadPressed = new AtomicBoolean(false);

    /**
     * Список зависимостей Lua UI
     */
    public ArrayList<String> luaDependenciesList;

    /**
     * Путь до главного файла UI
     */
    public final String pathToLuaMainFile = "EtherHack/lua/EtherHackMenu.lua";

    /**
     * Конструктор EtherGUI
     */
    public EtherGUI() {
        luaDependenciesList = new ArrayList<>();
        OnUIElementPostRenderHook.addListener(this);
    }

    /**
     * Компиляция UI Lua
     */
    public void loadLuaUI(){
        if(!LuaManager.loaded.contains(pathToLuaMainFile)){
            Logger.printLog("Loading EtherHackUI...");
            LuaManager.RunLua(pathToLuaMainFile, false);
        }
    }

    /**
     * Перезагрузка UI
     */
    public void reloadUI() {
        Logger.printLog("Reloading EtherHackUI...");

        Logger.printLog("Removing from LuaManager: " + pathToLuaMainFile);
        LuaManager.loaded.remove(pathToLuaMainFile);
        for (String path: luaDependenciesList ) {
            Logger.printLog("Removing from LuaManager: " + path);
            LuaManager.loaded.remove(path);
        }

        LuaManager.RunLua(pathToLuaMainFile, true);

        Logger.printLog("Reloading EtherHackUI has been completed!");
    }


    /**
     * Реализация функционала при вызове хука OnUIElementPostRender
     */
    @Override
    public void onCall() {
        boolean keyCurrentlyDown = Keyboard.isKeyDown(keyReload);
        if (keyCurrentlyDown && !wasKeyReloadPressed.get()) {
            reloadUI();
        }
        wasKeyReloadPressed.set(keyCurrentlyDown);
    }
}
