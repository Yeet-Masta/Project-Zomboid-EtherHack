package EtherHack.utils;

import EtherHack.annotations.SubscribeLuaEvent;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс EventSubscriber предоставляет функциональность для регистрации объектов и вызова методов-подписчиков на события Lua.
 */
public class EventSubscriber {
    private static Map<String, List<AbstractMap.SimpleEntry<Object, Method>>> subscribers = new HashMap<>();

    public EventSubscriber() {
    }

    /**
     * Регистрирует объект в качестве подписчика на события Lua.
     * @param object объект, который будет зарегистрирован в качестве подписчика
     */
    public static void register(Object object) {
        Logger.printLog("Registering a class object and subscribing to Lua events: " + object);

        for (Method method : object.getClass().getMethods()) {
            SubscribeLuaEvent[] annotations = method.getAnnotationsByType(SubscribeLuaEvent.class);

            for (SubscribeLuaEvent annotation : annotations) {
                String eventName = annotation.eventName();
                List<AbstractMap.SimpleEntry<Object, Method>> eventSubscribers =
                        subscribers.computeIfAbsent(eventName, k -> new ArrayList<>());

                eventSubscribers.add(new AbstractMap.SimpleEntry<>(object, method));
            }
        }
    }

    /**
     * Вызывает методы-подписчики для указанного события.
     * @param eventName имя события, для которого вызываются методы-подписчики
     */
    public static void invokeSubscriber(String eventName) {
        List<AbstractMap.SimpleEntry<Object, Method>> eventSubscribers = subscribers.get(eventName);

        if (eventSubscribers == null) {
            return;
        }

        for (AbstractMap.SimpleEntry<Object, Method> subscriber : eventSubscribers) {
            try {
                Method method = subscriber.getValue();
                method.invoke(subscriber.getKey(), (Object[]) null);
            } catch (Exception e) {
                Logger.printLog(String.format("Exception when calling a method '%s' for an event '%s': %s",
                        subscriber.getValue(), eventName, e));
            }
        }
    }

}
