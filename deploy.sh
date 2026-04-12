#!/bin/bash

# Script to build Docker images and run separate docker-compose files in correct order
# Orden: Infraestructura (bases datos, redis, rabbitmq, discovery-server) -> Servicios de aplicación

# Función para mostrar ayuda
show_help() {
  echo "Uso: ./deploy.sh [OPCIONES] [SERVICIO]"
  echo ""
  echo "OPCIONES:"
  echo "  --all           Compilar todos los servicios (por defecto)"
  echo "  --build         Reconstruir imágenes Docker"
  echo "  --help          Mostrar esta ayuda"
  echo ""
  echo "SERVICIOS (compilar solo uno):"
  echo "  api-gateway"
  echo "  discovery-server"
  echo "  product-services"
  echo "  user-services"
  echo "  cart-services"
  echo "  order-services"
  echo ""
  echo "EJEMPLOS:"
  echo "  ./deploy.sh                    # Compilar todos y iniciar"
  echo "  ./deploy.sh product-services   # Compilar solo Product-services"
  echo "  ./deploy.sh user-services --build   # Compilar User-services y reconstruir imagen"
  echo ""
}

# Variables
BUILD_ALL=false
REBUILD_IMAGE=false
SERVICE_TO_BUILD=""

# Parsear argumentos
for arg in "$@"; do
  case $arg in
    --all)
      BUILD_ALL=true
      ;;
    --build)
      REBUILD_IMAGE=true
      ;;
    --help)
      show_help
      exit 0
      ;;
    *)
      if [ -z "$SERVICE_TO_BUILD" ]; then
        SERVICE_TO_BUILD="$arg"
      fi
      ;;
  esac
done

# Validar servicio si se especificó
if [ -n "$SERVICE_TO_BUILD" ]; then
  case $SERVICE_TO_BUILD in
    api-gateway|discovery-server|product-services|user-services|cart-services|order-services)
      # Converts kebab-case to PascalCase for directory name
      SERVICE_DIR="${SERVICE_TO_BUILD//-/}"
      SERVICE_DIR="$(echo $SERVICE_DIR | sed 's/\b\(.\)/\U\1/g')"
      echo "Building only $SERVICE_TO_BUILD..."
      ;;
    *)
      echo "Error: Servicio no reconocido: $SERVICE_TO_BUILD"
      show_help
      exit 1
      ;;
  esac
fi

build_project() {
  project_dir="$1"
  project_name="$2"
  echo "Building $project_name..."
  if [ -x "$project_dir/mvnw" ]; then
    "$project_dir/mvnw" -f "$project_dir/pom.xml" clean package -DskipTests
  else
    mvn -f "$project_dir/pom.xml" clean package -DskipTests
  fi
  if [ $? -ne 0 ]; then
    echo "Failed to build $project_name"
    exit 1
  fi
}

build_image() {
  image_name="$1"
  project_dir="$2"
  echo "Building Docker image: $image_name..."
  docker build -t $image_name $project_dir
  if [ $? -ne 0 ]; then
    echo "Failed to build image: $image_name"
    exit 1
  fi
}

# Compilar servicios
if [ -n "$SERVICE_TO_BUILD" ]; then
  # Compilar solo el servicio específico
  case $SERVICE_TO_BUILD in
    api-gateway)
      build_project ./API-Gateway "API-Gateway"
      [ "$REBUILD_IMAGE" = true ] && build_image "api-gateway" "./API-Gateway"
      ;;
    discovery-server)
      build_project ./Discovery-server "Discovery-server"
      [ "$REBUILD_IMAGE" = true ] && build_image "discovery-server" "./Discovery-server"
      ;;
    product-services)
      build_project ./Product-services "Product-services"
      [ "$REBUILD_IMAGE" = true ] && build_image "product-services" "./Product-services"
      ;;
    user-services)
      build_project ./User-services "User-services"
      [ "$REBUILD_IMAGE" = true ] && build_image "user-services" "./User-services"
      ;;
    cart-services)
      build_project ./Cart-services "Cart-services"
      [ "$REBUILD_IMAGE" = true ] && build_image "cart-services" "./Cart-services"
      ;;
    order-services)
      build_project ./Order-services "Order-services"
      [ "$REBUILD_IMAGE" = true ] && build_image "order-services" "./Order-services"
      ;;
  esac
else
  # Compilar todos los servicios
  echo "Building project executables for all services..."
  
  build_project ./API-Gateway "API-Gateway"
  build_project ./Discovery-server "Discovery-server"
  build_project ./Product-services "Product-services"
  build_project ./User-services "User-services"
  build_project ./Cart-services "Cart-services"
  build_project ./Order-services "Order-services"
  # build_project ./Payment-services "Payment-services"
  # build_project ./Notification-services "Notification-services"

  echo "Project executables built successfully."

  echo "Building Docker images for all services..."

  docker build -t api-gateway ./API-Gateway
  docker build -t discovery-server ./Discovery-server
  docker build -t product-services ./Product-services
  docker build -t user-services ./User-services
  docker build -t cart-services ./Cart-services
  docker build -t order-services ./Order-services
  # docker build -t payment-services ./Payment-services
  # docker build -t notification-services ./Notification-services

  echo "Images built successfully."
fi

echo "Creating external network..."
docker network create e-commerce-network 2>/dev/null || true

echo ""
echo "=========================================="
echo "Orden de ejecución:"
echo "1. Infraestructura (PostgreSQL, Redis, RabbitMQ, Discovery Server)"
echo "2. Servicios de Aplicación"
echo "=========================================="

if command -v docker-compose >/dev/null 2>&1; then
  COMPOSE_CMD="docker-compose"
elif command -v docker >/dev/null 2>&1 && docker compose version >/dev/null 2>&1; then
  COMPOSE_CMD="docker compose"
else
  echo "Error: docker-compose or docker compose is required."
  exit 1
fi

# Solo reiniciar infraestructura si todos los servicios se compilan
if [ -z "$SERVICE_TO_BUILD" ] || [ "$SERVICE_TO_BUILD" = "discovery-server" ]; then
  echo ""
  echo ">>> Iniciando infraestructura (PostgreSQL, Redis, RabbitMQ, Discovery Server)..."
  $COMPOSE_CMD -f docker-compose.infra.yml up -d

  echo ""
  echo ">>> Esperando a que la infraestructura esté lista..."
  check_infra_health() {
    for service in postgres_product postgres_user postgres_cart postgres_order redis rabbitmq; do
      status=$(docker inspect --format='{{.State.Health.Status}}' $service 2>/dev/null || echo "unhealthy")
      if [ "$status" != "healthy" ]; then
        return 1
      fi
    done
    return 0
  }

  retry_count=0
  while ! check_infra_health && [ $retry_count -lt 20 ]; do
    echo "Esperando servicios de infraestructura... ($retry_count/20)"
    sleep 5
    retry_count=$((retry_count + 1))
  done

  if [ $retry_count -eq 20 ]; then
    echo "⚠ Timeout en infraestructura. Continuando de todas formas..."
  else
    echo "✓ Infraestructura lista"
  fi

  echo ""
  echo ">>> Esperando a que Discovery Server (Eureka) esté listo..."
  retry_count=0
  while [ $retry_count -lt 20 ]; do
    if docker logs discovery-server 2>/dev/null | grep -q "Tomcat started on port(s)"; then
      echo "✓ Discovery Server listo"
      break
    fi
    echo "Esperando Discovery Server... ($retry_count/20)"
    sleep 5
    retry_count=$((retry_count + 1))
  done
fi

# Iniciar o reiniciar servicios
if [ -n "$SERVICE_TO_BUILD" ]; then
  echo ""
  echo ">>> Reiniciando servicio: $SERVICE_TO_BUILD..."
  # Convertir a nombre de contenedor
  CONTAINER_NAME=$(echo $SERVICE_TO_BUILD | tr '[:upper:]' '[:lower:]')
  
  # Parar y eliminar el contenedor anterior si existe
  docker stop $CONTAINER_NAME 2>/dev/null
  docker rm $CONTAINER_NAME 2>/dev/null
  
  # Iniciar el nuevo contenedor con docker-compose
  $COMPOSE_CMD -f docker-compose.services.yml up -d $CONTAINER_NAME
  
  echo "✓ Servicio $SERVICE_TO_BUILD reiniciado"
else
  echo ""
  echo ">>> Iniciando servicios de aplicación..."
  $COMPOSE_CMD -f docker-compose.services.yml up -d
fi

echo ""
echo "✓ Operación completada"
echo ""
echo "=========================================="
echo "Servicios disponibles:"
echo "  - API Gateway:      http://localhost:8081"
echo "  - Eureka Dashboard: http://localhost:8761"
echo "  - RabbitMQ:         http://localhost:15672 (admin/admin123)"
echo "=========================================="
echo ""
echo "Para probar:"
echo "  curl -X GET http://localhost:8081/e-commerce/api/v1/product"
echo ""

# Manejar flag --build
if [ "$REBUILD_IMAGE" = true ]; then
  if [ -n "$SERVICE_TO_BUILD" ]; then
    echo "Image for $SERVICE_TO_BUILD already rebuilt and restarted."
  else
    echo "Images already rebuilt and services restarted."
  fi
fi