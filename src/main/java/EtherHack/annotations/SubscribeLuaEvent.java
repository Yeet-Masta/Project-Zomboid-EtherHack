package EtherHack.annotations;

import java.lang.annotation.*;

/**
 * Аннотация SubscribeLuaEvent используется для обозначения методов, которые подписаны на определенные события Lua.
 * Эти методы вызываются, когда происходит соответствующее событие Lua.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Repeatable(LuaEvents.class)
public @interface SubscribeLuaEvent {
    /**
     * Возвращает название события Lua, на которое подписан метод.
     * @return Название события Lua.
     */
    String eventName();
}