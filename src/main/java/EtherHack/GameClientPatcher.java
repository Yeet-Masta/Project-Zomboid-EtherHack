package EtherHack;

import EtherHack.utils.Patch;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.ArrayList;
import java.util.function.Consumer;
import EtherHack.utils.Logger;

import static org.objectweb.asm.Opcodes.*;

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
                //{"incomingNetData", "false"}
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
    // In GameClientPatcher.java
    public static void applyPatches() {
        try {
            Logger.printLog("Patching GameClient methods...");

            // Add these specific patches for incomingNetData
            Patch.injectIntoClass("zombie/network/GameClient", "incomingNetData", false, (MethodNode methodNode) -> {
                // Make the field public
                methodNode.access &= ~Opcodes.ACC_PRIVATE;
                methodNode.access &= ~Opcodes.ACC_PROTECTED;
                methodNode.access |= Opcodes.ACC_PUBLIC;

                // Add @Injected annotation
                if (methodNode.visibleAnnotations == null) {
                    methodNode.visibleAnnotations = new ArrayList<>();
                }
                methodNode.visibleAnnotations.add(new AnnotationNode("LEtherHack/annotations/Injected;"));
            });

            // Create getter method if it doesn't exist
            injectGetterMethod("zombie/network/GameClient", "getIncomingNetData",
                    "()Ljava/util/concurrent/ConcurrentLinkedQueue;",
                    (MethodNode methodNode) -> {
                        InsnList instructions = new InsnList();
                        instructions.add(new VarInsnNode(ALOAD, 0));
                        instructions.add(new FieldInsnNode(GETFIELD, "zombie/network/GameClient",
                                "incomingNetData", "Ljava/util/concurrent/ConcurrentLinkedQueue;"));
                        instructions.add(new InsnNode(ARETURN));
                        methodNode.instructions = instructions;
                    });

            // Make sure to patch all other required methods
            //patchMethod("gameLoadingDealWithNetData", true);
            //patchMethod("mainLoopDealWithNetData", true);
            patchMethod("incomingNetData", false);
            // ... other method patches
        } catch (Exception e) {
            Logger.printLog("Failed to patch GameClient: " + e.getMessage());
        }
    }

    private static void injectGetterMethod(String className, String methodName,
                                           String descriptor, Consumer<MethodNode> injector) {
        try {
            Patch.injectIntoClass(className, methodName, false, (MethodNode methodNode) -> {
                methodNode.access = Opcodes.ACC_PUBLIC;
                methodNode.desc = descriptor;
                injector.accept(methodNode);
            });
        } catch (Exception e) {
            Logger.printLog("Failed to inject getter method: " + e.getMessage());
        }
    }
}