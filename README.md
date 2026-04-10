# Devices API

Spring Boot REST API for managing devices, backed by PostgreSQL, JPA, Flyway, and Swagger/OpenAPI.

## Features

- CRUD operations for devices
- Filter devices by `brand` or `state`
- Swagger UI and OpenAPI docs
- Flyway database migrations
- Docker and Docker Compose support

## Tech Stack

- Java 21
- Spring Boot 4.0.5
- Spring Web
- Spring Data JPA
- PostgreSQL
- Flyway
- Springdoc OpenAPI
- Maven
- Docker

## Project Structure

```text
src/main/java/com/example/demo
├── config
├── controller
├── domain
├── dto
├── exception
├── repository
└── service

src/main/resources
├── application.yml
└── db/migration
```

## Prerequisites

- Java 21
- Maven 3.9+ or use `./mvnw`
- PostgreSQL running locally
- Docker Desktop or Docker Engine if using containers

## Configuration

Application properties are defined in [application.yml](/Users/sonia/Downloads/Demo/src/main/resources/application.yml).

Important environment variables:

- `DB_URL` default: `jdbc:postgresql://localhost:5432/demo`
- `DB_USERNAME` default: `sonia`
- `DB_PASSWORD` default: empty
- `PORT` default: `8080`

## Running Locally

1. Create the local database:

```sql
create database demo;
```

2. Start the application:

```bash
./mvnw spring-boot:run
```

3. Open Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/api-docs
```

## Running with Docker

The project includes:

- [Dockerfile](/Users/sonia/Downloads/Demo/Dockerfile)
- [compose.yaml](/Users/sonia/Downloads/Demo/compose.yaml)
- [.dockerignore](/Users/sonia/Downloads/Demo/.dockerignore)

Start the full stack:

```bash
docker compose up --build
```

Stop it:

```bash
docker compose down
```

Stop and remove the database volume:

```bash
docker compose down -v
```

## Database Migrations

Flyway migration files are stored in:

- [V1__create_devices_table.sql](/Users/sonia/Downloads/Demo/src/main/resources/db/migration/V1__create_devices_table.sql)
- [V2__insert_sample_devices.sql](/Users/sonia/Downloads/Demo/src/main/resources/db/migration/V2__insert_sample_devices.sql)

Important Flyway behavior:

- Each migration version runs only once per database.
- If you delete rows from `devices`, Flyway will not rerun `V2` on restart.
- To reseed data, either reset the database or add a new migration such as `V3__insert_sample_devices.sql`.

## Resetting the Local Database

Connect to the `postgres` database first:

```bash
psql -U sonia -d postgres
```

Terminate active sessions using `demo`:

```sql
select pg_terminate_backend(pid)
from pg_stat_activity
where datname = 'demo'
  and pid <> pg_backend_pid();
```

Drop and recreate the database:

```sql
drop database if exists demo;
create database demo;
```

Start the application again:

```bash
./mvnw spring-boot:run
```

## API Endpoints

Base path:

```text
/api/v1/devices
```

Endpoints:

- `POST /api/v1/devices` create a device
- `PUT /api/v1/devices/{id}` update a device
- `GET /api/v1/devices/{id}` fetch a device by id
- `GET /api/v1/devices` fetch all devices
- `GET /api/v1/devices?brand=Apple` filter by brand
- `GET /api/v1/devices?state=AVAILABLE` filter by state
- `DELETE /api/v1/devices/{id}` delete a device

## Sample Request

Create a device:

```bash
curl -X POST http://localhost:8080/api/v1/devices \
  -H "Content-Type: application/json" \
  -d '{
    "name": "iPhone 15 Pro",
    "brand": "Apple",
    "state": "AVAILABLE"
  }'
```

## Data Model

`Device` fields:

- `id` UUID
- `name` string
- `brand` string
- `state` enum: `AVAILABLE`, `IN_USE`, `INACTIVE`
- `createdAt` timestamp

## Business Rules

- A device in `IN_USE` state cannot have its `name` or `brand` changed.
- A device in `IN_USE` state cannot be deleted.
- Filtering by both `brand` and `state` at the same time is rejected.

## Validation and Errors

Create request validation:

- `name` is required
- `brand` is required
- `state` is required

Error responses are returned as the custom `ErrorResponse` payload with:

- `status`
- `message`
- `errors`
- `timestamp`

## Verifying Database State

Check migrations:

```sql
select * from public.flyway_schema_history;
```

Check data:

```sql
select * from public.devices;
```

Check current connection:

```sql
select current_database(), current_schema();
```

## Build and Test

Run tests:

```bash
./mvnw test
```

Build the jar:

```bash
./mvnw clean package
```

## Troubleshooting

### Swagger UI returns `/api-docs` 500

Make sure you are running the updated dependency set from [pom.xml](/Users/sonia/Downloads/Demo/pom.xml) and restart the app fully.

### `devices` table is missing

This usually means Flyway migrations did not run against the database you are checking, or you are connected to a different database/schema.

Use:

```sql
select current_database(), current_schema();
select * from public.flyway_schema_history;
```

### `devices` table exists but has no rows

That is expected if:

- `V2` already ran previously
- rows were deleted later

Flyway will not rerun `V2` automatically.

## Git

Add a remote:

```bash
git remote add origin <repo-url>
```

Push:

```bash
git add .
git commit -m "Add project documentation"
git push -u origin main
```
