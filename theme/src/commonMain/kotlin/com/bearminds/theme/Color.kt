package com.bearminds.theme

import androidx.compose.ui.graphics.Color

// Flutter Styles color mapping
// Base colors from Flutter styles.dart
val darkColor = Color(0xFF20243D) // _darkColor - main background
val darkVariantColor = Color(0xFF1B1E33) // _darkVariantColor - variant background
val purple2Color = Color(0xFF585D82) // _purple2Color - separator, disabled, ignore
val lightPurpleColor = Color(0xFFA0A3BA) // _lightPurpleColor - submenu, progress, inactive
val lightPurple2Color = Color(0xFFCACDE2) // _lightPurple2Color - secondary text, border
val greenColor = Color(0xFF19B596) // _greenColor - secondary, strong, pro
val green2Color = Color(0xFF2CCC82) // _green2Color - secondary container
val lightColor = Color(0xFFFCF1D3) // _lightColor - primary text, icons, active
val roseColor = Color(0xFFE89AA3) // _roseColor - weak
val redColor = Color(0xFFCA6677) // _redColor - error
val brownColor = Color(0xFFD38058) // _brownColor - normal

// Additional colors
val onboardingAccentColor = Color(0xFF9A6AC9) // Purple accent for onboarding text
val tealBadgeColor = Color(0xFF39898C) // Teal color for paywall savings badge

// Gradient 4 - Purple gradient for onboarding button (without alpha)
val onboardingGradient4Start = Color(0xFF8079CF)
val onboardingGradient4End = Color(0xFF833EAE)

val gradientStart = Color(0xFF585D85)
val gradientEnd1 = Color(0xFFEA9BA8)
val gradientEnd2 = Color(0xFFD78157)
val gradientEnd3 = Color(0xFF15BA97)
val gradient4Start = Color(0x598079CF)
val gradient4End = Color(0x59833EAE)
val gradient5Start = Color(0xFF383D67)
val gradient5End = Color(0xFF4D4460)

// Light theme (keeping existing for now, but app primarily uses dark)
val primaryLight = Color(0xFF6750A4)
val onPrimaryLight = Color(0xFF1C1B1F)
val primaryContainerLight = Color(0xFFEADDFF)
val onPrimaryContainerLight = Color(0xFF21005D)
val secondaryLight = Color(0xFF625B71)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFE8DEF8)
val onSecondaryContainerLight = Color(0xFF1D192B)
val tertiaryLight = Color(0xFF7D5260)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFFFD8E4)
val onTertiaryContainerLight = Color(0xFF31111D)
val errorLight = redColor
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF93000A)
val backgroundLight = Color(0xFFFFFBFE)
val onBackgroundLight = Color(0xFF1C1B1F)
val surfaceLight = Color(0xFFFFFBFE)
val onSurfaceLight = Color(0xFF1C1B1F)
val surfaceVariantLight = Color(0xFFE7E0EC)
val onSurfaceVariantLight = Color(0xFF49454F)
val outlineLight = Color(0xFF79747E)
val outlineVariantLight = Color(0xFFCAC4D0)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF313033)
val inverseOnSurfaceLight = Color(0xFFF4EFF4)
val inversePrimaryLight = Color(0xFFD0BCFF)

// Dark theme - mapped from Flutter styles
val primaryDark = darkColor // backgroundColor
val onPrimaryDark = lightColor // primaryTextColor
val primaryContainerDark = darkVariantColor // _darkVariantColor
val onPrimaryContainerDark = lightColor
val secondaryDark = greenColor // _greenColor
val onSecondaryDark = lightColor // _lightColor
val secondaryContainerDark = green2Color // _green2Color
val onSecondaryContainerDark = lightColor
val tertiaryDark = Color(0xFFEFB8C8)
val onTertiaryDark = Color(0xFF492532)
val tertiaryContainerDark = Color(0xFF633B48)
val onTertiaryContainerDark = Color(0xFFFFD8E4)
val errorDark = redColor // _redColor
val onErrorDark = lightColor // _lightColor
val errorContainerDark = redColor
val onErrorContainerDark = lightColor
val backgroundDark = darkColor // backgroundColor
val onBackgroundDark = lightColor // primaryTextColor
val surfaceDark = darkColor // backgroundColor
val onSurfaceDark = lightColor // primaryTextColor
val surfaceVariantDark = darkVariantColor // _darkVariantColor
val onSurfaceVariantDark = lightPurple2Color // _lightPurple2Color
val outlineDark = purple2Color // separatorColor
val outlineVariantDark = purple2Color
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = lightColor
val inverseOnSurfaceDark = darkColor
val inversePrimaryDark = Color(0xFF6750A4)
