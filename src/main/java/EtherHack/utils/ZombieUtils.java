package EtherHack.utils;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;

/**
 * Утилитарный класс, содержащий методы, относящиеся к зомби
 */
public class ZombieUtils {

    /**
     * Возвращает позицию зомби на экране по оси Y.
     *
     * @param zombie Объект IsoZombie, представляющий зомби.
     * @return Позиция зомби на экране по оси Y.
     */
    public static float getScreenPositionX(IsoZombie zombie) {
        int playerIndex = IsoCamera.frameState.playerIndex;
        float posScreenX = IsoUtils.XToScreen(zombie.x, zombie.y, zombie.getZ(), 0);
        float scale = Core.getInstance().getZoom(playerIndex);
        posScreenX -= IsoCamera.getOffX();
        posScreenX /= scale;

        return posScreenX;
    }

    /**
     * Возвращает позицию зомби на экране по оси Y.
     *
     * @param zombie Объект IsoZombie, представляющий зомби.
     * @return Позиция зомби на экране по оси Y.
     */
    public static float getScreenPositionY(IsoZombie zombie) {
        int playerIndex = IsoCamera.frameState.playerIndex;
        float posScreenY = IsoUtils.YToScreen(zombie.x, zombie.y, zombie.getZ(), 0);
        float scale = Core.getInstance().getZoom(playerIndex);
        posScreenY -= IsoCamera.getOffY();
        posScreenY -= (float) (128 / (2 / Core.TileScale));
        posScreenY /= scale;

        return posScreenY;
    }
}
