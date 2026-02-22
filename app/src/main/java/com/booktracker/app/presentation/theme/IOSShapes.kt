package com.booktracker.app.presentation.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

@Immutable
data class IOSShapes(
    val card: Shape = RoundedCornerShape(20.dp),
    val cover: Shape = RoundedCornerShape(12.dp),
    val button: Shape = RoundedCornerShape(12.dp),
    val capsule: Shape = RoundedCornerShape(50),
    val searchBar: Shape = RoundedCornerShape(10.dp),
    val segmentedControl: Shape = RoundedCornerShape(8.dp),
    val segmentedControlItem: Shape = RoundedCornerShape(7.dp),
    val sheet: Shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
    val progressBar: Shape = RoundedCornerShape(50),
    val tag: Shape = RoundedCornerShape(50)
)

val LocalIOSShapes = staticCompositionLocalOf { IOSShapes() }
