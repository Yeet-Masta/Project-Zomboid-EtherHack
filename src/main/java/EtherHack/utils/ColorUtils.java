package EtherHack.utils;

import zombie.core.Color;

public class ColorUtils {
   public static Color stringToColor(String var0) {
      String[] var1 = var0.split(",");
      return new Color(Integer.parseInt(var1[0]), Integer.parseInt(var1[1]), Integer.parseInt(var1[2]));
   }

   public static String colorToString(Color var0) {
      int var10000 = var0.getRed();
      return "" + var10000 + "," + var0.getGreen() + "," + var0.getBlue();
   }
}
