import org.gradle.jvm.tasks.Jar
import java.util.Properties

plugins {
    java
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

fun loadProperties(): Properties {
    return Properties().apply {
        project.file("src/main/resources/EtherHack/EtherHack.properties").inputStream().use {
            load(it)
        }
    }
}

group = "EtherHack"
version = loadProperties().getProperty("version").replace("'", "")

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.36")
    annotationProcessor("org.projectlombok:lombok:1.18.36")
    implementation("org.ow2.asm:asm:9.7.1")
    implementation("org.ow2.asm:asm-tree:9.7.1")
    implementation(files("lib/fmod.jar"))
    implementation(files("lib/zombie.jar"))
    implementation(files("lib/Kahlua.jar"))
    implementation(files("lib/org.jar"))

    tasks.named<Jar>("jar") {
        destinationDirectory.set(project.file("build"))
        archiveFileName.set("EtherHack-${version}.jar")

        manifest {
            attributes["Main-Class"] = "EtherHack.Main"
        }

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from(configurations.runtimeClasspath.get().map { file ->
            if (file.isDirectory) {
                file
            } else {
                zipTree(file)
            }
        })
    }
}