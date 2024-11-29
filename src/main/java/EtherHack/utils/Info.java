package EtherHack.utils;

import java.io.IOException;
import java.util.Properties;

public class Info {
   private static final String CHEAT_VERSION;
   public static final String CHEAT_GUI_TITLE;
   public static final String CHEAT_CREDITS_TITLE;
   public static final String CHEAT_WINDOW_TITLE_SUFFIX;
   public static final String CHEAT_NAME = "EtherHack";
   public static final String CHEAT_AUTHOR = "Yeet-Masta";
   public static final String CHEAT_TAG = "[EtherHack]: ";
   public static final String CHEAT_CREDITS_AUTHOR = "Author: Yeet-Masta";

   static {
      Properties var0 = new Properties();

      try {
         var0.load(Info.class.getClassLoader().getResourceAsStream("EtherHack/EtherHack.properties"));
         CHEAT_VERSION = var0.getProperty("version").replace("'", "");
      } catch (IOException var1) {
         throw new ExceptionInInitializerError("Unable to load version from EtherHack.properties");
      }

      CHEAT_GUI_TITLE = "EtherHack (" + CHEAT_VERSION + ")";
      CHEAT_CREDITS_TITLE = "Patched by EtherHack (" + CHEAT_VERSION + ")";
      CHEAT_WINDOW_TITLE_SUFFIX = " by EtherHack (" + CHEAT_VERSION + ")";
   }
}
