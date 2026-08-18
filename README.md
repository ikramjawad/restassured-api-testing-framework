# ECommerce-API-Automation

API automation framework for an e-commerce REST API, built with RestAssured, TestNG, and Maven.

Tests run against [DummyJSON](https://dummyjson.com), a free public e-commerce-style API (products, auth, carts).

## Tech Stack

- Java 21
- Maven
- RestAssured
- TestNG
- Jackson (POJO serialization/deserialization)

## Project Structure

```
src
├── main/java/com/ecommerce/api
│   ├── config/    # ConfigManager - reads base URL from config.properties
│   └── models/    # POJOs for request/response payloads
└── test/java/com/ecommerce/api
    ├── base/      # BaseTest - shared RestAssured setup
    └── tests/     # Test classes grouped by API resource (products, auth, carts)
```

## Running the tests

```bash
mvn test
```

This runs the TestNG suite defined in `testng.xml`.

## Configuration

The base URL is set in `src/test/resources/config.properties`:

```properties
base.url=https://dummyjson.com
```
