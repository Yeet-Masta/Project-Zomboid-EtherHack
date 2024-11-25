package EtherHack.Ether;

import EtherHack.utils.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.core.Translator;

public class EtherTranslator {
   private static final String TRANSLATIONS_PATH = "EtherHack/translations";
   private Map translations;

   public EtherTranslator() {
      Logger.printLog("Initializing EtherTranslator...");
      this.translations = new HashMap();
   }

   public void loadTranslations() {
      File var1 = new File("EtherHack/translations");
      File[] var2 = var1.listFiles(EtherTranslator::lambda$loadTranslations$0);
      if (var2 == null) {
         Logger.printLog("Failed to load translations: no files found.");
      } else {
         File[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            File var6 = var3[var5];
            String var7 = var6.getName().replace(".txt", "");
            HashMap var8 = new HashMap();

            try {
               BufferedReader var9 = new BufferedReader(new FileReader(var6));

               String var10;
               try {
                  while((var10 = var9.readLine()) != null) {
                     if (!var10.trim().isEmpty() && var10.contains("=")) {
                        String[] var11 = var10.split("=", 2);
                        if (var11.length >= 2) {
                           String var12 = var11[0].trim();
                           String var13 = var11[1].trim();
                           if (var13.endsWith(",")) {
                              var13 = var13.substring(0, var13.length() - 1);
                           }

                           var13 = var13.replaceAll("\"", "");
                           var8.put(var12, var13);
                        }
                     }
                  }
               } catch (Throwable var15) {
                  try {
                     var9.close();
                  } catch (Throwable var14) {
                     var15.addSuppressed(var14);
                  }

                  throw var15;
               }

               var9.close();
            } catch (Exception var16) {
               Logger.printLog("Failed to load translation file: " + var6.getName());
               var16.printStackTrace();
            }

            this.translations.put(var7, var8);
         }
      }

   }

   public String getTranslate(String var1) {
      return this.getTranslate(var1, (KahluaTable)null);
   }

   public String getTranslate(String var1, KahluaTable var2) {
      if (var1 == null) {
         Logger.printLog("The translation key value was not obtained!");
         return "???";
      } else {
         String var3 = Translator.getLanguage().name();
         Map var4 = (Map)this.translations.get(var3);
         if (var4 == null) {
            Logger.printLog("No translations for language code: " + var3);
            var4 = (Map)this.translations.get("EN");
            if (var4 == null) {
               return var1;
            }
         }

         String var5 = (String)var4.get(var1);
         if (var5 == null) {
            Logger.printLog("No translation for key: " + var1 + " for language: " + var3);
            return var1;
         } else {
            String var6;
            String var7;
            if (var2 != null && !var2.isEmpty()) {
               for(KahluaTableIterator var8 = var2.iterator(); var8.advance(); var5 = var5.replace("{" + var6 + "}", var7)) {
                  var6 = var8.getKey().toString();
                  var7 = var8.getValue().toString();
               }
            }

            var5 = var5.replace("<br>", "\n");
            return var5;
         }
      }
   }

   private static boolean lambda$loadTranslations$0(File var0, String var1) {
      return var1.endsWith(".txt");
   }
}
