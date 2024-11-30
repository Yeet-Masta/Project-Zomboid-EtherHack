package EtherHack;

import EtherHack.utils.Logger;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            Logger.print("You must specify one of the '--install' or '--uninstall' flags");
            return;
        }

        GamePatcher gamePatcher = new GamePatcher();
        switch (args[0]) {
            case "--install" -> gamePatcher.patchGame();
            case "--uninstall" -> gamePatcher.restoreFiles();
            default -> Logger.print("Unknown flag " + "'" + args[0] + "'");
        }
    }
}
