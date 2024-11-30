package EtherHack.Ether;

import EtherHack.hooks.OnUIElementPreRenderHook;
import EtherHack.hooks.interfaces.IOnUIElementPreRenderListener;
import EtherHack.utils.Info;
import EtherHack.utils.Rendering;
import zombie.core.Core;
import zombie.ui.UIFont;

public class EtherHackCredits implements IOnUIElementPreRenderListener {

    /**
     * Конструктор, реализующий подписку на события OnUIElementPreRender
     */
    public EtherHackCredits() {
        OnUIElementPreRenderHook.addListener(this);
    }

    /**
     * Реализация функционала при вызове хука OnUIElementPreRender
     */
    @Override
    public void onCall() {
        Core coreInstance = Core.getInstance();
        float screenHeight = (float)coreInstance.getScreenHeight();
        Rendering.DrawText(Info.CHEAT_CREDITS_TITLE, UIFont.Small, 15.0f, screenHeight - 30.0f, 255.0f, 255.0f, 255.0f, 255.0f);
        Rendering.DrawText(Info.CHEAT_CREDITS_AUTHOR, UIFont.Small,15.0f, screenHeight - 15.0f, 255.0f, 255.0f, 255.0f, 255.0f);
    }
}
