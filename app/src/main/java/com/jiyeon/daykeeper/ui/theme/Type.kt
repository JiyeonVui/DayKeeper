package com.jiyeon.daykeeper.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography tokens. Font mặc định (Roboto), chỉ 2 weight: 400 (Normal) và
 * 500 (Medium). Không set color trong TextStyle — color truyền ở call site.
 */
object DayKeeperType {
    val screenTitle = TextStyle(
        fontSize = 22.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 28.sp,
    )
    val dialogTitle = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
    )
    val sectionTitle = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
    )
    val bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 22.sp,
    )
    val body = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 22.sp,
    )
    val bodySmall = TextStyle(
        fontSize = 13.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 18.sp,
    )
    val caption = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
    )
    val micro = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
    )
    val displayTime = TextStyle(
        fontSize = 64.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 68.sp,
    )
}

/** Biến thể weight-500 của một [TextStyle]. */
val TextStyle.medium: TextStyle
    get() = copy(fontWeight = FontWeight.Medium)

/** Map token vào slot Material để component mặc định (Button, Dialog...) cũng đúng. */
val Typography = Typography(
    displayLarge = DayKeeperType.displayTime,
    titleLarge = DayKeeperType.screenTitle,
    titleMedium = DayKeeperType.dialogTitle,
    titleSmall = DayKeeperType.sectionTitle,
    bodyLarge = DayKeeperType.bodyLarge,
    bodyMedium = DayKeeperType.body,
    bodySmall = DayKeeperType.bodySmall,
    labelMedium = DayKeeperType.caption,
    labelSmall = DayKeeperType.micro,
)
