package com.bearminds.views

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bearminds.theme.AppTheme
import com.bearminds.theme.DimensTokens
import com.bearminds.theme.ShapeTokens
import com.bearminds.theme.gradientEnd3
import com.bearminds.theme.gradientStart
import com.bearminds.theme.lightPurpleColor
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * GradientButton - Button with gradient background matching Figma design
 *
 * Features:
 * - Gradient background from provided colors
 * - Rounded corners (8dp)
 * - Disabled state with reduced opacity
 * - Loading state with spinner
 * - Full width by default
 * - Drop shadow and glow effects matching Figma design:
 *   - Light purple gradient glow below button (stylistic effect)
 *   - Real elevation shadow that responds to press interactions
 *   - Inner top highlight
 */
@Composable
fun GradientButton(
    text: String,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    showShadow: Boolean = true
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val actualGradientColors = if (enabled) gradientColors
    else listOf(lightPurpleColor, lightPurpleColor)

    // Elevation and glow change on press
    val elevation = if (isPressed) 2.dp else 8.dp
    val glowAlpha = if (isPressed) 0.4f else 0.7f

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = if (showShadow) DimensTokens.spacingMd else 0.dp), // Extra space for shadow
        contentAlignment = Alignment.Center
    ) {
        // Layer 1: Gradient glow shadow (same gradient as button) - stylistic effect
        if (showShadow && enabled) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp)
                    .offset(y = 10.dp) // Position below the button
                    .blur(radius = 30.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
                    .alpha(glowAlpha)
                    .clip(ShapeTokens.medium)
                    .drawBehind {
                        val gradient = Brush.linearGradient(
                            colors = gradientColors,
                            start = Offset(0f, size.height),
                            end = Offset(size.width, 0f)
                        )
                        drawRect(brush = gradient)
                    }
            )
        }

        // Layer 2: Main button with gradient, real elevation shadow, and inner highlight
        Surface(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = ShapeTokens.medium,
            shadowElevation = if (showShadow && enabled) elevation else 0.dp,
            color = Color.Transparent,
            enabled = enabled && !isLoading,
            interactionSource = interactionSource
        ) {
            GradientButtonContent(
                gradientColors = actualGradientColors,
                enabled = enabled,
                isLoading = isLoading,
                text = text
            )
        }
    }
}

@Composable
private fun GradientButtonContent(
    gradientColors: List<Color>,
    enabled: Boolean,
    isLoading: Boolean,
    text: String
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .drawBehind {
                val gradient = Brush.linearGradient(
                    colors = gradientColors,
                    start = Offset(0f, size.height),
                    end = Offset(size.width, 0f)
                )
                drawRect(brush = gradient)
            },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

// ======================= PREVIEWS =======================

@Preview
@Composable
private fun Preview_GradientButton_Enabled() {
    AppTheme(isDarkTheme = true) {
        Box(
            modifier = Modifier
                .background(Color(0xFF1E1E2E))
                .padding(DimensTokens.spacingLg)
        ) {
            GradientButton(
                text = "Save password",
                gradientColors = listOf(gradientStart, gradientEnd3),
                onClick = {},
                enabled = true
            )
        }
    }
}

@Preview
@Composable
private fun Preview_GradientButton_Disabled() {
    AppTheme(isDarkTheme = true) {
        Box(
            modifier = Modifier
                .background(Color(0xFF1E1E2E))
                .padding(DimensTokens.spacingLg)
        ) {
            GradientButton(
                text = "Save password",
                gradientColors = listOf(gradientStart, gradientEnd3),
                onClick = {},
                enabled = false
            )
        }
    }
}

@Preview
@Composable
private fun Preview_GradientButton_NoShadow() {
    AppTheme(isDarkTheme = true) {
        Box(
            modifier = Modifier
                .background(Color(0xFF1E1E2E))
                .padding(DimensTokens.spacingLg)
        ) {
            GradientButton(
                text = "Save password",
                gradientColors = listOf(gradientStart, gradientEnd3),
                onClick = {},
                enabled = true,
                showShadow = false
            )
        }
    }
}

@Preview
@Composable
private fun Preview_GradientButton_Loading() {
    AppTheme(isDarkTheme = true) {
        Box(
            modifier = Modifier
                .background(Color(0xFF1E1E2E))
                .padding(DimensTokens.spacingLg)
        ) {
            GradientButton(
                text = "Save password",
                gradientColors = listOf(gradientStart, gradientEnd3),
                onClick = {},
                enabled = true,
                isLoading = true
            )
        }
    }
}
