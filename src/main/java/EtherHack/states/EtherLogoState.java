package EtherHack.states;

import zombie.GameTime;
import zombie.core.Core;
import zombie.core.SpriteRenderer;
import zombie.core.textures.Texture;
import zombie.gameStates.GameState;
import zombie.gameStates.GameStateMachine;
import zombie.gameStates.GameStateMachine.StateAction;
import zombie.input.GameKeyboard;
import zombie.input.Mouse;
import zombie.ui.UIManager;

/**
 * Класс EtherLogoState представляет состояние отображения логотипа EtherHack.
 */
public class EtherLogoState extends GameState {
    private float alpha = 0.0F;
    private float logoDisplayTime = 40.0F;
    private int stage = 0;
    private float targetAlpha = 0.0F;
    private boolean noRender = false;
    private final LogoElement etherLogo = new LogoElement("EtherHack/media/EtherLogo.png");

    /**
     * Создает новый экземпляр класса EtherLogoState.
     */
    public EtherLogoState() {
    }

    /**
     * Вызывается при входе в состояние.
     */
    public void enter() {
        UIManager.bSuspend = true;
        alpha = 0.0F;
        targetAlpha = 1.0F;
    }

    /**
     * Вызывается при выходе из состояния.
     */
    public void exit() {
        UIManager.bSuspend = false;
    }

    /**
     * Отрисовывка логотипа.
     */
    public void render() {
        Core core = Core.getInstance();
        if (noRender) {
            core.StartFrameUI();
            SpriteRenderer.instance.renderi(null, 0, 0, core.getOffscreenWidth(0), core.getOffscreenHeight(0), 0.0F, 0.0F, 0.0F, 1.0F, null);
            core.EndFrame();
        } else {
            core.StartFrameUI();
            core.EndFrame();
            boolean tempUseUIFBO = UIManager.useUIFBO;
            UIManager.useUIFBO = false;
            core.StartFrameUI();
            SpriteRenderer.instance.renderi(null, 0, 0, core.getOffscreenWidth(0), core.getOffscreenHeight(0), 0.0F, 0.0F, 0.0F, 1.0F, null);
            etherLogo.centerOnScreen();
            etherLogo.render(alpha);
            core.EndFrameUI();
            UIManager.useUIFBO = tempUseUIFBO;
        }

    }

    /**
     * Обновляет состояние.
     * @return действие, определяющее дальнейшее поведение игры
     */
    public GameStateMachine.StateAction update() {
        if (Mouse.isLeftDown() || GameKeyboard.isKeyDown(28) || GameKeyboard.isKeyDown(57) || GameKeyboard.isKeyDown(1)) {
            stage = 2;
        }

        GameTime gameTime = GameTime.getInstance();
        switch (stage) {
            case 0 -> {
                targetAlpha = 1.0F;
                if (alpha == 1.0F) {
                    stage = 1;
                }
            }
            case 1 -> {
                logoDisplayTime -= gameTime.getMultiplier() / 1.6F;
                if (logoDisplayTime <= 0.0F) {
                    stage = 2;
                }
            }
            case 2 -> {
                targetAlpha = 0.0F;
                if (alpha == 0.0F) {
                    noRender = true;
                    return StateAction.Continue;
                }
            }
        }

        updateAlpha(gameTime);
        return StateAction.Remain;
    }

    /**
     * Обновляет прозрачность логотипа.
     * @param gameTime время игры
     */
    private void updateAlpha(GameTime gameTime) {
        float alphaStep = 0.02F;
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
     * Внутренний класс, представляющий элемент логотипа.
     */
    private static final class LogoElement {
        private final Texture texture;
        private int x;
        private int y;
        private int width;
        private int height;

        /**
         * Создает новый экземпляр класса LogoElement с указанным путем текстуры.
         * @param texturePath путь к текстуре элемента
         */
        LogoElement(String texturePath) {
            texture = Texture.getSharedTexture(texturePath);
            if (texture != null) {
                width = texture.getWidth();
                height = texture.getHeight();
            }

        }

        /**
         * Выравнивает элемент по центру экрана.
         */
        void centerOnScreen() {
            Core core = Core.getInstance();
            x = (core.getScreenWidth() - width) / 2;
            y = (core.getScreenHeight() - height) / 2;
        }

        /**
         * Отрисовывает элемент с указанной прозрачностью.
         * @param alpha прозрачность элемента
         */
        void render(float alpha) {
            if (texture != null && texture.isReady()) {
                SpriteRenderer.instance.renderi(texture, x, y, width, height, 1.0F, 1.0F, 1.0F, alpha, null);
            }

        }
    }
}
