plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
}

group = "com.TodoList"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Add SQLite for persistence
    implementation("org.xerial:sqlite-jdbc:3.49.1.0")

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

application {
    mainClass.set("com.TodoList.Launcher")
}

javafx {
    version = "21.0.1"
    modules = listOf("javafx.controls", "javafx.fxml")
}

tasks.test {
    useJUnitPlatform()
}