# Retro Games

Классические игры с кнопочных телефонов 2000-х для Android.

## Игры

| Игра | Описание |
|------|----------|
| **Snake II** | Классическая змейка |
| **Space Impact** | Космический шутер |
| **Pairs II** | Найди пары карточек |
| **Bantumi** | Африканские шашки (манкала) |
| **Racing** | Гонки — уклоняйся от машин |
| **Bounce** | Прыгающий мячик по платформам |

## Проекты

| Папка | Описание |
|-------|----------|
| `app/` | Нативный Android (Kotlin + Compose) |
| `retro-games/` | Expo / React Native (для EAS Build и Expo Go) |

**Package name:** `com.vadim.retrophonegames`

## Сборка нативного APK

```bash
./gradlew assembleDebug
```

## Сборка AAB для Google Play (Expo)

```bash
cd retro-games
npm install -g eas-cli
eas login
eas build --platform android --profile production
```

В Expo Dashboard укажите **Base directory:** `retro-games`

## Управление

- **D-pad** (▲▼◀▶) — движение / навигация
- **OK** — действие / стрельба / прыжок
- **Назад** — возврат в меню
