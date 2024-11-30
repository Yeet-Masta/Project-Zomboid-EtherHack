package EtherHack.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Аннотация LuaEvents используется для пометки методов в Java-классах, которые должны быть подписаны на
 * события из сценариев Lua.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface LuaEvents {
    /**
     * Массив аннотаций SubscribeLuaEvent, которые представляют собой события, на которые метод должен быть подписан.
     * @return массив аннотаций SubscribeLuaEvent
     */
    SubscribeLuaEvent[] value();
}
