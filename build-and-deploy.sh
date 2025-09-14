#!/bin/bash

# Script para construir y desplegar los microservicios bancarios

echo "=== Construyendo y desplegando Banking Microservices ==="

# Verificar que Docker esté ejecutándose
if ! docker info > /dev/null 2>&1; then
    echo "Error: Docker no está ejecutándose. Por favor, inicia Docker y vuelve a intentar."
    exit 1
fi

# Construir las imágenes Docker
echo "1. Construyendo imágenes Docker..."

# Eureka Server
echo "Construyendo Eureka Server..."
cd eureka-server
mvn clean package -DskipTests
docker build -t banking-microservices/eureka-server:latest .
cd ..

# Cliente-Persona Service
echo "Construyendo Cliente-Persona Service..."
cd cliente-persona-service
mvn clean package -DskipTests
docker build -t banking-microservices/cliente-persona-service:latest .
cd ..

# Cuenta-Movimiento Service
echo "Construyendo Cuenta-Movimiento Service..."
cd cuenta-movimiento-service
mvn clean package -DskipTests
docker build -t banking-microservices/cuenta-movimiento-service:latest .
cd ..

# API Gateway
echo "Construyendo API Gateway..."
cd api-gateway
mvn clean package -DskipTests
docker build -t banking-microservices/api-gateway:latest .
cd ..

echo "2. Iniciando servicios con Docker Compose..."

# Detener contenedores existentes
docker compose down

# Iniciar servicios
docker compose up -d

echo "3. Esperando a que los servicios estén listos..."

# Esperar a que MySQL esté listo
echo "Esperando a que MySQL esté listo..."
while ! docker exec banking-mysql mysqladmin ping -h localhost --silent; do
    sleep 2
done

# Esperar a que Eureka esté listo
echo "Esperando a que Eureka esté listo..."
while ! curl -f http://localhost:8761/actuator/health > /dev/null 2>&1; do
    sleep 5
done

# Esperar a que los microservicios se registren en Eureka
echo "Esperando a que los microservicios se registren..."
sleep 30

echo "4. Verificando estado de los servicios..."

# Verificar Eureka
echo "Eureka Server: http://localhost:8761"
curl -s http://localhost:8761/actuator/health | jq .

# Verificar API Gateway
echo "API Gateway: http://localhost:8080"
curl -s http://localhost:8080/actuator/health | jq .

# Verificar Cliente-Persona Service
echo "Cliente-Persona Service: http://localhost:8081"
curl -s http://localhost:8081/actuator/health | jq .

# Verificar Cuenta-Movimiento Service
echo "Cuenta-Movimiento Service: http://localhost:8082"
curl -s http://localhost:8082/actuator/health | jq .

echo ""
echo "=== Despliegue completado ==="
echo ""
echo "Servicios disponibles:"
echo "- Eureka Server: http://localhost:8761"
echo "- API Gateway: http://localhost:8080"
echo "- Cliente-Persona Service: http://localhost:8081"
echo "- Cuenta-Movimiento Service: http://localhost:8082"
echo "- MySQL: localhost:3306"
echo "- Kafka: localhost:9092"
echo ""
echo "Para probar los endpoints, usa la colección de Postman: Banking_Microservices.postman_collection.json"
echo ""
echo "Para detener los servicios: docker compose down"
