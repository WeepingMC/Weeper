# Weeper

**Relaxed like weed, stable like Paper.**

[![Build](https://github.com/WeepingMC/Weeper/actions/workflows/build.yml/badge.svg)](https://github.com/WeepingMC/Weeper/actions/workflows/build.yml)

Weeper is a [Paper](https://papermc.io/) fork for Minecraft, built with [paperweight](https://github.com/PaperMC/paperweight). It layers extra patches, API additions, and quality-of-life changes on top of upstream Paper while staying close enough to track it easily.

Currently targeting Minecraft **26.3**.

## Modules

| Module | Description |
|---|---|
| `weeper-api` | Weeper's fork of the Paper API, with Weeper-specific patches |
| `weeper-server` | Weeper's fork of the Paper server, with Weeper-specific patches |
| `weeper-generator` | Fork of the Paper API generator tooling |
| `weeper-checkstyle` | Fork of Paper's checkstyle configuration |

Upstream Paper sources (`paper-api`, `paper-server`, `paper-generator`, `paper-checkstyle`) are pulled in automatically by paperweight and patched to produce the `weeper-*` modules above — you normally don't edit them directly.

## Getting started

Requires JDK 25.

```bash
git clone https://github.com/WeepingMC/Weeper.git
cd Weeper
./gradlew applyAllPatches
```

Run `applyAllPatches` before importing the project into your IDE.

### Run a dev server

```bash
./gradlew runDevServer
```

Connect to `localhost:25565`. Type `stop` in the console to shut it down.

### Build a server jar

```bash
./gradlew createMojmapPaperclipJar
```

The jar is written to `weeper-server/build/libs/weeper-paperclip-<version>-mojmap.jar`.

## Publishing

```bash
# Publish a dev bundle
./gradlew publishDevBundlePublicationToCsRepository -PpublishDevBundle

# Publish the API
./gradlew publishMavenPublicationToCsRepository
```

## Contributing

Making changes, rebuilding patches, and the file-patch vs. feature-patch workflows are covered in [contributing.md](contributing.md).

## Older Minecraft versions up to 1.21.4

This repository only tracks the current Minecraft version. Older Weeper builds live in the archive:

**[WeepingMC/Weeper-archive](https://github.com/WeepingMC/Weeper-archive)**
