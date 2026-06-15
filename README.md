# Customer Management Application

## Overview

This is a simple full-stack Customer Management application (Spring Boot backend + React frontend) used for demos and local development. It stores customer records in an H2 in-memory database and exposes a small REST API consumed by the React UI.

Key features
- Create and list customers
- Basic validation and duplicate-detection (unique constraint on firstName+lastName+dateOfBirth)
- In-memory H2 database for easy local development

## Tech Stack

Backend
- Java (project supports modern JDKs)
- Spring Boot (REST + Spring Data JPA)
- H2 (in-memory DB for development)
- Maven

Frontend
- React
- Redux Toolkit
- Axios

## Model

Customer fields
- `id`: Long
- `firstName`: String
- `lastName`: String
- `dateOfBirth`: yyyy-MM-dd

## API

All API responses use a common envelope `ApiResponse`:

{
  "code": <number>,
  "message": "<text>",
  "data": <object | array | null>
}

Endpoints
- POST `/customers` — create a customer (accepts JSON with `firstName`, `lastName`, `dateOfBirth`).
- GET `/customers` — returns all customers.

Behavior for duplicates and errors
- Unique constraint violations are surfaced by the backend and mapped to a validation-like response (HTTP 400) with `data` containing a field-level message, e.g.: `{"code":400,"message":"Validation failed","data":{"duplicate":"Customer with same firstName, lastName and dateOfBirth already exists"}}`.
- Other DB errors return a 500 with `message` and `data` describing the issue.

## Ports / Configuration
- Backend default port: `8482` (see `backend/src/main/resources/application.properties`).
- Frontend dev server default port: `3000`.
- Frontend will use `REACT_APP_API_BASE_URL` to override the backend base URL. Default is `http://localhost:8482`.

## Run Locally

Backend
```bash
cd backend
mvn clean install
# Start the backend
mvn mvn spring-boot:run
```

Frontend
```bash
cd frontend
npm install   # if node_modules is missing
npm start
```

Open the UI at `http://localhost:3000`.


## Quick E2E (curl)

Create a customer:

```bash
curl -v -H "Content-Type: application/json" -d '{"firstName":"Test","lastName":"User","dateOfBirth":"1990-01-01"}' http://localhost:8482/customers
```

Create same customer again to see duplicate handling (should return 400 with validation `data`):

```bash
curl -v -H "Content-Type: application/json" -d '{"firstName":"Test","lastName":"User","dateOfBirth":"1990-01-01"}' http://localhost:8482/customers
```

List customers:

```bash
curl http://localhost:8482/customers
```

## Frontend error handling notes

- The frontend maps backend errors into either a string message or a structured object of field errors. The UI displays either a single message or lists field-level messages (e.g., the duplicate message). If you see a raw DB message in the UI, update the backend error mapping or the frontend `CustomersApi.getApiErrorMessage` to sanitize it.

## Files of interest
- `backend/src/main/java/com/customer/controller/CustomerController.java`
- `backend/src/main/java/com/customer/service/CustomerService.java`
- `backend/src/main/java/com/customer/config/GlobalExceptionHandler.java` (maps DB integrity errors to validation-like responses)
- `frontend/src/components/CustomerForm.jsx` (form and error display)
- `frontend/src/api/CustomersApi.js` (maps and normalizes backend errors)

## Next steps / Improvements

- Add inline form field error binding (map `data` keys to input fields)
- Add end-to-end tests (Cypress / Playwright)
- Add Docker compose for local, repeatable environment

---

If you'd like, I can commit these changes, open a PR, or wire inline form-field errors next.

## Design Decisions

This project is intentionally small and focused on developer ergonomics for local development and demos. Key design choices and rationale:

- Backend framework: **Spring Boot + Spring Data JPA** — fast to scaffold REST APIs and integrate with JPA/H2 for demos.
- Database: **H2 (in-memory)** — zero setup for local development and CI; schema is created/updated on startup using JPA DDL (`spring.jpa.hibernate.ddl-auto=update`). Tradeoff: data is ephemeral and not suitable for production.
- DTOs and Entities: `CustomerDTO` is used to validate and map request payloads to `Customer` entity to keep persistence concerns separate from API contract.
- Error handling: a centralized `GlobalExceptionHandler` maps `MethodArgumentNotValidException` to a 400 validation response. `DataIntegrityViolationException` (unique constraint) is mapped to a validation-like 400 with a `data` object describing the duplicate field. The service rethrows integrity exceptions so the global handler can format responses consistently.
- API envelope: every API response uses `ApiResponse { code, message, data }` — simplifies client logic and error handling.
- Frontend: React + Redux Toolkit for predictable state management. `CustomersApi.getApiErrorMessage` normalizes backend errors into either a string or structured field-errors object so the UI can render friendly messages.

## Project Conventions

- Backend port: `8482` (set in `backend/src/main/resources/application.properties`).
- Frontend dev server: `3000` (see `frontend/package.json` scripts).
- Tests: backend uses Maven+Surefire and includes unit + lightweight integration tests (see `DuplicateCustomerIntegrationTest`).

## Contribution

If you plan to extend the project:

- Add inline field-level error binding in `frontend/src/components/CustomerForm.jsx` to attach messages directly to inputs.
- Add persistent DB and docker-compose for local reproducible environment.
- Add e2e tests (Cypress or Playwright) for the duplicate scenario and UI flows.

---

