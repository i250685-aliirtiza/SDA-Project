# Design to code

`GameHubApp` is the JavaFX composition and navigation shell. `OperationsService` owns application orchestration and transaction boundaries. `UserService` owns account identity and password hashing. `Database` owns SQLite connection setup and migration. The personnel hierarchy is intentionally isolated under `com.gamehub.personnel` as required by D4.

Information Expert is used by `OperationsService` for workflow invariants and by `Database` for persistence setup. Controller responsibilities are represented by service methods called from JavaFX actions. Prepared statements and typed Java time and money values provide protected variation at infrastructure boundaries.

`ReportRepository` now owns report SQL and `OperationsService` delegates report aggregates to it. Tournament, payment and reservation orchestration remain in `OperationsService` and are recorded as the remaining structural consolidation opportunity.

