# GameHub

Java 21 and JavaFX desktop application for gaming-station reservations, sessions, payments, memberships, teams, tournaments, maintenance and reports.

## Run

Windows: `./mvnw.cmd clean verify` then `./mvnw.cmd javafx:run`

Unix: `./mvnw clean verify` then `./mvnw javafx:run`

The database defaults to `%USERPROFILE%\\.gamehub\\gamehub.db` on Windows and `$HOME/.gamehub/gamehub.db` on Unix. Set `GAMEHUB_DB_PATH` for an isolated demo or test database. First launch provides manager bootstrap. Public signup creates customers. Staff and organizers are provisioned by an authenticated manager through the service layer.
