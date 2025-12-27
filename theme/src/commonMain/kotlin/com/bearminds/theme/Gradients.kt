package com.bearminds.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import kotlin.math.max

/**
 * Gradient utilities matching Flutter's Styles gradients
 */
object Gradients {
    /**
     * gradient5 - Radial gradient used in simplified password view
     * Matches Flutter's gradient5 function
     * @param width Container width in dp
     * @param height Container height in dp (typically 48dp)
     * @param opacity Opacity (0.0 to 1.0)
     */
    fun gradient5(width: Dp, height: Dp, opacity: Float = 1f): Brush {
        val widthPx = width.value
        val heightPx = height.value
        // Calculate radius based on Flutter's formula: (width / 2) / height
        val radius = if (heightPx > 0) (widthPx / 2) / heightPx else 1f
        return Brush.radialGradient(
            colors = listOf(
                gradient5Start.copy(alpha = opacity),
                gradient5End.copy(alpha = opacity)
            ),
            center = Offset(0f, -2f),
            radius = max(radius, 0.5f) // Ensure minimum radius
        )
    }

    /**
     * Linear gradient for password strength (weak/normal/strong)
     */
    fun linearGradient(colors: List<Color>, widthPx: Float = 1000f): Brush {
        // start and end are in pixel coordinates (0,0 is top-left)
        // For bottomLeft to topRight: start at (0, large) to end at (large, 0)
        return Brush.linearGradient(
            colors = colors,
            start = Offset(0f, widthPx), // BottomLeft - use large value for bottom
            end = Offset(widthPx, 0f) // TopRight - use large value for right
        )
    }
}
