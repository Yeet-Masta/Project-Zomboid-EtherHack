package EtherHack.utils;

import java.util.Properties;
import zombie.core.Color;

public class ConfigUtils {
   public static boolean getBooleanFromConfig(Properties var0, String var1, boolean var2) {
      String var3 = var0.getProperty(var1);
      return var3 != null ? Boolean.parseBoolean(var3) : var2;
   }

   public static Color getColorFromConfig(Properties var0, String var1, Color var2) {
      String var3 = var0.getProperty(var1);
      return var3 != null ? ColorUtils.stringToColor(var3) : var2;
   }
}
