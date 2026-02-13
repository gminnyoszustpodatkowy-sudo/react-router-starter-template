# Local Music Player (Android)

Prosty odtwarzacz lokalnych plików muzycznych na Androida (Kotlin + XML + Media3/ExoPlayer).

## Co potrafi (MVP)

- prosi o uprawnienie do odczytu muzyki (`READ_MEDIA_AUDIO` / `READ_EXTERNAL_STORAGE`),
- skanuje bibliotekę przez `MediaStore`,
- wyświetla listę utworów,
- odtwarza wybrany utwór,
- obsługuje przyciski `Prev / Play-Pause / Next`.

## Jak zbudować APK

### Opcja 1: Android Studio
1. Otwórz folder `android-local-music-player` w Android Studio.
2. Poczekaj na synchronizację Gradle.
3. Wybierz **Build > Build APK(s)**.
4. APK znajdziesz zwykle w `app/build/outputs/apk/debug/app-debug.apk`.

### Opcja 2: terminal
W folderze `android-local-music-player`:

```bash
./gradlew assembleDebug
```

> Jeśli nie masz `gradlew`, wygeneruj wrapper poleceniem `gradle wrapper` albo zbuduj APK przez Android Studio.
