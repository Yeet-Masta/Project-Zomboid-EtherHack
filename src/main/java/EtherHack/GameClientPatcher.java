package EtherHack;

import EtherHack.utils.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import java.util.function.Consumer;
import EtherHack.utils.Logger;

public class GameClientPatcher {

    /**
     * Patches all specified methods in GameClient
     */
    public static void patch() {
        Logger.printLog("Patching GameClient methods...");

        // Methods to patch with their static status
        String[][] methodsToPatch = {
                {"gameLoadingDealWithNetData", "false"},
                {"mainLoopDealWithNetData", "false"},
                {"mainLoopHandlePacketInternal", "false"},
                {"delayPacket", "false"},
                {"receivePlayerConnect", "true"}, // static method
                {"checkAddedRemovedItems", "false"},
                {"writeItemStats", "false"},
                {"loadResetID", "false"},
                {"saveResetID", "false"},
                {"getPlayerFromUsername", "false"},
                {"initStartupConfig", "false"},
                {"disconnect", "false"},
                {"loadConfig", "false"},
                {"receivePacketCounts", "true"}  // static method
        };

        for (String[] methodInfo : methodsToPatch) {
            patchMethod(methodInfo[0], Boolean.parseBoolean(methodInfo[1]));
        }
    }

    /**
     * Patches a single method using Patch.java utility
     */
    private static void patchMethod(String methodName, boolean isStatic) {
        try {
            Consumer<MethodNode> injector = (MethodNode method) -> {
                Logger.printLog("Modifying access for method: " + methodName);

                // Make method public by removing private/protected and adding public
                int newAccess = method.access;
                newAccess &= ~Opcodes.ACC_PRIVATE;
                newAccess &= ~Opcodes.ACC_PROTECTED;
                newAccess |= Opcodes.ACC_PUBLIC;

                // Keep static if it was static
                if ((method.access & Opcodes.ACC_STATIC) != 0) {
                    newAccess |= Opcodes.ACC_STATIC;
                }

                method.access = newAccess;

                // We don't need to add @Injected annotation as Patch.java handles this
            };

            // Use Patch utility to inject the modifications
            Patch.injectIntoClass("zombie/network/GameClient", methodName, isStatic, injector);
            Logger.printLog("Successfully patched method: " + methodName);

        } catch (Exception e) {
            Logger.printLog("Failed to patch method " + methodName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Call this to apply all patches
     */
    public static void applyPatches() {
        Logger.printLog("Starting GameClient patching process...");
        patch();
        // Patch.saveModifiedClasses() will be called by the main patcher
        Logger.printLog("GameClient patching completed.");
    }
}