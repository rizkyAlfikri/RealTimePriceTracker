package com.alfikri.rizky.realtimepricetracker.domain.usecase.preferences

import com.alfikri.rizky.realtimepricetracker.domain.model.preferences.ThemeMode
import com.alfikri.rizky.realtimepricetracker.domain.repository.preferences.ThemePreferencesRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

// Tests for ThemePreferencesUseCase
class ThemePreferencesUseCaseTest {

    private lateinit var repository: ThemePreferencesRepository
    private lateinit var useCase: ThemePreferencesUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = ThemePreferencesUseCase(repository)
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    // ========== observeThemeMode Tests ==========

    @Test
    fun `observeThemeMode returns flow from repository`() = runTest {
        // Given
        every { repository.observeThemeMode() } returns flowOf(ThemeMode.DARK)

        // When
        val result = useCase.observeThemeMode().first()

        // Then
        assertThat(result).isEqualTo(ThemeMode.DARK)
        verify { repository.observeThemeMode() }
    }

    // ========== getCurrentThemeMode Tests ==========

    @Test
    fun `getCurrentThemeMode returns theme from repository`() = runTest {
        // Given
        coEvery { repository.getThemeMode() } returns ThemeMode.LIGHT

        // When
        val result = useCase.getCurrentThemeMode()

        // Then
        assertThat(result).isEqualTo(ThemeMode.LIGHT)
        coVerify { repository.getThemeMode() }
    }

    // ========== setLightMode Tests ==========

    @Test
    fun `setLightMode sets theme to LIGHT`() = runTest {
        // Given
        coEvery { repository.setThemeMode(ThemeMode.LIGHT) } just Runs

        // When
        useCase.setLightMode()

        // Then
        coVerify { repository.setThemeMode(ThemeMode.LIGHT) }
    }

    // ========== setDarkMode Tests ==========

    @Test
    fun `setDarkMode sets theme to DARK`() = runTest {
        // Given
        coEvery { repository.setThemeMode(ThemeMode.DARK) } just Runs

        // When
        useCase.setDarkMode()

        // Then
        coVerify { repository.setThemeMode(ThemeMode.DARK) }
    }

    // ========== setSystemMode Tests ==========

    @Test
    fun `setSystemMode sets theme to SYSTEM`() = runTest {
        // Given
        coEvery { repository.setThemeMode(ThemeMode.SYSTEM) } just Runs

        // When
        useCase.setSystemMode()

        // Then
        coVerify { repository.setThemeMode(ThemeMode.SYSTEM) }
    }

    // ========== setThemeMode Tests ==========

    @Test
    fun `setThemeMode sets specified theme`() = runTest {
        // Given
        coEvery { repository.setThemeMode(any()) } just Runs

        // When
        useCase.setThemeMode(ThemeMode.DARK)

        // Then
        coVerify { repository.setThemeMode(ThemeMode.DARK) }
    }

    // ========== toggleTheme Tests ==========

    @Test
    fun `toggleTheme switches from LIGHT to DARK`() = runTest {
        // Given
        coEvery { repository.getThemeMode() } returns ThemeMode.LIGHT
        coEvery { repository.setThemeMode(ThemeMode.DARK) } just Runs

        // When
        useCase.toggleTheme()

        // Then
        coVerify { repository.setThemeMode(ThemeMode.DARK) }
    }

    @Test
    fun `toggleTheme switches from DARK to LIGHT`() = runTest {
        // Given
        coEvery { repository.getThemeMode() } returns ThemeMode.DARK
        coEvery { repository.setThemeMode(ThemeMode.LIGHT) } just Runs

        // When
        useCase.toggleTheme()

        // Then
        coVerify { repository.setThemeMode(ThemeMode.LIGHT) }
    }

    @Test
    fun `toggleTheme switches from SYSTEM to DARK`() = runTest {
        // Given
        coEvery { repository.getThemeMode() } returns ThemeMode.SYSTEM
        coEvery { repository.setThemeMode(ThemeMode.DARK) } just Runs

        // When
        useCase.toggleTheme()

        // Then
        coVerify { repository.setThemeMode(ThemeMode.DARK) }
    }

    // ========== resetToDefault Tests ==========

    @Test
    fun `resetToDefault calls repository resetTheme`() = runTest {
        // Given
        coEvery { repository.resetTheme() } just Runs

        // When
        useCase.resetToDefault()

        // Then
        coVerify { repository.resetTheme() }
    }

    // ========== isDarkMode Tests ==========

    @Test
    fun `isDarkMode returns true when theme is DARK`() = runTest {
        // Given
        coEvery { repository.getThemeMode() } returns ThemeMode.DARK

        // When
        val result = useCase.isDarkMode()

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `isLightMode returns true when theme is LIGHT`() = runTest {
        // Given
        coEvery { repository.getThemeMode() } returns ThemeMode.LIGHT

        // When
        val result = useCase.isLightMode()

        // Then
        assertThat(result).isTrue()
    }

    @Test
    fun `isSystemMode returns true when theme is SYSTEM`() = runTest {
        // Given
        coEvery { repository.getThemeMode() } returns ThemeMode.SYSTEM

        // When
        val result = useCase.isSystemMode()

        // Then
        assertThat(result).isTrue()
    }
}