package EtherHack.utils;

import zombie.core.Color;

/**
 * Утилитарный класс, содержащий методы, относящиеся к цвету
 */
public class ColorUtils {
    /**
     * Преобразует строку в объект Color.
     *
     * @param str Строка, представляющая цвет в формате "R,G,B", где R, G и B - значения красного, зеленого и синего цветов соответственно.
     * @return Объект Color, представляющий цвет, полученный из переданной строки.
     */
    public static Color stringToColor(String str) {
        String[] rgb = str.split(",");
        return new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), Integer.parseInt(rgb[2]));
    }

    /**
     * Преобразует объект Color в строку.
     *
     * @param color Объект Color, который нужно преобразовать в строку.
     * @return Строка, представляющая цвет в формате "R,G,B", где R, G и B - значения красного, зеленого и синего цветов соответственно.
     */
    public static String colorToString(Color color) {
        return color.getRed() + "," + color.getGreen() + "," + color.getBlue();
    }
}
