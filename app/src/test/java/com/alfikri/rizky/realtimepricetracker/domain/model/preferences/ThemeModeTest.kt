package com.alfikri.rizky.realtimepricetracker.domain.model.preferences

import com.google.common.truth.Truth.assertThat
import org.junit.Test

/**
 * Unit tests for ThemeMode enum
 */
class ThemeModeTest {

    @Test
    fun `default theme mode is SYSTEM`() {
        // When
        val default = ThemeMode.DEFAULT

        // Then
        assertThat(default).isEqualTo(ThemeMode.SYSTEM)
    }

    @Test
    fun `fromString converts LIGHT correctly`() {
        // When
        val result = ThemeMode.fromString("LIGHT")

        // Then
        assertThat(result).isEqualTo(ThemeMode.LIGHT)
    }

    @Test
    fun `fromString converts DARK correctly`() {
        // When
        val result = ThemeMode.fromString("DARK")

        // Then
        assertThat(result).isEqualTo(ThemeMode.DARK)
    }

    @Test
    fun `fromString converts SYSTEM correctly`() {
        // When
        val result = ThemeMode.fromString("SYSTEM")

        // Then
        assertThat(result).isEqualTo(ThemeMode.SYSTEM)
    }

    @Test
    fun `fromString is case insensitive`() {
        // When/Then
        assertThat(ThemeMode.fromString("light")).isEqualTo(ThemeMode.LIGHT)
        assertThat(ThemeMode.fromString("Light")).isEqualTo(ThemeMode.LIGHT)
        assertThat(ThemeMode.fromString("LIGHT")).isEqualTo(ThemeMode.LIGHT)

        assertThat(ThemeMode.fromString("dark")).isEqualTo(ThemeMode.DARK)
        assertThat(ThemeMode.fromString("Dark")).isEqualTo(ThemeMode.DARK)
        assertThat(ThemeMode.fromString("DARK")).isEqualTo(ThemeMode.DARK)

        assertThat(ThemeMode.fromString("system")).isEqualTo(ThemeMode.SYSTEM)
        assertThat(ThemeMode.fromString("System")).isEqualTo(ThemeMode.SYSTEM)
        assertThat(ThemeMode.fromString("SYSTEM")).isEqualTo(ThemeMode.SYSTEM)
    }

    @Test
    fun `fromString returns DEFAULT for null`() {
        // When
        val result = ThemeMode.fromString(null)

        // Then
        assertThat(result).isEqualTo(ThemeMode.DEFAULT)
    }

    @Test
    fun `fromString returns DEFAULT for invalid value`() {
        // When/Then
        assertThat(ThemeMode.fromString("invalid")).isEqualTo(ThemeMode.DEFAULT)
        assertThat(ThemeMode.fromString("")).isEqualTo(ThemeMode.DEFAULT)
        assertThat(ThemeMode.fromString("AUTO")).isEqualTo(ThemeMode.DEFAULT)
        assertThat(ThemeMode.fromString("123")).isEqualTo(ThemeMode.DEFAULT)
    }

    @Test
    fun `enum has exactly 3 values`() {
        // When
        val values = ThemeMode.values()

        // Then
        assertThat(values).hasLength(3)
        assertThat(values).asList().containsExactly(
            ThemeMode.LIGHT,
            ThemeMode.DARK,
            ThemeMode.SYSTEM
        )
    }

    @Test
    fun `enum name property returns correct values`() {
        // When/Then
        assertThat(ThemeMode.LIGHT.name).isEqualTo("LIGHT")
        assertThat(ThemeMode.DARK.name).isEqualTo("DARK")
        assertThat(ThemeMode.SYSTEM.name).isEqualTo("SYSTEM")
    }

    @Test
    fun `enum ordinal property is correct`() {
        // When/Then
        assertThat(ThemeMode.LIGHT.ordinal).isEqualTo(0)
        assertThat(ThemeMode.DARK.ordinal).isEqualTo(1)
        assertThat(ThemeMode.SYSTEM.ordinal).isEqualTo(2)
    }

    @Test
    fun `fromString round trip works correctly`() {
        // Given
        val themes = listOf(ThemeMode.LIGHT, ThemeMode.DARK, ThemeMode.SYSTEM)

        // When/Then
        themes.forEach { theme ->
            val string = theme.name
            val converted = ThemeMode.fromString(string)
            assertThat(converted).isEqualTo(theme)
        }
    }
}