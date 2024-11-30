package EtherHack.Ether;

import EtherHack.hooks.OnUIElementPostRenderHook;
import EtherHack.hooks.interfaces.IOnUIElementPostRenderListener;
import EtherHack.utils.Logger;
import zombie.Lua.LuaManager;

import java.util.concurrent.atomic.AtomicBoolean;

public class EtherInitializer implements IOnUIElementPostRenderListener {
    /**
     * Флаг, был ли загружен API EtherHack
     */
    private AtomicBoolean isAPIInitialized = new AtomicBoolean(false);

    public EtherInitializer(){
        OnUIElementPostRenderHook.addListener(this);
    }

    /**
     * Реализация функционала при вызове хука OnUIElementPostRender
     */
    @Override
    public void onCall() {
        if (isAPIInitialized.compareAndSet(false, true) || (isAPIInitialized.get() && !LuaManager.loaded.contains(EtherMain.getInstance().etherGUI.pathToLuaMainFile))){
            Logger.printLog("Initializing EtherAPI...");
            EtherMain.getInstance().etherAPI.init();
        }

        EtherMain.getInstance().etherGUI.loadLuaUI();
    }
}
