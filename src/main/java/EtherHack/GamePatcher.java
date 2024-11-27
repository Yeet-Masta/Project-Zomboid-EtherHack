package EtherHack;

import EtherHack.utils.Info;
import EtherHack.utils.Logger;
import EtherHack.utils.Patch;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import static org.objectweb.asm.Opcodes.*;


public class GamePatcher {
   private final String[] patchFiles = new String[]{
           "GameWindow.class",
           "inventory/ItemContainer.class",
           "Lua/LuaEventManager.class",
           "Lua/LuaManager.class"
   };
   private final String gameClassFolder = "zombie";
   private final String whiteListPathEtherFiles = "EtherHack";

   public void extractEtherHack() {
      try {
         String jarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
         Path gamePath = Paths.get(System.getProperty("user.dir"));

         try (JarFile jarFile = new JarFile(jarPath)) {
            jarFile.stream()
                    .filter(entry -> entry.getName().startsWith(whiteListPathEtherFiles))
                    .forEach(entry -> {
                       try {
                          Path destPath = gamePath.resolve(entry.getName());
                          if (entry.isDirectory()) {
                             Files.createDirectories(destPath);
                          } else {
                             Files.createDirectories(destPath.getParent());
                             try (InputStream is = jarFile.getInputStream(entry)) {
                                Files.copy(is, destPath, StandardCopyOption.REPLACE_EXISTING);
                             }
                          }
                       } catch (IOException e) {
                          Logger.print("Error extracting " + entry.getName() + ": " + e.getMessage());
                       }
                    });
         }
         Logger.print("Extraction completed successfully");
      } catch (IOException | URISyntaxException e) {
         Logger.print("Error during extraction: " + e.getMessage());
      }
   }

   public void uninstallEtherHackFiles() {
      Logger.print("Deleting all EtherHack files...");
      try {
         Path etherPath = Paths.get(System.getProperty("user.dir"), whiteListPathEtherFiles);
         if (Files.exists(etherPath)) {
            Files.walk(etherPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            Logger.print("Deletion of EtherHack files completed successfully");
         } else {
            Logger.print("No EtherHack directory found to delete");
         }
      } catch (IOException e) {
         Logger.print("Error during uninstallation: " + e.getMessage());
      }
   }

   public void backupGameFiles() {
      Path gamePath = Paths.get(System.getProperty("user.dir"));

      for (int i = 0; i < patchFiles.length; i++) {
         String progressMarker = String.format("[%d/%d]", i + 1, patchFiles.length);
         String currentFile = patchFiles[i];
         Logger.print("Creating a backup file '" + currentFile + "' " + progressMarker);

         Path sourcePath = gamePath.resolve(gameClassFolder).resolve(currentFile);
         Path backupPath = Paths.get(sourcePath + ".bkup");

         try {
            if (Files.exists(sourcePath)) {
               if (Files.exists(backupPath)) {
                  Logger.print("Backup of the file already exists. Skipping backup.");
               } else {
                  Files.copy(sourcePath, backupPath);
               }
            } else {
               Logger.print(currentFile + " file not found.");
            }
         } catch (IOException e) {
            Logger.print("Error while creating backup file: " + e.getMessage());
         }
      }
      Logger.print("Backups of game files have been completed!");
   }

   public boolean checkInjectedAnnotations() {
      return Arrays.stream(patchFiles)
              .anyMatch(file -> {
                 try {
                    return Patch.isInjectedAnnotationPresent(file, gameClassFolder);
                 } catch (Throwable e) {
                    Logger.print("Error checking injected annotations: " + e.getMessage());
                    return false;
                 }
              });
   }

   public boolean isGameFolder() {
      Path gameFolderPath = Paths.get(gameClassFolder);
      if (!Files.exists(gameFolderPath) || !Files.isDirectory(gameFolderPath)) {
         return false;
      }
      return Arrays.stream(patchFiles)
              .allMatch(file -> Files.exists(gameFolderPath.resolve(file)));
   }

   public void patchGame() {
      Logger.printCredits();
      Logger.print("Preparing to install the EtherHack...");

      if (!isGameFolder()) {
         Logger.print("No game files were found in this directory. Place the cheat in the root folder of the game");
         return;
      }

      Logger.print("Checking for injections in game files");
      if (checkInjectedAnnotations()) {
         Logger.print("Signs of interference were found in the game files. If you have installed this cheat before, run it with the '--uninstall' flag. Otherwise, check the integrity of the game files via Steam");
         return;
      }

      Logger.print("No signs of injections were found. Preparing for backup...");
      backupGameFiles();

      Logger.print("Preparation for injection into game files...");
      patchGameWindow();
      patchItemContainer();
      patchLuaEventManager();
      patchLuaManager();
      //GameClientPatcher.applyPatches();

      Patch.saveModifiedClasses();
      Logger.print("The injections were completed!");

      Logger.print("Extracting EtherHack files to the current directory...");
      extractEtherHack();

      Logger.print("The cheat installation is complete, you can enter the game!");
   }

   public void restoreFiles() {
      Logger.printCredits();
      Logger.print("Restoring files...");

      Path gamePath = Paths.get(System.getProperty("user.dir"));

      for (int i = 0; i < patchFiles.length; i++) {
         String currentFile = patchFiles[i];
         String progressMarker = String.format("[%d/%d]", i + 1, patchFiles.length);
         Logger.print("Restoring the file '" + currentFile + "' " + progressMarker);

         Path sourcePath = gamePath.resolve(gameClassFolder).resolve(currentFile);
         Path backupPath = Paths.get(sourcePath + ".bkup");

         try {
            if (Files.exists(backupPath)) {
               Files.deleteIfExists(sourcePath);
               Files.move(backupPath, sourcePath);
            } else {
               Logger.print("Backup file '" + currentFile + ".bkup' not found. Skipping restore");
            }
         } catch (IOException e) {
            Logger.print("Error when restoring the game file '" + currentFile + "': " + e.getMessage());
         }
      }

      Logger.print("Files restoration completed!");
      uninstallEtherHackFiles();
   }

   /**
    * Patches the game window class with custom functionality.
    * Injects code into InitDisplay and init methods.
    */
   public void patchGameWindow() {
      try {
         Logger.print("Patching GameWindow class...");

         // Patch InitDisplay method
         boolean initDisplaySuccess = false;
         try {
            Patch.injectIntoClass("zombie/GameWindow", "InitDisplay", true, (MethodNode methodNode) -> {
               // Update window title with cheat version
               String newTitle = "Project Zomboid" + Info.CHEAT_WINDOW_TITLE_SUFFIX;
               methodNode.instructions.forEach(insn -> {
                  if (insn instanceof LdcInsnNode) {
                     LdcInsnNode ldcInsn = (LdcInsnNode) insn;
                     if (ldcInsn.cst.equals("Project Zomboid")) {
                        ldcInsn.cst = newTitle;
                     }
                  }
               });
            });
            initDisplaySuccess = true;
         } catch (Exception e) {
            Logger.print("Failed to patch InitDisplay: " + e.getMessage());
         }

         // Patch init method
         boolean initSuccess = false;
         try {
            Patch.injectIntoClass("zombie/GameWindow", "init", true, (MethodNode methodNode) -> {
               // Initialize EtherHack components after LuaManager initialization
               InsnList injectedCode = new InsnList();
               injectedCode.add(new MethodInsnNode(INVOKESTATIC, "EtherHack/Ether/EtherLuaCompiler", "getInstance", "()LEtherHack/Ether/EtherLuaCompiler;", false));
               injectedCode.add(new MethodInsnNode(INVOKEVIRTUAL, "EtherHack/Ether/EtherLuaCompiler", "init", "()V", false));
               injectedCode.add(new MethodInsnNode(INVOKESTATIC, "EtherHack/Ether/EtherLogo", "getInstance", "()LEtherHack/Ether/EtherLogo;", false));
               injectedCode.add(new MethodInsnNode(INVOKEVIRTUAL, "EtherHack/Ether/EtherLogo", "init", "()V", false));
               injectedCode.add(new MethodInsnNode(INVOKESTATIC, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
               injectedCode.add(new MethodInsnNode(INVOKEVIRTUAL, "EtherHack/Ether/EtherMain", "init", "()V", false));

               // Find injection point after LuaManager.init()
               AbstractInsnNode target = findMethodCall(methodNode, "zombie/Lua/LuaManager", "init");
               if (target != null) {
                  methodNode.instructions.insert(target, injectedCode);
               } else {
                  throw new RuntimeException("Could not find LuaManager.init() call");
               }
            });
            initSuccess = true;
         } catch (Exception e) {
            Logger.print("Failed to patch init: " + e.getMessage());
         }

         if (initDisplaySuccess && initSuccess) {
            Logger.print("Successfully patched GameWindow class");
         } else {
            throw new RuntimeException("Failed to patch GameWindow class");
         }
      } catch (Exception e) {
         Logger.print("Critical error patching GameWindow: " + e.getMessage());
         throw new RuntimeException("GameWindow patch failed", e);
      }
   }

   /**
    * Patches the ItemContainer class to add unlimited carry functionality.
    * Modifies weight calculation methods.
    */
   public void patchItemContainer() {
      try {
         Logger.print("Patching ItemContainer class...");

         String[] methodsToModify = {"getWeight", "getCapacityWeight", "getContentsWeight"};
         boolean allSuccess = true;

         for (String methodName : methodsToModify) {
            try {
               Patch.injectIntoClass("zombie/inventory/ItemContainer", methodName, false, (MethodNode methodNode) -> {
                  // Add unlimited carry weight check
                  InsnList injectedCode = new InsnList();
                  LabelNode normalPath = new LabelNode();

                  // Check if EtherMain instance exists and unlimited carry is enabled
                  injectedCode.add(new MethodInsnNode(INVOKESTATIC, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
                  injectedCode.add(new JumpInsnNode(IFNULL, normalPath));
                  injectedCode.add(new MethodInsnNode(INVOKESTATIC, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
                  injectedCode.add(new FieldInsnNode(GETFIELD, "EtherHack/Ether/EtherMain", "etherAPI", "LEtherHack/Ether/EtherAPI;"));
                  injectedCode.add(new JumpInsnNode(IFNULL, normalPath));
                  injectedCode.add(new MethodInsnNode(INVOKESTATIC, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
                  injectedCode.add(new FieldInsnNode(GETFIELD, "EtherHack/Ether/EtherMain", "etherAPI", "LEtherHack/Ether/EtherAPI;"));
                  injectedCode.add(new FieldInsnNode(GETFIELD, "EtherHack/Ether/EtherAPI", "isUnlimitedCarry", "Z"));
                  injectedCode.add(new JumpInsnNode(IFEQ, normalPath));

                  // Return 0 if unlimited carry is enabled
                  injectedCode.add(new InsnNode(methodName.equals("getWeight") ? ICONST_0 : FCONST_0));
                  injectedCode.add(new InsnNode(methodName.equals("getWeight") ? IRETURN : FRETURN));
                  injectedCode.add(normalPath);

                  methodNode.instructions.insert(injectedCode);
               });
            } catch (Exception e) {
               Logger.print("Failed to patch " + methodName + ": " + e.getMessage());
               allSuccess = false;
            }
         }

         if (allSuccess) {
            Logger.print("Successfully patched ItemContainer class");
         } else {
            throw new RuntimeException("Failed to patch some ItemContainer methods");
         }
      } catch (Exception e) {
         Logger.print("Critical error patching ItemContainer: " + e.getMessage());
         throw new RuntimeException("ItemContainer patch failed", e);
      }
   }

   /**
    * Patches the LuaEventManager to add custom event handling.
    */
   public void patchLuaEventManager() {
      try {
         Logger.print("Patching LuaEventManager class...");

         Patch.injectIntoClass("zombie/Lua/LuaEventManager", "triggerEvent", true, (MethodNode methodNode) -> {
            InsnList injectedCode = new InsnList();

            // Add event subscriber invocation
            injectedCode.add(new VarInsnNode(ALOAD, 0));  // Load event name
            injectedCode.add(new MethodInsnNode(INVOKESTATIC, "EtherHack/utils/EventSubscriber", "invokeSubscriber", "(Ljava/lang/String;)V", false));

            methodNode.instructions.insertBefore(methodNode.instructions.getFirst(), injectedCode);
         });

         Logger.print("Successfully patched LuaEventManager class");
      } catch (Exception e) {
         Logger.print("Critical error patching LuaEventManager: " + e.getMessage());
         throw new RuntimeException("LuaEventManager patch failed", e);
      }
   }

   /**
    * Patches the LuaManager to add custom Lua compilation checks.
    */
   public void patchLuaManager() {
      try {
         Logger.print("Patching LuaManager class...");

         Patch.injectIntoClass("zombie/Lua/LuaManager", "RunLua", true, (MethodNode methodNode) -> {
            if (!methodNode.desc.equals("(Ljava/lang/String;Z)Ljava/lang/Object;")) {
               return;
            }

            InsnList injectedCode = new InsnList();
            LabelNode continueExecution = new LabelNode();

            // Add compilation check
            injectedCode.add(new MethodInsnNode(INVOKESTATIC, "EtherHack/Ether/EtherLuaCompiler", "getInstance", "()LEtherHack/Ether/EtherLuaCompiler;", false));
            injectedCode.add(new VarInsnNode(ALOAD, 0));  // Load file path
            injectedCode.add(new MethodInsnNode(INVOKEVIRTUAL, "EtherHack/Ether/EtherLuaCompiler", "isShouldLuaCompile", "(Ljava/lang/String;)Z", false));
            injectedCode.add(new JumpInsnNode(IFNE, continueExecution));

            // Return null if compilation is not allowed
            injectedCode.add(new InsnNode(ACONST_NULL));
            injectedCode.add(new InsnNode(ARETURN));
            injectedCode.add(continueExecution);

            methodNode.instructions.insert(injectedCode);
         });

         Logger.print("Successfully patched LuaManager class");
      } catch (Exception e) {
         Logger.print("Critical error patching LuaManager: " + e.getMessage());
         throw new RuntimeException("LuaManager patch failed", e);
      }
   }

   /**
    * Helper method to find a method call instruction in a method's instruction list.
    */
   private AbstractInsnNode findMethodCall(MethodNode methodNode, String owner, String name) {
      for (AbstractInsnNode insn : methodNode.instructions) {
         if (insn instanceof MethodInsnNode) {
            MethodInsnNode methodInsn = (MethodInsnNode) insn;
            if (methodInsn.owner.equals(owner) && methodInsn.name.equals(name)) {
               return insn;
            }
         }
      }
      return null;
   }
}