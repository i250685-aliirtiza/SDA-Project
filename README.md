# GameHub

Offline-first JavaFX desktop application for gaming-café reservations, sessions, tournaments, maintenance, and reporting.

## Prerequisites
Java 21 and Maven 3.9+ are required. This workspace currently has neither installed, so verification must be run after installing them.

## Run
Windows: `mvnw.cmd clean verify` then `mvnw.cmd javafx:run` (or use `mvn` if installed).
Unix: `./mvnw clean verify` then `./mvnw javafx:run`.

The database is created at `%USERPROFILE%/.gamehub/gamehub.db` (or `$HOME/.gamehub/gamehub.db`). Set `GAMEHUB_DB_PATH` to override it. A manager account can be bootstrapped from the first-launch screen.

## Scope
The implementation follows `implementation.md`, uses JavaFX 21, SQLite JDBC, prepared statements, transactions, and role-aware application services. Payment is cash/manual external confirmation only; no live gateway is implied.
