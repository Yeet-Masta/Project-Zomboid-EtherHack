package EtherHack.utils;

import EtherHack.Ether.EtherLuaMethods;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.Platform;

/**
 * Класс Exposer предоставляет функциональность для экспонирования API в Lua.
 */
public final class Exposer extends LuaJavaClassExposer {
    /**
     * Создает новый экземпляр класса Exposer.
     * @param converterManager менеджер конвертеров Kahlua
     * @param platform         платформа Kahlua
     * @param env              глобальная таблица Lua
     */
    public Exposer(KahluaConverterManager converterManager, Platform platform, KahluaTable env) {
        super(converterManager, platform, env);
    }
    /**
     * Экспонирует API в Lua.
     * @param api объект, содержащий методы API
     */
    public void exposeAPI(EtherLuaMethods api) {
        exposeGlobalFunctions(api);
    }
}
