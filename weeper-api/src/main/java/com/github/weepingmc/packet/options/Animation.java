package com.github.weepingmc.packet.options;

public enum Animation {

    /**
     * use {@link #SWING_MAIN_HAND}
     */
    @Deprecated
    SWING_MAIN_ARM,
    SWING_MAIN_HAND,
    /**
     * use {@link #CRITICAL_HIT}
     */
    @Deprecated(forRemoval = true, since = "1.21.3")
    @org.jetbrains.annotations.ApiStatus.ScheduledForRemoval(inVersion = "1.22.0")
    TAKE_DAMAGE,
    LEAVE_BED,
    SWING_OFFHAND,
    /**
     * use {@link #CRITICAL_HIT}
     */
    @Deprecated(forRemoval = true, since = "1.21.3")
    @org.jetbrains.annotations.ApiStatus.ScheduledForRemoval(inVersion = "1.22.0")
    CRITICAL_EFFECT,
    CRITICAL_HIT,
    MAGIC_CRITICAL_EFFECT
}
