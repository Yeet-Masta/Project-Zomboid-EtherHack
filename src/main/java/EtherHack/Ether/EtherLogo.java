package EtherHack.Ether;

import EtherHack.states.EtherLogoState;
import EtherHack.utils.Logger;
import java.util.List;
import zombie.GameWindow;
import zombie.gameStates.GameState;
import zombie.gameStates.TISLogoState;

/**
 * Класс EtherLogo представляет собой синглтон для управления логотипом Ether.
 * Он отвечает за инициализацию и отображение логотипа Ether в игре.
 */
public class EtherLogo {
    private static EtherLogo instance;

    private EtherLogo() {
    }

    /**
     * Инициализирует состояние логотипа EtherHack.
     * Добавляет состояние EtherLogoState в список состояний игры.
     * Если первое состояние является TISLogoState, то устанавливает индекс состояния EtherLogoState на 0.
     * В противном случае выводит сообщение об ошибке.
     */
    public void init() {
        List<GameState> states = GameWindow.states.States;
        GameState tisLogoState = states.get(0);
        if (tisLogoState instanceof TISLogoState) {
            GameWindow.states.States.add(0, new EtherLogoState());
            GameWindow.states.LoopToState = 1;
        } else {
            Logger.printLog("Error when initializing the EtherLogo!");
        }

    }

    /**
     * Получает экземпляр класса EtherLogo.
     * Если экземпляр еще не создан, создает новый экземпляр и возвращает его.
     * В противном случае возвращает существующий экземпляр.
     *
     * @return экземпляр класса EtherLogo
     */
    public static EtherLogo getInstance() {
        if (instance == null) {
            instance = new EtherLogo();
        }

        return instance;
    }
}
