package EtherHack.hooks;

import EtherHack.hooks.interfaces.IOnUIElementPreRenderListener;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class OnUIElementPreRenderHook {

    /**
     * Список слушателей, ожидающих события от объекта
     */
    private static List<IOnUIElementPreRenderListener> listeners = new ArrayList<>();

    /**
     * Регистрирует новый объект {@code IOnUIElementPreRenderListener} для уведомления о наступлении события в {@code Core}.
     *
     * @param listener объект слушателя для добавления. Не может быть {@code null}.
     * @throws NullPointerException если предоставленный слушатель равен {@code null}.
     */
    public static void addListener(IOnUIElementPreRenderListener listener) {
        if (listener == null) {
            throw new NullPointerException("Listener cannot be null");
        }
        listeners.add(listener);
    }

    /**
     * Уведомляет всех зарегистрированных слушателей о наступлении события в {@code Core}.
     */
    public static void call(){
        for (IOnUIElementPreRenderListener listener : listeners) {
            listener.onCall();
        }
    }
}
