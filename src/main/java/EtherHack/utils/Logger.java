package EtherHack.utils;

import zombie.debug.DebugLog;

public class Logger {
    /**
     * Вывод текста в консоль
     * @param text Текст для вывода
     */
    public static void print(String text) {
        System.out.println(Info.CHEAT_TAG + text);
    }

    /**
     * Вывод текста в log файл с типом логирования General
     * @param text Текст для вывода
     */
    public static void printLog(String text){
        DebugLog.General.print(Info.CHEAT_TAG + text);
    }

    /**
     * Вывод информации о чите и авторе
     */
    public static void printCredits(){

       System.out.println();
       System.out.println();
       System.out.println("_______ _________          _______  _______           _______  _______  _       ");
       System.out.println("(  ____ \\\\__   __/|\\     /|(  ____ \\(  ____ )|\\     /|(  ___  )(  ____ \\| \\    /\\");
       System.out.println("| (    \\/   ) (   | )   ( || (    \\/| (    )|| )   ( || (   ) || (    \\/|  \\  / /");
       System.out.println("| (__       | |   | (___) || (__    | (____)|| (___) || (___) || |      |  (_/ / ");
       System.out.println("|  __)      | |   |  ___  ||  __)   |     __)|  ___  ||  ___  || |      |   _ (  ");
       System.out.println("| (         | |   | (   ) || (      | (\\ (   | (   ) || (   ) || |      |  ( \\ \\ ");
       System.out.println("| (____/\\   | |   | )   ( || (____/\\| ) \\ \\__| )   ( || )   ( || (____/\\|  /  \\ \\");
       System.out.println("(_______/   )_(   |/     \\|(_______/|/   \\__/|/     \\||/     \\|(_______/|_/    \\/");
       System.out.println();
       System.out.println();
       System.out.println("____  _____  _    _  ____  ____  ____  ____     ____  _  _    _____  __  __  ____  ____  __    ____");
       System.out.println("(  _ \\(  _  )( \\/\\/ )( ___)(  _ \\( ___)(  _ \\   (  _ \\( \\/ )  (  _  )(  )(  )(_   )(_  _)(  )  ( ___)");
       System.out.println(" )___/ )(_)(  )    (  )__)  )   / )__)  )(_) )   ) _ < \\  /    )(_)(  )(__)(  / /_  _)(_  )(__  )__) ");
       System.out.println("(__)  (_____)(__/\\__)(____)(_)\\_)(____)(____/   (____/ (__)   (___/\\\\(______)(____)(____)(____)(____)");
       System.out.println();
       System.out.println();
    }
}
