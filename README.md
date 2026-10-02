# Horoscopo Android 2

A classic Android horoscope app built from scratch with Kotlin, XML layouts, activities, `RecyclerView`, and adapters.

This project intentionally follows the older Android View system so it can be compared with the IgniteCoders-style reference app while still keeping this repository's implementation original and easy to learn from.

## Current Features

- Home screen with all 12 zodiac signs.
- `RecyclerView.Adapter` for sign cards.
- Toggle between list and grid layouts.
- Detail screen for each sign.
- Favourite heart with one favourite sign at a time.
- Day, week, and month reading toggle.
- Light and dark mode through Material DayNight theme.

## Project Structure

- `data/`: zodiac model, repository, period enum, and favourite storage.
- `ui/`: activities and `RecyclerView` adapter.
- `res/layout/`: XML screens and card layouts.
- `res/drawable/`: vector icons.
- `res/menu/`: toolbar actions.

## Open In Android Studio

1. Open `C:\Users\Cash\AndroidStudioProjects\Horoscopo-Android2`.
2. Let Gradle sync.
3. Run the `app` configuration on an emulator or device.

## First Learning Checkpoint

The important lesson in this first version is how `RecyclerView` works:

- `MainActivity` chooses a layout manager and adapter.
- `HoroscopeAdapter` creates and binds view holders.
- Each view holder receives one `HoroscopeSign` and renders it into XML views.
- Clicking a row opens `DetailActivity`.
