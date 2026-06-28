package com.jiyeon.daykeeper.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Design tokens cố định (light mode) trích từ mockup.
 * Bind trực tiếp các token này thay vì dựa vào Material defaults.
 * Các token phụ thuộc theme (surface, text, divider...) được nạp vào
 * colorScheme trong [DayKeeperTheme] để MaterialTheme.colorScheme.* tự khớp.
 */
object DayKeeperColors {
    val Primary = Color(0xFF378ADD)
    val OnPrimary = Color(0xFFFFFFFF)
    val PrimaryContainer = Color(0xFFE6F1FB)
    val OnPrimaryContainer = Color(0xFF0C447C)

    // Màu thanh phân loại (giống nhau ở light/dark).
    val CategoryRest = Color(0xFF7F77DD)
    val CategoryStudy = Color(0xFF378ADD)
    val CategorySport = Color(0xFF1D9E75)
    val CategoryWork = Color(0xFFEF9F27)

    // Soft fill + text-on-soft cho chip "loại đang chọn" ở Add/Edit.
    val CategoryRestSoft = Color(0xFFEEEDFE)
    val OnCategoryRestSoft = Color(0xFF3C3489)
    val CategoryStudySoft = Color(0xFFE6F1FB)
    val OnCategoryStudySoft = Color(0xFF0C447C)
    val CategorySportSoft = Color(0xFFE1F5EE)
    val OnCategorySportSoft = Color(0xFF0F6E56)
    val CategoryWorkSoft = Color(0xFFFAEEDA)
    val OnCategoryWorkSoft = Color(0xFF854F0B)

    // Status (Summary).
    val StatusGood = Color(0xFF1D9E75)
    val StatusMedium = Color(0xFFEF9F27)
    val StatusBad = Color(0xFFD85A30)

    // Surfaces.
    val Background = Color(0xFFFFFFFF)
    val SurfaceVariant = Color(0xFFF1EFE8)
    val DangerContainer = Color(0xFFFCEBEB)
    val Scrim = Color(0x73000000) // black 45%

    // Text.
    val TextPrimary = Color(0xFF1A1915)
    val TextSecondary = Color(0xFF5F5E5A)
    val TextTertiary = Color(0xFF888780)
    val TextDanger = Color(0xFFA32D2D)

    // Borders / dividers.
    val Divider = Color(0x26000000)         // black 15%
    val BorderSecondary = Color(0x4D000000) // black 30%
}

/** Cùng token nhưng cho dark mode (nơi giá trị khác light). */
object DayKeeperColorsDark {
    val Primary = Color(0xFF85B7EB)
    val OnPrimary = Color(0xFF042C53)
    val PrimaryContainer = Color(0xFF0C447C)
    val OnPrimaryContainer = Color(0xFFB5D4F4)

    val Background = Color(0xFF1A1915)
    val SurfaceVariant = Color(0xFF2C2C2A)
    val DangerContainer = Color(0xFF501313)
    val Scrim = Color(0x8C000000) // black 55%

    val TextPrimary = Color(0xFFF1EFE8)
    val TextSecondary = Color(0xFFB4B2A9)
    val TextTertiary = Color(0xFF888780)
    val TextDanger = Color(0xFFF09595)

    val Divider = Color(0x26FFFFFF)         // white 15%
    val BorderSecondary = Color(0x4DFFFFFF) // white 30%
}
