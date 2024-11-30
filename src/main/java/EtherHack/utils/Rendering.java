package EtherHack.utils;

import zombie.debug.LineDrawer;
import zombie.ui.TextManager;
import zombie.ui.UIFont;

/**
 * Класс Rendering предоставляет функциональность для рендеринга.
 */
public class Rendering {
    public Rendering() {
    }

    /**
     * Рисует текст на экране.
     * @param text текст для отрисовки
     * @param font шрифт для текста
     * @param x координата X для размещения текста
     * @param y координата Y для размещения текста
     * @param r красный компонент цвета текста
     * @param g зеленый компонент цвета текста
     * @param b синий компонент цвета текста
     * @param a альфа-компонент цвета текста
     */
    public static void drawText(String text, UIFont font, float x, float y, float r, float g, float b, float a) {
        TextManager.instance.DrawString(font, x, y, text, r, g, b, a);
    }

    /**
     * Рисует центрированный текст на экране c черной тенью.
     * @param text текст для отрисовки
     * @param font шрифт для текста
     * @param x координата X для размещения текста
     * @param y координата Y для размещения текста
     * @param r красный компонент цвета текста
     * @param g зеленый компонент цвета текста
     * @param b синий компонент цвета текста
     * @param a альфа-компонент цвета текста
     */
    public static void drawTextCenterWithShadow(String text, UIFont font, float x, float y, float r, float g, float b, float a) {
        drawTextCenterWithShadow(text, font, x, y, r, g, b, a, 1.0f);
    }

    /**
     * Рисует центрированный текст на экране c черной тенью.
     * @param text текст для отрисовки
     * @param font шрифт для текста
     * @param x координата X для размещения текста
     * @param y координата Y для размещения текста
     * @param r красный компонент цвета текста
     * @param g зеленый компонент цвета текста
     * @param b синий компонент цвета текста
     * @param a альфа-компонент цвета текста
     * @param thickness толщина обводки
     */
    public static void drawTextCenterWithShadow(String text, UIFont font, float x, float y, float r, float g, float b, float a, float thickness) {
        TextManager.instance.DrawStringCentre(font, x + thickness, y + thickness, text, 0.0f, 0.0f, 0.0f, a);
        TextManager.instance.DrawStringCentre(font, x, y, text, r, g, b, a);
    }

    /**
     * Рисует центрированный текст на экране.
     * @param text текст для отрисовки
     * @param font шрифт для текста
     * @param x координата X для размещения текста
     * @param y координата Y для размещения текста
     * @param r красный компонент цвета текста
     * @param g зеленый компонент цвета текста
     * @param b синий компонент цвета текста
     * @param a альфа-компонент цвета текста
     */
    public static void drawTextCenter(String text, UIFont font, float x, float y, float r, float g, float b, float a) {
        TextManager.instance.DrawStringCentre(font, x, y, text, r, g, b, a);
    }

    /**
     * Рисует линию на экране между заданными точками.
     *
     * @param startX Начальная координата по оси X.
     * @param startY Начальная координата по оси Y.
     * @param endX Конечная координата по оси X.
     * @param endY Конечная координата по оси Y.
     * @param r Компонента красного цвета (от 0.0 до 1.0).
     * @param g Компонента зеленого цвета (от 0.0 до 1.0).
     * @param b Компонента синего цвета (от 0.0 до 1.0).
     * @param a Прозрачность линии (от 0.0 до 1.0).
     */
    public static void drawLine( int startX, int startY, int endX, int endY, float r, float g, float b, float a, int thickness){
        LineDrawer.drawLine(startX, startY, endX, endY, r, g, b, a, thickness);
    }

    /**
     * Рисует окружность на экране с заданными параметрами.
     *
     * @param posX Координата X центра окружности.
     * @param posY Координата Y центра окружности.
     * @param radius Радиус окружности.
     * @param segments Количество сегментов для приближенного построения окружности.
     * @param r Компонента красного цвета (от 0.0 до 1.0) для окружности.
     * @param g Компонента зеленого цвета (от 0.0 до 1.0) для окружности.
     * @param b Компонента синего цвета (от 0.0 до 1.0) для окружности.
     */
    public static void drawCircle(float posX, float posY, float radius, int segments, float r, float g, float b) {
        LineDrawer.drawCircle(posX, posY, radius, segments, r, g, b);
    }

    /**
     * Рисует дугу на экране с заданными параметрами.
     *
     * @param posX Координата X центра дуги.
     * @param posY Координата Y центра дуги.
     * @param thickness Толщина дуги.
     * @param radius Радиус дуги.
     * @param startAngle Начальный угол дуги в радианах.
     * @param endAngle Угол поворота дуги в радианах.
     * @param segments Количество сегментов для приближенного построения дуги.
     * @param r Компонента красного цвета (от 0.0 до 1.0) для дуги.
     * @param g Компонента зеленого цвета (от 0.0 до 1.0) для дуги.
     * @param b Компонента синего цвета (от 0.0 до 1.0) для дуги.
     * @param a Прозрачность дуги (от 0.0 до 1.0).
     */
    public static void drawArc(float posX, float posY, float thickness, float radius, float startAngle, float endAngle, int segments, float r, float g, float b, float a) {
        LineDrawer.drawArc(posX, posY, thickness, radius, startAngle, endAngle, segments, r, g, b, a);
    }

    /**
     * Рисует прямоугольник на экране с заданными параметрами.
     *
     * @param posX Координата X верхнего левого угла прямоугольника.
     * @param posY Координата Y верхнего левого угла прямоугольника.
     * @param width Ширина прямоугольника.
     * @param height Высота прямоугольника.
     * @param r Компонента красного цвета (от 0.0 до 1.0) для прямоугольника.
     * @param g Компонента зеленого цвета (от 0.0 до 1.0) для прямоугольника.
     * @param b Компонента синего цвета (от 0.0 до 1.0) для прямоугольника.
     * @param a Прозрачность прямоугольника (от 0.0 до 1.0).
     * @param thicknessBorder Толщина границы прямоугольника в пикселях.
     */
    public static void drawRect(float posX, float posY, float width, float height, float r, float g, float b, float a, int thicknessBorder) {
        LineDrawer.drawRect(posX, posY, width, height, r, g, b, a, thicknessBorder);
    }
}
