package EtherHack.Ether;

import EtherHack.utils.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class EtherLuaCompiler {
   private static EtherLuaCompiler instance;
   public boolean isBlockCompileDefaultLua = false;
   public boolean isBlockCompileLuaAboutEtherHack = false;
   public boolean isBlockCompileLuaWithBadWords = false;
   public ArrayList whiteListPathCompiler = new ArrayList();
   public ArrayList blackListWordsEtherUICompiler = new ArrayList();
   public String[] blackListWordsCompiler = new String[]{"logExploit", "LogExtender", "ISLogSystem", "writeLog", "sendLog", "PARP", "Bikinitools", "AVCS", "BTSE", "AntiCheat", "ISPerkLog", "getCore():quitToDesktop()", "KickPlayer", "kickPlayer", "playerKick", "PlayerKick", "banPlayer", "PlayerBan", "playerBan", "AnTiCheat"};
   public String[] stopDefaultLuaCompile = new String[]{"ISPerkLog"};

   public boolean isShouldLuaCompile(String var1) {
      Iterator var2 = this.whiteListPathCompiler.iterator();

      while(var2.hasNext()) {
         String var3 = (String)var2.next();
         if (var3.equals(var1)) {
            return true;
         }
      }

      String var10 = "";

      try {
         BufferedReader var11 = new BufferedReader(new FileReader(var1));

         try {
            StringBuilder var4 = new StringBuilder();

            while(true) {
               String var5;
               if ((var5 = var11.readLine()) == null) {
                  var10 = var4.toString();
                  break;
               }

               var4.append(var5).append("\n");
            }
         } catch (Throwable var8) {
            try {
               var11.close();
            } catch (Throwable var7) {
               var8.addSuppressed(var7);
            }

            throw var8;
         }

         var11.close();
      } catch (IOException var9) {
         var9.printStackTrace();
      }

      if (this.isBlockCompileLuaAboutEtherHack) {
         Iterator var12 = this.blackListWordsEtherUICompiler.iterator();

         while(var12.hasNext()) {
            String var14 = (String)var12.next();
            if (var10.contains(var14) && var1.toLowerCase().contains("mod")) {
               Logger.printLog("File '" + var1 + "' is not allowed to compile. Contains the word: '" + var14 + "'");
               return false;
            }
         }
      }

      String var6;
      String[] var13;
      int var15;
      int var16;
      if (this.isBlockCompileLuaWithBadWords) {
         var13 = this.blackListWordsCompiler;
         var15 = var13.length;

         for(var16 = 0; var16 < var15; ++var16) {
            var6 = var13[var16];
            if (var10.contains(var6) && var1.toLowerCase().contains("mod")) {
               Logger.printLog("File '" + var1 + "' is not allowed to compile. Contains the word: '" + var6 + "'");
               return false;
            }

            if (var1.toLowerCase().contains("mod") && var1.toLowerCase().contains(var6.toLowerCase())) {
               Logger.printLog("File '" + var1 + "' is not allowed to compile. Contains the word in the file name: '" + var6 + "'");
               return false;
            }
         }
      }

      if (this.isBlockCompileDefaultLua) {
         var13 = this.stopDefaultLuaCompile;
         var15 = var13.length;

         for(var16 = 0; var16 < var15; ++var16) {
            var6 = var13[var16];
            if (var1.toLowerCase().contains(var6.toLowerCase())) {
               Logger.printLog("File '" + var1 + "' is not allowed to compile. This is a standard logger - disable it!");
               return false;
            }
         }
      }

      return true;
   }

   public void addWordToBlacklistLuaCompiler(String var1) {
      if (!this.blackListWordsEtherUICompiler.contains(var1)) {
         this.blackListWordsEtherUICompiler.add(var1);
      }
   }

   public void addPathToWhiteListLuaCompiler(String var1) {
      if (!this.whiteListPathCompiler.contains(var1)) {
         this.whiteListPathCompiler.add(var1);
      }
   }

   public void init() {
      Logger.printLog("Initializing EtherLuaCompiler...");
   }

   public static EtherLuaCompiler getInstance() {
      if (instance == null) {
         instance = new EtherLuaCompiler();
      }

      return instance;
   }
}
