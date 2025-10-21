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

## 🔗 Endpoints Principales

### Eureka Dashboard

- **http://localhost:8761** - Panel de control de Eureka

### Monitoreo Resilience4j

- **http://localhost:8002/actuator/health** - Estado general incluyendo circuit breakers
- **http://localhost:8002/actuator/circuitbreakers** - Estado de circuit breakers
- **http://localhost:8002/actuator/retries** - Estado de reintentos
- **http://localhost:8002/actuator/timelimiters** - Estado de time limiters

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

## 🔍 Service Discovery con Netflix Eureka

### Comunicación entre Microservicios

El **Service Discovery** en este proyecto se implementa usando **Netflix Eureka**, que funciona como un servidor de registro central donde todos los servicios se registran automáticamente al iniciar.

**Proceso de Descubrimiento:**

1. **Registro Automático**: Cada servicio se registra en Eureka Server al iniciarse

   - `fx-course-service` se registra en el puerto 8001
   - `fx-catalog-service` se registra en el puerto 8002
   - Ambos se conectan a Eureka Server en `http://localhost:8761/eureka/`

2. **Descubrimiento Dinámico**: El servicio de catálogo usa `EurekaClient` para localizar dinámicamente el servicio de cursos mediante el nombre registrado `fx-course-service`.

3. **Comunicación Resiliente**: Una vez descubierto, se establece comunicación HTTP usando `RestTemplate` con patrones de resiliencia aplicados.

### Endpoints y Funcionalidad

**Endpoints de fx-course-service** (Puerto 8001):

- `GET /` → Retorna "Course App Home"
- `GET /courses` → Lista todos los cursos disponibles
- `GET /{id}` → Obtiene un curso específico por ID
- `GET /health` → Health check del servicio

**Endpoints de fx-catalog-service** (Puerto 8002):

- `GET /` → Consume el home de fx-course-service y retorna "Welcome to FutureX Course Catalog [mensaje del servicio]"
- `GET /catalog` → Consume `/courses` y retorna "Our courses are [lista de cursos]"
- `GET /firstcourse` → Consume `/1` y retorna "Our first course is [nombre del curso]"
- `GET /course/{id}` → Consume `/{id}` y retorna "Course {id} is [nombre del curso]"
- `GET /health` → Health check del catálogo

**Flujo de Comunicación Ejemplo:**

1. Cliente solicita `http://localhost:8002/catalog`
2. fx-catalog-service consulta a Eureka: "¿Dónde está fx-course-service?"
3. Eureka responde con la URL: `http://course-app:8001`
4. fx-catalog-service hace petición HTTP a `http://course-app:8001/courses`
5. Si fx-course-service responde, retorna los datos; si falla, activa el fallback

## 🛡️ Patrones de Resiliencia con Resilience4j

### Configuración del Sistema de Resiliencia

**Dependencias del Proyecto:**

- `spring-cloud-starter-circuitbreaker-resilience4j` para Circuit Breaker y patrones de resiliencia
- `spring-boot-starter-actuator` para monitoreo y métricas

**Configuración:** El proyecto incluye configuración tanto en clases Java (`ResilienceConfig.java`) como en `application.properties` para definir el comportamiento de los patrones de resiliencia.

### Mecanismos de Fallback

Cada método del `ResilientCourseService` implementa fallbacks automáticos que proporcionan respuestas alternativas cuando el servicio principal falla, como retornar listas vacías o mensajes informativos.

### Enfoque de Configuración

Aunque el proyecto no usa anotaciones `@CircuitBreaker`, utiliza configuración programática que es más flexible:

- **Inyección de dependencias**: `CircuitBreakerRegistry` y `RetryRegistry`
- **Decoradores funcionales**: Combinación de Circuit Breaker + Retry + Time Limiter
- **Configuración por instancia**: Cada servicio tiene su propia configuración

### Configuración de Patrones

**Circuit Breaker:**

- Ventana de 10 llamadas, 50% de umbral de falla
- 30 segundos abierto antes de intentar cerrar
- 3 llamadas permitidas en estado half-open

**Retry Pattern:**

- Máximo 3 intentos con espera de 2 segundos
- Backoff exponencial que multiplica x2 el tiempo de espera

**Time Limiter:**

- Timeout de 5 segundos por llamada para prevenir cuelgues

### Estados y Monitoreo

**Estados del Circuit Breaker:** CLOSED (normal) → OPEN (fallando) → HALF_OPEN (probando)

**Comportamiento de Fallback:** Cuando fx-course-service falla, el catálogo retorna respuestas alternativas como listas vacías o mensajes informativos.

### Beneficios para la Arquitectura

**Resilience4j** proporciona múltiples mecanismos de protección:

1. **Circuit Breaker**: Previene el "efecto cascada" al cortar llamadas a servicios que fallan
2. **Retry**: Maneja fallos temporales con reintentos inteligentes y backoff exponencial
3. **Time Limiter**: Evita que las llamadas cuelguen indefinidamente
4. **Bulkhead**: Aísla recursos críticos (aunque no implementado en este proyecto)
5. **Rate Limiter**: Controla la tasa de llamadas (no implementado aquí)

**Beneficios Específicos:**

- **Tolerancia a Fallos**: El sistema sigue funcionando aunque un servicio esté caído
- **Auto-recuperación**: El Circuit Breaker se auto-repara cuando el servicio se recupera
- **Respuesta Rápida**: Los fallbacks proporcionan respuestas inmediatas sin esperas
- **Observabilidad**: Spring Actuator expone métricas detalladas de cada patrón

### Evolución de las Herramientas de Resiliencia

**Hystrix** fue la biblioteca pionera en patrones de resiliencia, pero Netflix la marcó como en modo mantenimiento en 2018. **Resilience4j** es su sucesor moderno con ventajas significativas:

- **Funcional**: Basado en programación funcional, más ligero y flexible
- **Modular**: Cada patrón es una biblioteca independiente
- **Métricas**: Integración nativa con Micrometer y Spring Boot Actuator
- **Java 8+**: Aprovecha características modernas del lenguaje
- **Sin Dependencias**: No requiere bibliotecas externas pesadas como RxJava

El proyecto implementa Resilience4j como la solución más moderna y mantenida para patrones de resiliencia en microservicios.
