plugins {
    id("dev.kikugie.loom-back-compat")
}

val minecraftVersion = stonecutter.current.version
val modId = property("mod.id") as String
val modVersion = "${property("mod.version")}+$minecraftVersion-fabric"

group = property("mod.group") as String
version = modVersion
base.archivesName = modId

repositories {
    maven("https://maven.fabricmc.net/")
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")
    loomx.applyMojangMappings()
    modImplementation("net.fabricmc:fabric-loader:${property("deps.fabric_loader")}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${property("deps.fabric_api")}")
    include(modApi("teamreborn:energy:4.1.0")!!)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

loom {
    fabricModJsonPath = rootProject.file("src/main/resources/fabric.mod.json")
    runConfigs.configureEach {
        generateRunConfig = true
        runDirectory = rootProject.file("run")
    }
}

tasks.withType<Jar>().configureEach {
    from(rootProject.file("LICENSE.md")) { into("META-INF") }
    from(rootProject.file("LICENSES")) { into("META-INF/LICENSES") }
}

tasks.processResources {
    val values = mapOf(
        "modId" to modId,
        "version" to modVersion,
        "modName" to project.property("mod.name"),
        "description" to project.property("mod.description"),
        "license" to project.property("mod.license"),
        "homepage" to project.property("mod.homepage"),
        "issues" to project.property("mod.issues"),
        "sources" to project.property("mod.sources"),
        "minecraft" to minecraftVersion,
        "loader" to project.property("deps.fabric_loader"),
        "fabricApi" to project.property("deps.fabric_api"),
        "authors" to project.property("mod.authors"),
    )
    inputs.properties(values)
    exclude("META-INF/neoforge.mods.toml")
    filesMatching("fabric.mod.json") { expand(values) }
}
