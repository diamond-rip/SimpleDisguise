plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.8"
}

group = "rip.diamond"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    flatDir { dirs("libs") }
    maven("https://repo.codemc.io/repository/maven-releases/") //PacketEvents
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    implementation("io.github.revxrsal:lamp.common:4.0.0-rc.12")
    implementation("io.github.revxrsal:lamp.bukkit:4.0.0-rc.12")
    compileOnly(files("libs/paper-1.8.8.jar"))
    compileOnly("com.github.retrooper:packetevents-spigot:2.7.0")
}
