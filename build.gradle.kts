plugins {
    id("java")
    id("io.freefair.lombok") version "8.6"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.voxcrafterlp"
version = "1.2.3-RELEASE"

repositories {
    mavenCentral()
    maven {
        url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    }
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/Lezurex/github-version-checker")
        credentials {
            username = (project.findProperty("gpr.user") ?: System.getenv("USERNAME")) as String?
            password = (project.findProperty("gpr.key") ?: System.getenv("TOKEN")) as String?
        }
    }
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.21.3-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.36")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("com.lezurex:github-version-checker:1.0.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    tasks.withType<JavaCompile> {
        options.release.set(17)
    }
}

tasks.jar {
    from(sourceSets.main.get().output)
    from(subprojects.map { it.sourceSets.main.get().output })
}

tasks.shadowJar {
    archiveBaseName = "CauldronInteract"
    relocate("org.bstats", "com.voxcrafterlp.cauldroninteract.utils.bstats")
    relocate("com.lezurex.githubversionchecker", "com.voxcrafterlp.cauldroninteract.utils.githubversionchecker")
    relocate("com.google", "com.voxcrafterlp.cauldroninteract.utils.githubversionchecker.google")
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}
