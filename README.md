# Fintech_analysis

## SpendSense — Smart Money Assistant

SpendSense is a responsive personal finance workspace with a React/Vite frontend and a Java 21 / Spring Boot API backed by MySQL. The current vertical slice includes a cinematic dashboard, clearly labeled local demo data, local transaction entry/edit/delete, account balance rollups, category and cash-flow charts, transaction search/filtering, and CSV export. Registration and sign-in endpoints use BCrypt and server sessions.

## Requirements

- Node.js 20+ and npm
- Java 21 and Maven 3.9+
- MySQL 8+

## Run locally

1. Create a MySQL database and development user (or adjust the values below): `CREATE DATABASE spendsense;`
2. Copy `.env.example` to `.env` and provide a local database password. Never commit real secrets.
3. Start the API from `backend`: `mvn spring-boot:run` (Flyway applies the schema migration).
4. From the repository root run `npm install` and `npm run dev`.
5. Open the Vite URL printed by the dev server. The Vite `/api` proxy forwards API requests to port 8080.

The dashboard's first-run records and account opening balances are explicitly identified in code as local sample data. Local edits persist in browser storage for the prototype; this is not a substitute for authenticated server persistence. The API currently establishes the auth foundation; transaction/account API persistence is the next implementation slice. Do not use this prototype for actual financial records yet.

## API foundation

| Method | Path | Purpose |
|---|---|---|
| GET | `/api/health` | Health check |
| POST | `/api/auth/register` | Validate and register a user with BCrypt password hashing |
| POST | `/api/auth/login` | Establish an HTTP session |
| GET | `/api/auth/me` | Current authenticated principal |
| POST | `/api/auth/logout` | Invalidate session |
| GET / POST | `/api/accounts` | List user-owned accounts with calculated balances / create an account |
| GET / POST | `/api/transactions` | List user-owned transactions / record income, expense, or transfer |
| PUT / DELETE | `/api/transactions/{id}` | Update or delete an owned transaction |
| GET | `/api/csrf` | Initialize the CSRF cookie and return its header name |

The API uses Spring Security sessions and CSRF protection. Retrieve `/api/csrf` before unsafe requests and send the returned token in `X-XSRF-TOKEN`. The frontend's registration and sign-in forms use these APIs. Transaction forms still use browser-local demo persistence and are not yet connected to the protected APIs.

## Financial rules and limitations

- Money is represented as Java `BigDecimal` / MySQL `DECIMAL` in the API schema.
- Income and expenses are distinct transaction types. A transfer has distinct source and destination accounts and must not affect income, expenses, or savings.
- Account balance is opening balance plus recorded inflows less outflows. No direct balance mutation endpoint is provided.
- Current charts use actual records in the browser's explicitly marked sample/demo workspace and must not be read as authenticated user analytics.
- Refunds, reversals, credit-card liabilities, import deduplication, forecasting, notifications, and server-backed analytics are not implemented yet.
- No bank synchronization or AI processing is implemented. Never enter bank passwords, UPI PINs, or OTPs.

## Architecture

`src/` contains the Vite/React presentation and shared dark design system. `backend/src/main/java/com/spendsense` is the Spring application, security, authentication, and user persistence layer. Flyway migrations live in `backend/src/main/resources/db/migration`. Future mobile clients should use versioned JSON API DTOs and the same server-side authorization/business logic; avoid embedding financial calculation rules in a mobile client.

## Verification

Run `npm run build` for the frontend production build. Run `mvn test` under `backend` once Java 21 and Maven are installed. Neither Java nor Maven is bundled in the current workspace environment, so backend compilation was not available here.
