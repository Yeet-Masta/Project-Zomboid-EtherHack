package EtherHack.utils;
import zombie.core.Core;
import zombie.iso.IsoCamera;
import zombie.iso.IsoUtils;
import zombie.vehicles.BaseVehicle;

/**
 * Утилитарный класс, содержащий методы, относящиеся к транспорту
 */
public class VehicleUtils {

    /**
     * Возвращает позицию транспорта на экране по оси Y.
     *
     * @param vehicle Объект BaseVehicle, представляющий транспорт.
     * @return Позиция транспорта на экране по оси Y.
     */
    public static float getScreenPositionX(BaseVehicle vehicle) {
        int playerIndex = IsoCamera.frameState.playerIndex;
        float posScreenX = IsoUtils.XToScreen(vehicle.x, vehicle.y, vehicle.getZ(), 0);
        float scale = Core.getInstance().getZoom(playerIndex);
        posScreenX -= IsoCamera.getOffX();
        posScreenX /= scale;

        return posScreenX;
    }

    /**
     * Возвращает позицию транспорта на экране по оси Y.
     *
     * @param vehicle Объект BaseVehicle, представляющий транспорт.
     * @return Позиция транспорта на экране по оси Y.
     */
    public static float getScreenPositionY(BaseVehicle vehicle) {
        int playerIndex = IsoCamera.frameState.playerIndex;
        float posScreenY = IsoUtils.YToScreen(vehicle.x, vehicle.y, vehicle.getZ(), 0);
        float scale = Core.getInstance().getZoom(playerIndex);
        posScreenY -= IsoCamera.getOffY();
        posScreenY -= (float) (128 / (2 / Core.TileScale));
        posScreenY /= scale;

        return posScreenY;
    }
}
