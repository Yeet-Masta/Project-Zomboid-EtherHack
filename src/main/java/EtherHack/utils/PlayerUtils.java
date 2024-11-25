package EtherHack.utils;

import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.vehicles.BaseVehicle;

public class PlayerUtils {
   public static float getDistanceBetweenPlayerAndZombie(IsoPlayer var0, IsoZombie var1) {
      float var2 = var0.x - var1.x;
      float var3 = var0.y - var1.y;
      return (float)Math.sqrt((double)(var2 * var2 + var3 * var3));
   }

   public static float getDistanceBetweenPlayerAndVehicle(IsoPlayer var0, BaseVehicle var1) {
      float var2 = var0.x - var1.x;
      float var3 = var0.y - var1.y;
      return (float)Math.sqrt((double)(var2 * var2 + var3 * var3));
   }

   public static float getDistanceBetweenPlayers(IsoPlayer var0, IsoPlayer var1) {
      float var2 = var0.x - var1.x;
      float var3 = var0.y - var1.y;
      return (float)Math.sqrt((double)(var2 * var2 + var3 * var3));
   }

   public static float getScreenPositionX(IsoPlayer var0) {
      int var1 = IsoCamera.frameState.playerIndex;
      float var2 = IsoUtils.XToScreen(var0.x, var0.y, var0.getZ(), 0);
      float var3 = Core.getInstance().getZoom(var1);
      var2 -= IsoCamera.getOffX();
      var2 /= var3;
      return var2;
   }

   public static float getScreenPositionY(IsoPlayer var0) {
      int var1 = IsoCamera.frameState.playerIndex;
      float var2 = IsoUtils.YToScreen(var0.x, var0.y, var0.getZ(), 0);
      float var3 = Core.getInstance().getZoom(var1);
      var2 -= IsoCamera.getOffY();
      var2 -= (float)(128 / (2 / Core.TileScale));
      var2 /= var3;
      return var2;
   }
}
