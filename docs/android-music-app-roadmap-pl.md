# Roadmap: aplikacja Android do odtwarzania lokalnych plików muzycznych

Ten dokument opisuje praktyczny plan stworzenia aplikacji Android podobnej do Samsung Music, skupionej na **lokalnych plikach audio**.

## 1) Zakres MVP (wersja 1.0)

- Skanowanie lokalnej biblioteki muzycznej urządzenia.
- Widoki: Utwory, Albumy, Artyści, Foldery, Playlisty.
- Odtwarzanie w tle (play/pause/next/previous).
- Ekran „Now Playing” z paskiem postępu i okładką.
- Powiadomienie multimedialne + kontrola z lock screena.
- Kolejka odtwarzania i tryby: shuffle/repeat.
- Wyszukiwanie po tytule/artystach/albumach.

## 2) Rekomendowany stack technologiczny

- **Język:** Kotlin.
- **UI:** Jetpack Compose (szybszy development i nowoczesny UI).
- **Audio engine:** ExoPlayer (Media3).
- **Architektura:** MVVM + Clean-ish modularizacja.
- **DI:** Hilt.
- **Baza lokalna:** Room (cache metadanych i playlist).
- **Asynchroniczność:** Coroutines + Flow.

## 3) Architektura modułów

- `app` – nawigacja, ekran startowy, konfiguracja DI.
- `feature-library` – listy utworów/albumów/artystów/folderów.
- `feature-player` – ekran odtwarzacza i kontrolki.
- `feature-playlists` – tworzenie i edycja playlist.
- `core-media` – integracja Media3/ExoPlayer.
- `core-data` – Room, repozytoria, parser metadanych.
- `core-ui` – wspólne komponenty Compose.

## 4) Dostęp do plików i uprawnienia

W zależności od wersji Androida:

- **Android 13+ (API 33+):** `READ_MEDIA_AUDIO`.
- **Android 10–12:** `READ_EXTERNAL_STORAGE` (zależnie od target SDK i strategii dostępu).
- **Android 10+:** preferuj `MediaStore` zamiast bezpośredniego dostępu do ścieżek.

W praktyce najbezpieczniej:

1. Odczyt biblioteki przez `MediaStore.Audio.Media`.
2. Trzymanie URI plików, nie twardych ścieżek.
3. Obsługa odmowy uprawnień (ekran „brak dostępu”).

## 5) Odtwarzanie w tle (kluczowe)

Użyj `MediaSessionService` (Media3), aby:

- utrzymać odtwarzanie po wygaszeniu ekranu,
- udostępnić kontrolki systemowe (lock screen, słuchawki, Bluetooth),
- poprawnie integrować się z Android Auto / urządzeniami zewnętrznymi.

Do MVP wystarczy:

- jedna aktywna kolejka,
- trwałe powiadomienie odtwarzania,
- poprawna obsługa audio focus (pauza przy połączeniu, nawigacji itp.).

## 6) Dane i metadane

Źródła danych:

- `MediaStore` (title, artist, album, duration, track, uri),
- parser tagów ID3 tylko jeśli potrzebujesz bardziej zaawansowanych pól.

Warto cache’ować w Room:

- normalizowane encje (Track, Album, Artist),
- statystyki odtworzeń i ostatnio odtwarzane,
- playlisty użytkownika.

## 7) UX, który robi różnicę

- Szybki start aplikacji (splash + lazy loading list).
- Płynne przewijanie długich list (Paging lub rozsądny batching).
- Czytelne sortowanie i filtrowanie (A–Z, ostatnio dodane, czas trwania).
- Gesty: przeciągnij, aby dodać do kolejki / playlisty.
- Motyw jasny/ciemny + dynamic color.

## 8) Plan wdrożenia (iteracyjny)

### Iteracja 1
- MediaStore scan + lista utworów.
- Ekran playera i podstawowe sterowanie.

### Iteracja 2
- Albumy i artyści.
- Powiadomienie i lock screen controls.

### Iteracja 3
- Playlisty lokalne (Room).
- Wyszukiwarka + kolejka.

### Iteracja 4
- Ulepszenia wydajności i testy regresji.
- QA na kilku urządzeniach i wersjach Androida.

## 9) Testy i jakość

- **Unit tests:** mapowanie danych z MediaStore do modelu domenowego.
- **Instrumented tests:** flow uprawnień i podstawowe scenariusze odtwarzania.
- **Manual QA checklist:**
  - odtwarzanie po zablokowaniu ekranu,
  - zachowanie po podłączeniu słuchawek/Bluetooth,
  - restart aplikacji i odtworzenie poprzedniej kolejki.

## 10) Typowe pułapki

- Zbyt agresywne skanowanie bibliotek przy każdym starcie.
- Brak spójnej obsługi URI po zmianach pamięci urządzenia.
- Brak recovery po utracie audio focus.
- Niewystarczająca obsługa bardzo dużych bibliotek muzycznych.

## 11) Co dalej po MVP

- Equalizer (systemowy lub własny DSP).
- Teksty utworów (LRC), crossfade, gapless playback.
- Edycja tagów i okładek.
- Backup playlist (np. export/import JSON).

---

Jeśli chcesz, kolejnym krokiem może być przygotowanie konkretnego szkieletu projektu (pakiety, nazwy klas i pierwszy ekran Compose) oraz listy ticketów pod sprint 1.
