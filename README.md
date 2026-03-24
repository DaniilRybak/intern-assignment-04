# Timer & Stopwatch (Assignment-04)

## 1. Описание задания
Kotlin Multiplatform-приложение с двумя экранами:
- **Timer** — обратный отсчет с выбором времени, паузой/сбросом и прогресс-индикатором.
- **Stopwatch** — прямой отсчет времени, пауза/сброс и запись кругов (laps).

Проект реализован в формате multi-module KMP:
- `:composeApp` — host-приложение,
- `:model` — доменные состояния и интерфейсы,
- `:feature` — UI, ViewModel и use-case.

## 2. Скриншоты
![Timer](screenshots/timer.png)
![Stopwatch](screenshots/stopwatch.png)

## 3. Видео
[Смотреть видео](ссылка)

## 4. APK
[Скачать APK](releases)

## 5. Инструкция по запуску
### Требования
- JDK 11+
- Android Studio (последняя стабильная версия)
- Xcode (для iOS)

### Android
```zsh
./gradlew :composeApp:assembleDebug
```

### Запуск unit-тестов feature
```zsh
./gradlew :feature:allTests
```

### iOS
Открой папку `iosApp` в Xcode и запусти приложение на симуляторе.

## 6. Использованные ИИ-инструменты
- GitHub Copilot (помощь в генерации/рефакторинге кода)
- ChatGPT (поддержка по архитектуре, тестам и документации)
