# RestAssured API Testing Framework

API test automation framework for an e-commerce deals/reservations/tracking API, built with
**RestAssured**, **TestNG**, and **Maven**. Tests cover deal listings, deal detail lookups,
active reservations, and the earner payout tracker, including pagination, negative/auth
scenarios, and cross-environment response-shape quirks.

> The real API base URL and credentials are never committed to this repo. See
> [Configuration & secrets](#configuration--secrets) below.

## Tech stack

- Java 21
- Maven
- RestAssured 5.x
- TestNG 7.x
- Jackson (custom deserializers for inconsistent API response shapes)
- [dotenv-java](https://github.com/cdimascio/java-dotenv) for local secret/config loading

## Project structure

```
src
├── main/java/com/ecommerce/api
│   ├── config/    # ConfigManager - resolves base URL + API credentials (env var > .env > properties)
│   └── models/    # POJOs for request/response payloads, plus custom Jackson deserializers
└── test/java/com/ecommerce/api
    ├── base/      # BaseTest - shared RestAssured request spec setup
    └── tests/     # Test classes grouped by API resource
        ├── deals/         # DealsTests, DealDetailTests
        ├── reservations/  # ReservationsTests
        └── mytracker/     # MyTrackerTests
```

## Test suites

| Suite | Endpoint | Covers |
|---|---|---|
| `DealsTests` | `GET /v2/deals` | Paginates through all deals, validates every deal's required fields, cross-checks `payout_price` vs `retail_price` against `retail_type` (Below/Above/Full Retail), validates nested item + retailer link shapes, asserts no duplicate deal IDs across pages, invalid-credentials → 401 |
| `DealDetailTests` | `GET /v2/deals/{slug}` | Single-deal lookup by slug, same pricing/field validation as above, unknown slug → 404, invalid credentials → 401 |
| `ReservationsTests` | `GET /v2/deal/reservations/active` | Paginates active reservations, validates reservation + item + retailer link fields, cross-checks reservation count against `paging.total`, invalid credentials → 401 |
| `MyTrackerTests` | `GET /v2/my-tracker` | Paginates tracker rows, validates the `reserve_id -> purchase_id -> shipment_id` lifecycle (amount paid/qty received only expected once a shipment exists), handles non-deal ledger rows (fees/bonuses) and voided/never-shipped rows, empty-tracker case, invalid credentials → 401 |

### Handling inconsistent API responses

A few endpoints don't return a consistent shape, so custom Jackson deserializers normalize them
before assertions ever run:

- **`DealDetailItemsDeserializer`** — the deal-detail endpoint returns `items` as an array in one
  environment and a single object (with links nested under a field literally named `items`) in
  another. Both are normalized into `List<DealDetailItem>`.
- **`MyTrackerListDeserializer`** — `my_tracker` is an array when rows exist, but the literal
  string `"No record found"` when they don't.
- **`ReservationListDeserializer`** — same pattern: `reservation_list` is an array, or the string
  `"No reservations available"` when empty.

This keeps test code asserting against a plain `List`, regardless of what the API actually sent.

## Running the tests

```bash
mvn test
```

This runs the TestNG suite defined in [testng.xml](testng.xml) via the Maven Surefire plugin.

Run a single suite/class directly:

```bash
mvn test -Dtest=DealsTests
```

## Configuration & secrets

Nothing sensitive is committed to source control. `ConfigManager` resolves the base URL and API
credentials in this order: **real environment variable → `.env` (gitignored) → placeholder in
`config.properties`**. Real environment variables win so CI secrets (injected as env vars, not
files) always override local `.env` values.

Setup:

```bash
cp .env.example .env
# then fill in real values in .env
```

```dotenv
BASE_URL=https://buyandsell.com/api/   # example placeholder — set your real target API here
API_KEY=
API_SECRET_KEY=
```

`src/test/resources/config.properties` only ever holds a non-functional placeholder base URL
(`https://buyandsell.com/api/`) plus non-sensitive test fixture data (deal slugs). It's committed
so the project builds out of the box, but it does **not** point at a real API — set `BASE_URL` in
your own `.env` to run the suite against an actual target.

In CI, set `BASE_URL` / `API_KEY` / `API_SECRET_KEY` as real environment variables (e.g. GitHub
Actions secrets) rather than committing a `.env` file.

## Notes

- `BaseTest` builds a shared `RequestSpecification` (base URI, `Content-Type`, request/response
  logging filters, and the `API-KEY`/`API-SECRET` headers when credentials are configured).
  Negative-auth tests intentionally build their own request outside that spec so they can send
  deliberately invalid credentials.
- Pagination tests fetch every page up front and validate that the total number of unique IDs
  seen matches the API's own reported `paging.total`, catching both missing and duplicated
  records across pages.
