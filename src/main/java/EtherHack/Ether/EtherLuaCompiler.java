package EtherHack.Ether;

import EtherHack.utils.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class EtherLuaCompiler {
    private static EtherLuaCompiler instance;

    public boolean isBlockCompileDefaultLua = false; // Блокирует компиляцию некоторых стандартных LUA
    public boolean isBlockCompileLuaAboutEtherHack = false; // Блокирует компиляцию файлов с упоминанием EtherHack
    public boolean isBlockCompileLuaWithBadWords = false; // Блокирует компиляцию файлов с упоминанием запрещенных слов
    public ArrayList<String> whiteListPathCompiler = new ArrayList<>();
    public ArrayList<String> blackListWordsEtherUICompiler = new ArrayList<>();
    public String[] blackListWordsCompiler = new String[]{
           "logExploit", "LogExtender", "ISLogSystem", "writeLog", "sendLog", "PARP", "Bikinitools", "AVCS",
            "BTSE", "AntiCheat", "ISPerkLog", "getCore():quitToDesktop()", "KickPlayer", "kickPlayer", "playerKick",
            "PlayerKick", "banPlayer", "PlayerBan", "playerBan", "AnTiCheat"
    };

    public String[] stopDefaultLuaCompile = new String[]{
           "ISPerkLog"
    };

    /**
     * Проверяет, следует ли компилировать Lua-файл, основываясь на его пути и содержимом.
     *
     * @param filePath Путь к файлу, который нужно проверить.
     * @return {@code true}, если файл следует компилировать, иначе {@code false}.
     */
    public boolean isShouldLuaCompile(String filePath) {
        // Проверяем, есть ли путь в белом списке
        for (String pathElement: whiteListPathCompiler) {
            if (pathElement.equals(filePath)) {
                return true;
            }
        }

        // Если путь не в белом списке, читаем содержимое файла
        String content = "";
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder contentBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            content = contentBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            // обрабатываем ошибку чтения файла, возможно, возвращаем false или выбрасываем исключение
        }

        // Проверяем, есть ли слово из черного списка в содержимом
        if (isBlockCompileLuaAboutEtherHack){
            for (String blackListWord: blackListWordsEtherUICompiler) {
                if (content.contains(blackListWord) && filePath.toLowerCase().contains("mod")) {
                    Logger.printLog("File '" + filePath + "' is not allowed to compile. Contains the word: '" + blackListWord + "'");
                    return false;
                }
            }
        }

        if (isBlockCompileLuaWithBadWords) {
            for (String word : blackListWordsCompiler) {
                if (content.contains(word) && filePath.toLowerCase().contains("mod")) {
                    Logger.printLog("File '" + filePath + "' is not allowed to compile. Contains the word: '" + word + "'");
                    return false;
                }
                if (filePath.toLowerCase().contains("mod") && filePath.toLowerCase().contains(word.toLowerCase())) {
                    Logger.printLog("File '" + filePath + "' is not allowed to compile. Contains the word in the file name: '" + word + "'");
                    return false;
                }
            }
        }

        if(isBlockCompileDefaultLua){
            for (String word : stopDefaultLuaCompile) {
                if (filePath.toLowerCase().contains(word.toLowerCase())) {
                    Logger.printLog("File '" + filePath + "' is not allowed to compile. This is a standard logger - disable it!");
                    return false;
                }
            }
        }

        return true;
    }


    /**
     * Добавляет элемент в массив blackListWordsEtherUICompiler, если он там еще не присутствует.
     *
     * @param element элемент, который нужно добавить
     */
    public void addWordToBlacklistLuaCompiler(String element) {
        if (blackListWordsEtherUICompiler.contains(element)) {
            return ;
        }

        blackListWordsEtherUICompiler.add(element);
    }

    /**
     * Добавляет элемент в массив whiteListPathCompiler, если он там еще не присутствует.
     * @param element элемент, который нужно добавить
     */
    public void addPathToWhiteListLuaCompiler(String element) {
        if (whiteListPathCompiler.contains(element)) {
            return ;
        }

        whiteListPathCompiler.add(element);
    }

    /**
     * Инициализирует EtherLuaCompiler.
     */
    public void init() {
        Logger.printLog("Initializing EtherLuaCompiler...");
    }

    /**
     * Возвращает экземпляр класса EtherMain.
     * @return экземпляр класса EtherMain
     */
    public static EtherLuaCompiler getInstance() {
        if (instance == null) {
            instance = new EtherLuaCompiler();
        }

        return instance;
    }

}
