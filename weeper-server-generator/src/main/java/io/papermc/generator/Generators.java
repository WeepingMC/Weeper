package io.papermc.generator;

import io.papermc.generator.types.EntityMetaWatcherGenerator;
import io.papermc.generator.types.EntityTypeToEntityClassGenerator;
import io.papermc.generator.types.GeneratedKeyType;
import io.papermc.generator.types.GeneratedTagKeyType;
import io.papermc.generator.types.SourceGenerator;
import io.papermc.paper.registry.RegistryKey;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public interface Generators {

    SourceGenerator[] SERVER = {
            new EntityMetaWatcherGenerator("EntityMetaWatcher", "io.papermc.paper.entity.meta"),
            new EntityTypeToEntityClassGenerator("EntityTypeToEntityClass", "io.papermc.paper.entity.meta"),
    };

    private static <T, A> SourceGenerator simpleKey(final String className, final Class<A> apiType, final ResourceKey<? extends Registry<T>> registryKey, final RegistryKey<A> apiRegistryKey, final boolean publicCreateKeyMethod) {
        return new GeneratedKeyType<>(className, apiType, "io.papermc.paper.registry.keys", registryKey, apiRegistryKey, publicCreateKeyMethod);
    }

    private static <T, A> SourceGenerator simpleTagKey(final String className, final Class<A> apiType, final ResourceKey<? extends Registry<T>> registryKey, final RegistryKey<A> apiRegistryKey) {
        return new GeneratedTagKeyType<>(className, apiType, "io.papermc.paper.registry.keys.tags", registryKey, apiRegistryKey, true);
    }
}
