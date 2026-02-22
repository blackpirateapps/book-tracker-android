package com.booktracker.app.presentation.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class IOSColors(
    val background: Color = Color(0xFFFFFFFF),
    val secondaryBackground: Color = Color(0xFFF2F2F7),
    val surface: Color = Color.White,
    val primary: Color = Color(0xFF157EFB),
    val primaryVariant: Color = Color(0xFF0056CC),
    val secondaryLabel: Color = Color(0xFF8E8E93),
    val tertiaryLabel: Color = Color(0xFFC7C7CC),
    val label: Color = Color(0xFF000000),
    val secondaryText: Color = Color(0xFF3C3C43),
    val destructive: Color = Color(0xFFFF3B30),
    val success: Color = Color(0xFF34C759),
    val warning: Color = Color(0xFFFF9500),
    val separator: Color = Color(0xFFE5E5EA),
    val fill: Color = Color(0xFFF2F2F7),
    val segmentedControlBackground: Color = Color(0xFFE9E9EB),
    val segmentedControlSelected: Color = Color.White,
    val searchBarBackground: Color = Color(0xFFE9E9EB),
    val progressTrack: Color = Color(0xFFE5E5EA),
    val progressFill: Color = Color(0xFF157EFB),
    val cardShadow: Color = Color.Transparent,
    val shelfReading: Color = Color(0xFF157EFB),
    val shelfRead: Color = Color(0xFF34C759),
    val shelfAbandoned: Color = Color(0xFFFF3B30),
    val shelfReadingList: Color = Color(0xFFFF9500)
)

val LocalIOSColors = staticCompositionLocalOf { IOSColors() }
