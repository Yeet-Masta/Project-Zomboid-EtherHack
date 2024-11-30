package EtherHack.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
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

/**
 * Класс Patch предоставляет функциональность для внедрения патчей в классы игры.
 */
public class Patch {
    private static final Map<String, ClassNode> classNodeMap = new HashMap<>();

    public Patch() {
    }

    /**
     * Проверяет наличие аннотации Injected в указанном классе.
     * @param filePath    путь к файлу класса
     * @param gameFolder  папка игры
     * @return true, если аннотация Injected присутствует; в противном случае - false
     */
    public static boolean isInjectedAnnotationPresent(String filePath, String gameFolder) {
        Path currentPath = Paths.get("").toAbsolutePath();
        Path classPath = Paths.get(currentPath.toString(), gameFolder, filePath);

        try (FileInputStream fileInputStream = new FileInputStream(classPath.toString())) {
            ClassReader reader = new ClassReader(fileInputStream);
            final boolean[] isAnnotationFound = new boolean[]{false};
            reader.accept(new ClassVisitor(589824) {
                public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                    MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                    return new MethodVisitor(589824, mv) {
                        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                            if (descriptor.equals("LEtherHack/annotations/Injected;")) {
                                isAnnotationFound[0] = true;
                            }

                            return super.visitAnnotation(descriptor, visible);
                        }
                    };
                }
            }, 0);

            return isAnnotationFound[0];
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Добавляет аннотацию Injected в указанный метод класса.
     * @param classNode   узел класса
     * @param methodName  имя метода
     */
    private static void addInjectAnnotation(ClassNode classNode, String methodName) {
        for (MethodNode method : classNode.methods) {
            if (method.name.equals(methodName)) {
                if (method.visibleAnnotations == null) {
                    method.visibleAnnotations = new LinkedList<>();
                } else {
                    for (AnnotationNode annotation : method.visibleAnnotations) {
                        if (annotation.desc.equals("LEtherHack/annotations/Injected;")) {
                            return;
                        }
                    }
                }

                AnnotationNode annotationNode = new AnnotationNode("LEtherHack/annotations/Injected;");
                method.visibleAnnotations.add(annotationNode);
            }
        }
    }

    /**
     * Проверяет наличие аннотации Injected в указанном методе.
     * @param method узел метода
     * @return true, если аннотация Injected присутствует; в противном случае - false
     */
    private static boolean hasInjectedAnnotation(MethodNode method) {
        if (method.visibleAnnotations == null) {
            return false;
        }

        for (AnnotationNode annotation : method.visibleAnnotations) {
            if ("LInjected;".equals(annotation.desc)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Внедряет патч в указанный класс и метод.
     * @param className    имя класса
     * @param methodName   имя метода
     * @param isStatic     флаг, указывающий, является ли метод статическим
     * @param modifyMethod функция для модификации метода
     */
    public static void injectIntoClass(String className, String methodName, boolean isStatic, Consumer<MethodNode> modifyMethod) {
        Logger.print("Injection into a game file '" + className + "' in method: '" + methodName + "'");

        try {
            ClassNode classNode = classNodeMap.getOrDefault(className, new ClassNode());
            if (!classNodeMap.containsKey(className)) {
                ClassReader classReader = new ClassReader(className);
                classReader.accept(classNode, 0);
            }

            for (MethodNode method : classNode.methods) {
                if (method.name.equals(methodName) && Modifier.isStatic(method.access) == isStatic) {
                    if (!hasInjectedAnnotation(method)) {
                        addInjectAnnotation(classNode, methodName);
                    }

                    modifyMethod.accept(method);
                }
            }

            classNodeMap.put(className, classNode);
        } catch (IOException e) {
            Logger.print("An error occurred during injection into '" + className + "': " + e.getMessage());
        }
    }

    /**
     * Сохраняет измененные классы.
     */
    public static void saveModifiedClasses() {
        for (Map.Entry<String, ClassNode> stringClassNodeEntry : classNodeMap.entrySet()) {
            String className = stringClassNodeEntry.getKey();
            ClassNode classNode = stringClassNodeEntry.getValue();

            ClassWriter classWriter = new ClassWriter(3);
            classNode.accept(classWriter);
            byte[] modifiedClass = classWriter.toByteArray();

            try (FileOutputStream fos = new FileOutputStream(className + ".class")) {
                fos.write(modifiedClass);
            } catch (IOException except) {
                Logger.print("An error occurred while writing the modified class '" + className + "': " + except.getMessage());
            }
        }
    }
}
