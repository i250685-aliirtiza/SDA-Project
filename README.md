# GameHub

Java 21 and JavaFX desktop application for gaming-station reservations, sessions, payments, memberships, teams, tournaments, maintenance and reports.

## Run

On Windows, open PowerShell in this folder and run `.\run-gamehub.cmd`. The launcher uses a compatible `JAVA_HOME` or looks for a JDK 21 installation, including Eclipse Temurin. Use `.\run-gamehub.cmd verify` to build and run the tests.

On Linux or macOS, select JDK 21 with `JAVA_HOME` and run `./mvnw javafx:run`. Run `./mvnw clean verify` to build and test.

A fresh database opens with **Set up GameHub** on the sign-in screen. Create the manager account to add stations, staff and organizer accounts.

The database defaults to `%USERPROFILE%\\.gamehub\\gamehub.db` on Windows and `$HOME/.gamehub/gamehub.db` on Unix. Set `GAMEHUB_DB_PATH` for an isolated demo or test database. First launch provides manager bootstrap. Public signup creates customers. Staff and organizers are provisioned by an authenticated manager through the service layer.
