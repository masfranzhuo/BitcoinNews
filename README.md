# Bitcoin News (Flutter)

This repository now contains a Flutter implementation of the Bitcoin News app using clean architecture (data, domain, and presentation layers). It fetches Bitcoin-related headlines from the News API and displays them in a paginated, refreshable list.

## Highlights
- Clean architecture with `data`, `domain`, and `presentation` layers.
- Bloc state management for pagination and refresh.
- Cached network images for article thumbnails.

## Getting Started

### 1) Install dependencies
```sh
flutter pub get
```

### 2) Provide your News API key
The app expects a News API key via `--dart-define`:
```sh
flutter run --dart-define=NEWS_API_KEY=YOUR_API_KEY
```

You can get your API key from [News API](https://newsapi.org/).

### 3) Run the app
```sh
flutter run --dart-define=NEWS_API_KEY=YOUR_API_KEY
```

## Project Structure
```
lib/
  core/
  features/
    news/
      data/
      domain/
      presentation/
```
