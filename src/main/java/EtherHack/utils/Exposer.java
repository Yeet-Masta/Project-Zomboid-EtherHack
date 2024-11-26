package EtherHack.utils;

import EtherHack.Ether.EtherAPI;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.vm.KahluaTable;

public class Exposer extends LuaJavaClassExposer {
   public Exposer(KahluaConverterManager var1, se.krka.kahlua.j2se.J2SEPlatform var2, KahluaTable var3) {
      super(var1, var2, var3);
   }

   public void exposeAPI(EtherAPI.SafeEtherLuaMethods var1) {
      this.exposeGlobalFunctions(var1);
   }
}
