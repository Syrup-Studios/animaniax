# Publishing

The `publishMods` task uploads the selected Fabric or NeoForge jar to Modrinth and CurseForge. It uses `CHANGELOG.md` for release notes and `mod.mc_releases` in `stonecutter.properties.yaml` for compatible Minecraft versions. Update the changelog before each release.

Run a dry run with:

```sh
./gradlew :1.21.1-fabric:publishMods :1.21.1-neoforge:publishMods -Ppublish.dry_run=true
```

Set `publish.modrinth` and `publish.curseforge` to the project IDs in `stonecutter.properties.yaml`. Set `MODRINTH_TOKEN` and `CURSEFORGE_TOKEN` as environment variables, or provide `publish.modrinth_token` and `publish.curseforge_token` as Gradle properties. Do not commit tokens. Without both IDs and tokens, the task defaults to a dry run. Set `publish.dry_run=false` to require a live upload.

Set `publish.release_type` to `stable`, `beta`, or `alpha`.
