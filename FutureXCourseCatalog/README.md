# FutureX Course Catalog

## 📋 Descripción

Microservicio que actúa como fachada para el catálogo de cursos. Consume el servicio FutureXCourseApp implementando patrones de resiliencia con Resilience4j para garantizar la disponibilidad y tolerancia a fallos.

## 🎯 Funcionalidades

- **Service Consumer**: Consume APIs del servicio de cursos
- **Patrones de Resiliencia**: Circuit Breaker, Retry, Time Limiter
- **Fallback Mechanisms**: Respuestas alternativas cuando el servicio falla
- **Service Discovery**: Descubre dinámicamente el servicio de cursos
- **Monitoreo**: Métricas y estado de patrones de resiliencia

## 🔧 Configuración

- **Puerto**: 8002
- **Nombre del Servicio**: `fx-catalog-service`
- **Servicio Consumido**: `fx-course-service`

## 🛡️ Patrones de Resiliencia Implementados

### Circuit Breaker

- **Ventana**: 10 llamadas
- **Umbral de Falla**: 50%
- **Tiempo Abierto**: 30 segundos
- **Llamadas en Half-Open**: 3

### Retry Pattern

- **Máximo Intentos**: 3
- **Tiempo de Espera**: 2 segundos
- **Backoff Exponencial**: x2

### Time Limiter

- **Timeout**: 5 segundos por llamada

## 🌐 Endpoints Disponibles

### Catálogo de Cursos

- `GET /` - Home del catálogo con información del servicio de cursos
- `GET /catalog` - Lista completa de cursos disponibles
- `GET /firstcourse` - Información del primer curso
- `GET /course/{id}` - Información de un curso específico

### Monitoreo y Salud

- `GET /health` - Health check del catálogo
- `GET /actuator/health` - Información detallada incluyendo circuit breakers
- `GET /actuator/circuitbreakers` - Estado de los circuit breakers
- `GET /actuator/retries` - Información de reintentos
- `GET /actuator/timelimiters` - Estado de los time limiters

## 🚀 Ejecución

### Con Docker

```bash
docker-compose up course-catalog
```

### Local

```bash
# Asegurar que Eureka Server y Course App estén ejecutándose
mvn spring-boot:run
```

## 🔄 Comportamiento de Fallback

Cuando el servicio de cursos no está disponible:

- `/catalog` → Retorna lista vacía `"Our courses are []"`
- `/firstcourse` → Retorna mensaje de no disponible
- `/course/{id}` → Retorna "Curso no disponible temporalmente"

## 📊 Monitoreo de Resiliencia

El servicio expone métricas detalladas a través de Spring Boot Actuator para monitorear:

- Estado de los circuit breakers (CLOSED/OPEN/HALF_OPEN)
- Número de reintentos realizados
- Timeouts y latencias
- Tasas de éxito/fallo

## 🛠️ Tecnologías

- Spring Boot 3.3.3
- Spring Cloud 2023.0.3
- Resilience4j (Circuit Breaker, Retry, Time Limiter)
- Netflix Eureka Client
- Spring Boot Actuator
- RestTemplate para comunicación HTTP
