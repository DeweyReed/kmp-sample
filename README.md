# KMP Sample

[![Develop](https://github.com/DeweyReed/kmp-sample/actions/workflows/develop.yml/badge.svg)](https://github.com/DeweyReed/kmp-sample/actions/workflows/develop.yml)

A multi-module Kotlin Multiplatform (KMP) application built to demonstrate modern development best
practices. It consumes the [Spaceflight News API](https://spaceflightnewsapi.net/) to provide a
list-detail view of the latest spaceflight articles.

## Screenshots

TODO

## Features

- **Cross-Platform UI** - 100% shared UI using Compose Multiplatform
- **Offline-First** - Caches network requests using Room (KMP) for seamless offline viewing
- **Modern State Management** - Unidirectional Data Flow using the MVI (Model-View-Intent) pattern
- **Deep Modularization** - Strict separation of concerns via feature-based multi-module
  architecture

## Architecture

This project utilizes **Clean Architecture** combined with a multi-module setup managed by **Gradle
Convention Plugins** (`build-logic`). The project is split into the following layers:

- `base` - Core utilities and base classes
- `feature` - Feature modules
    - `api` - Public models and interfaces
    - `data` - Internal implementation (Repositories, Network, Database)
    - `presentation` - UI, ViewModels, and Composables
- `data` - Core module that collects feature `data` modules
- `composeApp` - The application module that wires dependencies together using dependency injection
- `androidApp`, `iosApp` - The app entry points

## Tech Stack

### Architecture

- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html) - Targeting Android, iOS,
  and Desktop
- **Gradle Convention Plugins** & **Version Catalogs** - Scalable, type-safe build logic
- [Koin](https://insert-koin.io/) - Dependency Injection
- [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlinlang.org/docs/flow.html) -
  Asynchronous programming

### UI

- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/) - UI framework
- [AndroidX Navigation Compose](https://developer.android.com/jetpack/compose/navigation) -
  Type-safe navigation
- [Coil](https://coil-kt.github.io/coil/) - Image loading

### Data

- [Ktor](https://ktor.io/) - Asynchronous HTTP client
- [Room (KMP)](https://developer.android.com/kotlin/multiplatform/room) - Local SQLite database
- [DataStore](https://developer.android.com/kotlin/multiplatform/datastore) - Local storage
- [Kotlinx Serialization](https://github.com/Kotlin/kotlinx.serialization) - JSON parsing

### Testing

- `kotlin-test` & `koin-test` - Unit testing
- `ktor-client-mock` - Mocking API responses

## License

[Apache License 2.0](LICENSE)
