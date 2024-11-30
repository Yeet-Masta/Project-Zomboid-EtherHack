package EtherHack.utils;

import zombie.core.Color;

import java.util.Properties;

/**
 * Утилитарный класс, содержащий методы, относящиеся к конфигу
 */
public class ConfigUtils {
    /**
     * Получает булево значение из объекта Properties по указанному ключу.
     * @param config       Объект Properties, из которого нужно получить значение.
     * @param key          Ключ, по которому нужно получить булево значение.
     * @param defaultValue Значение по умолчанию, которое будет возвращено, если ключ не найден или его значение некорректно.
     * @return Булево значение, полученное из объекта Properties по указанному ключу, или значение по умолчанию, если ключ не найден или его значение некорректно.
     */
    public static boolean getBooleanFromConfig(Properties config, String key, boolean defaultValue) {
        String valueStr = config.getProperty(key);
        return (valueStr != null) ? Boolean.parseBoolean(valueStr) : defaultValue;
    }
    /**
     * Получает цвет из объекта Properties по указанному ключу.
     * @param config       Объект Properties, из которого нужно получить значение.
     * @param key          Ключ, по которому нужно получить цвет.
     * @param defaultColor Значение по умолчанию, которое будет возвращено, если ключ не найден или его значение некорректно.
     * @return Цвет, полученный из объекта Properties по указанному ключу, или значение по умолчанию, если ключ не найден или его значение некорректно.
     */
    public static Color getColorFromConfig(Properties config, String key, Color defaultColor) {
        String colorStr = config.getProperty(key);
        return (colorStr != null) ? ColorUtils.stringToColor(colorStr) : defaultColor;
    }
}
