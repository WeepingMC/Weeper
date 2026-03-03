package io.papermc.paper.disguise;

import com.destroystokyo.paper.SkinParts;
import io.papermc.paper.datacomponent.item.ResolvableProfile;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import org.bukkit.Server;
import org.bukkit.entity.Pose;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record PlayerDisguise(ResolvableProfile resolvableProfile, @Nullable SkinParts skinParts,
                             @Nullable Component description, Pose pose) implements DisguiseData {

    @ApiStatus.Internal
    public PlayerDisguise {
        Objects.requireNonNull(resolvableProfile, "profile cannot be null");
    }

    public static Builder builder(ResolvableProfile playerProfile) {
        return new Builder(playerProfile);
    }

    /**
     * Represents the builder to configure certain appearance settings.
     */
    public static class Builder {
        private final ResolvableProfile resolvableProfile;
        @Nullable
        private SkinParts skinParts;
        @Nullable
        private Component description;
        private Pose pose = Pose.STANDING;

        @ApiStatus.Internal
        public Builder(ResolvableProfile resolvableProfile) {
            this.resolvableProfile = resolvableProfile;
        }

        /**
         * Defines which skin parts should be enabled for the fake player.
         * <p>
         * Use {@link Server#newSkinPartsBuilder()} to get a fresh builder instance for configuration.
         *
         * @param skinParts the skin parts that should be shown.
         * @return the builder instance
         */
        public Builder skinParts(SkinParts skinParts) {
            this.skinParts = skinParts;
            return this;
        }

        /**
         * Sets the below name description.
         *
         * @param description the description
         * @return the builder instance
         */
        public Builder description(@Nullable Component description) {
            this.description = description;
            return this;
        }

        public Builder pose(Pose pose) {
            this.pose = pose;
            return this;
        }

        /**
         * Builds the disguise
         *
         * @return the built disguise
         */
        public PlayerDisguise build() {
            return new PlayerDisguise(resolvableProfile, skinParts, description, pose);
        }
    }
}
