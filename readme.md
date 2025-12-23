# Weeper - Relaxed like weed and stable like Paper, using paperweight [![.github/workflows/build.yml](https://github.com/WeepingMC/Weeper/actions/workflows/build.yml/badge.svg)](https://github.com/WeepingMC/Weeper/actions/workflows/build.yml)

## Contributing

see [Contributing](contributing.md)

## Publishing

### Publish dev bundle

```bash
./gradlew publishDevBundlePublicationToCsRepository -PpublishDevBundle
```

### Publish api
    
```bash
    ./gradlew publishMavenPublicationToCsRepository
```


### Create artefact

```bash
   ./gradlew createMojmapPaperclipJar
```

Find the jar in `weeper-server/build/libs/weeper-paperclip-<version>-R0.1-SNAPSHOT-mojmap.jar`
