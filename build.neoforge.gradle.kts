plugins {
    id("me.modmuss50.mod-publish-plugin")
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
val uploadVersion = "${property("mod.version")}+$minecraftVersion-neoforge"
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
    file = tasks.named<Jar>("jar").flatMap { it.archiveFile }
    dryRun.set(publishDryRun)
    version = uploadVersion
    displayName = "${property("mod.name")} ${property("mod.version")} - NeoForge $minecraftVersion"
    changelog = providers.fileContents(rootProject.layout.projectDirectory.file("CHANGELOG.md")).asText
    type = when (property("publish.release_type").toString().lowercase()) {
        "stable" -> STABLE
        "beta" -> BETA
        "alpha" -> ALPHA
        else -> throw GradleException("publish.release_type must be stable, beta, or alpha")
    }
    modLoaders.add("neoforge")

    modrinth {
        projectId = modrinthId.ifBlank { "00000000" }
        accessToken = modrinthToken
        compatibleVersions.forEach { minecraftVersions.add(it) }
        environment = CLIENT_AND_SERVER
    }
    curseforge {
        projectId = curseForgeId.ifBlank { "0" }
        accessToken = curseForgeToken
        compatibleVersions.forEach { minecraftVersions.add(it) }
        client = true
        server = true
    }
}
