package com.alfikri.rizky.realtimepricetracker.domain.model.preferences

/**
 * Enum representing available theme modes
 */
enum class ThemeMode {
    /**
     * Light theme - always use light colors
     */
    LIGHT,

    /**
     * Dark theme - always use dark colors
     */
    DARK,

    /**
     * System default - follow system theme setting
     */
    SYSTEM;

    companion object {
        /**
         * Default theme mode
         */
        val DEFAULT = SYSTEM

        /**
         * Converts string to ThemeMode, with fallback to DEFAULT
         */
        fun fromString(value: String?): ThemeMode {
            return when (value?.uppercase()) {
                "LIGHT" -> LIGHT
                "DARK" -> DARK
                "SYSTEM" -> SYSTEM
                else -> DEFAULT
            }
        }
    }
}