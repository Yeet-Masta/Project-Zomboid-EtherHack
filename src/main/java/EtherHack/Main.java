package EtherHack;

import EtherHack.utils.Logger;

public class Main {
   public static void main(String[] var0) {
      if (var0.length != 1) {
         Logger.print("You must specify one of the '--install' or '--uninstall' flags");
      } else {
         GamePatcher var1 = new GamePatcher();
         switch (var0[0]) {
            case "--install":
               var1.patchGame();
               break;
            case "--uninstall":
               var1.restoreFiles();
               break;
            default:
               Logger.print("Unknown flag '" + var0[0] + "'");
         }

      }
   }
}
