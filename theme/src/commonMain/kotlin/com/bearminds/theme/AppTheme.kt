package com.bearminds.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val LocalIsDarkTheme = staticCompositionLocalOf { false }

@Composable
fun AppTheme(isDarkTheme: Boolean = false, content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isDarkTheme) darkScheme else lightScheme,
        shapes = MaterialTheme.shapes.copy(
            extraSmall = RoundedCornerShape(4.dp),
            small = RoundedCornerShape(8.dp),
            medium = RoundedCornerShape(12.dp),
            large = RoundedCornerShape(16.dp),
            extraLarge = RoundedCornerShape(24.dp)
        ),
        typography = AppTypography()
    ) {
        CompositionLocalProvider(LocalIsDarkTheme provides isDarkTheme) {
            content()
        }
    }
}

@Composable
fun AppTypography() = Typography(
    // Display styles - mapped from Flutter
    displayLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 60.sp, // displayMedium from Flutter
        lineHeight = 60.sp,
    ),
    displayMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 50.sp, // displaySmall and headlineMedium from Flutter
        lineHeight = 50.sp,
    ),
    displaySmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 50.sp, // displaySmall from Flutter (for keyboard on passcode screen)
        lineHeight = 50.sp,
    ),
    // Headline styles
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 34.sp, // onboardingIntroPrimaryStyle
        lineHeight = 34.sp,
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp, // headlineSmall from Flutter
        lineHeight = 30.sp,
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp, // onboardingGeneratePrimaryStyle
        lineHeight = 24.sp,
    ),
    // Title styles
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp, // titleLarge from Flutter
        lineHeight = 22.sp,
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp, // titleMedium from Flutter
        lineHeight = 20.sp,
    ),
    titleSmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp, // titleSmall from Flutter
        lineHeight = 14.sp,
    ),
    // Body styles
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 20.sp, // bodyMedium from Flutter
        lineHeight = 30.sp, // height: 1.5
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp, // onErrorTextStyle, pendingPrimaryTextStyle
        lineHeight = 16.sp,
    ),
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 10.sp, // bodySmall from Flutter
        lineHeight = 10.sp,
    ),
    // Label styles
    labelLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp, // labelLarge from Flutter
        lineHeight = 14.sp,
    ),
    labelMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp, // tabButtonStyle, generateHintStyle
        lineHeight = 12.sp,
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp, // analyzerTitleStyle, analyzerCountStyle
        lineHeight = 12.sp,
    )
)
