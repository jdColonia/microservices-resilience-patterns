# FutureX Course App

## 📋 Descripción

Microservicio que gestiona el catálogo de cursos. Proporciona operaciones CRUD para cursos y utiliza una base de datos H2 en memoria para almacenamiento.

## 🎯 Funcionalidades

- **Gestión de Cursos**: Crear, leer, actualizar y eliminar cursos
- **Base de Datos H2**: Almacenamiento en memoria con consola web
- **Service Discovery**: Registrado automáticamente en Eureka Server
- **API REST**: Endpoints para operaciones de cursos

## 🔧 Configuración

- **Puerto**: 8001
- **Nombre del Servicio**: `fx-course-service`
- **Base de Datos**: H2 en memoria
- **Consola H2**: http://localhost:8001/h2-console

## 📄 Datos de Prueba

El servicio incluye un `DataLoader` que carga cursos de ejemplo al inicio:

- Spring Boot Fundamentals
- Advanced Java Programming
- Microservices with Spring Cloud

## 🌐 Endpoints Disponibles

### Gestión de Cursos

- `GET /` - Home del servicio
- `GET /courses` - Lista todos los cursos
- `GET /{id}` - Obtiene un curso específico por ID
- `POST /courses` - Crea un nuevo curso
- `DELETE /{id}` - Elimina un curso por ID

### Monitoreo

- `GET /health` - Health check del servicio
- `GET /actuator/health` - Información detallada de salud

## 🚀 Ejecución

### Con Docker

```bash
docker-compose up course-app
```

### Local

```bash
mvn spring-boot:run
```

## 📊 Base de Datos H2

- **URL JDBC**: `jdbc:h2:mem:testdb`
- **Usuario**: `sa`
- **Contraseña**: (vacía)
- **Consola Web**: http://localhost:8001/h2-console

## 🛠️ Tecnologías

- Spring Boot 3.3.3
- Spring Data JPA
- Base de datos H2
- Spring Boot Actuator
- Netflix Eureka Client
