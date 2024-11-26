package EtherHack.utils;

import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;

public class ZombieUtils {
   public static float getScreenPositionX(IsoZombie var0) {
      int var1 = IsoCamera.frameState.playerIndex;
      float var2 = IsoUtils.XToScreen(var0.x, var0.y, var0.getZ(), 0);
      float var3 = Core.getInstance().getZoom(var1);
      var2 -= IsoCamera.getOffX();
      var2 /= var3;
      return var2;
   }

   public static float getScreenPositionY(IsoZombie var0) {
      int var1 = IsoCamera.frameState.playerIndex;
      float var2 = IsoUtils.YToScreen(var0.x, var0.y, var0.getZ(), 0);
      float var3 = Core.getInstance().getZoom(var1);
      var2 -= IsoCamera.getOffY();
      var2 -= (float)(128 / (2 / Core.TileScale));
      var2 /= var3;
      return var2;
   }
}