package com.jiyeon.daykeeper.ui.timeline.components

import androidx.compose.ui.graphics.Color
import com.jiyeon.daykeeper.data.local.ActivityCategory
import com.jiyeon.daykeeper.ui.theme.DayKeeperColors

/**
 * Màu thanh phân loại suy ra từ [ActivityCategory]. Dùng chung cho thanh
 * category ở Timeline và biểu đồ tuần ở Summary — không chọn màu thủ công.
 */
fun ActivityCategory.barColor(): Color = when (this) {
    ActivityCategory.REST -> DayKeeperColors.CategoryRest
    ActivityCategory.STUDY -> DayKeeperColors.CategoryStudy
    ActivityCategory.SPORT -> DayKeeperColors.CategorySport
    ActivityCategory.WORK -> DayKeeperColors.CategoryWork
}

/** Nền mềm cho chip "loại đang chọn" ở Add/Edit. */
fun ActivityCategory.softFillColor(): Color = when (this) {
    ActivityCategory.REST -> DayKeeperColors.CategoryRestSoft
    ActivityCategory.STUDY -> DayKeeperColors.CategoryStudySoft
    ActivityCategory.SPORT -> DayKeeperColors.CategorySportSoft
    ActivityCategory.WORK -> DayKeeperColors.CategoryWorkSoft
}

/** Màu chữ trên nền mềm [softFillColor]. */
fun ActivityCategory.onSoftFillColor(): Color = when (this) {
    ActivityCategory.REST -> DayKeeperColors.OnCategoryRestSoft
    ActivityCategory.STUDY -> DayKeeperColors.OnCategoryStudySoft
    ActivityCategory.SPORT -> DayKeeperColors.OnCategorySportSoft
    ActivityCategory.WORK -> DayKeeperColors.OnCategoryWorkSoft
}
