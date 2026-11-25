plugins {
    id("net.neoforged.moddev")
}

val minecraftVersion = stonecutter.current.version
val modId = property("mod.id") as String
val modVersion = "${property("mod.version")}+$minecraftVersion-neoforge"

group = property("mod.group") as String
version = modVersion
base.archivesName = modId

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

neoForge {
    version = property("deps.neoforge") as String
    runs {
        create("client") { client(); gameDirectory = rootProject.file("run") }
        create("server") { server(); gameDirectory = rootProject.file("run") }
    }
    mods.create(modId) { sourceSet(sourceSets.main.get()) }
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
        "minecraftRange" to project.property("mod.neoforge_mc_range"),
        "neoforge" to project.property("deps.neoforge"),
        "authors" to project.property("mod.authors"),
    )
    inputs.properties(values)
    exclude("fabric.mod.json")
    filesMatching("META-INF/neoforge.mods.toml") { expand(values) }
}
