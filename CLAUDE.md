# BearGuard Project Instructions

## Dependency Injection (Koin)

- **Always inject dependencies via constructor**, not using `by inject()` field injection
- For Android components that can't use constructor injection (Services, BroadcastReceivers):
  - Get dependencies from Koin in `onCreate()` using `get()` from KoinComponent
  - Store in a `lateinit var` property
- Example for Services:
  ```kotlin
  class MyService : Service(), KoinComponent {
      private lateinit var repository: MyRepository

      override fun onCreate() {
          super.onCreate()
          repository = get()
      }
  }
  ```

## Architecture

- Use MVI pattern with BaseViewModel (Events, State, Effects)
- ViewModels should have interfaces for all dependencies (for testability)
- Use DataStore for preferences, SQLDelight for complex data

## Testing

- Use Mokkery for mocking
- All ViewModel dependencies should be interfaces (mockable)
- Use `runTest` with `StandardTestDispatcher` for coroutine tests

## Code Style

- Use MaterialTheme.colorScheme for colors, never hardcoded colors
- Put user-facing strings in string resources
- Always create @Preview for @Composable functions
