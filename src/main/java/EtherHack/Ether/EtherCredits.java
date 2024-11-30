package EtherHack.Ether;

import EtherHack.annotations.SubscribeLuaEvent;
import EtherHack.utils.EventSubscriber;
import EtherHack.utils.Info;
import EtherHack.utils.Rendering;
import zombie.core.Core;
import zombie.ui.UIFont;

/**
 * Класс EtherCredits отвечает за отображение информации об авторе Ether в игре.
 */
public class EtherCredits {
    public EtherCredits() {
        EventSubscriber.register(this);
    }

    /**
     * Метод draw выполняет отрисовку информации об авторе Ether.
     * Он получает экземпляр Core для доступа к методам рендеринга,
     * получает высоту экрана и использует методы Rendering для отрисовки текста.
     */
    @SubscribeLuaEvent(
            eventName = "OnPreUIDraw"
    )
    public void draw() {
        if (!EtherMain.getInstance().etherAPI.isVisualDrawCredits) return;
        Core coreInstance = Core.getInstance();
        float screenHeight = (float)coreInstance.getScreenHeight();
        Rendering.drawText(Info.CHEAT_CREDITS_TITLE, UIFont.Small, 15.0F, screenHeight - 30.0F, 255.0F, 255.0F, 255.0F, 255.0F);
        Rendering.drawText(Info.CHEAT_CREDITS_AUTHOR, UIFont.Small, 15.0F, screenHeight - 15.0F, 255.0F, 255.0F, 255.0F, 255.0F);
    }
}
