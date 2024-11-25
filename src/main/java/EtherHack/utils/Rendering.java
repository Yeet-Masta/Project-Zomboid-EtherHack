package EtherHack.utils;

import zombie.debug.LineDrawer;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

public class Rendering {
   public static void drawText(String var0, UIFont var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      TextManager.instance.DrawString(var1, (double)var2, (double)var3, var0, (double)var4, (double)var5, (double)var6, (double)var7);
   }

   public static void drawTextCenterWithShadow(String var0, UIFont var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      drawTextCenterWithShadow(var0, var1, var2, var3, var4, var5, var6, var7, 1.0F);
   }

   public static void drawTextCenterWithShadow(String var0, UIFont var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      TextManager.instance.DrawStringCentre(var1, (double)(var2 + var8), (double)(var3 + var8), var0, 0.0, 0.0, 0.0, (double)var7);
      TextManager.instance.DrawStringCentre(var1, (double)var2, (double)var3, var0, (double)var4, (double)var5, (double)var6, (double)var7);
   }

   public static void drawTextCenter(String var0, UIFont var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      TextManager.instance.DrawStringCentre(var1, (double)var2, (double)var3, var0, (double)var4, (double)var5, (double)var6, (double)var7);
   }

   public static void drawLine(int var0, int var1, int var2, int var3, float var4, float var5, float var6, float var7, int var8) {
      LineDrawer.drawLine((float)var0, (float)var1, (float)var2, (float)var3, var4, var5, var6, var7, var8);
   }

   public static void drawCircle(float var0, float var1, float var2, int var3, float var4, float var5, float var6) {
      LineDrawer.drawCircle(var0, var1, var2, var3, var4, var5, var6);
   }

   public static void drawArc(float var0, float var1, float var2, float var3, float var4, float var5, int var6, float var7, float var8, float var9, float var10) {
      LineDrawer.drawArc(var0, var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   public static void drawRect(float var0, float var1, float var2, float var3, float var4, float var5, float var6, float var7, int var8) {
      LineDrawer.drawRect(var0, var1, var2, var3, var4, var5, var6, var7, var8);
   }
}
