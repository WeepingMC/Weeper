package com.github.weepingmc.pack;

public record PackInfo(
        String id,
        String name,
        PackPaths paths
) {

    public record PackPaths(
            String dataPack,
            String resourcePack
    ){}
}
