package com.booktracker.app.presentation.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

object IOSTheme {
    val colors: IOSColors
        @Composable
        get() = LocalIOSColors.current

    val typography: IOSTypography
        @Composable
        get() = LocalIOSTypography.current

    val shapes: IOSShapes
        @Composable
        get() = LocalIOSShapes.current

    val spacing: IOSSpacing
        @Composable
        get() = LocalIOSSpacing.current

    val dimensions: IOSDimensions
        @Composable
        get() = LocalIOSDimensions.current
}

@Composable
fun IOSTheme(
    colors: IOSColors = IOSColors(),
    typography: IOSTypography = IOSTypography(),
    shapes: IOSShapes = IOSShapes(),
    spacing: IOSSpacing = IOSSpacing(),
    dimensions: IOSDimensions = IOSDimensions(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalIOSColors provides colors,
        LocalIOSTypography provides typography,
        LocalIOSShapes provides shapes,
        LocalIOSSpacing provides spacing,
        LocalIOSDimensions provides dimensions,
    ) {
        Box {
            content()
        }
    }
}
