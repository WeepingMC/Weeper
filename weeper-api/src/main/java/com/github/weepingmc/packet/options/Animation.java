package com.github.weepingmc.packet.options;

/**
 * Represents various types of animations that can be used in the application.
 */
public enum Animation {

    /**
     * Deprecated. Use {@link #SWING_MAIN_HAND} instead.
     */
    @Deprecated
    SWING_MAIN_ARM,

    /**
     * Represents the animation for swinging the main hand.
     */
    SWING_MAIN_HAND,

    /**
     * Deprecated. Use {@link #CRITICAL_HIT} instead.
     *
     * @deprecated Scheduled for removal in version 1.22.0.
     * @since 1.21.3
     */
    @Deprecated(forRemoval = true, since = "1.21.3")
    @org.jetbrains.annotations.ApiStatus.ScheduledForRemoval(inVersion = "1.22.0")
    TAKE_DAMAGE,

    /**
     * Represents the animation for leaving a bed.
     */
    LEAVE_BED,

    /**
     * Represents the animation for swinging the offhand.
     */
    SWING_OFFHAND,

    /**
     * Deprecated. Use {@link #CRITICAL_HIT} instead.
     *
     * @deprecated Scheduled for removal in version 1.22.0.
     * @since 1.21.3
     */
    @Deprecated(forRemoval = true, since = "1.21.3")
    @org.jetbrains.annotations.ApiStatus.ScheduledForRemoval(inVersion = "1.22.0")
    CRITICAL_EFFECT,

    /**
     * Represents the animation for a critical hit.
     */
    CRITICAL_HIT,

    /**
     * Represents the animation for a magic critical effect.
     */
    MAGIC_CRITICAL_EFFECT
}
