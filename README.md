# Запуск и тестирование API приложения

## Как запустить приложение

```bash
./mvnw spring-boot:run
```

После запуска приложения вы можете тестировать его API с помощью `curl`. Ниже приведены основные примеры команд для тестирования.

---

## Примеры команд для тестирования API

### 1. Регистрация пользователя

```bash
curl -X POST -H "Content-Type: application/json" -d "{\"username\":\"user1\",\"password\":\"password123\"}" http://localhost:8080/api/users/register
```

### 2. Авторизация (логин) пользователя

```bash
curl -X GET "http://localhost:8080/api/users/login?username=user1&password=password123"
```

### 3. Создание задачи

```bash
curl -X POST -H "Content-Type: application/json" -d "{\"title\":\"Тестовая задача\",\"description\":\"Описание тестовой задачи\",\"targetDate\":\"2024-12-31T12:00:00\",\"userId\":\"ПОЛУЧЕННЫЙ_UUID_ПОЛЬЗОВАТЕЛЯ\"}" http://localhost:8080/api/tasks
```

### 4. Получение всех задач пользователя

```bash
curl -X GET "http://localhost:8080/api/tasks?userId=ПОЛУЧЕННЫЙ_UUID_ПОЛЬЗОВАТЕЛЯ"
```

### 5. Получение только невыполненных задач пользователя

```bash
curl -X GET "http://localhost:8080/api/tasks/pending?userId=ПОЛУЧЕННЫЙ_UUID_ПОЛЬЗОВАТЕЛЯ"
```

### 6. Удаление задачи (отметка как удалённая)

```bash
curl -X DELETE "http://localhost:8080/api/tasks/ПОЛУЧЕННЫЙ_UUID_ЗАДАЧИ"
```

### 7. Получение всех уведомлений пользователя

```bash
curl -X GET "http://localhost:8080/api/notifications?userId=ПОЛУЧЕННЫЙ_UUID_ПОЛЬЗОВАТЕЛЯ"
```

### 8. Получение непрочитанных уведомлений пользователя

```bash
curl -X GET "http://localhost:8080/api/notifications/pending?userId=ПОЛУЧЕННЫЙ_UUID_ПОЛЬЗОВАТЕЛЯ"
```

### 9. Отметка уведомления как прочитанного

```bash
curl -X POST "http://localhost:8080/api/notifications/ПОЛУЧЕННЫЙ_UUID_УВЕДОМЛЕНИЯ/read"
```

---

> **Примечание:** Во всех командах замените:
>
> * `ПОЛУЧЕННЫЙ_UUID_ПОЛЬЗОВАТЕЛЯ` — на реальный UUID пользователя, полученный после регистрации;
> * `ПОЛУЧЕННЫЙ_UUID_ЗАДАЧИ` — на UUID задачи, полученный при создании задачи;
> * `ПОЛУЧЕННЫЙ_UUID_УВЕДОМЛЕНИЯ` — на UUID уведомления, полученный в соответствующем ответе API.
