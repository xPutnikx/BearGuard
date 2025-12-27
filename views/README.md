# BearMinds KMP Views

Reusable UI components for Kotlin Multiplatform + Compose Multiplatform apps.

## Installation

Add as a git submodule:

```bash
git submodule add <your-views-repo-url> views
```

Add to your `settings.gradle.kts`:

```kotlin
include(":views")
```

Add dependency in your module's `build.gradle.kts`:

```kotlin
implementation(project(":views"))
```

**Note:** This module depends on `:architecture` and `:theme` modules.

## Components

### GradientButton

A customizable button with gradient background, shadow effects, and loading state.

```kotlin
GradientButton(
    text = "Continue",
    onClick = { /* handle click */ },
    modifier = Modifier.fillMaxWidth()
)

// With loading state
GradientButton(
    text = "Submit",
    onClick = { /* handle click */ },
    isLoading = state.isSubmitting,
    enabled = state.isValid
)

// Custom colors
GradientButton(
    text = "Delete",
    onClick = { /* handle click */ },
    gradientStart = errorColor,
    gradientEnd = errorColor.copy(alpha = 0.8f)
)
```

**Parameters:**
- `text` - Button label
- `onClick` - Click handler
- `modifier` - Compose modifier
- `enabled` - Whether button is enabled (default: true)
- `isLoading` - Show loading indicator (default: false)
- `gradientStart` / `gradientEnd` - Gradient colors
- `textColor` - Text color (default: white)

### StyledCircularProgressIndicator

A styled circular progress indicator matching the app theme.

```kotlin
// Indeterminate
StyledCircularProgressIndicator()

// With custom size
StyledCircularProgressIndicator(
    modifier = Modifier.size(48.dp)
)

// Custom color
StyledCircularProgressIndicator(
    color = MaterialTheme.colorScheme.primary
)
```

### FullScreenBottomSheet

A full-screen modal bottom sheet with optional header, title, and close button.

```kotlin
// As ComposableData (for overlay pattern)
val sheetData = FullScreenBottomSheetData(
    title = "Select Option",
    showClose = true,
    closeIcon = Icons.Rounded.Close,
    onClose = { /* dismiss */ },
    content = object : ComposableData {
        @Composable
        override fun Composable(modifier: Modifier) {
            // Sheet content
            OptionsList(onSelect = { /* handle selection */ })
        }
    }
)

// In ViewModel
var overlay: ComposableData? by mutableStateOf(null)
overlay = sheetData

// In Screen
viewModel.overlay?.Composable()
```

**As composable function:**

```kotlin
FullScreenBottomSheet(
    title = "Settings",
    showClose = true,
    closeIcon = Icons.Rounded.Close,
    onClose = { /* dismiss */ }
) {
    // Content
    SettingsContent()
}
```

**Parameters:**
- `title` - Optional header title
- `showClose` - Show close button (default: true)
- `closeIcon` - Icon for close button (optional)
- `onClose` - Close handler
- `iconColor` - Close icon color
- `windowInsets` - Window insets configuration
- `trailingAction` / `leadingAction` - Optional header actions as ComposableData
- `content` - Sheet content

## Usage with Architecture Module

These components are designed to work seamlessly with the architecture module:

```kotlin
class MyViewModel : BaseViewModel<Event, State>() {
    var overlay: ComposableData? by mutableStateOf(null)
        private set

    private fun showOptions() {
        overlay = FullScreenBottomSheetData(
            title = "Options",
            onClose = { overlay = null },
            content = OptionsContent(
                onSelect = { option ->
                    handleSelection(option)
                    overlay = null
                }
            )
        )
    }
}

@Composable
fun MyScreen(viewModel: MyViewModel = koinViewModel()) {
    val state by viewModel.viewState.collectAsState()

    RootScreen(effects = viewModel.effect) {
        // Screen content
        Button(onClick = { viewModel.onEvent(Event.ShowOptions) }) {
            Text("Show Options")
        }
    }

    // Render overlay
    viewModel.overlay?.Composable()
}
```

## Customization

Components use theme tokens for styling. To customize:

1. **Theme module** - Modify color/spacing tokens in the theme module
2. **Component parameters** - Use component parameters for one-off customizations
3. **Fork and modify** - Copy components and customize for your needs

## Dependencies

This module requires:
- `:architecture` module
- `:theme` module
- Compose Multiplatform
- Material3

## Platforms

- Android
- iOS (arm64, x64, simulator)
- macOS (arm64, x64)
- JVM (Desktop)

## License

MIT License - see LICENSE file for details.
