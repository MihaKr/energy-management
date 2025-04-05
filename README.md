# Energy Management System

Backend sistem za **upravljanje porabe energije** v različnih zgradbah. Sistem omogoča spremljanje meritev porabe energije, evidentiranje naprav ter povezovanje naprav z zgradbami.

### Tehnologije

* **Quarkus** kot backend framework
* **PostgreSQL** kot podatkovna baza
* **GraphQL** za izpostavitev API-jev (CRUDL)
* **Hibernate ORM** za mapiranje entitet
* **Flyway** za upravljanje migracij baze podatkov
* **Docker & Docker Compose** za orkestracijo

### Podatkovni model

1. **Building (Zgradba)**
    * Podatki o zgradbi (ime, lokacija)

2. **Device (Naprava)**
    * Podatki o napravi (tip: HVAC, SOLAR, METER, proizvajalec)

3. **Measurement (Meritev)**
    * Meritve porabe energije naprav v zgradbah s časovno značko  (naprava, zgradba, čas, vrednost)

4. **DeviceBuilding (Povezava naprava-zgradba)**
    * Povezovalna tabela med napravami in zgradbami z datumom namestitve (naprava, zgradba, datum_namestitve)

## English Description

A backend system for **energy consumption management** across different buildings. The system enables monitoring of energy consumption measurements, device registration, and connecting devices with buildings.

### Technologies

* **Quarkus** as the backend framework
* **PostgreSQL** as the database
* **GraphQL** for API exposure (CRUDL)
* **Hibernate ORM** for entity mapping
* **Flyway** for database migration management
* **Docker & Docker Compose** for orchestration

### Data Model

1. **Building**
    * Building information (name, location)

2. **Device**
    * Device information (type: HVAC, SOLAR, METER, manufacturer)

3. **Measurement**
    * Energy usage measurements of devices in buildings with timestamps  (device, building, timestamp, value)

4. **DeviceBuilding**
    * Junction table connecting devices to buildings with installation date (device, building, installed_since)

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven
- Docker and Docker Compose

Or use the default values in the docker-compose.yml file.

### Running the Application

1. **Build the application**:

```bash
./mvnw package
```

2. **Start the application with Docker Compose**:

```bash
docker-compose up -d
```

3. **Access the GraphQL UI**:

Open your browser and go to: [http://localhost:8080/q/graphql-ui](http://localhost:8080/q/graphql-ui)

### Running in Development Mode

For local development with hot reloading:

```bash
./mvnw quarkus:dev
```

This will start Quarkus in development mode with the UI available at [http://localhost:8080/q/dev/](http://localhost:8080/q/dev/).

## GraphQL API

The API provides complete CRUD operations for all entities:

### Example Queries

Here are some example queries you can try in the GraphQL UI:

**Get a Building by ID**
```graphql
query {
  getBuilding(buildingId: 1) {
    id
    name
    location
  }
}
```

**Get Energy Consumption for a Building in a Time Range**
```graphql
query {
  getEnergyConsumptionByBuilding(
    buildingId: 1, 
    from: "2024-04-01T00:00:00Z"", 
    to: "2024-04-07T00:00:00Z"
  )
}
```

**Create a New Device**
```graphql
mutation {
  createDevice(input: {
    type: HVAC
    manufacturer: "Siemens"
  }) {
    id
    type
    manufacturer
  }
}
```

**Add a Measurement**
```graphql
mutation {
  createMeasurement(input: {
    deviceId: 1
    buildingId: 1
    timestamp: "2024-04-01T00:00:00Z"
    energyKwh: 42.5
  }) {
    id
    timestamp
    energyKwh
  }
}
```

**Connect a Device to a Building**
```graphql
mutation {
  createDeviceBuilding(input: {
    deviceId: 1
    buildingId: 1
    installedSince: "2024-04-01T00:00:00Z"
  }) {
    deviceId
    buildingId
    installedSince
  }
}
```

### Queries

- `getBuilding(buildingId: ID!): Building`
- `getAllBuildings(limit: Int, offset: Int, orderDirection: String): [Building]`
- `getDevice(deviceId: ID!): Device`
- `getAllDevices(limit: Int, offset: Int, orderDirection: String): [Device]`
- `getMeasurement(measurementId: ID!): Measurement`
- `getAllMeasurements(limit: Int, offset: Int, orderDirection: String): [Measurement]`
- `getDeviceBuilding(deviceId: ID!, buildingId: ID!): DeviceBuilding`
- `getAllDeviceBuilding(limit: Int, offset: Int, orderDirection: String): [DeviceBuilding]`

### Special Queries

- `getEnergyConsumptionByBuilding(buildingId: ID!, from: Instant, to: Instant): Float`
- `getEnergyConsumptionByDevice(deviceId: ID!, from: Instant, to: Instant): Float`
- `getDevicesByBuilding(buildingId: ID!): [Device]`

### Mutations

- `createBuilding(input: BuildingInput!): Building`
- `updateBuilding(input: BuildingInput!): Building`
- `deleteBuilding(input: BuildingInput!): Boolean`
- `createDevice(input: DeviceInput!): Device`
- `updateDevice(input: DeviceInput!): Device`
- `deleteDevice(input: DeviceInput!): Boolean`
- `createMeasurement(input: MeasurementInput!): Measurement`
- `updateMeasurement(input: MeasurementInput!): Measurement`
- `removeMeasurement(input: MeasurementInput!): Boolean`
- `createDeviceBuilding(input: DeviceBuildingInput!): DeviceBuilding`
- `updateDeviceBuilding(input: DeviceBuildingInput!): DeviceBuilding`
- `deleteDeviceBuilding(input: DeviceBuildingInput!): Boolean`

## Testing

Run the tests with:

```bash
./mvnw test
```

The project includes tests for all GraphQL endpoints with specific tests for edge cases and data validation.

## Building Native Executable

You can create a native executable using:

```bash
./mvnw package -Dnative
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container:

```bash
./mvnw package -Dnative -Dquarkus.native.container-build=true
```

## Project Structure

- `src/main/java/com/example/energy/entity/` - Data model entities
- `src/main/java/com/example/energy/repository/` - Data access layer
- `src/main/java/com/example/energy/service/` - Business logic
- `src/main/java/com/example/energy/graphql/` - GraphQL endpoints
- `src/main/java/com/example/energy/graphql/input/` - GraphQL input DTOs
- `src/main/resources/db/migration/` - Flyway database migrations
- `src/main/docker/` - Docker configuration files
- `src/test/java/com/example/energy/Graphql/` - GraphQL endpoint tests