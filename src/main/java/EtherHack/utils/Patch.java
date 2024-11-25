package EtherHack.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class Patch {
   private static final Map<String, ClassNode> classNodeMap = new HashMap<>();

   public static void injectIntoClass(String className, String methodName, boolean isStatic, Consumer<MethodNode> injector) {
      Logger.print("Injection into a game file '" + className + "' in method: '" + methodName + "'");

      ClassNode classNode = classNodeMap.computeIfAbsent(className, key -> {
         ClassNode node = new ClassNode();
         try {
            ClassReader reader = new ClassReader(key);
            reader.accept(node, 0);
            return node;
         } catch (IOException e) {
            Logger.print("Failed to read class: " + e.getMessage());
            return null;
         }
      });

      if (classNode == null) {
         throw new RuntimeException("Failed to load class " + className);
      }

      for (MethodNode methodNode : classNode.methods) {
         if (methodNode.name.equals(methodName) && Modifier.isStatic(methodNode.access) == isStatic) {
            if (!hasInjectedAnnotation(methodNode)) {
               addInjectAnnotation(classNode, methodName);
            }
            injector.accept(methodNode);
         }
      }

      classNodeMap.put(className, classNode);
   }

   public static boolean isInjectedAnnotationPresent(String file, String baseDir) {
      Path filePath = Paths.get(baseDir, file);

      try (FileInputStream fis = new FileInputStream(filePath.toString())) {
         ClassReader reader = new ClassReader(fis);
         boolean[] found = new boolean[]{false};

         reader.accept(new ClassVisitor(589824) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
               MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
               return new MethodVisitor(589824, mv) {
                  @Override
                  public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                     if (descriptor.equals("LEtherHack/annotations/Injected;")) {
                        found[0] = true;
                     }
                     return super.visitAnnotation(descriptor, visible);
                  }
               };
            }
         }, 0);

         return found[0];
      } catch (IOException e) {
         Logger.print("Error checking for injected annotations: " + e.getMessage());
         return false;
      }
   }

   private static void addInjectAnnotation(ClassNode classNode, String methodName) {
      for (MethodNode method : classNode.methods) {
         if (method.name.equals(methodName)) {
            if (method.visibleAnnotations == null) {
               method.visibleAnnotations = new LinkedList<>();
            }

            // Check if annotation already exists
            boolean hasAnnotation = method.visibleAnnotations.stream()
                    .anyMatch(anno -> anno.desc.equals("LEtherHack/annotations/Injected;"));

            if (!hasAnnotation) {
               method.visibleAnnotations.add(new AnnotationNode("LEtherHack/annotations/Injected;"));
            }

            return;
         }
      }
   }

   private static boolean hasInjectedAnnotation(MethodNode method) {
      if (method.visibleAnnotations == null) {
         return false;
      }
      return method.visibleAnnotations.stream()
              .anyMatch(anno -> anno.desc.equals("LEtherHack/annotations/Injected;"));
   }

   public static void saveModifiedClasses() {
      for (Map.Entry<String, ClassNode> entry : classNodeMap.entrySet()) {
         String className = entry.getKey();
         ClassNode classNode = entry.getValue();

         try {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(writer);
            byte[] bytes = writer.toByteArray();

            try (FileOutputStream fos = new FileOutputStream(className + ".class")) {
               fos.write(bytes);
            }
         } catch (IOException e) {
            Logger.print("Error saving modified class '" + className + "': " + e.getMessage());
         }
      }
   }
}