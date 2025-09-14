# Banking Microservices
Sistema bancario implementado con arquitectura de microservicios usando Spring Boot, JPA, Docker y comunicación asíncrona.

## 📋 Información del Repositorio
- **Repositorio**: https://github.com/lukcho/banking-microservices
- **Clonación**: `git clone https://github.com/lukcho/banking-microservices.git`

## Arquitectura
El sistema está compuesto por los siguientes microservicios:

1. **Eureka Server** (Puerto 8761) - Servidor de descubrimiento de servicios
2. **API Gateway** (Puerto 8080) - Punto de entrada único para todas las APIs
3. **Cliente-Persona Service** (Puerto 8081) - Gestión de clientes y personas
4. **Cuenta-Movimiento Service** (Puerto 8082) - Gestión de cuentas y movimientos
5. **MySQL** (Puerto 3306) - Base de datos relacional
6. **Kafka** (Puerto 9092) - Sistema de mensajería asíncrona

## Tecnologías Utilizadas
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Cloud 2023.0.0**
- **Spring Data JPA**
- **MySQL 8.0**
- **Docker & Docker Compose**
- **Apache Kafka**
- **Netflix Eureka**
- **Spring Cloud Gateway**

## Funcionalidades Implementadas
### F1,F2, F3. F4. F5, F6. F7

## Estructura del Proyecto
```
banking-microservices/
├── eureka-server/                 # Servidor de descubrimiento
├── cliente-persona-service/       # Microservicio de clientes
├── cuenta-movimiento-service/     # Microservicio de cuentas y movimientos
├── api-gateway/                   # API Gateway
├── docker-compose.yml            # Orquestación de contenedores
├── BaseDatos.sql                 # Script de base de datos
├── build-and-deploy.sh           # Script de construcción y despliegue
└── Banking_Microservices.postman_collection.json  # Colección de Postman
```

## Patrones Implementados
- Repositorio
- Capa de Servicio
- DTO

## Endpoints de la API
### Clientes (`/api/clientes`)
- `GET /api/clientes` - Obtener todos los clientes
- `GET /api/clientes/{id}` - Obtener cliente por ID
- `GET /api/clientes/identificacion/{identificacion}` - Obtener por identificación
- `POST /api/clientes` - Crear nuevo cliente
- `PUT /api/clientes/{id}` - Actualizar cliente
- `DELETE /api/clientes/{id}` - Eliminar cliente

### Cuentas (`/api/cuentas`)
- `GET /api/cuentas` - Obtener todas las cuentas
- `GET /api/cuentas/{id}` - Obtener cuenta por ID
- `GET /api/cuentas/numero/{numero}` - Obtener por número de cuenta
- `GET /api/cuentas/cliente/{clienteId}` - Obtener cuentas de un cliente
- `POST /api/cuentas` - Crear nueva cuenta
- `PUT /api/cuentas/{id}` - Actualizar cuenta
- `DELETE /api/cuentas/{id}` - Eliminar cuenta

### Movimientos (`/api/movimientos`)
- `GET /api/movimientos` - Obtener todos los movimientos
- `GET /api/movimientos/{id}` - Obtener movimiento por ID
- `GET /api/movimientos/cuenta/{cuentaId}` - Obtener movimientos de una cuenta
- `POST /api/movimientos` - Crear nuevo movimiento
- `DELETE /api/movimientos/{id}` - Eliminar movimiento

### Reportes (`/api/reportes`)
- `GET /api/reportes?clienteId={id}&fechaInicio={fecha}&fechaFin={fecha}` - Estado de cuenta

## Instalación y Despliegue
### Prerrequisitos
- Java 17 o superior
- Maven 3.6 o superior
- Docker y Docker Compose
- Git

### Instalación de Docker (macOS)
#### Opción 1: Docker Desktop (Recomendado)
1. Ve a https://www.docker.com/products/docker-desktop/
2. Descarga Docker Desktop para Mac
3. Instala la aplicación
4. Inicia Docker Desktop

#### Opción 2: Usando Homebrew
```bash
# Instalar Homebrew (si no lo tienes)
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Configurar PATH
echo 'eval "$(/opt/homebrew/bin/brew shellenv)"' >> ~/.zprofile
eval "$(/opt/homebrew/bin/brew shellenv)"

# Instalar Docker Desktop
brew install --cask docker

# Iniciar Docker Desktop
open /Applications/Docker.app
```

### Pasos de Instalación
1. **Clonar el repositorio**
```bash
git clone https://github.com/lukcho/banking-microservices.git
cd banking-microservices
```

2. **Verificar que Docker esté ejecutándose**
```bash
docker info
```

3. **Construir y desplegar automáticamente**
```bash
chmod +x build-and-deploy.sh
./build-and-deploy.sh
```

4. **Construir y desplegar manualmente**
```bash
# Construir el proyecto
mvn clean package -DskipTests

# Construir imágenes Docker
docker compose build

# Iniciar servicios
docker compose up -d
```

### Verificación del Despliegue
1. **Eureka Server**: http://localhost:8761
2. **API Gateway**: http://localhost:8080
3. **Cliente-Persona Service**: http://localhost:8081
4. **Cuenta-Movimiento Service**: http://localhost:8082


### Verificación Rápida
Antes de desplegar, verifica que todo esté listo:

```bash
# 1. Verificar que Docker esté ejecutándose
docker --version
docker info

# 2. Verificar que Maven compile correctamente
mvn clean compile -q

# 3. Verificar que el empaquetado funcione
mvn clean package -DskipTests -q

# 4. Si todo está bien, desplegar
chmod +x build-and-deploy.sh
./build-and-deploy.sh
```

### Próximos Pasos
1. **Ejecutar el script de despliegue**:
   ```bash
   chmod +x build-and-deploy.sh
   ./build-and-deploy.sh
   ```

## Pruebas
### Ejecutar Pruebas Unitarias
```bash
# Cliente-Persona Service
cd cliente-persona-service
mvn test

# Cuenta-Movimiento Service
cd cuenta-movimiento-service
mvn test
```

### Pruebas con Postman
1. Importar la colección `Banking_Microservices.postman_collection.json`
2. Configurar la variable `base_url` como `http://localhost:8080`
3. Ejecutar las pruebas de los endpoints

## Ejemplos de Uso

### Crear un Cliente
```bash
curl -X POST http://localhost:8080/api/clientes \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Juan Pérez",
    "genero": "M",
    "edad": 30,
    "identificacion": "12345678",
    "direccion": "Calle 123 #45-67",
    "telefono": "3001234567",
    "contrasena": "password123",
    "estado": true
  }'
```

### Crear una Cuenta
```bash
curl -X POST http://localhost:8080/api/cuentas \
  -H "Content-Type: application/json" \
  -d '{
    "numeroCuenta": "225487",
    "tipoCuenta": "Corriente",
    "saldoInicial": 100.00,
    "estado": true,
    "clienteId": 1
  }'
```

### Realizar un Depósito
```bash
curl -X POST http://localhost:8080/api/movimientos \
  -H "Content-Type: application/json" \
  -d '{
    "tipoMovimiento": "Deposito",
    "valor": 500.00,
    "cuentaId": 1
  }'
```

### Generar Estado de Cuenta
```bash
curl "http://localhost:8080/api/reportes?clienteId=1&fechaInicio=2024-01-01T00:00:00&fechaFin=2024-12-31T23:59:59"
```