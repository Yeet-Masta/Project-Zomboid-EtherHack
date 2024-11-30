package EtherHack.gameStates;

import zombie.GameTime;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.gameStates.GameState;
import zombie.gameStates.GameStateMachine;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.ui.UIManager;

/**
 * Класс, описывающий состояние логотипа EtherHack
 */
public class EtherLogoState extends GameState {

    /**
     * Текущее значение прозрачности
     */
    private float alpha = 0.0f;

    /**
     * Время отображения логотипа
     */
    private float logoDisplayTime = 40.0f;

    /**
     * Текущая стадия отображения логотипа
     */
    private int stage = 0;

    /**
     * Целевое значение прозрачности
     */
    private float targetAlpha = 0.0f;

    /**
     * Флаг, определяющий необходимость отрисовки
     */
    private boolean noRender = false;

    /**
     * Логотип EtherHack
     */
    private final LogoElement etherLogo = new LogoElement("EtherHack/media/EtherLogo.png");


    /**
     * Конструктор по умолчанию
     */
    public EtherLogoState() {
    }

    /**
     * Метод, вызываемый при входе в состояние
     */
    public void enter() {
        UIManager.bSuspend = true;
        alpha = 0.0f;
        targetAlpha = 1.0f;
    }

    /**
     * Метод, вызываемый при выходе из состояния
     */
    public void exit() {
        UIManager.bSuspend = false;
    }

    /**
     * Метод, выполняющий отрисовку элементов состояния
     */
    public void render() {
        Core core = Core.getInstance();
        if (noRender) {
            core.StartFrameUI();
            SpriteRenderer.instance.renderi(null, 0, 0, core.getOffscreenWidth(0), core.getOffscreenHeight(0), 0.0f, 0.0f, 0.0f, 1.0f, null);
            core.EndFrame();
        } else {
            core.StartFrameUI();
            core.EndFrame();

            boolean tempUseUIFBO = UIManager.useUIFBO;
            UIManager.useUIFBO = false;
            core.StartFrameUI();
            SpriteRenderer.instance.renderi(null, 0, 0, core.getOffscreenWidth(0), core.getOffscreenHeight(0), 0.0f, 0.0f, 0.0f, 1.0f, null);
            etherLogo.centerOnScreen();
            etherLogo.render(alpha);
            core.EndFrameUI();
            UIManager.useUIFBO = tempUseUIFBO;
        }
    }

    /**
     * Метод, выполняющий обновление состояния
     */
    public GameStateMachine.StateAction update() {
        if (Mouse.isLeftDown() || GameKeyboard.isKeyDown(28) || GameKeyboard.isKeyDown(57) || GameKeyboard.isKeyDown(1)) {
            stage = 2;
        }

        GameTime gameTime = GameTime.getInstance();
        switch (stage) {
            case 0 -> {
                targetAlpha = 1.0f;
                if (alpha == 1.0f) {
                    stage = 1;
                }
            }
            case 1 -> {
                logoDisplayTime -= gameTime.getMultiplier() / 1.6f;
                if (logoDisplayTime <= 0.0f) {
                    stage = 2;
                }
            }
            case 2 -> {
                targetAlpha = 0.0f;
                if (alpha == 0.0f) {
                    noRender = true;
                    return GameStateMachine.StateAction.Continue;
                }
            }
        }

        updateAlpha(gameTime);

        return GameStateMachine.StateAction.Remain;
    }

    /**
     * Обновление текущего значения прозрачности.
     * Учитывает текущую стадию отображения логотипа и изменяет значение прозрачности соответственно.
     *
     * @param gameTime объект GameTime для доступа к коэффициенту ускорения времени
     */
    private void updateAlpha(GameTime gameTime) {
        float alphaStep = 0.02f;
        float deltaTime = alphaStep * gameTime.getMultiplier();
        if (alpha < targetAlpha) {
            alpha += deltaTime;
            if (alpha > targetAlpha) {
                alpha = targetAlpha;
            }
        } else if (alpha > targetAlpha) {
            alpha -= deltaTime;
            if (stage == 2) {
                alpha -= deltaTime;
            }
            if (alpha < targetAlpha) {
                alpha = targetAlpha;
            }
        }
    }

    /**
     * Вложенный класс, описывающий элемент логотипа
     */
    private static final class LogoElement {
        private final Texture texture;
        private int x;
        private int y;
        private int width;
        private int height;

        /**
         * Конструктор с параметрами
         * @param texturePath - путь к текстуре логотипа
         */
        LogoElement(String texturePath) {
            texture = Texture.getSharedTexture(texturePath);
            if (texture != null) {
                width = texture.getWidth();
                height = texture.getHeight();
            }
        }

        /**
         * Метод, выравнивающий логотип по центру экрана
         */
        void centerOnScreen() {
            Core core = Core.getInstance();
            x = (core.getScreenWidth() - width) / 2;
            y = (core.getScreenHeight() - height) / 2;
        }

        /**
         * Метод, выполняющий отрисовку логотипа
         * @param alpha - прозрачность логотипа
         */
        void render(float alpha) {
            if (texture != null && texture.isReady()) {
                SpriteRenderer.instance.renderi(texture, x, y, width, height, 1.0f, 1.0f, 1.0f, alpha, null);
            }
        }
    }
}
