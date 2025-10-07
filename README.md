# VTB Тестовое задание - Нагрузочное тестирование

## Описание
Реализация нагрузочного тестирования с использованием JMeter для вакансии Нагрузочного тестировщика. 

## Задания
- **Задание 1.1**: Развертывание Kafka и PostgreSQL
- **Задание 1.2**: Нагрузочное тестирование Kafka с помощью JMeter
- **Задание 1.3**: Spring Boot потребитель Kafka
- **Задание 2**: Мониторинг с Grafana
- **Задание 3**: Динамическое управление задержками
  
[ТЗ подробнее](./VTB_test_task/Тестовое_задание.docx)

## Технологический стек
- Apache Kafka + Zookeeper
- PostgreSQL
- Apache JMeter
- Spring Boot + Spring Kafka
- Docker + Docker Compose
- Prometheus + Grafana

## Важно!

### Сбор метрик JMeter
Для интеграции JMeter с Prometheus использован плагин:
- **Плагин**:  [jmeter-prometheus-plugin v0.7.1](https://github.com/topics/jmeter-plugin)
- Порт: 9270
- Endpoint: `/metrics`
- Формат: Native Prometheus metrics
  
## Быстрый старт

### 1. Запуск инфраструктуры
```bash
docker compose up -d
```

### 2. Создание топика Kafka
```bash
docker exec kafka kafka-topics --create \
  --topic test-topic \
  --bootstrap-server localhost:9092 \
  --partitions 3 \
  --replication-factor 1
  ```

### 3. Создание таблицы в PostgreSQL

```bash
docker exec postgres psql -U test_user -d test_db -c "
CREATE TABLE messages (
    id SERIAL PRIMARY KEY,
    msgUuid VARCHAR(36) NOT NULL UNIQUE,
    head BOOLEAN NOT NULL,
    timeRq BIGINT NOT NULL,
);"
```

### 4. Запуск Spring Boot приложения
 ```bash
 cd kafka-consumer
mvn clean package
java -jar target/kafka-consumer-1.0.0.jar
```

### 5. Запуск нагрузочного теста
 
- Откройте JMeter
- Загрузите jmeter/kafka-load-test.jmx
- Запустите тест

##  Мониторинг

- **Kafka UI**: http://localhost:8081(опционально, можно не запускать этот контейнер)
- **Health Check**: http://localhost:8080/actuator/health
- **Метрики Prometheus**: http://localhost:8080/actuator/prometheus
- **PostgreSQL**: localhost:5432 (test_db/test_user/test_password)
- **Cырые метрики PostgreSQL**: http://localhost:9187
- **Cтатус подключения всех источников метрик**: http://localhost:9090/service-discovery
- **Cтатус всех targets Prometheus**: http://localhost:9090/targets?search=
- **Cырые JSON данные о targets**: http://localhost:9090/api/v1/targets
- **Grafana**: http://localhost:3000





## Результаты тестирования
- Нагрузка: 5 -> 10 -> 12 -> 14 сообщений/секунду
- Общее время: 20 минут
- Сообщений отправлено: ~
- Каждое 10-е сообщение: head: false

Подробности в [REPORT.md](./VTB_test_task/REPORT.md)
