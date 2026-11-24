# Design to code

`GameHubApp` is the JavaFX composition and navigation shell. `OperationsService` owns application orchestration and transaction boundaries. `UserService` owns account identity and password hashing. `Database` owns SQLite connection setup and migration. The personnel hierarchy is intentionally isolated under `com.gamehub.personnel` as required by D4.

Information Expert is used by `OperationsService` for workflow invariants and by `Database` for persistence setup. Controller responsibilities are represented by service methods called from JavaFX actions. Prepared statements and typed Java time and money values provide protected variation at infrastructure boundaries.

Known deviation: the current application still has a facade-shaped `OperationsService` rather than separate repositories and controllers. This is recorded as remaining architecture work.
