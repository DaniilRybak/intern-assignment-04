# Timer & Stopwatch (Assignment-04)

## 1. Описание задания
Kotlin Multiplatform-приложение с двумя экранами:
- **Timer** — обратный отсчет с выбором времени, паузой/сбросом и прогресс-индикатором.
- **Stopwatch** — прямой отсчет времени, пауза/сброс и запись кругов (laps).

Проект реализован в формате multi-module KMP:
- `:composeApp` — host-приложение,
- `:model` — доменные состояния и интерфейсы,
- `:feature` — UI, ViewModel и use-case.

## 🛠 Технологический стек
- Kotlin Multiplatform
- Jetpack Compose Multiplatform
- Koin (DI)
- Kotlinx Coroutines (StateFlow, viewModelScope)
- Kotlinx Serialization
- Ktor Client

## 2.1 Скриншоты IOS
<img width="331" height="710" alt="Снимок экрана 2026-03-25 в 10 46 35" src="https://github.com/user-attachments/assets/24ed9bcb-ccba-4da5-98d2-c1b6a4b94db4" />
<img width="324" height="713" alt="Снимок экрана 2026-03-25 в 14 38 11" src="https://github.com/user-attachments/assets/8b9284b5-a90c-4396-a257-c1bbcf4770d7" />
<img width="330" height="714" alt="Снимок экрана 2026-03-25 в 10 46 59" src="https://github.com/user-attachments/assets/84d22cbd-52ff-48f8-8455-71ddbcd9fdc0" />
<img width="302" height="703" alt="Снимок экрана 2026-03-25 в 14 39 07" src="https://github.com/user-attachments/assets/618ff401-a676-4c7a-8003-29b352cd20a3" />
<img width="312" height="678" alt="Снимок экрана 2026-03-25 в 10 47 28" src="https://github.com/user-attachments/assets/86c6c335-dff8-410e-966e-9b4dbf57a118" />
<img width="321" height="709" alt="Снимок экрана 2026-03-25 в 11 12 49" src="https://github.com/user-attachments/assets/a37e9187-2eb6-4119-af8a-2cf13ffebb93" />

## 2.2 Скриншоты Android
<p>Аналогичный внешний вид интерфейса на Android + работа уведомлений на данной ОС</p>
<img width="170" height="361" alt="Снимок экрана 2026-03-25 в 11 21 45" src="https://github.com/user-attachments/assets/2a9a0b8c-03aa-4926-aba2-e819dda3f464" />
<img width="169" height="361" alt="Снимок экрана 2026-03-25 в 11 21 50" src="https://github.com/user-attachments/assets/0f107c46-67d9-4ab9-b1d1-f91a2fc416c4" />
<img width="169" height="366" alt="Снимок экрана 2026-03-25 в 11 18 03" src="https://github.com/user-attachments/assets/84882eba-34bd-491e-84b4-9ae05e34fb09" />



## 3. Видео
[Смотреть видео](https://youtube.com/shorts/O6kXoYFIu1c?feature=share)

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
- GitHub Copilot: ChatGPT, Claude, Gemini (помощь в генерации/рефакторинге кода/аудит)
- QWEN 3.5 (Планирование/ревью контрольныъ точек/аудит)
- Grok (Планирование/ревью контрольныъ точек)
