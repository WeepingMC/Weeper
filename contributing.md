# Contributing

## Setting up the workspace

1. Clone the repository with `git clone` `https://github.com/WeepingMC/Weeper.git` or `git@github.com:WeepingMC/Weeper.git`
2. Run `./gradlew applyAllPatches` to apply the patches before importing the project into your IDE


## Rebuilding the patches

Depending on the changes you make you need to rebuild the patches slightly differently.

### Modifying Paper Api excluding classes you add new into your fork

#### File patch based workflow

1. Make your changes to the Paper Api project
2. Cd into the Paper Api project
3. Run `git add <files you changed>`
4. Run `git commit --fixup file`
5. Run `git rebase -i --autosquash base`
6. Save and exit the editor
7. Cd back into the root of your project
8. Run `./gradlew rebuildPaperApiFilePatches`

#### Feature patch based workflow

1. Make your changes to the Paper Api project
2. Cd into the Paper Api project
3. Run `git add <files you changed>`
4. Run `git commit -m "<your message>"`
5. Cd back into the root of your project
6. Run `./gradlew rebuildPaperApiFeaturePatches`

### Modifying Paper Server

#### File patch based workflow

1. Make your changes to the Paper Server project
2. Cd into the Paper Server project
3. Run `git add <files you changed>`
4. Run `git commit --fixup file`
5. Run `git rebase -i --autosquash base`
6. Save and exit the editor
7. Cd back into the root of your project
8. Run `./gradlew rebuildPaperServerFilePatches`

#### Feature patch based workflow

1. Make your changes to the Paper Server project
2. Cd into the Paper Server project
3. Run `git add <files you changed>`
4. Run `git commit -m "<your message>"`
5. Cd back into the root of your project
6. Run `./gradlew rebuildPaperServerFeaturePatches`


### Rebuilding minecraft patches

#### File patch based workflow

1. Make your changes to the Minecraft project
2. Run `./gradlew fixupMinecraftSourcePatches`
3. Run `./gradlew rebuildMinecraftFilePatches`

#### Feature patch based workflow

tbd.

#### Acceess Transformer

Currently, access transformers only work for minecraft classes.
Notice that the access transformer file needs to be named after your fork,
for example `/build-data/weeper.at` for the Weeper fork.

Initially the transformers are applied on `./gradlew applyAllPatches` 
If you want to reapply ATs or apply newly added ATs you need to run `./gradlew cleanCache` before `./gradlew applyAllPatches`


## Rebuild Api Generator Patches

#### File patch based workflow

1. Make your changes to the Paper Api Generator project
2. Cd into the Paper Api Generator project
3. Run `git add <files you changed>`
4. Run `git commit --fixup file`
5. Run `git rebase -i --autosquash base`
6. Save and exit the editor
7. Cd back into the root of your project
8. Run `./gradlew rebuildPaperApiGeneratorFilePatches`

#### Feature patch based workflow

1. Make your changes to the Paper Api Generator project
2. Cd into the Paper Api Generator project
3. Run `git add <files you changed>`
4. Run `git commit -m "<your message>"`
5. Cd back into the root of your project
6. Run `./gradlew rebuildPaperApiGeneratorFeaturePatches`

### Rebuild Single File patches

Patches like `build.gradle.kts` in `fork-server` and `fork-api` need to be rebuilt specially.

1. Make your changes to the file
2. Run `./gradlew rebuildSingleFilePatches`


## Running the server

1. Run `./gradlew runDevServer` to start the server
2. Connect to `localhost:25565` to play
3. To stop the server, type `stop` in the console

## Publishing

### Publish dev bundle

```bash
./gradlew publishDevBundlePublicationToCsRepository -PpublishDevBundle
```

### Publish api
    
```bash
    ./gradlew publishMavenPublicationToCsRepository
```

