package EtherHack.utils;

import EtherHack.Ether.EtherLuaMethods;
import se.krka.kahlua.converter.KahluaConverterManager;
import se.krka.kahlua.integration.expose.LuaJavaClassExposer;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.Platform;

public final class Exposer extends LuaJavaClassExposer {
   public Exposer(KahluaConverterManager var1, se.krka.kahlua.j2se.J2SEPlatform var2, KahluaTable var3) {
      super(var1, var2, var3);
   }

   public void exposeAPI(EtherLuaMethods var1) {
      this.exposeGlobalFunctions(var1);
   }
}
