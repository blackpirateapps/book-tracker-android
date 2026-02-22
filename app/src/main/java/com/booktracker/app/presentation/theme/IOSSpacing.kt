package com.booktracker.app.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class IOSSpacing(
    val xxs: Dp = 2.dp,
    val xs: Dp = 4.dp,
    val sm: Dp = 8.dp,
    val md: Dp = 16.dp,
    val lg: Dp = 24.dp,
    val xl: Dp = 32.dp,
    val xxl: Dp = 48.dp
)

@Immutable
data class IOSDimensions(
    val navigationBarHeight: Dp = 44.dp,
    val largeTitleHeight: Dp = 52.dp,
    val searchBarHeight: Dp = 36.dp,
    val segmentedControlHeight: Dp = 32.dp,
    val cardMinHeight: Dp = 120.dp,
    val coverWidth: Dp = 80.dp,
    val coverHeight: Dp = 120.dp,
    val detailCoverWidth: Dp = 180.dp,
    val detailCoverHeight: Dp = 270.dp,
    val progressBarHeight: Dp = 4.dp,
    val separatorHeight: Dp = 0.5.dp,
    val iconSize: Dp = 22.dp,
    val addButtonSize: Dp = 30.dp,
    val sheetHandleWidth: Dp = 36.dp,
    val sheetHandleHeight: Dp = 5.dp
)

val LocalIOSSpacing = staticCompositionLocalOf { IOSSpacing() }
val LocalIOSDimensions = staticCompositionLocalOf { IOSDimensions() }
