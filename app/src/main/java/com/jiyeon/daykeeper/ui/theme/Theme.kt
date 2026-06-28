package com.jiyeon.daykeeper.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = DayKeeperColors.Primary,
    onPrimary = DayKeeperColors.OnPrimary,
    primaryContainer = DayKeeperColors.PrimaryContainer,
    onPrimaryContainer = DayKeeperColors.OnPrimaryContainer,
    background = DayKeeperColors.Background,
    onBackground = DayKeeperColors.TextPrimary,
    surface = DayKeeperColors.Background,
    onSurface = DayKeeperColors.TextPrimary,
    surfaceVariant = DayKeeperColors.SurfaceVariant,
    onSurfaceVariant = DayKeeperColors.TextSecondary,
    outline = DayKeeperColors.BorderSecondary,
    outlineVariant = DayKeeperColors.Divider,
    error = DayKeeperColors.StatusBad,
    errorContainer = DayKeeperColors.DangerContainer,
    onErrorContainer = DayKeeperColors.TextDanger,
    scrim = DayKeeperColors.Scrim,
)

private val DarkColorScheme = darkColorScheme(
    primary = DayKeeperColorsDark.Primary,
    onPrimary = DayKeeperColorsDark.OnPrimary,
    primaryContainer = DayKeeperColorsDark.PrimaryContainer,
    onPrimaryContainer = DayKeeperColorsDark.OnPrimaryContainer,
    background = DayKeeperColorsDark.Background,
    onBackground = DayKeeperColorsDark.TextPrimary,
    surface = DayKeeperColorsDark.Background,
    onSurface = DayKeeperColorsDark.TextPrimary,
    surfaceVariant = DayKeeperColorsDark.SurfaceVariant,
    onSurfaceVariant = DayKeeperColorsDark.TextSecondary,
    outline = DayKeeperColorsDark.BorderSecondary,
    outlineVariant = DayKeeperColorsDark.Divider,
    error = DayKeeperColors.StatusBad,
    errorContainer = DayKeeperColorsDark.DangerContainer,
    onErrorContainer = DayKeeperColorsDark.TextDanger,
    scrim = DayKeeperColorsDark.Scrim,
)

@Composable
fun DayKeeperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Giữ tham số để tương thích call-site; dynamic color bị bỏ để khớp
    // chính xác design tokens (không bao giờ dùng màu hệ thống).
    @Suppress("UNUSED_PARAMETER") dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
