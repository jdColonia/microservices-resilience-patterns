# Microservicios con Patrones de Resiliencia

Este proyecto implementa una arquitectura de microservicios con **Service Discovery** usando Eureka y patrones de resiliencia como **Circuit Breaker**, **Retry** y **Time Limiter** usando Resilience4j.

## ✒️ Autores

> - Juan David Colonia Aldana - A00395956
> - Miguel Ángel Gonzalez Arango - A00395687

## 🏗️ Arquitectura

- **FutureXEurekaServer**: Servidor de descubrimiento de servicios (Puerto 8761)
- **FutureXCourseApp**: Servicio de cursos con base de datos H2 (Puerto 8001)
- **FutureXCourseCatalog**: Servicio de catálogo que consume el servicio de cursos (Puerto 8002)

## 🚀 Ejecución con Docker

### Prerrequisitos

- Docker instalado

### Comandos

```bash
# Construir y ejecutar todos los servicios
docker-compose up --build

# Ejecutar en background
docker-compose up -d --build

# Detener servicios
docker-compose down
```

## 🔗 Endpoints Disponibles

### Eureka Dashboard

- **http://localhost:8761** - Panel de control de Eureka

### Course Service (fx-course-service)

- **http://localhost:8001** - Home del servicio
- **http://localhost:8001/courses** - Lista todos los cursos
- **http://localhost:8001/{id}** - Obtiene curso por ID
- **http://localhost:8001/health** - Health check

### Catalog Service (fx-catalog-service)

- **http://localhost:8002** - Home del catálogo
- **http://localhost:8002/catalog** - Catálogo de cursos
- **http://localhost:8002/firstcourse** - Primer curso
- **http://localhost:8002/course/{id}** - Curso específico por ID
- **http://localhost:8002/health** - Health check

### Monitoreo Resilience4j

- **http://localhost:8002/actuator/health** - Estado general incluyendo circuit breakers
- **http://localhost:8002/actuator/circuitbreakers** - Estado de circuit breakers
- **http://localhost:8002/actuator/retries** - Estado de reintentos
- **http://localhost:8002/actuator/timelimiters** - Estado de time limiters

## 🛡️ Patrones de Resiliencia Implementados

### 1. Service Discovery

- **Eureka Server** para registro y descubrimiento automático de servicios
- **EurekaClient** para comunicación entre servicios
- Los servicios se registran automáticamente al iniciar

### 2. Circuit Breaker

- **Configuración**: 50% de fallos en ventana de 10 llamadas abre el circuito
- **Tiempo abierto**: 30 segundos antes de intentar cerrar
- **Half-open**: 3 llamadas permitidas para probar si el servicio se recuperó
- **Fallback**: Respuestas alternativas cuando el servicio no está disponible

### 3. Retry Pattern

- **Reintentos**: Máximo 3 intentos con espera de 2 segundos
- **Backoff exponencial**: Multiplica por 2 el tiempo de espera entre reintentos

### 4. Time Limiter

- **Timeout**: 5 segundos máximo por llamada
- **Previene** llamadas que cuelguen indefinidamente

## 🔧 Tecnologías Utilizadas

- **Spring Boot 3.3.3**
- **Spring Cloud 2023.0.3**
- **Netflix Eureka** (Service Discovery)
- **Resilience4j** (Circuit Breaker, Retry, Time Limiter)
- **Spring Actuator** (Monitoring)
- **H2 Database** (Datos de prueba)
- **Docker & Docker Compose**
- **Maven** (Build tool)
- **Java 17**
