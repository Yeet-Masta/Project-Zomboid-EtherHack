package EtherHack.Ether;

import EtherHack.utils.Logger;

/**
 * Класс EtherMain является точкой входа для инициализации EtherHack.
 */
public class EtherMain {
    private static EtherMain instance;
    public EtherTranslator etherTranslator;
    public EtherCredits etherCredits;
    public EtherLuaManager etherLuaManager;
    public EtherAPI etherAPI;

    /**
     * Предотвращение создания нового экземпляра класса EtherMain из вне.
     */
    private EtherMain() {
    }

    /**
     * Инициализирует EtherHack.
     */
    public void init() {
        Logger.printLog("Initializing EtherHack...");

        etherTranslator = new EtherTranslator();
        etherTranslator.loadTranslations();

        etherCredits = new EtherCredits();

        etherAPI = new EtherAPI();
        etherAPI.loadAPI();

        etherLuaManager = new EtherLuaManager();
        etherLuaManager.loadLua();

        Logger.printLog("Initialization EtherHack was completed!");
    }

    /**
     * Возвращает экземпляр класса EtherMain.
     * @return экземпляр класса EtherMain
     */
    public static EtherMain getInstance() {
        if (instance == null) {
            instance = new EtherMain();
        }

        return instance;
    }
}
