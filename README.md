# LM Studio Chat

A premium, production-quality Android application designed for interacting with a locally running instance of **LM Studio** via its OpenAI-compatible local API.

## Features

- **Dark-Only Premium UI**: High aesthetics with deep black/dark gray palettes, clean layout lines, rounded cards, and smooth animations.
- **Dynamic Model Selection**: Fetches currently loaded models automatically from `GET /v1/models` and populates selection dropdowns across the application.
- **30 Built-in Personas**: Includes custom-tailored system prompt settings and temperature parameters for specialists like Android Expert, Senior Kotlin Developer, AI Researcher, UI/UX Designers, automation platforms, and general assistants.
- **Prompt Library**: Save, favorite, catalog, and query reusable instructions for rapid deployment.
- **Room Local History**: Seamless storage, search indexing, pinning, archiving, and deletion of user messages and streaming assistant blocks.
- **OkHttp Server-Sent Events (SSE)**: Complete Flow-based character streaming parser for seamless real-time response generation.

## Setup & Integration

1. Start LM Studio on your local network.
2. Under the Local Server tab in LM Studio, start the API server (typically listens on `http://localhost:1234` or `http://127.0.0.1:1234`).
3. Connect your Android device or emulator to the same local network.
4. Launch **LM Studio Chat** and navigate to **Settings** to customize the base API URL (e.g. `http://10.0.2.2:1234/v1` for emulators or `http://<your-host-ip>:1234/v1` for real devices).
5. The application will immediately pull model parameters and is ready for use.

## Architecture

Built using modern Android development principles:
- **Kotlin** & **Jetpack Compose**
- **Clean Architecture** (UI, Domain, Data layers)
- **MVVM Pattern**
- **Hilt** Dependency Injection
- **Flow** & **Coroutines** for reactive UI streams
- **Room Database** & **DataStore Preferences**
- **OkHttp** & **Retrofit**

## License
MIT
