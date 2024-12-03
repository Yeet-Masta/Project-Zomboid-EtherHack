package EtherHack;

import EtherHack.utils.Info;
import EtherHack.utils.Logger;
import EtherHack.utils.Patch;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.jar.JarFile;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

/**
 * Класс, отвечающий за установку и удаление чита из кодов игры
 */
public class GamePatcher {

   /**
    * Список всех файлов, подлежащих инъекции
    */
   private final String[] patchFiles = new String[]{
           "GameWindow.class", "inventory/ItemContainer.class", "Lua/LuaEventManager.class", "Lua/LuaManager.class"
   };

   /**
    * Название игровой папки с .class файлами
    */
   private final String gameClassFolder = "zombie";

   /**
    * Папки и файлы, которые нужно экспортировать в корневую директорию игры
    */
   private final String whiteListPathEtherFiles = "EtherHack";

   /**
    * Экспортирование файлов EtherHack в корневую директорию игры
    */
   public void extractEtherHack() {
      try {
         String jarFilePath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
         Path currentDirectory = Paths.get(System.getProperty("user.dir"));

         try (JarFile jarFile = new JarFile(jarFilePath)) {
            jarFile.stream().filter((entry) -> entry.getName().startsWith(whiteListPathEtherFiles))
                    .forEach((entry) -> {
                       try {
                          Path extractPath = currentDirectory.resolve(entry.getName());

                          if (entry.isDirectory()) {
                             Files.createDirectories(extractPath);
                          } else {
                             Files.createDirectories(extractPath.getParent());

                             try (InputStream inputStream = jarFile.getInputStream(entry)) {
                                Files.copy(inputStream, extractPath, StandardCopyOption.REPLACE_EXISTING);
                             }
                          }
                       } catch (IOException e) {
                          e.printStackTrace();
                       }
                    });
            Logger.print("Extraction completed successfully");
         }
      } catch (URISyntaxException | IOException e) {
         e.printStackTrace();
      }
   }


   /**
    *  Удаление всех экспортированных файлов EtherHack из директории игры
    */
   public void uninstallEtherHackFiles() {
      Logger.print("Deleting all EtherHack files...");

      try {
         Path currentDirectory = Paths.get(System.getProperty("user.dir"));
         Path targetPath = currentDirectory.resolve(whiteListPathEtherFiles);
         if (Files.exists(targetPath)) {
            Files.walk(targetPath).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
         }
         Logger.print("Deletion EtherHack files completed successfully");
      } catch (IOException except) {
         except.printStackTrace();
      }

   }

   /**
    * Создает резервные копии игровых файлов, если они еще не существуют.
    * Файлы будут сохранены с расширением .bkup в той же папке, что и оригиналы.
    */
   public void backupGameFiles() {
      Path currentPath = Paths.get("").toAbsolutePath();

      for (int i = 0; i < patchFiles.length; i++) {
         String iteration = "[" + (i + 1) + "/" + patchFiles.length + "]";
         Logger.print("Creating a backup file '" + patchFiles[i] + "' " + iteration);

         Path originalFilePath = Paths.get(currentPath.toString(), gameClassFolder, patchFiles[i]);

         if (Files.exists(originalFilePath)) {
            try {
               Path backupFilePath = Paths.get(originalFilePath + ".bkup");

               if (Files.exists(backupFilePath)) {
                  Logger.print("Backup of the file already exists. Skipping backup.");
               } else {
                  Files.copy(originalFilePath, backupFilePath);
               }
            } catch (IOException e) {
               Logger.print("Error while creating backup file: " + e.getMessage());
            }
         } else {
            Logger.print(patchFiles[i] + " file not found.");
         }
      }

      Logger.print("Backups of game files have been completed!");
   }

   /**
    * Внедрение в файл игрового окна
    */
   public void patchGameWindow() {
      Patch.injectIntoClass("zombie/GameWindow", "InitDisplay", true, (method) -> {
         String oldTitle = "Project Zomboid";
         String newTitle = "Project Zomboid" + Info.CHEAT_WINDOW_TITLE_SUFFIX;
         AbstractInsnNode[] nodes = method.instructions.toArray();

         for (AbstractInsnNode insn : nodes) {
            if (insn instanceof LdcInsnNode ldcInsnNode) {
               if (ldcInsnNode.cst.equals(oldTitle)) {
                  ldcInsnNode.cst = newTitle;
               }
            }
         }

      });
      Patch.injectIntoClass("zombie/GameWindow", "init", true, (method) -> {
         AbstractInsnNode insertionPoint = null;

         // Find the point of injection
         for (AbstractInsnNode insn : method.instructions.toArray()) {
            if (insn instanceof MethodInsnNode) {
               MethodInsnNode methodInsn = (MethodInsnNode) insn;
               if (methodInsn.getOpcode() == Opcodes.INVOKESTATIC
                       && methodInsn.owner.equals("zombie/Lua/LuaManager")
                       && methodInsn.name.equals("init")) {
                  insertionPoint = insn;
                  break;
               }
            }
         }

         if (insertionPoint != null) {
            InsnList initEtherLuaInstructions = new InsnList();
            initEtherLuaInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "EtherHack/Ether/EtherLuaCompiler", "getInstance", "()LEtherHack/Ether/EtherLuaCompiler;", false));
            initEtherLuaInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "EtherHack/Ether/EtherLuaCompiler", "init", "()V", false));
            method.instructions.insert(insertionPoint, initEtherLuaInstructions);
         } else {
            throw new IllegalStateException("Cannot find LuaManager.init() invocation in the method when patching the Game window");
         }
         AbstractInsnNode lastInsn = method.instructions.getLast();
         if (lastInsn != null){
            InsnList initLogoInstructions = new InsnList();
            initLogoInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "EtherHack/Ether/EtherLogo", "getInstance", "()LEtherHack/Ether/EtherLogo;", false));
            initLogoInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "EtherHack/Ether/EtherLogo", "init", "()V", false));
            method.instructions.insertBefore(lastInsn, initLogoInstructions);

            InsnList initEtherInstructions = new InsnList();
            initEtherInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
            initEtherInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "EtherHack/Ether/EtherMain", "init", "()V", false));
            method.instructions.insertBefore(lastInsn, initEtherInstructions);
         } else {
            throw new IllegalStateException("Could not find the end of the method when patching the Game window");
         }
      });

   }

   /**
    * Внедрение в файлы игровых предметов
    */
   public void patchItemContainer() {
      Patch.injectIntoClass("zombie/inventory/ItemContainer", "getWeight", false, (method) -> {
         InsnList newInstructions = new InsnList();
         LabelNode carryOnLabel = new LabelNode();
         newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
         newInstructions.add(new JumpInsnNode(Opcodes.IFNULL, carryOnLabel));
         newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
         newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "EtherHack/Ether/EtherMain", "etherAPI", "LEtherHack/Ether/EtherAPI;"));
         newInstructions.add(new JumpInsnNode(Opcodes.IFNULL, carryOnLabel));
         newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
         newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "EtherHack/Ether/EtherMain", "etherAPI", "LEtherHack/Ether/EtherAPI;"));
         newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "EtherHack/Ether/EtherAPI", "isUnlimitedCarry", "Z"));
         newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, carryOnLabel));
         newInstructions.add(new InsnNode(Opcodes.F_SAME));
         newInstructions.add(new InsnNode(Opcodes.IRETURN));
         newInstructions.add(carryOnLabel);
         method.instructions.insert(newInstructions);
      });
      Patch.injectIntoClass("zombie/inventory/ItemContainer", "getCapacityWeight", false, (method) -> {
         InsnList newInstructions = new InsnList();
         LabelNode carryOnLabel = new LabelNode();
         newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
         newInstructions.add(new JumpInsnNode(Opcodes.IFNULL, carryOnLabel));
         newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
         newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "EtherHack/Ether/EtherMain", "etherAPI", "LEtherHack/Ether/EtherAPI;"));
         newInstructions.add(new JumpInsnNode(Opcodes.IFNULL, carryOnLabel));
         newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
         newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "EtherHack/Ether/EtherMain", "etherAPI", "LEtherHack/Ether/EtherAPI;"));
         newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "EtherHack/Ether/EtherAPI", "isUnlimitedCarry", "Z"));
         newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, carryOnLabel));
         newInstructions.add(new InsnNode(Opcodes.FCONST_0));
         newInstructions.add(new InsnNode(Opcodes.FRETURN));
         newInstructions.add(carryOnLabel);
         method.instructions.insert(newInstructions);
      });
      Patch.injectIntoClass("zombie/inventory/ItemContainer", "getContentsWeight", false, (method) -> {
         InsnList newInstructions = new InsnList();
         LabelNode carryOnLabel = new LabelNode();
         newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
         newInstructions.add(new JumpInsnNode(Opcodes.IFNULL, carryOnLabel));
         newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
         newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "EtherHack/Ether/EtherMain", "etherAPI", "LEtherHack/Ether/EtherAPI;"));
         newInstructions.add(new JumpInsnNode(Opcodes.IFNULL, carryOnLabel));
         newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "EtherHack/Ether/EtherMain", "getInstance", "()LEtherHack/Ether/EtherMain;", false));
         newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "EtherHack/Ether/EtherMain", "etherAPI", "LEtherHack/Ether/EtherAPI;"));
         newInstructions.add(new FieldInsnNode(Opcodes.GETFIELD, "EtherHack/Ether/EtherAPI", "isUnlimitedCarry", "Z"));
         newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, carryOnLabel));
         newInstructions.add(new InsnNode(Opcodes.FCONST_0));
         newInstructions.add(new InsnNode(Opcodes.FRETURN));
         newInstructions.add(carryOnLabel);
         method.instructions.insert(newInstructions);
      });
   }

   /**
    * Внедрение в файл LuaEventManager
    */
   public void patchLuaEventManager() {
      Patch.injectIntoClass("zombie/Lua/LuaEventManager", "triggerEvent", true, (method) -> {
         InsnList toInject = new InsnList();
         toInject.add(new VarInsnNode(Opcodes.ALOAD, 0));
         toInject.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "EtherHack/utils/EventSubscriber", "invokeSubscriber", "(Ljava/lang/String;)V", false));
         method.instructions.insertBefore(method.instructions.get(0), toInject);
      });
   }

   /**
    * Внедрение в файл LuaManager
    */
   public void patchLuaManager() {
      Patch.injectIntoClass("zombie/Lua/LuaManager", "RunLua", true, (method) -> {
         if (!method.desc.equals("(Ljava/lang/String;Z)Ljava/lang/Object;")) {
            return;
         }

         InsnList newInstructions = new InsnList();
         LabelNode endOfMethodLabel = new LabelNode();

         newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "EtherHack/Ether/EtherLuaCompiler", "getInstance", "()LEtherHack/Ether/EtherLuaCompiler;", false));
         newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
         newInstructions.add(new MethodInsnNode(Opcodes.INVOKEVIRTUAL, "EtherHack/Ether/EtherLuaCompiler", "isShouldLuaCompile", "(Ljava/lang/String;)Z", false));

         newInstructions.add(new JumpInsnNode(Opcodes.IFNE, endOfMethodLabel));

         newInstructions.add(new InsnNode(Opcodes.ACONST_NULL));
         newInstructions.add(new InsnNode(Opcodes.ARETURN));

         newInstructions.add(endOfMethodLabel);

         method.instructions.insert(newInstructions);
      });
   }

   /**
    * Проверяет, содержит ли хотя бы один из заданных файлов аннотацию @Injected.
    * @return true, если аннотация @Injected найдена хотя бы в одном файле. false в противном случае.
    */
   public boolean checkInjectedAnnotations() {
      return Arrays.stream(patchFiles)
              .anyMatch(filePath -> Patch.isInjectedAnnotationPresent(filePath, gameClassFolder));
   }

   /**
    * Проверяет наличие игровой папки и определенных файлов внутри.
    * @return true, если игровая папка и все требуемые файлы присутствуют. false в противном случае.
    */
   public boolean isGameFolder() {
      Path gameFolderPath = Paths.get(gameClassFolder);

      // Проверяем, существует ли папка игры
      if (Files.exists(gameFolderPath) && Files.isDirectory(gameFolderPath)) {
         // Если папка существует, проверяем наличие всех необходимых файлов
         return Arrays.stream(patchFiles)
                 .allMatch(fileName -> Files.exists(gameFolderPath.resolve(fileName)));
      }

      return false;
   }

   /**
    * Патчинг игровых bytecode файлов игры
    * для реализации собственного фунционала
    */
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
      Logger.print("Preparation for injection into game file...");

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

   /**
    * Восстановление оригинальных файлов игры
    */
   public void restoreFiles() {
      Logger.printCredits();
      Logger.print("Restoring files...");
      Path currentPath = Paths.get("").toAbsolutePath();

      for(int i = 0; i < patchFiles.length; ++i) {
         String fileName = patchFiles[i];
         String iteration = "[" + (i + 1) + "/" + patchFiles.length + "]";
         Logger.print("Restoring the file '" + fileName + "' " + iteration);
         Path originalFilePath = Paths.get(currentPath.toString(), "zombie", patchFiles[i]);
         Path backupFilePath = Paths.get(originalFilePath.toString() + ".bkup");
         if (Files.exists(backupFilePath)) {
            try {
               if (Files.exists(originalFilePath)) {
                  Files.delete(originalFilePath);
               }

               Files.move(backupFilePath, originalFilePath);
            } catch (IOException e) {
               Logger.print("Error when restoring the game file '" + fileName + "': " + e.getMessage());
            }
         } else {
            Logger.print("Backup file '" + fileName + ".bkup' not found. Skipping restore");
         }
      }

      Logger.print("Files restoration completed!");
      uninstallEtherHackFiles();
   }
}