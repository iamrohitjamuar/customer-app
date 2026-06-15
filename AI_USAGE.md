# AI Usage and Validation Report

This document records how AI was used during development, what was delegated to AI vs done manually, how generated code was validated, examples of corrected AI mistakes, a rough time breakdown, and notes on impact.

## Tools used
- ChatGPT (assistant) — primary AI used to analyze the codebase, generate fixes, create tests, and run commands inside the workspace. (Model used for responses: GPT-5 mini.)
- The persona used in the editor is `GitHub Copilot` (the assistant presented itself as a coding partner named GitHub Copilot).

No other external AI tools (Claude, Bard, etc.) were used.

## What I (AI) did vs what the developer did

Tasks performed by AI (ChatGPT/GPT-5 mini):
- Inspected failing unit test reports and reproduced test failures locally.
- Identified root cause (service set `createdAt`), applied a focused fix in `CustomerService`.
- Added a `DataIntegrityViolationException` handler to `GlobalExceptionHandler` and adjusted `CustomerService` to rethrow integrity violations so the handler can produce a 400 validation-like response.
- Added an integration test `DuplicateCustomerIntegrationTest` to validate duplicate handling.
- Implemented frontend error normalization in `frontend/src/api/CustomersApi.js` and updated Redux slice and React components (`App.jsx`, `CustomerForm.jsx`) to render string or structured errors safely.
- Ran Maven tests, executed the integration test, started the backend, exercised endpoints with `curl`, and verified UI flows via running the frontend dev server.
- Updated `README.md` with setup/run/test instructions and a Design Decisions section.
- Created this `AI_USAGE.md` file.

Tasks performed by the developer (human):
- Reviewed and approved the suggested changes and test runs.
- (Optional) The developer can commit the changes, open PRs, and make UX choices about inline field error placement.

If you prefer, I can prepare a PR and push the changes to a branch — please confirm preferred commit/pr flow.

## How AI-generated code was validated
- Backend unit tests: `mvn -DskipTests=false test` (ran multiple times until all tests passed).
- Targeted integration test: `mvn -Dtest=DuplicateCustomerIntegrationTest test` to validate duplicate handling.
- Manual API checks: `curl` used to POST a customer, POST the same customer again (to trigger unique constraint), and GET `/customers` to verify database state.
- Frontend smoke: started the React dev server (`npm start`) and reproduced duplicate submission to verify friendly UI error display and to confirm React no longer crashes when backend returned structured errors.

Repro commands (run in repository root):

```bash
# Backend tests
cd backend
mvn -DskipTests=false test

# Run the duplicate-customer integration test only
mvn -Dtest=DuplicateCustomerIntegrationTest test

# Start backend (port 8482)
mvn -Dspring-boot.run.fork=false spring-boot:run

# From another shell: basic curl checks
curl -v -H "Content-Type: application/json" -d '{"firstName":"X","lastName":"Y","dateOfBirth":"1990-01-01"}' http://localhost:8482/customers
curl -v -H "Content-Type: application/json" -d '{"firstName":"X","lastName":"Y","dateOfBirth":"1990-01-01"}' http://localhost:8482/customers
curl http://localhost:8482/customers

# Frontend
cd frontend
npm install   # if needed
npm start
```

## Examples where AI made a mistake and how it was corrected

1. createdAt on save
   - Problem: `CustomerService.save()` set `createdAt` before calling the repository. The unit test expected `createdAt` to be null for the saved object used in the test. This caused assertion failures.
   - Fix: Removed service-side `createdAt` assignment so tests match expected behavior. (File changed: `backend/src/main/java/com/customer/service/CustomerService.java`)

2. Duplicate constraint handling
   - Problem: Initially `GlobalExceptionHandler` was added, but `CustomerService.save()` caught generic `Exception`, converting DB errors into 500 responses. As a result, `DataIntegrityViolationException` reached the service catch block and returned a 500 instead of allowing the global handler to map it to a validation-like 400.
   - Fix: Updated `CustomerService.save()` to rethrow `DataIntegrityViolationException` (catch and rethrow), letting `GlobalExceptionHandler` generate the 400 response. Also improved the global handler to inspect the root cause message and return a friendly `data.duplicate` message for unique-constraint violations.

3. Frontend rendering crash
   - Problem: The frontend rendered `error` directly inside a `<p>` element; when `error` was an object (e.g., `{ duplicate: '...' }`), React threw "Objects are not valid as a React child".
   - Fix: Normalized backend errors in `CustomersApi.getApiErrorMessage` to return either a string or an object of field errors. Updated `App.jsx` and `CustomerForm.jsx` to render string errors directly and map object values to paragraphs or inline messages.

Each of these corrections was validated by running the relevant tests and manual curl/UX checks.

## Time breakdown (approximate)

These are approximate elapsed times for the work performed in this session (developer + AI assistant collaboration):
- Investigation and reproducing failing tests: ~10–20 minutes
- Backend fixes + unit tests re-run: ~20–30 minutes
- Global exception handler + integration test: ~20–30 minutes
- Frontend error handling, component updates, and UI verification: ~20–30 minutes
- README and AI_USAGE documentation updates: ~10–15 minutes

Total wall time: ~1.5–2 hours.

Estimated time without AI assistance (manual): ~3–5 hours. AI reduced iteration time by rapidly suggesting focused patches, creating tests, and running validations.

## How AI impacted the development process

- Speed: AI suggested small, targeted patches (applied via automated edits) which reduced the time required to locate and fix issues.
- Test-driven feedback: AI wrote a focused integration test that codified expected behavior for duplicate handling, improving confidence.
- Repeatable validation: AI ran unit and integration tests and used `curl` to validate the runtime behavior, reducing the manual verification burden.
- Risk: AI made small mistakes (see examples) that required review; human review and test runs were necessary and were performed.

## Recommendations and next steps

- Commit and open a PR for the changes and run CI.
- Extend frontend to attach `data` field errors to specific form inputs for better UX.
- Add e2e tests (Cypress or Playwright) for the duplicate scenario and UI flows.

If you want, I can prepare the PR and add inline field-level error binding next.

---

Generated/edited files (summary)
- backend/src/main/java/com/customer/service/CustomerService.java
- backend/src/main/java/com/customer/config/GlobalExceptionHandler.java
- backend/src/test/java/com/customer/controller/DuplicateCustomerIntegrationTest.java
- frontend/src/api/CustomersApi.js
- frontend/src/store/customersSlice.js
- frontend/src/components/CustomerForm.jsx
- frontend/src/App.jsx
- README.md

If any of the above edits need to be adjusted (coding style, message wording, different HTTP status behavior), tell me which file(s) to update and I'll apply changes.
# AI Usage Documentation

## Overview

This project was developed with assistance from AI tools to improve development speed, validate implementation approaches, and generate boilerplate code. All AI-generated output was reviewed, modified where necessary, and tested before inclusion in the final solution.

---

## AI Tools Used

### ChatGPT

Used for:

* Generating initial Spring Boot project structure
* Generating React component scaffolding
* Creating DTO and entity examples
* Generating API endpoint examples
* Producing documentation templates
* Reviewing code quality and best practices

### GitHub Copilot (Optional)

Used for:

* Auto-completing repetitive code
* Suggesting import statements
* Generating getters/setters and simple methods
* Assisting with React state management syntax

---

## Tasks Delegated to AI

### Backend

AI-assisted tasks:

* Spring Boot project setup
* Entity class generation
* Repository generation
* REST controller scaffolding
* Service layer boilerplate
* H2 configuration examples

### Frontend

AI-assisted tasks:

* React component templates
* Axios integration examples
* Form state management examples
* Customer table rendering examples

### Documentation

AI-assisted tasks:

* README structure
* API usage examples
* Setup instructions
* This AI usage document

---

## Tasks Implemented and Reviewed Manually

The following areas were manually reviewed and adjusted:

* Application architecture decisions
* API endpoint design
* Validation strategy
* Error handling approach
* Component interaction flow
* Testing and debugging
* Final code cleanup
* UI layout adjustments

---

## Validation Process

All AI-generated code was validated through:

### Compilation Validation

Backend:

```bash
mvn clean install
```

Frontend:

```bash
npm install
npm start
```

### Functional Testing

Verified:

* Customer creation endpoint
* Customer retrieval endpoint
* H2 database persistence
* React form submission
* Customer listing UI

### Manual Review

Reviewed:

* Naming conventions
* Code readability
* Unused imports
* API contract consistency
* Error handling

---

## Examples of AI Corrections

### Example 1: CORS Configuration

Initial AI-generated code:

```java
@CrossOrigin("*")
```

Issue:

Incorrect annotation usage.

Corrected version:

```java
@CrossOrigin(origins = "*")
```

---

### Example 2: React State Reset

Initial AI suggestion omitted clearing the form after submission.

Correction:

```javascript
setCustomer({
  firstName: "",
  lastName: "",
  dateOfBirth: ""
});
```

---

### Example 3: Date Handling

Initial AI-generated examples used String values inconsistently.

Correction:

Backend entity updated to use:

```java
LocalDate dateOfBirth;
```

to ensure proper date handling.

---

## Estimated Time Comparison

### Without AI Assistance

| Activity             | Estimated Time |
| -------------------- | -------------- |
| Spring Boot setup    | 45 min         |
| REST API development | 60 min         |
| React UI creation    | 60 min         |
| Testing & debugging  | 45 min         |
| Documentation        | 30 min         |
| Total                | ~4 hours       |

### With AI Assistance

| Activity             | Actual Time |
| -------------------- | ----------- |
| Spring Boot setup    | 15 min      |
| REST API development | 25 min      |
| React UI creation    | 25 min      |
| Testing & debugging  | 30 min      |
| Documentation        | 15 min      |
| Total                | ~1.5 hours  |

Estimated time savings: approximately 60–65%.

---

## Impact of AI on Development Process

AI accelerated development by:

* Reducing boilerplate coding effort
* Providing quick examples and implementation patterns
* Assisting with React and Spring Boot syntax
* Helping generate documentation
* Acting as a secondary reviewer for code quality

However, all generated code was manually reviewed, validated, and tested before being included in the final submission.

AI served as a productivity tool rather than a replacement for software engineering judgment and implementation decisions.

---

## Final Statement

All submitted code was reviewed, understood, and validated by the developer. AI tools were used as assistants to improve efficiency, while responsibility for design decisions, testing, debugging, and final implementation remained with the developer.
