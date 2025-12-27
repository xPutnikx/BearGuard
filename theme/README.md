# BearMinds KMP Theme

Design system and theme components for Kotlin Multiplatform + Compose Multiplatform apps.

## Installation

Add as a git submodule:

```bash
git submodule add <your-theme-repo-url> theme
```

Add to your `settings.gradle.kts`:

```kotlin
include(":theme")
```

Add dependency in your module's `build.gradle.kts`:

```kotlin
implementation(project(":theme"))
```

## Components

### AppTheme

Main theme composable that wraps your app with Material3 theming.

```kotlin
@Composable
fun App() {
    AppTheme(isDarkTheme = isSystemInDarkTheme()) {
        // Your app content
        MainScreen()
    }
}
```

### Color Tokens

Predefined color constants for consistent branding:

```kotlin
// Primary colors
val darkColor = Color(0xFF17171A)
val greenColor = Color(0xFF32D583)
val lightColor = Color(0xFFFAFAFA)

// Semantic colors
val errorColor = Color(0xFFDC3D43)
val warningColor = Color(0xFFFDB528)

// Usage
Text(
    text = "Success!",
    color = greenColor
)
```

**Available colors:**
- `darkColor` - Primary dark background
- `greenColor` - Primary accent/success color
- `lightColor` - Primary light background
- `errorColor` - Error states
- `warningColor` - Warning states
- `grayColor` - Secondary text/icons
- And more...

### DimensTokens

Spacing tokens for consistent layout:

```kotlin
object DimensTokens {
    val spacingXxxs = 4.dp
    val spacingXxs = 6.dp
    val spacingXs = 8.dp
    val spacingMd = 12.dp
    val spacingLg = 16.dp
    val spacingXl = 24.dp
    val spacingXxl = 32.dp
}

// Usage
Column(
    modifier = Modifier.padding(DimensTokens.spacingLg),
    verticalArrangement = Arrangement.spacedBy(DimensTokens.spacingMd)
) { ... }
```

### ShapeTokens

Corner radius tokens for consistent shapes:

```kotlin
object ShapeTokens {
    val extraSmall = RoundedCornerShape(4.dp)
    val small = RoundedCornerShape(8.dp)
    val medium = RoundedCornerShape(12.dp)
    val large = RoundedCornerShape(16.dp)
    val extraLarge = RoundedCornerShape(24.dp)
}

// Usage
Card(shape = ShapeTokens.medium) { ... }
```

### Gradients

Gradient utilities for background effects:

```kotlin
// Linear gradient brush
val gradient = Brush.linearGradient(
    colors = listOf(greenColor, darkColor)
)

Box(
    modifier = Modifier.background(gradient)
) { ... }
```

## Customization

To customize colors for your app, you can:

1. **Fork and modify** - Copy this module and update color values
2. **Override in theme** - Use `MaterialTheme.colorScheme` with custom colors

```kotlin
// Example: Custom color scheme
private val MyLightScheme = lightColorScheme(
    primary = myBrandColor,
    onPrimary = Color.White,
    // ...
)

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MyLightScheme,
        content = content
    )
}
```

## Best Practices

1. **Always use DimensTokens** for spacing instead of hardcoded values
2. **Use MaterialTheme.colorScheme** for semantic colors in components
3. **Use color tokens** only for brand-specific colors
4. **Use ShapeTokens** for consistent corner radii

```kotlin
// Good
Spacer(modifier = Modifier.height(DimensTokens.spacingLg))
Text(color = MaterialTheme.colorScheme.onSurface)

// Avoid
Spacer(modifier = Modifier.height(16.dp))
Text(color = Color.Black)
```

## Dependencies

This module requires:
- Compose Multiplatform
- Material3

## Platforms

- Android
- iOS (arm64, x64, simulator)
- macOS (arm64, x64)
- JVM (Desktop)

## License

MIT License - see LICENSE file for details.
