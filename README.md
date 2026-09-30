# booking-api-tests

[![API Tests](https://github.com/srivamsiram/booking-api-tests/actions/workflows/api-tests.yml/badge.svg)](https://github.com/srivamsiram/booking-api-tests/actions/workflows/api-tests.yml)
[![Allure Report](https://img.shields.io/badge/Allure-live%20report-orange)](https://srivamsiram.github.io/booking-api-tests/)

An API test automation framework for the [Restful-Booker](https://restful-booker.herokuapp.com/apidoc/index.html) API, built with Java, REST Assured and TestNG. The tests run on every push to GitHub, and a live Allure report is published to GitHub Pages.

📊 **Live report:** https://srivamsiram.github.io/booking-api-tests/

---

## Tech stack

| Tool | Purpose |
|---|---|
| Java 17 | Language |
| Maven | Build and dependency management |
| REST Assured 5 | HTTP client and request specifications |
| TestNG 7 | Test runner, groups, `@DataProvider`, suites |
| Jackson Databind | JSON serialization with POJOs (no raw JSON strings in tests) |
| JSON Schema Validator | Contract testing |
| Allure Report | Reporting with steps and request/response attachments |
| GitHub Actions + GitHub Pages | CI pipeline and report hosting |

## Framework architecture

```
src/test/java/com/booking/
├── config/     ConfigManager: system property → env var → config.properties
├── models/     Jackson POJOs: Booking, BookingDates, AuthRequest, AuthResponse, CreateBookingResponse
├── clients/    BaseClient (shared request spec), AuthClient, BookingClient: return REST Assured Responses
├── filters/    ApiLoggingFilter: attaches every request/response to Allure and logs a one-line summary
├── utils/      BookingBuilder: test data builder with valid defaults and unique names
└── tests/      TestNG test classes: only call clients and make assertions

src/test/resources/
├── config.properties   Base URL and demo credentials
├── schemas/            JSON schemas for contract tests
└── suites/             smoke.xml, regression.xml, known-bugs.xml
```

**Design principles**
- **Layered:** tests → clients → REST Assured. Tests never call `given()` directly.
- **Independent tests:** every test creates its own data, so the regression suite runs **in parallel** (4 threads).
- **Known bugs are kept apart from regressions:** tests that prove real API defects are tagged `known-bug`. They're excluded from the pass/fail gate but still run in CI and appear in the report.

## Test coverage (36 tests)

| Area | Tests | Notes |
|---|---|---|
| CRUD happy path | 7 | Create → Get → Filter → Update (PUT) → Partial update (PATCH) → Delete |
| Authentication | 8 | Token generation, and PUT/PATCH/DELETE with no token or an invalid token → 403 |
| Negative | 3 | Non-existent IDs for GET/PUT/DELETE |
| Data-driven boundaries | 8 | `@DataProvider`: zero/large price, 1-char and unicode names, same-day stay, leap day… |
| Contract | 3 | Responses validated against JSON schemas |
| Known bugs | 8 | Assert correct behaviour. They fail on purpose to document the defects below. |

## Getting started

**Prerequisites:** JDK 17+ and Maven 3.9+

```bash
git clone https://github.com/srivamsiram/booking-api-tests.git
cd booking-api-tests
```

### Run tests

| Command | What it runs |
|---|---|
| `mvn clean test` | All regression tests (known bugs excluded) |
| `mvn test -Dgroups=smoke` | 5 critical-path smoke tests |
| `mvn test -Psmoke` | Smoke suite from `suites/smoke.xml` |
| `mvn test -Pregression` | Regression suite, run in parallel |
| `mvn test -Pknown-bugs` | Known-bug tests only (**expected to fail**) |

### View the Allure report locally

```bash
mvn allure:serve
```

### Configuration

Values are resolved in this order: **JVM system property → environment variable → `config.properties`**.

| Key | Env var | Default |
|---|---|---|
| `base.url` | `BOOKER_BASE_URL` | `https://restful-booker.herokuapp.com` |
| `username` | `BOOKER_USERNAME` | `admin` |
| `password` | `BOOKER_PASSWORD` | `password123` |

For example, to run against a local instance:

```bash
mvn test -Dbase.url=http://localhost:3001
```

> The environment variables use a `BOOKER_` prefix because Windows already defines `USERNAME`.

## CI/CD pipeline

`.github/workflows/api-tests.yml` runs on every push to `main`/`master`, on pull requests, and on demand:

1. Sets up Java 17 with a Maven dependency cache.
2. Runs the **regression suite**, which decides whether the build passes.
3. Runs the **known-bug suite**, for the report only.
4. Restores earlier report history from the `gh-pages` branch, so the report shows trend graphs.
5. Generates the Allure report with categories and environment info.
6. Publishes it to **GitHub Pages**.
7. Fails the build if any regression test failed.

## Deliberate Bugs Discovered

These defects were found while building the suite. Each one marked 🧪 has a `known-bug` test that asserts the **correct** behaviour and fails until the API is fixed.

| ID | Endpoint | Scenario | Expected | Actual | Test |
|---|---|---|---|---|---|
| BUG-1 | `POST /auth` | Wrong password | `401 Unauthorized` | `200 OK` with `{"reason":"Bad credentials"}` | 🧪 |
| BUG-2 | `POST /booking` | `Accept: application/json, text/javascript, …` (several types) | `200 OK` | `418 I'm a teapot` | Worked around in `BaseClient` |
| BUG-3 | `DELETE /booking/{id}` | Successful delete | `200` / `204 No Content` | `201 Created` | Asserted as current behaviour |
| BUG-4 | `POST /booking` | Missing `firstname` or `bookingdates` | `400 Bad Request` | `500 Internal Server Error` | 🧪 |
| BUG-5 | `POST /booking` | `firstname` sent as a number | `400 Bad Request` | `500 Internal Server Error` | 🧪 |
| BUG-6 | `POST /booking` | `totalprice: "abc"` | `400 Bad Request` | `200 OK`, price saved as `null` | 🧪 |
| BUG-7 | `POST /booking` | Invalid date `2026-13-45` | `400 Bad Request` | `200 OK`, date saved as `"0NaN-aN-aN"` | 🧪 |
| BUG-8 | `POST /booking` | Checkout date before checkin | `400 Bad Request` | `200 OK` | 🧪 |
| BUG-9 | `POST /booking` | Negative `totalprice` | `400 Bad Request` | `200 OK` | 🧪 |
| BUG-10 | `PUT` / `DELETE /booking/{id}` | Non-existent ID | `404 Not Found` | `405 Method Not Allowed` | Asserted as current behaviour |

To reproduce them:

```bash
mvn test -Pknown-bugs
```
