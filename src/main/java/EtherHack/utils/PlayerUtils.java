package EtherHack.utils;

import zombie.characters.IsoPlayer;
import zombie.characters.IsoZombie;
import zombie.core.Core;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.vehicles.BaseVehicle;

/**
 * Утилитарный класс, содержащий методы, относящиеся к игроку
 */
public class PlayerUtils {
    /**
     * Возвращает расстояние между двумя игроком и транспортом.
     *
     * @param player Игрок (IsoPlayer).
     * @param zombie Транспорт (IsoZombie).
     * @return Расстояние между двумя переданными игроками.
     */
    public static float getDistanceBetweenPlayerAndZombie(IsoPlayer player, IsoZombie zombie) {
        float dx = player.x - zombie.x;
        float dy = player.y - zombie.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Возвращает расстояние между двумя игроком и транспортом.
     *
     * @param player Игрок (IsoPlayer).
     * @param vehicle Транспорт (BaseVehicle).
     * @return Расстояние между двумя переданными игроками.
     */
    public static float getDistanceBetweenPlayerAndVehicle(IsoPlayer player, BaseVehicle vehicle) {
        float dx = player.x - vehicle.x;
        float dy = player.y - vehicle.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Возвращает расстояние между двумя переданными игроками.
     *
     * @param player1 Первый игрок (IsoPlayer).
     * @param player2 Второй игрок (IsoPlayer).
     * @return Расстояние между двумя переданными игроками.
     */
    public static float getDistanceBetweenPlayers(IsoPlayer player1, IsoPlayer player2) {
        float dx = player1.x - player2.x;
        float dy = player1.y - player2.y;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Возвращает позицию игрока на экране по оси X.
     *
     * @param player Объект IsoPlayer, представляющий игрока.
     * @return Позиция игрока на экране по оси X.
     */
    public static float getScreenPositionX(IsoPlayer player) {
        int playerIndex = IsoCamera.frameState.playerIndex;
        float posScreenX = IsoUtils.XToScreen(player.x, player.y, player.getZ(), 0);
        float scale = Core.getInstance().getZoom(playerIndex);
        posScreenX -= IsoCamera.getOffX();
        posScreenX /= scale;

        return posScreenX;
    }

    /**
     * Возвращает позицию игрока на экране по оси Y.
     *
     * @param player Объект IsoPlayer, представляющий игрока.
     * @return Позиция игрока на экране по оси Y.
     */
    public static float getScreenPositionY(IsoPlayer player) {
        int playerIndex = IsoCamera.frameState.playerIndex;
        float posScreenY = IsoUtils.YToScreen(player.x, player.y, player.getZ(), 0);
        float scale = Core.getInstance().getZoom(playerIndex);
        posScreenY -= IsoCamera.getOffY();
        posScreenY -= (float) (128 / (2 / Core.TileScale));
        posScreenY /= scale;

        return posScreenY;
    }
}
