package com.bearminds.views

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun CustomCircularProgressIndicator(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    strokeWidth: Dp = 4.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    animationDuration: Int = 1000,
    animationDelay: Int = 0
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(
            durationMillis = animationDuration,
            delayMillis = animationDelay,
            easing = FastOutSlowInEasing
        ),
        label = "progress_animation"
    )

    Canvas(
        modifier = modifier.size(size)
    ) {
        val canvasSize = size.toPx()
        val strokeWidthPx = strokeWidth.toPx()

        // Draw background arc (no rounded caps)
        drawArc(
            color = backgroundColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
            size = Size(canvasSize - strokeWidthPx, canvasSize - strokeWidthPx),
            style = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Butt // Square edges for background
            )
        )

        // Draw progress arc (rounded caps)
        if (animatedProgress > 0f) {
            drawArc(
                color = progressColor,
                startAngle = -90f, // Start from top
                sweepAngle = 360f * animatedProgress,
                useCenter = false,
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = Size(canvasSize - strokeWidthPx, canvasSize - strokeWidthPx),
                style = Stroke(
                    width = strokeWidthPx,
                    cap = StrokeCap.Round // Rounded edges for progress
                )
            )
        }
    }
}

@Composable
fun CustomCircularProgressWithText(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary,
    textColor: Color = MaterialTheme.colorScheme.primary,
    showPercentage: Boolean = true,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium,
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        CustomCircularProgressIndicator(
            progress = progress,
            size = size,
            strokeWidth = strokeWidth,
            backgroundColor = backgroundColor,
            progressColor = progressColor,
            modifier = Modifier.fillMaxSize()
        )

        if (showPercentage) {
            Text(
                text = "${(progress * 100).roundToInt()}%",
                style = textStyle,
                color = textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Indeterminate version
@Composable
fun CustomCircularProgressIndeterminate(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    strokeWidth: Dp = 4.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    progressColor: Color = MaterialTheme.colorScheme.primary
) {
    val infiniteTransition = rememberInfiniteTransition(label = "progress_rotation")

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation_animation"
    )

    Canvas(
        modifier = modifier
            .size(size)
            .graphicsLayer { rotationZ = rotationAngle }
    ) {
        val canvasSize = size.toPx()
        val strokeWidthPx = strokeWidth.toPx()

        // Draw background arc
        drawArc(
            color = backgroundColor,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
            size = Size(canvasSize - strokeWidthPx, canvasSize - strokeWidthPx),
            style = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Butt
            )
        )

        // Draw progress arc (partial circle)
        drawArc(
            color = progressColor,
            startAngle = 0f,
            sweepAngle = 90f, // Quarter circle
            useCenter = false,
            topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
            size = Size(canvasSize - strokeWidthPx, canvasSize - strokeWidthPx),
            style = Stroke(
                width = strokeWidthPx,
                cap = StrokeCap.Round
            )
        )
    }
}

// Different variants
@Composable
fun ThinCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier
) {
    CustomCircularProgressWithText(
        progress = progress,
        modifier = modifier,
        size = 60.dp,
        strokeWidth = 3.dp,
        backgroundColor = Color(0xFFF3F4F6),
        progressColor = Color(0xFF10B981),
    )
}

@Composable
fun ThickCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier
) {
    CustomCircularProgressWithText(
        progress = progress,
        modifier = modifier,
        size = 100.dp,
        strokeWidth = 12.dp,
        backgroundColor = Color(0xFFFEE2E2),
        progressColor = Color(0xFFEF4444),
    )
}

@Composable
fun GradientCircularProgress(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(1000),
        label = "gradient_progress"
    )

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasSize = size.toPx()
            val strokeWidthPx = strokeWidth.toPx()

            // Background
            drawArc(
                color = Color(0xFFF3F4F6),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                size = Size(canvasSize - strokeWidthPx, canvasSize - strokeWidthPx),
                style = Stroke(width = strokeWidthPx, cap = StrokeCap.Butt)
            )

            // Gradient progress
            if (animatedProgress > 0f) {
                val sweepAngle = 360f * animatedProgress
                val colors = listOf(
                    Color(0xFF8B5CF6),
                    Color(0xFFEC4899),
                    Color(0xFFEF4444)
                )

                // Simple approximation of gradient by drawing multiple arcs
                val segments = (sweepAngle / 10).toInt().coerceAtLeast(1)
                val segmentAngle = sweepAngle / segments

                for (i in 0 until segments) {
                    val startAngle = -90f + (i * segmentAngle)
                    val colorIndex = (i.toFloat() / segments * (colors.size - 1)).toInt()
                        .coerceIn(0, colors.size - 2)
                    val progress = i.toFloat() / segments
                    val color = lerp(colors[colorIndex], colors[colorIndex + 1], progress % 1f)

                    drawArc(
                        color = color,
                        startAngle = startAngle,
                        sweepAngle = segmentAngle,
                        useCenter = false,
                        topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2),
                        size = Size(canvasSize - strokeWidthPx, canvasSize - strokeWidthPx),
                        style = Stroke(width = strokeWidthPx, cap = StrokeCap.Round)
                    )
                }
            }
        }

        Text(
            text = "${(animatedProgress * 100).roundToInt()}%",
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
    }
}
