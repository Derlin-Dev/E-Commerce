# E-Commerce Microservices Project

Este repositorio contiene una arquitectura de microservicios para una plataforma de comercio electrónico construida con Spring Boot. El proyecto incluye varios servicios independientes, un gateway de API, un servidor de descubrimiento y una capa de infraestructura que utiliza PostgreSQL, Redis y RabbitMQ.

## Estructura del proyecto

- `API-Gateway/` - Gateway de entrada y enrutamiento de solicitudes.
- `Discovery-server/` - Servidor de descubrimiento para registrar y localizar servicios.
- `Product-services/` - Servicio de catálogo de productos.
- `User-services/` - Servicio de gestión de usuarios.
- `Cart-services/` - Servicio de carrito de compras.
- `Order-services/` - Servicio de pedidos.
- `Payment-services/` - Servicio de pagos.
- `Notification-services/` - Servicio de notificaciones.

## Contenedores y despliegue

Se han creado configuraciones de Docker y Docker Compose para ejecutar el proyecto en contenedores.

### Archivos principales

- `docker-compose.infra.yml` - Define la infraestructura externa:
  - PostgreSQL para las bases de datos de producto, usuario, carrito y órdenes.
  - Redis para caché/conexiones.
  - RabbitMQ para mensajería.
  - `Discovery-server`.

- `docker-compose.services.yml` - Define los servicios de la aplicación que se ejecutan sobre la infraestructura.
- `deploy.sh` - Script de despliegue que:
  1. Construye los ejecutables Maven de cada servicio en secuencia.
  2. Construye las imágenes Docker.
  3. Levanta primero la infraestructura con `docker-compose.infra.yml`.
  4. Espera que PostgreSQL, Redis y RabbitMQ estén listos antes de iniciar los servicios.

## Cómo desplegar el proyecto

1. Asegúrate de tener instalado Docker y Docker Compose.
2. Abre una terminal en la raíz del repositorio.
3. Haz ejecutable el script (si no lo está):

```bash
chmod +x deploy.sh
```

4. Ejecuta el script:

```bash
./deploy.sh
```

Este script compilará los servicios en orden y levantará primero la infraestructura.

### Notas importantes

- `payment-services` y `notification-services` están comentados actualmente para no construirse ni iniciarse en este despliegue.
- El script de despliegue espera que los servicios de infraestructura estén saludables antes de continuar.

## Cómo probar y validar

- Accede al servidor de descubrimiento en `http://localhost:8761`.
- El gateway de API estará disponible en `http://localhost:8081`.
- Los servicios individuales se exponen en los puertos configurados en `docker-compose.services.yml`.

## Mejora de imágenes Docker

Cada servicio utiliza un `Dockerfile` con multi-stage build. Esto permite:
- compilar la aplicación con Maven en un stage de construcción,
- copiar solo el JAR resultante a una imagen ligera de runtime (`eclipse-temurin:21-jre-alpine`).

## Estructura de redes y volúmenes

- Todos los servicios y la infraestructura se conectan a la red `e-commerce-network`.
- Las bases de datos PostgreSQL usan volúmenes persistentes para conservar datos.

## Recomendaciones

- Para activar `payment-services` y `notification-services`, habilita sus bloques en `docker-compose.services.yml` y descomenta las líneas de build correspondientes en `deploy.sh`.
- Para actualizar imágenes, ejecuta el script con `./deploy.sh --update` después de completar los cambios.
