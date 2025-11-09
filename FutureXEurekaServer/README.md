# FutureX Eureka Server

## 📋 Descripción

Servidor de descubrimiento de servicios basado en Netflix Eureka. Este componente central permite que los microservicios se registren y descubran entre sí de forma automática.

## 🎯 Propósito

- **Registry Central**: Mantiene un registro de todos los servicios disponibles
- **Service Discovery**: Permite a los servicios encontrar y comunicarse entre sí
- **Health Monitoring**: Supervisa la salud de los servicios registrados
- **Load Balancing**: Facilita la distribución de carga entre instancias

## 🔧 Configuración

- **Puerto**: 8761
- **Nombre del Servicio**: `fx-discovery-server`
- **Modo**: No se registra a sí mismo (`eureka.client.register-with-eureka=false`)

## 🚀 Ejecución

### Con Docker

```bash
docker-compose up eureka-server
```

### Local

```bash
mvn spring-boot:run
```

## 🌐 Acceso

- **Dashboard**: http://localhost:8761
- **Health Check**: http://localhost:8761/actuator/health

## 📊 Servicios Registrados

Una vez ejecutados todos los microservicios, el dashboard mostrará:

- `fx-course-service` (Puerto 8001)
- `fx-catalog-service` (Puerto 8002)

## 🛠️ Tecnologías

- Spring Boot 3.3.3
- Spring Cloud 2023.0.3
- Netflix Eureka Server
- Spring Boot Actuator
