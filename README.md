# Messenger MVP

Простой клиент-серверный мессенджер на Java в рамках тестового задания.

## Что реализовано
- Java server + Java desktop client
- Авторизация по предопределённым пользователям
- Список пользователей
- Обмен текстовыми сообщениями 1:1 в реальном времени
- Telegram-inspired dark UI на Swing + FlatLaf
- In-memory модель без хранения истории на сервере

## Ограничения MVP
- Нет регистрации
- Нет групп и каналов
- Нет медиа и файлов
- Нет оффлайн-доставки
- Нет read receipts
- Нет edit/delete
- Нет persistence

## Тестовые пользователи
- `alice` / `alice123`
- `bob` / `bob123`
- `charlie` / `charlie123`

## Требования
- JDK 21+

## Сборка
```bash
./gradlew build
```

## Запуск сервера
```bash
./gradlew :server:run --args='5050'
```

## Запуск клиента
```bash
./gradlew :client:run
```

Можно запускать несколько клиентов, просто открыв несколько терминалов и повторив команду.

### Вариант через IDE
- Запустить `com.example.messenger.client.ClientApplication`

## Demo flow
1. Запустить сервер
2. Открыть первый клиент
3. Войти под `alice`
4. Открыть второй клиент
5. Войти под `bob`
6. Отправить сообщение между клиентами

## Smoke tests
Позитивный smoke test:
```bash
python3 scripts/smoke_test.py
```

Негативный smoke test:
```bash
python3 scripts/negative_smoke_test.py
```

Оба теста ожидают, что сервер уже запущен на `127.0.0.1:5050`.

## Архитектура
### Модули
- `common` — протокол и DTO
- `server` — серверная часть
- `client` — Swing-клиент

### Протокол
- TCP sockets
- UTF-8
- один JSON-объект на строку

### Основные типы сообщений
- `LOGIN_REQUEST`
- `LOGIN_RESPONSE`
- `GET_USERS`
- `USERS_LIST`
- `SEND_MESSAGE`
- `MESSAGE_RECEIVED`
- `MESSAGE_ACK`
- `ERROR_RESPONSE`
- `LOGOUT`

## Текущее состояние
Проект собирается успешно через Gradle Wrapper.
