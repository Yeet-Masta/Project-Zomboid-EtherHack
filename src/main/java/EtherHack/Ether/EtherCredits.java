package EtherHack.Ether;

import EtherHack.annotations.SubscribeLuaEvent;
import EtherHack.utils.EventSubscriber;
import EtherHack.utils.Info;
import EtherHack.utils.Rendering;
import zombie.core.Core;
import zombie.ui.UIFont;

public class EtherCredits {
   public EtherCredits() {
      EventSubscriber.register(this);
   }

   @SubscribeLuaEvent(
      eventName = "OnPreUIDraw"
   )
   public void draw() {
      if (EtherMain.getInstance().etherAPI.isVisualDrawCredits) {
         Core var1 = Core.getInstance();
         float var2 = (float)var1.getScreenHeight();
         Rendering.drawText(Info.CHEAT_CREDITS_TITLE, UIFont.Small, 15.0F, var2 - 30.0F, 255.0F, 255.0F, 255.0F, 255.0F);
         Rendering.drawText("Author: Quzile", UIFont.Small, 15.0F, var2 - 15.0F, 255.0F, 255.0F, 255.0F, 255.0F);
      }

   }
}
