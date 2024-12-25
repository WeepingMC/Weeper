package io.papermc.testplugin;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

public class PackContainer {
    private final Set<Path> packs = new HashSet<>();

    public void addPack(Path pack) {
        this.packs.add(pack);
    }

    public Set<Path> getPacks() {
        return this.packs;
    }
}
