package EtherHack.Ether;

import EtherHack.utils.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import se.krka.kahlua.vm.KahluaTable;
import se.krka.kahlua.vm.KahluaTableIterator;
import zombie.core.Translator;

/**
 * Класс EtherTranslator представляет собой переводчик для игры EtherHack.
 */
public class EtherTranslator {
    private static final String TRANSLATIONS_PATH = "EtherHack/translations";
    private Map<String, Map<String, String>> translations;

    /**
     * Создает новый экземпляр класса EtherTranslator и инициализирует его.
     */
    public EtherTranslator() {
        Logger.printLog("Initializing EtherTranslator...");
        translations = new HashMap<>();
    }

    /**
     * Загружает все доступные переводы из файлов.
     */
    public void loadTranslations() {
        File translationsDir = new File(TRANSLATIONS_PATH);
        File[] translationFiles = translationsDir.listFiles((dir, name) -> name.endsWith(".txt"));

        if (translationFiles == null) {
            Logger.printLog("Failed to load translations: no files found.");
            return;
        }

        for (File file : translationFiles) { // Обходим все найденные файлы
            String languageCode = file.getName().replace(".txt", "");
            Map<String, String> languageTranslations = new HashMap<>();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

                String line;
                while((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty() || !line.contains("=")) {
                        continue;
                    }

                    String[] parts = line.split("=", 2);

                    if (parts.length < 2) {
                        continue;
                    }

                    String key = parts[0].trim();
                    String value = parts[1].trim();

                    if (value.endsWith(",")) {
                        value = value.substring(0, value.length() - 1);
                    }

                    value = value.replaceAll("\"", "");
                    languageTranslations.put(key, value);
                }

            } catch (Exception e) {
                Logger.printLog("Failed to load translation file: " + file.getName());
                e.printStackTrace();
            }

            translations.put(languageCode, languageTranslations);
        }
    }

    /**
     * Получает перевод для указанного ключа.
     * @param translationKey ключ перевода
     * @return перевод
     */
    public String getTranslate(String translationKey) {
        return getTranslate(translationKey, (KahluaTable)null);
    }

    /**
     * Получает перевод для указанного ключа с подстановкой аргументов.
     * @param translationKey ключ перевода
     * @param args аргументы для подстановки
     * @return перевод с подстановкой аргументов
     */
    public String getTranslate(String translationKey, KahluaTable args) {
        if (translationKey == null) {
            Logger.printLog("The translation key value was not obtained!");
            return "???";
        }

        String languageCode = Translator.getLanguage().name();
        Map<String, String> languageTranslations = translations.get(languageCode);

        if (languageTranslations == null) {
            Logger.printLog("No translations for language code: " + languageCode);
            languageTranslations = translations.get("EN");
            if (languageTranslations == null) {
                return translationKey;
            }
        }

        String translation = languageTranslations.get(translationKey);

        if (translation == null) {
            Logger.printLog("No translation for key: " + translationKey + " for language: " + languageCode);
            return translationKey;
        }

        if (args != null && !args.isEmpty()) {
            for(KahluaTableIterator iterator = args.iterator(); iterator.advance(); ) {
                String key = iterator.getKey().toString();
                String value = iterator.getValue().toString();
                translation = translation.replace("{" + key + "}", value);
            }
        }

        translation = translation.replace("<br>", "\n");
        return translation;
    }
}
