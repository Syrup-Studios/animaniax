plugins {
    id("me.modmuss50.mod-publish-plugin")
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
    maven("https://maven.syrupstudios.net/releases/")
}

dependencies {
    modImplementation("net.syrupstudios:syrup_library:${property("syrup_library_version")}+$minecraftVersion-fabric")
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
        "syrupLibraryVersion" to project.property("syrup_library_version"),
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

val modrinthToken = providers.gradleProperty("publish.modrinth_token")
    .orElse(providers.gradleProperty("MODRINTH_TOKEN"))
    .orElse(providers.environmentVariable("MODRINTH_TOKEN"))
val curseForgeToken = providers.gradleProperty("publish.curseforge_token")
    .orElse(providers.gradleProperty("CURSEFORGE_TOKEN"))
    .orElse(providers.environmentVariable("CURSEFORGE_TOKEN"))
val modrinthId = property("publish.modrinth").toString().trim()
val curseForgeId = property("publish.curseforge").toString().trim()
val publishDryRun = providers.gradleProperty("publish.dry_run").map {
    when (it) {
        "true" -> true
        "false" -> false
        else -> throw GradleException("publish.dry_run must be true or false")
    }
}.orElse(!modrinthToken.isPresent || !curseForgeToken.isPresent || modrinthId.isBlank() || curseForgeId.isBlank())
val uploadVersion = "${property("mod.version")}+$minecraftVersion-fabric"
val compatibleVersions = stonecutter.properties.rawOrNull("mod.mc_releases")
    ?.asList().orEmpty().map { it.toString() }

if (!publishDryRun.get()) {
    if (modrinthId.isBlank() || curseForgeId.isBlank()) {
        throw GradleException("Set publish.modrinth and publish.curseforge in stonecutter.properties.yaml before live publishing")
    }
    if (!modrinthToken.isPresent || !curseForgeToken.isPresent) {
        throw GradleException("Live publishing needs MODRINTH_TOKEN and CURSEFORGE_TOKEN (or Gradle publish.*_token properties)")
    }
}

publishMods {
    file = loomx.modJar.flatMap { it.archiveFile }
    dryRun.set(publishDryRun)
    version = uploadVersion
    displayName = "${property("mod.name")} ${property("mod.version")} - Fabric $minecraftVersion"
    changelog = providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText
    type = when (property("publish.release_type").toString().lowercase()) {
        "stable" -> STABLE
        "beta" -> BETA
        "alpha" -> ALPHA
        else -> throw GradleException("publish.release_type must be stable, beta, or alpha")
    }
    modLoaders.add("fabric")

    modrinth {
        projectId = modrinthId.ifBlank { "00000000" }
        accessToken = modrinthToken
        compatibleVersions.forEach { minecraftVersions.add(it) }
        environment = CLIENT_AND_SERVER
        requires("fabric-api")
    }
    curseforge {
        projectId = curseForgeId.ifBlank { "0" }
        accessToken = curseForgeToken
        compatibleVersions.forEach { minecraftVersions.add(it) }
        client = true
        server = true
        requires("fabric-api")
    }
}
