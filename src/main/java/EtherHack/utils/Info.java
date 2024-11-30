package EtherHack.utils;

import java.util.Properties;
import java.io.IOException;

public class Info {
    private static final String CHEAT_VERSION;
    public static final String CHEAT_GUI_TITLE;
    public static final String CHEAT_CREDITS_TITLE;
    public static final String CHEAT_WINDOW_TITLE_SUFFIX;
    public static final String CHEAT_NAME = "EtherHack";
    public static final String CHEAT_AUTHOR = "Quzile";
    public static final String CHEAT_TAG = "[" + CHEAT_NAME + "]: ";
    public static final String CHEAT_CREDITS_AUTHOR = "Author: " + CHEAT_AUTHOR;


    static {
        Properties properties = new Properties();
        try {
            properties.load(Info.class.getClassLoader().getResourceAsStream("EtherHack/EtherHack.properties"));
            CHEAT_VERSION = properties.getProperty("version").replace("'", "");
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Unable to load version from EtherHack.properties");
        }
        CHEAT_GUI_TITLE = CHEAT_NAME + " (" + CHEAT_VERSION + ")";
        CHEAT_CREDITS_TITLE = "Patched by " + CHEAT_NAME + " (" + CHEAT_VERSION + ")";
        CHEAT_WINDOW_TITLE_SUFFIX = " by " + CHEAT_NAME + " (" + CHEAT_VERSION + ")";
    }
}
